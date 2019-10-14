/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by Tareko on 09.09.2017.
 */
public final class ProtocolBuffer extends ByteBuf implements Cloneable {

    private ByteBuf byteBuf;

    public ProtocolBuffer(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    @Override
    public ProtocolBuffer clone() {
        return new ProtocolBuffer(Unpooled.buffer(byteBuf.readableBytes()).writeBytes(byteBuf));
    }

    public int readVarInt() {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public ProtocolBuffer writeVarInt(int value) {
        do {
            byte temp = (byte) (value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            writeByte(temp);
        } while (value != 0);
        return this;
    }

    public void writeString(String write) {
        byte[] values = write.getBytes(StandardCharsets.UTF_8);
        writeVarLong(values.length);
        writeBytes(values);
    }

    public ProtocolBuffer writeVarLong(long value) {
        do {
            byte temp = (byte) (value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            writeByte(temp);
        } while (value != 0);
        return this;
    }

    public String readString() {
        int integer = (int) readVarLong();

        byte[] buffer = new byte[integer];
        byteBuf.readBytes(buffer, 0, integer);

        return new String(buffer, StandardCharsets.UTF_8);
    }

    public long readVarLong() {
        int numRead = 0;
        long result = 0;
        byte read;
        do {
            read = readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 10) {
                throw new RuntimeException("VarLong is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    @Override
    public int capacity() {
        return byteBuf.capacity();
    }

    @Override
    public ByteBuf capacity(int i) {
        return byteBuf.capacity(i);
    }

    @Override
    public int maxCapacity() {
        return byteBuf.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return byteBuf.alloc();
    }

    @Override
    public ByteOrder order() {
        return byteBuf.order();
    }

    @Override
    public ByteBuf order(ByteOrder byteOrder) {
        return byteBuf.order(byteOrder);
    }

    @Override
    public ByteBuf unwrap() {
        return byteBuf.unwrap();
    }

    @Override
    public boolean isDirect() {
        return byteBuf.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return byteBuf.isReadOnly();
    }

    @Override
    public ByteBuf asReadOnly() {
        return byteBuf.asReadOnly();
    }

    @Override
    public int readerIndex() {
        return byteBuf.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(int i) {
        return byteBuf.readerIndex(i);
    }

    @Override
    public int writerIndex() {
        return byteBuf.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(int i) {
        return byteBuf.writerIndex(i);
    }

    @Override
    public ByteBuf setIndex(int i, int i1) {
        return byteBuf.setIndex(i, i1);
    }

    @Override
    public int readableBytes() {
        return byteBuf.readableBytes();
    }

    @Override
    public int writableBytes() {
        return byteBuf.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return byteBuf.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return byteBuf.isReadable();
    }

    @Override
    public boolean isReadable(int i) {
        return byteBuf.isReadable(i);
    }

    @Override
    public boolean isWritable() {
        return byteBuf.isWritable();
    }

    @Override
    public boolean isWritable(int i) {
        return byteBuf.isWritable(i);
    }

    @Override
    public ByteBuf clear() {
        return byteBuf.clear();
    }

    @Override
    public ByteBuf markReaderIndex() {
        return byteBuf.markReaderIndex();
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return byteBuf.resetReaderIndex();
    }

    @Override
    public ByteBuf markWriterIndex() {
        return byteBuf.markWriterIndex();
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return byteBuf.resetWriterIndex();
    }

    @Override
    public ByteBuf discardReadBytes() {
        return byteBuf.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        return byteBuf.discardSomeReadBytes();
    }

    @Override
    public ByteBuf ensureWritable(int i) {
        return byteBuf.ensureWritable(i);
    }

    @Override
    public int ensureWritable(int i, boolean b) {
        return byteBuf.ensureWritable(i, b);
    }

    @Override
    public boolean getBoolean(int i) {
        return byteBuf.getBoolean(i);
    }

    @Override
    public byte getByte(int i) {
        return byteBuf.getByte(i);
    }

    @Override
    public short getUnsignedByte(int i) {
        return byteBuf.getUnsignedByte(i);
    }

    @Override
    public short getShort(int i) {
        return byteBuf.getShort(i);
    }

    @Override
    public short getShortLE(int i) {
        return byteBuf.getShortLE(i);
    }

    @Override
    public int getUnsignedShort(int i) {
        return byteBuf.getUnsignedShort(i);
    }

    @Override
    public int getUnsignedShortLE(int i) {
        return byteBuf.getUnsignedShortLE(i);
    }

    @Override
    public int getMedium(int i) {
        return byteBuf.getMedium(i);
    }

    @Override
    public int getMediumLE(int i) {
        return byteBuf.getMediumLE(i);
    }

    @Override
    public int getUnsignedMedium(int i) {
        return byteBuf.getUnsignedMedium(i);
    }

    @Override
    public int getUnsignedMediumLE(int i) {
        return byteBuf.getUnsignedMediumLE(i);
    }

    @Override
    public int getInt(int i) {
        return byteBuf.getInt(i);
    }

    @Override
    public int getIntLE(int i) {
        return byteBuf.getIntLE(i);
    }

    @Override
    public long getUnsignedInt(int i) {
        return byteBuf.getUnsignedInt(i);
    }

    @Override
    public long getUnsignedIntLE(int i) {
        return byteBuf.getUnsignedIntLE(i);
    }

    @Override
    public long getLong(int i) {
        return byteBuf.getLong(i);
    }

    @Override
    public long getLongLE(int i) {
        return byteBuf.getLongLE(i);
    }

    @Override
    public char getChar(int i) {
        return byteBuf.getChar(i);
    }

    @Override
    public float getFloat(int i) {
        return byteBuf.getFloat(i);
    }

    @Override
    public double getDouble(int i) {
        return byteBuf.getDouble(i);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf byteBuf) {
        return this.byteBuf.getBytes(i, byteBuf);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf byteBuf, int i1) {
        return this.byteBuf.getBytes(i, byteBuf, i1);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuf byteBuf, int i1, int i2) {
        return this.byteBuf.getBytes(i, byteBuf, i1, i2);
    }

    @Override
    public ByteBuf getBytes(int i, byte[] bytes) {
        return byteBuf.getBytes(i, bytes);
    }

    @Override
    public ByteBuf getBytes(int i, byte[] bytes, int i1, int i2) {
        return byteBuf.getBytes(i, bytes, i1, i2);
    }

    @Override
    public ByteBuf getBytes(int i, ByteBuffer byteBuffer) {
        return byteBuf.getBytes(i, byteBuffer);
    }

    @Override
    public ByteBuf getBytes(int i, OutputStream outputStream, int i1) throws IOException {
        return byteBuf.getBytes(i, outputStream, i1);
    }

    @Override
    public int getBytes(int i, GatheringByteChannel gatheringByteChannel, int i1) throws IOException {
        return byteBuf.getBytes(i, gatheringByteChannel, i1);
    }

    @Override
    public int getBytes(int i, FileChannel fileChannel, long l, int i1) throws IOException {
        return byteBuf.getBytes(i, fileChannel, l, i1);
    }

    @Override
    public CharSequence getCharSequence(int i, int i1, Charset charset) {
        return byteBuf.getCharSequence(i, i1, charset);
    }

    @Override
    public ByteBuf setBoolean(int i, boolean b) {
        return byteBuf.setBoolean(i, b);
    }

    @Override
    public ByteBuf setByte(int i, int i1) {
        return byteBuf.setByte(i, i1);
    }

    @Override
    public ByteBuf setShort(int i, int i1) {
        return byteBuf.setShort(i, i1);
    }

    @Override
    public ByteBuf setShortLE(int i, int i1) {
        return byteBuf.setShortLE(i, i1);
    }

    @Override
    public ByteBuf setMedium(int i, int i1) {
        return byteBuf.setMedium(i, i1);
    }

    @Override
    public ByteBuf setMediumLE(int i, int i1) {
        return byteBuf.setMediumLE(i, i1);
    }

    @Override
    public ByteBuf setInt(int i, int i1) {
        return byteBuf.setInt(i, i1);
    }

    @Override
    public ByteBuf setIntLE(int i, int i1) {
        return byteBuf.setIntLE(i, i1);
    }

    @Override
    public ByteBuf setLong(int i, long l) {
        return byteBuf.setLong(i, l);
    }

    @Override
    public ByteBuf setLongLE(int i, long l) {
        return byteBuf.setLongLE(i, l);
    }

    @Override
    public ByteBuf setChar(int i, int i1) {
        return byteBuf.setChar(i, i1);
    }

    @Override
    public ByteBuf setFloat(int i, float v) {
        return byteBuf.setFloat(i, v);
    }

    @Override
    public ByteBuf setDouble(int i, double v) {
        return byteBuf.setDouble(i, v);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf byteBuf) {
        return this.byteBuf.setBytes(i, byteBuf);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf byteBuf, int i1) {
        return this.byteBuf.setBytes(i, byteBuf, i1);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuf byteBuf, int i1, int i2) {
        return this.byteBuf.setBytes(i, byteBuf, i1, i2);
    }

    @Override
    public ByteBuf setBytes(int i, byte[] bytes) {
        return byteBuf.setBytes(i, bytes);
    }

    @Override
    public ByteBuf setBytes(int i, byte[] bytes, int i1, int i2) {
        return byteBuf.setBytes(i, bytes, i1, i2);
    }

    @Override
    public ByteBuf setBytes(int i, ByteBuffer byteBuffer) {
        return byteBuf.setBytes(i, byteBuffer);
    }

    @Override
    public int setBytes(int i, InputStream inputStream, int i1) throws IOException {
        return byteBuf.setBytes(i, inputStream, i1);
    }

    @Override
    public int setBytes(int i, ScatteringByteChannel scatteringByteChannel, int i1) throws IOException {
        return byteBuf.setBytes(i, scatteringByteChannel, i1);
    }

    @Override
    public int setBytes(int i, FileChannel fileChannel, long l, int i1) throws IOException {
        return byteBuf.setBytes(i, fileChannel, l, i1);
    }

    @Override
    public ByteBuf setZero(int i, int i1) {
        return byteBuf.setZero(i, i1);
    }

    @Override
    public int setCharSequence(int i, CharSequence charSequence, Charset charset) {
        return byteBuf.setCharSequence(i, charSequence, charset);
    }

    @Override
    public boolean readBoolean() {
        return byteBuf.readBoolean();
    }

    @Override
    public byte readByte() {
        return byteBuf.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return byteBuf.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return byteBuf.readShort();
    }

    @Override
    public short readShortLE() {
        return byteBuf.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return byteBuf.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return byteBuf.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return byteBuf.readMedium();
    }

    @Override
    public int readMediumLE() {
        return byteBuf.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return byteBuf.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return byteBuf.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return byteBuf.readInt();
    }

    @Override
    public int readIntLE() {
        return byteBuf.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return byteBuf.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return byteBuf.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return byteBuf.readLong();
    }

    @Override
    public long readLongLE() {
        return byteBuf.readLongLE();
    }

    @Override
    public char readChar() {
        return byteBuf.readChar();
    }

    @Override
    public float readFloat() {
        return byteBuf.readFloat();
    }

    @Override
    public double readDouble() {
        return byteBuf.readDouble();
    }

    @Override
    public ByteBuf readBytes(int i) {
        return byteBuf.readBytes(i);
    }

    @Override
    public ByteBuf readSlice(int i) {
        return byteBuf.readSlice(i);
    }

    @Override
    public ByteBuf readRetainedSlice(int i) {
        return byteBuf.readRetainedSlice(i);
    }

    @Override
    public ByteBuf readBytes(ByteBuf byteBuf) {
        return this.byteBuf.readBytes(byteBuf);
    }

    @Override
    public ByteBuf readBytes(ByteBuf byteBuf, int i) {
        return this.byteBuf.readBytes(byteBuf, i);
    }

    @Override
    public ByteBuf readBytes(ByteBuf byteBuf, int i, int i1) {
        return this.byteBuf.readBytes(byteBuf, i, i1);
    }

    @Override
    public ByteBuf readBytes(byte[] bytes) {
        return byteBuf.readBytes(bytes);
    }

    @Override
    public ByteBuf readBytes(byte[] bytes, int i, int i1) {
        return byteBuf.readBytes(bytes, i, i1);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer byteBuffer) {
        return byteBuf.readBytes(byteBuffer);
    }

    @Override
    public ByteBuf readBytes(OutputStream outputStream, int i) throws IOException {
        return byteBuf.readBytes(outputStream, i);
    }

    @Override
    public int readBytes(GatheringByteChannel gatheringByteChannel, int i) throws IOException {
        return byteBuf.readBytes(gatheringByteChannel, i);
    }

    @Override
    public CharSequence readCharSequence(int i, Charset charset) {
        return byteBuf.readCharSequence(i, charset);
    }

    @Override
    public int readBytes(FileChannel fileChannel, long l, int i) throws IOException {
        return byteBuf.readBytes(fileChannel, l, i);
    }

    @Override
    public ByteBuf skipBytes(int i) {
        return byteBuf.skipBytes(i);
    }

    @Override
    public ByteBuf writeBoolean(boolean b) {
        return byteBuf.writeBoolean(b);
    }

    @Override
    public ByteBuf writeByte(int i) {
        return byteBuf.writeByte(i);
    }

    @Override
    public ByteBuf writeShort(int i) {
        return byteBuf.writeShort(i);
    }

    @Override
    public ByteBuf writeShortLE(int i) {
        return byteBuf.writeShortLE(i);
    }

    @Override
    public ByteBuf writeMedium(int i) {
        return byteBuf.writeMedium(i);
    }

    @Override
    public ByteBuf writeMediumLE(int i) {
        return byteBuf.writeMediumLE(i);
    }

    @Override
    public ByteBuf writeInt(int i) {
        return byteBuf.writeInt(i);
    }

    @Override
    public ByteBuf writeIntLE(int i) {
        return byteBuf.writeIntLE(i);
    }

    @Override
    public ByteBuf writeLong(long l) {
        return byteBuf.writeLong(l);
    }

    @Override
    public ByteBuf writeLongLE(long l) {
        return byteBuf.writeLongLE(l);
    }

    @Override
    public ByteBuf writeChar(int i) {
        return byteBuf.writeChar(i);
    }

    @Override
    public ByteBuf writeFloat(float v) {
        return byteBuf.writeFloat(v);
    }

    @Override
    public ByteBuf writeDouble(double v) {
        return byteBuf.writeDouble(v);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf byteBuf) {
        return this.byteBuf.writeBytes(byteBuf);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf byteBuf, int i) {
        return this.byteBuf.writeBytes(byteBuf, i);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf byteBuf, int i, int i1) {
        return this.byteBuf.writeBytes(byteBuf, i, i1);
    }

    @Override
    public ByteBuf writeBytes(byte[] bytes) {
        return byteBuf.writeBytes(bytes);
    }

    @Override
    public ByteBuf writeBytes(byte[] bytes, int i, int i1) {
        return byteBuf.writeBytes(bytes, i, i1);
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer byteBuffer) {
        return byteBuf.writeBytes(byteBuffer);
    }

    @Override
    public int writeBytes(InputStream inputStream, int i) throws IOException {
        return byteBuf.writeBytes(inputStream, i);
    }

    @Override
    public int writeBytes(ScatteringByteChannel scatteringByteChannel, int i) throws IOException {
        return byteBuf.writeBytes(scatteringByteChannel, i);
    }

    @Override
    public int writeBytes(FileChannel fileChannel, long l, int i) throws IOException {
        return byteBuf.writeBytes(fileChannel, l, i);
    }

    @Override
    public ByteBuf writeZero(int i) {
        return byteBuf.writeZero(i);
    }

    @Override
    public int writeCharSequence(CharSequence charSequence, Charset charset) {
        return byteBuf.writeCharSequence(charSequence, charset);
    }

    @Override
    public int indexOf(int i, int i1, byte b) {
        return byteBuf.indexOf(i, i1, b);
    }

    @Override
    public int bytesBefore(byte b) {
        return byteBuf.bytesBefore(b);
    }

    @Override
    public int bytesBefore(int i, byte b) {
        return byteBuf.bytesBefore(i, b);
    }

    @Override
    public int bytesBefore(int i, int i1, byte b) {
        return byteBuf.bytesBefore(i, i1, b);
    }

    @Override
    public int forEachByte(ByteProcessor byteProcessor) {
        return byteBuf.forEachByte(byteProcessor);
    }

    @Override
    public int forEachByte(int i, int i1, ByteProcessor byteProcessor) {
        return byteBuf.forEachByte(i, i1, byteProcessor);
    }

    @Override
    public int forEachByteDesc(ByteProcessor byteProcessor) {
        return byteBuf.forEachByteDesc(byteProcessor);
    }

    @Override
    public int forEachByteDesc(int i, int i1, ByteProcessor byteProcessor) {
        return byteBuf.forEachByteDesc(i, i1, byteProcessor);
    }

    @Override
    public ByteBuf copy() {
        return byteBuf.copy();
    }

    @Override
    public ByteBuf copy(int i, int i1) {
        return byteBuf.copy(i, i1);
    }

    @Override
    public ByteBuf slice() {
        return byteBuf.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return byteBuf.retainedSlice();
    }

    @Override
    public ByteBuf slice(int i, int i1) {
        return byteBuf.slice(i, i1);
    }

    @Override
    public ByteBuf retainedSlice(int i, int i1) {
        return byteBuf.retainedSlice(i, i1);
    }

    @Override
    public ByteBuf duplicate() {
        return byteBuf.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return byteBuf.retainedDuplicate();
    }

    @Override
    public int nioBufferCount() {
        return byteBuf.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return byteBuf.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int i, int i1) {
        return byteBuf.nioBuffer(i, i1);
    }

    @Override
    public ByteBuffer internalNioBuffer(int i, int i1) {
        return byteBuf.internalNioBuffer(i, i1);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return byteBuf.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int i, int i1) {
        return byteBuf.nioBuffers(i, i1);
    }

    @Override
    public boolean hasArray() {
        return byteBuf.hasArray();
    }

    @Override
    public byte[] array() {
        return byteBuf.array();
    }

    @Override
    public int arrayOffset() {
        return byteBuf.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return byteBuf.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return byteBuf.memoryAddress();
    }

    @Override
    public String toString(Charset charset) {
        return byteBuf.toString(charset);
    }

    @Override
    public String toString(int i, int i1, Charset charset) {
        return byteBuf.toString(i, i1, charset);
    }

    @Override
    public int hashCode() {
        return byteBuf.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return byteBuf.equals(o);
    }

    @Override
    public int compareTo(ByteBuf byteBuf) {
        return byteBuf.compareTo(byteBuf);
    }

    @Override
    public String toString() {
        return byteBuf.toString();
    }

    @Override
    public ByteBuf retain(int i) {
        return byteBuf.retain(i);
    }

    @Override
    public ByteBuf retain() {
        return byteBuf.retain();
    }

    @Override
    public ByteBuf touch() {
        return byteBuf.touch();
    }

    @Override
    public ByteBuf touch(Object o) {
        return byteBuf.touch(o);
    }

    public int forEachByte(ByteBufProcessor byteBufProcessor) {
        return byteBuf.forEachByte(byteBufProcessor);
    }

    public int forEachByte(int i, int i1, ByteBufProcessor byteBufProcessor) {
        return byteBuf.forEachByte(i, i1, byteBufProcessor);
    }

    public int forEachByteDesc(ByteBufProcessor byteBufProcessor) {
        return byteBuf.forEachByteDesc(byteBufProcessor);
    }

    public int forEachByteDesc(int i, int i1, ByteBufProcessor byteBufProcessor) {
        return byteBuf.forEachByteDesc(i, i1, byteBufProcessor);
    }

    @Override
    public int refCnt() {
        return byteBuf.refCnt();
    }

    @Override
    public boolean release() {
        return byteBuf.release();
    }

    @Override
    public boolean release(int i) {
        return byteBuf.release(i);
    }
}
