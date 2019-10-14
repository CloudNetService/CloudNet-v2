/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.modules;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Class loader for {@link Module}s that assigns itself ti the module's class loader.
 */
public class ModuleClassLoader extends URLClassLoader implements ModuleLoader {

    private ModuleConfig config;

    /**
     * Constructs a new class loader for modules
     *
     * @param config the module configuration this class loader is meant for
     *
     * @throws MalformedURLException when {@link ModuleConfig#file} does not
     *                               resolve to a url
     */
    public ModuleClassLoader(ModuleConfig config) throws MalformedURLException {
        super(new URL[] {config.getFile().toURI().toURL()});
        this.config = config;
    }

    public ModuleConfig getConfig() {
        return config;
    }

    public Module loadModule() throws Exception {
        Class<?> bootstrap = loadClass(config.getMain());
        Module module = (Module) bootstrap.getDeclaredConstructor().newInstance();

        module.setClassLoader(this);
        module.setModuleConfig(config);

        module.onLoad();

        return module;
    }

}
