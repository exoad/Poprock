package pkg.exoad.softgradient.core.services;

import pkg.exoad.softgradient.core.Pair;
import pkg.exoad.softgradient.core.services.mixins.DebuggableMixin;
import pkg.exoad.softgradient.core.services.mixins.NamedObjMixin;
import pkg.exoad.softgradient.core.TreeMap;

import java.nio.channels.ByteChannel;
import java.util.*;
import java.util.function.Function;

public final class RegistryServices
{
	private RegistryServices()
	{}
	
	public abstract static class RegistryEntry
	{
		public <T> boolean check(T r)
		{
			return true;
		}
		
		public Optional<?> defaultValue()
		{
			return Optional.empty();
		}
		
		public abstract String getCanonicalName();
	}
	
	private static final HashMap<Integer,EphemeralRegistry> OBJECTS=new HashMap<>();
	
	public static int registerEphemeralRegistry(int id)
	{
		DebugService
			.panicOn(
				OBJECTS
					.containsKey(id),
				"Failed to register a new registry with id: "+id+" because it already exists!"
			);
		OBJECTS
			.put(id,new EphemeralRegistry());
		return id;
	}
	
	public static final class EphemeralRegistry
		implements
		DebuggableMixin,
		NamedObjMixin
	{
		private final Hashtable<String /*leaf name*/,Pair<?
			extends RegistryEntry
			/*metadata*/,Object
			/*currentValue
			 */>> leaves;
		
		public EphemeralRegistry(String rootName)
		{
			this(rootName,0.75F,null);
		}
		
		public EphemeralRegistry(
			String rootName,float loadFactor,
			Pair<String/*leaf name*/,? extends RegistryEntry/*metadata*/>[] entries
		)
		{
			THROW_NOW_IF(loadFactor<=0,"The load factor for the entry must be "+
									   ">0!");
			leaves=new Hashtable<>(entries==null?0:entries.length,loadFactor);
			if(entries!=null)
				for(Pair<String,? extends RegistryEntry> m: entries)
					leaves.put(
						m.first(),
						Pair.make(
							m.second(),
							m
								.second()
								.defaultValue()
						)
					);
			setObjectName(rootName);
		}
		
		public RegistryEntry acquireEntry(String leafName)
		{
			THROW_NOW_IF(!leaves.containsKey(leafName),"The supplied "+
													   "leaf key of "+leafName+" was not found in this registry!");
			return leaves
				.get(leafName)
				.first();
		}
	}
}
