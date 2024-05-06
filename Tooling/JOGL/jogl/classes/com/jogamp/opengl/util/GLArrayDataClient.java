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

package com.jogamp.opengl.util;

import java.lang.reflect.Constructor;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.fixedfunc.GLPointerFuncUtil;

import jogamp.opengl.util.GLArrayHandler;
import jogamp.opengl.util.GLFixedArrayHandler;
import jogamp.opengl.util.glsl.GLSLArrayHandler;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.glsl.ShaderState;


public class GLArrayDataClient extends GLArrayDataWrapper implements GLArrayDataEditable {
  /** Default growth factor using the golden ratio 1.618 */
  public static final float DEFAULT_GROWTH_FACTOR = 1.618f;

  /**
   * Create a client side buffer object, using a predefined fixed function array index
   * and starting with a new created Buffer object with initialElementCount size
   *
   * On profiles GL2 and ES1 the fixed function pipeline behavior is as expected.
   * On profile ES2 the fixed function emulation will transform these calls to
   * EnableVertexAttribArray and VertexAttribPointer calls,
   * and a predefined vertex attribute variable name will be chosen.
   *
   * The default name mapping will be used,
   * see {@link GLPointerFuncUtil#getPredefinedArrayIndexName(int)}.
   *
   * @param index The GL array index
   * @param comps The array component number
   * @param dataType The array index GL data type
   * @param normalized Whether the data shall be normalized
   * @param initialElementCount
   *
   * @see com.jogamp.opengl.GLContext#getPredefinedArrayIndexName(int)
   */
  public static GLArrayDataClient createFixed(final int index, final int comps, final int dataType, final boolean normalized, final int initialElementCount)
    throws GLException
  {
      return new GLArrayDataClient(null, index, comps, dataType, normalized, 0, null, initialElementCount, DEFAULT_GROWTH_FACTOR, 0 /* mappedElementCount */,
                                   false, GLFixedArrayHandler.class, 0, 0, 0, 0, false);
  }

  /**
   * Create a client side buffer object, using a predefined fixed function array index
   * and starting with a given Buffer object incl it's stride
   *
   * On profiles GL2 and ES1 the fixed function pipeline behavior is as expected.
   * On profile ES2 the fixed function emulation will transform these calls to
   * EnableVertexAttribArray and VertexAttribPointer calls,
   * and a predefined vertex attribute variable name will be chosen.
   *
   * The default name mapping will be used,
   * see {@link GLPointerFuncUtil#getPredefinedArrayIndexName(int)}.
   *
   * @param index The GL array index
   * @param comps The array component number
   * @param dataType The array index GL data type
   * @param normalized Whether the data shall be normalized
   * @param stride
   * @param buffer the user define data
   *
   * @see com.jogamp.opengl.GLContext#getPredefinedArrayIndexName(int)
   */
  public static GLArrayDataClient createFixed(final int index, final int comps, final int dataType, final boolean normalized, final int stride,
                                              final Buffer buffer)
    throws GLException
  {
      return new GLArrayDataClient(null, index, comps, dataType, normalized, stride, buffer, comps*comps, DEFAULT_GROWTH_FACTOR, 0 /* mappedElementCount */,
                                   false, GLFixedArrayHandler.class, 0, 0, 0, 0, false);
  }

  /**
   * Create a client side buffer object, using a custom GLSL array attribute name
   * and starting with a new created Buffer object with initialElementCount size
   * @param name  The custom name for the GL attribute.
   * @param comps The array component number
   * @param dataType The array index GL data type
   * @param normalized Whether the data shall be normalized
   * @param initialElementCount
   */
  public static GLArrayDataClient createGLSL(final String name, final int comps,
                                             final int dataType, final boolean normalized, final int initialElementCount)
    throws GLException
  {
      return new GLArrayDataClient(name, -1, comps, dataType, normalized, 0, null, initialElementCount, DEFAULT_GROWTH_FACTOR, 0 /* mappedElementCount */,
                                   true, GLSLArrayHandler.class, 0, 0, 0, 0, true);
  }

