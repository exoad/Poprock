package pkg.exoad.poprock.core.registry;

import pkg.exoad.poprock.core.EventPoolService;
import pkg.exoad.poprock.core.Services;
import pkg.exoad.poprock.core.annotations.ServiceClass;
import pkg.exoad.poprock.core.annotations.VolatileImpl;
import pkg.exoad.poprock.core.debug.DebugService;
import pkg.exoad.poprock.core.debug.DebuggableMixin;
import pkg.exoad.poprock.core.mixins.NamedObjMixin;

import java.util.HashMap;

/**
 * <h2>Regsitry Services</h2>
 *
 * A registry is just a place for global properties to be accessed. Registries follows a
 * similar structure to {@link EventPoolService} where by which children are set based on
 * a registration system. Furthermore, all registries are public.
 *
 * @author Jack Meng
 */
@ServiceClass(requiresArming=true)
public final class RegistryServices
{
	/**
	 * Represents the entirety of the ephemeral registries stored in cache. (Singleton
	 * pattern)
	 */
	private static final HashMap<Integer,EphemeralRegistry> OBJECTS=new HashMap<>();
	private static final HashMap<Integer,BroadcastingRegistry> OBJECTS1=
		new HashMap<>();
	private static boolean armed=false;
	
	private RegistryServices()
	{}
	
	/**
	 * Arms the Registry Services. Should be automatically called by {@link Services}
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
	 * Registers a registry as "Ephemeral".
	 * <p>
	 * If this function panics, it is 99% a source issue.
	 *
	 * @param id The id alloted to this registry in the cache pool. THIS MUST BE UNIQUE OR
	 * ELSE THIS FUNCTION PANICKS!!
	 * @param config The configuration to use for registering this EphemeralRegistry
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
		);
		OBJECTS.put(
			id,
			new EphemeralRegistry(
				config.rootName(),
				config.loadFactor(),
				config.entries()
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
	
}
