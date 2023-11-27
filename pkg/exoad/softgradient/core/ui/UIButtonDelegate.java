package pkg.exoad.softgradient.core.ui;

import javax.swing.JButton;

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
      }

      public UIButtonDelegate withText(String text)
      {
            rootDelegate.setText(text);
            return this;
      }

      public UIButtonDelegate withAction(Runnable action)
      {
            rootDelegate.addActionListener(e->action.run());
            return this;
      }
}
