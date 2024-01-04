package pkg.exoad.poprock.core.debug;

import pkg.exoad.poprock.core.ChronosService;
import pkg.exoad.poprock.core.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
public final class MemoryDiagnosticsModule
	extends DiagnosticsModule<MemoryDiagnosticsModule,MemoryDiagnosticsModule.MemoryDiagnosticsSnapshot>
	implements DiagnosticsModuleMixin,
			   DiagnosticsNamedMixin
{
	public static MemoryDiagnosticsModule make()
	{
		return new MemoryDiagnosticsModule();
	}
	
	public static MemoryDiagnosticsModule make(long period)
	{
		return new MemoryDiagnosticsModule(period,0L);
	}
	
	public static MemoryDiagnosticsModule make(long period,long delay)
	{
		return new MemoryDiagnosticsModule(period,delay);
	}
	
	public record MemoryDiagnosticsSnapshot(
		long initialFreeMemory,long initialTotalMemory,long currentFreeMemory,
		long currentTotalMemory,float freeMemoryDeviation,float totalMemoryDeviation,
		DiagnosticsModuleState moduleState,long timeStamp
	)
		implements Serializable
	{
		@Override public String toString()
		{
			return String.format(
				"MemorySnapshot@%s{workerState=%s, initialFreeMemory=%d bytes, initialTotalMemory=%d bytes, currentFreeMemory=%d bytes, currentTotalMemory=%d bytes, deltaFreeMemory=%f bytes, deltaTotalMemory=%f bytes}",
				ChronosService.MM_DD_YYYY.format(new Date(timeStamp)),
				moduleState.name(),
				initialFreeMemory,
				initialTotalMemory,
				currentFreeMemory,
				currentTotalMemory,
				freeMemoryDeviation,
				totalMemoryDeviation
			);
		}
	}
	
	private static final long UNDEFINED_L=-1L;
	private final AtomicLong iFree;
	private final AtomicLong iTotal;
	private final AtomicLong fFree;
	private final AtomicLong fTotal;
	private final AtomicLong dFree;
	private final AtomicLong dTotal;
	private final long period;
	private final long delay;
	private DiagnosticsModuleState state;
	private final Runnable action;
	private final ScheduledExecutorService worker;
	private final Nullable<Consumer<MemoryDiagnosticsSnapshot>> pump;
	
	private MemoryDiagnosticsModule()
	{
		this(350L,0L);
	}
	
	private MemoryDiagnosticsModule(long period,long delay)
	{
		DebugService.panicOn(
			period<=0,
			"Period must be greater than 0 (period>0)"
		);
		DebugService.panicOn(delay<0,"Initial delay cannot be negative (delay>=0)");
		this.period=period;
		this.delay =delay;
		pump       =Nullable.of(null);
		state      =DiagnosticsModuleState.PAUSED;
		iFree      =new AtomicLong(UNDEFINED_L);
		iTotal     =new AtomicLong(UNDEFINED_L);
		fFree      =new AtomicLong(UNDEFINED_L);
		fTotal     =new AtomicLong(UNDEFINED_L);
		dFree      =new AtomicLong(UNDEFINED_L);
		dTotal     =new AtomicLong(UNDEFINED_L);
		worker     =Executors.newScheduledThreadPool(1);
		action     =()->{
			if(state==DiagnosticsModuleState.MONITORING)
			{
				long cFree=Runtime
					.getRuntime()
					.freeMemory();
				long cTotal=Runtime
					.getRuntime()
					.totalMemory();
				dFree.set(cFree-fFree.get());
				fTotal.set(cTotal);
				dTotal.set(cTotal-fTotal.get());
				fFree.set(cFree);
				pump.ifNotNull(x->CompletableFuture.runAsync(()->createSnapshot(x)));
			}
		};
	}
	
	@Override public void start()
	{
		if(state!=DiagnosticsModuleState.MONITORING)
		{
			iFree.set(Runtime
						  .getRuntime()
						  .freeMemory());
			iTotal.set(Runtime
						   .getRuntime()
						   .totalMemory());
			worker.scheduleAtFixedRate(action,delay,period,TimeUnit.MILLISECONDS);
			state=DiagnosticsModuleState.MONITORING;
		}
	}
	
	@Override public synchronized void createSnapshot(
		Consumer<MemoryDiagnosticsSnapshot> cb
	)
	{
		long time=System.currentTimeMillis();
		long iTotal=this.iTotal.getOpaque();
		long iFree=this.iFree.getOpaque();
		long fFree=this.fFree.getOpaque();
		long fTotal=this.fTotal.getOpaque();
		long dFree=this.dFree.getOpaque();
		long dTotal=this.dTotal.getOpaque();
		cb.accept(new MemoryDiagnosticsSnapshot(
			iFree,
			iTotal,
			fFree,
			fTotal,
			dFree,
			dTotal,
			state,
			time
		));
	}
	
	@Override public MemoryDiagnosticsModule withPump(
		Consumer<MemoryDiagnosticsSnapshot> r
	)
	{
		this.pump.assign(r);
		return this;
	}
	
	@Override public synchronized void pause()
	{
		state=DiagnosticsModuleState.PAUSED;
	}
	
	@Override public synchronized void end()
	{
		worker.shutdownNow();
		state=DiagnosticsModuleState.PAUSED;
	}
	
	@Override public synchronized void dispose()
	{
		if(state!=DiagnosticsModuleState.PAUSED)
			end();
		state=DiagnosticsModuleState.PAUSED;
		iFree.set(UNDEFINED_L);
		iTotal.set(UNDEFINED_L);
		fFree.set(UNDEFINED_L);
		fTotal.set(UNDEFINED_L);
		dFree.set(UNDEFINED_L);
		dTotal.set(UNDEFINED_L);
	}
	
	@Override public String toString()
	{
		return getDiagnosticsName()+String.format(
			"{initialFreeMemory=%d bytes,initialTotalMemory=%d bytes,currentFreeMemory=%d bytes,currentTotalMemory=%d bytes,deltaFreeMemory=%d bytes,deltaTotalMemory=%d bytes}",
			iFree.get(),
			iTotal.get(),
			fFree.get(),
			fTotal.get(),
			dFree.get(),
			dTotal.get()
		);
	}
}
