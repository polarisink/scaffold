package com.scaffold.udp.jna;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 高级比特流处理器，支持各种数据类型的读取
 */
public class AdvancedBitStreamReader {
    private final ByteBuf buffer;
    private int currentByte;
    private int bitsAvailableInCurrentByte = 0;

    public AdvancedBitStreamReader(ByteBuf buf) {
        this.buffer = Unpooled.wrappedBuffer(buf);
        loadNextByte();
    }

    private void loadNextByte() {
        if (buffer.isReadable()) {
            currentByte = buffer.readByte() & 0xFF;
            bitsAvailableInCurrentByte = 8;
        } else {
            currentByte = 0;
            bitsAvailableInCurrentByte = 0;
        }
    }

    /**
     * 读取指定数量的比特
     */
    private int readBitsInternal(int numBits) {
        if (numBits <= 0) return 0;

        int result = 0;

        while (numBits > 0) {
            if (bitsAvailableInCurrentByte == 0) {
                loadNextByte();
            }

            int bitsToRead = Math.min(numBits, bitsAvailableInCurrentByte);
            int bits = (currentByte >> (bitsAvailableInCurrentByte - bitsToRead))
                    & ((1 << bitsToRead) - 1);

            result = (result << bitsToRead) | bits;
            numBits -= bitsToRead;
            bitsAvailableInCurrentByte -= bitsToRead;
        }

        return result;
    }

    public byte readByte(int bitLength) {
        if (bitLength < 1 || bitLength > 8) {
            throw new IllegalArgumentException("Bit length for byte must be 1-8");
        }
        return (byte) readBitsInternal(bitLength);
    }

    public short readShort(int bitLength) {
        if (bitLength < 1 || bitLength > 16) {
            throw new IllegalArgumentException("Bit length for int must be 1-16");
        }
        return (short) readBitsInternal(bitLength);
    }

    public int readInt(int bitLength) {
        if (bitLength < 1 || bitLength > 32) {
            throw new IllegalArgumentException("Bit length for int must be 1-32");
        }
        return readBitsInternal(bitLength);
    }

    public long readLong(int bitLength) {
        if (bitLength < 1 || bitLength > 64) {
            throw new IllegalArgumentException("Bit length for long must be 1-64");
        }

        long result = 0;
        if (bitLength > 32) {
            int highBits = bitLength - 32;
            result = ((long) readBitsInternal(highBits)) << 32;
            bitLength = 32;
        }
        result |= (readBitsInternal(bitLength) & 0xFFFFFFFFL);
        return result;
    }

    public boolean readBoolean() {
        return readBitsInternal(1) == 1;
    }

    public void skipBits(int numBits) {
        readBitsInternal(numBits); // 简单实现，直接读取并丢弃
    }
}