package pkg.exoad.softgradient.core.ui;

import javax.swing.JSplitPane;

/**
 * The main content panel that holds all of the necessary information for
 * building the rest of the UI tree and delegates. This is not a library class,
 * this is part of the SoftGradient app itself.
 *
 * @author Jack Meng
 */
public final class UIAppMainDelegate
                                     extends
                                     UIDelegate<JSplitPane>
{

      /**
       * Singleton instance. We don't have to use the Singleton instance because
       * we are assured this will
       * be utilized in {@link pkg.exoad.softgradient.SoftGradientEntry}
       */
      public static final UIAppMainDelegate OBJECT=new UIAppMainDelegate();

      private UIAppMainDelegate()
      {
            // initialize this basic delegate with the required delegates
            rootDelegate=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            rootDelegate
                  .setDividerLocation(0.5);
            rootDelegate
                  .setResizeWeight(0.0);
            rootDelegate
                  .setContinuousLayout(true);
            rootDelegate
                  .setLeftComponent(new UIGradientDisplayChild());
            rootDelegate
                  .setRightComponent(new UIControllerDisplayChild());
            rootDelegate
                  .getLeftComponent()
                  .setMinimumSize(
                        rootDelegate
                              .getLeftComponent()
                              .getPreferredSize()
                  );
      }
}
