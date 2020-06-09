package eu.cloudnetservice.cloudnet.v2.master.module;

import com.google.common.collect.Maps;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.vdurmont.semver4j.Semver;
import eu.cloudnetservice.cloudnet.v2.master.bootstrap.CloudBootstrap;
import eu.cloudnetservice.cloudnet.v2.master.module.exception.ModuleDescriptionFileNotFoundException;
import eu.cloudnetservice.cloudnet.v2.master.module.exception.ModuleNotFoundException;
import eu.cloudnetservice.cloudnet.v2.master.module.model.CloudModuleDependency;
import eu.cloudnetservice.cloudnet.v2.master.module.model.CloudModuleDescriptionFile;
import org.checkerframework.checker.units.qual.A;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

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
        List<Path> toUpdate = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.updateModuleDirectory, "*.jar")) {
            for (Path path : stream) {
                toUpdate.add(path);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
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
        handleLoaded(toLoad, toUpdate);
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
            checkDependencies(this.resolveDependenciesSortedSingle(getModules().values(),
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

    /**
     * Here all modules are loaded from a list, checked for updates and migrated if necessary
     *
     * @param toLoaded contains all files that have to be loaded
     * @param toUpdate contains all update files which have to be checked if the update works
     */
    private void handleLoaded(@NotNull List<Path> toLoaded,@NotNull List<Path> toUpdate) {
        for (Path path : toLoaded) {
            Optional<JavaCloudModule> cloudModule = loadModule(path);
            cloudModule.ifPresent(javaCloudModule -> {
                if (javaCloudModule instanceof UpdateCloudModule) {
                    javaCloudModule.getModuleLogger().info(String.format("Check module update %s",
                                                                         javaCloudModule.getModuleJson().getName()));
                    UpdateCloudModule updateCloudModule = (UpdateCloudModule) javaCloudModule;
                    javaCloudModule.setUpdate(updateCloudModule.update(javaCloudModule.getModuleJson().getUpdateUrl()));
                }
            });
            cloudModule.ifPresent(javaCloudModule -> this.modules.put(javaCloudModule
                                                                          .getModuleJson()
                                                                          .getGroupId() + ":" + javaCloudModule
                                                                          .getModuleJson()
                                                                          .getName(),
                                                                      javaCloudModule));
            toLoaded.remove(path);
        }
        for (Path path : toUpdate) {
            Optional<JavaCloudModule> cloudModule = loadModule(path);
            cloudModule.ifPresent(javaCloudModule -> {
                Optional<CloudModule> moduleOptional = this.getModule(javaCloudModule
                                                                                .getModuleJson()
                                                                                .getGroupId() + ":" + javaCloudModule
                    .getModuleJson()
                    .getName());
                if (moduleOptional.isPresent()) {
                    CloudModule module = moduleOptional.get();
                    if (module instanceof JavaCloudModule && module.isUpdate()) {
                        if (javaCloudModule.getModuleJson().getSemVersion().isGreaterThan(module.getModuleJson().getSemVersion())) {
                            unloadModule(module);
                            this.modules.remove(module
                                                    .getModuleJson()
                                                    .getGroupId() + ":" + module.getModuleJson().getName());
                            if (module instanceof MigrateCloudModule) {
                                MigrateCloudModule migrateCloudModule = (MigrateCloudModule) module;
                                if (migrateCloudModule.migrate(module.getModuleJson().getSemVersion(),
                                                               javaCloudModule.getModuleJson().getSemVersion())) {
                                    module.getModuleLogger().info(String.format("Module %s successfully migrated from %s to %s",
                                                                             module.getModuleJson().getName(),
                                                                             module.getModuleJson().getVersion(),
                                                                             javaCloudModule.getModuleJson().getVersion()));
                                    try {
                                        Files.deleteIfExists(module.getModuleJson().getFile());
                                        Files.copy(javaCloudModule.getModuleJson().getFile(), module.getModuleJson().getFile());
                                        Files.deleteIfExists(javaCloudModule.getModuleJson().getFile());
                                        Optional<JavaCloudModule> optionalJavaCloudModule = loadModule(module.getModuleJson()
                                                                                                                   .getFile());
                                        optionalJavaCloudModule.ifPresent(value -> {
                                            this.modules.put(value
                                                                 .getModuleJson()
                                                                 .getGroupId() + ":" + javaCloudModule
                                                                 .getModuleJson()
                                                                 .getName(),
                                                             value);
                                            value.getModuleLogger().info(String.format("Update to %s was successful",
                                                                                       value.getModuleJson().getVersion()));
                                        });
                                        optionalJavaCloudModule.orElseThrow(() -> new RuntimeException(String.format(
                                            "New update of %s could not be loaded!",
                                            module.getModuleJson().getName())));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    module.getModuleLogger().info(String.format("Module %s could not be migrated from %s to %s ",
                                                                             module.getModuleJson().getName(),
                                                                             module.getModuleJson().getVersion(),
                                                                             javaCloudModule.getModuleJson().getVersion()));
                                }
                            } else {
                                try {
                                    Files.deleteIfExists(module.getModuleJson().getFile());
                                    Files.copy(javaCloudModule.getModuleJson().getFile(), module.getModuleJson().getFile());
                                    Files.deleteIfExists(javaCloudModule.getModuleJson().getFile());
                                    Optional<JavaCloudModule> optionalJavaCloudModule = loadModule(module.getModuleJson()
                                                                                                               .getFile());
                                    optionalJavaCloudModule.ifPresent(value -> {
                                        this.modules.put(value
                                                             .getModuleJson()
                                                             .getGroupId() + ":" + javaCloudModule
                                                             .getModuleJson()
                                                             .getName(),
                                                         value);
                                        value.getModuleLogger().info(String.format("Update to %s was successful",
                                                                                   value.getModuleJson().getVersion()));
                                    });
                                    optionalJavaCloudModule.orElseThrow(() -> new RuntimeException(String.format(
                                        "New update of %s could not be loaded!",
                                        module.getModuleJson().getName())));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }


                        }

                    }

                }
            });
            toUpdate.remove(path);
        }
        checkDependencies(resolveDependenciesSorted(getModules().values())).stream()
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


    private List<CloudModule> checkDependencies(@NotNull List<CloudModule> cloudModules) {
        Set<CloudModule> loadOrder = new HashSet<>();
        load:
        for (CloudModule cloudModule : cloudModules) {
            String moduleName = cloudModule.getModuleJson().getGroupId() + ":" + cloudModule.getModuleJson().getName();
            if (!this.semCloudNetVersion.satisfies(cloudModule.getModuleJson().getRequiredCloudNetVersion())) {
                System.err.println("Cannot load module " + moduleName + " because of missing required CloudNet version");
                this.modules.remove(moduleName);
                continue;
            }
            for (final CloudModuleDependency dependency : cloudModule.getModuleJson().getDependencies()) {
                String name = dependency.getGroupId() + ":" + dependency.getName();
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
                    System.err.println(String.format("Module(%s) not enabling, wrong version format %s",
                                                     moduleDescriptionFile.getName(),
                                                     moduleDescriptionFile.getVersion()));
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
    private boolean isModuleDetectedByPath(@NotNull Path path,@NotNull List<Path> toLoad) {
        boolean result = false;
        String check = path.toAbsolutePath().toString();
        Iterator<CloudModule> moduleIterator = this.getModules().values().iterator();
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
            for (String current : loaders.keySet()) {
                ModuleClassLoader loader = loaders.get(current);

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
     * Adds the class to a list of classes using the name
     *
     * @param name  is the name that should apply to the class when the class is added
     * @param clazz is the class to be added
     */
    void setClass(@NotNull String name,@NotNull Class<?> clazz) {
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
     * Returns the module instance associated to the given name, if present.
     *
     * @param name the name of the module to get.
     *
     * @return an {@code Optional} possibly containing the module's instance.
     */
    public Optional<CloudModule> getModule(@NotNull String name) {
        Optional<CloudModule> module = Optional.empty();
        if (this.modules.containsKey(name)) {
            module = Optional.of(getModules().get(name));
        }
        return module;
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
                             .append(":")
                             .append(description.getModuleJson().getName())
                             .append("; ");
            }
            throw new StackOverflowError("Dependency load injects itself or other injects other: " + stringBuilder.toString());
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
