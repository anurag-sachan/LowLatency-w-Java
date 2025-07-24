package example.LowGC;

import java.util.concurrent.locks.LockSupport;
import java.nio.ByteBuffer;
import sun.misc.Unsafe;
import java.lang.reflect.Field;

class LowLatency {
    // Get access to Unsafe for direct memory operations
    private static final Unsafe unsafe;
    
    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    // 2. Busy Spin Waiting (instead of Object.wait() which causes context switch)
    public static void busySpinWaitForCondition(final Condition condition) {
        while (!condition.isTrue()) {
            // CPU hint for spin-wait loop
            Thread.onSpinWait();
        }
    }
    
    public interface Condition {
        boolean isTrue();
    }
    
    // 3. Pause Spinning with LockSupport.parkNanos
    public static void pauseSpinWaitForCondition(final Condition condition, long nanosPause) {
        while (!condition.isTrue()) {
            LockSupport.parkNanos(nanosPause);
        }
    }
    
    // 4. Direct ByteBuffer for zero-copy
    public static ByteBuffer createDirectBuffer(int capacity) {
        return ByteBuffer.allocateDirect(capacity);
    }
    
    // 5. Unsafe direct memory access
    public static long allocateMemory(long bytes) {
        return unsafe.allocateMemory(bytes);
    }
    
    public static void freeMemory(long address) {
        unsafe.freeMemory(address);
    }
    
    public static void putLongAtAddress(long address, long value) {
        unsafe.putLong(address, value);
    }
    
    public static long getLongAtAddress(long address) {
        return unsafe.getLong(address);
    }
    
    // 6. Thread affinity (binding thread to CPU core)
    // Note: This requires external libraries like OpenHFT's Java-Thread-Affinity
    public static void setThreadAffinity(int cpuId) {
        // This is just pseudocode - you'd need a library to do this
        // For example: AffinityLock.acquireLock(cpuId);
        System.out.println("Setting thread affinity to CPU " + cpuId);
    }
    
    // Main method with examples
    public static void main(String[] args) {
        
        // 2. Busy Spin Waiting Example
        final long startTime = System.currentTimeMillis();
        final long waitUntil = startTime + 100; // 100ms wait
        
        Condition timeCondition = () -> System.currentTimeMillis() >= waitUntil;
        
        busySpinWaitForCondition(timeCondition);
        System.out.println("Busy spin wait completed after: " + 
                          (System.currentTimeMillis() - startTime) + "ms");
        
        // 3. Direct ByteBuffer Example
        ByteBuffer directBuffer = createDirectBuffer(1024);
        directBuffer.putLong(0, 123456789L);
        System.out.println("Value from direct buffer: " + directBuffer.getLong(0));
        
        // 4. Unsafe Memory Example
        long memAddress = allocateMemory(8); // Allocate 8 bytes
        putLongAtAddress(memAddress, 987654321L);
        System.out.println("Value from unsafe memory: " + getLongAtAddress(memAddress));
        freeMemory(memAddress);
        
        // 5. Thread Affinity Example (pseudo code)
        setThreadAffinity(1); // Try to bind to CPU core 1
    }
}