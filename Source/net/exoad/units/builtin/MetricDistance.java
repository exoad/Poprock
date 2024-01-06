package net.exoad.units.builtin;
public enum MetricDistance
{
	KILOMETER(1E3F,"km"),METER(1E1F,"m"),CENTIMETER(1E-3F,"cm");
	
	final float baseDist;
	final String unitName;
	
	MetricDistance(float i,String unitName)
	{
		this.baseDist=i;
		this.unitName=unitName;
	}
	
	public String getCanonicalName()
	{
		String n=name();
		return n.charAt(0)+n
			.substring(1,n.length()-1)
			.toLowerCase();
	}
	
	public String getUnitName()
	{
		return unitName;
	}
	
	public float getDistanceFromBase()
	{
		return baseDist;
	}
}
