package four_DeamonThread;

public class DeamonThread extends Thread{
    public DeamonThread(String name) {
        super(name);
    }
    @Override
    public void run() {
        while(true){
            if(Thread.currentThread().getName().equals("t")) System.out.println("NT!");
            if(Thread.currentThread().getName().equals("dt")) System.out.println("DT!");
        }
    }
    public static void main(String[] args) {
        DeamonThread dt= new DeamonThread("dt");
        dt.setDaemon(true);

        DeamonThread t= new DeamonThread("t");
        t.start();
        dt.start();
        System.out.println("main done");
    }
}
