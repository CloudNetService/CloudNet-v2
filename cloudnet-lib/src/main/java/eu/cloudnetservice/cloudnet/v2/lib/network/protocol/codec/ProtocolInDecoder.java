package eu.cloudnetservice.cloudnet.v2.lib.network.protocol.codec;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.IProtocol;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.ProtocolBuffer;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.ProtocolProvider;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.ProtocolStream;
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
