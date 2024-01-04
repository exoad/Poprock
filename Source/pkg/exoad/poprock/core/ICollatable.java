package pkg.exoad.poprock.core;
/**
 * Specifies that this class can be collated in to a different type after method chaining.
 * This is particularly used for Factory classes or classes that involve their
 * construction through <code>with</code> method chaining.
 *
 * @param <T> Often referred to as the "produced" type
 */
public interface ICollatable<T>
{
	/**
	 * Output a different type
	 *
	 * @return The produced object
	 */
	T collate();
}
