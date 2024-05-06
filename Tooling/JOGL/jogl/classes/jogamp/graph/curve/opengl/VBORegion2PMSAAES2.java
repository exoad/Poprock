/**
 * Copyright 2010-2024 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */
package jogamp.graph.curve.opengl;

import java.io.File;
import java.nio.FloatBuffer;

import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLUniformData;

import jogamp.graph.curve.opengl.shader.AttributeNames;
import jogamp.graph.curve.opengl.shader.UniformNames;

import com.jogamp.graph.curve.Region;
import com.jogamp.graph.curve.opengl.GLRegion;
import com.jogamp.graph.curve.opengl.RegionRenderer;
import com.jogamp.graph.curve.opengl.RenderState;
import com.jogamp.math.Matrix4f;
import com.jogamp.math.Recti;
import com.jogamp.math.geom.AABBox;
import com.jogamp.math.geom.Frustum;
import com.jogamp.math.util.SyncMatrices4f16;
import com.jogamp.opengl.FBObject;
import com.jogamp.opengl.FBObject.Attachment;
import com.jogamp.opengl.FBObject.TextureAttachment;
import com.jogamp.opengl.util.GLArrayDataServer;
import com.jogamp.opengl.util.GLReadBufferUtil;
import com.jogamp.opengl.util.glsl.ShaderProgram;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureSequence;

public final class VBORegion2PMSAAES2  extends GLRegion {
    private static final boolean DEBUG_FBO_1 = false;
    private static final boolean DEBUG_FBO_2 = false;
    private static final boolean SCREENSHOT_FBO = false;

    private final RenderState.ProgramLocal rsLocal;

    // Pass-1:
    private final GLUniformData gcu_ColorTexUnit;
    private final float[] colorTexBBox; // minX/minY, maxX/maxY, texW/texH
    private final GLUniformData gcu_ColorTexBBox; // vec2 gcu_ColorTexBBox[3] -> boxMin[2], boxMax[2] and texSize[2]
    private final float[] colorTexClearCol;
    private final GLUniformData gcu_ColorTexClearCol; // vec4 gcu_ColorTexClearCol
    private final float[/* 4*6 */] clipFrustum; // 6 frustum planes, each [n.x, n.y. n.z, d]
    private final GLUniformData gcu_ClipFrustum; // uniform vec4  gcu_ClipFrustum[6]; // L, R, B, T, N, F
    private ShaderProgram spPass1 = null;

    // Pass-2:
    private GLArrayDataServer gca_FboVerticesAttr;
    private GLArrayDataServer gca_FboTexCoordsAttr;
    private GLArrayDataServer indicesFbo;
    private final GLUniformData gcu_FboTexUnit;
    private final Matrix4f matP = new Matrix4f();
    private final Matrix4f matMv = new Matrix4f();
    private final GLUniformData gcu_PMVMatrix02;
    private ShaderProgram spPass2 = null;

    private FBObject fbo;
    private TextureAttachment texA;
    private GLReadBufferUtil screenshot = SCREENSHOT_FBO ? new GLReadBufferUtil(true, false) : null;

    private int fboWidth = 0;
    private int fboHeight = 0;
    private boolean fboDirty = true;

    final int[] maxTexSize = new int[] { -1 } ;

