package pkg.exoad.poprock.core.math;
public class Matrix2
{
	public float[][] m;
	
	public Matrix2()
	{
		this.m=new float[2][2];
	}
	
	public Matrix2(float[][] m)
	{
		this.m=m;
	}
	
	public Matrix2(float a,float b,float c,float d)
	{
		this.m=new float[2][2];
		this.m[0][0]=a;
		this.m[0][1]=b;
		this.m[1][0]=c;
		this.m[1][1]=d;
	}
	
	public Matrix2 add(Matrix2 e)
	{
		return new Matrix2(
			m[0][0]+e.m[0][0],m[0][1]+e.m[0][1],
			m[1][0]+e.m[1][0],m[1][1]+e.m[1][1]
		);
	}
	
	public Matrix2 sub(Matrix2 e)
	{
		return new Matrix2(
			m[0][0]-e.m[0][0],m[0][1]-e.m[0][1],
			m[1][0]-e.m[1][0],m[1][1]-e.m[1][1]
		);
	}
	
	public Matrix2 mul(float k)
	{
		return new Matrix2(
			m[0][0]*k,m[0][1]*k,
			m[1][0]*k,m[1][1]*k
		);
	}
	
	public Matrix2 inv()
	{
		return new Matrix2(
			m[1][1],-m[0][1],
			-m[1][0],m[0][0]
		).div(det());
	}
	
	public Matrix2 div(float k)
	{
		return new Matrix2(
			m[0][0]/k,m[0][1]/k,
			m[1][0]/k,m[1][1]/k
		);
	}
	
	public float det()
	{
		return m[0][0]*m[1][1]-m[0][1]*m[1][0];
	}
	
	public Matrix2 transpose()
	{
		return new Matrix2(
			m[0][0],m[1][0],
			m[0][1],m[1][1]
		);
	}
	
	public Matrix2 rot0(float theta)
	{
		return mul(rot(theta));
	}
	
	public Matrix2 mul(Matrix2 e)
	{
		return new Matrix2(
			m[0][0]*e.m[0][0]+m[0][1]*e.m[1][0],
			m[0][0]*e.m[0][1]+m[0][1]*e.m[1][1],
			m[1][0]*e.m[0][0]+m[1][1]*e.m[1][0],
			m[1][0]*e.m[0][1]+m[1][1]*e.m[1][1]
		);
	}
	
	public Matrix2 rot(float theta)
	{
		return new Matrix2(
			(float)Math.cos(theta),(float)-Math.sin(theta),
			(float)Math.sin(theta),(float)Math.cos(theta)
		);
	}
	
	public Matrix2 rot0(float x,float y,float z)
	{
		return rot0(new Vector3F(x,y,z));
	}
	
	public Matrix2 rot0(Vector3F v)
	{
		return rot0(v.theta());
	}
	
	public void rot1(float x,float y,float z)
	{
		rot1(new Vector3F(x,y,z));
	}
	
	public void rot1(Vector3F v)
	{
		rot1(v.theta());
	}
	
	public void rot1(float theta)
	{
		m=rot(theta).mul(this).m;
	}
	
	public Matrix2 rot0(float x,float y)
	{
		return rot0(new Vector2F(x,y));
	}
	
	public Matrix2 rot0(Vector2F v)
	{
		return rot0(v.theta());
	}
	
	public void rot1(float x,float y)
	{
		rot1(new Vector2F(x,y));
	}
	
	public void rot1(Vector2F v)
	{
		rot1(v.theta());
	}
}
