package pkg.exoad.softgradient.core.services.mixins;

import pkg.exoad.softgradient.core.services.DebugService;

public interface DebuggableMixin
                                 extends
                                 SelfReportingMixin
{

      default void THROW_NOW(String message)
      {
            DebugService
                  .throwNow(getCanonicallyNamedThis()+": "+message);
      }

      default void THROW_NOW_IF(boolean condition,String message)
      {
            if(condition)
                  THROW_NOW(message);
      }
}
