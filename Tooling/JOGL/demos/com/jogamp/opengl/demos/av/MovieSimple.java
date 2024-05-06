/**
 * Copyright 2012-2023 JogAmp Community. All rights reserved.
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

package com.jogamp.opengl.demos.av;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.jogamp.common.av.PTS;
import com.jogamp.common.net.Uri;
import com.jogamp.common.os.Clock;
import com.jogamp.common.util.InterruptSource;
import com.jogamp.graph.curve.Region;
import com.jogamp.graph.curve.opengl.GLRegion;
import com.jogamp.graph.curve.opengl.RegionRenderer;
import com.jogamp.graph.font.Font;
import com.jogamp.graph.font.FontScale;
import com.jogamp.newt.Window;
import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLRunnable;
import com.jogamp.opengl.JoglVersion;
import com.jogamp.opengl.demos.es2.TextureSequenceES2;
import com.jogamp.opengl.demos.graph.TextRendererGLELBase;
import com.jogamp.opengl.demos.util.MiscUtils;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLReadBufferUtil;
import com.jogamp.opengl.util.av.GLMediaPlayer;
import com.jogamp.opengl.util.av.GLMediaPlayer.GLMediaEventListener;
import com.jogamp.opengl.util.av.GLMediaPlayerFactory;
import com.jogamp.opengl.util.texture.TextureSequence.TextureFrame;

/**
 * Simple planar movie player w/ orthogonal 1:1 projection.
 */
public class MovieSimple implements GLEventListener {
    public static final int EFFECT_NORMAL                  =    0;
    public static final int EFFECT_GRADIENT_BOTTOM2TOP     = 1<<1;
    public static final int EFFECT_TRANSPARENT             = 1<<3;

    public static final String WINDOW_KEY = "window";
    public static final String PLAYER = "player";

    private static boolean waitForKey = false;
    private int surfWidth, surfHeight;
    private int prevMouseX; // , prevMouseY;
    private boolean  orthoProjection = true;
    private float zoom0;
    private float zoom1;
    private float zoom;
    private int effects = EFFECT_NORMAL;
    private float alpha = 1.0f;
    private int swapInterval = 1;
    private boolean swapIntervalSet = true;

    private TextureSequenceES2 screen=null;
    private GLMediaPlayer mPlayer;
    private final boolean mPlayerShared;
    private boolean useOriginalScale;
    private volatile boolean resetGLState = false;

    private volatile GLAutoDrawable autoDrawable = null;

    /** Blender's Big Buck Bunny: 24f 416p H.264,  AAC 48000 Hz, 2 ch, mpeg stream. */
    public static final Uri defURI;
    static {
        Uri _defURI = null;
        try {
            // Blender's Big Buck Bunny Trailer: 24f 640p VP8, Vorbis 44100Hz mono, WebM/Matroska Stream.
            // _defURI = new URI("http://video.webmfiles.org/big-buck-bunny_trailer.webm");
            _defURI = Uri.cast("http://archive.org/download/BigBuckBunny_328/BigBuckBunny_512kb.mp4");
        } catch (final URISyntaxException e) {
            e.printStackTrace();
        }
        defURI = _defURI;
    }

    private final class InfoTextRendererGLELBase extends TextRendererGLELBase {
        private final Font font = getFont(0, 0, 0);
        private final float fontSize = 10f;
        private final GLRegion regionFPS;

        InfoTextRendererGLELBase(final GLProfile glp, final int rmode) {
            // FIXME: Graph TextRenderer does not AA well w/o MSAA and FBO
            super(rmode, Region.DEFAULT_AA_SAMPLE_COUNT);
            this.setRendererCallbacks(RegionRenderer.defaultBlendEnable, RegionRenderer.defaultBlendDisable);
            regionFPS = GLRegion.create(glp, renderModes, null, 0, 0);
            System.err.println("RegionFPS "+Region.getRenderModeString(renderModes)+", sampleCount "+Region.DEFAULT_AA_SAMPLE_COUNT+", class "+regionFPS.getClass().getName());
            staticRGBAColor[0] = 0.9f;
            staticRGBAColor[1] = 0.9f;
            staticRGBAColor[2] = 0.9f;
            staticRGBAColor[3] = 1.0f;
        }

        @Override
        public void init(final GLAutoDrawable drawable) {
            super.init(drawable);
        }

