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
package com.jogamp.opengl.demos.graph.ui;

import java.io.IOException;

import com.jogamp.common.util.InterruptSource;
import com.jogamp.graph.curve.Region;
import com.jogamp.graph.font.Font;
import com.jogamp.graph.font.FontFactory;
import com.jogamp.graph.font.FontSet;
import com.jogamp.graph.ui.GraphShape;
import com.jogamp.graph.ui.Scene;
import com.jogamp.graph.ui.shapes.Button;
import com.jogamp.math.Recti;
import com.jogamp.math.geom.AABBox;
import com.jogamp.math.util.PMVMatrix4f;
import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.demos.util.CommandlineOptions;
import com.jogamp.opengl.util.Animator;

/**
 * Res independent Shape, Scene attached to GLWindow showing simple shape.
 * <p>
 * The shape is created using the normalized scene's default bounding box, normalized to 1 for the greater of width and height.
 * </p>
 */
public class UIShapeDemo00 {
    static CommandlineOptions options = new CommandlineOptions(1280, 720, Region.VBAA_RENDERING_BIT);

    public static void main(final String[] args) throws IOException {
        options.parse(args);
        System.err.println(options);
        final GLProfile reqGLP = GLProfile.get(options.glProfileName);
        System.err.println("GLProfile: "+reqGLP);

        // Resolution independent, no screen size
        //
        final Font font = FontFactory.get(FontFactory.UBUNTU).get(FontSet.FAMILY_LIGHT, FontSet.STYLE_SERIF);
        System.err.println("Font: "+font.getFullFamilyName());

        final GraphShape shape = new Button(options.renderModes, font, "Hello JogAmp", 0.30f, 0.30f/2.5f); // normalized: 1 is 100% surface size (width and/or height)
        System.err.println("Shape bounds "+shape.getBounds(reqGLP));

        final Scene scene = new Scene(options.graphAASamples);
        scene.setPMVMatrixSetup(new MyPMVMatrixSetup());
        scene.setClearParams(new float[] { 1f, 1f, 1f, 1f}, GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        scene.addShape(shape);
        scene.setAAQuality(options.graphAAQuality);

        final Animator animator = new Animator(0 /* w/o AWT */);

        final GLCapabilities caps = new GLCapabilities(reqGLP);
        caps.setAlphaBits(4);
        System.out.println("Requested: " + caps);

        final GLWindow window = GLWindow.create(caps);
        window.setSize(options.surface_width, options.surface_height);
        window.setTitle(UIShapeDemo00.class.getSimpleName()+": "+window.getSurfaceWidth()+" x "+window.getSurfaceHeight());
        window.setVisible(true);
        window.addGLEventListener(scene);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowResized(final WindowEvent e) {
                window.setTitle(UIShapeDemo00.class.getSimpleName()+": "+window.getSurfaceWidth()+" x "+window.getSurfaceHeight());
            }
            @Override
            public void windowDestroyNotify(final WindowEvent e) {
                animator.stop();
            }
        });

        scene.attachInputListenerTo(window);
        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent arg0) {
                final short keySym = arg0.getKeySymbol();
                if( keySym == KeyEvent.VK_RIGHT ) {
                    scene.setAAQuality( scene.getAAQuality() + 1 );
                    System.err.println("AA Quality "+scene.getAAQuality());
                } else if( keySym == KeyEvent.VK_LEFT ) {
                    scene.setAAQuality( scene.getAAQuality() - 1 );
                    System.err.println("AA Quality "+scene.getAAQuality());
                } else if( keySym == KeyEvent.VK_UP ) {
                    scene.setSampleCount(scene.getSampleCount() + 1); // validated / clipped
                    System.err.println("AA Samples "+scene.getSampleCount());
                } else if( keySym == KeyEvent.VK_DOWN ) {
                    scene.setSampleCount(scene.getSampleCount() - 1); // validated / clipped
                    System.err.println("AA Samples "+scene.getSampleCount());
                } else if( keySym == KeyEvent.VK_F4 || keySym == KeyEvent.VK_ESCAPE || keySym == KeyEvent.VK_Q ) {
                    new InterruptSource.Thread( () -> { window.destroy(); } ).start();
                }
            }
        });
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyed(final WindowEvent e) {
                animator.stop();
            }
        });

        animator.add(window);
        animator.start();
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
