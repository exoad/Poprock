package pkg.exoad.softgradient.tests;
import pkg.exoad.softgradient.tests.lib.TestRoot;
public interface Config
{
	static final boolean FAIL_FAST=true;
	static final TestRoot[] TESTS={
		new Test_RegistryServices()
	};
	
}