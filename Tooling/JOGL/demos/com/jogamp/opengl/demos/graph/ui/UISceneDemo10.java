/**
 * Copyright 2010-2023 JogAmp Community. All rights reserved.
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
package com.jogamp.opengl.demos.graph.ui;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.jogamp.common.net.Uri;
import com.jogamp.common.util.InterruptSource;
import com.jogamp.graph.curve.Region;
import com.jogamp.graph.font.Font;
import com.jogamp.graph.font.FontFactory;
import com.jogamp.graph.font.FontSet;
import com.jogamp.graph.ui.Scene;
import com.jogamp.graph.ui.Shape;
import com.jogamp.graph.ui.shapes.Button;
import com.jogamp.graph.ui.shapes.CrossHair;
import com.jogamp.graph.ui.shapes.GLButton;
import com.jogamp.graph.ui.shapes.MediaButton;
import com.jogamp.math.Recti;
import com.jogamp.math.Vec3f;
import com.jogamp.math.geom.AABBox;
import com.jogamp.math.util.PMVMatrix4f;
import com.jogamp.newt.Window;
import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.demos.es2.GearsES2;
import com.jogamp.opengl.demos.util.CommandlineOptions;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.av.GLMediaPlayer;
import com.jogamp.opengl.util.av.GLMediaPlayerFactory;

/**
 * Res independent Shape, in Scene attached to GLWindow w/ listener attached.
 * <p>
 * User can test Shape drag-move and drag-resize w/ 1-pointer
 * </p>
 */
public class UISceneDemo10 {
    static final boolean DEBUG = false;
    static final boolean TRACE = false;

    static CommandlineOptions options = new CommandlineOptions(1280, 720, Region.VBAA_RENDERING_BIT);

    static private final String defaultMediaPath = "http://archive.org/download/BigBuckBunny_328/BigBuckBunny_512kb.mp4";
    static private String filmPath = defaultMediaPath;

