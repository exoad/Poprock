package pkg.exoad.poprock.core.dsa;

import pkg.exoad.poprock.core.ITypeInferencing;

import java.util.ArrayList;
import java.util.Optional;

public class TreeMap<A,B>
	implements
	ITypeInferencing<Pair<Class<A>[],Class<B>[]>>

{
	public static <X,Z> TreeMap<X,Z> make()
	{
		return new TreeMap<>();
	}
	
	private java.util.TreeMap<A,B> delegates;
	
	public TreeMap()
	{
		delegates=new java.util.TreeMap<>();
	}
	
	public TreeMap<A,B> putIfAbsent(A key,B val)
	{
		delegates
			.putIfAbsent(key,val);
		return this;
	}
	
	public TreeMap<A,B> put(A key,B val)
	{
		delegates
			.put(key,val);
		return this;
	}
	
	@SuppressWarnings(
		"unchecked"
	) @Override public Optional<Pair<Class<A>[],Class<B>[]>> inferTyping()
	{
		if(delegates
			.isEmpty())
			return Optional
				.empty();
		ArrayList<Class<A>> typeA=new ArrayList<>(
			delegates
				.keySet()
				.size()
		);
		ArrayList<Class<B>> typeB=new ArrayList<>(
			delegates
				.entrySet()
				.size()
		);
		delegates
			.forEach((key,val)->{
				typeA
					.add(
						(Class<A>)key
							.getClass()
					);
				typeB
					.add(
						(Class<B>)val
							.getClass()
					);
			});
		return Optional
			.of(
				Pair
					.make(
						(Class<A>[])typeA
							.toArray(new Class[0]),
						(Class<B>[])typeB
							.toArray(new Class[0])
					)
			);
	}
}
