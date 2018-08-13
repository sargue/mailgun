package net.sargue.mailgun;

import java.util.StringJoiner;

/**
 * Represents a response from the Mailgun service.
 * <p>
 * This class encapsulates the {@code javax.ws.rs.core.Response} and extracts
 * some data from it like response status code, mailgun error code and the
 * response message as a String-encoded JSON.
 * <p>
 * For example, the response payload (body) of sending a message is this (in
 * JSON format):
 * <pre>{@code
 *  {
 *     "id": "<20160902095021.16212.7900.87F2C8F1@mydomain.com>",
 *     "message": "Queued. Thank you."
 *  }
 * }</pre>
 * <p>
 * The reason I am not parsing that and offering a POJO representation of that
 * response is to avoid adding another dependency to the library. You can use
 * any JSON library you want.
 */
public class Response {
    public enum ResponseType {
        OK, BAD_REQUEST, UNAUTHORIZED, REQUEST_FAILED, NOT_FOUND, SERVER_ERROR, OTHER
    }

    private ResponseType responseType;
    private int responseCode;
    private String responseMessage;

    public Response(int responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        switch (responseCode) {
            case 200:
                responseType = ResponseType.OK;
                break;
            case 400:
                responseType = ResponseType.BAD_REQUEST;
                break;
            case 401:
                responseType = ResponseType.UNAUTHORIZED;
                break;
            case 402:
                responseType = ResponseType.REQUEST_FAILED;
                break;
            case 404:
                responseType = ResponseType.NOT_FOUND;
                break;
            case 500:
                responseType = ResponseType.SERVER_ERROR;
                break;
            default:
                responseType = ResponseType.OTHER;
        }
    }

    /**
     * The type of the response.
     *
     * @return the type of the response
     */
    public ResponseType responseType() {
        return responseType;
    }

    /**
     * The HTTP status code of the response.
     *
     * The different recognized codes are better encoded with the
     * {@link ResponseType}. This method accesses the exact status code.
     *
     * @return the HTTP status code of the response
     */
    public int responseCode() {
        return responseCode;
    }

    /**
     * Shortcut method for checking if the response is ok. Equivalent of
     * checking if the {@link ResponseType} is {@code ResponseType.OK}.
     *
     * @return if the request was a success
     */
    public boolean isOk() {
        return responseType == ResponseType.OK;
    }

    /**
     * The response message body in JSON format as returned by the Mailgun
     * service.
     *
     * For example, the response payload (body) of sending a message is this (in
     * JSON format):
     * <pre>{@code
     *  {
     *     "id": "<20160902095021.16212.7900.87F2C8F1@mydomain.com>",
     *     "message": "Queued. Thank you."
     *  }
     * }</pre>
     *
     * @return the response message in JSON format
     */
    public String responseMessage() {
        return responseMessage;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Response.class.getSimpleName() + "[", "]")
            .add("responseType=" + responseType)
            .add("responseCode=" + responseCode)
            .add("responseMessage='" + responseMessage + "'")
            .toString();
    }
}
