package pkg.exoad.softgradient.core.ui;

import javax.swing.*;
import java.awt.*;

public class UILabelDelegate
                             extends
                             UIDelegate< JLabel >
{
      public static UILabelDelegate make(String text)
      {
            return new UILabelDelegate(text);
      }

      private UILabelDelegate(String text)
      {
            rootDelegate=new JLabel(text);
      }

      public UILabelDelegate withText(String text)
      {
            rootDelegate.setText(text);
            return this;
      }

      public UILabelDelegate withForegroundColor(Color color)
      {
            rootDelegate.setForeground(color);
            return this;
      }

      public UILabelDelegate withBackgroundColor(Color color)
      {
            rootDelegate.setBackground(color);
            return this;
      }
}
