/**
 * Copyright 2012-2024 JogAmp Community. All rights reserved.
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
package jogamp.opengl.util.av;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jogamp.nativewindow.AbstractGraphicsDevice;
import com.jogamp.nativewindow.DefaultGraphicsDevice;
import com.jogamp.nativewindow.NativeWindowFactory;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLES2;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;

import jogamp.opengl.Debug;

import com.jogamp.common.net.UriQueryProps;
import com.jogamp.common.nio.Buffers;
import com.jogamp.common.av.AudioSink;
import com.jogamp.common.av.PTS;
import com.jogamp.common.av.TimeFrameI;
import com.jogamp.common.net.Uri;
import com.jogamp.common.os.Clock;
import com.jogamp.common.util.IOUtil;
import com.jogamp.common.util.InterruptSource;
import com.jogamp.common.util.LFRingbuffer;
import com.jogamp.common.util.Ringbuffer;
import com.jogamp.common.util.TSPrinter;
import com.jogamp.common.util.WorkerThread;
import com.jogamp.math.FloatUtil;
import com.jogamp.math.Vec2i;
import com.jogamp.math.Vec4f;
import com.jogamp.opengl.GLExtensions;
import com.jogamp.opengl.util.av.SubtitleEventListener;
import com.jogamp.opengl.util.av.CodecID;
import com.jogamp.opengl.util.av.GLMediaPlayer;
import com.jogamp.opengl.util.av.SubTextEvent;
import com.jogamp.opengl.util.av.SubEmptyEvent;
import com.jogamp.opengl.util.av.SubBitmapEvent;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.TextureSequence;
import com.jogamp.opengl.util.texture.TextureSequence.TextureFrame;

/**
 * After object creation an implementation may customize the behavior:
 * <ul>
 *   <li>{@link #setDesTextureCount(int)}</li>
 *   <li>{@link #setTextureTarget(int)}</li>
 *   <li>{@link EGLMediaPlayerImpl#setEGLTexImageAttribs(boolean, boolean)}.</li>
 * </ul>
 *
 * <p>
 * See {@link GLMediaPlayer}.
 * </p>
 */
public abstract class GLMediaPlayerImpl implements GLMediaPlayer {
    private static final int STREAM_WORKER_DELAY = Debug.getIntProperty("jogl.debug.GLMediaPlayer.StreamWorker.delay", false, 0);
    private static final TSPrinter logout;
    private static final String unknown = "unknown";

    static {
        if( DEBUG || DEBUG_AVSYNC || DEBUG_NATIVE ) {
            logout = TSPrinter.stderr();
        } else {
            logout = null;
        }
    }

    private volatile State state;
    private final Object stateLock = new Object();
    private final AtomicBoolean oneVideoFrameAtPause = new AtomicBoolean(false);

    private int textureCount;
    private int textureTarget;
    private int textureFormat;
    private int textureInternalFormat;
    private int textureType;
    private int texUnit;
    private int userMaxChannels = -1; // not set

    private int textureFragmentShaderHashCode;

    private final int[] texMinMagFilter = { GL.GL_NEAREST, GL.GL_NEAREST };
    private final int[] texWrapST = { GL.GL_CLAMP_TO_EDGE, GL.GL_CLAMP_TO_EDGE };
    private boolean aRatioLbox = false;
    private final Vec4f aRatioLboxBackColor = new Vec4f();

    /** User requested URI stream location. */
    private Uri streamLoc;

    /**
     * In case {@link #streamLoc} is a {@link GLMediaPlayer#CameraInputScheme},
     * {@link #cameraPath} holds the URI's path portion
     * as parsed in {@link #playStream(Uri, int, int, int, int)}.
     * @see #cameraProps
     */
    protected Uri.Encoded cameraPath = null;
    /** Optional camera properties, see {@link #cameraPath}. */
    protected Map<String, String> cameraProps = null;

    private volatile float playSpeed = 1.0f;
    private float audioVolume = 1.0f;

    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private String title = "undef";
    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private int[] v_streams = new int[0];
    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private String[] v_langs = new String[0];
    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private int vid = GLMediaPlayer.STREAM_ID_NONE;
    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private int[] a_streams = new int[0];
    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private String[] a_langs = new String[0];
    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private int aid = GLMediaPlayer.STREAM_ID_NONE;
    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private int[] s_streams = new int[0];
    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private String[] s_langs = new String[0];
    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private int sid = GLMediaPlayer.STREAM_ID_NONE;
    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private int width = 0;
    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private int height = 0;
    /** Video avg. fps. Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private float fps = 0;
    /** Video avg. frame duration in ms. Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private float frame_duration = 0f;
    /** Stream bps. Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private int bps_stream = 0;
    /** Video bps. Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private int bps_video = 0;
    /** Audio bps. Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private int bps_audio = 0;
    /** In frames. Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private int videoFrames = 0;
    /** In frames. Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private int audioFrames = 0;
    /** In ms. Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private int duration = 0;
    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private CodecID acodecID = CodecID.NONE;
    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private CodecID vcodecID = CodecID.NONE;
    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private CodecID scodecID = CodecID.NONE;
    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private String acodec = unknown;
    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private String vcodec = unknown;
    /** Shall be set by the {@link #initStreamImpl(int, String, int, String, int)} method implementation. */
    private String scodec = unknown;

    private volatile int decodedFrameCount = 0;
    private int presentedFrameCount = 0;
    private int displayedFrameCount = 0;

    /**
     * Help detect EOS, limit is {@link #MAX_FRAMELESS_MS_UNTIL_EOS}.
     * To be used either by getNextTexture(..) or StreamWorker for audio-only.
     */
    private int nullFrameCount = 0;
    private int maxNullFrameCountUntilEOS = 0;
    /**
     * Help detect EOS, limit {@value} milliseconds without a valid frame.
     */
    private static final int MAX_FRAMELESS_MS_UNTIL_EOS = 5000;
    private static final int MAX_FRAMELESS_UNTIL_EOS_DEFAULT =  MAX_FRAMELESS_MS_UNTIL_EOS / 30; // default value assuming 30fps

    /** See {@link #getAudioSink()}. Set by implementation if used from within {@link #initStreamImpl(int, String, int, String, int)}! */
    protected AudioSink audioSink = null;
    protected boolean audioSinkPlaySpeedSet = false;

    protected volatile SubtitleEventListener subEventListener = null;

    /** AV System Clock Reference (SCR) */
    private final PTS av_scr = new PTS( () -> { return State.Playing == state ? playSpeed : 0f; } );
    private final PTS av_scr_cpy = new PTS( av_scr );
    /** Trigger System Clock Reference (SCR) reset. */
    private boolean video_scr_reset = false;
    private boolean audio_scr_reset = false;

    private final PTS video_pts_last = new PTS( () -> { return State.Playing == state ? playSpeed : 0f; } );

    /** Cumulative video pts diff. */
    private float video_dpts_cum = 0;
    /** Cumulative video frames. */
    private int video_dpts_count = 0;
    /** Cumulative audio pts diff. */
    private float audio_dpts_cum = 0;
    /** Cumulative audio frames. */
    private int audio_dpts_count = 0;

    private int audio_queued_last_ms = 0;
    private int audio_dequeued_last = 0;

    /** Number of min frame count required for video cumulative sync. */
    private static final int AV_DPTS_NUM = 20;
    /** Cumulative coefficient, value {@value}. */
    private static final float AV_DPTS_COEFF = 0.7943282f; // (float) Math.exp(Math.log(0.01) / VIDEO_DPTS_NUM);
    /** Maximum valid video pts diff. */
    private static final int AV_DPTS_MAX = 5000; // 5s max diff

    private TextureFrame[] videoFramesOrig = null;
    private Ringbuffer<TextureFrame> videoFramesFree =  null;
    private Ringbuffer<TextureFrame> videoFramesDecoded =  null;
    private volatile TextureFrame lastFrame = null;
    private Texture[] subTexOrig = null;
    private Ringbuffer<Texture> subTexFree =  null;
    private static final int SUB_TEX_IMAGES_MIN = TEXTURE_COUNT_MIN + 1;
    private static final boolean subDEBUG = false;

    /**
     * @see #isGLOriented()
     */
    private boolean isInGLOrientation = false;

    private final ArrayList<GLMediaEventListener> eventListener = new ArrayList<GLMediaEventListener>();
    private final ArrayList<GLMediaFrameListener> frameListener = new ArrayList<GLMediaFrameListener>();

    protected GLMediaPlayerImpl() {
        this.textureCount=0;
        this.textureTarget=GL.GL_TEXTURE_2D;
        this.textureFormat = GL.GL_RGBA;
        this.textureInternalFormat = GL.GL_RGBA;
        this.textureType = GL.GL_UNSIGNED_BYTE;
        this.texUnit = 0;
        this.textureFragmentShaderHashCode = 0;
        this.state = State.Uninitialized;
        try {
            streamLoc = Uri.cast("https://no/stream/");
        } catch (final URISyntaxException e) { }
    }

    @Override
    public final void setTextureUnit(final int u) { texUnit = u; }

    @Override
    public final int getTextureUnit() { return texUnit; }

    @Override
    public final int getTextureTarget() { return textureTarget; }

    protected final int getTextureFormat() { return textureFormat; }

    protected final int getTextureType() { return textureType; }

    @Override
    public final int getTextureCount() { return textureCount; }

    protected final void setTextureTarget(final int target) { textureTarget=target; }
    protected final void setTextureFormat(final int internalFormat, final int format) {
        textureInternalFormat=internalFormat;
        textureFormat=format;
    }
    protected final void setTextureType(final int t) { textureType=t; }

    @Override
    public final void setTextureMinMagFilter(final int[] minMagFilter) { texMinMagFilter[0] = minMagFilter[0]; texMinMagFilter[1] = minMagFilter[1];}
    @Override
    public final int[] getTextureMinMagFilter() { return texMinMagFilter; }

    @Override
    public final void setTextureWrapST(final int[] wrapST) { texWrapST[0] = wrapST[0]; texWrapST[1] = wrapST[1];}
    @Override
    public final int[] getTextureWrapST() { return texWrapST; }

    @Override
    public final void setAudioChannelLimit(final int cc) {
        userMaxChannels = Math.min(8, Math.max(1, cc));
    }
    protected int getAudioChannelLimit() { return userMaxChannels; }

    @Override
    public final String getRequiredExtensionsShaderStub() {
        if(GLES2.GL_TEXTURE_EXTERNAL_OES == textureTarget) {
            return ShaderCode.createExtensionDirective(GLExtensions.OES_EGL_image_external, ShaderCode.ENABLE);
        }
        return "";
    }

    @Override
    public final String getTextureSampler2DType() {
        switch(textureTarget) {
            case GL.GL_TEXTURE_2D:
            case GL2GL3.GL_TEXTURE_RECTANGLE:
                return TextureSequence.sampler2D;
            case GLES2.GL_TEXTURE_EXTERNAL_OES:
                return TextureSequence.samplerExternalOES;
            default:
                throw new GLException("Unsuported texture target: "+toHexString(textureTarget));
        }
    }

    protected String textureLookupFunctionName = "texture2D";

