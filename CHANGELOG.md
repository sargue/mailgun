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