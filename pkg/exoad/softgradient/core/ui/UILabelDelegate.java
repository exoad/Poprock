package pkg.exoad.softgradient.core.ui;

import pkg.exoad.softgradient.core.ColorObj;
import pkg.exoad.softgradient.core.ui.java.UXLabel;

public class UILabelDelegate
                             extends
                             UIDelegate<UXLabel>
{
      public static UILabelDelegate make(String text)
      {
            return new UILabelDelegate(text);
      }

      private UILabelDelegate(String text)
      {
            rootDelegate=new UXLabel(text);
      }

      public UILabelDelegate withText(String text)
      {
            rootDelegate
                  .setText(text);
            return this;
      }

      public UILabelDelegate withForegroundColor(ColorObj color)
      {
            rootDelegate
                  .setFgColor(color);
            return this;
      }
}
