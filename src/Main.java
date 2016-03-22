import File.*;
import MyString.MyString;
import Dictionary.*;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

/**
 * Created by Sir-S on 22.02.2016.
 */

public class Main {
    static void encode(String inFileName, String outFileName) {
        System.out.println("COMPRESSION");

        long t1 = System.currentTimeMillis();

        final int maxIndex = 65535;
        DictionaryForEncoding d = new DictionaryForEncoding(1024);

        OutputFile out = new OutputFile();
        BufferedInputStream in;

        try {
            out.open(outFileName);
            in = new BufferedInputStream(new FileInputStream(inFileName));

            int val = in.read();
            int countBytes = 1, countIndex = 1;
            int index = 255;
            int parent = val;
            int sizeFile = in.available();

            while ((val = in.read()) != -1) {
                if (sizeFile >= 20 && countBytes % ((sizeFile / 20)) == 0) {
                    System.out.print("Progress: " + ((long)countBytes * 100 / sizeFile + 1) + "% ");
                    System.out.println("(Time: " + (float) (System.currentTimeMillis() - t1) / 1000 + " s)");
                }

                ++countBytes;

                int current = d.find(parent, val);
                if (current == -1) {
                    ++countIndex;
                    if (index < maxIndex)
                        d.push(parent, val, ++index);

                    int parentIndex = d.getIndex(parent);

                    boolean bit = (parentIndex >> 8) > 0;
                    out.writeBit(bit);
                    if (bit)
                        out.write2Byte(parentIndex);
                    else out.writeByte(parentIndex);

                    parent = val;
                } else parent = current;
            }

            int parentIndex = d.getIndex(parent);

            boolean bit = (parentIndex >> 8) > 0;
            out.writeBit(bit);
            if (bit)
                out.write2Byte(parentIndex);
            else out.writeByte(parentIndex);

            in.close();
            out.close(index, countIndex);
        } catch (FileNotFoundException e) {
            System.out.println(inFileName + " не найден");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    static void decode(String inFileName, String outFileName) {
        System.out.println("DECOMPRESSION");

        long t1 = System.currentTimeMillis();

        InputFile in = new InputFile();
        BufferedOutputStream out;

        try {
            out = new BufferedOutputStream(new FileOutputStream(outFileName), 8 * 1024);
            int[] data = in.open(inFileName);

            int maxIndex = data[0], countIndex = data[1];
            DictionaryForDecoding d = new DictionaryForDecoding((int) (Math.sqrt(maxIndex)) + 1);

            int current = in.getIndex();
            int index = 255;

            MyString stack = new MyString(100);

            out.write(current);
            int count = 1;
            int previous;

            while (true) {
                if (countIndex >= 20 && count % ((countIndex / 20)) == 0) {
                    System.out.print("Progress: " + ((long) count * 100 / countIndex + 1) + "% ");
                    System.out.println("(Time: " + (float) (System.currentTimeMillis() - t1) / 1000 + " s)");
                }

                previous = current;
                current = in.getIndex();

                if (current < 0) break;

                ++count;
                if (index >= current) {
                    d.find(current, stack);
                    stack.reversePrint(out);
                    if (index < maxIndex) d.push(stack.getLast(), ++index, previous);
                } else {
                    d.find(previous, stack);
                    stack.reversePrint(out);
                    out.write(stack.getLast());
                    if (index < maxIndex) d.push(stack.getLast(), ++index, previous);
                }
            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println(outFileName + " не найден");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public static void help() {
        System.out.println("Пример: ");
        System.out.println("-c in.txt out.txt.lzw");
        System.out.println("Список параметров: ");
        System.out.println("   -c - архивировать файл");
        System.out.println("   -d - разархивировать файл");
    }

    public static void main(String args[]) {
        if (args.length == 3) {
            if (args[0].compareTo("-c") == 0)
                encode(args[1], args[2]);
            else if (args[0].compareTo("-d") == 0)
                decode(args[1], args[2]);
            else {
                System.out.println("Неправильные аргументы");
                help();
            }
        }
        else {
            if (args.length == 0) {
                System.out.println("Неправильные аргументы");
                help();
            }
            else if (args[0].compareTo("/?") == 0 || args[0].compareTo("help") == 0) {
                help();
            }
            else {
                System.out.println("Неправильные аргументы");
                help();
            }
        }
    }
}
