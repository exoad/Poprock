import net.exoad.image.RidgeConvolveKernel;

import java.util.Arrays;
@Section(name="Test ridge convolution calculations") public class Test_RidgeConvolve
	implements TestRoot,
			   TestMixin
{
	@Override public void test()
	{
		float[][] matrix_t1=RidgeConvolveKernel.compute(3);
		$ASSERT(Arrays.deepEquals(matrix_t1,new float[][]{
			{0,-1,0},
			{-1,4,-1},
			{0,-1,0}
		}),"Test #1 for convolution for matrix 3x3");
	}
}
