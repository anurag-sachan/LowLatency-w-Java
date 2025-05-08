package s7vn_nestedLocksVsSync;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockVsSynchronized {
    public static void main(String[] args) {
        NestedLocks locks= new NestedLocks();
        Runnable task= new Runnable(){
            @Override
            public void run() {
                locks.OuterLock();
            }
        };

        Thread th1=new Thread(task, "th1");
        Thread th2=new Thread(task, "th2");
        th1.start(); th2.start();
    }
}

class NestedLocks {
    Lock lock= new ReentrantLock();
    
    public void OuterLock(){
        lock.lock();
        System.out.println("thread: "+Thread.currentThread().getName());
        System.out.println("Outer Lock");
        InnerLock();
        lock.unlock();
    }
    
    private void InnerLock() {
        lock.lock();
        System.out.println("thread: "+Thread.currentThread().getName());
        System.out.println("Inner Lock");
        lock.unlock();
        System.out.println();
    }
}
