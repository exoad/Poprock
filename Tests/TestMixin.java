import net.exoad.annotations.NotVirtual;

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
