package Examples;

import java.util.Arrays;
import java.util.List;

// input can be of multiple data-type of same family here (int, float, double, number)
// <? extends Number>
class Wildcard {
    public static void main(String[] args) {
        // works on List < Integer >, List < Double >, and List < Number >
        List<Integer> list1 = Arrays.asList(4, 5, 6, 7);
        System.out.print("Total sum is:" + sum(list1));

        List<Double> list2 = Arrays.asList(4.1, 5.1, 6.1);
        System.out.print("Total sum is:" + sum(list2));
    }

    static double sum(List<? extends Number> list) {
        double sum=0;
        for (Number n : list) sum+=n.doubleValue();
        return sum;
    }
}