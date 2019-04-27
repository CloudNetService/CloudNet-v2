/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.WrapperExternal;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import java.util.function.Predicate;

public class PacketInWrapperInfo extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender)
    {
        WrapperExternal wrapperExternal = data.getObject("wrapper", new TypeToken<WrapperExternal>() {
        }.getType());
        CloudNetWrapper.getInstance().setSimpledUser(wrapperExternal.getUser());
        CloudNetWrapper.getInstance().getServerGroups().clear();
        NetworkUtils.addAll(CloudNetWrapper.getInstance().getServerGroups(), wrapperExternal.getServerGroups(), new Predicate<ServerGroup>() {
            @Override
            public boolean test(ServerGroup value)
            {
                System.out.println("Importing server group [" + value.getName() + "] from CloudNet-Master");
                return true;
            }
        });
        CloudNetWrapper.getInstance().getProxyGroups().clear();
        NetworkUtils.addAll(CloudNetWrapper.getInstance().getProxyGroups(), wrapperExternal.getProxyGroups(), new Predicate<ProxyGroup>() {
            @Override
            public boolean test(ProxyGroup value)
            {
                System.out.println("Importing proxy group [" + value.getName() + "] from CloudNet-Master");
                return true;
            }
        });
    }
}