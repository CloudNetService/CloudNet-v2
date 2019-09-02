/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.command.bukkit;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.NetworkUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.management.ManagementFactory;
import java.util.Arrays;

/**
 * Created by Tareko on 07.07.2017.
 */
public final class CommandResource extends Command {

    public CommandResource() {
        super("resource");
        setPermission("cloudnet.command.resource");
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        CloudAPI.getInstance().getLogger().finest(String.format("%s executed %s with arguments %s", sender, alias, Arrays.toString(args)));
        if (!testPermission(sender)) {
            return false;
        }
        long used = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L;
        long max = Runtime.getRuntime().maxMemory() / 1048576L;

        sender.sendMessage(CloudAPI.getInstance().getPrefix() + NetworkUtils.SPACE_STRING);
        sender.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Server: §b" + CloudAPI.getInstance()
                                                                                         .getServerId() + ':' + CloudAPI.getInstance()
                                                                                                                        .getUniqueId());
        sender.sendMessage(CloudAPI.getInstance().getPrefix() + "§7State§8: §b" + CloudServer.getInstance().getServerState());
        sender.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Template: §b" + CloudServer.getInstance().getTemplate().getName());
        sender.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Memory: §b" + used + "§7/§b" + max + "MB");
        sender.sendMessage(CloudAPI.getInstance().getPrefix() + "§7CPU-Usage internal: §b" + NetworkUtils.DECIMAL_FORMAT.format(NetworkUtils
                                                                                                                                    .internalCpuUsage()));
        sender.sendMessage(CloudAPI.getInstance().getPrefix() + NetworkUtils.SPACE_STRING);
        return false;
    }
}
