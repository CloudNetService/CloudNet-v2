/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.codec;

import de.dytanic.cloudnet.lib.network.protocol.IProtocol;
import de.dytanic.cloudnet.lib.network.protocol.ProtocolBuffer;
import de.dytanic.cloudnet.lib.network.protocol.ProtocolProvider;
import de.dytanic.cloudnet.lib.network.protocol.ProtocolStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by Tareko on 09.09.2017.
 */
public class ProtocolInDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        ProtocolBuffer protocolBuffer = ProtocolProvider.protocolBuffer(byteBuf);

        for (IProtocol iProtocol : ProtocolProvider.protocols()) {
            try {
                ProtocolStream protocolStream = iProtocol.createEmptyElement();
                protocolStream.read(protocolBuffer.clone());
                list.add(protocolStream);
                break;
            } catch (Exception ex) {

            }
        }
    }
}
