package pkg.exoad.poprock.core.services.mixins;

import pkg.exoad.poprock.core.Shared;
import net.exoad.annotations.NotVirtual;

import java.util.HashMap;

public interface NamedObjMixin
{
	final HashMap<NamedObjMixin,Shared<String>> OBJECTS=new HashMap<>();
	
	@NotVirtual public default NamedObjMixin withObjectName(String newName)
	{
		setObjectName(newName);
		return this;
	}
	
	@NotVirtual public default void setObjectName(String newName)
	{
		if(!OBJECTS
			.containsKey(this))
			OBJECTS
				.put(
					this,
					Shared
						.of(newName)
				);
		else
			OBJECTS
				.get(this)
				.setValue(newName);
	}
	
	@NotVirtual public default String getObjectName()
	{
		return OBJECTS
			.containsKey(this)?OBJECTS
			.get(this)
			.getValue():"";
	}
}
