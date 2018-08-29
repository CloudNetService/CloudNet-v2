package de.dytanic.cloudnetcore.discordbot.utils;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnetcore.discordbot.DiscordBot;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigUtil
{
    private final Path path = Paths.get("local/discordbot_config.yml");
    private Configuration configuration;

    public ConfigUtil() throws IOException
    {
        if (!Files.exists(path))
        {
            Files.createFile(path);

            Configuration config = new Configuration();

            this.write(config, new DiscordBotConfig(
                    "NDMxMzUwODMxMDAzMDc0NTYy.DmQEcA.DDiNG5JhHMa99mFbkYuVAmefYJE",
                    "340197684688453632",
                    "358855659371888640",
                    "483644392972746753",
                    "CloudNet " + NetworkUtils.class.getPackage().getImplementationVersion() + " by Dytanic",
                    "cnc!"
            ));

            try (OutputStream outputStream = Files.newOutputStream(this.path); OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))
            {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, outputStreamWriter);
            }
        } else
        {
            this.load();
        }
    }

    private void load()
    {
        try (InputStream inputStream = Files.newInputStream(this.path); InputStreamReader inputStreamReader =
                new InputStreamReader(inputStream, "UTF-8"))
        {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(inputStreamReader);
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }

        this.read();
    }

    private void read()
    {
        Configuration section = this.configuration.getSection("general");

        if (section.getString("Cmd_prefix").equals("") || section.getString("Cmd_prefix").equals(" "))
        {
            this.editConfig(this.configuration);
        }

        DiscordBotConfig discordBotConfig = new DiscordBotConfig(
                section.getString("Token"),
                section.getString("Guild"),
                section.getString("Channel"),
                section.getString("User_role"),
                section.getString("Game"),
                section.getString("Cmd_prefix")
        );

        DiscordBot.setDiscordBotConfig(discordBotConfig);
    }

    private void write(Configuration configuration, DiscordBotConfig discordBotConfig)
    {
        Configuration section = configuration.getSection("general");

        section.set("Token", "'" + discordBotConfig.getToken() + "'");
        section.set("Guild", discordBotConfig.getGuild());
        section.set("Channel", discordBotConfig.getChannel());
        section.set("User_role", discordBotConfig.getRole());
        section.set("Game", "'" + discordBotConfig.getGame() + "'");
        section.set("Cmd_prefix", "'" + discordBotConfig.getCmd_prefix() + "'");

        DiscordBot.setDiscordBotConfig(discordBotConfig);
    }

    private void editConfig(Configuration configuration)
    {
        Configuration section = configuration.getSection("general");
        section.set("Cmd_prefix", "cnc!");

        try (OutputStream outputStream = Files.newOutputStream(this.path); OutputStreamWriter outputStreamWriter =
                new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))
        {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, outputStreamWriter);
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
