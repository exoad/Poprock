package pkg.exoad.poprock.core.halite;

/**
 * Defines the basis for a loader class. This is primarily
 * seen with the main types of loaders:
 * <ul>
 * <li>Definition Based Loader
 * {@link pkg.exoad.poprock.core.halite.def.use_HaliteDefBuilder}</li>
 * <li>Model Based Loader
 * {@link pkg.exoad.poprock.core.halite.model.use_HaliteModelBuilder}</li>
 * </ul>
 */
public interface HaliteBuilder
{
	/**
	 * Loads a file
	 *
	 * @param fileName String based for the file name
	 * location
	 */
	void load(String fileName);
	
	default String loaderName()
	{
		return this.hashCode()+"@"+this;
	}
	
	default void attachForShutdown(String fileName)
	{
		Runtime
			.getRuntime()
			.addShutdownHook(new Thread(()->this.sync(
				fileName)));
	}
	
	/**
	 * Syncs the current loaded property model or
	 * definition
	 *
	 * @param fileName
	 */
	void sync(String fileName);
}
