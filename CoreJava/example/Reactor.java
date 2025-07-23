package example;

// 13. ZGC and G1 GC
// Run with: -XX:+UseZGC or -XX:+UseG1GC and observe GC behavior using logs

// 14. Flight Recorder (JFR)
// Run with: java -XX:StartFlightRecording=filename=recording.jfr,duration=10s YourApp

// Reactive Streams using Reactor
// Requires Maven dependency: io.projectreactor:reactor-core
import reactor.core.publisher.Flux;
class ReactorExample {
    public static void main(String[] args) {
        Flux.range(1, 5).map(i -> i * 10).subscribe(System.out::println);
    }
}