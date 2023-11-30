package pkg.exoad.softgradient.core.ui;

import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.Box;

import pkg.exoad.softgradient.core.services.DebugService;

import javax.swing.BoxLayout;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.util.function.Consumer;

public final class UIPanelDelegate
                                   extends
                                   UIDelegate< JPanel >
{

      public static final class UIHorizontalPanelDelegate
                                                          extends
                                                          UIDelegate< JPanel >
                                                          implements
                                                          UIDelegate.UIStrutContainedDelegate< UIHorizontalPanelDelegate >
      {
            int strutSize=0;
            private UIDelegate< ? > leftSide;
            private UIDelegate< ? > rightSide;

            public static UIHorizontalPanelDelegate make()
            {
                  return new UIHorizontalPanelDelegate();
            }

            private UIHorizontalPanelDelegate()
            {
                  rootDelegate=UIPanelDelegate.make()
                                              .withBoxLayout(BoxLayoutAlignment.X_AXIS)
                                              .asComponent();
            }

            public UIHorizontalPanelDelegate withRightComponent(UIDelegate< ? > rightSide)
            {
                  this.rightSide=rightSide;
                  return this;
            }

            public UIHorizontalPanelDelegate withLeftComponent(UIDelegate< ? > leftSide)
            {
                  this.leftSide=leftSide;
                  return this;
            }

            public UIHorizontalPanelDelegate withBackgroundColor(Color color)
            {
                  rootDelegate.setBackground(color);
                  return this;
            }

            @Override public JPanel asComponent()
            {
                  DebugService.throwIf(
                              leftSide==null,
                              "HorizontalPanelDelegate["+hashCode()+"] received a NULL leftSide component!"
                  );
                  DebugService.throwIf(
                              rightSide==null,
                              "HorizontalPanelDelegate["+hashCode()+"] received a NULL rightSide component!"
                  );
                  rootDelegate.add(leftSide.asComponent());
                  if(strutSize>=0)
                        rootDelegate.add(Box.createHorizontalStrut(strutSize));
                  rootDelegate.add(rightSide.asComponent());
                  return super.asComponent();
            }

            @Override public UIHorizontalPanelDelegate withStrut(int strut)
            {
                  DebugService.throwIf(
                              strut<0,
                              getNamedThis()+" received a strutSize<0"
                  );
                  this.strutSize=strut;
                  return this;
            }
      }

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

      public enum BoxLayoutAlignment
      {
            Y_AXIS(BoxLayout.Y_AXIS),

            X_AXIS(BoxLayout.X_AXIS),

            LINE_AXIS(BoxLayout.LINE_AXIS),

            PAGE_AXIS(BoxLayout.PAGE_AXIS);

            final int axis;

            BoxLayoutAlignment(int axis)
            {
                  this.axis=axis;
            }
      }

      public enum FlowLayoutAlignment
      {
            LEFT(FlowLayout.LEFT),

            LEADING(FlowLayout.LEADING),

            TRAILING(FlowLayout.TRAILING),

            CENTER(FlowLayout.CENTER),

            RIGHT(FlowLayout.RIGHT);

            final int axis;

            FlowLayoutAlignment(int axis)
            {
                  this.axis=axis;
            }
      }

      public UIPanelDelegate withBoxLayout(BoxLayoutAlignment axis)
      {
            rootDelegate.setLayout(
                        new BoxLayout(
                                    rootDelegate,
                                    axis.axis
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

      public UIPanelDelegate withGridLayout(int rows,int cols)
      {
            return withLayout(
                        new GridLayout(
                                    rows,
                                    cols
                        )
            );
      }

      public UIPanelDelegate withFlowLayout(FlowLayoutAlignment alignment,int hgap,int vgap)
      {
            DebugService.throwIf(
                        hgap<0,
                        getNamedThis()+" received hgap<0 for withFlowLayout"
            );
            DebugService.throwIf(
                        vgap<0,
                        getNamedThis()+" received hgap<0 for withFlowLayout"
            );
            return withLayout(
                        new FlowLayout(
                                    alignment.axis,
                                    hgap,
                                    vgap
                        )
            );
      }

      public UIPanelDelegate withComponent(UIBasicDelegate< ? > components)
      {
            rootDelegate.add(components.asComponent());
            return this;
      }

      public UIPanelDelegate withComponent(UIBasicDelegate< ? > components,Object constraints)
      {
            rootDelegate.add(
                        components.asComponent(),
                        constraints
            );
            return this;
      }

      public UIPanelDelegate withComponentIf(boolean condition,UIBasicDelegate< ? > component)
      {
            if(condition)
                  rootDelegate.add(component.asComponent());
            return this;
      }
}