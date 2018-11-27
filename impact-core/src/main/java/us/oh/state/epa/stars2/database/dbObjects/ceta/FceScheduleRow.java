package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.def.BasicUsersDef;

public class FceScheduleRow extends CetaBaseDB implements Comparable<FceScheduleRow>{
   
	private static final long serialVersionUID = -884638708680955208L;

	private int    rowNum;  // used to identify to row clicked.
    private int    order; // this is the FFY that is highest scheduled.
    private String facilityId;
    private String facilityName;
    private String operatingStatusCd;
    private String doLaaCd;
    private String countyCd;
    private String facilityTypeCd;
    private String permitClassCd;
    
    // Completed Inspection
    private Integer completedFceId;
    private Timestamp lastCompleted; // when reviewed
    private Integer inFfy; // FFY of completed one
    private Timestamp completedScheduledTimestamp;
    private Integer oldEvaluator;
    private boolean useOldEvaluator;
    private boolean completedAfsLocked;
    private boolean completedUsEpaCommitted;
    private boolean unchangedCompletedUsEpaCommitted;
    private Integer completedLastModified;
    
    // Scheduled Inspection
    private Integer scheduledFceId;
    private Integer neededBy;  // federal fiscal year needed by
    private Integer assignedStaff;
    private Integer unchangedAssignedStaff;
    private Timestamp unchangedNextScheduled;
    private Timestamp nextScheduled;
    private boolean scheduledUsEpaCommitted;
    private boolean unchangedScheduledUsEpaCommitted;
    private boolean useOldEval;
    private boolean flagErrorExisting; // when Inspection does not have both schedule and assigned staff selected
    private boolean flagOldDate; // when next FFY or Scheduled date is in the past.
    private boolean locked;  // set to true to indicate do not schedule one on the completed row because there are already scheduled ones
    private boolean scheduledAfsSchedLocked;
    private Integer scheduledLastModified;
    private String schedOrNot;
    private LinkedHashMap<String, Timestamp> pickListSchedule;
    private boolean allowAddAnother;  // allow to add another Inspection
    private boolean changed;  // indicate if row was updated.
    
    private static Integer currentFFY;  // to compare with neededBy to see if highlight is in the past
    private static Timestamp currentDate;  // to compare with nextScheduled to see if highlight in past or almost in past.
    
    private String cmpId;
    private String companyName;
    
    public FceScheduleRow() {
        super();
    }
    
    /** Populate this instance from a database ResultSet. */
    public final void populate(java.sql.ResultSet rs) throws SQLException {
        boolean errorFlag = true;
        try {
            setFacilityId(rs.getString("facility_id"));
            setFacilityName(rs.getString("facility_nm"));
            setCmpId(rs.getString("cmp_id"));
            setCompanyName(rs.getString("company_nm"));
            this.setCountyCd(rs.getString("county_cd"));
            setDoLaaCd(rs.getString("do_laa_cd"));
            setPermitClassCd(rs.getString("permit_classification_cd"));
            setFacilityTypeCd(rs.getString("facility_type_cd"));
            setOperatingStatusCd(rs.getString("operating_status_cd"));
            errorFlag = false;
            Integer fceId = AbstractDAO.getInteger(rs, "fce_id");
            if (fceId != null) {
                errorFlag = true;
                setLastCompleted(rs.getTimestamp("evaluated_dt"));
                if (lastCompleted != null) {
                    // completed Inspections
                    completedFceId = fceId;
                    setCompletedScheduledTimestamp(CetaBaseDB.quarterStart(rs.getTimestamp("scheduled_dt")));
                    setOldEvaluator(AbstractDAO.getInteger(rs, "evaluator_id"));
                    setCompletedUsEpaCommitted(AbstractDAO.translateIndicatorToBoolean(rs.getString("us_epa_committed")));
                    setUnchangedCompletedUsEpaCommitted(completedUsEpaCommitted);
                    String afsID = rs.getString("afs_id");
                    if (afsID != null) completedAfsLocked = true;
                    completedLastModified = AbstractDAO.getInteger(rs, "last_modified");
                } else {
                    scheduledFceId = fceId;
                    setAssignedStaff(AbstractDAO.getInteger(rs, "staff_assigned_id"));
                    unchangedAssignedStaff = assignedStaff;
                    setNextScheduled(CetaBaseDB.quarterStart(rs.getTimestamp("scheduled_dt")));
                    setUnchangedNextScheduled(nextScheduled);
                    setScheduledUsEpaCommitted(AbstractDAO.translateIndicatorToBoolean(rs.getString("us_epa_committed")));
                    setUnchangedScheduledUsEpaCommitted(scheduledUsEpaCommitted);
                    String afsID = rs.getString("afsSched_id");
                    if (afsID != null) scheduledAfsSchedLocked = true;
                    scheduledLastModified = AbstractDAO.getInteger(rs, "last_modified");
                }
            }
        } catch(SQLException e) {
            if (errorFlag) {
                logger.error(e.getMessage(), e);
                throw e;
            }
        }
    }
    
