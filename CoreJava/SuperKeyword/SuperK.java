package SuperKeyword;

class Animal{
    protected void animalSound(){
        System.out.println("Animal Sound.");
    }
}

class Dog extends Animal{
    public void animalSound(){
        super.animalSound();
        System.out.println("woff.");
    }
}

public class SuperK {
    public static void main(String[] args) {
        Dog dog=new Dog();
        dog.animalSound();
    }
}