package eu.cloudnetservice.cloudnet.v2.modules;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Tareko on 23.07.2017.
 */
public class ModuleManager {

    private final Queue<Module<?>> modules = new ConcurrentLinkedQueue<>();
    private final ModuleDetector moduleDetector = new ModuleDetector();
    private final Path directory = Paths.get("modules");
    private Collection<String> disabledModuleList = new ArrayList<>();

    public ModuleManager() {
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Collection<Module<?>> getModules() {
        return modules;
    }

    public Collection<String> getDisabledModuleList() {
        return disabledModuleList;
    }

    public void setDisabledModuleList(Collection<String> disabledModuleList) {
        this.disabledModuleList = disabledModuleList;
    }

    public Path getDirectory() {
        return directory;
    }

    public ModuleDetector getModuleDetector() {
        return moduleDetector;
    }

    public Collection<ModuleConfig> detect() {
        return detect(directory);
    }

    public ModuleManager loadModules(Path directory) {
        Collection<ModuleConfig> configs = detect(directory);

        for (ModuleConfig config : configs) {
            if (!disabledModuleList.contains(config.getName())) {
                System.out.println("Loading module \"" + config.getName() + "\" version: " + config.getVersion() + "...");

                ModuleLoader moduleLoader = null;
                try {
                    moduleLoader = new ModuleClassLoader(config);
                } catch (MalformedURLException e) {
                    e.initCause(new RuntimeException("[2001] Module main class cannot resolve!"));
                }
                Module<?> module = moduleLoader.loadModule();
                module.setModuleLoader(moduleLoader);
                module.setDataFolder(directory);
                this.modules.add(module);
            }
        }
        return this;
    }

    public ModuleManager loadModules() {
        return loadModules(directory);
    }

    public Collection<ModuleConfig> detect(Path directory) {
        return moduleDetector.detectAvailable(directory);
    }

    public ModuleManager loadInternalModules(Set<ModuleConfig> modules) {
        return loadInternalModules(modules, this.directory);
    }

    public ModuleManager loadInternalModules(Set<ModuleConfig> modules, Path dataFolder) {
        for (ModuleConfig moduleConfig : modules) {
            ModuleLoader moduleLoader = new ModuleInternalLoader(moduleConfig);
            Module<?> module = moduleLoader.loadModule();
            module.setDataFolder(dataFolder);
            module.setModuleLoader(moduleLoader);
            this.modules.add(module);
        }
        return this;
    }

    public ModuleManager enableModules() {
        for (Module<?> module : modules) {
            System.out.println("Enabling module \"" + module.getModuleConfig().getName() + "\" version: " + module.getModuleConfig()
                                                                                                                  .getVersion() + "...");
            module.onBootstrap();
        }
        return this;
    }

    public ModuleManager disableModule(Module<?> module) {
        System.out.println("Disabling module \"" + module.getModuleConfig().getName() + "\" version: " + module.getModuleConfig()
                                                                                                               .getVersion() + "...");
        module.onShutdown();
        modules.remove(module);
        return this;
    }

    public ModuleManager disableModules() {
        while (!modules.isEmpty()) {
            Module<?> module = modules.poll();
            System.out.println("Disabling module \"" + module.getModuleConfig().getName() + "\" version: " + module.getModuleConfig()
                                                                                                                   .getVersion() + "...");
            module.onShutdown();
        }
        return this;
    }

}
