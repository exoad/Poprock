package pkg.exoad.softgradient.core.services;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Basic Service - Some very rudementary functions that can be used for some
 * Java related stuffs:
 *
 * <ul>
 * <li>Reflection helper functions</li>
 * <li>Basic Null Safety</li>
 * <li>Object equality</li>
 * </ul>
 *
 * and more...
 *
 * @author Jack Meng
 */
public final class BasicService
{
      private BasicService()
      {
      }

      /**
       * A very naive implementation to determine if a list of classes contain
       * the provided class by just checking
       * their canonical names. VERY NAIVE AND SHOULD NOT BE USED FOR MOST
       * CASES. However, I have to say this is the most
       * easy way to do it :)
       *
       * @param classes The classes to search in
       * @param clazz The class to search for
       *
       * @return true or false if the desired class was found or not
       * respectively
       */
      public static boolean containsClass(
            Collection<Class<?>> classes,Class<?> clazz
      )
      {
            for(
                  Class<?> c: classes
            )
                  if(c
                        .getCanonicalName()
                        .equals(
                              clazz
                                    .getCanonicalName()
                        ))
                        return true;
            return false;
      }

      /**
       * Similar to {@link java.util.Objects#requireNonNull(Object)}, but
       * instead we are going to check for a vararg amount of Objects. It is
       * primarily useful for dealing
       * with parameter validation:
       *
       * <blockquote><pre>
       * public Foo(A a,B b,C c)
       * {
       * requireAllNonNull(a,b,c);
       * }
       * </pre></blockquote>
       *
       * @param objects Varargs of Objects (List<?>)
       */
      public static void requireAllNonNull(Object... objects)
      {
            for(
                  Object r: objects
            )
                  Objects
                        .requireNonNull(r);
      }

      /**
       * Supplies an empty runnable that does nothing when called. Very useful
       * for placeholders and when you don't want to pass a {@code null} that
       * could lead to a
       * {@link java.lang.NullPointerException}
       *
       * @return The Empty, do-nothing runnable
       */
      public static Runnable emptyRunnable()
      {
            return ()->{};
      }

      /**
       * Supplies an empty consumer that does nothing when supplied anything.
       * Very useful for placeholders and when you don't want to pass a
       * {@code null}.
       *
       * @return The empty, do-nothing consumer
       *
       * @see #emptyRunnable()
       */
      public static Consumer<?> emptyConsumer()
      {
            return e->{};
      }
}
