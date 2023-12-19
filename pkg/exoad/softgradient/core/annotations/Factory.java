package pkg.exoad.softgradient.core.annotations;
import java.lang.annotation.*;
/**
 * Signifies that the marked class is used to be used by the programmer to
 * create another object. This is usually the case to do something in a desired
 * way such as method chaining.
 *
 * @author Jack Meng
 * @see pkg.exoad.softgradient.core.ICollatable
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Documented
@Inherited
public @interface Factory
{
}
