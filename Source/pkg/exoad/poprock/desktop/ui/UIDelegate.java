package pkg.exoad.poprock.desktop.ui;

import javax.swing.*;
import javax.swing.border.Border;
import pkg.exoad.poprock.core.AwtMixerService;
import pkg.exoad.poprock.core.Color;

import java.awt.*;

public class UIDelegate<T extends JComponent>
	implements
	UIBasicDelegate<T>
{
	public record DelegateProperties(
		Size prefSize,Size size,Size minSize,Size maxSize,Offset origin,
		boolean visibility,Color bg,boolean opaqueness
	)
	{
	
	}
	
	public static <A extends JComponent> UIDelegate<A> makeWith(A component)
	{
		UIDelegate<A> delegate=new UIDelegate<>();
		delegate
			.setRootDelegate(component);
		return delegate;
	}
	
	protected T rootDelegate;
	
	@Override public T asComponent()
	{
		assert rootDelegate!=null;
		return rootDelegate;
	}
	
	public enum Alignment
	{
		TOP(Component.TOP_ALIGNMENT),
		
		CENTER(Component.CENTER_ALIGNMENT),
		
		RIGHT(Component.RIGHT_ALIGNMENT),
		
		LEFT(Component.LEFT_ALIGNMENT);
		
		final float axis;
		
		Alignment(float x)
		{
			this.axis=x;
		}
	}
	
	public DelegateProperties exposeProperties()
	{
		return new DelegateProperties(
			AwtMixerService
				.convertDimensionClass(
					rootDelegate
						.getPreferredSize()
				),
			AwtMixerService
				.convertDimensionClass(
					rootDelegate
						.getSize()
				),
			AwtMixerService
				.convertDimensionClass(
					rootDelegate
						.getMinimumSize()
				),
			AwtMixerService
				.convertDimensionClass(
					rootDelegate
						.getMaximumSize()
				),
			AwtMixerService
				.convertPointClass(
					rootDelegate
						.getLocation()
				),
			rootDelegate
				.isVisible(),
			new Color(
				rootDelegate
					.getBackground()
			),
			rootDelegate
				.isOpaque()
		);
	}
	
	protected void setRootDelegate(T e)
	{rootDelegate=e;}
	
	public void setVisibility(boolean visibility)
	{
		rootDelegate
			.setVisible(visibility);
	}
	
	public UIDelegate<T> withVisibility(boolean visibility)
	{
		setVisibility(visibility);
		return this;
	}
	
	public UIDelegate<T> withTransparency(boolean transparency)
	{
		if(transparency)
			rootDelegate
				.setBackground(
					Color.TRANSPARENT
						.asAwt()
				);
		rootDelegate
			.setOpaque(transparency);
		return this;
	}
	
	public UIDelegate<T> withAlignmentX(Alignment alignment)
	{
		rootDelegate
			.setAlignmentX(alignment.axis);
		return this;
	}
	
	public UIDelegate<T> withAlignmentY(Alignment alignment)
	{
		rootDelegate
			.setAlignmentY(alignment.axis);
		return this;
	}
	
	public UIDelegate<T> withBorder(Border border)
	{
		rootDelegate
			.setBorder(border);
		return this;
	}
	
	public UIDelegate<T> withPadding(int top,int left,int bottom,int right)
	{
		rootDelegate
			.setBorder(
				BorderFactory
					.createEmptyBorder(
						top,
						left,
						bottom,
						right
					)
			);
		return this;
	}
	
	public UIDelegate<T> withPadding(int padding)
	{
		return withPadding(
			padding,
			padding,
			padding,
			padding
		);
	}
	
	public UIDelegate<T> withPreferredSize(int width,int height)
	{
		rootDelegate
			.setPreferredSize(
				new Dimension(
					width,
					height
				)
			);
		return this;
	}
}
