package net.sargue.mailgun;

/**
 * Represents a response from the Mailgun service.
 */
public class Response {
    public enum ResponseType {
        OK, BAD_REQUEST, UNAUTHORIZED, REQUEST_FAILED, NOT_FOUND, SERVER_ERROR
    }

    private ResponseType responseType;
    private int responseCode;

    Response(javax.ws.rs.core.Response response) {
        responseCode = response.getStatus();
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
            default:
                responseType = ResponseType.SERVER_ERROR;
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
}
