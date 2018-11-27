package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

@SuppressWarnings("serial")
public class SiteVisit extends VisitBase {
    private Timestamp afsDate;
    private String afsId;
    private List<SvAttachment> attachments;
    private Note[] siteVisitNotes;
    private List<String> inspectionsReferencedIn;
    
    public SiteVisit() {
        super();
    }
    
    public SiteVisit(Integer id, Integer fceId, Integer fpId, List<Evaluator> evaluators, Timestamp visitDate, 
            String announced, String visitType, String memo) {
        super(id, fceId, fpId, evaluators, visitDate, 
                announced, visitType, memo);
    }
    
//    public SiteVisit(SiteVisit old) {
//        super(old);
//        afsDate = old.afsDate;
//        afsId = old.afsId;
//    }

    public final ValidationMessage[] validate() {
        validationMessages.clear();
        requiredField(visitType, "visitType", "Visit Type");
        requiredField(visitDate, "visitDate", "Visit Date");
        if (!isStackTest())
        	requiredField(complianceIssued, "complianceIssued", "Compliance Issue");
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Timestamp today = new Timestamp(cal.getTimeInMillis());
        
        if(visitDate != null) {
            if(today.before(visitDate)) {
                validationMessages.put("visitDate", new ValidationMessage("visitDate",
                        "Visit Date cannot be in the future",
                        ValidationMessage.Severity.ERROR, "visitDate"));
            }
        }

        boolean containsNull = false;
        boolean containsDup = false;
        HashSet<Integer> users = new HashSet<Integer>();
        for(Evaluator e : evaluators) {
            if(e.getEvaluator() == null) {
                containsNull = true;
            }
            if(!users.add(e.getEvaluator())) {
                containsDup = true;
            }
        }
        if(containsNull) {
            validationMessages.put("svTable", new ValidationMessage("svTable",
                    "Evaluator(s) include empty entries.",
                    ValidationMessage.Severity.ERROR, null));
        }
        if(containsDup) {
            validationMessages.put("svTable2", new ValidationMessage("svTable",
                "Evaluator(s) include duplicate names.",
                ValidationMessage.Severity.ERROR, null));
        }
        return new ArrayList<ValidationMessage>(validationMessages.values())
        .toArray(new ValidationMessage[0]);
    }

    /** Populate this instance from a database ResultSet. */
    public final void populate(java.sql.ResultSet rs)throws SQLException {

        try{
            setId(AbstractDAO.getInteger(rs, "visit_id"));
            setFceId(AbstractDAO.getInteger(rs, "fce_id"));
            setInspId(rs.getString("insp_id"));
            setFpId(AbstractDAO.getInteger(rs, "fp_id"));
            setVersionId(AbstractDAO.getInteger(rs, "version_id"));
            setFacilityHistId(AbstractDAO.getInteger(rs, "facility_hist_id"));
            setVisitDate(rs.getTimestamp("visit_dt"));
            setVisitType(rs.getString("site_visit_type_cd"));
            setAnnounced(rs.getString("announced"));
            setMemo(rs.getString("memo"));
            setAfsId(rs.getString("afs_id"));
            setAfsDate(rs.getTimestamp("afs_dt"));
            setFacilityId(rs.getString("facility_id"));
            setScscId(rs.getString("scsc_id"));
            setFacilityNm(rs.getString("facility_nm"));
            setPermitClassCd(rs.getString("permit_classification_cd"));
            setFacilityTypeCd(rs.getString("facility_type_cd"));
            setLastModified(AbstractDAO.getInteger(rs,"last_modified"));
            setOperatingStatusCd(rs.getString("operating_status_cd"));
            setLastShutdownDate(rs.getTimestamp("last_shutdown_date"));
            setSiteVisitVeCd(rs.getString("site_visit_ve_cd"));
            setCmpId(rs.getString("cmp_id"));
            setCompanyName(rs.getString("company_nm"));
            setSiteId(rs.getString("site_id"));
            setComplianceIssued(rs.getString("compliance_issued"));
            setCreatedDt(rs.getTimestamp("created_dt"));
        } catch(SQLException e) {
        	logger.error(e.getMessage(), e);
            throw e;
        }
    }

    public String getAfsInfo() {
        String dateStr = "";
        if(afsDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(afsDate.getTime());
            dateStr = (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.DAY_OF_MONTH) +
            "/" + cal.get(Calendar.YEAR);
        }
        String afsIdValue = "";
        if(afsId != null) afsIdValue = afsId;
        String rtn = afsIdValue + " " + dateStr;
        return rtn;
    }

    public Timestamp getAfsDate() {
        return afsDate;
    }

    public void setAfsDate(Timestamp afsDate) {
        this.afsDate = afsDate;
    }

    public String getAfsId() {
        return afsId;
    }

    public void setAfsId(String afsId) {
        this.afsId = afsId;
    }

    public List<SvAttachment> getAttachments() {
        if(attachments == null) {
            attachments = new ArrayList<SvAttachment>(0);
        }
        return attachments;
    }

    public void setAttachments(List<SvAttachment> attachments) {
        this.attachments = attachments;
    }
    
    public Note[] getSiteVisitNotes() {
		return siteVisitNotes;
	}

	public void setSiteVisitNotes(Note[] siteVisitNotes) {
		this.siteVisitNotes = siteVisitNotes;
		setDirty(true);
	}

	public List<String> getInspectionsReferencedIn() {
		if (inspectionsReferencedIn == null) return new ArrayList<String>();
		return inspectionsReferencedIn; 
	}

	public void setInspectionsReferencedIn(List<String> inspectionsReferencedIn) {
		this.inspectionsReferencedIn = inspectionsReferencedIn;
	}
}
