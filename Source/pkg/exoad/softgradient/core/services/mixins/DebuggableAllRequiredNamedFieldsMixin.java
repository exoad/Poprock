package pkg.exoad.softgradient.core.services.mixins;
import pkg.exoad.softgradient.core.annotations.NotVirtual;
import pkg.exoad.softgradient.core.services.DebugService;
public interface DebuggableAllRequiredNamedFieldsMixin
{
	@NotVirtual public default void ASSERT(Object e)
	{
		DebugService.panicOn(
			e==null,
			getClass().getCanonicalName()+"["+hashCode()+"] "+
			"requires a named parameter to be set!"
		);
	}
}
