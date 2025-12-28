import java.util.*;
import java.util.concurrent.*;

public class ConcurrentCollectionsDemo {
    
    // ============================================
    // 1. ConcurrentHashMap Example
    // ============================================
    static class UserCache {
        // Thread-safe map without external synchronization
        private ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
        
        public void put(String key, String value) {
            cache.put(key, value);
            System.out.println("  Added: " + key + " = " + value);
        }
        
        public String get(String key) {
            return cache.get(key);
        }
        
        // Atomic operation: only put if absent
        public String putIfAbsent(String key, String value) {
            String existing = cache.putIfAbsent(key, value);
            if (existing == null) {
                System.out.println("  Added: " + key + " = " + value);
                return value;
            } else {
                System.out.println("  Key exists: " + key + " (kept old value: " + existing + ")");
                return existing;
            }
        }
        
        // Atomic compute operations
        public void incrementCounter(String key) {
            cache.compute(key, (k, v) -> {
                int count = (v == null) ? 1 : Integer.parseInt(v) + 1;
                return String.valueOf(count);
            });
        }
        
        public void printCache() {
            System.out.println("  Cache contents: " + cache);
        }
    }
    
    // ============================================
    // 2. CopyOnWriteArrayList Example
    // ============================================
    static class EventListeners {
        // Thread-safe list optimized for reads
        private CopyOnWriteArrayList<String> listeners = new CopyOnWriteArrayList<>();
        
        public void addListener(String listener) {
            listeners.add(listener);
            System.out.println("  Added listener: " + listener);
        }
        
        public void removeListener(String listener) {
            listeners.remove(listener);
            System.out.println("  Removed listener: " + listener);
        }
        
        public void notifyListeners(String event) {
            // Safe to iterate while other threads modify
            for (String listener : listeners) {
                System.out.println("  Notifying " + listener + " about: " + event);
            }
        }
        
        public int getCount() {
            return listeners.size();
        }
    }
    
    // ============================================
    // 3. BlockingQueue Example (Producer-Consumer)
    // ============================================
    static class ProducerConsumer {
        private BlockingQueue<String> queue = new LinkedBlockingQueue<>(5); // Max 5 items
        
        public void produce(String item) throws InterruptedException {
            System.out.println("  Producer: Adding " + item + " to queue...");
            queue.put(item); // Blocks if queue is full
            System.out.println("  Producer: Added " + item + " (queue size: " + queue.size() + ")");
        }
        
        public String consume() throws InterruptedException {
            System.out.println("  Consumer: Waiting for item...");
            String item = queue.take(); // Blocks if queue is empty
            System.out.println("  Consumer: Got " + item + " (queue size: " + queue.size() + ")");
            return item;
        }
    }
    
    // ============================================
    // 4. ConcurrentLinkedQueue Example
    // ============================================
    static class TaskQueue {
        // Lock-free, unbounded queue
        private ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        
        public void addTask(String task) {
            queue.offer(task);
            System.out.println("  Added task: " + task);
        }
        
        public String pollTask() {
            String task = queue.poll();
            if (task != null) {
                System.out.println("  Polled task: " + task);
            }
            return task;
        }
        
        public boolean isEmpty() {
            return queue.isEmpty();
        }
    }
    
