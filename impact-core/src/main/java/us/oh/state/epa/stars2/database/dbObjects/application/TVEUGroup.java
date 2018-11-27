package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class TVEUGroup extends BaseDB {
    private Integer tvEuGroupId;
    private Integer applicationId;
    private String tvEuGroupName;
    private List<TVApplicationEU> eus;
    private List<TVApplicableReq> applicableRequirements;
    private List<TVApplicableReq> stateOnlyRequirements;

    public TVEUGroup() {
        super();
    }

    /**
     * Copy constructor
     * @param old
     */
    public TVEUGroup(TVEUGroup old) {
        super(old);
        if (old != null) {
            this.tvEuGroupId = old.tvEuGroupId;
            this.applicationId = old.applicationId;
            this.tvEuGroupName = old.tvEuGroupName;
            this.eus = old.eus;
            this.applicableRequirements = old.applicableRequirements;
            this.stateOnlyRequirements = old.stateOnlyRequirements;
        }
    }

    public void populate(ResultSet rs) {
        // data retrievals for this class join the pa_tv_eu_group table
        // with the tables needed to retrieve data for all the eus in the
        // group. The "required fields" set below will always be populated
        // in each row of the result set. The "optional fields" will not
        // be populated if there are no EUs in the group.
        try {
            setTvEuGroupId(AbstractDAO.getInteger(rs, "tv_eu_group_id"));
            setApplicationId(AbstractDAO.getInteger(rs, "application_id"));
            setTvEuGroupName(rs.getString("tv_eu_group_name"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }

        try {
            // if validated flag is null, no EUs are currently assigned
            // to this group
            String validatedFlag = rs.getString("validated_flag");
            if (validatedFlag != null) {
                eus = new ArrayList<TVApplicationEU>();
                do {
                    TVApplicationEU tempEu = new TVApplicationEU();
                    tempEu.populate(rs);
                    eus.add(tempEu);
                } while (rs.next());
            }
            setDirty(false);
        } catch (SQLException sqle) {
            logger.warn("Optional field error");
        }

    }

    public final List<TVApplicationEU> getEus() {
        if (eus == null) {
            eus = new ArrayList<TVApplicationEU>();
        }
        return eus;
    }

    public final void setEus(List<TVApplicationEU> eus) {
        this.eus = new ArrayList<TVApplicationEU>();
        if (eus != null) {
            this.eus.addAll(eus);
        }
    }

    public final Integer getTvEuGroupId() {
        return tvEuGroupId;
    }

    public final void setTvEuGroupId(Integer tvEuGroupId) {
        this.tvEuGroupId = tvEuGroupId;
    }

    public final String getTvEuGroupName() {
        return tvEuGroupName;
    }

    public final void setTvEuGroupName(String tvEuGroupName) {
        this.tvEuGroupName = tvEuGroupName;
    }

    public final Integer getApplicationId() {
        return applicationId;
    }

    public final void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public final List<TVApplicableReq> getApplicableRequirements() {
        if (applicableRequirements == null) {
            applicableRequirements = new ArrayList<TVApplicableReq>();
        }
        return applicableRequirements;
    }

    public final void setApplicableRequirements(
            List<TVApplicableReq> applicableRequirements) {
        this.applicableRequirements = new ArrayList<TVApplicableReq>();
        if (applicableRequirements != null) {
            this.applicableRequirements.addAll(applicableRequirements);
        }
    }

    public final List<TVApplicableReq> getStateOnlyRequirements() {
        if (stateOnlyRequirements == null) {
            stateOnlyRequirements = new ArrayList<TVApplicableReq>();
        }
        return stateOnlyRequirements;
    }

    public final void setStateOnlyRequirements(
            List<TVApplicableReq> stateOnlyRequirements) {
        this.stateOnlyRequirements = new ArrayList<TVApplicableReq>();
        if (stateOnlyRequirements != null) {
            this.stateOnlyRequirements.addAll(stateOnlyRequirements);
        }
    }

}
