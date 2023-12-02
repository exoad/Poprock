package pkg.exoad.softgradient.core.events;

import java.util.ArrayList;
import java.util.HashMap;

import pkg.exoad.softgradient.core.Pair;
import pkg.exoad.softgradient.core.services.DebugService;
import pkg.exoad.softgradient.core.services.mixins.DebuggableMixin;

public final class EventPool
                             implements
                             DebuggableMixin
{
      protected EventPool()
      {
      }

      public static final HashMap<Integer,EventPool> OBJECTS=new HashMap<>();

      public static long registerEventPool(int id)
      {
            DebugService
                  .throwIf(
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

      // Key: Identifier, Value: Pair of ArrayList of Listeners and the payload
      HashMap<Class<? extends EventPayload>,Pair<ArrayList<Runnable>,Object>> payloads=new HashMap<>();

      public void attachListener(Class<? extends EventPayload> id,Runnable r)
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

      public void registerEvent(Class<? extends EventPayload> id,Object payload)
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

      public Object getPayload(Class<? extends EventPayload> id)
      {
            THROW_NOW_IF(
                  !payloads
                        .containsKey(id),
                  "Payload fetch failed: No such event exists with the given id: "+id
                        .getCanonicalName()
            );
            // check assured by DebugService
            return payloads
                  .get(id)
                  .second();
      }

      // should be called sparingly please!!
      public final synchronized <T extends EventPayload> void ping(Class<T> id)
      {
            THROW_NOW_IF(
                  !payloads
                        .containsKey(id),
                  "Payload fetch failed: No such event exists with the given id: "+id
                        .getCanonicalName()
            );
            payloads
                  .get(id)
                  .first()
                  .forEach(Runnable::run);
      }

      public <T extends EventPayload> void dispatchEvent(Class<T> id,T payload)
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
                  .forEach(
                        Runnable::run
                  );
      }
}
