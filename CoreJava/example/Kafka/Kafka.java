package example.Kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A practical example of Kafka Producer and Consumer
 * Run this to see a live demonstration of message production and consumption
 */

public class Kafka {
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC = "example-topic";
    private static final String CONSUMER_GROUP = "example-group";

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        // Start producer and consumer in separate threads
        executor.submit(Kafka::runProducer);
        executor.submit(Kafka::runConsumer);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            executor.shutdown();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }

    /**
     * Run a Kafka producer that sends messages every second
     */
    private static void runProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        // Important producer configurations for reliability
        props.put(ProducerConfig.ACKS_CONFIG, "all");               // Wait for all replicas
        props.put(ProducerConfig.RETRIES_CONFIG, 3);                // Retry on temporary failures
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);             // Batch messages
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);  // Prevent duplicates
        
        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            int i = 0;
            Random random = new Random();
            String[] events = {"LOGIN", "LOGOUT", "PURCHASE", "PAGE_VIEW", "ERROR"};
            
            while (!Thread.currentThread().isInterrupted()) {
                String key = "user-" + (random.nextInt(10) + 1);
                String event = events[random.nextInt(events.length)];
                String value = String.format("Event %d: %s at %d", i++, event, System.currentTimeMillis());
                
                // Send record with callback to handle response
                producer.send(new ProducerRecord<>(TOPIC, key, value), (metadata, exception) -> {
                    if (exception != null) {
                        System.err.println("Error producing message: " + exception.getMessage());
                    } else {
                        System.out.printf("Produced: key=%s, value=%s, partition=%d, offset=%d%n", 
                                key, value, metadata.partition(), metadata.offset());
                    }
                });
                
                // Use a lower value in real applications
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Producer exception: " + e.getMessage());
        }
    }

    /**
     * Run a Kafka consumer that processes messages as they arrive
     */
    private static void runConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        
        // Important consumer configurations
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");  // Read from beginning if no offset
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);      // Manual offset commit for better control
        
        try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(TOPIC));
            
            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Consumed: key=%s, value=%s, partition=%d, offset=%d%n",
                            record.key(), record.value(), record.partition(), record.offset());
                    
                    // Process the record here (e.g., update database, trigger actions)
                    // In real applications, you'd handle business logic here
                    
                    // Simulate processing time
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                
                // Manually commit offsets after processing
                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        } catch (Exception e) {
            System.err.println("Consumer exception: " + e.getMessage());
        }
    }
}


// Before running this example, make sure you have Kafka running locally.
 
//  Quick start with Docker:
 
//  docker run -p 2181:2181 -p 9092:9092 \
//      --env ADVERTISED_HOST=localhost \
//      --env ADVERTISED_PORT=9092 \
//      spotify/kafka
 
//  Create the topic:
 
//  docker exec -it <container_id> \
//      /opt/kafka_2.11-0.10.1.0/bin/kafka-topics.sh \
//      --create --zookeeper localhost:2181 \
//      --replication-factor 1 --partitions 3 \
//      --topic example-topic
 