package pkg.exoad.poprock.core.debug;

import pkg.exoad.poprock.core.annotations.ProgramInvoked;
import pkg.exoad.poprock.core.annotations.ServiceClass;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Debug Service - Utility class for handling various exceptions that can be thrown during
 * the runtime of this program. <br/> Most of the things occuring here like throwing
 * errors should only happen in development. This is especially the case because the hints
 * provided are only able to.
 *
 * @author Jack Meng
 */
@ServiceClass(requiresArming=false)
public final class DebugService
{
	/**
	 * If you wish to edit this. The format defines the following tags:
	 * <ul>
	 *     <li><code>{0}</code> denotes the {@link LogLevel}</li>
	 *     <li><code>{1}</code> denotes the time invoked in <code>mm/dd/yy
	 *     HH:MM:SS</code> format
	 *     </li>
	 *     <li><code>{2}</code> denotes the part where the message of the
	 *     log entry will be shown</li>
	 * </ul>
	 */
	public static String logMessageFormat="[ {0} | {1} ] >> {2}";
	/**
	 * If you wish to edit this, it must follow the format accepted by
	 * {@link SimpleDateFormat}
	 */
	public static String logTimeStampFormat="mm/dd/YYYY HH:mm:ssss";
	private static volatile PrintStream out;
	private static boolean USE_PANIC=true;
	private static boolean USE_CUSTOM_STACK_TRACE=true;
	
	private DebugService()
	{
	}
	
	/**
	 * Sets whether any panic messages or other messages for exceptions and errors are
	 * thrown to the print stream with a customized stack trace.
	 * <p>
	 * This option only exists to reduce cluster in pre existing exceptions
	 * </p>
	 *
	 * @param b on or off
	 */
	public static void allowCustomStackTrace(boolean b)
	{
		USE_CUSTOM_STACK_TRACE=b;
	}
	
	/**
	 * Whether the debug service is using custom stack traces for panicking
	 *
	 * @return true or false
	 */
	public static boolean isUsingCustomStackTraces()
	{
		return USE_CUSTOM_STACK_TRACE;
	}
	
	/**
	 * Sets whether any functions depending on any of the DebugService's
	 * <code>panic</code> functions will not be able to emit
	 * anything.
	 * <h2>THIS IS A VERY DANGEROUS METHOD, JUST LEAVE IT ON!!</h2>
	 *
	 * @param b on or off
	 */
	public static void allowPanicking(boolean b)
	{
		USE_PANIC=b;
	}
	
	/**
	 * Whether the debug service is allowed to panic the current program. Most of the time
	 * this is set to <code>true</code> unless
	 * {@link DebugService#allowPanicking(boolean)} is modified.
	 *
	 * @return true or false
	 */
	public static boolean canPanic()
	{
		return USE_PANIC;
	}
	
	/**
	 * Macro call for <code>DebugService.log(LogLevel.INFO,...)</code>
	 *
	 * @param msg message
	 */
	public static void info(Object msg)
	{
		log(LogLevel.INFO,msg);
	}
	
	/**
	 * Logs a message to the console with the specified level
	 *
	 * @param level The level of the log
	 * @param msg The message to log
	 */
	public static void log(LogLevel level,Object msg)
	{
		getOut().println(MessageFormat.format(
			logMessageFormat,
			level
				.getLevelName()
				.toUpperCase(),
			new SimpleDateFormat(logTimeStampFormat).format(new Date(System.currentTimeMillis())),
			msg
		));
	}
	
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
	 *
	 * @param p The new output stream
	 */
	public static synchronized void setOut(PrintStream p)
	{
		out=p;
	}
	
	/**
	 * Macro call for <code>DebugService.log(LogLevel.WARN,...)</code>
	 *
	 * @param msg message
	 */
	public static void warn(Object msg)
	{
		log(LogLevel.WARN,msg);
	}
	
	/**
	 * Macro call for <code>DebugService.log(LogLevel.NOTE,...)</code>
	 *
	 * @param msg message
	 */
	public static void note(Object msg)
	{
		log(LogLevel.NOTE,msg);
	}
	
	/**
	 * Tells the debug service to prepare an exception right now because of an internal
	 * error. Also note this is a synchronized method meaning only ONE thread may call it
	 * at a time.
	 *
	 * @param message The error message. THIS SHOULD NOT BE AUTOMATICALLY GENERATED
	 */
	@ProgramInvoked public static synchronized void panicNow(String message)
	{
		if(USE_PANIC)
			panicOn(
				true,
				"\n\t[!]\t"+message
			);
	}
	
	/**
	 * Tells the debug service to prepare an exception ONLY IF the condition passed
	 * returns TRUE
	 *
	 * @param condition The condition
	 * @param message The message. THIS SHOULD NOT BE AUTOMATICALLY GENERATED. [This is
	 * usually the hint given to a programmer]
	 */
	public static synchronized void panicOn(boolean condition,String message)
	{
		if(condition&&USE_PANIC)
			throw modifyThrowable(
				new RuntimeException(
					"\n\t[!]\t"+message
				)
			);
	}
	
	public static RuntimeException modifyThrowable(
		Throwable throwable
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
					new StackTraceElement(
						throwable
							.getStackTrace()[1]
							.getClassName()+"...",
						throwable
							.getStackTrace()[1]
							.getMethodName(),
						":",
						throwable
							.getStackTrace()[1]
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
		return (RuntimeException)throwable;
	}
	
	/**
	 * Instructs the service to kill the current program with a runtime exception and
	 * another exception or throwable provided.
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
	
	public static synchronized void panicWith(Thread t,Throwable e)
	{
		throw modifyThrowable(new RuntimeException(
			t.getName()+" panicked on"+e.getMessage(),
			e
		));
	}
	
	/**
	 * Similar to {@link #panicOn(boolean,String)} but instead you can also pass a related
	 * cause of this exception.
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
	
	/**
	 * Represents the logging level used for a log event
	 *
	 * @author Jack Meng
	 * @see #log(LogLevel,Object)
	 */
	public enum LogLevel
	{
		/**
		 * INFO - Just a quick debug message
		 */
		INFO("info"),
		/**
		 * WARN - something might be wrong or in a critical state
		 */
		WARN("warn"),
		/**
		 * NOTE - Just a headsup
		 */
		NOTE("note");
		
		/**
		 * Internal representation to the print stream
		 */
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
}
