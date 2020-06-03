package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.module.JavaCloudModule;

/**
 * Created by Tareko on 23.08.2017.
 */
public final class CommandModules extends Command {

    public CommandModules() {
        super("modules", "cloudnet.cowmmand.modules", "m");

        description = "Lists all modules, versions and authors";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        sender.sendMessage("Running modules:", NetworkUtils.SPACE_STRING);
        for (JavaCloudModule module : CloudNet.getInstance().getModuleManager().getModules().values()) {
            sender.sendMessage(module.getModuleJson().getName() + ' ' + module.getModuleJson().getVersion() + " by " + module.getModuleJson().getAuthorsAsString() + NetworkUtils.EMPTY_STRING);
        }
    }
}
