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

## Inner Classes
Purpose: Limiting scope, hiding information from external calls

If tagged as static, nested class doesn't have any special access to variables or methods. But if it isn't tagged as static, it can access any variables or methods of that object. This means you don't have to pass in additional values when creating the class, it can access your array already. If you don't want to pass method parameters, you can move the inner class to inside a method - called a local class. Where and how you define it determines which variable scope it has. If the class only gets used/called once, you can make it anonymous (without a name) by doing "return new SuperClass() {all the code}". What is the point of an anonymous function? If you really only use it in one place, don't bother giving it a name. These are more like Event Handler mini functions.

## Generic classes

Similar to a Template in C++, useful in strongly typed languages, where you have to know what kind of variable everything is. For example, ArrayList is a generic type, which has only 1 implementation, but you have to pass in a type. 

Example:
```
public class Pair<T, U> {
  private T value1;
  private U value2;

  public Pair(T value1, U value2) {
    this.value1 = value1;
    this.value2 = value2;
  }

  public T getValue1() {
    return value1;
  }
}
```

However, when you pass in the types, they have to be objects, not primitives. 

Instantiating a Generic Class: Pair<String, Integer> pair = new Pair<>("Hello", 123), can also be written as var pair = new Pair<String, Integer>("Hello", 123), where you can use 'var' to infer the variable type. 

Inheritance - can use inheritance to fill in type parameters with hardcoded value types.

public class StringPair extends Pair<String, String>

public class KeyValuePair<V> extends Pair<String, V>

Generic interfaces - can also be used to fill in type parameters. Can be used to pass in function objects, called "first class functions", where you can declare a variable, and store a function in it. Treating functions as a piece of data. 

Wildcards - use a special syntax of "?" to expand acceptable types. Instead of List<T>, you can make it include additional object types using List<? super T> or List<? extends T> to extend it to either super classes of type T, or any subclasses of type T. 

# Lambda Expressions

Anonymous functions, in-line, can be passed in as parameters, or stored as variables.

A functional interface is an interface that only has one method on it. Instead of writing a class, you can use a lambda function.

Syntax: (Parameter list) -> {function body}
- If only one parameter, can leave off parentheses
- If only one expression in the function body, it doesn't need curly braces, and it doesn't need a 'return' statement

How does the compiler deal with Lambda functions?
- Needs to convert a function to the parameter type called by the object method
- Gets converted to an anonymous inner class
- Converts concise syntax to verbose syntax
- Because of that, the parameter type NEEDS to be a functional interface, so that the compiler knows exactly which method to implement with the lambda

Functional Interfaces
- Runnable, method void run()
- Callable<V>, method V call()
- Comparable<T>, method in compare(T, T)
- Predicate, method bool test(input)

Creating function variables - store the lambda in a variable, and then you can call it multiple times in different places. You can call the variable like it's a function (although the syntax is more like an object method).

A lambda function can also be replaced with a method reference if the function really just calls another built in function. You can change x -> System.out.println(x) to System.out::println. You can pass in method names, or the 'new' constructor.

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

# I/O

File Class - wrapper around a file path, exists(), createNewFile(), delete()

## Streams
Low level read in. Takes in bytes or characters. InputStream takes in bytes or binary, which are video, image, etc, but NOT text. Reader and Writer read and write characters, which is text formatted data. Processes data sequentially. 

Data Sources
- File
- Keyboard input
- Socket/network connection
- Pipes
- URL pages

Transformations
- Decompress/compress data - like a zip file
- Decryption/encryption
- Compute a "digest" - run a hash to get a fingerprint of the file
- Byte counting
- Line counting
- Buffering - collect a chunk instead of just a single byte

Transformations are applied as wrapper classes that take the data file as input. Work like a linked list of streams.

DataOutputStream class lets you write binary-formatted data values

Reader/Writer interfaces are basically identical to the InputStream/OutputStream, but apply to text and characters.

