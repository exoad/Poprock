package pkg.exoad.poprock.core.registry;
import pkg.exoad.poprock.core.mixins.NamedObjMixin;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
/**
 * <h2>External Registry</h2>
 * This is a registry that is implemented by a third party or can be implemented by a
 * third party. It should barely be used as the options provided by the standard registry
 * services should suffice.
 *
 * @author Jack Meng
 */
public non-sealed class ExternRegistry
	extends BaseRegistry
	implements
	NamedObjMixin
{
	
	@Override public void registerEntry(
		String name,
		RegistryEntry entry
	)
	{
		// break base implementation
	}
	
	@Override protected RegistryEntry acquireEntry(final String name)
	{
		// break base implementation
		return null;
	}
	
	@Override public Optional<Collection<Class<? extends RegistryEntry>>> inferTyping()
	{
		// break the default implementation
		return Optional.empty();
	}
	
	@Override protected void forEach(Consumer<Object> e)
	{
		// break base implementation
	}
}
