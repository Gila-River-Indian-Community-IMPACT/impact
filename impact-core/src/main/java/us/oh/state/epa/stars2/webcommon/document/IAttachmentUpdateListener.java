package us.oh.state.epa.stars2.webcommon.document;
/**
 * Listener interface added to allow row-level checking for attachments
 * to determine if update or delete is permitted.
 *
 */
public interface IAttachmentUpdateListener {
    public boolean isAttachmentUpdatePermitted(Attachments attachment);
    public boolean isAttachmentDeletePermitted(Attachments attachment);
}
