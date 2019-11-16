/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.permissions.listener;

import de.dytanic.cloudnet.event.IEventListener;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnetcore.api.event.network.UpdateAllEvent;
import de.dytanic.cloudnetcore.permissions.PermissionModule;

/**
 * Created by Tareko on 18.10.2017.
 */
public final class UpdateAllListener implements IEventListener<UpdateAllEvent> {

    @Override
    public void onCall(UpdateAllEvent event) {
        final PermissionPool permissionPool = PermissionModule.getInstance().getPermissionPool();
        permissionPool.setAvailable(PermissionModule.getInstance().getConfigPermission().isEnabled());
        permissionPool.getGroups().clear();
        permissionPool.getGroups().putAll(PermissionModule.getInstance().getConfigPermission().loadAll());
        event.getNetworkManager().getModuleProperties().append("permissionPool", permissionPool);
    }
}
