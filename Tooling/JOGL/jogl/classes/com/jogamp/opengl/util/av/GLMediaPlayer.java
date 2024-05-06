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
package com.jogamp.opengl.util.av;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;

import jogamp.opengl.Debug;

import java.io.PrintStream;
import java.util.List;

import com.jogamp.common.av.AudioSink;
import com.jogamp.common.av.PTS;
import com.jogamp.common.av.TimeFrameI;
import com.jogamp.common.net.Uri;
import com.jogamp.math.Vec4f;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureSequence;

/**
 * GLMediaPlayer interface specifies a {@link TextureSequence} state machine
 * using a multiplexed audio/video stream as it's source.
 * <p>
 * Audio maybe supported and played back internally or via an {@link AudioSink} implementation.
 * </p>
 * <p>
 * Audio and video streams can be selected or muted via {@link #playStream(Uri, int, int, int, int)}
 * or {@link #playStream(Uri, int, String, int, String, int, int)}
 * using the appropriate <a href="#streamIDs">stream id</a>'s.
 * </p>
 * <p>
 * Camera input can be selected using the {@link #CameraInputScheme} Uri.
 * </p>
 *
 * <a name="streamworker"><h5><i>StreamWorker</i> Decoding Thread</h5></a>
 * <p>
 * Most of the stream processing is performed on the decoding thread, a.k.a. <i>StreamWorker</i>:
 * <ul>
 *   <li>Stream initialization triggered by {@link #playStream(Uri, int, int, int, int) playStream(..)} - User gets notified whether the stream has been initialized or not via {@link GLMediaEventListener#attributesChanged(GLMediaPlayer, int, long) attributesChanges(..)}.</li>
 *   <li>Stream decoding - User gets notified of a new frame via {@link GLMediaEventListener#newFrameAvailable(GLMediaPlayer, com.jogamp.opengl.util.texture.TextureSequence.TextureFrame, long) newFrameAvailable(...)}.</li>
 *   <li>Caught <a href="#streamerror">exceptions on the decoding thread</a> are delivered as {@link StreamException}s.</li>
 * </ul>
 * <i>StreamWorker</i> generates it's own {@link GLContext}, shared with the one passed to {@link #initGL(GL)}.
 * The shared {@link GLContext} allows the decoding thread to push the video frame data directly into
 * the designated {@link TextureFrame}, later returned via {@link #getNextTexture(GL)} and used by the user.
 * </p>
 * <a name="streamerror"><h7><i>StreamWorker</i> Error Handling</h7></a>
 * <p>
 * Caught exceptions on <a href="#streamworker">StreamWorker</a> are delivered as {@link StreamException}s,
 * which either degrades the {@link State} to {@link State#Uninitialized} or {@link State#Paused}.
 * </p>
 * <p>
 * An occurring {@link StreamException} triggers a {@link GLMediaEventListener#EVENT_CHANGE_ERR EVENT_CHANGE_ERR} event,
 * which can be listened to via {@link GLMediaEventListener#attributesChanged(GLMediaPlayer, int, long)}.
 * </p>
 * <p>
 * An occurred {@link StreamException} can be read via {@link #getStreamException()}.
 * </p>
 *
 * </p>
 * <a name="lifecycle"><h5>GLMediaPlayer Lifecycle</h5></a>
 * <p>
 * <table border="1">
 *   <tr><th>Action</th>                                               <th>{@link State} Before</th>                                        <th>{@link State} After</th>                                                                                                       <th>{@link EventMask#Bit Event}</th></tr>
 *   <tr><td>{@link #playStream(Uri, int, int, int, int)}</td>              <td>{@link State#Uninitialized Uninitialized}</td>                   <td>{@link State#Initialized Initialized}<sup><a href="#streamworker">1</a></sup>, {@link State#Uninitialized Uninitialized}</td>  <td>{@link EventMask.Bit#Init Init} or ( {@link EventMask.Bit#Error Error} + {@link EventMask.Bit#Uninit Uninit} )</td></tr>
 *   <tr><td>{@link #initGL(GL)}</td>                                  <td>{@link State#Initialized Initialized}, {@link State#Uninitialized Uninitialized} </td>    <td>{@link State#Playing Playing}, {@link State#Uninitialized Uninitialized}</td>                         <td>{@link EventMask.Bit#Play Play} or ( {@link EventMask.Bit#Error Error} + {@link EventMask.Bit#Uninit Uninit} )</td></tr>
 *   <tr><td>{@link #pause(boolean)}</td>                              <td>{@link State#Playing Playing}</td>                               <td>{@link State#Paused Paused}</td>                                                                                               <td>{@link EventMask.Bit#Pause Pause}</td></tr>
 *   <tr><td>{@link #resume()}</td>                                    <td>{@link State#Paused Paused}</td>                                 <td>{@link State#Playing Playing}</td>                                                                                             <td>{@link EventMask.Bit#Play Play}</td></tr>
 *   <tr><td>{@link #stop()}</td>                                      <td>{@link State#Playing Playing}, {@link State#Paused Paused}</td>  <td>{@link State#Uninitialized Uninitialized}</td>                                                                                 <td>{@link EventMask.Bit#Pause Pause}</td></tr>
 *   <tr><td>{@link #seek(int)}</td>                                   <td>{@link State#Paused Paused}, {@link State#Playing Playing}</td>  <td>{@link State#Paused Paused}, {@link State#Playing Playing}</td>                                                                <td>none</td></tr>
 *   <tr><td>{@link #getNextTexture(GL)}</td>                          <td><i>any</i></td>                                                  <td><i>same</i></td>        <td>none</td></tr>
 *   <tr><td>{@link #getLastTexture()}</td>                            <td><i>any</i></td>                                                  <td><i>same</i></td>        <td>none</td></tr>
 *   <tr><td>{@link TextureFrame#END_OF_STREAM_PTS END_OF_STREAM}</td> <td>{@link State#Playing Playing}</td>                               <td>{@link State#Paused Paused}</td>                                                                                               <td>{@link EventMask.Bit#EOS EOS} + {@link EventMask.Bit#Pause Pause}</td></tr>
 *   <tr><td>{@link StreamException}</td>                              <td><i>any</i></td>                                                  <td>{@link State#Paused Paused}, {@link State#Uninitialized Uninitialized}</td>                                                    <td>{@link EventMask.Bit#Error Error} + ( {@link EventMask.Bit#Pause Pause} or {@link EventMask.Bit#Uninit Uninit} )</td></tr>
 *   <tr><td>{@link #destroy(GL)}</td>                                 <td><i>any</i></td>                                                  <td>{@link State#Uninitialized Uninitialized}</td>                                                                                 <td>{@link EventMask.Bit#Uninit Uninit}</td></tr>
 * </table>
 * </p>
 *
 * <a name="streamIDs"><h5>Audio and video Stream IDs</h5></a>
 * <p>
 * <table border="1">
 *   <tr><th>value</th>                    <th>request</th>             <th>get</th></tr>
 *   <tr><td>{@link #STREAM_ID_NONE}</td>  <td>mute</td>                <td>not available</td></tr>
 *   <tr><td>{@link #STREAM_ID_AUTO}</td>  <td>auto</td>                <td>unspecified</td></tr>
 *   <tr><td>&ge;0</td>                    <td>specific stream</td>     <td>specific stream</td></tr>
 * </table>
 * </p>
 * <p>
 * Current implementations (check each API doc link for details):
 * <ul>
 *   <li>{@link jogamp.opengl.util.av.NullGLMediaPlayer}</li>
 *   <li>{@link jogamp.opengl.util.av.impl.OMXGLMediaPlayer}</li>
 *   <li>{@link jogamp.opengl.util.av.impl.FFMPEGMediaPlayer}</li>
 *   <li>{@link jogamp.opengl.android.av.AndroidGLMediaPlayerAPI14}</li>
 * </ul>
 * </p>
 * <p>
 * Implementations of this interface must implement:
 * <pre>
 *    public static final boolean isAvailable();
 * </pre>
 * to be properly considered by {@link GLMediaPlayerFactory#create(ClassLoader, String)}
 * and {@link GLMediaPlayerFactory#createDefault()}.
 * </p>
 * <a name="timestampaccuracy"><h5>Timestamp Accuracy</h5></a>
 * <p>
 * <p>
 * Timestamp type and value range has been chosen to suit embedded CPUs
 * and characteristics of audio and video streaming. See {@link TimeFrameI}.
 * </p>
 *
 * <a name="synchronization"><h5>Audio and video synchronization</h5></a>
 * <p>
 * The class follows a passive A/V synchronization pattern.
 * Audio is being untouched, while {@link #getNextTexture(GL)} delivers a new video frame
 * only, if its timestamp is less than {@link #MAX_VIDEO_ASYNC} ahead of <i>time</i>.
 * If its timestamp is more than {@link #MAX_VIDEO_ASYNC} ahead of <i>time</i>,
 * the previous frame is returned.
 * If its timestamp is more than {@link #MAX_VIDEO_ASYNC} after <i>time</i>,
 * the frame is dropped and the next frame is being fetched.
 * </p>
 * <p>
 * https://en.wikipedia.org/wiki/Audio_to_video_synchronization
 * <pre>
 *   d_av = v_pts - a_pts;
 * </pre>
 * </p>
 * <p>
 * Recommendation of audio/video pts time lead/lag at production:
 * <ul>
 *   <li>Overall:    +40ms and -60ms  audio ahead video / audio after video</li>
 *   <li>Each stage:  +5ms and -15ms. audio ahead video / audio after video</li>
 * </ul>
 * </p>
 * <p>
 * Recommendation of av pts time lead/lag at presentation:
 * <ul>
 *   <li>TV:         +15ms and -45ms. audio ahead video / audio after video.</li>
 *   <li>Film:       +22ms and -22ms. audio ahead video / audio after video.</li>
 * </ul>
 * </p>
 *
 * <a name="teststreams"><h5>Test Streams</h5></a>
 * <p>
 * <table border="1">
 *   <tr><th colspan=5>Big Buck Bunny 24f 16:9</th></tr>
 *   <tr><td>Big Buck Bunny</td><td>320p</td><td>h264<td>aac 48000Hz 2 chan</td><td>http://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4</td></tr>
 *   <tr><td>Big Buck Bunny</td><td>240p</td><td>h264<td>aac 48000Hz 2 chan</td><td>http://archive.org/download/BigBuckBunny_328/BigBuckBunny_512kb.mp4</td></tr>
 *   <tr><td>Big Buck Bunny</td><td>720p</td><td>mpeg4<td>ac3 48000Hz 5.1 chan</td><td>http://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_720p_surround.avi</td></tr>
 *   <tr><td>Big Buck Bunny</td><td>720p</td><td>msmpeg4v2<td>mp3 48000Hz 2 chan</td><td>http://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_720p_stereo.avi</td></tr>
 *   <tr><td>Big Buck Bunny</td><td>720p</td><td>theora<td>vorbis 48000Hz 2 chan</td><td>http://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_720p_stereo.ogg</td></tr>
 *   <tr><td>Big Buck Bunny</td><td>1080p</td><td>mpeg4<td>ac3 48000Hz 5.1 chan</td><td>http://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_1080p_surround.avi</td></tr>
 *   <tr><th colspan=5>WebM/Matroska (vp8/vorbis)</th></tr>
 *   <tr><td>Big Buck Bunny Trailer</td><td>640p</td><td>vp8<td>vorbis 44100Hz 1 chan</td><td>http://video.webmfiles.org/big-buck-bunny_trailer.webm</td></tr>
 *   <tr><td>Elephants Dream</td><td>540p</td><td>vp8<td>vorbis 44100Hz 1 chan</td><td>http://video.webmfiles.org/elephants-dream.webm</td></tr>
 *   <tr><th colspan=5>You Tube http/rtsp</th></tr>
 *   <tr><td>Sintel</td><td colspan=3>http://www.youtube.com/watch?v=eRsGyueVLvQ</td><td>rtsp://v3.cache1.c.youtube.com/CiILENy73wIaGQn0LpXnygYbeRMYDSANFEgGUgZ2aWRlb3MM/0/0/0/video.3gp</td></tr>
 *   <tr><th colspan=5>Audio/Video Sync</th></tr>
 *   <tr><td>Five-minute-sync-test1080p</td><td colspan=3>https://www.youtube.com/watch?v=szoOsG9137U</td><td>rtsp://v7.cache8.c.youtube.com/CiILENy73wIaGQm133VvsA46sxMYDSANFEgGUgZ2aWRlb3MM/0/0/0/video.3gp</td></tr>
 *   <tr><td>Audio-Video-Sync-Test-Calibration-23.98fps-24fps</td><td colspan=4>https://www.youtube.com/watch?v=cGgf_dbDMsw</td></tr>
 *   <tr><td>sound_in_sync_test</td><td colspan=4>https://www.youtube.com/watch?v=O-zIZkhXNLE</td></tr>
 *   <!-- <tr><td> title </td><td>1080p</td><td>mpeg4<td>ac3 48000Hz 5.1 chan</td><td> url </td></tr> -->
 *   <!-- <tr><td> title </td><td colspan=3> url1 </td><td> url2 </td></tr>
 * </table>
 * </p>
 * <p>
 * Since 2.3.0 this interface uses {@link Uri} instead of {@link java.net.URI}.
 * </p>
 */
