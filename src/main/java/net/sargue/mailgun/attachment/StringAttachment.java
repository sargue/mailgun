package net.sargue.mailgun.attachment;

class StringAttachment extends Attachment {
    private String content;

    StringAttachment(Disposition disposition,
                     String fileName,
                     String mediaType,
                     String content)
    {
        super(disposition, fileName, mediaType);
        this.content = content;
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public String getContentAsString() {
        return content;
    }
}
