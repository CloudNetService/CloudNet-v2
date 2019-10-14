/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.util;

import de.dytanic.cloudnet.lib.utility.document.Document;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Tareko on 20.08.2017.
 */
public class MessageConfig {

    private Path path = Paths.get("local/ingame_messages.json");

    public MessageConfig() {
        if (!Files.exists(path)) {
            new Document().append("prefix", "§bCloud §8|§7 ")
                          .append("kick-maintenance", "§cThe network is currently in maintenance mode")
                          .append("full-join", "§cThe Network is full! You can join with a higher permissions!")
                          .append("hubCommandNoServerFound", "§cNo server was found, please wait")
                          .append("joinpower-deny", "You don't have any permissions to enter this server")
                          .append("server-group-maintenance-kick", "§cThis group is currently in maintenance mode!")
                          .append("mob-selector-maintenance-message",
                                  "§cThis group is currently in maintenance mode, please wait, before you can play!")
                          .append("notify-message-server-add", "§cThe server %server% is starting now...")
                          .append("notify-message-server-remove", "§cThe server %server% is now stopping!")
                          .append("hub-already", "§cYou are already connected to a hub server")
                          .append("server-kick-proxy-disallow", "§cYou have to connect from a internal proxy server!")
                          .saveAsConfig(path);
        }
    }

    public Document load() {

        boolean resave = false;
        Document document = Document.loadDocument(path);

        if (!document.contains("server-kick-proxy-disallow")) {
            document.append("server-kick-proxy-disallow", "§cYou have to connect from a internal proxy server!");
            resave = true;
        }

        if (resave) {
            document.saveAsConfig(path);
        }

        return document;
    }
}
