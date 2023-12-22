package com.jackmeng;
import java.util.ArrayList;

public class IntervalTree
{
	public static class IntInterval
	{
		int low, high;
		
		public IntInterval(int low,int high)
		{
			this.low =low;
			this.high=high;
		}
	}
	
	public static final class IntervalNode
	{
		IntInterval interval;
		int max;
		IntervalNode left, right;
		
		public IntervalNode(IntInterval interval)
		{
			this.interval=interval;
			this.max     =interval.high;
		}
	}
	
	private IntervalNode root;
	
	public void insert(IntInterval interval)
	{
		root=insert(root,interval);
	}
	
	private IntervalNode insert(IntervalNode node,IntInterval interval)
	{
		if(node==null)
			return new IntervalNode(interval);
		int low=interval.low;
		if(low<node.interval.low)
			node.left=insert(node.left,interval);
		else
			node.right=insert(node.right,interval);
		if(node.max<interval.high)
			node.max=interval.high;
		return node;
	}
	
	public ArrayList<IntInterval> search(int point)
	{
		ArrayList<IntInterval> results=new ArrayList<>();
		search(root,point,results);
		return results;
	}
	
	private void search(
		IntervalNode node,int point,ArrayList<IntInterval> results
	)
	{
		if(node==null)
			return;
		if(point>=node.interval.low&&point<=node.interval.high)
			results.add(node.interval);
		if(node.left!=null&&point<=node.left.max)
			search(node.left,point,results);
		if(node.right!=null&&point<=node.right.max)
			search(node.right,point,results);
	}
	
}