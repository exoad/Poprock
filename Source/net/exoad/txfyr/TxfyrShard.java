package net.exoad.txfyr;
import net.exoad.math.geom.Rectangle;
public record TxfyrShard(String key,int x,int y,int width,int height)
	implements Rectangle<Integer>
{
	public TxfyrShard
	{
		assert key!=null;
		assert x>=0&&y>=0;
		assert width>0&&height>0;
	}
	
	@Override public Integer area()
	{
		return width*height;
	}
	
	@Override public Integer perimeter()
	{
		return 2*width+2*height;
	}
	
	@Override public Integer diagonal()
	{
		return (int)Math.hypot(width,height);
	}
	
	@Override public String toString()
	{
		return "TxfyrShard[\n\tname="+key+"\n\tx="+x+"\n\ty="+y+"\n\twidth="+width+"\n\theight="+height+"]";
	}
}
