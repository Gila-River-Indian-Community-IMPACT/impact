package us.wy.state.deq.impact.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EmissionUnitTypeDef extends BaseDB {
	
	private static final long serialVersionUID = -8438004160374622437L;
	
	// ****************** Variables *******************
	private String EmissionUnitTypeCd;
	private boolean Deprecated;
	private String EmissionUnitTypeName;
	private String EmissionUnitTypeDesc;
	private int LastModifed;

	// Add new EU types in alphabetical order!
	public static final String ABS = "ABS";
	public static final String ACB = "ACB";
	public static final String AMN = "AMN";
	public static final String APT = "APT";
	public static final String BAK = "BAK";
	public static final String BGM = "BGM";
	public static final String BOL = "BOL";
	public static final String BVC = "BVC";
	public static final String CCU = "CCU";
	public static final String CKD = "CKD";
	public static final String CMX = "CMX";
	public static final String COT = "COT";
	public static final String CSH = "CSH";
	public static final String CTW = "CTW";
	public static final String DHY = "DHY";
	public static final String DIS = "DIS";
	public static final String DRY = "DRY";
	public static final String EGU = "EGU";
	public static final String ENG = "ENG";
	public static final String FAT = "FAT";
	public static final String FLR = "FLR";
	public static final String FUG = "FUG";
	public static final String GIN = "GIN";
	public static final String GRI = "GRI";
	public static final String HET = "HET";
	public static final String HMA = "HMA";
	public static final String INC = "INC";
	public static final String LUD = "LUD";
	public static final String MAC = "MAC";
	public static final String MAT = "MAT";
	public static final String MET = "MET";
	public static final String MIL = "MIL";
	public static final String MIX = "MIX";
	public static final String MLD = "MLD";
	public static final String OEP = "OEP";
	public static final String ORD = "ORD";
	public static final String OZG = "OZG";
	public static final String PAM = "PAM";
	public static final String PEL = "PEL";	
	public static final String PNE = "PNE";
	public static final String PRN = "PRN";
	public static final String SEB = "SEB";
	public static final String REM = "REM";
	public static final String RES = "RES";
	public static final String SEM = "SEM";
	public static final String SEP = "SEP";
	public static final String SRU = "SRU";
	public static final String STZ = "STZ";
	public static final String SVC = "SVC";	
	public static final String SVU = "SVU";
	public static final String TAR = "TAR";
	public static final String TGT = "TGT";
	public static final String TIM = "TIM";
	public static final String TKO = "TKO";
	public static final String TNK = "TNK";
	public static final String VNT = "VNT";
	public static final String WEL = "WEL";
	public static final String WWE = "WWE";

	private static final String defName = "EmissionUnitTypeDef";
	
	// ****************** Properties *******************
	public String getEmissionUnitTypeCd() {
		return EmissionUnitTypeCd;
	}

	public void setEmissionUnitTypeCd(String emissionUnitTypeCd) {
		EmissionUnitTypeCd = emissionUnitTypeCd;
	}

	public boolean isDeprecated() {
		return Deprecated;
	}

	public void setDeprecated(boolean deprecated) {
		Deprecated = deprecated;
	}

	public String getEmissionUnitTypeName() {
		return EmissionUnitTypeName;
	}

	public void setEmissionUnitTypeName(String emissionUnitTypeName) {
		EmissionUnitTypeName = emissionUnitTypeName;
	}

	public String getEmissionUnitTypeDesc() {
		return EmissionUnitTypeDesc;
	}

	public void setEmissionUnitTypeDesc(String emissionUnitTypeDesc) {
		EmissionUnitTypeDesc = emissionUnitTypeDesc;
	}
	
	public int getLastModifed() {
		return LastModifed;
	}

	public void setLastModifed(int lastModifed) {
		LastModifed = lastModifed;
	}

	// ****************** Public Static Methods *******************
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("FP_EMISSION_TYPE_DEF", "EU_TYPE_CD",
					"EU_TYPE_NAME", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

	// ****************** Implement Abstract class - BaseDB *******************
	@Override
	public void populate(ResultSet rs) throws SQLException {
		setEmissionUnitTypeCd(rs.getString("eu_type_cd"));
		setEmissionUnitTypeName(rs.getString("eu_type_name"));
		setEmissionUnitTypeDesc(rs.getString("eu_type_desc"));
		setDeprecated(AbstractDAO.translateIndicatorToBoolean(rs
				.getString("deprecated")));
		setLastModifed(AbstractDAO.getInteger(rs, "last_modified"));

	}

}