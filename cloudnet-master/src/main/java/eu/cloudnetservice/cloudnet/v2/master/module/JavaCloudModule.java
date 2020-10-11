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

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.event.Event;
import eu.cloudnetservice.cloudnet.v2.event.EventKey;
import eu.cloudnetservice.cloudnet.v2.event.EventListener;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.module.model.CloudModuleDescriptionFile;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Allows you to write your own module based on this class
 */
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

    /**
     * Is called after the update has been checked but before the module has been activated
     */
    @Override
    public void onLoad() {

    }

    /**
     * Is called when the module is activated
     */
    @Override
    public void onEnable() {

    }

    /**
     * Is called when the module is deactivated
     */
    @Override
    public void onDisable() {

    }

    /**
     * Can be used to interact with background information from the module
     * @return Returns the module description
     */
    @Override
    public CloudModuleDescriptionFile getModuleJson() {
        return this.moduleDescriptionFile;
    }

    /**
     * Can be used to interact with the data folder of the module
     * @return Returns the path to the folder
     */
    @Override
    public Path getDataFolder() {
        return Paths.get("modules", getModuleJson().getName());
    }

    /**
     * Can be used to interact with the main part of the cloud
     * @return Returns the current running Cloudnet instance
     */
    @Override
    public CloudNet getCloud() {
        return CloudNet.getInstance();
    }

    /**
     * The module logger can be used to output things to the console
     * @return Returns the logger instance
     */
    @Override
    public CloudModuleLogger getModuleLogger() {
        return cloudModuleLogger;
    }

    /**
     * Can be used to interact with the class loader module
     * @return Returns the class loader from the module instance
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    protected final void init(ClassLoader loader, CloudModuleDescriptionFile descriptionFile) {
        this.moduleDescriptionFile = descriptionFile;
        this.cloudModuleLogger = new CloudModuleLogger(this);
        this.classLoader = loader;
    }

    /**
     * @return Indicates if the module was activated
     */
    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * @return Indicates if the module was loaded
     */
    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * @return Indicates whether an update is available
     */
    @Override
    public boolean isUpdate() {
        return this.isUpdate;
    }

    /**
     * Allows to change the loading status of the module
     * @param loaded sets the new loaded status of the module
     */
    @Override
    public void setLoaded(boolean loaded) {
        if (isLoaded != loaded) {
            isLoaded = loaded;
            if (isLoaded) {
                onLoad();
            }
        }
    }

    /**
     * Allows to change the active status of the module
     * If false, the module is deactivated, if true, it is activated
     * @param enabled is the new status of the module
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled != enabled) {
            isEnabled = enabled;
            if (isEnabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    /**
     * Sets the update status
     * @param update is the new status of the variable
     */
    @Override
    public void setUpdate(boolean update) {
        if (isUpdate != update) {
            isUpdate = update;
        }
    }


    /**
     * Registered a IEventListener objective to a Event Class
     *
     * @param eventListener is the event listener to be registered
     */
    @Override
    public <T extends Event> void registerListener(EventListener<T> eventListener) {
        CloudNet.getInstance().getEventManager().registerListener(this, eventListener);
    }

    /**
     * Registered a command
     *
     * @param command is the command to register
     */
    @Override
    public void registerCommand(Command command) {
        CloudNet.getInstance().getCommandManager().registerCommand(command);
    }
}
