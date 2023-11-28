package pkg.exoad.softgradient.core.ui;

import javax.swing.JSplitPane;

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
            rootDelegate.setLeftComponent(new UIGradientDisplayChild());
            rootDelegate.setRightComponent(new UIControllerDisplayChild());
            rootDelegate.getLeftComponent()
                        .setMinimumSize(
                                    rootDelegate.getLeftComponent()
                                                .getPreferredSize()
                        );
      }
}
