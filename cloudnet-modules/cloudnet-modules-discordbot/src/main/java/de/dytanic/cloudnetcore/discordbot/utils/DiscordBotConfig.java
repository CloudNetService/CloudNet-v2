package de.dytanic.cloudnetcore.discordbot.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class DiscordBotConfig implements Serializable
{
    private String token, guild, channel, role, game, cmd_prefix;
}