Wrapper classes
- PrintWriter lets you write text-formatted data values (tokens)
- Scanner lets you read text-formatted data tokens

Use classes InputStreamReader or OutputStreamWriter to convert a stream to a reader

## Scanner

Methods
- hasNext()
- next()
- nextInt(), nextFloat(), etc
- useDelimiter(regex telling it what the token divider is - white space)
  - Use to skip comments
  - Overrides the delimeter - can only have one at a time, so use | in the regex if you want multiple

## In addition...

Files
- Class that lets you readAllLines and get a List<String> back

RandomAccessFile
- Ability to access information without going sequentially
- file pointer represents current location
- ability to move pointer to wherever you want, skipBytes(int), seek(long)
- Can read or write from where the pointer is

# JSON

Java-Script Object Notation

Specific format for sharing data. Text-based, Java-Script objects. Holds strings, numbers, boolean, array, objects, and null. Each object is like a dictionary, a set of key-value pairs. Like holding all the attributes of a python/java object. Nesting of lists and objects. Textual representation of a data tree. 

## I/O
Libraries in Java that are structured to both parse and create Json files.

Stream Parser
- Tokenizers that return one token at a time from the Json file
- Tokens are things like "begin object", "Key Name", "end object"
- Useful for pulling out one peice of data in the middle, or only when you want to read some of it

DOM
- Converts JSON text to an in-memory tree data structure
- Traverse the DOM to extract information
- Document Object Model (tree)

Serializer
- Going from Java object to Json file string
- Deserialize - take Json file string and turn it into a Java object
- Easy way to store our objects elsewhere
- Gson library - Gson gson = GsonBuilder().setPrettyPrint().create(); String jsonString = gson.toJson(java object);
- Gson gson = new Gson(); Object object = gson.fromJson(Reader, Object.class);
- Gson struggles with interfaces and inheritance - need to use a TypeAdapter
  - gson.registerTypeAdapter(Object.class, new TypeAdapter)
  - Override public Object read(JsonReader) which takes in the stream parser, and then parse it yourself

# Software Design

Structuring and organizing how your pieces work together. How can we create software, and do it well?

Goals:
- Works
- Easy to understand, debug, and maintain
- Holds up well under changes

General principles
- Don't repeat yourself!
- Decomposition of parts
- Make each piece do one thing, and does it well

### Principle 1 - Design is Inherently Iterative
You generally don't know enough to be able to design everything all at once. So, you can design a piece of it, then code it, find a problem, and redesign again. 
Designing everything first won't work out, and trying to just code without design is a bad idea. So, you need to iterate through both steps together.

### Principle 2 - Abstraction
Abstraction is a tool for dealing with complexity. You can use a car without knowing how everything works under the hood. In coding, classes, functions, and similar constructs are abstractions. Built in languages have a lot of their own abstractions, but you often need to build a lot of your own so that they can be well applied. Create classes to model real-life concepts, but then make that class an abstractions - give it a nice user interface wihout the user worrying about how exactly it works. Real world objects can be complicated, so you have to be judicious in your choices of which aspects of those objects are actually useful for your specific implementation. 

### Principle 3 - Good Naming
Descriptive, follow conventions. Classes should be nouns, methods should be verbs/verb phrases, or named after what they return. Use a thesaurus if you need to.

### Principle 4 - Single Responsibility
Each class has ONE and only one responsibility. You shouldn't be going back to change how a class works, or else it probably doesn't work right. A class can either do an action, or store data, but it should represent a single concept. Methods and functions should be similar, and they should perform one task. Your classes and functions should be easy to name, because they only do one thing. If a function or class really needs to do multiple things, then you should break it up, and delegate to helper functions/classes

### Principle 5 - Decomposition
Decomposition breaks complex problems into smaller and smaller pieces until you get to pieces that can be directly solved. Once you've solved all the subpieces, you can roll them up together to solve the larger pieces. With a larger piece, you need to break it into what responsibilities it has, and what concepts, ideas, and tasks it needs to do. The pieces should adhere to the single responsibility principle. Size of code classes or files is a good indication that it's too big. This process is used to decide which abstractions to use. System - subsystem - packages - classes - methods.

