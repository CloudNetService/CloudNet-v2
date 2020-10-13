/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.bridge.internal.command.proxied;

import eu.cloudnetservice.cloudnet.v2.api.CloudAPI;
import eu.cloudnetservice.cloudnet.v2.bridge.CloudProxy;
import eu.cloudnetservice.cloudnet.v2.bridge.event.proxied.ProxiedPlayerFallbackEvent;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;

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

        ProxiedPlayerFallbackEvent proxiedPlayerFallbackEvent = new ProxiedPlayerFallbackEvent(
            (ProxiedPlayer) commandSender,
            CloudAPI.getInstance().getOnlinePlayer(((ProxiedPlayer) commandSender).getUniqueId()),
            ProxiedPlayerFallbackEvent.FallbackType.HUB_COMMAND,
            fallback);

        ProxyServer.getInstance().getPluginManager().callEvent(proxiedPlayerFallbackEvent);

        fallback = proxiedPlayerFallbackEvent.getFallback();

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
