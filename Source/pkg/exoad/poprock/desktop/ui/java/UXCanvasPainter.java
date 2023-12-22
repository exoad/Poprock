package pkg.exoad.poprock.desktop.ui.java;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Objects;

public class UXCanvasPainter
{
	private final Graphics2D backer;
	
	public UXCanvasPainter(Graphics2D backer)
	{
		this.backer=Objects
			.requireNonNull(backer);
	}
	
	public void arm()
	{
		backer
			.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON
			);
		backer
			.setRenderingHint(
				RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY
			); // this probably has no difference
		backer
			.setRenderingHint(
				RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY
			); // this probably has no difference
	}
	
}
