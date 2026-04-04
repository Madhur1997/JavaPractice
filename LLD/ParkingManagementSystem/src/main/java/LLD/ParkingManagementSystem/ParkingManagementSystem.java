package LLD.ParkingManagementSystem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

interface ParkingPaymentStrategy {

    public Double calculateAmount(Vehicle vehicle, LocalDateTime startTime, LocalDateTime endTime);
}

class WeekdayPaymentStrategy implements ParkingPaymentStrategy {

    @Override
    public Double calculateAmount(Vehicle vehicle, LocalDateTime startTime, LocalDateTime endTime) {
        return 0.0;
    }
}

class WeekendPaymentStrategy implements ParkingPaymentStrategy {
    @Override
    public Double calculateAmount(Vehicle vehicle, LocalDateTime startTime, LocalDateTime endTime) {
        return 0.0;
    }
}

class PaymentService {
    private ParkingPaymentStrategy  parkingPaymentStrategy;
    public Double calculateAmount(Ticket ticket) {
        parkingPaymentStrategy.calculateAmount(ticket.getVehicle(), ticket.getEnterTime(), ticket.getExitTime());

        return 5.0;
    }
}

public class ParkingManagementSystem {
    private SlotAllocationService slotAllocationService;
    private TicketManagementService ticketManagementService;
    private PaymentService paymentService;
}

interface SlotAllocationStrategy {
    public ParkingSlot allocateSlot(Vehicle vehicle);
}

class FirstSlotAllocationStrategy implements SlotAllocationStrategy {

    @Override
    public ParkingSlot allocateSlot(Vehicle vehicle) {
        return ParkingSlot();
    }
}

class SlotAllocationService {
    private ParkingLot parkingLot;
    private SlotAllocationStrategy slotAllocationStrategy;

    public void setAllocationStrategy(SlotAllocationStrategy slotAllocationStrategy) {
        this.slotAllocationStrategy = slotAllocationStrategy;
    }

    public ParkingSlot allocateSlot(Vehicle vehicle) {

        ParkingSlot slot = this.slotAllocationStrategy.allocateSlot(vehicle);
        return slot;
    }
}

class ParkingLot {
    private List<ParkingFloor> parkingFloors;
}

class ParkingFloor {
    private Set<ParkingSlot> availableSlots = new TreeSet<ParkingSlot>();
}

class ParkingSlot implements Comparable<ParkingSlot> {
    private String slotId;
    private boolean isAvailable;

    public ParkingSlot(int slotId) {
        this.slotId = String.valueOf(slotId);
    }

    public void parkVehicle() {
        if (isAvailable) {
            isAvailable = false;
        } else {
            throw new RuntimeException("Slot is not available");
        }
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public int compareTo(ParkingSlot slot2) {
        return Integer.compare(Integer.parseInt(slotId), Integer.parseInt(slot2.getSlotId()));
    }

    public String getSlotId() {
        return slotId;
    }
}

abstract class Vehicle {
    private String registrationNumber;
    private String vehicleType;

    public Vehicle(String registrationNumber, String vehicleType) {
        this.registrationNumber = registrationNumber;
        this.vehicleType = vehicleType;
    }
}

class Car extends Vehicle {

    public Car(String registrationNumber) {
        super(registrationNumber, "Car");
    }
}

class Bike extends Vehicle {
    public Bike(String registrationNumber) {
        super(registrationNumber, "Bike");
    }
}

class TicketManagementService {

    public Ticket createTicket(Vehicle vehicle, ParkingSlot slot) {
        return new Ticket(vehicle, slot, LocalDateTime.now());
    }
}

class Ticket {
    private String ticketId;
    private LocalDateTime enterTime;
    private LocalDateTime endTime;
    private Vehicle vehicle;
    private ParkingSlot slot;

    public Ticket(Vehicle vehicle, ParkingSlot slot, LocalDateTime enterTime) {
        this.vehicle = vehicle;
        this.slot = slot;
        this.enterTime = enterTime;
    }

    public void exit(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getExitTime() {
        return endTime;
    }

    public LocalDateTime getEnterTime() {
        return enterTime;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
}
