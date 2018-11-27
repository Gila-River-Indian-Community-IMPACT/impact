package us.oh.state.epa.stars2.database.dbObjects.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class WorkloadData extends BaseDB {
    private Integer userId;
    private String activityName;
    private String permitType;
    private String permitIssuanceStatusCd;
    private String activityStatusCd;
    private String doLaaName;
    private String doLaaCd;
    private String doLaaShortDsc;
    private String doLaaId;
    private Timestamp startDt;

    public WorkloadData() {

    }

    public final String getActivityName() {
        return activityName;
    }

    public final void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public final Integer getUserId() {
        return userId;
    }

    public final void setUserId(Integer userId) {
        this.userId = userId;
    }

    public final String getPermitIssuanceStatusCd() {
        return permitIssuanceStatusCd;
    }

    public final void setPermitIssuanceStatusCd(String permitIssuanceStatusCd) {
        this.permitIssuanceStatusCd = permitIssuanceStatusCd;
    }

    public final String getPermitType() {
        return permitType;
    }

    public final void setPermitType(String permitType) {
        this.permitType = permitType;
    }

    public final String getActivityStatusCd() {
        return activityStatusCd;
    }

    public final void setActivityStatusCd(String activityStatusCd) {
        this.activityStatusCd = activityStatusCd;
    }

    public final String getDoLaaName() {
        return doLaaName;
    }

    public final void setDoLaaName(String doLaaName) {
        this.doLaaName = doLaaName;
    }

    public final String getDoLaaCd() {
        return doLaaCd;
    }

    public final void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    public final String getDoLaaId() {
        return doLaaId;
    }

    public final void setDoLaaId(String doLaaId) {
        this.doLaaId = doLaaId;
    }

    public final Timestamp getStartDt() {
        return startDt;
    }

    public final void setStartDt(Timestamp startDt) {
        this.startDt = startDt;
    }

    public final void populate(ResultSet rs) {
        try {
            setUserId(AbstractDAO.getInteger(rs, "user_id"));
            setActivityName(rs.getString("activity_template_nm"));
            setPermitType(rs.getString("permit_type_cd"));
            setPermitIssuanceStatusCd(rs.getString("permit_global_status_cd"));
            setActivityStatusCd(rs.getString("activity_status_cd"));
            setDoLaaName(rs.getString("do_laa_dsc"));
            setDoLaaCd(rs.getString("do_laa_cd"));
            setDoLaaShortDsc(rs.getString("do_laa_short_dsc"));
            setDoLaaId(rs.getString("do_laa_id"));
            // No one using this column yehp
            //setStartDt(rs.getTimestamp("start_dt"));

        } 
        catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }

    public String getDoLaaShortDsc() {
        return doLaaShortDsc;
    }

    public void setDoLaaShortDsc(String doLaaShortDsc) {
        this.doLaaShortDsc = doLaaShortDsc;
    }
}
