package pkg.exoad.poprock.core.debug;
import pkg.exoad.poprock.core.annotations.NotVirtual;
import pkg.exoad.poprock.core.mixins.InitializingMixinProvider;
public interface DiagnosticsModuleMixin
	extends InitializingMixinProvider
{
	default void init()
	{
		$DiagnosticsModuleMixin.__REFS.put(
			this,
			DiagnosticsMode.FAIL_FAST
		);// for debugging purposes
	}
	
	@NotVirtual
	default void runWith(Runnable r)
	{
		try
		{
			r.run();
		}catch(Exception e)
		{
			switch(getDiagnosticsMode())
			{
				case FAIL_FAST -> DebugService.panicWith(e);
				case EMIT -> DebugService.log(
					DebugService.LogLevel.WARN,
					DebugService.modifyThrowable(e)
				);
				case QUIET ->
				{
					// do nothing
				}
			}
		}
	}
	
	@NotVirtual
	default void setDiagnosticsMode(DiagnosticsMode mode)
	{
		$DiagnosticsModuleMixin.__REFS.put(this,mode);
	}
	
	@NotVirtual
	default DiagnosticsMode getDiagnosticsMode()
	{
		return $DiagnosticsModuleMixin.__REFS.get(this);
	}
}
