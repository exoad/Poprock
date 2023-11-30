package pkg.exoad.softgradient.core.ui;

import javax.swing.JPanel;

import pkg.exoad.softgradient.core.SharedConstants;
import pkg.exoad.softgradient.core.services.ColorService;
import pkg.exoad.softgradient.core.ui.UIPanelDelegate.BoxLayoutAlignment;

public final class UIControllerDelegateChilds
{
      public interface UIControllerDelegate
                                            extends
                                            UIBasicDelegate< JPanel >
      {
            public String getHeaderName();
      }

      private UIControllerDelegateChilds()
      {
      }

      public static final class WindowSetupChildBlock
                                                      extends
                                                      UIDelegate< JPanel >
                                                      implements
                                                      UIControllerDelegate

      {
            WindowSetupChildBlock()
            {
                  super();
                  rootDelegate=UIPanelDelegate.make()
                                              .withBoxLayout(BoxLayoutAlignment.Y_AXIS)
                                              .withComponent(
                                                          UIPanelDelegate.UIHorizontalPanelDelegate.make()
                                                                                                   .withLeftComponent(
                                                                                                               UILabelDelegate.make(
                                                                                                                           "Frame Width (X)"
                                                                                                               )
                                                                                                                              .withForegroundColor(
                                                                                                                                          ColorService.hexToColor(
                                                                                                                                                      SharedConstants.LAF_POPROCK_BG_FG
                                                                                                                                          )
                                                                                                                              )
                                                                                                   )
                                                                                                   .withRightComponent(
                                                                                                               UITextFieldDelegate.make()
                                                                                                                                  .withChangeListener(
                                                                                                                                              System.out::println
                                                                                                                                  )
                                                                                                   )
                                                                                                   .withStrut(10)
                                                                                                   .withTransparency(
                                                                                                               true
                                                                                                   )
                                                                                                   .withBorder(
                                                                                                               UIHelper.makeEmptyBorder()
                                                                                                   )
                                                                                                   .withAlignmentY(
                                                                                                               Alignment.LEFT
                                                                                                   )
                                              )
                                              .withComponent(
                                                          UIPanelDelegate.UIHorizontalPanelDelegate.make()
                                                                                                   .withLeftComponent(
                                                                                                               UILabelDelegate.make(
                                                                                                                           "Frame Height (Y)"
                                                                                                               )
                                                                                                                              .withForegroundColor(
                                                                                                                                          ColorService.hexToColor(
                                                                                                                                                      SharedConstants.LAF_POPROCK_BG_FG
                                                                                                                                          )
                                                                                                                              )
                                                                                                   )
                                                                                                   .withRightComponent(
                                                                                                               UITextFieldDelegate.make()
                                                                                                                                  .withChangeListener(
                                                                                                                                              System.out::println
                                                                                                                                  )
                                                                                                   )
                                                                                                   .withStrut(10)
                                                                                                   .withTransparency(
                                                                                                               true
                                                                                                   )
                                                                                                   .withBorder(
                                                                                                               UIHelper.makeEmptyBorder()
                                                                                                   )
                                                                                                   .withAlignmentY(
                                                                                                               Alignment.LEFT
                                                                                                   )
                                              )
                                              .asComponent();
            }

            @Override public String getHeaderName()
            {
                  return "Frame setup";
            }
      }
}
