package pkg.exoad.poprock.core.debug;
public enum DiagnosticsMode
{
	/**
	 * States that whatever program should try to crash the program as much as possible.
	 */
	FAIL_FAST,
	/**
	 * States that whatever program should hold the exceptions for as much as possible.
	 */
	QUIET,
	/**
	 * States that whatever program should only emit the messages for those exceptions,
	 * but not crash.
	 */
	EMIT;
}
