package pkg.exoad.poprock.core.io;
/**
 * Representation for Architecture information
 *
 * @author Jack Meng
 */
public enum ArchKey
{
	X86_32("x86_32",true),
	X86_64("x86_64",true),
	ARM("arm",true),
	ARM_64("arm64",true),
	PPC("ppc",false),
	IA64("ia64",false),
	SPARC("sparc",false),
	UNKNOWN("Unknown",false);
	
	final String name;
	final boolean supported;
	
	ArchKey(String name,boolean supported)
	{
		this.name     =name;
		this.supported=supported;
	}
	
	public boolean isSupported()
	{
		return supported;
	}
}
