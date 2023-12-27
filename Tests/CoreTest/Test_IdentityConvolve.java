import net.exoad.image.IdentityConvolveKernel;

import java.util.Arrays;
@Section(name="Test for Identity Convolve calculations") public class Test_IdentityConvolve
	implements
	TestRoot,
	TestMixin
{
	@Override public void test()
	{
		float[][] matrix_t1=IdentityConvolveKernel.compute(3);
		$ASSERT(Arrays.deepEquals(matrix_t1,new float[][]{
			{0,0,0},
			{0,1,0},
			{0,0,0}
		}),"TEST FOR 3x3 MATRIX IDENTITY");
		$PRINT("\n"+$STRINGIFY_2D_ARR(matrix_t1));
	}
}
