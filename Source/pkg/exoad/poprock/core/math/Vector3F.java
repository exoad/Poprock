package pkg.exoad.poprock.core.math;
public class Vector3F
{
	public float x, y, z;
	
	public Vector3F()
	{
		this.x=0.0F;
		this.y=0.0F;
		this.z=0.0F;
	}
	
	public Vector3F(float x,float y,float z)
	{
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public Vector3F cross(Vector3F e)
	{
		return new Vector3F(
			y*e.z-z*e.y,
			z*e.x-x*e.z,
			x*e.y-y*e.x
		);
	}
	
	public float dist(Vector3F e)
	{
		return (float)Math.sqrt((x-e.x)*(x-e.x)+(y-e.y)*(y-e.y)+(z-e.z)*(z-e.z));
	}
	
	public Vector3F norm()
	{
		return div(mag());
	}
	
	public Vector3F div(float k)
	{
		return new Vector3F(x/k,y/k,z/k);
	}
	
	public float mag()
	{
		return (float)Math.sqrt(x*x+y*y+z*z);
	}
	
	public float theta()
	{
		return (float)Math.atan2(y,x);
	}
	
	public Vector3F rot0(float theta)
	{
		return new Vector3F(
			(float)(x*Math.cos(theta)-y*Math.sin(theta)),
			(float)(x*Math.sin(theta)+y*Math.cos(theta)),
			z
		);
	}
	
	public void rot1(float theta)
	{
		this.x=(float)(x*Math.cos(theta)-y*Math.sin(theta));
		this.y=(float)(x*Math.sin(theta)+y*Math.cos(theta));
	}
}
