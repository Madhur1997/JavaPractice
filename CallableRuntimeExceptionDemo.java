import java.util.concurrent.*;

public class CallableRuntimeExceptionDemo {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        System.out.println("=== Callable with RuntimeException ===\n");
        
        // Scenario 1: RuntimeException - WITH future.get()
        System.out.println("--- Scenario 1: Calling future.get() ---");
        Future<String> future1 = executor.submit(() -> {
            System.out.println("Task 1: Throwing RuntimeException...");
            Thread.sleep(200);
            throw new RuntimeException("💥 Task 1 failed!");
        });

        try {
            String result = future1.get(); // Exception is caught HERE!
            System.out.println("Task 1 result: " + result);
        } catch (ExecutionException e) {
            // RuntimeException is wrapped in ExecutionException
            System.out.println("✅ Caught ExecutionException!");
            System.out.println("   Cause: " + e.getCause().getClass().getSimpleName());
            System.out.println("   Message: " + e.getCause().getMessage());
        }
        
        Thread.sleep(500);
        
        // Scenario 2: RuntimeException - WITHOUT future.get()
        System.out.println("\n--- Scenario 2: NOT calling future.get() ---");
        Future<String> future2 = executor.submit(() -> {
            System.out.println("Task 2: Throwing RuntimeException...");
            Thread.sleep(200);
            throw new RuntimeException("💥 Task 2 failed!");
        });
        
        // NOT calling future2.get() - exception is SWALLOWED!
        Thread.sleep(500);
        System.out.println("⚠️  Task 2's exception was SWALLOWED (didn't call .get())");
        
        // Scenario 3: NullPointerException (common RuntimeException)
        System.out.println("\n--- Scenario 3: NullPointerException ---");
        Future<Integer> future3 = executor.submit(() -> {
            System.out.println("Task 3: Causing NullPointerException...");
            String str = null;
            return str.length(); // NPE!
        });
        
        try {
            Integer result = future3.get();
            System.out.println("Task 3 result: " + result);
        } catch (ExecutionException e) {
            System.out.println("✅ Caught ExecutionException!");
            System.out.println("   Cause: " + e.getCause().getClass().getSimpleName());
            System.out.println("   Message: " + e.getCause().getMessage());
            System.out.println("\n   Stack trace of the original exception:");
            e.getCause().printStackTrace(System.out);
        }
        
        // Scenario 4: Multiple futures - some succeed, some fail
        System.out.println("\n--- Scenario 4: Multiple tasks ---");
        Future<String> f1 = executor.submit(() -> "Task A: ✅ Success");
        Future<String> f2 = executor.submit(() -> {
            throw new IllegalStateException("Task B: ❌ Failed");
        });
        Future<String> f3 = executor.submit(() -> "Task C: ✅ Success");
        Future<String> f4 = executor.submit(() -> {
            throw new ArithmeticException("Task D: ❌ Division by zero");
        });
        
        // Check each result
        Future<String>[] futures = new Future[]{f1, f2, f3, f4};
        for (int i = 0; i < futures.length; i++) {
            try {
                String result = futures[i].get();
                System.out.println(result);
            } catch (ExecutionException e) {
                System.out.println("Task " + (char)('A' + i) + ": ❌ " + 
                    e.getCause().getClass().getSimpleName() + " - " + 
                    e.getCause().getMessage());
            }
        }
        
        System.out.println("\n[Main]: ✅ Main thread handled all exceptions properly!");
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}