  /**
   * Create a client side buffer object, using a custom GLSL array attribute name
   * and starting with a given Buffer object incl it's stride
   * @param name  The custom name for the GL attribute.
   * @param comps The array component number
   * @param dataType The array index GL data type
   * @param normalized Whether the data shall be normalized
   * @param stride
   * @param buffer the user define data
   */
  public static GLArrayDataClient createGLSL(final String name, final int comps,
                                             final int dataType, final boolean normalized, final int stride, final Buffer buffer)
    throws GLException
  {
      return new GLArrayDataClient(name, -1, comps, dataType, normalized, stride, buffer, comps*comps, DEFAULT_GROWTH_FACTOR, 0 /* mappedElementCount */,
                                   true, GLSLArrayHandler.class, 0, 0, 0, 0, true);
  }

  @Override
  public void associate(final Object obj, final boolean enable) {
      if(obj instanceof ShaderState) {
          if(enable) {
              shaderState = (ShaderState)obj;
          } else {
              shaderState = null;
          }
      }
  }

  //
  // Data read access
  //

  @Override
  public final boolean isVBOWritten() { return bufferWritten; }

  @Override
  public final boolean enabled() { return bufferEnabled; }

  //
  // Data and GL state modification ..
  //

  @Override
  public final void setVBOWritten(final boolean written) {
      bufferWritten = ( 0 == mappedElemCount ) ? written : true;
  }

  @Override
  public void destroy(final GL gl) {
    clear(gl);
    super.destroy(gl);
  }

  @Override
  public void clear(final GL gl) {
    seal(gl, false);
    clear();
  }

  @Override
  public void seal(final GL gl, final boolean seal) {
    seal(seal);
    enableBuffer(gl, seal);
  }

  @Override
  public void enableBuffer(final GL gl, final boolean enable) {
    if( enableBufferAlways || bufferEnabled != enable ) {
        if(enable) {
            checkSeal(true);
            // init/generate VBO name if not done yet
            init_vbo(gl);
        }
        glArrayHandler.enableState(gl, enable, usesGLSL ? shaderState : null);
        bufferEnabled = enable;
    }
  }

  @Override
  public boolean bindBuffer(final GL gl, final boolean bind) {
      if(bind) {
          checkSeal(true);
          // init/generate VBO name if not done yet
          init_vbo(gl);
      }
      return glArrayHandler.bindBuffer(gl, bind);
  }

  @Override
  public void setEnableAlways(final boolean always) {
    enableBufferAlways = always;
  }

  //
  // Data modification ..
  //

  @Override
  public void clear() {
    if( buffer != null ) {
        buffer.clear();
    }
    sealed = false;
    bufferEnabled = false;
    bufferWritten = ( 0 == mappedElemCount ) ? false : true;
  }

  @Override
  public void seal(final boolean seal)
  {
    if( sealed == seal ) return;
    sealed = seal;
    bufferWritten = ( 0 == mappedElemCount ) ? false : true;
    if( seal ) {
        if ( null != buffer ) {
            buffer.flip();
        }
    } else if ( null != buffer ) {
        buffer.position(buffer.limit());
        buffer.limit(buffer.capacity());
    }
  }


  @Override
  public void rewind() {
    if(buffer!=null) {
        buffer.rewind();
    }
  }

  @Override
  public void padding(int doneInByteSize) {
    if ( buffer==null || sealed ) return;
    while(doneInByteSize<strideB) {
        Buffers.putb(buffer, (byte)0);
        doneInByteSize++;
    }
  }

  /**
   * Generic buffer relative put method.
   *
   * This class buffer Class must match the arguments buffer class.
   * The arguments remaining elements must be a multiple of this arrays element stride.
   */
  @Override
  public void put(final Buffer v) {
    if ( sealed ) return;
    /** FIXME: isn't true for interleaved arrays !
    if(0!=(v.remaining() % strideL)) {
        throw new GLException("Buffer length ("+v.remaining()+") is not a multiple of component-stride:\n\t"+this);
    } */
    growIfNeeded(v.remaining());
    Buffers.put(buffer, v);
  }

  @Override
  public void putb(final byte v) {
    if ( sealed ) return;
    growIfNeeded(1);
    Buffers.putb(buffer, v);
  }

