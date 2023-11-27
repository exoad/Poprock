package pkg.exoad.softgradient.core.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import pkg.exoad.softgradient.core.SharedConstants;
import pkg.exoad.softgradient.core.debug.UIDebugPanelFakeDelegate;
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
                                             .withBackgroundColor(
                                                         ColorService.hexToColor(
                                                                     SharedConstants.LAF_POPROCK_PRIMARY_1
                                                         )
                                             )
                                             .withForegroundColor(
                                                         ColorService.hexToColor(
                                                                     SharedConstants.LAF_POPROCK_BG_FG
                                                         )
                                             )
                                             .withPadding(4)
                                             .asComponent(),
                              BorderLayout.NORTH
                  );
                  add(
                              delegate.asComponent(),
                              BorderLayout.CENTER
                  );

            }
      }

      private JScrollPane scrollPane;
      private JPanel bottomButtonsPanel;

      public UIControllerDisplayChild()
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
                                              .withComponent(
                                                          UIButtonDelegate.make()
                                                                          .withText("TEST")
                                                                          .withAction(()-> {
                                                                                scrollPane.add(
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