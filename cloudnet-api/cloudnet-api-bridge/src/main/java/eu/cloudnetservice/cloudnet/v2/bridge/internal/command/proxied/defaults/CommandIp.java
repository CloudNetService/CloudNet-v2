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

package eu.cloudnetservice.cloudnet.v2.bridge.internal.command.proxied.defaults;

import eu.cloudnetservice.cloudnet.v2.api.CloudAPI;
import eu.cloudnetservice.cloudnet.v2.lib.player.OfflinePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class CommandIp extends Command {

    public CommandIp() {
        super("ip", "bungeecord.command.ip");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length < 1) {
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Please follow this command by a user name"));
            commandSender.sendMessage(TextComponent.fromLegacyText("Usage: /ip <player name>"));
            return;
        }

        OfflinePlayer user = CloudAPI.getInstance().getOfflinePlayer(args[0]);

        if (user == null) {
            commandSender.sendMessage(new TextComponent(TextComponent.fromLegacyText(ChatColor.RED + "That user is not registered!")));
        } else {
            commandSender.sendMessage(TextComponent.fromLegacyText(String.format("%sIP of %s is %s",
                                                                                 ChatColor.BLUE,
                                                                                 args[0],
                                                                                 user.getLastPlayerConnection().getHost())));
        }

    }
}
