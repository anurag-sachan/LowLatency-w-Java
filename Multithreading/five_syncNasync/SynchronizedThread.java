package five_syncNasync;

class Counter {
    int val=0;

    public int getCount(){
        return val;
    }

    // public void inc(){
    public synchronized void inc(){
        val++;
    }
}

public class SynchronizedThread extends Thread{

    private Counter counter;

    public SynchronizedThread(Counter counter) {
        this.counter= counter;
    }

    public static void main(String[] args) throws InterruptedException {
        Counter counter= new Counter();
        SynchronizedThread t1= new SynchronizedThread(counter);
        SynchronizedThread t2= new SynchronizedThread(counter);
        long startTime=System.nanoTime();

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(counter.getCount());
        long timeTaken=System.nanoTime()-startTime;
        System.out.println("Time Take: "+timeTaken+" nanoseconds");
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            System.out.println(Thread.currentThread().getName()+": "+counter.getCount());
            counter.inc();
        }
    }
}
