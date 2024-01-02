package pkg.exoad.poprock.core.debug;
import pkg.exoad.poprock.core.services.mixins.InitializingMixin;
public interface DiagnosticsModuleMixin
	extends InitializingMixin
{
	default void init()
	{
		$DiagnosticsModuleMixin.__REFS.put(
			this,
			DiagnosticsMode.FAIL_FAST
		);// for debugging purposes
	}
	
	default void setDiagnosticsMode(DiagnosticsMode mode)
	{
		$DiagnosticsModuleMixin.__REFS.put(this,mode);
	}
	
	default DiagnosticsMode getDiagnosticsMode()
	{
		return $DiagnosticsModuleMixin.__REFS.get(this);
	}
}
