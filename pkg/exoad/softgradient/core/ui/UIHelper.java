package pkg.exoad.softgradient.core.ui;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import java.awt.Color;

public final class UIHelper
{
      private UIHelper()
      {
      }

      public static Border makeLinedBorder(Color color)
      {
            return BorderFactory.createLineBorder(
                        color,
                        1
            );
      }

      public static Border makeLinedBorder(Color color,int thickness)
      {
            return BorderFactory.createLineBorder(
                        color,
                        thickness
            );
      }

      public static Border makeEmptyBorder()
      {
            return BorderFactory.createEmptyBorder();
      }
}
