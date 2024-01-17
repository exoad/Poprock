import pkg.exoad.poprock.core.io.image.Kernels;
@Section(name="Test gaussian auto generating calculations") public class Test_GaussianKernel
	implements TestRoot,
			   TestMixin
{
	@Override public void test()
	{
		$PRINT("\n"+$STRINGIFY_2D_ARR(Kernels.generateGaussianBlurKernel(3,1.0f)));
		
		$PRINT("\n"+$STRINGIFY_2D_ARR(Kernels.GAUSSIAN_BLUR_KERNEL_3x3));
		
		$PRINT("\n\n"+$STRINGIFY_2D_ARR(Kernels.generateGaussianBlurKernel(5,1.0f)));
		
		$PRINT("\n"+$STRINGIFY_2D_ARR(Kernels.GAUSSIAN_BLUR_KERNEL_5x5));
	}
}
