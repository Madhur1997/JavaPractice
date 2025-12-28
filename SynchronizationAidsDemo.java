import java.util.concurrent.*;

public class SynchronizationAidsDemo {
    
    // ============================================
    // 1. CountDownLatch Example
    // ============================================
    static class ServiceStarter {
        public static void startServices() throws InterruptedException {
            CountDownLatch latch = new CountDownLatch(3); // Wait for 3 services
            
            // Start Database Service
            new Thread(() -> {
                try {
                    System.out.println("Database service starting...");
                    Thread.sleep(1000);
                    System.out.println("✅ Database service ready!");
                    latch.countDown(); // Decrement count
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "DB-Service").start();
            
            // Start Cache Service
            new Thread(() -> {
                try {
                    System.out.println("Cache service starting...");
                    Thread.sleep(1500);
                    System.out.println("✅ Cache service ready!");
                    latch.countDown(); // Decrement count
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Cache-Service").start();
            
            // Start API Service
            new Thread(() -> {
                try {
                    System.out.println("API service starting...");
                    Thread.sleep(800);
                    System.out.println("✅ API service ready!");
                    latch.countDown(); // Decrement count
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "API-Service").start();
            
            System.out.println("\nMain thread waiting for all services...");
            latch.await(); // Block until count reaches 0
            System.out.println("🚀 All services ready! Application started!\n");
        }
    }
    
    // ============================================
    // 2. CyclicBarrier Example
    // ============================================
    static class ParallelSearch {
        public static void search() throws InterruptedException {
            int numThreads = 3;
            CyclicBarrier barrier = new CyclicBarrier(numThreads, () -> {
                // This runs when all threads reach the barrier
                System.out.println("    ⏸️  All threads reached checkpoint!\n");
            });
            
            Thread[] threads = new Thread[numThreads];
            
            for (int i = 0; i < numThreads; i++) {
                final int threadId = i + 1;
                threads[i] = new Thread(() -> {
                    try {
                        // Phase 1: Search
                        System.out.println("Thread-" + threadId + ": Searching...");
                        Thread.sleep((long)(Math.random() * 1000 + 500));
                        System.out.println("Thread-" + threadId + ": Search complete!");
                        
                        barrier.await(); // Wait for others (Checkpoint 1)
                        
                        // Phase 2: Process
                        System.out.println("Thread-" + threadId + ": Processing results...");
                        Thread.sleep((long)(Math.random() * 1000 + 500));
                        System.out.println("Thread-" + threadId + ": Processing complete!");
                        
                        barrier.await(); // Wait for others (Checkpoint 2)
                        
                        // Phase 3: Report
                        System.out.println("Thread-" + threadId + ": Generating report...");
                        Thread.sleep((long)(Math.random() * 1000 + 500));
                        System.out.println("Thread-" + threadId + ": Report ready!");
                        
                        barrier.await(); // Wait for others (Checkpoint 3)
                        
                        System.out.println("Thread-" + threadId + ": All phases complete! ✅");
                        
                    } catch (InterruptedException | BrokenBarrierException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            
            for (Thread t : threads) t.start();
            for (Thread t : threads) t.join();
        }
    }
    
    // ============================================
    // 3. Phaser Example
    // ============================================
    static class MultiPhaseTask {
        public static void execute() throws InterruptedException {
            Phaser phaser = new Phaser(3); // 3 parties
            
            for (int i = 1; i <= 3; i++) {
                final int workerId = i;
                new Thread(() -> {
                    try {
                        // Phase 0: Setup
                        System.out.println("Worker-" + workerId + ": Setting up...");
                        Thread.sleep(500);
                        System.out.println("Worker-" + workerId + ": Setup done! (Phase " + phaser.getPhase() + ")");
                        phaser.arriveAndAwaitAdvance(); // Wait for phase 0 to complete
                        
                        // Phase 1: Execute
                        System.out.println("Worker-" + workerId + ": Executing...");
                        Thread.sleep(700);
                        System.out.println("Worker-" + workerId + ": Execution done! (Phase " + phaser.getPhase() + ")");
                        phaser.arriveAndAwaitAdvance(); // Wait for phase 1 to complete
                        
                        // Phase 2: Cleanup
                        System.out.println("Worker-" + workerId + ": Cleaning up...");
                        Thread.sleep(400);
                        System.out.println("Worker-" + workerId + ": Cleanup done! (Phase " + phaser.getPhase() + ")");
                        phaser.arriveAndDeregister(); // Deregister from phaser
                        
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }, "Worker-" + i).start();
            }
            
            // Main thread monitors phases
            Thread.sleep(6000);
            System.out.println("\nMain: Final phase = " + phaser.getPhase());
            System.out.println("Main: Registered parties = " + phaser.getRegisteredParties() + "\n");
        }
    }
    
    // ============================================
    // 4. Exchanger Example
    // ============================================
    static class DataExchange {
        public static void demonstrate() throws InterruptedException {
            Exchanger<String> exchanger = new Exchanger<>();
            
            // Producer thread
            Thread producer = new Thread(() -> {
                try {
                    String data = "Data from Producer";
                    System.out.println("Producer: Sending - " + data);
                    
                    // Exchange data with consumer
                    String received = exchanger.exchange(data);
                    
                    System.out.println("Producer: Received - " + received);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Producer");
            
            // Consumer thread
            Thread consumer = new Thread(() -> {
                try {
                    Thread.sleep(500); // Simulate some work
                    String data = "Data from Consumer";
                    System.out.println("Consumer: Sending - " + data);
                    
                    // Exchange data with producer
                    String received = exchanger.exchange(data);
                    
                    System.out.println("Consumer: Received - " + received);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Consumer");
            
            producer.start();
            consumer.start();
            producer.join();
            consumer.join();
            System.out.println();
        }
    }
    
    // ============================================
    // 5. CountDownLatch vs CyclicBarrier
    // ============================================
    static class Comparison {
        public static void demonstrate() throws InterruptedException {
            System.out.println("CountDownLatch:");
            System.out.println("  • One-time use (cannot reset)");
            System.out.println("  • Main thread waits for workers");
            System.out.println("  • Workers don't wait for each other");
            System.out.println();
            
            System.out.println("CyclicBarrier:");
            System.out.println("  • Reusable (cyclic)");
            System.out.println("  • Workers wait for each other");
            System.out.println("  • All proceed together after barrier");
            System.out.println();
            
            System.out.println("Phaser:");
            System.out.println("  • More flexible than both");
            System.out.println("  • Dynamic party registration");
            System.out.println("  • Multiple phases");
            System.out.println();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Synchronization Aids Demo ===\n");
        
        // ============================================
        // Demo 1: CountDownLatch
        // ============================================
        System.out.println("--- 1. CountDownLatch (Wait for N tasks) ---");
        ServiceStarter.startServices();
        
        // ============================================
        // Demo 2: CyclicBarrier
        // ============================================
        System.out.println("--- 2. CyclicBarrier (Sync points) ---");
        ParallelSearch.search();
        System.out.println();
        
        // ============================================
        // Demo 3: Phaser
        // ============================================
        System.out.println("--- 3. Phaser (Multi-phase coordination) ---");
        MultiPhaseTask.execute();
        
        // ============================================
        // Demo 4: Exchanger
        // ============================================
        System.out.println("--- 4. Exchanger (Thread data exchange) ---");
        DataExchange.demonstrate();
        
        // ============================================
        // Demo 5: Comparison
        // ============================================
        System.out.println("--- 5. Comparison ---");
        Comparison.demonstrate();
        
        System.out.println("=== Key Takeaways ===");
        System.out.println("• CountDownLatch: Wait for N operations to complete");
        System.out.println("• CyclicBarrier: N threads wait for each other (reusable)");
        System.out.println("• Phaser: Advanced multi-phase synchronization");
        System.out.println("• Exchanger: Two threads exchange data");
    }
}

