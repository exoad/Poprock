package net.exoad.annotations.poprock.desktop.ui.java;

import javax.swing.JComponent;

import net.exoad.annotations.poprock.core.ColorObj;

public interface UXComponent<T extends JComponent>
{
	
	T expose();
	
	void setFgColor(ColorObj colorObj);
	
	void setBgColor(ColorObj colorObj);
}
