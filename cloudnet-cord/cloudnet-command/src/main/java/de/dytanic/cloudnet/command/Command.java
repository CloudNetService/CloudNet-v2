/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.command;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Tareko on 23.05.2017.
 */
@Getter
public abstract class Command
        implements CommandExecutor, Nameable {

    protected String name;
    protected String permission;
    protected String[] aliases;

    protected String descrption = "Default command discrption";

    private Collection<CommandArgument> commandArguments = new HashSet<>();

    protected Command(String name, String permission, String... aliases)
    {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
    }

    protected <T extends Command> T appendArgument(CommandArgument commandArgument)
    {
        this.commandArguments.add(commandArgument);
        return (T) this;
    }
}