package net.exoad.txfyr;

import net.exoad.image.BufferedImageType;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.LinkedHashSet;
public class TxfyrCluster
{
	public final String name;
	public final int width;
	public final int height;
	public final BufferedImage source;
	private final LinkedHashSet<TxfyrShard> shards;
	
	public TxfyrCluster(
		String key,int width,int height,
		BufferedImage source,
		Collection<TxfyrShard> shards
	)
	{
		assert key!=null;
		assert width>0&&height>0;
		assert source!=null;
		assert shards!=null&&!shards.isEmpty();
		this.shards=new LinkedHashSet<>(shards);
		this.name  =key;
		this.source=source;
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
					throw new InvalidTxfyrShardException("Txfyr Cluster["+name+"] has a null shard!");
				if(x.x()>width)
					throw new InvalidTxfyrShardException("Txfyr Cluster["+name+"] found a shard["+x.key()+"] with X="+x.x()+" which is greater than this cluster's width of "+width);
				if(x.y()>height)
					throw new InvalidTxfyrShardException("Txfyr Cluster["+name+"] found a shard["+x.key()+"] with Y="+x.y()+" which is greater than this cluster's height of "+height);
				if(x.area()>width*height)
					throw new InvalidTxfyrShardException(("Txfyr Cluster["+name+"] found a shard["+x.key()+"] that has a size "+x.area()+" that is greater than this cluster's size of "+(width*height)));
			});
	}
	
	@Override public String toString()
	{
		return "TxfyrCluster[name="+name+",width="+width+",sourceType="+BufferedImageType.of(
			source.getType())+",sourceRasterData="+source.getData()+"]";
	}
}
