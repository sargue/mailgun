# CLAUDE.md - Mailgun Java Library

This document provides comprehensive guidance for AI assistants working with the Mailgun Java library codebase.

## Project Overview

**Project Name:** Mailgun Java Library
**Current Version:** 2.0.0
**License:** MIT
**Language:** Java (requires Java 8+)
**Build Tool:** Gradle 7.6
**Package:** `net.sargue:mailgun`

### What This Library Does

A small Java library for sending email messages using the Mailgun REST API service. It provides a fluent interface (DSL-style) for building and sending emails with support for:
- Simple text and HTML emails
- Attachments and inline images
- Templates with parameters
- Async sending
- Multipart messages
- HTML content helpers for basic formatting

### Important Context

1. **Official Mailgun Library Exists:** Mailgun now has an [official Java library](https://github.com/mailgun/mailgun-java). This library is maintained independently.
2. **Version 2.x Migration:** Version 2.x uses `jakarta.*` package naming (JakartaEE 9). Version 1.x uses legacy `javax.*` naming.
3. **Android Support:** Not officially supported, but can work with specific configuration (see README.md:98-106)

## Codebase Structure

```
mailgun/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── net/sargue/mailgun/
│   │           ├── Configuration.java          # Main configuration class (thread-safe, reusable)
│   │           ├── Mail.java                   # Abstract mail representation
│   │           ├── MailBuilder.java            # Fluent builder for emails
│   │           ├── MailForm.java               # Form-based mail implementation
│   │           ├── MailMultipart.java          # Multipart mail implementation
│   │           ├── MultipartBuilder.java       # Builder for multipart emails
│   │           ├── MailgunException.java       # Custom exception
│   │           ├── Response.java               # API response wrapper
│   │           ├── MailRequestCallback.java    # Async callback interface
│   │           ├── MailRequestCallbackFactory.java
│   │           ├── MailSendFilter.java         # Filter for conditional sending
│   │           ├── package-info.java
│   │           └── content/                    # HTML content generation helpers
│   │               ├── Body.java               # Body content builder
│   │               ├── Builder.java            # Generic builder interface
│   │               ├── MailContent.java        # Content representation
│   │               ├── MessageBuilder.java     # Message-level builder
│   │               ├── ContentConverter.java   # Extensible content conversion
│   │               ├── Util.java               # Utility functions
│   │               └── package-info.java
│   └── test/
│       └── java/
│           └── net/sargue/mailgun/test/
│               ├── BasicTests.java             # Core functionality tests
│               ├── ConfigurationTests.java     # Configuration tests
│               ├── ContentTests.java           # Content generation tests
│               ├── HTMLContentTests.java       # HTML content tests
│               └── ExampleTest.java            # Usage examples
├── build.gradle                                # Gradle build configuration
├── gradle.properties                           # Gradle properties (credentials)
├── settings.gradle                             # Gradle settings
├── gradlew                                     # Gradle wrapper (Unix)
├── gradlew.bat                                 # Gradle wrapper (Windows)
├── .travis.yml                                 # Travis CI configuration
├── .gitignore                                  # Git ignore rules
├── README.md                                   # User documentation
├── CHANGELOG.md                                # Version history
├── PUBLISH.md                                  # Publishing notes (Catalan)
└── LICENSE.txt                                 # MIT License
```

## Key Architecture Patterns

### 1. Fluent Interface (Builder Pattern)

The library is designed as a fluent API. Users chain method calls to build emails:

```java
Mail.using(configuration)
    .to("user@example.com")
    .subject("Test")
    .text("Hello!")
    .build()
    .send();
```

**Important:** When modifying builders, maintain this fluent pattern by returning `this` or the appropriate builder type.

### 2. Configuration Singleton

The `Configuration` class:
- Is designed to be built once and reused throughout the application
- Is **thread-safe**
- Contains a single JAX-RS client for all requests
- Must be closed when no longer needed to free resources
- Supports default parameters that apply to all emails

**When modifying:** Ensure thread safety is maintained for any new fields.

### 3. Abstract Mail with Concrete Implementations

- `Mail` is abstract with two implementations:
  - `MailForm`: Simple form-based emails
  - `MailMultipart`: Emails with attachments/inline images

### 4. Content Converter Extension System

The `ContentConverter` interface allows extending the library to handle custom objects as email content. Converters are registered with the `Configuration` object.

## Dependencies

### Runtime Dependencies
- **Jersey 3.0.9** (JAX-RS implementation)
  - `jersey-client` - Core client
  - `jersey-hk2` - Dependency injection
  - `jersey-media-multipart` - Multipart support
- **Important:** Jersey 3.x uses `jakarta.*` namespace (not `javax.*`)

### Test Dependencies
- **JUnit 4.12** - Testing framework
- **WireMock 1.57** - HTTP API mocking
- **Awaitility 2.0.0** - Async testing
- **SLF4J Simple 1.7.21** - Logging for tests

### Dependency Compatibility Issues
Be aware of Jersey/GlassFish ecosystem compatibility issues. See README.md:55-66 and issue #1. Some libraries are repackaged under different Maven coordinates and can cause classpath conflicts.

## Development Workflows

### Building the Project

```bash
# Make gradlew executable (if needed)
chmod +x gradlew

# Build the project
./gradlew build

# Run tests only
./gradlew test

# Run checks (includes tests)
./gradlew check

# Generate Javadoc
./gradlew javadoc

# Generate source and javadoc jars
./gradlew sourcesJar javadocJar
```

### Running Tests

Tests use WireMock to mock the Mailgun REST API endpoint. All tests should:
1. Use the `WireMockRule` on port 8124
2. Configure test `Configuration` to point to `http://localhost:8124/api`
3. Stub expected API responses
4. Verify the correct HTTP requests were made

**Example test structure (see BasicTests.java:29-59):**
```java
@Rule
public WireMockRule wireMockRule = new WireMockRule(PORT);

@BeforeClass
public static void init() {
    configuration = new Configuration()
        .apiUrl("http://localhost:" + PORT + "/api")
        .domain(DOMAIN)
        .apiKey("key-test")
        .from(FROM_NAME, FROM_EMAIL);
}

@AfterClass
public static void cleanUp() {
    configuration.close(); // Important!
}
```

### Publishing (Maintainer Only)

The library is published to Maven Central via Sonatype. See PUBLISH.md for the manual process:
1. Add credentials to `gradle.properties`
2. Run `publish.cmd` (Windows) or equivalent
3. Login to https://oss.sonatype.org/
4. Close and release the staging repository

**For AI Assistants:** Do NOT attempt to publish or modify publishing configuration without explicit user request.

## Code Conventions

### Java Style
1. **Java Version:** Source and target compatibility is Java 8
2. **Indentation:** Standard Java conventions (appears to be 4 spaces)
3. **Package Structure:** Flat structure under `net.sargue.mailgun`, with `content` subpackage
4. **Null Handling:** Methods should handle null gracefully where appropriate
5. **Documentation:** Public APIs should have Javadoc comments
6. **NOSONAR Comments:** Suppression of SonarQube warnings is used sparingly (see Configuration.java:100)

### Naming Conventions
- **Classes:** PascalCase (e.g., `MailBuilder`, `Configuration`)
- **Methods:** camelCase, often matching email field names (e.g., `to()`, `from()`, `subject()`)
- **Constants:** UPPER_SNAKE_CASE for test constants
- **Packages:** lowercase (e.g., `net.sargue.mailgun.content`)

### Builder Pattern Specifics
- Builders return `this` for chaining
- Terminal operations return the built object or void
- `build()` creates the final object
- `send()` and `sendAsync()` execute the action

### Thread Safety
- `Configuration` is explicitly thread-safe
- `Mail` instances are not designed to be shared across threads
- The internal Jersey `Client` is reused and must be thread-safe

## Testing Conventions

### Test Organization
- Test classes in `net.sargue.mailgun.test` package
- Test class names end with `Tests` (not `Test`)
- Use descriptive test method names
- Group related tests in the same class

### WireMock Usage
Tests extensively use WireMock for mocking HTTP interactions:

```java
// Stub a successful response
stubFor(post(urlEqualTo("/api/domain/messages"))
    .willReturn(aResponse()
        .withStatus(200)
        .withBody("{\"message\":\"Queued\",\"id\":\"<123@mailgun>\"}")));

// Verify the request
verify(postRequestedFor(urlEqualTo("/api/domain/messages"))
    .withHeader("Authorization", matching("Basic .*"))
    .withRequestBody(containing("to=test@example.com")));
```

### Async Testing
Use Awaitility for async operations:
```java
await().atMost(5, TimeUnit.SECONDS)
    .until(callbackWasCalled);
```

## Common Modification Patterns

### Adding a New Mail Parameter

1. Add method to `MailBuilder`:
   ```java
   public MailBuilder newParameter(String value) {
       addParameter("o:new-parameter", value);
       return this;
   }
   ```

2. Add test in `BasicTests.java` or relevant test class

3. Update Javadoc and potentially README.md examples

### Adding a New Content Helper

1. Create method in `Body` class (content/Body.java)
2. Return appropriate builder for chaining
3. Implement HTML and text generation
4. Add tests in `ContentTests.java` or `HTMLContentTests.java`
5. Update documentation

### Modifying the Configuration

1. Add field to `Configuration` class
2. Add builder method returning `this`
3. Ensure thread-safety if mutable
4. Add getter method
5. Consider if it affects the deprecated `copy()` method
6. Add configuration tests in `ConfigurationTests.java`

## Important Files to Understand

### Core Classes (Priority Order)
1. **Configuration.java** - Start here; understand the config lifecycle
2. **Mail.java** - Understand send() and sendAsync() flow
3. **MailBuilder.java** - See how the fluent API works
4. **MailForm.java** & **MailMultipart.java** - Concrete implementations

### Content Helpers
5. **content/Body.java** - HTML/text content generation
6. **content/ContentConverter.java** - Extension mechanism

### Tests
7. **test/BasicTests.java** - Best examples of usage and testing patterns

## Git Workflow

### Branching
- Main branch: `master` (note: not `main`)
- Feature branches: Create from `master`
- Current development branch: `claude/create-codebase-documentation-015tv2ksdAVJHrhaLB9ykzmy`

### Commits
- Write clear, concise commit messages
- See git log for style (appears to be conventional)
- Recent commits show merge commits from pull requests

### CI/CD
- **Travis CI** runs on every commit
- Build command: `./gradlew check`
- Java version: OpenJDK 8

## Things to Avoid

1. **Breaking the Fluent API:** Always maintain method chaining
2. **Changing Public API Without Discussion:** This is a published library
3. **Breaking Thread-Safety:** Especially in `Configuration`
4. **Adding Heavy Dependencies:** Keep the library lightweight
5. **Supporting Pre-Java 8:** Language level is Java 8
6. **Changing Package Structure:** `net.sargue.mailgun` is established
7. **Removing Deprecated Methods:** May break existing users
8. **Modifying Publishing Configuration:** Without explicit request
9. **Creating Android-Specific Code:** Not officially supported
10. **Using `javax.*` imports:** Version 2.x uses `jakarta.*`

## Common Questions

### Q: Should I use `javax.*` or `jakarta.*`?
**A:** Version 2.x uses `jakarta.*`. Only use `javax.*` when working on 1.x branch.

### Q: How do I test email sending without calling Mailgun?
**A:** Use WireMock to mock the HTTP endpoints. See existing tests.

### Q: Can I make breaking changes?
**A:** Discuss with the maintainer first. This is a published library with users.

### Q: Should I update dependencies?
**A:** Be very careful. Jersey/GlassFish ecosystem has compatibility issues. Test thoroughly.

### Q: Where is the main entry point?
**A:** Users start with `Mail.using(configuration)` or `Configuration` constructor.

### Q: How do I add new Mailgun API features?
**A:** Add parameters via `addParameter()` in `MailBuilder`. Check Mailgun API docs.

## Useful Commands Reference

```bash
# Build and test
./gradlew clean build

# Run specific test class
./gradlew test --tests BasicTests

# Generate all artifacts
./gradlew clean build sourcesJar javadocJar

# Publish to Maven Central (maintainer only)
./gradlew publish

# Clean build directory
./gradlew clean

# See all tasks
./gradlew tasks --all
```

## External Resources

- **Javadocs:** http://www.javadoc.io/doc/net.sargue/mailgun
- **Mailgun API Docs:** https://documentation.mailgun.com/
- **Jersey Documentation:** https://eclipse-ee4j.github.io/jersey/
- **Repository:** https://github.com/sargue/mailgun
- **Issues:** https://github.com/sargue/mailgun/issues
- **Travis CI:** https://travis-ci.org/sargue/mailgun

## Recent Changes (v2.0.0)

- Migrated from `javax.*` to `jakarta.*` package naming
- Updated Jersey to 3.0.9
- Maintained Java 8 compatibility
- See CHANGELOG.md for full history

## Quick Start for AI Assistants

1. Read README.md for user-facing documentation
2. Explore Configuration.java and Mail.java for core concepts
3. Look at BasicTests.java for usage examples
4. Understand the fluent builder pattern used throughout
5. Remember this is a published library - be conservative with changes
6. Always run tests before committing
7. Maintain backward compatibility when possible

## Contact

- **Maintainer:** Sergi Baila (sergibaila@protonmail.com)
- **GitHub:** sargue

---

*Last Updated: 2025-11-14*
*Document Version: 1.0*
*Codebase Version: 2.0.0*
