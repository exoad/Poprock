package pkg.exoad.poprock.desktop;

/**
 * Basic information class for the poprock desktop library.
 *
 * @author Jack Meng
 */
public final class PoprockDesktop
{
	private PoprockDesktop(){}
	
	/**
	 * Returns the version of the desktop branch.
	 *
	 * <p>
	 * It is formatted in YYYY_MM_DD format, where the first 4 digits are the
	 * year, the next 2 digits are the month, and the last 2 digits are the
	 * day.
	 * </p>
	 *
	 * @return the version of the library
	 */
	public static long getVersion()
	{
		return 2023_12_22L;
	}
}
