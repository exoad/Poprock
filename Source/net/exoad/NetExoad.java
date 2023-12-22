package net.exoad;
import java.util.Random;
public final class NetExoad
{
	private static Random RNG;
	
	private NetExoad(){}
	
	public static Random rng()
	{
		if(RNG==null)
			RNG=new Random();
		return RNG;
	}
}