    public VBORegion2PMSAAES2(final GLProfile glp, final int renderModes, final TextureSequence colorTexSeq, final int pass2TexUnit,
                              final int initialVerticesCount, final int initialIndicesCount)
    {
        super(glp, renderModes, colorTexSeq);

        rsLocal = new RenderState.ProgramLocal();

        // We leave GLArrayDataClient.DEFAULT_GROWTH_FACTOR intact for avg +19% size, but 15% less CPU overhead compared to 1.2 (19% total)

        // Pass 1:
        initBuffer(initialVerticesCount, initialIndicesCount);

        if( hasColorTexture() ) {
            gcu_ColorTexUnit = new GLUniformData(UniformNames.gcu_ColorTexUnit, colorTexSeq.getTextureUnit());
            colorTexBBox = new float[6];
            gcu_ColorTexBBox = new GLUniformData(UniformNames.gcu_ColorTexBBox, 2, FloatBuffer.wrap(colorTexBBox));
            colorTexClearCol = new float[4];
            gcu_ColorTexClearCol = new GLUniformData(UniformNames.gcu_ColorTexClearCol, 4, FloatBuffer.wrap(colorTexClearCol));
        } else {
            gcu_ColorTexUnit = null;
            colorTexBBox = null;
            gcu_ColorTexBBox = null;
            colorTexClearCol = null;
            gcu_ColorTexClearCol = null;
        }
        clipFrustum = new float[4*6];
        gcu_ClipFrustum = new GLUniformData(UniformNames.gcu_ClipFrustum, 4, FloatBuffer.wrap(clipFrustum));
        gcu_PMVMatrix02 = new GLUniformData(UniformNames.gcu_PMVMatrix02, 4, 4, new SyncMatrices4f16( new Matrix4f[] { matP, matMv } ));

        // Pass 2:
        gcu_FboTexUnit = new GLUniformData(UniformNames.gcu_FboTexUnit, pass2TexUnit);

        indicesFbo = GLArrayDataServer.createData(3, GL.GL_UNSIGNED_SHORT, 2, GL.GL_STATIC_DRAW, GL.GL_ELEMENT_ARRAY_BUFFER);
        indicesFbo.puts((short) 0); indicesFbo.puts((short) 1); indicesFbo.puts((short) 3);
        indicesFbo.puts((short) 1); indicesFbo.puts((short) 2); indicesFbo.puts((short) 3);
        indicesFbo.seal(true);

        gca_FboTexCoordsAttr = GLArrayDataServer.createGLSL(AttributeNames.FBO_TEXCOORDS_ATTR_NAME, 2, GL.GL_FLOAT,
                                                           false, 4, GL.GL_STATIC_DRAW);
        gca_FboTexCoordsAttr.putf(0); gca_FboTexCoordsAttr.putf(0);
        gca_FboTexCoordsAttr.putf(0); gca_FboTexCoordsAttr.putf(1);
        gca_FboTexCoordsAttr.putf(1); gca_FboTexCoordsAttr.putf(1);
        gca_FboTexCoordsAttr.putf(1); gca_FboTexCoordsAttr.putf(0);
        gca_FboTexCoordsAttr.seal(true);

        gca_FboVerticesAttr = GLArrayDataServer.createGLSL(AttributeNames.FBO_VERTEX_ATTR_NAME, 3, GL.GL_FLOAT,
                                                           false, 4, GL.GL_STATIC_DRAW);
    }

    @Override
    public void setTextureUnit(final int pass2TexUnit) {
        gcu_FboTexUnit.setData(pass2TexUnit);
    }

    @Override
    protected final void clearImpl(final GL2ES2 gl) {
        fboDirty = true;
    }

    @Override
    protected void updateImpl(final GL2ES2 gl, final RegionRenderer renderer, final int curRenderModes) {
        // final boolean hasColorChannel = Region.hasColorChannel( curRenderModes );
        final boolean hasColorTexture = Region.hasColorTexture( curRenderModes );

        // seal buffers
        indicesBuffer.seal(gl, true);
        indicesBuffer.enableBuffer(gl, false);
        vpc_ileave.seal(gl, true);
        vpc_ileave.enableBuffer(gl, false);
        if( hasColorTexture && null != gcu_ColorTexUnit && colorTexSeq.isTextureAvailable() ) {
            if( colorTexSeq.useARatioAdjustment() ) {
                TextureSequence.setTexCoordBBox(colorTexSeq.getLastTexture().getTexture(), box, colorTexSeq.useARatioLetterbox(), colorTexBBox, false);
            } else {
                TextureSequence.setTexCoordBBoxSimple(colorTexSeq.getLastTexture().getTexture(), box, colorTexBBox, false);
            }
            colorTexSeq.getARatioLetterboxBackColor().toArray(colorTexClearCol);
        }
        // Pending gca_FboVerticesAttr-seal and fboPMVMatrix-setup, follow fboDirty

        // push data 2 GPU ..
        indicesFbo.seal(gl, true);
        indicesFbo.enableBuffer(gl, false);

        fboDirty = true;
        // the buffers were disabled, since due to real/fbo switching and other vbo usage
    }

