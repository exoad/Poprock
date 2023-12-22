package com.jackmeng;
public interface NaiveMonad<T>
{
	static <T> NaiveMonad<T> unit(T value)
	{
		return new NaiveMonad<>()
		{
			@Override public <R> NaiveMonad<R> bind(
				Function<? super T,NaiveMonad<R>> f
			)
			{
				return f.apply(value);
			}
		};
	}
	
	<R> NaiveMonad<R> bind(Function<? super T,NaiveMonad<R>> f);
}
