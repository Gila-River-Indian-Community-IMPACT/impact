package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;

public class PermitDocument extends Document {

    private Integer _permitId;
    private String _permitDocTypeCD;
    private String _permitDocTypeDsc;
    private String _issuanceStageFlag;
    private boolean requiredDoc;
    private boolean fixed;
    private boolean nsr;
    private boolean titleV;
    private boolean permitDetail;
    private boolean draftPublication;
    private boolean ppPublication;
    private boolean finalIssuance;
    private boolean withdrawalIssuance;
    private boolean feeSummary;
    private boolean generateTemplate;
    private String templatePath;
    private String permitDocumentID;

   

	

	public String getPermitDocumentID() {
		return permitDocumentID;
	}

	public void setPermitDocumentID(String permitDocumentID) {
		this.permitDocumentID = permitDocumentID;
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	/**
     * 
     */
    public PermitDocument() {
        super();
        this.requiredField(_permitId, "permitId");
        this.requiredField(_permitDocTypeCD, "permitDocTypeCD");
        this.requiredField(requiredDoc, "is_required_doc");
        setDirty(false);
    }

    /**
     * 
     */
    public PermitDocument(Object old) {

        super((Document)old);
        if (old != null && old instanceof PermitDocument) {
            PermitDocument t = (PermitDocument)old;
            setPermitId(t.getPermitId());
            setPermitDocTypeCD(t.getPermitDocTypeCD());
            setIssuanceStageFlag(t.getIssuanceStageFlag());
            setLastModified(t.getLastModified());
            setDirty(t.isDirty());
            setRequiredDoc(t.requiredDoc);
        }
    }

    public void populate(ResultSet rs) {

        try {
            super.populate(rs);
            setPermitId(AbstractDAO.getInteger(rs, "permit_id"));
            setPermitDocTypeCD(rs.getString("permit_doc_type_cd"));
            setIssuanceStageFlag(rs.getString("issuance_stage_flag"));
            setLastModified(AbstractDAO.getInteger(rs, "ppd_lm"));
            setDirty(false);
            setRequiredDoc((rs.getBoolean("is_required_doc")));
            //setGenerateTemplate((rs.getBoolean("generate_template")));
            setTemplatePath((rs.getString("template_path")));           
            setPermitDocumentID((rs.getString("document_id"))); 
		} catch (SQLException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e.getMessage());
			}
		}
    }
    
    public boolean isRequiredDoc() {
		return requiredDoc;
	}

	public void setRequiredDoc(boolean requiredDoc) {
		this.requiredDoc = requiredDoc;
	}

	
    public final String getPermitDocTypeCD() {
        return _permitDocTypeCD;
    }

    public final void setPermitDocTypeCD(String permitDocTypeCD) {
        this._permitDocTypeCD = permitDocTypeCD;
        this.requiredField(_permitDocTypeCD, "permitDocTypeCD");
        setDirty(true);
    }

    public final String getIssuanceStageFlag() {
        return _issuanceStageFlag;
    }

    public final void setIssuanceStageFlag(String issuanceStageCD) {
        this._issuanceStageFlag = issuanceStageCD;
        setDirty(true);
    }

    public final Integer getPermitId() {
        return _permitId;
    }

    public final void setPermitId(Integer permitId) {
        this._permitId = permitId;
        this.requiredField(_permitId, "permitId");
        setDirty(true);
    }

    public final String getBasePath() {

        String ret = File.separator + "Permits" + File.separator;
        if (_permitId != null) {
            ret += _permitId + File.separator;
        }
        ret += getFileName();
        return ret;
    }

    @Override
        public int hashCode() {

        final int PRIME = 31;
        int result = super.hashCode();

        result = PRIME * result
            + ((_permitId == null) ? 0 : _permitId.hashCode());
        result = PRIME * result
            + ((_permitDocTypeCD == null) ? 0 : _permitDocTypeCD.hashCode());
        result = PRIME * result
            + ((_issuanceStageFlag == null) ? 0 : _issuanceStageFlag.hashCode());
                        
        return result;
    }

    @Override
        public final boolean equals(Object obj) {

        if ((obj == null) || !(super.equals(obj))
            || (getClass() != obj.getClass())) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        final PermitDocument other = (PermitDocument) obj;

        // Either both null or equal values.
        if (((_permitId == null) && (other.getPermitId() != null))
            || ((_permitId != null) && (other.getPermitId() == null))
            || ((_permitId != null) && (other.getPermitId() != null) 
                && !(_permitId.equals(other.getPermitId())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_permitDocTypeCD == null) && (other.getPermitDocTypeCD() != null))
            || ((_permitDocTypeCD != null) && (other.getPermitDocTypeCD() == null))
            || ((_permitDocTypeCD != null)
                && (other.getPermitDocTypeCD() != null) 
                && !(_permitDocTypeCD.equals(other.getPermitDocTypeCD())))) {
                        
            return false;
        }

        // Either both null or equal values.
        if (((_issuanceStageFlag == null) && (other.getIssuanceStageFlag() != null))
            || ((_issuanceStageFlag != null) 
                && (other.getIssuanceStageFlag() == null))
            || ((_issuanceStageFlag != null)
                && (other.getIssuanceStageFlag() != null) 
                && !(_issuanceStageFlag.equals(other.getIssuanceStageFlag())))) {
                        
            return false;
        }
        return true;
    }

	public boolean isNsr() {
		return nsr;
	}

	public void setNsr(boolean nsr) {
		this.nsr = nsr;
	}

	public boolean isTitleV() {
		return titleV;
	}

	public void setTitleV(boolean titleV) {
		this.titleV = titleV;
	}

	public boolean isPermitDetail() {
		return permitDetail;
	}

	public void setPermitDetail(boolean permitDetail) {
		this.permitDetail = permitDetail;
	}

	public boolean isDraftPublication() {
		return draftPublication;
	}

	public void setDraftPublication(boolean draftPublication) {
		this.draftPublication = draftPublication;
	}

	public boolean isPpPublication() {
		return ppPublication;
	}

	public void setPpPublication(boolean ppPublication) {
		this.ppPublication = ppPublication;
	}

	public boolean isFinalIssuance() {
		return finalIssuance;
	}

	public void setFinalIssuance(boolean finalIssuance) {
		this.finalIssuance = finalIssuance;
	}

	public boolean isWithdrawalIssuance() {
		return withdrawalIssuance;
	}

	public void setWithdrawalIssuance(boolean withdrawalIssuance) {
		this.withdrawalIssuance = withdrawalIssuance;
	}

	public boolean isGenerateTemplate() {
		return generateTemplate;
	}

	public void setGenerateTemplate(boolean generateTemplate) {
		this.generateTemplate = generateTemplate;
	}
	public String getPermitDocTypeDsc() {
		return _permitDocTypeDsc;
	}

	public void setPermitDocTypeDsc(String permitDocTypeDsc) {
		this._permitDocTypeDsc = permitDocTypeDsc;
	}

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
	
	public boolean isFeeSummary() {
		return feeSummary;
	}

	public void setFeeSummary(boolean feeSummary) {
		this.feeSummary = feeSummary;
	}
}
