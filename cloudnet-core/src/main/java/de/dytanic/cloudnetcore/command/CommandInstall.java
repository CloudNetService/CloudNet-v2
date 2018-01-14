/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.web.client.WebClient;
import de.dytanic.cloudnetcore.CloudNet;

import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Tareko on 23.08.2017.
 */
public class CommandInstall extends Command {

    private WebClient webClient;

    public CommandInstall()
    {
        super("install", "cloudnet.command.install");
        webClient = new WebClient();
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 1:
                if(args[0].equalsIgnoreCase("update"))
                {
                    CloudNet.getInstance().checkForUpdates();
                    return;
                }
                if(args[0].equalsIgnoreCase("modules"))
                {
                    sender.sendMessage("Installable Modules:");
                    for(String module : webClient.getInstallableModules())
                    {
                        sender.sendMessage("- " + module);
                    }
                    return;
                }
                if(args[0].equalsIgnoreCase("templates"))
                {
                    sender.sendMessage("Installable Templates:");
                    for(String templates : webClient.getInstallableTemplates())
                    {
                        sender.sendMessage("- \"" + WebClient.DEFAULT_URL + "/templates/" + templates + "\"");
                    }
                    sender.sendMessage("Copied the url of the template into the group");
                }
                break;
            case 2:
                if(args[0].equalsIgnoreCase("module"))
                {
                    try{
                        URLConnection urlConnection = new URL(WebClient.DEFAULT_URL + "modules/" + args[1]).openConnection();
                        urlConnection.connect();
                        Files.copy(urlConnection.getInputStream(), Paths.get("modules/" + args[1] + ".jar"));
                        sender.sendMessage("Module " + args[1] + " was successfully installed, please use the command \"reload modules\"");
                    }catch (Exception ex){
                        sender.sendMessage("Failed to download module \"" + args[1] + "\"");
                    }
                }
                break;
            default:
                sender.sendMessage(
                        "install update",
                        "install modules",
                        "install module <module>",
                        "install templates"
                );
                break;
        }
    }
}