package example.Kafka.MessageDrivenArch;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

class MDA {
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC_NAME = "example-topic";
    private static final String CONSUMER_GROUP_ID = "example-group";
    
    public static void main(String[] args) {
        // Create producer and consumer threads
        Thread producerThread = new Thread(() -> runProducer());
        Thread consumerThread = new Thread(() -> runConsumer());
        
        // Start the threads
        producerThread.start();
        consumerThread.start();
        
        // Wait for them to finish
        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private static void runProducer() {
        // Configure producer properties
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        
        // Create the producer
        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            // Send 10 messages
            for (int i = 0; i < 10; i++) {
                String key = "key-" + i;
                String value = "Message #" + i;
                
                // Create producer record
                ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, key, value);
                
                // Send the record and get future
                Future<RecordMetadata> future = producer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        if (exception != null) {
                            System.err.println("Error sending message: " + exception.getMessage());
                        } else {
                            System.out.printf("Message sent to partition %d, offset %d%n", 
                                    metadata.partition(), metadata.offset());
                        }
                    }
                });
                
                // Synchronously wait for result (not recommended in production)
                try {
                    RecordMetadata metadata = future.get();
                    System.out.printf("Sent: %s, to partition %d with offset %d%n", 
                            value, metadata.partition(), metadata.offset());
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println("Error waiting for send result: " + e.getMessage());
                    if (e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                }
                
                // Sleep a bit between sends
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    private static void runConsumer() {
        // Configure consumer properties
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        
        // Create the consumer
        try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
            // Subscribe to the topic
            consumer.subscribe(Collections.singletonList(TOPIC_NAME));
            
            // Poll for records
            int emptyPolls = 0;
            while (emptyPolls < 5) { // Exit after 5 empty polls
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                
                if (records.isEmpty()) {
                    emptyPolls++;
                    continue;
                }
                
                emptyPolls = 0; // Reset count when we get records
                
                // Process the records
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Received: key=%s, value=%s, partition=%d, offset=%d%n",
                            record.key(), record.value(), record.partition(), record.offset());
                }
                
                // Manually commit offsets
                consumer.commitSync();
            }
        }
    }
}