package app.desktop.ui;

import pkg.exoad.poprock.core.ColorService;
import pkg.exoad.poprock.core.EventPoolService;
import pkg.exoad.poprock.desktop.ui.*;
import pkg.exoad.poprock.desktop.ui.UIPanelDelegate.BoxLayoutAlignment;
import pkg.exoad.poprock.desktop.ui.java.UXPanel;

import java.util.List;

public final class UIControllerDelegateChilds
{
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
	
	private UIControllerDelegateChilds()
	{
	}
	
	public interface UIControllerDelegate
		extends
		UIBasicDelegate<UXPanel>
	{
		default String getPopupName()
		{return getHeaderName();}
		
		String getHeaderName();
		
		default String getBasicDescription()
		{return getCanonicallyNamedThis();}
	}
	
	public static final class WindowSetupChildBlock
		extends
		UIDelegate<UXPanel>
		implements
		UIControllerDelegate
	
	{
		public WindowSetupChildBlock()
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
