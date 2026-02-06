# Filter Query API

Java library for programmatically building and evaluating filter expressions against resource objects.

Resources are represented as `Map<String, String>` where attribute names (keys) are case-sensitive and attributes (values) are case-insensitive.

Note: Requires Java 17+

## Usage

```java
// Create a simple user resource
Map<String, String> user = new LinkedHashMap<>();
user.put("firstname", "Joe");
user.put("surname", "Bloggs");
user.put("role", "administrator");
user.put("age", "35");

// Match all administrators older than 25
Filter filter = Filter.and(
    Filter.equalTo("role", "administrator"),
    Filter.greaterThan("age", "25")
);

filter.matches(user); // true

user.put("age", "20");
filter.matches(user); // false
```

## Available Filter predicates

| Method | Description |
|---|---|
| `Filter.trueValue()` | Always matches |
| `Filter.falseValue()` | Never matches |
| `Filter.and(filters...)` | Matches if all children match |
| `Filter.or(filters...)` | Matches if any child matches |
| `Filter.not(filter)` | Inverts the result |
| `Filter.present(attr)` | Matches if the attribute exists |
| `Filter.equalTo(attr, value)` | Case-insensitive equality |
| `Filter.greaterThan(attr, value)` | Numeric or lexicographic greater-than |
| `Filter.lessThan(attr, value)` | Numeric or lexicographic less-than |
| `Filter.regexMatches(attr, expr)` | Case-insensitive regex match |

You can compose Filters into arbitrarily complex expressions:

```java
Filter filter = Filter.or(
    Filter.and(
        Filter.equalTo("role", "manager"),
        Filter.greaterThan("age", "30")
    ),
    Filter.not(Filter.present("restricted"))
);
```

## String Representation

Filters can be converted to a user-friendly string using `toFilterString()`:

```java
Filter filter = Filter.and(
    Filter.equalTo("role", "administrator"),
    Filter.greaterThan("age", "30")
);

filter.toFilterString(); // ((role == "administrator") && (age > "30"))
```

## Design

The design hinges on the creation of an expression tree to represent a Filter. This tree is built as the user constructs their Filter.
The common interface of `matches()` allows us to treat all Filter operators the same, whether it is a grouping mechanism like `and()` or a simple `equalTo()`.
Once the composite tree is built, and the top-level `matches()` is called, the tree will be traversed and evaluated. The leaf nodes are evaluated using simple 
standard library comparisons and the parent nodes delegate to the leaf nodes using `stream()` filtering.

An alternative considered was the classic composite design pattern with a small interface and many classes implementing. This proved to be quite verbose though and 
I wanted to showcase some modern Java techniques (records, sealed interfaces, switch expressions, etc.) that allow you to focus more on business logic and less on boilerplate.

### Extending for new types

The library uses a sealed interface with records defined inside. All filter types are defined as subtypes of `Filter`, enabling exhaustive `switch` expressions 
with compile-time safety. To add a new type of filter you would simply add the new record and new static factory method, then add your filter logic into the 
`switch` expression. The compile-time safety aspect ensures that you must provide an implementation if you create a new type of filter.

### Third party applications
Third party code can pattern match on the filter structure directly (just like we do internally). One trade-off with this approach has to do with the compiler enforcement 
of exhaustiveness. So if the library developer adds a new record to Filter, every third party switch() that lacks a default branch will fail to compile. This could be annoying 
but forces the failure at compile time instead of surprising people at runtime.

Example third party extension:

```java
String sqlFilter = switch (filter) {
    case Filter.EqualTo eq -> eq.attribute() + " = '" + eq.value() + "'";
    case Filter.And and -> and.filters().stream()
            .map(this::toSql).collect(Collectors.joining(" AND "));
    // compiler enforces all cases are handled
};
```

### Filter string parsing
A good future improvement is to finish the implementation of filter string parsing. Right now we can build them from an expression tree, but cannot parse them back.
There are many edge cases here dealing with character sequences and malformed strings, but a jumping off point has been provided in `static Filter fromFilterString(String filterString) {}`.

This was not fully implemented since it was not a hard requirement.

## Development

This project uses the Gradle build system to handle task running. The `gradlew` wrapper utility is included per Gradle convention to allow use without downloading specific Gradle installations.

### Building
```
./gradlew build
```

### Testing

```
./gradlew test
```

### Publishing

To publish this library to the local Maven repo:

```
./gradlew publishToMavenLocal
```

To publish to a remote Maven repo, add a `publishing {}` section to `build.gradle` like so:
```groovy
publishing {
    publications {
        // create MavenPublication here
    }
    repositories {
        maven {
            name = "MyRepo" //  optional target repository name
            url = "https://some.server.org/repo/url"
            credentials {
                // creds here
            }
        }
    }
}
```
Then use 
```
./gradlew publish
```