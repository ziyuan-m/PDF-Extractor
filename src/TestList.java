import java.util.*;

public class TestList {
    public static void main(String[] args) {
        List<Integer> numList = new ArrayList<>();
        for (int i = 0; i <= 99999999; i++) {
            numList.add(i + 1);
        }
        long startTime = System.currentTimeMillis();
        int index = Collections.binarySearch(numList, 2000);
        long endTime = System.currentTimeMillis();
        System.out.println("time used: " + (endTime - startTime) + "ms");
        System.out.println(index);

        Map<Integer, Integer> numMap = new TreeMap<>();
        for (int i = 0; i <= 99999999; i++) {
            numMap.put(i, i + 1);
        }
        System.out.println("*********************");
        startTime = System.currentTimeMillis();
        int value = numMap.get(2000);
        endTime = System.currentTimeMillis();
        System.out.println();
        System.out.println("time used: " + (endTime - startTime) + "ms");
    }
}
