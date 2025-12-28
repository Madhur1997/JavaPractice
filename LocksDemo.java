import java.util.concurrent.locks.*;
import java.util.concurrent.*;

public class LocksDemo {
    
    // ============================================
    // 1. ReentrantLock Example
    // ============================================
    static class BankAccount {
        private int balance = 1000;
        private final Lock lock = new ReentrantLock();
        
        public void withdraw(int amount, String person) {
            lock.lock();
            try {
                if (balance >= amount) {
                    System.out.println(person + " checking balance: $" + balance);
                    Thread.sleep(100); // Simulate processing time
                    balance -= amount;
                    System.out.println(person + " withdrew $" + amount + ", remaining: $" + balance);
                } else {
                    System.out.println(person + " insufficient funds!");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock(); // MUST unlock in finally!
            }
        }
        
        public boolean tryWithdraw(int amount, String person) {
            if (lock.tryLock()) { // Non-blocking attempt
                try {
                    if (balance >= amount) {
                        balance -= amount;
                        System.out.println(person + " withdrew $" + amount + " (tryLock succeeded)");
                        return true;
                    }
                    return false;
                } finally {
                    lock.unlock();
                }
            } else {
                System.out.println(person + " couldn't get lock (tryLock failed)");
                return false;
            }
        }
        
        public int getBalance() {
            lock.lock();
            try {
                return balance;
            } finally {
                lock.unlock();
            }
        }
    }
    
    // ============================================
    // 2. ReadWriteLock Example
    // ============================================
    static class SharedCache {
        private String data = "Initial Data";
        private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
        private int readCount = 0;
        
        public String read(String reader) {
            rwLock.readLock().lock(); // Multiple readers allowed
            try {
                readCount++;
                System.out.println("  [" + reader + "] Reading: " + data + " (reader #" + readCount + ")");
                Thread.sleep(500); // Simulate slow read
                return data;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            } finally {
                readCount--;
                rwLock.readLock().unlock();
            }
        }
        
        public void write(String writer, String newData) {
            rwLock.writeLock().lock(); // Only one writer allowed
            try {
                System.out.println("  [" + writer + "] Writing: " + newData);
                Thread.sleep(300);
                data = newData;
                System.out.println("  [" + writer + "] Write complete!");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                rwLock.writeLock().unlock();
            }
        }
    }
    
    // ============================================
    // 3. Lock with Timeout Example
    // ============================================
    static class TimedResource {
        private final Lock lock = new ReentrantLock();
        
        public void accessWithTimeout(String user) {
            try {
                System.out.println(user + " trying to acquire lock...");
                if (lock.tryLock(2, TimeUnit.SECONDS)) { // Wait max 2 seconds
                    try {
                        System.out.println(user + " got lock! Processing...");
                        Thread.sleep(3000); // Simulate long operation
                        System.out.println(user + " done processing");
                    } finally {
                        lock.unlock();
                    }
                } else {
                    System.out.println(user + " timeout! Couldn't get lock in 2 seconds");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(user + " was interrupted");
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Locks Demo ===\n");
        
        // ============================================
        // Demo 1: ReentrantLock
        // ============================================
        System.out.println("--- 1. ReentrantLock (Basic) ---");
        BankAccount account = new BankAccount();
        
        Thread t1 = new Thread(() -> account.withdraw(600, "Alice"));
        Thread t2 = new Thread(() -> account.withdraw(500, "Bob"));
        Thread t3 = new Thread(() -> account.withdraw(400, "Charlie"));
        
        t1.start(); t2.start(); t3.start();
        t1.join(); t2.join(); t3.join();
        
        System.out.println("Final balance: $" + account.getBalance());
        System.out.println();
        
        // ============================================
        // Demo 2: tryLock (non-blocking)
        // ============================================
        System.out.println("--- 2. tryLock (Non-blocking) ---");
        BankAccount account2 = new BankAccount();
        
        Thread t4 = new Thread(() -> {
            account2.withdraw(200, "Dave"); // This will hold lock
        });
        
        Thread t5 = new Thread(() -> {
            try {
                Thread.sleep(50); // Let Dave get lock first
                account2.tryWithdraw(100, "Eve"); // This will fail tryLock
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        t4.start(); t5.start();
        t4.join(); t5.join();
        System.out.println();
        
        // ============================================
        // Demo 3: ReadWriteLock (Multiple readers)
        // ============================================
        System.out.println("--- 3. ReadWriteLock (Multiple Readers) ---");
        SharedCache cache = new SharedCache();
        
        // Create 3 readers - they can all read simultaneously
        Thread reader1 = new Thread(() -> cache.read("Reader-1"));
        Thread reader2 = new Thread(() -> cache.read("Reader-2"));
        Thread reader3 = new Thread(() -> cache.read("Reader-3"));
        
        System.out.println("Starting 3 readers simultaneously...");
        reader1.start(); reader2.start(); reader3.start();
        reader1.join(); reader2.join(); reader3.join();
        
        System.out.println("\nNow a writer (will block readers)...");
        Thread writer = new Thread(() -> cache.write("Writer-1", "Updated Data"));
        writer.start();
        writer.join();
        
        System.out.println("\nReading after write:");
        cache.read("Reader-4");
        System.out.println();
        
        // ============================================
        // Demo 4: Lock with Timeout
        // ============================================
        System.out.println("--- 4. Lock with Timeout ---");
        TimedResource resource = new TimedResource();
        
        Thread t6 = new Thread(() -> resource.accessWithTimeout("User-1"));
        Thread t7 = new Thread(() -> resource.accessWithTimeout("User-2"));
        
        t6.start();
        Thread.sleep(100); // Let User-1 get lock first
        t7.start(); // User-2 will timeout
        
        t6.join();
        t7.join();
        
        System.out.println("\n=== Key Takeaways ===");
        System.out.println("• ReentrantLock: More flexible than synchronized");
        System.out.println("• tryLock(): Non-blocking lock attempt");
        System.out.println("• tryLock(timeout): Wait with timeout");
        System.out.println("• ReadWriteLock: Multiple readers OR one writer");
        System.out.println("• Always unlock in finally block!");
    }
}

