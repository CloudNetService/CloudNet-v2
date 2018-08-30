package de.dytanic.cloudnetcore.discordbot.console;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.WrapperMeta;
import lombok.Getter;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

public class ConsoleOutput
{
    @Getter
    private static Deque<String> messageQueue = new LinkedBlockingDeque<>();
    @Getter
    private static Thread thread;
    private static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static void start(TextChannel textChannel)
    {
        sendStart(textChannel);
        (thread = new Thread(() -> {
            while (true)
            {
                try
                {
                    Thread.sleep(750L);
                } catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                }

                try
                {
                    StringBuilder stringBuilder = new StringBuilder();

                    while (!messageQueue.isEmpty())
                    {
                        String input = messageQueue.poll();
                        input = input.replace("`", "'");
                        if (!input.startsWith("["))
                        {
                            input = "[" + dateFormat.format(System.currentTimeMillis()) + "] " + input;
                        }

                        if (stringBuilder.length() + input.length() > 1995)
                        {
                            messageQueue.offerFirst(input);
                            break;
                        }

                        stringBuilder.append(input).append("\n");
                    }

                    if (stringBuilder.length() != 0 && !stringBuilder.substring(0).isEmpty() && !stringBuilder.substring(0).equals(" "))
                    {
                        textChannel.sendMessage(stringBuilder.substring(0)).queue();
                    }
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        })).start();

        CloudNet.getLogger().getHandler().add((input) -> {
            if (isValid(input))
            {
                messageQueue.offer(input);
            }
        });
    }

    private static boolean isValid(String input)
    {
        return !input.isEmpty() && !input.equals(" ");
    }

    private static void sendStart(TextChannel textChannel)
    {
        textChannel.sendMessage(
                "╔═══╗╔╗───╔═══╗╔╗─╔╗╔═══╗╔═╗─╔╗╔═══╗╔════╗ [" + NetworkUtils.class.getPackage().getImplementationVersion() + "]\n" +
                "║╔═╗║║║───║╔═╗║║║─║║╚╗╔╗║║║╚╗║║║╔══╝║╔╗╔╗║\n" +
                "║║─╚╝║║───║║─║║║║─║║─║║║║║╔╗╚╝║║╚══╗╚╝║║╚╝\n" +
                "║║─╔╗║║─╔╗║║─║║║║─║║─║║║║║║╚╗║║║╔══╝──║║──\n" +
                "║╚═╝║║╚═╝║║╚═╝║║╚═╝║╔╝╚╝║║║─║║║║╚══╗──║║──\n" +
                "╚═══╝╚═══╝╚═══╝╚═══╝╚═══╝╚╝─╚═╝╚═══╝──╚╝──" +
                "\n" +
                "«» The Cloud Network Environment Technology\n" +
                "«» Support https://discord.gg/5NUhKuR      [" + NetworkUtils.class.getPackage().getSpecificationVersion() + "]\n" +
                "«» Java " + System.getProperty("java.version") + " @" + System.getProperty("user.name") + NetworkUtils.SPACE_STRING + System.getProperty("os.name") + NetworkUtils.SPACE_STRING + "\n" +
                "«» Discord Bot by _Klaro was successfully started!\n").queue();

        textChannel.sendMessage("Loading Modules...").queue();
        for (WrapperMeta wrapperMeta : CloudNet.getInstance().getConfig().getWrappers())
        {
            textChannel.sendMessage("Loading Wrapper " + wrapperMeta.getId() + " @ " + wrapperMeta.getHostName()).queue();
        }
    }
}