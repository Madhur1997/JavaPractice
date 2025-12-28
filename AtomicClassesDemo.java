import java.util.concurrent.atomic.*;
import java.util.concurrent.*;

public class AtomicClassesDemo {
    
    // ============================================
    // 1. AtomicInteger Example
    // ============================================
    static class AtomicCounter {
        private AtomicInteger count = new AtomicInteger(0);
        
        public void increment() {
            count.incrementAndGet(); // Atomic ++count
        }
        
        public void decrement() {
            count.decrementAndGet(); // Atomic --count
        }
        
        public void add(int value) {
            count.addAndGet(value); // Atomic count += value
        }
        
        public int get() {
            return count.get();
        }
        
        public void compareAndSetExample() {
            int current = count.get();
            System.out.println("Current value: " + current);
            
            // If value is still 'current', update to 100
            boolean success = count.compareAndSet(current, 100);
            System.out.println("CAS to 100: " + (success ? "SUCCESS" : "FAILED"));
            System.out.println("New value: " + count.get());
        }
    }
    
    // Compare with NON-atomic counter
    static class NonAtomicCounter {
        private int count = 0;
        
        public void increment() {
            count++; // NOT thread-safe!
        }
        
        public int get() {
            return count;
        }
    }
    
    // ============================================
    // 2. AtomicLong Example
    // ============================================
    static class Statistics {
        private AtomicLong totalRequests = new AtomicLong(0);
        private AtomicLong totalTime = new AtomicLong(0);
        
        public void recordRequest(long duration) {
            totalRequests.incrementAndGet();
            totalTime.addAndGet(duration);
        }
        
        public double getAverageTime() {
            long requests = totalRequests.get();
            return requests > 0 ? (double) totalTime.get() / requests : 0;
        }
        
        public void printStats() {
            System.out.println("Total requests: " + totalRequests.get());
            System.out.println("Total time: " + totalTime.get() + "ms");
            System.out.println("Average time: " + String.format("%.2f", getAverageTime()) + "ms");
        }
    }
    
    // ============================================
    // 3. AtomicBoolean Example
    // ============================================
    static class Service {
        private AtomicBoolean isRunning = new AtomicBoolean(false);
        
        public boolean start() {
            // Only start if not already running
            if (isRunning.compareAndSet(false, true)) {
                System.out.println("Service started!");
                return true;
            } else {
                System.out.println("Service already running!");
                return false;
            }
        }
        
        public void stop() {
            isRunning.set(false);
            System.out.println("Service stopped!");
        }
        
        public boolean isRunning() {
            return isRunning.get();
        }
    }
    
    // ============================================
    // 4. AtomicReference Example
    // ============================================
    static class Configuration {
        private AtomicReference<Config> config = new AtomicReference<>(new Config("v1", 100));
        
        public void updateConfig(Config newConfig) {
            Config old = config.getAndSet(newConfig);
            System.out.println("Updated config from " + old + " to " + newConfig);
        }
        
        public void compareAndUpdate(Config expected, Config newConfig) {
            if (config.compareAndSet(expected, newConfig)) {
                System.out.println("Successfully updated to: " + newConfig);
            } else {
                System.out.println("Update failed - config changed by another thread!");
            }
        }
        
        public Config getConfig() {
            return config.get();
        }
    }
    
    static class Config {
        final String version;
        final int maxConnections;
        
        Config(String version, int maxConnections) {
            this.version = version;
            this.maxConnections = maxConnections;
        }
        
        @Override
        public String toString() {
            return "Config{v=" + version + ", max=" + maxConnections + "}";
        }
    }
    
    // ============================================
    // 5. AtomicIntegerArray Example
    // ============================================
    static class Histogram {
        private AtomicIntegerArray buckets = new AtomicIntegerArray(5);
        
        public void increment(int index) {
            buckets.incrementAndGet(index);
        }
        
