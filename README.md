# Faux Pas: Error handling in Functional Programming

[![Spilled coffee](docs/spilled-coffee.jpg)](https://pixabay.com/en/mistake-spill-slip-up-accident-876597/)

[![Stability: Sustained](https://masterminds.github.io/stability/sustained.svg)](https://masterminds.github.io/stability/sustained.html)
![Build Status](https://github.com/zalando/faux-pas/workflows/build/badge.svg)
[![Coverage Status](https://img.shields.io/coveralls/zalando/faux-pas/main.svg)](https://coveralls.io/r/zalando/faux-pas)
[![Code Quality](https://img.shields.io/codacy/grade/b3a619ff47574eb68f38bdf74906e91a/main.svg)](https://www.codacy.com/app/whiskeysierra/faux-pas)
[![Javadoc](http://javadoc.io/badge/org.zalando/faux-pas.svg)](http://www.javadoc.io/doc/org.zalando/faux-pas)
[![Release](https://img.shields.io/github/release/zalando/faux-pas.svg)](https://github.com/zalando/faux-pas/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/faux-pas.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/faux-pas)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/zalando/faux-pas/main/LICENSE)

> **Faux pas** *noun*, /fəʊ pɑː/: blunder; misstep, false step

_**F**aux  **P**as_ is a library that simplifies error handling for **F**unctional **P**rogramming in Java. It fixes the
issue that none of the functional interfaces in the Java Runtime by default is allowed to throw checked exceptions.

- **Technology stack**: Java 8+, functional interfaces
- **Status**:  0.x, originally ported from [Riptide](https://www.github.com/zalando/riptide), used in production

## Example

```java
interface Client {
    User read(final String name) throws IOException;
}

Function<String, User> readUser = throwingFunction(client::read);
readUser.apply("Bob"); // may throw IOException directly
```

## Features

- Checked exceptions for functional interfaces 
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
- [*sneakily throws*](https://projectlombok.org/features/SneakyThrows.html) the original exception

#### Creation

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

### CompletableFuture.exceptionally(Function)

[`CompletableFuture.exceptionally(..)`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html#exceptionally-java.util.function.Function-)
is a very powerful but often overlooked tool. It allows to inject [partial exception handling](https://stackoverflow.com/questions/37032990/separated-exception-handling-of-a-completablefuture)
into a `CompletableFuture`:

```java
future.exceptionally(e -> {
    Throwable t = e instanceof CompletionException ? e.getCause() : e;

    if (t instanceof NoRouteToHostException) {
        return fallbackValueFor(e);
    }

    throw e instanceof CompletionException ? e : new CompletionException(t);
})
```

Unfortunately it has a contract that makes it harder to use than it needs to:

- It takes a [`Throwable`](https://docs.oracle.com/javase/8/docs/api/java/lang/Throwable.html) as an argument, but
  doesn't allow to re-throw it *as-is*. This can be circumvented by optionally [wrapping it in a
  `CompletionException`](http://cs.oswego.edu/pipermail/concurrency-interest/2014-August/012910.html) before
  rethrowing it.
- The throwable argument is [sometimes wrapped](https://stackoverflow.com/questions/27430255/surprising-behavior-of-java-8-completablefuture-exceptionally-method) 
  inside a [`CompletionException`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletionException.html)
  and sometimes it's not, depending on whether there is any other computation step before the `exceptionally(..)` call
  or not.
  
In order to use the operation correctly one needs to follow these rules:
1. Unwrap given throwable if it's an instance of `CompletionException`.
2. Wrap checked exceptions in a `CompletionException` before throwing.

`FauxPas.partially(..)` relives some of the pain by changing the interface and contract a bit to make it more usable.
The following example is functionally equivalent to the one from above:

```java
future.exceptionally(partially(e -> {
    if (e instanceof NoRouteToHostException) {
        return fallbackValueFor(e);
    }

    throw e;
}))
```

1. Takes a `ThrowingFunction<Throwable, T, Throwable>`, i.e. it allows clients to
    - directly re-throw the throwable argument
    - throw any exception during exception handling *as-is*
2. Will automatically unwrap a `CompletionException` before passing it to the given function.
   I.e. the supplied function will never have to deal with `CompletionException` directly. Except for the rare occasion
   that the `CompletionException` has no cause, in which case it will be passed to the given function. 
3. Will automatically wrap any thrown `Exception` inside a `CompletionException`, if needed.

The last example is actually so common, that there is an overloaded version of `partially` that caters for this use 
particular case:

```java

future.exceptionally(partially(NoRouteToHostException.class, this::fallbackValueFor))
```

### CompletableFuture.whenComplete(BiConsumer)

```java
future.whenComplete(failedWith(TimeoutException.class, e -> {
    request.cancel();
}))
```

Other missing pieces in `CompletableFuture`'s API are `exceptionallyCompose` and `handleCompose`. Both can be seen as
a combination of `exceptionally` + `compose` and `handle` + `compose` respectively. They basically allow to supply
another `CompletableFuture` rather than concrete values directly. This is allows for asynchronous fallbacks:

```java
exceptionallyCompose(users.find(name), e -> archive.find(name))
```

## Getting Help

If you have questions, concerns, bug reports, etc., please file an issue in this repository's [Issue Tracker](../../issues).

## Getting Involved/Contributing

To contribute, simply make a pull request and add a brief description (1-2 sentences) of your addition or change. For
more details, check the [contribution guidelines](.github/CONTRIBUTING.md).

## Alternatives

- [Lombok's `@SneakyThrows`](https://projectlombok.org/features/SneakyThrows.html)
- [Durian's Errors](https://github.com/diffplug/durian)
- [Spotify's Completable Futures](https://github.com/spotify/completable-futures)

