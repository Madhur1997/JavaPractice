package net.sfdc.ci;

/*

Problem Statement
A startup owner wants to schedule meetings with investors. There are n investors available.

Given:

firstDay[n] - First day when investor i is available
lastDay[n] - Last day when investor i is available
Constraint: Only one meeting per day allowed

Goal: Return the maximum number of meetings the owner can schedule

Example
Input:
firstDay = [1, 1, 2, 3]
lastDay  = [1, 2, 2, 3]

Output: 3

Explanation:
Day 1 → Available: Investor 1, 2 → Meet Investor 1
Day 2 → Available: Investor 2, 3 → Meet Investor 2
Day 3 → Available: Investor 4 → Meet Investor 4

Total meetings = 3


123
323

213
233


12
3

112
332

pq -> 2, 3

ans = 2
 */

import java.util.*;

public class MeetingScheduling {

    int maxMeetings(int firstDay[], int lastDay[]) {
        List<ArrayList<Integer>> l = new ArrayList<ArrayList<Integer>>();

        for(int i=0; i<firstDay.length; i++) {
            l.add(new ArrayList(Arrays.asList(firstDay[i], lastDay[i])));
        }

        Collections.sort(l, (a, b) -> {
            if(a.get(0) != b.get(0)) return a.get(0) - b.get(0);
            return a.get(1) - b.get(1);
        });

        PriorityQueue<Integer> pq = new PriorityQueue<>((a,b) -> a-b);

        int maxMeetings = 0, nextDay = 0;
        for(int i=0; i<l.size();) {
            while(i<l.size() && l.get(i).get(0) == nextDay) {
                pq.offer(l.get(i).get(1));
                i++;
            }

            while(!pq.isEmpty() && pq.peek() < nextDay) pq.poll();

            if(!pq.isEmpty()) {
                pq.poll();
                maxMeetings++;
            }

            nextDay++;
        }

        return maxMeetings;
    }
}
