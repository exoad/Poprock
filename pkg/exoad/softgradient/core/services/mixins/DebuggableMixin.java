package pkg.exoad.softgradient.core.services.mixins;

import pkg.exoad.softgradient.core.services.DebugService;

/**
 * A [minimal] mixin sort of interface to allow for easy use of throwing
 * exceptions using DebugService.
 * <p>
 * <strong>Example Usage</strong>
 * <blockquote><pre>
 * class Foo
 * implements DebuggableMixin
 * {
 * public Foo()
 * {
 * THROW_NOW("NoImpl Constructor");
 * }
 * }
 * </pre></blockquote>
 *
 * @author Jack Meng
 */
public interface DebuggableMixin
                                 extends
                                 SelfReportingMixin // really shouldn't this for mixins
{

      /**
       * @see pkg.exoad.softgradient.core.services.DebugService#throwNow(String)
       *
       * @param message
       */
      default void THROW_NOW(String message)
      {
            DebugService
                  .throwNow(getCanonicallyNamedThis()+": "+message);
      }

      /**
       * @see pkg.exoad.softgradient.core.services.DebugService#panicOn(boolean,
       * String)
       *
       * @param condition
       * @param message
       */
      default void THROW_NOW_IF(boolean condition,String message)
      {
            if(condition)
                  THROW_NOW(message);
      }
}
