package de.dytanic.cloudnetcore.discordbot.Utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * DiscordBotConfig
 *
 * @author _Klaro | Pasqual K.
 * Created by _Klaro on 26.08.2018.
 * Copyright (c) _Klaro
 * Do not decompile this resource our say it´s yours.
 * Visit our Discord for more information: http://discord.deinsystem.net
 * Or our Teamspeak: deinsystem.net
 */

@Getter
@AllArgsConstructor
public class DiscordBotConfig implements Serializable
{
    String token, guild, channel, role, game, cmd_prefix;
}
