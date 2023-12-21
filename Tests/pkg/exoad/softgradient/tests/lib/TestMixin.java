package pkg.exoad.softgradient.tests.lib;
import pkg.exoad.softgradient.core.annotations.NotVirtual;
import pkg.exoad.softgradient.tests.TestMain;

public interface TestMixin
{
	@NotVirtual public default void $ASSERT(boolean condition,String truth)
	{
		if(!condition)
			$INVALIDATE(truth);
	}
	
	@NotVirtual public default void $PRINT(String truth)
	{
		System.out.println("[DEBUG_] | "+truth);
	}
	
	@NotVirtual public default void $INVALIDATE(String truth)
	{
		TestMain.INVALIDATIONS.add(truth);
	}
}
