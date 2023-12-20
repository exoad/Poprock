package pkg.exoad.softgradient.tests;
import pkg.exoad.softgradient.tests.lib.Section;
import pkg.exoad.softgradient.tests.lib.TestRoot;

import java.util.Arrays;
public class TestMain
{
	
	public static void main(String[] args)
	{
		for(int i=0;i<Config.TESTS.length;i++)
		{
			// pre validation
			TestRoot r=Config.TESTS[i];
			if(Arrays
				.stream(r
							.getClass()
							.getDeclaredAnnotations())
				.anyMatch(x->x instanceof Section))
				System.out.println("[CHECK_] | Test("+i+") Annotation#OK#{"+Arrays.toString(
					r
						.getClass()
						.getDeclaredAnnotations())+
								   "}:"+r.getClass());
			else
			{
				if(Config.FAIL_FAST)
				{
					System.out.println("[CHECK_] | Test("+i+") Annotation"+
									   "#FAILED#{"+Arrays.toString(r
																	   .getClass()
																	   .getDeclaredAnnotations())+
									   "}:"+r.getClass());
					System.exit(-1);
				}
				else
					System.out.println("[CHECK_] | Test("+i+") Annotation"+
									   "#SKIP#{"+Arrays.toString(r
																	 .getClass()
																	 .getDeclaredAnnotations())+
									   "}:"+r.getClass());
			}
		}
	}
}
