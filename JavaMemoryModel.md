# Java Memory Organization Model

## Overview: JVM Runtime Memory Areas

```
┌─────────────────────────────────────────────────────────────────┐
│                    JVM Memory Structure                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │              Heap (Shared across all threads)             │  │
│  │  ┌──────────────────────────────────────────────────┐    │  │
│  │  │  Young Generation                                │    │  │
│  │  │  ┌────────┐  ┌────────┐  ┌──────────────────┐  │    │  │
│  │  │  │ Eden   │  │Survivor│  │   Survivor       │  │    │  │
│  │  │  │ Space  │  │ S0     │  │   S1             │  │    │  │
│  │  │  └────────┘  └────────┘  └──────────────────┘  │    │  │
│  │  └──────────────────────────────────────────────────┘    │  │
│  │  ┌──────────────────────────────────────────────────┐    │  │
│  │  │  Old Generation (Tenured)                        │    │  │
│  │  │  Long-lived objects                              │    │  │
│  │  └──────────────────────────────────────────────────┘    │  │
│  └───────────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │       Method Area / Metaspace (Shared)                    │  │
│  │  - Class metadata                                         │  │
│  │  - Static variables                                       │  │
│  │  - Constant pool                                          │  │
│  │  - Method bytecode                                        │  │
│  └───────────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐      │
│  │ Thread Stack  │  │ Thread Stack  │  │ Thread Stack  │      │
│  │   (Thread 1)  │  │   (Thread 2)  │  │   (Thread 3)  │      │
│  │               │  │               │  │               │      │
│  │ ┌───────────┐ │  │ ┌───────────┐ │  │ ┌───────────┐ │      │
│  │ │Local Vars │ │  │ │Local Vars │ │  │ │Local Vars │ │      │
│  │ ├───────────┤ │  │ ├───────────┤ │  │ ├───────────┤ │      │
│  │ │Method Call│ │  │ │Method Call│ │  │ │Method Call│ │      │
│  │ ├───────────┤ │  │ ├───────────┤ │  │ ├───────────┤ │      │
│  │ │Frame Info │ │  │ │Frame Info │ │  │ │Frame Info │ │      │
│  │ └───────────┘ │  │ └───────────┘ │  │ └───────────┘ │      │
│  └───────────────┘  └───────────────┘  └───────────────┘      │
│                                                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │
│  │PC Register  │  │PC Register  │  │PC Register  │            │
│  │ (Thread 1)  │  │ (Thread 2)  │  │ (Thread 3)  │            │
│  └─────────────┘  └─────────────┘  └─────────────┘            │
│                                                                 │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │       Native Method Stacks                                │  │
│  │  (For JNI - Java Native Interface calls)                  │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 1. Heap (Shared Memory)

### Purpose
Stores **all objects** and **instance variables** (class members).

### Characteristics
- ✅ **Shared** across all threads
- ✅ **Garbage collected**
- ✅ **Largest** memory area
- ✅ Created at JVM startup
- ⚠️ **OutOfMemoryError** if full

### What's stored here?
- All objects created with `new`
- Instance variables (non-static fields)
- Arrays

### Heap Structure (Generational GC)

```
Heap Memory:
┌──────────────────────────────────────────────────┐
│ Young Generation (~1/3 of heap)                  │
│ ┌──────────────┐ ┌─────┐ ┌─────┐                │
│ │ Eden Space   │ │ S0  │ │ S1  │                │
│ │ (New objects)│ │     │ │     │                │
│ └──────────────┘ └─────┘ └─────┘                │
├──────────────────────────────────────────────────┤
│ Old Generation / Tenured (~2/3 of heap)          │
│ (Long-lived objects promoted from Young Gen)     │
└──────────────────────────────────────────────────┘
```

### Example:
```java
public class HeapExample {
    int instanceVar;  // Stored in Heap
    
