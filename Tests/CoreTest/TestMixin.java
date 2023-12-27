import net.exoad.annotations.NotVirtual;

import java.util.Arrays;

public interface TestMixin
{
	@NotVirtual default void $ASSERT(boolean condition,String truth)
	{
		if(!condition)
			$INVALIDATE(truth);
	}
	
	@NotVirtual default void $INVALIDATE(String truth)
	{
		TestMain.INVALIDATIONS.add(truth);
	}
	
	@NotVirtual default void $PRINT(String truth)
	{
		System.out.println("[DEBUG_] | "+truth);
	}
	
	@NotVirtual default String $STRINGIFY_2D_ARR(float[][] e)
	{
		assert e!=null;
		StringBuilder sb=new StringBuilder();
		for(float[] r: e)
			sb
				.append(Arrays.toString(r))
				.append("\n");
		return sb.toString();
	}
	
	@NotVirtual default String $STRINGIFY_2D_ARR(int[][] e)
	{
		assert e!=null;
		StringBuilder sb=new StringBuilder();
		for(int[] r: e)
			sb
				.append(Arrays.toString(r))
				.append("\n");
		return sb.toString();
	}
	
	@NotVirtual default String $STRINGIFY_2D_ARR(Object[][] e)
	{
		assert e!=null;
		StringBuilder sb=new StringBuilder();
		for(Object[] r: e)
			sb
				.append(Arrays.toString(r))
				.append("\n");
		return sb.toString();
	}
	
	@NotVirtual default String $STRINGIFY_2D_ARR(double[][] e)
	{
		assert e!=null;
		StringBuilder sb=new StringBuilder();
		for(double[] r: e)
			sb
				.append(Arrays.toString(r))
				.append("\n");
		return sb.toString();
	}
	
	@NotVirtual default String $STRINGIFY_2D_ARR(long[][] e)
	{
		assert e!=null;
		StringBuilder sb=new StringBuilder();
		for(long[] r: e)
			sb
				.append(Arrays.toString(r))
				.append("\n");
		return sb.toString();
	}
	
	@NotVirtual default String $STRINGIFY_2D_ARR(
		boolean[][] e
	) // why tf would this even be a case smh
	{
		assert e!=null;
		StringBuilder sb=new StringBuilder();
		for(boolean[] r: e)
			sb
				.append(Arrays.toString(r))
				.append("\n");
		return sb.toString();
	}
}