    /**
     * <p>
     * Since multiple {@link Region}s may share one
     * {@link ShaderProgram} managed and owned by {@link RegionRendered}, the uniform data must always be updated.
     * </p>
     *
     * @param gl
     * @param renderer
     * @param pass1
     * @param renderModes
     */
    public void useShaderProgram(final GL2ES2 gl, final RegionRenderer renderer, final int curRenderModes, final boolean pass1) {
        final boolean isTwoPass = Region.isTwoPass( curRenderModes );
        final boolean hasColorChannel = Region.hasColorChannel( curRenderModes );
        final boolean hasColorTexture = Region.hasColorTexture( curRenderModes ) && null != colorTexSeq;

        final RenderState rs = renderer.getRenderState();
        final boolean hasFrustumClipping = null != rs.getClipFrustum() && ( ( !isTwoPass && pass1 ) || ( isTwoPass && !pass1 ) );

        final boolean updateLocGlobal = renderer.useShaderProgram(gl, curRenderModes, pass1, colorTexSeq);
        final ShaderProgram sp = renderer.getRenderState().getShaderProgram();
        final boolean updateLocLocal;
        if( pass1 ) {
            updateLocLocal = !sp.equals(spPass1);
            spPass1 = sp;
            if( DEBUG ) {
                if( DEBUG_ALL_EVENT || updateLocLocal || updateLocGlobal ) {
                    System.err.println("XXX changedSP.p1 updateLocation loc "+updateLocLocal+" / glob "+updateLocGlobal+", sp "+sp.program()+" / "+sp.id());
                }
            }
            if( updateLocLocal ) {
                rs.updateAttributeLoc(gl, true, gca_VerticesAttr, true);
                rs.updateAttributeLoc(gl, true, gca_CurveParamsAttr, true);
                if( hasColorChannel && null != gca_ColorsAttr ) {
                    rs.updateAttributeLoc(gl, true, gca_ColorsAttr, true);
                }
            }
            if( isTwoPass ) {
                rsLocal.update(gl, rs, updateLocLocal, curRenderModes, false, true, true);
                rs.updateUniformLoc(gl, updateLocLocal, gcu_PMVMatrix02, true);
            } else {
                rsLocal.update(gl, rs, updateLocLocal, curRenderModes, true, true, true);
            }
            if( hasColorTexture && null != gcu_ColorTexUnit ) {
                rs.updateUniformLoc(gl, updateLocLocal, gcu_ColorTexUnit, true);
                rs.updateUniformLoc(gl, updateLocLocal, gcu_ColorTexBBox, true);
                rs.updateUniformLoc(gl, updateLocLocal, gcu_ColorTexClearCol, true);
            }
        } else {
            updateLocLocal = !sp.equals(spPass2);
            spPass2 = sp;
            if( DEBUG ) {
                if( DEBUG_ALL_EVENT || updateLocLocal || updateLocGlobal ) {
                    System.err.println("XXX changedSP.p2 updateLocation loc "+updateLocLocal+" / glob "+updateLocGlobal+", sp "+sp.program()+" / "+sp.id());
                }
            }
            if( updateLocLocal ) {
                rs.updateAttributeLoc(gl, true, gca_FboVerticesAttr, true);
                rs.updateAttributeLoc(gl, true, gca_FboTexCoordsAttr, true);
            }
            rsLocal.update(gl, rs, updateLocLocal, curRenderModes, true, false, true);
            rs.updateUniformDataLoc(gl, updateLocLocal, false /* updateData */, gcu_FboTexUnit, true); // FIXME always update if changing tex-unit
        }
        if( hasFrustumClipping && updateLocLocal ) {
            rs.updateUniformLoc(gl, true, gcu_ClipFrustum, true);
        }
    }

    private final AABBox drawWinBox = new AABBox();
    private final Recti drawView = new Recti();

    private static final int border = 2; // surrounding border, i.e. width += 2*border, height +=2*border

