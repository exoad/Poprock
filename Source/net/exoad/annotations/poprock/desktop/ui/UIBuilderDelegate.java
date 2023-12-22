package net.exoad.annotations.poprock.desktop.ui;

import java.util.function.Supplier;

import javax.swing.JComponent;

/**
 * A builder delegate that allows for the usage of a lambda
 * {@link java.util.function.Supplier} inplace of a regular delegate. This is
 * especially useful for logical delegation that a simple ternary operator
 * cannot suffice.
 * <p>
 * Example Usage
 * <blockquote><pre>
 * UIOtherDelegate().make()
 * .withComponent(
 * UIBuilderDelegate.make(()->
 * {
 * int i=fetchSomeNumber();
 * return GlobalContext.at(i);
 * }
 * )
 * )
 * </pre></blockquote>
 *
 * You could definitely do something similar where the logic for
 * {@code fetchSomeNumber} was handled outside of the root
 * {@code UIOtherDelegate}'s make, but most of the time this is not the case
 *
 * @author Jack Meng
 * @see UIBasicDelegate
 */
public class UIBuilderDelegate<T extends JComponent>
	extends
	UIDelegate<T>
{
	public static <B extends JComponent> UIBuilderDelegate<B> make(
		Supplier<UIDelegate<B>> component
	)
	{
		return new UIBuilderDelegate<>(component);
	}
	
	private Supplier<UIDelegate<T>> builder;
	
	private UIBuilderDelegate(Supplier<UIDelegate<T>> builder)
	{
		this.builder=builder;
	}
	
	@Override public T asComponent()
	{
		return builder
			.get()
			.asComponent();
	}
}
