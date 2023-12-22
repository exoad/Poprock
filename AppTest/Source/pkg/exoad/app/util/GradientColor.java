package pkg.exoad.app.util;

import net.exoad.annotations.poprock.core.ColorObj;

import java.awt.*;

public record GradientColor(ColorObj color,float fraction)
{
	public Color asAwt()
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