        public void print() {
            System.out.print("Histogram: [");
            for (int i = 0; i < buckets.length(); i++) {
                System.out.print(buckets.get(i) + (i < buckets.length() - 1 ? ", " : ""));
            }
            System.out.println("]");
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Atomic Classes Demo ===\n");
        
        // ============================================
        // Demo 1: AtomicInteger vs Regular int
        // ============================================
        System.out.println("--- 1. AtomicInteger vs Regular int ---");
        
        AtomicCounter atomicCounter = new AtomicCounter();
        NonAtomicCounter nonAtomicCounter = new NonAtomicCounter();
        
        // 10 threads, each incrementing 1000 times
        int numThreads = 10;
        int iterations = 1000;
        
        Thread[] threads1 = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads1[i] = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    atomicCounter.increment();
                    nonAtomicCounter.increment();
                }
            });
        }
        
        for (Thread t : threads1) t.start();
        for (Thread t : threads1) t.join();
        
        System.out.println("Expected count: " + (numThreads * iterations));
        System.out.println("Atomic counter: " + atomicCounter.get() + " ✅");
        System.out.println("Non-atomic counter: " + nonAtomicCounter.get() + " ❌ (likely wrong!)");
        System.out.println();
        
        // ============================================
        // Demo 2: Compare-And-Set (CAS)
        // ============================================
        System.out.println("--- 2. Compare-And-Set (CAS) ---");
        AtomicCounter casCounter = new AtomicCounter();
        casCounter.add(50);
        casCounter.compareAndSetExample();
        System.out.println();
        
        // ============================================
        // Demo 3: AtomicLong for Statistics
        // ============================================
        System.out.println("--- 3. AtomicLong for Statistics ---");
        Statistics stats = new Statistics();
        
        Thread[] threads2 = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads2[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    stats.recordRequest(10 + (long)(Math.random() * 90)); // 10-100ms
                }
            });
        }
        
        for (Thread t : threads2) t.start();
        for (Thread t : threads2) t.join();
        
        stats.printStats();
        System.out.println();
        
        // ============================================
        // Demo 4: AtomicBoolean for Service Control
        // ============================================
        System.out.println("--- 4. AtomicBoolean for Service Control ---");
        Service service = new Service();
        
        // Multiple threads trying to start service
        Thread t1 = new Thread(() -> service.start());
        Thread t2 = new Thread(() -> service.start());
        Thread t3 = new Thread(() -> service.start());
        
        t1.start(); t2.start(); t3.start();
        t1.join(); t2.join(); t3.join();
        
        System.out.println("Is running? " + service.isRunning());
        service.stop();
        System.out.println();
        
        // ============================================
        // Demo 5: AtomicReference for Config
        // ============================================
        System.out.println("--- 5. AtomicReference for Configuration ---");
        Configuration configuration = new Configuration();
        
        System.out.println("Initial config: " + configuration.getConfig());
        
        Config newConfig = new Config("v2", 200);
        configuration.updateConfig(newConfig);
        
        // Try compare-and-set
        Config expected = new Config("v2", 200); // Different object, same values
        Config newerConfig = new Config("v3", 300);
        configuration.compareAndUpdate(expected, newerConfig); // Will fail (reference equality)
        
        configuration.compareAndUpdate(newConfig, newerConfig); // Will succeed
        System.out.println();
        
        // ============================================
        // Demo 6: AtomicIntegerArray for Histogram
        // ============================================
        System.out.println("--- 6. AtomicIntegerArray for Histogram ---");
        Histogram histogram = new Histogram();
        
        Thread[] threads3 = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads3[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    int bucket = (int)(Math.random() * 5);
                    histogram.increment(bucket);
                }
            });
        }
        
        for (Thread t : threads3) t.start();
        for (Thread t : threads3) t.join();
        
        histogram.print();
        System.out.println();
        
        // ============================================
        // Demo 7: Performance Comparison
        // ============================================
        System.out.println("--- 7. Performance Test ---");
        
        // Atomic approach
        AtomicInteger atomicPerf = new AtomicInteger(0);
        long startAtomic = System.nanoTime();
        
        Thread[] perfThreads1 = new Thread[5];
        for (int i = 0; i < 5; i++) {
            perfThreads1[i] = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    atomicPerf.incrementAndGet();
                }
            });
        }
        for (Thread t : perfThreads1) t.start();
        for (Thread t : perfThreads1) t.join();
        
        long atomicTime = System.nanoTime() - startAtomic;
        
        // Synchronized approach
        Object lock = new Object();
        int[] syncCounter = {0};
        long startSync = System.nanoTime();
        
        Thread[] perfThreads2 = new Thread[5];
        for (int i = 0; i < 5; i++) {
            perfThreads2[i] = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    synchronized(lock) {
                        syncCounter[0]++;
                    }
                }
            });
        }
        for (Thread t : perfThreads2) t.start();
        for (Thread t : perfThreads2) t.join();
        
        long syncTime = System.nanoTime() - startSync;
        
        System.out.println("Atomic approach: " + atomicTime / 1_000_000 + "ms");
        System.out.println("Synchronized approach: " + syncTime / 1_000_000 + "ms");
        System.out.println("Atomic is " + String.format("%.2f", (double)syncTime / atomicTime) + "x faster!");
        
        System.out.println("\n=== Key Takeaways ===");
        System.out.println("• Atomic classes: Lock-free thread safety");
        System.out.println("• AtomicInteger/Long: Counters, IDs, statistics");
        System.out.println("• AtomicBoolean: Flags, state switches");
        System.out.println("• AtomicReference: Thread-safe object references");
        System.out.println("• CAS: Compare-And-Set for conditional updates");
        System.out.println("• Faster than locks for simple operations!");
    }
}

