package net.sfdc.ci;

import java.util.*;

public class NestedSum {

    private static int computeMaxDepth(List<Object> nestedList) {
        int maxDepth = 1;
        for(Object o: nestedList) {
            if(o instanceof List<?>) {
                maxDepth = Math.max(maxDepth, computeMaxDepth((List<Object>)o)+1);
            }
        }
        return maxDepth;
    }

    private static int computeDepthSum(List<Object> nestedList, int currDepth, int maxDepth) {
        int sum = 0;
        for(Object o: nestedList) {
            if(o instanceof List<?>) {
                sum += computeDepthSum((List<Object>)o, currDepth+1, maxDepth);
            } else {
                sum += (maxDepth-currDepth+1)*(Integer)o;
            }
        }
        return sum;
    }

    // Function to calculate the depth sum inverse of a
    // nested list.
    static int depthSumInverse(List<Object> nestedList)
    {
        int maxDepth = computeMaxDepth(nestedList);
        int sum = computeDepthSum(nestedList, 1, maxDepth);

        return sum;
    }
}
