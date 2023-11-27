package pkg.exoad.softgradient.core;

import java.awt.Color;

public final record GradientColor(ColorObj color,float fraction)
{
      public Color asAwt()
      {
            return color.asAwt();
      }
}
