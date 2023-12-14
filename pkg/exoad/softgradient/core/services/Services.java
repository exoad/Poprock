package pkg.exoad.softgradient.core.services;
import java.util.Arrays;
public final class Services
{
	private Services() {}
	
	public static void ensureServicesArmed()
	{
		Arrays.stream(Package.getPackages()).filter(e->e.getName().equals(
			"pkg.exoad.softgradient.core.services"));
	}
}