public interface GLMediaPlayer extends TextureSequence {
    public static final boolean DEBUG = Debug.debug("GLMediaPlayer");
    public static final boolean DEBUG_AVSYNC = Debug.debug("GLMediaPlayer.AVSync");
    public static final boolean DEBUG_NATIVE = Debug.debug("GLMediaPlayer.Native");

    /** Default texture count, value {@value}. */
    public static final int TEXTURE_COUNT_DEFAULT = 3;

    /** Minimum texture count, value {@value}. Using the minimum texture count disables multi-threaded decoding. */
    public static final int TEXTURE_COUNT_MIN = 1;

    /** Constant {@value} for <i>mute</i> or <i>not available</i>. See <a href="#streamIDs">Audio and video Stream IDs</a>. */
    public static final int STREAM_ID_NONE = -2;
    /** Constant {@value} for <i>auto</i> or <i>unspecified</i>. See <a href="#streamIDs">Audio and video Stream IDs</a>. */
    public static final int STREAM_ID_AUTO = -1;

    /**
     * {@link Uri#scheme Uri scheme} name {@value} for camera input. E.g. <code>camera:/0</code>
     * for the 1st camera device.
     * <p>
     * The {@link Uri#path Uri path} is being used to identify the camera (<i>&lt;id&gt;</i>),
     * where the root fwd-slash is being cut-off.
     * </p>
     * <p>
     * The <i>&lt;id&gt;</i> is usually an integer value indexing the camera
     * ranging from [0..<i>max-number</i>].
     * </p>
     * <p>
     * The <i>&lt;somewhere&gt;</i> is usually empty, since it would imply a networking camera protocol.
     * </p>
     * <p>
     * The {@link Uri#query Uri query} is used to pass options to the camera
     * using <i>;</i> as the separator. The latter avoids trouble w/ escaping.
     * </p>
     * <pre>
     *    camera:/&lt;id&gt;
     *    camera:/&lt;id&gt;?width=640;height=480;rate=15
     *    camera:/&lt;id&gt;?size=640x480;rate=15
     *    camera://&lt;somewhere&gt;/&lt;id&gt;
     *    camera://&lt;somewhere&gt;/&lt;id&gt;?width=640;height=480;rate=15
     *    camera://&lt;somewhere&gt;/&lt;id&gt;?size=640x480;rate=15
     *    camera:///&lt;id&gt;?width=640;height=480;rate=15
     *    camera:///&lt;id&gt;?size=640x480;rate=15
     * </pre>
     * <pre>
     *  Uri: [scheme:][//authority][path][?query][#fragment]
     *  w/ authority: [user-info@]host[:port]
     *  Note: 'path' starts w/ fwd slash
     * </pre>
     * </p>
     */
    public static final Uri.Encoded CameraInputScheme = Uri.Encoded.cast("camera");
    /** Camera property {@value}, size as string, e.g. <code>1280x720</code>, <code>hd720</code>. May not be supported on all platforms. See {@link #CameraInputScheme}. */
    public static final String CameraPropSizeS = "size";
    /** Camera property {@value}. See {@link #CameraInputScheme}. */
    public static final String CameraPropWidth = "width";
    /** Camera property {@value}. See {@link #CameraInputScheme}. */
    public static final String CameraPropHeight = "height";
    /** Camera property {@value}. See {@link #CameraInputScheme}. */
    public static final String CameraPropRate = "rate";

