package pkg.exoad.softgradient.core;

import java.awt.Color;

public final record GradientColor(ColorObj color,float fraction)
{
      public Color asAwt()
      {
            return color
                  .asAwt();
      }

      @Override public String toString()
      {
            return String
                  .format(
                        "ColorObj{color=%s,fraction=%f}",
                        color,
                        fraction
                  );
      }
}
