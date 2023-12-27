package pkg.exoad.poprock.core.registry;
import java.util.Optional;
/**
 * A Descriptive Registry is one that also possess another property that presents a user
 * friendly description of this entry. This is extremely useful for when you are writing a
 * settings page, this part will be able to automate that styling.
 *
 * <p>
 * This class aims to automate the creation of a regular Descriptive Registry entry by
 * complying with the named construction.
 * </p>
 *
 * @author Jack Meng
 * @see RegistryEntryFactory
 */
public final class DescriptiveRegistryEntryFactory
	extends RegistryEntryFactory
{
	/**
	 * Internal raw representation of the description
	 */
	private String description;
	
	/**
	 * Function used to start the method chaining for the Descriptive Registry Entry
	 * class
	 *
	 * @return A new descriptive entry instance for method chaining
	 */
	public static DescriptiveRegistryEntryFactory make()
	{
		return new DescriptiveRegistryEntryFactory();
	}
	
	/**
	 * Named construction for the property Description of the Descriptive Entry class.
	 *
	 * @param str The description
	 *
	 * @return this (covariance)
	 */
	public DescriptiveRegistryEntryFactory withDescription(String str)
	{
		this.description=str;
		return this;
	}
	
	@Override public RegistryEntry collate()
	{
		super.checkTargets();
		ASSERT(description);
		return new DescriptiveRegistryEntry()
		{
			@Override public String description()
			{
				return description;
			}
			
			@Override public String getCanonicalName()
			{
				return canonicalName;
			}
			
			@Override public Optional<?> defaultValue()
			{
				return Optional.of(defaultValueCheck);
			}
			
			@Override public void setCurrentValue(Object r)
			{
				if(check(r))
					super.setCurrentValue(r);
			}
			
			@Override public <T> boolean check(T r)
			{
				return check.call(r);
			}
		};
	}
}
