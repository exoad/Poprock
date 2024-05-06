/**
 * Copyright 2023 JogAmp Community. All rights reserved.
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
package com.jogamp.graph.ui.layout;

import java.util.List;

import com.jogamp.graph.ui.Group;
import com.jogamp.graph.ui.Shape;
import com.jogamp.math.FloatUtil;
import com.jogamp.math.Vec2f;
import com.jogamp.math.Vec3f;
import com.jogamp.math.geom.AABBox;
import com.jogamp.math.util.PMVMatrix4f;

/**
 * GraphUI Grid {@link Group.Layout}.
 * <p>
 * A grid of {@link Shape}s
 * <ul>
 *   <li>Optionally centered {@link Alignment.Bit#CenterHoriz horizontally}, {@link Alignment.Bit#CenterVert vertically} or {@link Alignment#Center both}.</li>
 *   <li>Optionally scaled to cell-size if given and {@link Alignment#Fill}</li>
 *   <li>Unscaled {@link Padding} is applied to each {@Shape} via {@link Shape#setPaddding(Padding)} if passed in constructor and is scaled if {@link Alignment.Bit#Fill}</li>
 *   <li>Without cell-size behaves like a grid bag using individual shape sizes including {@link Padding}</li>
 *   <li>Scaled {@link Gap} is applied unscaled if used.</li>
 *   <li>Can be filled in {@link Order#COLUMN} or {@link Order#ROW} major-order.</li>
 *   <li>Not implemented {@link Alignment}: {@link Alignment.Bit#Top Top}, {@link Alignment.Bit#Right Right}, {@link Alignment.Bit#Bottom Bottom}, {@link Alignment.Bit#Left Left}</li>
 *   <li>..</li>
 * </ul>
 * </p>
 */
public class GridLayout implements Group.Layout {
    /** Layout order for {@link Group#getShapes()}} after population. */
    public static enum Order {
        /** COLUMN layout order of {@link Group#getShapes()}} is left to right and top to bottom. */
        COLUMN,
        /** ROW layout order of {@link Group#getShapes()}} is top to bottom and left to right. */
        ROW
    }
    private final Order order;
    private final int col_limit;
    private final int row_limit;
    private final Vec2f cellSize;
    private final Alignment alignment;
    /** Scaled {@link Gap} value is applied w/o additional scaling. */
    private final Gap gap;
    /** Unscaled {@link Padding} value. */
    private final Padding padding;
    private int row_count, col_count;

    private static final boolean TRACE_LAYOUT = false;

    /**
     * Default layout order of {@link Group#getShapes()}} is {@link Order#COLUMN}.
     * @param column_limit [1..inf)
     * @param cellWidth
     * @param cellHeight
     * @param alignment TODO
     */
    public GridLayout(final int column_limit, final float cellWidth, final float cellHeight, final Alignment alignment) {
        this(alignment, Math.max(1, column_limit), -1, cellWidth, cellHeight, Gap.None, null);
    }

    /**
     * Default layout order of {@link Group#getShapes()}} is {@link Order#COLUMN}.
     * @param column_limit [1..inf)
     * @param cellWidth
     * @param cellHeight
     * @param alignment TODO
     * @param gap scaled {@link Gap} value is applied w/o additional scaling
     */
    public GridLayout(final int column_limit, final float cellWidth, final float cellHeight, final Alignment alignment, final Gap gap) {
        this(alignment, Math.max(1, column_limit), -1, cellWidth, cellHeight, gap, null);
    }

    /**
     * Default layout order of {@link Group#getShapes()}} is {@link Order#COLUMN}.
     * @param column_limit [1..inf)
     * @param cellWidth
     * @param cellHeight
     * @param alignment TODO
     * @param gap scaled {@link Gap} value is applied w/o additional scaling
     * @param padding unscaled {@link Padding} applied to each {@Shape} via {@link Shape#setPaddding(Padding)} and is scaled if {@link Alignment.Bit#Fill}
     */
    public GridLayout(final int column_limit, final float cellWidth, final float cellHeight, final Alignment alignment, final Gap gap, final Padding padding) {
        this(alignment, Math.max(1, column_limit), -1, cellWidth, cellHeight, gap, padding);
    }

