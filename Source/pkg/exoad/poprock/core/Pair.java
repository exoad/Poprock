package pkg.exoad.poprock.core;

public final class Pair<A,B>
{
	public static <X,Z> Pair<X,Z> make(X first,Z second)
	{
		return new Pair<>(first,second);
	}
	
	private A first;
	private B second;
	
	public Pair(A first,B second)
	{
		this.first =first;
		this.second=second;
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
	
}
