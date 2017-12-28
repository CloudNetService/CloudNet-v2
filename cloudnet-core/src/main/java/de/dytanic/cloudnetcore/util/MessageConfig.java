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

    public MessageConfig()
    {
        if (!Files.exists(path))
        {
            new Document()
                    .append("prefix", "§bCloud §8|§7 ")
                    .append("kick-maintenance", "§cThe network is currently in maintenance mode")
                    .append("full-join", "§cThe Network is full! you can join with a higher permissions!")
                    .append("hubCommandNoServerFound", "§cNo Server was founded please waiting")
                    .append("joinpower-deny", "You don't have any permissions to enter this server")
                    .append("server-group-maintenance-kick", "§cThis group is currently in maintenance!")
                    .append("mob-selector-maintenance-message", "§cThis group is currently in maintenance mode, please wait, before you can play!")
                    .append("notify-message-server-add", "§cThe server %server% starting...")
                    .append("notify-message-server-remove", "§cThe server %server% is now stopped!")
                    .append("hub-already", "§cYou are already on the hub server")
                    .saveAsConfig(path);
        }
    }

    public Document load()
    {

        Document document = Document.loadDocument(path);

        if(!document.contains("mob-selector-maintenance-message"))
        {
            document.append("mob-selector-maintenance-message", "§cThis group is currently in maintenance mode, please wait, before you can play!").saveAsConfig(path);
        }

        if(!document.contains("notify-message-server-add"))
        {
            document.append("notify-message-server-add", "§cThe server %server% starting...").saveAsConfig(path);
        }

        if(!document.contains("notify-message-server-remove"))
        {
            document.append("notify-message-server-remove", "§cThe server %server% is now stopped!").saveAsConfig(path);
        }

        if(!document.contains("hub-already"))
        {
            document.append("hub-already", "§cYou are already on the hub server").saveAsConfig(path);
        }

        return document;
    }
}