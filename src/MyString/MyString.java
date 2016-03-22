package MyString;

import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * Created by Sir-S on 22.02.2016.
 */

public class MyString {
    private int[] str;
    public int len;
    private int size;

    public MyString(int size) {
        str = new int[size];
        len = 0;
        this.size = size;
    }

    public void pushBack(int c) {
        if (len + 1 == size) {
            int[] newStr = new int[size * 2];
            size *= 2;
            System.arraycopy(str, 0, newStr, 0, len);
            str = newStr;
        }
        str[len++] = c;
    }

    public void reversePrint(BufferedOutputStream out) throws IOException {
        try {
            for (int i = len - 1; i >= 0; --i)
                out.write(str[i]);
        } catch (IOException e) {
            throw new IOException("Ошибка записи");
        }

    }

    public int getLast() {
        return str[len - 1];
    }
}