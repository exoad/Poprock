package pkg.exoad.poprock.desktop.ui.java;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

@DefsEqual(
	target="SplitPane_HeavyType", reference={JPanel.class,JSplitPane.class}
) public class UXSplit
	extends
	JSplitPane
{
	public enum SplitLocation
	{
		Y_AXIS(JSplitPane.VERTICAL_SPLIT),X_AXIS(JSplitPane.HORIZONTAL_SPLIT);
		
		final int awtValue;
		
		SplitLocation(int v)
		{
			this.awtValue=v;
		}
	}
	
	public UXSplit(SplitLocation alignment)
	{
		super(alignment.awtValue);
	}
}
