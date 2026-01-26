# My notes

# Java 
Gets compiled, and then turned in byte code files so it can run on any machine without having to recompile. Uses an interpreter like a JVM to run on any machine.

Best of both worlds between the speed of compiling, and the portability of running on any machine.

But now, python also works that way, so it's pretty even now.

## Javadoc
Documentation for the Java class library 

Generated from code and Javadoc comments

[Standard Libraries](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/module-summary.html)

Packages - folders

Class - file

## Primitive Datatypes
byte - integer of size one byte

short - integer of size two bytes

int - integer of size four bytes

long - integer of size eight bytes

float - real number of size four bytes

double - real number of size eight bytes

char - two bytes, unicode encoding, lots of different characters from lots of different alphabets

boolean - True or False



Not objects: no methods, don't need to call new

Wrapper classes - Integer, Double, Boolean, etc

Ex: Integer i = new Integer(34)

Allows for useful methods - parsing strings into integers, int i = Integer.parseInt("34")

## Objects and Classes

Objects live in the heap

The only way to create an object is to call 'new', which returns a reference (pointer) to the object, and that reference is what lives in the runtime stack

So, if you change the object you're pointing to, it updates for all the other pointers

## Strings
Object, lives in the heap, but doesn't need 'new' declaration - String s = "Hello"

Strings are immutable - the object is not allowed to be changed, so either you can't change it, or you have to create a new object

Safe to have it be immutable

Concatenation with '+', and creates new string object
- Super inefficient in general, so if you do a lot of concatenation, use StringBuilder
- StringBuilder builder = new StringBuilder();
- builder.append("First part ");
- builder.append("next part ");
- String str = builder.toString()

Formatting example: String s3 = String.format("%s %s", s1, s2) 
- Calls a static method, which is a variable on the class String, not the object s3

Methods: length, charAt, trim, startsWith

Special Characters:
- \n newline
- \t tab
- \" double quote
- \' single quote
- \\ backslah
- \b backspace
- \uXXXX unicode character constant


## Arrays

Declaration: type[] 

When you create it, you have to declare new type[number]. It will set the values to some default - either 0s, falses, etc, but for any objects, it sets it to null pointers.

Can also initialize as {elements}.

Can create multiple dimension arrays by doing array[][]

## Packages
A folder becomes a 'package'. In a class file, declare the package it belongs to: "package folder_directory". You can import packages using the command "import package", or else you have to type out the full name of the class (ie, java.util.data.Date)

# Creating Classes

## Static
Belongs to the CLASS, not an instance of the class. Accessed by ClassName.thing. Cannot use the 'this' reference in a static method, because there is no object it is being called on. Can only reference static parts of the class, and not any other methods. 

If a static method really needs to call another method, create an instance of the object inside the static method, and then use that to call stuff.

Can use a static attribute to generate unique ids; set up a static counter and increase it every time you give out an id. A class level piece of data that every instance can share, and update to other instances.

## Object Class
Every class actually extends from the Java Object class. This allows every class to havee common features. Examples include:
- toString() defaults to return the memory address
- equals(Object obj) defaults to be reference equality (same as calling ==)
- hashCode() returns an integer for storing the object in a hash
- clone() returns a pointer to a copy
- getClass() returns the class information, use getClass().getName() to return the name of the class

Overwriting methods:
- Have to start with decorator @Override
- toString() may want to have something more informative
- equals() will want to change to compare by value
  -   Any type can be passed into equals(), so you want to start with type checking
  -   Should star wtih (object == null) and (this.getClass() != object.getClass())
  -   Put value comparison after a casting, eg: Person p = (Person)object, and then use p to get values
- hashCode() needs to get overwritten ... for reasons.
  -   Can call Object.hash(pass in attributes)
  -   Should pass in the same attributes as those you use to check equality
  -   Deterministic manner of creating an integer that can act as an index for an array that you put your object into
  -   Should look like a random number generator
  -   Can call the hashCode method on the attributes

REMINDER: Primitives do not have any methods, so can't be called with a .equals() methods, and works just fine wtih ==.

