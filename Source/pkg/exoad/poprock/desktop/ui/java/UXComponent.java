package pkg.exoad.poprock.desktop.ui.java;

import javax.swing.JComponent;

import pkg.exoad.poprock.core.Color;

public interface UXComponent<T extends JComponent>
{
	
	T expose();
	
	void setFgColor(Color colorObj);
	
	void setBgColor(Color colorObj);
}
