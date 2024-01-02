package app.txfyr;

import static java.lang.System.out;

import net.exoad.txfyr.Txfyr;
import net.exoad.txfyr.TxfyrReader;
class Txfyr_AppEntry
{
	public static void main(String[] args)
	{
		final String path="../../../Content/txfyr_test/test.txf";
		out.println("Txfyr_Version="+Txfyr.VERSION);
		out.println("Is_Valid_Txfyr_File="+Txfyr.isValidTxfyrFile(path));
		final var xd=TxfyrReader.exportAllShards(path);
		out.println(xd);
		
	}
}