    /**
     * Default layout order of {@link Group#getShapes()}} is {@link Order#ROW}.
     * @param cellWidth
     * @param cellHeight
     * @param alignment TODO
     * @param row_limit [1..inf)
     */
    public GridLayout(final float cellWidth, final float cellHeight, final Alignment alignment, final int row_limit) {
        this(alignment, -1, Math.max(1, row_limit), cellWidth, cellHeight, Gap.None, null);
    }

    /**
     * Default layout order of {@link Group#getShapes()}} is {@link Order#ROW}.
     * @param cellWidth
     * @param cellHeight
     * @param alignment TODO
     * @param gap scaled {@link Gap} value is applied w/o additional scaling
     * @param row_limit [1..inf)
     */
    public GridLayout(final float cellWidth, final float cellHeight, final Alignment alignment, final Gap gap, final int row_limit) {
        this(alignment, -1, Math.max(1, row_limit), cellWidth, cellHeight, gap, null);
    }

    /**
     * Default layout order of {@link Group#getShapes()}} is {@link Order#ROW}.
     * @param cellWidth
     * @param cellHeight
     * @param alignment TODO
     * @param gap scaled {@link Gap} value is applied w/o additional scaling
     * @param padding unscaled {@link Padding} applied to each {@Shape} via {@link Shape#setPaddding(Padding)} and is scaled if {@link Alignment.Bit#Fill}
     * @param row_limit [1..inf)
     */
    public GridLayout(final float cellWidth, final float cellHeight, final Alignment alignment, final Gap gap, final Padding padding, final int row_limit) {
        this(alignment, -1, Math.max(1, row_limit), cellWidth, cellHeight, gap, padding);
    }

    private GridLayout(final Alignment alignment, final int column_limit, final int row_limit, final float cellWidth, final float cellHeight,
                       final Gap gap, final Padding padding) {
        this.order = 0 < column_limit ? Order.COLUMN : Order.ROW;
        this.col_limit = column_limit;
        this.row_limit = row_limit;
        this.cellSize = new Vec2f(Math.max(0f, cellWidth), Math.max(0f, cellHeight));
        this.alignment = alignment;
        this.gap = gap;
        this.padding = padding;
        row_count = 0;
        col_count = 0;
    }

    /** Returns given {@link Order}. */
    public Order getOrder() { return order; }
    /** Returns column count after {@link #layout(Group, AABBox, PMVMatrix4f)}. */
    public int getColumnCount() { return col_count; }
    /** Returns row count after {@link #layout(Group, AABBox, PMVMatrix4f)}. */
    public int getRowCount() { return row_count; }
    /** Returns the preset cell size */
    public Vec2f getCellSize() { return cellSize; }
    /** Returns given {@link Alignment}. */
    public Alignment getAlignment() { return alignment; }
    /** Returns given scaled {@link Gap}. */
    public Gap getGap() { return gap; }
    /** Returns given unscaled {@link Padding}, may be {@code null} if not given via constructor. */
    public Padding getPadding() { return padding; }

    @Override
    public void preValidate(final Shape s) {
        if( null != padding ) {
            s.setPaddding(padding);
        }
    }

