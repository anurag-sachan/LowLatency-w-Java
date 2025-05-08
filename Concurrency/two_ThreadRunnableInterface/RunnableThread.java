package two_ThreadRunnableInterface;

public class RunnableThread {
    public static void main(String[] args) throws InterruptedException {
        Test test= new Test();
        Thread thread= new Thread(test);
        System.out.println(thread.getState());
        thread.start();
        System.out.println(thread.getState());
        int i=10;
        while (i>8) {
            System.out.println("main");
            Thread.sleep(100);
            System.out.println(thread.getState());
            i--;
        }
        thread.join();
        System.out.println(thread.getState());
    }   
}

class Test implements Runnable{
    @Override
    public void run() {
        int i=10;
        while (i>7){
            System.out.println("test");
            i--;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}