package example.ChronicleQueue;

import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This example demonstrates using Chronicle Queue for high-throughput, low-latency messaging
 * Chronicle Queue is a persisted queue that's memory-mapped for extremely fast performance
 */
class ChronicleQueue {
    private static final int NUM_MESSAGES = 10_000_000;
    private static final String QUEUE_PATH = "chronicle-queue-demo";
    
    public static void main(String[] args) throws Exception {
        // Clean up any previous queue files
        Files.createDirectories(Path.of(QUEUE_PATH));
        
        System.out.println("=== Chronicle Queue Example ===");
        System.out.println("Messages: " + NUM_MESSAGES);
        
        // Run producer and consumer in separate threads
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        // Use AtomicLong to track timing across threads
        AtomicLong producerFinished = new AtomicLong(0);
        
        // Start producer
        executor.submit(() -> {
            try {
                runProducer(NUM_MESSAGES, producerFinished);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // Start consumer
        executor.submit(() -> {
            try {
                // Wait a moment for the producer to start
                Thread.sleep(100);
                runConsumer(NUM_MESSAGES, producerFinished);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // Shutdown gracefully
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
        
        System.out.println("Example completed!");
    }
    
    /**
     * Producer that writes messages to Chronicle Queue
     */
    private static void runProducer(int numMessages, AtomicLong finishedTime) throws Exception {
        // Create or open the queue for writing
        try (SingleChronicleQueue queue = SingleChronicleQueueBuilder
                .single(QUEUE_PATH)
                .build()) {
            
            ExcerptAppender appender = queue.acquireAppender();
            
            System.out.println("Producer started, writing " + numMessages + " messages");
            long startTime = System.nanoTime();
            
            // Write messages as fast as possible
            for (int i = 0; i < numMessages; i++) {
                // Write message in a try-with-resources block to ensure proper release
                try (DocumentContext dc = appender.writingDocument()) {
                    dc.wire().write("message").int32(i);
                    dc.wire().write("timestamp").int64(System.nanoTime());
                }
                
                // Occasionally report progress
                if (i > 0 && i % 1_000_000 == 0) {
                    System.out.println("Produced " + i + " messages");
                }
            }
            
            long endTime = System.nanoTime();
            finishedTime.set(endTime); // Signal to consumer that we're done
            
            double durationMs = (endTime - startTime) / 1_000_000.0;
            double throughput = numMessages * 1000.0 / durationMs;
            
            System.out.printf("Producer finished: %.2f ms, %.2f msg/sec%n", 
                durationMs, throughput);
        }
    }
    
    /**
     * Consumer that reads messages from Chronicle Queue
     */
    private static void runConsumer(int expectedMessages, AtomicLong producerFinished) throws Exception {
        // Create or open the queue for reading
        try (SingleChronicleQueue queue = SingleChronicleQueueBuilder
                .single(QUEUE_PATH)
                .build()) {
            
            ExcerptTailer tailer = queue.createTailer();
            
            System.out.println("Consumer started, expecting " + expectedMessages + " messages");
            long startTime = System.nanoTime();
            
            int count = 0;
            long firstMessageTime = 0;
            long lastMessageTime = 0;
            long maxLatency = 0;
            long totalLatency = 0;
            
            // Read until we've consumed all expected messages
            while (count < expectedMessages) {
                // Try to read a message
                try (DocumentContext dc = tailer.readingDocument()) {
                    if (!dc.isPresent()) {
                        // No message available yet
                        if (producerFinished.get() > 0) {
                            // If producer is done, we might have missed some messages
                            System.out.println("Producer finished but consumer only received " + count + " messages");
                            break;
                        }
                        
                        // Sleep briefly to avoid busy-waiting
                        Thread.sleep(1);
                        continue;
                    }
                    
                    // Extract message data
                    int messageNum = dc.wire().read("message").int32();
                    long messageTime = dc.wire().read("timestamp").int64();
                    
                    // Record timing info
                    long now = System.nanoTime();
                    long latency = now - messageTime;
                    
                    if (count == 0) {
                        firstMessageTime = messageTime;
                    }
                    lastMessageTime = messageTime;
                    maxLatency = Math.max(maxLatency, latency);
                    totalLatency += latency;
                    
                    count++;
                    
                    // Occasionally report progress
                    if (count > 0 && count % 1_000_000 == 0) {
                        System.out.println("Consumed " + count + " messages");
                    }
                }
            }
            
            long endTime = System.nanoTime();
            
            System.out.println("Consumer received " + count + " messages");
            System.out.printf("Consumer throughput: %.2f msg/sec%n", 
                count * 1000.0 / ((endTime - startTime) / 1_000_000.0));
            
            if (count > 0) {
                System.out.printf("Producer throughput: %.2f msg/sec%n", 
                    count * 1000.0 / ((lastMessageTime - firstMessageTime) / 1_000_000.0));
                System.out.printf("Average latency: %.2f µs%n", totalLatency / count / 1000.0);
                System.out.printf("Maximum latency: %.2f µs%n", maxLatency / 1000.0);
            }
        }
    }
}

/**
 * To run this example, you'll need to add the Chronicle Queue dependency to your pom.xml:
 * 
 * <dependency>
 *     <groupId>net.openhft</groupId>
 *     <artifactId>chronicle-queue</artifactId>
 *     <version>5.22.35</version>
 * </dependency>
 * 
 * Chronicle Queue is a commercial product with a free community license.
 * The Chronicle Queue files are memory-mapped, meaning they're accessible
 * directly from memory, which enables extremely low latency.
 */