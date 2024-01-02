package pkg.exoad.poprock.core.registry;
import net.exoad.annotations.VolatileImpl;
import pkg.exoad.poprock.core.debug.DebugService;
import pkg.exoad.poprock.core.debug.DebuggableMixin;
import pkg.exoad.poprock.core.services.BasicService;
import pkg.exoad.poprock.core.services.mixins.NamedObjMixin;
import pkg.exoad.poprock.core.Pair;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;
/**
 * <h2>Ephemeral Registry</h2>
 *
 * An EphemeralRegistry is the most basic registry type. By basic meaning that any
 * functionality that depends on this registry must perform manual lookup within this
 * registry. This means when a property is changed, no MVC pattern is implemented by the
 * registry.
 *
 * <p>
 * <em>However, you can get around this by using a registry entry's
 * internal checker function as a listener. <strong>However, sometimes this is
 * not</strong> guranteed to be called.
 * </em>
 * <h3>Leaf Validation</h3>
 * <p>
 * Leaf validation defines how properties flow within the registry. This just means what
 * happens when the program wants to set an entry's value to something else. For the most
 * part, this is quite trivial, we call the entry's checker function and see what it shows
 * and do the appropriate operation based on the result. However, Ephemeral Registry also
 * performs a few optimizations on behalf of the entries themselves. One of the biggest is
 * basic object equality. If the current entry's value is already equal to
 * </p>
 * </p>
 */
public final class EphemeralRegistry
	extends BaseRegistry
	implements DebuggableMixin,
			   NamedObjMixin,
			   Serializable
{
	/**
	 * Internal conventions that are preserved
	 */
	private static final CharSequence[] NOT_ALLOWED_SEQUENCES={
		">",
		"<",
		"/",
		"?",
		"\"",
		"'",
		"[",
		"]",
		";",
		":",
		"!"
	};
	/**
	 * The internal representation of all of the properties
	 */
	private final HashMap<String,RegistryEntry> leaves;
	
	/**
	 * Initiates a new Ephemeral Registry by using just the rootName
	 *
	 * @param rootName The name to use for this registry (this is descriptive)
	 */
	private EphemeralRegistry(String rootName)
	{
		this(rootName,0.75F,null);
	}
	
	/**
	 * Initiates a new Ephemeral Reigstry with some more parameters.
	 *
	 * @param rootName The name to use for this registry (this is descriptive)
	 * @param loadFactor The load factor to use for the internal backing hash table
	 * @param entries Any default entries that can be emplaced into the hashtable
	 */
	@VolatileImpl(reason="load factor less than 0") EphemeralRegistry(
		String rootName,float loadFactor,
		Pair<String/*leaf name*/,RegistryEntry/*metadata*/>[] entries
	)
	{
		THROW_NOW_IF(
			loadFactor<=0,
			"The load factor for the entry must be "+">0!"
		);
		leaves=new HashMap<>(entries==null?0:entries.length,loadFactor);
		if(entries!=null)
		{
			for(Pair<String,RegistryEntry> m: entries)
			{
				String name=assertLeafNameFormat(m.first());
				leaves.put(
					name,
					m.second()
				);
			}
		}
	}
	
	/**
	 * Formats the rootName accordingly. If the root name is invalid, this function throws
	 * an exception which is always a programming error.
	 *
	 * @param rootName The rootname
	 *
	 * @return The formatted rootname.
	 */
	@VolatileImpl(
		reason="The supplied rootName contains invalid "+
			   "characters"
	) private static String assertLeafNameFormat(String rootName)
	{
		DebugService.panicOn(
			BasicService.strContains(
				rootName,
				NOT_ALLOWED_SEQUENCES
			),
			"The supplied rootName of \""+rootName+"\" was "+
			"found to contain an invalid character!"
		);
		return rootName.toLowerCase();
	}
	
	/**
	 * Similar to just {@link #makeConfig(String,float,Pair[])} but just with one
	 * <strong>required</strong> parameter.
	 *
	 * @param rootName REQUIRED - the root node's name
	 *
	 * @return the config
	 *
	 * @see #makeConfig(String,float,Pair[])
	 */
	public static EphemeralRegistryConfig makeConfig(String rootName)
	{
		return makeConfig(rootName,0.75F,null);
	}
	
	/**
	 * Creates a configuration functionally
	 *
	 * @param rootName REQUIRED - the root node's name
	 * @param loadFactor OPTIONAL - the load factor used for the internal hashtable for
	 * performance optimization. By default this value is 0 .75 (float)
	 * @param entries OPTIONAL - default entries to inject into the internal hashtable
	 *
	 * @return the config
	 *
	 * @see EphemeralRegistryConfig
	 */
	public static EphemeralRegistryConfig makeConfig(
		String rootName,float loadFactor,
		Pair<String,RegistryEntry>[] entries
	)
	{
		return new EphemeralRegistryConfig(rootName,loadFactor,entries);
	}
	
	@VolatileImpl(reason="The supplied leaf was already registered.") @Override public void registerEntry(
		String name,
		final RegistryEntry entry
	)
	{
		name=assertLeafNameFormat(name);
		THROW_NOW_IF(leaves.containsKey(name),"The supplied leaf "+
											  "key of "+name+"was "+
											  "already found in the registry!");
		leaves.put(name,entry);
	}
	
	@VolatileImpl(reason="The supplied leaf was not found.") @Override protected RegistryEntry acquireEntry(
		String leafName
	)
	{
		String name=assertLeafNameFormat(leafName);
		THROW_NOW_IF(
			!leaves.containsKey(name),
			"The supplied leaf key of "+name+" was not found in this "+
			"registry!"
		);
		return leaves
			.get(name);
	}
	
	@Override public Optional<Collection<Class<? extends RegistryEntry>>> inferTyping()
	{
		return Optional.empty();
	}
	
	@Override protected void forEach(final Consumer<Object> e)
	{
		leaves
			.keySet()
			.forEach(e);
	}
	
	/**
	 * The configuration styling used for creating an ephemeral registry.
	 *
	 * @param rootName REQUIRED - the root node's name
	 * @param loadFactor OPTIONAL - the load factor used for the internal hashtable of the
	 * registry
	 * @param entries OPTIONAL - any default entries to be injected into the registry
	 */
	public record EphemeralRegistryConfig(
		String rootName,float loadFactor, /*defaultValue=0.75|>0*/
		Pair<String,RegistryEntry>[] entries /*optional AKA
			nullable*/
	)
	{}
}