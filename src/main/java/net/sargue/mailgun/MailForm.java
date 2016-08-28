package net.sargue.mailgun;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;

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
        // if no "from" specified revert to configuration default
        if (!form.asMap().containsKey("from") &&
                configuration().from() != null)
            form.param("from", configuration().from());
    }
}
