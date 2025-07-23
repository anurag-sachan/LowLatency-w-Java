package example;

// Redis cache usage
import redis.clients.jedis.Jedis;
class RedisCache {
    public static void main(String[] args) {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.set("user:1", "Anurag");
            System.out.println("Fetched from Redis: " + jedis.get("user:1"));
        }
    }
}