package eu.cloudnetservice.v2.bridge.internal.command.proxied.defaults;

import eu.cloudnetservice.v2.api.CloudAPI;
import eu.cloudnetservice.v2.lib.player.OfflinePlayer;
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
