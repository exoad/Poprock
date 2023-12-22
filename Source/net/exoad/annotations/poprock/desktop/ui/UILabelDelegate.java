package net.exoad.annotations.poprock.desktop.ui;

import net.exoad.annotations.poprock.core.ColorObj;
import net.exoad.annotations.poprock.desktop.ui.java.UXLabel;

public class UILabelDelegate
	extends
	UIDelegate<UXLabel>
{
	private UILabelDelegate(String text)
	{
		rootDelegate=new UXLabel(text);
	}
	
	public static UILabelDelegate make(String text)
	{
		return new UILabelDelegate(text);
	}
	
	public UILabelDelegate withText(String text)
	{
		rootDelegate
			.setText(text);
		return this;
	}
	
	public UILabelDelegate withForegroundColor(ColorObj color)
	{
		rootDelegate
			.setFgColor(color);
		return this;
	}
}
