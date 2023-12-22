package net.exoad.math;
public final class Maths
{
	private Maths(){}
	
	public static int clampInt(int value,int min,int max)
	{
		return Math.max(min,Math.min(max,value));
	}
	
	public static float clampFloat(float value,float min,float max)
	{
		return Math.max(min,Math.min(max,value));
	}
	
	public static float[] windowFraction(float fraction,int width,int height)
	{
		return new float[]
			{
				fraction*width,
				fraction*height
			};
	}
	
	public static float[] windowFraction(
		float fraction,int width,int height,int padding
	)
	{
		return new float[]
			{
				fraction*(width-padding),
				fraction*(height-padding)
			};
	}
	
	public static float[] windowFunction1(int length)
	{
		float[] window=new float[length];
		for(int i=0;i<length;i++)
			window[i]=(float)(0.5f*(1.0f-Math.cos(2.0f*Math.PI*i/(length-1))));
		return window;
	}
	
}
