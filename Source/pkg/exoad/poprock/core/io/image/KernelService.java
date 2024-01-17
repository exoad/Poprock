package pkg.exoad.poprock.core.io.image;
import pkg.exoad.poprock.core.annotations.ServiceClass;

import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
@ServiceClass(requiresArming=false)
public final class KernelService
{
	private KernelService(){}
	
	public static int convolveBufferedImageAt(
		BufferedImage image,float[][] kernel,int x,int y
	)
	{
		float r=0, g=0, b=0;
		for(int i=-1;i<=1;i++)
		{
			for(int j=-1;j<=1;j++)
			{
				int rgb=image.getRGB(x+j,y+i);
				int red=(rgb>>16)&0xFF;
				int green=(rgb>>8)&0xFF;
				int blue=rgb&0xFF;
				r+=red*kernel[i+1][j+1];
				g+=green*kernel[i+1][j+1];
				b+=blue*kernel[i+1][j+1];
			}
		}
		return (Math.min(255,Math.max(0,(int)r))<<16)|(Math.min(255,
																Math.max(0,
																		 (int)g))<<8)|Math.min(
			255,
			Math.max(0,(int)b));
	}
	
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
