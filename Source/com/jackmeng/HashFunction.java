package com.jackmeng;
// Copyright 2023 Jack Meng. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

public class HashFunction
{
	private final int size;
	private final int seed;
	
	public HashFunction(int size)
	{
		this.size=size;
		this.seed=(int)(Math.random()*Integer.MAX_VALUE);
	}
	
	public int hash(String value)
	{
		int hash=seed;
		for(int i=0;i<value.length();i++)
			hash=(hash*31+value.charAt(i))%size;
		return hash;
	}
}
