package pkg.exoad.softgradient.core.ui;

import java.awt.LayoutManager;

import javax.swing.JPanel;

public final class UIPanelDelegate
                                   extends
                                   UIDelegate< JPanel >
{

      public static UIPanelDelegate make()
      {
            return new UIPanelDelegate();
      }

      private UIPanelDelegate()
      {
            rootDelegate=new JPanel();
      }

      public UIPanelDelegate withLayout(LayoutManager layout)
      {
            rootDelegate.setLayout(layout);
            return this;
      }

      public UIPanelDelegate withComponent(UIDelegate< ? > components)
      {
            rootDelegate.add(components.asComponent());
            return this;
      }

      public UIPanelDelegate withComponent(UIDelegate< ? > components,Object constraints)
      {
            rootDelegate.add(
                        components.asComponent(),
                        constraints
            );
            return this;
      }

}