    /**
     * {@inheritDoc}
     *
     * This implementation simply sets and returns the build-in function name of <code>texture2D</code>,
     * if not overridden by specialization, e.g. using the ffmpeg implementation.
     */
    @Override
    public String setTextureLookupFunctionName(final String texLookupFuncName) throws IllegalStateException {
        textureLookupFunctionName = "texture2D";
        resetTextureFragmentShaderHashCode();
        return textureLookupFunctionName;
    }

    /**
     * {@inheritDoc}
     *
     * This implementation simply returns the build-in function name of <code>texture2D</code>,
     * if not overridden by specialization, e.g. using the ffmpeg implementation.
     */
    @Override
    public final String getTextureLookupFunctionName() {
        return textureLookupFunctionName;
    }

    /**
     * {@inheritDoc}
     *
     * This implementation simply returns an empty string since it's using
     * the build-in function <code>texture2D</code>,
     * if not overridden by specialization.
     */
    @Override
    public String getTextureLookupFragmentShaderImpl() {
        return "";
    }

    @Override
    public String getTextureFragmentShaderHashID() {
        // return getTextureSampler2DType()+";"+getTextureLookupFunctionName()+";"+getTextureLookupFragmentShaderImpl();
        return getTextureSampler2DType()+";"+getTextureLookupFunctionName();
    }

    @Override
    public final int getTextureFragmentShaderHashCode() {
        if( State.Uninitialized == state ) {
            resetTextureFragmentShaderHashCode();
            return 0;
        } else if( 0 == textureFragmentShaderHashCode ) {
            final int hash = getTextureFragmentShaderHashID().hashCode();
            textureFragmentShaderHashCode = hash;
        }
        return textureFragmentShaderHashCode;
    }
    protected final void resetTextureFragmentShaderHashCode() { textureFragmentShaderHashCode = 0; }

    @Override
    public final int getDecodedFrameCount() { return decodedFrameCount; }

    @Override
    public final int getPresentedFrameCount() { return presentedFrameCount; }

    @Override
    public final PTS getPTS() { return av_scr_cpy; }

    @Override
    public final int getVideoPTS() { return video_pts_last.getCurrent(); }

    @Override
    public final int getAudioPTS() {
        if( State.Uninitialized != state && null != audioSink ) {
            return audioSink.getPTS().getCurrent();
        }
        return 0;
    }
    /** Override if not using audioSink! */
    protected PTS getAudioPTSImpl() {
        if( null != audioSink ) {
            return audioSink.getPTS();
        } else {
            return dummy_audio_pts;
        }
    }
    /** Override if not using audioSink! */
    protected PTS getUpdatedAudioPTS() {
        if( null != audioSink ) {
            return audioSink.updateQueue();
        } else {
            return dummy_audio_pts;
        }
    }
    private final PTS dummy_audio_pts = new PTS( () -> { return State.Playing == state ? playSpeed : 0f; } );

    /** Override if not using audioSink! */
    protected int getAudioQueuedDuration() {
        if( null != audioSink ) {
            return (int)(audioSink.getQueuedDuration()*1000f);
        } else {
            return 0;
        }
    }
    /** Override if not using audioSink! */
    protected int getLastBufferedAudioPTS() {
        if( null != audioSink ) {
            return audioSink.getLastBufferedPTS();
        } else {
            return 0;
        }
    }

    @Override
    public final State getState() { return state; }

    protected final void setState(final State s) { state=s; }

    @Override
    public final State resume() {
        synchronized( stateLock ) {
            final State preState = state;
            if( State.Paused == state ) {
                if( resumeImpl() ) {
                    if( null != audioSink ) {
                        audioSink.play(); // cont. w/ new data
                    }
                    if( null != streamWorker ) {
                        streamWorker.resume();
                    }
                    changeState(new GLMediaPlayer.EventMask(), State.Playing);
                    {
                        final int _pending_seek = pending_seek;
                        pending_seek = -1;
                        if( 0 <= _pending_seek ) {
                            this.seek(_pending_seek);
                        }
                    }
                }
            }
            if(DEBUG) { logout.println("Play: "+preState+" -> "+state+", "+toString()); }
            return state;
        }
    }
    protected abstract boolean resumeImpl();

    @Override
    public final State pause(final boolean flush) {
        return pauseImpl(flush, new GLMediaPlayer.EventMask());
    }
    private final State pauseImpl(final boolean flush, GLMediaPlayer.EventMask eventMask) {
        synchronized( stateLock ) {
            final State preState = state;
            if( State.Playing == state ) {
                eventMask = addStateEventMask(eventMask, State.Paused);
                setState( State.Paused );
                if( null != streamWorker ) {
                    streamWorker.pause(true);
                }
                if( flush ) {
                    resetAVPTSAndFlush(false);
                } else if( null != audioSink ) {
                    audioSink.pause();
                }
                attributesUpdated( eventMask );
                if( !pauseImpl() ) {
                    resume();
                }
            }
            if(DEBUG) { logout.println("Pause: "+preState+" -> "+state+", "+toString()); }
            return state;
        }
    }
    protected abstract boolean pauseImpl();

    @Override
    public final State stop() {
        synchronized( stateLock ) {
            final State preState = state;
            if( null != streamWorker ) {
                streamWorker.stop(true);
                streamWorker = null;
            }
            resetAVPTSAndFlush(true);
            stopImpl();
            changeState(new GLMediaPlayer.EventMask(), State.Uninitialized);
            // attachedObjects.clear();
            if(DEBUG) { logout.println("Stop: "+preState+" -> "+state+", "+toString()); }
            return state;
        }
    }
    protected abstract void stopImpl();

    @Override
    public final State destroy(final GL gl) {
        return destroyImpl(gl, new GLMediaPlayer.EventMask(), true);
    }
    private final State destroyImpl(final GL gl, final GLMediaPlayer.EventMask eventMask, final boolean wait) {
        synchronized( stateLock ) {
            if( null != streamWorker ) {
                streamWorker.stop(wait);
                streamWorker = null;
            }
            resetAVPTSAndFlush(true);
            destroyImpl();
            removeAllTextureFrames(gl);
            lastFrame = null;
            textureCount=0;
            changeState(eventMask, State.Uninitialized);
            attachedObjects.clear();
            return state;
        }
    }
    protected abstract void destroyImpl();

    @Override
    public final int seek(int msec) {
        final int pts1;
        final State preState;
        synchronized( stateLock ) {
            preState = state;
            switch(state) {
                case Playing:
                case Paused:
                    final State _state = state;
                    setState( State.Paused );
                    // Adjust target ..
                    if( msec >= duration ) {
                        msec = duration - (int)Math.floor(frame_duration);
                    } else if( msec < 0 ) {
                        msec = 0;
                    }
                    if( null != streamWorker ) {
                        streamWorker.pause(true);
                    }
                    pts1 = seekImpl(msec);
                    resetAVPTSAndFlush(false);
                    if( null != audioSink && State.Playing == _state ) {
                        audioSink.play(); // cont. w/ new data
                    }
                    if(DEBUG) {
                        logout.println("Seek("+msec+"): "+getPerfString());
                    }
                    if( null != streamWorker ) {
                        streamWorker.resume();
                    }
                    setState( _state );
                    attributesUpdated(new GLMediaPlayer.EventMask(GLMediaPlayer.EventMask.Bit.Seek));
                    break;
                default:
                    pending_seek = msec;
                    pts1 = 0;
            }
        }
        oneVideoFrameAtPause.set(true);
        if(DEBUG) { logout.println("Seek("+msec+"): "+preState+" -> "+state+", "+toString()); }
        return pts1;
    }
    protected int pending_seek = -1;
    protected abstract int seekImpl(int msec);

    @Override
    public final float getPlaySpeed() { return playSpeed; }

    private static final float clipZeroOneAllowMax(final float v) {
        if( v < 0.01f ) {
            return 0.0f;
        } else if( Math.abs(1.0f - v) < 0.01f ) {
            return 1.0f;
        }
        return v;
    }

    @Override
    public final boolean setPlaySpeed(float rate) {
        synchronized( stateLock ) {
            final float preSpeed = playSpeed;
            boolean res = false;
            rate = clipZeroOneAllowMax(rate);
            if( rate > 0.1f ) {
                if(State.Uninitialized != state ) {
                    if( setPlaySpeedImpl(rate) ) {
                        resetAVPTS();
                        playSpeed = rate;
                        res = true;
                    }
                } else {
                    // earmark ..
                    playSpeed = rate;
                    res = true;
                }
            }
            if(DEBUG) { logout.println("setPlaySpeed("+rate+"): "+state+", "+preSpeed+" -> "+playSpeed+", "+toString()); }
            return res;
        }
    }
    /**
     * Override if not using AudioSink, or AudioSink's {@link AudioSink#setPlaySpeed(float)} is not sufficient!
     * <p>
     * AudioSink shall respect <code>!audioSinkPlaySpeedSet</code> to determine data_size
     * at {@link AudioSink#enqueueData(int, ByteBuffer, int)}
     * </p>
     */
    protected boolean setPlaySpeedImpl(final float rate) {
        if( null != audioSink ) {
            audioSinkPlaySpeedSet = audioSink.setPlaySpeed(rate);
        }
        // still true, even if audioSink rejects command since we deal w/ video sync
        // and AudioSink w/ audioSinkPlaySpeedSet at enqueueData(..).
        return true;
    }

    @Override
    public final float getAudioVolume() {
        getAudioVolumeImpl();
        return audioVolume;
    }

    @Override
    public boolean isAudioMuted() {
        return FloatUtil.isZero(audioVolume);
    }

    /**
     * Override if not using AudioSink, or AudioSink's {@link AudioSink#getVolume()} is not sufficient!
     */
    protected void getAudioVolumeImpl() {
        if( null != audioSink ) {
            audioVolume = audioSink.getVolume();
        }
    }

    @Override
    public boolean setAudioVolume(float v) {
        synchronized( stateLock ) {
            final float preVolume = audioVolume;
            boolean res = false;
            v = clipZeroOneAllowMax(v);
            if(State.Uninitialized != state ) {
                if( setAudioVolumeImpl(v) ) {
                    audioVolume = v;
                    res = true;
                }
            } else {
                // earmark ..
                audioVolume = v;
                res = true;
            }
            if(DEBUG) { logout.println("setAudioVolume("+v+"): "+state+", "+preVolume+" -> "+audioVolume+", "+toString()); }
            return res;
        }
    }
    /**
     * Override if not using AudioSink, or AudioSink's {@link AudioSink#setVolume(float)} is not sufficient!
     */
    protected boolean setAudioVolumeImpl(final float v) {
        if( null != audioSink ) {
            final boolean res = audioSink.setVolume(v);
            if( State.Playing == state ) {
                if( FloatUtil.isZero(v) ) {
                    audioSink.flush(); // implies stop!
                } else {
                    audioSink.play(); // cont. w/ new data
                }
            }
            return res;
        }
        // still true, even if audioSink rejects command ..
        return true;
    }

