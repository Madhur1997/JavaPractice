package net.sfdc.ci;

/*
Problem: Build a delivery cost tracking system
Asked in 3 parts:

1. Cost Calculation
    add_driver(driverId)
    add_delivery(driverId, startTime, endTime)
    get_total_cost()
Discussed approach, data structures, and time complexity before coding.

2. Payment Tracking
    pay_up_to_time(upToTime)
    get_cost_to_be_paid()

3. Analytics
    get_max_active_drivers_in_last_24_hours(currentTime)

*/

import java.sql.Array;
import java.util.*;

class Driver {
    private int driverId;
}

class Delivery {
    public int driverId, startTime, endTime;
    public Delivery(int driverId, int startTime, int endTime) {
        this.driverId = driverId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}

public class DeliveryCostTracking {
    private final Map<Integer, Driver> driverMap = new HashMap<>();
    private final TreeSet<Delivery> deliveries = new TreeSet<Delivery>((a, b) -> {
       if(a.endTime != b.endTime) return a.endTime - b.endTime;
       return a.startTime - b.startTime;
    });
    private int totalCost = 0, unitCost = 1;
    private int paidAmount = 0, lastPaidTime = 0;
    public void add_driver(int driverId) {
        driverMap.putIfAbsent(driverId, new Driver());
    }

    public void add_delivery(int driverId, int startTime, int endTime) {
        deliveries.add(new Delivery(driverId, startTime, endTime));
        totalCost += (endTime - startTime) * unitCost;
    }

    public int get_total_cost() {
        return totalCost;
    }

    public int pay_upto_time(int time) {
        SortedSet<Delivery> rangeDeliveries = deliveries.subSet(new Delivery(0, lastPaidTime, lastPaidTime), new Delivery(0, time, time+1));
        Iterator<Delivery> iter = rangeDeliveries.iterator();
        int currAmount = 0;
        while(iter.hasNext()) {
            Delivery delivery = iter.next();
            currAmount += (delivery.endTime - delivery.startTime)*unitCost;
        }

        paidAmount += currAmount;
        lastPaidTime = time;
        return currAmount;
    }

    public int get_cost_to_be_paid() {
        return totalCost - paidAmount;
    }

    public int get_max_active_drivers_in_last_24_hours(int currentTime) {
        SortedSet<Delivery> rangeDeliveries = deliveries.subSet(new Delivery(0, -1, currentTime-24), new Delivery(0, currentTime, currentTime));
        Iterator<Delivery> iter = rangeDeliveries.iterator();
        List<Integer> l = new ArrayList<>();
        while(iter.hasNext()) {
            Delivery delivery = iter.next();
            l.add(delivery.startTime);
            l.add(-1 * delivery.endTime);
        }

        Collections.sort(l, Comparator.comparingInt(Math::abs));

        Iterator<Integer> it = l.iterator();
        int ct = 0;
        while(it.hasNext()) {
            int val = it.next();
            if(val > 0) ct++;
            else ct--;
        }

        return ct;
    }
}