## Inheritance
Code reuse mechanism. Ability to modify a class and add on additional methods or variables. Importing information and then adding something. You can also override methods to make them work the way you want for the child. A class can only inherit from one other class.

Polymorphism - ability to store multiple different object types by using their parent type. Being able to interact with objects, even if you don't know exactly what it is, you know sort of what they all have because of their super methods.

Ability to be generic in parameter type to provide flexibility (ie, ArrayList vs Collection).

Syntax:
```
public class Student extends Person {
  public Student() {
    super(); 
    setYear(YearInSchool.FRESHMAN);
    setGPA(0.0)
  }
  public Student(String name, int age, YearInSchool year, float gpa) {
    super(name, age);
    this.year = year;
    this.gpa = gpa;
  }
  ...
  @Override
  public boolean equals(Object o) {
    boolean b = super.equals(o);
    if (!b) {
      return false;
    } else {
      Student s = (Student) o;
      return (year == s.year && gpa == s.gpa)
    }  
  }

  @Override
  public int hashCode() {
    return (super.hashCode() * (int)gpa) ^ year.hashCode();
  }  
}
```

Using methods in the Parent class
- protected: function keyword, a member only visible to the inheritance tree, something that all descendants can access (also something the package can access in Java)
- Claim a protected method so that the child can override it on purpose (use @Override)
- A child can't access a private variable, so if the student wants to access its age, you have to use the getAge method.
- abstract: function keyword, saying you're declaring a method, but you aren't defining it for the parent
  - protected abstract int agePriority();
- If a class has an abstract method, it become abstract, so you have to label the class as abstract because it isn't fully defined
  - Cannot call new on an abstract class
  - Conveying intent of not wanting a parent object directly
- An abstract class only has purpose in being a super class.
- final: function keyword, saying a child CANNOT override that method
  - A class can be tagged final if you can't inherit from that class


## Interface Inheritance
A class that only has abstract method definitions. A datatype that a class can implement. Polymorphism without inheriting code.

Keyword is "implement" the interface. Promising to have all the interface methods. Can implement as many interfaces as you want.

public class Person extends whatever implements Moveable, Comparable, Runnable {}

Declaration: public interface Name { void go(); } 
- Includes return and paramater types, but no other keywords, no need to call abstract

An interface can extend from another interface - public interface MyInterface extends Moveable

To get rid of any duplication in code, you can introduce an abstract class between the interface and the full classes to implement any shared code.


## Enumeration
A different type of Java file. Example:

public enum YearInSchool {

FRESHMAN,

SOPHOMORE, 

JUNIOR,

SENIOR,

GRADUATE,

}

Treated as just a limited number of instances of this class. Can use == without .equals() without running into any issues.

## Constructors
Tips for default or incomplete construction:

You can call one constructor from another one by using the call "this(input)".

## Records
Sometimes, you have a class that only holds data, but doesn't really implement any meaningful algorithms. Easily written or generated.

To simplify this, you can use a record. 

Example: public record Pet(int id, String name, String type) {}

... and that's all you need. Java will fill in the dots for getter and setter methods, hash codes, equals, etc.

Record objects are immutable. Getter methods don't have 'get' in it - instead of .getName(), it's just .name(). You can add in additional methods to the record, but they can't change any of the attribute values.

## Copying Objects

Shallow copy - creates a new object of the same class, but doesn't copy the variable values, it just links them. If you modify one of the variables in the previous object, then it will modify the copy as well.

Deep copy - creates a copy of the object, and creates a copy of each nested variable.

You don't need to copy any immutable objects, becuase you don't have to worry about someone messing up the original value.

Writing classes to support copying: 
- Method 1: call new and pass in the original object to the constructor, constructor example is "public Course(Course other) {setName(other.name)}
- Method 2: call object.clone() - official method in java, clone() method belongs to Object class, clone() calls copy constructor. You don't need to remember which object type it is to copy it.
- You need to copy each variable as well if they aren't immutable. If the variables are immutable, you can just set the value.
- When creating a deep copy of a list, for example, you need to copy and add in each element from the original list.
- Default Java clone only makes a shallow copy

# Error Handling

Causes of Errors: 
- Bugs in a code - null pointers, etc, just fix it
- Bad input given
- Out of memory
- Runtime stack overflow
- Bad internet connection

