package net.sargue.mailgun.test;

import net.sargue.mailgun.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testing default methods.
 */
public class TestMail {
    @Test
    void sendAsyncOk() {
        Mail mail = new Mail() {
            @Override
            public Optional<String> getFirstValue(String param) {
                return Optional.empty();
            }

            @Override
            public List<String> getValues(String param) {
                return null;
            }

            @Override
            public Set<String> parameterKeySet() {
                return null;
            }

            @Override
            public Response send() {
                return new Response(200, "ok");
            }

            @Override
            public Configuration configuration() {
                Configuration configuration = mock(Configuration.class);
                when(configuration.mailSendFilter()).thenReturn(x -> true);
                return configuration;
            }
        };

        final AtomicBoolean callbackCalled = new AtomicBoolean(false);
        mail.sendAsync(new MailRequestCallback() {
            @Override
            public void completed(Response response) {
                callbackCalled.set(true);
            }

            @Override
            public void failed(Throwable throwable) {
                Assertions.fail("Should not fail");
            }
        });

        await().until(callbackCalled::get);
    }

    @Test
    void sendAsyncOkNullCallback() {
        final AtomicBoolean sent = new AtomicBoolean(false);
        Mail mail = new Mail() {
            @Override
            public Optional<String> getFirstValue(String param) {
                return Optional.empty();
            }

            @Override
            public List<String> getValues(String param) {
                return null;
            }

            @Override
            public Set<String> parameterKeySet() {
                return null;
            }

            @Override
            public Response send() {
                sent.set(true);
                return new Response(200, "ok");
            }

            @Override
            public Configuration configuration() {
                Configuration configuration = mock(Configuration.class);
                when(configuration.mailSendFilter()).thenReturn(x -> true);
                return configuration;
            }
        };

        mail.sendAsync(null);

        await().until(sent::get);
    }

    @Test
    void sendAsyncNok() {
        Mail mail = new Mail() {
            @Override
            public Optional<String> getFirstValue(String param) {
                return Optional.empty();
            }

            @Override
            public List<String> getValues(String param) {
                return null;
            }

            @Override
            public Set<String> parameterKeySet() {
                return null;
            }

            @Override
            public Response send() {
                throw new MailgunException("test");
            }

            @Override
            public Configuration configuration() {
                Configuration configuration = mock(Configuration.class);
                when(configuration.mailSendFilter()).thenReturn(x -> true);
                return configuration;
            }
        };

        final AtomicBoolean callbackCalled = new AtomicBoolean(false);
        mail.sendAsync(new MailRequestCallback() {
            @Override
            public void completed(Response response) {
                Assertions.fail("Should not complete");
            }

            @Override
            public void failed(Throwable throwable) {
                callbackCalled.set(true);
            }
        });

        await().until(callbackCalled::get);
    }

    @Test
    void sendAsyncNokNullCallback() {
        final AtomicBoolean sentCalled = new AtomicBoolean(false);
        Mail mail = new Mail() {
            @Override
            public Optional<String> getFirstValue(String param) {
                return Optional.empty();
            }

            @Override
            public List<String> getValues(String param) {
                return null;
            }

            @Override
            public Set<String> parameterKeySet() {
                return null;
            }

            @Override
            public Response send() {
                sentCalled.set(true);
                throw new MailgunException("test");
            }

            @Override
            public Configuration configuration() {
                Configuration configuration = mock(Configuration.class);
                when(configuration.mailSendFilter()).thenReturn(x -> true);
                return configuration;
            }
        };

        mail.sendAsync(null);

        await().until(sentCalled::get);
    }

    @Test
    void sendAsyncFiltered() {
        Mail mail = new Mail() {
            @Override
            public Optional<String> getFirstValue(String param) {
                return Optional.empty();
            }

            @Override
            public List<String> getValues(String param) {
                return null;
            }

            @Override
            public Set<String> parameterKeySet() {
                return null;
            }

            @Override
            public Response send() {
                Assertions.fail("Should not send.");
                return null;
            }

            @Override
            public Configuration configuration() {
                Configuration configuration = mock(Configuration.class);
                when(configuration.mailSendFilter()).thenReturn(x -> false);
                return configuration;
            }
        };

        mail.sendAsync(new MailRequestCallback() {
            @Override
            public void completed(Response response) {
                Assertions.fail("Should not complete");
            }

            @Override
            public void failed(Throwable throwable) {
                Assertions.fail("Should not fail");
            }
        });

        mail.sendAsync();
    }
}
