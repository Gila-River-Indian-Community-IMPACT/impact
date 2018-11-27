package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class FacilityPermitInfo extends BaseDB {
    private Integer permitId;
    private String permitNbr;
    private String permitTypeCd;
    private Timestamp effectiveDate;
    private List<Integer> applicationIds;
    private List<FacilityAppInfo> applications = new ArrayList<FacilityAppInfo>();
    private List<FacilityPermitEuInfo> eus;

    public FacilityPermitInfo() {
        super();
    }

    public final void populate(ResultSet rs) {
        try {
            setPermitId(AbstractDAO.getInteger(rs, "permit_id"));
            setPermitNbr(rs.getString("permit_nbr"));
            setEffectiveDate(rs.getTimestamp("effective_date"));
            setPermitTypeCd(rs.getString("permit_type_cd"));
            eus = new ArrayList<FacilityPermitEuInfo>();
            FacilityPermitEuInfo fpeu = new FacilityPermitEuInfo();
            fpeu.setEuCorrId(AbstractDAO.getInteger(rs, "corr_epa_emu_id"));
            fpeu.setRevocationDt(rs.getTimestamp("revocation_dt"));
            fpeu.setSupersededDt(rs.getTimestamp("superseded_dt"));
            fpeu.setTerminatedDt(rs.getTimestamp("terminated_dt"));
            eus.add(fpeu);
            while(rs.next()) {
                if(permitId.equals(AbstractDAO.getInteger(rs, "permit_id"))) {
                    fpeu = new FacilityPermitEuInfo();
                    fpeu.setEuCorrId(AbstractDAO.getInteger(rs, "corr_epa_emu_id"));
                    fpeu.setRevocationDt(rs.getTimestamp("revocation_dt"));
                    fpeu.setSupersededDt(rs.getTimestamp("superseded_dt"));
                    fpeu.setTerminatedDt(rs.getTimestamp("terminated_dt"));
                    eus.add(fpeu);
                } else {
                    rs.previous();
                    break;
                }
            }
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
    }

    public Timestamp getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Timestamp effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Integer getPermitId() {
        return permitId;
    }

    public void setPermitId(Integer permitId) {
        this.permitId = permitId;
    }

    public String getPermitNbr() {
        return permitNbr;
    }

    public void setPermitNbr(String permitNbr) {
        this.permitNbr = permitNbr;
    }

    public String getPermitTypeCd() {
        return permitTypeCd;
    }

    public void setPermitTypeCd(String permitTypeCd) {
        this.permitTypeCd = permitTypeCd;
    }

    public List<Integer> getApplicationIds() {
        return applicationIds;
    }

    public void setApplicationIds(List<Integer> applicationIds) {
        this.applicationIds = applicationIds;
    }

    public List<FacilityAppInfo> getApplications() {
        return applications;
    }
    
//    public StringBuffer getApplicationNbrs() {
//        StringBuffer sb = new StringBuffer();
//        for(FacilityAppInfo fai : applications) {
//            sb.append(fai.getApplicationNbr());
//            sb.append("; ");
//        }
//        return sb;
//    }
//    
//    public StringBuffer getApplicationDates() {
//        StringBuffer sb = new StringBuffer();
//        for(FacilityAppInfo fai : applications) {
//            sb.append(getDateStr(fai.getReceivedDate()));
//            sb.append(" ");
//        }
//        return sb;
//    }
    
    public static String getDateStr(Timestamp ts) {
        if(ts == null) return "--/--/----";
        Calendar cal = Calendar.getInstance();
        cal.setTime(ts);
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DAY_OF_MONTH);
        return Integer.toString(m) + "/" + Integer.toString(d) + "/" + Integer.toString(y);
    }

    public void setApplications(List<FacilityAppInfo> applications) {
        this.applications = applications;
    }

    public List<FacilityPermitEuInfo> getEus() {
        return eus;
    }

    public void setEus(List<FacilityPermitEuInfo> eus) {
        this.eus = eus;
    }

    
}