    @Override
    public void playStream(final Uri streamLoc, final int vid, final int aid, final int sid, final int textureCount) throws IllegalStateException, IllegalArgumentException {
        playStream(streamLoc, vid, null, aid, null, sid, textureCount);
    }
    @Override
    public void playStream(final Uri streamLoc, final int vid,
                           final String alang, final int aid, final String slang, final int sid, final int reqTextureCount) throws IllegalStateException, IllegalArgumentException
    {
        synchronized( stateLock ) {
            if(State.Uninitialized != state) {
                throw new IllegalStateException("Instance not in state unintialized: "+this);
            }
            if(null == streamLoc) {
                initTestStream();
                return;
            }
            if( STREAM_ID_NONE != vid ) {
                textureCount = validateTextureCount(reqTextureCount);
                if( textureCount < TEXTURE_COUNT_MIN ) {
                    throw new InternalError("Validated texture count < "+TEXTURE_COUNT_MIN+": "+textureCount);
                }
            } else {
                textureCount = 0;
            }
            decodedFrameCount = 0;
            presentedFrameCount = 0;
            displayedFrameCount = 0;
            nullFrameCount = 0;
            maxNullFrameCountUntilEOS = MAX_FRAMELESS_UNTIL_EOS_DEFAULT;
            this.streamLoc = streamLoc;

            // Pre-parse for camera-input scheme
            cameraPath = null;
            cameraProps = null;
            final Uri.Encoded streamLocScheme = streamLoc.scheme;
            if( null != streamLocScheme && streamLocScheme.equals(CameraInputScheme) ) {
                final Uri.Encoded rawPath = streamLoc.path;
                if( null != rawPath && rawPath.length() > 0 ) {
                    // cut-off root fwd-slash
                    cameraPath = rawPath.substring(1);
                    final UriQueryProps props = UriQueryProps.create(streamLoc, ';');
                    cameraProps = props.getProperties();
                } else {
                    throw new IllegalArgumentException("Camera path is empty: "+streamLoc.toString());
                }
            }

            final int aid2 = alang != null && alang.length() > 0 ? STREAM_ID_AUTO : aid;
            final int sid2 = slang != null && slang.length() > 0 ? STREAM_ID_AUTO : sid;
            this.vid = vid;
            this.aid = aid2;
            this.sid = sid2;
            new InterruptSource.Thread() {
                @Override
                public void run() {
                    try {
                        // StreamWorker may be used, see API-doc of StreamWorker
                        initStreamImpl(vid, alang, aid2, slang, sid2);
                    } catch (final Throwable t) {
                        streamErr = new StreamException(t.getClass().getSimpleName()+" while initializing: "+GLMediaPlayerImpl.this.toString(), t);
                        changeState(new GLMediaPlayer.EventMask(GLMediaPlayer.EventMask.Bit.Error), GLMediaPlayer.State.Uninitialized);
                    } // also initializes width, height, .. etc
                }
            }.start();
        }
    }
    /**
     * Implementation shall set the following set of data here
     * @param vid video stream id, see <a href="#streamIDs">audio and video Stream IDs</a>
     * @param alang desired audio language, pass {@code null} to use {@code aid}
     * @param aid fallback audio stream id in case {@code alang} is {@code null}, see <a href="#streamIDs">audio and video Stream IDs</a>
     * @param slang desired subtitle language, pass {@code null} to use {@code sid}
     * @param sid fallback subtitle stream id in case {@code alang} is {@code null}, see <a href="#streamIDs">audio and video Stream IDs</a>
     * @see #vid
     * @see #aid
     * @see #sid
     * @see #width
     * @see #height
     * @see #fps
     * @see #bps_stream
     * @see #videoFrames
     * @see #audioFrames
     * @see #acodec
     * @see #vcodec
    */
    protected abstract void initStreamImpl(int vid, String alang, int aid, String slang, int sid) throws Exception;

    @Override
    public void switchStream(final int vid, final int aid, final int sid) throws IllegalStateException, IllegalArgumentException {
        final int v_pts = getVideoPTS();
        stop();
        seek(v_pts);
        playStream(getUri(), vid, aid, sid, getTextureCount());
    }

    @Override
    public final StreamException getStreamException() {
        StreamException e = null;
        synchronized( stateLock ) {
            if( null != streamWorker ) {
                e = streamWorker.getStreamException();
            }
            if( null == e ) {
                e = streamErr;
            }
            streamErr = null;
        }
        return e;
    }

    @Override
    public final void initGL(final GL gl) throws IllegalStateException, StreamException, GLException {
        synchronized( stateLock ) {
            // if(State.Initialized != state && State.Uninitialized != state) {
            //    throw new IllegalStateException("Stream not in state [un]initialized: "+this);
            // }
            if( null != streamWorker ) {
                final StreamException streamInitErr = getStreamException();
                if( null != streamInitErr ) {
                    streamWorker = null; // already terminated!
                    destroy(null);
                    throw streamInitErr;
                }
            }
            if(DEBUG) {
                logout.println("GLMediaPlayer.initGL: "+this);
            }
            try {
                resetAVPTSAndFlush(true);
                removeAllTextureFrames(gl);
                if( State.Uninitialized != state ) {
                    initGLImpl(gl);
                    setAudioVolume( audioVolume ); // update volume
                    setPlaySpeed( playSpeed ); // update playSpeed
                    if(DEBUG) {
                        logout.println("initGLImpl.X "+this);
                    }
                    if( null != gl ) {
                        videoFramesOrig = createTexFrames(gl, textureCount);
                        if( TEXTURE_COUNT_MIN == textureCount ) {
                            videoFramesFree = null;
                            videoFramesDecoded = null;
                            lastFrame = videoFramesOrig[0];
                        } else {
                            videoFramesFree = new LFRingbuffer<TextureFrame>(videoFramesOrig);
                            videoFramesDecoded = new LFRingbuffer<TextureFrame>(TextureFrame[].class, textureCount);
                            lastFrame = videoFramesFree.getBlocking();
                        }
                        if( STREAM_ID_NONE != sid ) {
                            subTexOrig = createSubTextures(gl, Math.max(SUB_TEX_IMAGES_MIN, textureCount)); // minimum 2 textures
                            subTexFree = new LFRingbuffer<Texture>(subTexOrig);
                        } else {
                            subTexOrig = null;
                            subTexFree = null;
                        }
                    } else {
                        videoFramesOrig = null;
                        videoFramesFree = null;
                        videoFramesDecoded = null;
                        lastFrame = null;
                        subTexOrig = null;
                        subTexFree = null;
                    }
                    if( null == streamWorker &&
                        ( TEXTURE_COUNT_MIN < textureCount || STREAM_ID_NONE == vid ) ) // Enable StreamWorker for 'audio only' as well (Bug 918).
                    {
                        streamWorker = new StreamWorker();
                    }
                    if( null != streamWorker ) {
                        streamWorker.initGL(gl);
                        streamWorker.resume();
                    }
                    changeState(new GLMediaPlayer.EventMask(), State.Paused);
                    resume();
                } else if( null == gl ) {
                    width = 0;
                    height = 0;
                    setTextureFormat(GL.GL_RGBA, GL.GL_RGBA);
                    setTextureType(GL.GL_UNSIGNED_BYTE);
                    textureCount = 0;
                    videoFramesOrig = null;
                    videoFramesFree = null;
                    videoFramesDecoded = null;
                    lastFrame = null;
                    subTexOrig = null;
                    subTexFree = null;
                } else {
                    // Using a dummy test frame
                    width = TestTexture.singleton.getWidth();
                    height = TestTexture.singleton.getHeight();
                    setTextureFormat(GL.GL_RGBA, GL.GL_RGBA);
                    setTextureType(GL.GL_UNSIGNED_BYTE);
                    textureCount = Math.max(TEXTURE_COUNT_MIN, textureCount);
                    videoFramesOrig = createTestTexFrames(gl, textureCount);
                    if( TEXTURE_COUNT_MIN == textureCount ) {
                        videoFramesFree = null;
                        videoFramesDecoded = null;
                        lastFrame = videoFramesOrig[0];
                    } else {
                        videoFramesFree = new LFRingbuffer<TextureFrame>(videoFramesOrig);
                        videoFramesDecoded = new LFRingbuffer<TextureFrame>(TextureFrame[].class, textureCount);
                        lastFrame = videoFramesFree.getBlocking( );
                    }
                    subTexOrig = null;
                    subTexFree = null;
                    // changeState(0, State.Paused);
                }
            } catch (final Throwable t) {
                destroyImpl(gl, new GLMediaPlayer.EventMask(GLMediaPlayer.EventMask.Bit.Error), false /* wait */); // -> GLMediaPlayer.State.Uninitialized
                throw new GLException("Error initializing GL resources", t);
            }
        }
    }
    /**
     * Shall initialize all GL related resources, if not audio-only.
     * <p>
     * Shall also take care of {@link AudioSink} initialization if appropriate.
     * </p>
     * @param gl null for audio-only, otherwise a valid and current GL object.
     * @throws IOException
     * @throws GLException
     */
    protected abstract void initGLImpl(GL gl) throws IOException, GLException;

    /**
     * Returns the validated number of textures to be handled.
     * <p>
     * Default is {@link #TEXTURE_COUNT_DEFAULT} minimum textures, if <code>desiredTextureCount</code>
     * is < {@link #TEXTURE_COUNT_MIN}, {@link #TEXTURE_COUNT_MIN} is returned.
     * </p>
     * <p>
     * Implementation must at least return a texture count of {@link #TEXTURE_COUNT_MIN}, <i>two</i>, the last texture and the decoding texture.
     * </p>
     */
    protected int validateTextureCount(final int desiredTextureCount) {
        return desiredTextureCount < TEXTURE_COUNT_MIN ? TEXTURE_COUNT_MIN : desiredTextureCount;
    }

    protected TextureFrame[] createTexFrames(final GL gl, final int count) {
        final int[] texNames = new int[count];
        gl.glGenTextures(count, texNames, 0);
        final int err = gl.glGetError();
        if( GL.GL_NO_ERROR != err ) {
            throw new RuntimeException("TextureNames creation failed (num: "+count+"): err "+toHexString(err));
        }
        final TextureFrame[] texFrames = new TextureFrame[count];
        for(int i=0; i<count; i++) {
            texFrames[i] = createTexImage(gl, texNames[i]);
        }
        return texFrames;
    }
    protected TextureFrame[] createTestTexFrames(final GL gl, final int count) {
        final int[] texNames = new int[count];
        gl.glGenTextures(count, texNames, 0);
        final int err = gl.glGetError();
        if( GL.GL_NO_ERROR != err ) {
            throw new RuntimeException("TextureNames creation failed (num: "+count+"): err "+toHexString(err));
        }
        final TextureFrame[] texFrames = new TextureFrame[count];
        for(int i=0; i<count; i++) {
            texFrames[i] = createTestTexImage(gl, texNames[i]);
        }
        return texFrames;
    }
    protected Texture[] createSubTextures(final GL gl, final int count) {
        final int[] texNames = new int[count];
        gl.glGenTextures(count, texNames, 0);
        final int err = gl.glGetError();
        if( GL.GL_NO_ERROR != err ) {
            throw new RuntimeException("TextureNames creation failed (num: "+count+"): err "+toHexString(err));
        }
        final Texture[] textures = new Texture[count];
        for(int i=0; i<count; i++) {
            textures[i] = new Texture(texNames[i], true /* ownsTextureID */,
                                      textureTarget, 1, 1, 1, 1, true);
        }
        return textures;
    }

