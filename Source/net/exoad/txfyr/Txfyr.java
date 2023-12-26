package net.exoad.txfyr;
import pkg.exoad.poprock.core.struct.Result;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
public final class Txfyr
{
	private Txfyr(){}
	
	public static long getVersion()
	{
		return 2023_12_25L;
	}
	
	public static Optional<ZipEntry[]> exportAllIdentifiers(String path)
	{
		File f=new File(path);
		if(path.endsWith(".txf")&&f.canWrite()&&f.canRead()&&f.isFile()&&!f.isDirectory())
		{
			try(ZipFile zipFile=new ZipFile(path))
			{
				Enumeration<? extends ZipEntry> entries=zipFile.entries();
				ArrayList<ZipEntry> stash=new ArrayList<>();
				while(entries.hasMoreElements())
				{
					ZipEntry entry=entries.nextElement();
					if(entry
						.getName()
						.endsWith(".txfyr"))
						stash.add(entry);
				}
				return Optional.of(stash.toArray(new ZipEntry[0]));
			}catch(IOException e)
			{
				e.printStackTrace();
				return Optional.empty();
			}
		}
		return Optional.empty();
	}
	
	public static Result<Boolean,TxfyrValidityReason> isValidTxfyrFile(String path)
	{
		if(!path.endsWith(".txf"))
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
					.endsWith(".txfyr"))
					return Result.make(true,TxfyrValidityReason.VALID_FILE);
			}
			return Result.make(false,TxfyrValidityReason.NO_TXFYR_IDENTIFIER);
		}catch(IOException e)
		{
			e.printStackTrace();
			return Result.make(false,TxfyrValidityReason.EXCEPTION_CAUGHT);
		}
	}
	
	public enum TxfyrValidityReason
	{
		INCORRECT_FILE_EXTENSION,
		IS_NOT_VALID_FILE,
		EXCEPTION_CAUGHT,
		NO_TXFYR_IDENTIFIER,VALID_FILE;
	}
}
