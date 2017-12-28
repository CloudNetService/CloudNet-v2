/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.modules;

import lombok.Getter;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by Tareko on 23.07.2017.
 */
@Getter
public class ModuleClassLoader extends URLClassLoader implements ModuleLoader {

    private ModuleConfig config;

    public ModuleClassLoader(ModuleConfig config) throws Exception
    {
        super(new URL[]{config.getFile().toURI().toURL()});
        this.config = config;
    }

    public Module loadModule() throws Exception
    {
        Class<?> bootstrap = loadClass(this.config.getMain());
        Module module = (Module) bootstrap.getDeclaredConstructor().newInstance();

        module.setClassLoader(this);
        module.setModuleConfig(config);

        module.onLoad();

        return module;
    }

}