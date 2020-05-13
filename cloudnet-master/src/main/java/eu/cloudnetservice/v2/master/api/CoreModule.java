package eu.cloudnetservice.v2.master.api;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnet.event.EventListener;
import de.dytanic.cloudnet.modules.Module;
import eu.cloudnetservice.v2.master.CloudNet;

/**
 * This Class creates the simply option of simple registering of Listeners and Commands
 */
public class CoreModule extends Module<CloudNet> {

    /**
     * Registered a IEventListener objective to a Event Class
     *
     * @param eventListener
     * @param <T>
     */
    public <T extends Event> void registerListener(EventListener<T> eventListener) {
        CloudNet.getInstance().getEventManager().registerListener(this, eventListener);
    }

    /**
     *
     */
    public void appendModuleProperty(String key, Object value) {
        CloudNet.getInstance().getNetworkManager().getModuleProperties().append(key, value);
    }

    /**
     * Registered a command
     *
     * @param command
     */
    public void registerCommand(Command command) {
        CloudNet.getInstance().getCommandManager().registerCommand(command);
    }

    @Override
    public CloudNet getCloud() {
        return CloudNet.getInstance();
    }
}
