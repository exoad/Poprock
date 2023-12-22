package pkg.exoad.poprock.ui;

import java.awt.Color;

import javax.swing.ImageIcon;

import pkg.exoad.poprock.ColorObj;
import pkg.exoad.poprock.services.ColorService;
import pkg.exoad.poprock.ui.java.UXButton;
import pkg.exoad.poprock.ThemeConstants;

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
				new Color(
					r,
					g,
					b
				)
			);
		return this;
	}
	
	public UIButtonDelegate withBackgroundColor(ColorObj color)
	{
		rootDelegate
			.setBgColor(color);
		return this;
	}
	
	public UIButtonDelegate withForegroundColor(int r,int g,int b)
	{
		rootDelegate
			.setForeground(
				new Color(
					r,
					g,
					b
				)
			);
		return this;
	}
	
	public UIButtonDelegate withForegroundColor(ColorObj color)
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