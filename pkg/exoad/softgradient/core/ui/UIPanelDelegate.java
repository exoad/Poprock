package pkg.exoad.softgradient.core.ui;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.util.function.Consumer;

public final class UIPanelDelegate
                                   extends
                                   UIDelegate< JPanel >
{

      private Consumer< Graphics2D > earlyPaintDelegate;
      private Consumer< Graphics2D > latePaintDelegate;

      public static UIPanelDelegate make()
      {
            return new UIPanelDelegate();
      }

      private UIPanelDelegate()
      {
            rootDelegate=new JPanel()
            {
                  @Override public void paintComponent(Graphics g)
                  {
                        if(earlyPaintDelegate!=null)
                              earlyPaintDelegate.accept((Graphics2D)g);
                        super.paintComponent(g);
                        if(latePaintDelegate!=null)
                              latePaintDelegate.accept((Graphics2D)g);
                  }
            };
      }

      public UIPanelDelegate withBoxLayout(int axis)
      {
            rootDelegate.setLayout(
                        new BoxLayout(
                                    rootDelegate,
                                    axis
                        )
            );
            return this;
      }

      public UIPanelDelegate withLatePaintDelegate(Consumer< Graphics2D > paintDelegate)
      {
            this.latePaintDelegate=paintDelegate;
            return this;
      }

      public UIPanelDelegate withEarlyPaintDelegate(Consumer< Graphics2D > paintDelegate)
      {
            this.earlyPaintDelegate=paintDelegate;
            return this;
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

      public UIPanelDelegate withComponentIf(boolean condition,UIDelegate< ? > component)
      {
            if(condition)
                  rootDelegate.add(component.asComponent());
            return this;
      }
}