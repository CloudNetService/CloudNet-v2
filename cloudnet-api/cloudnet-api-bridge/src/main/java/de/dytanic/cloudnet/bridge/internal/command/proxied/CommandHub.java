package de.dytanic.cloudnet.bridge.internal.command.proxied;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudProxy;
import de.dytanic.cloudnet.bridge.event.proxied.ProxiedPlayerFallbackEvent;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

/**
 * Created by Tareko on 20.08.2017.
 */
public final class CommandHub extends Command {

    public CommandHub() {
        super("hub", null, "lobby", "l", "leave", "game");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        CloudAPI.getInstance().getLogger().finest(String.format("%s executed %s with arguments %s%n",
                                                                commandSender,
                                                                this,
                                                                Arrays.toString(args)));
        if (!(commandSender instanceof ProxiedPlayer)) {
            return;
        }

        ServerInfo serverInfo = CloudProxy.getInstance().getServers().get(
            ((ProxiedPlayer) commandSender).getServer().getInfo().getName());

        if (serverInfo != null) {
            if (CloudProxy.getInstance()
                          .getProxyGroup()
                          .getProxyConfig()
                          .getDynamicFallback()
                          .getNamedFallbacks()
                          .contains(serverInfo.getServiceId().getGroup())) {
                commandSender.sendMessage(
                    TextComponent.fromLegacyText(
                        ChatColor.translateAlternateColorCodes(
                            '&', CloudAPI.getInstance().getCloudNetwork().getMessages().getString("hub-already"))));
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
            commandSender.sendMessage(TextComponent.fromLegacyText(
                ChatColor.translateAlternateColorCodes('&',
                                                       CloudAPI.getInstance()
                                                               .getCloudNetwork()
                                                               .getMessages()
                                                               .getString("hubCommandNoServerFound"))));
        } else {
            ((ProxiedPlayer) commandSender).connect(ProxyServer.getInstance().getServerInfo(fallback));
        }
    }
}
