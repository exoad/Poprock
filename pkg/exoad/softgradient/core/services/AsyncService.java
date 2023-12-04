package pkg.exoad.softgradient.core.services;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AsyncService - A utility class providing some basic asynchronous
 * functionalities.
 *
 * @author Jack Meng
 */
public final class AsyncService
{
      private AsyncService()
      {
      }

      /**
       * Invokes a task on a new {@link java.lang.Thread} object and runs it
       * concurrently immediately (or when the JVM is ready)
       *
       * @param runnable The task
       */
      public static void runAsync(Runnable runnable)
      {
            new Thread(runnable)
                  .start();
      }

      /**
       * WORKER1 delegates all of the routine tasks that either run as daemons
       * or periodically.
       *
       * @see java.util.Timer
       */
      public static final Timer WORKER1=new Timer(
            "pkg.exoad-softgradient-worker#1"
      );

      /**
       * WORKER2 delegates all of the other options for a
       * {@link java.util.concurrent.ForkJoinPool} for a workstealing thread
       * pool.
       */
      public static final ExecutorService WORKER2=Executors
            .newWorkStealingPool();
}
