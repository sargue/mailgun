package net.sargue.mailgun;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedMap;

import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED_TYPE;

class MailForm extends Mail {
    private Form form;

    MailForm(Configuration configuration, Form form) {
        super(configuration);
        this.form = form;
    }

    @Override
    public String getFirstValue(String param) {
        return form.asMap().getFirst(param);
    }

    @Override
    public List<String> getValues(String param) {
        return form.asMap().get(param);
    }

    @Override
    Entity<?> entity() {
        return Entity.entity(form, APPLICATION_FORM_URLENCODED_TYPE);
    }

    @Override
    void prepareSend() {
        // apply default parameters
        MultivaluedMap<String, String> parameters = form.asMap();
        Map<String, List<String>> def = configuration().defaultParameters();
        for (Map.Entry<String, List<String>> entry : def.entrySet())
            if (!parameters.containsKey(entry.getKey()))
                parameters.addAll(entry.getKey(), entry.getValue());
    }
}