    @Override
    protected void drawImpl(final GL2ES2 gl, final RegionRenderer renderer, final int curRenderModes) {
        if( 0 >= indicesBuffer.getElemCount() ) {
            if(DEBUG_INSTANCE) {
                System.err.printf("VBORegion2PMSAAES2.drawImpl: Empty%n");
            }
            return; // empty!
        }
        if( Float.isInfinite(box.getWidth()) || Float.isInfinite(box.getHeight()) ) {
            if(DEBUG_INSTANCE) {
                System.err.printf("VBORegion2PMSAAES2.drawImpl: Inf %s%n", box);
            }
            return; // inf
        }
        final RenderState rs = renderer.getRenderState();
        final int vpWidth = renderer.getWidth();
        final int vpHeight = renderer.getHeight();
        if(vpWidth <=0 || vpHeight <= 0 || rs.getSampleCount() < 0) {
            useShaderProgram(gl, renderer, curRenderModes, true);
            renderRegion(gl, rs, curRenderModes);
        } else {
            if(0 > maxTexSize[0]) {
                gl.glGetIntegerv(GL.GL_MAX_TEXTURE_SIZE, maxTexSize, 0);
            }
            final float winWidth, winHeight;

            final float ratioObjWinWidth, ratioObjWinHeight;
            final float diffObjWidth, diffObjHeight;
            final float diffObjBorderWidth, diffObjBorderHeight;
            int targetFboWidth, targetFboHeight;
            {
                final float diffWinWidth, diffWinHeight;
                final int targetWinWidth, targetWinHeight;

                // Calculate perspective pixel width/height for FBO,
                // considering the sampleCount.
                drawView.setWidth(vpWidth);
                drawView.setHeight(vpHeight);

                box.mapToWindow(drawWinBox, renderer.getMatrix().getPMv(), drawView, true /* useCenterZ */);

                winWidth = drawWinBox.getWidth();
                winHeight = drawWinBox.getHeight();
                targetWinWidth = (int)Math.ceil(winWidth);
                targetWinHeight = (int)Math.ceil(winHeight);
                diffWinWidth = targetWinWidth-winWidth;
                diffWinHeight = targetWinHeight-winHeight;

                ratioObjWinWidth = box.getWidth() / winWidth;
                ratioObjWinHeight= box.getHeight() / winHeight;
                diffObjWidth = diffWinWidth * ratioObjWinWidth;
                diffObjHeight = diffWinHeight * ratioObjWinHeight;
                diffObjBorderWidth = border * ratioObjWinWidth;
                diffObjBorderHeight = border * ratioObjWinHeight;

                targetFboWidth = targetWinWidth+2*border;
                targetFboHeight = targetWinHeight+2*border;

                if( DEBUG_FBO_2 ) {
                    final float ratioWinWidth, ratioWinHeight;
                    ratioWinWidth = winWidth/targetWinWidth;
                    ratioWinHeight = winHeight/targetWinHeight;

                    System.err.printf("XXX.MinMax obj %s%n", box.toString());
                    System.err.printf("XXX.MinMax obj d[%.3f, %.3f], r[%f, %f], b[%f, %f]%n",
                            diffObjWidth, diffObjHeight, ratioObjWinWidth, ratioObjWinWidth, diffObjBorderWidth, diffObjBorderHeight);
                    System.err.printf("XXX.MinMax win %s%n", drawWinBox.toString());
                    System.err.printf("XXX.MinMax view[%s] -> win[%.3f, %.3f], i[%d x %d], d[%.3f, %.3f], r[%f, %f]: FBO i[%d x %d], samples %d%n",
                            drawView,
                            winWidth, winHeight, targetWinWidth, targetWinHeight,
                            diffWinWidth, diffWinHeight, ratioWinWidth, ratioWinHeight,
                            targetFboWidth, targetFboHeight,
                            rs.getSampleCount());
                }
            }
            if( 0 >= targetFboWidth || 0 >= targetFboHeight ) {
                // Nothing ..
                return;
            }
            final int deltaFboWidth = Math.abs(targetFboWidth-fboWidth);
            final int deltaFboHeight = Math.abs(targetFboHeight-fboHeight);
            final boolean hasDelta = 0!=deltaFboWidth || 0!=deltaFboHeight;
            if( DEBUG_FBO_2 ) {
                System.err.printf("XXX.maxDelta: hasDelta %b: %d / %d,  %.3f, %.3f%n",
                        hasDelta, deltaFboWidth, deltaFboHeight, (float)deltaFboWidth/fboWidth, (float)deltaFboHeight/fboHeight);
                System.err.printf("XXX.Scale %d * [%f x %f]: %d x %d%n",
                        rs.getSampleCount(), winWidth, winHeight, targetFboWidth, targetFboHeight);
            }
            if( hasDelta || fboDirty || isShapeDirty() || null == fbo ) {
                // FIXME: rescale
                final float minX = box.getMinX()-diffObjBorderWidth;
                final float minY = box.getMinY()-diffObjBorderHeight;
                final float maxX = box.getMaxX()+diffObjBorderWidth+diffObjWidth;
                final float maxY = box.getMaxY()+diffObjBorderHeight+diffObjHeight;
                final float minZ = box.getMinZ();
                gca_FboVerticesAttr.seal(false);
                {
                    final FloatBuffer fb = (FloatBuffer)gca_FboVerticesAttr.getBuffer();
                    fb.put(0, minX); fb.put( 1, minY); fb.put( 2, minZ);
                    fb.put(3, minX); fb.put( 4, maxY); fb.put( 5, minZ);
                    fb.put(6, maxX); fb.put( 7, maxY); fb.put( 8, minZ);
                    fb.put(9, maxX); fb.put(10, minY); fb.put(11, minZ);
                    fb.position(12);
                }
                gca_FboVerticesAttr.seal(true);
                matP.setToOrtho(minX, maxX, minY, maxY, -1, 1);
                useShaderProgram(gl, renderer, curRenderModes, true);
                renderRegion2FBO(gl, rs, curRenderModes,
                                 // (int)drawWinBox.getMinX(), (int)drawWinBox.getMinY(), targetFboWidth, targetFboHeight,
                                 0, 0, targetFboWidth, targetFboHeight,
                                 vpWidth, vpHeight);
            } else if( isStateDirty() ) {
                useShaderProgram(gl, renderer, curRenderModes, true);
                renderRegion2FBO(gl, rs, curRenderModes,
                                 // (int)drawWinBox.getMinX(), (int)drawWinBox.getMinY(), targetFboWidth, targetFboHeight,
                                 0, 0, targetFboWidth, targetFboHeight,
                                 vpWidth, vpHeight);
            }
            useShaderProgram(gl, renderer, curRenderModes, false);
            renderFBO(gl, rs, vpWidth, vpHeight);
        }
    }

