package pkg.exoad.poprock;
/**
 * One return. Ten parameters
 *
 * @param <R> Return value
 * @param <A> 1
 * @param <B> 2
 * @param <C> 3
 * @param <D> 4
 * @param <E> 5
 * @param <F> 6
 * @param <G> 7
 * @param <H> 8
 * @param <I> 9
 * @param <J> 10
 */
@FunctionalInterface
public interface Functor1$10<R,A,B,C,D,E,F,G,H,I,J>
	extends Functor
{
	R call(
		A one,B second,C third,D four,E five,F six,G seven,H eight,I nine,
		J ten
	);
}
