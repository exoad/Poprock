package pkg.exoad.poprock.core.registry;
import net.exoad.annotations.NotVirtual;
import pkg.exoad.poprock.core.ITypeInferencing;
import pkg.exoad.poprock.core.debug.DebugService;
import pkg.exoad.poprock.core.services.EventPoolService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Consumer;
/**
 * This is the root class used by all of the various implementations of a Registry.
 * Registries are meant to be collatable groups of properties.
 *
 * @author Jack Meng
 */
public sealed abstract class BaseRegistry
	implements
	ITypeInferencing<Collection<Class<? extends RegistryEntry>>>
	permits ExternRegistry,
			EphemeralRegistry,
			WeakRegistry
{
	protected BaseRegistry(){}
	
	/**
	 * This method is the main entry into "putting" a leaf into a registry. It acts
	 * similar to the registration process used by {@link EventPoolService}.
	 *
	 * @param name The name of the leaf
	 * @param entry The base modal of the leaf (property)
	 *
	 * @see RegistryEntry
	 */
	abstract void registerEntry(
		String name,
		/*covariant*/ RegistryEntry entry
	);
	
	public String acquireEntryCanonicalName(String name)
	{
		return acquireEntry(name).getCanonicalName();
	}
	
	/**
	 * Returns the internal representation of a leaf.
	 * <p>
	 * <em>Note that this method must handle an invalid name</em>
	 * </p>
	 *
	 * @param name The name of the leaf to look up
	 *
	 * @return The RegistryEntry
	 */
	protected abstract RegistryEntry acquireEntry(String name);
	
	public Object acquireEntryValue(String name)
	{
		return acquireEntry(name).currentValue;
	}
	
	@NotVirtual /*probably not a good suggestion other than
		mixins*/ @Override
	public Optional<Collection<Class<? extends RegistryEntry>>> inferTyping()
	{
		HashSet<Class<? extends RegistryEntry>> e=new HashSet<>();
		forEach(x->{
			if(x instanceof RegistryEntry r)
				e.add(r.getClass());
			else
				DebugService.log(
					DebugService.LogLevel.WARN,
					"An invalid entry: "+(((x.hashCode()/31)<<2)&0xFF)+" was found with a confounding type: "+x
						.getClass()
						.getCanonicalName()+". Expected: "+RegistryEntry.class.getCanonicalName()+" or lower"
				);
		});
		return e.isEmpty()?Optional.empty():Optional.of(e);
	}
	
	/**
	 * An internal iterable instance (should not be exposed by reflection)
	 *
	 * @param e Consumer
	 */
	protected abstract void forEach(Consumer<Object> e);
}
