package net.sargue.mailgun.content;

public interface ContentConverter<T> {
    String toString(T value);
}
