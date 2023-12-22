package net.exoad.annotations.poprock.core.services.mixins;
import net.exoad.annotations.NotVirtual;
import net.exoad.annotations.poprock.core.services.DebugService;
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
