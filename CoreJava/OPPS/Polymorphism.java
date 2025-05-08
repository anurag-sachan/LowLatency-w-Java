package OPPS;

public class Polymorphism {
    public static void main(String[] args) {
        TwoFxn tFxn= new TwoFxn();
        System.out.println(tFxn.addition("Anurag", "Sachan"));
        System.out.println(tFxn.addition(0,8));
    }
}

class TwoFxn{
    String addition(String a, String b){
        return a+" "+b;
    }
    int addition(int a, int b){
        return a+b;
    }
}