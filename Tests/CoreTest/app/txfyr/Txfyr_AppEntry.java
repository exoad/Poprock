package app.txfyr;

import net.exoad.txfyr.Txfyr;
import net.exoad.txfyr.TxfyrReader;

import static java.lang.System.out;
class Txfyr_AppEntry
{
	public static void main(String[] args)
	{
		final String path=args[0];
		out.println("Txfyr_Version="+Txfyr.getVersion());
		out.println("Is_Valid_Txfyr_File="+Txfyr.isValidTxfyrFile(path));
		StringBuilder sb=new StringBuilder();
		TxfyrReader.exportAllClusters(path);
	}
}
