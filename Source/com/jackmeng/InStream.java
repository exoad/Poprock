package com.jackmeng;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class InStream
{
	private BufferedReader br;
	private StringTokenizer st;
	
	public InStream(InputStream e)
	{
		InputStream io=e==null?System.in:e;
		br     =new BufferedReader(new InputStreamReader(io));
	}
	
	public synchronized void reader(BufferedReader br)
	{
		this.br=br;
	}
	
	public BufferedReader reader()
	{
		return this.br;
	}
	
	public String next()
	{
		return next(x->{
			x.printStackTrace();
			return null;
		});
	}
	
	public String next(Callback<Void,Exception> errorCallback)
	{
		try
		{
			while(st==null||!st.hasMoreTokens())
				st=new StringTokenizer(br.readLine());
		}catch(IOException e)
		{
			errorCallback.call(e);
		}
		return st.toString();
	}
	
	public String nextln(Callback<Void,Exception> errorCallback)
	{
		String x="";
		try
		{
			x=br
				.readLine()
				.trim();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		return x;
	}
}