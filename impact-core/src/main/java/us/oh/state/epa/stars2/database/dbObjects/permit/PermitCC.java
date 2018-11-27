package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class PermitCC extends BaseDB {

    private Integer _permitCCID;
    private Integer _permitID;
    private String _name;
    private String _address1;
    private String _address2;
    private String _city;
    private String _state;
    private String _zipCode;

    public PermitCC() {

        this.requiredField(_name, "name");
        this.requiredField(_address1, "address1");
        this.requiredField(_city, "city");
        this.requiredField(_state, "state");
        this.requiredField(_zipCode, "zipCode");
        setDirty(false);

    }

    /* (non-Javadoc)
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDB#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PermitCC))
            return false;
        else
            return getPermitCCId().equals(((PermitCC)obj).getPermitCCId());
    }

    public PermitCC(PermitCC old) {

        super(old);

        setPermitCCId(old.getPermitCCId());
        setPermitId(old.getPermitId());
        setName(old.getName());
        setAddress1(old.getAddress1());
        setAddress2(old.getAddress2());
        setCity(old.getCity());
        setState(old.getState());
        setZipCode(old.getZipCode());

        setLastModified(old.getLastModified());
        setDirty(old.isDirty());
    }

    public void populate(ResultSet rs) {

        try {
            setPermitCCId(AbstractDAO.getInteger(rs, "permit_cc_id"));
            setPermitId(AbstractDAO.getInteger(rs, "permit_id"));
            setName(rs.getString("name"));
            setAddress1(rs.getString("address1"));
            setAddress2(rs.getString("address2"));
            setCity(rs.getString("city"));
            setState(rs.getString("state"));
            setZipCode(rs.getString("zip"));

            setLastModified(AbstractDAO.getInteger(rs, "pcc_lm"));
            setDirty(false);
        }
        catch (SQLException sqle) {
            logger.error("Required field error");
        }

    }

    public final Integer getPermitCCId() {
        return _permitCCID;
    }

    public final void setPermitCCId(Integer permitCCID) {
        _permitCCID = permitCCID;
    }

    public final Integer getPermitId() {
        return _permitID;
    }

    public final void setPermitId(Integer permitID) {
        _permitID = permitID;
    }

    public final String getName() {
        return _name;
    }

    public final void setName(String name) {
        _name = name;
        this.requiredField(_name, "name");
        setDirty(true);
    }

    public final String getAddress1() {
        return _address1;
    }

    public final void setAddress1(String address) {
        _address1 = address;
        this.requiredField(_address1, "address1");
        setDirty(true);
    }

    public final String getAddress2() {
        return _address2;
    }

    public final void setAddress2(String address) {
        _address2 = address;
        setDirty(true);
    }

    public final String getCity() {
        return _city;
    }

    public final void setCity(String city) {
        _city = city;
        this.requiredField(_city, "city");
        setDirty(true);
    }

    public final String getState() {
        return _state;
    }

    public final void setState(String state) {
        _state = state;
        this.requiredField(_state, "state");
        setDirty(true);
    }

    public final String getZipCode() {
        return _zipCode;
    }

    public final void setZipCode(String zipCode) {
        _zipCode = zipCode;
        this.requiredField(_zipCode, "zipcode");
        setDirty(true);
    }
}
