package us.oh.state.epa.stars2.webcommon.document;

public interface IAttachmentListener {

    public AttachmentEvent createAttachment(Attachments attachment) throws AttachmentException;
    public AttachmentEvent updateAttachment(Attachments attachment);
    public AttachmentEvent deleteAttachment(Attachments attachment);
    public void cancelAttachment();
}
