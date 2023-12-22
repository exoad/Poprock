package pkg.exoad.poprock.services.mixins;
import pkg.exoad.poprock.annotations.NotVirtual;
import pkg.exoad.poprock.services.DebugService;
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
