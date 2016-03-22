package File;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Sir-S on 22.02.2016.
 */

public class InputFile {
    private BufferedInputStream inStream;
    private int buffer;
    private int lengthBuffer;
    private int tail;
    private int lengthTail;


    public InputFile() {
        inStream = null;
        buffer = 0;
        lengthBuffer = 0;
        tail  = 0;
        lengthTail = 0;
    }

    public int[] open(String filename) throws IOException {
        int[] data = new int[2];
        try {
            inStream = new BufferedInputStream(new FileInputStream(filename));
            data[0] = inStream.read() << 24 | inStream.read() << 16 | inStream.read() << 8 | inStream.read();
            data[1] = inStream.read() << 24 | inStream.read() << 16 | inStream.read() << 8 | inStream.read();
            lengthTail = inStream.read();
            if (lengthTail < 0 || lengthTail > 7 || (lengthTail == 0 && buffer != 0)) throw new IOException();
            tail = inStream.read() << (8 - lengthTail);
        } catch (FileNotFoundException e) {
            throw new IOException(filename + " не найден");
        } catch (IOException e) {
            throw new IOException("Файл испорчен");
        }
        return data;
    }

    public void close() throws IOException {
        buffer = 0;
        lengthBuffer = 0;
        tail = 0;
        lengthTail = 0;
        try {
            inStream.close();
        } catch (IOException e) {
            throw (new IOException("Ошибка закрытия файла"));
        }
    }

    private boolean readBit() throws IOException {
        boolean bit;
        if (lengthBuffer == 0) {
            try {
                buffer = inStream.read();
                if(buffer == -1) throw new IOException();
                lengthBuffer = 8;
            } catch (IOException e) {
                if (lengthTail > 0) {
                    bit = (tail & 0x80000000) > 0;
                    tail <<= 1;
                    --lengthTail;
                    return bit;
                } else throw new IOException("Конец Файла");
            }
        }

        bit = ((buffer << (byte) (32 - lengthBuffer)) & 0x80000000) != 0 ;
        --lengthBuffer;
        return bit;
    }

    private int readByte() throws IOException {
        int val = 0;
        int tmp;
        try {
            tmp = inStream.read();
            if (tmp == -1) throw new IOException("Конец файла");
            if (lengthBuffer == 0) {
                val = tmp;
            }
            else {
                byte s = (byte) (8 - lengthBuffer);
                buffer <<= s;
                buffer |= (tmp << 24) >>> (24 + lengthBuffer);
                val = buffer;
                buffer = (byte) (((tmp << (s + 23)) << 1) >>> (s + 24));
            }
        } catch (IOException e) {
            if (lengthBuffer == 0 && lengthTail == 0) throw new IOException("Конец файла");
            if (lengthBuffer > 0 && lengthTail > 0) {
                buffer <<= lengthTail;
                tail = (byte) ((tail << 24) >>> (24 + lengthBuffer));
                buffer |= tail;
                val = buffer;
                buffer = 0;
                tail = 0;
                lengthBuffer = 0;
                lengthTail = 0;
            }
        }
        return val;
    }

    private int read2Byte() throws IOException {
        int val = 0;
        int tmp;
        tmp = readByte();
        val |= tmp;
        val = val << 24 >>> 24;
        val <<= 8;
        tmp = readByte();
        val |= tmp << 24 >>> 24;
        return val;
    }

    public int getIndex() {
        try {
            int in;
            boolean bit;
            bit = readBit();
            if (!bit)
                in = readByte() << 24 >>> 24;
            else
                in = read2Byte() << 16 >>> 16;
            return in;
        } catch (IOException e) {
            return -1;
        }
    }
}
