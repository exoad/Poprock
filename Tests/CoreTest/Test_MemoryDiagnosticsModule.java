import pkg.exoad.poprock.core.debug.MemoryDiagnosticsModule;

import java.util.ArrayList;
@Section(name="Test Memory diagnostics module accuracy")
public class Test_MemoryDiagnosticsModule
	implements TestRoot,
			   TestMixin
{
	@Override public void test()
	{
		MemoryDiagnosticsModule d=MemoryDiagnosticsModule
			.make(75L)
			.withPump(x->$PRINT(x.toString()));
		d.start();
		ArrayList<Object> e=new ArrayList<>();
		ArrayList<Object> e2=new ArrayList<>();
		for(int i=0;i<1800;i++)
		{
			Object r=new Object();
			e.add(r);
			e2.add(new Object[6][6][6][6][6][6]);
		}
		d.end();
	}
}
