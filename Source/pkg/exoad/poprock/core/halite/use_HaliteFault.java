package pkg.exoad.poprock.core.halite;

import pkg.exoad.poprock.core.debug.DebugService;
/**
 * Just an abstraction for a specific Halite Exception??
 *
 * @author Jack Meng
 */
public class use_HaliteFault
	extends Exception
{
	
	/**
	 * Super
	 */
	public use_HaliteFault(String cause)
	{
		super(cause);
	}
	
	/**
	 * Static
	 *
	 * @param cause Super
	 */
	public static void launch_fault(final String cause)
	{
		DebugService.info(cause);
		new use_HaliteFault(
			"\n===================================\n"+cause+"\n===================================")
			.printStackTrace();
	}
}