    /** Maximum video frame async of {@value} milliseconds. */
    public static final int MAX_VIDEO_ASYNC = 22;
    public static final int MIN_VIDEO_ASYNC = 11;

    /**
     * A StreamException encapsulates a caught exception in the decoder thread, a.k.a <i>StreamWorker</i>,
     * see See <a href="#streamerror"><i>StreamWorker</i> Error Handling</a>.
     */
    @SuppressWarnings("serial")
    public static class StreamException extends Exception {
        public StreamException(final Throwable cause) {
            super(cause);
        }
        public StreamException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    /** Chapter meta-data of stream, see {@link GLMediaPlayer#getChapters()}. */
    public static class Chapter {
        /** Chapter ID */
        public final int id;
        /** Chapter start PTS in ms */
        public final int start;
        /** Chapter end PTS in ms */
        public final int end;
        /** Chapter title */
        public final String title;
        public Chapter(final int i, final int s, final int e, final String t) {
            id = i; start = s; end = e; title = t;
        }
        /** Returns chapter duration, i.e. {@code end - start + 1}. */
        public int duration() { return end - start + 1; }
        @Override
        public String toString() {
            return String.format("%02d: [%s .. %s] %s", id, PTS.toTimeStr(start), PTS.toTimeStr(end), title);
        }
    }

