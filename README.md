# Faux Pas: Error handling in Functional Programming

[![Spilled coffee](docs/spilled-coffee.jpg)](https://pixabay.com/en/mistake-spill-slip-up-accident-876597/)

[![Build Status](https://img.shields.io/travis/zalando/faux-pas/master.svg)](https://travis-ci.org/zalando/faux-pas)
[![Coverage Status](https://img.shields.io/coveralls/zalando/faux-pas/master.svg)](https://coveralls.io/r/zalando/faux-pas)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/org.zalando/faux-pas/badge.svg)](http://www.javadoc.io/doc/org.zalando/faux-pas)
[![Release](https://img.shields.io/github/release/zalando/faux-pas.svg)](https://github.com/zalando/faux-pas/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/faux-pas.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/faux-pas)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/zalando/faux-pas/master/LICENSE)

> **Faux pas** *noun*, /fəʊ pɑː/: blunder; misstep, false step

_**F**aux  **P**as_ is library that simplifies error handling for **F**unctional **P**rogramming in Java. It fixes the
issue that none of the functional interfaces in the Java Runtime by default is allowed to throw checked exceptions.

- **Technology stack**: Java 8+, functional interfaces
- **Status**:  0.x, currently being ported from [Riptide](https://www.github.com/zalando/riptide)

## Example

```java
interface Client {
    User read(final String name) throws IOException;
}

Function<String, User> readUser = throwingFunction(client::read);
readUser.apply("Bob"); // may throw IOException directly

Function<String, User> readUser = throwingFunction(client::read, rethrow(unchecked()));
readUser.apply("Bob"); // may throw UncheckedIOException
```

## Features

- Checked exceptions for functional interfaces 
- Flexible strategies for error handling
- Compatible with the JDK types

## Dependencies

- Java 8 or higher
- Lombok (no runtime dependency)

## Installation

Add the following dependency to your project:

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>faux-pas</artifactId>
    <version>${faux-pas.version}</version>
</dependency>
```

## Usage

### Throwing functional interfaces

*Faux Pas* has a variant of every major functional interface from the Java core:

 - [`ThrowingRunnable`](src/main/java/org/zalando/fauxpas/ThrowingRunnable.java)
 - [`ThrowingSupplier`](src/main/java/org/zalando/fauxpas/ThrowingSupplier.java)
 - [`ThrowingConsumer`](src/main/java/org/zalando/fauxpas/ThrowingConsumer.java)
 - [`ThrowingFunction`](src/main/java/org/zalando/fauxpas/ThrowingFunction.java)
 - [`ThrowingUnaryOperator`](src/main/java/org/zalando/fauxpas/ThrowingUnaryOperator.java)
 - [`ThrowingPredicate`](src/main/java/org/zalando/fauxpas/ThrowingPredicate.java)
 - [`ThrowingBiConsumer`](src/main/java/org/zalando/fauxpas/ThrowingBiConsumer.java)
 - [`ThrowingBiFunction`](src/main/java/org/zalando/fauxpas/ThrowingBiFunction.java)
 - [`ThrowingBinaryOperator`](src/main/java/org/zalando/fauxpas/ThrowingBinaryOperator.java)
 - [`ThrowingBiPredicate`](src/main/java/org/zalando/fauxpas/ThrowingBiPredicate.java)

The followings statements apply to each of them:
- extends the official interface, i.e. they are 100% compatible
- defaults to [*sneakily throwing*](https://projectlombok.org/features/SneakyThrows.html) the original exception

### Creation

The way the Java runtime implemented functional interfaces always requires additional type information, either by
using a cast or a local variable:

```java
// compiler error
client::read.apply(name);

// too verbose
((ThrowingFunction<String, User, IOException>) client::read).apply(name);

// local variable may not always be desired
ThrowingFunction<String, User, IOException> readUser = client::read;
readUser.apply(name);
```

As a workaround there is a static *factory* method for every interface type in`FauxPas`. All of them are called
`throwingRunnable`, `throwingSupplier` and so forth. It allows for concise one-line statements:

```java
List<User> users = names.stream()
    .map(throwingFunction(client::read))
    .collect(toList());
```

### Strategies

Every factory method `FauxPas.throwing*(..)` can optionally be used with a `Strategy`, a pluggable mechanism that
basically performs the transformation from `Throwing*` ➙ `*` while customizing the error handling strategy. There are
three strategies available by default:

#### Logging

The `FauxPas.loggingAnd(..)` strategy handles any raised exception by logging them to a customizable logger and falling
back to another strategy, e.g. one of the remaining:

```java
throwingFunction(client::read, loggingAnd(rethrow(sneakily())))
```

#### Ignore

The `FauxPas.ignore()` strategy handles any raised exception by ignoring them completely.

#### Rethrow

The `FauxPas.rethrow()` strategy handles any raised exception by either rethrowing them directly, e.g. `Error` and
`RuntimeException` or by wrapping them in meaningful unchecked exceptions, e.g. `UncheckedIOException` or
`RuntimeException`. It allows to customize the whole transformation process or only the fallback part:

```java
// completely defines exception transformation
throwingFunction(client::read, rethrow(throwable -> {
    // transform here
}))
```

```java
// only transform unmapped exceptions
throwingFunction(client::read, rethrow(unchecked(throwable -> {
    // transform everything that is neither Error nor RuntimeException
})))
```

```java
// uses Lombok's @SneakyThrows to re-throw checked exceptions without declaring it
throwingFunction(client::read, rethrow(unchecked(sneakily())))
```

### Try-with-resources alternative

Traditional `try-with-resources` statements are compiled into byte code that includes
[unreachable parts](http://stackoverflow.com/a/17356707) and unfortunately JaCoCo has no
[support for filtering](https://github.com/jacoco/jacoco/wiki/FilteringOptions) yet. That's why we came up with an
alternative implementation. The [official example](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)
for the `try-with-resources` statement looks like this:

```java
try (BufferedReader br =
               new BufferedReader(new FileReader(path))) {
    return br.readLine();
}
```

Compared to ours:

```java
return tryWith(new BufferedReader(new FileReader(path)), br -> 
    br.readLine()
);
```

## Getting Help

If you have questions, concerns, bug reports, etc., please file an issue in this repository's [Issue Tracker](../../issues).

## Getting Involved/Contributing

To contribute, simply make a pull request and add a brief description (1-2 sentences) of your addition or change. For
more details, check the [contribution guidelines](CONTRIBUTING.md).

## Alternatives

- [Lombok's `@SneakyThrows`](https://projectlombok.org/features/SneakyThrows.html)
- [Durian's Errors](https://github.com/diffplug/durian)

