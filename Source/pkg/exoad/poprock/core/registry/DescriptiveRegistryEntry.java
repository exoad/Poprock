package pkg.exoad.poprock.core.registry;

import java.io.Serializable;
/**
 * The base representation of a Descriptive Registry entry that just has one internal
 * property of {@code description}. <strong>Please use the Factory method to construct
 * this object from scratch.
 * </strong>
 *
 * @author Jack Meng
 * @see DescriptiveRegistryEntryFactory
 */
abstract class DescriptiveRegistryEntry
	extends RegistryEntry
	implements
	Serializable
{
	/**
	 * @return The description of this entry
	 */
	public abstract String description();
}