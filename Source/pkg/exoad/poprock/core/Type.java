package pkg.exoad.poprock.core;
public class Type<A>
{
	private final Class<A> base;
	
	private Type(Class<A> base)
	{
		assert base!=null;
		this.base=base;
	}
	
	public <B> A conform(B e)
	{
		return base.cast(e);
	}
	
	public boolean isEqual(Class<?> another)
	{
		// naive implementation for now, please fix!!
		return base
				   .getCanonicalName()
				   .equals(another.getCanonicalName())&&base
				   .getPackage()
				   .getName()
				   .equals(another
							   .getPackage()
							   .getName());
	}
}