### Principle 6 - Algorithm and Data Structure Selection
No amount of abstraction will hide fundamentally flawed algorithms or data structures. Your program needs to be fast. You need to decide what you need to do with your data, so that you can choose the right structures.

### Principle 7 - Minimize Coupling
Code should be shy. The less classes that know each other the better. Minimize the number of other classes that a class interacts with or knows about. Low coupling reduces ripple effects when you change some piece of one code. You still need coupling so that your program will work, but don't include a dependency unless it's really necessary.

### Principle 8 - Encapsulation / Information Hiding
A class should hide its internal implementation as much as possible. Not all of the class needs to be seen, and if you hide as much as possible, then it makes it easier to use, as well as more protected against misuse or coupling. Private variables and methods can be changed without affecting any other code. You can also use naming conventions to hide implementation details (and make sure not to use naming that betrays those internal details). However, if a class is inherently tied to an implementation, use it in the name to be descriptive. You can use an interface to hide specific implementations, and declare variables as interface types to prevent improper use. 

### Principle 9 - Avoid Code Duplication
You shouldn't use copy-paste programming, because if you need to change it, you need to change all of those copies. Copied code is usually pretty important, so you may be missing an important abstraction. Common code can be factored into a separate method or class, or placed in a common superclass. 

# HTTP

Client connection with server: client initiates communication with the server, establishes a connection that allows data to be sent back and forth. In order to connect, the client and server machines each need an IP address, and the client needs to know the server's IP address to connect. Instead of using IP addresses in most cases, we normally use a domain name and the Domain Name Service to convert a domain name into an IP address. A server machine will probably be running multiple programs through the internet, so an IP address is not enough information. Each server program communicates on a 'port' number, so the client must know that port number to connect. A default HTTP port is 80, HTTPS is 443, email is 25, SSH is 22.

HTTP Get Request: Needs a URL - Uniform Resource Locator - Protocol, Domain name, port number (optional, falls back to default protocol ports), path string. When you type in a url into a browser, it makes a connection, then constructs an HTTP request, which looks like: 
- GET (request type) URL path HTTP/1.1 (HTTP version)
- (Headers, key/value pairs) Accept: types \n Accept-Encoding: types \n User-Agent: browser information
A GET response looks like:
- Version, status code, status code explanation
- Headers
- Empty line
- Response body

HTTP Post Request: Mostly how forms works. Request has a method, path, version, header, and request body. A post has data appended to it. The response will also have body data.

Request types: GET (get data), POST (give new data), PUT (update resource), DELETE (delete information)

Status codes: 200 - good, 400 - client error/bad request, 500 - server error, 300 - redirect

## Web API
A Web API uses things that look like URLs and HTTP requests to call functions over the web.

GET request
- Get /function_name, maybe one header is Authorization: Auth-Token
- Returns the same sort of response with a response body

POST request
- Sends input parameters in request body as a JSON object
- Needs to be a post request if you want to send data
- Use response status code to raise errors

## Curl
Used for debugging web APIs that don't use the browser. Command line tool to create and send HTTP requests. Available, shareable, good for automation. 

Example: curl byu.edu - returns response body, such as the actual home page for the byu website in html. Defaults to GET, can change using -X (type). Can use -v to show more information. Can use -H to specify headers, -d for request body data or --data-binary to put in request body data from file, -o to dump output response in a file. 

Using curl requests with the API:
- curl -X POST http://localhost:8080/session -d '{"username": "me", "password": "1234"}
- curl -X GET http://localhost:8080/game -H 'Authorization: auth-token'
- curl -X DELETE http://localhost:8080/session -H 'Authorization: auth-token'

Instead of using curl, there are some other interfaces you can use, such as postman, which you can use online, or in VS code.

## Web Server

