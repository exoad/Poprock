package pkg.exoad.softgradient.core.services;

import pkg.exoad.softgradient.core.ITypeInferencing;
import pkg.exoad.softgradient.core.Pair;
import pkg.exoad.softgradient.core.services.mixins.DebuggableMixin;
import pkg.exoad.softgradient.core.services.mixins.NamedObjMixin;

import java.util.*;

public final class RegistryServices
{
	private RegistryServices()
	{}
	
	public abstract static class BaseRegistry
		implements
		ITypeInferencing<Class<? extends RegistryEntry>>
	{
		abstract RegistryEntry acquireEntry(String name);
		
		abstract void setEntry(
			String name,/*covariant
		 */ RegistryEntry entry
		);
		
		public Object acquireEntryValue(String name)
		{
			return acquireEntry(name).currentValue;
		}
	}
	
	public abstract static class RegistryEntry
	{
		private Object currentValue;
		
		/**
		 * Most likely should be overriden as the default implement just
		 * returns
		 * <code>true</code> no matter what happens!
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
		 * Most likely should be overriden as the default implementation does
		 * not use {@link #check(Object)}
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
		 * Retrieves the current value held by this current registry entry.
		 *
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
		
		public Optional<?> defaultValue()
		{
			return Optional.empty();
		}
		
		public abstract String getCanonicalName();
	}
	
	/**
	 * Represents the entirety of the ephemeral registries stored in cache.
	 * (Singleton pattern)
	 */
	private static final HashMap<Integer,EphemeralRegistry> OBJECTS=new HashMap<>();
	
	/**
	 * Registers a registry as "Ephemeral".
	 *
	 * If this function panics, it is 99% a source issue.
	 *
	 * @param id The id alloted to this registry in the cache pool. THIS MUST BE
	 * UNIQUE OR ELSE THIS FUNCTION PANICKS!!
	 * @param config The configuration to use for registering this
	 * EphemeralRegistry
	 *
	 * @return The id alloted
	 *
	 * @see EphemeralRegistry.EphemeralRegistryConfig
	 */
	public static int registerEphemeralRegistry(
		int id,EphemeralRegistry.EphemeralRegistryConfig config
	)
	{
		DebugService.panicOn(
			OBJECTS.containsKey(id),
			"Failed to register a new registry with id: "+id+" because it already exists!"
		); OBJECTS.put(
		id,
		new EphemeralRegistry(
			config.rootName,
			config.loadFactor,
			config.entries
		)
	); return id;
	}
	
	public static final class BroadcastingRegistry
		implements DebuggableMixin,
				   NamedObjMixin
	{
	
	}
	
	/**
	 * <h2>Ephemeral Registry</h2>
	 */
	public static final class EphemeralRegistry
		extends BaseRegistry
		implements DebuggableMixin,
				   NamedObjMixin
	{
		/**
		 * Creates a configuration functionally
		 *
		 * @param rootName REQUIRED - the root node's name
		 * @param loadFactor OPTIONAL - the load factor used for the internal
		 * hashtable for performance optimization. By default this value is 0
		 * .75 (float
		 * @param entries OPTIONAL - default entries to inject into the internal
		 * hashtable
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
		
		/**
		 * Similar to just {@link #makeConfig(String,float,Pair[])} but just
		 * with one <strong>required</strong> parameter.
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
		
		@Override public Optional<Class<? extends RegistryEntry>> inferTyping()
		{
			return Optional.empty();
		}
		
		/**
		 * The configuration styling used for creating an ephemeral registry.
		 *
		 * @param rootName REQUIRED - the root node's name
		 * @param loadFactor OPTIONAL - the load factor used for the internal
		 * hashtable of the registry
		 * @param entries OPTIONAL - any default entries to be injected into the
		 * registry
		 */
		public static final record EphemeralRegistryConfig(
			String rootName,float loadFactor, /*defaultValue=0.75|>0*/
			Pair<String,RegistryEntry>[] entries /*optional AKA
			nullable*/
		)
		{}
		
		private final Hashtable<String,RegistryEntry> leaves;
		
		public EphemeralRegistry(
			String rootName
		)
		{
			this(rootName,0.75F,null);
		}
		
		public EphemeralRegistry(
			String rootName,float loadFactor,
			Pair<String/*leaf name*/,RegistryEntry/*metadata*/>[] entries
		)
		{
			THROW_NOW_IF(
				loadFactor<=0,
				"The load factor for the entry must be "+">0!"
			);
			leaves=new Hashtable<>(entries==null?0:entries.length,loadFactor);
			if(entries!=null)
			{
				for(Pair<String,RegistryEntry> m: entries)
				{
					leaves.put(
						m.first(),
						m.second()
					);
					setObjectName(rootName);
				}
			}
		}
		
		public RegistryEntry acquireEntry(String leafName)
		{
			THROW_NOW_IF(
				!leaves.containsKey(leafName),
				"The supplied "+"leaf key of "+leafName+" was not found in this registry!"
			);
			return leaves
				.get(leafName);
		}
		
		@Override void setEntry(final String name,final RegistryEntry entry)
		{
		
		}
	}
}
