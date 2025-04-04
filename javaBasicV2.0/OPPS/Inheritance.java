package OPPS;

public class Inheritance {
    public static void main(String[] args) {
        // goes to triangle -> calls Shape(Parent constructor) -> Triangle(Self constructor) 
        Triangle triangle=new Triangle();
        
        // can access from parent class
        // System.out.println(triangle.sharpness);

        // w/ encapsulation
        System.out.println(triangle.getSharpness());
    }
}

class Shape{

    // int sharpness=2;
    
    // encapsulation
    private int sharpness=2;
    public int getSharpness() {
        return sharpness;
    }

    Shape(){
        System.out.println("Hello from Shape");
    }
}

class Triangle extends Shape{
    Triangle(){
        System.out.println("Hello from Triangle");
    }
}