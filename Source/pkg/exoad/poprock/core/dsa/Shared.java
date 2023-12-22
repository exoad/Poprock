package pkg.exoad.poprock.core.dsa;

import java.util.HashMap;
import java.util.UUID;

import java.lang.ref.WeakReference;

/**
 * <h2>Shared Object Pooling</h2>
 * A Shared Object at the simplest represents an object that can perform basic
 * atomicity operations, such as modifying a non-final variable inside a
 * lambda.
 * <p>
 * However, the main quirk of a Shared Object is that all allocated
 * SharedObjects are placed inside a pool. Similar to Java's String pool, if
 * multiple objects have the same value, only one instance of it will exist in
 * that pool, and one pointer. This method is not meant to save on memory, nor
 * does it even achieve such a goal; instead, it is focused on making sure
 * multiple instances of an object that can hold the same value don't exist at
 * the same time. For example, in concurrency, Shared objects will help to
 * maintain atomicity and mitigate race conditions.
 * <p>
 * The Shared Class itself does not hold the true value of the object, instead a
 * singular ID or numerical pointer to the value in the pool. Furthermore, the
 * usage of this class is very very simple and acts just like a normal Wrapper
 * class that just exists to get rid of the "modification of non-final variable
 * in a lambda" error thrown by Java. Just look at {@link #setValue(Object)} and
 * {@link #getValue()} they can't be anymore easier! <em>All the internal
 * complexity are hidden away</em>
 * <p>
 * <strong>Example Usage</strong>
 * <blockquote><pre>
 * public record FooBar(int integerValue,String stringValue) {}
 * // ...
 * Shared<FooBar> fooBar=Shared.of(new FooBar(30,"Hello World"));
 * fooBar.setValue(new FooBar(100,"Foo!"));
 * System.out.println(fooBar.getValue().stringValue());
 * </pre></blockquote>
 *
 * @author Jack Meng
 */
public class Shared<T>
{
	static HashMap<Long,WeakReference<Object>> OBJECTS=new HashMap<>();
	
	/**
	 * Creator function used to return a new shared object handler.
	 *
	 * @param <E> Type of the value
	 * @param value The value
	 *
	 * @return The Shared object handler
	 */
	public static <E> Shared<E> of(E value)
	{
		return new Shared<>(value);
	}
	
	private static <T> WeakReference<T> weakOf(T obj)
	{
		return new WeakReference<>(obj);
	}
	
	private long id;
	
	/**
	 * Creates a new Shared Object pool with default initializations, mainly
	 * hooking up this Shared object to the pool.
	 *
	 * @param e The initial value
	 */
	private Shared(T e)
	{
		UUID uuid=UUID
			.fromString(
				(e
					 .getClass()
					 .hashCode()*31+e
					 .hashCode())+""+e
					.getClass()
					.getCanonicalName()
			);
		id=(uuid
				.getLeastSignificantBits()&uuid
				.getMostSignificantBits())&0xFF;
		OBJECTS
			.put(
				id,
				Shared
					.weakOf(e)
			);
	}
	
	/**
	 * Modifies the current value pointed to
	 *
	 * @param e
	 */
	public void setValue(T e)
	{
		OBJECTS
			.put(
				id,
				Shared
					.weakOf(e)
			);
	}
	
	public synchronized void expirePoolReserve()
	{
		OBJECTS
			.remove(id);
		id=0;
	}
	
	/**
	 * Please note that this method can return a nullable object because of
	 * garbage collection {@link java.lang.ref.WeakReference}
	 *
	 * @return The shared object
	 */
	@SuppressWarnings(
		"unchecked"
	) public T getValue()
	{
		return (T)OBJECTS
			.get(id)
			.get();
	}
	
	public static <V extends Shared<?>> boolean computeEquality(
		V a,V b
	)
	{
		return a
				   .getValue()!=null&&b
										  .getValue()!=null&&a
				   .getValue()
				   .equals(
					   b
						   .getValue()
				   );
	}
	
	@Override public boolean equals(Object r)
	{
		return r instanceof Shared<?> r1&&Shared
			.computeEquality(this,r1);
	}
}
