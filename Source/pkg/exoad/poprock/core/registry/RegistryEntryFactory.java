package pkg.exoad.poprock.core.registry;
import pkg.exoad.poprock.core.Functor11;
import pkg.exoad.poprock.core.ICollatable;
import pkg.exoad.poprock.core.debug.DebugService;
import pkg.exoad.poprock.core.debug.DebuggableAllRequiredNamedFieldsMixin;
import pkg.exoad.poprock.core.services.SharedServices;

import java.util.Optional;
/**
 * <strong>RegistryEntry Factory</strong>
 * <p>
 * This class helps to construct a RegistryEntry using method chaining. It provides all of
 * the necessary fields and construction functionality to make a working Registry Entry.
 * </p>
 *
 * <strong>To use this class, you must call
 * {@link RegistryEntryFactory#make() }!</strong>
 *
 * @author Jack Meng
 * @see RegistryEntry
 * @see RegistryEntryFactory#make()
 */
public sealed class RegistryEntryFactory
	implements
	ICollatable<RegistryEntry>,
	DebuggableAllRequiredNamedFieldsMixin
	permits
	DescriptiveRegistryEntryFactory
{
	protected Functor11<Boolean,Object> check;
	protected Object defaultValueCheck;
	protected String canonicalName;
	private Object currentValue;
	
	protected RegistryEntryFactory(){}
	
	/**
	 * Starts the factory of making a registry entry using method chaining
	 *
	 * @return A new instance of the Registry Entry Factory
	 */
	public static RegistryEntryFactory make()
	{
		return new RegistryEntryFactory();
	}
	
	/**
	 * Named constructor method for setting the checker function. Please note that the
	 * functionality of the checker is mainly used for custom user implemented checks. For
	 * example Integer Bound Checks, these are not handled by any registries themselves
	 * (for the most part).
	 *
	 * @param check The checker function
	 *
	 * @return this
	 */
	public RegistryEntryFactory withCheck(Functor11<Boolean,Object> check)
	{
		this.check=check;
		return this;
	}
	
	/**
	 * Named constructor method for setting the canonical name property
	 *
	 * @param name The canonical name
	 *
	 * @return this
	 */
	public RegistryEntryFactory withCanonicalName(String name)
	{
		this.canonicalName=name;
		return this;
	}
	
	/**
	 * Named constructor method for setting the default value on false evaluation
	 *
	 * @param defaultCheck The default value
	 *
	 * @return this
	 */
	public RegistryEntryFactory withDefaultValue(
		Object defaultCheck
	)
	{
		this.defaultValueCheck=defaultCheck;
		return this;
	}
	
	public RegistryEntryFactory withValue(Object value)
	{
		DebugService.panicOn(check==null||!check.call(value),"The "+
															 "registry "+
															 "entry "+
															 "factory was"+
															 " given an "+
															 "invalid "+
															 "value due "+
															 "to the "+
															 "check "+
															 "functor "+
															 "being "+
															 "invalid!");
		this.currentValue=value;
		return this;
	}
	
	@Override public RegistryEntry collate()
	{
		checkTargets();
		RegistryEntry r=new RegistryEntry()
		{
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
		r.currentValue=this.currentValue;
		return r;
	}
	
	/**
	 * Internal method used to check the most basic properties.
	 * <p>
	 * <strong>This method should be called by child classes to make
	 * sure at least the base properties are met.
	 * </strong>
	 * </p>
	 */
	protected void checkTargets()
	{
		ASSERT(check);
		ASSERT(defaultValueCheck);
		ASSERT(canonicalName);
		if(currentValue==null)
			SharedServices
				.getBool("registry_services.allow_nullable_value")
				.ifPresent(x->{
							   
							   if(currentValue==null)
							   {
								   DebugService.log(
									   DebugService.LogLevel.WARN,
									   "Entry factory["+hashCode()+"] was found to "+
									   "have an initial null value, setting it to "+
									   "the default value!"
								   );
								   currentValue=defaultValueCheck;
							   }
						   }
				);
		
	}
}