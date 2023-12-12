package pkg.exoad.softgradient.core.ui;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class UIWindow
	extends
	JFrame
	implements
	Runnable
{
	
	public static UIWindow make()
	{
		return new UIWindow();
	}
	
	private UIWindow()
	{
		setTitle("SoftGradient ~ exoad");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
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
