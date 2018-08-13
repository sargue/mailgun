package net.sargue.mailgun.attachment;

import java.io.InputStream;

class InputStreamAttachment extends Attachment {
    private InputStream inputStream;

    InputStreamAttachment(Disposition disposition,
                          String fileName,
                          String mediaType,
                          InputStream inputStream)
    {
        super(disposition, fileName, mediaType);
        this.inputStream = inputStream;
    }

    @Override
    public boolean isInputStream() {
        return true;
    }

    @Override
    public InputStream getContentAsInputStream() {
        return inputStream;
    }
}
