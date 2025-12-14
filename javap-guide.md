# javap - Java Class File Disassembler

## What is javap?

`javap` is a command-line tool that **disassembles** Java `.class` files and shows you:
- Class structure
- Methods and their signatures
- Fields
- Constructors
- Bytecode instructions (with `-c` flag)
- Private members (with `-p` flag)

It's like "reverse engineering" a compiled Java class to see what's inside!

---

## Basic Usage

```bash
# Compile first
javac MyClass.java

# Then disassemble
javap MyClass
```

---

## Common javap Flags

### 1. **Basic Info** (no flags)
```bash
javap JavapDemo
```

**Output:**
```
Compiled from "JavapDemo.java"
public class JavapDemo {
  public java.lang.String name;
  public JavapDemo(java.lang.String);
  public void increment();
  public static void staticMethod();
  public int add(int, int);
  public <T> T genericMethod(T);
  public static void main(java.lang.String[]);
}
```
**Shows:** Only public/protected members

---

### 2. **Show Private Members** (`-p` or `-private`)
```bash
javap -p JavapDemo
```

**Output:**
```
Compiled from "JavapDemo.java"
public class JavapDemo {
  private static final java.lang.String CONSTANT;
  private int count;
  public java.lang.String name;
  public JavapDemo(java.lang.String);
  public void increment();
  private java.lang.String getMessage();
  public static void staticMethod();
  public int add(int, int);
  public <T> T genericMethod(T);
  public static void main(java.lang.String[]);
}
```
**Shows:** ALL members including private

---

### 3. **Show Bytecode** (`-c`)
```bash
javap -c JavapDemo
```

**Output:**
```
Compiled from "JavapDemo.java"
public class JavapDemo {
  public JavapDemo(java.lang.String);
    Code:
       0: aload_0
       1: invokespecial #1    // Method java/lang/Object."<init>":()V
       4: aload_0
       5: aload_1
       6: putfield      #7    // Field name:Ljava/lang/String;
       9: aload_0
      10: iconst_0
      11: putfield      #13   // Field count:I
      14: return

  public void increment();
    Code:
       0: aload_0
       1: dup
       2: getfield      #13   // Field count:I
       5: iconst_1
       6: iadd
       7: putfield      #13   // Field count:I
      10: return
  ...
}
```
**Shows:** Bytecode instructions (JVM assembly)

---

### 4. **Show Verbose Info** (`-v` or `-verbose`)
```bash
javap -v JavapDemo
```

**Output:**
```
Classfile /path/to/JavapDemo.class
  Last modified Dec 14, 2025; size 1234 bytes
  SHA-256 checksum abc123...
  Compiled from "JavapDemo.java"
public class JavapDemo
  minor version: 0
  major version: 61
  flags: (0x0021) ACC_PUBLIC, ACC_SUPER
  this_class: #7                          // JavapDemo
  super_class: #2                         // java/lang/Object
  interfaces: 0, fields: 3, methods: 7, attributes: 1
Constant pool:
   #1 = Methodref          #2.#3          // java/lang/Object."<init>":()V
   #2 = Class              #4             // java/lang/Object
   #3 = NameAndType        #5:#6          // "<init>":()V
   ...
{
  private static final java.lang.String CONSTANT;
    descriptor: Ljava/lang/String;
    flags: (0x001a) ACC_PRIVATE, ACC_STATIC, ACC_FINAL

  public JavapDemo(java.lang.String);
    descriptor: (Ljava/lang/String;)V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=2, locals=2, args_size=2
         0: aload_0
         1: invokespecial #1    // Method java/lang/Object."<init>":()V
         4: aload_0
         5: aload_1
         6: putfield      #7    // Field name:Ljava/lang/String;
         9: aload_0
        10: iconst_0
        11: putfield      #13   // Field count:I
        14: return
      LineNumberTable:
        line 10: 0
        line 11: 4
        line 12: 9
        line 13: 14
  ...
}
```
**Shows:** EVERYTHING - constant pool, bytecode, line numbers, stack info

---

### 5. **Show Method Signatures** (`-s`)
```bash
javap -s JavapDemo
```

**Output:**
```
public class JavapDemo {
  public java.lang.String name;
    descriptor: Ljava/lang/String;
  
  public JavapDemo(java.lang.String);
    descriptor: (Ljava/lang/String;)V
  
  public int add(int, int);
    descriptor: (II)I
  
  public <T> T genericMethod(T);
    descriptor: (Ljava/lang/Object;)Ljava/lang/Object;
  
  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
}
```
**Shows:** Internal JVM type signatures

---

### 6. **Show System Info** (`-sysinfo`)
```bash
javap -sysinfo JavapDemo
```

**Output:**
```
Classfile /path/to/JavapDemo.class
  Last modified Dec 14, 2025; size 1234 bytes
  SHA-256 checksum abc123def456...
```

---

### 7. **Show Constants** (`-constants`)
```bash
javap -constants JavapDemo
```

**Output:**
```
public class JavapDemo {
  private static final java.lang.String CONSTANT = "Hello";
  ...
}
```
**Shows:** Constant values

