# Mailgun Java library

## Introduction
### What is this?

This is a small Java library to ease the sending of email messages using the
great [Mailgun](http://www.mailgun.com/) service. It uses the RESTful Java
client library [Jersey](https://jersey.java.net/).

### What is [Mailgun](http://www.mailgun.com/)?

An email sending service with a REST API.

### What is Jersey?

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

## Usage

The library is pretty straighforward. You just need to remember two classes:

* `Configuration`: which usually is a singleton you build one and re-use
* `MailBuilder`: the entry point to build and send emails
* `MailContent`: an optional helper to build HTML and text message bodys

That was three classes but the last one is optional although very useful
if you want to send some simple messages in HTML.

The library is built to be used as a fluent interface, almost a DSL, so the code
is quite self explanatory.

### Requirements and dependencies

Requires Java 7+.

Depends on [Jersey 2](https://jersey.java.net/).

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

The test suite is pretty basic as, for the moment, just builds and sends
some real messages through Mailgun.

In order to enable the test you need to configure some properties as I cannot
commit it to a public repository.

Create a `mailgun-test.properties` in the project root directory with a content
like this:

```
domain = yourmailgundomain.com
apiKey = key-1234yourkey
fromName = Test account
fromEmail =  postmaster@yourmailgundomain.com
toName = Your Name
toEmail = you@email.com
```

## Contributing

All contribution is welcome. Use the issues section to send feature requests.
Pull requests are also welcome, just try to stick with the overall code style
and provide some tests if possible.