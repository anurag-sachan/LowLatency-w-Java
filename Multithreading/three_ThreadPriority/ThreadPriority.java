package three_ThreadPriority;

public class ThreadPriority {
    public static void main(String[] args) {
        MyThread lp=new MyThread("LP");
        MyThread mp=new MyThread("MP");
        MyThread hp=new MyThread("HP");
        lp.setPriority(Thread.MIN_PRIORITY);
        mp.setPriority(Thread.NORM_PRIORITY);
        hp.setPriority(Thread.MAX_PRIORITY);
        lp.start();
        mp.start();
        hp.start();
    }
}

class MyThread extends Thread{
    public MyThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        int i=25;
        while(i>1){
            i--;
            System.out.println("Thread name: "+Thread.currentThread().getPriority());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
