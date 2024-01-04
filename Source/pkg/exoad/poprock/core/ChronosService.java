package pkg.exoad.poprock.core;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
public final class ChronosService
{
	public record Chronos(long time,TimeUnit unit){}
	
	private ChronosService(){}
	
	public static SimpleDateFormat MM_DD_YYYY=new SimpleDateFormat("MM_dd_yyyy");
	
	public static String formatTime(long ms,String format)
	{
		return new SimpleDateFormat(format).format(new Date(ms));
	}
}