        @Override
        public void dispose(final GLAutoDrawable drawable) {
            if( null != regionFPS ) {
                regionFPS.destroy(drawable.getGL().getGL2ES2());
            }
            super.dispose(drawable);
        }

        String text1_old = null;

        @Override
        public void display(final GLAutoDrawable drawable) {
            final GLAnimatorControl anim = drawable.getAnimator();
            final float lfps = null != anim ? anim.getLastFPS() : 0f;
            final float tfps = null != anim ? anim.getTotalFPS() : 0f;
            final long currentMillis = Clock.currentMillis();
            final PTS scr = mPlayer.getPTS();
            final float pts_s = scr.get(currentMillis) / 1000f;
            final float now = currentMillis / 1000f;

            // Note: MODELVIEW is from [ 0 .. height ]

            final int height = drawable.getSurfaceHeight();

            final float aspect = (float)mPlayer.getWidth() / (float)mPlayer.getHeight();

            final String ptsPrec = null != regionFPS ? "3.1" : "3.0";
            final String text1 = String.format("%0"+ptsPrec+"f/%0"+ptsPrec+"f/%0"+ptsPrec+"f s, %s (%01.2fx, vol %01.2f), a %01.2f, fps %02.1f -> %02.1f / %02.1f, swap %d",
                    now, pts_s, mPlayer.getDuration() / 1000f,
                    mPlayer.getState().toString().toLowerCase(), mPlayer.getPlaySpeed(), mPlayer.getAudioVolume(),
                    aspect, mPlayer.getFramerate(), lfps, tfps, drawable.getGL().getSwapInterval());
            final String text2 = String.format("audio: id %d, kbps %d, codec %s",
                    mPlayer.getAID(), mPlayer.getAudioBitrate()/1000, mPlayer.getAudioCodec());
            final String text3 = String.format("video: id %d, kbps %d, codec %s",
                    mPlayer.getVID(), mPlayer.getVideoBitrate()/1000, mPlayer.getVideoCodec());
            final String text4 = mPlayer.getUri().path.decode();
            if( displayOSD && null != renderer ) {
                // We share ClearColor w/ MovieSimple's init !
                final float pixelSize = FontScale.toPixels(fontSize, dpiH);
                final GL2ES2 gl = drawable.getGL().getGL2ES2();
                if( !text1.equals(text1_old) ) {
                    renderString(drawable, font, pixelSize, text1, 1 /* col */,  1 /* row */, 0,      0, -1, regionFPS.clear(gl)); // clear-cache
                    text1_old = text1;
                } else {
                    renderRegion(drawable, font, pixelSize, 1 /* col */,  1 /* row */, 0,      0, -1, regionFPS);
                }
                renderString(drawable, font, pixelSize, text2, 1 /* col */, -4 /* row */, 0, height, -1, true);
                renderString(drawable, font, pixelSize, text3, 1 /* col */, -3 /* row */, 0, height, -1, true);
                renderString(drawable, font, pixelSize, text4, 1 /* col */, -2 /* row */, 0, height, -1, true);
            }
        } };
    private InfoTextRendererGLELBase textRendererGLEL = null;
    private boolean displayOSD = true;

