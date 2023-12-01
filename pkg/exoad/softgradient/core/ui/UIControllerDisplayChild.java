package pkg.exoad.softgradient.core.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Component;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import pkg.exoad.softgradient.core.SharedConstants;
import pkg.exoad.softgradient.core.events.EventPool;
import pkg.exoad.softgradient.core.events.GradientEventPayload;
import pkg.exoad.softgradient.core.services.ColorService;
import pkg.exoad.softgradient.core.services.DebugService;
import pkg.exoad.softgradient.core.ui.UIControllerDelegateChilds.UIControllerDelegate;
import pkg.exoad.softgradient.core.ui.UIDelegate.Alignment;
import pkg.exoad.softgradient.core.ui.UIPanelDelegate.BoxLayoutAlignment;

class UIControllerDisplayChild
                               extends
                               JPanel
{
      private static final class InnerControllerBlock
                                                      extends
                                                      JPanel

      {
            public static InnerControllerBlock make(UIDelegate< ? > delegate)
            {
                  if(delegate instanceof UIControllerDelegate e)
                        return new InnerControllerBlock(
                                    e.getHeaderName(),
                                    e
                        );
                  DebugService.throwNow(
                              "delegate for 'make' of InnerControllerBlock received a none instance of UIControllerDelegate!"
                  );
                  return null;
            }

            private InnerControllerBlock(String name,UIBasicDelegate< ? > delegate)
            {
                  setBorder(UIHelper.makeEmptyBorder());
                  setLayout(new BorderLayout());
                  add(
                              UIPanelDelegate.make()
                                             .withBoxLayout(BoxLayoutAlignment.Y_AXIS)
                                             .withLatePaintDelegate(
                                                         g-> {
                                                               g.setRenderingHint(
                                                                           RenderingHints.KEY_ANTIALIASING,
                                                                           RenderingHints.VALUE_ANTIALIAS_ON
                                                               );
                                                               g.setColor(
                                                                           ColorService.hexToColor(
                                                                                       SharedConstants.LAF_POPROCK_PRIMARY_1
                                                                           )
                                                               );
                                                               g.fillRoundRect(
                                                                           SharedConstants.CONTROLLER_BLOCKS_PADDING,
                                                                           SharedConstants.CONTROLLER_BLOCKS_PADDING,
                                                                           getWidth()-SharedConstants.CONTROLLER_BLOCKS_PADDING,
                                                                           getHeight()-SharedConstants.CONTROLLER_BLOCKS_PADDING,
                                                                           6,
                                                                           6

                                                               );
                                                         }
                                             )
                                             .withComponent(
                                                         UILabelDelegate.make(name)
                                                                        .withForegroundColor(
                                                                                    ColorService.hexToColor(
                                                                                                SharedConstants.LAF_POPROCK_BG_FG
                                                                                    )
                                                                        )
                                                                        .withPadding(4)
                                                                        .withTransparency(true)
                                                                        .withAlignmentX(Alignment.LEFT)
                                             )
                                             .withComponent(delegate)
                                             .withPadding(
                                                         SharedConstants.CONTROLLER_BLOCKS_PADDING-2,
                                                         SharedConstants.CONTROLLER_BLOCKS_PADDING+5,
                                                         SharedConstants.CONTROLLER_BLOCKS_PADDING-5,
                                                         SharedConstants.CONTROLLER_BLOCKS_PADDING+5
                                             )
                                             .asComponent(),
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
                                       .withBoxLayout(BoxLayoutAlignment.Y_AXIS)
                                       .asComponent();
            setBorder(BorderFactory.createEmptyBorder());
            scrollPane=new JScrollPane(blocksPanel);
            scrollPane.getVerticalScrollBar()
                      .setUnitIncrement(SharedConstants.CONTROLLER_SCROLLBAR_UNIT_INCREMENT);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
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
                                                                                      ()->EventPool.OBJECTS.get(1)
                                                                                                           .dispatchEvent(
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
                                                                                                        new UIControllerDelegateChilds.WindowSetupChildBlock().withBorder(
                                                                                                                    UIHelper.makeLinedBorder(
                                                                                                                                Color.white
                                                                                                                    )
                                                                                                        )
                                                                                            )
                                                                                );
                                                                          })
                                              )
                                              .withComponentIf(
                                                          SharedConstants.DEV_MODE,
                                                          UIButtonDelegate.make()
                                                                          .withText(
                                                                                      "RandomColor"
                                                                          )
                                                                          .withAction(
                                                                                      ()->EventPool.OBJECTS.get(1)
                                                                                                           .dispatchEvent(
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
                                              .withTransparency(true)
                                              .asComponent();
            add(
                        bottomButtonsPanel,
                        BorderLayout.SOUTH
            );

      }
}