package com.jackmeng;
import java.util.LinkedList;
import java.util.Queue;

public class LazyQueue<T>
{
	private final Queue<T> queue=new LinkedList<>();
	private final Queue<T> lazyQueue=new LinkedList<>();
	
	public void enqueue(T item)
	{
		lazyQueue.add(item);
	}
	
	public T dequeue()
	{
		if(queue.isEmpty())
			drainLazyQueue();
		return queue.poll();
	}
	
	private void drainLazyQueue()
	{
		while(!lazyQueue.isEmpty())
			queue.add(lazyQueue.poll());
	}
	
	public T peek()
	{
		if(queue.isEmpty())
			drainLazyQueue();
		return queue.peek();
	}
	
	public boolean isEmpty()
	{
		return queue.isEmpty()&&lazyQueue.isEmpty();
	}
	
	public int size()
	{
		return queue.size()+lazyQueue.size();
	}
}
