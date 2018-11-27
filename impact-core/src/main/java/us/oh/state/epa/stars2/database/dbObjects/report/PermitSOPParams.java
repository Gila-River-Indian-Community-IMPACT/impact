package us.oh.state.epa.stars2.database.dbObjects.report;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class PermitSOPParams extends BaseDB {
    private Integer paramId;
    private String schedule;
    private String permitGlobalStatusCd;
    private String activityName;
    private Integer warningDuration;
    private Integer dangerDuration;

    public PermitSOPParams() {
    }

    public final String getSchedule() {
        return schedule;
    }

    public final void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public final void populate(ResultSet rs) {
        try {
            setParamId(rs.getInt("param_id"));
            setSchedule(rs.getString("schedule"));
            setPermitGlobalStatusCd(rs.getString("permit_global_status_cd"));
            setActivityName(rs.getString("activity_template_nm"));
            setWarningDuration(rs.getInt("warning_duration"));
            setDangerDuration(rs.getInt("danger_duration"));

        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result
                + ((activityName == null) ? 0 : activityName.hashCode());
        result = PRIME * result
                + ((dangerDuration == null) ? 0 : dangerDuration.hashCode());
        result = PRIME * result + ((paramId == null) ? 0 : paramId.hashCode());
        result = PRIME
                * result
                + ((permitGlobalStatusCd == null) ? 0 : permitGlobalStatusCd
                        .hashCode());
        result = PRIME * result
                + ((schedule == null) ? 0 : schedule.hashCode());
        result = PRIME * result
                + ((warningDuration == null) ? 0 : warningDuration.hashCode());
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
        final PermitSOPParams other = (PermitSOPParams) obj;
        if (activityName == null) {
            if (other.activityName != null)
                return false;
        } else if (!activityName.equals(other.activityName))
            return false;
        if (dangerDuration == null) {
            if (other.dangerDuration != null)
                return false;
        } else if (!dangerDuration.equals(other.dangerDuration))
            return false;
        if (paramId == null) {
            if (other.paramId != null)
                return false;
        } else if (!paramId.equals(other.paramId))
            return false;
        if (permitGlobalStatusCd == null) {
            if (other.permitGlobalStatusCd != null)
                return false;
        } else if (!permitGlobalStatusCd.equals(other.permitGlobalStatusCd))
            return false;
        if (schedule == null) {
            if (other.schedule != null)
                return false;
        } else if (!schedule.equals(other.schedule))
            return false;
        if (warningDuration == null) {
            if (other.warningDuration != null)
                return false;
        } else if (!warningDuration.equals(other.warningDuration))
            return false;
        return true;
    }

    public final String getActivityName() {
        return activityName;
    }

    public final void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public final Integer getDangerDuration() {
        return dangerDuration;
    }

    public final void setDangerDuration(Integer dangerDuration) {
        this.dangerDuration = dangerDuration;
    }

    public final Integer getParamId() {
        return paramId;
    }

    public final void setParamId(Integer paramId) {
        this.paramId = paramId;
    }

    public final Integer getWarningDuration() {
        return warningDuration;
    }

    public final void setWarningDuration(Integer warningDuration) {
        this.warningDuration = warningDuration;
    }

    public final String getPermitGlobalStatusCd() {
        String ret = "N";
        if (permitGlobalStatusCd != null) {
            ret = permitGlobalStatusCd;
        }

        return ret;
    }

    public final void setPermitGlobalStatusCd(String permitGlobalStatusCd) {
        this.permitGlobalStatusCd = permitGlobalStatusCd;
    }

}
