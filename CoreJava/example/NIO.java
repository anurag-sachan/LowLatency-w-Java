package example;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

class NIO {
    public static void main(String[] args) throws Exception {
        var channel = FileChannel.open(Paths.get("/Users/anurag/Data/scrap/javaLowLatency/CoreJava/Examples/Files/source.txt"));
        var buffer = ByteBuffer.allocate(1024);
        while (channel.read(buffer) > 0) {
            buffer.flip();
            while (buffer.hasRemaining())
                System.out.print((char) buffer.get());
            buffer.clear();
        }
        channel.close();
    }
}