    /**
     * As the contract of {@link GLMediaFrameListener} and {@link TexSeqEventListener} requests,
     * implementations of {@link GLMediaEventListener} shall:
     * <ul>
     *   <li>off-load complex or {@link GLMediaPlayer} commands on another thread, or</li>
     *   <li>simply changing a volatile state of their {@link GLEventListener} implementation.</li>
     * </ul>
     */
    public interface GLMediaEventListener {
        /**
         * @param mp the event source
         * @param event_mask the changes attributes
         * @param when system time in msec.
         */
        public void attributesChanged(GLMediaPlayer mp, EventMask event_mask, long when);
    }
    /**
     * {@inheritDoc}
     * <p>
     * Optional Video {@link TextureFrame} listener.
     * Usually one wants to use {@link GLMediaPlayer#getNextTexture(GL)} is used to retrieve the next frame and keep
     * decoding going, while {@link GLMediaPlayer#getLastTexture(GL)} is used to simply retrieve the
     * last decoded frame.
     * </p>
     * <p>
     * As the contract of {@link TexSeqEventListener} requests,
     * implementations of {@link GLMediaEventListener} shall also:
     * <ul>
     *   <li>off-load complex or {@link GLMediaPlayer} commands on another thread, or</li>
     *   <li>simply changing a volatile state of their {@link GLEventListener} implementation.</li>
     * </ul>
     * </p>
     */
    public interface GLMediaFrameListener extends TexSeqEventListener<GLMediaPlayer> {
    }

    /** Changes attributes event mask */
    public static final class EventMask {

        /** Attribute change bits */
        public static enum Bit {
            /** State changed to {@link State#Initialized}. See <a href="#lifecycle">Lifecycle</a>.*/
            Init   ( 1<<0 ),
            /** State changed to {@link State#Uninitialized}. See <a href="#lifecycle">Lifecycle</a>.*/
            Uninit ( 1<<1 ),
            /** State changed to {@link State#Playing}. See <a href="#lifecycle">Lifecycle</a>.*/
            Play   ( 1<<2 ),
            /** State changed to {@link State#Paused}. See <a href="#lifecycle">Lifecycle</a>.*/
            Pause  ( 1<<3 ),
            /** Time position has changed, e.g. via {@link GLMediaPlayer#seek(int)}.*/
            Seek   ( 1<<4 ),
            /** End of stream reached. See <a href("#lifecycle">Lifecycle</a>.*/
            EOS    ( 1<<5 ),
            /** An error occurred, e.g. during off-thread initialization. See {@link StreamException} and <a href("#lifecycle">Lifecycle</a>. */
            Error  ( 1<<6 ),

            /** Stream video id change. */
            VID    ( 1<<16 ),
            /** Stream audio id change. */
            AID    ( 1<<17 ),
            /** Stream subtitle id change. */
            SID    ( 1<<18 ),
            /** TextureFrame size or vertical flip change. */
            Size   ( 1<<19 ),
            /** Stream fps change. */
            FPS    ( 1<<20 ),
            /** Stream bps change. */
            BPS ( 1<<21 ),
            /** Stream length change. */
            Length ( 1<<22 ),
            /** Audio, video or subtitle stream codec change. */
            Codec  ( 1<<23 ),
            /** Audio stream codec change. */
            ACodec  ( 1<<24 ),
            /** Video stream codec change. */
            VCodec  ( 1<<25 ),
            /** Subtitle stream codec change. */
            SCodec  ( 1<<26 );

            Bit(final int v) { value = v; }
            public final int value;
        }
        public int mask;

        public static int getBits(final List<Bit> v) {
            int res = 0;
            for(final Bit b : v) {
                res |= b.value;
            }
            return res;
        }
        public EventMask(final List<Bit> v) {
            mask = getBits(v);
        }
        public EventMask(final Bit v) {
            mask = v.value;
        }
        public EventMask(final int v) {
            mask = v;
        }
        public EventMask() {
            mask = 0;
        }

        public boolean isSet(final Bit bit) { return bit.value == ( mask & bit.value ); }
        public boolean isSet(final List<Bit> bits) { final int bits_i = getBits(bits); return bits_i == ( mask & bits_i ); }
        public boolean isSet(final int bits) { return bits == ( mask & bits ); }
        public boolean isZero() { return 0 == mask; }

        public EventMask setBit(final Bit v) { mask |= v.value; return this; }
        public EventMask setBits(final List<Bit> v) {
            for(final Bit b : v) {
                mask |= b.value;
            }
            return this;
        }

