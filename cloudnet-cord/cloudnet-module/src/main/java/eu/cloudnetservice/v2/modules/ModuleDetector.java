package eu.cloudnetservice.v2.modules;

import eu.cloudnetservice.v2.modules.exception.ModuleLoadException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * Class for finding modules in a given directory
 */
public class ModuleDetector {

    /**
     * Finds and reads potential modules from a given directory.
     *
     * @param dir the directory to search in
     *
     * @return a set containing all found and valid modules, an empty set, if
     * the given {@code dir} is not a directory
     */
    public Set<ModuleConfig> detectAvailable(Path dir) {
        Set<ModuleConfig> moduleConfigs = new HashSet<>();

        try (Stream<Path> files = Files.list(dir)) {
            files.filter(path -> Files.exists(path) && Files.isRegularFile(path) && path.toString().endsWith(".jar"))
                 .forEach(path -> {
                     try (JarFile jarFile = new JarFile(path.toFile())) {
                         JarEntry jarEntry = jarFile.getJarEntry("module.properties");
                         if (jarEntry == null) {
                             throw new ModuleLoadException("Cannot find \"module.properties\" file");
                         }

                         try (InputStreamReader reader = new InputStreamReader(jarFile.getInputStream(jarEntry), StandardCharsets.UTF_8)) {
                             Properties properties = new Properties();
                             properties.load(reader);
                             ModuleConfig moduleConfig = new ModuleConfig(path.toFile(),
                                                                          properties.getProperty("name"),
                                                                          properties.getProperty("version"),
                                                                          properties.getProperty("author"),
                                                                          properties.getProperty("main"));
                             moduleConfigs.add(moduleConfig);
                         }

                     } catch (Exception ex) {
                         ex.printStackTrace();
                     }
                 });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return moduleConfigs;
    }

}
