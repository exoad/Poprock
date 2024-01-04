package net.exoad.annotations;
import java.lang.annotation.*;
/**
 * Since Java does not support free functions for the most part
 *
 * @author Jack Meng
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceClass
{
	boolean requiresArming();
}
