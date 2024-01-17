package pkg.exoad.poprock.desktop.ui.java;

import java.lang.annotation.*;

@Documented @Retention(
	RetentionPolicy.SOURCE
) @Target(
	{ElementType.TYPE}
) public @interface DefsEqual
{
	String target();
	
	Class<?>[] reference();
}