    private void renderFBO(final GL2ES2 gl, final RenderState rs, final int width, final int height) {
        gl.glViewport(0, 0, width, height);

        if( rs.hintBitsSet(RenderState.BITHINT_BLENDING_ENABLED | RenderState.BITHINT_GLOBAL_DEPTH_TEST_ENABLED) ) {
            // BITHINT_GLOBAL_DEPTH_TEST_ENABLED path added by commit 45395696c252c215a8a22d05e5da7e98c662d07e (2014-04-10),
            // but not in VBORegion2PVBAA.renderVBO(..) -> investigate if required for working MSAA:
            // - "VBORegion2PMSAAES2 enables/disables GL_DEPTH_TEST, otherwise MSAA is corrupt"
            //
            // RGB is already multiplied w/ alpha via renderRegion2FBO(..)
            gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
            gl.glEnable(GL.GL_DEPTH_TEST);

        } else if( rs.hintBitsSet(RenderState.BITHINT_BLENDING_ENABLED) ) {
            // RGB is already multiplied w/ alpha via renderRegion2FBO(..)
            gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
        }

        {
            final Frustum f = rs.getClipFrustum();
            if( null != f ) {
                f.getPlanes(clipFrustum, 0);
                gl.glUniform(gcu_ClipFrustum); // Always update, since program maybe used by multiple regions
            }
        }

        gl.glActiveTexture(GL.GL_TEXTURE0 + gcu_FboTexUnit.intValue());

        fbo.use(gl, texA);
        gca_FboVerticesAttr.enableBuffer(gl, true);
        gca_FboTexCoordsAttr.enableBuffer(gl, true);
        indicesFbo.bindBuffer(gl, true); // keeps VBO binding

        gl.glDrawElements(GL.GL_TRIANGLES, indicesFbo.getElemCount() * indicesFbo.getCompsPerElem(), GL.GL_UNSIGNED_SHORT, 0);

        indicesFbo.bindBuffer(gl, false);
        gca_FboTexCoordsAttr.enableBuffer(gl, false);
        gca_FboVerticesAttr.enableBuffer(gl, false);
        fbo.unuse(gl);

        // setback: gl.glActiveTexture(currentActiveTextureEngine[0]);
    }

