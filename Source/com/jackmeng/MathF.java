package com.jackmeng;
public final class MathF
{
	private MathF(){}
	
	public static int binomial_coefficient(int n,int k)
	{
		int result=1;
		for(int i=1;i<=k;i++)
			result=result*(n-k+i)/i;
		return result;
	}
	
	public static float clamp_with_epsilon(
		float value,float min,float max,float epsilon
	)
	{
		return Math.max(min-epsilon,Math.min(max+epsilon,value));
	}
	
	public static float exponential_clamp(
		float value,float min,float max,float exponent
	)
	{
		float range=max-min;
		float normalized=(value-min)/range;
		float clamped=Math.max(0,Math.min(1,normalized));
		float buffer=(float)(range*(1-Math.pow(clamped,exponent)));
		return Math.max(min-buffer,Math.min(max+buffer,value));
	}
	
	public static float smoothstep_Clamp(
		float value,float min,float max,float smoothness
	)
	{
		float range=max-min;
		float normalized=(value-min)/range;
		float clamped=Math.max(0,Math.min(1,normalized));
		float smooth=clamped*clamped*(3-2*clamped);
		float buffer=range*(1-smoothness*smooth);
		return Math.max(min-buffer,Math.min(max+buffer,value));
	}
	
	public static int clamp(int value,int min,int max)
	{
		return Math.max(min,Math.min(max,value));
	}
}
