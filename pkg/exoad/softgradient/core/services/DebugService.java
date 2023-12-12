package pkg.exoad.softgradient.core.services;

import pkg.exoad.softgradient.core.annotations.ProgramInvoked;
/**
 * Debug Service - Utility class for handling various exceptions that can be
 * thrown during the runtime of this program. <br/> Most of the things occuring
 * here like throwing errors should only happen in development. This is
 * especially the case because the hints provided are only able to.
 *
 * @author Jack Meng
 */
public final class DebugService
{
	private DebugService()
	{
	}
	
	private static RuntimeException modifyThrowable(
		RuntimeException throwable
	)
	{
		throwable
			.setStackTrace(
				new StackTraceElement[]{
					// this part should really be customized, but we can't cuz StackTraceElement.class is an immutable class!! D: big sad
					
					// we only supply the beginning stack trace because this will be the most useful call because it identifies the invoking source
					new StackTraceElement(
						throwable
							.getStackTrace()[0]
							.getClassName()+"...",
						throwable
							.getStackTrace()[0]
							.getMethodName(),
						":",
						throwable
							.getStackTrace()[0]
							.getLineNumber()
					),
					// we only supply the ending stack trace because that could be useful to identify the root source
					new StackTraceElement(
						throwable
							.getStackTrace()[throwable
												 .getStackTrace().length-1]
							.getClassName()+"...",
						throwable
							.getStackTrace()[throwable
												 .getStackTrace().length-1]
							.getMethodName(),
						":",
						throwable
							.getStackTrace()[throwable
												 .getStackTrace().length-1]
							.getLineNumber()
					)
				}
			);
		return throwable;
	}
	
	/**
	 * Tells the debug service to prepare an exception right now because of an
	 * internal error. Also note this is a synchronized method meaning only ONE
	 * thread may call it at a time.
	 *
	 * @param message The error message. THIS SHOULD NOT BE AUTOMATICALLY
	 * GENERATED
	 */
	@ProgramInvoked public static synchronized void throwNow(String message)
	{
		panicOn(
			true,
			"\n\t[!]\t"+message
		);
	}
	
	/**
	 * Tells the debug service to prepare an exception ONLY IF the condition
	 * passed returns TRUE
	 *
	 * @param condition The condition
	 * @param message The message. THIS SHOULD NOT BE AUTOMATICALLY GENERATED.
	 * [This is usually the hint given to a programmer]
	 */
	public static synchronized void panicOn(boolean condition,String message)
	{
		if(condition)
			throw modifyThrowable(
				new RuntimeException(
					"\n\t[!]\t"+message
				)
			);
	}
	
	/**
	 * Similar to {@link #panicOn(boolean,String)} but instead you can also pass
	 * a related cause of this exception.
	 *
	 * @param condition The condition
	 * @param message The message
	 * @param cause The related caused exception
	 *
	 * @see #panicOn(boolean,String)
	 */
	public static synchronized void panicOn(
		boolean condition,String message,Throwable cause
	)
	{
		if(condition)
			throw modifyThrowable(
				new RuntimeException(
					"\n\t[!]\t"+message,
					cause
				)
			);
	}
}
