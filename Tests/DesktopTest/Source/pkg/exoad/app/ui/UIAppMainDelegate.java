package pkg.exoad.app.ui;

import pkg.exoad.poprock.desktop.ui.UIDelegate;
import pkg.exoad.poprock.desktop.ui.java.UXSplit;
import pkg.exoad.poprock.desktop.ui.java.UXSplit.SplitLocation;

/**
 * The main content panel that holds all of the necessary information for
 * building the rest of the UI tree and delegates. This is not a library class,
 * this is part of the Poprock app itself.
 *
 * @author Jack Meng
 */
public final class UIAppMainDelegate
	extends
	UIDelegate<UXSplit>
{
	
	/**
	 * Singleton instance. We don't have to use the Singleton instance because
	 * we are assured this will be utilized in {@link pkg.exoad.app.AppEntry}
	 */
	public static final UIAppMainDelegate OBJECT=new UIAppMainDelegate();
	
	private UIAppMainDelegate()
	{
		// initialize this basic delegate with the required delegates
		rootDelegate=new UXSplit(SplitLocation.X_AXIS);
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
