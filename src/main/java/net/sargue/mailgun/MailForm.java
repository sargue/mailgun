package net.sargue.mailgun;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedMap;

import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED_TYPE;

class MailForm extends Mail {
    private Form form = new Form();

    MailForm(Configuration configuration, Form form) {
        super(configuration);
        this.form = form;
    }

    @Override
    Entity<?> entity() {
        return Entity.entity(form, APPLICATION_FORM_URLENCODED_TYPE);
    }

    @Override
    void prepareSend() {
        // apply default parameters
        MultivaluedMap<String, String> parameters = form.asMap();
        Map<String, List<String>> defaultParameters = configuration().defaultParameters();
        for (String name : defaultParameters.keySet()) {
            if (!parameters.containsKey(name)) {
                parameters.addAll(name, defaultParameters.get(name));
            }
        }
    }
}
