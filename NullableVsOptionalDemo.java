import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @Nullable vs Optional - Key Difference: Compile-Time Safety
 * - @Nullable: Documentation only - compiler doesn't enforce null checks
 * - Optional: Wrapper type - compiler forces you to handle absence
 */
public class NullableVsOptionalDemo {

    public static void main(String[] args) {
        System.out.println("=== @Nullable vs Optional ===\n");
        demonstrateNullableVsOptional();
        demonstrateOptionalAPI();
        demonstrateRealWorldExample();
    }

    // @Nullable: Compiles but crashes at runtime if you forget null check
    @Nullable
    private static String cache;
    
    @Nullable
    public static String getNullable() {
        return cache;
    }
    
    // Optional: Forces handling at compile time
    public static Optional<String> getOptional() {
        return Optional.ofNullable(cache);
    }
    
    static class User {
        private final String name;
        User(String name) { this.name = name; }
        public String getName() { return name; }
    }
    
    public static Optional<User> findUser(String id) {
        return "123".equals(id) ? Optional.of(new User("John")) : Optional.empty();
    }
    
    public static void demonstrateNullableVsOptional() {
        System.out.println("1. Comparison:");
        
        // @Nullable - can forget to check
        String nullable = getNullable();
        if (nullable != null) {  // Easy to forget this check!
            System.out.println("   @Nullable: " + nullable.toUpperCase());
        } else {
            System.out.println("   @Nullable: null (must remember to check)");
        }
        
        // Optional - must handle
        String optional = getOptional().orElse("DEFAULT");
        System.out.println("   Optional: " + optional + " (forced to handle)\n");
    }
    
    public static void demonstrateOptionalAPI() {
        System.out.println("2. Optional API:");
        
        Optional<String> present = Optional.of("Hello");
        Optional<String> absent = Optional.empty();
        
        // Common operations
        System.out.println("   isPresent: " + present.isPresent());
        System.out.println("   orElse: " + absent.orElse("default"));
        System.out.println("   orElseGet: " + absent.orElseGet(() -> "lazy-default"));
        System.out.println("   map: " + present.map(String::length).orElse(0));
        
        // Method chaining
        String result = findUser("123")
            .map(User::getName)
            .map(String::toUpperCase)
            .orElse("UNKNOWN");
        System.out.println("   Chaining: " + result);
        
        // Anti-pattern: Never call .get() without checking
        try {
            absent.get();  // ❌ Throws NoSuchElementException
        } catch (Exception e) {
            System.out.println("   ❌ Don't use .get() without check: " + e.getClass().getSimpleName());
        }
        System.out.println();
    }
    
    enum CloudType {
        AWS("aws"), AZURE("azure"), NATIVE_AWS("native-aws");
        
        private final String identifier;
        CloudType(String identifier) { this.identifier = identifier; }
        public String getIdentifier() { return identifier; }
        
        public static Optional<CloudType> valueOfIdentifier(String value) {
            for (CloudType type : values()) {
                if (type.getIdentifier().equals(value)) {
                    return Optional.of(type);
                }
            }
            return Optional.empty();
        }
    }
    
    public static void demonstrateRealWorldExample() {
        System.out.println("3. Real-World Pattern:");
        
        CloudType type = CloudType.valueOfIdentifier("aws").orElse(CloudType.NATIVE_AWS);
        System.out.println("   Found 'aws': " + type);
        
        CloudType defaultType = CloudType.valueOfIdentifier("unknown").orElse(CloudType.NATIVE_AWS);
        System.out.println("   Unknown defaults to: " + defaultType);
    }
    
    /*
     * WHEN TO USE:
     * 
     * @Nullable:
     * - Method parameters: void setName(@Nullable String name)
     * - Fields: @Nullable private String cache
     * - Internal/private methods
     * - Performance-critical code (no object allocation)
     * 
     * Optional:
     * - Public API return types (forces caller to handle absence)
     * - Method chaining: findUser().map().orElse()
     * 
     * ANTI-PATTERNS:
     * - ❌ Optional as field: private Optional<String> name
     * - ❌ Optional as parameter: void set(Optional<String> name)
     * - ❌ Calling .get() without isPresent() check
     * 
     * SUMMARY:
     * Optional provides compile-time safety but costs an object allocation.
     * @Nullable is documentation only but has zero overhead.
     * Use Optional for public APIs, @Nullable for internal code.
     */
}