



public class ThreadSafeLruCache<K, V> {
    private final int capacity;
    private final LinkedHashMap<K, V> map;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ThreadSafeLruCache(int capacity) {
        this.capacity = capacity;
        this.map = new LinkedHashMap<K,V>(capacity, 0.75f, true) {

            @Override protected boolean removeEldestEntry(Map.Entry<K, V> e) {
                return size() > ThreadSafeLruCache.this.capacity;
            }
        }

        public V get(K key) {
            lock.writeLock().lock(); // access order reorders the map, so we require a write lock
            try {
                return map.get(key);
            } finally {
                lock.writeLock().unlock();
            }
        }

        public void set(K key, V value) {
            lock.writeLock().lock();
            try {
                this.map.put(key, value);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
}