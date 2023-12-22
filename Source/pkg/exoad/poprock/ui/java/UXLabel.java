package pkg.exoad.poprock.ui.java;

import javax.swing.JComponent;
import javax.swing.JLabel;

import pkg.exoad.poprock.services.mixins.DebuggableMixin;
import pkg.exoad.poprock.ColorObj;

@DefsEqual(
	target="Label_Type", reference={JLabel.class,JComponent.class}
) public class UXLabel
	extends
	JLabel
	implements
	DebuggableMixin
{
	public UXLabel(String rootedContent)
	{
		super(rootedContent);
	}
	
	public void setFgColor(ColorObj colorObj)
	{
		setForeground(
			colorObj
				.asAwt()
		);
	}
}
