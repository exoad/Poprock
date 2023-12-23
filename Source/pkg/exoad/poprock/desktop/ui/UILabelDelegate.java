package pkg.exoad.poprock.desktop.ui;

import pkg.exoad.poprock.core.Color;
import pkg.exoad.poprock.desktop.ui.java.UXLabel;

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
	
	public UILabelDelegate withForegroundColor(Color color)
	{
		rootDelegate
			.setFgColor(color);
		return this;
	}
}
