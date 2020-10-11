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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class loader enables a clean unloading of a module.
 */
public final class ModuleClassLoader extends URLClassLoader {

    private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
    private final CloudModuleManager moduleManager;


    static {
        ClassLoader.registerAsParallelCapable();
    }

    public ModuleClassLoader(final ClassLoader parent, final Path file, CloudModuleManager manager) throws
        MalformedURLException {
        super(new URL[] {file.toUri().toURL()}, parent);
        this.moduleManager = manager;
    }

    /**
     * Finds classes by name
     * @param name defines the class name
     * @return Returns the class object
     * @throws ClassNotFoundException is called if the classes cannot be found using the name
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    /**
     * Finds classes globally in the module manager or only in the class loader
     * @param name defines the class name
     * @param checkGlobal indicates whether the class should be searched for in the module manager.
     * @return Returns the class object
     * @throws ClassNotFoundException is called if the classes cannot be found using the name
     */
    Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        Class<?> result = classes.get(name);

        if (result == null) {
            if (checkGlobal) {
                result = moduleManager.getClassByName(name);
            }

            if (result == null) {
                result = super.findClass(name);

                if (result != null) {
                    moduleManager.setClass(name, result);
                }
            }

            classes.put(name, result);
        }

        return result;
    }

    Set<String> getClasses() {
        return classes.keySet();
    }
}
