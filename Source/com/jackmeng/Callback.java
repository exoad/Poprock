package com.jackmeng;
@FunctionalInterface public interface Callback<T,E>
{
	T call(E arg);
}
