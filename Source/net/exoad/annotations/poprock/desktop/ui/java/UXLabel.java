package net.exoad.annotations.poprock.desktop.ui.java;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.exoad.annotations.poprock.core.services.mixins.DebuggableMixin;
import net.exoad.annotations.poprock.core.ColorObj;

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
