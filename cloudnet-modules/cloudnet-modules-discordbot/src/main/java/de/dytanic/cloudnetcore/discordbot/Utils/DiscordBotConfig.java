package de.dytanic.cloudnetcore.discordbot.Utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class DiscordBotConfig implements Serializable
{
    String token, guild, channel, role, game, cmd_prefix;
}
