package example;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MemoryMappedFile {
    private static final String FILE_NAME = "mmapped_file.dat";
    private static final int FILE_SIZE = 1024 * 1024; // 1MB

    public static void main(String[] args) {
        try {
            // Create the memory mapped file
            File file = new File(FILE_NAME);
            
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                // Set the file size
                raf.setLength(FILE_SIZE);
                
                // Get the file channel
                FileChannel fileChannel = raf.getChannel();
                
                // Map the file into memory
                MappedByteBuffer buffer = fileChannel.map(
                        FileChannel.MapMode.READ_WRITE, 0, FILE_SIZE);
                
                System.out.println("Memory mapped file created successfully");
                
                // Write data to the memory mapped file
                long startTime = System.nanoTime();
                
                for (int i = 0; i < 1000; i++) {
                    buffer.putLong(i * 8, i); // Write 1000 longs to the file
                }
                
                // Force changes to be written to disk
                buffer.force();
                
                long endTime = System.nanoTime();
                System.out.println("Write time: " + (endTime - startTime) / 1000 + " μs");
                
                // Read data from the memory mapped file
                startTime = System.nanoTime();
                
                long sum = 0;
                for (int i = 0; i < 1000; i++) {
                    sum += buffer.getLong(i * 8);
                }
                
                endTime = System.nanoTime();
                System.out.println("Read time: " + (endTime - startTime) / 1000 + " μs");
                System.out.println("Sum: " + sum);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}