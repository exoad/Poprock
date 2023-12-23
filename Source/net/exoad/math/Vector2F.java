package net.exoad.math;
public class Vector2F
{
	public float x, y;
	
	public Vector2F()
	{
		this.x=0.0F;
		this.y=0.0F;
	}
	
	public Vector2F(float x,float y)
	{
		this.x=x;
		this.y=y;
	}
	
	public Vector2F cross(Vector2F e)
	{
		return new Vector2F(y*e.x-x*e.y,x*e.y-y*e.x);
	}
	
	public float dist(Vector2F e)
	{
		return (float)Math.sqrt((x-e.x)*(x-e.x)+(y-e.y)*(y-e.y));
	}
	
	public Vector2F norm()
	{
		return div(mag());
	}
	
	public Vector2F div(float k)
	{
		return new Vector2F(x/k,y/k);
	}
	
	public float mag()
	{
		return (float)Math.sqrt(x*x+y*y);
	}
	
	public float theta()
	{
		return (float)Math.atan2(y,x);
	}
	
	public Vector2F rot0(float theta)
	{
		return new Vector2F(
			(float)(x*Math.cos(theta)-y*Math.sin(theta)),
			(float)(x*Math.sin(theta)+y*Math.cos(theta))
		);
	}
	
	public void rot1(float theta)
	{
		this.x=(float)(x*Math.cos(theta)-y*Math.sin(theta));
		this.y=(float)(x*Math.sin(theta)+y*Math.cos(theta));
	}
	
	public Vector2F lerp(Vector2F vec,float a)
	{
		return add(vec
					   .sub(this)
					   .mult(a));
	}
	
	public Vector2F add(Vector2F e)
	{
		return new Vector2F(x+e.x,y+e.y);
	}
	
	public Vector2F sub(Vector2F e)
	{
		return new Vector2F(x-e.x,y-e.y);
	}
	
	public Vector2F mult(float k)
	{
		return new Vector2F(k*x,k*y);
	}
	
	public Vector2F reflect(Vector2F vec)
	{
		return sub(vec.mult(2*dot(vec)));
	}
	
	public float dot(Vector2F e)
	{
		return x*e.x+y*e.y;
	}
}
