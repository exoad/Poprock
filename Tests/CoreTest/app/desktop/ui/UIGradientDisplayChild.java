package app.desktop.ui;

import pkg.exoad.poprock.core.services.ColorService;
import pkg.exoad.poprock.core.services.EventPoolService;
import pkg.exoad.poprock.desktop.ui.ThemeConstants;

import javax.swing.*;
import java.awt.*;

class UIGradientDisplayChild
	extends
	JPanel
{
	
	public UIGradientDisplayChild()
	{
		EventPoolService
			.getPool(1)
			.attachListener(
				GradientEventPayload.class,
				this::repaint
			);
	}
	
	@Override public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2=(Graphics2D)g;
		g2
			.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON
			);
		if(EventPoolService
			   .getPool(1)
			   .getPayload(
				   GradientEventPayload.class
			   )!=null&&!EventPoolService
			.getPool(1)
			.getPayload(
				GradientEventPayload.class
			)
			.equals(
				GradientEventPayload.EMPTY
			))
		{
			GradientEventPayload e=EventPoolService
				.getPool(1)
				.getPayload(GradientEventPayload.class);
			g
				.setColor(
					e
						.colors()[0]
						.color()
						.asAwt()
				);
			g
				.fillRoundRect(
					(getWidth()-Math
						.min(
							getWidth(),
							getHeight()
						))/2,
					(getHeight()-Math
						.min(
							getWidth(),
							getHeight()
						))/2,
					Math
						.min(
							getWidth(),
							getHeight()
						),
					Math
						.min(
							getWidth(),
							getHeight()
						),
					(int)(Math
							  .min(
								  getWidth(),
								  getHeight()
							  )*SharedConstants.ROUND_RECT_ARC),
					(int)(Math
							  .min(
								  getWidth(),
								  getHeight()
							  )*SharedConstants.ROUND_RECT_ARC)
				);
		}
		else
		{
			g
				.setColor(
					ColorService
						.hexToColor(
							ThemeConstants.LAF_POPROCK_BG_FG
						)
						.asAwt()
				);
			g
				.fillRect(
					0,
					0,
					getWidth(),
					getHeight()
				);
		}
		g
			.dispose();
	}
}