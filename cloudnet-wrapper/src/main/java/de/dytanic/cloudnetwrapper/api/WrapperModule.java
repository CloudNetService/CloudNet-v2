package de.dytanic.cloudnetwrapper.api;
/*
 * Created by Mc_Ruben on 29.09.2018
 */

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.modules.Module;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public class WrapperModule extends Module<CloudNetWrapper> {

    /**
     * Registers a command to the CloudNet-Wrapper
     * @param command the command to register
     */
    public void registerCommand(Command command) {
        getCloud().getCommandManager().registerCommand(command);
    }

    @Override
    public CloudNetWrapper getCloud() {
        return CloudNetWrapper.getInstance();
    }
}
