package eu.cloudnetservice.cloudnet.v2.command;

import eu.cloudnetservice.cloudnet.v2.lib.interfaces.Nameable;

import java.util.Collection;
import java.util.HashSet;

/**
 * Abstract class to define a command with an executor and a name
 */
public abstract class Command implements CommandExecutor, Nameable {

    protected String name;
    protected String permission;
    protected String[] aliases;

    protected String description = "Default command description";

    /**
     * Constructs a new command with a name, a needed permission and variable aliases.
     *
     * @param name       the name of this command
     * @param permission the permission a user has to have
     * @param aliases    other names of this command
     */
    protected Command(String name, String permission, String... aliases) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }

    public String[] getAliases() {
        return aliases;
    }
}
