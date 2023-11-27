package pkg.exoad.softgradient.core.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import pkg.exoad.softgradient.core.BasicService;
import pkg.exoad.softgradient.core.SharedConstants;
import pkg.exoad.softgradient.core.events.EventPool;
import pkg.exoad.softgradient.core.events.GradientEventPayload;

class UIControllerDisplayChild
                               extends
                               JPanel
{
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