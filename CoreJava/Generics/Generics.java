package Generics;

public class Generics {
    public static void main(String[] args) {
        Pair<String, Integer> pair= new Pair<>("Anurag",8);
        System.out.println(pair.getkey());
        System.out.println(pair.getValue());
    }
}

class Pair<K,V>{
    private K key;
    private V value;

    Pair(K key, V val){
        this.key=key;
        this.value=val;
    }

    K getkey(){
        return key;
    }

    V getValue(){
        return value;
    }
}
