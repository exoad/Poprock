package pkg.exoad.app.ui;

import net.exoad.annotations.poprock.core.services.ColorService;
import net.exoad.annotations.poprock.core.services.EventPoolService;
import net.exoad.annotations.poprock.desktop.ui.*;
import net.exoad.annotations.poprock.desktop.ui.UIPanelDelegate.BoxLayoutAlignment;
import net.exoad.annotations.poprock.desktop.ui.java.UXPanel;
import pkg.exoad.app.events.payloads.ControllerChildDelegatesEventPayload;

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