    private static class TestTexture {
        private static final TextureData singleton;
        static {
            TextureData data = null;
            try {
                final URLConnection urlConn = IOUtil.getResource("jogamp/opengl/assets/test-ntsc01-28x16.png", NullGLMediaPlayer.class.getClassLoader());
                if(null != urlConn) {
                    data = TextureIO.newTextureData(GLProfile.getGL2ES2(), urlConn.getInputStream(), false, TextureIO.PNG);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
            if(null == data) {
                final int w = 28;
                final int h = 16;
                final ByteBuffer buffer = Buffers.newDirectByteBuffer(w*h*4);
                while(buffer.hasRemaining()) {
                    buffer.put((byte) 0xEA); buffer.put((byte) 0xEA); buffer.put((byte) 0xEA); buffer.put((byte) 0xEA);
                }
                buffer.rewind();
                data = new TextureData(GLProfile.getGL2ES2(),
                                        GL.GL_RGBA, w, h, 0,
                                        GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, false,
                                        false, false, buffer, null);
            }
            singleton = data;
        }
    }
    private final TextureSequence.TextureFrame createTestTexImage(final GL gl, final int texName) {
        final Texture texture = createTexImageImpl(gl, texName, TestTexture.singleton.getWidth(), TestTexture.singleton.getHeight());
        texture.updateImage(gl, TestTexture.singleton);
        return new TextureSequence.TextureFrame( texture );
    }
    private void initTestStream() {
        textureCount = TEXTURE_COUNT_MIN;
        final float _fps = 24f;
        final int _duration = 10*60*1000; // msec
        final int _totalFrames = (int) ( (_duration/1000)*_fps );
        updateAttributes("test",
                         new int[0], new String[0], GLMediaPlayer.STREAM_ID_NONE,
                         new int[0], new String[0], GLMediaPlayer.STREAM_ID_NONE,
                         new int[0], new String[0], GLMediaPlayer.STREAM_ID_NONE,
                         TestTexture.singleton.getWidth(), TestTexture.singleton.getHeight(), 0, 0, 0, _fps, _totalFrames, 0, _duration,
                         "png-static", null, null, CodecID.toFFmpeg(CodecID.PNG), -1, -1);
    }

    protected abstract TextureFrame createTexImage(GL gl, int texName);

    /**
     * Creating a {@link Texture} instance by taking ownership of the given {@code texName} texture object.
     * @param gl current GL object
     * @param texName generated texture object to be used and taken ownership of
     * @param tWidth
     * @param tHeight
     * @return
     */
    protected final Texture createTexImageImpl(final GL gl, final int texName, final int tWidth, final int tHeight) {
        if( 0 > texName ) {
            throw new RuntimeException("TextureName "+toHexString(texName)+" invalid.");
        }
        gl.glActiveTexture(GL.GL_TEXTURE0+getTextureUnit());
        gl.glBindTexture(textureTarget, texName);
        {
            final int err = gl.glGetError();
            if( GL.GL_NO_ERROR != err ) {
                throw new RuntimeException("Couldn't bind textureName "+toHexString(texName)+" to 2D target, err "+toHexString(err));
            }
        }

        if(GLES2.GL_TEXTURE_EXTERNAL_OES != textureTarget) {
            // create space for buffer with a texture
            gl.glTexImage2D(
                    textureTarget,    // target
                    0,                // level
                    textureInternalFormat, // internal format
                    tWidth,           // width
                    tHeight,          // height
                    0,                // border
                    textureFormat,
                    textureType,
                    null);            // pixels -- will be provided later
            {
                final int err = gl.glGetError();
                if( GL.GL_NO_ERROR != err ) {
                    throw new RuntimeException("Couldn't create TexImage2D RGBA "+tWidth+"x"+tHeight+", target "+toHexString(textureTarget)+
                                   ", ifmt "+toHexString(textureInternalFormat)+", fmt "+toHexString(textureFormat)+", type "+toHexString(textureType)+
                                   ", err "+toHexString(err));
                }
            }
            if(DEBUG) {
                logout.println("Created TexImage2D RGBA "+tWidth+"x"+tHeight+", target "+toHexString(textureTarget)+
                                   ", ifmt "+toHexString(textureInternalFormat)+", fmt "+toHexString(textureFormat)+", type "+toHexString(textureType));
            }
        }
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_MIN_FILTER, texMinMagFilter[0]);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_MAG_FILTER, texMinMagFilter[1]);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_WRAP_S, texWrapST[0]);
        gl.glTexParameteri(textureTarget, GL.GL_TEXTURE_WRAP_T, texWrapST[1]);

