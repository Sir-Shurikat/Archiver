package Dictionary;

/**
 * Created by Sir-S on 06.03.2016.
 */

public class DictionaryForEncoding extends Dictionary{
    public DictionaryForEncoding(int size) {
        super(size);
    }

    public int find(int parent, int current) {
        int hash = ((parent << 8)^(current)) % (size * size - 1);
        if (hash < 256) hash = 256;
        while (data[hash / size][hash % size].parent != parent || data[hash / size][hash % size].b != current){
            if (data[hash / size][hash % size].parent == -1)
                return -1;
            else ++hash;
            if (hash > (size * size - 1)) hash = 256;
        }
        return hash;
    }

    public int push(int parent, int current, int index) {
        int hash = ((parent << 8) ^ (current)) % (size * size - 1);
        if (hash < 256) hash = 256;

        while (data[hash / size][hash % size].parent != -1){
            ++hash;
            if (hash > (size * size - 1)) hash = 256;
        }

        data[hash / size][hash % size].index = index;
        data[hash / size][hash % size].parent = parent;
        data[hash / size][hash % size].b = current;
        return hash;
    }
}