---

## Common Flag Combinations

### Most Useful: Private + Bytecode
```bash
javap -p -c JavapDemo
```
Shows all members with bytecode

### Everything: Verbose + Private
```bash
javap -v -p JavapDemo
```
Shows absolutely everything

### Quick Overview: Public + Signatures
```bash
javap -s JavapDemo
```
Shows public API with signatures

---

## Practical Use Cases

### 1. **See What Methods a Class Has**
```bash
javap java.lang.String
```
See all public methods in String class

### 2. **Check Private Methods** (debugging)
```bash
javap -p MyClass
```
See if private method exists

### 3. **Understand How Java Compiles Code**
```bash
javap -c MyClass
```
See bytecode to understand compilation

### 4. **Check Generics (Type Erasure)**
```bash
javap -s MyClass
```
See that `List<String>` becomes `List`

### 5. **Verify Java Version**
```bash
javap -v MyClass | grep "major version"
```
Check which Java version compiled the class

### 6. **Examine Third-Party Libraries**
```bash
javap -cp gson.jar com.google.gson.Gson
```
See methods in libraries without source code

---

## Understanding Bytecode Instructions

When you use `javap -c`, you see bytecode. Here are common instructions:

| Instruction | Meaning |
|-------------|---------|
| `aload_0` | Load reference from local variable 0 (usually `this`) |
| `aload_1` | Load reference from local variable 1 (first parameter) |
| `iload_1` | Load int from local variable 1 |
| `iconst_0` | Push constant 0 onto stack |
| `iadd` | Add two integers |
| `putfield` | Set instance field |
| `getfield` | Get instance field |
| `invokevirtual` | Invoke instance method |
| `invokestatic` | Invoke static method |
| `invokespecial` | Invoke constructor or private method |
| `return` | Return void |
| `ireturn` | Return int |
| `areturn` | Return reference |

---

## Understanding Type Signatures

| Signature | Type |
|-----------|------|
| `V` | void |
| `I` | int |
| `J` | long |
| `F` | float |
| `D` | double |
| `Z` | boolean |
| `C` | char |
| `B` | byte |
| `S` | short |
| `Ljava/lang/String;` | String |
| `[I` | int[] |
| `[[I` | int[][] |
| `(II)I` | Method taking 2 ints, returning int |
| `([Ljava/lang/String;)V` | Method taking String[], returning void |

---

## Examples

### Example 1: Check String Methods
```bash
javap java.lang.String | grep length
```
**Output:**
```
public int length();
```

### Example 2: See How String Concatenation Works
```bash
# Create test file
echo 'public class Test { 
    public static void main(String[] args) { 
        String s = "Hello" + " " + "World"; 
    } 
}' > Test.java

javac Test.java
javap -c Test
```
You'll see it uses StringBuilder internally!

### Example 3: Check Lambda Implementation
```bash
# Create lambda example
echo 'import java.util.function.Function;
public class Lambda {
    public static void main(String[] args) {
        Function<String, Integer> f = s -> s.length();
    }
}' > Lambda.java

javac Lambda.java
javap -p Lambda
```
You'll see a synthetic method `lambda$main$0`!

---

## Quick Reference

| Command | What it Shows |
|---------|---------------|
| `javap MyClass` | Public members |
| `javap -p MyClass` | All members (including private) |
| `javap -c MyClass` | Bytecode |
| `javap -v MyClass` | Everything (verbose) |
| `javap -s MyClass` | Method signatures |
| `javap -constants MyClass` | Constant values |
| `javap -l MyClass` | Line numbers |
| `javap -cp path MyClass` | Use custom classpath |

---

## Tips

1. **Always compile first**: `javap` works on `.class` files, not `.java` files
2. **Use full package names**: `javap com.example.MyClass`
3. **Set classpath**: `javap -cp mylib.jar com.example.MyClass`
4. **Pipe to less**: `javap -v MyClass | less` (for long output)
5. **Grep for specific methods**: `javap MyClass | grep myMethod`

---

## Common Errors

### Error: "class not found"
```bash
javap MyClass
# Error: class not found
```
**Solution:** Make sure `.class` file exists and you're in the right directory

### Error: "NoClassDefFoundError"
```bash
javap com.example.MyClass
# Error: NoClassDefFoundError
```
**Solution:** Use `-cp` to specify classpath

---

## Why Use javap?

✅ **Debugging**: See what's actually compiled
✅ **Learning**: Understand how Java works internally
✅ **Reverse engineering**: Examine libraries without source
✅ **Verification**: Check if methods/fields exist
✅ **Performance analysis**: See bytecode optimizations
✅ **Compatibility**: Check Java version of class files
✅ **Generic type erasure**: See what happens to generics

---

## Summary

`javap` is a powerful tool for looking inside compiled Java classes.

**Most common usage:**
```bash
javac MyClass.java          # Compile
javap MyClass               # See public members
javap -p MyClass            # See all members
javap -c MyClass            # See bytecode
javap -v MyClass            # See everything
```

It's like X-ray vision for Java classes! 👓

