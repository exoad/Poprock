package pkg.exoad.poprock.core.halite.def;

import pkg.exoad.poprock.core.Functor11;
import pkg.exoad.poprock.core.halite.def.builtin.impl_DGuard;
import pkg.exoad.poprock.core.halite.impl_Modifier;

import java.util.Optional;

/**
 * This is the base definition for a single "property" in a
 * property file.
 *
 * @author Jack Meng
 */
public class use_Def<T>
	implements
	Functor11<Boolean,String>
{
	
	public final String key, property_name;
	public final T property_default_value;
	public final impl_DGuard[] coalesce;
	private final Optional<impl_Modifier<String>> modifier;
	public Optional<String> opt_Descriptor;
	private def_State myState=def_State.NOT_LOADED;
	private Optional<Runnable> actionpotential;
	
	/**
	 * @param key The Key Name, this is not the formal name
	 * of the key.
	 * @param property_name The formal property_name
	 * used in
	 * the property files.
	 * @param property_default_value The default value of
	 * this property structure returned when value was
	 * invalidated. This property can be nullable and
	 * should
	 * ONLY BE NULLABLE when the INVALIDATION_STATE has
	 * been
	 * set to "CRITICAL_EFFORT"
	 * @param e The guard coalescing group to use
	 */
	public use_Def(
		String key,String property_name,
		T property_default_value,impl_DGuard... e
	)
	{
		this(
			null,
			null,
			null,
			key,
			property_name,
			property_default_value,
			e
		);
	}
	
	/**
	 * @param potential_modifier This modifier can be used
	 * for when the desired value might need altering after
	 * all checks have passed. It is strongly advised that
	 * the programmer implement a modifier so that there
	 * are
	 * no type casting inconsistencies.
	 * @param action The action to run when this definition
	 * is being loaded. This action is run as a none
	 * blocking action initiated by default by
	 * HaliteLoader.
	 * @param descriptor A basic description attribtue of
	 * this property_definition. May be optional
	 * @param key The Key Name, this is not the formal name
	 * of the key.
	 * @param property_name The formal property_name
	 * used in
	 * the property files.
	 * @param property_default_value The default value of
	 * this property structure returned when value was
	 * invalidated. This property can be nullable and
	 * should
	 * ONLY BE NULLABLE when the INVALIDATION_STATE has
	 * been
	 * set to "CRITICAL_EFFORT"
	 * @param e The guard coalescing group to use
	 */
	public use_Def(
		impl_Modifier<String> potential_modifier,
		Runnable action,String descriptor,
		String key,String property_name,
		T property_default_value,
		impl_DGuard... e
	)
	{
		this.key                   =key;
		this.modifier              =
			potential_modifier==null?Optional.empty():
				Optional.of(
					potential_modifier);
		this.actionpotential       =action==null?
			Optional.empty():Optional.of(
			action);
		this.property_default_value=property_default_value;
		this.property_name         =property_name;
		this.coalesce              =e;
		this.opt_Descriptor        =descriptor==null?
			Optional.empty():Optional.of(
			descriptor);
	}
	
	/**
	 * @return The current state of this definition
	 */
	public def_State state()
	{
		return myState;
	}
	
	/**
	 * @return The desired action to run before this
	 * property_definition is loaded by the HaliteLoader
	 */
	public Optional<Runnable> dawn_action()
	{
		return actionpotential;
	}
	
	public Optional<impl_Modifier<String>> modifier()
	{
		return modifier;
	}
	
	@Override public Boolean call(String arg0)
	{
		boolean r=true;
		for(impl_DGuard e: coalesce)
			r=e.check(arg0);
		myState=r?def_State.VALID:def_State.INVALID;
		return r;
	}
	
	/**
	 * Defines a bunch of definition states that it can be
	 * in during runtime.
	 *
	 * @author Jack Meng
	 */
	public enum def_State
	{
		/**
		 * A not loaded state means the property has not
		 * been read and processed yet.
		 */
		NOT_LOADED,
		/**
		 * The encountered property_value was marked by the
		 * specified guards as "valid"
		 */
		VALID,
		/**
		 * The encountered property_value was marked by the
		 * specified guard as "invalid"
		 */
		INVALID
	}
}
