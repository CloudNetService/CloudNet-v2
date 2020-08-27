package eu.cloudnetservice.cloudnet.v2.command;

import org.jline.reader.ParsedLine;

/**
 * Interface for classes that execute commands.
 */
public interface CommandExecutor {
    /**
     * Method that is called when a command should execute.
     *
     * @param sender the sender that dispatched the execution of the command
     * @param parsedLine the line with jline parser
     * @param args   the arguments that the command was called with
     */
    void onExecuteCommand(CommandSender sender, ParsedLine parsedLine, String[] args);
}
