package pkg.exoad.poprock.core.txfyr;
import pkg.exoad.poprock.core.Result;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
public final class Txfyr
{
	public static final long VERSION=2024_01_01L;
	public static final String[] ALLOWED_FILE_TYPES_EXTENSIONS=new String[]{"png","jpg","jpeg"};
	
	private Txfyr(){}
	
	public static Result<Boolean,TxfyrValidityReason> isValidTxfyrFile(String path)
	{
		if(!path.endsWith(getPackageFileExtension()))
			return Result.make(false,TxfyrValidityReason.INCORRECT_FILE_EXTENSION);
		File f=new File(path);
		if(f.isDirectory()||!f.isFile()||f.isDirectory()||!f.canRead()||!f.canWrite())
			return Result.make(false,TxfyrValidityReason.IS_NOT_VALID_FILE);
		try(ZipFile zipFile=new ZipFile(path))
		{
			Enumeration<? extends ZipEntry> entries=zipFile.entries();
			while(entries.hasMoreElements())
			{
				ZipEntry entry=entries.nextElement();
				if(entry
					.getName()
					.endsWith(getIdentifierFileExtension()))
					return Result.make(true,TxfyrValidityReason.VALID_FILE);
			}
			return Result.make(false,TxfyrValidityReason.NO_TXFYR_IDENTIFIER);
		}catch(IOException e)
		{
			e.printStackTrace();
			return Result.make(false,TxfyrValidityReason.EXCEPTION_CAUGHT);
		}
	}
	
	public static String getPackageFileExtension()
	{
		return ".txf";
	}
	
	public static String getIdentifierFileExtension()
	{
		return ".txfyr";
	}
	
	public enum TxfyrValidityReason
	{
		INCORRECT_FILE_EXTENSION,
		IS_NOT_VALID_FILE,
		EXCEPTION_CAUGHT,
		NO_TXFYR_IDENTIFIER,VALID_FILE
	}
}
