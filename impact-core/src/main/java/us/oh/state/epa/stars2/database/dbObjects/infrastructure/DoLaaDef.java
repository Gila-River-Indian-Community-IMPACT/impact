package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

@SuppressWarnings("serial")
public class DoLaaDef extends SimpleDef {
    private static final String defName = "DoLaaDef";
    private String _doLaaID;
    private String _doLaaShortDsc;
    private String _addressLine1;
    private String _addressLine2;
    private String _addressLine3;
    private String _phoneNumber;
  
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrieveDoLaas", DoLaaDef.class);
            data.addExcludedKey("Lake");
            data.addExcludedKey("MTAPC");
            cfgMgr.addDef(defName, data);
        }
        return data;
    }

    public final void populate(ResultSet rs) {
        super.populate(rs);

        try {
            setDoLaaID(rs.getString("do_laa_id"));
            setDoLaaShortDsc(rs.getString("do_laa_short_dsc"));
            setAddressLine1(rs.getString("address1"));
            setAddressLine2(rs.getString("address2"));
            setAddressLine3(rs.getString("address3"));
            setPhoneNumber(rs.getString("phone"));
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }

    }

    public final String getDoLaaID() {
        return _doLaaID;
    }

    public final void setDoLaaID(String doLaaID) {
        _doLaaID = doLaaID;
    }

    public final String getAddressLine1() {
        return _addressLine1;
    }

    public final void setAddressLine1(String address) {
        _addressLine1 = address;
    }

    public final String getAddressLine2() {
        return _addressLine2;
    }

    public final void setAddressLine2(String address) {
        _addressLine2 = address;
    }

    public final String getAddressLine3() {
        return _addressLine3;
    }

    public final void setAddressLine3(String address) {
        _addressLine3 = address;
    }

    public final String getPhoneNumber() {
        return _phoneNumber;
    }

    public final void setPhoneNumber(String phone) {
        _phoneNumber = phone;
    }

    public String getDoLaaShortDsc() {
        return _doLaaShortDsc;
    }

    public void setDoLaaShortDsc(String doLaaShortDsc) {
        _doLaaShortDsc = doLaaShortDsc;
    }
}
