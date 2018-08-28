package de.dytanic.cloudnetcore.discordbot;

import de.dytanic.cloudnetcore.api.CoreModule;
import de.dytanic.cloudnetcore.discordbot.Utils.ConfigUtil;
import de.dytanic.cloudnetcore.discordbot.Utils.DiscordBotConfig;
import de.dytanic.cloudnetcore.discordbot.console.ConsoleInput;
import de.dytanic.cloudnetcore.discordbot.console.ConsoleOutput;
import de.dytanic.cloudnetcore.discordbot.override.CommandCreate0;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class DiscordBot extends CoreModule
{
    @Getter
    private static DiscordBot discordBot;
    @Setter @Getter
    private static DiscordBotConfig discordBotConfig;
    @Getter
    private static JDA jda;
    @Getter
    private static Role role;
    @Getter
    private static TextChannel textChannel;
    private static Guild guild;
    private boolean connected = false;

    @Override
    public void onLoad() { discordBot = this; }

    @Override
    public void onBootstrap()
    {
        try
        {
            new ConfigUtil();
            jda = new JDABuilder(AccountType.BOT)
                    .setAudioEnabled(false)
                    .setAutoReconnect(true)
                    .setToken(discordBotConfig.getToken())
                    .setGame(Game.playing(discordBotConfig.getGame()))
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .addEventListener(new ListenerAdapter()
                    {
                        @Override
                        public void onReady(ReadyEvent event)
                        {
                            event.getJDA().addEventListener(new ConsoleInput());
                            guild = event.getJDA().getGuildById(discordBotConfig.getGuild());
                            textChannel = guild.getTextChannelById(discordBotConfig.getChannel());
                            role = guild.getRoleById(discordBotConfig.getRole());
                            if (!isValid(guild, textChannel, role))
                            {
                                System.out.println("The bot couldn't find the guild, channel or user role! Please check the configuration!");
                                return;
                            }
                            ConsoleOutput.start(textChannel);
                            connected = true;
                            registerCommand(new CommandCreate0());
                            event.getJDA().removeEventListener(this);
                        }
                    }).buildAsync();
        } catch (LoginException | IOException ex)
        {
            System.out.println("The bot couldn't connect to the Discord Server! Please check the configuration!");
        }
    }

    @Override
    public void onShutdown()
    {
        if (!ConsoleOutput.getMessageQueue().isEmpty()) ConsoleOutput.getMessageQueue().clear();
        if (!ConsoleOutput.getThread().isInterrupted()) ConsoleOutput.getThread().stop();
        jda.removeEventListener(new ConsoleInput());
        if (connected) jda.shutdownNow();
        if (!ConsoleOutput.getThread().isInterrupted()) ConsoleOutput.getThread().stop();
    }

    private boolean isValid(Guild guild, TextChannel textChannel, Role role)
    {
        if (textChannel == null || guild == null)
            return false;
        return true;
    }
}
