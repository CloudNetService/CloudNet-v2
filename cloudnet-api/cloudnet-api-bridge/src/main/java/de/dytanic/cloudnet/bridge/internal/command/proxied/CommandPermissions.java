/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.command.proxied;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.*;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tareko on 23.08.2017.
 */
public final class CommandPermissions extends Command implements TabExecutor {

    public CommandPermissions() {
        super("cperms", "cloudnet.command.permissions", "permissions", "perms");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("group")) {
                if (args.length == 1) {
                    sender.sendMessage(NetworkUtils.SPACE_STRING);
                    sender.sendMessage("The following permission groups are registered:");
                    ArrayList<PermissionGroup> permissionGroups = new ArrayList<>(permissionPool.getGroups().values());
                    permissionGroups.sort(Comparator.comparingInt(PermissionGroup::getTagId));
                    for (PermissionGroup permissionGroup : permissionGroups) {
                        sender.sendMessage(permissionGroup.getName() + " [" + permissionGroup.getJoinPower() + "] implements " + permissionGroup.getImplementGroups());
                    }
                    sender.sendMessage(NetworkUtils.SPACE_STRING);
                    return;
                }
                if (args.length == 2) {
                    if (permissionPool.getGroups().containsKey(args[1])) {
                        sender.sendMessage(NetworkUtils.SPACE_STRING);
                        PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                        sender.sendMessage("Name: " + permissionGroup.getName());
                        sender.sendMessage("Implementations: " + permissionGroup.getImplementGroups());
                        sender.sendMessage("TagId: " + permissionGroup.getTagId());
                        sender.sendMessage("JoinPower: " + permissionGroup.getJoinPower());
                        for (Map.Entry<String, Boolean> x : permissionGroup.getPermissions().entrySet())
                            sender.sendMessage("- " + x.getKey() + ":" + x.getValue());
                        sender.sendMessage(NetworkUtils.SPACE_STRING);
                        sender.sendMessage("Permissions for groups:");
                        for (Map.Entry<String, List<String>> x : permissionGroup.getServerGroupPermissions().entrySet()) {
                            sender.sendMessage(x.getKey() + ":");
                            CollectionWrapper.iterator(x.getValue(), new Runnabled<String>() {
                                @Override
                                public void run(String obj) {
                                    sender.sendMessage("- " + obj);
                                }
                            });
                        }
                        sender.sendMessage(NetworkUtils.SPACE_STRING);
                    } else {
                        sender.sendMessage("The specified permission group doesn't exist");
                    }
                }

                if (args.length == 4) {
                    if (args[2].equalsIgnoreCase("setDisplay")) {
                        if (permissionPool.getGroups().containsKey(args[1])) {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setDisplay(args[3].replace("_", NetworkUtils.SPACE_STRING));
                            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
                            sender.sendMessage("You set the display name of the permission group " + permissionGroup.getName() + " to \"" + permissionGroup.getDisplay() + "\"");
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }

                    if (args[2].equalsIgnoreCase("setPrefix")) {
                        if (permissionPool.getGroups().containsKey(args[1])) {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setPrefix(args[3].replace("_", NetworkUtils.SPACE_STRING));
                            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
                            sender.sendMessage("You set the prefix of the permission group " + permissionGroup.getName() + " to \"" + permissionGroup.getPrefix() + "\"");
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }

                    if (args[2].equalsIgnoreCase("setSuffix")) {
                        if (permissionPool.getGroups().containsKey(args[1])) {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setSuffix(args[3].replace("_", NetworkUtils.SPACE_STRING));
                            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
                            sender.sendMessage("You set the suffix of the permission group " + permissionGroup.getName() + " the suffix \"" + permissionGroup.getSuffix() + "\"");
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }

                    if (args[2].equalsIgnoreCase("setDefault")) {
                        if (permissionPool.getGroups().containsKey(args[1])) {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setDefaultGroup(args[3].equalsIgnoreCase("true"));
                            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
                            sender.sendMessage("You set the default group attribute of the permission group" + permissionGroup.getName() + " to \"" + permissionGroup.isDefaultGroup() + "\"");
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }

                    if (args[2].equalsIgnoreCase("setJoinPower")) {
                        if (permissionPool.getGroups().containsKey(args[1]) && NetworkUtils.checkIsNumber(args[3])) {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setJoinPower(Integer.parseInt(args[3]));
                            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
                            sender.sendMessage("You set the needed join power of the permission group " + permissionGroup.getName() + " to \"" + permissionGroup.getJoinPower() + "\"");
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }

                    if (args[2].equalsIgnoreCase("setTagId")) {
                        if (permissionPool.getGroups().containsKey(args[1]) && NetworkUtils.checkIsNumber(args[3])) {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setTagId(Integer.parseInt(args[3]));
                            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
                            sender.sendMessage("You set the tagID of the the permission group " + permissionGroup.getName() + " to \"" + permissionGroup.getTagId() + "\"");
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }
                    return;
                }

                if (args.length == 5) {
                    if (args[2].equalsIgnoreCase("add") && args[3].equalsIgnoreCase("permission")) {
                        if (permissionPool.getGroups().containsKey(args[1])) {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.getPermissions().put(args[4].replaceFirst("-", NetworkUtils.EMPTY_STRING), !args[4].startsWith("-"));
                            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
                            sender.sendMessage("You added the permission " + args[4] + " for the permission group \"" + permissionGroup.getName() + "\"");
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }

                    if (args[2].equalsIgnoreCase("remove") && args[3].equalsIgnoreCase("permission")) {
                        if (permissionPool.getGroups().containsKey(args[1])) {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.getPermissions().remove(args[4]);
                            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
                            sender.sendMessage("You removed the permission " + args[4] + " for the permission group \"" + permissionGroup.getName() + "\"");
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }
                }

                if (args.length == 6) {
                    if (args[2].equalsIgnoreCase("add") && args[3].equalsIgnoreCase("permission")) {
                        if (permissionPool.getGroups().containsKey(args[1])) {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);

                            if (!permissionGroup.getServerGroupPermissions().containsKey(args[5])) {
                                permissionGroup.getServerGroupPermissions().put(args[5], new ArrayList<>());
                            }

                            permissionGroup.getServerGroupPermissions().get(args[5]).add(args[4].replaceFirst("-", NetworkUtils.EMPTY_STRING));
                            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
                            sender.sendMessage("You added the permission " + args[4] + " for the permission group \"" + permissionGroup.getName() + "\" on server group " + args[5]);
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }

                    if (args[2].equalsIgnoreCase("remove") && args[3].equalsIgnoreCase("permission")) {
                        if (permissionPool.getGroups().containsKey(args[1])) {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);

                            if (!permissionGroup.getServerGroupPermissions().containsKey(args[5])) {
                                permissionGroup.getServerGroupPermissions().put(args[5], new ArrayList<>());
                            }

                            permissionGroup.getServerGroupPermissions().get(args[5]).remove(args[4].replaceFirst("-", NetworkUtils.EMPTY_STRING));
                            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
                            sender.sendMessage("You removed the permission " + args[4] + " for the permission group \"" + permissionGroup.getName() + "\" on server group " + args[5]);
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }
                }

                return;
            }
            if (args[0].equalsIgnoreCase("user")) {
                if (args.length == 2) {
                    UUID uniqueId = CloudAPI.getInstance().getPlayerUniqueId(args[1]);
                    if (uniqueId != null) {
                        OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(uniqueId);
                        if (offlinePlayer != null && offlinePlayer.getPermissionEntity() != null) {
                            StringBuilder stringBuilder = new StringBuilder();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
                            for (GroupEntityData groupEntityData : offlinePlayer.getPermissionEntity().getGroups()) {
                                stringBuilder.append(groupEntityData.getGroup() + "@" + (groupEntityData.getTimeout() == 0 || groupEntityData.getTimeout() == -1 ? "LIFETIME" : simpleDateFormat.format(groupEntityData.getTimeout())) + NetworkUtils.SPACE_STRING);
                            }
                            sender.sendMessage(NetworkUtils.SPACE_STRING);
                            sender.sendMessage("Player " + offlinePlayer.getName() + ": " + offlinePlayer.getUniqueId());
                            sender.sendMessage("Groups: " + stringBuilder.substring(0));

                            for (Map.Entry<String, Boolean> booleanEntry : offlinePlayer.getPermissionEntity().getPermissions().entrySet()) {
                                sender.sendMessage("- " + booleanEntry.getKey() + " [" + booleanEntry.getValue() + "]");
                            }

                            sender.sendMessage(NetworkUtils.SPACE_STRING);
                        } else {
                            sender.sendMessage("The player isn't registered in permissions database");
                        }
                    } else {
                        sender.sendMessage("The player isn't registered in permissions database");
                    }
                    return;
                }

                //UTILS

                if (args.length == 5) {
                    UUID uniqueId = CloudAPI.getInstance().getPlayerUniqueId(args[1]);
                    if (uniqueId != null) {
                        OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(uniqueId);
                        if (offlinePlayer != null && offlinePlayer.getPermissionEntity() != null) {
                            if (args[2].equalsIgnoreCase("GROUP")) {
                                if (args[3].equalsIgnoreCase("REMOVE")) {
                                    if (permissionPool.getGroups().containsKey(args[4])) {
                                        GroupEntityData groupEntityData = null;
                                        for (GroupEntityData groupEntity : offlinePlayer.getPermissionEntity().getGroups()) {
                                            if (groupEntity.getGroup().equalsIgnoreCase(args[4])) {
                                                groupEntityData = groupEntity;
                                            }
                                        }

                                        if (groupEntityData != null) {
                                            offlinePlayer.getPermissionEntity().getGroups().remove(groupEntityData);
                                        }

                                        if (offlinePlayer.getPermissionEntity().getGroups().size() == 0) {
                                            offlinePlayer.getPermissionEntity().getGroups().add(new GroupEntityData(permissionPool.getDefaultGroup().getName(), 0));
                                        }
                                        updatePlayer(offlinePlayer);
                                        sender.sendMessage("The player " + offlinePlayer.getName() + " is no longer a member of permission group " + args[4]);
                                    }
                                }
                            }

                            if (args[2].equalsIgnoreCase("ADD")) {
                                if (args[3].equalsIgnoreCase("PERMISSION")) {
                                    offlinePlayer.getPermissionEntity().getPermissions().put(args[4].replaceFirst("-", NetworkUtils.EMPTY_STRING), !args[4].startsWith("-"));
                                    updatePlayer(offlinePlayer);
                                    sender.sendMessage("The permission \"" + args[4] + "\" was added for " + offlinePlayer.getName());
                                }
                            }

                            if (args[2].equalsIgnoreCase("REMOVE")) {
                                if (args[3].equalsIgnoreCase("PERMISSION")) {
                                    offlinePlayer.getPermissionEntity().getPermissions().remove(args[4]);
                                    updatePlayer(offlinePlayer);
                                    sender.sendMessage("The permission \"" + args[4] + "\" was removed for " + offlinePlayer.getName());
                                }
                            }
                        } else {
                            sender.sendMessage("The player isn't registered in permissions database");
                        }
                    } else {
                        sender.sendMessage("The player isn't registered in permissions database");
                    }
                    return;
                }

                if (args.length == 6) {
                    UUID uniqueId = CloudAPI.getInstance().getPlayerUniqueId(args[1]);
                    if (uniqueId != null) {
                        OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(uniqueId);
                        if (offlinePlayer != null && offlinePlayer.getPermissionEntity() != null) {
                            if (args[2].equalsIgnoreCase("GROUP")) {
                                if (args[3].equalsIgnoreCase("SET")) {
                                    if (permissionPool.getGroups().containsKey(args[4])) {
                                        offlinePlayer.getPermissionEntity().getGroups().clear();
                                        offlinePlayer.getPermissionEntity().getGroups()
                                                .add(new GroupEntityData(args[4],
                                                        (args[5].equalsIgnoreCase("lifetime") ? 0L : NetworkUtils.checkIsNumber(args[5]) ? calcDays(Integer.parseInt(args[5])) : 0L)));
                                        updatePlayer(offlinePlayer);
                                        sender.sendMessage("The central group of " + offlinePlayer.getName() + " is now " + args[4]);
                                    } else {
                                        sender.sendMessage("The specified permission group doesn't exist");
                                    }
                                    return;
                                }
                                if (args[3].equalsIgnoreCase("ADD")) {
                                    if (permissionPool.getGroups().containsKey(args[4])) {
                                        offlinePlayer.getPermissionEntity().getGroups()
                                                .add(new GroupEntityData(args[4],
                                                        (args[5].equalsIgnoreCase("lifetime") ? 0L : NetworkUtils.checkIsNumber(args[5]) ? calcDays(Integer.parseInt(args[5])) : 0L)));
                                        updatePlayer(offlinePlayer);
                                        sender.sendMessage("The player " + offlinePlayer.getName() + " is now also a member of the group " + args[4]);
                                    } else {
                                        sender.sendMessage("The specified permission group doesn't exist");
                                    }
                                    return;
                                }
                            }
                        } else {
                            sender.sendMessage("The player isn't registered in permissions database");
                        }
                    } else {
                        sender.sendMessage("The player isn't registered in permissions database");
                    }
                    return;
                }

                return;
            }

            if (args[0].equalsIgnoreCase("create")) {
                if (args.length == 2) {
                    if (!permissionPool.getGroups().containsKey(args[1])) {
                        PermissionGroup permissionGroup = new DefaultPermissionGroup(args[1]);
                        CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
                        sender.sendMessage("The group " + args[1] + " was created!");
                    } else {
                        sender.sendMessage("The permission group already exists");
                    }
                }
            }
        } else {
            sender.sendMessage("CloudNet-Permissions: [\"_\" = \" \"]");
            sender.sendMessage(NetworkUtils.SPACE_STRING);
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms CREATE <groupName>");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms GROUP");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms GROUP <name>");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms GROUP <name> add permission <permission>");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms GROUP <name> remove permission <permission>");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms GROUP <name> add permission <permission> <group>");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms GROUP <name> remove permission <permission> <group>");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms GROUP <name> setDisplay <display>");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms GROUP <name> setJoinPower <joinPower>");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms GROUP <name> setSuffix <suffix>");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms GROUP <name> setPrefix <prefix>");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms GROUP <name> setTagId <tagId>");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms GROUP <name> setDefault <true : false>");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms USER <user>");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms USER <user> GROUP SET <name> <lifetime | time in days> ");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms USER <user> GROUP ADD <name> <lifetime | time in days> ");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms USER <user> GROUP REMOVE <name>");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms USER <user> ADD PERMISSION <permission>");
            sender.sendMessage(CloudAPI.getInstance().getPrefix() + "/cperms USER <user> REMOVE PERMISSION <permission>");
        }
    }

    private void updatePlayer(OfflinePlayer offlinePlayer) {
        CloudAPI.getInstance().updatePlayer(offlinePlayer);
    }

    private long calcDays(int value) {
        return (System.currentTimeMillis() + ((TimeUnit.DAYS.toMillis(value))));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
        return new LinkedList<>(CloudAPI.getInstance().getPermissionPool().getGroups().keySet());
    }
}