package net.sargue.mailgun.adapters.jersey2;

import net.sargue.mailgun.MailBuilder;
import net.sargue.mailgun.MailRequestCallback;
import net.sargue.mailgun.MailgunException;
import net.sargue.mailgun.Response;
import net.sargue.mailgun.adapters.MailImplementationHelper;
import net.sargue.mailgun.attachment.Attachment;
import net.sargue.mailgun.log.Log;
import net.sargue.mailgun.log.Logger;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED_TYPE;

public class Jersey2Mail extends MailImplementationHelper {
    private static final Log log = Logger.getLogger(Jersey2Mail.class);
    private Client client;

    Jersey2Mail(MailBuilder mailBuilder, Client client) {
        super(mailBuilder);
        this.client = client;
    }

    @Override
    protected Response sendFiltered() {
        return convertResponse(request().post(entity()));
    }

    @Override
    protected void sendAsyncFiltered(MailRequestCallback callback) {
        request().async()
                 .post(entity(),
                       new InvocationCallback<javax.ws.rs.core.Response>() {
                           @Override
                           public void completed(javax.ws.rs.core.Response response) {
                               if (callback != null)
                                   callback.completed(convertResponse(response));
                           }

                           @Override
                           public void failed(Throwable t) {
                               if (callback != null)
                                   callback.failed(t);
                               else
                                   log.warn("Jersey 2 request failed without callback", t);
                           }
                       });
    }

    private Response convertResponse(javax.ws.rs.core.Response response) {
        return new Response(response.getStatus(), response.readEntity(String.class));
    }

    private Invocation.Builder request() {
        return client
                .target(configuration().apiUrl())
                .path(configuration().domain())
                .path("messages")
                .request();
    }

    private boolean isMultiPart() {
        return !mailBuilder().attachments().isEmpty();
    }

    private Entity<?> entity() {
        return isMultiPart() ? multiPartEntity() : formEntity();
    }

    private Entity<?> formEntity() {
        Form form = new Form();
        for (String parameter : parameterKeySet())
            getValues(parameter).forEach(value -> form.param(parameter, value));

        return Entity.entity(form, APPLICATION_FORM_URLENCODED_TYPE);
    }

    private Entity<?> multiPartEntity() {
        try (FormDataMultiPart form = new FormDataMultiPart()) {
            for (String parameter : parameterKeySet())
                getValues(parameter).forEach(value -> form.field(parameter,
                                                                 value));

            for (Attachment attachment : mailBuilder().attachments())
                form.bodyPart(attachmentToBodyPart(attachment));

            return Entity.entity(form, form.getMediaType());
        } catch (IOException e) {
            throw new MailgunException("Exception closing multipart", e);
        }
    }

    private BodyPart attachmentToBodyPart(Attachment attachment) {
        MediaType mediaType = attachment.mediaType() == null ? null : MediaType.valueOf(attachment.mediaType());

        if (attachment.isFile())
            return new FileDataBodyPart(attachment.bodyPartName(),
                                        attachment.getContentAsFile(),
                                        mediaType);

        if (attachment.isInputStream())
            return new StreamDataBodyPart(attachment.bodyPartName(),
                                          attachment.getContentAsInputStream(),
                                          attachment.fileName(),
                                          mediaType);

        if (attachment.isString())
            return new StreamDataBodyPart(attachment.bodyPartName(),
                                          new ByteArrayInputStream(attachment.getContentAsString().getBytes()),
                                          attachment.fileName(),
                                          mediaType);

        throw new IllegalStateException("Shouldn't be here, new Attachment type?");
    }
}
