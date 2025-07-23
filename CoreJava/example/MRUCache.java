package example;

// In-memory cache with MRU eviction
import java.util.*;
class MRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;
    public MRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
    public static void main(String[] args) {
        var cache = new MRUCache<String, String>(3);
        cache.put("a", "apple");
        cache.put("b", "banana");
        cache.put("c", "cherry");
        cache.get("a"); // MRU
        cache.put("d", "date"); // evicts b
        System.out.println(cache);
    }
}