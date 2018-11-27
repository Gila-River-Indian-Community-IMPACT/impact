package us.oh.state.epa.stars2.database.dbObjects.complianceReport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.webcommon.ValidationBase;

@SuppressWarnings("serial")
public class CompliancePerDetail extends BaseDB {

    private int EUId;
    private String OepaEuId;
    private String EuDesc;
    private Integer permitId;
    private transient Timestamp PTIOIssueDate;
    private long PTIOIssueDateLong;
    private String operate;
    private String physicalChange;
    private String deviations;
    private String deviationsFromMRR;
    private String description;
    private String airToxicsChange;
    private String comment;
    private int reportId;
    private String permitInfo;
    private List<String> permitNumbers;
    private List<SelectItem> permitInfoItems;
    private String epaEmuId;
    private String companyId;
    private String companyDesc;
    private transient Timestamp initialInstallComplete;
    private transient Timestamp modificationBegun;
    private transient Timestamp commencedOperation;
    
//    private Long initialInstallCompleteLong;
//    private Long modificationBegunLong;
//    private Long commencedOperationLong;
    
    private boolean initInstallDtFromFP;//#2121
    private boolean modificationDtFromFP;
    private boolean commenceDtFromFP;
    
    public ValidationMessage[] validate() {
        //make sure each parameter has been completed
        List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
        
        if (this.getDeviationsFromMRR() == null
				|| ((!this.getDeviationsFromMRR().equals("Yes")) && (!this.getDeviationsFromMRR().equals("No")))) {
        	 		
			messages.add(new ValidationMessage("deviation","Emissions Unit " +this.getEpaEmuId() 
					+ ": must select yes or no for deviations for each emissions unit", 
					ValidationMessage.Severity.WARNING, ValidationBase.COMPLIANCE_TAG));
		}        
        if (this.getDeviations() == null || ((!this.getDeviations().equals("Yes")) 
        		&& (!this.getDeviations().equals("No")))) {
			
			 messages.add(new ValidationMessage("deviation","Emissions Unit " + this.getEpaEmuId() 
					 + ": must select yes or no for deviations for each emissions unit",
					 ValidationMessage.Severity.WARNING, ValidationBase.COMPLIANCE_TAG));
		}
        
        /*
        if (this.getAirToxicsChange()== null || ((!this.getAirToxicsChange().equals("Yes")) && (!this.getAirToxicsChange().equals("No"))))  {
            messages.add(new ValidationMessage("Compliance Report","Emissions Unit " +this.getEpaEmuId() + ": A response to 'Change to Air Toxics' is required",ValidationMessage.Severity.WARNING));
        }
        */
        /*
        if (this.getPhysicalChange()== null || ((!this.getPhysicalChange().equals("Yes")) && (!this.getPhysicalChange().equals("No"))))  {
            messages.add(new ValidationMessage("Compliance Report","Emissions Unit " +this.getEpaEmuId() + ": A response to 'Physical Change' is required",ValidationMessage.Severity.WARNING));
        }
        if (this.getOperate()== null || ((!this.getOperate().equals("Yes")) && (!this.getOperate().equals("No"))))  {
            messages.add(new ValidationMessage("Compliance Report","Emissions Unit " +this.getEpaEmuId() + ": A response to 'Operating' is required",ValidationMessage.Severity.WARNING));
        }
        */        
    
        return messages.toArray(new ValidationMessage[0]);
    }
    
    public String getCompanyDesc() {
        return companyDesc;
    }

