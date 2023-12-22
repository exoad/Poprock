package net.exoad.annotations;

import java.lang.annotation.*;

/**
 * Signifies that a particular function or method possess the ability to "panic"
 * or throw a Runtime exception. Most of the time, this exception thrown is
 * usually due to programmer error. Otherwise, if it is not, this annotation
 * should have its usage minimized.
 *
 * @author Jack Meng
 */
@Target({ElementType.METHOD,ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.CLASS)
@Documented
@Inherited
public @interface VolatileImpl
{
	String reason();
}
