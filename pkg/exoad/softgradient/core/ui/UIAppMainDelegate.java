package pkg.exoad.softgradient.core.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import pkg.exoad.softgradient.core.BasicService;
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
            rootDelegate.setLeftComponent(new GradientDisplayChild());
            rootDelegate.setRightComponent(new ControllerDisplayChild());

            rootDelegate.getLeftComponent()
                        .setMinimumSize(
                                    rootDelegate.getLeftComponent()
                                                .getPreferredSize()
                        );
      }

      private static class ControllerDisplayChild
                                                  extends
                                                  JPanel
      {
            private JScrollPane scrollPane;
            private JPanel bottomButtonsPanel;

            public ControllerDisplayChild()
            {
                  setBorder(BorderFactory.createEmptyBorder());
                  scrollPane=new JScrollPane();
                  scrollPane.setBorder(BorderFactory.createEmptyBorder());
                  setLayout(new BorderLayout());
                  add(
                              scrollPane,
                              BorderLayout.CENTER
                  );
                  bottomButtonsPanel=UIPanelDelegate.make()
                                                    .withLayout(
                                                                new FlowLayout(
                                                                            FlowLayout.RIGHT,
                                                                            6,
                                                                            0
                                                                )
                                                    )

                                                    .withComponent(
                                                                UIButtonDelegate.make()
                                                                                .withText("New")
                                                                                .withBackgroundColor(
                                                                                            BasicService.hexToColor(
                                                                                                        SharedConstants.LAF_POPROCK_PRIMARY_2
                                                                                            )
                                                                                )
                                                                                .withAction(
                                                                                            ()->EventPool.dispatchEvent(
                                                                                                        GradientEventPayload.class,
                                                                                                        GradientEventPayload.EMPTY
                                                                                            )
                                                                                )
                                                    )
                                                    .withComponent(
                                                                UIButtonDelegate.make()
                                                                                .withText("Render")
                                                                // TODO: Add action
                                                    )
                                                    .asComponent();
                  add(
                              bottomButtonsPanel,
                              BorderLayout.SOUTH
                  );

            }
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
                  if(EventPool.getPayload(GradientEventPayload.class)!=null&&!EventPool.getPayload(
                              GradientEventPayload.class
                  )
                                                                                       .equals(
                                                                                                   GradientEventPayload.EMPTY
                                                                                       ))
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
                  else
                  {
                        g.setColor(BasicService.hexToColor(SharedConstants.LAF_POPROCK_BG_FG));
                        g.fillRect(
                                    0,
                                    0,
                                    getWidth(),
                                    getHeight()
                        );
                  }
                  g.dispose();
            }
      }
}
