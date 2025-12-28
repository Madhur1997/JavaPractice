import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class CallableDemo {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("=== Callable vs Runnable ===\n");
        
        // Runnable - no return value
        Runnable runnable = () -> {
            System.out.println("Runnable: I can't return anything!");
        };
        Thread t1 = new Thread(runnable);
        t1.start();
        t1.join();
        // No way to get a result from runnable!
        
        System.out.println();
        
        // Callable - returns a value!
        Callable<Integer> callable = () -> {
            System.out.println("Callable: I'm doing some calculation...");
            Thread.sleep(1000);
            int result = 42 + 8;
            System.out.println("Callable: Done calculating!");
            return result; // Can return a value!
        };
        
        // Wrap Callable in FutureTask
        FutureTask<Integer> futureTask = new FutureTask<>(callable);
        Thread t2 = new Thread(futureTask);
        t2.start();
        
        System.out.println("Main: Doing other work while callable runs...");
        
        // Get the result (blocks if not ready yet)
        Integer result = futureTask.get();

        System.out.println("Main: Got result from Callable: " + result);
    }
}

