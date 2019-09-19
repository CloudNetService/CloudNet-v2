/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.command.proxied;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudProxy;
import de.dytanic.cloudnet.bridge.event.proxied.ProxiedPlayerFallbackEvent;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

/**
 * Created by Tareko on 20.08.2017.
 */
public final class CommandHub extends Command {

    public CommandHub() {
        super("hub");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        CloudAPI.getInstance().getLogger().finest(String.format("%s executed %s with arguments %s",
                                                                commandSender,
                                                                this,
                                                                Arrays.toString(args)));
        if (!(commandSender instanceof ProxiedPlayer)) {
            return;
        }

        ServerInfo serverInfo = CloudProxy.getInstance().getCachedServers().get(((ProxiedPlayer) commandSender).getServer()
                                                                                                               .getInfo()
                                                                                                               .getName());

        if (serverInfo != null) {
            if (CloudProxy.getInstance()
                          .getProxyGroup()
                          .getProxyConfig()
                          .getDynamicFallback()
                          .getNamedFallbackes()
                          .contains(serverInfo.getServiceId().getGroup())) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                                                 CloudAPI.getInstance()
                                                                                         .getCloudNetwork()
                                                                                         .getMessages()
                                                                                         .getString("hub-already")));
                return;
            }
        }

        String fallback = CloudProxy.getInstance().fallbackOnEnabledKick((((ProxiedPlayer) commandSender)),
                                                                         CloudAPI.getInstance().getGroup(),
                                                                         ((ProxiedPlayer) commandSender).getServer().getInfo().getName());

        ProxiedPlayerFallbackEvent proxiedPlayerFallbackEvent = new ProxiedPlayerFallbackEvent((ProxiedPlayer) commandSender,
                                                                                               CloudAPI.getInstance()
                                                                                                       .getOnlinePlayer(((ProxiedPlayer) commandSender)
                                                                                                                            .getUniqueId()),
                                                                                               ProxiedPlayerFallbackEvent.FallbackType.HUB_COMMAND,
                                                                                               fallback);

        ProxyServer.getInstance().getPluginManager().callEvent(proxiedPlayerFallbackEvent);

        fallback = proxiedPlayerFallbackEvent.getFallback();

        ProxyServer.getInstance().getPluginManager().callEvent(proxiedPlayerFallbackEvent);

        if (fallback == null) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                                             CloudAPI.getInstance()
                                                                                     .getCloudNetwork()
                                                                                     .getMessages()
                                                                                     .getString("hubCommandNoServerFound")));
        } else {
            ((ProxiedPlayer) commandSender).connect(ProxyServer.getInstance().getServerInfo(fallback));
        }
    }

    @Override
    public String[] getAliases() {
        return new String[] {"lobby", "leave", "game", "l"};
    }
}
