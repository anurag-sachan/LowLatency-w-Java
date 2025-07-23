package Examples;

import java.io.BufferedReader;
import java.io.FileReader;

public class TryWithResources {
    public static void main(String[] args) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader("/Users/anurag/Data/scrap/javaLowLatency/CoreJava/Examples/source.txt"))) {
            System.out.println(br.readLine());
        } catch (Exception e){
            System.out.println(e);
        }
    }
}