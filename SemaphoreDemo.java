import java.util.concurrent.*;

public class SemaphoreDemo {
    
    // ============================================
    // 1. Connection Pool Example
    // ============================================
    static class DatabaseConnectionPool {
        private final Semaphore semaphore;
        private final int maxConnections;
        
        public DatabaseConnectionPool(int maxConnections) {
            this.maxConnections = maxConnections;
            this.semaphore = new Semaphore(maxConnections); // 3 permits
        }
        
        public void executeQuery(String user, int queryId) {
            try {
                System.out.println(user + " requesting connection for Query-" + queryId + "...");
                semaphore.acquire(); // Get a permit (blocks if none available)
                
                System.out.println("  ✅ " + user + " got connection! Executing Query-" + queryId);
                System.out.println("     Available connections: " + semaphore.availablePermits() + "/" + maxConnections);
                
                Thread.sleep(2000); // Simulate query execution
                
                System.out.println("  ✅ " + user + " completed Query-" + queryId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                semaphore.release(); // Return permit
                System.out.println("  ↩️  " + user + " released connection");
            }
        }
        
        public boolean tryExecuteQuery(String user, int queryId) {
            if (semaphore.tryAcquire()) { // Non-blocking attempt
                try {
                    System.out.println("  ✅ " + user + " got connection (tryAcquire)! Executing Query-" + queryId);
                    Thread.sleep(1000);
                    return true;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                } finally {
                    semaphore.release();
                }
            } else {
                System.out.println("  ❌ " + user + " couldn't get connection (all busy)");
                return false;
            }
        }
    }
    
    // ============================================
    // 2. Rate Limiter Example
    // ============================================
    static class RateLimiter {
        private final Semaphore semaphore;
        
        public RateLimiter(int requestsPerSecond) {
            this.semaphore = new Semaphore(requestsPerSecond);
            
            // Refill permits every second
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        int current = semaphore.availablePermits();
                        if (current < requestsPerSecond) {
                            semaphore.release(requestsPerSecond - current);
                            System.out.println("    [RateLimiter] Refilled permits to " + requestsPerSecond);
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }, "Refill-Thread").start();
        }
        
        public void makeRequest(String user, int requestId) {
            try {
                semaphore.acquire();
                System.out.println(user + " making Request-" + requestId + " (permits left: " + semaphore.availablePermits() + ")");
                Thread.sleep(100); // Simulate API call
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    // ============================================
    // 3. Binary Semaphore (Mutex)
    // ============================================
    static class Counter {
        private int count = 0;
        private final Semaphore mutex = new Semaphore(1); // Binary semaphore = mutex
        
        public void increment(String thread) {
            try {
                mutex.acquire();
                count++;
                System.out.println(thread + " incremented counter to: " + count);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                mutex.release();
            }
        }
        
        public int getCount() {
            return count;
        }
    }
    
    // ============================================
    // 4. Parking Lot Example
    // ============================================
    static class ParkingLot {
        private final Semaphore spaces;
        private final int totalSpaces;
        
        public ParkingLot(int totalSpaces) {
            this.totalSpaces = totalSpaces;
            this.spaces = new Semaphore(totalSpaces, true); // Fair semaphore
        }
        
        public void parkCar(String car) {
            try {
                System.out.println(car + " arriving... (available spaces: " + spaces.availablePermits() + ")");
                spaces.acquire();
                System.out.println("  🚗 " + car + " parked! (spaces left: " + spaces.availablePermits() + ")");
                Thread.sleep(1500); // Car stays parked
                System.out.println("  🚗 " + car + " leaving...");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                spaces.release();
                System.out.println("  ✅ " + car + " left (spaces available: " + spaces.availablePermits() + ")");
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Semaphore Demo ===\n");
        
        // ============================================
        // Demo 1: Database Connection Pool
        // ============================================
        System.out.println("--- 1. Connection Pool (3 connections max) ---");
        DatabaseConnectionPool pool = new DatabaseConnectionPool(3);
        
        // Create 5 users competing for 3 connections
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int id = i + 1;
            threads[i] = new Thread(() -> pool.executeQuery("User-" + id, id));
        }
        
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        
        System.out.println();
        
        // ============================================
        // Demo 2: tryAcquire (non-blocking)
        // ============================================
        System.out.println("--- 2. tryAcquire (Non-blocking) ---");
        DatabaseConnectionPool pool2 = new DatabaseConnectionPool(2);
        
        Thread t1 = new Thread(() -> pool2.executeQuery("Alice", 1));
        Thread t2 = new Thread(() -> pool2.executeQuery("Bob", 2));
        
        t1.start(); t2.start();
        Thread.sleep(500); // Let them acquire both permits
        
        Thread t3 = new Thread(() -> pool2.tryExecuteQuery("Charlie", 3)); // Will fail
        t3.start();
        
        t1.join(); t2.join(); t3.join();
        System.out.println();
        
        // ============================================
        // Demo 3: Binary Semaphore (Mutex)
        // ============================================
        System.out.println("--- 3. Binary Semaphore (Acts like Lock) ---");
        Counter counter = new Counter();
        
        Thread[] counterThreads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int id = i + 1;
            counterThreads[i] = new Thread(() -> counter.increment("Thread-" + id));
        }
        
        for (Thread t : counterThreads) t.start();
        for (Thread t : counterThreads) t.join();
        
        System.out.println("Final count: " + counter.getCount());
        System.out.println();
        
        // ============================================
        // Demo 4: Parking Lot
        // ============================================
        System.out.println("--- 4. Parking Lot (2 spaces) ---");
        ParkingLot parking = new ParkingLot(2);
        
        String[] cars = {"Car-A", "Car-B", "Car-C", "Car-D"};
        Thread[] carThreads = new Thread[4];
        
        for (int i = 0; i < 4; i++) {
            final String car = cars[i];
            carThreads[i] = new Thread(() -> parking.parkCar(car));
        }
        
        for (Thread t : carThreads) {
            t.start();
            Thread.sleep(200); // Stagger arrivals
        }
        
        for (Thread t : carThreads) t.join();
        
        System.out.println("\n=== Key Takeaways ===");
        System.out.println("• Semaphore: Controls access to N resources");
        System.out.println("• acquire(): Get permit (blocks if none available)");
        System.out.println("• release(): Return permit");
        System.out.println("• tryAcquire(): Non-blocking attempt");
        System.out.println("• Binary Semaphore (1 permit) = Mutex/Lock");
        System.out.println("• Fair semaphore: FIFO order");
    }
}

