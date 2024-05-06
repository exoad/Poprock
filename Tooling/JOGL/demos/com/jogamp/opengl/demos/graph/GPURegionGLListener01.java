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

package com.jogamp.opengl.demos.graph;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.graph.curve.OutlineShape;
import com.jogamp.graph.curve.Region;
import com.jogamp.graph.curve.opengl.GLRegion;
import com.jogamp.graph.curve.opengl.RenderState;
import com.jogamp.math.Vec4f;
import com.jogamp.math.geom.plane.Path2F;
import com.jogamp.math.geom.plane.WindingRule;
import com.jogamp.math.util.PMVMatrix4f;
import com.jogamp.graph.curve.opengl.RegionRenderer;
import com.jogamp.opengl.util.PMVMatrix;

/** Demonstrate the rendering of multiple outlines into one region/OutlineShape
 *  These Outlines are not necessary connected or contained.
 *  The output of this demo shows two identical shapes but the left one
 *  has some vertices with off-curve flag set to true, and the right allt he vertices
 *  are on the curve. Demos the Res. Independent Nurbs based Curve rendering
 *
 */
public class GPURegionGLListener01 extends GPURendererListenerBase01 {
    final int shape_ctor_mode;
    OutlineShape outlineShape = null;

    public GPURegionGLListener01 (final int renderModes, final int sampleCount, final boolean debug, final boolean trace) {
        this(1, renderModes, Region.DEFAULT_AA_QUALITY, sampleCount, debug, trace);
    }

    public GPURegionGLListener01 (final int shape_ctor_mode, final int renderModes, final int aaQuality, final int sampleCount, final boolean debug, final boolean trace) {
        super(RegionRenderer.create(RegionRenderer.defaultBlendEnable, RegionRenderer.defaultBlendDisable), renderModes, debug, trace);
        this.shape_ctor_mode = shape_ctor_mode;
        this.getRenderer().setHintBits(RenderState.BITHINT_GLOBAL_DEPTH_TEST_ENABLED);
        setMatrix(-20, 00, -50, 0f);
        getRenderer().setAAQuality(aaQuality);
        getRenderer().setSampleCount(sampleCount);
    }

    private void createTestOutline00(){
        outlineShape.addVertex(0.0f,-10.0f, true);
        outlineShape.addVertex(15.0f,-10.0f, true);
        outlineShape.addVertex(10.0f,5.0f, false);
        outlineShape.addVertex(15.0f,10.0f, true);
        outlineShape.addVertex(6.0f,15.0f, false);
        outlineShape.addVertex(5.0f,8.0f, false);
        outlineShape.addVertex(0.0f,10.0f,true);
        outlineShape.closeLastOutline(true);
        outlineShape.addEmptyOutline();
        outlineShape.addVertex(5.0f,-5.0f,true);
        outlineShape.addVertex(10.0f,-5.0f, false);
        outlineShape.addVertex(10.0f,0.0f, true);
        outlineShape.addVertex(5.0f,0.0f, false);
        outlineShape.closeLastOutline(true);

        /** Same shape as above but without any off-curve vertices */
        final float offset = 30;
        outlineShape.addEmptyOutline();
        outlineShape.addVertex(offset+0.0f,-10.0f, true);
        outlineShape.addVertex(offset+17.0f,-10.0f, true);
        outlineShape.addVertex(offset+11.0f,5.0f, true);
        outlineShape.addVertex(offset+16.0f,10.0f, true);
        outlineShape.addVertex(offset+7.0f,15.0f, true);
        outlineShape.addVertex(offset+6.0f,8.0f, true);
        outlineShape.addVertex(offset+0.0f,10.0f, true);
        outlineShape.closeLastOutline(true);
        outlineShape.addEmptyOutline();
        outlineShape.addVertex(offset+5.0f,0.0f, true);
        outlineShape.addVertex(offset+5.0f,-5.0f, true);
        outlineShape.addVertex(offset+10.0f,-5.0f, true);
        outlineShape.addVertex(offset+10.0f,0.0f, true);
        outlineShape.closeLastOutline(true);
    }

