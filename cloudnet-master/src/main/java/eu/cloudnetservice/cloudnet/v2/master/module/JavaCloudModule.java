package eu.cloudnetservice.cloudnet.v2.master.module;

import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.module.model.CloudModuleDescriptionFile;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaCloudModule implements CloudModule {

    private CloudModuleDescriptionFile moduleDescriptionFile;
    private CloudModuleLogger cloudModuleLogger;
    private ClassLoader classLoader;
    private boolean isEnabled = false;

    public JavaCloudModule() {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof ModuleClassLoader)) {
            throw new IllegalStateException("JavaPlugin requires " + ModuleClassLoader.class.getName());
        }
        ((ModuleClassLoader) classLoader).initialize(this);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public CloudModuleDescriptionFile getModuleJson() {
        return this.moduleDescriptionFile;
    }

    @Override
    public Path getDataFolder() {
        return Paths.get("modules",getModuleJson().getName());
    }

    @Override
    public CloudNet getCloud() {
        return CloudNet.getInstance();
    }

    @Override
    public CloudModuleLogger getModuleLogger() {
        return cloudModuleLogger;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    final void init(ClassLoader loader, CloudModuleDescriptionFile descriptionFile) {
        this.moduleDescriptionFile = descriptionFile;
        this.cloudModuleLogger = new CloudModuleLogger(this);
        this.classLoader = loader;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    protected void setEnabled(final boolean enabled) {
        if (isEnabled != enabled) {
            isEnabled = enabled;
            if (isEnabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }
}
