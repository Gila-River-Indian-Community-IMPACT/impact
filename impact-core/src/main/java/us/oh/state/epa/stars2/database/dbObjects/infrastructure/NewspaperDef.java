
package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class NewspaperDef extends BaseDB {

    private String _countyCd;
    private String _newspaperName;
    private String _faxNumber;
    private String _phoneNumber;
    private String _contactPerson;
    private String _emailAddress;

    public final void populate(ResultSet rs) {

        try {
            setCountyCd(rs.getString("county_cd"));
            setNewspaperName(rs.getString("newspaper_nm"));
            setFaxNumber(rs.getString("fax_number"));
            setPhoneNumber(rs.getString("phone_number"));
            setContactPerson(rs.getString("contact_person"));
            setEmailAddress(rs.getString("email_address"));
            setLastModified(AbstractDAO.getInteger(rs, "newspaper_lm"));
            setDirty(false);
        }
        catch (SQLException sqle) {
            logger.warn("Required field error: " + sqle.getMessage(), sqle);
        }

    }

    public String getCountyCd() {
        return _countyCd;
    }

    public void setCountyCd(String countyCd) {
        _countyCd = countyCd;
        setDirty(true);
    }

    public String getNewspaperName() {
        return _newspaperName;
    }

    public void setNewspaperName(String newspaperName) {
        _newspaperName = newspaperName;
        setDirty(true);
    }

    public String getFaxNumber() {
        return _faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        _faxNumber = faxNumber;
        setDirty(true);
    }

    public String getPhoneNumber() {
        return _phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        _phoneNumber = phoneNumber;
        setDirty(true);
    }

    public String getContactPerson() {
        return _contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        _contactPerson = contactPerson;
        setDirty(true);
    }

    public String getEmailAddress() {
        return _emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        _emailAddress = emailAddress;
        setDirty(true);
    }

}
