package Examples;

// reduced boilerplate & variables
// immutable
// to remember: called similar to constructor
record User(String name, int age) {
    // You can add methods
    public String greet() {
        return "Hello, " + name;
    }
}

// class User{
//     String name;
//     int age;

//     User(String name, int age){
//         this.name=name;
//         this.age=age;
//     }
// }

class Record {
    public static void main(String[] args) {
        var u = new User("anurag", 24);
        // System.out.println(u.name);
        // to remember: even variables are called w. ()
        System.out.println(u.name());
        System.out.println(u.greet());
    }
}