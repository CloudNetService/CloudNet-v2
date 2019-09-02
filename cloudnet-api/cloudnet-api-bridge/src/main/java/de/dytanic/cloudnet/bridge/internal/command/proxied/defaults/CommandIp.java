package de.dytanic.cloudnet.bridge.internal.command.proxied.defaults;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Tareko on 19.01.2018.
 */
public class CommandIp extends Command {

    public CommandIp() {
        super("ip", "bungeecord.command.ip");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length < 1) {
            commandSender.sendMessage(new TextComponent(TextComponent.fromLegacyText(ChatColor.RED + "Please follow this command by a user name")));
            return;
        }

        OfflinePlayer user = CloudAPI.getInstance().getOfflinePlayer(args[0]);

        if (user == null) {
            commandSender.sendMessage(new TextComponent(TextComponent.fromLegacyText(ChatColor.RED + "That user is not registered!")));
        } else {
            commandSender.sendMessage(TextComponent.fromLegacyText(ChatColor.BLUE + "IP of " + args[0] + " is " + user.getLastPlayerConnection()
                                                                                                                      .getHost()));
        }

    }
}
