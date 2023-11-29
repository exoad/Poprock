package pkg.exoad.softgradient.core.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Component;
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

            private InnerControllerBlock(String name,UIDelegate< ? > delegate)
            {
                  setBorder(BorderFactory.createEmptyBorder());
                  setLayout(new BorderLayout());
                  add(

                              UIPanelDelegate.make()
                                             .withEarlyPaintDelegate(
                                                         g-> {
                                                               g.setColor(
                                                                           ColorService.hexToColor(
                                                                                       SharedConstants.LAF_POPROCK_PRIMARY_1
                                                                           )
                                                               );
                                                               g.fillRect(
                                                                           0,
                                                                           0,
                                                                           getPreferredSize().width,
                                                                           getPreferredSize().height
                                                               );
                                                         }
                                             )
                                             .withComponent(
                                                         UILabelDelegate.make(name)
                                                                        .withForegroundColor(
                                                                                    ColorService.hexToColor(
                                                                                                SharedConstants.LAF_POPROCK_PRIMARY_1
                                                                                    )
                                                                        )
                                                                        .withPadding(4)
                                             )
                                             .asComponent(),
                              BorderLayout.NORTH
                  );
                  add(
                              delegate.asComponent(),
                              BorderLayout.CENTER
                  );

            }
      }

      private void insertNewBlock(InnerControllerBlock block)
      {
            block.setAlignmentX(Component.CENTER_ALIGNMENT);
            block.setAlignmentY(Component.TOP_ALIGNMENT);
            block.setMaximumSize(
                        new java.awt.Dimension(
                                    Integer.MAX_VALUE,
                                    block.getPreferredSize().height
                        )
            );
            blocksPanel.add(block);
            scrollPane.revalidate();
            scrollPane.repaint();
      }

      private JPanel blocksPanel;
      private JScrollPane scrollPane;
      private JPanel bottomButtonsPanel;

      public UIControllerDisplayChild()
      {
            blocksPanel=UIPanelDelegate.make()
                                       .withBoxLayout(BoxLayout.Y_AXIS)
                                       .asComponent();
            setBorder(BorderFactory.createEmptyBorder());
            scrollPane=new JScrollPane(blocksPanel);
            scrollPane.getVerticalScrollBar()
                      .setUnitIncrement(SharedConstants.CONTROLLER_SCROLLBAR_UNIT_INCREMENT);
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
                                                                                      ColorService.hexToColor(
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
                                              .withComponentIf(
                                                          SharedConstants.DEV_MODE,
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
                                              .withComponentIf(
                                                          SharedConstants.DEV_MODE,
                                                          UIButtonDelegate.make()
                                                                          .withText(
                                                                                      "RandomColor"
                                                                          )
                                                                          .withAction(
                                                                                      ()->EventPool.dispatchEvent(
                                                                                                  GradientEventPayload.class,
                                                                                                  GradientEventPayload.makeRandomColor()
                                                                                      )
                                                                          )
                                                                          .withBackgroundColor(
                                                                                      ColorService.hexToColor(
                                                                                                  SharedConstants.LAF_POPROCK_PRIMARY_2
                                                                                      )
                                                                          )
                                              )
                                              .asComponent();
            add(
                        bottomButtonsPanel,
                        BorderLayout.SOUTH
            );

      }
}