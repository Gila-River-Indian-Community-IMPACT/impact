package us.oh.state.epa.stars2.database.dbObjects.attachments;

import java.io.File;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.def.ComplianceReportAllSubAttachmentTypesDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

public class Attachment extends Document {

	private static final long serialVersionUID = 6047257029029598635L;

	private String attachmentType;
    private Date submitDate;
    private int reportId;
    // NOTE: changed name from basePath to attachmentBasePath
    // to avoid confusion with Document.basePath and to allow
    // this value to be marhshalled
    private String attachmentBasePath;
    private String subPath="";
    private String objectId="";
    // NOTE: got rid of basePathInitialized because using it
    // caused problems when an Attachment is marshalled via XML
    // test for attachmentBasePath==null seems to be a good replacement
//    private boolean basePathInitialized=false;
    private int refLastModified;
    private Integer tradeSecretDocId;
    private String tradeSecretJustification;
    private String tradeSecretDocURL;

    public boolean isHasAttachmentType() {
        if (attachmentType != null && attachmentType.length()>0) {
            return true;
        }
            
        return false;
    }
 
    public boolean isExtensionUnknown() {
        if (!getExtension().equals("xls") && !getExtension().equals("xlsx") && getExtension().equals("pdf") && getExtension().equals("doc") && getExtension().equals("jpg") && getExtension().equals("txt") && getExtension().equals("dwg") && getExtension().equals("docx")) {
            return true;
        }
            
        return false;
    }
    public Attachment() {
        super();
    }
    
    public Attachment(Attachment old) {
        super(old);
        if (old != null) {
            setAttachmentType(old.getAttachmentType());
            setSubmitDate(old.getSubmitDate());
            setReportId(old.getReportId());
            setSubPath(old.getSubPath());
            setObjectId(old.getObjectId());
            setRefLastModified(old.getRefLastModified());
            setAttachmentBasePath(old.getAttachmentBasePath());
            setTradeSecretDocId(old.getTradeSecretDocId());
            setTradeSecretDocURL(old.getTradeSecretDocURL());
            setTradeSecretJustification(old.getTradeSecretJustification());
        }
    }
    
    public Attachment(Document doc) {
        super(doc);
    }
    
    public String getObjectId() {
        return objectId;
    }

    public boolean isEditable() {
        boolean ret = false;
        if (InfrastructureDefs.getCurrentUserId().equals(getLastModifiedBy())) {
            ret = true;
        }
            
        return ret;
    }
    
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }


    public Date getSubmitDate() {
        return submitDate;
    }

    public String getBasePath() {
    	
        if (attachmentBasePath == null && getSubPath() != null && getFileName() != null) {
            attachmentBasePath="";
            if (getSubPath().length()>0) {
                attachmentBasePath+=File.separator+getSubPath();
            }
            // objectId is not required (e.g. for facility attachments)
            if (getObjectId() != null && getObjectId().length()>0) {
                attachmentBasePath += File.separator+getObjectId();
            }
            attachmentBasePath += File.separator + getFileName();
//            basePathInitialized=true;
        }
        return attachmentBasePath;
    }
    
    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public void populate(ResultSet rs) {

        super.populate(rs);

        try {
            attachmentBasePath=rs.getString("path");
//            basePathInitialized = true;
            setRefLastModified(AbstractDAO.getInteger(rs, "ref_lm"));
            setDirty(false);
        }
        catch (SQLException sqle) {
            logger.error("Required field error");
        } 
        try {
            setAttachmentType(rs.getString("attachment_type_cd")); 
        } catch (SQLException sqle2) {
            logger.warn("Optional field 'attachment_type_dsc' not found");
        }
    }

    public String getDirName() {
        String baseDir = File.separator;

        if (getFacilityID() != null && getFacilityID().length() > 0) {
            baseDir = File.separator+"Facilities"+File.separator + getFacilityID();
        } else {
            logger.error("NULL FacilityID");
        }

        return baseDir;
    }
    
    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getSubPath() {
        return subPath;
    }

    public void setSubPath(String subPath) {
        this.subPath = subPath;
    }

    public int getRefLastModified() {
        return refLastModified;
    }

    public void setRefLastModified(int refLastModified) {
        this.refLastModified = refLastModified;
    }

    public final String getAttachmentBasePath() {
        return attachmentBasePath;
    }

    /**
     * Set the attachmentBasePath variable. Note: this is here just
     * to support XML marshalling and should not be used anywhere else.
     * @param attachmentBasePath
     */
    public final void setAttachmentBasePath(String attachmentBasePath) {
        this.attachmentBasePath = attachmentBasePath;
    }

    public final Integer getTradeSecretDocId() {
        return tradeSecretDocId;
    }

    public final void setTradeSecretDocId(Integer tradeSecretDocId) {
        this.tradeSecretDocId = tradeSecretDocId;
    }

    public final String getTradeSecretJustification() {
        return tradeSecretJustification;
    }

    public final void setTradeSecretJustification(String tradeSecretJustification) {
        this.tradeSecretJustification = tradeSecretJustification;
    }

    public final String getTradeSecretDocURL() {
        return tradeSecretDocURL;
    }
    
    public final void setTradeSecretDocURL(String url) {
        tradeSecretDocURL = url;
    }
    
    // Use this function to check tradeSecretAllowed.
    public boolean isTradeSecretAllowed() {
        return false;
    }
}
