package pkg.exoad.softgradient.tests;
import pkg.exoad.softgradient.core.services.RegistryServices;
import pkg.exoad.softgradient.tests.lib.Section;
import pkg.exoad.softgradient.tests.lib.TestMixin;
import pkg.exoad.softgradient.tests.lib.TestRoot;
@Section(name="Base Registry Entry Load Factor Test") public class Test_RegistryServices
	implements TestRoot,
			   TestMixin
{
	@Override public void test()
	{
		RegistryServices.armService();
		RegistryServices.registerEphemeralRegistry(
			1,
			new RegistryServices.EphemeralRegistry.EphemeralRegistryConfig(
				"test_exoad",
				0.75F,
				null
			)
		);
		$ASSERT(RegistryServices.getEphemeral(1)!=null,"Was null");
	}
}
