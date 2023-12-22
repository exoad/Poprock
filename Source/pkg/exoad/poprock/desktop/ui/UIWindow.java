package pkg.exoad.poprock.desktop.ui;

import javax.swing.*;
import java.awt.*;

public class UIWindow
	extends
	JFrame
	implements
	Runnable
{
	
	private UIWindow()
	{
		setTitle("Poprock ~ exoad");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
	}
	
	public static UIWindow make()
	{
		return new UIWindow();
	}
	
	public UIWindow withTitle(String title)
	{
		setTitle(title);
		return this;
	}
	
	public UIWindow withSize(int width,int height)
	{
		Dimension dim=new Dimension(
			width,
			height
		);
		setPreferredSize(dim);
		setSize(dim);
		return this;
	}
	
	public UIWindow withIcon(ImageIcon icon)
	{
		setIconImage(
			icon
				.getImage()
		);
		return this;
	}
	
	public UIWindow withMinSize(int width,int height)
	{
		setMinimumSize(
			new Dimension(
				width,
				height
			)
		);
		return this;
	}
	
	public UIWindow withMainDelegate(UIDelegate<?> delegate)
	{
		setContentPane(
			delegate
				.asComponent()
		);
		return this;
	}
	
	@Override public void run()
	{
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
	}
	
}
