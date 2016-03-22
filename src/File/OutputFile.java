package File;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * Created by Sir-S on 22.02.2016.
 */

public class OutputFile {
    private BufferedOutputStream outStream;
    private int buffer;
    private int lengthBuffer;
    private String fileName;

    public OutputFile() {
        outStream = null;
        buffer = 0;
        lengthBuffer = 0;
        fileName = "";
    }

    public void open(String fileName) throws IOException {
        try {
            this.fileName = fileName;
            outStream = new BufferedOutputStream(new FileOutputStream(fileName));
            for (int i = 0; i < 10; ++i)
                outStream.write(0);
        } catch (FileNotFoundException e) {
            throw new IOException(fileName + " не найден");
        } catch(IOException e) {
            throw new IOException("Ошибка записи");
        }
    }

    public void close(int maxIndex, int countIndex) throws IOException {
        RandomAccessFile tmp;
        try {
            outStream.close();
            tmp = new RandomAccessFile(fileName, "rw");
            tmp.seek(0);
            tmp.writeInt(maxIndex);
            tmp.writeInt(countIndex);
            if (lengthBuffer > 0) {
                tmp.writeByte(lengthBuffer);
                tmp.writeByte(buffer);
            }
        } catch (IOException e) {
            throw new IOException("Ошибка записи");
        }
        try {
            tmp.close();
        } catch (IOException e) {
            throw new IOException("Ошибка закрытия файла");
        }
        buffer = 0;
        lengthBuffer = 0;
    }

    public int writeBit(boolean bit) throws IOException {
        if (lengthBuffer < 8) {
            buffer <<= 1;
            if (bit) buffer |= 1;
            ++lengthBuffer;
        }
        if (lengthBuffer == 8) {
            try {
                outStream.write(buffer);
            } catch (IOException e) {
                throw new IOException("Ошибка записи");
            }
            buffer = 0;
            lengthBuffer = 0;
        }
        return buffer;
    }

    public void writeByte(int val) throws IOException {
        int s = 8 - lengthBuffer;
        buffer = buffer << s;
        buffer |= val >>> lengthBuffer;
        try {
            outStream.write(buffer);
        }
        catch (IOException e) {
            throw new IOException("Ошибка записи");
        }
        buffer = val << (s + 24) >>> (s + 24);
    }

    public void write2Byte(int val) throws IOException {
        int tmp = val >>> 8;
        writeByte(tmp);
        tmp = val & 0xff;
        writeByte(tmp);
    }
}