    // ============================================
    // 5. Regular HashMap vs ConcurrentHashMap
    // ============================================
    static class HashMapComparison {
        public static void testRegularHashMap() throws InterruptedException {
            Map<Integer, Integer> map = new HashMap<>();
            
            Thread[] threads = new Thread[10];
            for (int i = 0; i < 10; i++) {
                final int threadId = i;
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 100; j++) {
                        map.put(threadId * 100 + j, j);
                    }
                });
            }
            
            for (Thread t : threads) t.start();
            for (Thread t : threads) t.join();
            
            System.out.println("  Regular HashMap size: " + map.size() + " (might be wrong or crash!)");
        }
        
        public static void testConcurrentHashMap() throws InterruptedException {
            ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();
            
            Thread[] threads = new Thread[10];
            for (int i = 0; i < 10; i++) {
                final int threadId = i;
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 100; j++) {
                        map.put(threadId * 100 + j, j);
                    }
                });
            }
            
            for (Thread t : threads) t.start();
            for (Thread t : threads) t.join();
            
            System.out.println("  ConcurrentHashMap size: " + map.size() + " ✅ (always correct!)");
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Concurrent Collections Demo ===\n");
        
        // ============================================
        // Demo 1: ConcurrentHashMap
        // ============================================
        System.out.println("--- 1. ConcurrentHashMap ---");
        UserCache cache = new UserCache();
        
        Thread t1 = new Thread(() -> cache.put("user1", "Alice"));
        Thread t2 = new Thread(() -> cache.put("user2", "Bob"));
        Thread t3 = new Thread(() -> cache.putIfAbsent("user1", "Charlie")); // Won't add
        
        t1.start(); t2.start(); t1.join(); t2.join();
        Thread.sleep(100);
        t3.start(); t3.join();
        
        cache.printCache();
        
        System.out.println("\nAtomic counter operations:");
        Thread[] counterThreads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            counterThreads[i] = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    cache.incrementCounter("visits");
                }
            });
        }
        for (Thread t : counterThreads) t.start();
        for (Thread t : counterThreads) t.join();
        
        System.out.println("  Total visits: " + cache.get("visits"));
        System.out.println();
        
        // ============================================
        // Demo 2: CopyOnWriteArrayList
        // ============================================
        System.out.println("--- 2. CopyOnWriteArrayList ---");
        EventListeners listeners = new EventListeners();
        
        listeners.addListener("Listener-1");
        listeners.addListener("Listener-2");
        listeners.addListener("Listener-3");
        
        // Thread 1: Notify (iterate)
        Thread notifier = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                listeners.notifyListeners("Event-" + i);
                try { Thread.sleep(100); } catch (InterruptedException e) {}
            }
        });
        
        // Thread 2: Modify list while iterating (safe!)
        Thread modifier = new Thread(() -> {
            try {
                Thread.sleep(50);
                listeners.addListener("Listener-4");
                Thread.sleep(100);
                listeners.removeListener("Listener-2");
            } catch (InterruptedException e) {}
        });
        
        notifier.start();
        modifier.start();
        notifier.join();
        modifier.join();
        
        System.out.println("  Final listener count: " + listeners.getCount());
        System.out.println();
        
        // ============================================
        // Demo 3: BlockingQueue (Producer-Consumer)
        // ============================================
        System.out.println("--- 3. BlockingQueue (Producer-Consumer) ---");
        ProducerConsumer pc = new ProducerConsumer();
        
        // Producer thread
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    pc.produce("Item-" + i);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Consumer thread
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    pc.consume();
                    Thread.sleep(500); // Slower consumer
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        consumer.start();
        Thread.sleep(100);
        producer.start();
        
        producer.join();
        consumer.join();
        System.out.println();
        
        // ============================================
        // Demo 4: ConcurrentLinkedQueue
        // ============================================
        System.out.println("--- 4. ConcurrentLinkedQueue ---");
        TaskQueue taskQueue = new TaskQueue();
        
        // Multiple producers
        Thread[] producers = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int producerId = i + 1;
            producers[i] = new Thread(() -> {
                for (int j = 1; j <= 2; j++) {
                    taskQueue.addTask("Task-P" + producerId + "-" + j);
                }
            });
        }
        
        for (Thread t : producers) t.start();
        for (Thread t : producers) t.join();
        
        // Multiple consumers
        Thread[] consumers = new Thread[2];
        for (int i = 0; i < 2; i++) {
            final int consumerId = i + 1;
            consumers[i] = new Thread(() -> {
                while (!taskQueue.isEmpty()) {
                    taskQueue.pollTask();
                    try { Thread.sleep(100); } catch (InterruptedException e) {}
                }
            });
        }
        
        for (Thread t : consumers) t.start();
        for (Thread t : consumers) t.join();
        System.out.println();
        
        // ============================================
        // Demo 5: HashMap vs ConcurrentHashMap
        // ============================================
        System.out.println("--- 5. HashMap vs ConcurrentHashMap ---");
        
        System.out.println("Testing regular HashMap (NOT thread-safe):");
        try {
            HashMapComparison.testRegularHashMap();
        } catch (Exception e) {
            System.out.println("  HashMap crashed: " + e.getClass().getSimpleName());
        }
        
        System.out.println("\nTesting ConcurrentHashMap (thread-safe):");
        HashMapComparison.testConcurrentHashMap();
        System.out.println();
        
        System.out.println("=== Key Takeaways ===");
        System.out.println("• ConcurrentHashMap: Thread-safe map, no external sync needed");
        System.out.println("• CopyOnWriteArrayList: Read-optimized, safe iteration during modification");
        System.out.println("• BlockingQueue: Producer-consumer pattern with blocking");
        System.out.println("• ConcurrentLinkedQueue: Lock-free, unbounded queue");
        System.out.println("• Always prefer concurrent collections over synchronized wrappers!");
    }
}

