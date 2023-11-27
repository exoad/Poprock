package pkg.exoad.softgradient.core.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import pkg.exoad.softgradient.core.ColorObj;
import pkg.exoad.softgradient.core.GradientColor;
import pkg.exoad.softgradient.core.SharedConstants;
import pkg.exoad.softgradient.core.debug.UIDebugPanelDelegate;
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
            UIDebugPanelDelegate left=new UIDebugPanelDelegate(
                        Color.RED
            );
            left.setPreferredSize(
                        new java.awt.Dimension(
                                    SharedConstants.WINDOW_WIDTH/2,
                                    SharedConstants.WINDOW_HEIGHT
                        )
            );
            rootDelegate.setLeftComponent(
                        left
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
                                                                                           new GradientColor(
                                                                                                       ColorObj.randomColorObj(),
                                                                                                       1f,
                                                                                                       0.5f
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
      }
}
