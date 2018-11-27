package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.webcommon.reports.ReportProfileBase;

@SuppressWarnings("serial")
public class EmissionsReportEUGroup extends BaseDB {
    private Integer reportEuGroupID;
    private String reportEuGroupName;
    private EmissionsReportPeriod period;
    private List<Integer> eus = new ArrayList<Integer>(0);

    // NOT IN DATABASE
    // Used to tie together a reportGroup (orig) and the
    // reportGroup (comp) it is being compared to.
    // These are for temporary in-memory use and not on disk.
    // points to the original reportGroup object or null
    private EmissionsReportEUGroup orig;
    // points to the comparison reportGroup object or null.
    private EmissionsReportEUGroup comp;
    // indicate if caution should be displayed on tree.
    private boolean caution;
    // Used to indicate the EU should not be in the group
    private boolean notInFacility;
    private String notInFacilityMsg;
    private ArrayList<String> notInFacilityList;
    private ArrayList<String> notHaveProcessList;
    private ArrayList<String> isMarkedEG71List;
    // does not have same control equipment and release points.
    private String notSameCeEpList;

    public EmissionsReportEUGroup() {
        super();
        orig = null;
        comp = null;
        caution = false;
        notInFacility = false;
        notInFacilityList = new ArrayList<String>(0);
        notHaveProcessList = new ArrayList<String>(0);
        isMarkedEG71List = new ArrayList<String>(0);
    }

    public EmissionsReportEUGroup(EmissionsReportEUGroup old) {
        super(old);

        if (old != null) {
            setReportEuGroupID(old.getReportEuGroupID());
            setReportEuGroupName(old.getReportEuGroupName());
            setPeriod(old.getPeriod());
        }
    }
    
    public final boolean containsEU(int correlationId) {
        for(Integer id : eus) {
            if(id == null) {
                continue;
            }
            if(id.intValue() == correlationId) {
                return true;
            }
        }
        return false;
    }

    public final void addNotInFacilityList(String e) {
        notInFacilityList.add(e);
    }

    public final void addNotHaveProcessList(String e) {
        notHaveProcessList.add(e);
    }

    public final void addIsMarkedEG71List(String e) {
        isMarkedEG71List.add(e);
    }
    
    /*
     * The expected use is to first set notInFacility to true. This initializes
     * the lists to empty. Then calls to addNotInFacilityList(),
     * addNotHaveProcessList() and addIsMarkedEG71List() will add EU names to
     * the lists. Finally, notInFacilityMsgGen() must be called to generate the
     * message in notInFacilityMsg. The web page wil check notInFacility to
     * determine whether it should display this message.
     */
    public final void notInFacilityMsgGen() {
        String sep = "";
        notInFacilityMsg = null;
        StringBuffer s1 = genMsg(
                notInFacilityList,
                "Facility inventory indicates that emission unit ",
                " does not require reporting.<br>Edit the facility inventory or remove the emission unit from this group.",
                "Facility inventory indicates that emission units ",
                " do not require reporting.<br>Edit the facility inventory or remove the emission units from this group.");
        StringBuffer s2 = genMsg(
                notHaveProcessList,
                "Facility inventory indicates that emission unit ",
                " does not include the group's SCC code.<br>Edit the facility inventory or remove the emission unit from this group.",
                "Facility inventory indicates that emission units ",
                " do not include the group's SCC code.<br>Edit the facility inventory or remove the emission units from this group.");
        StringBuffer s3 = genMsg(
                isMarkedEG71List,
                "The emission unit ",
                " is not marked for detailed emissions.<br>Remove the emission unit from this group or specify detailed emissions for the emission unit.",
                "The Emission Units ",
                " are not marked for detailed emissions.<br>Remove the emission units from this group or specify detailed emissions for the emission units.");
        if (s1.length() > 0 && s2.length() > 0) {
            sep = "<br><br>";
        }
        notInFacilityMsg = s1.toString() + sep + s2.toString();
        sep = "";
        if (notInFacilityMsg.length() > 0 && s3.length() > 0) {
            sep = "<br><br>";
        }
        notInFacilityMsg = notInFacilityMsg.concat(sep + s3.toString());
        if(notInFacilityMsg.length() > 0 && null != notSameCeEpList &&
                notSameCeEpList.length() > 0) {
            sep = "<br><br>";
        }
        if(notSameCeEpList != null) {
            notInFacilityMsg = notInFacilityMsg.concat(sep + notSameCeEpList);
        }
    }

    private StringBuffer genMsg(ArrayList<String> lst, String beginning,
            String ending, String pluralBeginning, String pluralEnding) {
        StringBuffer b = new StringBuffer();
        if (!lst.isEmpty()) {
            String begin;
            String end;
            if (lst.size() == 1) {
                begin = beginning;
                end = ending;
            } else {
                begin = pluralBeginning;
                end = pluralEnding;
            }
            b.append(begin + lst.get(0));
            for (int i = 1; i < lst.size(); i++) {
                if (i == lst.size() - 1) {
                    b.append(" and " + lst.get(i));
                } else {
                    b.append(", " + lst.get(i));
                }
            }
            b.append(end);
        }
        return b;
    }
    
