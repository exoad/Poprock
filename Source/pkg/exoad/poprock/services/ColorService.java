package pkg.exoad.poprock.services;

import java.awt.Color;

import pkg.exoad.poprock.annotations.ServiceClass;
import pkg.exoad.poprock.ColorObj;

/**
 * Color Service - A utility class for providing basic operations on Colors.
 * However, it is not used for more complicated color operations.
 *
 * @author Jack Meng
 * @see java.awt.Color
 * @see ColorObj
 */
@ServiceClass(requiresArming=false)
public final class ColorService
{
	private ColorService()
	{
	}
	
	/**
	 * <strong>AWT Function</strong>
	 * <br/> This function converts from a HEX Color code #FFFFFF to a color
	 * object. <br/> The format must follow "#ffffff" with a hashtag at the
	 * start and 6 trailing hexadecimal characters.
	 *
	 * @param hex The provided hex.
	 *
	 * @return The Color Object
	 */
	public static ColorObj hexToColor(String hex)
	{
		return new ColorObj(
			Integer
				.valueOf(
					hex // here we ignore the first symbol which should be a hashtag, but we really don't care, so technically it could be anything
						.substring(
							1,
							3
						),
					16
				),
			Integer
				.valueOf(
					hex
						.substring(
							3,
							5
						),
					16
				),
			Integer
				.valueOf(
					hex
						.substring(
							5,
							7
						),
					16
				)
		);
	}
	
	public static Color hexToColorAWT(String hex)
	{
		return new Color(
			Integer
				.valueOf(
					hex // here we ignore the first symbol which should be a hashtag, but we really don't care, so technically it could be anything
						.substring(
							1,
							3
						),
					16
				),
			Integer
				.valueOf(
					hex
						.substring(
							3,
							5
						),
					16
				),
			Integer
				.valueOf(
					hex
						.substring(
							5,
							7
						),
					16
				)
		);
	}
	
	/**
	 * <strong>AWT Function</strong>
	 * Takes a color object and inverts the color values. For example, the
	 * invert of WHITE would be BLACK.
	 *
	 * @param r The AWT Color Object to invert
	 *
	 * @return The invertted AWT Color Object
	 */
	public static Color invertColor(Color r)
	{
		return new Color(
			255-r
				.getRed(),
			255-r
				.getGreen(),
			255-r
				.getBlue()
		);
	}
}
