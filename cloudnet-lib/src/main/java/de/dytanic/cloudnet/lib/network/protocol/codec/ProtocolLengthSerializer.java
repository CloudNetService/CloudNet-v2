/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by Tareko on 31.05.2017.
 */
public class ProtocolLengthSerializer extends MessageToByteEncoder {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {

		if (!(msg instanceof ByteBuf)) return;
		ByteBuf in = (ByteBuf) msg;

		int readableBytes = in.readableBytes();
		int lengthByteSpace = getVarIntSize(readableBytes);

		if (lengthByteSpace > 3)
		{
			throw new IllegalArgumentException();
		}

		out.ensureWritable(lengthByteSpace + readableBytes);
		writeVarInt(readableBytes, out);
		out.writeBytes(in, in.readerIndex(), readableBytes);
	}

	private int getVarIntSize(int value)
	{
		if ((value & -128) == 0)
		{
			return 1;
		} else if ((value & -16384) == 0)
		{
			return 2;
		} else if ((value & -2097152) == 0)
		{
			return 3;
		} else if ((value & -268435456) == 0)
		{
			return 4;
		}
		return 5;
	}

	private void writeVarInt(int number, ByteBuf byteBuf)
	{
		while ((number & -128) != 0)
		{
			byteBuf.writeByte(number & 127 | 128);
			number >>>= 7;
		}

		byteBuf.writeByte(number);
	}

}
