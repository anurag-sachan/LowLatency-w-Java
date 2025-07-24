package example.LowGC;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

/**
 * This example demonstrates various techniques for reducing garbage collection overhead
 * and achieving high performance in Java applications.
 */
class LowGCHighPerformance {

    /**
     * Main method to run various examples
     */
    public static void main(String[] args) throws Exception {
        // Example 1: Using on-heap vs off-heap memory
        compareHeapVsOffHeap();
        
        // Example 2: Object pooling to reduce allocations
        demonstrateObjectPooling();
        
        // Example 3: Reusing objects to reduce allocations
        demonstrateObjectReuse();
        
        // Example 4: String handling to reduce GC pressure
        demonstrateStringHandling();
        
        // Example 5: Measure performance with different approaches
        measurePerformance();
    }
    
    /**
     * Compare on-heap vs off-heap memory performance
     */
    private static void compareHeapVsOffHeap() {
        System.out.println("==== On-Heap vs Off-Heap Memory ====");
        
        // On-heap allocation (managed by JVM GC)
        long startHeap = System.nanoTime();
        byte[] onHeapArray = new byte[100 * 1024 * 1024]; // 100MB on-heap array
        for (int i = 0; i < 1000000; i++) {
            onHeapArray[i % onHeapArray.length] = (byte) i;
        }
        long heapTime = System.nanoTime() - startHeap;
        
        // Off-heap allocation (direct memory, not managed by JVM GC)
        long startOffHeap = System.nanoTime();
        ByteBuffer offHeapBuffer = ByteBuffer.allocateDirect(100 * 1024 * 1024); // 100MB off-heap buffer
        for (int i = 0; i < 1000000; i++) {
            offHeapBuffer.put(i % offHeapBuffer.capacity(), (byte) i);
        }
        long offHeapTime = System.nanoTime() - startOffHeap;
        
        System.out.printf("On-heap time: %.2f ms%n", heapTime / 1_000_000.0);
        System.out.printf("Off-heap time: %.2f ms%n", offHeapTime / 1_000_000.0);
        System.out.println("Off-heap memory is not subject to GC pauses but requires manual management");
        System.out.println();
        
        // Don't forget to clean up off-heap memory
        // In a real application, you would use try-with-resources or other cleanup mechanisms
        // DirectByteBuffer has a PhantomReference that eventually frees the memory
    }
    
    /**
     * Demonstrate object pooling to reduce allocations
     */
    private static void demonstrateObjectPooling() {
        System.out.println("==== Object Pooling ====");
        
        // Create a simple object pool
        ObjectPool<ExpensiveObject> pool = new ObjectPool<>(
                () -> new ExpensiveObject("Pooled object"), 10);
        
        // Using objects without pooling (creates garbage)
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            ExpensiveObject obj = new ExpensiveObject("New object " + i);
            obj.doWork();
            // Object becomes garbage after this loop iteration
        }
        long withoutPoolTime = System.nanoTime() - startTime;
        
