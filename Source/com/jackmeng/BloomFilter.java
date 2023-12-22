package com.jackmeng;

// Copyright 2023 Jack Meng. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

import java.util.BitSet;

public class BloomFilter
{
	
	private final BitSet bitset;
	private final HashFunction[] hashFxs;
	
	public BloomFilter(int size,int hashes)
	{
		this.bitset =new BitSet(size);
		this.hashFxs=new HashFunction[hashes];
		for(int i=0;i<hashes;i++)
			hashFxs[i]=new HashFunction(size);
	}
	
	public void add(String value)
	{
		for(HashFunction hashFunction: hashFxs)
		{
			int hash=hashFunction.hash(value);
			bitset.set(hash);
		}
	}
	
	public boolean contains(String value)
	{
		for(HashFunction hashFunction: hashFxs)
		{
			int hash=hashFunction.hash(value);
			if(!bitset.get(hash))
				return false;
		}
		return true;
	}
	
}
