package net.sargue.mailgun.content;

/**
 * A converter to get a String from an instance of T.
 * <p>
 * Used mainly in the {@link Builder#text(Object)} method.
 *
 * @param <T> the type of the object this converter works on
 */
@FunctionalInterface
public interface ContentConverter<T> {
    /**
     * @param value the value
     * @return the String representation to put in the content of the mail
     */
    String toString(T value);
}
