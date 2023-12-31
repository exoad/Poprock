package pkg.exoad.poprock.core.struct;
/**
 * A non managed version of {@link Shared}
 *
 * @param <T> internal type
 *
 * @author Jack Meng (exoad)
 */
public class Wrap<T>
{
	public T value;
	
	private Wrap(T value)
	{
		this.value=value;
	}
	
	public static <A> Wrap<A> of(A value)
	{
		return new Wrap<>(value);
	}
	
	public T getValue()
	{
		return value;
	}
	
	public void setValue(T value)
	{
		this.value=value;
	}
	
	@Override public String toString()
	{
		return "Wrapped["+value+"]";
	}
}
