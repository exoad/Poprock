package pkg.exoad.poprock.core.debug;

import pkg.exoad.poprock.core.annotations.NotVirtual;
/**
 * A mixin that contains some helpful functions for getting a formatted name of
 * the current object represented with its current runtime state.
 *
 * @author Jack Meng
 * @see java.lang.Object#hashCode()
 */
public interface SelfReportingMixin
{
	@NotVirtual default String getNamedThis()
	{
		return this
				   .getClass()
				   .getName()+"["+hashCode()+"]";
	}
	
	@NotVirtual default String getCanonicallyNamedThis()
	{
		return this
				   .getClass()
				   .getCanonicalName()+"["+hashCode()+"]";
	}
}
