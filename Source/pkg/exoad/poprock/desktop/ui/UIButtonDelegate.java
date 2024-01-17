package pkg.exoad.poprock.desktop.ui;

import javax.swing.*;
import pkg.exoad.poprock.core.Color;
import pkg.exoad.poprock.core.ColorService;
import pkg.exoad.poprock.desktop.ui.java.UXButton;

public final class UIButtonDelegate
	extends
	UIDelegate<UXButton>
{
	public static UIButtonDelegate make()
	{
		return new UIButtonDelegate();
	}
	
	private UIButtonDelegate()
	{
		rootDelegate=new UXButton();
		rootDelegate
			.setFocusPainted(false);
		rootDelegate
			.setBorderPainted(false);
		withBackgroundColor(
			ColorService
				.hexToColor(ThemeConstants.LAF_POPROCK_PRIMARY_1)
		)
			.withForegroundColor(
				ColorService
					.hexToColor(ThemeConstants.LAF_POPROCK_BG_FG)
			);
	}
	
	public UIButtonDelegate withText(String text)
	{
		rootDelegate
			.setText(text);
		return this;
	}
	
	public UIButtonDelegate withIcon(ImageIcon icon)
	{
		rootDelegate
			.setIcon(icon);
		return this;
	}
	
	public UIButtonDelegate withBackgroundColor(int r,int g,int b)
	{
		rootDelegate
			.setBackground(
				new java.awt.Color(
					r,
					g,
					b
				)
			);
		return this;
	}
	
	public UIButtonDelegate withBackgroundColor(Color color)
	{
		rootDelegate
			.setBgColor(color);
		return this;
	}
	
	public UIButtonDelegate withForegroundColor(int r,int g,int b)
	{
		rootDelegate
			.setForeground(
				new java.awt.Color(
					r,
					g,
					b
				)
			);
		return this;
	}
	
	public UIButtonDelegate withForegroundColor(Color color)
	{
		rootDelegate
			.setFgColor(color);
		return this;
	}
	
	public UIButtonDelegate withAction(Runnable action)
	{
		rootDelegate
			.addActionListener(
				e->action
					.run()
			);
		return this;
	}
}