import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LRUCache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> entries;
    private final Node<K, V> leastRecentlyUsed;
    private final Node<K, V> mostRecentlyUsed;

    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }

        this.capacity = capacity;
        this.entries = new HashMap<>();
        this.leastRecentlyUsed = new Node<>(null, null);
        this.mostRecentlyUsed = new Node<>(null, null);
        leastRecentlyUsed.next = mostRecentlyUsed;
        mostRecentlyUsed.previous = leastRecentlyUsed;
    }

    public V get(K key) {
        Node<K, V> node = entries.get(Objects.requireNonNull(key, "key"));
        if (node == null) {
            return null;
        }

        moveToMostRecentlyUsed(node);
        return node.value;
    }

    public void put(K key, V value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");

        Node<K, V> node = entries.get(key);
        if (node != null) {
            node.value = value;
            moveToMostRecentlyUsed(node);
            return;
        }

        node = new Node<>(key, value);
        entries.put(key, node);
        addBeforeMostRecentlyUsed(node);

        if (entries.size() > capacity) {
            Node<K, V> evicted = leastRecentlyUsed.next;
            unlink(evicted);
            entries.remove(evicted.key);
        }
    }

    public int size() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    private void moveToMostRecentlyUsed(Node<K, V> node) {
        unlink(node);
        addBeforeMostRecentlyUsed(node);
    }

    private void addBeforeMostRecentlyUsed(Node<K, V> node) {
        node.previous = mostRecentlyUsed.previous;
        node.next = mostRecentlyUsed;
        mostRecentlyUsed.previous.next = node;
        mostRecentlyUsed.previous = node;
    }

    private void unlink(Node<K, V> node) {
        node.previous.next = node.next;
        node.next.previous = node.previous;
    }

    private static final class Node<K, V> {
        private final K key;
        private V value;
        private Node<K, V> previous;
        private Node<K, V> next;

        private Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