    private void renderRegion2FBO(final GL2ES2 gl, final RenderState rs, final int curRenderModes,
                                  final int fboX, final int fboY, final int targetFboWidth, final int targetFboHeight,
                                  final int vpWidth, final int vpHeight) {
        if( 0 >= targetFboWidth || 0 >= targetFboHeight ) {
            throw new IllegalArgumentException("targetFBOSize "+targetFboWidth+"x"+targetFboHeight+" must be greater than 0");
        }
        final boolean blendingEnabled = rs.hintBitsSet(RenderState.BITHINT_BLENDING_ENABLED);
        final int targetFboSamples = rs.getSampleCount() > 1 ? rs.getSampleCount() : 0;
        final boolean fboSampleTypeMatch;
        {
            final int oldSampleCount = null != fbo ? fbo.getNumSamples() : 0;
            fboSampleTypeMatch = ( oldSampleCount > 1 && targetFboSamples > 1 ) || ( oldSampleCount <= 1 && targetFboSamples <= 1 );
        }

        if(null == fbo || !fboSampleTypeMatch) {
            if( null != fbo ) {
                fbo.destroy(gl);
            }
            fboWidth  = targetFboWidth;
            fboHeight  = targetFboHeight;
            fbo = new FBObject();
            fbo.init(gl, targetFboWidth, targetFboHeight, targetFboSamples);
            rs.setSampleCount( Math.max(1, fbo.getNumSamples()) );
            if( 0 == targetFboSamples ) {
                texA = fbo.attachTexture2D(gl, 0, true, GL.GL_LINEAR, GL.GL_LINEAR, GL.GL_CLAMP_TO_EDGE, GL.GL_CLAMP_TO_EDGE);
                if( !blendingEnabled ) {
                    // no depth-buffer w/ blending
                    fbo.attachRenderbuffer(gl, Attachment.Type.DEPTH, FBObject.DEFAULT_BITS);
                }
            } else {
                fbo.attachColorbuffer(gl, 0, true);
                if( !blendingEnabled ) {
                    // no depth-buffer w/ blending
                    fbo.attachRenderbuffer(gl, Attachment.Type.DEPTH, FBObject.DEFAULT_BITS);
                }
                final FBObject ssink = new FBObject();
                {
                    ssink.init(gl, targetFboWidth, targetFboHeight, 0);
                    texA = ssink.attachTexture2D(gl, 0, true, GL.GL_NEAREST, GL.GL_NEAREST, GL.GL_CLAMP_TO_EDGE, GL.GL_CLAMP_TO_EDGE);
                    if( !blendingEnabled ) {
                        // no depth-buffer w/ blending
                        ssink.attachRenderbuffer(gl, Attachment.Type.DEPTH, FBObject.DEFAULT_BITS);
                    }
                }
                fbo.setSamplingSink(ssink);
                fbo.resetSamplingSink(gl); // validate
            }
            if( DEBUG_FBO_1 ) {
                System.err.printf("XXX.createFBO: blending %b, %dx%d%n%s%n", blendingEnabled, targetFboWidth, targetFboHeight, fbo.toString());
            }
        } else if( targetFboWidth != fboWidth || targetFboHeight != fboHeight || fbo.getNumSamples() != targetFboSamples ) {
            fbo.reset(gl, targetFboWidth, targetFboHeight, targetFboSamples);
            rs.setSampleCount( Math.max(1, fbo.getNumSamples()) );
            if( DEBUG_FBO_1 ) {
                System.err.printf("XXX.resetFBO: %dx%d -> %dx%d%n%s%n", fboWidth, fboHeight, targetFboWidth, targetFboHeight, fbo );
            }
            fboWidth  = targetFboWidth;
            fboHeight  = targetFboHeight;
        }
        fbo.bind(gl);

        //render texture
        gl.glViewport(fboX, fboY, targetFboWidth, targetFboHeight);
        if( blendingEnabled ) {
            gl.glClearColor(0f, 0f, 0f, 0.0f);
            gl.glClear(GL.GL_COLOR_BUFFER_BIT); // no depth-buffer w/ blending
            // For already pre-multiplied alpha values, use:
            // gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);

            // Multiply RGB w/ Alpha, preserve alpha for renderFBO(..)
            gl.glBlendFuncSeparate(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA, GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);

            if( rs.hintBitsSet(RenderState.BITHINT_GLOBAL_DEPTH_TEST_ENABLED) ) {
                gl.glDisable(GL.GL_DEPTH_TEST);
            }
        } else {
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        }

        renderRegion(gl, rs, curRenderModes);

        if( SCREENSHOT_FBO ) {
            screenshot.setReadBuffer(GL.GL_COLOR_ATTACHMENT0);
            if(screenshot.readPixels(gl, fboX, fboY, targetFboWidth, targetFboHeight, false)) {
                final File f = new File("screenshot_vboregion2pmsaa.png");
                screenshot.write(f);
            }
        }

        fbo.unbind(gl);
        fboDirty = false;
    }

