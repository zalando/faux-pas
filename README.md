# Faux Pas: Error handling in Functional Programming

[![Banner 888×244](docs/spilled-coffee.jpg)](https://pixabay.com/en/mistake-spill-slip-up-accident-876597/)

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
- **Status**:  Alpha, Beta, 1.1, etc. It's OK to write a sentence, too. The goal is to let interested people know where this project is at.
- Describe what sets this apart from related-projects. Linking to another doc or page is OK if this can't be expressed in a sentence or two.

## Example

```java
interface Client {
    User read(final String name) throws IOException;
}

ThrowingFunction<String, User, IOException> loadUser = client::read;
Function<String, User> loadUserUnchecked = loadUser.with(unchecked());
```

## Features

- Unchecked exceptions

## Dependencies

- Java 8

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

### Try-with alternative

Traditional `try-with` statements are compiled into byte code that includes
[unreachable parts](http://stackoverflow.com/a/17356707) and unfortunately JaCoCo has no
[support for filtering](https://github.com/jacoco/jacoco/wiki/FilteringOptions) yet. That's why we came up with an
alternative implementation:

```java

tryWith(openStream(), stream -> {
    process(stream);
});

```

## Getting Help

If you have questions, concerns, bug reports, etc., please file an issue in this repository's [Issue Tracker](../../issues).

## Getting Involved/Contributing

To contribute, simply make a pull request and add a brief description (1-2 sentences) of your addition or change. For
more details, check the [contribution guidelines](CONTRIBUTING.md).

## Alternatives

- [Lombok's `@SneakyThrows`](https://projectlombok.org/features/SneakyThrows.html)
- [Durian's Errors](https://github.com/diffplug/durian) (alternative to Faux Pas) 

