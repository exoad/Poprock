/**
 * Copyright 2014 JogAmp Community. All rights reserved.
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

package jogamp.graph.curve.tess;

import com.jogamp.graph.geom.Triangle;
import com.jogamp.graph.geom.Vertex;
import com.jogamp.math.Vec2f;
import com.jogamp.math.Vec3f;
import com.jogamp.math.VectorUtil;

/**
 * Experimental Add-On ..
 *
 *  Disabled by default
 */
public class CDTriangulator2DExpAddOn {

    private final Vec3f tempV3a = new Vec3f();
    private final Vec3f tempV3b = new Vec3f();

    protected final void markLineInTriangle(final Triangle tri1) {
        if( !tri1.isOnCurve() || !tri1.isLine() ) {
            return;
        }

        final boolean[] boundVs = tri1.getVerticesBoundary();
        final Vertex[] triVs = tri1.getVertices();
        final Vertex v0 = triVs[0];
        final Vertex v1 = triVs[1];
        final Vertex v2 = triVs[2];

        int lineSegCount = 0;
        final boolean v0IsLS, v1IsLS, v2IsLS;
        if( v0.isOnCurve() && VectorUtil.isVec2Zero(v0.getTexCoord()) && !boundVs[0] ) {
            v0IsLS = true;
            lineSegCount++;
        } else {
            v0IsLS = false;
        }
        if( v1.isOnCurve() && VectorUtil.isVec2Zero(v1.getTexCoord()) && !boundVs[1] ) {
            v1IsLS = true;
            lineSegCount++;
        } else {
            v1IsLS = false;
        }
        if( v2.isOnCurve() && VectorUtil.isVec2Zero(v2.getTexCoord()) && !boundVs[2] ) {
            v2IsLS = true;
            lineSegCount++;
        } else {
            v2IsLS = false;
        }
        if( 2 > lineSegCount ) {
            return;
        } else {
            if(CDTriangulator2D.DEBUG) {
                System.err.println("CDTri.markLine.1: "+tri1);
                System.err.println("CDTri.markLine.1: count "+lineSegCount+", v0IsLS "+v0IsLS+", v1IsLS "+v1IsLS+", v2IsLS "+v2IsLS);
            }
            final float texZTag = 2f;
            if( true ) {
                if( v0IsLS ) {
                    v0.setTexCoord(0f, 0f, texZTag);
                }
                if( v1IsLS ) {
                    v1.setTexCoord(0f, 0f, texZTag);
                }
                if( v2IsLS ) {
                    v2.setTexCoord(0f, 0f, texZTag);
                }
            } else {
                if( v0IsLS ) {
                    final Vertex v = v0.copy();
                    v.setTexCoord(0f, 0f, texZTag);
                    triVs[0] = v;
                }
                if( v1IsLS ) {
                    final Vertex v = v1.copy();
                    v.setTexCoord(0f, 0f, texZTag);
                    triVs[1] = v;
                }
                if( v2IsLS ) {
                    final Vertex v = v2.copy();
                    v.setTexCoord(0f, 0f, texZTag);
                    triVs[2] = v;
                }
            }
            if ( false ) {
                final Vertex vL1, vL2, vL3, vO;
                if( 3 == lineSegCount ) {
                    vL1 = v0; vL2=v1; vL3=v2; vO=null;
                } else if( v0IsLS && v1IsLS ) {
                    vL1 = v0; vL2=v1; vL3=null; vO=v2;
                } else if( v0IsLS && v2IsLS ) {
                    vL1 = v0; vL2=v2; vL3=null; vO=v1;
                } else if( v1IsLS && v2IsLS ) {
                    vL1 = v1; vL2=v2; vL3=null; vO=v0;
                } else {
                    return; // unreachable
                }
                if( null != vL1 ) {
                    vL1.setTexCoord(texZTag, 0f, 0f);
                }
                if( null != vL2 ) {
                    vL2.setTexCoord(texZTag, 0f, 0f);
                }
                if( null != vL3 ) {
                    vL3.setTexCoord(texZTag, 0f, 0f);
                }
            }
        }
    }

