package net.exoad.image;
import pkg.exoad.poprock.core.struct.Pair;

import java.awt.image.Kernel;
import java.util.Arrays;
public final class ConvolveFunctor
{
	private ConvolveFunctor(){}
	
	public static Pair<Integer,float[]> compute0(int radius)
	{
		assert radius>0;
		return compute1(radius,1);
	}
	
	public static Pair<Integer,float[]> compute1(int radius,int shift)
	{
		assert radius>0;
		int size=radius*2+shift;
		float[] matrix=new float[size*size];
		float d=1f/(size*size);
		Arrays.fill(matrix,d);
		return Pair.make(size,matrix);
	}
}
