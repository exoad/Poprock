package pkg.exoad.poprock.core;
import org.luaj.vm2.lib.jse.JsePlatform;
public class LuaService
{
	public static void execLua(String lua)
	{
		JsePlatform.standardGlobals().load(lua).call();
	}
}
