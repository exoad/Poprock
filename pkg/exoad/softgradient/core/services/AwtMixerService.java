package pkg.exoad.softgradient.core.services;

import pkg.exoad.softgradient.core.Offset;
import pkg.exoad.softgradient.core.Size;

import java.awt.Point;
import java.awt.Dimension;

public final class AwtMixerService
{
      private AwtMixerService()
      {
      }

      public static Offset convertPointClass(Point point)
      {
            return new Offset(
                  point.x,
                  point.y
            );
      }

      public static Size convertDimensionClass(Dimension dim)
      {
            return new Size(
                  dim.width,
                  dim.height
            );

      }
}