  @Override
  public void put3b(final byte v1, final byte v2, final byte v3) {
    if ( sealed ) return;
    growIfNeeded(3);
    Buffers.put3b(buffer, v1, v2, v3);
  }

  @Override
  public void put4b(final byte v1, final byte v2, final byte v3, final byte v4) {
    if ( sealed ) return;
    growIfNeeded(4);
    Buffers.put4b(buffer, v1, v2, v3, v4);
  }

  @Override
  public void putb(final byte[] src, final int offset, final int length) {
    if ( sealed ) return;
    growIfNeeded(length);
    Buffers.putb(buffer, src, offset, length);
  }

  @Override
  public void puts(final short v) {
    if ( sealed ) return;
    growIfNeeded(1);
    Buffers.puts(buffer, v);
  }

  @Override
  public void put3s(final short v1, final short v2, final short v3) {
    if ( sealed ) return;
    growIfNeeded(3);
    Buffers.put3s(buffer, v1, v2, v3);
  }

  @Override
  public void put4s(final short v1, final short v2, final short v3, final short v4) {
    if ( sealed ) return;
    growIfNeeded(4);
    Buffers.put4s(buffer, v1, v2, v3, v4);
  }

  @Override
  public void puts(final short[] src, final int offset, final int length) {
    if ( sealed ) return;
    growIfNeeded(length);
    Buffers.puts(buffer, src, offset, length);
  }

  @Override
  public void puti(final int v) {
    if ( sealed ) return;
    growIfNeeded(1);
    Buffers.puti(buffer, v);
  }

  @Override
  public void put3i(final int v1, final int v2, final int v3) {
    if ( sealed ) return;
    growIfNeeded(3);
    Buffers.put3i(buffer, v1, v2, v3);
  }

  @Override
  public void put4i(final int v1, final int v2, final int v3, final int v4) {
    if ( sealed ) return;
    growIfNeeded(4);
    Buffers.put4i(buffer, v1, v2, v3, v4);
  }

  @Override
  public void puti(final int[] src, final int offset, final int length) {
    if ( sealed ) return;
    growIfNeeded(length);
    Buffers.puti(buffer, src, offset, length);
  }

  @Override
  public void putx(final int v) {
    puti(v);
  }

  @Override
  public void putf(final float v) {
    if ( sealed ) return;
    growIfNeeded(1);
    Buffers.putf(buffer, v);
  }

  @Override
  public void put3f(final float v1, final float v2, final float v3) {
    if ( sealed ) return;
    growIfNeeded(3);
    Buffers.put3f(buffer, v1, v2, v3);
  }

  @Override
  public void put4f(final float v1, final float v2, final float v3, final float v4) {
    if ( sealed ) return;
    growIfNeeded(4);
    Buffers.put4f(buffer, v1, v2, v3, v4);
  }

  @Override
  public void putf(final float[] src, final int offset, final int length) {
    if ( sealed ) return;
    growIfNeeded(length);
    Buffers.putf(buffer, src, offset, length);
  }

  @Override
  public String toString() {
    return "GLArrayDataClient["+name+
                       ", index "+index+
                       ", location "+location+
                       ", isVertexAttribute "+isVertexAttr+
                       ", usesGLSL "+usesGLSL+
                       ", usesShaderState "+(null!=shaderState)+
                       ", dataType 0x"+Integer.toHexString(compType)+
                       ", bufferClazz "+compClazz+
                       ", compsPerElem "+compsPerElement+
                       ", stride "+strideB+"b "+strideL+"c"+
                       ", initElemCount "+initElemCount+
                       ", mappedElemCount "+mappedElemCount+
                       ", "+elemStatsToString()+
                       ", bufferEnabled "+bufferEnabled+
                       ", bufferWritten "+bufferWritten+
                       ", buffer "+buffer+
                       ", alive "+alive+
                       "]";
  }

  /**
   * Returning element-count from given componentCount, rounding up to componentsPerElement.
   */
  public int compsToElemCount(final int componentCount) {
      return ( componentCount + compsPerElement - 1 ) / compsPerElement;
  }

