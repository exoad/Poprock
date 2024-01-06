package net.exoad.units.builtin;
import net.exoad.units.IUnitFamily;

import java.util.Collection;
public class MetricDistanceUnitFamily
	implements IUnitFamily<Float,MetricDistance>
{
	private static MetricDistanceUnitFamily family;
	
	public MetricDistanceUnitFamily getFamily()
	{
		if(family==null)
			family=new MetricDistanceUnitFamily();
		return family;
	}
	
	private MetricDistanceUnitFamily(){}
	
	@Override public Float submit(
		final Float type,final MetricDistance from,final MetricDistance to
	)
	{
		assert type!=null;
		assert from!=null;
		assert to!=null;
		return null;
	}
	
	@Override public Collection<MetricDistance> exportAllUnits()
	{
		return null;
	}
}
