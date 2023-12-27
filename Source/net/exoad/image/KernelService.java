package net.exoad.image;
import net.exoad.annotations.ServiceClass;

import java.awt.image.Kernel;
@ServiceClass(requiresArming=false)
public final class KernelService
{
	private KernelService(){}
	
	/**
	 * Assures all values in the kernel sum to 1
	 *
	 * @param kernel the supplied kernel
	 */
	public static void normalizeKernel(float[][] kernel)
	{
		float sum=0f;
		for(float[] r: kernel)
			for(float v: r)
				sum+=v;
		for(int i=0;i<kernel.length;i++)
			for(int j=0;j<kernel[i].length;j++)
				kernel[i][j]/=sum;
	}
	
	public static Kernel fromMatrix1D(float[] matrix,int size)
	{
		return new Kernel(size,size,matrix);
	}
	
	public static Kernel fromMatrix2D(float[][] matrix,int width,int height)
	{
		int size=matrix.length;
		int rows=matrix.length;
		int cols=matrix[0].length;
		float[] flatMatrix=new float[rows*cols];
		for(int i=0;i<rows;i++)
			System.arraycopy(matrix[i],0,flatMatrix,i*cols,cols);
		return new Kernel(cols,rows,flatMatrix);
	}
}
