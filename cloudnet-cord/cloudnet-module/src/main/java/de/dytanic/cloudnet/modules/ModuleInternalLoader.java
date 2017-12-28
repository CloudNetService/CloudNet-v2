/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.modules;

/**
 * Created by Tareko on 02.11.2017.
 */
public class ModuleInternalLoader implements ModuleLoader {

    private ModuleConfig moduleConfig;

    public ModuleInternalLoader(ModuleConfig moduleConfig)
    {
        this.moduleConfig = moduleConfig;
    }

    @Override
    public Module loadModule() throws Exception
    {
        Module module = (Module) getClass().getClassLoader().loadClass(this.moduleConfig.getMain()).newInstance();
        module.setModuleConfig(moduleConfig);
        module.setClassLoader(null);
        return module;
    }
}