package pkg.exoad.poprock.ui.java;

import javax.swing.JComponent;

import pkg.exoad.poprock.ColorObj;

public interface UXComponent<T extends JComponent>
{
	
	T expose();
	
	void setFgColor(ColorObj colorObj);
	
	void setBgColor(ColorObj colorObj);
}
