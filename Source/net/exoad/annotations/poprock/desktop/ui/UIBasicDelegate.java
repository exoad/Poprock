package net.exoad.annotations.poprock.desktop.ui;

import net.exoad.annotations.poprock.core.services.mixins.SelfReportingMixin;

import javax.swing.*;

/**
 * Root of all UI delegates to extend from and the main extrapolation from the
 * AWT/Swing roots.
 * <p>
 * <h2>Delegation</h2>
 * In Poprock UI building, delegation stands for an express way to build UI
 * elements with a framework like Java Swing/AWT. A user should only modify an
 * element on creation and not use that same object elsewhere (similar to
 * exposing that element in a public {@code SharedConstants} class).
 * <p>
 * In regular Java Swing or other imperative frameworks during UI building, you
 * will end up with the following code:
 * <blockquote><pre>
 * Frame f=Frame();
 * f.width=300;
 * f.height=400;
 *
 * ContentPane p=ContentPane();
 * p.width=300;
 * p.height=400;
 * p.painter= (Canvas canvas) => {
 * canvas.color=GREEN;
 * canvas.fillRect(0,0,300,400);
 * }
 * </pre></blockquote>
 *
 * This can cause code to look very linear and very hard to debug. Instead,
 * Poprock tries to take it a reactive way to build applications but still
 * being imperative whenever the programmer wants:
 *
 * <blockquote><pre>
 * UIButtonDelegate.make()
 * .withHeight(300)
 * .withWidth(400)
 * .withChild(
 * UIPainterPanel.make()
 * .withLateCanvasPainter(g->
 * g.setColor(Colors.GREEN);
 * g.fillRect(0,0,300,400);
 * )
 * );
 * </pre></blockquote>
 *
 * There are no residue to the components because we only need them for
 * construction and not for any advanced setting.
 *
 * @author Jack Meng
 */
@SwingContainer public interface UIBasicDelegate<T extends JComponent>
	extends
	SelfReportingMixin
{
	/**
	 * Calls the underlying {@link javax.swing.JComponent#repaint()} for
	 * graphical management
	 */
	default void refresh()
	{
		asComponent()
			.repaint();
	}
	
	/**
	 * Converts to a Swing Lightweight component form
	 *
	 * @return
	 */
	public T asComponent();
	
	/**
	 * Calls the underlying {@link javax.swing.JComponent#revalidate()} for
	 * layout management
	 */
	public default void hardRefresh()
	{
		asComponent()
			.revalidate();
	}
}
