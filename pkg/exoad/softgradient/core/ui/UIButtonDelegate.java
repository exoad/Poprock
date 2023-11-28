package pkg.exoad.softgradient.core.ui;

import java.awt.Color;

import javax.swing.JButton;

import pkg.exoad.softgradient.core.SharedConstants;
import pkg.exoad.softgradient.core.services.ColorService;

public final class UIButtonDelegate
                                    extends
                                    UIDelegate< JButton >
{
      public static UIButtonDelegate make()
      {
            return new UIButtonDelegate();
      }

      private UIButtonDelegate()
      {
            rootDelegate=new JButton();
            rootDelegate.setFocusPainted(false);
            rootDelegate.setBorderPainted(false);
            withForegroundColor(
                        ColorService.hexToColor(
                                    SharedConstants.LAF_POPROCK_BG_FG
                        )
            ).withBackgroundColor(
                        ColorService.hexToColor(
                                    SharedConstants.LAF_POPROCK_PRIMARY_1
                        )
            );
      }

      public UIButtonDelegate withText(String text)
      {
            rootDelegate.setText(text);
            return this;
      }

      public UIButtonDelegate withForegroundColor(Color color)
      {
            rootDelegate.setForeground(color);
            return this;
      }

      public UIButtonDelegate withBackgroundColor(Color color)
      {
            rootDelegate.setBackground(color);
            return this;
      }

      public UIButtonDelegate withAction(Runnable action)
      {
            rootDelegate.addActionListener(e->action.run());
            return this;
      }
}
