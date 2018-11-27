package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EmissionsAttachmentTypeDef;

/**
 * This class is the DB Object class for the PA_APPLICATION_DOCUMENT table.
 * It is named "ApplicationDocumentRef" instead of ApplicationDocument because
 * the ApplicationDocument class is needed as a subclass of Document in order
 * to provide a means for specifying the correct directory path for application
 * documents.
 *
 */
public class EmissionsDocumentRef extends BaseDB {
    private static final long serialVersionUID = -8452083775629742145L;
    private Integer emissionsDocId;
    private EmissionsDocument publicDoc;
    private Integer emissionsRptId;
    private String emissionsDocumentTypeCD;
    private Integer documentId;
    private Integer tradeSecretDocId;
    private EmissionsDocument tradeSecretDoc;
    private String tradeSecretReason;
    private String description;

    public EmissionsDocumentRef() {
        super();
    }

    /**
     * Copy Constructor
     * 
     * @param applicationDocument
     *            a <code>ApplicationDocument</code> object
     */
    public EmissionsDocumentRef(EmissionsDocumentRef ref) {
        super(ref);

        if (ref != null) {
            this.emissionsDocId = ref.emissionsDocId;
            this.publicDoc = ref.publicDoc;
            this.emissionsRptId = ref.emissionsRptId;
            this.emissionsDocumentTypeCD = ref.emissionsDocumentTypeCD;
            this.documentId = ref.documentId;
            this.description = ref.description;
            this.tradeSecretDocId = ref.tradeSecretDocId;
            this.tradeSecretDoc =  ref.tradeSecretDoc;
            this.tradeSecretReason = ref.tradeSecretReason;
        }
    }

    public final boolean getTradeSecret() {
        return tradeSecretDocId != null;
    }

    public final void populate(java.sql.ResultSet rs) {

        try {
            setEmissionsDocId(AbstractDAO.getInteger(rs, "emissions_doc_id"));
            setEmissionsDocumentTypeCD(rs.getString("attachment_type_cd"));
            setEmissionsRptId(AbstractDAO.getInteger(rs, "emissions_rpt_id"));
            setDocumentId(AbstractDAO.getInteger(rs, "document_id"));
            setDescription(rs.getString("description"));
            setTradeSecretDocId(AbstractDAO.getInteger(rs, "trade_secret_doc_id"));
            setTradeSecretReason(rs.getString("trade_secret_reason"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }

    public final String getTradeSecretReason() {
        return tradeSecretReason;
    }

    public final void setTradeSecretReason(String tradeSecretReason) {
        if(tradeSecretReason != null) {
            this.tradeSecretReason = tradeSecretReason.trim();
        } else {
            this.tradeSecretReason = tradeSecretReason;
        }
    }

    public final Integer getTradeSecretDocId() {
        return tradeSecretDocId;
    }

    public final void setTradeSecretDocId(Integer tradeSecretDocId) {
        this.tradeSecretDocId = tradeSecretDocId;
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
        if(description != null) {
            this.description = description.trim();
        } else {
            this.description = description;
        }
    }

    public EmissionsDocument getPublicDoc() {
        return publicDoc;
    }

    public void setPublicDoc(EmissionsDocument publicDoc) {
        this.publicDoc = publicDoc;
    }

    public EmissionsDocument getTradeSecretDoc() {
        return tradeSecretDoc;
    }

    public void setTradeSecretDoc(EmissionsDocument tradeSecretDoc) {
        this.tradeSecretDoc = tradeSecretDoc;
    }

    public String getEmissionsDocumentTypeCD() {
        return emissionsDocumentTypeCD;
    }

    public void setEmissionsDocumentTypeCD(String emissionsDocumentTypeCD) {
        this.emissionsDocumentTypeCD = emissionsDocumentTypeCD;
    }

    public Integer getEmissionsRptId() {
        return emissionsRptId;
    }

    public void setEmissionsRptId(Integer emissionsRptId) {
        this.emissionsRptId = emissionsRptId;
    }

    public Integer getEmissionsDocId() {
        return emissionsDocId;
    }

    public void setEmissionsDocId(Integer emissionsDocId) {
        this.emissionsDocId = emissionsDocId;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((documentId == null) ? 0 : documentId.hashCode());
        result = PRIME * result + ((emissionsDocId == null) ? 0 : emissionsDocId.hashCode());
        result = PRIME * result + ((emissionsDocumentTypeCD == null) ? 0 : emissionsDocumentTypeCD.hashCode());
        result = PRIME * result + ((emissionsRptId == null) ? 0 : emissionsRptId.hashCode());
        result = PRIME * result + ((tradeSecretDocId == null) ? 0 : tradeSecretDocId.hashCode());
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
        final EmissionsDocumentRef other = (EmissionsDocumentRef) obj;
        if (documentId == null) {
            if (other.documentId != null)
                return false;
        } else if (!documentId.equals(other.documentId))
            return false;
        if (emissionsDocId == null) {
            if (other.emissionsDocId != null)
                return false;
        } else if (!emissionsDocId.equals(other.emissionsDocId))
            return false;
        if (emissionsDocumentTypeCD == null) {
            if (other.emissionsDocumentTypeCD != null)
                return false;
        } else if (!emissionsDocumentTypeCD.equals(other.emissionsDocumentTypeCD))
            return false;
        if (emissionsRptId == null) {
            if (other.emissionsRptId != null)
                return false;
        } else if (!emissionsRptId.equals(other.emissionsRptId))
            return false;
        if (tradeSecretDocId == null) {
            if (other.tradeSecretDocId != null)
                return false;
        } else if (!tradeSecretDocId.equals(other.tradeSecretDocId))
            return false;
        return true;
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

    public final boolean isTradeSecretOnly() {
        return tradeSecretDocId != null && documentId == null;
    }
    
    // Use this function to check tradeSecretAllowed.
    public boolean isTradeSecretAllowed() {
    	boolean ret = false;

    	DefSelectItems tempAttachmentTypesDef = EmissionsAttachmentTypeDef.getData().getItems();
    	if (tempAttachmentTypesDef != null) {
    		BaseDef attachmentDef = tempAttachmentTypesDef.getItem(getEmissionsDocumentTypeCD());
    		if ((attachmentDef != null) && (attachmentDef instanceof EmissionsAttachmentTypeDef)) {
   				EmissionsAttachmentTypeDef attachmentTypeDef = (EmissionsAttachmentTypeDef)attachmentDef;
   				ret = attachmentTypeDef.isTradeSecretAllowed();
    		}
    	}
    	
    	return ret;
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
