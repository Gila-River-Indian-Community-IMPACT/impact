package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;

/**
 * @author Kbradley Note: (TO DO LATER) some attributes can go to base later.
 */
public class ContactHistList extends FacilityHistList {
    private String contactTypeCd;
    private String contactName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String companyName;
    private String middleName;
    private Integer contactId;

    public ContactHistList() {
    }

    public final String getContactName() {
        return contactName;
    }

    public final void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public final String getContactTypeCd() {
        return contactTypeCd;
    }

    public final void setContactTypeCd(String contactTypeCd) {
        this.contactTypeCd = contactTypeCd;
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

    public final Integer getContactId() {
        return contactId;
    }

    public final void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            super.populate(rs);
            setContactTypeCd(rs.getString("contact_type_cd"));
            setFirstName(rs.getString("first_nm"));
            setLastName(rs.getString("last_nm"));
            setPhoneNumber(rs.getString("phone_no"));
            setCompanyName(null);     // obsolete column
            setMiddleName(rs.getString("middle_nm"));
            if (lastName == null || lastName.equals("")) {
            	setContactName(companyName == null ? "NO-NAME" : companyName);
        	} else {
        		String name = lastName;
        		if (firstName != null) {
        			name += ", " + firstName;
        			if (middleName != null) {
        				name += " " + middleName;
        			}
        		}
        		setContactName(name);
        	}
            setContactId(AbstractDAO.getInteger(rs, "contact_id"));

        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
