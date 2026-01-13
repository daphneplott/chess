# My notes

# Java Script
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




