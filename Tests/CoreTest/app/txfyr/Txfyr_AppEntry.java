package app.txfyr;

import net.exoad.txfyr.Txfyr;

import java.util.zip.ZipEntry;

import static java.lang.System.out;
class Txfyr_AppEntry
{
	public static void main(String[] args)
	{
		final String path=args[0];
		out.println("Txfyr_Version="+Txfyr.getVersion());
		out.println("Is_Valid_Txfyr_File="+Txfyr.isValidTxfyrFile(path));
		StringBuilder sb=new StringBuilder();
		Txfyr
			.exportAllIdentifiers(path)
			.ifPresentOrElse(x->{
				for(ZipEntry e: x)
					sb
						.append("[")
						.append(e.getName())
						.append("]");
			},()->sb.append("null"));
		out.println("All_Txfyr_Identifiers="+sb);
	}
}
