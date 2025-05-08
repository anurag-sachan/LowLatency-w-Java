package eight_readWriteLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class readWriteLock {

    ReadWriteLock rwl= new ReentrantReadWriteLock();
    Lock readLock= rwl.readLock();
    Lock writeLock= rwl.writeLock();

    int val=0;

    public int getVal(){
        readLock.lock(); //multiple threads can acquire this lock unlike lock.lock() where other thread wait.
        try{
            return val;
        }finally{
            readLock.unlock();
        }
    }

    public void incVal() throws InterruptedException{
        writeLock.lock(); //threads can't read while writing. 
        val++;
        // Thread.sleep(100); //demo-purpose
        writeLock.unlock();
    }

    public static void main(String[] args) throws InterruptedException {
        readWriteLock rwLock = new readWriteLock();
        Runnable rTask= new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                    System.out.println(Thread.currentThread().getName()+" read: "+rwLock.getVal());
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };

        Runnable wTask= new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        rwLock.incVal();
                        System.out.println(Thread.currentThread().getName()+" incremented.");
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };

        // writer Thread is always given preference
        Thread rt= new Thread(rTask, "read-thread");
        Thread wt= new Thread(wTask, "write-thread");
        Thread rt2= new Thread(rTask, "read-thread2");

        rt.start();
        wt.start();
        rt2.start();

        rt.join();
        wt.join();
        rt2.join();

        System.out.println("count: "+rwLock.val);
    }
}
