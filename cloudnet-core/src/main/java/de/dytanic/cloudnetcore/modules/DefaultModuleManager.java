/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.modules;

import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;
import de.dytanic.cloudnetcore.CloudNet;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Tareko on 22.10.2017.
 */
public class DefaultModuleManager {

    private Collection<DefaultModule> modules = new CopyOnWriteArrayList<>();

    public DefaultModuleManager() throws Exception {
        Properties properties = new Properties();

        try (InputStream inputStream = CloudNet.class.getClassLoader().getResourceAsStream("modules/modules.properties")) {
            properties.load(inputStream);
        }

        Collection<?> property = Collections.list(properties.propertyNames());
        CollectionWrapper.iterator(property, new Runnabled() {
            @Override
            public void run(Object obj) {
                String pro = obj.toString();
                modules.add(new DefaultModule(pro, properties.getProperty(pro)));
            }
        });

        Path path;
        for (DefaultModule defaultModule : modules) {
            path = Paths.get("modules/" + defaultModule.getModuleName() + ".jar");

            Files.deleteIfExists(path);

            try (InputStream inputStream = defaultModule.stream()) {
                Files.copy(inputStream, path);
            }
        }
    }

    public Collection<DefaultModule> getModules() {
        return modules;
    }
}
