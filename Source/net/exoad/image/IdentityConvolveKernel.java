package net.exoad.image;

public final class IdentityConvolveKernel
{
	private IdentityConvolveKernel(){}
	
	public static float[][] compute(int size)
	{
		float[][] kernel=new float[size][size];
		if(size%2==0)
			size++;
		kernel[size/2][size/2]=1f;
		KernelService.normalizeKernel(kernel);
		return kernel;
	}
}
