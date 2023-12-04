package pkg.exoad.softgradient.core.services;

import pkg.exoad.softgradient.core.Offset;
import pkg.exoad.softgradient.core.Size;

import java.awt.Point;
import java.awt.Dimension;

/**
 * AwtMixerService - A utility class designed to convert from common AWT Utility
 * classes to SoftGradient versions. For example, instead of having to import
 * {@link java.awt.Dimension}, you can easily
 * just use SoftGradient's core {@link pkg.exoad.softgradient.core.Size} when
 * importing {@link pkg.exoad.softgradient.core}
 *
 * @author Jack Meng
 */
public final class AwtMixerService
{
      private AwtMixerService()
      {
      }

      /**
       * Converts from an AWT Point class to a SoftGradient Offset class which
       * is basically the same thing.
       *
       * @param point The AWT Point object
       *
       * @return The SoftGradient Offset object
       *
       * @see java.awt.Point
       * @see pkg.exoad.softgradient.core.Offset
       */
      public static Offset convertPointClass(Point point)
      {
            return new Offset(
                  point.x,
                  point.y
            );
      }

      /**
       * Converts from an AWT Dimension class to a SoftGradient Size class.
       *
       * @param dim The AWT Dimension object
       *
       * @return The SoftGradient Size object
       *
       * @see java.awt.Dimension
       * @see pkg.exoad.softgradient.core.Size
       */
      public static Size convertDimensionClass(Dimension dim)
      {
            return new Size(
                  dim.width,
                  dim.height
            );

      }
}
