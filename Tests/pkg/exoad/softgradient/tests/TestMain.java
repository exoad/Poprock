package pkg.exoad.softgradient.tests;
import pkg.exoad.softgradient.tests.lib.Section;
import pkg.exoad.softgradient.tests.lib.TestRoot;

import java.util.Arrays;
import java.util.Stack;
public class TestMain
{
	public static final Stack<String> INVALIDATIONS=new Stack<>();
	
	public static void main(String[] args)
	{
		long passed=0, failed=0, skipped=0, invalidations=0;
		for(int i=0;i<Config.TESTS.length;i++)
		{
			invalidations+=INVALIDATIONS.size();
			if(!INVALIDATIONS.isEmpty())
				INVALIDATIONS.clear();
			// pre validation
			TestRoot r=Config.TESTS[i];
			if(Arrays
				.stream(r
							.getClass()
							.getDeclaredAnnotations())
				.anyMatch(x->x instanceof Section))
			{
				System.out.println("[PCHECK] | Test("+i+") Annotation#OK#{"+Arrays.toString(
					r
						.getClass()
						.getDeclaredAnnotations())+
								   "}:"+r.getClass());
				String tcName=
					((Section)r
						.getClass()
						.getDeclaredAnnotations()[0]).name(); // :(
				passed++;
			}
			else
			{
				if(Config.FAIL_FAST)
				{
					System.out.println("[PCHECK] | Test("+i+") Annotation"+
									   "#FAILED#{"+Arrays.toString(r
																	   .getClass()
																	   .getDeclaredAnnotations())+
									   "}:"+r.getClass());
					failed++;
					System.exit(-1);
				}
				else
				{
					System.out.println("[PCHECK] | Test("+i+") Annotation"+
									   "#SKIP#{"+Arrays.toString(r
																	 .getClass()
																	 .getDeclaredAnnotations())+
									   "}:"+r.getClass());
					skipped++;
					invalidations=passed-skipped;
				}
			}
		}
		System.out.println("[RESULT]\n - \tPassed: "+passed+"\n - \tFailed:"+
						   " "+failed+" (Invalidations: +"+invalidations+")\n "+
						   "- \tSkipped: "+skipped);
	}
}
