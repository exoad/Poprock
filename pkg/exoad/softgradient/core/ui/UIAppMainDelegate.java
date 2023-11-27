package pkg.exoad.softgradient.core.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import pkg.exoad.softgradient.core.ColorObj;
import pkg.exoad.softgradient.core.GradientColor;
import pkg.exoad.softgradient.core.SharedConstants;
import pkg.exoad.softgradient.core.events.EventPool;
import pkg.exoad.softgradient.core.events.GradientEventPayload;

public final class UIAppMainDelegate
                                     extends
                                     UIDelegate< JSplitPane >
{

      public UIAppMainDelegate()
      {
            rootDelegate=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            rootDelegate.setDividerLocation(0.5);
            rootDelegate.setResizeWeight(0.0);
            rootDelegate.setContinuousLayout(true);

            rootDelegate.setLeftComponent(
                        UIPadding.wrapAllSides(
                                    new GradientDisplayChild(),
                                    SharedConstants.GRADIENT_WINDOW_PADDING
                        )
                                 .asComponent()
            );
            rootDelegate.setRightComponent(
                        UIPanelDelegate.make()
                                       .withLayout(new BorderLayout())
                                       .withComponent(
                                                   UIButtonDelegate.make()
                                                                   .withText("Random Color")
                                                                   .withAction(
                                                                               ()->EventPool.dispatchEvent(
                                                                                           GradientEventPayload.class,
                                                                                           new GradientEventPayload(
                                                                                                       new GradientColor[] {
                                                                                                                            new GradientColor(
                                                                                                                                        ColorObj.randomColorObj(),
                                                                                                                                        1.0f
                                                                                                                            )
                                                                                                       },
                                                                                                       0.0f,
                                                                                                       0.0f,
                                                                                                       1.0f,
                                                                                                       1.0f
                                                                                           )
                                                                               )
                                                                   )
                                       )
                                       .asComponent()
            );

            rootDelegate.getLeftComponent()
                        .setMinimumSize(
                                    rootDelegate.getLeftComponent()
                                                .getPreferredSize()
                        );
      }

      private static class GradientDisplayChild
                                                extends
                                                JPanel
      {
            public GradientDisplayChild()
            {
                  EventPool.attachListener(
                              GradientEventPayload.class,
                              this::repaint
                  );
            }

            @Override public void paintComponent(Graphics g)
            {
                  super.paintComponent(g);
                  Graphics2D g2=(Graphics2D)g;
                  g2.setRenderingHint(
                              RenderingHints.KEY_ANTIALIASING,
                              RenderingHints.VALUE_ANTIALIAS_ON
                  );
                  if(EventPool.getPayload(GradientEventPayload.class)!=null)
                  {
                        GradientEventPayload e=(GradientEventPayload)EventPool.getPayload(GradientEventPayload.class);
                        g.setColor(
                                    e.colors()[0].color()
                                                 .asAwt()
                        );
                        g.fillRoundRect(
                                    (getWidth()-Math.min(
                                                getWidth(),
                                                getHeight()
                                    ))/2,
                                    (getHeight()-Math.min(
                                                getWidth(),
                                                getHeight()
                                    ))/2,
                                    Math.min(
                                                getWidth(),
                                                getHeight()
                                    ),
                                    Math.min(
                                                getWidth(),
                                                getHeight()
                                    ),
                                    (int)(Math.min(
                                                getWidth(),
                                                getHeight()
                                    )*SharedConstants.ROUND_RECT_ARC),
                                    (int)(Math.min(
                                                getWidth(),
                                                getHeight()
                                    )*SharedConstants.ROUND_RECT_ARC)
                        );
                  }
                  g.dispose();
            }
      }
}
