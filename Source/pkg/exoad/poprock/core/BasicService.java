package pkg.exoad.poprock.core;

import net.exoad.annotations.ServiceClass;
import net.exoad.math.Maths;
import pkg.exoad.poprock.core.Pair;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Basic Service - Some very rudementary functions that can be used for some Java related
 * stuffs:
 *
 * <ul>
 * <li>Reflection helper functions</li>
 * <li>Basic Null Safety</li>
 * <li>Object equality</li>
 * </ul>
 *
 * and more...
 *
 * @author Jack Meng
 */
@ServiceClass(requiresArming=false)
public final class BasicService
{
	private static final List<Class<? extends Serializable>> PRIMS_CLASS=Arrays.asList(
		Boolean.class,
		Integer.class,
		Double.class,
		Long.class,
		Short.class,
		Byte.class,
		Float.class
	);
	
	private BasicService()
	{
	}
	
	/**
	 * A very naive implementation to determine if a list of classes contain the provided
	 * class by just checking their canonical names. VERY NAIVE AND SHOULD NOT BE USED FOR
	 * MOST CASES. However, I have to say this is the most easy way to do it :)
	 *
	 * @param classes The classes to search in
	 * @param clazz The class to search for
	 *
	 * @return true or false if the desired class was found or not respectively
	 */
	public static boolean containsClass(
		Collection<Class<?>> classes,Class<?> clazz
	)
	{
		for(
			Class<?> c: classes
		)
			if(c
				.getCanonicalName()
				.equals(
					clazz
						.getCanonicalName()
				))
				return true;
		return false;
	}
	
	public static int clampBounds(int i)
	{
		return Maths.clampInt(i,Integer.MIN_VALUE,Integer.MAX_VALUE);
	}
	
	/**
	 * A macro call for {@link java.lang.String#contains(CharSequence)} where you might
	 * want check against multiple sequences.
	 *
	 * @param str The target string to search in
	 * @param chars The supplied sequences to search against
	 *
	 * @return true or false if any of the target sequences were found
	 */
	public static boolean strContains(String str,CharSequence... chars)
	{
		assert chars!=null;
		for(CharSequence r: chars)
			if(str.contains(r))
				return true;
		return false;
	}
	
	/**
	 * Checks if any values in a Pair are nulled values. Please note that this function
	 * does not do additional checks after just {@link Pair#first()} or
	 * {@link Pair#second()} for nullability!
	 *
	 * @param v An array of Pairs to check
	 *
	 * @return true or false whether the supplied Pair contains any nulls
	 */
	public static boolean anyPairedNulls(Pair<?,?>[] v)
	{
		if(v==null) return true;
		for(Pair<?,?> a: v)
			if(a.first()==null||a.second()==null)
				return true;
		return false;
	}
	
	/**
	 * Similar to {@link java.util.Objects#requireNonNull(Object)}, but instead we are
	 * going to check for a vararg amount of Objects. It is primarily useful for dealing
	 * with parameter validation:
	 *
	 * <blockquote><pre>
	 * public Foo(A a,B b,C c)
	 * {
	 * requireAllNonNull(a,b,c);
	 * }
	 * </pre></blockquote>
	 *
	 * @param objects Varargs of Objects (List<?>)
	 */
	public static void requireAllNonNull(Object... objects)
	{
		for(
			Object r: objects
		)
			Objects
				.requireNonNull(r);
	}
	
	/**
	 * Supplies an empty runnable that does nothing when called. Very useful for
	 * placeholders and when you don't want to pass a {@code null} that could lead to a
	 * {@link java.lang.NullPointerException}
	 *
	 * @return The Empty, do-nothing runnable
	 */
	public static Runnable emptyRunnable()
	{
		return ()->{};
	}
	
	/**
	 * Supplies an empty consumer that does nothing when supplied anything. Very useful
	 * for placeholders and when you don't want to pass a {@code null}.
	 *
	 * @return The empty, do-nothing consumer
	 *
	 * @see #emptyRunnable()
	 */
	public static Consumer<?> emptyConsumer()
	{
		return e->{};
	}
	
	public static boolean isWrapperClass(Class<?> e)
	{
		return PRIMS_CLASS
			.contains(e);
	}
}
