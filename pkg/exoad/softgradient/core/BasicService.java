package pkg.exoad.softgradient.core;

import java.util.Collection;

public final class BasicService
{
      private BasicService()
      {
      }

      public static boolean containsClass(Collection< Class< ? > > classes,Class< ? > clazz)
      {
            for(Class< ? > c : classes)
                  if(c.getCanonicalName()
                      .equals(clazz.getCanonicalName()))
                        return true;
            return false;
      }
}
