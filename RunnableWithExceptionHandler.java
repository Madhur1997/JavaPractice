import java.util.concurrent.*;

public class RunnableWithExceptionHandler {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Runnable with Exception Handler ===\n");
        
        // Create thread factory with exception handler
        ThreadFactory factory = r -> {
            Thread t = new Thread(r);
            t.setUncaughtExceptionHandler((thread, exception) -> {
                System.out.println("⚠️  Caught exception in " + thread.getName());
                System.out.println("   Exception: " + exception.getMessage());
            });
            return t;
        };
        
        ExecutorService customExecutor = Executors.newFixedThreadPool(2, factory);
        
        // Submit Runnable that throws exception
        customExecutor.submit(() -> {
            System.out.println("Task running...");
            throw new RuntimeException("Task failed!");
        });
        
        Thread.sleep(500);
        
        customExecutor.shutdown();
        customExecutor.awaitTermination(5, TimeUnit.SECONDS);
    }
}