        return new Texture(texName, true /* ownsTextureID */,
                           textureTarget,
                           tWidth, tHeight, width,  height, !isInGLOrientation);
    }

    protected void destroyTexFrame(final GL gl, final TextureFrame frame) {
        frame.getTexture().destroy(gl);
    }

    @Override
    public final boolean useARatioAdjustment() { return true; }

    @Override
    public void setARatioAdjustment(final boolean v) { } // intentionally not supported

    @Override
    public final boolean useARatioLetterbox() { return aRatioLbox; }

    @Override
    public Vec4f getARatioLetterboxBackColor() { return aRatioLboxBackColor; }

    @Override
    public void setARatioLetterbox(final boolean v, final Vec4f backColor) {
        aRatioLbox = v;
        if( null != backColor ) {
            aRatioLboxBackColor.set(backColor);
        }
    };

    @Override
    public final boolean isTextureAvailable() {
        return null != lastFrame; // Note: lastFrame is test-texture if using initGL() pre stream ready
    }

    @Override
    public final TextureFrame getLastTexture() throws IllegalStateException {
        return lastFrame;
    }

    private final void destroyTexFrames(final GL gl, final TextureFrame[] texFrames) {
        if( null != texFrames ) {
            for(int i=0; i<texFrames.length; i++) {
                final TextureFrame frame = texFrames[i];
                if(null != frame) {
                    if( null != gl ) {
                        destroyTexFrame(gl, frame);
                    }
                    texFrames[i] = null;
                }
                if( DEBUG ) {
                    logout.println(Thread.currentThread().getName()+"> Clear TexFrame["+i+"]: "+frame+" -> null");
                }
            }
        }
    }
    private final void destroyTextures(final GL gl, final Texture[] textures) {
        if( null != textures ) {
            for(int i=0; i<textures.length; i++) {
                final Texture tex = textures[i];
                if(null != tex) {
                    if( null != gl ) {
                        tex.destroy(gl);
                    }
                    textures[i] = null;
                }
                if( DEBUG ) {
                    logout.println(Thread.currentThread().getName()+"> Clear Texture["+i+"]: "+tex+" -> null");
                }
            }
        }
    }
    private final void removeAllTextureFrames(final GL gl) {
        destroyTexFrames(gl, videoFramesOrig);
        videoFramesOrig = null;
        videoFramesFree = null;
        videoFramesDecoded = null;
        lastFrame = null;
        cachedFrame = null;
        if( subDEBUG ) {
            System.err.println("GLMediaPlayer: removeAllTextureFrames: subTexFree "+subTexFree);
        }
        destroyTextures(gl, subTexOrig); // can crash, if event obj w/ texture-copy still in use
        subTexOrig = null;
        subTexFree = null;
    }

    private TextureFrame cachedFrame = null;
    private long lastMillis = 0;
    private int repeatedFrame = 0;

    private final boolean[] stGotVFrame = { false };

    protected boolean audioStreamEnabled() {
        return GLMediaPlayer.STREAM_ID_NONE != aid && !isAudioMuted() && ( 1.0f == getPlaySpeed() || audioSinkPlaySpeedSet );
    }

    private int getAudioDequeued(final int audio_queued_ms) {
        int res;
        if( audio_queued_last_ms > audio_queued_ms ) {
            res = audio_queued_last_ms - audio_queued_ms;
        } else {
            res = audio_dequeued_last;
        }
        return res;
    }

    @Override
    public final TextureFrame getNextTexture(final GL gl) throws IllegalStateException {
        synchronized( stateLock ) {
            final boolean oneVideoFrame = State.Paused == state && oneVideoFrameAtPause.compareAndSet(true, false);
            if( oneVideoFrame || State.Playing == state ) {
                boolean dropFrame = false;
                try {
                    do {
                        final long currentMillis = Clock.currentMillis();
                        final boolean audioStreamEnabled = audioStreamEnabled();
                        final int audio_queued_ms;
                        if( audioStreamEnabled && !audio_scr_reset ) {
                            audio_queued_ms = getAudioQueuedDuration();
                        } else {
                            audio_queued_ms = 100;
                        }
                        final int audio_dequeued_ms = getAudioDequeued(audio_queued_ms);
                        audio_dequeued_last = audio_dequeued_ms; // update

                        final int min0_audio_queued_ms = Math.max( audio_dequeued_ms, MAX_VIDEO_ASYNC );
                        final int min1_audio_queued_ms = Math.max( 2*audio_dequeued_ms, 2*MAX_VIDEO_ASYNC );
                        final int max_adelay = Math.max( 4*audio_dequeued_ms, 4*MAX_VIDEO_ASYNC );

                        char syncModeA  = '_', syncModeB  = '_';
                        char resetModeA = '_', resetModeV = '_';

                        final PTS audio_pts = new PTS( () -> { return State.Playing == state ? playSpeed : 0f; } );
                        final int audio_pts_lb;
                        final boolean use_audio;
                        if( audioStreamEnabled ) {
                            final PTS apts = getUpdatedAudioPTS();
                            if( !apts.isValid() ) {
                                audio_pts.set(currentMillis, 0);
                                use_audio = false;
                            } else {
                                audio_pts.set(apts);
                                use_audio = true;
                                if( audio_scr_reset ) {
                                    audio_scr_reset = false;
                                    resetSCR(apts);
                                    resetModeA = 'A';
                                }
                            }
                            audio_pts_lb = getLastBufferedAudioPTS();
                        } else {
                            audio_pts.set(currentMillis, 0);
                            audio_pts_lb = 0;
                            use_audio = false;
                        }

                        final boolean droppedFrame;
                        if( dropFrame ) {
                            presentedFrameCount--;
                            dropFrame = false;
                            droppedFrame = true;
                        } else {
                            droppedFrame = false;
                        }

                        final PTS video_pts = new PTS( () -> { return State.Playing == state ? playSpeed : 0f; } );
                        final boolean hasVideoFrame;
                        TextureFrame nextFrame;
                        if( null != cachedFrame && ( audio_queued_ms > min0_audio_queued_ms || !use_audio ) ) {
                            nextFrame = cachedFrame;
                            cachedFrame = null;
                            presentedFrameCount--;
                            video_pts.set(currentMillis, nextFrame.getPTS());
                            hasVideoFrame = true;
                            repeatedFrame++;
                            syncModeA = 'r';
                        } else {
                            if( null != cachedFrame && null != videoFramesFree ) {
                                // Push back skipped repeated frame due to low audio_queued_ms
                                videoFramesFree.putBlocking(cachedFrame);
                                syncModeA = 'z';
                            }
                            cachedFrame = null;
                            repeatedFrame = 0;
                            if( null != videoFramesDecoded ) {
                                // multi-threaded and video available
                                nextFrame = videoFramesDecoded.get();
                                if( null != nextFrame ) {
                                    video_pts.set(currentMillis, nextFrame.getPTS());
                                    hasVideoFrame = true;
                                } else {
                                    video_pts.set(currentMillis, 0);
                                    hasVideoFrame = false;
                                    syncModeA = 'e';
                                }
                            } else {
                                // single-threaded or audio-only
                                video_pts.set(currentMillis, getNextSingleThreaded(gl, lastFrame, stGotVFrame));
                                nextFrame = lastFrame;
                                hasVideoFrame = stGotVFrame[0];
                            }
                        }
                        if( !hasVideoFrame && oneVideoFrame ) {
                            oneVideoFrameAtPause.set(true);
                        }

                        if( hasVideoFrame && video_pts.isValid() ) {
                            final int frame_period_last = video_pts.diffLast(video_pts_last); // rendering loop interrupted ?
                            if( video_scr_reset || frame_period_last > frame_duration*10 ) {
                                video_scr_reset = false;
                                resetSCR( use_audio ? audio_pts : video_pts );
                                resetModeV = 'V';
                            }
                        }

                        if( video_pts.isEOS() ||
                            ( duration > 0 && duration <= video_pts.get(currentMillis) ) || maxNullFrameCountUntilEOS <= nullFrameCount )
                        {
                            // EOS
                            if( DEBUG || DEBUG_AVSYNC ) {
                                logout.println(currentMillis, "AV-EOS (getNextTexture): EOS_PTS "+(video_pts.isEOS())+", "+this);
                            }
                            pauseImpl(true, new GLMediaPlayer.EventMask(GLMediaPlayer.EventMask.Bit.EOS));

                        } else if( !hasVideoFrame || !video_pts.isValid() ) { // invalid or no video frame
                            if( null == videoFramesDecoded || !videoFramesDecoded.isEmpty() ) {
                                nullFrameCount++;
                            }
                            if( DEBUG_AVSYNC ) {
                                syncModeB = '?';
                                final String nullFrameCount_s = nullFrameCount > 0 ? ", nullFrames "+nullFrameCount+", " : ", ";
                                logout.println(currentMillis, "AV"+syncModeA+syncModeB+":"+resetModeA+resetModeV+
                                        ": dT "+(currentMillis-lastMillis)+nullFrameCount_s+
                                        getPerfStringImpl(currentMillis, video_pts, use_audio, audio_pts, audio_queued_ms, audio_pts_lb) + ", droppedFrame "+droppedFrame);
                            }
                        } else { // valid pts and has video frames
                            nullFrameCount=0;

                            presentedFrameCount++;

                            // d_apts > 0: audio too slow (behind SCR) repeat video frame, < 0: audio too fast (in front of SCR) drop video frame
                            final int d_apts;
                            if( use_audio && audio_pts.isValid() ) {
                                d_apts = av_scr.diff(currentMillis, audio_pts);
                            } else {
                                d_apts = 0;
                            }
                            // d_vpts > 0: video too fast (in front of SCR) repeat frame, < 0: video too slow (behind SCR) drop frame
                            int d_vpts = video_pts.diff(currentMillis, av_scr);

                            final boolean d_apts_off = -AV_DPTS_MAX > d_apts || d_apts > AV_DPTS_MAX;
                            final boolean d_vpts_off = -AV_DPTS_MAX > d_vpts || d_vpts > AV_DPTS_MAX;
                            if( d_apts_off || d_vpts_off ) {
                                // Extreme rare off audio/video DPTS
                                resetSCR( use_audio ? audio_pts : video_pts );
                                resetModeA = d_apts_off ? 'A' : 'a';
                                resetModeV = d_vpts_off ? 'V' : 'v';
                                if( DEBUG_AVSYNC ) {
                                    syncModeB = '*';
                                    logout.println(currentMillis, "AV"+syncModeA+syncModeB+":"+resetModeA+resetModeV+
                                            ": dT "+(currentMillis-lastMillis)+", "+
                                            getPerfStringImpl(currentMillis, video_pts, use_audio, audio_pts, audio_queued_ms, audio_pts_lb)); // + ", "+nextFrame);
                                }
                            } else {
                                final int dt_a;
                                final boolean scr_resynced;
                                if( use_audio ) {
                                    audio_dpts_count++;
                                    if( droppedFrame ) {
                                        audio_dpts_cum = d_apts * AV_DPTS_COEFF + audio_dpts_cum; // weight on current frame's PTS
                                    } else {
                                        audio_dpts_cum = d_apts + AV_DPTS_COEFF * audio_dpts_cum;
                                    }
                                    dt_a = (int) ( getDPTSAvg(audio_dpts_cum, audio_dpts_count) / playSpeed + 0.5f );
                                    if( ( dt_a < -MAX_VIDEO_ASYNC && d_apts < 0 ) || ( dt_a > max_adelay && d_apts > 0 ) ) {
                                        // resync to audio
                                        scr_resynced = true;
                                        syncModeB = '*';
                                        av_scr.set(audio_pts);
                                        audio_dpts_cum = d_apts * AV_DPTS_COEFF + d_apts; // total weight on current frame's PTS
                                        audio_dpts_count = AV_DPTS_NUM - AV_DPTS_NUM/4;
                                        d_vpts = video_pts.diff(currentMillis, av_scr);
                                        resetModeA = 'A';
                                    } else {
                                        scr_resynced = false;
                                    }
                                } else {
                                    dt_a = 0;
                                    scr_resynced = false;
                                }
                                final int avg_dpy_duration, maxVideoDelay;
                                {
                                    final int dpy_den = displayedFrameCount > 0 ? displayedFrameCount : 1;
                                    avg_dpy_duration = ( (int) ( ( currentMillis - av_scr.getSCR() ) * playSpeed + 0.5f ) ) / dpy_den ; // ms/f
                                    maxVideoDelay = Math.min(Math.max(avg_dpy_duration, MIN_VIDEO_ASYNC), MAX_VIDEO_ASYNC);
                                }
                                video_dpts_count++;
                                if( droppedFrame || scr_resynced ) {
                                    video_dpts_cum = d_vpts * AV_DPTS_COEFF + video_dpts_cum; // weight on current frame's PTS
                                } else {
                                    video_dpts_cum = d_vpts + AV_DPTS_COEFF * video_dpts_cum;
                                }
                                final int dt_v = (int) ( getDPTSAvg(video_dpts_cum, video_dpts_count) + 0.5f );
                                // final TextureFrame _nextFrame = nextFrame;
                                if( dt_v > maxVideoDelay && d_vpts >= 0 &&
                                    ( audio_queued_ms > min1_audio_queued_ms || !use_audio ) )
                                {
                                    cachedFrame = nextFrame;
                                    nextFrame = null;
                                    syncModeB = 'c';
                                } else if( dt_v < -maxVideoDelay && d_vpts < 0 &&
                                           ( null != videoFramesDecoded && videoFramesDecoded.size() > 0 || playSpeed > 2.0f ) ) {
                                    // frame is too late and one decoded frame is already available (or playSpeed > 2)
                                    dropFrame = true;
                                    syncModeB = 'd';
                                } else if( repeatedFrame > 0 ) {
                                    syncModeB = 'r';
                                } else {
                                    syncModeB = '_';
                                }
                                video_pts_last.set(video_pts);
                                if( DEBUG_AVSYNC ) {
                                    logout.println(currentMillis, "AV"+syncModeA+syncModeB+":"+resetModeA+resetModeV+
                                            ": dT "+(currentMillis-lastMillis)+", dt[v "+dt_v+", a "+dt_a+"]/"+maxVideoDelay+", "+
                                            getPerfStringImpl(currentMillis, video_pts, use_audio,
                                                               audio_pts, audio_queued_ms, audio_pts_lb) +
                                                               ", avg dpy-fps "+avg_dpy_duration+" ms/f"); // , "+_nextFrame);
                                }
                            } // sync
                        } // valid pts and has video frames

                        if( null != videoFramesFree && null != nextFrame ) {
                            // Had frame and not single threaded ? (TEXTURE_COUNT_MIN < textureCount)
                            final TextureFrame _lastFrame = lastFrame;
                            lastFrame = nextFrame;
                            if( null != _lastFrame ) {
                                videoFramesFree.putBlocking(_lastFrame);
                            }
                        }
                        lastMillis = currentMillis;
                        audio_queued_last_ms = audio_queued_ms;
                    } while( dropFrame );
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
            displayedFrameCount++;
            av_scr_cpy.set(av_scr);
            return lastFrame;
        }
    }
    protected void preNextTextureImpl(final GL gl) {}
    protected void postNextTextureImpl(final GL gl) {}
    /**
     * Process stream until the next video frame, i.e. {@link TextureFrame}, has been reached.
     * Audio frames, i.e. {@link AudioSink.AudioFrame}, shall be handled in the process.
     * <p>
     * Video frames shall be ignored, if {@link #getVID()} is {@link #STREAM_ID_NONE}.
     * </p>
     * <p>
     * Audio frames shall be ignored, if {@link #getAID()} is {@link #STREAM_ID_NONE}.
     * </p>
     * <p>
     * Subtitle frames shall be ignored, if {@link #getSID()} is {@link #STREAM_ID_NONE}.
     * </p>
     * <p>
     * Method may be invoked on the <a href="#streamworker"><i>StreamWorker</i> decoding thread</a>.
     * </p>
     * <p>
     * Implementation shall care of OpenGL synchronization as required, e.g. glFinish()/glFlush()!
     * </p>
     * @param gl valid and current GL instance, shall be <code>null</code> for audio only.
     * @param vFrame next video {@link TextureFrame} to store the video PTS and texture data,
     *                       shall be <code>null</code> for audio only.
     * @param sTex {@link Texture} instance as bitmap subtitle target element.
     *             May be {@code null} for no desired bitmap subtitle.
     * @param sTexUsed Result value. If the {@link Texture} {@code sTex} is used and {@link #pushSubtitleTex(Object, int, int, int, int, int, int, int, int, int, int)},
     *                 {@code true} must be written into {@code sTexUsed}
     * @return the last processed video PTS value, maybe {@link TimeFrameI#INVALID_PTS} if video frame is invalid or n/a.
     *         Will be {@link TimeFrameI#END_OF_STREAM_PTS} if end of stream reached.
     * @throws InterruptedException if waiting for next frame fails
     */
    protected abstract int getNextTextureImpl(GL gl, TextureFrame vFrame, Texture sTex, boolean[] sTexUsed) throws InterruptedException;

    protected final int getNextSingleThreaded(final GL gl, final TextureFrame nextVFrame, final boolean[] gotVFrame) throws InterruptedException {
        final int pts;
        if( STREAM_ID_NONE != vid ) {
            preNextTextureImpl(gl);
            final boolean[] sTexUsed = { false };
            final Texture subTex = ( null != subTexFree && STREAM_ID_NONE != sid ) ? subTexFree.get() : null;
            pts = getNextTextureImpl(gl, nextVFrame, subTex, sTexUsed);
            postNextTextureImpl(gl);
            if( null != subTex && !sTexUsed[0] ) {
                subTexFree.putBlocking(subTex); // return unused
            }
            if( TimeFrameI.INVALID_PTS != pts ) {
                newVideoFrameAvailable(nextVFrame, Clock.currentMillis());
                gotVFrame[0] = true;
            } else {
                gotVFrame[0] = false;
            }
        } else {
            // audio only
            pts = getNextTextureImpl(null, null, null, null);
            gotVFrame[0] = false;
        }
        return pts;
    }


    /**
     * {@inheritDoc}
     * <p>
     * Note: All {@link AudioSink} operations are performed from {@link GLMediaPlayerImpl},
     * i.e. {@link #resume()}, {@link #pause(boolean)}, {@link #seek(int)}, {@link #setPlaySpeed(float)}, {@link #getAudioPTS()}.
     * </p>
     * <p>
     * Implementations using an {@link AudioSink} shall write it's instance to {@link #audioSink}
     * from within their {@link #initStreamImpl(int, String, int, String, int)} implementation.
     * </p>
     */
    @Override
    public final AudioSink getAudioSink() { return audioSink; }

    private void flushAllVideoFrames() {
        if( null != videoFramesFree ) {
            videoFramesFree.resetFull(videoFramesOrig);
            lastFrame = videoFramesFree.get();
            if( null == lastFrame ) { throw new InternalError("XXX"); }
            videoFramesDecoded.clear();
        }
        cachedFrame = null;
    }
    private void resetAVPTSAndFlush(final boolean set_scr_cpy) {
        resetSCR(av_scr);
        if( set_scr_cpy ) {
            av_scr_cpy.set(av_scr);
        }
        audio_queued_last_ms = 0;
        audio_dequeued_last = 0;
        resetAVPTS();
        flushAllVideoFrames();
        if( null != audioSink ) {
            audioSink.flush();
        }
    }
    private void resetSCR(final PTS pts) {
        av_scr.set(pts);
        audio_dpts_cum = 0;
        audio_dpts_count = 0;
        video_dpts_cum = 0;
        video_dpts_count = 0;
    }
    private void resetAVPTS() {
        nullFrameCount = 0;
        presentedFrameCount = 0;
        displayedFrameCount = 0;
        decodedFrameCount = 0;
        video_scr_reset = true;
        audio_scr_reset = true;
    }
    private static final int getDPTSAvg(final float dpts_cum, final int dpts_count) {
        final int dpts_avg = (int) ( dpts_cum * (1.0f - AV_DPTS_COEFF) + 0.5f );
        final int dpts_avg_diff = dpts_count >= AV_DPTS_NUM ? dpts_avg : 0;
        return dpts_avg_diff;
    }

    private final void newVideoFrameAvailable(final TextureFrame frame, final long currentMillis) {
        decodedFrameCount++; // safe: only written-to either from stream-worker or user thread
        if( 0 == frame.getDuration() ) { // patch frame duration if not set already
            frame.setDuration( (int) frame_duration );
        }
        synchronized(frameListenerLock) {
            final int sz = frameListener.size();
            for(int i=0; i<sz; ++i) {
                frameListener.get(i).newFrameAvailable(this, frame, currentMillis);
            }
        }
    }

    /**
     * After {@link GLMediaPlayerImpl#initStreamImpl(int, String, int, String, int) initStreamImpl(..)} is completed via
     * {@link GLMediaPlayerImpl#updateAttributes(int, int, int, int, int, int, int, int, int, float, int, int, int, String, String) updateAttributes(..)},
     * the latter decides whether StreamWorker is being used.
     */
    private final class StreamWorker {
        private volatile GLContext sharedGLCtx = null;
        private boolean hasSharedGLCtx = false;
        private GLDrawable dummyDrawable = null;

        private void makeCurrent(final GLContext ctx) {
            if( GLContext.CONTEXT_NOT_CURRENT >= ctx.makeCurrent() ) {
                throw new GLException("Couldn't make ctx current: "+ctx);
            }
        }

        private void destroySharedGL() {
            if( null != sharedGLCtx ) {
                if( sharedGLCtx.isCreated() ) {
                    // Catch dispose GLExceptions by GLEventListener, just 'print' them
                    // so we can continue with the destruction.
                    try {
                        sharedGLCtx.destroy();
                    } catch (final GLException gle) {
                        gle.printStackTrace();
                    }
                }
                sharedGLCtx = null;
                hasSharedGLCtx = false;
            }
            if( null != dummyDrawable ) {
                final AbstractGraphicsDevice device = dummyDrawable.getNativeSurface().getGraphicsConfiguration().getScreen().getDevice();
                dummyDrawable.setRealized(false);
                dummyDrawable = null;
                synchronized( singleLock ) {
                    if( singleDEBUG ) { System.err.println("ZZZ:       device "+singleCount+": "+device.getClass()+", "+device); }
                    if( device == singleDevice && 0 == --singleCount ) {
                        DefaultGraphicsDevice.swapHandleAndOwnership(singleOwner, singleDevice);
                        if( singleDEBUG ) {
                            System.err.println("ZZZ: singleOwner   "+singleOwner.getClass()+", "+singleOwner);
                            System.err.println("ZZZ: singleDevice "+singleDevice.getClass()+", "+singleDevice);
                        }
                        device.close();
                        singleOwner = null;
                        singleDevice = null;
                    }
                }
            }
        }

        public final synchronized void initGL(final GL gl) {
            if( null == gl ) {
                return;
            }
            final GLContext glCtx = gl.getContext();
            final boolean glCtxCurrent = glCtx.isCurrent();
            final GLProfile glp = gl.getGLProfile();
            final GLDrawableFactory factory = GLDrawableFactory.getFactory(glp);
            boolean createNewDevice = true;
            AbstractGraphicsDevice device = glCtx.getGLDrawable().getNativeSurface().getGraphicsConfiguration().getScreen().getDevice();
            synchronized( singleLock ) {
                if( null == singleOwner || singleOwner.getUniqueID().equals(device.getUniqueID()) ) {
                    if( null == singleOwner ) {
                        singleDevice = (DefaultGraphicsDevice) NativeWindowFactory.createDevice(device.getType(), device.getConnection(), device.getUnitID(), true);
                        singleOwner = new DefaultGraphicsDevice(singleDevice.getType(), singleDevice.getConnection(), singleDevice.getUnitID(), singleDevice.getHandle(), null);
                        DefaultGraphicsDevice.swapHandleAndOwnership(singleOwner, singleDevice);
                        if( singleDEBUG ) {
                            System.err.println("XXX: origDevice "+device.getClass()+", "+device);
                            System.err.println("XXX: singleOwner  "+singleOwner.getClass()+", "+singleOwner);
                            System.err.println("XXX: singleDevice "+singleDevice.getClass()+", "+singleDevice);
                        }
                    }
                    createNewDevice = false;
                    device = singleDevice;
                    ++singleCount;
                    if( singleDEBUG ) { System.err.println("XXX: singleDevice "+singleCount+": "+device.getClass()+", "+device); }
                } else {
                    if( singleDEBUG ) { System.err.println("XXX: createDevice from "+device.getClass()+", "+device); }
                }
            }
            dummyDrawable = factory.createDummyDrawable(device, createNewDevice, glCtx.getGLDrawable().getChosenGLCapabilities(), null);
            dummyDrawable.setRealized(true);
            sharedGLCtx = dummyDrawable.createContext(glCtx);
            hasSharedGLCtx = null != sharedGLCtx;
            makeCurrent(sharedGLCtx);
            if( glCtxCurrent ) {
                makeCurrent(glCtx);
            } else {
                sharedGLCtx.release();
            }
        }
        public final synchronized void pause(final boolean waitUntilDone) {
            wt.pause(waitUntilDone);;
        }
        public final synchronized void resume() {
            wt.resume();
        }
        private final synchronized void stop(final boolean waitUntilDone) {
            wt.stop(waitUntilDone);
        }

        private final synchronized StreamException getStreamException() {
            final Exception e = wt.getError(true);
            if( null != e ) {
                return new StreamException(e);
            }
            return null;
        }

        private final WorkerThread.StateCallback stateCB = (final WorkerThread self, final WorkerThread.StateCallback.State cause) -> {
            switch( cause ) {
                case INIT:
                    break;
                case PAUSED:
                    if( hasSharedGLCtx ) {
                        postNextTextureImpl(sharedGLCtx.getGL());
                        sharedGLCtx.release();
                    }
                    break;
                case RESUMED:
                    if( hasSharedGLCtx ) {
                        makeCurrent(sharedGLCtx);
                        preNextTextureImpl(sharedGLCtx.getGL());
                    }
                    if( null == videoFramesFree && STREAM_ID_NONE != vid ) {
                        throw new InternalError("XXX videoFramesFree is null");
                    }
                    break;
                case END:
                    if( hasSharedGLCtx ) {
                        postNextTextureImpl(sharedGLCtx.getGL());
                    }
                    destroySharedGL();
                    break;
                default:
                    break;
            }
        };
        private final WorkerThread.Callback action = (final WorkerThread self) -> {
            final GL gl;
            TextureFrame vidFrame = null;
            final boolean[] subTexUsed = { false };
            Texture subTex = null;
            try {
                if( STREAM_ID_NONE != vid ) {
                    vidFrame = videoFramesFree.getBlocking();
                    vidFrame.setPTS( TimeFrameI.INVALID_PTS ); // mark invalid until processed!
                    gl = sharedGLCtx.getGL();
                } else {
                    gl = null;
                }
                if( null != gl && STREAM_ID_NONE != sid && null != subTexFree ) {
                    subTex = subTexFree.getBlocking();
                }
                final int vPTS = getNextTextureImpl(gl, vidFrame, subTex, subTexUsed);
                if( null != subTex ) {
                    if( !subTexUsed[0] ) {
                        subTexFree.putBlocking(subTex);// return unused
                    } else if( subDEBUG ) {
                        System.err.println("GLMediaPlayer: Consumed SubTex: sid "+sid+", free "+subTexFree+", subTex "+subTex);
                    }
                }
                boolean audioEOS = false;
                if( TimeFrameI.INVALID_PTS != vPTS ) {
                    if( null != vidFrame ) {
                        if( STREAM_WORKER_DELAY > 0 ) {
                            java.lang.Thread.sleep(STREAM_WORKER_DELAY);
                        }
                        if( !videoFramesDecoded.put(vidFrame) ) {
                            throw new InternalError("XXX: free "+videoFramesFree+", decoded "+videoFramesDecoded+", "+GLMediaPlayerImpl.this);
                        }
                        newVideoFrameAvailable(vidFrame, Clock.currentMillis());
                        vidFrame = null;
                    } else {
                        // audio only
                        if( TimeFrameI.END_OF_STREAM_PTS == vPTS || ( duration > 0 && duration < vPTS ) ) {
                            audioEOS = true;
                        } else {
                            nullFrameCount = 0;
                        }
                    }
                } else if( null == vidFrame ) {
                    // audio only
                    audioEOS = maxNullFrameCountUntilEOS <= nullFrameCount;
                    if( null == audioSink || 0 == audioSink.getEnqueuedFrameCount() ) {
                        nullFrameCount++;
                    }
                }
                if( audioEOS ) {
                    // state transition incl. notification
                    self.pause(false);
                    if( DEBUG || DEBUG_AVSYNC ) {
                        logout.println( "AV-EOS (StreamWorker): EOS_PTS "+(TimeFrameI.END_OF_STREAM_PTS == vPTS)+", "+GLMediaPlayerImpl.this);
                    }
                    pauseImpl(true, new GLMediaPlayer.EventMask(GLMediaPlayer.EventMask.Bit.EOS));
                }
            } finally {
                if( null != vidFrame ) { // put back
                    videoFramesFree.putBlocking(vidFrame);
                }
            }
        };
        final WorkerThread wt =new WorkerThread(null, null, true /* daemonThread */, action, stateCB);

        /**
         * Starts this daemon thread,
         * <p>
         * This thread pauses after it's started!
         * </p>
         **/
        StreamWorker() {
            wt.start( true );
        }
    }
    private volatile StreamWorker streamWorker = null;
    private StreamException streamErr = null;
    private static final boolean singleDEBUG = false;
    private static final Object singleLock = new Object();
    private static DefaultGraphicsDevice singleDevice = null;
    private static DefaultGraphicsDevice singleOwner = null;
    private static int singleCount = 0;

    protected final void pushSound(final ByteBuffer sampleData, final int data_size, final int audio_pts) {
        if( audioStreamEnabled() ) {
            audioSink.enqueueData( audio_pts, sampleData, data_size);
        }
    }
    protected final void pushSubtitleEmpty(final int start_display_pts, final int end_display_pts) {
        if( null != subEventListener ) {
            subEventListener.run( new SubEmptyEvent(start_display_pts, end_display_pts) );
        }
    }
    protected final void pushSubtitleText(final String text, final int start_display_pts, final int end_display_pts) {
        if( null != subEventListener ) {
            subEventListener.run( new SubTextEvent(this.scodecID, getLang(getSID()), SubTextEvent.TextFormat.TEXT, text, start_display_pts, end_display_pts) );
        }
    }
    protected final void pushSubtitleASS(final String ass, final int start_display_pts, final int end_display_pts) {
        if( null != subEventListener ) {
            subEventListener.run( new SubTextEvent(this.scodecID, getLang(getSID()), SubTextEvent.TextFormat.ASS, ass, start_display_pts, end_display_pts) );
        }
    }
    /** {@link GLMediaPlayerImpl#pushSubtitleTex(Object, int, int, int, int, int, int, int, int, int)} */
    private final SubBitmapEvent.TextureOwner subTexRelease = new SubBitmapEvent.TextureOwner() {
        @Override
        public void release(final Texture subTex) {
            if( null != subTexFree && null != subTex ) {
                // return unused
                try {
                    subTexFree.putBlocking(subTex);
                    if( subDEBUG ) {
                        System.err.println("GLMediaPlayer: Released SubTex: sid "+sid+", free "+subTexFree+", subTex "+subTex);
                    }
                } catch (final InterruptedException e) {
                    throw new InternalError("GLMediaPlayer.SubTexRelease: Release failed, all full: sid "+sid+", free "+subTexFree+", subTex "+subTex+", "+GLMediaPlayerImpl.this, e);
                }
            }
        }

    };
    protected final void pushSubtitleTex(final Object texObj, final int texID, final int texWidth, final int texHeight,
                                         final int x, final int y, final int width, final int height,
                                         final int start_display_pts, final int end_display_pts)
    {
        final Texture subTex = (Texture)texObj;
        if( null != subTex ) {
            subTex.set(texWidth, texHeight, width, height);
        }
        if( null != subEventListener ) {
            subEventListener.run( new SubBitmapEvent(this.scodecID, getLang(getSID()), new Vec2i(x, y), new Vec2i(width, height),
                                                     subTex, start_display_pts, end_display_pts, subTexRelease) );
        } else {
            subTexRelease.release(subTex); // release right away
        }
    }

    protected final GLMediaPlayer.EventMask addStateEventMask(final GLMediaPlayer.EventMask eventMask, final State newState) {
        if( state != newState ) {
            switch( newState ) {
                case Uninitialized:
                    eventMask.setBit(GLMediaPlayer.EventMask.Bit.Uninit);
                    break;
                case Initialized:
                    eventMask.setBit(GLMediaPlayer.EventMask.Bit.Init);
                    break;
                case Playing:
                    eventMask.setBit(GLMediaPlayer.EventMask.Bit.Play);
                    break;
                case Paused:
                    eventMask.setBit(GLMediaPlayer.EventMask.Bit.Pause);
                    break;
            }
        }
        return eventMask;
    }

    protected final void attributesUpdated(final GLMediaPlayer.EventMask eventMask) {
        if( !eventMask.isZero() ) {
            final long now = Clock.currentMillis();
            if( DEBUG ) {
                logout.println("GLMediaPlayer.AttributesChanged: "+eventMask+", state "+state+", when "+now);
            }
            synchronized(eventListenerLock) {
                final int sz = eventListener.size();
                for(int i=0; i<sz; ++i) {
                    eventListener.get(i).attributesChanged(this, eventMask, now);
                }
            }
        }
    }

    protected final void changeState(GLMediaPlayer.EventMask eventMask, final State newState) {
        eventMask = addStateEventMask(eventMask, newState);
        if( !eventMask.isZero() ) {
            setState( newState );
            if(State.Uninitialized == state) {
                textureLookupFunctionName = "texture2D";
                textureFragmentShaderHashCode = 0;
            }
            attributesUpdated( eventMask );
        }
    }

    /**
     * Called initially by {@link #initStreamImpl(int, String, int, String, int)}, which
     * is called off-thread by {@link #playStream(Uri, int, int, int, int)}.
     * <p>
     * The latter catches an occurring exception and set the state delivers the error events.
     * </p>
     * <p>
     * Further calls are issues off-thread by the decoder implementation.
     * </p>
     */
    protected final void updateAttributes(final String title,
                                          final int[] v_streams, final String[] v_langs, int vid,
                                          final int[] a_streams, final String[] a_langs, int aid,
                                          final int[] s_streams, final String[] s_langs, int sid,
                                          final int width, final int height,
                                          final int bps_stream, final int bps_video, final int bps_audio,
                                          final float fps, final int videoFrames, final int audioFrames, final int duration,
                                          final String vcodec, final String acodec, final String scodec,
                                          final int ffmpegVCodecID, final int ffmpegACodecID, final int ffmpegSCodecID)
    {
        final GLMediaPlayer.EventMask eventMask = new GLMediaPlayer.EventMask();
        final boolean wasUninitialized = state == State.Uninitialized;

        if( wasUninitialized ) {
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.Init);
            setState( State.Initialized );
        }
        if( null == title ) {
            final String basename;
            final String s = getUri().path.decode();
            final int li = s.lastIndexOf('/');
            if( 0 < li ) {
                basename = s.substring(li+1);
            } else {
                basename = s;
            }
            final int di = basename.lastIndexOf('.');
            if( 0 < di ) {
                this.title = basename.substring(0, di);
            } else {
                this.title = basename;
            }
        } else {
            this.title = title;
        }

        this.v_streams = v_streams;
        this.v_langs = v_langs;
        this.a_streams = a_streams;
        this.a_langs = a_langs;
        this.s_streams = s_streams;
        this.s_langs = s_langs;

        if( STREAM_ID_AUTO == vid || 0 == v_streams.length ) {
            vid = STREAM_ID_NONE;
        }
        if( this.vid != vid ) {
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.VID);
            this.vid = vid;
        }

        if( STREAM_ID_AUTO == aid || 0 == a_streams.length ) {
            aid = STREAM_ID_NONE;
        }
        if( this.aid != aid ) {
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.AID);
            this.aid = aid;
        }

        if( STREAM_ID_AUTO == sid || 0 == s_streams.length ) {
            sid = STREAM_ID_NONE;
        }
        if( this.sid != sid ) {
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.SID);
            this.sid = sid;
        }

        if( this.width != width || this.height != height ) {
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.Size);
            this.width = width;
            this.height = height;
        }
        if( this.fps != fps ) {
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.FPS);
            this.fps = fps;
            if( 0 != fps ) {
                this.frame_duration = 1000f / fps;
                final int fdurI = (int)this.frame_duration;
                if( 0 < fdurI ) {
                    this.maxNullFrameCountUntilEOS = MAX_FRAMELESS_MS_UNTIL_EOS / fdurI;
                } else {
                    this.maxNullFrameCountUntilEOS = MAX_FRAMELESS_UNTIL_EOS_DEFAULT;
                }
            } else {
                this.frame_duration = 0;
                this.maxNullFrameCountUntilEOS = MAX_FRAMELESS_UNTIL_EOS_DEFAULT;
            }
        }
        if( this.bps_stream != bps_stream || this.bps_video != bps_video || this.bps_audio != bps_audio ) {
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.BPS);
            this.bps_stream = bps_stream;
            this.bps_video = bps_video;
            this.bps_audio = bps_audio;
        }
        if( this.videoFrames != videoFrames || this.audioFrames != audioFrames || this.duration != duration ) {
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.Length);
            this.videoFrames = videoFrames;
            this.audioFrames = audioFrames;
            this.duration = duration;
        }
        if( (null!=acodec && acodec.length()>0 && !this.acodec.equals(acodec)) ) {
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.Codec);
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.ACodec);
            this.acodec = acodec;
        }
        if( (null!=vcodec && vcodec.length()>0 && !this.vcodec.equals(vcodec)) ) {
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.Codec);
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.VCodec);
            this.vcodec = vcodec;
        }
        if( (null!=scodec && scodec.length()>0 && !this.scodec.equals(scodec)) ) {
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.Codec);
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.SCodec);
            this.scodec = scodec;
        }
        final CodecID acodecID = CodecID.fromFFmpeg(ffmpegACodecID);
        final CodecID vcodecID = CodecID.fromFFmpeg(ffmpegVCodecID);
        final CodecID scodecID = CodecID.fromFFmpeg(ffmpegSCodecID);
        if( (0<ffmpegACodecID && CodecID.isAudioCodec(acodecID, true) && this.acodecID != acodecID) ) {
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.Codec);
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.ACodec);
            this.acodecID = acodecID;
        }
        if( (0<ffmpegVCodecID && CodecID.isVideoCodec(vcodecID) && this.vcodecID != vcodecID) ) {
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.Codec);
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.VCodec);
            this.vcodecID = vcodecID;
        }
        if( (0<ffmpegSCodecID && CodecID.isSubtitleCodec(scodecID) && this.scodecID != scodecID) ) {
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.Codec);
            eventMask.setBit(GLMediaPlayer.EventMask.Bit.SCodec);
            this.scodecID = scodecID;
        }
        if( eventMask.isZero() ) {
            return;
        }
        if( wasUninitialized ) {
            updateMetadata();
            if( DEBUG ) {
                logout.println("XXX Initialize @ updateAttributes: "+this);
            }
        }
        attributesUpdated(eventMask);
    }

    protected void setIsGLOriented(final boolean isGLOriented) {
        if( isInGLOrientation != isGLOriented ) {
            if( DEBUG ) {
                logout.println("XXX gl-orient "+isInGLOrientation+" -> "+isGLOriented);
            }
            isInGLOrientation = isGLOriented;
            if( null != videoFramesOrig ) {
                for(int i=0; i<videoFramesOrig.length; i++) {
                    videoFramesOrig[i].getTexture().setMustFlipVertically(!isGLOriented);
                }
                attributesUpdated(new GLMediaPlayer.EventMask(GLMediaPlayer.EventMask.Bit.Size));
            }
        }
    }

    @Override
    public final Uri getUri() { return streamLoc; }

    private static int getNextImpl(final int[] array, final int current, final boolean use_no_stream) {
        final int alt = array.length > 0 ? array[0] : STREAM_ID_NONE;
        if( STREAM_ID_NONE == current ) {
            return alt;
        }
        if( array.length > 1 ) {
            for(int i=0; i<array.length; ++i) {
                if( current == array[i] ) {
                    if( i+1 < array.length ) {
                        return array[i+1];
                    } else {
                        return use_no_stream ? STREAM_ID_NONE : array[0];
                    }
                }
            }
        }
        return alt;
    }

    @Override
    public final int[] getVStreams() { return v_streams; }

    @Override
    public String[] getVLangs() { return v_langs; }

    @Override
    public final int getVID() { return vid; }

    @Override
    public int getNextVID() {
        return getNextImpl(v_streams, vid, false);
    }

    @Override
    public final int[] getAStreams() { return a_streams; }

    @Override
    public String[] getALangs() { return a_langs; }

    @Override
    public final int getAID() { return aid; }

    @Override
    public final int getNextAID() {
        return getNextImpl(a_streams, aid, false);
    }
    @Override
    public final int[] getSStreams() { return s_streams; }

    @Override
    public String[] getSLangs() { return s_langs; }

    @Override
    public final int getSID() { return sid; }

    @Override
    public int getNextSID() {
        return getNextImpl(s_streams, sid, true);
    }

    @Override
    public final boolean hasStreamID(final int id) {
        for(int i = v_streams.length-1; i>=0; --i) {
            if( v_streams[i] == id ) { return true; }
        }
        for(int i = a_streams.length-1; i>=0; --i) {
            if( a_streams[i] == id ) { return true; }
        }
        for(int i = s_streams.length-1; i>=0; --i) {
            if( s_streams[i] == id ) { return true; }
        }
        return false;
    }

    @Override
    public String getLang(final int id) {
        for(int i = v_streams.length-1; i>=0; --i) {
            if( v_streams[i] == id ) { return v_langs[i]; }
        }
        for(int i = a_streams.length-1; i>=0; --i) {
            if( a_streams[i] == id ) { return a_langs[i]; }
        }
        for(int i = s_streams.length-1; i>=0; --i) {
            if( s_streams[i] == id ) { return s_langs[i]; }
        }
        return "undef";
    }

    @Override
    public final CodecID getVideoCodecID() { return vcodecID; }

    @Override
    public final CodecID getAudioCodecID() { return acodecID; }

    @Override
    public CodecID getSubtitleCodecID() { return scodecID; }

    @Override
    public final String getVideoCodec() { return vcodec; }

    @Override
    public final String getAudioCodec() { return acodec; }

    @Override
    public String getSubtitleCodec() { return scodec; }

    @Override
    public final int getVideoFrames() { return videoFrames; }

    @Override
    public final int getAudioFrames() { return audioFrames; }

    @Override
    public final int getDuration() { return duration; }

    @Override
    public final long getStreamBitrate() { return bps_stream; }

    @Override
    public final int getVideoBitrate() { return bps_video; }

    @Override
    public final int getAudioBitrate() { return bps_audio; }

    @Override
    public final float getFramerate() { return fps; }

    @Override
    public final boolean isGLOriented() { return isInGLOrientation; }

    @Override
    public final int getWidth() { return width; }

    @Override
    public final int getHeight() { return height; }

    /** Implementation shall update metadata, e.g. {@link #getChapters()} if supported. Called after {@link State#Initialized} is reached. */
    protected void updateMetadata() {}

    @Override
    public String getTitle() { return this.title; }

    @Override
    public Chapter[] getChapters() { return new Chapter[0]; }

    @Override
    public final Chapter getChapter(final int msec) {
        for(final Chapter c : getChapters()) {
            if( c.start <= msec && msec <= c.end ) {
                return c;
            }
        }
        return null;
    }

    @Override
    public final String toString() {
        final String tt = PTS.toTimeStr(getDuration());
        final String loc = ( null != streamLoc ) ? streamLoc.toString() : "<undefined stream>" ;
        final int freeVideoFrames = null != videoFramesFree ? videoFramesFree.size() : 0;
        final int decVideoFrames = null != videoFramesDecoded ? videoFramesDecoded.size() : 0;
        final int video_scr_ms = av_scr.getCurrent();
        final String camPath = null != cameraPath ? ", camera: "+cameraPath : "";
        return getClass().getSimpleName()+"["+state+", vSCR "+video_scr_ms+", "+getChapters().length+" chapters, duration "+tt+", frames[p "+presentedFrameCount+", d "+decodedFrameCount+", t "+videoFrames+", z "+nullFrameCount+" / "+maxNullFrameCountUntilEOS+"], "+
               "speed "+playSpeed+", "+bps_stream+" bps, hasSW "+(null!=streamWorker)+
               ", Texture[count "+textureCount+", free "+freeVideoFrames+", dec "+decVideoFrames+", tagt "+toHexString(textureTarget)+", ifmt "+toHexString(textureInternalFormat)+", fmt "+toHexString(textureFormat)+", type "+toHexString(textureType)+"], "+
               "Video[id "+vid+"/"+Arrays.toString(v_streams)+"/"+Arrays.toString(v_langs)+", "+vcodecID+"/'"+vcodec+"', "+width+"x"+height+", glOrient "+isInGLOrientation+", "+fps+" fps, "+frame_duration+" fdur, "+bps_video+" bps], "+
               "Audio[id "+aid+"/"+Arrays.toString(a_streams)+"/"+Arrays.toString(a_langs)+", "+acodecID+"/'"+acodec+"', "+bps_audio+" bps, "+audioFrames+" frames], "+
               "Subs[id "+sid+"/"+Arrays.toString(s_streams)+"/"+Arrays.toString(s_langs)+", "+scodecID+"/'"+scodec+"'], uri "+loc+camPath+"]";
    }

    @Override
    public final String getPerfString() {
        final long currentMillis = Clock.currentMillis();
        final PTS audio_pts = getAudioPTSImpl();
        final int audio_queued_ms = getAudioQueuedDuration();
        final int audio_pts_lb = getLastBufferedAudioPTS();
        return getPerfStringImpl(currentMillis, video_pts_last, audioStreamEnabled(), audio_pts, audio_queued_ms, audio_pts_lb);
    }
    private final String getPerfStringImpl(final long currentMillis, final PTS video_pts, final boolean use_audio,
                                           final PTS audio_pts, final int audio_queued_ms, final int autio_pts_lb) {
        final int audio_dequeued_ms = getAudioDequeued(audio_queued_ms);

        // d_vpts > 0: video too fast (in front of SCR) repeat frame, < 0: video too slow (behind SCR) drop frame
        final int d_vpts = video_pts.getLast() - av_scr.get(currentMillis); // equals: video_pts.diff(currentMillis, av_scr);
        final int video_dpts_avrg = getDPTSAvg(video_dpts_cum, video_dpts_count);

        // d_apts > 0: audio too slow (behind SCR) repeat video frame, < 0: audio too fast (in front of SCR) drop video frame
        final int d_apts, audio_dpts_avrg, d_avpts0, d_avpts1;
        if( use_audio && audio_pts.isValid() ) {
            d_apts = av_scr.diff(currentMillis, audio_pts);
            audio_dpts_avrg = getDPTSAvg(audio_dpts_cum, audio_dpts_count);
            d_avpts0 = video_pts.diff(currentMillis, audio_pts);
            d_avpts1 = video_dpts_avrg - audio_dpts_avrg;
        } else {
            d_apts = 0;
            audio_dpts_avrg = 0;
            d_avpts0 = 0;
            d_avpts1 = 0;
        }

        final int vFramesQueued, vFramesFree;
        if( null != videoFramesDecoded ) {
            vFramesQueued = this.videoFramesDecoded.size();
            vFramesFree = this.videoFramesFree.size();
        } else {
            vFramesQueued = 0;
            vFramesFree = 1;
        }
        return "frames[p"+presentedFrameCount+" d"+decodedFrameCount+" q"+vFramesQueued+" r"+repeatedFrame+" f"+vFramesFree+"/"+videoFramesOrig.length+"], "+
               "dAV[v-a "+d_avpts0+", avg "+d_avpts1+"], SCR "+av_scr.get(currentMillis)+
               ", vpts "+video_pts.getLast()+", dSCR["+d_vpts+", avg "+video_dpts_avrg+"]"+
               ", apts "+audio_pts.get(currentMillis)+" dSCR["+d_apts+", avg "+audio_dpts_avrg+
               "] (deq "+audio_dequeued_ms+"ms, left "+audio_queued_ms+"ms, lb "+autio_pts_lb+")]";
    }

    @Override
    public final void addEventListener(final GLMediaEventListener l) {
        if(l == null) {
            return;
        }
        synchronized(eventListenerLock) {
            eventListener.add(l);
        }
    }

    @Override
    public final void removeEventListener(final GLMediaEventListener l) {
        if (l == null) {
            return;
        }
        synchronized(eventListenerLock) {
            eventListener.remove(l);
        }
    }

    @Override
    public final GLMediaEventListener[] getEventListeners() {
        synchronized(eventListenerLock) {
            return eventListener.toArray(new GLMediaEventListener[eventListener.size()]);
        }
    }
    private final Object eventListenerLock = new Object();

    @Override
    public final void addFrameListener(final GLMediaFrameListener l) {
        if(l == null) {
            return;
        }
        synchronized(frameListenerLock) {
            frameListener.add(l);
        }
    }

    @Override
    public final void removeFrameListener(final GLMediaFrameListener l) {
        if (l == null) {
            return;
        }
        synchronized(frameListenerLock) {
            frameListener.remove(l);
        }
    }

    @Override
    public final GLMediaFrameListener[] getFrameListeners() {
        synchronized(frameListenerLock) {
            return frameListener.toArray(new GLMediaFrameListener[frameListener.size()]);
        }
    }
    private final Object frameListenerLock = new Object();

    @Override
    public final void setSubtitleEventListener(final SubtitleEventListener l) {
        this.subEventListener = l;
    }
    @Override
    public final SubtitleEventListener getSubtitleEventListener() { return subEventListener; }

    @Override
    public final Object getAttachedObject(final String name) {
        return attachedObjects.get(name);
    }

    @Override
    public final Object attachObject(final String name, final Object obj) {
        return attachedObjects.put(name, obj);
    }

    @Override
    public final Object detachObject(final String name) {
        return attachedObjects.remove(name);
    }

    private final HashMap<String, Object> attachedObjects = new HashMap<String, Object>();

    protected static final String toHexString(final long v) {
        return "0x"+Long.toHexString(v);
    }
    protected static final String toHexString(final int v) {
        return "0x"+Integer.toHexString(v);
    }
    protected static final int getPropIntVal(final Map<String, String> props, final String key) {
        final String val = props.get(key);
        try {
            return Integer.parseInt(val);
        } catch (final NumberFormatException nfe) {
            if(DEBUG) {
                logout.println("Not a valid integer for <"+key+">: <"+val+">");
            }
        }
        return 0;
    }
}