    /**
     * If this and the other triangle compose a rectangle return the
     * given <code>tempV2</code> array w/ shortest side first.
     * Otherwise return null;
     * <p>
     * Experimental CODE, enabled only if {@link #TEST_LINE_AA} is set .. WIP
     * </p>
     * <p>
       One test uses method: ROESSLER-2012-OGLES <http://www.cg.tuwien.ac.at/research/publications/2012/ROESSLER-2012-OGLES/>
     * </p>
     * <p>
     * However, we would need to tesselate all lines appropriately,
     * i.e. create 2 triangles sharing the middle actual line using thickness+radius.
     *
     * This test simply used our default font w/ a line thickness of 2 pixels,
     * which produced mentioned rectangles.
     * This is of course not the case for arbitrary Outline shapes.
     * </p>
     * @param tri2
     * @param checkThisOnCurve
     * @param temp temp storage
     */
    protected final Vec2f processLineAA(final int i, final Triangle tri1, final Triangle tri2, final Vec2f temp) {
        if(CDTriangulator2D.DEBUG){
            System.err.println("CDTri.genP2["+i+"].1: ? t1 "+tri1);
            System.err.println("CDTri.genP2["+i+"].1: ? t2 "+tri2);
        }
        final Vec2f rect = processLineAAImpl(tri1, tri2, temp);
        if(CDTriangulator2D.DEBUG){
            if( null != rect ) {
                System.err.println("CDTri.genP2["+i+"].1: RECT ["+rect.x()+", "+rect.y()+"]");
                System.err.println("CDTri.genP2["+i+"].1: RECT t1 "+tri1);
                System.err.println("CDTri.genP2["+i+"].1: RECT t2 "+tri2);
            } else {
                System.err.println("CDTri.genP2["+i+"].1: RECT NOPE, t1 "+tri1);
                System.err.println("CDTri.genP2["+i+"].1: RECT NOPE, t2 "+tri2);
            }
        }
        return rect;
    }
    private final Vec2f processLineAAImpl(final Triangle tri1, final Triangle tri2, final Vec2f temp) {
        if( !tri1.isOnCurve() || !tri2.isOnCurve() || !tri1.isLine() || !tri2.isLine() ) {
            return null;
        }
        final Vec2f rect;
        int eqCount = 0;
        final int[] commonIdxA = { -1, -1 };
        final int[] commonIdxB = { -1, -1 };
        final Vertex[] verts1 = tri1.getVertices();
        final Vertex[] verts2 = tri2.getVertices();
        Vec3f coord = verts1[0].getCoord();
        if( coord.isEqual( verts2[0].getCoord() ) ) {
            commonIdxA[eqCount] = 0;
            commonIdxB[eqCount] = 0;
            eqCount++;
        } else if( coord.isEqual( verts2[1].getCoord() ) ) {
            commonIdxA[eqCount] = 0;
            commonIdxB[eqCount] = 1;
            eqCount++;
        } else if( coord.isEqual( verts2[2].getCoord() ) ) {
            commonIdxA[eqCount] = 0;
            commonIdxB[eqCount] = 2;
            eqCount++;
        }
        coord = verts1[1].getCoord();
        if( coord.isEqual( verts2[0].getCoord() ) ) {
            commonIdxA[eqCount] = 1;
            commonIdxB[eqCount] = 0;
            eqCount++;
        } else if( coord.isEqual( verts2[1].getCoord() ) ) {
            commonIdxA[eqCount] = 1;
            commonIdxB[eqCount] = 1;
            eqCount++;
        } else if( coord.isEqual( verts2[2].getCoord() ) ) {
            commonIdxA[eqCount] = 1;
            commonIdxB[eqCount] = 2;
            eqCount++;
        }
        final int otherIdxA;
        if( 2 == eqCount ) {
            otherIdxA = 3 - ( commonIdxA[0] + commonIdxA[1] );
        } else {
            coord = verts1[2].getCoord();
            if( coord.isEqual( verts2[0].getCoord() ) ) {
                commonIdxA[eqCount] = 2;
                commonIdxB[eqCount] = 0;
                eqCount++;
            } else if( coord.isEqual( verts2[1].getCoord() ) ) {
                commonIdxA[eqCount] = 2;
                commonIdxB[eqCount] = 1;
                eqCount++;
            } else if( coord.isEqual( verts2[2].getCoord() ) ) {
                commonIdxA[eqCount] = 2;
                commonIdxB[eqCount] = 2;
                eqCount++;
            }
            if( 2 == eqCount ) {
                otherIdxA = 3 - ( commonIdxA[0] + commonIdxA[1] );
            } else {
                otherIdxA = -1;
            }
        }
        if( 0 <= otherIdxA && commonIdxB[0] != commonIdxB[1] ) {
            final int otherIdxB = 3 - ( commonIdxB[0] + commonIdxB[1] );
            // Reference must be equal, i.e. sharing the actual same vertices!
            if( verts1[commonIdxA[0]] != verts2[commonIdxB[0]] || verts1[commonIdxA[1]] != verts2[commonIdxB[1]] ) {
                throw new InternalError("XXX: diff shared verts"); // FIXME remove when clear
            }
            final Vertex vC0A, vC1A, vOA, vOB;
            if( false ) {
                // Fetch only!
                vC0A = verts1[commonIdxA[0]];
                vC1A = verts1[commonIdxA[1]];
                vOA = verts1[otherIdxA];
                vOB = verts2[otherIdxB];
            } else {
                // Fetch and copy, write-back to triangles
                vC0A = verts1[commonIdxA[0]].copy();
                verts1[commonIdxA[0]] = vC0A;
                verts2[commonIdxB[0]] = vC0A;
                vC1A = verts1[commonIdxA[1]].copy();
                verts1[commonIdxA[1]] = vC1A;
                verts2[commonIdxB[1]] = vC1A;
                vOA = verts1[otherIdxA].copy();
                verts1[otherIdxA] = vOA;
                vOB = verts2[otherIdxB].copy();
                verts2[otherIdxB] = vOB;
            }

            final float texZTag = 2f;
            final Vec3f vOACoords = vOA.getCoord();
            final float dOC0A = vOACoords.dist( vC0A.getCoord() );
            final float dOC1A = vOACoords.dist( vC1A.getCoord() );
            if( false ) {
                final Vec3f vec3Z = new Vec3f(0f, 0f, -1f);
                final Vec3f vecLongSide, vecLineHeight;
                if( dOC0A < dOC1A ) {
                    temp.set( dOC0A, // line width
                              dOC1A); // long side
                    vecLongSide = tempV3a.minus(vOACoords, vC1A.getCoord()).normalize(); // normal long side vector
                    vecLineHeight = tempV3b.cross(vec3Z, tempV3a); // the line-height vector (normal)
                    vOA.setTexCoord(-1f, -1f, texZTag);
                    vC1A.setTexCoord(1f, -1f, texZTag);
                    vOB.setTexCoord(0f,   1f, texZTag);
                    vC0A.setTexCoord(0f,  1f, texZTag);
                } else {
                    temp.set( dOC1A, // line width
                              dOC0A); // long side
                    vecLongSide = tempV3a.minus(vOACoords, vC0A.getCoord()).normalize(); // normal long side vector
                    vecLineHeight = tempV3b.cross(vec3Z, tempV3a); // the line-height vector (normal)
                }
                if(CDTriangulator2D.DEBUG){
                    System.err.println("RECT.0 : long-side-vec   "+vecLongSide);
                    System.err.println("RECT.0 : line-height-vec "+vecLineHeight);
                }

            } else {
                /**
                 * Using method: ROESSLER-2012-OGLES <http://www.cg.tuwien.ac.at/research/publications/2012/ROESSLER-2012-OGLES/>
                 *
                 * Arbitrary but consistently pick left/right and set texCoords, FIXME: validate
                 *
                 * Testing w/ fixed line-width 1, and radius 1/3.
                 */
                final float lineWidth;
                final Vertex vL1, vL2, vR1, vR2;
                if( dOC0A < dOC1A ) {
                    lineWidth = dOC0A; // line width
                    temp.set( dOC0A, // line width
                              dOC1A); // long side
                    // Left:  vOA, vC1A
                    // Right: vOB, vC0A
                    vL1 = vOA; vL2 = vC1A;
                    vR1 = vOB; vR2 = vC0A;
                } else {
                    lineWidth = dOC1A; // line width
                    temp.set( dOC1A, // line width
                              dOC0A); // long side
                    // Left:  vOB, vC1A
                    // Right: vOA, vC0A
                    vL1 = vOB; vL2 = vC1A;
                    vR1 = vOA; vR2 = vC0A;
                }
                final float r = lineWidth/3f;
                final float wa = lineWidth + r;
                final float waHalf = wa / 2f;
                vL1.setTexCoord(lineWidth,  waHalf, texZTag);
                vL2.setTexCoord(lineWidth,  waHalf, texZTag);
                vR1.setTexCoord(lineWidth, -waHalf, texZTag);
                vR2.setTexCoord(lineWidth, -waHalf, texZTag);
                if(CDTriangulator2D.DEBUG){
                    System.err.println("RECT.0 : lineWidth: "+lineWidth+", dim "+dOC0A+" x "+dOC1A+", radius "+r);
                    System.err.println("RECT Left.0: "+vL1+", "+vL2);
                    System.err.println("RECT Right.0: "+vR1+", "+vR2);
                }
            }
            rect = temp;
        } else {
            rect = null;
        }
        return rect;
    }
}
