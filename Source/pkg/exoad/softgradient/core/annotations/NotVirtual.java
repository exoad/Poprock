package pkg.exoad.softgradient.core.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Suggests that whatever is being marked using this annotatation has no ability
 * to prevent itself from being overriden. While at the same time, it does not
 * want to be overridden; for example, in mixins.
 *
 * @author Jack Meng
 */
@Retention(
	RetentionPolicy.SOURCE
) @Documented @Target(
	{ElementType.METHOD,ElementType.FIELD,ElementType.TYPE}
) public @interface NotVirtual
{

}
