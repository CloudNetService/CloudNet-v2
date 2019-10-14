/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.modules;

import de.dytanic.cloudnet.modules.exception.ModuleLoadException;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
    public Set<ModuleConfig> detectAvailable(File dir) {
        Set<ModuleConfig> moduleConfigs = new HashSet<>();

        File[] files = dir.listFiles(pathname -> pathname.isFile() && pathname.exists() && pathname.getName().endsWith(".jar"));
        if (files == null) {
            return moduleConfigs;
        }
        for (File file : files) {
            try (JarFile jarFile = new JarFile(file)) {
                JarEntry jarEntry = jarFile.getJarEntry("module.properties");
                if (jarEntry == null) {
                    throw new ModuleLoadException("Cannot find \"module.properties\" file");
                }

                try (InputStreamReader reader = new InputStreamReader(jarFile.getInputStream(jarEntry), StandardCharsets.UTF_8)) {
                    Properties properties = new Properties();
                    properties.load(reader);
                    ModuleConfig moduleConfig = new ModuleConfig(file,
                                                                 properties.getProperty("name"),
                                                                 properties.getProperty("version"),
                                                                 properties.getProperty("author"),
                                                                 properties.getProperty("main"));
                    moduleConfigs.add(moduleConfig);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return moduleConfigs;
    }

}
