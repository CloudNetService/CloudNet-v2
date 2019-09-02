/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.permissions.command;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.DefaultPermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutUpdateOfflinePlayer;
import de.dytanic.cloudnetcore.permissions.PermissionModule;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tareko on 20.08.2017.
 */
public final class CommandPermissions extends Command {

    public CommandPermissions() {
        super("perms", "cloudnet.command.permissions", "permissions", "cperms");

        description = "Manages the permissions system of permission module";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        PermissionPool permissionPool = PermissionModule.getInstance().getPermissionPool();
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("group")) {
                if (args.length == 1) {
                    sender.sendMessage(NetworkUtils.SPACE_STRING, "Permission groups:");
                    ArrayList<PermissionGroup> permissionGroups = new ArrayList<>(PermissionModule.getInstance()
                                                                                                  .getPermissionPool()
                                                                                                  .getGroups()
                                                                                                  .values());
                    permissionGroups.sort(Comparator.comparingInt(PermissionGroup::getTagId));
                    for (PermissionGroup permissionGroup : permissionGroups) {
                        sender.sendMessage(permissionGroup.getName() + " [" + permissionGroup.getJoinPower() + "] implements " + permissionGroup
                            .getImplementGroups());
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
                        for (Map.Entry<String, Boolean> x : permissionGroup.getPermissions().entrySet()) {
                            sender.sendMessage("- " + x.getKey() + ':' + x.getValue());
                        }
                        sender.sendMessage(NetworkUtils.SPACE_STRING);
                        sender.sendMessage("Permissions for server groups:");
                        for (Map.Entry<String, List<String>> x : permissionGroup.getServerGroupPermissions().entrySet()) {
                            sender.sendMessage(x.getKey() + ':');
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
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("You set the display for the permission group " + permissionGroup.getName() + " to \"" + permissionGroup
                                .getDisplay() + '"');
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }

                    if (args[2].equalsIgnoreCase("setPrefix")) {
                        if (permissionPool.getGroups().containsKey(args[1])) {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setPrefix(args[3].replace("_", NetworkUtils.SPACE_STRING));
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("You set the prefix for the permission group " + permissionGroup.getName() + " to \"" + permissionGroup
                                .getPrefix() + '"');
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }

                    if (args[2].equalsIgnoreCase("setSuffix")) {
                        if (permissionPool.getGroups().containsKey(args[1])) {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setSuffix(args[3].replace("_", NetworkUtils.SPACE_STRING));
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("You set the suffix for the permission group " + permissionGroup.getName() + " to \"" + permissionGroup
                                .getSuffix() + '"');
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }
                    if (args[2].equalsIgnoreCase("setColor")) {
                        if (permissionPool.getGroups().containsKey(args[1])) {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setColor(args[3].toLowerCase());
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("You set the color for the permission group " + permissionGroup.getName() + " to \"" + permissionGroup
                                .getColor() + '"');
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }

                    if (args[2].equalsIgnoreCase("setDefault")) {
                        if (permissionPool.getGroups().containsKey(args[1])) {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setDefaultGroup(args[3].equalsIgnoreCase("true"));
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("You set the default group attribute for the permission group " + permissionGroup.getName() + " to \"" + permissionGroup
                                .isDefaultGroup() + '"');
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }

                    if (args[2].equalsIgnoreCase("setJoinPower")) {
                        if (permissionPool.getGroups().containsKey(args[1]) && NetworkUtils.checkIsNumber(args[3])) {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setJoinPower(Integer.parseInt(args[3]));
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("You set the join power for the permission group " + permissionGroup.getName() + " to \"" + permissionGroup
                                .getJoinPower() + '"');
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }

                    if (args[2].equalsIgnoreCase("setTagId")) {
                        if (permissionPool.getGroups().containsKey(args[1]) && NetworkUtils.checkIsNumber(args[3])) {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.setTagId(Integer.parseInt(args[3]));
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("You set the tagID for the permission group " + permissionGroup.getName() + " to \"" + permissionGroup
                                .getTagId() + '"');
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
                            String permission = args[4].replaceFirst("-", NetworkUtils.EMPTY_STRING);
                            boolean value = !args[4].startsWith("-");
                            if (!permissionIsSet(permissionGroup.getPermissions(), permission, value)) {
                                permissionGroup.getPermissions().put(permission, value);
                                PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                                CloudNet.getInstance().getNetworkManager().reload();
                                CloudNet.getInstance().getNetworkManager().updateAll0();
                                sender.sendMessage("You added the permission " + args[4] + " to the permission group \"" + permissionGroup.getName() + '"');
                            } else {
                                sender.sendMessage("The permission " + permission + " with the value " + String.valueOf(value)
                                                                                                               .toLowerCase() + " is already set for the permission group " + permissionGroup
                                    .getName());
                            }
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }

                    if (args[2].equalsIgnoreCase("remove") && args[3].equalsIgnoreCase("permission")) {
                        if (permissionPool.getGroups().containsKey(args[1])) {
                            PermissionGroup permissionGroup = permissionPool.getGroups().get(args[1]);
                            permissionGroup.getPermissions().remove(args[4]);
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("You removed the permission " + args[4] + " from the permission group \"" + permissionGroup.getName() + '"');
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

                            permissionGroup.getServerGroupPermissions().get(args[5]).add(args[4].replaceFirst("-", ""));
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("You added the permission " + args[4] + " to the permission group \"" + permissionGroup.getName() + "\" on the server group " + args[5]);
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

                            permissionGroup.getServerGroupPermissions().get(args[5]).remove(args[4].replaceFirst("-", ""));
                            PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                            CloudNet.getInstance().getNetworkManager().reload();
                            CloudNet.getInstance().getNetworkManager().updateAll0();
                            sender.sendMessage("You removed the permission " + args[4] + " from the permission group \"" + permissionGroup.getName() + "\" on the server group " + args[5]);
                        } else {
                            sender.sendMessage("The specified permission group doesn't exist");
                        }
                    }
                }

                return;
            }
            if (args[0].equalsIgnoreCase("user")) {
                if (args.length == 2) {
                    UUID uniqueId = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().get(args[1]);
                    if (uniqueId != null) {
                        OfflinePlayer offlinePlayer = CloudNet.getInstance().getDbHandlers().getPlayerDatabase().getPlayer(uniqueId);
                        if (offlinePlayer != null && offlinePlayer.getPermissionEntity() != null) {
                            StringBuilder stringBuilder = new StringBuilder();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
                            for (GroupEntityData groupEntityData : offlinePlayer.getPermissionEntity().getGroups()) {
                                stringBuilder.append(groupEntityData.getGroup() + '@' + (groupEntityData.getTimeout() == 0 || groupEntityData
                                    .getTimeout() == -1 ? "LIFETIME" : simpleDateFormat.format(groupEntityData.getTimeout())) + NetworkUtils.SPACE_STRING);
                            }

                            sender.sendMessage(NetworkUtils.SPACE_STRING,
                                               "Player " + offlinePlayer.getName() + ": " + offlinePlayer.getUniqueId(),
                                               "Groups: " + stringBuilder.substring(0),
                                               NetworkUtils.SPACE_STRING);

                            for (Map.Entry<String, Boolean> booleanEntry : offlinePlayer.getPermissionEntity()
                                                                                        .getPermissions()
                                                                                        .entrySet()) {
                                sender.sendMessage("- " + booleanEntry.getKey() + " [" + booleanEntry.getValue() + ']');
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

                if (args.length == 5) {//perms user Dytanic group remove Admin
                    UUID uniqueId = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().get(args[1]);
                    if (uniqueId != null) {
                        OfflinePlayer offlinePlayer = CloudNet.getInstance().getDbHandlers().getPlayerDatabase().getPlayer(uniqueId);
                        if (offlinePlayer != null && offlinePlayer.getPermissionEntity() != null) {
                            if (args[2].equalsIgnoreCase("GROUP")) {
                                if (args[3].equalsIgnoreCase("REMOVE")) {
                                    if (PermissionModule.getInstance().getPermissionPool().getGroups().containsKey(args[4])) {
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
                                            offlinePlayer.getPermissionEntity()
                                                         .getGroups()
                                                         .add(new GroupEntityData(permissionPool.getDefaultGroup().getName(), 0));
                                        }
                                        updatePlayer(offlinePlayer);
                                        sender.sendMessage("The player " + offlinePlayer.getName() + " is no longer a member of the group " + args[4]);
                                    }
                                }
                            }

                            if (args[2].equalsIgnoreCase("ADD")) {
                                if (args[3].equalsIgnoreCase("PERMISSION")) {
                                    offlinePlayer.getPermissionEntity().getPermissions().put(args[4].replaceFirst("-", ""),
                                                                                             !args[4].startsWith("-"));
                                    updatePlayer(offlinePlayer);
                                    sender.sendMessage("The player \"" + offlinePlayer.getName() + "\" now has the permission " + args[4]);
                                }
                            }

                            if (args[2].equalsIgnoreCase("REMOVE")) {
                                if (args[3].equalsIgnoreCase("PERMISSION")) {
                                    offlinePlayer.getPermissionEntity().getPermissions().remove(args[4]);
                                    updatePlayer(offlinePlayer);
                                    sender.sendMessage("The players \"" + offlinePlayer.getName() + "\" no longer has the permission " + args[4]);
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
                    UUID uniqueId = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().get(args[1]);
                    if (uniqueId != null) {
                        OfflinePlayer offlinePlayer = CloudNet.getInstance().getDbHandlers().getPlayerDatabase().getPlayer(uniqueId);
                        if (offlinePlayer != null && offlinePlayer.getPermissionEntity() != null) {
                            if (args[2].equalsIgnoreCase("GROUP")) {
                                if (args[3].equalsIgnoreCase("SET")) {
                                    if (permissionPool.getGroups().containsKey(args[4])) {
                                        offlinePlayer.getPermissionEntity().getGroups().clear();
                                        offlinePlayer.getPermissionEntity().getGroups().add(new GroupEntityData(args[4],
                                                                                                                (args[5].equalsIgnoreCase(
                                                                                                                    "lifetime") ? 0L : NetworkUtils
                                                                                                                    .checkIsNumber(args[5]) ? calcDays(
                                                                                                                    Integer.parseInt(args[5])) : 0L)));
                                        updatePlayer(offlinePlayer);
                                        sender.sendMessage("The central group of " + offlinePlayer.getName() + " is now " + args[4]);
                                    } else {
                                        sender.sendMessage("The specified permission group doesn't exist");
                                    }
                                    return;
                                }
                                if (args[3].equalsIgnoreCase("ADD")) {
                                    if (permissionPool.getGroups().containsKey(args[4])) {
                                        offlinePlayer.getPermissionEntity().getGroups().add(new GroupEntityData(args[4],
                                                                                                                (args[5].equalsIgnoreCase(
                                                                                                                    "lifetime") ? 0L : NetworkUtils
                                                                                                                    .checkIsNumber(args[4]) ? calcDays(
                                                                                                                    Integer.parseInt(args[4])) : 0L)));
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
                        PermissionModule.getInstance().getConfigPermission().updatePermissionGroup(permissionGroup);
                        CloudNet.getInstance().getNetworkManager().reload();
                        CloudNet.getInstance().getNetworkManager().updateAll0();
                        sender.sendMessage("The group " + args[1] + " was created!");
                    } else {
                        sender.sendMessage("The permission group already exists");
                    }
                }
            }
        } else {
            sender.sendMessage("CloudNet-Permissions: [\"_\" = \" \"]",
                               NetworkUtils.SPACE_STRING,
                               "perms CREATE <groupName>",
                               "perms GROUP",
                               "perms GROUP <name>",
                               "perms GROUP <name> add permission <permission>",
                               "perms GROUP <name> remove permission <permission>",
                               "perms GROUP <name> add permission <permission> <group>",
                               "perms GROUP <name> remove permission <permission> <group>",
                               "perms GROUP <name> setDisplay <display>",
                               "perms GROUP <name> setJoinPower <joinPower>",
                               "perms GROUP <name> setSuffix <joinPower>",
                               "perms GROUP <name> setPrefix <prefix>",
                               "perms GROUP <name> setTagId <tagId>",
                               "perms GROUP <name> setDefault <true : false>",
                               "perms GROUP <name> setJoinPower <joinPower>",
                               "perms GROUP <name> setColor <colorCode>",
                               "perms USER <user>",
                               "perms USER <user> GROUP SET <name> <lifetime | time in days>",
                               "perms USER <user> GROUP ADD <name> <lifetime | time in days>",
                               "perms USER <user> GROUP REMOVE <name>",
                               "perms USER <user> ADD PERMISSION <permission>",
                               "perms USER <user> REMOVE PERMISSION <permission>");
        }
    }

    private boolean permissionIsSet(Map<String, Boolean> permissions, String permission, boolean value) {
        if (permissions.containsKey(permission)) {
            return permissions.get(permission).equals(value);
        }
        return false;
    }

    private void updatePlayer(OfflinePlayer offlinePlayer) {
        CloudNet.getInstance().getDbHandlers().getPlayerDatabase().updatePlayer(offlinePlayer);
        if (CloudNet.getInstance().getNetworkManager().getOnlinePlayers().containsKey(offlinePlayer.getUniqueId())) {
            CloudNet.getInstance().getNetworkManager().getOnlinePlayers().get(offlinePlayer.getUniqueId()).setPermissionEntity(offlinePlayer
                                                                                                                                   .getPermissionEntity());
            CloudNet.getInstance().getNetworkManager().handlePlayerUpdate(CloudNet.getInstance()
                                                                                  .getNetworkManager()
                                                                                  .getOnlinePlayers()
                                                                                  .get(offlinePlayer.getUniqueId()));
        }
        CloudNet.getInstance().getNetworkManager().sendAllUpdate(new PacketOutUpdateOfflinePlayer(offlinePlayer));
    }

    private long calcDays(int value) {
        return (System.currentTimeMillis() + ((TimeUnit.DAYS.toMillis(value))));
    }
}
