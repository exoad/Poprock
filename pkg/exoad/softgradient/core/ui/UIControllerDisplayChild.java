package pkg.exoad.softgradient.core.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.util.Optional;

import pkg.exoad.softgradient.core.SharedConstants;
import pkg.exoad.softgradient.core.events.EventPool;
import pkg.exoad.softgradient.core.events.GradientEventPayload;
import pkg.exoad.softgradient.core.services.ColorService;

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
                                                                      0,
                                                                      0
                                                          )
                                              )
                                              .withComponentBuilder(
                                                          ()->SharedConstants.DEV_MODE
                                                                      ? Optional.of(
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
                                                                      : Optional.empty()
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

                                                                          })
                                              )
                                              .asComponent();
            add(
                        bottomButtonsPanel,
                        BorderLayout.SOUTH
            );

      }
}