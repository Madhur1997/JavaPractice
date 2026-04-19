package net.sfdc.ci;

// A project has several sub-tasks given along with the execution time for each.
// Each sub-task is either independent or requires the execution of some dependent task previously.
// Given that tasks can be executed in parallel,
// find the optimal sequence of sub-tasks execution so that the entire project completion takes the least amount of time.

/*

Use queue along with topological sort to account for indegrees.
For each task maintain a timer when it ends. dp[task] = max(dp[currTask] + time[task], dp[task]);
Only add a particular task to a queue when all its dependencies are satisfied.
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

            List<Integer> tasks = new ArrayList<>(Arrays.asList(1, 2, 3));

        }
    }
}
