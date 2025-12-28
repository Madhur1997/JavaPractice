import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

public class ExecutorWithCallable {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("=== ExecutorService with Callable ===\n");
        
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        // Create tasks that return results
        List<Future<Integer>> futures = new ArrayList<>();
        
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            
            // Submit Callable that returns a value
            Future<Integer> future = executor.submit(() -> {
                System.out.println("Task " + taskId + " computing...");
                Thread.sleep(1000);
                int result = taskId * 10;
                System.out.println("Task " + taskId + " result: " + result);
                return result;
            });
            
            futures.add(future);
        }
        
        System.out.println("\nMain: All tasks submitted, waiting for results...\n");
        
        // Collect all results
        int sum = 0;
        for (int i = 0; i < futures.size(); i++) {
            Integer result = futures.get(i).get(); // Block and wait for result
            sum += result;
            System.out.println("Collected result from task " + (i+1) + ": " + result);
        }
        
        System.out.println("\nSum of all results: " + sum);
        
        executor.shutdown();
    }
}

