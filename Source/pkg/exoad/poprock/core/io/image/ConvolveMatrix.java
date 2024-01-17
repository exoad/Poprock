package pkg.exoad.poprock.core.io.image;
import pkg.exoad.poprock.core.Pair;

import java.util.Arrays;
public final class ConvolveMatrix
{
	private ConvolveMatrix(){}
	
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
