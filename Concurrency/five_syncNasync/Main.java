package five_syncNasync;

public class Main {
    public static void main(String[] args) {
        long startTime = System.nanoTime();  // Use nanoTime for better precision
        int c = 0;
        // while(i<2000){
        //     i++;
        // }
        for (int i = 0; i < 2000; i++) {
            c++;
        }
        long timeTaken = System.nanoTime() - startTime;  // Calculate time in nanoseconds
        System.out.println(c);
        System.out.println("Time taken: " + timeTaken + " nanoseconds");
    }
}