    public static void main(String[] args) {
        HeapExample obj1 = new HeapExample();  // obj1 object → Heap
        HeapExample obj2 = new HeapExample();  // obj2 object → Heap
        
        int[] array = new int[100];  // Array object → Heap
        String str = new String("Hello");  // String object → Heap
    }
}
```

**Memory layout:**
```
Heap:
┌─────────────────────────┐
│ HeapExample@1 object    │ ← obj1
│   instanceVar: 0        │
├─────────────────────────┤
│ HeapExample@2 object    │ ← obj2
│   instanceVar: 0        │
├─────────────────────────┤
│ int[100] array          │
│   [0,0,0...0]           │
├─────────────────────────┤
│ String object           │
│   value: "Hello"        │
└─────────────────────────┘
```

### Configure Heap Size:
```bash
# Set initial heap size
java -Xms512m MyApp

# Set maximum heap size
java -Xmx2g MyApp

# Set both
java -Xms512m -Xmx2g MyApp
```

---

## 2. Stack (Thread-Local Memory)

### Purpose
Stores **method calls**, **local variables**, and **partial results**.

### Characteristics
- ✅ **One stack per thread**
- ✅ **LIFO** (Last In First Out)
- ✅ **Fast** access (push/pop)
- ✅ **Automatically managed** (no GC needed)
- ✅ **Fixed size** per thread
- ⚠️ **StackOverflowError** if too deep

### What's stored here?
- Primitive local variables (int, float, boolean, etc.)
- Object references (not the objects themselves!)
- Method call frames
- Partial computation results

### Stack Frame Structure:
```
Stack (per thread):
┌─────────────────────────────┐
│  Frame for method3()        │ ← Top (current method)
│  - Local variables          │
│  - Operand stack            │
│  - Return address           │
├─────────────────────────────┤
│  Frame for method2()        │
│  - Local variables          │
│  - Operand stack            │
│  - Return address           │
├─────────────────────────────┤
│  Frame for method1()        │
│  - Local variables          │
│  - Operand stack            │
│  - Return address           │
├─────────────────────────────┤
│  Frame for main()           │ ← Bottom (first method)
│  - args                     │
│  - Local variables          │
└─────────────────────────────┘
```

### Example:
```java
public class StackExample {
    public static void main(String[] args) {
        int x = 10;              // x → Stack
        String str = "Hello";    // str reference → Stack
                                 // "Hello" object → Heap (String pool)
        
        method1(x);
    }
    
    static void method1(int a) {  // a → Stack
        int b = 20;               // b → Stack
        method2(b);
    }
    
    static void method2(int c) {  // c → Stack
        int d = 30;               // d → Stack
    }
}
```

**Memory layout when method2() is executing:**
```
Stack:                          Heap:
┌──────────────────────────┐   ┌──────────────────┐
│ Frame: method2()         │   │ "Hello" object   │
│   c = 20                 │   │ (String pool)    │
│   d = 30                 │   └──────────────────┘
├──────────────────────────┤            ↑
│ Frame: method1()         │            │
│   a = 10                 │            │
│   b = 20                 │            │
├──────────────────────────┤            │
│ Frame: main()            │            │
│   args = [...]           │            │
│   x = 10                 │            │
│   str = ───────────────────────────────┘
└──────────────────────────┘   (reference)
```

### Configure Stack Size:
```bash
# Set stack size per thread
java -Xss1m MyApp    # 1 MB stack per thread
java -Xss512k MyApp  # 512 KB stack per thread
```

---

## 3. Method Area / Metaspace (Shared Memory)

### Purpose
Stores **class-level data** and **static variables**.

### Characteristics
- ✅ **Shared** across all threads
- ✅ Created at JVM startup
- ✅ **Logically part of Heap** (but often separate)
- ✅ **Garbage collected** (can unload classes)
- ⚠️ **OutOfMemoryError: Metaspace** if full

### What's stored here?
- Class metadata (class structure)
- Method bytecode
- Static variables
- Constant pool (literals, symbolic references)
- Field and method data

### Before Java 8: PermGen
```
Java 7 and earlier:
Method Area = PermGen (Permanent Generation)
- Fixed size
- Part of Heap
- Limited by -XX:MaxPermSize
```

### Java 8+: Metaspace
```
Java 8 and later:
Method Area = Metaspace
- Dynamic size
- Native memory (not in Heap)
- Limited by available system memory
- Controlled by -XX:MaxMetaspaceSize
```

### Example:
```java
public class MethodAreaExample {
    static int staticVar = 100;        // → Method Area
    static final String CONSTANT = "Hi"; // → Method Area (constant pool)
    
