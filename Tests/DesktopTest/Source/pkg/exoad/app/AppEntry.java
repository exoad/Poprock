package pkg.exoad.app;

import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
import pkg.exoad.app.events.payloads.ControllerChildDelegatesEventPayload;
import pkg.exoad.app.events.payloads.GradientEventPayload;
import pkg.exoad.app.ui.UIAppMainDelegate;
import pkg.exoad.poprock.core.Color;
import pkg.exoad.poprock.core.debug.DebugService;
import pkg.exoad.poprock.core.services.ColorService;
import pkg.exoad.poprock.core.services.EventPoolService;
import pkg.exoad.poprock.core.services.RegistryServices;
import pkg.exoad.poprock.desktop.ui.ThemeConstants;
import pkg.exoad.poprock.desktop.ui.UIWindow;
import pkg.exoad.poprock.desktop.ui.services.UIServices;

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
		UIServices.propagate(UIServices.PropertyKey.USE_OPENGL_TRACED);
		System
			.setProperty(
				"flatlaf.menuBarEmbeded",
				"true"
			);
		UIManager
			.put(
				"ScrollBar.background",
				Color.TRANSPARENT
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
				Color.TRANSPARENT
			);
		UIManager
			.put(
				"Component.focusColor",
				Color.TRANSPARENT
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
