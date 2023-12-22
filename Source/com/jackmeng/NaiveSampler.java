package com.jackmeng;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;

public class NaiveSampler<T>
	implements
	Consumer<T>
{
	private final int sampleRate;
	private final Callback<Void,T> callback;
	private int counter;
	
	public NaiveSampler(int sampleRate,Callback<Void,T> callback)
	{
		this.sampleRate=sampleRate;
		this.callback  =callback;
		counter        =0;
	}
	
	public static void sampleFile(
		String filePath,int sampleRate,Callback<Void,String> callback
	)
		throws
		IOException
	{
		try(BufferedReader reader=new BufferedReader(new FileReader(filePath)))
		{
			NaiveSampler<String> sampler=new NaiveSampler<>(
				sampleRate,
				callback
			);
			String line;
			while((line=reader.readLine())!=null)
				sampler.accept(line);
		}
	}
	
	@Override public void accept(T t)
	{
		if(counter==0)
			callback.call(t);
		counter=(counter+1)%sampleRate;
	}
	
}