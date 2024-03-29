package pkg.exoad.poprock.core;

import pkg.exoad.poprock.core.annotations.NotVirtual;
import pkg.exoad.poprock.core.annotations.ServiceClass;
import pkg.exoad.poprock.core.annotations.VolatileImpl;
import pkg.exoad.poprock.core.debug.DebugService;
import pkg.exoad.poprock.core.debug.DebuggableMixin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

/**
 * <h2>EventPool</h2> - <strong>The main global event registry for Poprock
 * both the library and app for dispatching events.</strong> It is structured using a Map
 * making it so that listeners can subscribe to a specific payload type (a class/record
 * that implements {@link EventPoolService.EventPayload}).
 * <p>
 * <h3>Events can be dispatched by anyone</h3>, which is a downside to
 * this method, but it proves robust for the most part in terms of ease of use. Especially
 * because there are no special permissions and other shenanigans that goes into just
 * dispatching a simple event.
 * </p>
 * <p>
 * <h3>Listeners must know their EventPool</h3>, this is the only
 * measure that goes into categorizing data passing through. Listeners must know the pool
 * id {@link #registerEventPool(int)} in order to subscribe to it.
 * </p>
 * <p>
 * <h3>Events can be listened to by anyone</h3>, which can be a good or
 * bad. Especially since there could be credentials being passed around, but look, this
 * app is designed for simply creating gradients not being the next solution to fixing
 * some security pandemic.
 * </p>
 * <p>
 * <h3>Listeners must know what they want can or cannot exist</h3>,
 * meaning something that wants to listen to anything in the event pool must know that the
 * event they want to subscribe has to exist first.
 * {@link EventPoolService.EventPool#isRegisteredPayloadID(Class)}
 *
 * <blockquote><pre>
 * EventPool
 * .getPool(1)
 * .attachListener(FooEventPayload.class, () -> {}); // ERROR
 * </pre></blockquote>
 * </p>
 * <em>The above code snipper throws an error is because
 * {@code FooEventPayload} has not been registered to the EventPool by someone
 * {@link #registerEventPool(int)}
 * <p>
 * <h3>Event Pools are finalized</h3>
 * Once a event pool is registered using {@link #registerEventPool(int)}, there is no
 * going back... <em>Unless you use something quirky reflection manipulation (which highly
 * against!).
 * </p>
 * @author Jack Meng
 */
@ServiceClass(requiresArming=false)
public final class EventPoolService
{
	private static final HashMap<Integer,EventPool> OBJECTS=new HashMap<>();
	
	private EventPoolService()
	{
	}
	
	/**
	 * Returns all of the payload types within an existing event payload.
	 * <p>
	 * Has a chance to throw an error because of {@link #getPool(int)}
	 *
	 * @param id identifier of the event pool (int)
	 *
	 * @return A list of all of the types in that event pool
	 */
	public static Optional<Collection<Class<? extends EventPayload>>> inferEventPool(
		int id
	)
	{
		return Optional
			.of(
				getPool(id).payloads
					.keySet()
			);
	}
	
	/**
	 * Retrieves a specific EventPool based on an ID.
	 * <p>
	 * <strong><em>Please note that this function has a chance to panic if the
	 * requested pool is not found!</em></strong>(Class)
	 *
	 * @param id identifier of the event pool (int)
	 *
	 * @return The requested EventPool if found
	 */
	public static EventPool getPool(int id)
	{
		DebugService
			.panicOn(
				!OBJECTS
					.containsKey(id),
				"Failed to locate pool "+id+" because it already exists in the pool!"
			);
		return OBJECTS
			.get(id);
	}
	
	/**
	 * Tries to allocate a new event pool with the specific ID.
	 * <p>
	 * <strong>This function has a chance to panic if the requested ID
	 * to register with already exists!
	 * </strong>This is because of the immutable nature of the EventPool
	 * design.
	 *
	 * @param id identifier of the event pool to register as (int)
	 *
	 * @return identifier (unmodified)
	 */
	public static int registerEventPool(int id)
	{
		DebugService
			.panicOn(
				OBJECTS
					.containsKey(id),
				"Failed to register event "+id+" because it already exists in the pool!"
			);
		OBJECTS
			.put(
				id,
				new EventPool()
			);
		return id;
	}
	
