package pkg.exoad.poprock.core;
/**
 * An improvised pair data structure for returning a result with a description.
 *
 * @param <T> The type of the result
 * @param <V> The resul description of type Enum
 *
 * @author Jack Meng
 */
public final class Result<T,V extends Enum<?>>
{
	public final T result;
	public final V reason;
	
	private Result(T result,V reason)
	{
		assert reason!=null;
		this.result=result;
		this.reason=reason;
	}
	
	public static <A,B extends Enum<?>> Result<A,B> make(A result,B reason)
	{
		return new Result<>(result,reason);
	}
	
	@Override public String toString()
	{
		return result+" because of "+reason;
	}
}
