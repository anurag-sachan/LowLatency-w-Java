package example.LMAXDisruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadFactory;

class LMAXDisruptor {
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