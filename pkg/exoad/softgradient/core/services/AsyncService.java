package pkg.exoad.softgradient.core.services;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AsyncService
{
      private AsyncService()
      {
      }

      public static void runAsync(Runnable runnable)
      {
            new Thread(runnable)
                  .start();
      }

      public static final Timer WORKER1=new Timer("pkg.exoad-softgradient-worker#1");
      public static final ExecutorService WORKER2=Executors
            .newWorkStealingPool();
}
