package pkg.exoad.poprock.ui.java;

import javax.swing.JButton;
import javax.swing.JPanel;

import pkg.exoad.poprock.ColorObj;

@DefsEqual(
	target="Button_Type", reference={JButton.class,JPanel.class}
) public class UXButton
	extends
	JButton
{
	public void setBgColor(ColorObj color)
	{
		super.setBackground(
			color
				.asAwt()
		);
	}
	
	public void setFgColor(ColorObj color)
	{
		super.setForeground(
			color
				.asAwt()
		);
	}
}
