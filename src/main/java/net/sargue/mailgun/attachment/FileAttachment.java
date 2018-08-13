package net.sargue.mailgun.attachment;

import java.io.File;

class FileAttachment extends Attachment {
    private File file;

    FileAttachment(Disposition disposition,
                   String fileName,
                   String mediaType,
                   File file)
    {
        super(disposition, fileName, mediaType);
        this.file = file;
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public File getContentAsFile() {
        return file;
    }
}