Java forces you to deal with erorrs - you either have to handle it, or advertise the fact that you didn't. If you don't deal with an error, it will kill your code.
These rules do not apply to Errors or RuntimeExceptions.

Handle: 

An exception will be detecting way down low in the operating system. That system will gather information about it, and 'throws' the exception upward until a program says "I know what to do about it". Adding in handling lets your program deal with it before the terminal yells at you, and being able to gracefully exit your code.

```
try {regular code}
catch (Exception ex) {error handling code}
catch (otherExceptionType ex) {handling another error}
continuation code
```

Declare:

Advertise the fact that you didn't handle any exceptions. Lets the programmer know what exceptions may be thrown from a certain function or subfunction. 
```
public void method()
    throws Exception {}
```

Details:

You can only throw an object. Throwable --> Exception and Error
- Error is catastrophic, something you're program won't recover from, so you might as well ignore it.
- Exceptions --> IOException, InvalidURL
  - These are the ones that Java will check, so you need to have exceptions for these
- Exceptions --> RuntimeException --> NullPointer, IndexOutOfBounds
  - RuntimeExceptions are not checked by Java - things that are your fault, have to do with bugs, things you should just fix
  - Too common to bother enforcing, because really anything could call it

Common Errors and General advice:
- An object constructor or method can throw an error
- Main methods probably shouldn't throw an error, or else it's a bad program
- Can use ex.printStackTrace()
- Multiple catch blocks for different errors
- Even if you don't do anything useful, and you just end the program, you should exit gracefully and provide useful information to the user
- Use a finally clause (which happens after both try and catch) to do anything that needs to happen every time, something you don't want to accidentally skip, like closing a file, or deallocating other resources
- Java added try with resource: try (open scanner) {} which lets Java write the finally block automatically

Creating custom exception types: public class CustomName extends Exception {imitate normal exception methods}

# Collections

When regular arrays do not suffice; Collections can only store object references; implements many useful associated algorithms.

Types:
- Collection --> List, Set (--> SortedSet --> NavigableSet), Queue (--> Deque)
  - Add, remove, length, iterator methods
- Map --> SortedMap --> NavigableMap
  - Key-Value pairs
- Iterator --> ListIterator
  - Iterates through values in a Collection
 
Lists
- Ordered, accessed by index
- ArrayList, LinkedList
- Uses a more powerful iterator called a ListIterator

Set 
- Unordered, no duplicate values
- HashSet (need a good Hash/Equal methods), TreeSet (needs comparability), LinkedHashSet

Queue
- Holding elements in an order, can only return one element, no random access
- add, peek, remove
- ArrayDeque, LinkedList, Stack, PriorityQueue

Deque
- Double ended queue, insert/remove both ends
- ArrayDeque, LinkedList

Stack 
- Don't actually use the Java stack, use the Deque

Maps
- key-value pairs, put, get, contains, remove, key/value/entry sets
- HashMap, TreeMap, LinkedHashMap

Iterable
- Can iterate over collections
- Use for (Object o : collection)

## Equality Checking
Default Java is to compare objects by identity/address. You have to be careful what 'equals' means for two different collections.

## Sorted Collections

TreeSet, TreeMap, PriorityQueue

Elements in a sorted collection need to be comparable - there must be a working >, <, and == methods

Implementation:

```
public class TimeOfDay implements Comparable<TimeOfDay> { ...
  @Override
  public int compareTo(TimeOfDay timeOfDay) {
    int result = Integer.compare(hour, timeOfDay.hour);
    if (result == 0) {
      result = Integer.compare(minute, timeOfDay.minute);
    }
    return result;
  } ...
}
```

A Compare function returns a negative value if you are smaller, 0 if you are equal, and positive if you are larger. Most smaller classes have a built in compare method. 

What happens if you modify an object that is currently in a tree? It's sort order could change, and now it's in the wrong place, and now your tree is broken. If you want to change an object, you must take it out, then change it, then put it back in. If it's not in the right place, then your tree can't find it, and may say it's not in there when it is. If it's in a Hash, then if you change it it's Hash will change, and now you can't access it again.

