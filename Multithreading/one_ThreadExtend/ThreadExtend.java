package one_ThreadExtend;

public class ThreadExtend{
    public static void main(String[] args) throws InterruptedException {
        Test test=new Test();
        test.start();
        while(true){
            System.out.println("main");
            Thread.sleep(100);
            Thread.yield();
        } 
    }
}

class Test extends Thread{
    @Override
    public void run() {
        while (true){
            System.out.println("test");
            try {
                Thread.sleep(100);
                Thread.yield();
            } catch (InterruptedException e) {
            }
        }
    }
}