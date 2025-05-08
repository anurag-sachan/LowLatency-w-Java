package OPPS;

public class Abstraction {
    public static void main(String[] args) {
        Vehicle car = new Car();
        car.accelerate();
    }
}

abstract class Vehicle{
    abstract void accelerate();
}

class Car extends Vehicle{
    void accelerate(){
        System.out.println("Car is accelerating.");
    }
}