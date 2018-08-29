package de.dytanic.cloudnetcore.discordbot.console;

import de.dytanic.cloudnet.command.CommandManager;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.discordbot.DiscordBot;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ConsoleInput extends ListenerAdapter
{
    private final String cmd_prefix = DiscordBot.getDiscordBotConfig().getCmd_prefix();

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        CommandManager commandManager = CloudNet.getInstance().getCommandManager();
        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        if (event.getAuthor().isBot()) return;
        if (!event.getChannel().getId().equals(DiscordBot.getDiscordBotConfig().getChannel())) return;
        if (!event.getMember().getRoles().contains(DiscordBot.getRole()) && !event.getMember().isOwner() &&
                !event.getMember().hasPermission(Permission.ADMINISTRATOR) && !event.getMember().hasPermission(Permission.MANAGE_SERVER)) return;

        if (event.getMessage().getContentDisplay().startsWith(cmd_prefix))
        {
            String command = event.getMessage().getContentDisplay().replace(cmd_prefix, "");
            switch (command.toLowerCase())
            {
                case "clearqueue":
                    ConsoleOutput.getMessageQueue().clear();
                    event.getChannel().sendMessage("The queue has been cleared.").queue();
                    return;
                case "help":
                    event.getChannel().sendMessage("You can use the command \"" + cmd_prefix + "help\" and \"" + cmd_prefix + "clearqueue\"").queue();
                    return;
                default: event.getChannel().sendMessage("This command isn't supported, yet. You can use the command \"" + cmd_prefix
                     + "help\" to see a list of all commands.").queue();
                break;
            }
            return;
        }

        if (event.getMessage().getContentDisplay().toLowerCase().startsWith("create") || event.getMessage().getContentDisplay().toLowerCase().startsWith("start"))
        {
            CloudNet.getInstance().getCommandManager().dispatchCommand(event.getMessage().getContentDisplay().replace("create", "create0")
                    .replace("start", "create0"));
            return;
        }

        if (event.getMessage().getContentDisplay().toLowerCase().contains("stop"))
        {
            event.getChannel().sendMessage(
                    "CloudNetV2 will be stopped...\n\n" +
                    "╔════╗╔╗─╔╗╔═══╗╔═╗─╔╗╔╗╔═╗╔═══╗\n" +
                    "║╔╗╔╗║║║─║║║╔═╗║║║╚╗║║║║║╔╝║╔═╗║\n" +
                    "╚╝║║╚╝║╚═╝║║║─║║║╔╗╚╝║║╚╝╝─║╚══╗\n" +
                    "──║║──║╔═╗║║╚═╝║║║╚╗║║║╔╗║─╚══╗║\n" +
                    "──║║──║║─║║║╔═╗║║║─║║║║║║╚╗║╚═╝║\n" +
                    "──╚╝──╚╝─╚╝╚╝─╚╝╚╝─╚═╝╚╝╚═╝╚═══╝\n\n" +
                    "Thanks for using DiscordBot V1 by _Klaro.").queue();

            NetworkUtils.sleepUninterruptedly(100L);

            CloudNet.getInstance().shutdown();
            return;
        }

        if (!commandManager.dispatchCommand(event.getMessage().getContentDisplay()))
            event.getChannel().sendMessage("[" + dateFormat.format(System.currentTimeMillis()) + "] Command not found. Use the command \"help\" for further information!").queue();
    }
}
