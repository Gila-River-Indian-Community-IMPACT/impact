package us.oh.state.epa.stars2.database.dbObjects.report;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class PermitWorkers extends BaseDB {
    private String activityName;
    private Integer userId;
    private String firstName;
    private String lastName;
    private String doLaaName;
    private String doLaaCd;
    private String doLaaShortDsc;
    private String doLaaId;

    public PermitWorkers() {
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

    public final String getFirstName() {
        return firstName;
    }

    public final void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public final String getLastName() {
        return lastName;
    }

    public final void setLastName(String lastName) {
        this.lastName = lastName;
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

    public final void populate(ResultSet rs) {
        try {
            setUserId(AbstractDAO.getInteger(rs, "user_id"));
            setActivityName(rs.getString("activity_template_nm"));
            setFirstName(rs.getString("first_nm"));
            setLastName(rs.getString("last_nm"));
            setDoLaaName(rs.getString("do_laa_dsc"));
            setDoLaaCd(rs.getString("do_laa_cd"));
            setDoLaaId(rs.getString("do_laa_id"));
            setDoLaaShortDsc(rs.getString("do_laa_short_dsc"));

        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }

    public String getDoLaaShortDsc() {
        return doLaaShortDsc;
    }

    public void setDoLaaShortDsc(String doLaaShortDsc) {
        this.doLaaShortDsc = doLaaShortDsc;
    }
}
