package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;

@SuppressWarnings("serial")
public class ValidationControl extends SimpleDef {
	
	// data items (besides code, desc, last modified and deprecated)
	private boolean reqAdminPermitMod;
	private boolean pbrNotif;
	private boolean tvPtoPermitApp;
	private boolean tvPtiPtioPermitApp;
	private boolean tivAcidRain;
	private boolean demEgPntModeling;
	private boolean psdScreenModeling;
//	private boolean ntvEr;
	private boolean smtvTvEr;
	private boolean erEis;
	private boolean erThreasholdExceeded;
	private boolean stackTest;
	private boolean cemsComsRataReport;
	private boolean facilityCheck;
	private static final String defName = "ValidationControl";

	// public final static String FAC_LAT_LONG = "facilLatLong";
	public final static String FAC_NCIS = "facilNaics";
	// public final static String CORE_PLACE = "faciCorePlace";
	public final static String FAC_BASIC = "facilBasic";
	// public final static String PRIM_CONT = "facilPrimCont";
	public final static String BILL_CONT = "facilBillCont";
	public final static String ONSITE_CONT = "facilOnSiteCont";
	// public final static String OWNER_CONT = "facilOwnerCont";
	public final static String OFFICAL_CONT = "facilOfficialCont"; // Responsible
																	// Official
	// public final static String FAC_EMAIL = "facilEmail";
	public final static String EU_BASIC = "euBasic";
	public final static String EU_USER_ID = "euUserId";
	public final static String EU_PROCS = "euProcesses";
	// public final static String EU_CAP = "euCap";
	public final static String EU_OP = "euOpStat";
	// public final static String EU_ORIS = "euOris";
	public final static String PROC_SCC = "procScc";
	public final static String PROC_DESC = "procDesc";
	public final static String CE_BASIC = "ceBasic";
	public final static String CE_ADDNL = "ceAdditional";
	public final static String CE_GAS = "ceGasFlowTemp";
	public final static String CE_INIT_INSTALL = "ceInitInstallDate";
	public final static String STACK_REL_HEIGHT = "stackRelHeight";
	//public final static String STACK_BUILDING = "stackBuildDim";
	//public final static String STACK_SHAPE = "stackShape";
	//public final static String STACK_XSECT = "stackXSection";
	public final static String STACK_DIAM = "stackDiameter";
	//public final static String STACK_FENCE = "stackFenceDist";
	//public final static String STACK_FLOW_TEMP = "stackFlowTemp";
	// public final static String FUGITIVE_AREA_VOLUMN = "fugitiveEpAreaVol";
	// public final static String FUGITIVE_PLUME = "fugitivePlumeTemp";
	// public final static String FUGITIVE_LAT_LONG = "FugitiveLatLong";
	// public final static String FUGIIVE_LAT_LONG_TECH = "FugitiveLatLongTech";
	// public final static String FUGIIVE_USER_DESC = "FugitiveUserDesc";
	// public final static String TV_EU_CLASS = "tvEuClass";
	public final static String FAC_LOC = "facilLocation";
	public final static String FAC_API = "facilApi";
	public final static String FAC_AFS = "facilAfs";
	public final static String FAC_OWNER = "facilOwner";
	public final static String ENV_CONT = "facilEnvCont";
	public final static String FAC_FED_RULE = "facilFedRules";
	public final static String FAC_OP_STATUS = "facilOpStat";
	public final static String EU_PTE = "euPte";
	public final static String EU_ADD = "euAdditional";
	public final static String EU_REP = "euSerialTracking";
	public final static String RP_BASIC = "rpBasic";
	public final static String RP_ADD = "rpAdditional";
	public final static String RP_OP_STATUS = "rpOpStat";
	public final static String RP_FUG_LOC = "rpFugLatLong";
	public final static String RP_NON_FUG_LOC = "rpHorizVertLatLong";
	public final static String RP_RELEASE_HEIGHT = "rpReleaseHeight";
	public final static String RP_CEM = "rpCem";
	public final static String EU_ENG_ST = "euEngineTracking";
	public final static String NSR_BILL_CONT = "facilNSRBillCont";

	public ValidationControl() {
		super();
		// set all flags to false
	}

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("InfrastructureSQL.retrieveValidationControl",
					ValidationControl.class);

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