        @Override
        public String toString() {
            int count = 0;
            final StringBuilder out = new StringBuilder();
            for (final Bit dt : Bit.values()) {
                if( isSet(dt) ) {
                    if( 0 < count ) { out.append(", "); }
                    out.append(dt.name()); count++;
                }
            }
            if( 0 == count ) {
                out.append("None");
            } else if( 1 < count ) {
                out.insert(0, "[");
                out.append("]");
            }
            return out.toString();
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            return (other instanceof EventMask) &&
                   this.mask == ((EventMask)other).mask;
        }
    }

    /**
     * See <a href="#lifecycle">Lifecycle</a>.
     */
    public enum State {
        /** Uninitialized player, no resources shall be hold. */
        Uninitialized(0),
        /** Stream has been initialized, user may play or call {@link #initGL(GL)}. */
        Initialized(1),
        /** Stream is playing. */
        Playing(2),
        /** Stream is pausing. */
        Paused(3);

        public final int id;

        State(final int id){
            this.id = id;
        }
    }

    /** Print native library information of used implementation to given out PrintStream. */
    public void printNativeInfo(final PrintStream out);

    public int getTextureCount();

    /** Sets the texture unit. Defaults to 0. */
    public void setTextureUnit(int u);

    /** Sets the texture min-mag filter, defaults to {@link GL#GL_NEAREST}. */
    public void setTextureMinMagFilter(int[] minMagFilter);
    /** Sets the texture min-mag filter, defaults to {@link GL#GL_CLAMP_TO_EDGE}. */
    public void setTextureWrapST(int[] wrapST);

    /**
     * {@inheritDoc}
     * <p>
     * Defaults to {@code true} and toggling not supported.
     * </p>
     */
    @Override
    public boolean useARatioAdjustment();

    /**
     * {@inheritDoc}
     * <p>
     * Defaults to {@code true} and toggling is not supported.
     * </p>
     */
    @Override
    public void setARatioAdjustment(final boolean v);

    /**
     * {@inheritDoc}
     * <p>
     * Defaults to {@code false} and toggling is supported via {@link #setARatioLetterbox(boolean, Vec4f)}
     * </p>
     */
    @Override
    public boolean useARatioLetterbox();

    /**
     * {@inheritDoc}
     */
    @Override
    public Vec4f getARatioLetterboxBackColor();

    /**
     * {@inheritDoc}
     * <p>
     * Defaults to {@code false}.
     * </p>
     */
    @Override
    public void setARatioLetterbox(final boolean v, Vec4f backColor);

    /**
     * Limit maximum supported audio channels by user.
     * <p>
     * Must be set before {@link #playStream(Uri, int, int, int, int)}
     * </p>
     * <p>
     * May be utilized to enforce 1 channel (mono) downsampling
     * in combination with JOAL/OpenAL to experience spatial 3D position effects.
     * </p>
     * @param cc maximum supported audio channels, will be clipped [1..x], with x being the underlying audio subsystem's maximum
     * @see #playStream(Uri, int, int, int, int)
     */
    public void setAudioChannelLimit(final int cc);

    /**
     * Issues asynchronous stream initialization.
     * <p>
     * <a href="#lifecycle">Lifecycle</a>: {@link State#Uninitialized} -> {@link State#Initialized}<sup><a href="#streamworker">1</a></sup> or {@link State#Uninitialized}
     * </p>
     * <p>
     * {@link State#Initialized} is reached asynchronous,
     * i.e. user gets notified via {@link GLMediaEventListener#attributesChanged(GLMediaPlayer, int, long) attributesChanges(..)}.
     * </p>
     * <p>
     * A possible caught asynchronous {@link StreamException} while initializing the stream off-thread
     * will be thrown at {@link #initGL(GL)}.
     * </p>
     * <p>
     * Muted audio can be achieved by passing {@link #STREAM_ID_NONE} to <code>aid</code>.
     * </p>
     * <p>
     * Muted video can be achieved by passing {@link #STREAM_ID_NONE} to <code>vid</code>,
     * in which case <code>textureCount</code> is ignored as well as the passed GL object of the subsequent {@link #initGL(GL)} call.
     * </p>
     * @param streamLoc the stream location
     * @param vid video stream id, see <a href="#streamIDs">audio and video Stream IDs</a>
     * @param aid audio stream id, see <a href="#streamIDs">audio and video Stream IDs</a>
     * @param sid subtitle stream id, see <a href="#streamIDs">audio and video Stream IDs</a>
     * @param textureCount desired number of buffered textures to be decoded off-thread, will be validated by implementation.
     *        The minimum value is {@link #TEXTURE_COUNT_MIN} (single-threaded) or above to enable multi-threaded stream decoding.
     *        Default is {@link #TEXTURE_COUNT_DEFAULT}.
     *        Value is ignored if video is muted.
     * @throws IllegalStateException if not invoked in {@link State#Uninitialized}
     * @throws IllegalArgumentException if arguments are invalid
     * @see #playStream(Uri, int, String, int, String, int, int)
     * @since 2.6.0
     */
    public void playStream(Uri streamLoc, int vid, int aid, int sid, int textureCount) throws IllegalStateException, IllegalArgumentException;

    /**
     * Same as {@link #playStream(Uri, int, int, int, int)}, but providing desired audio- and subtile languages to be selected.
     * @param streamLoc the stream location
     * @param vid video stream id, see <a href="#streamIDs">audio and video Stream IDs</a>
     * @param alang desired audio language, pass {@code null} to use {@code aid}
     * @param aid fallback audio stream id in case {@code alang} is {@code null}, see <a href="#streamIDs">audio and video Stream IDs</a>
     * @param slang desired subtitle language, pass {@code null} to use {@code sid}
     * @param sid fallback subtitle stream id in case {@code alang} is {@code null}, see <a href="#streamIDs">audio and video Stream IDs</a>
     * @param textureCount desired number of buffered textures to be decoded off-thread, will be validated by implementation.
     *        The minimum value is {@link #TEXTURE_COUNT_MIN} (single-threaded) or above to enable multi-threaded stream decoding.
     *        Default is {@link #TEXTURE_COUNT_DEFAULT}.
     *        Value is ignored if video is muted.
     * @throws IllegalStateException if not invoked in {@link State#Uninitialized}
     * @throws IllegalArgumentException if arguments are invalid
     * @see #playStream(Uri, int, int, int, int)
     * @since 2.6.0
     */
    public void playStream(final Uri streamLoc, final int vid,
                           final String alang, final int aid, final String slang, final int sid,
                           final int reqTextureCount) throws IllegalStateException, IllegalArgumentException;

