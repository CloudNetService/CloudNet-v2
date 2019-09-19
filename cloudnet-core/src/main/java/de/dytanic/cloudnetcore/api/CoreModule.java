/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnet.event.IEventListener;
import de.dytanic.cloudnet.modules.Module;
import de.dytanic.cloudnetcore.CloudNet;

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
    public <T extends Event> void registerListener(IEventListener<T> eventListener) {
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
