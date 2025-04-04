package OPPS;

// Encapsulation is a way of hiding the implementation details of a class from outside access and only exposing a public interface that can be used to interact with the class.

// hide implementation w/ private access modifier
// generate getter setter for changes
public class Encapsulation{
    public static void main(String[] args) {
        Person person=new Person();
        //accessible
        // System.out.println(person.name);
        
        //make private, unaccesible
        System.out.println(person.getName());
        person.setName("Abhishek");
        System.out.println(person.getName());

    }
}

class Person{
    private String name="Anurag";
    private int age;

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public int getAge() {
        return age;
    }
}