package net.exoad.platform;
import java.io.IOException;
/**
 * Base service class for some very basic information regarding the system
 *
 * @author Jack Meng
 */
public final class Platform
{
	private Platform(){}
	
	/**
	 * Get the OS version (os.version)
	 *
	 * @return The OS version
	 */
	public static String getOSVersion()
	{
		return System.getProperty("os.version");
	}
	
	/**
	 * Whether the system is a desktop Windows system. This function does not
	 * check if the system is supported or not.
	 *
	 * @return Whether the system is a desktop Windows system
	 */
	public static boolean isWindowsDesktop()
	{
		return getPlatform()==PlatformKey.WINDOWS_11||
			   getPlatform()==PlatformKey.WINDOWS_10||
			   getPlatform()==PlatformKey.WINDOWS_8_1||
			   getPlatform()==PlatformKey.WINDOWS_8||
			   getPlatform()==PlatformKey.WINDOWS_7||
			   getPlatform()==PlatformKey.WINDOWS_VISTA||
			   getPlatform()==PlatformKey.WINDOWS_XP||
			   getPlatform()==PlatformKey.WINDOWS_2003||
			   getPlatform()==PlatformKey.WINDOWS_2000;
	}
	
	/**
	 * Get the platform of the system (os.name)
	 *
	 * @return The platform of the system
	 */
	public static PlatformKey getPlatform()
	{
		String osName=System.getProperty("os.name");
		if(osName.contains("Windows"))
		{
			if(osName.contains("11"))
				return PlatformKey.WINDOWS_11;
			else if(osName.contains("10"))
				return PlatformKey.WINDOWS_10;
			else if(osName.contains("8.1"))
				return PlatformKey.WINDOWS_8_1;
			else if(osName.contains("8"))
				return PlatformKey.WINDOWS_8;
			else if(osName.contains("7"))
				return PlatformKey.WINDOWS_7;
			else if(osName.contains("NT"))
				return PlatformKey.WINDOWS_NT;
			else if(osName.contains("Server 2008"))
				return PlatformKey.WINDOWS_SERVER_2008;
			else if(osName.contains("Server 2008 R2"))
				return PlatformKey.WINDOWS_SERVER_2008_R2;
			else if(osName.contains("Server 2012"))
				return PlatformKey.WINDOWS_SERVER_2012;
			else if(osName.contains("Server 2012 R2"))
				return PlatformKey.WINDOWS_SERVER_2012_R2;
			else if(osName.contains("Server 2016"))
				return PlatformKey.WINDOWS_SERVER_2016;
			else if(osName.contains("Server 2019"))
				return PlatformKey.WINDOWS_SERVER_2019;
			else if(osName.contains("Server 2022"))
				return PlatformKey.WINDOWS_SERVER_2022;
			else if(osName.contains("Vista"))
				return PlatformKey.WINDOWS_VISTA;
			else if(osName.contains("XP"))
				return PlatformKey.WINDOWS_XP;
			else if(osName.contains("2003"))
				return PlatformKey.WINDOWS_2003;
			else if(osName.contains("2000"))
				return PlatformKey.WINDOWS_2000;
			else
				return PlatformKey.WINDOWS_UNKNOWN;
		}
		else if(osName.contains("Mac OS X"))
			return PlatformKey.MACOS;
		else if(osName.contains("Linux"))
			return PlatformKey.UNIX;
		else if(osName.contains("Solaris"))
			return PlatformKey.SOLARIS;
		else
			return PlatformKey.UNKNOWN;
	}
	
	/**
	 * Whether the system is supported or not
	 *
	 * @return Whether the system is supported or not
	 */
	public static boolean isPlatformSupported()
	{
		return getPlatform().isSupported();
	}
	
	/**
	 * Whether the system is a MACOS system
	 *
	 * @return Whether the system is a MACOS system
	 */
	public static boolean isMacos()
	{
		return getPlatform()==PlatformKey.MACOS;
	}
	
	/**
	 * Whether the system is a UNIX based machine
	 *
	 * @return Whether the system is a UNIX based machine
	 */
	public static boolean isUnix()
	{
		return getPlatform()==PlatformKey.UNIX;
	}
	
	/**
	 * Whether the system's architecture is supported or not
	 *
	 * @return Whether the system's architecture is supported or not
	 */
	public static boolean isArchSupported()
	{
		return getArch().isSupported();
	}
	
	/**
	 * Get the architecture of the system (os.arch)
	 *
	 * @return The architecture of the system
	 */
	public static ArchKey getArch()
	{
		String osArch=System.getProperty("os.arch");
		if(osArch.contains("x86"))
		{
			if(osArch.contains("32"))
				return ArchKey.X86_32;
			else if(osArch.contains("64"))
				return ArchKey.X86_64;
			else
				return ArchKey.UNKNOWN;
		}
		else if(osArch.contains("arm"))
		{
			if(osArch.contains("64"))
				return ArchKey.ARM_64;
			else
				return ArchKey.ARM;
		}
		else if(osArch.contains("ppc"))
			return ArchKey.PPC;
		else if(osArch.contains("ia64"))
			return ArchKey.IA64;
		else if(osArch.contains("sparc"))
			return ArchKey.SPARC;
		else
			return ArchKey.UNKNOWN;
	}
	
	/**
	 * Executes a command in the system using Runtime.getRuntime().exec()
	 *
	 * @param command The command to execute
	 *
	 * @throws IOException If the command cannot be executed
	 */
	public static void execCommand(String command)
		throws
		IOException
	{
		Runtime
			.getRuntime()
			.exec(command);
	}
	
	/**
	 * UNIX "uname"
	 *
	 * @return The output of "uname"
	 */
	public static String __unix_UNAME()
	{
		ProcessBuilder pb=new ProcessBuilder("uname");
		try
		{
			Process p=pb.start();
			p.waitFor();
			return new String(p
								  .getInputStream()
								  .readAllBytes());
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * UNIX "export"
	 *
	 * @param key The key
	 * @param value The value
	 */
	public static void __unix_SETENV(String key,String value)
	{
		ProcessBuilder pb=new ProcessBuilder("export",key+"="+value);
		try
		{
			Process p=pb.start();
			p.waitFor();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
