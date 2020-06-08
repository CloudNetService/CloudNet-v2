package eu.cloudnetservice.cloudnet.v2.master.module;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.event.Event;
import eu.cloudnetservice.cloudnet.v2.event.EventKey;
import eu.cloudnetservice.cloudnet.v2.event.EventListener;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.module.model.CloudModuleDescriptionFile;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaCloudModule extends EventKey implements CloudModule {

    private CloudModuleDescriptionFile moduleDescriptionFile;
    private CloudModuleLogger cloudModuleLogger;
    private ClassLoader classLoader;
    private boolean isEnabled = false;
    private boolean isLoaded = false;
    private boolean isUpdate = false;

    public JavaCloudModule() {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof ModuleClassLoader)) {
            throw new IllegalStateException("JavaPlugin requires " + ModuleClassLoader.class.getName());
        }
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

    protected final void init(ClassLoader loader, CloudModuleDescriptionFile descriptionFile) {
        this.moduleDescriptionFile = descriptionFile;
        this.cloudModuleLogger = new CloudModuleLogger(this);
        this.classLoader = loader;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public boolean isUpdate() {
        return this.isUpdate;
    }

    @Override
    public void setLoaded(final boolean loaded) {
        if (isLoaded != loaded) {
            isLoaded = loaded;
            if (isLoaded) {
                onLoad();
            }
        }
    }

    @Override
    public void setEnabled(final boolean enabled) {
        if (isEnabled != enabled) {
            isEnabled = enabled;
            if (isEnabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    @Override
    public void setUpdate(final boolean update) {
        if (isUpdate != update) {
            isUpdate = update;
        }
    }


    /**
     * Registered a IEventListener objective to a Event Class
     *
     * @param eventListener
     * @param <T>
     */
    @Override
    public <T extends Event> void registerListener(EventListener<T> eventListener) {
        CloudNet.getInstance().getEventManager().registerListener(this, eventListener);
    }

    /**
     * Registered a command
     *
     * @param command
     */
    @Override
    public void registerCommand(Command command) {
        CloudNet.getInstance().getCommandManager().registerCommand(command);
    }
}
