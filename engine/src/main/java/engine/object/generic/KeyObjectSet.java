package engine.object.generic;

public class KeyObjectSet<K, T> {

    public K key;
    public T object;

    public KeyObjectSet(K key, T object) {
        this.key = key;
        this.object = object;
    }
}
