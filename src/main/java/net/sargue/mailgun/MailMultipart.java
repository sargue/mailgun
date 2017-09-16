package net.sargue.mailgun;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import java.util.List;
import java.util.Map;

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
        // apply default parameters
        Map<String, List<String>> defaultParameters = configuration().defaultParameters();
        for (String name : defaultParameters.keySet()) {
            if (form.getField(name) == null) {
                for (String value : defaultParameters.get(name)) {
                    form.field(name, value);
                }
            }
        }
    }

    @Override
    void configureClient(Client client) {
        client.register(MultiPartFeature.class);
    }
}
