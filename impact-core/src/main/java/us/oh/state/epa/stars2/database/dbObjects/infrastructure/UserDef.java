package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * UserDef.
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version 1.0
 * @author Tom Dixon
 */
@SuppressWarnings("serial")
public class UserDef extends BaseDB {
    private Integer userId;
    private String networkLoginNm;
    private String activeInd;
    private String passwordVal;
    private Timestamp passwordExpDt;
    private Integer managerId;
    private Contact contact;
    private ArrayList<SecurityGroup> securityGroups;
    private Integer[] groupIds;
    private String userFirstNm;
    private String userLastNm;
    private Integer positionNumber;

    public UserDef() {
    }

    public UserDef(UserDef old) {
        setUserId(old.getUserId());
        setNetworkLoginNm(old.getNetworkLoginNm());
        setActiveInd(old.getActiveInd());
        setPasswordVal(old.getPasswordVal());
        setUserFirstNm(old.getUserFirstNm());
        setUserLastNm(old.getUserLastNm());
        setPasswordExpDt(old.getPasswordExpDt());
        setManagerId(old.getManagerId());
        setContact(new Contact(old.getContact()));
        setUserFirstNm(old.getUserFirstNm());
        setUserLastNm(old.getUserLastNm());
        setPositionNumber(old.getPositionNumber());

        securityGroups = new ArrayList<SecurityGroup>();

        for (SecurityGroup sg : old.getSecurityGroups()) {
            securityGroups.add(sg);
        }

        if ((old.getGroupIds() != null) && (old.getGroupIds().length > 0)) {
            Integer[] oldGroupIds = old.getGroupIds();
            groupIds = new Integer[oldGroupIds.length];

            for (int i = 0; i < oldGroupIds.length; i++) {
                groupIds[i] = oldGroupIds[0];
            }
        }
    }

    
    
    public String getPositionNumberString() {
		return null == positionNumber? null : String.valueOf(positionNumber);
	}

	public void setPositionNumberString(String positionNumberString) {
		this.positionNumber = 
				null == positionNumber? 
						null : Integer.valueOf(positionNumberString);
	}

	public Integer getPositionNumber() {
		return positionNumber;
	}

	public void setPositionNumber(Integer positionNumber) {
		this.positionNumber = positionNumber;
	}

	/**
     * UserId.
     * 
     * @return Integer
     */
    public final Integer getUserId() {
        return userId;
    }

    /**
     * UserId.
     * 
     * @param userId
     */
    public final void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * NetworkLoginNm.
     * 
     * @return String
     */
    public final String getNetworkLoginNm() {
        return networkLoginNm;
    }

    /**
     * NetworkLoginNm.
     * 
     * @param networkLoginNm
     */
    public final void setNetworkLoginNm(String networkLoginNm) {
        this.networkLoginNm = networkLoginNm;
    }

    /**
     * Active.
     * 
     * @return String
     */
    public final boolean isActive() {
        boolean ret = false;

        if (activeInd.compareToIgnoreCase("Y") == 0) {
            ret = true;
        }

        return ret;
    }

    /**
     * ActiveInd.
     * 
     * @return String
     */
    public final String getActiveInd() {
        return activeInd;
    }

    /**
     * ActiveInd.
     * 
     * @param activeInd
     */
    public final void setActive(boolean active) {
        if (active) {
            this.activeInd = "Y";
        } else {
            this.activeInd = "N";
        }
    }

    /**
     * ActiveInd.
     * 
     * @param activeInd
     */
    public final void setActiveInd(String activeInd) {
        this.activeInd = activeInd;
    }

    /**
     * PasswordVal.
     * 
     * @return String
     */
    public final String getPasswordVal() {
        return passwordVal;
    }

    /**
     * PasswordVal.
     * 
     * @param passwordVal
     */
    public final void setPasswordVal(String passwordVal) {
        this.passwordVal = passwordVal;
    }

    /**
     * UserFirstName.
     * 
     * @return String
     */
    public final String getUserFirstNm() {
    	return userFirstNm;
    }

    /**
     * UserFirstNm.
     * 
     * @param userFirstNm
     */
    public final void setUserFirstNm(String userFirstNm) {
    	this.userFirstNm = userFirstNm;
    }

    /**
     * UserLastName.
     * 
     * @return String
     */
    public final String getUserLastNm() {
    	return userLastNm;
    }

    /**
     * UserLastNm.
     * 
     * @param userLastNm
     */
    public final void setUserLastNm(String userLastNm) {
    	this.userLastNm = userLastNm;
    }

    /**
     * ManagerId.
     * 
     * @return Integer
     */
    public final Integer getManagerId() {
        return managerId;
    }

    /**
     * ManagerId.
     * 
     * @param managerId
     */
    public final void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    /**
     * Contact.
     * 
     * @return Contact
     */
    public final Contact getContact() {
        return contact;
    }

    /**
     * Contact.
     * 
     * @param contact
     */
    public final void setContact(Contact contact) {
        this.contact = contact;
    }

    /**
     * @return Returns the passwordexpdt.
     */
    public final Timestamp getPasswordExpDt() {
        return passwordExpDt;
    }

    /**
     * @param _password_exp_dt
     *            The _password_exp_dt to set.
     */
    public final void setPasswordExpDt(Timestamp passwordExpDt) {
        this.passwordExpDt = passwordExpDt;
    }

    public final ArrayList<SecurityGroup> getSecurityGroups() {
        return securityGroups;
    }

    public final void setSecurityGroups(ArrayList<SecurityGroup> securityGroups) {
        this.securityGroups = securityGroups;
    }

    public final Integer[] getGroupIds() {
        return groupIds;
    }

    public final void setGroupIds(Integer[] groupIds) {
        this.groupIds = groupIds;
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            setUserId(AbstractDAO.getInteger(rs, "user_id"));
            setNetworkLoginNm(rs.getString("network_login_nm"));
            setPasswordVal(rs.getString("password_val"));
            setPasswordExpDt(rs.getTimestamp("password_exp_dt"));
            setManagerId(AbstractDAO.getInteger(rs, "manager_id"));
            setActiveInd(rs.getString("active_ind"));
            setLastModified(AbstractDAO.getInteger(rs, "user_def_lm"));
            setUserFirstNm(rs.getString("first_nm"));
            setUserLastNm(rs.getString("last_nm"));
            setPositionNumber(0 == rs.getInt("position_number")? null : rs.getInt("position_number"));

            Contact tempContact = new Contact();
            tempContact.populate(rs);

            setContact(tempContact);

            securityGroups = new ArrayList<SecurityGroup>();
            Integer securityGroupId = AbstractDAO.getInteger(rs, "security_group_id");
            
            // securityGroupId will be set to zero if we don't want to retrieve this information
            if (securityGroupId != null && securityGroupId >= 0) {
	            do {
	                if (securityGroupId != null) {
	                    SecurityGroup tempSecurityGroup = new SecurityGroup();
	
	                    tempSecurityGroup.populate(rs);
	                    securityGroups.add(tempSecurityGroup);
	                }
	            } while (rs.next());
            }
		} catch (SQLException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e.getMessage());
			}
		}
    }
    
    public final String getNameOnly() {
		if (userLastNm == null || userLastNm.equals("")) {
			return "";
		}
		String name = userLastNm;
		if (userFirstNm != null) {
			name += ", " + userFirstNm;
		}
		return name;
	}
}
