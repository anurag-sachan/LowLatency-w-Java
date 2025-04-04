package AccessModifiers.pkg1;

public class Cone {
    public static void main(String[] args) {
        System.out.println(C1.a); //static

        C1 c1=new C1();
        System.out.println(c1.b); //accesible here bcz of same pkg
    }
}
