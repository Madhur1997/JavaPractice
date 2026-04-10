package net.sfdc.ci;

import java.util.HashMap;

interface State {
    public void selectSlot(VendingMachine vm, int slotId);
    public void insertMoney(VendingMachine vm, int amount);
    public void addProductToSlot(VendingMachine vm, int slot, int count);
    public void dispenseProduct(VendingMachine vm);
}

class IdleState implements State {

    @Override
    public void selectSlot(VendingMachine vm, int slotId) {
        vm.setState(new SlotSelectedState(slotId));
    }

    @Override
    public void insertMoney(VendingMachine vm, int amount) {
        System.out.println("Select product first...");
    }

    @Override
    public void addProductToSlot(VendingMachine vm, int slot, int count) {
        vm.getInventory().addProductToSlot(slot, count);
    }

    @Override
    public void dispenseProduct(VendingMachine vm) {
        System.out.println("Product not selected...");
    }

}

class SlotSelectedState implements State {
    private int balance;
    private final int slotId;

    public SlotSelectedState(int slotId) {
        this.slotId = slotId;
        this.balance = 0;
    }

    @Override
    public void selectSlot(VendingMachine vm, int productId) {
        System.out.println("Slot already selected");
    }

    @Override
    public void insertMoney(VendingMachine vm, int amount) {
        this.balance += amount;
        int price = vm.getInventory().getSlot(slotId).getPrice();
        if(this.balance >= price) {
            vm.setState(new DispensingProductState());
            System.out.println("Returning remaining balance to the user " + (this.balance - price));
            return;
        }

        System.out.println("Insufficient balance, add " + (price - this.balance));
    }

    @Override
    public void addProductToSlot(VendingMachine vm, int slot, int count) {
        System.out.println("Vending machine is being used, please wait...");
    }

    @Override
    public void dispenseProduct(VendingMachine vm) {
        int price = vm.getInventory().getSlot(slotId).getPrice();
        System.out.println("Insufficient balance, add " + (price - this.balance));
    }
}

class DispensingProductState implements State {

    @Override
    public void selectSlot(VendingMachine vm, int productId) {
        System.out.println("Vending machine is being used, please wait...");
    }

    @Override
    public void insertMoney(VendingMachine vm, int amount) {
        System.out.println("Vending machine is being used, please wait...");
    }

    @Override
    public void addProductToSlot(VendingMachine vm, int slot, int count) {
        System.out.println("Vending machine is being used, please wait...");
    }

    @Override
    public void dispenseProduct(VendingMachine vm) {
        // Waits for hardware signal to know that amount and product has been dispensed.
        // then transitions the vending machine to the idle state.
        vm.setState(new IdleState());
    }
}

class Slot {
    private int id;
    private int count;
    private int cost;

    public Slot(int id, int count, int cost) {
        this.id = id;
        this.count = count;
        this.cost = cost;
    }

    public void increaseCount(int count) {
        this.count += count;
    }

    public int getPrice() {
        return cost;
    }
}

class Inventory {
    private final HashMap<Integer, Slot> slots = new HashMap<Integer, Slot>();

    public void dispenseProduct(int slotId) {

    }

    public void addProductToSlot(int slotId, int count) {
        slots.get(slotId).increaseCount(count);
    }

    public Slot getSlot(int slotId) {
        return slots.get(slotId);
    }
}

public class VendingMachine {
    private State state = new IdleState();
    private Inventory inventory = new Inventory();
    public void selectSlot(int slotId) {
        this.state.selectSlot(this, slotId);
    }

    public void insertMoney(int amount) {
        this.state.insertMoney(this, amount);
    }

    public void addProductToSlot(int slotId, int count) {
        this.state.addProductToSlot(this, slotId, count);
    }

    public void dispenseProduct() {
        this.state.dispenseProduct(this);
    }

    public void setState(State state) {
        this.state = state;
    }

    public Inventory getInventory() {
        return inventory;
    }
}


