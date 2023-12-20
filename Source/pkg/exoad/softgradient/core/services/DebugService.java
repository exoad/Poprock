package pkg.exoad.softgradient.core.services;

import pkg.exoad.softgradient.core.annotations.ProgramInvoked;
import pkg.exoad.softgradient.core.annotations.ServiceClass;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Debug Service - Utility class for handling various exceptions that can be
 * thrown during the runtime of this program. <br/> Most of the things occuring
 * here like throwing errors should only happen in development. This is
 * especially the case because the hints provided are only able to.
 *
 * @author Jack Meng
 */
@ServiceClass(requiresArming=false)
public final class DebugService
{
	private DebugService()
	{
	}
	
	/**
	 * If you wish to edit this. The format defines the following tags:
	 * <ul>
	 *     <li><code>{1}</code> denotes the {@link LogLevel}</li>
	 *     <li><code>{2}</code> denotes the time invoked in <code>mm/dd/yy
	 *     HH:MM:SS</code> format
	 *     </li>
	 *     <li><code>{3}</code> denotes the part where the message of the
	 *     log entry will be shown</li>
	 * </ul>
	 */
	public static String logMessageFormat="[ {1} | {2} ] >> {3}";
	
	/**
	 * If you wish to edit this, it must follow the format accepted by
	 * {@link SimpleDateFormat}
	 */
	public static String logTimeStampFormat="mm/dd/YYYY HH:mm:ssss";
	
	/**
	 * Represents the logging level used for a log event
	 *
	 * @author Jack Meng
	 *
	 * @see #log(LogLevel,Object)
	 */
	public enum LogLevel
	{
		INFO("info"),
		WARN("warn"),
		NOTE("note");
		
		final String levelName;
		
		LogLevel(String l)
		{
			this.levelName=l;
		}
		
		public String getLevelName()
		{
			return this.levelName;
		}
	}
	
	private static volatile PrintStream out;
	
	/**
	 * @return The internal output stream used by this debug service
	 */
	public static PrintStream getOut()
	{
		if(out==null)
			setOut(new PrintStream(System.out));
		return out;
	}
	
	/**
	 * Sets the internal output stream used by this debug service
	 * @param p The new output stream
	 */
	public static synchronized void setOut(PrintStream p)
	{
		out=p;
	}
	
	/**
	 * Logs a message to the console with the specified level
	 * @param level The level of the log
	 * @param msg The message to log
	 */
	public static void log(LogLevel level,Object msg)
	{
		out.println(MessageFormat.format(
			logMessageFormat,
			level
				.getLevelName()
				.toUpperCase(),
			new SimpleDateFormat(logTimeStampFormat).format(new Date(System.currentTimeMillis())),
			msg.toString()
		));
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
	 * Instructs the service to kill the current program with a runtime
	 * exception and another exception or throwable provided.
	 *
	 * @param e The other provided throwable
	 */
	public static synchronized void panicWith(Throwable e)
	{
		throw modifyThrowable(new RuntimeException(Thread
													   .currentThread()
													   .getName()+" panicked "+
												   "on "+e.getMessage(),e));
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
