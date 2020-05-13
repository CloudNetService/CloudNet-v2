package eu.cloudnetservice.v2.master.command;

import eu.cloudnetservice.v2.command.Command;
import eu.cloudnetservice.v2.command.CommandSender;
import eu.cloudnetservice.v2.lib.NetworkUtils;
import eu.cloudnetservice.v2.modules.Module;
import eu.cloudnetservice.v2.master.CloudNet;

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
        for (Module module : CloudNet.getInstance().getModuleManager().getModules()) {
            sender.sendMessage(module.getName() + ' ' + module.getModuleConfig().getVersion() + " by " + module.getModuleConfig()
                                                                                                               .getAuthor() + NetworkUtils.EMPTY_STRING);
        }
    }
}
