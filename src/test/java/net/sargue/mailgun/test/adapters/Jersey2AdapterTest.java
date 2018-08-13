package net.sargue.mailgun.test.adapters;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.RestClientAdapter;
import net.sargue.mailgun.adapters.jersey2.Jersey2Adapter;

public class Jersey2AdapterTest extends BasicTests {
    @Override
    protected RestClientAdapter restClientAdapter(Configuration configuration) throws ClassNotFoundException {
        return new Jersey2Adapter(configuration);
    }
}
