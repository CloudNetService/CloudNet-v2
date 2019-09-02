/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.codec;

import de.dytanic.cloudnet.lib.network.protocol.ProtocolBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by Tareko on 31.05.2017.
 */
public final class ProtocolLengthSerializer extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        ProtocolBuffer in = new ProtocolBuffer(msg), outbuffer = new ProtocolBuffer(out);
        int readableBytes = in.readableBytes(), lengthByteSpace = getVarIntSize(readableBytes);

        if (lengthByteSpace > 3) {
            throw new IllegalArgumentException();
        }

        out.ensureWritable(lengthByteSpace + readableBytes);
        outbuffer.writeVarInt(readableBytes);
        out.writeBytes(in, in.readerIndex(), readableBytes);
    }

    private int getVarIntSize(int value) {
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
