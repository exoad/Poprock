package pkg.exoad.poprock.desktop.ui.java;

import javax.swing.JButton;
import javax.swing.JPanel;

import pkg.exoad.poprock.core.Color;

@DefsEqual(
	target="Button_Type", reference={JButton.class,JPanel.class}
) public class UXButton
	extends
	JButton
{
	public void setBgColor(Color color)
	{
		super.setBackground(
			color
				.asAwt()
		);
	}
	
	public void setFgColor(Color color)
	{
		super.setForeground(
			color
				.asAwt()
		);
	}
}
