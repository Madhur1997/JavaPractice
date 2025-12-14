import java.util.*;

public class GenerationalGCDemo {
    
    // Static field → Will eventually be in Old Generation (long-lived)
    private static List<String> longLivedObjects = new ArrayList<>();
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║    Generational GC Demonstration                 ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");
        
        demonstrateShortLivedObjects();
        demonstrateLongLivedObjects();
        demonstrateObjectPromotion();
        visualizeGenerations();
        showGCBehavior();
        
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║               Summary                            ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║ Young Gen: Fast GC, most objects die            ║");
        System.out.println("║ Old Gen: Slow GC, long-lived objects            ║");
        System.out.println("║ Promotion: Age threshold (~15 Minor GCs)        ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
    }
    
    // ============================================
    // 1. SHORT-LIVED OBJECTS (Young Gen)
    // ============================================
    
    public static void demonstrateShortLivedObjects() {
        System.out.println("=== 1. Short-Lived Objects (Young Gen) ===\n");
        
        System.out.println("Creating many temporary objects...");
        
        for (int i = 0; i < 1000; i++) {
            // These objects are created and immediately become garbage
            String temp = "Temporary string " + i;
            StringBuilder sb = new StringBuilder();
            sb.append(temp);
            int[] array = new int[10];
            
            // When this loop iteration ends, all these objects are unreachable
            // They stay in Young Gen (Eden) and die in next Minor GC
        }
        
        System.out.println("✓ Created 1000 iterations of temporary objects");
        System.out.println("  Location: Eden Space (Young Generation)");
        System.out.println("  Lifetime: Dies in next Minor GC");
        System.out.println("  Never promoted to Old Generation\n");
    }
    
    // ============================================
    // 2. LONG-LIVED OBJECTS (Old Gen)
    // ============================================
    
    public static void demonstrateLongLivedObjects() {
        System.out.println("=== 2. Long-Lived Objects (Old Gen) ===\n");
        
        System.out.println("Creating objects that survive...");
        
        // Add to static collection → these objects will survive
        for (int i = 0; i < 100; i++) {
            longLivedObjects.add("Long-lived object " + i);
        }
        
        System.out.println("✓ Created 100 objects stored in static list");
        System.out.println("  Initial Location: Eden Space (Young Gen)");
        System.out.println("  After Minor GCs: Survivor Space (S0/S1)");
        System.out.println("  Eventually: Promoted to Old Generation");
        System.out.println("  Reason: Referenced by static field (never garbage)\n");
    }
    
    // ============================================
    // 3. OBJECT PROMOTION TIMELINE
    // ============================================
    
    public static void demonstrateObjectPromotion() {
        System.out.println("=== 3. Object Promotion Timeline ===\n");
        
        System.out.println("Object Lifecycle:");
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("1. Object created");
        System.out.println("   └─→ Allocated in Eden Space");
        System.out.println();
        System.out.println("2. Eden fills up → Minor GC #1");
        System.out.println("   └─→ If alive: Moved to Survivor S0 (age = 1)");
        System.out.println("   └─→ If dead: Collected and removed");
        System.out.println();
        System.out.println("3. Eden fills again → Minor GC #2");
        System.out.println("   └─→ If alive: Moved to Survivor S1 (age = 2)");
        System.out.println();
        System.out.println("4. ... continues ping-ponging between S0 ↔ S1");
        System.out.println("   └─→ Age increments with each Minor GC");
        System.out.println();
        System.out.println("5. Age reaches threshold (typically 15)");
        System.out.println("   └─→ PROMOTED to Old Generation (Tenured)");
        System.out.println();
        System.out.println("6. Lives in Old Generation");
        System.out.println("   └─→ Only collected during Major GC");
        System.out.println("─────────────────────────────────────────────────\n");
    }
    
    // ============================================
    // 4. VISUAL REPRESENTATION
    // ============================================
    
    public static void visualizeGenerations() {
        System.out.println("=== 4. Memory Layout Visualization ===\n");
        
        System.out.println("Heap Memory Structure:");
        System.out.println();
        System.out.println("┌────────────────────────────────────────────────────┐");
        System.out.println("│  YOUNG GENERATION (~1/3 of heap)                  │");
        System.out.println("│  ┌──────────────┐  ┌──────┐  ┌──────┐            │");
        System.out.println("│  │ Eden (80%)   │  │ S0   │  │ S1   │            │");
        System.out.println("│  │              │  │(10%) │  │(10%) │            │");
        System.out.println("│  │ [NEW OBJECTS]│  │[Age1]│  │[Age2]│            │");
        System.out.println("│  │ Born here    │  │      │  │      │            │");
        System.out.println("│  └──────────────┘  └──────┘  └──────┘            │");
        System.out.println("│                                                   │");
        System.out.println("│  Frequent Minor GC ⚡ (10-50ms)                   │");
        System.out.println("└────────────────────────────────────────────────────┘");
        System.out.println("                      ↓");
        System.out.println("              Objects that survive");
        System.out.println("              multiple Minor GCs");
        System.out.println("              (age ≥ 15)");
        System.out.println("                      ↓");
        System.out.println("┌────────────────────────────────────────────────────┐");
        System.out.println("│  OLD GENERATION / TENURED (~2/3 of heap)          │");
        System.out.println("│                                                   │");
        System.out.println("│  [LONG-LIVED OBJECTS]                             │");
        System.out.println("│  • Promoted from Young Gen                        │");
        System.out.println("│  • Long-term survivors                            │");
        System.out.println("│                                                   │");
        System.out.println("│  Infrequent Major GC 🐌 (100ms-seconds)          │");
        System.out.println("└────────────────────────────────────────────────────┘");
        System.out.println();
    }
    
