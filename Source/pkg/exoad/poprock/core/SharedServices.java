package pkg.exoad.poprock.core;
import pkg.exoad.poprock.core.annotations.ServiceClass;
import pkg.exoad.poprock.core.debug.DebugService;
import pkg.exoad.poprock.core.debug.DiagnosticsMode;

import java.util.Optional;
import java.util.TreeMap;
@ServiceClass(requiresArming=false)
public final class SharedServices
{
	private SharedServices(){}
	
	private static final /*preserve ordering*/ TreeMap<String,Object> properties;
	
	static
	{
		properties=new TreeMap<>();
		// load default properties
		properties.put("registry_services.allow_nullable_value",true);
		properties.put("debug.volume",DiagnosticsMode.FAIL_FAST);
	}
	
	private static void noContain(String r)
	{
		DebugService.log(
			DebugService.LogLevel.WARN,
			"Key: "+r+" was not found "+
			"in the shared registry! Doing nothing..."
		);
	}
	
	private static void noSameType(String r,Object value)
	{
		DebugService.log(DebugService.LogLevel.WARN,"Key: "+r+" has an "+
													"incompatible type "+
													"of: "+value
														.getClass()
														.getCanonicalName()+" conflicts with allowed "+properties
														.get(r)
														.getClass()
														.getCanonicalName()+" Do nothing...");
	}
	
	/**
	 * Converts the desired entry value into the input value. "set"
	 *
	 * @param r The value name
	 * @param value The supplied value
	 */
	public static void set(String r,Object value)
	{
		if(!properties.containsKey(r))
			noContain(r);
		else if(!properties
			.get(r)
			.getClass()
			.equals(value.getClass()))
			noSameType(r,value);
		else
			properties.put(r,value);
	}
	
	public static Optional<Object> get(String r)
	{
		if(!properties.containsKey(r))
		{
			noContain(r);
			return Optional.empty();
		}
		return Optional.of(properties.get(r));
	}
	
	public static Optional<Boolean> getBool(String r)
	{
		if(!properties.containsKey(r)||
		   !properties
			   .get(r)
			   .getClass()
			   .equals(Boolean.class)||get(r).isEmpty())
		{
			DebugService.log(
				DebugService.LogLevel.WARN,
				"The supplied key "+r+"was found to be invalid"+
				" in the registry for fit: "+Boolean.class.getCanonicalName()
			);
			return Optional.empty();
		}
		return Optional.of((Boolean)get(r).get()); // this is so unsafe bc of
		// incorrect type checking at runtime produced by stupid shitty type
		// erasure, but
		// it should work 99% of the time
	}
}
