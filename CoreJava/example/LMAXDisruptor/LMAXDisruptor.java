package example.LMAXDisruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadFactory;

public class LMAXDisruptor {
    // Event to pass through the disruptor
    public static class ValueEvent {
        private long value;
        
        public long getValue() {
            return value;
        }
        
        public void setValue(long value) {
            this.value = value;
        }
    }
    
    // Factory for creating ValueEvents
    public static class ValueEventFactory implements EventFactory<ValueEvent> {
        @Override
        public ValueEvent newInstance() {
            return new ValueEvent();
        }
    }
    
    // Event handler
    public static class ValueEventHandler implements EventHandler<ValueEvent> {
        private long sum = 0;
        
        @Override
        public void onEvent(ValueEvent event, long sequence, boolean endOfBatch) {
            sum += event.getValue();
        }
        
        public long getSum() {
            return sum;
        }
    }
    
    public static void main(String[] args) throws Exception {
        // The factory for the events
        ValueEventFactory factory = new ValueEventFactory();
        
        // Ring buffer size, must be power of 2
        int bufferSize = 1024;
        
        // Create the disruptor
        ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;
        
        Disruptor<ValueEvent> disruptor = new Disruptor<>(
                factory,
                bufferSize,
                threadFactory,
                ProducerType.SINGLE,
                new YieldingWaitStrategy());
        
        // Connect the handler
        ValueEventHandler handler = new ValueEventHandler();
        disruptor.handleEventsWith(handler);
        
        // Start the disruptor, starts all threads
        disruptor.start();
        
        // Get the ring buffer from the disruptor to be used for publishing
        RingBuffer<ValueEvent> ringBuffer = disruptor.getRingBuffer();
        
        ByteBuffer bb = ByteBuffer.allocate(8);
        
        // Publish events
        long startTime = System.nanoTime();
        for (long l = 0; l < 1_000_000; l++) {
            bb.putLong(0, l);
            ringBuffer.publishEvent((event, sequence, buffer) -> event.setValue(buffer.getLong(0)), bb);
        }
        
        // Wait until all events are processed
        disruptor.shutdown();
        
        long endTime = System.nanoTime();
        
        System.out.printf("Processing time: %.3f ms%n", (endTime - startTime) / 1_000_000.0);
        System.out.println("Sum: " + handler.getSum());
    }
}