package de.dytanic.cloudnet.command;

/**
 * Class that defines a command sender in a terminal.
 * An instance of this class has all permissions, a random UUID and the name {@code CONSOLE}
 */
public class ConsoleCommandSender implements CommandSender {

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public void sendMessage(String... message) {
        for (final String s : message) {
            System.out.println(s);
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return true; // CONSOLE has all permissions
    }

}
