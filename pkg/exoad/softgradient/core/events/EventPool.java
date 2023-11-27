package pkg.exoad.softgradient.core.events;

import java.util.ArrayList;
import java.util.HashMap;

import pkg.exoad.softgradient.core.Pair;

public final class EventPool
{
      private EventPool()
      {
      }

      // Key: Identifier, Value: Pair of ArrayList of Listeners and the payload
      static HashMap< Class< ? extends EventPayload >, Pair< ArrayList< Runnable >, Object > > payloads=new HashMap<>();

      public static void attachListener(Class< ? extends EventPayload > id,Runnable r)
      {
            if(payloads.containsKey(id))
                  payloads.get(id)
                          .first()
                          .add(r);
            else throw new RuntimeException(
                        "Listener Attach failed: No such event exists with the given id: "+id.getCanonicalName()
            );
      }

      public static void registerEvent(Class< ? extends EventPayload > id,Object payload)
      {
            payloads.put(
                        id,
                        new Pair<>(
                                    new ArrayList<>(),
                                    payload
                        )
            );
      }

      public static void dispatchEvent(Class< ? extends EventPayload > id,Object payload)
      {
            if(payload==null)
                  throw new RuntimeException(
                              "Dispatch failed: Payload cannot be null"
                  );
            if(payloads.containsKey(id))
            {
                  payloads.get(id)
                          .second(payload);
                  payloads.get(id)
                          .first()
                          .forEach(
                                      Runnable::run
                          );
            }
            else throw new RuntimeException(
                        "Dispatch failed: No such event exists with the given id: "+id.getCanonicalName()
            );
      }
}
