package pkg.exoad.poprock.desktop.ui;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;

import java.awt.Color;
import java.util.Objects;

public final class UIHelper
{
	private UIHelper()
	{
	}
	
	public static Border makeLinedBorder(Color color)
	{
		return BorderFactory
			.createLineBorder(
				color,
				1
			);
	}
	
	public static Border makeLinedBorder(Color color,int thickness)
	{
		return BorderFactory
			.createLineBorder(
				color,
				thickness
			);
	}
	
	public static UIDelegate<JPopupMenu> makePopupMenu(
		UIPopupItemChilds.PopupConfiguration config,
		UIPopupItemChilds.SimplePopupDelegate... items
	)
	{
		Objects
			.requireNonNull(items);
		Objects
			.requireNonNull(config);
		JPopupMenu popupMenu=new JPopupMenu(
			config
				.name()
		);
		popupMenu
			.setBorderPainted(
				config
					.borderPainted()
			);
		popupMenu
			.setInvoker(
				config
					.invoker()
			);
		for(UIPopupItemChilds.SimplePopupDelegate item: items)
		{
			JMenuItem item2=new JMenuItem(
				item
					.name()
			);
			item2
				.addActionListener(
					e->item
						.action()
				);
			popupMenu
				.add(item2);
		}
		return UIDelegate
			.makeWith(popupMenu);
	}
	
	public static Border makeEmptyBorder()
	{
		return BorderFactory
			.createEmptyBorder();
	}
}
