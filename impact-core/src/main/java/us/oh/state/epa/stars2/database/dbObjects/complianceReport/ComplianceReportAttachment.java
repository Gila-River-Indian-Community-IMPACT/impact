package us.oh.state.epa.stars2.database.dbObjects.complianceReport;

import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.def.ComplianceReportAllSubAttachmentTypesDef;
import us.oh.state.epa.stars2.def.DefSelectItems;

@SuppressWarnings("serial")
public class ComplianceReportAttachment extends Attachment {
	
	public ComplianceReportAttachment() {
		super();
	}

	public ComplianceReportAttachment(ComplianceReportAttachment old) {
		super(old);
	}
	
	public ComplianceReportAttachment(Attachment doc) {
		super(doc);
	}
    
	private Integer originalDocId;
	
	private Integer originalTradeSecretDocId;
	
	
	
    public Integer getOriginalDocId() {
		return originalDocId;
	}

	public void setOriginalDocId(Integer originalDocId) {
		this.originalDocId = originalDocId;
	}

	public Integer getOriginalTradeSecretDocId() {
		return originalTradeSecretDocId;
	}

	public void setOriginalTradeSecretDocId(Integer originalTradeSecretDocId) {
		this.originalTradeSecretDocId = originalTradeSecretDocId;
	}

	// Use this function to check tradeSecretAllowed.
	@Override
    public boolean isTradeSecretAllowed() {
		boolean ret = false;

    	if (getTradeSecretDocId() != null) {
    		DefSelectItems tempAttachmentTypesDef = ComplianceReportAllSubAttachmentTypesDef.getData().getItems();
    		if (tempAttachmentTypesDef != null) {
    			BaseDef attachmentDef = tempAttachmentTypesDef.getItem(getDocTypeCd());
    			if (attachmentDef != null) {
    				ComplianceReportAllSubAttachmentTypesDef attachmentTypeDef = (ComplianceReportAllSubAttachmentTypesDef)attachmentDef;
    				ret = attachmentTypeDef.isTradeSecretAllowed();
    			}
    		}
    	}

		return ret;
    }
}
