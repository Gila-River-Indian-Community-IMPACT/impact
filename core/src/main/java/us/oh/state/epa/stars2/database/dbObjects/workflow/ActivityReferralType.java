/*
 * CustomDetailTypeDef.java
 *
 * Created on March 5, 2004, 12:39 PM
 */

package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2006 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @author pyeh
 *
 */
@SuppressWarnings("serial")
public final class ActivityReferralType extends BaseDB {
    private Integer activityReferralTypeId;
    private String activityReferralTypeDsc;
    private String extendProcessEndDate;
    private Integer defaultDays;

    /**
     * 
     */
    public ActivityReferralType() {
        super();
    }

    /**
     * @param old
     */
    public ActivityReferralType(ActivityReferralType old) {
        super(old);
        if (old != null) {
            setActivityReferralTypeId(old.getActivityReferralTypeId());
            setActivityReferralTypeDsc(old.getActivityReferralTypeDsc());
            setDefaultDays(old.getDefaultDays());
            setExtendProcessEndDate(old.getExtendProcessEndDate());
        }
    }

    public final void populate(ResultSet rs) {
        try {
            setActivityReferralTypeId(AbstractDAO.getInteger(rs, "ACTIVITY_REFERRAL_TYPE_ID"));
            setActivityReferralTypeDsc(rs.getString("ACTIVITY_REFERRAL_TYPE_DSC"));
            setDefaultDays(AbstractDAO.getInteger(rs, "DEFAULT_DAYS"));
            setExtendProcessEndDate(rs.getString("EXTEND_PROCESS_END_DATE"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        }
        catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }

    public final String getActivityReferralTypeDsc() {
        return activityReferralTypeDsc;
    }

    public final void setActivityReferralTypeDsc(String activityReferralTypeDsc) {
        this.activityReferralTypeDsc = activityReferralTypeDsc;
    }

    public final Integer getActivityReferralTypeId() {
        return activityReferralTypeId;
    }

    public final void setActivityReferralTypeId(Integer activityReferralTypeId) {
        this.activityReferralTypeId = activityReferralTypeId;
    }

    public final Integer getDefaultDays() {
        return defaultDays;
    }

    public final void setDefaultDays(Integer defaultDays) {
        this.defaultDays = defaultDays;
    }

    public final String getExtendProcessEndDate() {
        return extendProcessEndDate;
    }

    public final void setExtendProcessEndDate(String extendProcessEndDate) {
        this.extendProcessEndDate = extendProcessEndDate;
    }
}