    int instanceVar;  // Metadata about this field → Method Area
                      // Actual value for each object → Heap
    
    public void method() {  // Method bytecode → Method Area
        // implementation
    }
}
```

**Memory layout:**
```
Method Area / Metaspace:
┌────────────────────────────────────┐
│ Class: MethodAreaExample           │
│   - Class metadata                 │
│   - Field descriptors              │
│   - Method bytecode                │
│   - Static variables:              │
│     staticVar = 100                │
│   - Constant pool:                 │
│     CONSTANT = "Hi"                │
└────────────────────────────────────┘

Heap:
┌────────────────────────────────────┐
│ MethodAreaExample object           │
│   instanceVar = (some value)       │
└────────────────────────────────────┘
```

### Configure Metaspace:
```bash
# Set initial metaspace size
java -XX:MetaspaceSize=128m MyApp

# Set maximum metaspace size
java -XX:MaxMetaspaceSize=256m MyApp
```

---

## 4. PC Register (Program Counter) - Thread-Local

### Purpose
Stores the **address of the current instruction** being executed.

### Characteristics
- ✅ **One per thread**
- ✅ **Very small** (just an address)
- ✅ Points to current bytecode instruction

### What it does:
```
PC Register = Current instruction address

Bytecode:
0: iconst_1      ← PC = 0 (executing this)
1: istore_1
2: iconst_2
3: istore_2
4: iload_1       ← PC = 4 (after a few steps)
5: iload_2
6: iadd
7: ireturn
```

---

## 5. Native Method Stack - Thread-Local

### Purpose
Supports **native methods** (written in C/C++ via JNI).

### Characteristics
- ✅ **One per thread**
- ✅ Used for native method calls
- ✅ Similar to Java Stack

### Example:
```java
// Java method → uses Java Stack
public void javaMethod() {
    System.out.println("Java");
}

// Native method → uses Native Method Stack
public native void nativeMethod();
```

---

## Complete Example: Where Everything Goes

```java
public class MemoryDemo {
    // Static variable → Method Area
    static int staticCounter = 0;
    
    // Instance variable → Heap (in each object)
    int instanceCounter = 0;
    
    // Constant → Method Area (constant pool)
    static final String CONSTANT = "Hello";
    
    public static void main(String[] args) {
        // Local variable (reference) → Stack
        // Object itself → Heap
        MemoryDemo obj1 = new MemoryDemo();
        
        // Primitive local variable → Stack
        int localVar = 10;
        
        // Call method
        obj1.increment(5);
    }
    
    public void increment(int value) {
        // Parameter 'value' → Stack
        // 'this' reference → Stack
        // Actual object → Heap
        
        this.instanceCounter += value;
        staticCounter += value;
        
        // New object → Heap
        String str = new String("World");
    }
}
```

**Complete Memory Layout:**
```
┌─────────────────────────────────────────────────────────────┐
│ Method Area / Metaspace (Shared)                            │
├─────────────────────────────────────────────────────────────┤
│ Class: MemoryDemo                                           │
│   - Class metadata                                          │
│   - Method bytecode (main, increment)                       │
│   - staticCounter = 5                                       │
│   - CONSTANT = "Hello"                                      │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ Heap (Shared)                                               │
├─────────────────────────────────────────────────────────────┤
│ MemoryDemo object #1                                        │
│   instanceCounter = 5                                       │
├─────────────────────────────────────────────────────────────┤
│ String object "World"                                       │
│   value: ['W','o','r','l','d']                              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ Stack (Thread: main)                                        │
├─────────────────────────────────────────────────────────────┤
│ Frame: increment()                                          │
│   this = reference to MemoryDemo object #1 → Heap           │
│   value = 5                                                 │
│   str = reference to String "World" → Heap                  │
├─────────────────────────────────────────────────────────────┤
│ Frame: main()                                               │
│   args = reference to String[] → Heap                       │
│   obj1 = reference to MemoryDemo object #1 → Heap           │
│   localVar = 10                                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ PC Register (Thread: main)                                  │
├─────────────────────────────────────────────────────────────┤
│ Current instruction address: 23                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Stack vs Heap: Quick Comparison