    private void createTestOutline01(){
        outlineShape.moveTo(0.0f,-10.0f, 0f);
        outlineShape.lineTo(15.0f,-10.0f, 0f);
        outlineShape.quadTo(10.0f,5.0f,0f, 15.0f,10.0f,0f);
        outlineShape.cubicTo(6.0f,15.0f,0f, 5.0f,8.0f,0f, 0.0f,10.0f,0f);
        outlineShape.closePath();
        outlineShape.moveTo(5.0f,-5.0f,0f);
        outlineShape.quadTo(10.0f,-5.0f,0f, 10.0f,0.0f,0f);
        outlineShape.quadTo(5.0f,0.0f,0f, 5.0f,-5.0f,0f);
        outlineShape.closePath();

        /** Same shape as above but without any off-curve vertices */
        final float offset = 30;
        outlineShape.moveTo(offset+0.0f,-10.0f,0f);
        outlineShape.lineTo(offset+17.0f,-10.0f,0f);
        outlineShape.lineTo(offset+11.0f,5.0f,0f);
        outlineShape.lineTo(offset+16.0f,10.0f,0f);
        outlineShape.lineTo(offset+7.0f,15.0f,0f);
        outlineShape.lineTo(offset+6.0f,8.0f,0f);
        outlineShape.lineTo(offset+0.0f,10.0f,0f);
        outlineShape.closePath();
        outlineShape.moveTo(offset+5.0f,0.0f,0f);
        outlineShape.lineTo(offset+5.0f,-5.0f,0f);
        outlineShape.lineTo(offset+10.0f,-5.0f,0f);
        outlineShape.lineTo(offset+10.0f,0.0f,0f);
        outlineShape.closePath();
    }

    private void createTestOutline02(){
        final Path2F path = new Path2F(WindingRule.NON_ZERO);
        path.moveTo(0.0f,-10.0f);
        path.lineTo(15.0f,-10.0f);
        path.quadTo(10.0f,5.0f, 15.0f,10.0f);
        path.cubicTo(6.0f,15.0f, 5.0f,8.0f, 0.0f,10.0f);
        path.closePath();
        path.moveTo(5.0f,-5.0f);
        path.quadTo(10.0f,-5.0f, 10.0f,0.0f);
        path.quadTo(5.0f,0.0f, 5.0f,-5.0f);
        path.closePath();

        /** Same shape as above but without any off-curve vertices */
        final float offset = 30;
        path.moveTo(offset+0.0f,-10.0f);
        path.lineTo(offset+17.0f,-10.0f);
        path.lineTo(offset+11.0f,5.0f);
        path.lineTo(offset+16.0f,10.0f);
        path.lineTo(offset+7.0f,15.0f);
        path.lineTo(offset+6.0f,8.0f);
        path.lineTo(offset+0.0f,10.0f);
        path.closePath();
        path.moveTo(offset+5.0f,0.0f);
        path.lineTo(offset+5.0f,-5.0f);
        path.lineTo(offset+10.0f,-5.0f);
        path.lineTo(offset+10.0f,0.0f);
        path.closePath();

        System.err.println("GPURegionGLListener01.createTestOutline02.X: path "+path);
        path.printSegments(System.err);
        outlineShape.addPath(path, false /* connect */);
    }

    private void createTestOutline03(){
        {
            final Path2F path = new Path2F(WindingRule.NON_ZERO);
            path.moveTo(0.0f,-10.0f);
            path.lineTo(15.0f,-10.0f);
            path.quadTo(10.0f,5.0f, 15.0f,10.0f);
            path.cubicTo(6.0f,15.0f, 5.0f,8.0f, 0.0f,10.0f);
            path.closePath();
            System.err.println("GPURegionGLListener01.createTestOutline03.0: path "+path);
            path.printSegments(System.err);
            {
                final Path2F path2 = new Path2F(WindingRule.NON_ZERO);
                path2.moveTo(5.0f,-5.0f);
                path2.quadTo(10.0f,-5.0f, 10.0f,0.0f);
                path2.quadTo(5.0f,0.0f, 5.0f,-5.0f);
                path2.closePath();
                System.err.println("GPURegionGLListener01.createTestOutline03.0: path2 "+path2);
                path2.printSegments(System.err);
                path.append(path2, false);
                System.err.println("GPURegionGLListener01.createTestOutline03.1: path "+path);
                path.printSegments(System.err);
            }
            outlineShape.addPath(path, false /* connect */);
        }
        {
            /** Same shape as above but without any off-curve vertices */
            final float offset = 30;
            final Path2F path = new Path2F(WindingRule.NON_ZERO);
            path.moveTo(offset+0.0f,-10.0f);
            path.lineTo(offset+17.0f,-10.0f);
            path.lineTo(offset+11.0f,5.0f);
            path.lineTo(offset+16.0f,10.0f);
            path.lineTo(offset+7.0f,15.0f);
            path.lineTo(offset+6.0f,8.0f);
            path.lineTo(offset+0.0f,10.0f);
            path.closePath();
            path.moveTo(offset+5.0f,0.0f);
            path.lineTo(offset+5.0f,-5.0f);
            path.lineTo(offset+10.0f,-5.0f);
            path.lineTo(offset+10.0f,0.0f);
            path.closePath();
            System.err.println("GPURegionGLListener01.createTestOutline03.3: path "+path);
            path.printSegments(System.err);
            outlineShape.addPath(path, false /* connect */);
        }

    }

