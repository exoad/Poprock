package pkg.exoad.softgradient.tests.lib;
import pkg.exoad.softgradient.core.annotations.NotVirtual;
public interface TestMixin
{
	@NotVirtual public default void $ASSERT(boolean condition,String truth)
	{
		if(!condition)
			System.out.println("[FAILED] | "+truth);
	}
	
	@NotVirtual public default void $PRINT(String truth)
	{
		System.out.println("[ALERT_] | "+truth);
	}
}