  /**
   * Increase the capacity of the buffer if necessary to add given spareComponents components.
   * <p>
   * Buffer will not change if remaining free slots, capacity less position, satisfy spareComponents components.
   * </p>
   * @param spareComponents number of components to add if necessary.
   * @return true if buffer size has changed, i.e. grown. Otherwise false.
   */
  public final boolean growIfNeeded(final int spareComponents) {
    if( null == buffer || buffer.remaining() < spareComponents ) {
        if( 0 != mappedElemCount ) {
            throw new GLException("Mapped buffer can't grow. Insufficient storage size: Needed "+spareComponents+" components, "+
                                  "mappedElementCount "+mappedElemCount+
                                  ", has mapped buffer "+buffer+"; "+this);
        }
        if( null == buffer ) {
            final int required_elems = compsToElemCount(spareComponents);
            return reserve( Math.max( initElemCount, required_elems ) );
        } else {
            final int has_comps = buffer.capacity();
            final int required_elems = compsToElemCount(has_comps + spareComponents);
            final int new_elems = compsToElemCount( (int)( has_comps * growthFactor + 0.5f ) );
            final int elementCount = Math.max( new_elems, required_elems );
            return reserve( elementCount );
        }
    }
    return false;
  }

  /**
   * Increase the capacity of the buffer to given elementCount element size,
   * i.e. elementCount * componentsPerElement components.
   * <p>
   * Buffer will not change if given elementCount is lower or equal current capacity.
   * </p>
   * @param elementCount number of elements to hold.
   * @return true if buffer size has changed, i.e. grown. Otherwise false.
   */
  public final boolean reserve(int elementCount) {
    if(!alive || sealed) {
       throw new GLException("Invalid state: "+this);
    }

    // add the stride delta
    elementCount += (elementCount/compsPerElement)*(strideL-compsPerElement);

    final int osize = (buffer!=null) ? buffer.capacity() : 0;
    final int nsize = elementCount * compsPerElement;
    if( nsize <= osize ) {
        return false;
    }
    final Buffer oldBuffer = buffer;

    if(compClazz==ByteBuffer.class) {
        final ByteBuffer newBBuffer = Buffers.newDirectByteBuffer( nsize );
        if(oldBuffer!=null) {
            oldBuffer.flip();
            newBBuffer.put((ByteBuffer)oldBuffer);
        }
        buffer = newBBuffer;
    } else if(compClazz==ShortBuffer.class) {
        final ShortBuffer newSBuffer = Buffers.newDirectShortBuffer( nsize );
        if(oldBuffer!=null) {
            oldBuffer.flip();
            newSBuffer.put((ShortBuffer)oldBuffer);
        }
        buffer = newSBuffer;
    } else if(compClazz==IntBuffer.class) {
        final IntBuffer newIBuffer = Buffers.newDirectIntBuffer( nsize );
        if(oldBuffer!=null) {
            oldBuffer.flip();
            newIBuffer.put((IntBuffer)oldBuffer);
        }
        buffer = newIBuffer;
    } else if(compClazz==FloatBuffer.class) {
        final FloatBuffer newFBuffer = Buffers.newDirectFloatBuffer( nsize );
        if(oldBuffer!=null) {
            oldBuffer.flip();
            newFBuffer.put((FloatBuffer)oldBuffer);
        }
        buffer = newFBuffer;
    } else {
        throw new GLException("Given Buffer Class not supported: "+compClazz+":\n\t"+this);
    }
    if(DEBUG) {
        System.err.println("*** Size: Reserve: comps: "+compsPerElement+", "+(osize/compsPerElement)+"/"+osize+" -> "+(nsize/compsPerElement)+"/"+nsize+
                           "; "+oldBuffer+" -> "+buffer+"; "+this);
    }
    return true;
  }

  // non public matters

  protected final void checkSeal(final boolean test) throws GLException {
    if(!alive) {
        throw new GLException("Invalid state: "+this);
    }
    if(sealed!=test) {
        if(test) {
            throw new GLException("Not Sealed yet, seal first:\n\t"+this);
        } else {
            throw new GLException("Already Sealed, can't modify VBO:\n\t"+this);
        }
    }
  }

