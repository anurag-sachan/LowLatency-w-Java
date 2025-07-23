package Examples;

class Generic {
    public static void main(String[] args) {
        System.out.println(square(5));
    }

    public static <T extends Number> double square(T n){
        return n.doubleValue()*n.doubleValue();
    }
}