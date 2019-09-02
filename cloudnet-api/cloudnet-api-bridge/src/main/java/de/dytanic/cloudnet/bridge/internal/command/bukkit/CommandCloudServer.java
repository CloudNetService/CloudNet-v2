/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.command.bukkit;

import com.google.common.collect.ImmutableList;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.internal.serverselectors.MobSelector;
import de.dytanic.cloudnet.bridge.internal.serverselectors.SignSelector;
import de.dytanic.cloudnet.bridge.internal.serverselectors.packet.out.PacketOutAddMob;
import de.dytanic.cloudnet.bridge.internal.serverselectors.packet.out.PacketOutAddSign;
import de.dytanic.cloudnet.bridge.internal.serverselectors.packet.out.PacketOutRemoveMob;
import de.dytanic.cloudnet.bridge.internal.serverselectors.packet.out.PacketOutRemoveSign;
import de.dytanic.cloudnet.bridge.internal.util.ItemStackBuilder;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnet.lib.serverselectors.sign.Position;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnet.lib.utility.document.Document;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Tareko on 23.08.2017.
 */
public final class CommandCloudServer implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        CloudAPI.getInstance().getLogger().finest(String.format("%s executed %s (label = %s) with arguments %s",
                                                                commandSender,
                                                                command,
                                                                label,
                                                                Arrays.toString(args)));

        if (args.length > 5) {
            if (args[0].equalsIgnoreCase("createMob")) {
                return playerGuard(commandSender, player -> createMob(commandSender, args, player));
            }
        } else if (args.length > 2) {
            if (args[0].equalsIgnoreCase("editMobLine")) {
                return editMobLine(commandSender, args);
            }
            if (args[0].equalsIgnoreCase("setDisplay")) {
                return setDisplay(commandSender, args);
            }
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("removeSign")) {
                return playerGuard(commandSender, player -> removeSign(commandSender, player));

            } else if (args[0].equalsIgnoreCase("listMobs")) {
                return listMobs(commandSender);
            } else if (args[0].equalsIgnoreCase("moblist")) {
                return mobList(commandSender);
            } else if (args[0].equalsIgnoreCase("debug")) {
                return debug(commandSender);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("copyTo")) {
                return copyTo(commandSender, args[1]);
            } else if (args[0].equalsIgnoreCase("createSign")) {
                return playerGuard(commandSender, player -> createSign(commandSender, args, player));
            } else if (args[0].equalsIgnoreCase("removeSigns")) {
                return removeSigns(commandSender, args[1]);

            } else if (args[0].equalsIgnoreCase("removeMob")) {
                return removeMob(commandSender, args[1]);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setItem")) {
                return setItem(commandSender, args);
            }
        }

        help(commandSender);

        return false;
    }

    private static boolean playerGuard(CommandSender sender, Function<Player, Boolean> method) {
        if (sender instanceof Player) {
            return method.apply((Player) sender);
        } else {
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "This command can only be called by a player!");
            return false;
        }
    }

    private boolean createMob(CommandSender commandSender, String[] args, Player player) {
        if (checkMobSelectorActive(commandSender)) {
            return true;
        }

        try {
            EntityType entityType = EntityType.valueOf(args[1].toUpperCase());
            if (!entityType.isAlive() || !entityType.isSpawnable()) {
                return true;
            }
            if (MobSelector.getInstance().getMobs().values().stream().noneMatch(mob -> mob.getMob().getName().equalsIgnoreCase(args[2]))) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 6; i < args.length; i++) {
                    stringBuilder.append(args[i]).append(NetworkUtils.SPACE_STRING);
                }

                if (stringBuilder.length() > 32) {
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The display name cannot be longer then 32 characters");
                    return true;
                }

                int materialId = NetworkUtils.checkIsNumber(args[4]) ? Integer.parseInt(args[4]) : -1;
                Material material = ItemStackBuilder.getMaterialIgnoreVersion(args[4].toUpperCase(Locale.ENGLISH), materialId);

                if (material == null) {
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "An item with this itemName does not exist");
                    return true;
                }

                ServerMob serverMob = new ServerMob(UUID.randomUUID(),
                                                    stringBuilder.substring(0, stringBuilder.length() - 1),
                                                    args[2],
                                                    entityType.name(),
                                                    args[3],
                                                    -1,
                                                    material.name(),
                                                    args[5].equalsIgnoreCase("true"),
                                                    MobSelector.getInstance()
                                                               .toPosition(CloudAPI.getInstance().getGroup(), player.getLocation()),
                                                    "§8#§c%group% &bPlayers online §8|§7 %group_online% of %max_players%",
                                                    new Document());
                CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutAddMob(serverMob));
                player.sendMessage(CloudAPI.getInstance().getPrefix() + "The mob will be created, please wait...");

            } else {
                commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The mob with the name " + args[2] + " already exists!");
                return true;
            }
        } catch (Exception ex) {
            for (EntityType entityType : EntityType.values()) {
                commandSender.sendMessage("- " + entityType.name());
            }
        }
        return false;
    }

    private boolean editMobLine(CommandSender commandSender, String[] args) {
        if (checkMobSelectorActive(commandSender)) {
            return true;
        }

        MobSelector.MobImpl mob = findMobWithName(args[1]);
        if (mob != null) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 2; i < args.length; i++) {
                stringBuilder.append(args[i]).append(NetworkUtils.SPACE_STRING);
            }

            mob.getMob().setDisplayMessage(stringBuilder.substring(0, stringBuilder.length() - 1));
            CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutAddMob(mob.getMob()));
            commandSender.sendMessage(CloudAPI.getInstance()
                                              .getPrefix() + "You set the mobline for \"" + args[1] + "\" the line \"" + stringBuilder.substring(
                0,
                stringBuilder.length() - 1) + '"');
            return true;
        }
        return false;
    }

    private boolean setDisplay(CommandSender commandSender, String[] args) {
        MobSelector.MobImpl mob = findMobWithName(args[1]);
        if (mob != null) {
            StringBuilder stringBuilder = new StringBuilder();

            for (short i = 2; i < args.length; i++) {
                stringBuilder.append(args[i]).append(NetworkUtils.SPACE_STRING);
            }

            mob.getMob().setDisplay(stringBuilder.substring(0, stringBuilder.length() - 1));
            CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutAddMob(mob.getMob()));
            commandSender.sendMessage(CloudAPI.getInstance()
                                              .getPrefix() + "You set the display for \"" + args[1] + "\" the line \"" + stringBuilder.substring(
                0,
                stringBuilder.length() - 1) + '"');
            return true;
        }
        return false;
    }

    private boolean removeSign(CommandSender commandSender, Player player) {
        if (checkSignSelectorActive(commandSender)) {
            return true;
        }

        Block block = player.getTargetBlock((Set<Material>) null, 15);
        if (block.getState() instanceof org.bukkit.block.Sign) {
            if (SignSelector.getInstance().containsPosition(block.getLocation())) {
                Sign sign = SignSelector.getInstance().getSignByPosition(block.getLocation());

                if (sign != null) {
                    CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutRemoveSign(sign));
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The sign has been removed");
                }
            }
        }
        return false;
    }

    private boolean listMobs(CommandSender commandSender) {
        if (checkMobSelectorActive(commandSender)) {
            return true;
        }

        MobSelector.getInstance().getMobs().forEach((uuid, mob) -> commandSender.sendMessage("- " + mob.getMob().getName()));
        return false;
    }

    private boolean mobList(CommandSender commandSender) {
        if (checkMobSelectorActive(commandSender)) {
            return true;
        }

        for (EntityType entityType : EntityType.values()) {
            if (entityType.isAlive() && entityType.isSpawnable()) {
                commandSender.sendMessage("- " + entityType.name());
            }
        }
        return false;
    }

    private boolean debug(CommandSender commandSender) {
        CloudAPI.getInstance().setDebug(!CloudAPI.getInstance().isDebug());

        final LoggerContext context = (LoggerContext) LogManager.getContext(false);
        final Configuration configuration = context.getConfiguration();
        final LoggerConfig rootLoggerConfig = configuration.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);

        if (CloudAPI.getInstance().isDebug()) {
            rootLoggerConfig.setLevel(Level.ALL);
            commandSender.sendMessage("§aDebug output for server has been enabled.");
        } else {
            rootLoggerConfig.setLevel(Level.INFO);
            commandSender.sendMessage("§cDebug output for server has been disabled.");
        }
        context.updateLoggers(configuration);
        return false;
    }

    private boolean copyTo(CommandSender commandSender, String arg) {
        if (checkSignSelectorActive(commandSender)) {
            return false;
        }

        if (CloudAPI.getInstance().getServerGroupMap().containsKey(arg)) {
            for (Sign sign : SignSelector.getInstance().getSigns().values()) {
                CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutAddSign(new Sign(sign.getTargetGroup(),
                                                                                                       new Position(arg,
                                                                                                                    sign.getPosition()
                                                                                                                        .getWorld(),
                                                                                                                    sign.getPosition()
                                                                                                                        .getX(),
                                                                                                                    sign.getPosition()
                                                                                                                        .getY(),
                                                                                                                    sign.getPosition()
                                                                                                                        .getZ()))));
            }

            commandSender.sendMessage(CloudAPI.getInstance()
                                              .getPrefix() + "The signs by this group was successfully copied to the target group.");
        }
        return true;
    }

    private boolean createSign(CommandSender commandSender, String[] args, Player player) {
        if (checkSignSelectorActive(commandSender)) {
            return false;
        }

        Block block = player.getTargetBlock((Set<Material>) null, 15);
        if (block.getState() instanceof org.bukkit.block.Sign) {
            if (!SignSelector.getInstance().containsPosition(block.getLocation())) {
                if (CloudAPI.getInstance().getServerGroupMap().containsKey(args[1])) {
                    Sign sign = new Sign(args[1], SignSelector.getInstance().toPosition(block.getLocation()));
                    CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutAddSign(sign));
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The sign was successfully created!");
                } else {
                    commandSender.sendMessage("The group doesn't exist");
                }
            } else {
                commandSender.sendMessage("The sign already exists!");
            }
        }
        return false;
    }

    private boolean removeSigns(CommandSender commandSender, String arg) {
        if (checkSignSelectorActive(commandSender)) {
            return true;
        }

        for (Sign sign : SignSelector.getInstance().getSigns().values()) {
            if (sign.getTargetGroup() != null && sign.getTargetGroup().equalsIgnoreCase(arg)) {
                CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutRemoveSign(sign));
            }
        }

        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "§7You deleted all signs from the group \"§6" + arg + "§7\"");
        return false;
    }

    private boolean removeMob(CommandSender commandSender, String arg) {
        if (checkMobSelectorActive(commandSender)) {
            return true;
        }

        MobSelector.MobImpl mob = findMobWithName(arg);
        if (mob != null) {
            CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutRemoveMob(mob.getMob()));
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The mob has been removed");
        } else {
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The Mob doesn't exist on this group");
        }
        return false;
    }

    private boolean setItem(CommandSender commandSender, String[] args) {
        if (checkMobSelectorActive(commandSender)) {
            return true;
        }

        MobSelector.MobImpl mob = findMobWithName(args[1]);
        if (mob != null) {
            int itemId = NetworkUtils.checkIsNumber(args[2]) ? Integer.parseInt(args[2]) : 138;
            Material material = ItemStackBuilder.getMaterialIgnoreVersion(args[2], itemId);
            if (material != null) {
                mob.getMob().setItemName(material.name());
                CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutAddMob(mob.getMob()));
                commandSender.sendMessage(CloudAPI.getInstance()
                                                  .getPrefix() + "You set the item for \"" + args[1] + "\" the material \"" + material.name() + '"');
            }
            return true;
        }
        return false;
    }

    private void help(CommandSender commandSender) {
        if (SignSelector.getInstance() != null) {
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cs createSign <targetGroup>");
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cs removeSign");
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cs removeSigns <targetGroup>");
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cs copyTo <targetGroup>");
        }
        if (MobSelector.getInstance() != null) {
            commandSender.sendMessage(CloudAPI.getInstance()
                                              .getPrefix() + "/cs createMob <entityType> <name> <targetGroup> <itemName> <autoJoin> <displayName>");
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cs removeMob <name>");
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cs listMobs");
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cs moblist");
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cs setDisplay <name> <display>");
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cs setItem <name> <itemId>");
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cs editMobLine <name> <display>");
        }
        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cs debug");
    }

    private static boolean checkMobSelectorActive(CommandSender commandSender) {
        if (MobSelector.getInstance() == null) {
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The Module \"MobSelector\" isn't enabled!");
            return true;
        } else {
            return false;
        }
    }

    private static MobSelector.MobImpl findMobWithName(String arg) {
        return MobSelector.getInstance()
                          .getMobs()
                          .values()
                          .stream()
                          .filter(value -> value.getMob().getName().equalsIgnoreCase(arg))
                          .findFirst()
                          .orElse(null);
    }

    private static boolean checkSignSelectorActive(CommandSender commandSender) {
        if (SignSelector.getInstance() == null) {
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The Module \"SignSelector\" isn't enabled!");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        switch (args.length) {
            case 1: {
                return ImmutableList.of("createSign",
                                        "removeSign",
                                        "removeSigns",
                                        "copyTo",
                                        "createMob",
                                        "removeMob",
                                        "listsMobs",
                                        "moblist",
                                        "setDisplay",
                                        "setItem",
                                        "editMobLine",
                                        "debug");
            }
            case 2: {
                if (args[0].equalsIgnoreCase("createsign") || args[0].equalsIgnoreCase("removesigns") || args[0].equalsIgnoreCase("copyto")) {
                    return ImmutableList.copyOf(CloudAPI.getInstance().getServerGroupMap().keySet());
                } else if (args[0].equalsIgnoreCase("removeMob") || args[0].equalsIgnoreCase("setDisplay") || args[0].equalsIgnoreCase(
                    "setItem") || args[0].equalsIgnoreCase("editMobLine")) {
                    return ImmutableList.copyOf(MobSelector.getInstance()
                                                           .getMobs()
                                                           .values()
                                                           .stream()
                                                           .map(mob -> mob.getMob().getName())
                                                           .collect(Collectors.toList()));
                } else if (args[0].equalsIgnoreCase("createMob")) {
                    return ImmutableList.copyOf(Arrays.stream(EntityType.values()).map(Enum::name).collect(Collectors.toList()));
                }
            }
            case 4: {
                if (args[0].equalsIgnoreCase("createMob")) {
                    return ImmutableList.copyOf(CloudAPI.getInstance().getServerGroupMap().keySet());
                }
            }
            case 5: {
                if (args[0].equalsIgnoreCase("createMob")) {
                    return ImmutableList.copyOf(Arrays.stream(Material.values()).map(Enum::name).collect(Collectors.toList()));
                }
            }
            case 6: {
                if (args[0].equalsIgnoreCase("createMob")) {
                    return ImmutableList.of("true", "false");
                }
            }
        }
        return ImmutableList.of();
    }
}
