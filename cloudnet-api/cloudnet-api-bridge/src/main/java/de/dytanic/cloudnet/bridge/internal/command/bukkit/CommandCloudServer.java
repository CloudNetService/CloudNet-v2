/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.command.bukkit;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.internal.serverselectors.MobSelector;
import de.dytanic.cloudnet.bridge.internal.serverselectors.SignSelector;
import de.dytanic.cloudnet.bridge.internal.serverselectors.packet.out.PacketOutAddMob;
import de.dytanic.cloudnet.bridge.internal.serverselectors.packet.out.PacketOutAddSign;
import de.dytanic.cloudnet.bridge.internal.serverselectors.packet.out.PacketOutRemoveMob;
import de.dytanic.cloudnet.bridge.internal.serverselectors.packet.out.PacketOutRemoveSign;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnet.lib.serverselectors.sign.Sign;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by Tareko on 23.08.2017.
 */
public final class CommandCloudServer implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        if (!(commandSender instanceof Player)) return false;

        Player player = (Player) commandSender;


        if (args.length > 5)
        {
            if (args[0].equalsIgnoreCase("createMob"))
            {
                if (MobSelector.getInstance() == null)
                {
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The Module \"MobSelector\" isn't enabled!");
                    return false;
                }

                try
                {
                    EntityType entityType = EntityType.valueOf(args[1].toUpperCase());
                    if (!entityType.isAlive() || !entityType.isSpawnable()) return false;
                    if (CollectionWrapper.filter(MobSelector.getInstance().getMobs().values(), new Acceptable<MobSelector.MobImpl>() {
                        @Override
                        public boolean isAccepted(MobSelector.MobImpl value)
                        {
                            return value.getMob().getName().equalsIgnoreCase(args[2]);
                        }
                    }) == null)
                    {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (short i = 6; i < args.length; i++)
                        {
                            stringBuilder.append(args[i]).append(NetworkUtils.SPACE_STRING);
                        }
                        if (stringBuilder.length() > 32)
                        {
                            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The display cannot be longe then 32 characters");
                            return false;
                        }
                        ServerMob serverMob = new ServerMob(UUID.randomUUID(), stringBuilder.substring(0, stringBuilder.length() - 1), args[2], entityType.name(), args[3],
                                NetworkUtils.checkIsNumber(args[4]) ? (Integer.parseInt(args[4]) != 0 ? Integer.parseInt(args[4]) : 138) : 138
                                , args[5].equalsIgnoreCase("true"),
                                MobSelector.getInstance().toPosition(CloudAPI.getInstance().getGroup(), player.getLocation()), "§8#§c%group% &bPlayers online §8|§7 %group_online% of %max_players%", new Document());
                        CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutAddMob(serverMob));
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "The mob will be created, please wait...");

                    } else
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The mob with the name " + args[2] + " already exists!");
                        return false;
                    }
                } catch (Exception ex)
                {
                    for (EntityType entityType : EntityType.values()) commandSender.sendMessage("- " + entityType.name());
                }
            }
        }

        if (args.length > 2)
        {
            if (args[0].equalsIgnoreCase("editMobLine"))
            {
                if (MobSelector.getInstance() == null)
                {
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The Module \"MobSelector\" isn't enabled!");
                    return false;
                }

                MobSelector.MobImpl mob = CollectionWrapper.filter(MobSelector.getInstance().getMobs().values(), new Acceptable<MobSelector.MobImpl>() {
                    @Override
                    public boolean isAccepted(MobSelector.MobImpl value)
                    {
                        return value.getMob().getName().equalsIgnoreCase(args[1]);
                    }
                });
                if (mob != null)
                {
                    StringBuilder stringBuilder = new StringBuilder();

                    for (short i = 2; i < args.length; i++)
                        stringBuilder.append(args[i]).append(NetworkUtils.SPACE_STRING);

                    mob.getMob().setDisplayMessage(stringBuilder.substring(0, stringBuilder.length() - 1));
                    CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutAddMob(mob.getMob()));
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "You set the mobline for \"" + args[1] + "\" the line \"" + stringBuilder.substring(0, stringBuilder.length() - 1) + "\"");
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("setDisplay"))
            {
                MobSelector.MobImpl mob = CollectionWrapper.filter(MobSelector.getInstance().getMobs().values(), new Acceptable<MobSelector.MobImpl>() {
                    @Override
                    public boolean isAccepted(MobSelector.MobImpl value)
                    {
                        return value.getMob().getName().equalsIgnoreCase(args[1]);
                    }
                });
                if (mob != null)
                {
                    StringBuilder stringBuilder = new StringBuilder();

                    for (short i = 2; i < args.length; i++)
                        stringBuilder.append(args[i]).append(NetworkUtils.SPACE_STRING);

                    mob.getMob().setDisplay(stringBuilder.substring(0, stringBuilder.length() - 1));
                    CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutAddMob(mob.getMob()));
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "You set the display for \"" + args[1] + "\" the line \"" + stringBuilder.substring(0, stringBuilder.length() - 1) + "\"");
                    return false;
                }
            }
        }

        switch (args.length)
        {
            case 2:
                if (args[0].equalsIgnoreCase("createSign"))
                {
                    if (SignSelector.getInstance() == null)
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The Module \"SignSelector\" isn't enabled!");
                        return false;
                    }

                    Block block = player.getTargetBlock((Set<Material>) null, 15);
                    if (block.getState() instanceof org.bukkit.block.Sign)
                        if (!SignSelector.getInstance().containsPosition(block.getLocation()))
                        {
                            if (CloudAPI.getInstance().getServerGroupMap().containsKey(args[1]))
                            {
                                Sign sign = new Sign(args[1], SignSelector.getInstance().toPosition(block.getLocation()));
                                CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutAddSign(sign));
                                commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The sign was successfully created!");

                            } else commandSender.sendMessage("The group doesn't exist");
                        } else commandSender.sendMessage("The sign already exists!");
                    return false;
                }
                if (args[0].equalsIgnoreCase("removeSigns"))
                {
                    if (SignSelector.getInstance() == null)
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The Module \"SignSelector\" isn't enabled!");
                        return false;
                    }

                    for (Sign sign : SignSelector.getInstance().getSigns().values())
                        if (sign.getTargetGroup() != null && sign.getTargetGroup().equalsIgnoreCase(args[1]))
                            CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutRemoveSign(sign));

                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "§7You deleted all signs from the group \"§6" + args[1] + "§7\"");

                }
                if (args[0].equalsIgnoreCase("removeMob"))
                {
                    if (MobSelector.getInstance() == null)
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The Module \"MobSelector\" isn't enabled!");
                        return false;
                    }

                    MobSelector.MobImpl mob = CollectionWrapper.filter(MobSelector.getInstance().getMobs().values(), new Acceptable<MobSelector.MobImpl>() {
                        @Override
                        public boolean isAccepted(MobSelector.MobImpl value)
                        {
                            return value.getMob().getName().equalsIgnoreCase(args[1]);
                        }
                    });
                    if (mob != null)
                    {
                        CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutRemoveMob(mob.getMob()));
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "The mob has been removed");

                    } else
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "The Mob doesn't exist on this group");
                }
                break;
            case 1:
                if (args[0].equalsIgnoreCase("removeSign"))
                {
                    if (SignSelector.getInstance() == null)
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The Module \"SignSelector\" isn't enabled!");
                        return false;
                    }

                    Block block = player.getTargetBlock((Set<Material>) null, 15);
                    if (block.getState() instanceof org.bukkit.block.Sign)
                    {
                        if (SignSelector.getInstance().containsPosition(block.getLocation()))
                        {
                            Sign sign = SignSelector.getInstance().getSignByPosition(block.getLocation());

                            if (sign != null)
                            {
                                CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutRemoveSign(sign));
                                commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The sign has been removed");
                            }
                        }
                    }

                }
                if (args[0].equalsIgnoreCase("listMobs"))
                {
                    if (MobSelector.getInstance() == null)
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The Module \"MobSelector\" isn't enabled!");
                        return false;
                    }

                    CollectionWrapper.iterator(MobSelector.getInstance().getMobs().values(), new Consumer<MobSelector.MobImpl>() {
                        @Override
                        public void accept(MobSelector.MobImpl obj)
                        {
                            commandSender.sendMessage("- " + obj.getMob().getName());
                        }
                    });
                }
                if (args[0].equalsIgnoreCase("moblist"))
                {
                    if (MobSelector.getInstance() == null)
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The Module \"MobSelector\" isn't enabled!");
                        return false;
                    }

                    for (EntityType entityType : EntityType.values())
                    {
                        if (entityType.isAlive() && entityType.isSpawnable())
                            commandSender.sendMessage("- " + entityType.name());
                    }
                }
                break;
            case 3:
                if (args[0].equalsIgnoreCase("setItem"))
                {
                    if (MobSelector.getInstance() == null)
                    {
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "The Module \"MobSelector\" isn't enabled!");
                        return false;
                    }

                    MobSelector.MobImpl mob = CollectionWrapper.filter(MobSelector.getInstance().getMobs().values(), new Acceptable<MobSelector.MobImpl>() {
                        @Override
                        public boolean isAccepted(MobSelector.MobImpl value)
                        {
                            return value.getMob().getName().equalsIgnoreCase(args[1]);
                        }
                    });
                    if (mob != null)
                    {
                        int itemId = NetworkUtils.checkIsNumber(args[2]) ? Integer.parseInt(args[2]) : 138;
                        mob.getMob().setItemId(itemId);
                        CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutAddMob(mob.getMob()));
                        commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "You set the item for \"" + args[1] + "\" the itemId \"" + itemId + "\"");
                        return false;
                    }
                }
                break;
            default:
                if (SignSelector.getInstance() != null)
                {
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cloudserver createSign <targetGroup>");
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cloudserver removeSign");
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cloudserver removeSigns <targetGroup>");
                }
                if (MobSelector.getInstance() != null)
                {
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cloudserver createMob <entityType> <name> <targetGroup> <itemId> <autoJoin> <displayName>");
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cloudserver removeMob <name>");
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cloudserver listMobs");
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cloudserver moblist");
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cloudserver setDisplay <name> <display>");
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cloudserver setItem <name> <itemId>");
                    commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cloudserver editMobLine <name> <display>");
                }
                break;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args)
    {
        return new LinkedList<>(CloudAPI.getInstance().getServerGroupMap().keySet());
    }
}