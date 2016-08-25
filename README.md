# Faux Pas: Error handling in Functional Programming

[![Spilled coffee](docs/spilled-coffee.jpg)](https://pixabay.com/en/mistake-spill-slip-up-accident-876597/)

[![Build Status](https://img.shields.io/travis/zalando-incubator/faux-pas.svg)](https://travis-ci.org/zalando-incubator/faux-pas)
[![Coverage Status](https://img.shields.io/coveralls/zalando-incubator/faux-pas.svg)](https://coveralls.io/r/zalando-incubator/faux-pas)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/org.zalando/faux-pas/badge.svg)](http://www.javadoc.io/doc/org.zalando/faux-pas)
[![Release](https://img.shields.io/github/release/zalando-incubator/faux-pas.svg)](https://github.com/zalando-incubator/faux-pas/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/faux-pas.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/faux-pas)

> **Faux pas** (fō pä′), noun: a slip or blunder in etiquette, manners, or conduct; an embarrassing social blunder or indiscretion.

_**F**aux  **P**as_ is library that simplifies error handling for **F**unctional **P**rogramming in Java.

Put a meaningful, short, plain-language description of what
this project is trying to accomplish and why it matters.
Describe the problem(s) this project solves.
Describe how this software can improve the lives of its audience.

- **Technology stack**: Java 8+, functional interfaces
- **Status**:  0.x, currently being ported from [Riptide](https://www.github.com/zalando/riptide)

## Example

```java
interface Client {
    User read(final String name) throws IOException;
}

ThrowingFunction<String, User, IOException> readUser = client::read;

readUser.apply("Bob"); // may throw IOException
readUser.with(unchecked()).apply("Bob") // may throw UncheckedIOException
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

 - [`ThrowingRunnable`](blob/master/src/main/java/org/zalando/fauxpas/ThrowingRunnable.java)
 - [`ThrowingSupplier`](blob/master/src/main/java/org/zalando/fauxpas/ThrowingSupplier.java)
 - [`ThrowingConsumer`](blob/master/src/main/java/org/zalando/fauxpas/ThrowingConsumer.java)
 - [`ThrowingFunction`](blob/master/src/main/java/org/zalando/fauxpas/ThrowingFunction.java)
 - [`ThrowingPredicate`](blob/master/src/main/java/org/zalando/fauxpas/ThrowingPredicate.java)
 - [`ThrowingBiConsumer`](blob/master/src/main/java/org/zalando/fauxpas/ThrowingBiConsumer.java)
 - [`ThrowingBiFunction`](blob/master/src/main/java/org/zalando/fauxpas/ThrowingBiFunction.java)
 - [`ThrowingBiPredicate`](blob/master/src/main/java/org/zalando/fauxpas/ThrowingBiPredicate.java)

The followings statements apply to each of them:
- extends the official interface, i.e. they are 100% compatible
- defaults to [*sneakily throwing*](https://projectlombok.org/features/SneakyThrows.html) the original exception
- has a conversion method `with(Strategy)` to flexibly override the error handling, see [Strategies](#strategies) for details

### Syntactic Sugar

The way the Java runtime implemented functional interfaces always requires additional type information, either by
using a cast or a local variable:

```java
client::read.with(unchecked()) // compiler error
((ThrowingFunction<String, User, IOException>) client::read).with(unchecked()) // too verbose

ThrowingFunction<String, User, IOException> readUser = client::read; // local variable not always desired
readUser.with(unchecked())
```

As a workaround there is a static *factory* method for every interface type in`FauxPas`. All of them are called
`throwingRunnable`, `throwingSupplier` and so forth. It allows for concise one-line statements:

```java
throwingFunction(client:.read).with(unchecked())
```

### Strategies

Every functional interface type that is defined in *Faux Pas* comes with a conversion method `with(Strategy)` that
basically performs the transformation from `Throwing*` ➙ `*` while customizing the error handling strategy. There are
two strategies available by default:

#### Logging

The `FauxPas.logging()` strategy handles any raised exception by logging them to a customizable logger and falling
back to default values, i.e. `null` or `false` depending on the interface.

#### Rethrow

The `FauxPas.rethrow()` strategy handles any raised exception by either rethrowing them directly, e.g. `Error` and
`RuntimeException` or by wrapping them meaningful unchecked exceptions, e.g. `UncheckedIOException` or
`RuntimeException`. It allows to customize the whole transformation process or only the fallback part:

```java
// completely defines exception transformation
function.with(rethrow(myHandling))
```

```java
// only transform unmapped exceptions
function.with(rethrow(checked(myFallback)))
```

```java
// uses Lombok's @SneakyThrows to re-throw checked exceptions without declaring it
function.with(rethrow(sneakily()))
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