    public void printScreen(final GLAutoDrawable drawable) throws GLException, IOException {
        final String filename = String.format("MovieSimple-snap%02d-%03dx%03d.png", screenshot_num++, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
        if(screenshot.readPixels(drawable.getGL(), false)) {
            screenshot.write(new File(filename.toString()));
        }
    }
    private final GLReadBufferUtil screenshot;
    private int screenshot_num = 0;

    public void printScreenOnGLThread(final GLAutoDrawable drawable) {
        drawable.invoke(false, new GLRunnable() {
            @Override
            public boolean run(final GLAutoDrawable drawable) {
                try {
                    printScreen(drawable);
                } catch (final GLException e) {
                    e.printStackTrace();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    private final MouseListener mouseAction = new MouseAdapter() {

        @Override
        public void mouseClicked(final MouseEvent e) {
            if(null != autoDrawable && e.getClickCount() == 3 ) {
                MiscUtils.destroyWindow(autoDrawable);
            }
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            if(e.getY()<=surfHeight/2 && null!=mPlayer && 1 == e.getClickCount()) {
                if(GLMediaPlayer.State.Playing == mPlayer.getState()) {
                    mPlayer.pause(false);
                } else {
                    mPlayer.resume();
                }
            }
        }
        @Override
        public void mouseReleased(final MouseEvent e) {
            if(e.getY()<=surfHeight/2) {
                zoom = zoom0;
                if( null != screen ) {
                    screen.setZoom(zoom);
                    screen.setZRotation(-1f);
                }
                System.err.println("zoom: "+zoom);
            }
        }
        @Override
        public void mouseMoved(final MouseEvent e) {
            prevMouseX = e.getX();
            // prevMouseY = e.getY();
        }
        @Override
        public void mouseDragged(final MouseEvent e) {
            final int x = e.getX();
            final int y = e.getY();

            if(y>surfHeight/2) {
                final float dp  = (float)(x-prevMouseX)/(float)surfWidth;
                final int pts0 = mPlayer.getPTS().getCurrent();
                mPlayer.seek(pts0 + (int) (mPlayer.getDuration() * dp));
            } else {
                mPlayer.resume();
                zoom = zoom1;
                if( null != screen ) {
                    screen.setZoom(zoom);
                    screen.setZRotation(1f);
                }
            }

            prevMouseX = x;
            // prevMouseY = y;
        }
        @Override
        public void mouseWheelMoved(final MouseEvent e) {
            if( !e.isShiftDown() ) {
                zoom += e.getRotation()[1]/10f; // vertical: wheel
                if( null != screen ) {
                    screen.setZoom(zoom);
                }
                System.err.println("zoom: "+zoom);
            }
        } };

    private final KeyListener keyAction = new KeyAdapter() {
        @Override
        public void keyReleased(final KeyEvent e)  {
            if( e.isAutoRepeat() ) {
                return;
            }
            System.err.println("MC "+e);
            final int pts0 = mPlayer.getPTS().getCurrent();
            int pts1 = 0;
            switch(e.getKeySymbol()) {
                case KeyEvent.VK_V: {
                    switch(swapInterval) {
                        case  0: swapInterval = -1; break;
                        case -1: swapInterval =  1; break;
                        case  1: swapInterval =  0; break;
                        default: swapInterval =  1; break;
                    }
                    swapIntervalSet = true;
                    break;
                }
                case KeyEvent.VK_O:          displayOSD = !displayOSD; break;
                case KeyEvent.VK_RIGHT:      pts1 = pts0 +  1000; break;
                case KeyEvent.VK_UP:         pts1 = pts0 + 10000; break;
                case KeyEvent.VK_PAGE_UP:    pts1 = pts0 + 30000; break;
                case KeyEvent.VK_LEFT:       pts1 = pts0 -  1000; break;
                case KeyEvent.VK_DOWN:       pts1 = pts0 - 10000; break;
                case KeyEvent.VK_PAGE_DOWN:  pts1 = pts0 - 30000; break;
                case KeyEvent.VK_HOME:
                case KeyEvent.VK_BACK_SPACE: {
                    System.err.println("Seek: "+pts0+" -> 0");
                    mPlayer.seek(0);
                    break;
                }
                case KeyEvent.VK_SPACE: {
                    if( GLMediaPlayer.State.Paused == mPlayer.getState() ) {
                        mPlayer.resume();
                    } else if(GLMediaPlayer.State.Uninitialized == mPlayer.getState()) {
                        playStream(mPlayer.getUri(), GLMediaPlayer.STREAM_ID_AUTO, GLMediaPlayer.STREAM_ID_AUTO, 3 /* textureCount */);
                    } else if( e.isShiftDown() ) {
                        mPlayer.stop();
                    } else {
                        mPlayer.pause(false);
                    }
                    break;
                }
                case KeyEvent.VK_MULTIPLY:
                      mPlayer.setPlaySpeed(1.0f);
                      break;
                case KeyEvent.VK_SUBTRACT: {
                      float playSpeed = mPlayer.getPlaySpeed();
                      if( e.isShiftDown() ) {
                          playSpeed /= 2.0f;
                      } else {
                          playSpeed -= 0.1f;
                      }
                      mPlayer.setPlaySpeed(playSpeed);
                    } break;
                case KeyEvent.VK_ADD: {
                      float playSpeed = mPlayer.getPlaySpeed();
                      if( e.isShiftDown() ) {
                          playSpeed *= 2.0f;
                      } else {
                          playSpeed += 0.1f;
                      }
                      mPlayer.setPlaySpeed(playSpeed);
                    } break;
                case KeyEvent.VK_M: {
                      float audioVolume = mPlayer.getAudioVolume();
                      if( audioVolume > 0.5f ) {
                          audioVolume = 0f;
                      } else {
                          audioVolume = 1f;
                      }
                      mPlayer.setAudioVolume(audioVolume);
                    } break;
                case KeyEvent.VK_S:
                    if(null != autoDrawable) {
                        printScreenOnGLThread(autoDrawable);
                    }
                    break;
                case KeyEvent.VK_F4:
                case KeyEvent.VK_ESCAPE:
                case KeyEvent.VK_Q:
                    if(null != autoDrawable) {
                        MiscUtils.destroyWindow(autoDrawable);
                    }
                    break;
            }

            if( 0 != pts1 ) {
                System.err.println("Seek: "+pts0+" -> "+pts1);
                mPlayer.seek(pts1);
            }
        } };

    /**
     * Default constructor which also issues {@link #playStream(URI, int, int, int)} w/ default values
     * and polls until the {@link GLMediaPlayer} is {@link GLMediaPlayer.State#Initialized}.
     * If {@link GLMediaEventListener#EVENT_CHANGE_EOS} is reached, the stream is started over again.
     * <p>
     * This default constructor is merely useful for some <i>drop-in</i> test, e.g. using an applet.
     * </p>
     */
    public MovieSimple() {
        this(null);

        mPlayer.addEventListener(new GLMediaEventListener() {
            @Override
            public void attributesChanged(final GLMediaPlayer mp, final GLMediaPlayer.EventMask eventMask, final long when) {
                System.err.println("MovieSimple.0 AttributesChanges: "+eventMask+", when "+when);
                System.err.println("MovieSimple.0 State: "+mp);
                if( eventMask.isSet(GLMediaPlayer.EventMask.Bit.EOS) ) {
                    new InterruptSource.Thread() {
                        @Override
                        public void run() {
                            // loop for-ever ..
                            mPlayer.seek(0);
                            mPlayer.resume();
                        } }.start();
                }
            }
        });
        playStream(defURI, GLMediaPlayer.STREAM_ID_AUTO, GLMediaPlayer.STREAM_ID_AUTO, 3 /* textureCount */);
    }

    /** Custom constructor, user needs to issue {@link #playStream(URI, int, int, int)} afterwards. */
    public MovieSimple(final GLMediaPlayer sharedMediaPlayer) throws IllegalStateException {
        screenshot = new GLReadBufferUtil(false, false);
        mPlayer = sharedMediaPlayer;
        mPlayerShared = null != mPlayer;
        useOriginalScale = false;
        if( !mPlayerShared ) {
            mPlayer = GLMediaPlayerFactory.createDefault();
            mPlayer.attachObject(PLAYER, this);
        }
        System.out.println("pC.1a shared "+mPlayerShared+", "+mPlayer);
    }

    public void playStream(final Uri streamLoc, final int vid, final int aid, final int textureCount) {
        mPlayer.playStream(streamLoc, vid, aid, GLMediaPlayer.STREAM_ID_NONE, textureCount);
        System.out.println("pC.1b "+mPlayer);
    }

    public void setSwapInterval(final int v) { this.swapInterval = v; }

    public void setUseOriginalScale(final boolean v) {
        useOriginalScale = v;
    }

    public GLMediaPlayer getGLMediaPlayer() { return mPlayer; }

    /** defaults to true */
    public void setOrthoProjection(final boolean v) { orthoProjection=v; }
    public boolean getOrthoProjection() { return orthoProjection; }

    public void setEffects(final int e) { effects = e; };
    public void setTransparency(final float alpha) { this.alpha = alpha; }

    public void resetGLState() {
        resetGLState = true;
    }

    @Override
    public void init(final GLAutoDrawable drawable) {
        if(null == mPlayer) {
            throw new InternalError("mPlayer null");
        }
        // final boolean hasVideo = GLMediaPlayer.STREAM_ID_NONE != mPlayer.getVID();
        resetGLState = false;

        zoom0 =  orthoProjection ? 0f : -2.5f;
        zoom1 = orthoProjection ? 0f : -5f;
        zoom = zoom0;

        autoDrawable = drawable;

        final GL2ES2 gl = drawable.getGL().getGL2ES2();
        System.err.println(JoglVersion.getGLInfo(gl, null));
        System.err.println("Alpha: "+alpha+", opaque "+drawable.getChosenGLCapabilities().isBackgroundOpaque()+
                           ", "+drawable.getClass().getName()+", "+drawable);

        screen = new TextureSequenceES2(mPlayer, mPlayerShared, orthoProjection, zoom);
        screen.setEffects(effects);
        screen.setTransparency(alpha);
        screen.setUseOriginalScale(useOriginalScale);

        if(waitForKey) {
            MiscUtils.waitForKey("Init>");
        }

        try {
            mPlayer.initGL(gl);
        } catch (final Exception e) {
            e.printStackTrace();
            if(null != mPlayer) {
                mPlayer.destroy(gl);
                mPlayer = null;
            }
            throw new GLException(e);
        }
        screen.init(drawable);

        final Object upstreamWidget = drawable.getUpstreamWidget();
        if (upstreamWidget instanceof Window) {
            final Window window = (Window) upstreamWidget;
            window.addMouseListener(mouseAction);
            window.addKeyListener(keyAction);
            surfWidth = window.getSurfaceWidth();
            surfHeight = window.getSurfaceHeight();
        }
        final int rmode = drawable.getChosenGLCapabilities().getSampleBuffers() ? 0 : Region.VBAA_RENDERING_BIT;
        textRendererGLEL = new InfoTextRendererGLELBase(gl.getGLProfile(), rmode);
        drawable.addGLEventListener(textRendererGLEL);
    }

    @Override
    public void reshape(final GLAutoDrawable drawable, final int x, final int y, final int width, final int height) {
        if(null == mPlayer) { return; }
        screen.reshape(drawable, x, y, width, height);
        surfWidth = width;
        surfHeight = height;
        System.out.println("pR "+mPlayer);
    }

    @Override
    public void dispose(final GLAutoDrawable drawable) {
        autoDrawable = null;
        screenshot.dispose(drawable.getGL());
        disposeImpl(drawable, true);
    }

    private void disposeImpl(final GLAutoDrawable drawable, final boolean disposePlayer) {
        if(null == mPlayer) { return; }

        final Object upstreamWidget = drawable.getUpstreamWidget();
        if (upstreamWidget instanceof Window) {
            final Window window = (Window) upstreamWidget;
            window.removeMouseListener(mouseAction);
            window.removeKeyListener(keyAction);
        }

        System.out.println("pD.1 "+mPlayer+", disposePlayer "+disposePlayer);
        final GL2ES2 gl = drawable.getGL().getGL2ES2();
        if( null != textRendererGLEL ) {
            drawable.disposeGLEventListener(textRendererGLEL, true);
            textRendererGLEL = null;
        }
        if( disposePlayer ) {
            if(!mPlayerShared) {
                mPlayer.destroy(gl);
            } else {
                // mPlayer.stop(gl);
            }
            System.out.println("pD.X "+mPlayer);
            mPlayer=null;
        }
        screen.dispose(drawable);
        screen = null;
    }

    long lastPerfPos = 0;

    @Override
    public void display(final GLAutoDrawable drawable) {
        final GL2ES2 gl = drawable.getGL().getGL2ES2();
        if( swapIntervalSet ) {
            final int _swapInterval = swapInterval;
            gl.setSwapInterval(_swapInterval); // in case switching the drawable (impl. may bound attribute there)
            if( null != drawable.getAnimator() ) {
                drawable.getAnimator().resetFPSCounter();
            }
            swapInterval = gl.getSwapInterval();
            System.err.println("Swap Interval: "+_swapInterval+" -> "+swapInterval);
            swapIntervalSet = false;
        }
        if(null == mPlayer) { return; }

        if( resetGLState ) {
            resetGLState = false;
            System.err.println("XXX resetGLState");
            disposeImpl(drawable, false);
            init(drawable);
            reshape(drawable, 0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
        }

        final long currentPos = System.currentTimeMillis();
        if( currentPos - lastPerfPos > 2000 ) {
            // System.err.println( mPlayer.getPerfString() );
            lastPerfPos = currentPos;
        }
        screen.display(drawable);
    }

    static class MyGLMediaEventListener implements GLMediaEventListener {
            void destroyWindow(final Window window) {
                new InterruptSource.Thread( () -> { window.destroy(); } ).start();
            }
            @Override
            public void attributesChanged(final GLMediaPlayer mp, final GLMediaPlayer.EventMask eventMask, final long when) {
                System.err.println("MovieSimple.1 AttributesChanges: "+eventMask+", when "+when);
                System.err.println("MovieSimple.1 State: "+mp);
                final GLWindow window = (GLWindow) mp.getAttachedObject(WINDOW_KEY);
                final MovieSimple ms = (MovieSimple)mp.getAttachedObject(PLAYER);
                if( eventMask.isSet(GLMediaPlayer.EventMask.Bit.Size) ) {
                    if( origSize ) {
                        window.setSurfaceSize(mp.getWidth(), mp.getHeight());
                    }
                    // window.disposeGLEventListener(ms, false /* remove */ );
                    // ms.resetGLState();
                }
                if( eventMask.isSet(GLMediaPlayer.EventMask.Bit.Init) ) {
                    // Use GLEventListener in all cases [A+V, V, A]
                    final GLAnimatorControl anim = window.getAnimator();
                    anim.setUpdateFPSFrames(60, null);
                    anim.resetFPSCounter();
                    ms.resetGLState();

                    /**
                     * Kick off player w/o GLEventListener, i.e. for audio only.
                     *
                        new InterruptSource.Thread() {
                            public void run() {
                                try {
                                    mp.initGL(null);
                                    if ( GLMediaPlayer.State.Paused == mp.getState() ) { // init OK
                                        mp.play();
                                    }
                                    System.out.println("play.1 "+mp);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    destroyWindow();
                                    return;
                                }
                            }
                        }.start();
                    */
                } else if( eventMask.isSet(GLMediaPlayer.EventMask.Bit.Play) ) {
                    window.getAnimator().resetFPSCounter();
                }

                boolean destroy = false;
                Throwable err = null;

                if( eventMask.isSet(GLMediaPlayer.EventMask.Bit.EOS) ) {
                    err = ms.mPlayer.getStreamException();
                    if( null != err ) {
                        System.err.println("MovieSimple State: Exception");
                        destroy = true;
                    } else {
                        if( loopEOS ) {
                            new InterruptSource.Thread() {
                                @Override
                                public void run() {
                                    mp.setPlaySpeed(1f);
                                    mp.seek(0);
                                    mp.resume();
                                }
                            }.start();
                        } else {
                            destroy = true;
                        }
                    }
                }
                if( eventMask.isSet(GLMediaPlayer.EventMask.Bit.Error) ) {
                    err = ms.mPlayer.getStreamException();
                    if( null != err ) {
                        System.err.println("MovieSimple State: Exception");
                    }
                    destroy = true;
                }
                if( destroy ) {
                    if( null != err ) {
                        err.printStackTrace();
                    }
                    destroyWindow(window);
                }
            }
        };
    public final static MyGLMediaEventListener myGLMediaEventListener = new MyGLMediaEventListener();

    static boolean loopEOS = false;
    static boolean origSize;

    public static void main(final String[] args) throws IOException, URISyntaxException {
        int swapInterval = 1;
        float playSpeed = 1.0f;
        int width = 800;
        int height = 600;
        int textureCount = 3; // default - threaded
        boolean ortho = true;
        boolean useOrigScale = false;

        boolean forceES2 = false;
        boolean forceES3 = false;
        boolean forceGL3 = false;
        boolean forceGLDef = false;
        int vid = GLMediaPlayer.STREAM_ID_AUTO;
        int aid = GLMediaPlayer.STREAM_ID_AUTO;
        float audioVolume = 1.0f;

        final int windowCount;
        {
            int _windowCount = 0;
            for(int i=0; i<args.length; i++) {
                if(args[i].equals("-url")) {
                    i++;
                    _windowCount++;
                }
            }
            windowCount = Math.max(1, _windowCount);
        }
        final String[] urls_s = new String[windowCount];
        {
            boolean _origSize = false;
            int url_idx = 0;
            for(int i=0; i<args.length; i++) {
                if(args[i].equals("-vid")) {
                    i++;
                    vid = MiscUtils.atoi(args[i], vid);
                } else if(args[i].equals("-aid")) {
                    i++;
                    aid = MiscUtils.atoi(args[i], aid);
                } else if(args[i].equals("-width")) {
                    i++;
                    width = MiscUtils.atoi(args[i], width);
                } else if(args[i].equals("-height")) {
                    i++;
                    height = MiscUtils.atoi(args[i], height);
                } else if(args[i].equals("-osize")) {
                    _origSize = true;
                } else if(args[i].equals("-textureCount")) {
                    i++;
                    textureCount = MiscUtils.atoi(args[i], textureCount);
                } else if(args[i].equals("-es2")) {
                    forceES2 = true;
                } else if(args[i].equals("-es3")) {
                    forceES3 = true;
                } else if(args[i].equals("-gl3")) {
                    forceGL3 = true;
                } else if(args[i].equals("-gldef")) {
                    forceGLDef = true;
                } else if(args[i].equals("-vsync")) {
                    i++;
                    swapInterval = MiscUtils.atoi(args[i], swapInterval);
                } else if(args[i].equals("-speed")) {
                    i++;
                    playSpeed = MiscUtils.atof(args[i], playSpeed);
                } else if(args[i].equals("-mute")) {
                    audioVolume = 0.0f;
                } else if(args[i].equals("-projection")) {
                    ortho=false;
                } else if(args[i].equals("-orig_scale")) {
                    useOrigScale=true;
                } else if(args[i].equals("-loop")) {
                    loopEOS=true;
                } else if(args[i].equals("-url")) {
                    i++;
                    urls_s[url_idx++] = args[i];
                } else if(args[i].equals("-wait")) {
                    waitForKey = true;
                }
            }
            origSize = _origSize;
        }
        Uri streamLoc0 = null;
        if( null != urls_s[0] ) {
            streamLoc0 = Uri.tryUriOrFile( urls_s[0] );
        }
        if( null == streamLoc0 ) {
            streamLoc0 = defURI;
        }
        System.err.println("url_s "+urls_s[0]);
        System.err.println("stream0 "+streamLoc0);
        System.err.println("vid "+vid+", aid "+aid);
        System.err.println("textureCount "+textureCount);
        System.err.println("forceES2   "+forceES2);
        System.err.println("forceES3   "+forceES3);
        System.err.println("forceGL3   "+forceGL3);
        System.err.println("forceGLDef "+forceGLDef);
        System.err.println("swapInterval "+swapInterval);
        System.err.println("playSpeed "+playSpeed);
        System.err.println("audioVolume "+audioVolume);

        final GLProfile glp;
        if(forceGLDef) {
            glp = GLProfile.getDefault();
        } else if(forceGL3) {
            glp = GLProfile.get(GLProfile.GL3);
        } else if(forceES3) {
            glp = GLProfile.get(GLProfile.GLES3);
        } else if(forceES2) {
            glp = GLProfile.get(GLProfile.GLES2);
        } else {
            glp = GLProfile.getGL2ES2();
        }
        System.err.println("GLProfile: "+glp);
        final GLCapabilities caps = new GLCapabilities(glp);
        // caps.setAlphaBits(4); // NOTE_ALPHA_BLENDING: We go w/o alpha and blending!

        final MovieSimple[] mss = new MovieSimple[windowCount];
        final GLWindow[] windows = new GLWindow[windowCount];
        for(int i=0; i<windowCount; i++) {
            final Animator anim = new Animator(0 /* w/o AWT */);
            anim.start();
            windows[i] = GLWindow.create(caps);
            windows[i].addWindowListener(new WindowAdapter() {
                @Override
                public void windowDestroyed(final WindowEvent e) {
                    anim.stop();
                }
            });
            mss[i] = new MovieSimple(null);
            mss[i].setSwapInterval(swapInterval);
            mss[i].setUseOriginalScale(useOrigScale);
            mss[i].setOrthoProjection(ortho);
            mss[i].mPlayer.setPlaySpeed(playSpeed);
            mss[i].mPlayer.setAudioVolume(audioVolume);
            mss[i].mPlayer.attachObject(WINDOW_KEY, windows[i]);
            mss[i].mPlayer.addEventListener(myGLMediaEventListener);

            anim.add(windows[i]);
            windows[i].addGLEventListener(mss[i]);
            windows[i].setTitle("Player "+i);
            windows[i].setSize(width, height);
            windows[i].setVisible(true);

            Uri streamLocN = null;
            if( 0 == i ) {
                streamLocN = streamLoc0;
            } else {
                if( null != urls_s[i] ) {
                    streamLocN = Uri.tryUriOrFile( urls_s[i] );
                }
                if( null == streamLocN ) {
                    streamLocN = defURI;
                }
            }
            System.err.println("Win #"+i+": stream "+streamLocN);
            mss[i].playStream(streamLocN, vid, aid, textureCount);
        }
    }

}
