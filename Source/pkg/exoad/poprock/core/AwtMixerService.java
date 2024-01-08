package pkg.exoad.poprock.core;

import net.exoad.annotations.ServiceClass;
import pkg.exoad.poprock.desktop.ui.Offset;
import pkg.exoad.poprock.desktop.ui.Size;

import java.awt.Point;
import java.awt.Dimension;

/**
 * AwtMixerService - A utility class designed to convert from common AWT Utility
 * classes to Poprock versions. For example, instead of having to import
 * {@link java.awt.Dimension}, you can easily just use Poprock's core
 * {@link Size} when importing
 * {@link pkg.exoad.poprock.core}
 *
 * @author Jack Meng
 */
@ServiceClass(requiresArming=false)
public final class AwtMixerService
{
	private AwtMixerService()
	{
	}
	
	/**
	 * Converts from an AWT Point class to a Poprock Offset class which is
	 * basically the same thing.
	 *
	 * @param point The AWT Point object
	 *
	 * @return The Poprock Offset object
	 *
	 * @see java.awt.Point
	 * @see Offset
	 */
	public static Offset convertPointClass(Point point)
	{
		return new Offset(
			point.x,
			point.y
		);
	}
	
	/**
	 * Converts from an AWT Dimension class to a Poprock Size class.
	 *
	 * @param dim The AWT Dimension object
	 *
	 * @return The Poprock Size object
	 *
	 * @see java.awt.Dimension
	 * @see Size
	 */
	public static Size convertDimensionClass(Dimension dim)
	{
		return new Size(
			dim.width,
			dim.height
		);
		
	}
}
