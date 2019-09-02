/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.codec;

import de.dytanic.cloudnet.lib.network.protocol.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by Tareko on 09.09.2017.
 */
public final class ProtocolOutEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        ProtocolBuffer protocolBuffer = ProtocolProvider.protocolBuffer(byteBuf);

        if (o instanceof ProtocolRequest) {
            ProtocolRequest protocolRequest = ((ProtocolRequest) o);
            IProtocol iProtocol = ProtocolProvider.getProtocol(protocolRequest.getId());
            ProtocolStream protocolStream = iProtocol.createElement(protocolRequest.getElement());
            protocolStream.write(protocolBuffer);
        } else {
            for (IProtocol iProtocol : ProtocolProvider.protocols()) {
                ProtocolStream protocolStream = iProtocol.createElement(o);
                if (protocolStream != null) {
                    protocolStream.write(protocolBuffer);
                    break;
                }
            }
        }
    }
}
