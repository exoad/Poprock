package pkg.exoad.softgradient.core.ui.java;

import javax.swing.JComponent;

import pkg.exoad.softgradient.core.ColorObj;

public interface UXComponent<T extends JComponent>
{
	
	T expose();
	
	void setFgColor(ColorObj colorObj);
	
	void setBgColor(ColorObj colorObj);
}
