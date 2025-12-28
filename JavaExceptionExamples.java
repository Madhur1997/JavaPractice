import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.text.*;

public class JavaExceptionExamples {
    
    // ============================================
    // CHECKED EXCEPTIONS - Must catch or declare
    // ============================================
    
    // 1. IOException - File/IO operations
    public static void checkedIOException() throws IOException {
        // try(FileInputStream file = new FileInputStream("config.txt")) {
        //     BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
        //     String line = reader.readLine();  // IOException
        // }catch(FileNotFoundException e) {
        //     e.printStackTrace();
        // }
        FileInputStream file = new FileInputStream("config.txt");  // FileNotFoundException (subclass)
        BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
        String line = reader.readLine();  // IOException
    }
    
    // 2. SQLException - Database operations
    public static void checkedSQLException() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM users");  // SQLException
    }
    
    // 3. ClassNotFoundException - Dynamic class loading
    public static void checkedClassNotFoundException() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.example.MyClass");  // ClassNotFoundException
    }
    
    // 4. InterruptedException - Thread operations
    public static void checkedInterruptedException() throws InterruptedException {
        Thread.sleep(1000);  // InterruptedException
    }
    
    // 5. MalformedURLException - URL parsing
    public static void checkedMalformedURLException() throws MalformedURLException {
        URL url = new URL("htp://invalid-url");  // MalformedURLException
    }
    
    // 6. ParseException - Date/text parsing
    public static void checkedParseException() throws java.text.ParseException {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = sdf.parse("invalid-date");  // ParseException
    }
    
    // HOW TO HANDLE CHECKED EXCEPTIONS
    public static void handleCheckedExceptions() {
        // Option 1: Try-Catch
        try {
            FileInputStream file = new FileInputStream("config.txt");
        } catch (FileNotFoundException e) {
            System.out.println("File not found, using defaults");
            // Handle gracefully
        }
        
        // Option 2: Try-Catch-Finally
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("data.txt");
            // Use file
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // Option 3: Try-with-resources (Java 7+)
        try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
            String line = br.readLine();
        } catch (IOException e) {
            System.out.println("Error reading file");
        }
    }
    
    // ============================================
    // UNCHECKED EXCEPTIONS - Optional to catch
    // ============================================
    
    // 1. NullPointerException - Most common!
    public static void uncheckedNullPointer() {
        String str = null;
        int len = str.length();  // 💥 NullPointerException
    }
    
    // 2. ArrayIndexOutOfBoundsException
    public static void uncheckedArrayIndexOutOfBounds() {
        int[] arr = {1, 2, 3};
        int x = arr[10];  // 💥 ArrayIndexOutOfBoundsException
    }
    
    // 3. ClassCastException
    public static void uncheckedClassCast() {
        Object obj = "Hello";
        Integer num = (Integer) obj;  // 💥 ClassCastException
    }
    
    // 4. ArithmeticException
    public static void uncheckedArithmetic() {
        int x = 10 / 0;  // 💥 ArithmeticException (division by zero)
    }
    
    // 5. NumberFormatException
    public static void uncheckedNumberFormat() {
        String str = "abc";
        int num = Integer.parseInt(str);  // 💥 NumberFormatException
    }
    
    // 6. IllegalArgumentException
    public static void uncheckedIllegalArgument() {
        try {
            Thread.sleep(-1000);  // 💥 IllegalArgumentException (negative sleep time)
        } catch (InterruptedException e) {
            System.out.println("InterruptedException: " + e.getMessage());
        }
    }
    
    // 7. IllegalStateException
    public static void uncheckedIllegalState() {
        Iterator<String> iter = new ArrayList<String>().iterator();
        iter.remove();  // 💥 IllegalStateException (no current element)
    }
    
    // 8. IndexOutOfBoundsException
    public static void uncheckedIndexOutOfBounds() {
        List<String> list = new ArrayList<>();
        String item = list.get(5);  // 💥 IndexOutOfBoundsException
    }
    
    // 9. UnsupportedOperationException
    public static void uncheckedUnsupportedOperation() {
        List<String> immutableList = Arrays.asList("a", "b", "c");
        immutableList.add("d");  // 💥 UnsupportedOperationException
    }
    
    // 10. NoSuchElementException
    public static void uncheckedNoSuchElement() {
        Queue<String> queue = new LinkedList<>();
        String item = queue.element();  // 💥 NoSuchElementException (empty queue)
    }
    
    // HOW TO PREVENT UNCHECKED EXCEPTIONS (Fix the code!)
    public static void preventUncheckedExceptions() {
        // 1. Null checks
        String str = null;
        if (str != null) {
            int len = str.length();  // ✓ Safe
        }
        
        // 2. Array bounds checks
        int[] arr = {1, 2, 3};
        int index = 10;
        if (index >= 0 && index < arr.length) {
            int x = arr[index];  // ✓ Safe
        }
        
        // 3. Validation before parsing
        String numStr = "abc";
        try {
            int num = Integer.parseInt(numStr);
        } catch (NumberFormatException e) {
            // Optional: can catch if you expect invalid input
            System.out.println("Invalid number format");
        }
        
        // 4. Check collection size
        List<String> list = new ArrayList<>();
        if (!list.isEmpty()) {
            String first = list.get(0);  // ✓ Safe
        }
    }
    
    // ============================================
    // COMPARISON EXAMPLES
    // ============================================
    
    public static void comparisonExample() {
        // CHECKED: Expected scenario - must handle
        try {
            FileInputStream fis = new FileInputStream("config.txt");
            // File might not exist - that's normal, handle it
        } catch (FileNotFoundException e) {
            System.out.println("Config not found, creating defaults...");
            createDefaultConfig();
        }
        
        // UNCHECKED: Programming error - fix the bug!
        String[] names = {"Alice", "Bob"};
        // This is a BUG - should check array bounds
        // names[5] = "Charlie";  // Don't catch this, fix the code!
        
        // Correct way:
        if (names.length > 5) {
            names[5] = "Charlie";
        } else {
            System.out.println("Array too small, need to resize");
        }
    }
    
    private static void createDefaultConfig() {
        System.out.println("Creating default configuration...");
    }
    
    // ============================================
    // MAIN - DEMO
    // ============================================
    
    public static void main(String[] args) {
        System.out.println("=== Checked Exceptions Demo ===");
        
        // Must handle checked exceptions
        try {
            checkedIOException();
        } catch (IOException e) {
            System.out.println("Caught IOException: " + e.getMessage());
        }
        
        System.out.println("\n=== Unchecked Exceptions Demo ===");
        
        // Can optionally catch unchecked exceptions
        try {
            uncheckedNullPointer();
        } catch (NullPointerException e) {
            System.out.println("Caught NullPointerException: " + e.getMessage());
        }
        
        try {
            uncheckedArrayIndexOutOfBounds();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught ArrayIndexOutOfBoundsException: " + e.getMessage());
        }
        
        try {
            uncheckedArithmetic();
        } catch (ArithmeticException e) {
            System.out.println("Caught ArithmeticException: " + e.getMessage());
        }
        
        try {
            uncheckedNumberFormat();
        } catch (NumberFormatException e) {
            System.out.println("Caught NumberFormatException: " + e.getMessage());
        }
        
        System.out.println("\n=== Prevention is better than catching ===");
        preventUncheckedExceptions();
    }
}

