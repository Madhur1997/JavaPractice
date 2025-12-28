public class DaemonThreadDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread daemonThread = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                System.out.println("Daemon counting: " + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Daemon finished"); // This might NOT print!
        });
        
        Thread t1 = new Thread();
        daemonThread.setDaemon(true); // Make it a daemon thread
        daemonThread.start();
        
        Thread.sleep(1500); // Main sleeps for 1.5 seconds
        System.out.println("Main thread exiting...");
        // When main exits, daemon thread is KILLED immediately!
    }
}