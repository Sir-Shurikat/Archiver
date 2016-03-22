package Dictionary;

/**
 * Created by Sir-S on 22.02.2016.
 */

class Node {
    int b;
    int index;
    int parent;
    Node(int b, int index, int parent) {
        this.b = b;
        this.index = index;
        this.parent = parent;
    }
}

public abstract class Dictionary {
    protected Node[][] data;
    protected int size;

    public int getIndex(int pos) {
        return data[pos / size][pos % size].index;
    }

    protected Dictionary(int size) {
        this.size = size;
        data = new Node[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                data[i][j] = new Node(0, -1, -1);
            }
        }
        for (int i = 0; i < 256; ++i) {
            data[i / size][i % size].b = i;
            data[i / size][i % size].index = i;
        }
    }
}
