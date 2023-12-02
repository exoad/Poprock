package pkg.exoad.softgradient.core.ui;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import pkg.exoad.softgradient.core.Offset;
import pkg.exoad.softgradient.core.services.BasicService;

public final class UIPopupItemChilds
{
      public static void showPopupMenuDelegate(UIDelegate< ? extends JPopupMenu > popDelegate,Offset offsetLocal)
      {
            popDelegate.asComponent()
                       .show(
                                   popDelegate.asComponent()
                                              .getInvoker(),
                                   offsetLocal.x(),
                                   offsetLocal.y()
                       );
      }

      private UIPopupItemChilds()
      {
      }

      public static record SimplePopupDelegate(String name,Runnable action)
      {
            public SimplePopupDelegate
            {
                  BasicService.requireAllNonNull(
                              name,
                              action
                  );
            }
      }

      public static record PopupConfiguration(String name,boolean borderPainted,JComponent invoker)
      {

      }
}
