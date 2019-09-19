/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.signs;

import de.dytanic.cloudnet.event.IEventListener;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnetcore.api.CoreModule;
import de.dytanic.cloudnetcore.api.event.network.ChannelInitEvent;
import de.dytanic.cloudnetcore.api.event.network.UpdateAllEvent;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.signs.config.ConfigSignLayout;
import de.dytanic.cloudnetcore.signs.database.SignDatabase;
import de.dytanic.cloudnetcore.signs.packet.in.PacketInAddSign;
import de.dytanic.cloudnetcore.signs.packet.in.PacketInRemoveSign;
import de.dytanic.cloudnetcore.signs.packet.out.PacketOutSignSelector;

/**
 * Created by Tareko on 16.10.2017.
 */
public class SignsModule extends CoreModule implements IEventListener<UpdateAllEvent> {

    private static SignsModule instance;
    private ConfigSignLayout configSignLayout;
    private SignDatabase signDatabase;

    public static SignsModule getInstance() {
        return instance;
    }

    public ConfigSignLayout getConfigSignLayout() {
        return configSignLayout;
    }

    public SignDatabase getSignDatabase() {
        return signDatabase;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onBootstrap() {
        configSignLayout = new ConfigSignLayout();
        configSignLayout.loadLayout();
        signDatabase = new SignDatabase(getCloud().getDatabaseManager().getDatabase("cloud_internal_cfg"));

        if (getCloud().getPacketManager().buildHandlers(PacketRC.SERVER_SELECTORS + 1).size() == 0) {
            getCloud().getPacketManager().registerHandler(PacketRC.SERVER_SELECTORS + 1, PacketInAddSign.class);
        }
        if (getCloud().getPacketManager().buildHandlers(PacketRC.SERVER_SELECTORS + 2).size() == 0) {
            getCloud().getPacketManager().registerHandler(PacketRC.SERVER_SELECTORS + 2, PacketInRemoveSign.class);
        }

        getCloud().getEventManager().registerListener(this, this);
        getCloud().getEventManager().registerListener(this, new ListenerImpl());
    }

    @Override
    public void onCall(UpdateAllEvent event) {
        if (event.isOnlineCloudNetworkUpdate()) {
            event.getNetworkManager().sendToLobbys(new PacketOutSignSelector(signDatabase.loadAll(), configSignLayout.loadLayout()));
        }
    }

    private class ListenerImpl implements IEventListener<ChannelInitEvent> {

        @Override
        public void onCall(ChannelInitEvent event) {
            if (event.getINetworkComponent() instanceof Wrapper) {
                return;
            }

            if (event.getINetworkComponent() instanceof MinecraftServer && (((MinecraftServer) event.getINetworkComponent()).getGroupMode()
                                                                                                                            .equals(
                                                                                                                                ServerGroupMode.LOBBY) || ((MinecraftServer) event
                .getINetworkComponent()).getGroupMode().equals(ServerGroupMode.STATIC_LOBBY))) {
                event.getINetworkComponent().sendPacket(new PacketOutSignSelector(signDatabase.loadAll(), configSignLayout.loadLayout()));
            }
        }
    }
}
