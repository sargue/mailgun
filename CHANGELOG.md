# Changelog

## v1.0.0

First published version.

## v1.1.0

* New *fire and forget* async send method. See `Mail.sendAsync()`.
* Some notable changes on the content generation helpers. More info
[in the wiki](https://github.com/sargue/mailgun/wiki/Mail-content-using-content-helpers)
The main code for sending simple mails hasn't changed.
TL;DR Don't use `MailBuilder` anymore, use `Mail.bodyBuilder()`.

Migration guide:

Where you had
```java
MailContent mailContent = new MailContent()
     [content stuff here]
     .close();

MailBuilder.using(configuration)
     .content(mailContent)
     [mail envelope stuff here]
     .build()
     .send();
```

You can translate it to this beauty:
```java
Mail.using(configuration)
    .body()
    [content stuff here]
    .mail()
    [mail envelope stuff here]
    .build()
    .send();
```

## v1.2.0

* New method to add inline images thanks to [Lance Reid](https://github.com/lancedfr). See [PR](https://github.com/sargue/mailgun/pull/5).

## v1.3.0

* New method to retrieve the body of the response. Useful to check more information in the event of errors.

## v1.3.1

* Merged PR [MailBuilder support for null name, to reduce burden of user](https://github.com/sargue/mailgun/pull/9)

## v1.3.2

* Fixed dependency: Jersey 2.25 as >=2.26 targets Java EE 8 and Java 8 language level.

## 1.4.0

* New [low level functions](https://github.com/sargue/mailgun/wiki/Mail-content-using-content-helpers#low-level-html) on the HTML builder.

## 1.4.1

* Fixed some null handling

## 1.5.0

* Configuration now can store default parameters to be used when they
are not specified on each mail building process. Inspired by [
this issue report](https://github.com/sargue/mailgun/issues/21)

## 1.6.0

* New methods to retrieve parameter values from a Mail instance

## 1.7.0

* New configuration facility to provide default callbacks for async sending

## 1.8.0

* Fixed callback factory lacking context (Mail reference). Breaks v1.7.0 compatibility. Sorry about that, even if v1.7.0 lived only one day.
* New mail filter to decide mail sending per instance.

## 1.8.1

* Fixed text content missing line separators

## 1.9.0

* New configuration options to define timeouts for the underlying connection client. [#29](https://github.com/sargue/mailgun/issues/29)

## 1.9.1

* JAX-RS client reuse, better performance under load. Remember to reuse
the Configuration object and close it when it's no longer needed. It's safe
to use just one for the entire life of the application.

## 1.9.2

* security improvements

## 1.10.0

* Merged PR [to add convenience methods to work with Mailgun's templates](https://github.com/sargue/mailgun/pull/36)
* Updated Gradle to version 6, now the project is built with Java 11 but
keeping compatibility with Java 7 for now.
* Due to the sunsetting of bintray I'm now publishing directly to Maven Central
via Sonatype.

## 2.0.0

* Migration to `jakarta.*` package name prefix: [#46](https://github.com/sargue/mailgun/issues/46)
* If you need to keep compatibility with `javax.*` stick with versions 1.x.x