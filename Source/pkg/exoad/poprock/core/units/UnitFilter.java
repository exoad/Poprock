package pkg.exoad.poprock.core.units;
public record UnitFilter<C,B extends IUnitFamily<B,A>,A extends Enum<A>>(
	B instance,A from,A to
)
{
}
