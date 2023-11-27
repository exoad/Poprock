package pkg.exoad.softgradient.core.ui;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import java.awt.*;
import java.util.function.Consumer;

public final class UIPanelDelegate
                                   extends
                                   UIDelegate< JPanel >
{

      public static UIPanelDelegate make()
      {
            return new UIPanelDelegate();
      }

      private Consumer< Graphics2D > paintDelegate;

      private UIPanelDelegate()
      {
            rootDelegate=new JPanel()
            {
                  @Override public void paintComponent(Graphics g)
                  {
                        if(paintDelegate!=null)
                              paintDelegate.accept((Graphics2D)g);
                        super.paintComponent(g);
                        g.dispose();
                  }
            };
      }

      public UIPanelDelegate withLayout(LayoutManager layout)
      {
            rootDelegate.setLayout(layout);
            return this;
      }

      public UIPanelDelegate withPaintDelegate(Consumer< Graphics2D > paintDelegate)
      {
            this.paintDelegate=paintDelegate;
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
