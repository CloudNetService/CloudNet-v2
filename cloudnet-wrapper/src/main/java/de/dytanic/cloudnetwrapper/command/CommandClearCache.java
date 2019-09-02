/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnetwrapper.util.FileUtility;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CommandClearCache extends Command {

    public CommandClearCache() {
        super("clearcache", "cloudnet.command.clearcache");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        try {
            FileUtility.deleteDirectory(new File("local/cache"));
            Files.createDirectories(Paths.get("local/cache/web_templates"));
            Files.createDirectories(Paths.get("local/cache/web_plugins"));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        sender.sendMessage("The Cache was cleared!");
    }
}
