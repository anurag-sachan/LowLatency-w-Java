package six_Locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockExample {
    public static void main(String[] args) {
        BankAcc bank= new BankAcc();

        Runnable task= new Runnable() {
            @Override
            public void run() {
                try {
                    bank.withdraw(50);
                } catch (InterruptedException e) {
                }
            }
        };
        Thread t1= new Thread(task, "pp1");
        Thread t2= new Thread(task, "pp2");
        t1.start(); t2.start();
    }
}

class BankAcc{

    int balance=200;

    Lock lock=new ReentrantLock();

    public void withdraw(int i) throws InterruptedException {
    // public synchronized void withdraw(int i) throws InterruptedException {
        System.out.println("Thread name: "+Thread.currentThread().getName());
        // lock.lock(); //same as synchronized
        // lock.tryLock() //non blocking without timeout
        if(lock.tryLock(1000, TimeUnit.MILLISECONDS)){
            if(i<balance){
                System.out.printf("withdrew %d: %s\n",i,Thread.currentThread().getName());
                balance=balance-i;
                Thread.sleep(500);
                System.out.println("balance: "+balance);
            }else System.out.println("insufficient balance: "+balance);
            lock.unlock();
        }else System.out.println("could not acquire Lock");
    } 
}
