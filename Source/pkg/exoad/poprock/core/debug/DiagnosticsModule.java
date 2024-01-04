package pkg.exoad.poprock.core.debug;
import pkg.exoad.poprock.core.IDisposable;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
public abstract class DiagnosticsModule<C extends DiagnosticsModule<C,A>,A extends Record> /* stupid ass generic-def for covariance */
	implements IDisposable
{
	private final AtomicLong startTimeStamp;
	private final AtomicBoolean started;
	
	protected DiagnosticsModule()
	{
		this.startTimeStamp=new AtomicLong(-1L);
		this.started       =new AtomicBoolean(false);
	}
	
	public boolean isStarted()
	{
		return started.get();
	}
	
	protected void setStarted(boolean newState)
	{
		this.started.set(newState);
	}
	
	public long computeElapsed()
	{
		return System.currentTimeMillis()-startTimeStamp.get();
	}
	
	public long getStartTimeStamp()
	{
		return startTimeStamp.get();
	}
	
	protected void setStartTimeStamp(long timeStamp)
	{
		this.startTimeStamp.set(timeStamp);
	}
	
	protected void setStartTimeStampNow()
	{
		setStartTimeStamp(System.currentTimeMillis());
	}
	
	public abstract void start();
	
	public abstract void createSnapshot(Consumer<A> cb);
	
	protected abstract C withPump(Consumer<A> r);
	
	public abstract void pause();
	
	public abstract void end();
	
	@Override public abstract void dispose();
	
	@Override public abstract String toString();
}
