package pkg.exoad.poprock.desktop.ui;

import javax.swing.*;
import pkg.exoad.poprock.core.debug.DebugService;
import pkg.exoad.poprock.desktop.ui.java.UXPanel;

import java.awt.*;
import java.util.function.Consumer;

public final class UIPanelDelegate
	extends
	UIDelegate<UXPanel>
{
	
	private Consumer<Graphics2D> earlyPaintDelegate;
	private Consumer<Graphics2D> latePaintDelegate;
	private UIPanelDelegate()
	{
		rootDelegate=new UXPanel()
		{
			@Override public void paintComponent(Graphics g)
			{
				if(earlyPaintDelegate!=null)
					earlyPaintDelegate
						.accept((Graphics2D)g);
				super.paintComponent(g);
				if(latePaintDelegate!=null)
					latePaintDelegate
						.accept((Graphics2D)g);
			}
		};
	}
	
	public static UIPanelDelegate make()
	{
		return new UIPanelDelegate();
	}
	
	public UIPanelDelegate withBoxLayout(BoxLayoutAlignment axis)
	{
		rootDelegate
			.setLayout(
				new BoxLayout(
					rootDelegate,
					axis.axis
				)
			);
		return this;
	}
	
	public UIPanelDelegate withLatePaintDelegate(
		Consumer<Graphics2D> paintDelegate
	)
	{
		this.latePaintDelegate=paintDelegate;
		return this;
	}
	
	public UIPanelDelegate withEarlyPaintDelegate(
		Consumer<Graphics2D> paintDelegate
	)
	{
		this.earlyPaintDelegate=paintDelegate;
		return this;
	}
	
	public UIPanelDelegate withGridLayout(int rows,int cols)
	{
		return withLayout(
			new GridLayout(
				rows,
				cols
			)
		);
	}
	
	public UIPanelDelegate withLayout(LayoutManager layout)
	{
		rootDelegate
			.setLayout(layout);
		return this;
	}
	
	public UIPanelDelegate withFlowLayout(
		FlowLayoutAlignment alignment,int hgap,int vgap
	)
	{
		DebugService
			.panicOn(
				hgap<0,
				getNamedThis()+" received hgap<0 for withFlowLayout"
			);
		DebugService
			.panicOn(
				vgap<0,
				getNamedThis()+" received hgap<0 for withFlowLayout"
			);
		return withLayout(
			new FlowLayout(
				alignment.axis,
				hgap,
				vgap
			)
		);
	}
	
	public UIPanelDelegate withComponent(UIBasicDelegate<?> components)
	{
		rootDelegate
			.add(
				components
					.asComponent()
			);
		return this;
	}
	
	public UIPanelDelegate withComponent(
		UIBasicDelegate<?> components,Object constraints
	)
	{
		rootDelegate
			.add(
				components
					.asComponent(),
				constraints
			);
		return this;
	}
	
	public UIPanelDelegate withComponentIf(
		boolean condition,UIBasicDelegate<?> component
	)
	{
		if(condition)
			rootDelegate
				.add(
					component
						.asComponent()
				);
		return this;
	}
	
	public enum BoxLayoutAlignment
	{
		Y_AXIS(BoxLayout.Y_AXIS),
		
		X_AXIS(BoxLayout.X_AXIS),
		
		LINE_AXIS(BoxLayout.LINE_AXIS),
		
		PAGE_AXIS(BoxLayout.PAGE_AXIS);
		
		final int axis;
		
		BoxLayoutAlignment(int axis)
		{
			this.axis=axis;
		}
	}
	
	public enum FlowLayoutAlignment
	{
		LEFT(FlowLayout.LEFT),
		
		LEADING(FlowLayout.LEADING),
		
		TRAILING(FlowLayout.TRAILING),
		
		CENTER(FlowLayout.CENTER),
		
		RIGHT(FlowLayout.RIGHT);
		
		final int axis;
		
		FlowLayoutAlignment(int axis)
		{
			this.axis=axis;
		}
	}
	
	public static final class UIHorizontalPanelDelegate
		extends
		UIDelegate<UXPanel>
	{
		int strutSize=0;
		private UIDelegate<?> leftSide;
		private UIDelegate<?> rightSide;
		
		private UIHorizontalPanelDelegate()
		{
			rootDelegate=UIPanelDelegate
				.make()
				.withBoxLayout(BoxLayoutAlignment.X_AXIS)
				.asComponent();
		}
		
		public static UIHorizontalPanelDelegate make()
		{
			return new UIHorizontalPanelDelegate();
		}
		
		public UIHorizontalPanelDelegate withRightComponent(
			UIDelegate<?> rightSide
		)
		{
			this.rightSide=rightSide;
			return this;
		}
		
		public UIHorizontalPanelDelegate withLeftComponent(
			UIDelegate<?> leftSide
		)
		{
			this.leftSide=leftSide;
			return this;
		}
		
		public UIHorizontalPanelDelegate withBackgroundColor(Color color)
		{
			rootDelegate
				.setBackground(color);
			return this;
		}
		
		@Override public UXPanel asComponent()
		{
			DebugService
				.panicOn(
					leftSide==null,
					"HorizontalPanelDelegate["+hashCode()+"] received a NULL leftSide component!"
				);
			DebugService
				.panicOn(
					rightSide==null,
					"HorizontalPanelDelegate["+hashCode()+"] received a NULL rightSide component!"
				);
			rootDelegate
				.add(
					leftSide
						.asComponent()
				);
			if(strutSize>=0)
				rootDelegate
					.add(
						Box
							.createHorizontalStrut(strutSize)
					);
			rootDelegate
				.add(
					rightSide
						.asComponent()
				);
			return super.asComponent();
		}
		
		public UIHorizontalPanelDelegate withStrut(int strut)
		{
			DebugService
				.panicOn(
					strut<0,
					getNamedThis()+" received a strutSize<0"
				);
			this.strutSize=strut;
			return this;
		}
	}
}