package pkg.exoad.poprock.core.units;
import java.util.Collection;
/**
 * Interface for a singular unit family
 *
 * @param <U> base type for conversion utility
 * @param <A> base unit from a to b
 */
public interface IUnitFamily<U,A extends Enum<A>>
{
	U submit(U type,A from,A to);
	
	Collection<A> exportAllUnits();
	
	Class<A> getBaseClass();
}