### Javalin 

Javalin.create()
  .get("/hello", ctx -> ctx.result("Hello BYU!")
  .start(8080)

--- Similarly can be ...

public class AlternateSimpleHelloBYUServer {
  public static void main(String[] args) {
    Javalin.create()
      .get("/hello", AlternateSimpleHelloBYUServer::handleHello)
      .start(8080)
  }

  private static void handleHello(Context ctx) {
    ctx.result("Hello BYU!")
  }
}

--- Similarly can be ...

public class Alternate2SimpleHelloBYUServer {
  public static void main(String[] args) {
    Javalin.create()
      .get("/hello", new HelloHandler())
      .start(8080)
  }

  private static Class HelloHandler implments Handler {
    @Override
    public void handle(Context ctx) throws Exception {
      ctx.result("Hello BYU!")
    }
  }
}

Things necessary for project 3:
- Should include a port number as the first arg, or a default port
- use createHandlers(javalinServer) to move .get into function which runs as javalinServer.get()
- Put Handlers into their own files

What does the Server return:
- HTTP version, status code, reason
- Headers
- Content

Creating the context/response in a handler:
- context.json("{object}")
- context.status(number)
- context.contentType('application/json')
- context.header(name, value)
- context.result('{message: Hello BYU!}')
- body, headerMap, header, path, result, json, header, contentType, status

### Handlers

Routes - Url with a handler attached to it

Before and After Handlers
- Something called for every request
- Authentication
- Making sure the response bodies are full
- Logging or debugging
- If a Before handler throws an error, it won't proceed to other handlers
- Use javalin.before()
- Can take an optional pattern to restrict routes to which they are applied: before("/protected/*" ...)
- Additionally have after handlers

Extracting information from url:
- get("/hello/<name>", context -> {return "Hello: " + context.pathParam("name");})
- Use {} to match only one segment of the url
- Use <> to math the entire rest of the url
- Wildcard parameter: use * to match all, without caring what the value is

### Error Handling

Errors must be caught by the handler. Can use and catch exceptions. Can use the return value of the function to indicate if there was an error down the line. Can use the 'result' class to package the errors. Make sure any exceptions that are thrown (or percolated) are actually useful for each of those classes. Each layer of code should throw it's own exception type. Let the DAO be the only one to throw a DataAccessException, and let the Service throw something else, even through percolating.

Javalin
- javalin.exception(Exception.class, (e, ctx) -> {handle exception, put info into context})
- javalin.error(404, ctx -> {process erorr consistently across server})

### Serving Static Files

Allows a website to actually work, and be able to be visited. Need all the information to be together. Can use a resources folder to hold the html, css, js, images, data, icons, etc. Need to tell the server where the webfiles are. Files are called static because they aren't changing.

javalin.create(config -> config.staticFiles.add("folder name"))


# Quality Code

Software design is both engineering and an art.

Good principles:
- helpful naming
- formatting/indentation/whitespace
- comments, docstrings, explanations
- short methods or subfunctions
- decomposition

Naming conventions
- Classes explain what they represent
- functions explain what they do
- Packages are all lower case, reverse of domain name
- Classes use CamelCase with front capitalization
- Methods and variables are camelCase with no front capitalization
- Constants are ALL_CAPS

Readability
- Good naming
- Line length is not too long
- Comments (but not unnecessary ones)
- Put separate if conditions on separate lines
- Line things up well when you wrap lines
- Use proper indentation
- Good and consistent use of white space - newlines, indentation, spaces between variables or expressions
- Curly brace or parantheses placement
- Subexpressions or submethods
- Don't write code that never gets used

# Unit Testing

Positive test: make sure it works when given valid input. Negative test: make sure it fails or errors when it ought to.
Test Driven Development is when you write all of your tests before you actually write the code. This helps the coder understand the program better, and help them to focus on what it is supposed to look like. Need to test that each piece works the way that it should before adding it to the system, and then testing each smaller assembly, and so on for each and every step.

Unit testing focuses most on the classes, sometimes on certain methods. After unit testing you need integration testing and then system testing. But the person writing the code is in charge of the unit tests. 

Unit tests are methods that compare a calculated result to an expected result. Each method should have their own tests. Unit tests can be run and rerun frequently so that you know if any new code you've introduced has caused anything to fail. Testing old code is called regression testing.

A test driver is a program that runs all the tests, and gives you a report. You can use the framework Junit to create a test driver. Use Assertions.assertTrue(stuff), or assertEquals, assertFalse, assertThrows, assertArrayEquals, assertDoesNotThrow, assertNotEquals, assertHttpOk. Use decorator @Test above test cases, and don't put it above supporting methods. The tests should be nested inside a SomethingTest class. A lot of tests will have similar setup or objects, wo you can declare the variables in the class, and then tag a @BeforeAll method that can create all those objects, and an @AfterAll method that can clean up those objects (such as a server). Use @BeforeEach and @AfterEach to run in between each test, and potentially reset information.

# Relational Databases

Stores data, often lots of data in an efficient way. Structured so you can query it and update it. Handles multi-user access, catastrophic events, consistency.

Relational models: tables, linked ids, storing different kinds of objects. Uses SQL (Structured Query Language) to operate on database. Other types of databases can hold other types of data models.

Programmatic access - accessed using database APIs, and uses a database driver to work with the API; Interactive access - graphical interface for an end user.

Embedded vs Client: Embedded is a single use database, such as persistent information for a downloaded app. Client is where multiple users need to access data, and go through the network and a server. 

Table
- A table is like a class
- A row is like an object
- An element is like a attribute
- Relationships - uses primary and foreign keys

Example - Book Club

Member: id, name, email adress
Book: id, title, author, genre, category id
Books Read: member id, book id
Genre: genre, description

Primary key - unique identifiers for the rows in a certain table
Foregin key - pointer/associated with a primary key from another table

Representing Trees
- A category name may be Top, Must Read (new, old, really old), Optional (new, old, really old)
- Include a 'parent id' column that acts as a foreign key pointing to its own table

Relationships
- One to One - one column going to one other column. One person has one social security number, and that number shows up one place in a social security record
- One to Many - one column connected to lots of other tables. A state has many counties, but a county only belongs to one state.
- Many to Many - lots of columns connected to lots of other columns. A class contains many students, a student is in many classes. Need to create another table that records connections between students and classes (like enrollments).
- Better to keep tables separate and use keys instead of putting everything together at once.
- Can use entity relationship diagrams: draw lines that represent relationships. Use + at foreign key side, use o- or o< to denote One to One or One to Many. Trees will self reference.


SQL Queries:
- Select
- From
- Where

SQL Data Types
- Char(n) - fixed string length
- VarChart() - varying string length
- Integer, smallint, float, real, double precision, numeric(precision, scale), decimal(precision, scale)
- BLOB - binary large objects (images, sound, video)
- CLOB - character large object (text documents)
- Date, teim, timetz, timestamp, timestamptz

SQL Create
- CREATE TABLE name
- id integer not null primary key auto_increment, // Unique
- title varchar(255) not null,
- author varchar(255) not null,
- foreign key(genre) references genre(genre),
- foreign key(category_id) references category(id)
- Can also use create table if not exists

SQL insert
- insert into book (title, author, genre, category_id) values ('The work','Gerald Lund','Historical fiction', 3);

SQL Drop tables
- drop table if exists book;
- Table is gone, data is gone, it's just gone.
- Can't delete a table with columns being used as foreign keys in another table, you have to delete that other table first.

SQL updates
- Update member
- Set name = 'Christ Jones', email_adress = 'chris@gmail.com'
- Where id = 3
- If you don't include Where, it will affect everybody

SQL Delete
- Delete from book (cleans everything)
- Delete from member
- Where id = 3
- Foreign key constraints will impact what order you can remove stuff from
