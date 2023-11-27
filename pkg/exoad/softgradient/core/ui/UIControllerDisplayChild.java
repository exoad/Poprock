package pkg.exoad.softgradient.core.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import pkg.exoad.softgradient.core.SharedConstants;
import pkg.exoad.softgradient.core.events.EventPool;
import pkg.exoad.softgradient.core.events.GradientEventPayload;
import pkg.exoad.softgradient.core.services.ColorService;

class UIControllerDisplayChild
                               extends
                               JPanel
{
      private static final class InnerControllerBlock
                                                      extends
                                                      JPanel
      {
            public static InnerControllerBlock make(String name,UIDelegate< ? > delegate)
            {
                  return new InnerControllerBlock(
                              name,
                              delegate
                  );
            }

            private UIDelegate< ? > delegate;

            private InnerControllerBlock(String name,UIDelegate< ? > delegate)
            {
                  this.delegate=delegate;
                  setBorder(BorderFactory.createEmptyBorder());
                  setLayout(new BorderLayout());
                  add(
                              UILabelDelegate.make(name)
                                             .withForegroundColor(
                                                         ColorService.hexToColor(
                                                                     SharedConstants.LAF_POPROCK_BG_FG
                                                         )
                                             )
                                             .withPadding(4)
                                             .withBackgroundColor(
                                                         ColorService.hexToColor(
                                                                     SharedConstants.LAF_POPROCK_PRIMARY_1
                                                         )
                                             )
                                             .asComponent(),
                              BorderLayout.NORTH
                  );
                  add(
                              delegate.asComponent(),
                              BorderLayout.CENTER
                  );
                  setAlignmentX(Component.LEFT_ALIGNMENT);
                  setAlignmentY(Component.TOP_ALIGNMENT);
                  setAutoscrolls(true);
                  setMaximumSize(
                              new Dimension(
                                          Integer.MAX_VALUE,
                                          getPreferredSize().height
                              )
                  );
                  setDoubleBuffered(true);
            }
      }

      private JScrollPane scrollPane;
      private JPanel scrollContentPane;
      private JPanel bottomButtonsPanel;

      private synchronized void insertNewBlock(InnerControllerBlock block)
      {
            scrollContentPane.add(block);
            scrollContentPane.revalidate();
            scrollContentPane.repaint();
      }

      public UIControllerDisplayChild()
      {
            setBorder(BorderFactory.createEmptyBorder());
            scrollContentPane=new JPanel(true);
            scrollContentPane.setLayout(
                        new BoxLayout(
                                    scrollContentPane,
                                    BoxLayout.Y_AXIS
                        )
            );
            scrollPane=new JScrollPane(scrollContentPane);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getVerticalScrollBar()
                      .setUnitIncrement(SharedConstants.CONTROLLER_SCROLLBAR_UNIT_INCREMENT);
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
                                                                          .withAction(
                                                                                      ()->EventPool.dispatchEvent(
                                                                                                  GradientEventPayload.class,
                                                                                                  GradientEventPayload.EMPTY
                                                                                      )
                                                                          )
                                                                          .withBackgroundColor(
                                                                                      ColorService.hexToColor(
                                                                                                  SharedConstants.LAF_POPROCK_PRIMARY_2
                                                                                      )
                                                                          )

                                              )
                                              .withComponent(
                                                          UIButtonDelegate.make()
                                                                          .withText("TEST")
                                                                          .withAction(()-> {
                                                                                insertNewBlock(
                                                                                            InnerControllerBlock.make(
                                                                                                        "Amogus",
                                                                                                        UIButtonDelegate.make()
                                                                                                                        .withText(
                                                                                                                                    "Yes"
                                                                                                                        )
                                                                                            )
                                                                                );
                                                                          })
                                                          // TODO: Add action
                                              )
                                              .asComponent();
            add(
                        bottomButtonsPanel,
                        BorderLayout.SOUTH
            );

      }
}