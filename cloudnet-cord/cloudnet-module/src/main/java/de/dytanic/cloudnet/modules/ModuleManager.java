/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.modules;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Tareko on 23.07.2017.
 */
public class ModuleManager {

    private final Collection<Module> modules = new ConcurrentLinkedQueue<>();
    private ModuleDetector moduleDetector = new ModuleDetector();
    private File directory = new File("modules");
    private Collection<String> disabledModuleList = new ArrayList<>();

    public ModuleManager() {
        directory.mkdir();
    }

    public Collection<Module> getModules() {
        return modules;
    }

    public Collection<String> getDisabledModuleList() {
        return disabledModuleList;
    }

    public void setDisabledModuleList(Collection<String> disabledModuleList) {
        this.disabledModuleList = disabledModuleList;
    }

    public File getDirectory() {
        return directory;
    }

    public ModuleDetector getModuleDetector() {
        return moduleDetector;
    }

    public Collection<ModuleConfig> detect() throws Exception {
        return detect(directory);
    }

    public Collection<ModuleConfig> detect(File directory) {
        Set<ModuleConfig> modules = moduleDetector.detectAvailable(directory);
        return modules;
    }

    public ModuleManager loadModules() throws Exception {
        return loadModules(directory);
    }

    public ModuleManager loadModules(File directory) throws Exception {
        Collection<ModuleConfig> configs = detect(directory);

        for (ModuleConfig config : configs) {
            if (!disabledModuleList.contains(config.getName())) {
                System.out.println("Loading module \"" + config.getName() + "\" version: " + config.getVersion() + "...");

                ModuleLoader moduleLoader = new ModuleClassLoader(config);
                Module module = moduleLoader.loadModule();
                module.setModuleLoader(moduleLoader);
                module.setDataFolder(directory);
                this.modules.add(module);
            }
        }
        return this;
    }

    public ModuleManager loadInternalModules(Set<ModuleConfig> modules) throws Exception {
        return loadInternalModules(modules, this.directory);
    }

    public ModuleManager loadInternalModules(Set<ModuleConfig> modules, File dataFolder) throws Exception {
        for (ModuleConfig moduleConfig : modules) {
            ModuleLoader moduleLoader = new ModuleInternalLoader(moduleConfig);
            Module module = moduleLoader.loadModule();
            module.setDataFolder(dataFolder);
            module.setModuleLoader(moduleLoader);
            this.modules.add(module);
        }
        return this;
    }

    public ModuleManager enableModules() {
        for (Module module : modules) {
            System.out.println("Enabling module \"" + module.getModuleConfig().getName() + "\" version: " + module.getModuleConfig()
                                                                                                                  .getVersion() + "...");
            module.onBootstrap();
        }
        return this;
    }

    public ModuleManager disableModule(Module module) {
        System.out.println("Disabling module \"" + module.getModuleConfig().getName() + "\" version: " + module.getModuleConfig()
                                                                                                               .getVersion() + "...");
        module.onShutdown();
        modules.remove(module);
        return this;
    }

    public ModuleManager disableModules() {
        while (!modules.isEmpty()) {
            Module module = (Module) ((Queue) modules).poll();
            System.out.println("Disabling module \"" + module.getModuleConfig().getName() + "\" version: " + module.getModuleConfig()
                                                                                                                   .getVersion() + "...");
            module.onShutdown();
        }
        return this;
    }

}
