package pkg.exoad.poprock.desktop.ui.services;
import javax.swing.*;
import pkg.exoad.poprock.core.debug.DebugService;
public final class UIServices
{
	private UIServices(){}
	
	public static void propagate(
		PropertyKey... key
	)
	{
		DebugService.panicOn(key==null,"Property keys cannot be null for "+
									   "propagation!");
		assert key!=null;
		for(PropertyKey k: key)
		{
			DebugService.panicOn(k==null,"A property key was found to be "+
										 "null for propagation!");
			assert k!=null;
			if(k.type==PropertyKeyType.SYSTEM)
			{
				System.setProperty(k.keyName,k.value.toString());
				DebugService.log(DebugService.LogLevel.INFO,"Set system "+
															"property "+
															k.keyName+
															" to "+
															k.value);
			}
			else if(k.type==PropertyKeyType.INTERNAL)
			{
				UIManager.put(k.keyName,k.value);
				DebugService.log(DebugService.LogLevel.INFO,"Set UI "+
															"property "+
															k.keyName+
															" to "+
															k.value);
			}
		}
	}
	
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
}