    // ============================================
    // 5. GC BEHAVIOR COMPARISON
    // ============================================
    
    public static void showGCBehavior() {
        System.out.println("=== 5. GC Behavior Comparison ===\n");
        
        System.out.println("┌─────────────────┬──────────────────┬──────────────────┐");
        System.out.println("│ Aspect          │ Young Gen        │ Old Gen          │");
        System.out.println("├─────────────────┼──────────────────┼──────────────────┤");
        System.out.println("│ Objects         │ New (age 0-15)   │ Old (age 15+)    │");
        System.out.println("│ Size            │ ~1/3 heap        │ ~2/3 heap        │");
        System.out.println("│ GC Type         │ Minor GC         │ Major/Full GC    │");
        System.out.println("│ GC Frequency    │ Very frequent    │ Infrequent       │");
        System.out.println("│ GC Duration     │ 10-50 ms         │ 100ms-seconds    │");
        System.out.println("│ Death Rate      │ 90-98%           │ Low              │");
        System.out.println("│ Algorithm       │ Copy/Scavenge    │ Mark-Sweep       │");
        System.out.println("└─────────────────┴──────────────────┴──────────────────┘");
        System.out.println();
        
        System.out.println("Real-world example:");
        System.out.println();
        System.out.println("Web Server handling 1000 requests/second:");
        System.out.println();
        System.out.println("Young Generation:");
        System.out.println("  • Request objects created (1000/sec)");
        System.out.println("  • Response objects created (1000/sec)");
        System.out.println("  • Parser objects, buffers, etc.");
        System.out.println("  → 99% die immediately after request handled");
        System.out.println("  → Minor GC every few seconds (fast!)");
        System.out.println();
        System.out.println("Old Generation:");
        System.out.println("  • Database connection pool");
        System.out.println("  • Session cache");
        System.out.println("  • Configuration objects");
        System.out.println("  → Stay alive forever");
        System.out.println("  → Major GC every few hours (rare!)");
        System.out.println();
    }
    
    // ============================================
    // BONUS: Simulate Memory Behavior
    // ============================================
    
    public static void simulateMemoryBehavior() {
        System.out.println("=== Simulating Memory Behavior ===\n");
        
        List<Object> survivors = new ArrayList<>();
        
        System.out.println("Creating 10,000 objects...");
        for (int i = 0; i < 10000; i++) {
            // Most objects die immediately (Young Gen)
            String temp = "Temporary " + i;
            Object obj = new Object();
            
            // Only 2% survive (promoted to Old Gen eventually)
            if (i % 50 == 0) {
                survivors.add(temp);
            }
        }
        
        System.out.println("✓ Created 10,000 objects");
        System.out.println("  98% died in Young Gen (Minor GC)");
        System.out.println("  2% survived and stored (" + survivors.size() + " objects)");
        System.out.println("  Survivors will eventually be in Old Gen\n");
        
        // Suggest GC to demonstrate (not guaranteed to run)
        System.out.println("Suggesting garbage collection...");
        System.gc();
        System.out.println("✓ GC suggested (Minor GC cleans Young Gen)");
        System.out.println("  Dead objects from Young Gen collected");
        System.out.println("  Survivors remain and age incremented\n");
    }
    
    // ============================================
    // EXAMPLE: Real Application Pattern
    // ============================================
    
    static class RealWorldExample {
        // These go to Old Gen (long-lived)
        private static Map<String, User> userCache = new HashMap<>();
        private static List<Connection> connectionPool = new ArrayList<>();
        
        public static void handleRequest(String userId, String data) {
            // These go to Young Gen (short-lived)
            String processedData = processData(data);
            RequestContext context = new RequestContext();
            Response response = buildResponse(processedData);
            
            // These objects die when method returns
            // Never promoted to Old Gen!
        }
        
        private static String processData(String data) {
            // More temporary objects in Young Gen
            StringBuilder sb = new StringBuilder();
            String[] parts = data.split(",");
            for (String part : parts) {
                sb.append(part.trim()).append(" ");
            }
            return sb.toString();
        }
        
        private static Response buildResponse(String data) {
            return new Response(data);
        }
        
        static class User { }
        static class Connection { }
        static class RequestContext { }
        static class Response { 
            Response(String data) { }
        }
    }
}

