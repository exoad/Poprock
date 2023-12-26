package app.desktop.ui.util;

import pkg.exoad.poprock.core.Color;

public record GradientColor(Color color,float fraction)
{
	public java.awt.Color asAwt()
	{
		return color
			.asAwt();
	}
	
	@Override public String toString()
	{
		return String
			.format(
				"ColorObj{color=%s,fraction=%f}",
				color,
				fraction
			);
	}
}
