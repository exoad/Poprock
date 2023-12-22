package com.jackmeng;
public final class NaiveFFT
{
	private NaiveFFT(){}
	
	public static Complex[] cooleytukey(Complex[] x)
	{
		int n=x.length;
		if(n==1)
			return new Complex[]{x[0]};
		if(n%2!=0)
			throw new IllegalArgumentException("n is not a power of 2");
		Complex[] even=new Complex[n/2];
		for(int i=0;i<n/2;i++)
			even[i]=x[2*i];
		Complex[] q=cooleytukey(even);
		for(int i=0;i<n/2;i++)
			even[i]=x[2*i+1];
		Complex[] r=cooleytukey(even);
		Complex[] y=new Complex[n];
		for(int k=0;k<n/2;k++)
		{
			double kth=-2*k*Math.PI/n;
			Complex wk=new Complex(Math.cos(kth),Math.sin(kth));
			y[k]    =q[k].plus(wk.times(r[k]));
			y[k+n/2]=q[k].minus(wk.times(r[k]));
		}
		return y;
	}
}