    public void setCompanyDesc(String companyDesc) {
        this.companyDesc = companyDesc;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void populate(ResultSet rs) throws SQLException {
       /*
            1. PER_DETAIL_COMMENT=?,
            2. OPERATING=?,
            3. PHYSICAL_CHANGE=?,
            4. DEVIATIONS=?,
            5. MRR_DEVIATIONS=?
            6. AIR_TOXICS_CHANGE,
            7  LAST_MODIFIED=? 
            8. WHERE REPORT_ID=? 
            9  CORR_EPA_EMU_ID=?
        */
        try {
            setReportId(AbstractDAO.getInteger(rs, "report_id"));
            setComment(rs.getString("per_detail_comment"));
            setEUId(AbstractDAO.getInteger(rs,"corr_epa_emu_id"));
            setAirToxicsChange(rs.getString("air_toxics_change"));
            setDeviations(rs.getString("deviations"));
            setOperate(rs.getString("operating"));
            setPhysicalChange(rs.getString("physical_change"));
            setDeviationsFromMRR(rs.getString("mrr_deviations"));
            setPermitId(AbstractDAO.getInteger(rs,"permit_id"));
            setInitialInstallComplete(rs.getTimestamp("initial_install_complete_dt"));
            setModificationBegun(rs.getTimestamp("modification_begun_dt"));
            setCommencedOperation(rs.getTimestamp("commenced_operation_dt"));
            setLastModified(AbstractDAO.getInteger(rs,"last_modified"));
            
            setDirty(false);
        } catch (SQLException sqle) {
            logger.error("Required field error");
        } finally {
            newObject = false;
        }
    }

    public String getAirToxicsChange() {
        return airToxicsChange;
    }

    public void setAirToxicsChange(String airToxicsChange) {
        this.airToxicsChange = airToxicsChange;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeviations() {
        return deviations;
    }

    public void setDeviations(String deviations) {
        this.deviations = deviations;
    }

    public String getDeviationsFromMRR() {
        return deviationsFromMRR;
    }

    public void setDeviationsFromMRR(String deviationsFromMRR) {
        this.deviationsFromMRR = deviationsFromMRR;
    }

    public int getEUId() {
        return EUId;
    }

    public void setEUId(int id) {
        EUId = id;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public String getPhysicalChange() {
        return physicalChange;
    }

    public void setPhysicalChange(String physicalChange) {
        this.physicalChange = physicalChange;
    }

    public Timestamp getPTIOIssueDate() {
        return PTIOIssueDate;
    }

    public void setPTIOIssueDate(Timestamp issueDate) {
        PTIOIssueDate = issueDate;
        if (PTIOIssueDate != null) {
            PTIOIssueDateLong = PTIOIssueDate.getTime();
        } else {
            PTIOIssueDateLong = 0;
        }
    }

    public String getEuDesc() {
        return EuDesc;
    }

    public void setEuDesc(String euDesc) {
        EuDesc = euDesc;
    }

    public String getOepaEuId() {
        return OepaEuId;
    }

    public void setOepaEuId(String oepaEuId) {
        OepaEuId = oepaEuId;
    }

    public Integer getPermitId() {
        return permitId;
    }

    public void setPermitId(Integer permitId) {
        this.permitId = permitId;
    }

    public String getEpaEmuId() {
        return epaEmuId;
    }

    public void setEpaEmuId(String epaEmuId) {
        this.epaEmuId = epaEmuId;
    }

    public String getPermitInfo() {
        return permitInfo;
    }

    public void setPermitInfo(String permitInfo) {
        this.permitInfo = permitInfo;
    	permitInfoItems = new ArrayList<SelectItem>();
    	permitNumbers = new ArrayList<String>();
    	if (permitInfo != null) {
    		String[] itemVals = permitInfo.split(";");
    		for (String itemVal : itemVals) {
    			String[] itemDetails = itemVal.split(":");
    			String permitNumber = itemDetails[0].trim();
    			permitNumbers.add(permitNumber);
    			permitInfoItems.add(new SelectItem(permitNumber, itemVal));
    		}
    	}
    }
    
    public List<SelectItem> getPermitInfoItems() {
    	if (permitInfoItems == null) {
    		permitInfoItems = new ArrayList<SelectItem>();
    	}
    	return permitInfoItems;
    }
    
    public List<String> getPermitNumbers() {
    	if (permitNumbers == null) {
    		permitNumbers = new ArrayList<String>();
    	}
    	return permitNumbers;
    }

    public long getPTIOIssueDateLong() {
        long ret = 0;
        if (PTIOIssueDate != null) {
            ret = PTIOIssueDate.getTime();
        }

        return ret;
    }

    public void setPTIOIssueDateLong(long issueDateLong) {
        PTIOIssueDate = null;
        if (issueDateLong > 0) {
            PTIOIssueDate = new Timestamp(issueDateLong);
        }
    }

    public Timestamp getInitialInstallComplete() {
        return initialInstallComplete;
    }

    public void setInitialInstallComplete(Timestamp initialInstallComplete) {
        this.initialInstallComplete = initialInstallComplete;
    }

    public Timestamp getModificationBegun() {
        return modificationBegun;
    }

    public void setModificationBegun(Timestamp modificationBegun) {
        this.modificationBegun = modificationBegun;
    }

    public Timestamp getCommencedOperation() {
        return commencedOperation;
    }

    public void setCommencedOperation(Timestamp commencedOperation) {
        this.commencedOperation = commencedOperation;
    }

    public boolean isCommenceDtFromFP() {
		return commenceDtFromFP;
	}

	public void setCommenceDtFromFP(boolean commenceDtFromFP) {
		this.commenceDtFromFP = commenceDtFromFP;
	}

	public boolean isInitInstallDtFromFP() {
		return initInstallDtFromFP;
	}

	public void setInitInstallDtFromFP(boolean initInstallDtFromFP) {
		this.initInstallDtFromFP = initInstallDtFromFP;
	}

	public boolean isModificationDtFromFP() {
		return modificationDtFromFP;
	}

	public void setModificationDtFromFP(boolean modificationDtFromFP) {
		this.modificationDtFromFP = modificationDtFromFP;
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // manually set transient date values since this does not appear to
        // work properly with persistence
        setPTIOIssueDateLong(this.PTIOIssueDateLong);
    }

	public Long getCommencedOperationLong() {
		if (commencedOperation == null) {
            return null;
        }
        return commencedOperation.getTime();
	}

	public void setCommencedOperationLong(Long commencedOperationLong) {
		if (commencedOperationLong != null) {
			commencedOperation = new Timestamp(commencedOperationLong);
        }
	}

	public Long getInitialInstallCompleteLong() {
		if (initialInstallComplete == null) {
            return null;
        }
        return initialInstallComplete.getTime();
	}

	public void setInitialInstallCompleteLong(Long initialInstallCompleteLong) {
		if (initialInstallCompleteLong != null) {
			initialInstallComplete = new Timestamp(initialInstallCompleteLong);
        }
	}

	public Long getModificationBegunLong() {
		if (modificationBegun == null) {
            return null;
        }
        return modificationBegun.getTime();
	}

	public void setModificationBegunLong(Long modificationBegunLong) {
		if (modificationBegunLong != null) {
			modificationBegun = new Timestamp(modificationBegunLong);
        }
	}
}
