package pkg.exoad.poprock.core;

public final class Pair<A,B>
{
	private A first;
	private B second;
	
	public Pair(A first,B second)
	{
		this.first =first;
		this.second=second;
	}
	
	public static <X,Z> Pair<X,Z> make(X first,Z second)
	{
		return new Pair<>(first,second);
	}
	
	public A first()
	{
		return first;
	}
	
	public B second()
	{
		return second;
	}
	
	public void first(A first)
	{
		this.first=first;
	}
	
	public void second(B second)
	{
		this.second=second;
	}
	
	@Override public int hashCode()
	{
		return (((((1<<1)-1)&first.hashCode())
				 ^(0))<<((Integer.BYTES
						  *8)/2))
			   
			   |((((1<<1)-1)&second.hashCode())
				 ^(0));
	}
	
	@Override public boolean equals(Object cum)
	{
		if(!(cum instanceof Pair<?,?>))
			return false;
		return ((Pair<?,?>)cum).first.equals(first)&&((Pair<?,?>)cum).second.equals(
			second);
	}
	
	@Override public String toString()
	{
		return "Pair{ "+first.toString()+" , "+second.toString()+" }";
	}
	
}
