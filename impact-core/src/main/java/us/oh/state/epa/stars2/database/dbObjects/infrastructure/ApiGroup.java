package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.util.Utility;

public class ApiGroup  extends BaseDB {
	private static final long serialVersionUID = 3998650591573671099L;
	
	private int apiCd;
	private int fpId;
	private String apiNo;
		
	private final boolean isValidApiNo(String apiNo){
		if (Utility.tryParseInt(apiNo) == null) {
			return false;
		}
		return apiNo.length() == 6 || apiNo.length() == 7;
	}
	
	@Override
	public ValidationMessage[] validate(){
		ArrayList<ValidationMessage> errors = new ArrayList<ValidationMessage>();

		if	(Utility.isNullOrEmpty(apiNo) || !isValidApiNo(this.apiNo)){
			errors.add(new ValidationMessage("apiNo", "Invalid API Number. API Number must be 6 or 7 digits"));
		}
		
		return errors.toArray(new ValidationMessage[0]);
	}

	public int getApiCd() {
		return apiCd;
	}

	public void setApiCd(int apiCd) {
		this.apiCd = apiCd;
	}

	public int getFpId() {
		return fpId;
	}

	public void setFpId(int fpId) {
		this.fpId = fpId;
	}

	public String getApiNo() {
		return apiNo;
	}

	public void setApiNo(String apiNo) {
		this.apiNo = apiNo.trim();
	}
	
	@Override
	public void populate(ResultSet rs) throws SQLException {
		try {
			setApiCd(rs.getInt("api_cd"));
            setFpId(rs.getInt("fp_id"));
            setApiNo(rs.getString("api_no"));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }	
	}
	
	public ApiGroup copy() {
		ApiGroup api = new ApiGroup();
		
		api.setApiCd(this.apiCd);
		api.setApiNo(this.apiNo);
		api.setFpId(this.fpId);
		
		return api;
	}
	
	public String getWOGCCAPIUrl() {

		String urlPart = SystemPropertyDef.getSystemPropertyValue("WOGCC_API_URL_PART", null);

		if (!Utility.isNullOrEmpty(urlPart)) {
			urlPart += this.apiNo;
		}

		return urlPart;
	}
	
}
