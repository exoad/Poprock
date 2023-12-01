package pkg.exoad.softgradient.core.services;

public final class DebugService
{
      private DebugService()
      {
      }

      public static synchronized void throwNow(String message)
      {
            throwIf(
                        true,
                        message
            );
      }

      public static synchronized void throwIf(boolean condition,String message)
      {
            if(condition)
                  throw new RuntimeException(message);
      }

      public static synchronized void throwIf(boolean condition,String message,Throwable cause)
      {
            if(condition)
                  throw new RuntimeException(
                              message,
                              cause
                  );
      }
}
