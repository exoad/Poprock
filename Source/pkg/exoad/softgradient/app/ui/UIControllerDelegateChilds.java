package pkg.exoad.softgradient.app.ui;

import java.util.List;

import pkg.exoad.softgradient.core.ThemeConstants;
import pkg.exoad.softgradient.app.events.payloads.ControllerChildDelegatesEventPayload;
import pkg.exoad.softgradient.core.services.ColorService;
import pkg.exoad.softgradient.core.services.EventPoolService;
import pkg.exoad.softgradient.core.ui.UIBasicDelegate;
import pkg.exoad.softgradient.core.ui.UIDelegate;
import pkg.exoad.softgradient.core.ui.UIHelper;
import pkg.exoad.softgradient.core.ui.UILabelDelegate;
import pkg.exoad.softgradient.core.ui.UIPanelDelegate;
import pkg.exoad.softgradient.core.ui.UIPanelDelegate.BoxLayoutAlignment;
import pkg.exoad.softgradient.core.ui.java.UXPanel;

public final class UIControllerDelegateChilds
{
	public interface UIControllerDelegate
		extends
		UIBasicDelegate<UXPanel>
	{
		public String getHeaderName();
		
		public default String getPopupName()
		{return getHeaderName();}
		
		public default String getBasicDescription()
		{return getCanonicallyNamedThis();}
	}
	
	private UIControllerDelegateChilds()
	{
	}
	
	static
	{
		// TODO: fix this part for the single child event pool used here
		EventPoolService
			.getPool(1)
			.dispatchEvent(
				ControllerChildDelegatesEventPayload.class,
				new ControllerChildDelegatesEventPayload(
					List
						.of(
							new UIControllerDelegate[]{
								// exports all avaliable children delegates
								new WindowSetupChildBlock()
							}
						)
				)
			);
	}
	
	public static final class WindowSetupChildBlock
		extends
		UIDelegate<UXPanel>
		implements
		UIControllerDelegate
	
	{
		WindowSetupChildBlock()
		{
			super();
			rootDelegate=UIPanelDelegate
				.make()
				.withBoxLayout(BoxLayoutAlignment.Y_AXIS)
				.withComponent(
					UIPanelDelegate.UIHorizontalPanelDelegate
						.make()
						.withLeftComponent(
							UILabelDelegate
								.make(
									"Frame Width (X)"
								)
								.withForegroundColor(
									ColorService
										.hexToColor(
											ThemeConstants.LAF_POPROCK_BG_FG
										)
								)
						)
						/*------------------------------------- /
						/ .withRightComponent(                  /
						/       UITextFieldDelegate             /
						/             .make()                   /
						/             .withChangeListener(      /
						/                   System.out::println /
						/             )                         /
						/ )                                     /
						/--------------------------------------*/
						.withStrut(10)
						.withTransparency(
							true
						)
						.withBorder(
							UIHelper
								.makeEmptyBorder()
						)
						.withAlignmentY(
							Alignment.LEFT
						)
				)
				.withComponent(
					UIPanelDelegate.UIHorizontalPanelDelegate
						.make()
						.withLeftComponent(
							UILabelDelegate
								.make("Frame Height (Y)")
								.withForegroundColor(
									ColorService
										.hexToColor(
											ThemeConstants.LAF_POPROCK_BG_FG
										)
								)
						)
						/*------------------------------------- /
						/ .withRightComponent(                  /
						/       UITextFieldDelegate             /
						/             .make()                   /
						/             .withChangeListener(      /
						/                   System.out::println /
						/             )                         /
						/ )                                     /
						/--------------------------------------*/
						.withStrut(10)
						.withTransparency(
							true
						)
						.withBorder(
							UIHelper
								.makeEmptyBorder()
						)
						.withAlignmentY(
							Alignment.LEFT
						)
				)
				.asComponent();
		}
		
		@Override public String getHeaderName()
		{return "Frame setup";}
	}
}
