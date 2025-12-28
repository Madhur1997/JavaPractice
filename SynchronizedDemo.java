class SafeBankAccount {
    private int balance = 1000;
    
    // synchronized keyword = only ONE thread at a time!
    public synchronized void withdraw(int amount) {
        if (balance >= amount) {
            System.out.println(Thread.currentThread().getName() + 
                             " checking balance: $" + balance);
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            balance -= amount;
            System.out.println(Thread.currentThread().getName() + 
                             " withdrew $" + amount + 
                             ", remaining: $" + balance);
        } else {
            System.out.println(Thread.currentThread().getName() + 
                             " insufficient funds! Balance: $" + balance);
        }
    }
    
    public synchronized int getBalance() {
        return balance;
    }
}

public class SynchronizedDemo {
    public static void main(String[] args) throws InterruptedException {
        SafeBankAccount account = new SafeBankAccount();
        
        System.out.println("Initial balance: $" + account.getBalance());
        System.out.println("\nTwo people trying to withdraw $600 each...\n");
        
        Thread person1 = new Thread(() -> {
            account.withdraw(600);
        }, "Person1");
        
        Thread person2 = new Thread(() -> {
            account.withdraw(600);
        }, "Person2");
        
        person1.start();
        person2.start();
        
        person1.join();
        person2.join();
        
        System.out.println("\nFinal balance: $" + account.getBalance());
        System.out.println("SUCCESS: One person got money, other was rejected!");
    }
}