| Feature | Stack | Heap |
|---------|-------|------|
| **Stores** | Local variables, method calls | Objects, arrays |
| **Scope** | Thread-local | Shared |
| **Size** | Small (~1 MB default) | Large (~GB) |
| **Access** | Fast (LIFO) | Slower |
| **Management** | Automatic (scope-based) | Garbage Collector |
| **Lifetime** | Method execution | Until GC collects |
| **Error** | StackOverflowError | OutOfMemoryError |
| **Thread-safe** | Yes (per thread) | No (needs sync) |

---

## Memory Errors

### 1. StackOverflowError
```java
public void recursion() {
    recursion();  // Infinite recursion
}
// → StackOverflowError (stack full)
```

### 2. OutOfMemoryError: Java heap space
```java
List<byte[]> list = new ArrayList<>();
while (true) {
    list.add(new byte[1024 * 1024]);  // Add 1 MB
}
// → OutOfMemoryError: Java heap space
```

### 3. OutOfMemoryError: Metaspace
```java
// Loading too many classes dynamically
while (true) {
    // Generate and load new classes
}
// → OutOfMemoryError: Metaspace
```

---

## Memory Configuration Flags

```bash
# Heap
-Xms<size>          # Initial heap size
-Xmx<size>          # Maximum heap size
-XX:NewRatio=<n>    # Ratio of old/young generation

# Stack
-Xss<size>          # Stack size per thread

# Metaspace (Java 8+)
-XX:MetaspaceSize=<size>     # Initial metaspace
-XX:MaxMetaspaceSize=<size>  # Maximum metaspace

# PermGen (Java 7 and earlier)
-XX:PermSize=<size>          # Initial perm gen
-XX:MaxPermSize=<size>       # Maximum perm gen

# Garbage Collection
-XX:+UseG1GC               # Use G1 Garbage Collector
-XX:+UseConcMarkSweepGC    # Use CMS GC
-XX:+UseParallelGC         # Use Parallel GC
-XX:+PrintGCDetails        # Print GC details

# Example
java -Xms512m -Xmx2g -Xss1m -XX:MaxMetaspaceSize=256m MyApp
```

---

## Visual Summary

```
┌──────────────────────────────────────────────────────────┐
│                    JVM Memory                            │
│                                                          │
│  SHARED (All threads):                                   │
│  ┌────────────────────────────────────────────────────┐  │
│  │ Heap: Objects, arrays, instance variables          │  │
│  │       (Garbage collected)                          │  │
│  └────────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────────┐  │
│  │ Method Area/Metaspace: Classes, static vars,      │  │
│  │                        constants, bytecode         │  │
│  └────────────────────────────────────────────────────┘  │
│                                                          │
│  PER THREAD:                                             │
│  ┌────────────────────────────────────────────────────┐  │
│  │ Stack: Local variables, method calls              │  │
│  │        (No GC, automatic scope-based cleanup)     │  │
│  └────────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────────┐  │
│  │ PC Register: Current instruction address          │  │
│  └────────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────────┐  │
│  │ Native Method Stack: For JNI calls                │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
```

---

## Key Takeaways

1. **Heap**: Objects and instance variables (shared, GC)
2. **Stack**: Local variables and method calls (per thread, automatic)
3. **Method Area**: Class metadata and static variables (shared)
4. **PC Register**: Current instruction (per thread)
5. **Native Method Stack**: For native code (per thread)

**Remember:**
- Objects → Heap
- Primitives/References in methods → Stack
- Static variables → Method Area
- Class definitions → Method Area

