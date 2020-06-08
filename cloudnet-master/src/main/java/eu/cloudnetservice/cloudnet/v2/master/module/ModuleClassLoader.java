package eu.cloudnetservice.cloudnet.v2.master.module;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public class ModuleClassLoader extends URLClassLoader {

    private final Map<String, Class<?>> classes = new java.util.concurrent.ConcurrentHashMap<String, Class<?>>(); // Spigot
    private final CloudModuleManager moduleManager;

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public ModuleClassLoader(final ClassLoader parent, final Path file, CloudModuleManager manager) throws
        MalformedURLException {
        super(new URL[] {file.toUri().toURL()}, parent);
        this.moduleManager = manager;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

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
