package Dictionary;

import MyString.MyString;

/**
 * Created by Sir-S on 06.03.2016.
 */

public class DictionaryForDecoding extends Dictionary {
    public DictionaryForDecoding(int size) {
        super(size);
    }

    public boolean find(int index, MyString stack){
        stack.len = 0;
        Node tmp = data[index / size][index % size];
        if (tmp.index == -1) return false;
        stack.pushBack(tmp.b);
        while (tmp.parent != -1) {
            tmp = data[tmp.parent / size][tmp.parent % size];
            stack.pushBack(tmp.b);
        }
        return true;
    }

    public void push(int b, int index, int previous) {
        Node tmp = data[index / size][index % size];
        tmp.b = b;
        tmp.index = index;
        tmp.parent = previous;
    }
}