    private void renderRegion(final GL2ES2 gl, final RenderState rs, final int curRenderModes) {
        // final boolean hasColorChannel = Region.hasColorChannel( curRenderModes );
        final boolean hasColorTexture = Region.hasColorTexture( curRenderModes );

        gl.glUniform(gcu_PMVMatrix02);

        vpc_ileave.enableBuffer(gl, true);
        indicesBuffer.bindBuffer(gl, true); // keeps VBO binding
        if( hasColorTexture && null != gcu_ColorTexUnit && colorTexSeq.isTextureAvailable() ) {
            final TextureSequence.TextureFrame frame = colorTexSeq.getNextTexture(gl);
            gl.glActiveTexture(GL.GL_TEXTURE0 + colorTexSeq.getTextureUnit());
            final Texture tex = frame.getTexture();
            tex.bind(gl);
            tex.enable(gl); // nop on core
            gcu_ColorTexUnit.setData(colorTexSeq.getTextureUnit());
            gl.glUniform(gcu_ColorTexUnit); // Always update, since program maybe used by multiple regions
            gl.glUniform(gcu_ColorTexBBox); // Always update, since program maybe used by multiple regions
            gl.glUniform(gcu_ColorTexClearCol); // Always update, since program maybe used by multiple regions
            gl.glDrawElements(GL.GL_TRIANGLES, indicesBuffer.getElemCount() * indicesBuffer.getCompsPerElem(), glIdxType(), 0);
            tex.disable(gl); // nop on core
        } else {
            gl.glDrawElements(GL.GL_TRIANGLES, indicesBuffer.getElemCount() * indicesBuffer.getCompsPerElem(), glIdxType(), 0);
        }

        indicesBuffer.bindBuffer(gl, false);
        vpc_ileave.enableBuffer(gl, false);
    }

    @Override
    protected void destroyImpl(final GL2ES2 gl) {
        if(DEBUG_INSTANCE) {
            System.err.println("VBORegion2PES2 Destroy: " + this);
        }
        if(null != fbo) {
            fbo.destroy(gl);
            fbo = null;
            texA = null;
        }
        if(null != gca_FboVerticesAttr) {
            gca_FboVerticesAttr.destroy(gl);
            gca_FboVerticesAttr = null;
        }
        if(null != gca_FboTexCoordsAttr) {
            gca_FboTexCoordsAttr.destroy(gl);
            gca_FboTexCoordsAttr = null;
        }
        if(null != indicesFbo) {
            indicesFbo.destroy(gl);
            indicesFbo = null;
        }
        if( null != screenshot ) {
            screenshot.dispose(gl);
            screenshot = null;
        }
        spPass1 = null; // owned by RegionRenderer
        spPass2 = null; // owned by RegionRenderer
    }
}
