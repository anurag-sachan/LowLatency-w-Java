package eleven_ExecutorFramework;

import java.util.concurrent.atomic.*;;

public class AtomicVar {
    public static void main(String[] args) throws InterruptedException {
        Counter counter=new Counter();

        Thread t1=new Thread(()->{
            for (int i = 0; i < 1000; i++) {
                counter.inc();
            }
        });
        Thread t2=new Thread(()->{
            for (int i = 0; i < 1000; i++) {
                counter.inc();
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(counter.getVal());
    }
}

class Counter{
    AtomicInteger val= new AtomicInteger(0);

    void inc(){
        val.incrementAndGet();
    }

    int getVal(){
        return val.get();
    }
}
