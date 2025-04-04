package eleven_ExecutorFramework;

import java.util.concurrent.*;;

public class Futures {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executor=Executors.newFixedThreadPool(2);
        Future<?> future= executor.submit(()-> System.out.println("Hello"));
        System.out.println(future.get()); //blocking call
        if(future.isDone()) System.out.println("Task is Done!");
        executor.shutdown();
    }
}
