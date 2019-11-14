/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.modules;

import de.dytanic.cloudnetcore.CloudNet;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Tareko on 22.10.2017.
 */
public class DefaultModuleManager {

    private List<DefaultModule> modules = new ArrayList<>();

    public DefaultModuleManager() throws Exception {

        try (InputStream inputStream = CloudNet.class.getClassLoader().getResourceAsStream("modules/modules.properties")) {
            if (inputStream != null) {
                Properties properties = new Properties();
                properties.load(inputStream);
                properties.stringPropertyNames()
                          .forEach(property ->
                                       modules.add(new DefaultModule(property, properties.getProperty(property))));
            }
        }

        for (DefaultModule defaultModule : modules) {
            Path path = Paths.get("modules/" + defaultModule.getModuleName() + ".jar");
            Files.deleteIfExists(path);

            try (InputStream inputStream = defaultModule.stream()) {
                Files.copy(inputStream, path);
            }
        }
    }

    public List<DefaultModule> getModules() {
        return modules;
    }
}
