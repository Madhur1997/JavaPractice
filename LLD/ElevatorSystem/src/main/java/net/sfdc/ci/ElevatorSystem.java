package net.sfdc.ci;

/*
1. Press up/down from outside.
2. Select floor number from inside
3. Multi lift system
4. Different algorithms to select next lift.
*/

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

enum ElevatorState {
    Idle,
    Moving,
    OutOfOrder
}

enum Direction {
    UP,
    DOWN
}

class Elevator {
    private int id;
    private List<Integer> queuedfloors;
    private int currentFloor;
    private Direction direction;
    private ElevatorState state;
    private int maxFloor;
    private int minFloor;

    // Getters;Setters

    public void command(int floor) {

    }

    public void cancel(int floor) {

    }

    // Elevator sends this signal to keep the state in sync.
    public void updateCurrentFloor(int floor) {
        this.currentFloor = floor;
    }

    public int getId() {
        return this.id;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public int getCurrentFloor() {
        return this.currentFloor;
    }

    public int getMaxFloor() {
        return this.maxFloor;
    }

    public int getMinFloor() {
        return this.minFloor;
    }
}

class ElevatorManager {
    private List<Elevator> elevators;
    private ElevatorStrategy strategy;
    public Optional<Elevator> scheduleElevator(int floor, Direction direction) {
        return strategy.selectElevator(elevators, floor, direction);
    }
}

interface ElevatorStrategy {
    public Optional<Elevator> selectElevator(List<Elevator> elevators, int floor, Direction direction);
}

class OddEvenSelectionStrategy implements ElevatorStrategy {

    @Override
    public Optional<Elevator> selectElevator(List<Elevator> elevators, int floor, Direction direction) {
        Iterator<Elevator> iter = elevators.iterator();
        Elevator e = null;
        int minDistance = Integer.MAX_VALUE;
        while(iter.hasNext()) {
            Elevator elevator = iter.next();
            // Can also have additional checks like only schedule a lift which is currently atleast 2 floors away from the target floor.
            if(elevator.getId()%2 == floor%2) {
                if((elevator.getCurrentFloor() < floor && elevator.getDirection() == Direction.UP) || (elevator.getCurrentFloor() > floor && elevator.getDirection() == Direction.DOWN)) {
                    if(Math.abs(floor - elevator.getCurrentFloor()) < minDistance) {
                        minDistance = Math.abs(floor - elevator.getCurrentFloor());
                        e = elevator;
                    }
                } else if(elevator.getDirection() == Direction.UP) {
                    if(elevator.getMaxFloor()*2 - elevator.getCurrentFloor() - floor  < minDistance) {
                        minDistance = elevator.getMaxFloor()*2 - elevator.getCurrentFloor() - floor;
                        e = elevator;
                    }
                } else {
                    if(elevator.getCurrentFloor() - 2*elevator.getMinFloor()  +  floor   < minDistance) {
                        minDistance = elevator.getCurrentFloor() - 2*elevator.getMinFloor()  +  floor;
                        e = elevator;
                    }
                }
            }
        }

        if(e == null) return Optional.empty();

        e.command(floor);
        return Optional.of(e);
    }
}

class NearestSelectionStrategy implements ElevatorStrategy {

    @Override
    public Optional<Elevator> selectElevator(List<Elevator> elevators, int floor, Direction direction) {
        return null;
    }
}

public class ElevatorSystem {
    private ElevatorManager manager;

    public String scheduleElevator(int floor, Direction direction) {
        Optional<Elevator> o = this.manager.scheduleElevator(floor, direction);
        if(o.isPresent()) return String.valueOf(o.get().getId());
        return "Elevator Busy";
    }

    public void selectTargetFloor(Elevator elevator, int floor) {
        elevator.command(floor);
    }
}




