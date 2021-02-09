/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.master.module;

import com.google.common.collect.Maps;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.vdurmont.semver4j.Semver;
import eu.cloudnetservice.cloudnet.v2.master.bootstrap.CloudBootstrap;
import eu.cloudnetservice.cloudnet.v2.master.module.exception.ModuleDescriptionFileNotFoundException;
import eu.cloudnetservice.cloudnet.v2.master.module.model.CloudModuleDependency;
import eu.cloudnetservice.cloudnet.v2.master.module.model.CloudModuleDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Manages loading of modules, dependencies, updating, running migrations and unloading of modules.
 */
public final class CloudModuleManager {

    private final Map<String, CloudModule> modules;
    private final Path moduleDirectory;
    private final Path updateModuleDirectory;
    private final Semver semCloudNetVersion;
    private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
    private final Map<String, ModuleClassLoader> loaders = new LinkedHashMap<>();

    public CloudModuleManager() {
        this.modules = new LinkedHashMap<>();
        this.moduleDirectory = Paths.get("modules");
        this.updateModuleDirectory = Paths.get(moduleDirectory.toString(), "update");
        if (!Files.exists(this.updateModuleDirectory)) {
            try {
                Files.createDirectories(this.updateModuleDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.semCloudNetVersion = new Semver(String.format("%s",
                                                           CloudBootstrap.class.getPackage().getImplementationVersion()),
                                             Semver.SemverType.NPM);
    }

    /**
     * Looks for files in the modules and update folder and indexes all files.
     * Afterwards all modules are loaded and checked, whether updates are available and whether migrations need to be run.
     */
    public void detectModules() {
        List<Path> toLoad = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.moduleDirectory, "*.jar")) {
            for (Path path : stream) {
                if (this.isModuleDetectedByPath(path, toLoad)) {
                    continue;
                }
                toLoad.add(path);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        handleLoaded(toLoad);
    }

    /**
     * Unloads the given module.
     * If it is currently enabled, disables the module and unloads all known classes.
     *
     * @param module the module that should be unloaded.
     */
    public void unloadModule(@NotNull CloudModule module) {
        if (module instanceof JavaCloudModule) {
            if (module.isEnabled()) {
                disableModule(module);
            } else {
                String name = String.format("%s:%s",
                                            module.getModuleJson().getGroupId(),
                                            module.getModuleJson().getName());
                JavaCloudModule javaCloudModule = (JavaCloudModule) module;

                if (this.modules.containsKey(name)) {
                    unloadClasses(javaCloudModule);
                }
            }

        }

    }

    /**
     * Disables the module and unloads all classes from the module.
     *
     * @param module the module that should be disabled.
     */
    public void disableModule(@NotNull CloudModule module) {
        if (module instanceof JavaCloudModule) {
            String name = String.format("%s:%s",
                                        module.getModuleJson().getGroupId(),
                                        module.getModuleJson().getName());
            if (module.isEnabled()) {

                module.getModuleLogger().info(String.format("Disabling %s from %s with version %s",
                                                            name,
                                                            module.getModuleJson().getAuthorsAsString(),
                                                            module.getModuleJson().getVersion()));
                JavaCloudModule javaCloudModule = (JavaCloudModule) module;
                if (this.modules.containsKey(name)) {
                    javaCloudModule.setEnabled(false);
                    unloadClasses(javaCloudModule);
                }
            }
        } else {
            System.err.println("Module is not associated with this ModuleLoader");
        }
    }

    /**
     * Unloads all classes from one module
     *
     * @param cloudModule contains all information
     */
    private void unloadClasses(@NotNull JavaCloudModule cloudModule) {
        String name = String.format("%s:%s",
                                    cloudModule.getModuleJson().getGroupId(),
                                    cloudModule.getModuleJson().getName());
        this.loaders.remove(name);
        ClassLoader classLoader = cloudModule.getClassLoader();
        if (classLoader instanceof ModuleClassLoader) {
            ModuleClassLoader moduleClassLoader = (ModuleClassLoader) classLoader;
            Set<String> names = moduleClassLoader.getClasses();
            for (String s : names) {
                removeClass(s);
            }
        }
    }

    /**
     * Checks a module's dependencies and attempts to load a module.
     * If a dependency is not present or incompatible with the module, this will disable and unload the module cleanly.
     *
     * @param module the module that should be loaded.
     */
    public void enableModule(@NotNull CloudModule module) {
        if (module instanceof JavaCloudModule) {
            JavaCloudModule javaCloudModule = (JavaCloudModule) module;
            checkDependencies(this.resolveDependenciesSortedSingle(modules.values(),
                                                                   javaCloudModule)).forEach(cloudModule -> {
                if (!cloudModule.isEnabled()) {
                    cloudModule.getModuleLogger().info(String.format("Enabling module %s from %s with version %s",
                                                                     cloudModule.getModuleJson().getName(),
                                                                     cloudModule.getModuleJson().getAuthorsAsString(),
                                                                     cloudModule.getModuleJson().getVersion()));
                    cloudModule.setEnabled(true);
                }
            });
        } else {
            System.err.println("Module is not associated with this ModuleLoader");
        }
    }

    private void checkModuleForAvailableUpdate(@NotNull CloudModule module) {
        if (module instanceof UpdateCloudModule) {
            module.getModuleLogger().info(String.format("Check module update %s",
                                                        module.getModuleJson().getName()));
            UpdateCloudModule updateCloudModule = (UpdateCloudModule) module;
            module.setUpdate(updateCloudModule.update(module.getModuleJson().getUpdateUrl()));
        }
    }

    private List<CloudModule> checkDependencies(@NotNull List<CloudModule> cloudModules) {
        Set<CloudModule> loadOrder = new HashSet<>();
        load:
        for (CloudModule cloudModule : cloudModules) {
            String moduleName = cloudModule.getModuleJson().getGroupId() + ':' + cloudModule.getModuleJson().getName();
            if (!this.semCloudNetVersion.satisfies(cloudModule.getModuleJson().getRequiredCloudNetVersion())) {
                System.err.println("Cannot load module " + moduleName + " because of missing required CloudNet version");
                this.modules.remove(moduleName);
                continue;
            }
            for (final CloudModuleDependency dependency : cloudModule.getModuleJson().getDependencies()) {
                String name = dependency.getGroupId() + ':' + dependency.getName();
                Optional<CloudModule> optionalCloudModule = getModule(name);
                if (!optionalCloudModule.isPresent()) {
                    System.err.println("unable to load module " + moduleName + " because of missing dependency " + name);
                    this.modules.remove(moduleName);
                    continue load;
                }
                if (!optionalCloudModule.get().getModuleJson().getSemVersion().satisfies(dependency.getVersion())) {
                    System.err.println("Cannot load module " + moduleName + " because of missing dependency with version " + dependency.getVersion());
                    this.modules.remove(moduleName);
                    continue load;
                }
                optionalCloudModule.ifPresent(loadOrder::add);
            }
            loadOrder.add(cloudModule);
        }
        List<CloudModule> forLoading = new ArrayList<>(resolveDependenciesSorted(loadOrder));
        Collections.reverse(forLoading);
        return forLoading;
    }


    /**
     * @param module is registered in the map and it is displayed that it was successfully updated
     */
    private void updateSuccessful(@NotNull CloudModule module) {
        module.getModuleLogger().info(String.format("Update to %s was successful",
                                                    module.getModuleJson().getVersion()));
        this.registerModule(module);

    }

    /**
     * Returns the module instance associated to the given name, if present.
     *
     * @param name the name of the module to get.
     *
     * @return an {@code Optional} possibly containing the module's instance.
     */
    public Optional<CloudModule> getModule(@NotNull String name) {
        Optional<CloudModule> module = Optional.empty();
        if (this.modules.containsKey(name)) {
            module = Optional.of(modules.get(name));
        }
        return module;
    }

    /**
     * Migrates the module from an old version to a new
     * @param oldModule is the old once
     * @param newModule is the new one where the migration will take place
     */
    private void migrateFromOldToNewModule(@NotNull CloudModule oldModule, @NotNull CloudModule newModule) {
        if (newModule instanceof MigrateCloudModule) {
            MigrateCloudModule migrateCloudModule = (MigrateCloudModule) newModule;
            if (migrateCloudModule.migrate(newModule.getModuleJson().getSemVersion(),
                                           oldModule.getModuleJson().getSemVersion())) {
                newModule.getModuleLogger().info(String.format("Module %s successfully migrated from %s to %s",
                                                               oldModule.getModuleJson().getName(),
                                                               oldModule.getModuleJson().getVersion(),
                                                               newModule.getModuleJson().getVersion()));
                this.updateModule(oldModule, newModule);
            } else {
                oldModule.getModuleLogger().info(String.format("Module %s could not be migrated from %s to %s ",
                                                               oldModule.getModuleJson().getName(),
                                                               oldModule.getModuleJson().getName(),
                                                               newModule.getModuleJson().getVersion()));
            }
        }
    }

    /**
     * Replaces the old module with the new one and loads it into the module system
     * @param oldModule is the module to be deleted
     * @param newModule is the module to be copied and loaded
     */
    private void updateModule(@NotNull CloudModule oldModule, @NotNull CloudModule newModule) {
        try {
            Files.deleteIfExists(oldModule.getModuleJson().getFile());
            Files.copy(newModule.getModuleJson().getFile(), oldModule.getModuleJson().getFile());
            Files.deleteIfExists(newModule.getModuleJson().getFile());
            Optional<JavaCloudModule> optionalJavaCloudModule = loadModule(newModule.getModuleJson()
                                                                                    .getFile());
            optionalJavaCloudModule.ifPresent(this::updateSuccessful);
            optionalJavaCloudModule.orElseThrow(() -> new RuntimeException(String.format(
                "New update of %s could not be loaded!",
                oldModule.getModuleJson().getName())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks the file if the new version is larger than the old one and if migration is possible
     * @param oldModule should be checked
     */
    private void checkForNewerVersion(@NotNull CloudModule oldModule) {
        Optional<CloudModule> newOptionalModule = this.getModule(translateModuleAsUniqueString(oldModule));
        if (newOptionalModule.isPresent()) {
            CloudModule newModule = newOptionalModule.get();
            if (newModule instanceof JavaCloudModule && newModule.isUpdate()) {
                if (oldModule.getModuleJson().getSemVersion().isGreaterThan(newModule.getModuleJson().getSemVersion())) {
                    unloadModule(newModule);
                    this.modules.remove(translateModuleAsUniqueString(newModule));
                    if (newModule instanceof MigrateCloudModule) {
                        this.migrateFromOldToNewModule(oldModule, newModule);
                    } else {
                        this.updateModule(oldModule, newModule);
                    }
                }
            }
        }
    }

    /**
     * Creates a unique string from a module
     *
     * @param module is used for the string
     *
     * @return Returns the unique string
     */
    public String translateModuleAsUniqueString(@NotNull CloudModule module) {
        return module
            .getModuleJson()
            .getGroupId() + ':' + module
            .getModuleJson()
            .getName();
    }

    /**
     * Here all modules are loaded from a list, checked for updates and migrated if necessary
     *
     * @param toLoaded contains all files that have to be loaded
     */
    private void handleLoaded(@NotNull List<Path> toLoaded) {
        for (Path path : toLoaded) {
            Optional<JavaCloudModule> cloudModule = loadModule(path);
            cloudModule.ifPresent(this::checkModuleForAvailableUpdate);
            cloudModule.ifPresent(this::registerModule);
        }
        List<Path> toUpdate = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.updateModuleDirectory, "*.jar")) {
            for (Path path : stream) {
                toUpdate.add(path);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        for (Path path : toUpdate) {
            Optional<JavaCloudModule> cloudModule = loadModule(path);
            cloudModule.ifPresent(this::checkForNewerVersion);
        }
        checkDependencies(resolveDependenciesSorted(modules.values())).stream()
                                                                      .filter(javaCloudModule -> !javaCloudModule.isLoaded())
                                                                      .forEach(javaCloudModule -> {
                                                                          javaCloudModule.getModuleLogger().info(
                                                                              String.format(
                                                                                  "Loading module %s from %s with version %s",
                                                                                  javaCloudModule.getModuleJson()
                                                                                                 .getName(),
                                                                                  javaCloudModule.getModuleJson()
                                                                                                 .getAuthorsAsString(),
                                                                                  javaCloudModule.getModuleJson()
                                                                                                 .getVersion()));
                                                                          javaCloudModule.setLoaded(true);
                                                                           });
    }

    /**
     * Attempts to load a module from the given path.
     *
     * @param path the place where the module resides.
     *
     * @return an {@code Optional} containing the loaded module, if successful.
     */
    public Optional<JavaCloudModule> loadModule(@NotNull Path path) {
        Optional<JavaCloudModule> javaModule = Optional.empty();
        try {
            Optional<CloudModuleDescriptionFile> cloudModuleDescriptionFile = getCloudModuleDescriptionFile(path);
            if (cloudModuleDescriptionFile.isPresent()) {
                ModuleClassLoader classLoader = new ModuleClassLoader(getClass().getClassLoader(), path, this);
                Class<?> jarClazz = classLoader.loadClass(cloudModuleDescriptionFile.get().getMain());
                Class<? extends JavaCloudModule> mainClazz = jarClazz.asSubclass(JavaCloudModule.class);
                JavaCloudModule javaCloudModule = mainClazz.getDeclaredConstructor().newInstance();
                javaModule = Optional.of(javaCloudModule);
                javaModule.ifPresent(cloudModule -> cloudModule.init(classLoader, cloudModuleDescriptionFile.get()));
                javaModule.ifPresent(cloudModule -> this.loaders.put(String.format("%s:%s",
                                                                                   cloudModule.getModuleJson().getGroupId(),
                                                                                   cloudModule.getModuleJson().getName()), classLoader));
            }
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return javaModule;
    }

    /**
     * @param module is registered in the map
     */
    private void registerModule(@NotNull CloudModule module) {
        this.modules.put(module
                             .getModuleJson()
                             .getGroupId() + ':' + module
                             .getModuleJson()
                             .getName(),
                         module);
    }

    /**
     * Attempts to read and parse a module's description file.
     *
     * @param module the path where the module resides.
     *
     * @return an {@code Optional} possibly containing the parsed description file.
     */
    public Optional<CloudModuleDescriptionFile> getCloudModuleDescriptionFile(@NotNull Path module) {
        Optional<CloudModuleDescriptionFile> cloudModuleDescriptionFile = Optional.empty();
        try (JarFile moduleJar = new JarFile(module.toFile())) {
            ZipEntry moduleJsonFile = moduleJar.getEntry("module_v2.json");
            if (moduleJsonFile == null) {
                throw new ModuleDescriptionFileNotFoundException("The module don't contain a module_v2.json!");
            }
            try (InputStream stream = moduleJar.getInputStream(moduleJsonFile)) {
                CloudModuleDescriptionFile moduleDescriptionFile = new CloudModuleDescriptionFile(stream, module);
                if (moduleDescriptionFile.getSemVersion().getMajor() != null && moduleDescriptionFile.getSemVersion()
                                                                                                     .getMinor() != null && moduleDescriptionFile
                    .getSemVersion()
                    .getPatch() != null) {
                    cloudModuleDescriptionFile = Optional.of(moduleDescriptionFile);
                } else {
                    System.err.printf("Module(%s) not enabling, wrong version format %s%n",
                                      moduleDescriptionFile.getName(),
                                      moduleDescriptionFile.getVersion());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Something is wrong with the jar", e);
        }
        return cloudModuleDescriptionFile;
    }

    /**
     * Checks whether the given path is present in the list of modules to load.
     * The check is done using the absolute path.
     *
     * @param path   the path to the module.
     * @param toLoad a list of all modules already indexed.
     *
     * @return {@code true}, if the module is present in the list, {@code false} otherwise.
     */
    private boolean isModuleDetectedByPath(@NotNull Path path, @NotNull List<Path> toLoad) {
        boolean result = false;
        String check = path.toAbsolutePath().toString();
        Iterator<CloudModule> moduleIterator = this.modules.values().iterator();
        while (moduleIterator.hasNext() && !result) {
            result = moduleIterator.next().getModuleJson().getFile().toAbsolutePath().toString().equals(check);
        }
        if (!result) {
            Iterator<Path> iterator = toLoad.iterator();
            while (iterator.hasNext() && !result) {
                result = iterator.next().toAbsolutePath().toString().equals(check);
            }
        }
        return result;
    }

    /**
     * Adds the class to a list of classes using the name
     *
     * @param name  is the name that should apply to the class when the class is added
     * @param clazz is the class to be added
     */
    void setClass(@NotNull String name, @NotNull Class<?> clazz) {
        if (!classes.containsKey(name)) {
            classes.put(name, clazz);
        }
    }

    /**
     * Removes a class from the cache by the given name.
     *
     * @param name the name of the class to remove.
     */
    private void removeClass(@NotNull String name) {
        classes.remove(name);
    }

    /**
     * @return a map of loaded modules' names mapped to the respective module instances.
     */
    public Map<String, CloudModule> getModules() {
        return modules;
    }

    /**
     * Attempts to the a class by the given name.
     * Searches every modules' class loaders.
     *
     * @param name the fully-qualified name of the class.
     *
     * @return the class, if present in any class loader. {@code null}, if the class is not present in any search class loader.
     */
    Class<?> getClassByName(@NotNull String name) {
        Class<?> cachedClass = classes.get(name);

        if (cachedClass != null) {
            return cachedClass;
        } else {
            for (final ModuleClassLoader loader : loaders.values()) {

                try {
                    cachedClass = loader.findClass(name, false);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (cachedClass != null) {
                    return cachedClass;
                }
            }
        }
        return null;
    }

    /**
     * Sorts all dependencies Recursively based on a module
     *
     * @param cloudModuleDescriptionFiles a list containing the dependencies to be resolved.
     * @param javaCloudModule             the module for which to resolve the dependencies.
     *
     * @return a list of modules that the given module depends on in the order they need to be loaded.
     */
    private List<CloudModule> resolveDependenciesSortedSingle(@NotNull Collection<CloudModule> cloudModuleDescriptionFiles,
                                                              CloudModule javaCloudModule) {
        MutableGraph<CloudModule> graph = GraphBuilder
            .directed()
            .expectedNodeCount(cloudModuleDescriptionFiles.size())
            .allowsSelfLoops(false)
            .build();
        Map<String, CloudModule> candidateAsMap = Maps.uniqueIndex(cloudModuleDescriptionFiles,
                                                                   f -> {
                                                                       if (f != null) {
                                                                           return String.format("%s:%s",
                                                                                                f.getModuleJson().getGroupId(),
                                                                                                f.getModuleJson().getName());
                                                                       }
                                                                       return null;
                                                                   });

        graph.addNode(javaCloudModule);
        for (CloudModuleDependency dependency : javaCloudModule.getModuleJson().getDependencies()) {
            CloudModule dependencyContainer = candidateAsMap.get(String.format("%s:%s",
                                                                               dependency.getGroupId(),
                                                                               dependency.getName()));
            if (dependencyContainer != null) {
                graph.putEdge(dependencyContainer, javaCloudModule);
            }
        }

        List<CloudModule> sorted = new ArrayList<>();
        Map<CloudModule, Integer> integerMap = new HashMap<>();

        for (CloudModule node : graph.nodes()) {
            this.visitDependency(graph, node, integerMap, sorted, new ArrayDeque<>());
        }

        return sorted;
    }

    /**
     * Sorts all dependencies Recursively using a module list
     *
     * @param cloudModuleDescriptionFiles a list containing the dependencies which should be resolved.
     *
     * @return a list of modules in the order they need to be loaded.
     */
    private List<CloudModule> resolveDependenciesSorted(@NotNull Collection<CloudModule> cloudModuleDescriptionFiles) {
        Set<CloudModule> sorted = new HashSet<>();
        for (CloudModule module : cloudModuleDescriptionFiles) {
            sorted.addAll(this.resolveDependenciesSortedSingle(cloudModuleDescriptionFiles, module));
        }
        return new ArrayList<>(sorted);
    }

    /**
     * Resolves the dependency and looks for the order
     *
     * @param graph            indicates the graph in question
     * @param node             specifies the module in question
     * @param marks            are indicators where the system is located
     * @param sorted           is a list where at the end all dependencies come in sorted
     * @param currentIteration tell from which module to iterate
     */
    private void visitDependency(Graph<CloudModule> graph,
                                 CloudModule node,
                                 Map<CloudModule, Integer> marks,
                                 List<CloudModule> sorted,
                                 Deque<CloudModule> currentIteration) {

        Integer integer = marks.getOrDefault(node, 0);
        if (integer == 2) {
            return;
        } else if (integer == 1) {
            currentIteration.addLast(node);

            StringBuilder stringBuilder = new StringBuilder();
            for (CloudModule description : currentIteration) {
                stringBuilder.append(description.getModuleJson().getGroupId())
                             .append(':')
                             .append(description.getModuleJson().getName())
                             .append("; ");
            }
            throw new StackOverflowError("Dependency load injects itself or other injects other: " + stringBuilder);
        }

        currentIteration.addLast(node);
        marks.put(node, 2);
        for (CloudModule edge : graph.successors(node)) {
            this.visitDependency(graph, edge, marks, sorted, currentIteration);
        }

        marks.put(node, 2);
        currentIteration.removeLast();
        sorted.add(node);
    }
}
