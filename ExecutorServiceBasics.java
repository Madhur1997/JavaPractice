import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceBasics {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== ExecutorService Demo ===\n");
        
        // Create a thread pool with 3 threads
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        System.out.println("Submitting 6 tasks to 3-thread pool...\n");
        
        // Submit 6 tasks
        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId + 
                                 " started on " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Task " + taskId + " finished");
            });
        }
        
        // Shutdown executor
        System.out.println("Main: All tasks submitted, shutting down...\n");
        executor.shutdown(); // Don't accept new tasks
        
        // Wait for all tasks to complete
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        System.out.println("\nMain: All tasks completed!");
    }
}

