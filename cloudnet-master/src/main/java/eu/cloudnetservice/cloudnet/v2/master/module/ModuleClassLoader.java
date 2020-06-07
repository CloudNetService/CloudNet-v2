package eu.cloudnetservice.cloudnet.v2.master.module;

import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.module.model.CloudModuleDescriptionFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class ModuleClassLoader extends URLClassLoader {

    static {
        ClassLoader.registerAsParallelCapable();
    }
    private final CloudModuleDescriptionFile description;
    private final ClassLoader loader;

    public ModuleClassLoader(final ClassLoader parent, final CloudModuleDescriptionFile description, final Path file) throws
        MalformedURLException {
        super(new URL[] {file.toUri().toURL()}, parent);
        this.loader = parent;
        this.description = description;
    }

    synchronized void initialize(final JavaCloudModule javaCloudModule) {
        javaCloudModule.init(this.loader,this.description);
    }
}
