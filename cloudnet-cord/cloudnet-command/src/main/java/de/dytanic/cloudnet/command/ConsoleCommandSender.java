/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.command;


import java.util.Arrays;

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
        Arrays.asList(message).forEach(System.out::println);
    }

    @Override
    public boolean hasPermission(String permission) {
        return true; // CONSOLE has all permissions
    }

}
