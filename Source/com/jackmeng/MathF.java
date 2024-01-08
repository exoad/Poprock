package com.jackmeng;
public final class MathF
{
	private MathF(){}
	
	public static int binomialCoefficient(int n,int k)
	{
		int result=1;
		for(int i=1;i<=k;i++)
			result=result*(n-k+i)/i;
		return result;
	}
	
	public static float clampWithEpsilon(
		float value,float min,float max,float epsilon
	)
	{
		return Math.max(min-epsilon,Math.min(max+epsilon,value));
	}
	
	public static float exponentialClamp(
		float value,float min,float max,float exponent
	)
	{
		float range=max-min;
		float normalized=(value-min)/range;
		float clamped=Math.max(0,Math.min(1,normalized));
		float buffer=(float)(range*(1-Math.pow(clamped,exponent)));
		return Math.max(min-buffer,Math.min(max+buffer,value));
	}
	
	public static float smoothStepClamp(
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
}
