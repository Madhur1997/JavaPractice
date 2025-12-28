import java.util.concurrent.*;

public class RunnableExceptionDemo {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        System.out.println("=== Runnable Exception Handling ===\n");
        
        // Example 1: Unchecked exception (RuntimeException)
        executor.submit(() -> {
            System.out.println("Task 1: About to throw exception...");
            throw new RuntimeException("Something went wrong!");
            // Exception is SWALLOWED! Main thread doesn't know!
        });
        
        Thread.sleep(500);
        
        // Example 2: Trying to throw checked exception - WON'T COMPILE!
        executor.submit(() -> {
            System.out.println("Task 2: Running...");
            // throw new Exception("Checked exception"); // ❌ Compilation error!
            
            // You MUST catch it yourself
            try {
                throw new Exception("Must catch this!");
            } catch (Exception e) {
                System.out.println("Task 2: Caught exception: " + e.getMessage());
            }
        });
        
        Thread.sleep(500);
        
        System.out.println("\nMain: Still running! (Didn't know about exceptions)");
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}

