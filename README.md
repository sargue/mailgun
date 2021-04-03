# Mailgun Java library

[![Javadocs](http://www.javadoc.io/badge/net.sargue/mailgun.svg)](http://www.javadoc.io/doc/net.sargue/mailgun)
[![Build Status](https://travis-ci.org/sargue/mailgun.svg?branch=master)](https://travis-ci.org/sargue/mailgun)
[![License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://www.opensource.org/licenses/MIT)
[![Download](https://api.bintray.com/packages/sargue/maven/net.sargue%3Amailgun/images/download.svg)](https://bintray.com/sargue/maven/net.sargue%3Amailgun/_latestVersion)

## Introduction
### What is this?

This is a small Java library to ease the sending of email messages using the
great [Mailgun](http://www.mailgun.com/) service. It uses the RESTful Java
client library [Jersey](https://jersey.github.io/).

### What is [Mailgun](http://www.mailgun.com/)?

An email sending service with a REST API.

### What is [Jersey](https://jersey.github.io/)?

A RESTful java library. Actually, the reference implementation of
[JAX-RS](http://jax-rs-spec.java.net/), the standard API for RESTful web
services for Java.

## Installation

Add the dependency to your project:

#### Gradle

`implementation 'net.sargue:mailgun:1.10.0'`

#### Maven

```xml
<dependency>
    <groupId>net.sargue</groupId>
    <artifactId>mailgun</artifactId>
    <version>1.10.0</version>
</dependency>
```

#### A note about dependencies

This project depends on the Jersey library (see above). The Jersey library
is part of the bigger *glassfish*/*Oracle* ecosystem which apparently
doesn't have top notch compatibility very high on its priority list.

Said so, you may encounter problems with dependencies as there are some
libraries which are repackaged under different Maven coordinates and will
leak duplicates on your classpath.

Please, see [issue #1](https://github.com/sargue/mailgun/issues/1) for details
and workarounds. Thanks for your understanding.

## Usage

The library is pretty straighforward. You just need to remember two classes:

* `Configuration`: which usually is a singleton you build once and re-use
* `Mail`: the entry point to build and send emails
* `MailContent`: an optional helper to build HTML and text message bodys

That were three classes but the last one is optional although very useful
if you want to send some simple messages in HTML.

The library is built to be used as a fluent interface, almost a DSL, so the code
is quite self explanatory.

### Javadocs

You can 
[browse the javadocs](http://www.javadoc.io/doc/net.sargue/mailgun) 
published thanks to the great javadoc.io service.

### Requirements and dependencies

The runtime requirement is Java 7 or higher.

Gradle is used to build the project and requires at least Java 8. I currently
build it using Java 11 (AdoptOpenJDK 11 with HotSpot).

Depends on [Jersey 2](https://jersey.github.io/) client.

### Android support

There is not. Android is not officially supported. I have no experience on Android development so I won't be able to help much on any issue. There are a [number of issues raised](https://github.com/sargue/mailgun/issues?q=label%3Aandroid) which indicate that the library *can* be used on Android but YMMV.

The main issue about using this library on android is the repackaging of some packages done by Jersey, like `javax.inject`. If using gradle you could try to add this:

```gradle
configurations {
    all*.exclude group: 'org.glassfish.hk2.external', module:'javax.inject'
}
```

Anyway try it and if you find a problem please report it. I will try to help.

### Configuration

First of all you need to prepare a `Configuration` object used by the library.

Usually you can do this once and keep the same object for all your calls. It
is thread safe.

```java
Configuration configuration = new Configuration()
    .domain("somedomain.com")
    .apiKey("key-xxxxxxxxxxxxxxxxxxxxxxxxx")
    .from("Test account", "postmaster@somedomain.com");
```

### Sending a basic email

```java
Mail.using(configuration)
    .to("marty@mcfly.com")
    .subject("This is the subject")
    .text("Hello world!")
    .build()
    .send();
```

### Sending an email with an attachment

```java
Mail.using(configuration)
    .to("marty@mcfly.com")
    .subject("This message has an text attachment")
    .text("Please find attached a file.")
    .multipart()
    .attachment(new File("/path/to/image.jpg"))
    .build()
    .send();
```

### More examples

Some fields (more or less the ones that make sense) can be repeated.
Like `to()` to send to multiple recipients, `attachment()` to include
more than one attachment and so on.

```java
Mail.using(configuration)
    .to("marty@mcfly.com")
    .to("george@mcfly.com")
    .cc("lorraine@mcfly.com")
    .cc("dave@mcfly.com")
    .subject("This is the subject")
    .text("Hello world!")
    .build()
    .send();
```

```java
Mail.using(configuration)
    .to("marty@mcfly.com")
    .subject("This message has an text attachment")
    .text("Please find attached a file.")
    .multipart()
    .attachment(new File("/path/to/image.jpg"))
    .attachment(new File("/path/to/report.pdf"))
    .build()
    .send();
```

### Advanced content using content helpers

The classes on the package `net.sargue.mailgun.content` are designed 
to easily build basic HTML 
messages. It's not supposed to be used for building cutting edge responsive
modern HTML messages. It's just for simple cases where you need to send a
message and you want to use some basic HTML like tables and some formatting.

Some self explanatory examples:

```java
Mail.using(configuration)
    .body()
    .h1("This is a heading")
    .p("And this some text")
    .mail()
    .to("marty@mcfly.com")
    .subject("This is the subject")
    .build()
    .send();
```

```java
Mail.using(configuration)
    .body()
    .h3("Monthly report")
    .p("Report of the number of time travels this month")
    .table()
        .row("Marty", "5")
        .row("Doc", "7")
        .row("Einstein", "0")
    .end()
    .mail()
    .to("marty@mcfly.com")
    .subject("Monthly Delorean usage")
    .build()
    .send();
```

Of course you can keep the body content and mail building separated.

```java
Body body = Body.builder()
                .h1("This is a heading")
                .p("And this some text")
                .build();

Mail.using(configuration)
    .to("marty@mcfly.com")
    .subject("This is the subject")
    .content(body)
    .build()
    .send();
```

There is also a very powerful extension mechanism which are the *content 
converters*. Check it out with some more information about the these
 classes [in the wiki](https://github.com/sargue/mailgun/wiki/Mail-content-using-content-helpers).

## Changelog

The changelog is in a [separate page](https://github.com/sargue/mailgun/blob/master/CHANGELOG.md).

## Test suite

There is a test suite using [WireMock](http://wiremock.org) to mock the Mailgun
REST API endpoint.

The mail content test suite is a work in progress right now.

## Contributing

All contributions are welcome. Use the issues section to send feature requests.
Pull requests are also welcome, just try to stick with the overall code style
and provide some tests if possible.
