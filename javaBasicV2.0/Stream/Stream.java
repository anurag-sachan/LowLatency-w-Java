package Stream;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Stream {
    public static void main(String[] args) {
        List<List<String>> listOfLists = Arrays.asList(
            Arrays.asList("Reflection", "Collection", "Stream"),
            Arrays.asList("Structure", "State", "Flow"),
            Arrays.asList("Sorting", "Mapping", "Reduction", "Stream")
        );

        // Set<String> intermediateResults = new HashSet<>();

        HashSet<String> set=new HashSet<>();
        System.out.println(listOfLists.stream()
        .flatMap(List::stream)
        .filter(s->s.startsWith("S"))
        .peek(set::add)
        // .peek(v-> set.add(v))
        .collect(Collectors.toList()));

        // set.forEach(x-> System.out.println(x));
        // System.out.println();

        set.forEach(System.out::println);

        // List<String> result = listOfLists.stream()
        //     .flatMap(List::stream)
        //     .filter(s -> s.startsWith("S"))
        //     .map(String::toUpperCase)
        //     .distinct()
        //     .sorted()
        //     .peek(s -> intermediateResults.add(s))
        //     .collect(Collectors.toList());

        // System.out.println("Intermediate Results:");
        // intermediateResults.forEach(System.out::println);

        // System.out.println("Final Result:");
        // result.forEach(System.out::println);
    }
}
