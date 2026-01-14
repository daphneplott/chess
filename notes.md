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
Use "public class Child extends Parent". 

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



