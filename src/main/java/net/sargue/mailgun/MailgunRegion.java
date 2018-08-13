package net.sargue.mailgun;

/**
 * A Mailgun region.
 *
 * @see <a href="https://documentation.mailgun.com/en/latest/api-intro.html#mailgun-regions">Mailgun documentation</a>
 */
public enum MailgunRegion {
    US("https://api.mailgun.net/v3"), EU("https://api.eu.mailgun.net/v3");

    private String apiUrl;

    MailgunRegion(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    /**
     * @return the default API URL for this region
     */
    public String apiUrl() {
        return apiUrl;
    }
}


