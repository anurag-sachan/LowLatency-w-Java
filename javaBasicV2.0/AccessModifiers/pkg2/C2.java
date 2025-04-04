package AccessModifiers.pkg2;

import AccessModifiers.pkg1.C1;

// public class C2{
public class C2{

    protected int val=10; //protected Val
    public static void main(String[] args) {
        C1 c1=new C1(); //accessible bcz C1(pkg1) is public 
        // System.out.println(c1.b); //make b public in pkg1.C1
    }
}

class ProtectedAM extends C2{
    public static void main(String[] args) {
        ProtectedAM pam=new ProtectedAM();
        System.out.println(pam.val); //protected variable use
    }
}
