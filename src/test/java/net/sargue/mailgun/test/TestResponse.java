package net.sargue.mailgun.test;

import net.sargue.mailgun.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestResponse {
    @Test
    void constructor() {
        Response response = new Response(200, "aMessage");

        assertEquals(Response.ResponseType.OK, response.responseType());
        assertEquals(200, response.responseCode());
        assertTrue(response.isOk());
        assertEquals("aMessage", response.responseMessage());
        assertEquals("Response[responseType=OK, responseCode=200, responseMessage='aMessage']",
                     response.toString());

        response = new Response(400, null);
        assertEquals(Response.ResponseType.BAD_REQUEST, response.responseType());
        assertEquals(400, response.responseCode());
        assertFalse(response.isOk());

        response = new Response(401, null);
        assertEquals(Response.ResponseType.UNAUTHORIZED, response.responseType());
        assertFalse(response.isOk());

        response = new Response(402, null);
        assertEquals(Response.ResponseType.REQUEST_FAILED, response.responseType());
        assertFalse(response.isOk());

        response = new Response(404, null);
        assertEquals(Response.ResponseType.NOT_FOUND, response.responseType());
        assertFalse(response.isOk());

        response = new Response(500, null);
        assertEquals(Response.ResponseType.SERVER_ERROR, response.responseType());
        assertFalse(response.isOk());

        response = new Response(999, null);
        assertEquals(Response.ResponseType.OTHER, response.responseType());
        assertFalse(response.isOk());

    }
}
