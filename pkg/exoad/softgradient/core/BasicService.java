package pkg.exoad.softgradient.core;

import java.awt.Color;
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

      public static Color hexToColor(String hex)
      {
            return new Color(
                        Integer.valueOf(
                                    hex.substring(
                                                1,
                                                3
                                    ),
                                    16
                        ),
                        Integer.valueOf(
                                    hex.substring(
                                                3,
                                                5
                                    ),
                                    16
                        ),
                        Integer.valueOf(
                                    hex.substring(
                                                5,
                                                7
                                    ),
                                    16
                        )
            );
      }
}
