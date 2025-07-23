package example;
import jdk.incubator.foreign.*;

// Foreign Memory Access API (Preview)
// Requires: --enable-preview --add-modules jdk.incubator.foreign
// Java22+
public class ForeignMemory {
    public static void main(String[] args) {
        // Allocate native memory using Arena (auto-closeable scope)
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment segment = arena.allocate(ValueLayout.JAVA_INT); // 4 bytes

            // Write to memory
            segment.set(ValueLayout.JAVA_INT, 0, 123);

            // Read from memory
            int value = segment.get(ValueLayout.JAVA_INT, 0);
            System.out.println("Value from native memory: " + value);
        }
    }
}
