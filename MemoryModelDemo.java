public class MemoryModelDemo {
    
    // ============================================
    // STATIC VARIABLES → Method Area / Metaspace
    // ============================================
    static int staticCounter = 0;
    static final String CONSTANT = "I'm in Method Area";
    
    // ============================================
    // INSTANCE VARIABLES → Heap (in each object)
    // ============================================
    int instanceCounter = 0;
    String name;
    
    // Constructor
    public MemoryModelDemo(String name) {
        this.name = name;
    }
    
    // ============================================
    // DEMONSTRATION METHODS
    // ============================================
    
    public void demonstrateMemoryAreas() {
        System.out.println("=== Memory Areas Demo ===\n");
        
        // LOCAL VARIABLES → Stack
        int localPrimitive = 42;
        
        // REFERENCE → Stack, OBJECT → Heap
        MemoryModelDemo localObject = new MemoryModelDemo("Local Object");
        
        System.out.println("1. LOCAL PRIMITIVE (Stack):");
        System.out.println("   int localPrimitive = " + localPrimitive);
        System.out.println("   Location: Stack (current thread)\n");
        
        System.out.println("2. LOCAL OBJECT REFERENCE (Stack → Heap):");
        System.out.println("   Reference 'localObject' → Stack");
        System.out.println("   Actual object → Heap");
        System.out.println("   Object name: " + localObject.name + "\n");
        
        System.out.println("3. INSTANCE VARIABLE (Heap):");
        System.out.println("   this.name = " + this.name);
        System.out.println("   this.instanceCounter = " + this.instanceCounter);
        System.out.println("   Location: Heap (inside 'this' object)\n");
        
        System.out.println("4. STATIC VARIABLE (Method Area):");
        System.out.println("   staticCounter = " + staticCounter);
        System.out.println("   CONSTANT = " + CONSTANT);
        System.out.println("   Location: Method Area / Metaspace\n");
    }
    
    public void demonstrateStackFrames() {
        System.out.println("=== Stack Frames Demo ===\n");
        System.out.println("Calling method chain: main → method1 → method2 → method3");
        System.out.println("Each method gets its own stack frame\n");
        method1();
    }
    
    private void method1() {
        int var1 = 1;
        System.out.println("method1() executing, var1 = " + var1);
        System.out.println("  Stack now has: [main frame] [method1 frame]");
        method2();
        System.out.println("Back to method1()");
    }
    
    private void method2() {
        int var2 = 2;
        System.out.println("  method2() executing, var2 = " + var2);
        System.out.println("  Stack now has: [main frame] [method1 frame] [method2 frame]");
        method3();
        System.out.println("  Back to method2()");
    }
    
    private void method3() {
        int var3 = 3;
        System.out.println("    method3() executing, var3 = " + var3);
        System.out.println("    Stack now has: [main frame] [method1 frame] [method2 frame] [method3 frame]");
        System.out.println("    This is the deepest point!");
    }
    
    public static void demonstrateHeapAllocation() {
        System.out.println("\n=== Heap Allocation Demo ===\n");
        
        // Each 'new' allocates memory on the Heap
        MemoryModelDemo obj1 = new MemoryModelDemo("Object 1");
        MemoryModelDemo obj2 = new MemoryModelDemo("Object 2");
        MemoryModelDemo obj3 = new MemoryModelDemo("Object 3");
        
        obj1.instanceCounter = 10;
        obj2.instanceCounter = 20;
        obj3.instanceCounter = 30;
        
        System.out.println("Created 3 objects in Heap:");
        System.out.println("  obj1: " + obj1.name + ", counter = " + obj1.instanceCounter);
        System.out.println("  obj2: " + obj2.name + ", counter = " + obj2.instanceCounter);
        System.out.println("  obj3: " + obj3.name + ", counter = " + obj3.instanceCounter);
        System.out.println("\nEach object occupies separate space in Heap");
        System.out.println("But references (obj1, obj2, obj3) are on Stack\n");
    }
    
    public static void demonstrateStaticVsInstance() {
        System.out.println("=== Static vs Instance Variables ===\n");
        
        MemoryModelDemo obj1 = new MemoryModelDemo("First");
        MemoryModelDemo obj2 = new MemoryModelDemo("Second");
        
        // Instance variables - each object has its own copy
        obj1.instanceCounter = 100;
        obj2.instanceCounter = 200;
        
        // Static variable - shared by all instances
        staticCounter = 999;
        
        System.out.println("Instance variables (separate for each object):");
        System.out.println("  obj1.instanceCounter = " + obj1.instanceCounter + " (in obj1 on Heap)");
        System.out.println("  obj2.instanceCounter = " + obj2.instanceCounter + " (in obj2 on Heap)");
        
        System.out.println("\nStatic variable (shared by all objects):");
        System.out.println("  MemoryModelDemo.staticCounter = " + staticCounter);
        System.out.println("  obj1.staticCounter = " + obj1.staticCounter);
        System.out.println("  obj2.staticCounter = " + obj2.staticCounter);
        System.out.println("  All point to the SAME variable in Method Area!\n");
    }
    
    public static void demonstrateReferences() {
        System.out.println("=== References: Stack vs Heap ===\n");
        
        // Reference on Stack, Object on Heap
        String str1 = new String("Hello");
        String str2 = str1;  // Copy reference, not object
        
        System.out.println("String str1 = new String(\"Hello\");");
        System.out.println("String str2 = str1;");
        System.out.println();
        
        System.out.println("Memory layout:");
        System.out.println("  Stack:");
        System.out.println("    str1 → (reference to Heap)");
        System.out.println("    str2 → (reference to Heap)");
        System.out.println("           ↓");
        System.out.println("  Heap:");
        System.out.println("    String object: \"Hello\" ← Both point here!");
        System.out.println();
        
        System.out.println("str1 == str2? " + (str1 == str2) + " (same reference)");
        System.out.println("System.identityHashCode(str1): " + System.identityHashCode(str1));
        System.out.println("System.identityHashCode(str2): " + System.identityHashCode(str2));
        System.out.println("(Same hash code = same object in memory)\n");
    }
    
    public static void demonstrateGarbageCollection() {
        System.out.println("=== Garbage Collection ===\n");
        
        MemoryModelDemo obj = new MemoryModelDemo("Temporary");
        System.out.println("Created object: " + obj.name);
        System.out.println("  Object is on Heap");
        System.out.println("  Reference 'obj' is on Stack");
        
        obj = null;  // Remove reference
        System.out.println("\nSet obj = null");
        System.out.println("  Reference on Stack now points to nothing");
        System.out.println("  Object on Heap is now unreachable");
        System.out.println("  Eligible for Garbage Collection!");
        
        // Suggest GC (not guaranteed to run immediately)
        System.gc();
        System.out.println("\nCalled System.gc() - suggesting GC to run");
        System.out.println("  GC will eventually reclaim the unreachable object\n");
    }
    
    public static void visualizeMemoryLayout() {
        System.out.println("=== Complete Memory Layout ===\n");
        
        int x = 10;
        MemoryModelDemo demo = new MemoryModelDemo("Demo Object");
        demo.instanceCounter = 5;
        staticCounter = 100;
        
        System.out.println("After executing:");
        System.out.println("  int x = 10;");
        System.out.println("  MemoryModelDemo demo = new MemoryModelDemo(\"Demo Object\");");
        System.out.println("  demo.instanceCounter = 5;");
        System.out.println("  staticCounter = 100;");
        System.out.println();
        
        System.out.println("┌─────────────────────────────────────────────┐");
        System.out.println("│ Method Area / Metaspace (Shared)           │");
        System.out.println("├─────────────────────────────────────────────┤");
        System.out.println("│ Class: MemoryModelDemo                      │");
        System.out.println("│   - staticCounter = 100                     │");
        System.out.println("│   - CONSTANT = \"I'm in Method Area\"         │");
        System.out.println("│   - Method bytecode                         │");
        System.out.println("└─────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────┐");
        System.out.println("│ Heap (Shared)                               │");
        System.out.println("├─────────────────────────────────────────────┤");
        System.out.println("│ MemoryModelDemo object                      │");
        System.out.println("│   - instanceCounter = 5                     │");
        System.out.println("│   - name = \"Demo Object\"                    │");
        System.out.println("└─────────────────────────────────────────────┘");
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────┐");
        System.out.println("│ Stack (Thread: main)                        │");
        System.out.println("├─────────────────────────────────────────────┤");
        System.out.println("│ Frame: visualizeMemoryLayout()              │");
        System.out.println("│   - x = 10                                  │");
        System.out.println("│   - demo = (reference to object in Heap)    │");
        System.out.println("├─────────────────────────────────────────────┤");
        System.out.println("│ Frame: main()                               │");
        System.out.println("│   - args                                    │");
        System.out.println("└─────────────────────────────────────────────┘");
        System.out.println();
    }
    
    // ============================================
    // MAIN METHOD
    // ============================================
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║   Java Memory Organization Demonstration   ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println();
        
        MemoryModelDemo demo = new MemoryModelDemo("Main Demo");
        
        demo.demonstrateMemoryAreas();
        demo.demonstrateStackFrames();
        demonstrateHeapAllocation();
        demonstrateStaticVsInstance();
        demonstrateReferences();
        demonstrateGarbageCollection();
        visualizeMemoryLayout();
        
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║              Summary                       ║");
        System.out.println("╠════════════════════════════════════════════╣");
        System.out.println("║ • Heap: Objects & instance variables      ║");
        System.out.println("║ • Stack: Local vars & method frames       ║");
        System.out.println("║ • Method Area: Classes & static vars      ║");
        System.out.println("║ • PC Register: Current instruction        ║");
        System.out.println("║ • Native Stack: JNI calls                 ║");
        System.out.println("╚════════════════════════════════════════════╝");
    }
}