    public static void main(final String[] args) throws IOException {
        Font font = null;

        if( 0 != args.length ) {
            final int[] idx = { 0 };
            for (idx[0] = 0; idx[0] < args.length; ++idx[0]) {
                if( options.parse(args, idx) ) {
                    continue;
                } else if(args[idx[0]].equals("-font")) {
                    ++idx[0];
                    font = FontFactory.get(new File(args[idx[0]]));
                } else if(args[idx[0]].equals("-film")) {
                    ++idx[0];
                    filmPath = args[idx[0]];
                }
            }
        }
        System.err.println(options);

        final GLCapabilities reqCaps = options.getGLCaps();
        System.out.println("Requested: " + reqCaps);

        final GLWindow window = GLWindow.create(reqCaps);

        //
        // Resolution independent, no screen size
        //
        if( null == font ) {
            font = FontFactory.get(FontFactory.UBUNTU).get(FontSet.FAMILY_LIGHT, FontSet.STYLE_SERIF);
        }
        System.err.println("Font: "+font.getFullFamilyName());
        final Shape shape = makeShape(window, font, options.renderModes);
        System.err.println("m0 shape bounds "+shape.getBounds(reqCaps.getGLProfile()));
        System.err.println("m0 "+shape);

        // Scene for Shape ...
        final Scene scene = new Scene(options.graphAASamples);
        scene.setPMVMatrixSetup(new MyPMVMatrixSetup());
        scene.setClearParams(new float[] { 1f, 1f, 1f, 1f}, GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        shape.onMove((final Shape s, final Vec3f origin, final Vec3f dest, MouseEvent e) -> {
                final Vec3f p = shape.getPosition();
                System.err.println("Shape moved: "+origin+" -> "+p);
            } );
        shape.addMouseListener(new Shape.MouseGestureAdapter() {
            @Override
            public void mouseMoved(final MouseEvent e) {
                final Recti viewport = scene.getViewport(new Recti());
                // flip to GL window coordinates, origin bottom-left
                final int glWinX = e.getX();
                final int glWinY = viewport.height() - e.getY() - 1;
                testProject(scene, shape, glWinX, glWinY);
            }
            @Override
            public void mouseDragged(final MouseEvent e) {
                final Recti viewport = scene.getViewport(new Recti());
                // flip to GL window coordinates, origin bottom-left
                final int glWinX = e.getX();
                final int glWinY = viewport.height() - e.getY() - 1;
                testProject(scene, shape, glWinX, glWinY);
            }
        } );
        scene.addShape(shape);

        window.setSize(options.surface_width, options.surface_height);
        window.setTitle(UISceneDemo10.class.getSimpleName()+": "+window.getSurfaceWidth()+" x "+window.getSurfaceHeight());
        window.setVisible(true);
        window.addGLEventListener(scene);
        scene.attachInputListenerTo(window);

        final Animator animator = new Animator(0 /* w/o AWT */);
        animator.setUpdateFPSFrames(5*60, null);
        animator.add(window);

        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent arg0) {
                final short keySym = arg0.getKeySymbol();
                if( keySym == KeyEvent.VK_F4 || keySym == KeyEvent.VK_ESCAPE || keySym == KeyEvent.VK_Q ) {
                    new InterruptSource.Thread( () -> { window.destroy(); } ).start();
                }
            }
        });
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowResized(final WindowEvent e) {
                window.setTitle(UISceneDemo10.class.getSimpleName()+": "+window.getSurfaceWidth()+" x "+window.getSurfaceHeight());
            }
            @Override
            public void windowDestroyed(final WindowEvent e) {
                animator.stop();
            }
        });

        animator.start();

        //
        // After initial display we can use screen resolution post initial Scene.reshape(..)
        // However, in this example we merely use the resolution to
        // - Scale the shape to the sceneBox, i.e. normalizing to screen-size 1x1
        scene.waitUntilDisplayed();

        System.err.println("m1.1 Scene "+scene.getBounds());
        System.err.println("m1.1 "+shape);
        try { Thread.sleep(1000); } catch (final InterruptedException e1) { }

        System.err.println("You may test moving the Shape by dragging the shape with 1-pointer.");
        System.err.println("You may test resizing the Shape by dragging the shape on 1/5th of the bottom-left or bottom-right corner with 1-pointer.");
        System.err.println("Press F4 or 'window close' to exit ..");
    }

    static void testProject(final Scene scene, final Shape shape, final int glWinX, final int glWinY) {
        final PMVMatrix4f pmv = new PMVMatrix4f();
        final Vec3f objPos = shape.winToShapeCoord(scene.getPMVMatrixSetup(), scene.getViewport(), glWinX, glWinY, pmv, new Vec3f());
        System.err.printf("MM1: winToObjCoord: obj %s%n", objPos);
        final int[] glWinPos = shape.shapeToWinCoord(scene.getPMVMatrixSetup(), scene.getViewport(), objPos, pmv, new int[2]);
        final int windx = glWinPos[0]-glWinX;
        final int windy = glWinPos[1]-glWinY;
        System.err.printf("MM2: objToWinCoord: winCoords %d / %d, diff %d x %d%n", glWinPos[0], glWinPos[1], windx, windy);
    }

    @SuppressWarnings("unused")
    static Shape makeShape(final Window window, final Font font, final int renderModes) {
        final float sw = 0.25f;
        final float sh = sw / 2.5f;

        if( false ) {
            Uri filmUri;
            try {
                filmUri = Uri.cast( filmPath );
            } catch (final URISyntaxException e1) {
                throw new RuntimeException(e1);
            }
            final GLMediaPlayer mPlayer = GLMediaPlayerFactory.createDefault();
            // mPlayer.setTextureUnit(texUnitMediaPlayer);
            final MediaButton b = new MediaButton(renderModes, sw, sh, mPlayer);
            b.setVerbose(false);
            b.addDefaultEventListener();
            b.setToggleable(true);
            b.setToggle(true);
            b.setToggleOffColorMod(0f, 1f, 0f, 1.0f);
            b.addMouseListener(new Shape.MouseGestureAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    mPlayer.setAudioVolume( b.isToggleOn() ? 1f : 0f );
                } } );
            mPlayer.playStream(filmUri, GLMediaPlayer.STREAM_ID_AUTO, GLMediaPlayer.STREAM_ID_AUTO, GLMediaPlayer.STREAM_ID_NONE, GLMediaPlayer.TEXTURE_COUNT_DEFAULT);
            return b;
        } else if( true ) {
            final GLEventListener glel;
            {
                final GearsES2 gears = new GearsES2(0);
                gears.setVerbose(false);
                gears.setClearColor(new float[] { 0.9f, 0.9f, 0.9f, 1f } );
                window.addKeyListener(gears.getKeyListener());
                glel = gears;
            }
            final int texUnit = 1;
            final GLButton b = new GLButton(renderModes, sw,
                                            sh, texUnit, glel, false /* useAlpha */);
            b.setToggleable(true);
            b.setToggle(true); // toggle == true -> animation
            b.setAnimate(true);
            b.addMouseListener(new Shape.MouseGestureAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    b.setAnimate( b.isToggleOn() );
                } } );
            return b;
        } else if( true ){
            return new Button(renderModes, font, "+", sw, sh).setPerp();
        } else {
            final CrossHair b = new CrossHair(renderModes, sw, sw, 1f/100f);
            return b;
        }
    }
    static class MyPMVMatrixSetup extends Scene.DefaultPMVMatrixSetup {
        @Override
        public void set(final PMVMatrix4f pmv, final Recti viewport) {
            super.set(pmv, viewport);

            // Scale (back) to have normalized plane dimensions, 1 for the greater of width and height.
            final AABBox planeBox0 = new AABBox();
            setPlaneBox(planeBox0, pmv, viewport);
            final float sx = planeBox0.getWidth();
            final float sy = planeBox0.getHeight();
            final float sxy = sx > sy ? sx : sy;
            pmv.scaleP(sxy, sxy, 1f);
        }
    };

}
