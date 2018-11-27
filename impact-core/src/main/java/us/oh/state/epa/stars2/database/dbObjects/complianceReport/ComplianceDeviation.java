package us.oh.state.epa.stars2.database.dbObjects.complianceReport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.webcommon.ValidationBase;

@SuppressWarnings("serial")
public class ComplianceDeviation extends BaseDB {
    private int reportId;
    private transient Timestamp startDate;
    private long startDateLong;
    private transient Timestamp endDate;
    private long endDateLong;
    private String identifier;
    private String controlPermit;
    private String perDescription;
    private String perProbableCause;
    private String perCorrectiveAction;
    private String tvccComplianceMethod;
    private String tvccExcursionsSubmitted;
    private String tvccExcursionsOther;
    private int deviationId;
    
    public ComplianceDeviation() {
        super();
    }

    public ComplianceDeviation(ComplianceDeviation old) {
        super(old);
        setDeviationId(old.getDeviationId());
        setReportId(old.getReportId());
        setStartDate(old.getStartDate());
        setEndDate(old.getEndDate());
        setIdentifier(old.getIdentifier());
        setControlPermit(old.getControlPermit());
        setPerDescription(old.getPerDescription());
        setPerProbableCause(old.getPerProbableCause());
        setPerCorrectiveAction(old.getPerCorrectiveAction());
        setTvccComplianceMethod(old.getTvccComplianceMethod());
        setTvccExcursionsSubmitted(old.getTvccExcursionsSubmitted());
        setTvccExcursionsOther(old.getTvccExcursionsOther());
    }
    
    public int getDeviationId() {
        return deviationId;
    }

    public void setDeviationId(int deviationId) {
        this.deviationId = deviationId;
    }

    public void populate(ResultSet rs) throws SQLException {
        try {
            setReportId(AbstractDAO.getInteger(rs,"report_id"));
            setDeviationId(AbstractDAO.getInteger(rs,"deviation_id"));
            setIdentifier(rs.getString("identifier"));
            setControlPermit(rs.getString("control_permit"));
            setStartDate(rs.getTimestamp("start_date"));
            setEndDate(rs.getTimestamp("end_date"));
            
            setPerCorrectiveAction(rs.getString("per_corrective_action"));
            setPerDescription(rs.getString("per_description"));
            setPerProbableCause(rs.getString("per_probable_cause"));
            
            setTvccComplianceMethod(rs.getString("tvcc_compliance_method"));
            setTvccExcursionsOther(rs.getString("tvcc_excursions_other"));
            setTvccExcursionsSubmitted(rs.getString("tvcc_excursions_submitted"));
            
            setDirty(false);
        } catch (SQLException sqle) {
            logger.error("Required field error");
        } finally {
            newObject = false;
        }
    }

    public String getControlPermit() {
        return controlPermit;
    }

    public void setControlPermit(String controlPermit) {
        this.controlPermit = controlPermit;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
        if (this.endDate != null) {
            this.endDateLong = this.endDate.getTime();
        } else {
            this.endDateLong = 0;
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPerCorrectiveAction() {
        return perCorrectiveAction;
    }

    public void setPerCorrectiveAction(String perCorrectiveAction) {
        this.perCorrectiveAction = perCorrectiveAction;
    }

    public String getPerDescription() {
        return perDescription;
    }

    public void setPerDescription(String perDescription) {
        this.perDescription = perDescription;
    }

    public String getPerProbableCause() {
        return perProbableCause;
    }

    public void setPerProbableCause(String perProbableCause) {
        this.perProbableCause = perProbableCause;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
        if (this.startDate != null) {
            this.startDateLong = this.startDate.getTime();
        } else {
            this.startDateLong = 0;
        }
    }

    public String getTvccComplianceMethod() {
        return tvccComplianceMethod;
    }

    public void setTvccComplianceMethod(String tvccComplianceMethod) {
        this.tvccComplianceMethod = tvccComplianceMethod;
    }

    public String getTvccExcursionsOther() {
        return tvccExcursionsOther;
    }

    public void setTvccExcursionsOther(String tvccExcursionsOther) {
        this.tvccExcursionsOther = tvccExcursionsOther;
    }

    public String getTvccExcursionsSubmitted() {
        return tvccExcursionsSubmitted;
    }

    public void setTvccExcursionsSubmitted(String tvccExcursionsSubmitted) {
        this.tvccExcursionsSubmitted = tvccExcursionsSubmitted;
    }

    public long getStartDateLong() {
        long ret = 0;
        
        if (startDate != null) {
            ret = startDate.getTime();
        }
            
        return ret;
    }

    public void setStartDateLong(long startDateLong) {
        startDate = null;
        if (startDateLong > 0) {
            startDate = new Timestamp(startDateLong);
        }
    }

    public long getEndDateLong() {
        long ret = 0;
        
        if (endDate != null) {
            ret = endDate.getTime();
        }

        return ret;
    }

    public void setEndDateLong(long endDateLong) {
        endDate = null;
        if (endDateLong > 0) {
            endDate = new Timestamp(endDateLong);
        }
    }
    
    public ValidationMessage[] validate() {
        //make sure each parameter has been completed
        List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
        if (getStartDate() == null || getEndDate()==null) {
            messages.add(new ValidationMessage("deviationsPER","Each deviation must have both a start and end date.",
            		ValidationMessage.Severity.WARNING, ValidationBase.COMPLIANCE_TAG));
        } else {
            if (getStartDate().after(getEndDate())) {
                messages.add(new ValidationMessage("deviationsPER","Each deviation's start date must be on or before the end date.",
                	ValidationMessage.Severity.WARNING, ValidationBase.COMPLIANCE_TAG));
            }
        }
        return messages.toArray(new ValidationMessage[0]);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // manually set transient date values since this does not appear to
        // work properly with persistence
        setStartDateLong(this.startDateLong);
        setEndDateLong(this.endDateLong);
    }

}
