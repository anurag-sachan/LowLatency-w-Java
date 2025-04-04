package eleven_ExecutorFramework;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;;

public class ExecutorFramework {
    public static void main(String[] args) {
        long startTime=System.nanoTime();
        AtomicInteger val= new AtomicInteger(0);
        ExecutorService executor=Executors.newFixedThreadPool(2);

        Runnable task = () ->{
            while (val.get()<2000) {
                int currentVal= val.incrementAndGet();
                // System.out.println("current val: "+currentVal);
            }
        };

        executor.submit(task);
        executor.submit(task);

        System.out.println("time taken: "+(System.nanoTime()-startTime));
        executor.shutdown();
    }
}
