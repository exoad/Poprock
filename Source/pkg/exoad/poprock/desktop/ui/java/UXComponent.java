package pkg.exoad.poprock.desktop.ui.java;

import javax.swing.JComponent;

import pkg.exoad.poprock.core.ColorObj;

public interface UXComponent<T extends JComponent>
{
	
	T expose();
	
	void setFgColor(ColorObj colorObj);
	
	void setBgColor(ColorObj colorObj);
}
