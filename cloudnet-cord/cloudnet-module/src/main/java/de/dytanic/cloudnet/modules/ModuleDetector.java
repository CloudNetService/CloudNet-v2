/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.modules;

import de.dytanic.cloudnet.modules.exception.ModuleLoadException;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Tareko on 23.07.2017.
 */
public class ModuleDetector {

    public Set<ModuleConfig> detectAvaible(File dir)
    {
        Set<ModuleConfig> moduleConfigs = new HashSet<>();

        for(File file : dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname)
            {
                return pathname.isFile() && pathname.exists() && pathname.getName().endsWith(".jar");
            }
        }))
        {
            try(JarFile jarFile = new JarFile(file))
            {

                JarEntry jarEntry = jarFile.getJarEntry("module.properties");
                if(jarEntry == null)
                {
                    throw new ModuleLoadException("Cannot find \"module.properties\" file");
                }

                try(InputStream inputStream = jarFile.getInputStream(jarEntry); InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                {
                    Properties properties = new Properties();
                    properties.load(reader);
                    ModuleConfig moduleConfig = new ModuleConfig(file, properties.getProperty("name"), properties.getProperty("version"), properties.getProperty("author"), properties.getProperty("main"));
                    moduleConfigs.add(moduleConfig);
                }

            }catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        return moduleConfigs;
    }

}