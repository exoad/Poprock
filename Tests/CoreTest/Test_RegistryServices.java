import pkg.exoad.poprock.core.registry.EphemeralRegistry;
import pkg.exoad.poprock.core.registry.RegistryEntryFactory;
import pkg.exoad.poprock.core.registry.RegistryServices;

@Section(name="Base Registry Entry Load Factor Test") public class Test_RegistryServices
	implements TestRoot,
			   TestMixin
{
	@Override public void test()
	{
		RegistryServices.armService();
		RegistryServices.registerEphemeralRegistry(
			1,
			new EphemeralRegistry.EphemeralRegistryConfig(
				"test_exoad",
				0.75F,
				null
			)
		);
		$ASSERT(RegistryServices.getEphemeral(1)!=null,"Was null");
		$ASSERT(RegistryServices
					.getEphemeral(1)
					.getObjectName()
					.equals(
						"test_exoad"),"Differing registry name");
		Object ref=new Object();
		RegistryServices
			.getEphemeral(1)
			.registerEntry(
				"entry#1",
				RegistryEntryFactory
					.make()
					.withCanonicalName("Test Entry #1")
					.withCheck(e->true)
					.withDefaultValue(ref)
					.collate()
			);
		$ASSERT(RegistryServices
					.getEphemeral(1)
					.acquireEntryValue("entry#1")
					.equals(ref),"Differing registry acquired value");
	}
}
