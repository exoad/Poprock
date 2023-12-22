package pkg.exoad.poprock.core;

import java.util.Optional;

/**
 * A general purpose interface used for any classes that want to return type
 * inferencing because they either handle generics (and thus type erasure) OR it
 * must handle multiple types (meaning everything is reduced to Objects).
 *
 * @param <B> The type to be inferred.
 */
public interface ITypeInferencing<B>
{
	/**
	 * The class may or may not return a valid type inferencing due to internal
	 * reasons, so an <code>Optional</code> is returned.
	 *
	 * @return The typing inferred from the internal representation of the class
	 */
	Optional<B> inferTyping();
}
