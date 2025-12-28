public class RunnableDemo {
    public static void main(String[] args) {
        System.out.println("=== Three Ways to Use Runnable ===\n");
        
        // Way 1: Create a class that implements Runnable
        Thread t1 = new Thread(new MyTask());
        t1.start();
        
        // Way 2: Anonymous class
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Anonymous class: " + Thread.currentThread().getName());
            }
        });
        t2.start();
        
        // Way 3: Lambda (Java 8+) - Simplest!
        Thread t3 = new Thread(() -> {
            throw new RuntimeException("Task failed!");
            // System.out.println("Lambda: " + Thread.currentThread().getName());
        });
    }
}

class MyTask implements Runnable {
    @Override
    public void run() {
        System.out.println("Separate class: " + Thread.currentThread().getName());
    }
}

