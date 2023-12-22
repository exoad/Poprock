package pkg.exoad.poprock.ui;

import pkg.exoad.poprock.ColorObj;
import pkg.exoad.poprock.ui.java.UXLabel;

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
