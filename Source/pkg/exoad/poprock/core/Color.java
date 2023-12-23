package pkg.exoad.poprock.core;

public class Color
{
	public static final Color TRANSPARENT=new Color(
		0,
		0,
		0,
		0
	);
	
	private java.awt.Color color;
	
	public Color(java.awt.Color color)
	{
		this.color=color;
	}
	
	public Color(int r,int g,int b)
	{
		this.color=new java.awt.Color(
			r,
			g,
			b
		);
	}
	
	public Color(int r,int g,int b,int a)
	{
		this.color=new java.awt.Color(
			r,
			g,
			b,
			a
		);
	}
	
	public Color(float r,float g,float b)
	{
		this.color=new java.awt.Color(
			r,
			g,
			b
		);
	}
	
	public Color(float r,float g,float b,float a)
	{
		this.color=new java.awt.Color(
			r,
			g,
			b,
			a
		);
	}
	
	public Color withRed(int r)
	{
		color=new java.awt.Color(
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
	
	public Color withGreen(int g)
	{
		color=new java.awt.Color(
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
	
	public Color withBlue(int b)
	{
		color=new java.awt.Color(
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
	
	public Color withAlpha(int a)
	{
		color=new java.awt.Color(
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
	
	public java.awt.Color asAwt()
	{
		return color;
	}
	
	public static Color generate(float saturation,float brightness)
	{
		float hue=SharedConstants.RNG
			.nextFloat();
		return new Color(
			java.awt.Color
				.getHSBColor(
					hue*360,
					saturation,
					brightness
				)
		);
	}
	
	public static Color randomColorObj()
	{
		return new Color(
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