    public int compareTo(FceScheduleRow o) {
        if (order < o.order) return -1;
        if (order > o.order) return 1;
        if (!facilityId.equals(o.facilityId)) return facilityId.compareTo(o.facilityId);
        if (completedFceId != null  && o.completedFceId == null) return -1;  // put existing ones first
        if (completedFceId == null  && o.completedFceId != null) return 1; // put existing ones first
        // put in FFY order
        if (neededBy != null && o.neededBy != null) return neededBy.compareTo(o.neededBy);
        return 0;  // should not reach here
    }
    
    
    public boolean isActiveUser() {
        Boolean rtn = false;
        if (oldEvaluator != null) {
            rtn = BasicUsersDef.getAllUserStatus(oldEvaluator);
        }
        return rtn;
    }
    
    public String getLastCompletedSched() {
        return CetaBaseDB.getScheduled(lastCompleted);
    }

    public String getCountyCd() {
        return countyCd;
    }

    public void setCountyCd(String countyCd) {
        this.countyCd = countyCd;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public Timestamp getLastCompleted() {
        return lastCompleted;
    }

    public void setLastCompleted(Timestamp lastCompleted) {
        this.lastCompleted = lastCompleted;
    }

    public Timestamp getNextScheduled() {
        return CetaBaseDB.quarterStart(nextScheduled);
    }

    public String getSchedOrNot() {
        return schedOrNot;
    }

    public void setSchedOrNot(String schedOrNot) {
        this.schedOrNot = schedOrNot;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getOperatingStatusCd() {
        return operatingStatusCd;
    }

    public void setOperatingStatusCd(String operatingStatusCd) {
        this.operatingStatusCd = operatingStatusCd;
    }

    public String getDoLaaCd() {
        return doLaaCd;
    }

    public void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    public Integer getNeededBy() {
        return neededBy;
    }

    public void setNeededBy(Integer neededBy) {
        this.neededBy = neededBy;
        flagOldDate = false;
        if (neededBy == null && nextScheduled == null) return;
        if ((neededBy != null && 0 > neededBy.compareTo(currentFFY)) ||
                (nextScheduled!= null && nextScheduled.before(currentDate))) flagOldDate = true; 
    }
    
    public void setNextScheduled(Timestamp nextScheduled) {
        this.nextScheduled = nextScheduled;
        flagOldDate = false;
        if (neededBy == null && nextScheduled == null) return;
        if ((neededBy != null && 0 > neededBy.compareTo(currentFFY)) ||
                (nextScheduled!= null && nextScheduled.before(currentDate))) flagOldDate = true; 
    }
    
    public LinkedHashMap<String, Timestamp> getPickListSchedule() {
        return pickListSchedule;
    }

    public void setPickListSchedule(
            LinkedHashMap<String, Timestamp> pickListSchedule) {
        this.pickListSchedule = pickListSchedule;
    }

    public Integer getAssignedStaff() {
        return assignedStaff;
    }

    public void setAssignedStaff(Integer assignedStaff) {
        this.assignedStaff = assignedStaff;
    }

    public Integer getOldEvaluator() {
        return oldEvaluator;
    }

    public void setOldEvaluator(Integer oldEvaluator) {
        this.oldEvaluator = oldEvaluator;
    }

    public boolean isUseOldEval() {
        return useOldEval;
    }

    public void setUseOldEval(boolean useOldEval) {
        if (useOldEval) {
            assignedStaff = oldEvaluator;
        }
        else assignedStaff = null;
        this.useOldEval = useOldEval;
    }

    public boolean isFlagErrorExisting() {
        return flagErrorExisting;
    }

    public void setFlagErrorExisting(boolean flagErrorExisting) {
        this.flagErrorExisting = flagErrorExisting;
    }

    public boolean isAllowAddAnother() {
        return allowAddAnother;
    }

    public void setAllowAddAnother(boolean allowAddAnother) {
        this.allowAddAnother = allowAddAnother;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public Integer getInFfy() {
        return inFfy;
    }

    public void setInFfy(Integer inFfy) {
        this.inFfy = inFfy;
    }

    public Integer getCompletedFceId() {
        return completedFceId;
    }

    public void setCompletedFceId(Integer completedFceId) {
        this.completedFceId = completedFceId;
    }

    public Timestamp getCompletedScheduledTimestamp() {
        return completedScheduledTimestamp;
    }

    public void setCompletedScheduledTimestamp(Timestamp completedScheduledTimestamp) {
        this.completedScheduledTimestamp = completedScheduledTimestamp;
    }

    public boolean isScheduledAfsSchedLocked() {
        return scheduledAfsSchedLocked;
    }

    public void setScheduledAfsSchedLocked(boolean scheduledAfsSchedLocked) {
        this.scheduledAfsSchedLocked = scheduledAfsSchedLocked;
    }

    public Integer getScheduledFceId() {
        return scheduledFceId;
    }

    public void setScheduledFceId(Integer scheduledFceId) {
        this.scheduledFceId = scheduledFceId;
    }

    public boolean isUnchangedCompletedUsEpaCommitted() {
        return unchangedCompletedUsEpaCommitted;
    }

    public void setUnchangedCompletedUsEpaCommitted(
            boolean unchangedCompletedUsEpaCommitted) {
        this.unchangedCompletedUsEpaCommitted = unchangedCompletedUsEpaCommitted;
    }

    public boolean isCompletedUsEpaCommitted() {
        return completedUsEpaCommitted;
    }

    public void setCompletedUsEpaCommitted(boolean completedUsEpaCommitted) {
        this.completedUsEpaCommitted = completedUsEpaCommitted;
    }

    public Integer getUnchangedAssignedStaff() {
        return unchangedAssignedStaff;
    }

    public void setUnchangedAssignedStaff(Integer unchangedAssignedStaff) {
        this.unchangedAssignedStaff = unchangedAssignedStaff;
    }

    public Timestamp getUnchangedNextScheduled() {
        return unchangedNextScheduled;
    }

    public void setUnchangedNextScheduled(Timestamp unchangedNextScheduled) {
        this.unchangedNextScheduled = unchangedNextScheduled;
    }

    public Integer getCompletedLastModified() {
        return completedLastModified;
    }

    public void setCompletedLastModified(Integer completedLastModified) {
        this.completedLastModified = completedLastModified;
    }

    public Integer getScheduledLastModified() {
        return scheduledLastModified;
    }

    public void setScheduledLastModified(Integer scheduledLastModified) {
        this.scheduledLastModified = scheduledLastModified;
    }

    public boolean isScheduledUsEpaCommitted() {
        return scheduledUsEpaCommitted;
    }

    public void setScheduledUsEpaCommitted(boolean scheduledUsEpaCommitted) {
        this.scheduledUsEpaCommitted = scheduledUsEpaCommitted;
    }

    public boolean isUnchangedScheduledUsEpaCommitted() {
        return unchangedScheduledUsEpaCommitted;
    }

    public void setUnchangedScheduledUsEpaCommitted(
            boolean unchangedScheduledUsEpaCommitted) {
        this.unchangedScheduledUsEpaCommitted = unchangedScheduledUsEpaCommitted;
    }

    public boolean isUseOldEvaluator() {
        return useOldEvaluator;
    }

    public void setUseOldEvaluator(boolean useOldEvaluator) {
        this.useOldEvaluator = useOldEvaluator;
        assignedStaff = oldEvaluator;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public boolean isCompletedAfsLocked() {
        return completedAfsLocked;
    }

    public void setCompletedAfsLocked(boolean completedAfsLocked) {
        this.completedAfsLocked = completedAfsLocked;
    }

    public boolean isFlagOldDate() {
        return flagOldDate;
    }

    public static Integer getCurrentFFY() {
        return currentFFY;
    }

    public static void setCurrentFFY(Integer currentFFY) {
        FceScheduleRow.currentFFY = currentFFY;
    }

    public static Timestamp getCurrentDate() {
        return currentDate;
    }

    public static void setCurrentDate(Timestamp currentDate) {
        FceScheduleRow.currentDate = currentDate;
    }

	public String getFacilityTypeCd() {
		return facilityTypeCd;
	}

	public void setFacilityTypeCd(String facilityTypeCd) {
		this.facilityTypeCd = facilityTypeCd;
	}

	public String getPermitClassCd() {
		return permitClassCd;
	}

	public void setPermitClassCd(String permitClassCd) {
		this.permitClassCd = permitClassCd;
	}
	
	public String getCmpId() {
        return cmpId;
    }

    public void setCmpId(String cmpId) {
    	this.cmpId = cmpId;
    }
    
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
