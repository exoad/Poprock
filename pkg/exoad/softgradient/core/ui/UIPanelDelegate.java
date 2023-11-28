package pkg.exoad.softgradient.core.ui;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import pkg.exoad.softgradient.core.SharedConstants;

import java.awt.*;
import java.util.Optional;
import java.util.function.Supplier;
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
                        if(SharedConstants.LAYOUT_DEBUG_MODE)
                        {
                              g.setColor(Color.pink);
                              g.fillRect(
                                          0,
                                          0,
                                          getPreferredSize().width,
                                          getPreferredSize().height
                              );
                        }
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

      public UIPanelDelegate withComponentBuilder(Supplier< Optional< UIDelegate< ? > > > components)
      {
            components.get()
                      .ifPresent(c->rootDelegate.add(c.asComponent()));
            return this;
      }
}
