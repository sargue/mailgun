package net.sargue.mailgun;

/**
 * A callback container for asynchronous sending of emails.
 */
public interface MailRequestCallback {
    /**
     * This method is called upon reception of the response from the Mailgun
     * service.
     * <p>
     * Notice that it may be an error response, check the response status.
     *
     * @param response the response from the Mailgun service
     */
    void completed(Response response);

    /**
     * Called when there is a problem sending the request.
     * <p>
     * The most common example is a network error.
     *
     * @param throwable the exception thrown
     */
    void failed(Throwable throwable);
}
