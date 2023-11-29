package pkg.exoad.softgradient.core.ui;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public final class UIControllerDelegateChilds
{
      private UIControllerDelegateChilds()
      {
      }

      public static final class WindowSetupChildBlock
                                                      extends
                                                      UIDelegate< JPanel >
      {
            public WindowSetupChildBlock()
            {
                  super();
                  rootDelegate=new JPanel();
                  rootDelegate.setLayout(
                              new BoxLayout(
                                          rootDelegate,
                                          BoxLayout.Y_AXIS
                              )
                  );
                  rootDelegate.add(
                              UITextFieldDelegate.make()
                                                 .withText("Amogus")
                                                 .withAlignmentX(Alignment.LEFT)
                                                 .asComponent()
                  );

            }
      }
}
