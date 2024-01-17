package pkg.exoad.poprock.core.units;
import java.util.LinkedHashSet;
public interface UnitMarker<A extends Enum<A>>
{
	LinkedHashSet<A> exportOrderedUnits();
}
