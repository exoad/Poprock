package net.exoad.annotations;

import java.lang.annotation.*;
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface ProgramInvoked
{
}