    public final List<ValidationMessage> submitVerify() {
        if(caution || notInFacility) {
            validationMessages.put("EUG " + reportEuGroupID,
                    new ValidationMessage("edit",
                            "Report Emission Unit Group: " +
                            reportEuGroupName + 
                            " is currently not valid",
                            ValidationMessage.Severity.ERROR, "group:"
                            + ReportProfileBase.treeNodeId(this), reportEuGroupName));
        }
        return new ArrayList<ValidationMessage>(validationMessages.values());
    }
    
    // Determine if list of EUs is the same as those in this group
    public final boolean sameEus(List<Integer> otherEus) {
        boolean ret = false;
        
        if(eus.size() == otherEus.size() &&
                eus.containsAll(otherEus)) {
            ret = true;
        }
        
        return ret;
    }

    public final List<Integer> getEus() {
        return eus;
    }

    public final void addEu(Integer newEuId) {
        if (newEuId != null) {
            this.eus.add(newEuId);
        }
    }

    public final void setEus(List<Integer> eus) {
        if (eus == null) {
            this.eus = new ArrayList<Integer>(0);
        } else {
            this.eus = eus;
        }
    }
    
    public final boolean zeroEmissions() {
        boolean zero = false;
        if(period != null && period.zeroHours()) {
            zero = true;
        }
        return zero;
    }

    public final EmissionsReportPeriod getPeriod() {
        return period;
    }

    public final void setPeriod(EmissionsReportPeriod period) {
        this.period = period;
    }

    public final Integer getReportEuGroupID() {
        return reportEuGroupID;
    }

    public final void setReportEuGroupID(Integer reportEuGroupID) {
        this.reportEuGroupID = reportEuGroupID;
    }

    public final String getReportEuGroupName() {
        return reportEuGroupName;
    }

    public final void setReportEuGroupName(String reportEuGroupName) {
        this.reportEuGroupName = reportEuGroupName;
    }

    public final void populate(ResultSet rs) throws SQLException {
        try {
            setReportEuGroupID(AbstractDAO.getInteger(rs, "report_eu_group_id"));
            setReportEuGroupName(rs.getString("report_eu_group_nm"));
            setLastModified(AbstractDAO.getInteger(rs, "rreg_lm"));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }

        try {
            if (AbstractDAO.getInteger(rs, "emission_period_id") != null) {
                period = new EmissionsReportPeriod();
                period.populate(rs);
            }
        } catch (SQLException sqle) {
            //OK if this attribute not present
            //logger.warn(sqle.getMessage() + ": emission_period_id");
        }

        try {
            do {
                Integer c = AbstractDAO.getInteger(rs, "corr_id");
                if(c != null) eus.add(c);
            } while (rs.next());
        } catch (SQLException sqle) {
            // OK if this attribute not there
            //logger.warn(sqle.getMessage() + ": corr_id");
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((period == null) ? 0 : period.hashCode());
        result = PRIME * result
                + ((reportEuGroupID == null) ? 0 : reportEuGroupID.hashCode());
        result = PRIME
                * result
                + ((reportEuGroupName == null) ? 0 : reportEuGroupName
                        .hashCode());
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
        final EmissionsReportEUGroup other = (EmissionsReportEUGroup) obj;
        if (period == null) {
            if (other.period != null)
                return false;
        } else if (!period.equals(other.period))
            return false;
        if (reportEuGroupID == null) {
            if (other.reportEuGroupID != null)
                return false;
        } else if (!reportEuGroupID.equals(other.reportEuGroupID))
            return false;
        if (reportEuGroupName == null) {
            if (other.reportEuGroupName != null)
                return false;
        } else if (!reportEuGroupName.equals(other.reportEuGroupName))
            return false;
        return true;
    }

    public final EmissionsReportEUGroup getComp() {
        return comp;
    }

    public final void setComp(EmissionsReportEUGroup comp) {
        this.comp = comp;
    }

    public final EmissionsReportEUGroup getOrig() {
        return orig;
    }

    public final void setOrig(EmissionsReportEUGroup orig) {
        this.orig = orig;
    }

    public final boolean isCaution() {
        return caution;
    }

    public final void setCaution(boolean caution) {
        this.caution = caution;
    }

    public final boolean isNotInFacility() {
        return notInFacility;
    }

    public final void setNotInFacility(boolean notInFacility) {
        if (!this.notInFacility && notInFacility) {
            // If setting to true, then initialize the lists.
            notInFacilityList = new ArrayList<String>(0);
            notHaveProcessList = new ArrayList<String>(0);
            isMarkedEG71List = new ArrayList<String>(0);
        }
        this.notInFacility = notInFacility;
    }

    public final String getNotInFacilityMsg() {
        return notInFacilityMsg;
    }

    public void setNotSameCeEpList(String notSameCeEpList) {
        this.notSameCeEpList = notSameCeEpList;
    }
}
