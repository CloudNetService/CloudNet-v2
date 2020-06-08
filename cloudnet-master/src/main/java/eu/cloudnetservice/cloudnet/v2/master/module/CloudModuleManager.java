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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public final class CloudModuleManager {

    private final Map<String, CloudModule> modules;
    private final Path moduleDirectory;
    private final Path updateModuleDirectory;
    private final Semver semCloudNetVersion;
    private final Map<String, Class<?>> classes = new java.util.concurrent.ConcurrentHashMap<String, Class<?>>(); // Spigot
    private final Map<String, ModuleClassLoader> loaders = new LinkedHashMap<>();

    public CloudModuleManager() {
        modules = new LinkedHashMap<>();
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

    public void detectModules() {
        List<Path> toUpdate = new CopyOnWriteArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.updateModuleDirectory, "*.jar")) {
            for (Path path : stream) {
                toUpdate.add(path);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        List<Path> toLoad = new CopyOnWriteArrayList<>();
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

    public void unloadModule(CloudModule module) {
        if (module instanceof JavaCloudModule) {
            if (module.isEnabled()) {
                disableModule(module);
            } else {
                String name = String.format("%s:%s",
                                            module.getModuleJson().getGroupId(),
                                            module.getModuleJson().getName());
                JavaCloudModule javaCloudModule = (JavaCloudModule) module;

                if (this.modules.containsKey(name)) {
                    javaCloudModule.setEnabled(false);
                    this.loaders.remove(name);
                    final ClassLoader classLoader = javaCloudModule.getClassLoader();
                    if (classLoader instanceof ModuleClassLoader) {
                        ModuleClassLoader moduleClassLoader = (ModuleClassLoader) classLoader;
                        Set<String> names = moduleClassLoader.getClasses();
                        for (String s : names) {
                            removeClass(s);
                        }
                        try {
                            moduleClassLoader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }

    }

    public void disableModule(CloudModule module) {
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
                    this.loaders.remove(name);
                    final ClassLoader classLoader = javaCloudModule.getClassLoader();
                    if (classLoader instanceof ModuleClassLoader) {
                        ModuleClassLoader moduleClassLoader = (ModuleClassLoader) classLoader;
                        Set<String> names = moduleClassLoader.getClasses();
                        for (String s : names) {
                            removeClass(s);
                        }
                    }
                }
            }
        } else {
            System.err.println("Module is not associated with this ModuleLoader");
        }
    }

    public void enableModule(CloudModule module) {
        if (module instanceof JavaCloudModule) {
            JavaCloudModule javaCloudModule = (JavaCloudModule) module;
            final List<CloudModule> cloudModules = this.resolveDependenciesSortedSingle(new ArrayList<>(getModules().values()),
                                                                                        javaCloudModule);
            final Set<CloudModule> loadOrder = new HashSet<>();
            load:
            for (CloudModule cloudModule : cloudModules) {
                String moduleName = cloudModule.getModuleJson().getGroupId() + ":" + cloudModule.getModuleJson().getName();
                if (!this.semCloudNetVersion.satisfies(cloudModule.getModuleJson().getRequiredCloudNetVersion())) {
                    System.err.println("Cannot load module " + moduleName + " because of missing required CloudNet version");
                    this.modules.remove(moduleName);
                    continue load;
                }
                for (final CloudModuleDependency dependency : cloudModule.getModuleJson().getDependencies()) {
                    String dependName = dependency.getGroupId() + ":" + dependency.getName();
                    final Optional<CloudModule> optionalCloudModule = getModule(dependName);
                    if (!optionalCloudModule.isPresent()) {
                        System.err.println("unable to load module " + moduleName + " because of missing dependency " + dependName);
                        this.modules.remove(moduleName);
                        continue load;
                    }
                    if (!optionalCloudModule.get().getModuleJson().getSemVersion().satisfies(dependency.getVersion())) {
                        System.err.println("Cannot load module " + moduleName + " because of missing dependency with version " + dependency
                            .getVersion());
                        this.modules.remove(moduleName);
                        continue load;
                    }

                    optionalCloudModule.ifPresent(loadOrder::add);
                }
                loadOrder.add(cloudModule);
            }

            List<CloudModule> forLoading = new ArrayList<>(resolveDependenciesSorted(new ArrayList<>(loadOrder)));
            Collections.reverse(forLoading);
            forLoading.forEach(cloudModule -> {
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

    private void handleUpdate(final List<Path> toUpdate) {
        for (Path path : toUpdate) {
            Optional<JavaCloudModule> cloudModule = loadModule(path);
        }
    }

    private void handleLoaded(final List<Path> toLoaded, final List<Path> toUpdate) {
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
        for (final Path path : toUpdate) {
            Optional<JavaCloudModule> cloudModule = loadModule(path);
            cloudModule.ifPresent(javaCloudModule -> {
                final Optional<CloudModule> moduleOptional = this.getModule(javaCloudModule
                                                                                .getModuleJson()
                                                                                .getGroupId() + ":" + javaCloudModule
                    .getModuleJson()
                    .getName());
                if (moduleOptional.isPresent()) {
                    CloudModule module = moduleOptional.get();
                    if (module instanceof JavaCloudModule && module.isUpdate()) {
                        if (javaCloudModule.getModuleJson().getSemVersion().isGreaterThan(module.getModuleJson().getSemVersion())) {
                            JavaCloudModule jcm = (JavaCloudModule) module;
                            unloadModule(jcm);
                            this.modules.remove(module
                                                    .getModuleJson()
                                                    .getGroupId() + ":" + module.getModuleJson().getName());
                            if (jcm instanceof MigrateCloudModule) {
                                MigrateCloudModule migrateCloudModule = (MigrateCloudModule) jcm;
                                if (migrateCloudModule.migrate(module.getModuleJson().getSemVersion(),
                                                               javaCloudModule.getModuleJson().getSemVersion())) {
                                    jcm.getModuleLogger().info(String.format("Module %s successfully migrated from %s to %s",
                                                                             module.getModuleJson().getName(),
                                                                             module.getModuleJson().getVersion(),
                                                                             javaCloudModule.getModuleJson().getVersion()));
                                    try {
                                        Files.deleteIfExists(module.getModuleJson().getFile());
                                        Files.copy(javaCloudModule.getModuleJson().getFile(), module.getModuleJson().getFile());
                                        Files.deleteIfExists(javaCloudModule.getModuleJson().getFile());
                                        final Optional<JavaCloudModule> optionalJavaCloudModule = loadModule(module.getModuleJson()
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
                                    jcm.getModuleLogger().info(String.format("Module %s could not be migrated from %s to %s ",
                                                                             module.getModuleJson().getName(),
                                                                             module.getModuleJson().getVersion(),
                                                                             javaCloudModule.getModuleJson().getVersion()));
                                }
                            } else {
                                try {
                                    Files.copy(javaCloudModule.getModuleJson().getFile(), module.getModuleJson().getFile());
                                    Files.deleteIfExists(javaCloudModule.getModuleJson().getFile());
                                    final Optional<JavaCloudModule> optionalJavaCloudModule = loadModule(module.getModuleJson().getFile());
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
        final List<CloudModule> cloudModules = resolveDependenciesSorted(new ArrayList<>(getModules().values()));
        final Set<CloudModule> loadOrder = new HashSet<>();
        load:
        for (CloudModule cloudModule : cloudModules) {
            String moduleName = cloudModule.getModuleJson().getGroupId() + ":" + cloudModule.getModuleJson().getName();
            if (!this.semCloudNetVersion.satisfies(cloudModule.getModuleJson().getRequiredCloudNetVersion())) {
                System.err.println("Cannot load module " + moduleName + " because of missing required CloudNet version");
                this.modules.remove(moduleName);
                continue load;
            }
            for (final CloudModuleDependency dependency : cloudModule.getModuleJson().getDependencies()) {
                String name = dependency.getGroupId() + ":" + dependency.getName();
                final Optional<CloudModule> optionalCloudModule = getModule(name);
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
        List<CloudModule> forLoading = new ArrayList<>(resolveDependenciesSorted(new ArrayList<>(loadOrder)));
        Collections.reverse(forLoading);
        forLoading.stream()
                  .filter(javaCloudModule -> !javaCloudModule.isLoaded())
                  .forEach(javaCloudModule -> {
                      javaCloudModule.getModuleLogger().info(String.format("Loading module %s from %s with version %s",
                                                                           javaCloudModule.getModuleJson().getName(),
                                                                           javaCloudModule.getModuleJson().getAuthorsAsString(),
                                                                           javaCloudModule.getModuleJson().getVersion()));
                      javaCloudModule.setLoaded(true);
                  });
    }

    public Optional<JavaCloudModule> loadModule(Path path) {
        Optional<JavaCloudModule> javaModule = Optional.empty();
        try {
            Optional<CloudModuleDescriptionFile> cloudModuleDescriptionFile = getCloudModuleDescriptionFile(path);
            if (cloudModuleDescriptionFile.isPresent()) {
                ModuleClassLoader classLoader = new ModuleClassLoader(getClass().getClassLoader(), path, this);
                final Class<?> jarClazz = classLoader.loadClass(cloudModuleDescriptionFile.get().getMain());
                final Class<? extends JavaCloudModule> mainClazz = jarClazz.asSubclass(JavaCloudModule.class);
                final JavaCloudModule javaCloudModule = mainClazz.getDeclaredConstructor().newInstance();
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

    public Optional<CloudModuleDescriptionFile> getCloudModuleDescriptionFile(Path module) {
        Optional<CloudModuleDescriptionFile> cloudModuleDescriptionFile = Optional.empty();
        if (module != null) {
            try (JarFile moduleJar = new JarFile(module.toFile())) {
                ZipEntry moduleJsonFile = moduleJar.getEntry("module_v2.json");
                if (moduleJsonFile == null) {
                    throw new ModuleDescriptionFileNotFoundException("The module don't contain a module_v2.json!");
                }
                try (InputStream stream = moduleJar.getInputStream(moduleJsonFile)) {
                    CloudModuleDescriptionFile moduleDescriptionFile = new CloudModuleDescriptionFile(stream, module);
                    if (moduleDescriptionFile.getSemVersion().getMajor() != null && moduleDescriptionFile.getSemVersion()
                                                                                                         .getMajor() != null && moduleDescriptionFile
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
        } else {
            throw new ModuleNotFoundException("Module file not found");
        }
        return cloudModuleDescriptionFile;
    }

    private boolean isModuleDetectedByPath(@NotNull Path path, List<Path> toLoad) {
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

    Class<?> getClassByName(final String name) {
        Class<?> cachedClass = classes.get(name);

        if (cachedClass != null) {
            return cachedClass;
        } else {
            for (String current : loaders.keySet()) {
                ModuleClassLoader loader = loaders.get(current);

                try {
                    cachedClass = loader.findClass(name, false);
                } catch (ClassNotFoundException cnfe) {
                }
                if (cachedClass != null) {
                    return cachedClass;
                }
            }
        }
        return null;
    }

    void setClass(final String name, final Class<?> clazz) {
        if (!classes.containsKey(name)) {
            classes.put(name, clazz);
        }
    }

    private void removeClass(String name) {
        classes.remove(name);
    }

    public Map<String, CloudModule> getModules() {
        return modules;
    }

    public Optional<CloudModule> getModule(@NotNull String name) {
        return Optional.of(getModules().get(name));
    }

    private List<CloudModule> resolveDependenciesSortedSingle(@NotNull List<CloudModule> cloudModuleDescriptionFiles,
                                                              CloudModule javaCloudModule) {
        MutableGraph<CloudModule> graph = GraphBuilder
            .directed()
            .expectedNodeCount(cloudModuleDescriptionFiles.size())
            .allowsSelfLoops(false)
            .build();
        Map<String, CloudModule> candidateAsMap = Maps.uniqueIndex(cloudModuleDescriptionFiles,
                                                                   f -> String.format("%s:%s",
                                                                                      f.getModuleJson().getGroupId(),
                                                                                      f.getModuleJson().getName()));

        CloudModule descriptionFile = javaCloudModule;
        graph.addNode(descriptionFile);
        for (CloudModuleDependency dependency : descriptionFile.getModuleJson().getDependencies()) {
            CloudModule dependencyContainer = candidateAsMap.get(String.format("%s:%s",
                                                                               dependency.getGroupId(),
                                                                               dependency.getName()));
            if (dependencyContainer != null) {
                graph.putEdge(dependencyContainer, descriptionFile);
            }
        }

        List<CloudModule> sorted = new ArrayList<>();
        Map<CloudModule, Integer> integerMap = new HashMap<>();

        for (CloudModule node : graph.nodes()) {
            this.visitDependency(graph, node, integerMap, sorted, new ArrayDeque<>());
        }

        return sorted;
    }

    private List<CloudModule> resolveDependenciesSorted(@NotNull List<CloudModule> cloudModuleDescriptionFiles) {
        MutableGraph<CloudModule> graph = GraphBuilder
            .directed()
            .expectedNodeCount(cloudModuleDescriptionFiles.size())
            .allowsSelfLoops(false)
            .build();
        Map<String, CloudModule> candidateAsMap = Maps.uniqueIndex(cloudModuleDescriptionFiles,
                                                                   f -> String.format("%s:%s",
                                                                                      f.getModuleJson().getGroupId(),
                                                                                      f.getModuleJson().getName()));

        for (CloudModule descriptionFile : cloudModuleDescriptionFiles) {
            graph.addNode(descriptionFile);
            for (CloudModuleDependency dependency : descriptionFile.getModuleJson().getDependencies()) {
                CloudModule dependencyContainer = candidateAsMap.get(String.format("%s:%s",
                                                                                   dependency.getGroupId(),
                                                                                   dependency.getName()));
                if (dependencyContainer != null) {
                    graph.putEdge(dependencyContainer, descriptionFile);
                }
            }
        }
        List<CloudModule> sorted = new ArrayList<>();
        Map<CloudModule, Integer> integerMap = new HashMap<>();

        for (CloudModule node : graph.nodes()) {
            this.visitDependency(graph, node, integerMap, sorted, new ArrayDeque<>());
        }

        return sorted;
    }

    //TODO: Gibt es ein Limit für den Integer von der Map. Wenn ja die If etwas umbauen.

    private void visitDependency(Graph<CloudModule> graph,
                                 CloudModule node,
                                 Map<CloudModule, Integer> marks,
                                 List<CloudModule> sorted,
                                 Deque<CloudModule> currentIteration) {

        final Integer integer = marks.getOrDefault(node, 0);
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
