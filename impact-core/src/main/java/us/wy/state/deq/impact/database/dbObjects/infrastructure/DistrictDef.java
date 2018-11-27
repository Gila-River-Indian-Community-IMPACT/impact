package us.wy.state.deq.impact.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class DistrictDef extends BaseDB {
	
	private static final long serialVersionUID = 1460603677384143376L;
	private String districtId;
    private String districtCd;
    private String districtName;
    
    public final void populate(ResultSet rs) {
        try {
            setDistrictCd(rs.getString("do_laa_cd"));
            setDistrictName(rs.getString("do_laa_dsc"));
            setDistrictId(rs.getString("do_laa_id"));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }

	public String getDistrictId() {
		return districtId;
	}

	public void setDistrictId(String districtId) {
		this.districtId = districtId;
	}

	public String getDistrictCd() {
		return districtCd;
	}

	public void setDistrictCd(String districtCd) {
		this.districtCd = districtCd;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
}