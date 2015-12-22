package net.sargue.mailgun;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;

class MailMultipart extends Mail {
    private FormDataMultiPart form = new FormDataMultiPart();

    MailMultipart(Configuration configuration, FormDataMultiPart form) {
        super(configuration);
        this.form = form;
    }

    @Override
    Entity<?> entity() {
        return Entity.entity(form, form.getMediaType());
    }

    @Override
    void prepareSend() {
        // if no "from" specified revert to configuration default
        if (form.getField("from") == null)
            form.field("from", configuration().from());
    }

    @Override
    void configureClient(Client client) {
        client.register(MultiPartFeature.class);
    }
}
