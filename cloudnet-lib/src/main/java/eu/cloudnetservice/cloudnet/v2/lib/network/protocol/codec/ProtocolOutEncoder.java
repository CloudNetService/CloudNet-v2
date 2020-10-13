/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.lib.network.protocol.codec;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

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
