package pkg.exoad.softgradient.core.services;

import pkg.exoad.softgradient.core.*;
import pkg.exoad.softgradient.core.annotations.NotVirtual;
import pkg.exoad.softgradient.core.annotations.ServiceClass;
import pkg.exoad.softgradient.core.annotations.VolatileImpl;
import pkg.exoad.softgradient.core.services.mixins.DebuggableAllRequiredNamedFieldsMixin;
import pkg.exoad.softgradient.core.services.mixins.DebuggableMixin;
import pkg.exoad.softgradient.core.services.mixins.NamedObjMixin;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

/**
 * <h2>Regsitry Services</h2>
 *
 * A registry is just a place for global properties to be accessed. Registries
 * follows a similar structure to {@link EventPoolService} where by which
 * children are set based on a registration system. Furthermore, all registries
 * are public.
 *
 * @author Jack Meng
 */
@ServiceClass(requiresArming=true)
public final class RegistryServices
{
	private RegistryServices()
	{}
	
	private static boolean armed=false;
	
	/**
	 * Arms the Registry Services. Should be automatically called by
	 * {@link Services}
	 */
	public static synchronized void armService()
	{
		if(!armed) armed=true;
	}
	
	/**
	 * Internal macro method
	 *
	 * @param r Runnable
	 */
	static void runOnArmed(Runnable r)
	{
		if(armed) r.run();
	}
	
