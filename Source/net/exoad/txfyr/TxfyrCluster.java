package net.exoad.txfyr;

import java.util.Collection;
import java.util.LinkedHashSet;
public class TxfyrCluster
{
	public final String key;
	public final int width;
	public final int height;
	private final LinkedHashSet<TxfyrShard> shards;
	
	public TxfyrCluster(
		String key,int width,int height,
		Collection<TxfyrShard> shards
	)
	{
		assert key!=null;
		assert width>0&&height>0;
		assert shards!=null&&!shards.isEmpty();
		this.shards=new LinkedHashSet<>(shards);
		this.key   =key;
		this.width =width;
		this.height=height;
		validate();
	}
	
	public synchronized void validate()
	{
		shards
			.parallelStream()
			.forEachOrdered(x->{
				if(x==null)
					throw new InvalidTxfyrShardException("Txfyr Cluster["+key+"] has a null shard!");
				if(x.x()>width)
					throw new InvalidTxfyrShardException("Txfyr Cluster["+key+"] found a shard["+x.key()+"] with X="+x.x()+" which is greater than this cluster's width of "+width);
				if(x.y()>height)
					throw new InvalidTxfyrShardException("Txfyr Cluster["+key+"] found a shard["+x.key()+"] with Y="+x.y()+" which is greater than this cluster's height of "+height);
				if(x.area()>width*height)
					throw new InvalidTxfyrShardException(("Txfyr Cluster["+key+"] found a shard["+x.key()+"] that has a size "+x.area()+" that is greater than this cluster's size of "+(width*height)));
			});
	}
}
