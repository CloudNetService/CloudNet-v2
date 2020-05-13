package de.dytanic.cloudnet.bridge.internal.command.proxied.defaults;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudProxy;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.awt.TextComponent;
import java.util.Map;

public class CommandServer extends Command {
    public CommandServer() {
        super("server", "bungeecord.command.server");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final Map<String, ServerInfo> servers = CloudProxy.getInstance().getServers();
        if ( args.length == 0 )
        {
            if ( sender instanceof ProxiedPlayer )
            {
                ComponentBuilder builder = new ComponentBuilder()
                    .appendLegacy(CloudAPI.getInstance().getPrefix())
                    .appendLegacy("&eCurrent Server: &r " + ( (ProxiedPlayer) sender ).getServer().getInfo().getName());
                sender.sendMessage(ProxyServer.getInstance().getTranslation("current_server", ( (ProxiedPlayer) sender ).getServer().getInfo().getName() ) );
            }
        }
    }
}
