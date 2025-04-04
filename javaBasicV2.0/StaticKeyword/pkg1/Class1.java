package StaticKeyword.pkg1;

import StaticKeyword.pkg2.Class2;

public class Class1 {
    // public for visibility
    // static for calling without making instance
    public static boolean literacy= true;

    // without creating object
    // public boolean literacy= true;
    public static void Hello(){
        System.out.println("Hello");
    }

    public static void main(String[] args) {
        Class2 c2=new Class2();
    }
}
