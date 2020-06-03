package eu.cloudnetservice.cloudnet.v2.master.module;

import com.google.common.collect.Maps;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.vdurmont.semver4j.Requirement;
import com.vdurmont.semver4j.SemverException;
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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public final class CloudModuleManager {

    private final Map<String, JavaCloudModule> modules;
    private final Collection<Path> toLoad = new CopyOnWriteArrayList<>();
    private final Path moduleDirectory;

    public CloudModuleManager() {
        // Allows to use duplicated modules
        modules = new LinkedHashMap<>();
        this.moduleDirectory = Paths.get("modules");
        if (!Files.exists(this.moduleDirectory)) {
            try {
                Files.createDirectory(this.moduleDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void detectModules() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.moduleDirectory, "*.jar")) {
            for (Path path : stream) {
                if (this.isModuleDetectedByPath(path)) {
                    continue;
                }
                toLoad.add(path);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        handleLoaded();
    }

    public void disableModule(CloudModule module) {
        if (module instanceof JavaCloudModule) {
            if (module.isEnabled()) {
                String name = String.format("%s:%s:%s", module.getModuleJson().getGroupId(),module.getModuleJson().getName(), module.getModuleJson().getVersion());
                module.getModuleLogger().info(String.format("Disabling %s from %s", name, module.getModuleJson().getAuthorsAsString()));
                JavaCloudModule javaCloudModule = (JavaCloudModule) module;
                if (this.modules.containsKey(name)) {
                    javaCloudModule.setEnabled(false);
                }
            }
        } else {
            System.err.println("Module is not associated with this ModuleLoader");
        }
    }

    public void enableModule(CloudModule module) {

        if (module instanceof JavaCloudModule) {
            if (!module.isEnabled()) {
                String name = String.format("%s:%s", module.getModuleJson().getGroupId(),module.getModuleJson().getName());
                module.getModuleLogger().info(String.format("Enabling %s from %s", name, module.getModuleJson().getAuthorsAsString()));

                JavaCloudModule javaCloudModule = (JavaCloudModule) module;
                final List<CloudModuleDescriptionFile> cloudModuleDescriptionFiles = this.resolveDependenciesSortedSignle(getModules().values()
                                                                                                                                      .stream()
                                                                                                                                      .map(
                                                                                                                                          JavaCloudModule::getModuleJson)
                                                                                                                                      .collect(
                                                                                                                                          Collectors
                                                                                                                                              .toList()),
                                                                                                                          javaCloudModule);
                load:
                for (CloudModuleDescriptionFile descriptionFile : cloudModuleDescriptionFiles) {
                    String moduleName = descriptionFile.getGroupId() + ":" + descriptionFile.getName();
                    for (final CloudModuleDependency dependency : descriptionFile.getDependencies()) {
                        String dependName = dependency.getGroupId() + ":" + dependency.getName();
                        final Optional<JavaCloudModule> omodule = getModule(dependName);
                        if (!omodule.isPresent()) {
                            System.err.println("unable to load module " + moduleName + " because of missing dependency " + dependName);
                            this.modules.remove(moduleName);
                            continue load;
                        }
                        if (omodule.get().getModuleJson().getSemVersion().satisfies(dependency.getVersion())) {
                            System.err.println("Cannot load module " + moduleName + " because of missing dependency with version " + dependency.getVersion());
                            this.modules.remove(moduleName);
                            continue load;
                        }
                        omodule.ifPresent(javaCloudModule1 -> javaCloudModule.setEnabled(true));
                    }
                }
            }
        } else {
            System.err.println("Module is not associated with this ModuleLoader");
        }
    }

    private void handleLoaded() {
        for (Path path : this.toLoad) {
            this.toLoad.remove(path);
            Optional<JavaCloudModule> cloudModule = loadModule(path);
            cloudModule.ifPresent(javaCloudModule -> this.modules.put(javaCloudModule.getModuleJson().getGroupId()+":"+javaCloudModule.getModuleJson().getName(), javaCloudModule));
        }
        final List<CloudModuleDescriptionFile> cloudModuleDescriptionFiles = resolveDependenciesSorted(getModules().values()
                                                                                                                   .stream()
                                                                                                                   .map(JavaCloudModule::getModuleJson)
                                                                                                                   .collect(Collectors.toList()));
        load:
        for (CloudModuleDescriptionFile descriptionFile : cloudModuleDescriptionFiles) {
            String moduleName = descriptionFile.getGroupId() + ":" + descriptionFile.getName();
            for (final CloudModuleDependency dependency : descriptionFile.getDependencies()) {
                String name = dependency.getGroupId() + ":" + dependency.getName();
                final Optional<JavaCloudModule> omodule = getModule(name);
                if (!omodule.isPresent()) {
                    System.err.println("unable to load module " + moduleName + " because of missing dependency " + name);
                    this.modules.remove(moduleName);
                    continue load;
                }
                if (omodule.get().getModuleJson().getSemVersion().satisfies(dependency.getVersion())) {
                    System.err.println("Cannot load module " + moduleName + " because of missing dependency with version " + dependency.getVersion());
                    this.modules.remove(moduleName);
                    continue load;
                }
                omodule.ifPresent(JavaCloudModule::onLoad);
            }
        }
    }

    public Optional<JavaCloudModule> loadModule(Path path) {
        Optional<JavaCloudModule> javaModule = Optional.empty();
        try {
            Optional<CloudModuleDescriptionFile> cloudModuleDescriptionFile = getCloudModuleDescriptionFile(path);
            if (cloudModuleDescriptionFile.isPresent()) {
                ModuleClassLoader classLoader = new ModuleClassLoader(getClass().getClassLoader(),cloudModuleDescriptionFile.get(),path);
                final Class<?> jarClazz = classLoader.loadClass(cloudModuleDescriptionFile.get().getMain());
                final Class<? extends JavaCloudModule> mainClazz = jarClazz.asSubclass(JavaCloudModule.class);
                javaModule = Optional.of(mainClazz.getDeclaredConstructor().newInstance());
            }
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return javaModule;
    }

    public Optional<CloudModuleDescriptionFile> getCloudModuleDescriptionFile(Path module) {
        if (module != null) {
            try (JarFile moduleJar = new JarFile(module.toFile())) {
                ZipEntry moduleJsonFile = moduleJar.getEntry("module_v2.json");
                if (moduleJsonFile == null) {
                    throw new ModuleDescriptionFileNotFoundException("The module don't contain a module_v2.json!");
                }
                try (InputStream stream = moduleJar.getInputStream(moduleJsonFile)) {
                    CloudModuleDescriptionFile moduleDescriptionFile = new CloudModuleDescriptionFile(stream,module);
                    if (moduleDescriptionFile.getSemVersion().getMajor() != null && moduleDescriptionFile.getSemVersion().getMajor() != null && moduleDescriptionFile.getSemVersion().getPatch() != null) {
                        return Optional.of(moduleDescriptionFile);
                    } else {
                        System.err.println(String.format("Module(%s) not enabling, wrong version format %s",moduleDescriptionFile.getName(),moduleDescriptionFile.getVersion()));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Something is wrong with the jar",e);
            }
        } else {
            throw new ModuleNotFoundException("Module file not found");
        }
        return Optional.empty();
    }

    private boolean isModuleDetectedByPath(@NotNull Path path) {
        boolean result = false;
        String check = path.toAbsolutePath().toString();
        Iterator<JavaCloudModule> moduleIterator = this.getModules().values().iterator();
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

    public Map<String, JavaCloudModule> getModules() {
        return modules;
    }

    public Optional<JavaCloudModule> getModule(@NotNull  String name) {
        return Optional.of(getModules().get(name));
    }

    private List<CloudModuleDescriptionFile> resolveDependenciesSortedSignle(@NotNull List<CloudModuleDescriptionFile> cloudModuleDescriptionFiles,JavaCloudModule javaCloudModule) {
        MutableGraph<CloudModuleDescriptionFile> graph = GraphBuilder
            .directed()
            .expectedNodeCount(cloudModuleDescriptionFiles.size())
            .allowsSelfLoops(false)
            .build();
        Map<String, CloudModuleDescriptionFile> candidateAsMap = Maps.uniqueIndex(cloudModuleDescriptionFiles, f -> String.format("%s:%s", f.getGroupId(), f.getName()));

        CloudModuleDescriptionFile descriptionFile = javaCloudModule.getModuleJson();
        graph.addNode(descriptionFile);
        for (CloudModuleDependency dependency : descriptionFile.getDependencies()) {
            CloudModuleDescriptionFile dependencyContainer = candidateAsMap.get(String.format("%s:%s", dependency.getGroupId(), dependency.getName()));
            if (dependencyContainer != null) {
                graph.putEdge(dependencyContainer, descriptionFile);
            }
        }

        List<CloudModuleDescriptionFile> sorted = new ArrayList<>();
        Map<CloudModuleDescriptionFile, Integer> integerMap = new HashMap<>();

        for (CloudModuleDescriptionFile node : graph.nodes()) {
            this.visitDependency(graph, node, integerMap, sorted, new ArrayDeque<>());
        }

        return sorted;
    }

    private List<CloudModuleDescriptionFile> resolveDependenciesSorted(@NotNull List<CloudModuleDescriptionFile> cloudModuleDescriptionFiles) {
        MutableGraph<CloudModuleDescriptionFile> graph = GraphBuilder
            .directed()
            .expectedNodeCount(cloudModuleDescriptionFiles.size())
            .allowsSelfLoops(false)
            .build();
        Map<String, CloudModuleDescriptionFile> candidateAsMap = Maps.uniqueIndex(cloudModuleDescriptionFiles, f -> String.format("%s:%s", f.getGroupId(), f.getName()));

        for (CloudModuleDescriptionFile descriptionFile : cloudModuleDescriptionFiles) {
            graph.addNode(descriptionFile);
            for (CloudModuleDependency dependency : descriptionFile.getDependencies()) {
                CloudModuleDescriptionFile dependencyContainer = candidateAsMap.get(String.format("%s:%s", dependency.getGroupId(), dependency.getName()));
                if (dependencyContainer != null) {
                    graph.putEdge(dependencyContainer, descriptionFile);
                }
            }
        }
        List<CloudModuleDescriptionFile> sorted = new ArrayList<>();
        Map<CloudModuleDescriptionFile, Integer> integerMap = new HashMap<>();

        for (CloudModuleDescriptionFile node : graph.nodes()) {
            this.visitDependency(graph, node, integerMap, sorted, new ArrayDeque<>());
        }

        return sorted;
    }

    //TODO: Gibt es ein Limit für den Integer von der Map. Wenn ja die If etwas umbauen.

    private void visitDependency(Graph<CloudModuleDescriptionFile> graph,
                                 CloudModuleDescriptionFile node,
                                 Map<CloudModuleDescriptionFile, Integer> marks,
                                 List<CloudModuleDescriptionFile> sorted,
                                 Deque<CloudModuleDescriptionFile> currentIteration) {

        if (marks.getOrDefault(node, 0) == 1) {
            currentIteration.addLast(node);

            StringBuilder stringBuilder = new StringBuilder();
            for (CloudModuleDescriptionFile description : currentIteration) {
                stringBuilder.append(description.getGroupId()).append(":").append(description.getName()).append("; ");
            }
            throw new StackOverflowError("Dependency load injects itself or other injects other: " + stringBuilder.toString());
        }

        currentIteration.addLast(node);
        marks.put(node, 2);
        for (CloudModuleDescriptionFile edge : graph.successors(node)) {
            this.visitDependency(graph, edge, marks, sorted, currentIteration);
        }

        marks.put(node, 2);
        currentIteration.removeLast();
        sorted.add(node);
    }
}
