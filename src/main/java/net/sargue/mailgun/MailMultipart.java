package net.sargue.mailgun;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import java.util.List;
import java.util.Map;

class MailMultipart extends Mail {
    private FormDataMultiPart form;

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
        // apply default parameters
        Map<String, List<String>> def = configuration().defaultParameters();
        for (Map.Entry<String, List<String>> entry : def.entrySet())
            if (form.getField(entry.getKey()) == null)
                for (String value : entry.getValue())
                    form.field(entry.getKey(), value);
    }

    @Override
    void configureClient(Client client) {
        client.register(MultiPartFeature.class);
    }
}
