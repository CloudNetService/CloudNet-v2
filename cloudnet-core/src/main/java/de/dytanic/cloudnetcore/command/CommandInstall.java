/*
 * @author _Klaro | Pasqual K.
 */

package de.dytanic.cloudnetcore.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.web.client.WebClient;
import de.dytanic.cloudnetcore.CloudNet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

public class CommandInstall extends Command
{
    private final WebClient webClient = new WebClient();

    public CommandInstall()
    {
        super("install", "cloudnet.command.install");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        switch (args.length)
        {
            case 1:
            {
                if (args[0].equalsIgnoreCase("update"))
                {
                    CloudNet.getInstance().checkForUpdates();
                    return;
                }
                Iterator iterator;
                if (args[0].equalsIgnoreCase("modules"))
                {
                    sender.sendMessage("Installable Modules:");
                    iterator = this.webClient.getInstallableModules().iterator();
                    while (iterator.hasNext())
                    {
                        sender.sendMessage("» " + iterator.next());
                    }
                    return;
                }
                break;
            }
            case 2:
            {
                if (args[0].equalsIgnoreCase("module"))
                {
                    try
                    {
                        URLConnection urlConnection = (new URL(webClient.DEFAULT_URL + "modules/" +  args[1] + ".jar")).openConnection();
                        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                        urlConnection.setConnectTimeout(1000);
                        urlConnection.connect();
                        try (InputStream inputStream = urlConnection.getInputStream())
                        {
                            Files.copy(inputStream, Paths.get("modules/" + args[1] + ".jar"), StandardCopyOption.REPLACE_EXISTING);
                        }
                        CloudNet.getInstance().getModuleManager().disableModules();
                        CloudNet.getInstance().getModuleManager().enableModules();
                    } catch (IOException ex)
                    {
                        sender.sendMessage("Failed to download module \"" + args[1] + ".jar\".");
                    }
                    return;
                }
                break;
            }
            default:
                sender.sendMessage("install <update, modules, module <name>");
        }
    }
}
