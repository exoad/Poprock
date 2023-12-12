package pkg.exoad.softgradient.core.ui.java;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented @Retention(
	RetentionPolicy.SOURCE
) @Target(
	{ElementType.TYPE}
) public @interface DefsEqual
{
	String target();
	
	Class<?>[] reference();
}
