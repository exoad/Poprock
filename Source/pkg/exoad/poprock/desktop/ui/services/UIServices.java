package pkg.exoad.poprock.desktop.ui.services;
import pkg.exoad.poprock.core.services.DebugService;

import javax.swing.*;
public final class UIServices
{
	private UIServices(){}
	
	private enum PropertyKeyType
	{
		INTERNAL,
		SYSTEM
	}
	
	public enum PropertyKey
	{
		USE_OPENGL(PropertyKeyType.SYSTEM,"sun.java.2d"+
										  ".opengl","true"),
		
		USE_OPENGL_TRACED(PropertyKeyType.SYSTEM,"sun.java"+
												 ".2d"+
												 ".opengl","True"),
		USE_FLATLAF_WINDOW_DECOR(
			PropertyKeyType.SYSTEM,
			"flatlaf"+
			".useWindowDecorations",
			"true"
		),
		USE_FLATLAF_EMBEDDED_MENUBAR(PropertyKeyType.SYSTEM,"flatlaf"+
															".menuBarEmbeded"
			,"true");
		final PropertyKeyType type;
		final String keyName;
		final Object value;
		
		PropertyKey(PropertyKeyType type,String keyName,Object value)
		{
			this.type   =type;
			this.keyName=keyName;
			this.value  =value;
		}
		
	}
	
	public static void propagate(
		PropertyKey... key
	)
	{
		DebugService.panicOn(key==null,"Property keys cannot be null for "+
									   "propagation!");
		for(PropertyKey k: key)
		{
			DebugService.panicOn(k==null,"A property key was found to be "+
										 "null for propagation!");
			if(k.type==PropertyKeyType.SYSTEM)
				System.setProperty(k.keyName,k.value.toString());
			else if(k.type==PropertyKeyType.INTERNAL)
				UIManager.put(k.keyName,k.value);
		}
	}
}
