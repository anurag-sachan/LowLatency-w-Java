package AccessModifiers.pkg1;

public class C1 { //public (accessible to all pkgs), default(accessible to only same pkg) 
    static int a=10; //static
    protected int b=5;
    // public int b=5; //accessible in pkg2.C2
}
