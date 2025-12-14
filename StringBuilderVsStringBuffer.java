public class StringBuilderVsStringBuffer {
    
    // ============================================
    // KEY DIFFERENCES
    // ============================================
    
    /*
     * StringBuilder vs StringBuffer vs String
     * 
     * String:
     * - Immutable (cannot be changed)
     * - Thread-safe (because immutable)
     * - Slow for concatenation (creates new objects)
     * 
     * StringBuilder:
     * - Mutable (can be changed)
     * - NOT thread-safe (no synchronization)
     * - Fast (no synchronization overhead)
     * - Use in single-threaded scenarios
     * 
     * StringBuffer:
     * - Mutable (can be changed)
     * - Thread-safe (synchronized methods)
     * - Slower than StringBuilder (synchronization overhead)
     * - Use in multi-threaded scenarios
     */
    
    // ============================================
    // 1. THREAD SAFETY
    // ============================================
    
    public static void demonstrateThreadSafety() throws InterruptedException {
        System.out.println("=== Thread Safety Demo ===\n");
        
        // StringBuilder - NOT thread-safe
        StringBuilder sb = new StringBuilder();
        
        // StringBuffer - Thread-safe
        StringBuffer sbf = new StringBuffer();
        
        // Create multiple threads modifying the same objects
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                sb.append("A");
                sbf.append("A");
            }
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                sb.append("B");
                sbf.append("B");
            }
        });
        
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        
        System.out.println("StringBuilder length (might be wrong!): " + sb.length());
        System.out.println("Expected: 2000");
        System.out.println("StringBuffer length (always correct): " + sbf.length());
        System.out.println("Expected: 2000\n");
        
        // StringBuilder can have race conditions in multi-threaded environments!
        // StringBuffer will always be correct
    }
    
    // ============================================
    // 2. PERFORMANCE COMPARISON
    // ============================================
    
    public static void demonstratePerformance() {
        System.out.println("=== Performance Demo ===\n");
        
        int iterations = 100000;
        
        // String concatenation (slowest - creates many objects)
        long start = System.currentTimeMillis();
        String str = "";
        for (int i = 0; i < 10000; i++) {  // Reduced for String
            str += "x";  // Creates new String object each time!
        }
        long stringTime = System.currentTimeMillis() - start;
        
        // StringBuilder (fastest - mutable, no synchronization)
        start = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.append("x");
        }
        long sbTime = System.currentTimeMillis() - start;
        
        // StringBuffer (slower - mutable, synchronized)
        start = System.currentTimeMillis();
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < iterations; i++) {
            sbf.append("x");
        }
        long sbfTime = System.currentTimeMillis() - start;
        
        System.out.println("String concatenation (10K iterations): " + stringTime + "ms");
        System.out.println("StringBuilder (100K iterations): " + sbTime + "ms");
        System.out.println("StringBuffer (100K iterations): " + sbfTime + "ms");
        System.out.println("\nStringBuilder is ~" + (sbfTime / (double) sbTime) + "x faster than StringBuffer");
        System.out.println();
    }
    
    // ============================================
    // 3. COMMON METHODS (Same for Both)
    // ============================================
    
    public static void demonstrateCommonMethods() {
        System.out.println("=== Common Methods Demo ===\n");
        
        StringBuilder sb = new StringBuilder("Hello");
        
        // append() - Add to end
        sb.append(" World");
        System.out.println("After append: " + sb);  // Hello World
        
        // insert() - Insert at position
        sb.insert(6, "Beautiful ");
        System.out.println("After insert: " + sb);  // Hello Beautiful World
        
        // delete() - Remove characters
        sb.delete(6, 16);
        System.out.println("After delete: " + sb);  // Hello World
        
        // reverse() - Reverse the string
        sb.reverse();
        System.out.println("After reverse: " + sb);  // dlroW olleH
        sb.reverse();  // Reverse back
        
        // replace() - Replace range
        sb.replace(0, 5, "Hi");
        System.out.println("After replace: " + sb);  // Hi World
        
        // capacity() - Current capacity
        System.out.println("Capacity: " + sb.capacity());
        
        // length() - Current length
        System.out.println("Length: " + sb.length());
        
        // charAt() - Get character at index
        System.out.println("Char at 0: " + sb.charAt(0));
        
        // substring() - Extract substring
        System.out.println("Substring(0,2): " + sb.substring(0, 2));
        
        // toString() - Convert to String
        String result = sb.toString();
        System.out.println("Final string: " + result);
        System.out.println();
    }
    
    // ============================================
    // 4. IMMUTABLE STRING PROBLEM
    // ============================================
    
    public static void demonstrateStringImmutability() {
        System.out.println("=== String Immutability Problem ===\n");
        
        // String is immutable - each concatenation creates a new object
        String s = "A";
        System.out.println("Original string address: " + System.identityHashCode(s));
        
        s = s + "B";  // Creates NEW String object!
        System.out.println("After concat address: " + System.identityHashCode(s));
        
        s = s + "C";  // Creates ANOTHER new String object!
        System.out.println("After concat address: " + System.identityHashCode(s));
        
        // Each concatenation:
        // 1. Creates a new String object
        // 2. Copies old content
        // 3. Adds new content
        // 4. Old object becomes garbage
        
        System.out.println("\nThis creates 3 String objects for just 2 concatenations!\n");
        
        // StringBuilder is mutable - modifies the same object
        StringBuilder sb = new StringBuilder("A");
        System.out.println("StringBuilder address: " + System.identityHashCode(sb));
        
        sb.append("B");  // Modifies same object!
        System.out.println("After append address: " + System.identityHashCode(sb));
        
        sb.append("C");  // Still same object!
        System.out.println("After append address: " + System.identityHashCode(sb));
        
        System.out.println("\nStringBuilder uses 1 object for all operations!\n");
    }
    
    // ============================================
    // 5. WHEN TO USE WHAT
    // ============================================
    
    // Use StringBuilder - Single thread (most common)
    public static String buildMessage(String[] parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part).append(" ");
        }
        return sb.toString().trim();
    }
    
    // Use StringBuffer - Multiple threads
    public static class ThreadSafeLogger {
        private StringBuffer log = new StringBuffer();
        
        public void addLog(String message) {
            // Multiple threads can safely call this
            log.append(message).append("\n");
        }
        
        public String getLog() {
            return log.toString();
        }
    }
    
    // Use String - Small, one-time concatenations
    public static String formatName(String first, String last) {
        return first + " " + last;  // OK - only one concatenation
    }
    
    // BAD - Don't use String in loops!
    public static String buildStringWrong(int n) {
        String result = "";
        for (int i = 0; i < n; i++) {
            result += i + ",";  // ❌ Creates n String objects!
        }
        return result;
    }
    
    // GOOD - Use StringBuilder in loops
    public static String buildStringRight(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(i).append(",");  // ✓ Modifies same object
        }
        return sb.toString();
    }
    
    // ============================================
    // 6. METHOD CHAINING
    // ============================================
    
    public static void demonstrateMethodChaining() {
        System.out.println("=== Method Chaining Demo ===\n");
        
        // Both support method chaining
        String result = new StringBuilder()
            .append("Hello")
            .append(" ")
            .append("World")
            .append("!")
            .reverse()
            .toString();
        
        System.out.println("Chained result: " + result);
        System.out.println();
    }
    
    // ============================================
    // 7. CAPACITY AND GROWTH
    // ============================================
    
    public static void demonstrateCapacity() {
        System.out.println("=== Capacity Demo ===\n");
        
        // Default capacity is 16
        StringBuilder sb = new StringBuilder();
        System.out.println("Initial capacity: " + sb.capacity());  // 16
        System.out.println("Initial length: " + sb.length());      // 0
        
        // Capacity with initial string
        StringBuilder sb2 = new StringBuilder("Hello");
        System.out.println("\nWith 'Hello':");
        System.out.println("Capacity: " + sb2.capacity());  // 21 (16 + 5)
        System.out.println("Length: " + sb2.length());      // 5
        
        // Capacity grows automatically
        sb.append("123456789012345678901234567890");  // More than 16 chars
        System.out.println("\nAfter exceeding capacity:");
        System.out.println("New capacity: " + sb.capacity());  // Auto-grows
        System.out.println("Length: " + sb.length());
        
        // Pre-allocate if you know the size
        StringBuilder sb3 = new StringBuilder(1000);  // Avoid resizing
        System.out.println("\nPre-allocated capacity: " + sb3.capacity());
        System.out.println();
    }
    
    // ============================================
    // COMPARISON TABLE
    // ============================================
    
    public static void printComparisonTable() {
        System.out.println("╔═══════════════════════╦═══════════╦═══════════════╦══════════════╗");
        System.out.println("║ Feature               ║ String    ║ StringBuilder ║ StringBuffer ║");
        System.out.println("╠═══════════════════════╬═══════════╬═══════════════╬══════════════╣");
        System.out.println("║ Mutable?              ║ No        ║ Yes           ║ Yes          ║");
        System.out.println("║ Thread-Safe?          ║ Yes       ║ No            ║ Yes          ║");
        System.out.println("║ Synchronized?         ║ N/A       ║ No            ║ Yes          ║");
        System.out.println("║ Performance           ║ Slow      ║ Fast          ║ Medium       ║");
        System.out.println("║ When to use?          ║ Immutable ║ Single-thread ║ Multi-thread ║");
        System.out.println("║ Since version         ║ 1.0       ║ 1.5 (Java 5)  ║ 1.0          ║");
        System.out.println("╚═══════════════════════╩═══════════╩═══════════════╩══════════════╝");
        System.out.println();
    }
    
    // ============================================
    // MAIN
    // ============================================
    
    public static void main(String[] args) throws InterruptedException {
        printComparisonTable();
        demonstrateCommonMethods();
        demonstrateStringImmutability();
        demonstratePerformance();
        demonstrateThreadSafety();
        demonstrateMethodChaining();
        demonstrateCapacity();
        
        System.out.println("=== Best Practices ===\n");
        System.out.println("✓ Use StringBuilder for single-threaded string building");
        System.out.println("✓ Use StringBuffer only when multiple threads access it");
        System.out.println("✓ Use String for simple concatenations (1-2 operations)");
        System.out.println("✗ Never use String concatenation in loops!");
        System.out.println("✓ Pre-allocate capacity if you know the size");
    }
}

