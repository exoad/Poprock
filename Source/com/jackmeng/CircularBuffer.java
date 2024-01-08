package com.jackmeng;

import java.nio.ByteBuffer;

public class CircularBuffer
{
	private final byte[] buffer;
	private int start, sz;
	
	public CircularBuffer(int init_Size)
	{
		buffer=new byte[init_Size];
	}
	
	public CircularBuffer()
	{
		this(255);
	}
	
	public int usedSize()
	{
		return sz;
	}
	
	public int totalSize()
	{
		return buffer.length;
	}
	
	public int freeSize()
	{
		return totalSize()-usedSize();
	}
	
	public void drop(int elementsCount)
	{
		int t=Math.min(elementsCount,sz);
		int sz_1=sz-t;
		int diff=sz-sz_1;
		start=calc_offset(start,diff,buffer.length);
		sz   =sz_1;
	}
	
	public int push(byte[] data,int i,int len)
	{
		int rem=len;
		rem=getRem(data,rem,i);
		return len-rem;
	}
	
	private int getRem(final byte[] data,int rem,int off)
	{
		do
		{
			int off_set=offset();
			int a=free(off_set);
			if(a==0)
				break;
			int copy=Math.min(rem,a);
			System.arraycopy(data,off,buffer,off_set,copy);
			next(copy);
			rem-=copy;
			off+=copy;
		}
		while(rem>0);
		return rem;
	}
	
	public void opush(byte[] data,int i,int len)
	{
		int rem=len;
		if(!overflows(len))
		{
			System.arraycopy(data,i,buffer,offset(),len);
			next(len);
			return;
		}
		rem=getRem(data,rem,i); // ??
	}
	
	public void init()
	{
		start=0;
		sz   =0;
	}
	
	private static int calc_offset(int len_0,int sz,int buff_len)
	{
		return (sz+len_0)%buff_len;
	}
	
	public int offset()
	{
		return calc_offset(start,sz,buffer.length);
	}
	
	public void peek(Callback<Void,ByteBuffer> e)
	{
		int f1=start;
		int f1_sz=buffer.length-start;
		if(f1_sz>sz)
			f1_sz=sz;
		ByteBuffer f=ByteBuffer.wrap(buffer,f1,f1_sz);
		e.call(f);
		if(f1_sz!=sz)
		{
			int f2=0;
			int f2_sz=sz-f1_sz;
			ByteBuffer er=ByteBuffer.wrap(buffer,f2,f2_sz);
			e.call(er);
		}
	}
	
	public int peek(byte[] data,int i,int len)
	{
		if(sz==0)
		{
			return 0;
		}
		int read=Math.min(len,sz);
		int offset=offset();
		int f1=start;
		int f1_sz=buffer.length-start;
		if(f1_sz>sz)
			f1_sz=sz;
		if(f1_sz>=read)
			f1_sz=read;
		System.arraycopy(data,f1,data,i,f1_sz);
		read-=f1_sz;
		if(read==0)
			return f1_sz;
		int f2=offset<=start?0:sz;
		int f2_sz=buffer.length-f1_sz;
		if(f2_sz>=read)
			f2_sz=read;
		System.arraycopy(data,f2,data,i+f1_sz,f2_sz);
		read-=f2_sz;
		return len-read;
	}
	
	public int pop(byte[] bytes,int i,int len)
	{
		int read=peek(bytes,i,len);
		drop(read);
		return read;
	}
	
	public boolean overflows(int n)
	{
		return offset()+n>buffer.length;
	}
	
	public void next(int n)
	{
		int sz_1=sz+n;
		int overflow=0;
		if(sz_1>buffer.length)
		{
			sz      =buffer.length;
			overflow=sz_1-buffer.length;
		}
		else
			sz=sz_1;
		start=calc_offset(start,overflow,buffer.length);
	}
	
	public int free(int n)
	{
		return start>=n?sz>0?n-start:buffer.length-n:buffer.length-n;
	}
}