	/**
	 * This is the root class used by all of the various implementations of a
	 * Registry. Registries are meant to be collatable groups of properties.
	 *
	 * @author Jack Meng
	 */
	public sealed abstract static class BaseRegistry
		implements
		ITypeInferencing<Collection<Class<? extends RegistryEntry>>>
		permits
		EphemeralRegistry,
		WeakRegistry,
		ExternRegistry
	{
		protected BaseRegistry(){}
		
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
		
		/**
		 * This method is the main entry into "putting" a leaf into a registry.
		 * It acts similar to the registration process used by
		 * {@link EventPoolService}.
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
					DebugService.log(
						DebugService.LogLevel.WARN,
						"An invalid entry: "+(((x.hashCode()/31)<<2)&0xFF)+" was found with a confounding type: "+x
							.getClass()
							.getCanonicalName()+". Expected: "+RegistryEntry.class.getCanonicalName()+" or lower"
					);
			});
			return e.isEmpty()?Optional.empty():Optional.of(e);
		}
	}
	
	/**
	 * <h2>External Registry</h2>
	 * This is a registry that is implemented by a third party or can be
	 * implemented by a third party. It should barely be used as the options
	 * provided by the standard registry services should suffice.
	 *
	 * @author Jack Meng
	 */
	public static non-sealed class ExternRegistry
		extends BaseRegistry
		implements
		NamedObjMixin
	{
		
		@Override protected RegistryEntry acquireEntry(final String name)
		{
			// break base implementation
			return null;
		}
		
		@Override public void registerEntry(String name,RegistryEntry entry)
		{
			// break base implementation
		}
		
		@Override protected void forEach(Consumer<Object> e)
		{
			// break base implementation
		}
		
		@Override public Optional<Collection<Class<? extends RegistryEntry>>> inferTyping()
		{
			// break the default implementation
			return Optional.empty();
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
	public static sealed class RegistryEntryFactory
		implements
		ICollatable<RegistryEntry>,
		DebuggableAllRequiredNamedFieldsMixin
		permits
		DescriptiveRegistryEntryFactory
	{
		protected Functor11<Boolean,Object> check;
		protected Object defaultValueCheck;
		protected String canonicalName;
		
		private RegistryEntryFactory(){}
		
		/**
		 * Named constructor method for setting the checker function. Please
		 * note that the functionality of the checker is mainly used for custom
		 * user implemented checks. For example Integer Bound Checks, these are
		 * not handled by any registries themselves (for the most part).
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
		 * Named constructor method for setting the default value on false
		 * evaluation
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
		}
		
		@Override public RegistryEntry collate()
		{
			checkTargets();
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
				
				@Override public Optional<?> defaultValue()
				{
					return Optional.of(defaultValueCheck);
				}
				
				@Override public void setCurrentValue(Object r)
				{
					if(check(r))
						super.setCurrentValue(r);
				}
			};
		}
	}
	
	/**
	 * The base representation of a Descriptive Registry entry that just has one
	 * internal property of {@code description}. <strong>Please use the Factory
	 * method to construct this object from scratch.
	 * </strong>
	 *
	 * @author Jack Meng
	 * @see DescriptiveRegistryEntryFactory
	 */
	public abstract static class DescriptiveRegistryEntry
		extends RegistryEntry
		implements
		Serializable
	{
		/**
		 * @return The description of this entry
		 */
		public abstract String description();
	}
	
	/**
	 * A Descriptive Registry is one that also possess another property that
	 * presents a user friendly description of this entry. This is extremely
	 * useful for when you are writing a settings page, this part will be able
	 * to automate that styling.
	 *
	 * <p>
	 * This class aims to automate the creation of a regular Descriptive
	 * Registry entry by complying with the named construction.
	 * </p>
	 *
	 * @author Jack Meng
	 * @see RegistryEntryFactory
	 */
	public static final class DescriptiveRegistryEntryFactory
		extends RegistryEntryFactory
	{
		/**
		 * Internal raw representation of the description
		 */
		private String description;
		
		/**
		 * Named construction for the property Description of the Descriptive
		 * Entry class.
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
				
				@Override public <T> boolean check(T r)
				{
					return check.call(r);
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
			};
		}
	}
	
	/**
	 * Base class for a registry entry which describes a certain data type held
	 * in the registry. By default it holds some very routinely used properties
	 * of an entry, but must be overriden if you want the correct
	 * functionalities.
	 *
	 * @author Jack Meng
	 */
	public abstract static class RegistryEntry
		implements Serializable
	{
		protected Object currentValue;
		
		/**
		 * Most likely should be overriden as the default implement just
		 * returns
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
	
	/**
	 * Represents the entirety of the ephemeral registries stored in cache.
	 * (Singleton pattern)
	 */
	private static final HashMap<Integer,EphemeralRegistry> OBJECTS=new HashMap<>();
	
	private static final HashMap<Integer,BroadcastingRegistry> OBJECTS1=
		new HashMap<>();
	
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
	 * @see EphemeralRegistry.EphemeralRegistryConfig
	 */
	@VolatileImpl(reason="Supplied id already exists") public static void registerEphemeralRegistry(
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
	);
	}
	
	/**
	 * Acquires the ephemeral registry found from the pool.
	 * <p>
	 * If this function panics, it is 99% a source issue.
	 * </p>
	 *
	 * @param id The id of the registry to look for
	 *
	 * @return The ephemeral registry that was found under the supplied ID
	 */
	@VolatileImpl(reason="Supplied id does not exist") public static EphemeralRegistry getEphemeral(
		int id
	)
	{
		DebugService.panicOn(!OBJECTS.containsKey(id),"Failed to find a "+
													  "registry with "+id);
		return OBJECTS.get(id);
	}
	
	// TODO: Finish impl
	public static final class BroadcastingRegistry
		implements DebuggableMixin,
				   NamedObjMixin
	{
	
	}
	
	// TODO: Finish impl
	public static final class PrivateStashedRegistry
		implements DebuggableMixin,
				   NamedObjMixin
	{
	
	}
	
	// TODO: Finish impl
	
	/**
	 * <h2>Weak Registry</h2>
	 * A weak registry makes it so that all entries held by the internal hash
	 * table are attributed using a weak reference once that entry has been
	 * accessed AT LEAST ONCE.
	 *
	 * <p>
	 * If the entry has been accessed at least once, it is purely up to when the
	 * next GC phase until that entry disappears.
	 * </p>
	 *
	 * @author Jack Meng
	 */
	public static final class WeakRegistry
		extends BaseRegistry
		implements DebuggableMixin,
				   NamedObjMixin
	{
		private transient HashMap<String,WeakReference<RegistryEntry>> leafs;
		
		@Override protected RegistryEntry acquireEntry(final String name)
		{
			return null;
		}
		
		@Override void registerEntry(
			final String name,final RegistryEntry entry
		)
		{
		
		}
		
		@Override protected void forEach(final Consumer<Object> e)
		{
		
		}
	}
	
	/**
	 * <h2>Ephemeral Registry</h2>
	 *
	 * An EphemeralRegistry is the most basic registry type. By basic meaning
	 * that any functionality that depends on this registry must perform manual
	 * lookup within this registry. This means when a property is changed, no
	 * MVC pattern is implemented by the registry.
	 *
	 * <p>
	 * <em>However, you can get around this by using a registry entry's
	 * internal checker function as a listener. <strong>However, sometimes this
	 * is not</strong> guranteed to be called.
	 * </em>
	 *
	 * <h3>Leaf Validation</h3>
	 * <p>
	 * Leaf validation defines how properties flow within the registry. This
	 * just means what happens when the program wants to set an entry's value to
	 * something else. For the most part, this is quite trivial, we call the
	 * entry's checker function and see what it shows and do the appropriate
	 * operation based on the result. However, Ephemeral Registry also performs
	 * a few optimizations on behalf of the entries themselves. One of the
	 * biggest is basic object equality. If the current entry's value is already
	 * equal to
	 * </p>
	 * </p>
	 */
	public static final class EphemeralRegistry
		extends BaseRegistry
		implements DebuggableMixin,
				   NamedObjMixin,
				   Serializable
	{
		/**
		 * Creates a configuration functionally
		 *
		 * @param rootName REQUIRED - the root node's name
		 * @param loadFactor OPTIONAL - the load factor used for the internal
		 * hashtable for performance optimization. By default this value is 0
		 * .75 (float)
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
		
		@Override public Optional<Collection<Class<? extends RegistryEntry>>> inferTyping()
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
		
		private final HashMap<String,RegistryEntry> leaves;
		
		/**
		 * Initiates a new Ephemeral Registry by using just the rootName
		 *
		 * @param rootName The name to use for this registry (this is
		 * descriptive)
		 */
		private EphemeralRegistry(String rootName)
		{
			this(rootName,0.75F,null);
		}
		
		/**
		 * Initiates a new Ephemeral Reigstry with some more parameters.
		 *
		 * @param rootName The name to use for this registry (this is
		 * descriptive)
		 * @param loadFactor The load factor to use for the internal backing
		 * hash table
		 * @param entries Any default entries that can be emplaced into the
		 * hashtable
		 */
		@VolatileImpl(reason="load factor less than 0") private EphemeralRegistry(
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
		 * Formats the rootName accordingly. If the root name is invalid, this
		 * function throws an exception which is always a programming error.
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
		
		@Override protected void forEach(final Consumer<Object> e)
		{
			leaves
				.keySet()
				.forEach(e);
		}
	}
}
