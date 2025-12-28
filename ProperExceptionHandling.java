import java.util.concurrent.*;
import java.util.*;

public class ProperExceptionHandling {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        System.out.println("=== Proper Exception Handling ===\n");
        
        List<Future<Integer>> futures = new ArrayList<>();
        
        // Submit multiple tasks, some will fail
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            Future<Integer> future = executor.submit(new Callable<Integer> () {
                @Override
                public Integer call() throws IllegalStateException {
                    System.out.println("Task " + taskId + " started");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (taskId == 3) {
                        throw new IllegalStateException("Task 3 failed!");
                    }
                    return taskId * 10;
                }
            });
            futures.add(future);
        }
        
        // Collect results and handle exceptions properly
        System.out.println("\nCollecting results:\n");
        
        int successCount = 0;
        int failureCount = 0;
        
        for (int i = 0; i < futures.size(); i++) {
            try {
                Integer result = futures.get(i).get();
                System.out.println("✓ Task " + (i+1) + " succeeded: " + result);
                successCount++;
                
            } catch (ExecutionException e) {
                // Task threw an exception
                System.out.println("✗ Task " + (i+1) + " failed: " + 
                                 e.getCause().getMessage());
                failureCount++;
                
            } catch (InterruptedException e) {
                // Task was interrupted
                System.out.println("✗ Task " + (i+1) + " was interrupted");
                Thread.currentThread().interrupt();
                failureCount++;
            }
        }
        
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Summary:");
        System.out.println("  Succeeded: " + successCount);
        System.out.println("  Failed: " + failureCount);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━");
        
        executor.shutdown();
    }
}

