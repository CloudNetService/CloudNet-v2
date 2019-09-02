/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketAPIInGetProxys extends PacketAPIIO {

	@Override
	public void handleInput(Document data, PacketSender packetSender) {
		if (data.contains("group")) {

			Collection<ProxyInfo> proxyInfos = CloudNet.getInstance().getProxys(data.getString("group")).stream().map(ProxyServer::getProxyInfo).collect(Collectors.toList());
			packetSender.sendPacket(getResult(new Document("proxyInfos", proxyInfos)));
		} else {

			Collection<ProxyInfo> proxyInfos = CloudNet.getInstance().getProxys().values().stream().map(ProxyServer::getProxyInfo).collect(Collectors.toList());
			packetSender.sendPacket(getResult(new Document("proxyInfos", proxyInfos)));
		}
	}

	@Override
	protected Packet getResult(Document value) {
		return new Packet(packetUniqueId, PacketRC.SERVER_HANDLE, value);
	}
}