	/**
	 * This is a "marker" interface for all EventPayloads that want to get added to any
	 * kind of EventPool.
	 * <p>
	 * <strong>Why?</strong> This way EventPool knows that the programmer has
	 * acknowledged that the class used a payload interface will be used properly. (AKA
	 * following MVC patterns)
	 *
	 * @author Jack Meng
	 */
	public interface EventPayload
	{
	}
	
	public static final class EventPool
		implements
		DebuggableMixin
	
	{
		
		// Key: Identifier, Value: Pair of Stack of Listeners and the payload
		private final HashMap</*id of the event*/Class<? extends EventPayload/*must
		extend
		marker interface*/>,Pair</*all the listeners*/ArrayList<Runnable>,/*the
				currently held result of this payload*/Object>> payloads=new HashMap<>();
		
		/**
		 * Subscribe to an event payload type
		 *
		 * @param id identifier of the payload type
		 * @param r The listener lambda (function)
		 */
		@VolatileImpl(reason="The suggested listener ID already exists") public void attachListener(
			Class<? extends EventPayload> id,Runnable r
		)
		{
			THROW_NOW_IF(
				!payloads
					.containsKey(id),
				"Listener Attach failed: No such event exists with the given id: "+id
					.getCanonicalName()
			);
			// check performed by DebugService, no need to assure additional tcs
			payloads
				.get(id)
				.first()
				.add(r);
		}
		
		/**
		 * Register an event payload type to this pool.
		 *
		 * @param id identifier of the payload
		 * @param payload initial value (usually null)
		 */
		public void registerEvent(
			Class<? extends EventPayload> id,Object payload
		)
		{
			payloads
				.put(
					id,
					new Pair<>(
						new ArrayList<>(),
						payload
					)
				);
		}
		
		/**
		 * Returns the currently cached value of this payload event type (can be null).
		 *
		 * @param id idenitifier of the payload
		 *
		 * @return the value of the payload
		 *
		 * @see java.lang.Class#cast(Object)
		 */
		@VolatileImpl(reason="Suggested ID does not exist!") public <T extends EventPayload> T getPayload(
			Class<T> id
		)
		{
			THROW_NOW_IF(
				!payloads
					.containsKey(id),
				"Payload fetch failed: No such event exists with the given id: "+id
					.getCanonicalName()
			);
			// check assured by DebugService
			return id
				.cast(
					payloads
						.get(id)
						.second()
				);
		}
		
		/**
		 * Dry runs all of the listeners under an event payload. This should be used
		 * sparingly as the currently cached value could be unexpected or dependent on
		 * when it is run.
		 *
		 * @param <T> identifier type
		 * @param id identifier of the payload
		 */
		@NotVirtual public synchronized <T extends EventPayload> void ping(
			Class<T> id
		)      // should be called sparingly please!!
		{
			THROW_NOW_IF(
				!payloads
					.containsKey(id),
				"Payload fetch failed: No such event exists with the given "+
				"id: "+id
					.getCanonicalName()
			);
			payloads
				.get(id)
				.first()
				.parallelStream()
				.forEachOrdered(Runnable::run);
		}
		
		@NotVirtual public <T extends EventPayload> boolean isRegisteredPayloadID(
			Class<T> id
		)
		{
			// simply invoke for the map's contains key on its registry
			return payloads
				.containsKey(id);
		}
		
		public <T extends EventPayload> void dispatchEvent(
			Class<T> id,T payload
		)
		{
			THROW_NOW_IF(
				payload==null,
				"Dispatch failed: Payload cannot be null"
			);
			THROW_NOW_IF(
				!payloads
					.containsKey(id),
				"Dispatch failed: No such event exists with the given id: "+id
					.getCanonicalName()
			);
			// we dont need another check, if the debuggable_mixin can handle the error correctly
			payloads
				.get(id)
				.second(payload);
			payloads
				.get(id)
				.first()
				.parallelStream()
				.forEachOrdered(Runnable::run);
		}
	}
}
