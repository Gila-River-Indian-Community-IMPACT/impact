package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * This class is the DB Object class for the PA_APPLICATION_DOCUMENT table.
 * It is named "ApplicationDocumentRef" instead of ApplicationDocument because
 * the ApplicationDocument class is needed as a subclass of Document in order
 * to provide a means for specifying the correct directory path for application
 * documents.
 *
 */
public class ApplicationDocumentRef extends BaseDB {
	private static final long serialVersionUID = 873193431982621577L;
	
	private Integer applicationDocId;
    private ApplicationDocument publicDoc;
    private Integer applicationId;
    private Integer applicationEUId;
    private String applicationDocumentTypeCD;
    private String eacFormTypeCD;
    private Integer documentId;
    private Integer tradeSecretDocId;
    private ApplicationDocument tradeSecretDoc;
    private String tradeSecretReason;
    private String description;
	private boolean requiredDoc;

	public ApplicationDocumentRef() {
        super();
    }

    /**
     * Copy Constructor
     * 
     * @param applicationDocument
     *            a <code>ApplicationDocument</code> object
     */
    public ApplicationDocumentRef(ApplicationDocumentRef ref) {
        super(ref);

        if (ref != null) {
            this.applicationDocId = ref.applicationDocId;
            this.applicationId = ref.applicationId;
            this.applicationEUId = ref.applicationEUId;
            this.applicationDocumentTypeCD = ref.applicationDocumentTypeCD;
            this.eacFormTypeCD = ref.eacFormTypeCD;
            this.documentId = ref.documentId;
            this.description = ref.description;
            this.tradeSecretDocId = ref.tradeSecretDocId;
            this.tradeSecretReason = ref.tradeSecretReason;
            this.publicDoc = ref.publicDoc;
            this.tradeSecretDoc = ref.tradeSecretDoc;
            this.requiredDoc = ref.requiredDoc;
        }
    }

    public final String getApplicationDocumentTypeCD() {
        return applicationDocumentTypeCD;
    }

    public final void setApplicationDocumentTypeCD(
            String applicationDocumentTypeCD) {
        this.applicationDocumentTypeCD = applicationDocumentTypeCD;
    }

    public final boolean getTradeSecret() {
        return tradeSecretDocId != null;
    }

    public final boolean isTradeSecretOnly() {
        return tradeSecretDocId != null && documentId == null;
    }
    
    public final String getEacFormTypeCD() {
        return eacFormTypeCD;
    }

    public final void setEacFormTypeCD(String eacFormTypeCD) {
        this.eacFormTypeCD = eacFormTypeCD;
    }

    public final Integer getApplicationId() {
        return applicationId;
    }

    public final void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public final Integer getApplicationEUId() {
        return applicationEUId;
    }

    public final void setApplicationEUId(Integer applicationEUId) {
        this.applicationEUId = applicationEUId;
    }

    public boolean isRequiredDoc() {
		return requiredDoc;
	}

	public void setRequiredDoc(boolean requiredDoc) {
		this.requiredDoc = requiredDoc;
	}

