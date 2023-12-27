package pkg.exoad.poprock.core.registry;
import java.io.Serializable;
import java.util.Optional;
/**
 * Base class for a registry entry which describes a certain data type held in the
 * registry. By default it holds some very routinely used properties of an entry, but must
 * be overriden if you want the correct functionalities.
 *
 * @author Jack Meng
 */
public abstract class RegistryEntry
	implements Serializable
{
	protected Object currentValue;
	
	/**
	 * Most likely should be overriden as the default implement just returns
	 * <code>true</code> no matter what happens!
	 * public
	 *
	 * @param r The value to check against
	 * @param <T> Object type
	 *
	 * @return true or false if the provided value is valid for this entry
	 */
	public <T> boolean check(T r)
	{
		return true;
	}
	
	/**
	 * Retrieves the current value held by this current registry entry.
	 * <p>
	 * <strong>This most likely does not have to be overriden!</strong>
	 *
	 * @return The current value held
	 *
	 * @see #currentValue
	 */
	public Object getCurrentValue()
	{
		return currentValue;
	}
	
	/**
	 * Most likely should be overriden as the default implementation does not use
	 * {@link #check(Object)}
	 *
	 * @param r The value to set to
	 * @param <T> Object type
	 *
	 * @see #check(Object)
	 */
	public <T> void setCurrentValue(T r)
	{
		currentValue=r;
	}
	
	/**
	 * Returns the default value held by this entry. This can be nullable
	 *
	 * @return The default value wrapped in an {@link java.util.Optional}
	 */
	public Optional<?> defaultValue()
	{
		return Optional.empty();
	}
	
	public abstract String getCanonicalName();
}