    /**
     * Switches current {@link #playStream(Uri, int, int, int, int)} to given stream IDs and continues at same {@link #getVideoPTS()}.
     * <p>
     * Implementation just issues {@link #stop()}, {@link #seek(int)} and {@link #playStream(Uri, int, int, int, int)}.
     * </p>
     * @param vid video stream id, see <a href="#streamIDs">audio and video Stream IDs</a>
     * @param aid audio stream id, see <a href="#streamIDs">audio and video Stream IDs</a>
     * @param sid subtitle stream id, see <a href="#streamIDs">audio and video Stream IDs</a>
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     * @since 2.6.0
     */
    public void switchStream(final int vid, final int aid, final int sid) throws IllegalStateException, IllegalArgumentException;

    /**
     * Returns the {@link StreamException} caught in the decoder thread, or <code>null</code> if none occured.
     * <p>
     * Method clears the cached {@link StreamException}, hence an immediate subsequent call will return <code>null</code>.
     * </p>
     * @see GLMediaEventListener#EVENT_CHANGE_ERR
     * @see StreamException
     */
    public StreamException getStreamException();

    /**
     * Initializes OpenGL related resources.
     * <p>
     * <a href="#lifecycle">Lifecycle</a>: {@link State#Initialized} -> {@link State#Paused} or {@link State#Initialized}
     * </p>
     * Argument <code>gl</code> is ignored if video is muted, see {@link #playStream(Uri, int, int, int, int)}.
     *
     * @param gl current GL object. Maybe <code>null</code>, for audio only.
     * @throws IllegalStateException if not invoked in {@link State#Initialized}.
     * @throws StreamException forwarded from the off-thread stream initialization
     * @throws GLException in case of difficulties to initialize the GL resources
     */
    public void initGL(GL gl) throws IllegalStateException, StreamException, GLException;

    /**
     * If implementation uses a {@link AudioSink}, it's instance will be returned.
     * <p>
     * The {@link AudioSink} instance is available after {@link #playStream(Uri, int, int, int, int)},
     * if used by implementation.
     * </p>
     */
    public AudioSink getAudioSink();

    /**
     * Releases the GL, stream and other resources, including {@link #attachObject(String, Object) attached user objects}.
     * <p>
     * <a href="#lifecycle">Lifecycle</a>: <code>ANY</code> -> {@link State#Uninitialized}
     * </p>
     */
    public State destroy(GL gl);

    /**
     * Stops streaming and releases the GL, stream and other resources, but keeps {@link #attachObject(String, Object) attached user objects}.
     * <p>
     * <a href="#lifecycle">Lifecycle</a>: <code>ANY</code> -> {@link State#Uninitialized}
     * </p>
     */
    public State stop();

    /**
     * Sets the playback speed.
     * <p>
     * To simplify test, play speed is  <i>normalized</i>, i.e.
     * <ul>
     *   <li><code>1.0f</code>: if <code> Math.abs(1.0f - rate) < 0.01f </code></li>
     * </ul>
     * </p>
     * @return true if successful, otherwise false, i.e. due to unsupported value range of implementation.
     */
    public boolean setPlaySpeed(float rate);

    /** Returns the playback speed. */
    public float getPlaySpeed();

    /**
     * Sets the audio volume, [0f..1f].
     * <p>
     * To simplify test, volume is <i>normalized</i>, i.e.
     * <ul>
     *   <li><code>0.0f</code>: if <code> Math.abs(v) < 0.01f </code></li>
     *   <li><code>1.0f</code>: if <code> Math.abs(1.0f - v) < 0.01f </code></li>
     * </ul>
     * </p>
     * @return true if successful, otherwise false, i.e. due to unsupported value range of implementation.
     */
    public boolean setAudioVolume(float v);

    /** Returns the audio volume. */
    public float getAudioVolume();

    /** Returns true if audio is muted, i.e. {@link #setAudioVolume(float)} to zero. */
    public boolean isAudioMuted();

    /**
     * Starts or resumes the <i>StreamWorker</i> decoding thread.
     * <p>
     * <a href="#lifecycle">Lifecycle</a>: {@link State#Paused} -> {@link State#Playing}
     * </p>
     */
    public State resume();

    /**
     * Pauses the <i>StreamWorker</i> decoding thread.
     * <p>
     * <a href="#lifecycle">Lifecycle</a>: {@link State#Playing} -> {@link State#Paused}
     * </p>
     * <p>
     * If a <i>new</i> frame is desired after the next {@link #resume()} call,
     * e.g. to make a snapshot of a camera input stream,
     * <code>flush</code> shall be set to <code>true</code>.
     * </p>
     * @param flush if <code>true</code> flushes the video and audio buffers, otherwise keep them intact.
     */
    public State pause(boolean flush);

    /**
     * Seeks to the new absolute position. The <i>StreamWorker</i> decoding thread
     * is paused while doing so and the A/V buffers are flushed.
     * <p>
     * Allowed in state {@link State#Playing} and {@link State#Paused}, otherwise ignored,
     * see <a href="#lifecycle">Lifecycle</a>.
     * </p>
     *
     * @param msec absolute desired time position in milliseconds
     * @return time current position in milliseconds, after seeking to the desired position
     **/
    public int seek(int msec);

