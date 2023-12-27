package net.exoad.image;
public final class RidgeConvolveKernel
{
	private RidgeConvolveKernel(){}
	
	public static float[][] compute(int size)
	{
		assert size%2==1;
		float[][] matrix=new float[size][size];
		int center=size/2;
		for(int i=0;i<size;i++)
			for(int j=0;j<size;j++)
				if(i==center||j==center)
					matrix[i][j]=1.0f;
		return matrix;
	}
}
