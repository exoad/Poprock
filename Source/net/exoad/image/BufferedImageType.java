package net.exoad.image;
import pkg.exoad.poprock.core.Wrap;

import java.awt.image.BufferedImage;
import java.util.Arrays;
public class BufferedImageType
{
	/**
	 * Match the suggested buffered image type provided as an integer. If no match is
	 * found, <code>null</code> will be returned.
	 *
	 * @param i The buffered image type called from {@link BufferedImage#getType()}
	 *
	 * @return {@link Type}
	 */
	public static Type of(int i)
	{
		Wrap<Type> type=Wrap.of(null);
		Arrays
			.stream(Type.values())
			.forEach(x->{
				if(x.payload==i)
					type.setValue(x);
			});
		return type.getValue();
	}
	
	public enum Type
	{
		RGB(BufferedImage.TYPE_INT_RGB),
		BGR(BufferedImage.TYPE_INT_BGR),
		ARGB(BufferedImage.TYPE_INT_ARGB),
		ARGB_PRE(BufferedImage.TYPE_INT_ARGB_PRE),
		THREEBYTE_BGR(BufferedImage.TYPE_3BYTE_BGR),
		FOURBYTE_ABGR(BufferedImage.TYPE_4BYTE_ABGR),
		FOURBYTE_ABGR_PRE(BufferedImage.TYPE_4BYTE_ABGR_PRE),
		BYTE_BINARY(BufferedImage.TYPE_BYTE_BINARY),
		USHORT_565_RGB(BufferedImage.TYPE_USHORT_565_RGB),
		USHORT_555_RGB(BufferedImage.TYPE_USHORT_555_RGB),
		BYTE_GRAY(BufferedImage.TYPE_BYTE_GRAY),
		USHORT_GRAY(BufferedImage.TYPE_USHORT_GRAY),
		BYTE_INDEXED(BufferedImage.TYPE_BYTE_INDEXED);
		
		public final int payload;
		
		Type(int i)
		{
			this.payload=i;
		}
	}
}
