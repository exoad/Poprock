package pkg.exoad.poprock.core.services.mixins;

import net.exoad.annotations.NotVirtual;
import pkg.exoad.poprock.core.services.DebugService;

/**
 * A [minimal] mixin sort of interface to allow for easy use of throwing
 * exceptions using DebugService.
 * <p>
 * <strong>Example Usage</strong>
 * <blockquote><pre>
 * class Foo
 * implements DebuggableMixin
 * {
 * public Foo()
 * {
 * THROW_NOW("NoImpl Constructor");
 * }
 * }
 * </pre></blockquote>
 *
 * @author Jack Meng
 */
public interface DebuggableMixin
	extends
	SelfReportingMixin // really shouldn't this for mixins
{
	
	/**
	 * @param message hint for the error message
	 *
	 * @see DebugService#throwNow(String)
	 */
	@NotVirtual default void THROW_NOW(String message)
	{
		DebugService
			.throwNow(getCanonicallyNamedThis()+": "+message);
	}
	
	/**
	 * @param condition When to panic only if this boolean is true
	 * @param message hint for the error message
	 *
	 * @see DebugService#panicOn(boolean,
	 * String)
	 */
	@NotVirtual default void THROW_NOW_IF(boolean condition,String message)
	{
		if(condition)
			THROW_NOW(message);
	}
	
	/**
	 * @param level LogLevel
	 * @param message Attached log message
	 *
	 * @see DebugService#log(DebugService.LogLevel,Object)
	 */
	@NotVirtual default void LOG(DebugService.LogLevel level,String message)
	{
		DebugService.log(level,message);
	}
}