        // Using objects with pooling (reuses objects)
        startTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            ExpensiveObject obj = pool.borrow();
            obj.doWork();
            pool.returnToPool(obj);
            // Object is returned to pool, not garbage
        }
        long withPoolTime = System.nanoTime() - startTime;
        
        System.out.printf("Without pooling: %.2f ms%n", withoutPoolTime / 1_000_000.0);
        System.out.printf("With pooling: %.2f ms%n", withPoolTime / 1_000_000.0);
        System.out.println("Object pooling reduces allocations and GC pressure");
        System.out.println();
    }
    
    /**
     * Demonstrate object reuse to reduce allocations
     */
    private static void demonstrateObjectReuse() {
        System.out.println("==== Object Reuse ====");
        
        // Creating new objects every time
        long startTime = System.nanoTime();
        int sum = 0;
        for (int i = 0; i < 1000000; i++) {
            Point p = new Point(i, i);
            sum += p.getX() + p.getY();
        }
        long newObjectsTime = System.nanoTime() - startTime;
        
        // Reusing a single object
        startTime = System.nanoTime();
        sum = 0;
        Point reusedPoint = new Point(0, 0);
        for (int i = 0; i < 1000000; i++) {
            reusedPoint.setX(i);
            reusedPoint.setY(i);
            sum += reusedPoint.getX() + reusedPoint.getY();
        }
        long reuseTime = System.nanoTime() - startTime;
        
        System.out.printf("Creating new objects: %.2f ms%n", newObjectsTime / 1_000_000.0);
        System.out.printf("Reusing objects: %.2f ms%n", reuseTime / 1_000_000.0);
        System.out.println("Object reuse minimizes allocations and GC overhead");
        System.out.println();
    }
    
    /**
     * Demonstrate string handling techniques to reduce GC pressure
     */
    private static void demonstrateStringHandling() {
        System.out.println("==== String Handling ====");
        
        // Using string concatenation (creates many temporary strings)
        long startTime = System.nanoTime();
        String result = "";
        for (int i = 0; i < 10000; i++) {
            result += i + ",";
        }
        long concatTime = System.nanoTime() - startTime;
        
        // Using StringBuilder (reuses the same buffer)
        startTime = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append(i).append(",");
        }
        result = sb.toString();
        long builderTime = System.nanoTime() - startTime;
        
        // Pre-sizing StringBuilder for even better performance
        startTime = System.nanoTime();
        StringBuilder presizedSb = new StringBuilder(60000); // Pre-allocate capacity
        for (int i = 0; i < 10000; i++) {
            presizedSb.append(i).append(",");
        }
        result = presizedSb.toString();
        long presizedBuilderTime = System.nanoTime() - startTime;
        
        System.out.printf("String concatenation: %.2f ms%n", concatTime / 1_000_000.0);
        System.out.printf("StringBuilder: %.2f ms%n", builderTime / 1_000_000.0);
        System.out.printf("Pre-sized StringBuilder: %.2f ms%n", presizedBuilderTime / 1_000_000.0);
        System.out.println("Proper string handling drastically reduces temporary object creation");
        System.out.println();
    }
    
    /**
     * Measure performance of different approaches
     */
    private static void measurePerformance() {
        System.out.println("==== Performance Measurement ====");
        
        // Example of a high-performance calculation using primitives
        long startTime = System.nanoTime();
        double sum = 0.0;
        for (int i = 0; i < 10_000_000; i++) {
            sum += Math.sqrt(i * 0.01);
        }
        long primitiveTime = System.nanoTime() - startTime;
        
        // Same calculation using boxed types (creates objects)
        startTime = System.nanoTime();
        Double boxedSum = 0.0;
        for (Integer i = 0; i < 10_000_000; i++) {
            boxedSum += Math.sqrt(i * 0.01);
        }
        long boxedTime = System.nanoTime() - startTime;
        
        System.out.printf("Primitive calculation: %.2f ms%n", primitiveTime / 1_000_000.0);
        System.out.printf("Boxed calculation: %.2f ms%n", boxedTime / 1_000_000.0);
        System.out.println("Using primitives avoids boxing/unboxing and object creation");
        System.out.println();
        
        // Demonstrate cache line padding to avoid false sharing in concurrent code
        System.out.println("Cache line padding can help avoid false sharing in multi-threaded code");
        System.out.println("This is an advanced technique used in high-performance libraries");
    }
    
    /**
     * A simple object pool implementation
     */
    static class ObjectPool<T> {
        private final T[] pool;
        private final boolean[] inUse;
        private final Supplier<T> factory;
        
        @SuppressWarnings("unchecked")
        public ObjectPool(Supplier<T> factory, int size) {
            this.factory = factory;
            this.pool = (T[]) new Object[size];
            this.inUse = new boolean[size];
            
            // Pre-populate the pool
            for (int i = 0; i < size; i++) {
                pool[i] = factory.get();
            }
        }
        
        public T borrow() {
            // Find an available object
            for (int i = 0; i < pool.length; i++) {
                if (!inUse[i]) {
                    inUse[i] = true;
                    return pool[i];
                }
            }
            
            // If no objects available, create a new one
            // In a real pool, you might block or grow the pool
            return factory.get();
        }
        
        public void returnToPool(T obj) {
            // Find the object in the pool
            for (int i = 0; i < pool.length; i++) {
                if (pool[i] == obj) {
                    inUse[i] = false;
                    return;
                }
            }
            // If object wasn't from the pool, ignore
        }
    }
    
    /**
     * A simple mutable point class for the object reuse demo
     */
    static class Point {
        private int x;
        private int y;
        
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getX() { return x; }
        public int getY() { return y; }
        public void setX(int x) { this.x = x; }
        public void setY(int y) { this.y = y; }
    }
    
    /**
     * Example class for object pooling demo
     */
    static class ExpensiveObject {
        private final String name;
        private final byte[] data;
        
        public ExpensiveObject(String name) {
            this.name = name;
            // Allocate some memory to make the object "expensive"
            this.data = new byte[1024];
        }
        
        public void doWork() {
            // Simulate some work
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (i % 256);
            }
        }
    }
}