    /**
     * See <a href="#lifecycle">Lifecycle</a>.
     * @return the current state, either {@link State#Uninitialized}, {@link State#Initialized}, {@link State#Playing} or {@link State#Paused}
     */
    public State getState();

    /**
     * Return an array of detected video stream IDs.
     */
    public int[] getVStreams();

    /**
     * Return an array of detected video stream language codes, matching {@link #getVStreams()} array and its indices.
     * <p>
     * The language code is supposed to be 3-letters of `ISO 639-2 language codes`.
     * </p>
     * @see #getLang(int)
     */
    public String[] getVLangs();

    /**
     * Return the video stream id, see <a href="#streamIDs">audio and video Stream IDs</a>.
     */
    public int getVID();

    /** Returns the next video stream id, rotates. */
    public int getNextVID();

    /**
     * Return an array of detected audio stream IDs.
     */
    public int[] getAStreams();

    /**
     * Return an array of detected audio stream language codes, matching {@link #getAStreams()} array and its indices.
     * <p>
     * The language code is supposed to be 3-letters of `ISO 639-2 language codes`.
     * </p>
     * @see #getLang(int)
     */
    public String[] getALangs();

    /**
     * Return the audio stream id, see <a href="#streamIDs">audio and video Stream IDs</a>.
     */
    public int getAID();

    /** Returns the next audio stream id, rotates. */
    public int getNextAID();

    /**
     * Return an array of detected subtitle stream IDs.
     */
    public int[] getSStreams();

    /**
     * Return an array of detected subtitle stream language codes, matching {@link #getSStreams()} array and its indices.
     * <p>
     * The language code is supposed to be 3-letters of `ISO 639-2 language codes`.
     * </p>
     * @see #getLang(int)
     */
    public String[] getSLangs();

    /**
     * Return the subtitle stream id, see <a href="#streamIDs">audio and video Stream IDs</a>.
     */
    public int getSID();

    /** Returns the next subtitle stream id, rotates including no-stream*/
    public int getNextSID();

    /**
     * Return whether the given stream ID is available, i.e. matching one of the stream IDs in {@link #getVStreams()}, {@link #getAStreams()} or {@link #getSStreams()}.
     */
    public boolean hasStreamID(int id);

    /**
     * Return the matching language code of given stream ID, matching one of the stream IDs in {@link #getVStreams()}, {@link #getAStreams()} or {@link #getSStreams()}.
     * <p>
     * The language code is supposed to be 3-letters of `ISO 639-2 language codes`.
     * </p>
     * <p>
     * If the stream ID is not available, {@code und} is returned
     * </p>
     * @see #getVStreams()
     * @see #getAStreams()
     * @see #getSStreams()
     * @see #getVLangs()
     * @see #getALangs()
     * @see #getSLangs()
     */
    public String getLang(int id);

    /**
     * @return the current decoded video frame count since {@link #resume()} and {@link #seek(int)}
     *         as increased by {@link #getNextTexture(GL)} or the decoding thread.
     */
    public int getDecodedFrameCount();

    /**
     * @return the current presented video frame count since {@link #resume()} and {@link #seek(int)}
     *         as increased by {@link #getNextTexture(GL)} for new frames.
     */
    public int getPresentedFrameCount();


    /**
     * Returns current System Clock Reference (SCR) presentation timestamp ({@link PTS}).
     * <p>
     * To retrieve the current interpolated PTS against the stored System Clock Reference (SCR), use:
     * <pre>
     *   int pts = mPlayer.getPTS().get(Clock.currentMillis());
     * </pre>
     * </p>
     **/
    public PTS getPTS();

    /**
     * Returns current video presentation timestamp (PTS) in milliseconds of {@link #getLastTexture()},
     * try using {@link #getPTS()}.
     * <p>
     * The relative millisecond PTS since start of the presentation stored in integer
     * covers a time span of 2'147'483'647 ms (see {@link Integer#MAX_VALUE}
     * or 2'147'483 seconds or 24.855 days.
     * </p>
     * @see #getPTS()
     **/
    public int getVideoPTS();

    /**
     * Returns current audio presentation timestamp (PTS) in milliseconds,
     * try using {@link #getPTS()}.
     * <p>
     * The relative millisecond PTS since start of the presentation stored in integer
     * covers a time span of 2'147'483'647 ms (see {@link Integer#MAX_VALUE}
     * or 2'147'483 seconds or 24.855 days.
     * </p>
     * @see #getPTS()
     **/
    public int getAudioPTS();

    /**
     * {@inheritDoc}
     * <p>
     * Returns the last decoded Video {@link TextureSequence.TextureFrame}.
     * </p>
     * <p>
     * See <a href="#synchronization">audio and video synchronization</a>.
     * </p>
     * @throws IllegalStateException if not invoked in {@link State#Paused} or {@link State#Playing}
     */
    @Override
    public TextureSequence.TextureFrame getLastTexture() throws IllegalStateException;

    /**
     * {@inheritDoc}
     * <p>
     * Returns the next Video {@link TextureSequence.TextureFrame} to be rendered in sync with {@link #getPTS()}
     * and keeps decoding going.
     * </p>
     * <p>
     * In case the current state is not {@link State#Playing}, {@link #getLastTexture()} is returned.
     * </p>
     * <p>
     * See <a href="#synchronization">audio and video synchronization</a>.
     * </p>
     * @throws IllegalStateException if not invoked in {@link State#Paused} or {@link State#Playing}
     *
     * @see #addEventListener(GLMediaEventListener)
     * @see GLMediaEventListener#newFrameAvailable(GLMediaPlayer, TextureFrame, long)
     */
    @Override
    public TextureSequence.TextureFrame getNextTexture(GL gl) throws IllegalStateException;

