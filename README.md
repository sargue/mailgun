# Mailgun Java library

[![Javadocs](http://www.javadoc.io/badge/net.sargue/mailgun.svg)](http://www.javadoc.io/doc/net.sargue/mailgun)

## Introduction
### What is this?

This is a small Java library to ease the sending of email messages using the
great [Mailgun](http://www.mailgun.com/) service. It uses the RESTful Java
client library [Jersey](https://jersey.java.net/).

### What is [Mailgun](http://www.mailgun.com/)?

An email sending service with a REST API.

### What is [Jersey](https://jersey.java.net/)?

A RESTful java library. Actually, the reference implementation of
[JAX-RS](http://jax-rs-spec.java.net/), the standard API for RESTful web
services for Java.

## Installation

Add the dependency to your project:

#### Gradle

`compile 'net.sargue:mailgun:1.0.0'`

#### Maven

```xml
<dependency>
    <groupId>net.sargue</groupId>
    <artifactId>mailgun</artifactId>
    <version>1.0.0</version>
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

* `Configuration`: which usually is a singleton you build one and re-use
* `MailBuilder`: the entry point to build and send emails
* `MailContent`: an optional helper to build HTML and text message bodys

That was three classes but the last one is optional although very useful
if you want to send some simple messages in HTML.

The library is built to be used as a fluent interface, almost a DSL, so the code
is quite self explanatory.

You can [check the javadocs](http://www.javadoc.io/doc/net.sargue/mailgun).

### Requirements and dependencies

Requires Java 7+.

Depends on [Jersey 2](https://jersey.java.net/) client.

### Configuration

First of all you need to prepare a `Configuration` object used by the library.

Usually you can do this once and keep the same object for all your calls. It
is thread safe.

```
Configuration configuration = new Configuration()
    .domain("somedomain.com")
    .apiKey("key-xxxxxxxxxxxxxxxxxxxxxxxxx")
    .from("Test account", "postmaster@somedomain.com");
```

### Sending a basic email

```
MailBuilder.using(configuration)
    .to("marty@mcfly.com")
    .subject("This is the subject")
    .text("Hello world!")
    .build()
    .send();
```

### Sending an email with an attachment

```
MailBuilder.using(configuration)
    .to("marty@mcfly.com")
    .subject("This message has an text attachment")
    .text("Please find attached a file.")
    .multipart()
    .attachment(new File("/path/to/image.jpg"))
    .build()
    .send();
```

### Advanced content using `MailContent`

The `MailContent` class is a helper designed to build easily basic HTML 
messages. It's not supposed to be used for building cutting edge responsive
modern HTML messages. It's just for simple cases where you need to send a
message and you want to use some basic HTML like tables and some formatting.

Some self explanatory examples:

```
MailContent content = new MailContent()
    .h3("Monthly report")
    .p("Report of the number of time travels this month")
    .table()
        .row("Marty", "5")
        .row("Doc", "7")
        .row("Einstein", "0")
    .end()
    .close();

MailBuilder.using(configuration)
    .to("marty@mcfly.com")
    .subject("Monthly Delorean usage")
    .content(content)
    .build()
    .send();
```

I have some internal half-developed extensions to this class, like converters,
formatters, text padding and limiters, and so on. Not sure if it will be
useful so send some feedback if you want to see more functionality here.

## Test suite

There is a test suite using [WireMock](http://wiremock.org) to mock the Mailgun
REST API endpoint.

The mail content test suite is a work in progress right now.

## Contributing

All contribution is welcome. Use the issues section to send feature requests.
Pull requests are also welcome, just try to stick with the overall code style
and provide some tests if possible.