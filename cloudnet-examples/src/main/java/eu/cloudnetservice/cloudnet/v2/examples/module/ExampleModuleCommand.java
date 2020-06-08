package eu.cloudnetservice.cloudnet.v2.examples.module;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;

public class ExampleModuleCommand extends Command {
    /**
     * Constructs a new command with a name, a needed permission and variable aliases.
     *
     * @param name       the name of this command
     * @param permission the permission a user has to have
     * @param aliases    other names of this command
     */

    private final ModuleExample moduleExample;

    public ExampleModuleCommand(ModuleExample moduleExample,final String name, final String permission, final String... aliases) {
        super(name, permission, aliases);
        this.moduleExample = moduleExample;
    }

    @Override
    public void onExecuteCommand(final CommandSender sender, final String[] args) {
        this.moduleExample.getModuleLogger().warning(String.format("ExampleCommand Sender: %s",sender.getName()));
    }
}
