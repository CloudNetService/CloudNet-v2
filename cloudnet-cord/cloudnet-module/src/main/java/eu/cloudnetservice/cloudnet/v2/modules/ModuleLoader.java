package eu.cloudnetservice.cloudnet.v2.modules;

/**
 * Interface for {@link ClassLoader}s that load {@link Module}s.
 */
public interface ModuleLoader {

    /**
     * Load the module that is attached to this loader.
     *
     * @return the module that was loaded
     *
     */
    Module<?> loadModule();

}
