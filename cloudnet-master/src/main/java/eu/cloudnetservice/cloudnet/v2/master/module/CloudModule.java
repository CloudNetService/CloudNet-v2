package eu.cloudnetservice.cloudnet.v2.master.module;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.event.Event;
import eu.cloudnetservice.cloudnet.v2.event.EventListener;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.module.model.CloudModuleDescriptionFile;

import java.nio.file.Path;

/**
 * This class is the interface for the java module
 */
public interface CloudModule {

    /**
     * Is called after the update has been checked but before the module has been activated
     */
    void onLoad();

    /**
     * Is called when the module is activated
     */
    void onEnable();

    /**
     * Is called when the module is deactivated
     */
    void onDisable();

    /**
     * Allows to change the loading status of the module
     * @param loaded sets the new loaded status of the module
     */
    void setLoaded(boolean loaded);

    /**
     * Allows to change the active status of the module
     * If false, the module is deactivated, if true, it is activated
     * @param enabled is the new status of the module
     */
    void setEnabled(boolean enabled);

    /**
     * Sets the update status
     * @param update is the new status of the variable
     */
    void setUpdate(boolean update);

    /**
     * Can be used to interact with background information from the module
     * @return Returns the module description
     */
    CloudModuleDescriptionFile getModuleJson();

    /**
     * Can be used to interact with the data folder of the module
     * @return Returns the path to the folder
     */
    Path getDataFolder();

    /**
     * Can be used to interact with the main part of the cloud
     * @return Returns the current running Cloudnet instance
     */
    CloudNet getCloud();

    /**
     * The module logger can be used to output things to the console
     * @return Returns the logger instance
     */
    CloudModuleLogger getModuleLogger();

    /**
     * @return Indicates if the module was activated
     */
    boolean isEnabled();

    /**
     * @return Indicates if the module was loaded
     */
    boolean isLoaded();

    /**
     * @return Indicates whether an update is available
     */
    boolean isUpdate();

    /**
     * Registered a IEventListener objective to a Event Class
     *
     * @param eventListener is the event listener to be registered
     */
    <T extends Event> void registerListener(EventListener<T> eventListener);

    /**
     * Registered a command
     *
     * @param command is the command to register
     */
    void registerCommand(Command command);
}
