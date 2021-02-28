package eu.cloudnetservice.cloudnet.v2.examples.module;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import org.jline.reader.ParsedLine;

public class ExampleModuleCommand extends Command {


    private final ModuleExample moduleExample;

    /**
     * Constructs a new command with a name, a needed permission and variable aliases.
     *
     * @param name       the name of this command
     * @param permission the permission a user has to have
     * @param aliases    other names of this command
     */
    public ExampleModuleCommand(ModuleExample moduleExample,String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.moduleExample = moduleExample;
    }

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        this.moduleExample.getModuleLogger().warning(String.format("ExampleCommand Sender: %s",sender.getName()));
    }
}
