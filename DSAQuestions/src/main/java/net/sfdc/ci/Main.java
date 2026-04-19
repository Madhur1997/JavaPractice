package net.sfdc.ci;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        List<Object> nestedList = Arrays.asList(
            Arrays.asList(1, 1), 3, Arrays.asList(1, 1));
        System.out.println(NestedSum.depthSumInverse(nestedList));

        List<Object> nestedList2 = Arrays.asList(
            1, Arrays.asList(3, Arrays.asList(5, Arrays.asList(3, 4, 5))));
        System.out.println(NestedSum.depthSumInverse(nestedList2));

        List<List<Integer>> l = new ArrayList<List<Integer>>();
        l.stream().map(inner -> inner.stream().mapToInt(Integer::intValue).toArray()).toArray();
    }
}
