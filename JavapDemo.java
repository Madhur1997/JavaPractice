public class JavapDemo {
    // Static field
    private static final String CONSTANT = "Hello";
    
    // Instance fields
    private int count;
    public String name;
    
    // Constructor
    public JavapDemo(String name) {
        this.name = name;
        this.count = 0;
    }
    
    // Public method
    public void increment() {
        count++;
    }
    
    // Private method
    private String getMessage() {
        return CONSTANT + " " + name;
    }
    
    // Static method
    public static void staticMethod() {
        System.out.println("Static method");
    }
    
    // Method with parameters and return type
    public int add(int a, int b) {
        return a + b;
    }
    
    // Generic method
    public <T> T genericMethod(T item) {
        return item;
    }
    
    // Main method
    public static void main(String[] args) {
        JavapDemo demo = new JavapDemo("World");
        demo.increment();
        System.out.println(demo.getMessage());
        System.out.println(demo.add(5, 10));
    }
}

