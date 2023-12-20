package pkg.exoad.softgradient.core;
/**
 * <h2>Functor</h2>
 *
 * This mixin provides and verification that the inheriting class or interface
 * is of a functor type.
 * <p>
 * Functors are marked by their 2 digit code at the end usually in a format
 * like
 * <code>FunctorXX</code> where XX denotes the two numbers. However, there
 * are symbols that are used. For example "?" which is used to denoted the use
 * of an <code>Optional</code> and is found commonly in the return type.
 * </p>
 *
 * <h3>Number code</h3>
 * The two digit number code have great significance on how they reveal what
 * that functor should be used for. An example could be
 * <code>Functor?12</code> which represents a function that has a singular
 * <code>Optional</code> return type along with 2 parameters.
 * <p>
 * <strong>First Digit</strong>
 * <br/> This represents how many return values are returned by that function.
 * Usually it is between 0 or 1.
 * </p>
 * <p>
 * <strong>Second Digit</strong>
 * <br/> This represents how many parameters are expected and usually does not
 * have any symbols representing it.
 * </p>
 *
 * <h4>Oh no! My functor needs to have 30 parameter types!</h4> First of all,
 * you are doing something wrong in your code. Anyways, a Functor that needs to
 * go into more than a single digit for one of its number codes will use the "$"
 * code separate the two indicators. Thus <code>Functor1$30</code> will
 * represent one return type followed by 30 parameter types.
 *
 * @author Jack Meng
 */
public interface Functor
{
}
