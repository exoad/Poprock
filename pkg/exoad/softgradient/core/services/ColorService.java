package pkg.exoad.softgradient.core.services;

import java.awt.Color;

public final class ColorService
{
      private ColorService()
      {
      }

      public static Color hexToColor(String hex)
      {
            return new Color(
                  Integer
                        .valueOf(
                              hex
                                    .substring(
                                          1,
                                          3
                                    ),
                              16
                        ),
                  Integer
                        .valueOf(
                              hex
                                    .substring(
                                          3,
                                          5
                                    ),
                              16
                        ),
                  Integer
                        .valueOf(
                              hex
                                    .substring(
                                          5,
                                          7
                                    ),
                              16
                        )
            );
      }

      public static Color invertColor(Color r)
      {
            return new Color(
                  255-r
                        .getRed(),
                  255-r
                        .getGreen(),
                  255-r
                        .getBlue()
            );
      }
}
