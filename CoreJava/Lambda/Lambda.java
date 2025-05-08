package Lambda;

import java.util.ArrayList;

public class Lambda {
    public static void main(String[] args) {
        
        int[] arr={1,2,4,6,7,9,12,13};
        ArrayList<Integer> list=new ArrayList<>();
        for (int i : arr) {
            list.add(i);
        }
        System.out.println(list);
        list.forEach(x->{
            if(x%2==0) System.out.println(x);
        });
    }
}
