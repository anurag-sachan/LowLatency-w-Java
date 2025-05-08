package StaticKeyword.pkg2;

import StaticKeyword.pkg1.Class1;

public class Class2 {

    // static block is called before constructor, for example check pkg1 Class1
    static{
        System.out.println("static block");
    }

    public Class2(){
        System.out.println("Constructor");
    }
    public static void main(String[] args) {
        // Class1 c1= new Class1();
        // System.out.println(c1.literacy);

        System.out.println(Class1.literacy);
        Class1.Hello();

        // Class2 c2=new Class2();

    }
}
