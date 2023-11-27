package pkg.exoad.softgradient.core.ui;

import javax.swing.JComponent;

public class UIDelegate< T extends JComponent >
{
      protected T rootDelegate;

      public final T asComponent()
      {
            assert rootDelegate!=null;
            return rootDelegate;
      }
}
