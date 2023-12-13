package pkg.exoad.softgradient.core.services;

import pkg.exoad.softgradient.core.*;
import pkg.exoad.softgradient.core.annotations.NotVirtual;
import pkg.exoad.softgradient.core.services.mixins.DebuggableAllRequiredNamedFieldsMixin;
import pkg.exoad.softgradient.core.services.mixins.DebuggableMixin;
import pkg.exoad.softgradient.core.services.mixins.NamedObjMixin;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

public final class RegistryServices
{
	private RegistryServices()
	{}
	
	public abstract static class BaseRegistry
		implements
		ITypeInferencing<Collection<Class<? extends RegistryEntry>>>
	{
		abstract RegistryEntry acquireEntry(String name);
		
		abstract void setEntry(
			String name,
			/*covariant*/ RegistryEntry entry
		);
		
		public Object acquireEntryValue(String name)
		{
			return acquireEntry(name).currentValue;
		}
		
		/**
		 * An internal iterable instance (should not be exposed by reflection)
		 *
		 * @param e Consumer
		 */
		protected abstract void forEach(Consumer<Object> e);
		
		@NotVirtual @Override
		public Optional<Collection<Class<? extends RegistryEntry>>> inferTyping()
		{
			HashSet<Class<? extends RegistryEntry>> e=new HashSet<>();
			forEach(x->{
				if(x instanceof RegistryEntry r)
					e.add(r.getClass());
				else
					DebugService.logWarning("An invalid entry: "+(((x.hashCode()/31)<<2)&0xFF)+" was found with a confounding type: "+x
						.getClass()
						.getCanonicalName()+". Expected: "+RegistryEntry.class.getCanonicalName()+" or lower")
					;
			});
			return e.isEmpty()?Optional.empty():Optional.of(e);
		}
	}
	
	/**
	 * Construction method to begin the method chaining pattern for creating a
	 * RegistryEntry
	 *
	 * @return A new registry entry factory
	 *
	 * @see RegistryEntryFactory
	 */
	public static RegistryEntryFactory makeEntry()
	{
		return new RegistryEntryFactory();
	}
	
	/**
	 * <strong>RegistryEntry Factory</strong>
	 * <p>
	 * This class helps to construct a RegistryEntry using method chaining. It
	 * provides all of the necessary fields and construction functionality to
	 * make a working Registry Entry.
	 * </p>
	 *
	 * <strong>To use this class, you must call
	 * {@link RegistryServices#makeEntry()}!</strong>
	 *
	 * @author Jack Meng
	 * @see RegistryEntry
	 * @see RegistryServices#makeEntry()
	 */
	public static final class RegistryEntryFactory
		implements
		ICollatable<RegistryEntry>,
		DebuggableAllRequiredNamedFieldsMixin
	{
		
		private Functor11<Boolean,Object> check;
		private Functor01<Object> setValueFilter;
		private Object defaultValueCheck;
		private String canonicalName;
		
		private RegistryEntryFactory(){}
		
		public RegistryEntryFactory withCheck(Functor11<Boolean,Object> check)
		{
			this.check=check;
			return this;
		}
		
		public RegistryEntryFactory withCanonicalName(String name)
		{
			this.canonicalName=name;
			return this;
		}
		
		public RegistryEntryFactory withSetValueFilter(Functor01<Object> filter)
		{
			this.setValueFilter=filter;
			return this;
		}
		
		public RegistryEntryFactory withDefaultValue(
			Object defaultCheck
		)
		{
			this.defaultValueCheck=defaultCheck;
			return this;
		}
		
		@Override public RegistryEntry collate()
		{
			ASSERT(check);
			ASSERT(setValueFilter);
			ASSERT(defaultValueCheck);
			ASSERT(canonicalName);
			return new RegistryEntry()
			{
				@Override public String getCanonicalName()
				{
					return canonicalName;
				}
				
				@Override public <T> boolean check(T r)
				{
					return check.call(r);
				}
			};
		}
	}
	
	public abstract static class RegistryEntry
		implements Serializable
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
	 * <p>
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
		
		@Override protected void forEach(final Consumer<Object> e)
		{
			leaves
				.keySet()
				.forEach(e);
		}
	}
}
