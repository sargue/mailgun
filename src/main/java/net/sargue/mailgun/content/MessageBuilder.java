package net.sargue.mailgun.content;

class MessageBuilder {
    private static final String CRLF = "\r\n";

    private StringBuilder sb = new StringBuilder();

    MessageBuilder a(String str) {
        sb.append(str == null ? "" : str);
        return this;
    }

    MessageBuilder a(char c) {
        sb.append(c);
        return this;
    }

    MessageBuilder sp() {
        return a(' ');
    }

    MessageBuilder nl() {
        sb.append(CRLF);
        return this;
    }

    MessageBuilder nl(int count) {
        for (int i = 0; i < count; i++)
            sb.append(CRLF);
        return this;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
