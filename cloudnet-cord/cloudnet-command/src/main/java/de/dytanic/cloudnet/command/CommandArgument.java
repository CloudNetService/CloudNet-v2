/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 13.07.2017.
 */
@Getter
@AllArgsConstructor
public abstract class CommandArgument {

    private String name;

    public abstract void preExecute(Command command, String commandLine);

    public abstract void postExecute(Command command, String commandLine);

}