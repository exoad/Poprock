package pkg.exoad.softgradient.core.ui;

import java.awt.Color;

import javax.swing.JButton;

import pkg.exoad.softgradient.core.BasicService;
import pkg.exoad.softgradient.core.SharedConstants;

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
            withBackgroundColor(BasicService.hexToColor(SharedConstants.LAF_POPROCK_PRIMARY_1)).withForegroundColor(
                        BasicService.hexToColor(SharedConstants.LAF_POPROCK_BG_FG)
            );
      }

      public UIButtonDelegate withText(String text)
      {
            rootDelegate.setText(text);
            return this;
      }

      public UIButtonDelegate withBackgroundColor(int r,int g,int b)
      {
            rootDelegate.setBackground(
                        new Color(
                                    r,
                                    g,
                                    b
                        )
            );
            return this;
      }

      public UIButtonDelegate withBackgroundColor(Color color)
      {
            rootDelegate.setBackground(color);
            return this;
      }

      public UIButtonDelegate withForegroundColor(int r,int g,int b)
      {
            rootDelegate.setForeground(
                        new Color(
                                    r,
                                    g,
                                    b
                        )
            );
            return this;
      }

      public UIButtonDelegate withForegroundColor(Color color)
      {
            rootDelegate.setForeground(color);
            return this;
      }

      public UIButtonDelegate withAction(Runnable action)
      {
            rootDelegate.addActionListener(e->action.run());
            return this;
      }
}