  protected GLArrayDataClient(final String name, final int index, final int comps, final int dataType, final boolean normalized, final int stride, final Buffer data,
                              final int initialElementCount, final float growthFactor,
                              final int mappedElementCount, final boolean isVertexAttribute,
                              final Class<? extends GLArrayHandler> handlerClass,
                              final int vboName, final long vboOffset, final int vboUsage, final int vboTarget, final boolean usesGLSL)
    throws GLException
  {
    super(name, index, comps, dataType, normalized, stride, data, mappedElementCount,
          isVertexAttribute, vboName, vboOffset, vboUsage, vboTarget);

    if( 0<mappedElementCount && 0<initialElementCount ) { // null!=buffer case validated in super.init(..)
        throw new IllegalArgumentException("mappedElementCount:="+mappedElementCount+" specified, but passing non zero initialElementSize");
    }

    // immutable types
    this.initElemCount = initialElementCount;
    this.growthFactor = growthFactor;
    try {
        final Constructor<? extends GLArrayHandler> ctor = handlerClass.getConstructor(GLArrayDataEditable.class);
        this.glArrayHandler = ctor.newInstance(this);
    } catch (final Exception e) {
        throw new RuntimeException("Could not ctor "+handlerClass.getName()+"("+this.getClass().getName()+")", e);
    }
    this.usesGLSL = usesGLSL;

    // mutable types
    this.sealed=false;
    this.bufferEnabled=false;
    this.enableBufferAlways=false;
    this.bufferWritten = ( 0 == mappedElementCount ) ? false : true;

    if(null==buffer && initialElementCount>0) {
        reserve(initialElementCount);
    }
  }

  protected void init_vbo(final GL gl) {
      if(!isValidated ) {
          isValidated = true;
          validate(gl.getGLProfile(), true);
      }
  }

  /**
   * Copy Constructor
   * <p>
   * Buffer is {@link Buffers#slice(Buffer) sliced}, i.e. sharing content but using own state.
   * </p>
   * <p>
   * All other values are simply copied.
   * </p>
   */
  public GLArrayDataClient(final GLArrayDataClient src) {
    super(src);

    // immutable types
    this.initElemCount = src.initElemCount;
    if( null != src.glArrayHandler ) {
        final Class<? extends GLArrayHandler> clazz = src.glArrayHandler.getClass();
        try {
            final Constructor<? extends GLArrayHandler> ctor = clazz.getConstructor(GLArrayDataEditable.class);
            this.glArrayHandler = ctor.newInstance(this);
        } catch (final Exception e) {
            throw new RuntimeException("Could not ctor "+clazz.getName()+"("+this.getClass().getName()+")", e);
        }
    } else {
        this.glArrayHandler = null;
    }
    this.usesGLSL = src.usesGLSL;

    // mutable types
    this.growthFactor = src.growthFactor;
    this.isValidated = src.isValidated;
    this.bufferEnabled = src.bufferEnabled;
    this.bufferWritten = src.bufferWritten;
    this.enableBufferAlways = src.enableBufferAlways;
    this.shaderState = src.shaderState;
  }

  /**
   * Returns this buffer's growth factor.
   * <p>
   * Default is {@link #DEFAULT_GROWTH_FACTOR}, i.e. the golden ratio 1.618.
   * </p>
   * @see #setGrowthFactor(float)
   * @see #DEFAULT_GROWTH_FACTOR
   */
  public float getGrowthFactor() { return growthFactor; }

  /**
   * Sets a new growth factor for this buffer.
   * <p>
   * Default is {@link #DEFAULT_GROWTH_FACTOR}, i.e. the golden ratio 1.618.
   * </p>
   * @param v new growth factor, which will be clipped to a minimum of 1, i.e. 0% minimum growth.
   * @see #getGrowthFactor()
   * @see #DEFAULT_GROWTH_FACTOR
   */
  public void setGrowthFactor(final float v) {
      growthFactor = Math.max(1f, v);
  }

  protected final int initElemCount;
  protected final GLArrayHandler glArrayHandler;
  protected final boolean usesGLSL;

  protected float growthFactor;
  private boolean isValidated = false;
  protected boolean bufferEnabled;
  protected boolean bufferWritten;
  protected boolean enableBufferAlways;

  protected ShaderState shaderState;

}

