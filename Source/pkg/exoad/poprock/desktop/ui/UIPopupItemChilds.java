package pkg.exoad.poprock.desktop.ui;

import pkg.exoad.poprock.core.BasicService;

import javax.swing.*;

public final class UIPopupItemChilds
{
	private UIPopupItemChilds()
	{
	}
	
	public static void showPopupMenuDelegate(
		UIDelegate<? extends JPopupMenu> popDelegate,Offset offsetLocal
	)
	{
		popDelegate
			.asComponent()
			.show(
				popDelegate
					.asComponent()
					.getInvoker(),
				offsetLocal
					.x(),
				offsetLocal
					.y()
			);
	}
	
	public record SimplePopupDelegate(String name,Runnable action)
	{
		public SimplePopupDelegate
		{
			BasicService
				.requireAllNonNull(
					name,
					action
				);
		}
	}
	
	public static record PopupConfiguration(
		String name,boolean borderPainted,JComponent invoker
	)
	{
	
	}
}
