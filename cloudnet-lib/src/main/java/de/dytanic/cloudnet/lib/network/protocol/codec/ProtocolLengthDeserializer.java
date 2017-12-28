/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by Tareko on 31.05.2017.
 */
public class ProtocolLengthDeserializer extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		in.markReaderIndex();
		byte[] lengthBytes = new byte[3];

		for (int i = 0; i < 3; i++)
		{
			if (!in.isReadable())
			{
				in.resetReaderIndex();
				return;
			}
			lengthBytes[i] = in.readByte();
			if (lengthBytes[i] >= 0)
			{
				ByteBuf buffer = Unpooled.wrappedBuffer(lengthBytes);

				try
				{
					int packetLength = readVarInt(buffer);
					if (in.readableBytes() < packetLength)
					{
						in.resetReaderIndex();
						return;
					}

					out.add(in.readBytes(packetLength));
				}
				finally
				{
					buffer.release();
				}

				return;
			}
		}
	}

	private int readVarInt(ByteBuf byteBuf)
	{
		int number = 0;
		int round = 0;
		byte currentByte;

		do {
			currentByte = byteBuf.readByte();
			number |= (currentByte & 127) << round++ * 7;

			if (round > 5) {
				throw new RuntimeException();
			}
		} while ((currentByte & 128) == 128);

		return number;
	}

}
