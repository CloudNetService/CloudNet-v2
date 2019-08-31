/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.permissions.listener;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.event.IEventListener;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.api.event.network.UpdateAllEvent;
import de.dytanic.cloudnetcore.permissions.PermissionModule;

/**
 * Created by Tareko on 18.10.2017.
 */
public final class UpdateAllListener implements IEventListener<UpdateAllEvent> {

    @Override
    public void onCall(UpdateAllEvent event)
    {
        PermissionModule.getInstance().getPermissionPool().setAvailable(PermissionModule.getInstance().getConfigPermission().isEnabled());
        PermissionModule.getInstance().getPermissionPool().getGroups().clear();
        NetworkUtils.addAll(PermissionModule.getInstance().getPermissionPool().getGroups(), PermissionModule.getInstance().getConfigPermission().loadAll());
        event.getNetworkManager().getModuleProperties().append("permissionPool",
                Document.GSON.toJson(PermissionModule.getInstance().getPermissionPool(),
                        TypeToken.get(PermissionPool.class).getType()));
    }
}