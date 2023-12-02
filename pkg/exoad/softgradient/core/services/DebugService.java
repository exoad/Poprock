package pkg.exoad.softgradient.core.services;

public final class DebugService
{
      private DebugService()
      {
      }

      private static RuntimeException modifyThrowable(
            RuntimeException throwable
      )
      {
            throwable
                  .setStackTrace(
                        new StackTraceElement[]{
                                                new StackTraceElement(
                                                      throwable
                                                            .getStackTrace()[0]
                                                                  .getClassName()+"...",
                                                      throwable
                                                            .getStackTrace()[0]
                                                                  .getMethodName(),
                                                      ":",
                                                      throwable
                                                            .getStackTrace()[0]
                                                                  .getLineNumber()
                                                ),
                                                new StackTraceElement(
                                                      throwable
                                                            .getStackTrace()[throwable
                                                                  .getStackTrace().length-1]
                                                                        .getClassName()+"...",
                                                      throwable
                                                            .getStackTrace()[throwable
                                                                  .getStackTrace().length-1]
                                                                        .getMethodName(),
                                                      ":",
                                                      throwable
                                                            .getStackTrace()[throwable
                                                                  .getStackTrace().length-1]
                                                                        .getLineNumber()
                                                )
                        }
                  );
            return throwable;
      }

      public static synchronized void throwNow(String message)
      {
            throwIf(
                  true,
                  "\n\t[!]\t"+message
            );
      }

      public static synchronized void throwIf(boolean condition,String message)
      {
            if(condition)
                  throw modifyThrowable(
                        new RuntimeException(
                              "\n\t[!]\t"+message
                        )
                  );
      }

      public static synchronized void throwIf(
            boolean condition,String message,Throwable cause
      )
      {
            if(condition)
                  throw modifyThrowable(
                        new RuntimeException(
                              "\n\t[!]\t"+message,
                              cause
                        )
                  );
      }
}
