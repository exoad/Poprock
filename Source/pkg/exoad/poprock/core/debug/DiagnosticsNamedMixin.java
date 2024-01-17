package pkg.exoad.poprock.core.debug;
import pkg.exoad.poprock.core.annotations.NotVirtual;
public interface DiagnosticsNamedMixin
{
	@NotVirtual default String getDiagnosticsName()
	{
		return "poprock#"+getClass().getName()+"["+hashCode()+"]";
	}
}
