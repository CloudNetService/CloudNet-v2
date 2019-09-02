/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.permissions.listener;

import de.dytanic.cloudnet.event.IEventListener;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnetcore.api.event.player.PlayerInitEvent;
import de.dytanic.cloudnetcore.permissions.PermissionModule;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Tareko on 18.10.2017.
 */
public final class PlayerInitListener implements IEventListener<PlayerInitEvent> {

    @Override
    public void onCall(PlayerInitEvent event) {

        if (event.getCloudPlayer().getPermissionEntity() == null) {
            event.getCloudPlayer().setPermissionEntity(PermissionModule.getInstance()
                                                                       .getPermissionPool()
                                                                       .getNewPermissionEntity(event.getCloudPlayer()));
        }

        Collection<GroupEntityData> groupEntiys = new LinkedList<>();
        for (GroupEntityData groupEntityData : event.getCloudPlayer().getPermissionEntity().getGroups()) {
            if (!PermissionModule.getInstance()
                                 .getPermissionPool()
                                 .getGroups()
                                 .containsKey(groupEntityData.getGroup()) || (System.currentTimeMillis() > groupEntityData.getTimeout() && groupEntityData
                .getTimeout() != -1 && groupEntityData.getTimeout() != 0)) {
                groupEntiys.add(groupEntityData);
            }
        }

        for (GroupEntityData groupEntityData : groupEntiys) {
            event.getCloudPlayer().getPermissionEntity().getGroups().remove(groupEntityData);
        }

        if (event.getCloudPlayer().getPermissionEntity().getGroups().size() == 0) {
            event.getCloudPlayer().getPermissionEntity().getGroups().add(new GroupEntityData(PermissionModule.getInstance()
                                                                                                             .getPermissionPool()
                                                                                                             .getDefaultGroup()
                                                                                                             .getName(), 0L));
        }
    }
}
