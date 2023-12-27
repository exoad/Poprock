public interface Config
{
	boolean FAIL_FAST=true;
	TestRoot[] TESTS={
		new Test_RegistryServices(),
		new Test_IdentityConvolve(),
		new Test_RidgeConvolve()
	};
	
}
