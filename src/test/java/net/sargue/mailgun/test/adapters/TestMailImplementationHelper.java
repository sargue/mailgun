package net.sargue.mailgun.test.adapters;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.MailBuilder;
import net.sargue.mailgun.MailRequestCallback;
import net.sargue.mailgun.Response;
import net.sargue.mailgun.adapters.MailImplementationHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestMailImplementationHelper {
    @Test
    void sendAsyncFiltered() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.mailSendFilter()).thenReturn(x -> true);
        MailBuilder mailBuilder = mock(MailBuilder.class);
        when(mailBuilder.configuration()).thenReturn(configuration);

        MailImplementationHelper helper = new MailImplementationHelper(mailBuilder) {
            @Override
            protected Response sendFiltered() {
                return new Response(200, "ok");
            }
        };

        final AtomicBoolean callbackCalled = new AtomicBoolean(false);
        helper.sendAsync(new MailRequestCallback() {
            @Override
            public void completed(Response response) {
                callbackCalled.set(true);
            }

            @Override
            public void failed(Throwable throwable) {
                Assertions.fail("Should not fail.");
            }
        });

        await().until(callbackCalled::get);
    }
}
