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

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.ProtocolBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public final class ProtocolLengthSerializer extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        ProtocolBuffer in = new ProtocolBuffer(msg);
        ProtocolBuffer outBuffer = new ProtocolBuffer(out);
        int readableBytes = in.readableBytes(), lengthByteSpace = getVarIntSize(readableBytes);

        if (lengthByteSpace > 3) {
            throw new IllegalArgumentException();
        }

        out.ensureWritable(lengthByteSpace + readableBytes);
        outBuffer.writeVarInt(readableBytes);
        out.writeBytes(in, in.readerIndex(), readableBytes);
    }

    private static int getVarIntSize(int value) {
        if ((value & -128) == 0) {
            return 1;
        } else if ((value & -16384) == 0) {
            return 2;
        } else if ((value & -2097152) == 0) {
            return 3;
        } else if ((value & -268435456) == 0) {
            return 4;
        }
        return 5;
    }

}
