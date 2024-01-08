package com.jackmeng;
import java.util.HashMap;
import java.util.Map;

public class NaiveLRUCache<K,V>
{
	private final int capacity;
	private final Map<K,Node<K,V>> map;
	private final Node<K,V> tail;
	private final int numColors;
	private final Node<K,V>[][] colorBlocks;
	
	@SuppressWarnings("unchecked")
	public NaiveLRUCache(int capacity,int numColors,int blockSize)
	{
		this.capacity=capacity;
		this.map     =new HashMap<>(capacity);
		Node<K,V> head=new Node<>(null,null);
		this.tail       =new Node<>(null,null);
		head.next       =tail;
		tail.prev       =head;
		this.numColors  =numColors;
		this.colorBlocks=new Node[numColors][blockSize];
		for(int i=0;i<numColors;i++)
		{
			for(int j=0;j<blockSize;j++)
				colorBlocks[i][j]=new Node<>(null,null);
			colorBlocks[i][0].prev          =head;
			colorBlocks[i][blockSize-1].next=tail;
			for(int j=0;j<blockSize-1;j++)
			{
				colorBlocks[i][j].next  =colorBlocks[i][j+1];
				colorBlocks[i][j+1].prev=colorBlocks[i][j];
			}
		}
	}
	
	public V get(K key)
	{
		Node<K,V> node=map.get(key);
		if(node==null)
			return null;
		removeNode(node);
		addToFront(node,getColor(key));
		return node.value;
	}
	
	private void removeNode(Node<K,V> node)
	{
		node.prev.next=node.next;
		node.next.prev=node.prev;
	}
	
	public void addToFront(Node<K,V> node,int color)
	{
		Node<K,V> tailPrev=tail.prev;
		node.next                 =tail;
		node.prev                 =tailPrev;
		tailPrev.next             =node;
		tail.prev                 =node;
		colorBlocks[color][0].prev=node;
		node.nextBlock            =colorBlocks[color][0];
		node.prevBlock            =null;
		colorBlocks[color][0]     =node;
	}
	
	public int getColor(K key)
	{
		return key.hashCode()%numColors;
	}
	
	public void put(K key,V value)
	{
		Node<K,V> node=map.get(key);
		if(node!=null)
		{
			node.value=value;
			removeNode(node);
			addToFront(node,getColor(key));
		}
		else
		{
			if(map.size()==capacity)
				evict();
			node=new Node<>(key,value);
			map.put(key,node);
			addToFront(node,getColor(key));
		}
	}
	
	private void evict()
	{
		for(int i=0;i<numColors;i++)
		{
			if(colorBlocks[i][0].next!=tail)
			{
				Node<K,V> node=colorBlocks[i][0].next;
				removeNode(node);
				map.remove(node.key);
				return;
			}
		}
	}
	
	private static class Node<K,V>
	{
		K key;
		V value;
		Node<K,V> prev;
		Node<K,V> next;
		Node<K,V> prevBlock;
		Node<K,V> nextBlock;
		
		Node(K key,V value)
		{
			this.key      =key;
			this.value    =value;
			this.prev     =null;
			this.next     =null;
			this.prevBlock=null;
			this.nextBlock=null;
		}
	}
}