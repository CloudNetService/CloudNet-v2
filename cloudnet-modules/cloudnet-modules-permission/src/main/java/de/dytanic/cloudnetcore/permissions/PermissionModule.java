/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.permissions;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.api.CoreModule;
import de.dytanic.cloudnetcore.permissions.command.CommandPermissions;
import de.dytanic.cloudnetcore.permissions.config.ConfigPermissions;
import de.dytanic.cloudnetcore.permissions.listener.PlayerInitListener;
import de.dytanic.cloudnetcore.permissions.listener.UpdateAllListener;
import de.dytanic.cloudnetcore.permissions.network.packet.in.PacketInUpdatePermissionGroup;

/**
 * Created by Tareko on 17.10.2017.
 */
public class PermissionModule extends CoreModule {

    private static PermissionModule instance;

    public static PermissionModule getInstance() {
        return instance;
    }

    private ConfigPermissions configPermission;
    private PermissionPool permissionPool;

    public ConfigPermissions getConfigPermission() {
        return configPermission;
    }

    public PermissionPool getPermissionPool() {
        return permissionPool;
    }

    @Override
    public void onLoad()
    {
        instance = this;
    }

    @Override
    public void onBootstrap()
    {
        try
        {
            configPermission = new ConfigPermissions();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        permissionPool = new PermissionPool();
        permissionPool.setAvailable(configPermission.isEnabled0());
        NetworkUtils.addAll(permissionPool.getGroups(), configPermission.loadAll0());

        getCloud().getNetworkManager().getModuleProperties().append("permissionPool",
                Document.GSON.toJson(permissionPool, TypeToken.get(PermissionPool.class).getType()));

        getCloud().getEventManager().registerListener(this, new PlayerInitListener());
        getCloud().getEventManager().registerListener(this, new UpdateAllListener());

        getCloud().getCommandManager().registerCommand(new CommandPermissions());

        getCloud().getPacketManager().registerHandler(PacketRC.CN_CORE + 1, PacketInUpdatePermissionGroup.class);
    }
}