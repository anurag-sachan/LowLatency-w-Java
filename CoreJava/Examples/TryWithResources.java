package Examples;
public class TryWithResources {
    public static void main(String[] args) throws Exception {
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("data.txt"))) {
            System.out.println(br.readLine());
        } catch (Exception e){
            System.out.println(e);
        }
    }
}