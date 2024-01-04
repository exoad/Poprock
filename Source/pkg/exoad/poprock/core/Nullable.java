package pkg.exoad.poprock.core;
import java.util.function.Consumer;
public class Nullable<A>
{
	public static <B> Nullable<B> of(B value)
	{
		return new Nullable<>(value);
	}
	
	// exploiting Java's type reification :) [this could be for the best or for the worst]
	public static final Nullable<Object> NULL=Nullable.of(null);
	
	private A ref;
	
	private Nullable(A value)
	{
		this.ref=value;
	}
	
	/**
	 * Basic type inferencing. Gurantees of either of the generics type or of
	 * {@link Null}
	 *
	 * @return The class (runtime type)
	 */
	public Class<?> infer()
	{
		return isNull()?Null.class:ref.getClass();
	}
	
	public boolean isNull()
	{
		return ref==null;
	}
	
	public A peek()
	{
		return ref;
	}
	
	public void assign(A value)
	{
		this.ref=value;
	}
	
	public Nullable<A> ifNotNull(Consumer<A> cb)
	{
		if(!isNull())
			cb.accept(ref);
		return this;
	}
	
	public Nullable<A> ifNull(Runnable r)
	{
		if(isNull())
			r.run();
		return this;
	}
	
	public void assignIfNull(A ref)
	{
		if(isNull())
			this.ref=ref;
	}
	
	@Override public String toString()
	{
		return isNull()?"null":ref.toString();
	}
}
