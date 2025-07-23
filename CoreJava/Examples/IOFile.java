package Examples;

import java.io.FileInputStream;
import java.io.FileOutputStream;

class IOFileCopy {
    public static void main(String[] args) throws Exception {
        try (var in = new FileInputStream("/Users/anurag/Data/scrap/javaLowLatency/CoreJava/Examples/source.txt");
            var out = new FileOutputStream("/Users/anurag/Data/scrap/javaLowLatency/CoreJava/Examples/dest.txt")) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
                out.write(buf, 0, len);
        }
    }
}