	public void populate(ResultSet rs) {
		super.populate(rs);
		try {
			setReqAdminPermitMod(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("req_Admin_Permit_Mod")));
			setTvPtoPermitApp(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("Tv_Pto_Permit_App")));
			setTvPtiPtioPermitApp(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("Tv_Pti_Ptio_Permit_App")));
			setTivAcidRain(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("Tiv_Acid_Rain")));
			setDemEgPntModeling(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("DemEgPnt_Modeling")));
			setPsdScreenModeling(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("Psd_Screen_Modeling")));
			/*setNtvEr(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("Ntv_Er")));*/
			setSmtvTvEr(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("Smtv_Tv_Er")));
			setErEis(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("Er_Eis")));
			setErThreasholdExceeded(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("Er_Threashold_Exceeded")));
			setStackTest(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("Stack_Test")));
			setCemsComsRataReport(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("CEMS_COMS_RATA_REPORT")));
			setFacilityCheck(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("facility_check")));
		} catch (SQLException sqle) {
			logger.error("Error populating ValidationControl: "
					+ sqle.getMessage());
		}

	}

	public static ValidationControl getValidationControl(String cd)
			throws ApplicationException {
		ValidationControl vc = (ValidationControl) ValidationControl.getData()
				.getItems().getItem(cd);
		if (null == vc) {
			throw new ApplicationException("validationTag \"" + cd
					+ "\" not found)");
		}
		return vc;
	}

	public static boolean check(String cd, ValidationControl vc, Logger logger) {
		
		try {

			ValidationControl vc2 = getValidationControl(cd);
			if (vc.isReqAdminPermitMod() && vc2.isReqAdminPermitMod())
				return true;
			if (vc.isTvPtoPermitApp() && vc2.isTvPtoPermitApp())
				return true;
			if (vc.isTvPtiPtioPermitApp() && vc2.isTvPtiPtioPermitApp())
				return true;
			if (vc.isTivAcidRain() && vc2.isTivAcidRain())
				return true;
			if (vc.isDemEgPntModeling() && vc2.isDemEgPntModeling())
				return true;
			if (vc.isPsdScreenModeling() && vc2.isPsdScreenModeling())
				return true;
			/*if (vc.isNtvEr() && vc2.isNtvEr())
				return true;*/
			if (vc.isSmtvTvEr() && vc2.isSmtvTvEr())
				return true;
			if (vc.isErEis() && vc2.isErEis())
				return true;
			if (vc.isErThreasholdExceeded() && vc2.isErThreasholdExceeded())
				return true;
			if (vc.isStackTest() && vc2.isStackTest())
				return true;
			if (vc.isCemsComsRataReport() && vc2.isCemsComsRataReport())
				return true;
			if (vc.isFacilityCheck() && vc2.isFacilityCheck())
				return true;
			
		} catch (ApplicationException ae) {
			logger.error("Did not find ValidationControl tag", ae);
			// default to no checking.
		}
		return false;
	}

	public boolean isDemEgPntModeling() {
		return demEgPntModeling;
	}

	public void setDemEgPntModeling(boolean demEgPntModeling) {
		this.demEgPntModeling = demEgPntModeling;
	}

	public boolean isErEis() {
		return erEis;
	}

	public void setErEis(boolean erEis) {
		this.erEis = erEis;
	}

	public boolean isErThreasholdExceeded() {
		return erThreasholdExceeded;
	}

	public void setErThreasholdExceeded(boolean erThreasholdExceeded) {
		this.erThreasholdExceeded = erThreasholdExceeded;
	}

	/*public boolean isNtvEr() {
		return ntvEr;
	}

	public void setNtvEr(boolean ntvEr) {
		this.ntvEr = ntvEr;
	}*/

	public boolean isPbrNotif() {
		return pbrNotif;
	}

	public void setPbrNotif(boolean pbrNotif) {
		this.pbrNotif = pbrNotif;
	}

	public boolean isPsdScreenModeling() {
		return psdScreenModeling;
	}

	public void setPsdScreenModeling(boolean psdScreenModeling) {
		this.psdScreenModeling = psdScreenModeling;
	}

	public boolean isReqAdminPermitMod() {
		return reqAdminPermitMod;
	}

	public void setReqAdminPermitMod(boolean reqAdminPermitMod) {
		this.reqAdminPermitMod = reqAdminPermitMod;
	}

	public boolean isSmtvTvEr() {
		return smtvTvEr;
	}

	public void setSmtvTvEr(boolean smtvTvEr) {
		this.smtvTvEr = smtvTvEr;
	}

	public boolean isTivAcidRain() {
		return tivAcidRain;
	}

	public void setTivAcidRain(boolean tivAcidRain) {
		this.tivAcidRain = tivAcidRain;
	}

	public boolean isTvPtiPtioPermitApp() {
		return tvPtiPtioPermitApp;
	}

	public void setTvPtiPtioPermitApp(boolean tvPtiPtioPermitApp) {
		this.tvPtiPtioPermitApp = tvPtiPtioPermitApp;
	}

	public boolean isTvPtoPermitApp() {
		return tvPtoPermitApp;
	}

	public void setTvPtoPermitApp(boolean tvPtoPermitApp) {
		this.tvPtoPermitApp = tvPtoPermitApp;
	}
	
	public boolean isStackTest() {
		return stackTest;
	}

	public void setStackTest(boolean stacktest) {
		this.stackTest = stacktest;
	}

	public boolean isCemsComsRataReport() {
		return cemsComsRataReport;
	}

	public void setCemsComsRataReport(boolean cemsComsRataReport) {
		this.cemsComsRataReport = cemsComsRataReport;
	}
	
	public boolean isFacilityCheck() {
		return facilityCheck;
	}
	
	public void setFacilityCheck(boolean facilityCheck) {
		this.facilityCheck = facilityCheck;
	}

}