    /**
     * Return the stream location, as set by {@link #playStream(Uri, int, int, int, int)}.
     * @since 2.3.0
     */
    public Uri getUri();

    /**
     * <i>Warning:</i> Optional information, may not be supported by implementation.
     * @return the {@link CodecID} of the video stream, if available
     */
    public CodecID getVideoCodecID();

    /**
     * <i>Warning:</i> Optional information, may not be supported by implementation.
     * @return the codec of the video stream, if available
     */
    public String getVideoCodec();

    /**
     * <i>Warning:</i> Optional information, may not be supported by implementation.
     * @return the {@link CodecID} of the audio stream, if available
     */
    public CodecID getAudioCodecID();

    /**
     * <i>Warning:</i> Optional information, may not be supported by implementation.
     * @return the codec of the audio stream, if available
     */
    public String getAudioCodec();

    /**
     * <i>Warning:</i> Optional information, may not be supported by implementation.
     * @return the {@link CodecID} of the subtitle stream, if available
     */
    public CodecID getSubtitleCodecID();

    /**
     * <i>Warning:</i> Optional information, may not be supported by implementation.
     * @return the codec of the subtitle stream, if available
     */
    public String getSubtitleCodec();

    /**
     * <i>Warning:</i> Optional information, may not be supported by implementation.
     * @return the total number of video frames
     */
    public int getVideoFrames();

    /**
     * <i>Warning:</i> Optional information, may not be supported by implementation.
     * @return the total number of audio frames
     */
    public int getAudioFrames();

    /**
     * Return total duration of stream in msec.
     * <p>
     * The duration stored in integer covers 2'147'483'647 ms (see {@link Integer#MAX_VALUE}
     * or 2'147'483 seconds or 24.855 days.
     * </p>
     */
    public int getDuration();

    /**
     * <i>Warning:</i> Optional information, may not be supported by implementation.
     * @return the overall bitrate of the stream.
     */
    public long getStreamBitrate();

    /**
     * <i>Warning:</i> Optional information, may not be supported by implementation.
     * @return video bitrate
     */
    public int getVideoBitrate();

    /**
     * <i>Warning:</i> Optional information, may not be supported by implementation.
     * @return the audio bitrate
     */
    public int getAudioBitrate();

    /**
     * <i>Warning:</i> Optional information, may not be supported by implementation.
     * @return the framerate of the video
     */
    public float getFramerate();

    /**
     * Returns <code>true</code> if the video frame is oriented in
     * OpenGL's coordinate system, <i>origin at bottom left</i>.
     * <p>
     * Otherwise returns <code>false</code>, i.e.
     * video frame is oriented <i>origin at top left</i>.
     * </p>
     * <p>
     * <code>false</code> is the default assumption for videos,
     * but user shall not rely on.
     * </p>
     * <p>
     * <code>false</code> GL orientation leads to
     * {@link Texture#getMustFlipVertically()} == <code>true</code>,
     * as reflected by all {@link TextureFrame}'s {@link Texture}s
     * retrieved via {@link #getLastTexture()} or {@link #getNextTexture(GL)}.
     * </p>
     */
    public boolean isGLOriented();

    /** Returns the width of the video. */
    public int getWidth();

    /** Returns the height of the video. */
    public int getHeight();

    /**
     * Returns title meta-data from stream, available after {@link State#Initialized} is reached after issuing {@link #playStream(Uri, int, int, int, int)}.
     * <p>
     * In case no title meta-data is being used, the {@link #getUri()} basename w/o suffix is being returned.
     * </p>
     */
    public String getTitle();

    /** Returns {@link Chapter} meta-data from stream, available after {@link State#Initialized} is reached after issuing {@link #playStream(Uri, int, int, int, int)}. */
    public Chapter[] getChapters();
    /**
     * Returns {@link Chapter} covering given time position in milliseconds or null if none covers given time
     * @param msec desired chapter covering time position in milliseconds
     */
    public Chapter getChapter(int msec);

    /** Returns a string representation of this player, incl. state and audio/video details. */
    @Override
    public String toString();

    /** Returns a string representation of this player's performance values. */
    public String getPerfString();

    /** Adds a {@link GLMediaEventListener} to this player. */
    public void addEventListener(GLMediaEventListener l);

    /** Removes a {@link GLMediaEventListener} to this player. */
    public void removeEventListener(GLMediaEventListener l);

    /** Return all {@link GLMediaEventListener} of this player. */
    public GLMediaEventListener[] getEventListeners();

    /** Adds a {@link GLMediaFrameListener} to this player. */
    public void addFrameListener(GLMediaFrameListener l);

    /** Removes a {@link GLMediaFrameListener} to this player. */
    public void removeFrameListener(GLMediaFrameListener l);

    /** Return all {@link GLMediaFrameListener} of this player. */
    public GLMediaFrameListener[] getFrameListeners();

    /** Sets the {@link SubtitleEventListener} for this player. */
    public void setSubtitleEventListener(SubtitleEventListener l);
    /** Returns the {@link #setSubtitleEventListener(SubtitleEventListener)} of this player. */
    public SubtitleEventListener getSubtitleEventListener();

    /**
     * Returns the attached user object for the given name.
     */
    public Object getAttachedObject(String name);

    /**
     * Attaches the user object for the given name.
     * Returns the previously set object, may be null.
     */
    public Object attachObject(String name, Object obj);

    /**
     * Detaches the user object for the given name.
     * Returns the previously set object, may be null.
     */
    public Object detachObject(String name);

}