    public final void populate(java.sql.ResultSet rs) {

        try {
            setApplicationDocId(AbstractDAO.getInteger(rs, "application_doc_id"));
            setApplicationDocumentTypeCD(rs.getString("application_doc_type_cd"));
            setEacFormTypeCD(rs.getString("eac_form_type_cd"));
            setApplicationId(AbstractDAO.getInteger(rs, "application_id"));
            setApplicationEUId(AbstractDAO.getInteger(rs, "application_eu_id"));
            setDocumentId(AbstractDAO.getInteger(rs, "document_id"));
            setDescription(rs.getString("description"));
            setTradeSecretDocId(AbstractDAO.getInteger(rs, "trade_secret_doc_id"));
            setTradeSecretReason(rs.getString("trade_secret_reason"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
            setRequiredDoc(rs.getBoolean("is_required_doc"));
            
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((applicationDocId == null) ? 0 : applicationDocId.hashCode());
        result = PRIME * result + ((applicationDocumentTypeCD == null) ? 0 : applicationDocumentTypeCD.hashCode());
        result = PRIME * result + ((applicationEUId == null) ? 0 : applicationEUId.hashCode());
        result = PRIME * result + ((applicationId == null) ? 0 : applicationId.hashCode());
        result = PRIME * result + ((documentId == null) ? 0 : documentId.hashCode());
        result = PRIME * result + ((eacFormTypeCD == null) ? 0 : eacFormTypeCD.hashCode());
        result = PRIME * result + ((tradeSecretDocId == null) ? 0 : tradeSecretDocId.hashCode());
        result = PRIME * result + ((tradeSecretReason == null) ? 0 : tradeSecretReason.hashCode());
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
        final ApplicationDocumentRef other = (ApplicationDocumentRef) obj;
        if (applicationDocId == null) {
            if (other.applicationDocId != null)
                return false;
        } else if (!applicationDocId.equals(other.applicationDocId))
            return false;
        if (applicationDocumentTypeCD == null) {
            if (other.applicationDocumentTypeCD != null)
                return false;
        } else if (!applicationDocumentTypeCD.equals(other.applicationDocumentTypeCD))
            return false;
        if (applicationEUId == null) {
            if (other.applicationEUId != null)
                return false;
        } else if (!applicationEUId.equals(other.applicationEUId))
            return false;
        if (applicationId == null) {
            if (other.applicationId != null)
                return false;
        } else if (!applicationId.equals(other.applicationId))
            return false;
        if (documentId == null) {
            if (other.documentId != null)
                return false;
        } else if (!documentId.equals(other.documentId))
            return false;
        if (eacFormTypeCD == null) {
            if (other.eacFormTypeCD != null)
                return false;
        } else if (!eacFormTypeCD.equals(other.eacFormTypeCD))
            return false;
        if (tradeSecretDocId == null) {
            if (other.tradeSecretDocId != null)
                return false;
        } else if (!tradeSecretDocId.equals(other.tradeSecretDocId))
            return false;
        if (tradeSecretReason == null) {
            if (other.tradeSecretReason != null)
                return false;
        } else if (!tradeSecretReason.equals(other.tradeSecretReason))
            return false;
        return true;
    }

    public final String getTradeSecretReason() {
        return tradeSecretReason;
    }

    public final void setTradeSecretReason(String tradeSecretReason) {
        this.tradeSecretReason = tradeSecretReason;
    }

    public final Integer getTradeSecretDocId() {
        return tradeSecretDocId;
    }

    public final void setTradeSecretDocId(Integer tradeSecretDocId) {
        this.tradeSecretDocId = tradeSecretDocId;
    }

    public final Integer getApplicationDocId() {
        return applicationDocId;
    }

    public final void setApplicationDocId(Integer applicationDocId) {
        this.applicationDocId = applicationDocId;
    }

    public final Integer getDocumentId() {
        return documentId;
    }

    public final void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final ApplicationDocument getPublicDoc() {
        return publicDoc;
    }

    public final void setPublicDoc(ApplicationDocument applicationDoc) {
        this.publicDoc = applicationDoc;
    }

    public final ApplicationDocument getTradeSecretDoc() {
        return tradeSecretDoc;
    }

    public final void setTradeSecretDoc(ApplicationDocument tradeSecretDoc) {
        this.tradeSecretDoc = tradeSecretDoc;
    }
    
    public final Integer getLastModifiedBy() {
        Integer lastModifiedBy = null;
        if (publicDoc != null) {
            lastModifiedBy = publicDoc.getLastModifiedBy();
        } else if (tradeSecretDoc != null) {
            lastModifiedBy = tradeSecretDoc.getLastModifiedBy();
        }
        return lastModifiedBy;
    }
    
    public final Timestamp getLastModifiedTS() {
        Timestamp lastModifiedTS = null;
        if (publicDoc != null) {
            lastModifiedTS = publicDoc.getLastModifiedTS();
        } else if (tradeSecretDoc != null) {
            lastModifiedTS = tradeSecretDoc.getLastModifiedTS();
        }
        return lastModifiedTS;
    }
	
    // Use this function to check tradeSecretAllowed.
    public boolean isTradeSecretAllowed() {
        return false;
    }
    
    public final Timestamp getUploadDate() {
        Timestamp uploadDate = null;
        if (publicDoc != null) {
        	uploadDate = publicDoc.getUploadDate();
        } else if (tradeSecretDoc != null) {
        	uploadDate = tradeSecretDoc.getUploadDate();
        }
        return uploadDate;
    }
}
