# Mailgun Java library

[![Javadocs](http://www.javadoc.io/badge/net.sargue/mailgun.svg)](http://www.javadoc.io/doc/net.sargue/mailgun)
[![Build Status](https://travis-ci.org/sargue/mailgun.svg?branch=master)](https://travis-ci.org/sargue/mailgun)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=mailgun&metric=alert_status)](https://sonarcloud.io/dashboard?id=mailgun)
[![License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](http://www.opensource.org/licenses/MIT)
[![Download](https://api.bintray.com/packages/sargue/maven/net.sargue%3Amailgun/images/download.svg)](https://bintray.com/sargue/maven/net.sargue%3Amailgun/_latestVersion)

# ALPHA VERSION

This is the branch for a future version 2.x of the library. This is
a work in progress. Comments, suggestions and contributions are welcome.

This is alpha version work in progress. Breaking changes will happen.

## Introduction
### What is this?

This is a small Java library to ease the sending of email messages using the
great [Mailgun](https://www.mailgun.com/) service.

### What is [Mailgun](http://www.mailgun.com/)?

An email sending service with a REST API.

## Installation

Add the dependency to your project:

#### Gradle

`compile 'net.sargue:mailgun:2.0.0'`

#### Maven

```xml
<dependency>
    <groupId>net.sargue</groupId>
    <artifactId>mailgun</artifactId>
    <version>2.0.0</version>
</dependency>
```

### REST client library

TL;DR

You need a REST client library installed and supported on the classpath.

If you are not using any REST client library in your project add
these dependencies:

```
compile 'org.glassfish.jersey.core:jersey-client:2.25.1'
compile 'org.glassfish.jersey.media:jersey-media-multipart:2.25.1'
```

This library requires a REST client installed on the classpath. The
only library supported out of the box is [Jersey](https://jersey.github.io/)
but you can implement support for any library. See
[the wiki](https://github.com/sargue/mailgun/wiki/REST-client-library-support)
for more details.

## Usage

The library is pretty straighforward. You just need to remember two classes:

* `Configuration`: which usually is a singleton you build once and re-use
* `Mail`: the entry point to build and send emails
* `MailContent`: an optional helper to build HTML and text message bodys

That was three classes but the last one is optional although very useful
if you want to send some simple messages in HTML.

The library is built to be used as a fluent interface, almost a DSL, so the code
is quite self explanatory.

### Javadocs

You can 
[browse the javadocs](http://www.javadoc.io/doc/net.sargue/mailgun) 
published thanks to the great javadoc.io service.

### Requirements and dependencies

Requires Java 8+ and a
[supported REST client library](https://github.com/sargue/mailgun/wiki/REST-client-library-support)

#### About certificates and Java versions

On the 22nd of January 2018 the mailgun service switched its SSL certificate
and the current one is not supported out of the box on any Java version
prior to JRE 8u91.

TL;DR You need to upgrade your JRE or manually import the CA.

See [this SO answer](https://stackoverflow.com/a/48425037/518992) and
[this issue](https://github.com/sargue/mailgun/issues/27).

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
    .subject("This message has a text attachment")
    .text("Please find attached a file.")
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
    .subject("This message has a text attachment")
    .text("Please find attached a file.")
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
