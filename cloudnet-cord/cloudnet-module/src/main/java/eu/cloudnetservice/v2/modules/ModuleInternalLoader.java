package eu.cloudnetservice.v2.modules;

/**
 * Module loader that is meant to load internal modules that should use the
 * system class loader.
 */
public class ModuleInternalLoader implements ModuleLoader {

    private final ModuleConfig moduleConfig;

    /**
     * Constructs a new loader for loading an internal module.
     *
     * @param moduleConfig the configuration to load the module from
     */
    public ModuleInternalLoader(ModuleConfig moduleConfig) {
        this.moduleConfig = moduleConfig;
    }

    @Override
    public Module loadModule() throws Exception {
        Module module = (Module) getClass().getClassLoader().loadClass(this.moduleConfig.getMain()).getConstructor().newInstance();
        module.setModuleConfig(moduleConfig);
        module.setClassLoader(null);
        return module;
    }
}
