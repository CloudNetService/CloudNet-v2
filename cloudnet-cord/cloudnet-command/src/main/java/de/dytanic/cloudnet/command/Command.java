/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.command;

import de.dytanic.cloudnet.lib.interfaces.Nameable;

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

    private Collection<CommandArgument> commandArguments = new HashSet<>();

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

    /**
     * Appends a new argument to this command
     *
     * @param commandArgument the argument to append
     * @param <T>             a subclass of {@link Command}
     *
     * @return the command for chaining
     */
    protected <T extends Command> T appendArgument(CommandArgument commandArgument) {
        this.commandArguments.add(commandArgument);
        //noinspection unchecked
        return (T) this;
    }

    @Override
    public String getName() {
        return name;
    }

    public Collection<CommandArgument> getCommandArguments() {
        return commandArguments;
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