    private void createTestOutline04(){
        final Path2F path = new Path2F(WindingRule.NON_ZERO);

        path.moveTo(0.0f,10.0f);
        path.cubicTo(5.0f,8.0f, 6.0f,15.0f, 15.0f,10.0f);
        path.quadTo(10.0f,5.0f, 15.0f,-10.0f);
        path.lineTo(0.0f,-10.0f);
        path.closePath();
        path.moveTo(5.0f,-5.0f);
        path.quadTo(5.0f,0.0f, 10.0f,0.0f);
        path.quadTo(10.0f,-5.0f, 5.0f,-5.0f);
        path.closePath();

        /** Same shape as above but without any off-curve vertices */
        final float offset = 30;
        path.moveTo(offset+0.0f,10.0f);
        path.lineTo(offset+6.0f,8.0f);
        path.lineTo(offset+7.0f,15.0f);
        path.lineTo(offset+16.0f,10.0f);
        path.lineTo(offset+11.0f,5.0f);
        path.lineTo(offset+17.0f,-10.0f);
        path.lineTo(offset+0.0f,-10.0f);
        path.closePath();
        path.moveTo(offset+10.0f,0.0f);
        path.lineTo(offset+10.0f,-5.0f);
        path.lineTo(offset+5.0f,-5.0f);
        path.lineTo(offset+5.0f,0.0f);
        path.closePath();

        System.err.println("GPURegionGLListener01.createTestOutline04.X: path "+path);
        path.printSegments(System.err);
        outlineShape.addPathRev(path, false /* connect */);
    }

    @Override
    public void init(final GLAutoDrawable drawable) {
        super.init(drawable);

        final GL2ES2 gl = drawable.getGL().getGL2ES2();

        gl.setSwapInterval(1);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_BLEND);
        getRenderer().setColorStatic(0.0f, 0.0f, 0.0f, 1.0f);

        outlineShape = new OutlineShape();
        switch( shape_ctor_mode ) {
            case 0:
                createTestOutline00();
                break;
            case 2:
                createTestOutline02();
                break;
            case 3:
                createTestOutline03();
                break;
            case 4:
                createTestOutline04();
                break;
            default:
                createTestOutline01();
                break;
        }
        region = GLRegion.create(gl.getGLProfile(), getRenderModes(), null, outlineShape);
        region.addOutlineShape(outlineShape, null, region.hasColorChannel() ? getRenderer().getColorStatic(new Vec4f()) : null);
    }

    @Override
    public void display(final GLAutoDrawable drawable) {
        final GL2ES2 gl = drawable.getGL().getGL2ES2();

        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        final RegionRenderer regionRenderer = getRenderer();
        final PMVMatrix4f pmv = regionRenderer.getMatrix();
        pmv.loadMvIdentity();
        pmv.translateMv(getXTran(), getYTran(), getZTran());
        pmv.rotateMv(getAngleRad(), 0, 1, 0);
        if( weight != regionRenderer.getWeight() ) {
            regionRenderer.setWeight(weight);
        }
        regionRenderer.enable(gl, true);
        region.draw(gl, regionRenderer);
        regionRenderer.enable(gl, false);
    }
}
