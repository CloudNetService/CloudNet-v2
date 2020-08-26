package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.command.TabCompletable;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.WrapperMeta;
import eu.cloudnetservice.cloudnet.v2.master.setup.SetupCreateWrapper;

import java.util.ArrayList;
import java.util.List;

public class CommandWrapper extends Command implements TabCompletable {
    /**
     * Constructs a new command with a name, a needed permission and variable aliases.
     */
    public CommandWrapper() {
        super("wrapper", "cloudnet.command.wrapper", "w");
    }

    @Override
    public void onExecuteCommand(final CommandSender sender, final String[] args) {
        if (args.length <= 0) {
            sender.sendMessage("wrapper create - Start a wrapper setup to generate a config.yml");
            sender.sendMessage("wrapper list - List all wrappers");
            return;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("create")) {
                CloudNet.getInstance().getConsoleRegistry().registerInput(new SetupCreateWrapper(sender));
                CloudNet.getInstance().getConsoleManager().changeConsoleInput(SetupCreateWrapper.class);
            }
            if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage("Registered   Wrappers:");
                CloudNet.getInstance().getConfig().getWrappers().stream().map(WrapperMeta::getId).forEach(sender::sendMessage);
            }
        }
    }

    @Override
    public List<String> onTab(final long argsLength, final String lastWord, String[] args) {
        List<String> strings = new ArrayList<>();
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("wrapper")) {
                strings.add("create");
                strings.add("list");
            }
        }
        return strings;
    }
}
