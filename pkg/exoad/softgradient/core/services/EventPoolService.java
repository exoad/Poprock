package pkg.exoad.softgradient.core.services;

import java.util.*;

import pkg.exoad.softgradient.core.Pair;
import pkg.exoad.softgradient.core.annotations.ServiceClass;
import pkg.exoad.softgradient.core.services.mixins.DebuggableMixin;
import pkg.exoad.softgradient.core.annotations.NotVirtual;

/**
 * <h2>EventPool</h2> - <strong>The main global event registry for SoftGradient
 * both the library and app for dispatching events.</strong> It is structured
 * using a Map making it so that listeners can subscribe to a specific payload
 * type (a class/record that implements
 * {@link pkg.exoad.softgradient.core.services.EventPoolService.EventPayload}).
 * <p>
 * <h3>Events can be dispatched by anyone</h3>, which is a downside to
 * this method, but it proves robust for the most part in terms of ease of use.
 * Especially because there are no special permissions and other shenanigans
 * that goes into just dispatching a simple event.
 * <p>
 * <h3>Listeners must know their EventPool</h3>, this is the only
 * measure that goes into categorizing data passing through. Listeners must know
 * the pool id {@link #registerEventPool(int)} in order to subscribe to it.
 * <p>
 * <h3>Event Pools are ephemeral</h3>. If you are using SoftGradient as
 * a library and never call
 * {@link pkg.exoad.softgradient.app.AppEntry#main(String[])}, your {@code 1}
 * event pool might not be the same as the one found in the default SoftGradient
 * app. For example, in the SoftGradient App, event pool {@code 1} represents a
 * singular pipeline for deploying colors. While event pool {@code 2} represents
 * a singular pipeline for passing through commands and other non color related
 * operations to listeners.
 * <p>
 * <h3>Events can be listened to by anyone</h3>, which can be a good or
 * bad. Especially since there could be credentials being passed around, but
 * look, this app is designed for simply creating gradients not being the next
 * solution to fixing some security pandemic.
 * <p>
 * <h3>Listeners must know what they want can or cannot exist</h3>,
 * meaning something that wants to listen to anything in the event pool must
 * know that the event they want to subscribe has to exist first.
 * {@link
 * pkg.exoad.softgradient.core.services.EventPoolService.EventPool#isRegisteredPayloadID(Class)}
 *
 * <blockquote><pre>
 * EventPool
 * .getPool(1)
 * .attachListener(FooEventPayload.class, () -> {}); // ERROR
 * </pre></blockquote>
 *
 * <em>The above code snipper throws an error is because
 * {@code FooEventPayload} has not been registered to the EventPool by someone
 * {@link #registerEventPool(int)}
 * <p>
 * <h3>Event Pools are finalized</h3>
 * Once a event pool is registered using {@link #registerEventPool(int)}, there
 * is no going back... <em>Unless you use something quirky reflection
 * manipulation (which highly against!).
 *
 * @author Jack Meng
 */
@ServiceClass(requiresArming=false)
public final class EventPoolService
{
	private EventPoolService()
	{
	}
	
	/**
	 * This is a "marker" interface for all EventPayloads that want to get added
	 * to any kind of EventPool.
	 * <p>
	 * <strong>Why?</strong> This way EventPool knows that the programmer has
	 * acknowledged that the class used a payload interface will be used
	 * properly. (AKA following MVC patterns)
	 *
	 * @author Jack Meng
	 */
	public interface EventPayload
	{
	}
	
	private static final HashMap<Integer,EventPool> OBJECTS=new HashMap<>();
	
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
	
	public static final class EventPool
		implements
		DebuggableMixin
	
	{
		
		// Key: Identifier, Value: Pair of Stack of Listeners and the payload
		private HashMap</*id of the event*/Class<? extends EventPayload/*must
		extend
		marker interface*/>,Pair</*all the listeners*/PriorityQueue<Runnable>,/*the
		currently held result of this payload*/Object>> payloads=new HashMap<>();
		
		/**
		 * Subscribe to an event payload type
		 *
		 * @param id identifier of the payload type
		 * @param r The listener lambda (function)
		 */
		public void attachListener(
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
						new PriorityQueue<>(),
						payload
					)
				);
		}
		
		/**
		 * Returns the currently cached value of this payload event type (can be
		 * null).
		 *
		 * @param id idenitifier of the payload
		 *
		 * @return the value of the payload
		 *
		 * @see java.lang.Class#cast(Object)
		 */
		public <T extends EventPayload> T getPayload(Class<T> id)
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
		 * Dry runs all of the listeners under an event payload. This should be
		 * used sparingly as the currently cached value could be unexpected or
		 * dependent on when it is run.
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
				"Payload fetch failed: StackNo such event exists with the given id: "+id
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
