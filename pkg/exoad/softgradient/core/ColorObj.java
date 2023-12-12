package pkg.exoad.softgradient.core;

import java.awt.Color;

public class ColorObj
{
	public static final ColorObj TRANSPARENT=new ColorObj(
		0,
		0,
		0,
		0
	);
	
	private Color color;
	
	public ColorObj(Color color)
	{
		this.color=color;
	}
	
	public ColorObj(int r,int g,int b)
	{
		this.color=new Color(
			r,
			g,
			b
		);
	}
	
	public ColorObj(int r,int g,int b,int a)
	{
		this.color=new Color(
			r,
			g,
			b,
			a
		);
	}
	
	public ColorObj(float r,float g,float b)
	{
		this.color=new Color(
			r,
			g,
			b
		);
	}
	
	public ColorObj(float r,float g,float b,float a)
	{
		this.color=new Color(
			r,
			g,
			b,
			a
		);
	}
	
	public ColorObj withRed(int r)
	{
		color=new Color(
			r,
			color
				.getGreen(),
			color
				.getBlue(),
			color
				.getAlpha()
		);
		return this;
	}
	
	public ColorObj withGreen(int g)
	{
		color=new Color(
			color
				.getRed(),
			g,
			color
				.getBlue(),
			color
				.getAlpha()
		);
		return this;
	}
	
	public ColorObj withBlue(int b)
	{
		color=new Color(
			color
				.getRed(),
			color
				.getGreen(),
			b,
			color
				.getAlpha()
		);
		return this;
	}
	
	public ColorObj withAlpha(int a)
	{
		color=new Color(
			color
				.getRed(),
			color
				.getGreen(),
			color
				.getBlue(),
			a
		);
		return this;
	}
	
	public Color asAwt()
	{
		return color;
	}
	
	public static ColorObj generate(float saturation,float brightness)
	{
		float hue=SharedConstants.RNG
			.nextFloat();
		return new ColorObj(
			Color
				.getHSBColor(
					hue*360,
					saturation,
					brightness
				)
		);
	}
	
	public static ColorObj randomColorObj()
	{
		return new ColorObj(
			SharedConstants.RNG
				.nextFloat(),
			SharedConstants.RNG
				.nextFloat(),
			SharedConstants.RNG
				.nextFloat()
		);
	}
	
	@Override public String toString()
	{
		return "ColorObj{r="+color
			.getRed()+",g="+color
				   .getGreen()+",b="+color
				   .getBlue()+",a="+color
				   .getAlpha()+"}";
	}
	
}