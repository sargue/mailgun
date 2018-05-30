package net.sargue.mailgun;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class MailMultipart extends Mail {
    private FormDataMultiPart form;

    MailMultipart(Configuration configuration, FormDataMultiPart form) {
        super(configuration);
        this.form = form;
    }

    @Override
    public String getFirstValue(String param) {
        FormDataBodyPart bodyPart = form.getField(param);
        return bodyPart == null ? null : bodyPart.getValue();
    }

    @Override
    public List<String> getValues(String param) {
        List<FormDataBodyPart> bodyParts = form.getFields(param);
        if (bodyParts == null) return Collections.emptyList();

        List<String> values = new ArrayList<>(bodyParts.size());
        for (FormDataBodyPart bodyPart : bodyParts)
            values.add(bodyPart.getValue());
        return values;
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
