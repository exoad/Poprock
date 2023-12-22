package net.exoad.annotations;
import pkg.exoad.poprock.core.ICollatable;

import java.lang.annotation.*;
/**
 * Signifies that the marked class is used to be used by the programmer to
 * create another object. This is usually the case to do something in a desired
 * way such as method chaining.
 *
 * @author Jack Meng
 * @see ICollatable
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Documented
@Inherited
public @interface Factory
{
}
