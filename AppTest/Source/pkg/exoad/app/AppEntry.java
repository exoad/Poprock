package pkg.exoad.app;

import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
import net.exoad.annotations.poprock.core.ColorObj;
import net.exoad.annotations.poprock.core.services.*;
import net.exoad.annotations.poprock.desktop.services.UIServices;
import net.exoad.annotations.poprock.desktop.ui.ThemeConstants;
import net.exoad.annotations.poprock.desktop.ui.UIWindow;
import pkg.exoad.app.events.payloads.ControllerChildDelegatesEventPayload;
import pkg.exoad.app.events.payloads.GradientEventPayload;
import pkg.exoad.app.ui.UIAppMainDelegate;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

/**
 * Main entry point into the Poprock application. Should not be called if you
 * are using it as a library!
 *
 * @author Jack Meng
 */
public class AppEntry
{
	static
	{
		System
			.setProperty(
				"flatlaf.menuBarEmbeded",
				"true"
			);
		UIManager
			.put(
				"ScrollBar.background",
				ColorObj.TRANSPARENT
			);
		UIManager
			.put(
				"ScrollBar.showButtons",
				false
			);
		UIManager
			.put(
				"JScrollPane.smoothScrolling",
				true
			);
		UIManager
			.put(
				"Component.focusedBorderColor",
				ColorObj.TRANSPARENT
			);
		UIManager
			.put(
				"Component.focusColor",
				ColorObj.TRANSPARENT
			);
		UIManager
			.put(
				"TabbedPane.tabSeparatorsFullHeight",
				false
			);
		UIManager
			.put(
				"TabbedPane.showTabSeparators",
				true
			);
		UIManager
			.put(
				"Button.arc",
				10
			);
		UIManager
			.put(
				"ScrollBar.thumb",
				new ColorUIResource(
					ColorService
						.hexToColor(
							ThemeConstants.LAF_POPROCK_PRIMARY_1
						)
						.asAwt()
				)
			);
		try
		{
			UIManager
				.setLookAndFeel(new FlatHighContrastIJTheme());
		}catch(UnsupportedLookAndFeelException e)
		{
			DebugService.panicWith(e);
		}
	}
	
	public static void main(String[] args)
	{
		RegistryServices.armService();
		UIServices.propagate(
			UIServices.PropertyKey.USE_OPENGL_TRACED,
			UIServices.PropertyKey.USE_FLATLAF_WINDOW_DECOR
		);
		// EVENT POOL REGISTRATION
		//
		// Pool_ID "1" -> Default color pipeline pool
		// Pool_ID "2" -> Registration for all UIControllerChildDelegates (shenanigans)
		EventPoolService
			.registerEventPool(1);
		EventPoolService
			.getPool(1)
			.registerEvent(
				GradientEventPayload.class,
				null
			);
		EventPoolService
			.getPool(1)
			.registerEvent(
				ControllerChildDelegatesEventPayload.class,
				null
			);
		UIWindow
			.make()
			.withTitle("Poprock ~ exoad")
			.withSize(
				SharedConstants.WINDOW_WIDTH,
				SharedConstants.WINDOW_HEIGHT
			)
			.withIcon(
				AssetsService
					.fetchImageIcon("Content/app-icon.png")
			)
			.withMinSize(
				SharedConstants.WINDOW_WIDTH,
				SharedConstants.WINDOW_HEIGHT
			)
			.withMainDelegate(
				UIAppMainDelegate.OBJECT
					.withPadding(
						SharedConstants.GRADIENT_WINDOW_PADDING
					)
			)
			.run();
	}
}
