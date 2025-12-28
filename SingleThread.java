public class SingleThread {
    public static void main(String[] args) {
        // This is the MAIN thread - it's running right now
        System.out.println("Main thread: " + Thread.currentThread().getName());
        
        // Let's create a new thread
        Thread myThread = new Thread(() -> {
            // This code will run in a SEPARATE thread
            for (int i = 1; i <= 5; i++) {
                System.out.println("Child thread counting: " + i);
                try {
                    Thread.sleep(500); // Pause for half a second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Child thread finished!");
        });
        
        // Start the thread (this actually begins the execution)
        myThread.start();
        
        // Meanwhile, the main thread continues running
        System.out.println("Main thread continues running...");
        System.out.println("Main thread finished!");
    }
}