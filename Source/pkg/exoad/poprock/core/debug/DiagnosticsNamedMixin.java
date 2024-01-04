package pkg.exoad.poprock.core.debug;
import net.exoad.annotations.NotVirtual;
public interface DiagnosticsNamedMixin
{
	@NotVirtual default String getDiagnosticsName()
	{
		return "poprock#"+getClass().getName()+"["+hashCode()+"]";
	}
}
