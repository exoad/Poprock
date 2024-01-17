package pkg.exoad.poprock.core.io.image;
public final class Kernels
{
	public static final float[][] IDENTITY_KERNEL={{0f,0f,0f},{0f,1f,0f},{0f,0f,0f}};
	
	public static final float[][] RIDGE_KERNEL_1={{0f,-1f,0f},{-1,4f,-1f},{0f,-1f,0f}};
	
	public static final float[][] RIDGE_KERNEL_2={{-1f,-1f,-1f},{-1f,8f,-1f},
												  {-1f,-1f,-1f}};
	
	public static final float[][] SHARPEN_KERNEL_1={{0f,-1f,0f},{-1f,5f,-1f},{0f,-1f,0f}};
	
	public static final float[][] SHARPEN_KERNEL_2={{-1f,-1f,-1f},{-1f,9f,-1f},
													{-1f,-1f,-1f}};
	
	public static final float[][] SOBEL_X_KERNEL={{1f,0f,-1f},{2f,0f,-2f},{1f,0f,-1f}};
	
	public static final float[][] SOBEL_Y_KERNEL={{1f,2f,1f},{0f,0f,0f},{-1f,-2f,-1f}};
	
	public static final float[][] BOX_BLUR_KERNEL={{1f/9f,1f/9f,1f/9f},
												   {1f/9f,1f/9f,1f/9f},
												   {1f/9f,1f/9f,1f/9f}};
	
	public static final float[][] GAUSSIAN_BLUR_KERNEL_3x3={{1f/16f,2f/16f,1f/16f},
															{2f/16f,4f/16f,2f/16f},
															{1f/16f,2f/16f,1f/16f}};
	
	public static final float[][] GAUSSIAN_BLUR_KERNEL_5x5={
		{1f/256f,4f/256f,6f/256f,4f/256f,1f/256f},
		{4f/256f,16/256f,24f/256f,16f/256f,4f/256f},
		{6f/256f,24f/256f,36f/256f,24f/256f,6f/256f},
		{4f/256f,16f/256f,24f/256f,16f/256f,4f/256f},
		{1f/256f,4f/256f,6f/256f,4f/256f,1f/256f}};
	
	private Kernels(){}
	
	public static float[][] generateGaussianBlurKernel(int size,float sigma)
	{
		if(size%2==0||size<3)
			throw new IllegalArgumentException("Invalid size (size >=3 and size is odd)");
		float[][] kernel=new float[size][size];
		float sum=0.0F;
		int center=size/2;
		for(int i=0;i<size;i++)
		{
			for(int j=0;j<size;j++)
			{
				int x=i-center;
				int y=j-center;
				kernel[i][j]=(float)Math.exp(-(x*x+y*y)/(2.0*Math.PI*sigma*sigma));
				sum+=kernel[i][j];
			}
		}
		KernelService.normalizeKernel(kernel);
		return kernel;
	}
}