    @Override
    public void layout(final Group g, final AABBox box, final PMVMatrix4f pmv) {
        final boolean hasCellWidth = !FloatUtil.isZero(cellSize.x());
        final boolean hasCellHeight = !FloatUtil.isZero(cellSize.y());
        final boolean isCenteredHoriz = hasCellWidth && alignment.isSet(Alignment.Bit.CenterHoriz);
        final boolean isCenteredVert = hasCellHeight && alignment.isSet(Alignment.Bit.CenterVert);
        final boolean isScaled = alignment.isSet(Alignment.Bit.Fill) && ( hasCellWidth || hasCellHeight );
        final List<Shape> shapes = g.getShapes();
        if( Order.COLUMN == order ) {
            row_count = (int) Math.ceil( (double)shapes.size() / (double)col_limit );
            col_count = col_limit;
        } else { // Order.ROW_MAJOR == order
            row_count = row_limit;
            col_count = (int) Math.ceil( (double)shapes.size() / (double)row_limit );
        }
        if( TRACE_LAYOUT ) {
            System.err.println("gl.00: "+order+", "+col_count+" x "+row_count+", a "+alignment+", shapes "+shapes.size()+", "+gap+", "+box);
        }
        int col_i = 0, row_i = 0;
        float x=0, y=0;
        float totalWidth=-Float.MAX_VALUE, totalHeight=-Float.MAX_VALUE;
        final AABBox[] sboxes = new AABBox[shapes.size()];
        final float[] y_pos = new float[col_count * row_count]; // y_bottom = totalHeight - y_pos[..]

        // Pass-1: Determine totalHeight, while collect sbox and y_pos
        for(int i=0; i < shapes.size(); ++i) {
            final Shape s = shapes.get(i);
            // measure size
            pmv.pushMv();
            s.applyMatToMv(pmv);
            final AABBox sbox = s.getBounds().transform(pmv.getMv(), new AABBox());
            pmv.popMv();

            final float shapeWidthU  = sbox.getWidth();
            final float shapeHeightU = sbox.getHeight();
            if( FloatUtil.isZero(shapeHeightU) || FloatUtil.isZero(shapeHeightU) ) {
                continue;
            }
            sboxes[i] = sbox;
            final float sxy;
            if( isScaled ) {
                // scaling to cell size
                final float cellWidth = hasCellWidth ? cellSize.x() : shapeWidthU;
                final float cellHeight = hasCellHeight ? cellSize.y() : shapeHeightU;
                final float sx = cellWidth / shapeWidthU;
                final float sy = cellHeight/ shapeHeightU;
                sxy = sx < sy ? sx : sy;
            } else {
                sxy = 1;
            }
            final float shapeWidthS = sxy * shapeWidthU;
            final float shapeHeightS = sxy * shapeHeightU;
            final float cellWidthS = hasCellWidth ? cellSize.x() : shapeWidthS;
            final float cellHeightS = hasCellHeight ? cellSize.y() : shapeHeightS;

            // bottom y_pos, top to bottom, to be subtracted from totalHeight
            final float y0 = y + cellHeightS;
            final float x1 = x + cellWidthS;
            totalHeight = Math.max(totalHeight, y0);
            totalWidth = Math.max(totalWidth, x1);
            y_pos[col_count * row_i + col_i] = y0;
            if( TRACE_LAYOUT ) {
                System.err.println("gl.00: y("+i+")["+col_i+"]["+row_i+"]: "+y0+", ["+cellWidthS+" x "+cellHeightS+"]");
            }

            // position for next cell
            if( i + 1 < shapes.size() ) {
                if( Order.COLUMN == order ) {
                    if( col_i + 1 == col_count ) {
                        col_i = 0;
                        row_i++;
                        x = 0;
                        y += cellHeightS + gap.height();
                    } else {
                        col_i++;
                        x += cellWidthS  + gap.width();
                    }
                } else { // Order.ROW_MAJOR == order
                    if( row_i + 1 == row_count ) {
                        row_i = 0;
                        col_i++;
                        y = 0;
                        x += cellWidthS  + gap.width();
                    } else {
                        row_i++;
                        y += cellHeightS + gap.height();
                    }
                }
            }
        }
        if( TRACE_LAYOUT ) {
            System.err.println("gl[__].00: Total "+totalWidth+" / "+totalHeight);
        }

        // Pass-2: Layout
        row_i = 0; col_i = 0;
        x = 0; y = 0;
        for(int i=0; i < shapes.size(); ++i) {
            final Shape s = shapes.get(i);
            final AABBox sbox = sboxes[i];
            if( null == sbox ) {
                continue;
            }
            if( TRACE_LAYOUT ) {
                System.err.println("gl("+i+")["+col_i+"]["+row_i+"].0: sbox "+sbox+", s "+s);
            }

            // IF isScaled: Uniform scale w/ lowest axis scale and center position on lower-scale axis
            final float shapeWidthU = sbox.getWidth();
            final float shapeHeightU = sbox.getHeight();
            final float sxy;
            float dxh = 0, dyh = 0;
            if( isScaled ) {
                // scaling to cell size
                final float cellWidth = hasCellWidth ? cellSize.x() : shapeWidthU;
                final float cellHeight = hasCellHeight ? cellSize.y() : shapeHeightU;
                final float sx = cellWidth / shapeWidthU;
                final float sy = cellHeight/ shapeHeightU;
                sxy = sx < sy ? sx : sy;

                if( TRACE_LAYOUT ) {
                    System.err.println("gl("+i+")["+col_i+"]["+row_i+"].s: "+sx+" x "+sy+" -> "+sxy+": +"+dxh+" / "+dyh+", U: s "+shapeWidthU+" x "+shapeHeightU+", sz "+cellWidth+" x "+cellHeight);
                }
            } else {
                sxy = 1;
            }
            final float shapeWidthS = sxy * shapeWidthU;
            final float shapeHeightS = sxy * shapeHeightU;
            final float cellWidthS = hasCellWidth ? cellSize.x() : shapeWidthS;
            final float cellHeightS = hasCellHeight ? cellSize.y() : shapeHeightS;

            y = totalHeight - y_pos[col_count * row_i + col_i];

            if( isCenteredHoriz ) {
                dxh += 0.5f * ( cellWidthS - shapeWidthS ); // horiz-center
            }
            if( isCenteredVert ) {
                dyh += 0.5f * ( cellHeightS - shapeHeightS ); // vert-center
            }

            if( TRACE_LAYOUT ) {
                System.err.println("gl("+i+")["+col_i+"]["+row_i+"].m: "+x+" / "+y+" + "+dxh+" / "+dyh+", S: s "+shapeWidthS+" x "+shapeHeightS+", sz "+cellWidthS+" x "+cellHeightS);
            }
            // Position and scale shape
            {
                // New shape position, relative to previous position
                final float aX = x + dxh;
                final float aY = y + dyh;
                s.moveTo( aX, aY, s.getPosition().z() );

                // Remove the negative or positive delta on centered axis.
                // Only remove negative offset of non-centered axis (i.e. underline)
                final Vec3f diffBL = new Vec3f(s.getBounds().getLow());
                diffBL.setZ(0);
                if( isCenteredHoriz || isCenteredVert ) {
                    if( !isCenteredVert && diffBL.y() > 0 ) {
                        diffBL.setY(0); // only adjust negative if !center-vert
                    } else if( !isCenteredHoriz && diffBL.x() > 0 ) {
                        diffBL.setX(0); // only adjust negative if !center-horiz
                    }
                    diffBL.mul(s.getScale()).scale(-1f);
                } else {
                    diffBL.min(new Vec3f()).mul(s.getScale()).scale(-1f);
                }
                s.move( diffBL.scale(sxy) );
                if( TRACE_LAYOUT ) {
                    System.err.println("gl("+i+")["+col_i+"]["+row_i+"].bl: sbox0 "+s.getBounds()+", diffBL_ "+diffBL);
                }

                // resize bounds
                box.resize(  x,               y,               sbox.getMinZ());
                box.resize(  x + cellWidthS,  y + cellHeightS, sbox.getMaxZ());
            }
            s.scale( sxy, sxy, 1f);

            if( TRACE_LAYOUT ) {
                System.err.println("gl("+i+")["+col_i+"]["+row_i+"].x: "+x+" / "+y+" + "+dxh+" / "+dyh+" -> "+s.getPosition()+", p3 "+shapeWidthS+" x "+shapeHeightS+", sz3 "+cellWidthS+" x "+cellHeightS+", box "+box.getWidth()+" x "+box.getHeight());
                System.err.println("gl("+i+")["+col_i+"]["+row_i+"].x: "+s);
                System.err.println("gl("+i+")["+col_i+"]["+row_i+"].x: "+box);
            }

            if( i + 1 < shapes.size() ) {
                // position for next cell
                if( Order.COLUMN == order ) {
                    if( col_i + 1 == col_count ) {
                        col_i = 0;
                        row_i++;
                        x = 0;
                    } else {
                        col_i++;
                        x += cellWidthS  + gap.width();
                    }
                } else { // Order.ROW_MAJOR == order
                    if( row_i + 1 == row_count ) {
                        row_i = 0;
                        col_i++;
                        y = 0;
                        x += cellWidthS  + gap.width();
                    } else {
                        row_i++;
                    }
                }
            }
        }
        if( Float.isInfinite(box.getWidth()) || Float.isInfinite(box.getHeight()) ) {
            box.resize(0, 0, 0);
        }
        if( TRACE_LAYOUT ) {
            System.err.println("gl.xx: "+box);
        }
    }

    @Override
    public String toString() {
        final String p_s = ( null == padding || padding.zeroSize() ) ? "" : ", "+padding.toString();
        final String g_s = gap.zeroSumSize() ? "" : ", "+gap.toString();
        return "Grid["+col_count+"x"+row_count+", "+order+", cell "+cellSize+", a "+alignment+g_s+p_s+"]";
    }
}

