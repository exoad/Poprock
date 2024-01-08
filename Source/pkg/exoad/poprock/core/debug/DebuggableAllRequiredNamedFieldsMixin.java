package pkg.exoad.poprock.core.debug;
import net.exoad.annotations.NotVirtual;
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
