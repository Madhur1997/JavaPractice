import java.util.ArrayList;
import java.util.List;

public class JavaGenericsPractice {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        var list1 = new ArrayList<String>() {{
            add("Hello");
            add("World");
        }};

        // Fails compile time.
        // addNumbersInList(list1);

        // var list2 = new ArrayList<Integer>() {{
        //     add(1);
        //     add(2);
        // }};

        // // Object res = printList(list1);   // ✓
        // // System.out.println(res);
        // Object res2 = printList(list2);  // ✓
        // System.out.println(res2);

        List<Number> list3 = new ArrayList<Number>() {{
            add(1);
            add(2);
            add(3.12);
        }};

        Number res3 = addNumbersInList(list3);
        System.out.println(res3);
    }

    public static Number printList(List<?> list) {
        for (Object item : list) {  // Can only treat items as Object
            System.out.println(item);
            System.out.println(item.getClass());
        }

        Number res = 0;
        for(var item : list) {
            res = res.doubleValue() + ((Integer) item).doubleValue();
        }

        return res;
    }

    public static Number addNumbersInList(List<? extends Number> list) {
        for (Object item : list) {  // Can only treat items as Object
            System.out.println(item);
            System.out.println(item.getClass());
        }

        double sum = 0;
        for(Number num : list) {  // Use Number directly
            sum += num.doubleValue();  // No cast needed!
        }

        return sum;
    }
}