package Examples;

import java.util.LinkedList;
import java.util.Queue;

// Object Pooling
class StringBuilderPool {
    private final Queue<StringBuilder> pool = new LinkedList<>();
    public static void main(String[] args) {
        var pool = new StringBuilderPool();
        var sb = pool.acquire();
        sb.append("Hello");
        System.out.println(sb);
        pool.release(sb);
    }

    StringBuilder acquire() {
        return pool.poll() != null ? pool.poll() : new StringBuilder();
    }
    
    void release(StringBuilder sb) {
        sb.setLength(0);
        pool.offer(sb);
    }
}