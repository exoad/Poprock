package com.jackmeng;
import java.util.ArrayList;

public class ConsumableList<T>
	extends ArrayList<T>
{
	@Override public T get(int index)
	{
		T e=super.get(index);
		remove(index);
		return e;
	}
}