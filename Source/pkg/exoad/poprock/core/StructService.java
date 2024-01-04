package pkg.exoad.poprock.core;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
public final class StructService
{
	private StructService(){}
	
	public static <A> boolean isEmpty(A[] e)
	{
		return e==null||e.length==0;
	}
	
	public static <T> ArrayList<T> arrayList(T[] e)
	{
		if(isEmpty(e))
			return new ArrayList<>();
		ArrayList<T> r=new ArrayList<>(e.length); // make no gurantees for the size exceed
		Collections.addAll(r,e);
		return r;
	}
}
