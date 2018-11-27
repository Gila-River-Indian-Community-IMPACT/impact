package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.def.PBRNotifDocTypeDef;

public class PBRNotificationDocument extends Attachment {

    private Integer applicationId;
    
    public PBRNotificationDocument() {
        super();
    }
    
    /**
     * Copy constructor.
     * @param old
     */
    public PBRNotificationDocument(PBRNotificationDocument old) {
        super(old);
        if (old != null) {
            setApplicationId(old.getApplicationId());
            if (applicationId != null) {
                setObjectId(applicationId.toString());
            }
        }
    }
    
    /**
     * Create a PBRNotificationDocument from an Attachment.
     * @param doc
     */
    public PBRNotificationDocument(Attachment doc) {
        super(doc);
        if (doc != null) {
            setApplicationId(Integer.decode(doc.getObjectId()));
        }
    }
    
    @Override
    public void populate(ResultSet rs) {
        super.populate(rs);
        try {
            setApplicationId(AbstractDAO.getInteger(rs, "application_id"));
        } catch (SQLException e) {
            logger.error("Required field error");
        }
    }

    public final Integer getApplicationId() {
        return applicationId;
    }
    public final void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
        if (applicationId != null) {
            setObjectId(applicationId.toString());
        } else {
            setObjectId(null);
        }
    }

    @Override
    public String getAttachmentType() {
        String attachmentType = null;
        if (getDocTypeCd() != null) {
            attachmentType = PBRNotifDocTypeDef.getData().getItems().getItemDesc(getDocTypeCd());
        }
        return attachmentType;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((applicationId == null) ? 0 : applicationId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PBRNotificationDocument other = (PBRNotificationDocument) obj;
        if (applicationId == null) {
            if (other.applicationId != null)
                return false;
        } else if (!applicationId.equals(other.applicationId))
            return false;
        return true;
    }
}
