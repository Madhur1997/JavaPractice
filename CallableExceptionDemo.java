import java.util.concurrent.*;

public class CallableExceptionDemo {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        System.out.println("=== Callable Exception Handling ===\n");
        
        // Task that throws exception
        Future<String> future1 = executor.submit(() -> {
            System.out.println("Task 1: About to throw exception...");
            Thread.sleep(500);
            throw new Exception("Something went wrong!"); // ✓ Allowed!
        });
        
        // Task that succeeds
        Future<String> future2 = executor.submit(() -> {
            System.out.println("Task 2: Working normally...");
            Thread.sleep(300);
            return "Success!";
        });
        
        // Try to get results
        System.out.println("\nGetting results...\n");
        
        // Get result from task 2 (succeeds)
        try {
            String result2 = future2.get();
            System.out.println("Task 2 result: " + result2);
        } catch (ExecutionException e) {
            System.out.println("Task 2 failed: " + e.getCause().getMessage());
        }
        
        // Get result from task 1 (throws exception)
        try {
            String result1 = future1.get(); // Exception is thrown HERE!
            System.out.println("Task 1 result: " + result1);
        } catch (ExecutionException e) {
            System.out.println("Task 1 failed: " + e.getCause().getMessage());
            System.out.println("Exception type: " + e.getCause().getClass().getSimpleName());
        }
        
        System.out.println("\nMain: I know which tasks failed!");
        
        executor.shutdown();
    }
}

