package pkg.exoad.softgradient.core.services;

import pkg.exoad.softgradient.core.Pair;
import pkg.exoad.softgradient.core.services.mixins.DebuggableMixin;
import pkg.exoad.softgradient.core.services.mixins.NamedObjMixin;
import pkg.exoad.softgradient.core.TreeMap;

import java.nio.channels.ByteChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Function;

public final class RegistryServices
{
   private RegistryServices()
   {}

   private static final HashMap<Integer,EphemeralRegistry> OBJECTS=new HashMap<>();

   public static int registerRegistry(int id)
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
      private LinkedList<Pair</*node tag*/String,TreeMap</*node colored pointer*/? extends ByteChannel,Pair</*validator*/Function<Object,Boolean>,/*held value*/Object>>>> nodes;

      public EphemeralRegistry()
      {
         nodes=new LinkedList<>();
         nodes
            .addFirst(
               Pair
                  .make(getObjectName(),new TreeMap<>())
            );
      }
   }
}
