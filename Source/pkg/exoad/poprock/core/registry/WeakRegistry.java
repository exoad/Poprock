package pkg.exoad.poprock.core.registry;
import net.exoad.annotations.VolatileImpl;
import pkg.exoad.poprock.core.debug.DebugService;
import pkg.exoad.poprock.core.debug.DebuggableMixin;
import pkg.exoad.poprock.core.mixins.NamedObjMixin;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
/**
 * <h2>Weak Registry</h2>
 * A weak registry makes it so that all entries held by the internal hash table are
 * attributed using a weak reference once that entry has been accessed AT LEAST ONCE.
 *
 * <p>
 * If the entry has been accessed at least once, it is purely up to when the next GC phase
 * until that entry disappears.
 * </p>
 * <h2>THIS IS A WORK IN PROGRESS</h2>
 *
 * @author Jack Meng
 */
public final class WeakRegistry
	extends BaseRegistry
	implements DebuggableMixin,
			   NamedObjMixin
{
	private final transient HashMap<Long,WeakReference<RegistryEntry>> leafs;
	
	public WeakRegistry(String rootName)
	{
		setObjectName(rootName);
		leafs=new HashMap<>();
	}
	
	@Override public synchronized void registerEntry(
		final String name,final RegistryEntry entry
	)
	{
		long id=UUID
			.fromString(name)
			.getLeastSignificantBits();
		THROW_NOW_IF(leafs.containsKey(id)&&leafs
						 .get(id)
						 .refersTo(entry)
			,"The supplied entry was found to be bound to the same object"+
			 " reference ["+((entry.hashCode()<<31)&0xFF)+" clashes with "+((Objects
																				 .requireNonNull(
																					 leafs
																						 .get(
																							 id)
																						 .get())
																				 .hashCode()<<31)&0xFF)+"]");
		leafs.put(id,new WeakReference<>(entry));
	}
	
	@Override protected RegistryEntry acquireEntry(final String name)
	{
		DebugService.panicNow("A WEAK REGISTRY DOES NOT HAVE A VALID "+
							  "FULFILLMENT FUNCTION");
		return null;
	}
	
	@VolatileImpl(
		reason="The computed hash of the name can be "+
			   "nonexistent"
	) @Override public synchronized Object acquireEntryValue(String name)
	{
		long id=UUID
			.fromString(name)
			.getLeastSignificantBits();
		THROW_NOW_IF(
			!leafs.containsKey(id),
			"The supplied name "+name+
			"does not exist in this "+
			"registry!"
		);
		return leafs
			.get(id)
			.get();
	}
	
	@Override protected void forEach(final Consumer<Object> e)
	{
	
	}
	
	public boolean isBound(long name)
	{
		return true;
		// no impl
	}
}