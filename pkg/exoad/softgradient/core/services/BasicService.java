package pkg.exoad.softgradient.core.services;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

public final class BasicService
{
      private BasicService()
      {
      }

      public static boolean containsClass(Collection<Class<?>> classes,Class<?> clazz)
      {
            for(Class<?> c: classes)
                  if(c
                        .getCanonicalName()
                        .equals(
                              clazz
                                    .getCanonicalName()
                        ))
                        return true;
            return false;
      }

      public static void requireAllNonNull(Object... objects)
      {
            for(Object r: objects)
                  Objects
                        .requireNonNull(r);
      }

      public static Runnable emptyRunnable()
      {
            return ()->{};
      }

      public static Consumer<?> emptyConsumer()
      {
            return e->{};
      }
}
