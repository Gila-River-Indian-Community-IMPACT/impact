package us.oh.state.epa.stars2.webcommon.bean;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import us.oh.state.epa.aport.admin.domain.User;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountryDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountyDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccLevel;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SecurityGroup;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.StateDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.ContEquipTypeDef;
import us.oh.state.epa.stars2.def.ContactTitle;
import us.oh.state.epa.stars2.def.ContactTypeDef;
import us.oh.state.epa.stars2.def.Country;
import us.oh.state.epa.stars2.def.County;
import us.oh.state.epa.stars2.def.DOLAA;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.FieldAuditLogAttributeDef;
import us.oh.state.epa.stars2.def.FieldAuditLogCategoryDef;
import us.oh.state.epa.stars2.def.GeoPolygonDef;
import us.oh.state.epa.stars2.def.MonitoringContractorDef;
import us.oh.state.epa.stars2.def.NAICSDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.PTIOMACTSubpartDef;
import us.oh.state.epa.stars2.def.PTIONESHAPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIONSPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIOPERDueDateDef;
import us.oh.state.epa.stars2.def.PTIORegulatoryStatus;
import us.oh.state.epa.stars2.def.PTIRegulatoryStatus;
import us.oh.state.epa.stars2.def.RUMCategoryDef;
import us.oh.state.epa.stars2.def.RUMDispositionDef;
import us.oh.state.epa.stars2.def.RUMReasonDef;
import us.oh.state.epa.stars2.def.RelocationDispositionDef;
import us.oh.state.epa.stars2.def.RelocationJFODef;
import us.oh.state.epa.stars2.def.ReportOfEmissionsStateDef;
import us.oh.state.epa.stars2.def.SICDef;
import us.oh.state.epa.stars2.def.State;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.def.TVClassification;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.userAuth.UserAttributes;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.converter.PhoneNumberConverter;
import us.wy.state.deq.impact.bo.CompanyService;
import us.wy.state.deq.impact.database.dbObjects.company.Company;
import us.wy.state.deq.impact.database.dbObjects.infrastructure.DistrictDef;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;
import us.wy.state.deq.impact.def.IndianReservationDef;

public class InfrastructureDefs extends AppBase {
	
	private static final long serialVersionUID = -3170379767190209360L;
	private static Logger logger = Logger.getLogger(InfrastructureDefs.class);

	public static final int AMBIENT_MONITOR_REPORT_UPLOADER_ROLE_ID = 13;
	
	private String[] quarterList = { "NE", "NW", "SE", "SW" };
	private LinkedHashMap<String, String> quarterQuarters;
	private LinkedHashMap<String, String> quarters;
	private LinkedHashMap<String, String> states;
	private LinkedHashMap<String, String> districts;
	private LinkedHashMap<String, String> phyStates;
	private LinkedHashMap<String, String> counties;
	private LinkedHashMap<String, String> cities;
	private LinkedHashMap<String, String> countries;
	private LinkedHashMap<String, Integer> years;
	private LinkedHashMap<String, Integer> eisYears;
	private LinkedHashMap<String, Integer> eis2Years;
	private LinkedHashMap<String, Integer> oddYears;

	private LinkedHashMap<String, Integer> yearQuarters;
	private LinkedHashMap<String, String> reportPollutants;
	private LinkedHashMap<String, String> contEquipAirFlows;
	private LinkedHashMap<String, String> sccLevel1Codes;
	private LinkedHashMap<String, String> contactTitles;
	private LinkedHashMap<String, Integer> companies;
	private LinkedHashMap<String, String> geoPolygons;

	private long lastSicUpdate;
	private ArrayList<SelectItem> formattedSicSelectCodes;
	private long lastNaicsUpdate;
	private ArrayList<SelectItem> formattedNaicsSelectCodes;
	private ArrayList<SelectItem> formattedNaicsSelectAllCodes;
	
	private String deqName;
	private String maxLatitude;
	private String minLatitude;
	private String maxLongitude;
	private String minLongitude;

	// Poshan: Just put this converter here for now. later move to other back-end bean.
	private PhoneNumberConverter phoneNumberConverter = new PhoneNumberConverter();
	private String js = "window.history.forward(1);";
	
	private CompanyService companyService;
	
	private InfrastructureService infrastructureService;

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	public CompanyService getCompanyService() {
		return companyService;
	}

	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}

	public final PhoneNumberConverter getPhoneNumberConverter() {
		return phoneNumberConverter;
	}

	public String getLoginSuccessTarget() {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + 
				"/facilitySelector.jsf";
	}
	
	public final String getJs() {
		String ret = js;
		js = "window.history.forward(1);";
		DisplayUtil du = (DisplayUtil) FacesUtil
				.getManagedBean(DisplayUtil.DISPLAY_UTIL_SESSION_ATTRIBUTE);
		du.getQueuedMessages();
		return ret;
	}

	public final String getIframeResize() {
		String ret = "";
		if (!isInternalApp()) {
			ret = "parent.adjustIFrameSize(document.getElementById('body').scrollHeight,document.getElementById('body').scrollWidth,'dapcapp');";
		}

		return ret;
	}

	public final String getIframeReload() {
		String ret = "";
		if (!isInternalApp()) {
			ret = "parent.adjustIFrameSize(document.getElementById('body').scrollHeight,1200,'dapcapp');";
		}

		return ret;
	}

	public final String getSizeAlert() {
		String ret = "";
		if (!isInternalApp()) {
			ret = "alert('Width = ' + document.getElementById('body').scrollWidth +"
					+ "', Height = ' + document.getElementById('body').scrollHeight); ";
		}
		return ret;
	}

	public final void setJs(String js) {
		this.js = this.js + js;
	}

	public final void init() {
	}

	public final LinkedHashMap<String, String> getQuarterQuarters() {
		return getQuarterItems(this.quarterQuarters);
	}

	public final LinkedHashMap<String, String> getQuarters() {
		return getQuarterItems(this.quarters);
	}

	private final LinkedHashMap<String, String> getQuarterItems(
			LinkedHashMap<String, String> source) {
		if (source != null) {
			return source;
		}

		source = generateQuarterItems();

		return source;
	}

	private final LinkedHashMap<String, String> generateQuarterItems() {
		LinkedHashMap<String, String> source = new LinkedHashMap<String, String>();
		for (String item : this.quarterList) {
			source.put(item, item);
		}

		return source;
	}

	public final LinkedHashMap<String, String> getStates() {
		if (states == null || State.getData().isRefresh()) {
			init();

			try {
				StateDef[] tempArray = getInfrastructureService().retrieveStates();

				states = new LinkedHashMap<String, String>();
				for (StateDef tempState : tempArray) {
					states.put(tempState.getStateNm(), tempState.getStateCd());
				}
			} catch (RemoteException re) {
				logger.error(re.getMessage(), re);
				DisplayUtil
						.displayError("System error. Please contact system administrator");
			}

			// Reset sync timer
			State.getData().setRefresh(false);
		}
		return states;
	}

	public final LinkedHashMap<String, String> getDistricts() {
		if (districts == null || DOLAA.getData().isRefresh()) {
			init();

			try {
				DistrictDef[] tempArray = getInfrastructureService()
						.retrieveDistricts();

				districts = new LinkedHashMap<String, String>();
				for (DistrictDef tempState : tempArray) {
					districts.put(tempState.getDistrictName(),
							tempState.getDistrictCd());
				}
			} catch (RemoteException re) {
				logger.error(re.getMessage(), re);
				DisplayUtil
						.displayError("System error. Please contact system administrator");
			}

			// Reset sync timer
			DOLAA.getData().setRefresh(false);
		}
		return districts;
	}

	public final LinkedHashMap<String, String> getPhyStates() {
		if (phyStates == null) {
			init();

			try {
				StateDef[] tempArray = getInfrastructureService().retrieveStates();

				phyStates = new LinkedHashMap<String, String>();
				for (StateDef tempState : tempArray) {
					if (tempState.getCountryCd().equals(Country.US)) {
						phyStates.put(tempState.getStateNm(),
								tempState.getStateCd());
					}
				}
			} catch (RemoteException re) {
				logger.error(re.getMessage(), re);
				DisplayUtil
						.displayError("System error. Please contact system administrator");
			}
		}
		return phyStates;
	}

	public final LinkedHashMap<String, String> getCounties() {
		if (counties == null || County.getData().isRefresh()) {
			init();

			try {
				CountyDef[] tempArray = getInfrastructureService().retrieveCounties();

				counties = new LinkedHashMap<String, String>();
				for (CountyDef temp : tempArray) {
					counties.put(temp.getCountyNm(), temp.getCountyCd());
				}
			} catch (RemoteException re) {
				logger.error(re.getMessage(), re);
				DisplayUtil
						.displayError("System error. Please contact system administrator");
			}

			// Reset sync timer
			County.getData().setRefresh(false);
		}
		return counties;
	}
	
	public final LinkedHashMap<String, String> getCities() {
//		if (cities == null || City.getData().isRefresh()) {
//			init();
//
//			try {
//				CityDef[] tempArray = getInfrastructureService().retrieveCities();
//
//				cities = new LinkedHashMap<String, String>();
//				for (CityDef temp : tempArray) {
//					cities.put(temp.getCityNm(), temp.getCityCd());
//				}
//			} catch (RemoteException re) {
//				logger.error(re.getMessage(), re);
//				DisplayUtil
//						.displayError("System error. Please contact system administrator");
//			}
//
//			// Reset sync timer
//			City.getData().setRefresh(false);
//		}
		return cities;
	}

	public final LinkedHashMap<String, String> getCountries() {
		if (countries == null || Country.getData().isRefresh()) {
			init();

			try {
				CountryDef[] tempArray = getInfrastructureService().retrieveCountries();

				countries = new LinkedHashMap<String, String>();
				for (CountryDef temp : tempArray) {
					countries.put(temp.getCountryNm(), temp.getCountryCd());
				}
			} catch (RemoteException re) {
				logger.error(re.getMessage(), re);
				DisplayUtil
						.displayError("System error. Please contact system administrator");
			}

			// Reset sync timer
			Country.getData().setRefresh(false);
		}
		return countries;
	}

	public LinkedHashMap<String, String> getGeoPolygons() {
		if (geoPolygons == null || GeoPolygonDef.getData().isRefresh()) {
			init();

			try {
				GeoPolygonDef[] tempArray = getInfrastructureService().retrieveGeoPolygonDefs();

				geoPolygons = new LinkedHashMap<String, String>();
				for (GeoPolygonDef temp : tempArray) {
					geoPolygons.put(temp.getLabel(), temp.getCode());
				}
			} catch (RemoteException re) {
				logger.error(re.getMessage(), re);
				DisplayUtil
						.displayError("System error. Please contact system administrator");
			}

			// Reset sync timer
			GeoPolygonDef.getData().setRefresh(false);
		}
		return geoPolygons;
	}

    public final DefSelectItems getBasicUsersDef() {
        return BasicUsersDef.getData().getItems();
    }	
	
	// public final LinkedHashMap<String, Integer> getUsers() {
	// if (users == null
	// || ((usersTS == null) || (usersTS.getTime() + 4 * 60 * 60
	// * 1000 < System.currentTimeMillis()))) {
	// init();
	//
	// try {
	// SimpleIdDef[] tempUsers = getInfrastructureService().retrieveUserList(
	// true);
	//
	// users = new LinkedHashMap<String, Integer>();
	// userList = new LinkedHashMap<Integer, String>();
	// for (SimpleIdDef tempDef : tempUsers) {
	// users.put(tempDef.getDescription(), tempDef.getId());
	// userList.put(tempDef.getId(), tempDef.getDescription());
	// }
	// usersTS = new Timestamp(System.currentTimeMillis());
	// } catch (RemoteException re) {
	// logger.error(re.getMessage(), re);
	// DisplayUtil
	// .displayError("System error. Please contact system administrator");
	// }
	// }
	// return users;
	// }
	//
	// public final String getUserNm(int userId) {
	// if (userList == null) {
	// getUsers();
	// }
	// return userList.get(new Integer(userId));
	// }
	//
	// public final LinkedHashMap<String, Integer> getAllUsers() {
	// if (userAllList == null
	// || ((usersAllTS == null) || (usersAllTS.getTime() + 4 * 60 * 60
	// * 1000 < System.currentTimeMillis()))) {
	// try {
	// SimpleIdDef[] tempUsers = getInfrastructureService().retrieveUserList(
	// false);
	// allUsers = new LinkedHashMap<String, Integer>();
	// userAllList = new LinkedHashMap<Integer, String>();
	// for (SimpleIdDef tempDef : tempUsers) {
	// allUsers.put(tempDef.getDescription(), tempDef.getId());
	// userAllList.put(tempDef.getId(), tempDef.getDescription());
	// }
	// usersAllTS = new Timestamp(System.currentTimeMillis());
	// } catch (RemoteException re) {
	// logger.error(re.getMessage(), re);
	// DisplayUtil
	// .displayError("System error. Please contact system administrator");
	// }
	// }
	// return allUsers;
	// }
	//
	// public final String getUserNmAll(int userId) {
	// if (userAllList == null) {
	// getAllUsers();
	// }
	// return userAllList.get(new Integer(userId));
	// }
	//
	// public final LinkedHashMap<Integer, String> getUserList() {
	// if (userList == null) {
	// getUsers();
	// }
	// return userList;
	// }

	public final DefSelectItems getPtioRegulatoryStatuses() {
		return PTIORegulatoryStatus.getData().getItems();
	}

	public final DefSelectItems getPtiRegulatoryStatuses() {
		return PTIRegulatoryStatus.getData().getItems();
	}

	public final DefSelectItems getPERDueDateDefs() {
		return PTIOPERDueDateDef.getData().getItems();
	}

	public final DefSelectItems getTvClassifications() {
		return TVClassification.getData().getItems();
	}

	public final ArrayList<SelectItem> getSicSelectAllDefs() {
		return getSicSelectDefs(true);
	}

	public final ArrayList<SelectItem> getSicSelectDefs() {
		return getSicSelectDefs(false);
	}

	private final ArrayList<SelectItem> getSicSelectDefs(boolean all) {
		if (formattedSicSelectCodes == null
				|| lastSicUpdate < SICDef.getData().getLastRefreshTime()) {
			String desc;
			formattedSicSelectCodes = new ArrayList<SelectItem>();
			DefSelectItems sicCodes = SICDef.getData().getItems();
			for (SelectItem item : sicCodes.getAllItems()) {
				desc = item.getValue() + "  " + item.getLabel();
				boolean b = false;
				if (!all) {
					b = item.isDisabled();
				}
				SelectItem si = new SelectItem(item.getValue(), desc, desc, b);
				formattedSicSelectCodes.add(si);
			}
			lastSicUpdate = SICDef.getData().getLastRefreshTime();
		}
		return formattedSicSelectCodes;
	}

	// public final LinkedHashMap<String, String> getSicDefs() {
	// if (formattedSicCodes == null || lastSicUpdate <
	// SICDef.getData().getLastRefreshTime()) {
	// String desc;
	// formattedSicCodes = new LinkedHashMap<String, String>();
	// DefSelectItems sicCodes = SICDef.getData().getItems();
	// for (SelectItem item : sicCodes.getCurrentItems()) {
	// desc = item.getValue() + "  " + item.getLabel();
	// formattedSicCodes.put(desc, (String) item.getValue());
	// }
	// lastSicUpdate = SICDef.getData().getLastRefreshTime();
	// }
	// return formattedSicCodes;
	// }

	// public final LinkedHashMap<String, String> getNaicsDefs() {
	// if (formattedNaicsCodes == null || lastNaicsUpdate <
	// NAICSDef.getData().getLastRefreshTime()) {
	// String desc;
	// formattedNaicsCodes = new LinkedHashMap<String, String>();
	// DefSelectItems sicCodes = NAICSDef.getData().getItems();
	// for (SelectItem item : sicCodes.getCurrentItems()) {
	// desc = item.getValue() + "  " + item.getLabel();
	// formattedNaicsCodes.put(desc, (String) item.getValue());
	// }
	// lastNaicsUpdate = NAICSDef.getData().getLastRefreshTime();
	// }
	// return formattedNaicsCodes;
	// }

	public final ArrayList<SelectItem> getNaicsSelectAllDefs() {
		getNaicsSelectDefsInternal();
		return formattedNaicsSelectAllCodes;
	}

	public final ArrayList<SelectItem> getNaicsSelectDefs() {
		getNaicsSelectDefsInternal();
		return formattedNaicsSelectCodes;
	}

	private final void getNaicsSelectDefsInternal() {
		if (formattedNaicsSelectCodes == null
				|| lastNaicsUpdate < NAICSDef.getData().getLastRefreshTime()) {
			String desc;
			formattedNaicsSelectCodes = new ArrayList<SelectItem>();
			formattedNaicsSelectAllCodes = new ArrayList<SelectItem>();
			DefSelectItems naicsCodes = NAICSDef.getData().getItems();
			for (SelectItem item : naicsCodes.getAllItems()) {
				desc = item.getValue() + "  " + item.getLabel();
				boolean b = item.isDisabled();
				SelectItem si = new SelectItem(item.getValue(), desc, desc, b);
				formattedNaicsSelectCodes.add(si);
				if (b) {
					si = new SelectItem(item.getValue(), desc, desc, false);
				}
				formattedNaicsSelectAllCodes.add(si);
			}
			lastNaicsUpdate = NAICSDef.getData().getLastRefreshTime();
		}
	}

	public final LinkedHashMap<String, Integer> getYears() {
		Integer year = Calendar.getInstance().get(Calendar.YEAR);
		String key = year.toString();
		if (years == null || null == years.get(key)) {
			years = new LinkedHashMap<String, Integer>();

			while (year >= 1993) {
				years.put(year.toString(), year--);
			}
		}
		return years;
	}

	public final LinkedHashMap<String, Integer> getYearQuarters() {
		if (yearQuarters == null) {
			yearQuarters = new LinkedHashMap<String, Integer>();

			Integer quarter = 1;

			while (quarter <= 4) {
				yearQuarters.put(quarter.toString(), quarter++);
			}
		}
		return yearQuarters;
	}

	public final LinkedHashMap<String, Integer> getEisYears() {
		Integer year = Calendar.getInstance().get(Calendar.YEAR) - 1;
		String key = year.toString();
		if (eisYears == null || null == eisYears.get(key)) {
			eisYears = new LinkedHashMap<String, Integer>();

			while (year >= 2007) {
				eisYears.put(year.toString(), year--);
			}
		}
		return eisYears;
	}

	public final LinkedHashMap<String, Integer> getEis2Years() {
		Integer year = Calendar.getInstance().get(Calendar.YEAR) - 1;
		String key = year.toString();
		if (eis2Years == null || null == eis2Years.get(key)) {
			eis2Years = new LinkedHashMap<String, Integer>();

			while (year >= 2005) {
				eis2Years.put(year.toString(), year--);
			}
		}
		return eis2Years;
	}

	public final DefSelectItems getReportingStates() {
		return ReportOfEmissionsStateDef.getData().getItems();
	}

	public final LinkedHashMap<String, String> getReportPollutants() {
		if (reportPollutants == null) {
			init();

			try {
				SimpleDef[] tempDefs = getInfrastructureService()
						.retrieveReportPollutants();

				reportPollutants = new LinkedHashMap<String, String>();
				for (SimpleDef tempDef : tempDefs) {
					reportPollutants.put(tempDef.getDescription(),
							tempDef.getCode());
				}
			} catch (RemoteException re) {
				logger.error(re.getMessage(), re);
				DisplayUtil
						.displayError("System error. Please contact system administrator");
			}
		}
		return reportPollutants;
	}

	public final DefSelectItems getContactTypes() {
		return ContactTypeDef.getData().getItems();
	}

	public final DefSelectItems getNotOwnerContactTypes() {
		return ContactTypeDef.getData().getItems();
	}

	public final HashMap<String, String> getContactNoOwnerTypes() {
		HashMap<String, String> ret = new HashMap<String, String>();
		List<SelectItem> selItems;
		List<Object> excludeCodes = new ArrayList<Object>();
		excludeCodes.add(new String("ownr"));

		selItems = ContactTypeDef.getData().getItems()
				.getItems("ownr", excludeCodes);

		for (SelectItem temp : selItems) {
			ret.put(temp.getLabel(), (String) temp.getValue());
		}

		return ret;
	}

	public final DefSelectItems getContEquipTypes() {
		return ContEquipTypeDef.getData().getItems();
	}

	public final DefSelectItems getMactSubparts() {
		return PTIOMACTSubpartDef.getData().getItems();
	}

	public final DefSelectItems getNeshapsSubparts() {
		return PTIONESHAPSSubpartDef.getData().getItems();
	}

	public final DefSelectItems getNspsSubparts() {
		return PTIONSPSSubpartDef.getData().getItems();
	}

	public final LinkedHashMap<String, String> getContEquipAirFlows() {
		if (contEquipAirFlows == null) {
			init();

			try {
				SimpleDef[] tempDefs = getInfrastructureService().retrieveSimpleDefs(
						"fp_control_equip_airflow_def",
						"control_equip_airflow_cd",
						"control_equip_airflow_dsc", null, null);

				contEquipAirFlows = new LinkedHashMap<String, String>();
				for (SimpleDef tempDef : tempDefs) {
					contEquipAirFlows.put(tempDef.getDescription(),
							tempDef.getCode());
				}
			} catch (RemoteException re) {
				logger.error(re.getMessage(), re);
				DisplayUtil
						.displayError("System error. Please contact system administrator");
			}
		}
		return contEquipAirFlows;
	}

	public final DefSelectItems getNoteTypes() {
		return NoteType.getData().getItems();
	}

	public final LinkedHashMap<String, String> getSccLevel1Codes() {
		int index;
		String sccLevel;

		init();

		try {
			SccLevel[] tempDefs = getInfrastructureService().retrieveSccLevelCodes(1,"","","","","");

			sccLevel1Codes = new LinkedHashMap<String, String>();
			for (SccLevel tempDef : tempDefs) {
				sccLevel = tempDef.getSccLevelDsc();
				if (sccLevel == null) {
					logger.error("invalid data in reference table for scc code level 1 ");
					continue;
				}
				index = sccLevel.indexOf(":");
				if (index == -1) {
					logger.error("invalid data in reference table for scc code level 1 description: ["
							+ sccLevel
							+ "]; SCCs associated with level ignored; Please correct SCC.");
					continue;
				}
				sccLevel1Codes.put(sccLevel, sccLevel);
			}
		} catch (RemoteException re) {
			logger.error(re.getMessage(), re);
			DisplayUtil
					.displayError("System error. Please contact system administrator");
		}

		return sccLevel1Codes;
	}

	public final LinkedHashMap<String, String> getSccLevelsCodes(int level, String level1Desc, String level2Desc, String level3Desc, String level4Desc,
			String levelCode) {
		if (levelCode == null) {
			return null;
		}

		int index;
		String sccLevel;
		LinkedHashMap<String, String> sccLevelsCodes = null;
		init();

		try {
			SccLevel[] tempDefs = getInfrastructureService().retrieveSccLevelCodes(
					level, level1Desc, level2Desc, level3Desc, level4Desc, levelCode);

			sccLevelsCodes = new LinkedHashMap<String, String>();
			for (SccLevel tempDef : tempDefs) {
				sccLevel = tempDef.getSccLevelDsc();
				if (sccLevel == null) {
					logger.error("invalid data in reference table for scc code level "
							+ level);
					continue;
				}
				index = sccLevel.indexOf(":");
				if (index == -1) {
					logger.error("invalid data in reference table for scc code level "
							+ level
							+ " description: ["
							+ sccLevel
							+ "]; SCCs associated with level ignored; Please correct SCC.");
					continue;
				}
				sccLevelsCodes.put(sccLevel, sccLevel);
				
				
			}
		} catch (RemoteException re) {
			logger.error(re.getMessage(), re);
			DisplayUtil
					.displayError("System error. Please contact system administrator");
		}

		return sccLevelsCodes;
	}

	public final LinkedHashMap<String, String> getContactTitles() {
		if (contactTitles == null || ContactTitle.getData().isRefresh()) {
			init();

			try {
				SimpleDef[] tempDefs = getInfrastructureService().retrieveSimpleDefs(
						"cm_title_def", "title_cd", "title_dsc", "deprecated",
						null);

				contactTitles = new LinkedHashMap<String, String>();
				for (SimpleDef tempDef : tempDefs) {
					if (!tempDef.isDeprecated()) {
						contactTitles.put(tempDef.getDescription(),
								tempDef.getCode());
					}
				}
			} catch (RemoteException re) {
				logger.error(re.getMessage(), re);
				DisplayUtil
						.displayError("System error. Please contact system administrator");
			}

			// set timer for data pull refresh
			ContactTitle.getData().setRefresh(false);
		}
		return contactTitles;
	}

	public final DefSelectItems getContactPersonTitles() {
		return ContactTitle.getData().getItems();
	}

	public final LinkedHashMap<String, Integer> getCompanies() {
//		if (companies != null)
//			return companies;

		init();

		try {
			Company[] tempCompanies = getCompanyService().retrieveCompanies();
			companies = new LinkedHashMap<String, Integer>();

			for (Company tempCompany : tempCompanies) {
				companies
						.put(tempCompany.getName(), tempCompany.getCompanyId());
			}
		} catch (RemoteException re) {
			logger.error(re.getMessage(), re);
			DisplayUtil
					.displayError("System error. Please contact system administrator");
		}

		return companies;
	}

	public static Integer getCurrentUserId() {

		Integer userId = new Integer(-1);
		
		UserAttributes ua  = (UserAttributes) FacesUtil.getManagedBean("userAttrs");
/*
		if ((FacesContext.getCurrentInstance()) != null
				&& (FacesContext.getCurrentInstance().getExternalContext() != null)
				&& (FacesContext.getCurrentInstance().getExternalContext()
						.getSessionMap() != null)) {*/

		/*	UserAttributes ua = (UserAttributes) FacesContext
					.getCurrentInstance().getExternalContext().getSessionMap()
					.get("userAttrs");*/
			
			

			if ((ua != null) && (ua.getUserId() != null)) {
				userId = ua.getUserId();
			}
		

		return userId;
	}

	public final boolean isStars2Admin() {
		return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin();
	}

	public static UserAttributes getCurrentUserAttrs() {
		UserAttributes ua = null;

		/*if ((FacesContext.getCurrentInstance()) != null
				&& (FacesContext.getCurrentInstance().getExternalContext() != null)
				&& (FacesContext.getCurrentInstance().getExternalContext()
						.getSessionMap() != null)) {
			ua = (UserAttributes) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("userAttrs");
		}*/
		ua  = (UserAttributes) FacesUtil.getManagedBean("userAttrs");

		return ua;
	}

	/**
	 * @return user
	 * @throws DAOException
	 * 
	 */
	public static User getPortalUser() throws DAOException {
		User user = null;
		UserAttributes userAttrs = null;
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			HttpSession userSession = (HttpSession) ctx.getExternalContext()
					.getSession(false);
			userAttrs = (UserAttributes) userSession.getAttribute("userAttrs");
			user = userAttrs.getItsUser();

			String msg = "(Case 3) ITS User class is ";
			if (user != null) {
				msg += user.getClass().getName() + ".";
			} else {
				msg += "null.";
			}
			logger.debug(msg);
		} catch (Exception ex) {
			throw new DAOException(
					"Unable to retrieve user:" + ex.getMessage(), ex);
		}
		return user;
	}

	public final Timestamp getCurrentDate() {
		return new Timestamp(System.currentTimeMillis());
	}

	public final Timestamp getTodaysDate() {
		Calendar cal = GregorianCalendar.getInstance();
//		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Timestamp(cal.getTimeInMillis());
	}

	public final DefSelectItems getFieldAuditLogCategories() {
		return FieldAuditLogCategoryDef.getData().getItems();
	}

	public final DefSelectItems getFieldAuditLogAttributes() {
		return FieldAuditLogAttributeDef.getData().getItems();
	}

	public final DefSelectItems getRUMReasonTypes() {
		return RUMReasonDef.getData().getItems();
	}

	public final DefSelectItems getRUMDispositionTypes() {
		return RUMDispositionDef.getData().getItems();
	}

	public final DefSelectItems getRUMCategoryTypes() {
		return RUMCategoryDef.getData().getItems();
	}

	public final DefSelectItems getRelocationJFODef() {
		return RelocationJFODef.getData().getItems();
	}

	public final DefSelectItems getRelocationDispositionDef() {
		return RelocationDispositionDef.getData().getItems();
	}

	public final DefSelectItems getRelocationRequestTypes() {
		String defName = "RelocationReportsDef";
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PA_APPLICATION_TYPE_DEF", "APPLICATION_TYPE_CD",
					"APPLICATION_TYPE_DSC", "DEPRECATED");

			// filter out the non-relocation definitions
			data.addExcludedKey("PBR");
			data.addExcludedKey("PTIO");
			data.addExcludedKey("TV");
			data.addExcludedKey("RPC");
			data.addExcludedKey("RPE");
			data.addExcludedKey("RPR");
			cfgMgr.addDef(defName, data);
		}
		return data.getItems();
	}

	public final DefSelectItems getDoLaas() {
		return DoLaaDef.getData().getItems();
	}

	public final String getMavenVersion() {
		return Config.findNode("app.buildVersion").getText();
	}
	
	public final DefSelectItems getMonitoringContractors() {
		return MonitoringContractorDef.getData().getItems();
	}
	
	public final boolean isTimesheetEntryEnabled() {
		return (boolean)Config.getEnvEntry("app/timesheetEntryEnabled", false);
	}
	
	public final String getGoogleMapsApiKey() {
		return (String)Config.getEnvEntry("app/googleMapsApiKey", 
				"GOOGLE_MAPS_API_KEY_NOT_FOUND");
	}
	
	public DefSelectItems getAllActiveUsersDef() {
		return BasicUsersDef.getAllActiveUserData().getItems();
	}
	
	public DefSelectItems getAllUsersDef() {
		return BasicUsersDef.getAllUserData().getItems();
	}
	
	public final LinkedHashMap<String, Integer> getOddYears() {
		Integer year = Year.now().getValue();
		// if this is a even year, then bump up the year
		if (0 == year % 2) {
			year++;
		}
		String key = Year.of(year).format(DateTimeFormatter.ofPattern("yy"));

		// create the map if it is empty or if the current year is not in the map
		if (null == oddYears || null == oddYears.get(key)) {
			oddYears = new LinkedHashMap<String, Integer>();
			while (year >= 2001) {
				oddYears.put(
						Year.of(year).format(DateTimeFormatter.ofPattern("yy")),
						year);
				year -= 2;
			}
		}

		return oddYears;
	}
	
	public boolean isAmbientMonitorReportUploader() {
		boolean ambientMonitorReportUploader = false;
		try {
			UserDef userDef = getInfrastructureService().retrieveUserDef(
					getCurrentUserId());

			if (null != userDef) {
				for (SecurityGroup sg : userDef.getSecurityGroups()) {
					if (sg.getSecurityGroupId().equals(
							AMBIENT_MONITOR_REPORT_UPLOADER_ROLE_ID)) {
						ambientMonitorReportUploader = true;
						break;
					}
				}
			}
		} catch (RemoteException e) {
			DisplayUtil.displayError("Failed to retrieve the user profile");
			handleException(e);
		}

		return ambientMonitorReportUploader;
	}
	
	public final String getTinyMCEApiKey() {
		return (String)Config.getEnvEntry("app/tinyMCEApiKey", 
				"TINYMCE_API_KEY_NOT_FOUND");
	}

	public final String getScsTokenKey() {
		return (String)Config.getEnvEntry("app/scsTokenKey", 
				"SCS_TOKEN_KEY_NOT_FOUND");
	}
	
	public String getMaxLatitude() {
		return  SystemPropertyDef.getSystemPropertyValue("MaxLatitude", null);
	}

	public void setMaxLatitude(String maxLatitude) {
		this.maxLatitude = maxLatitude;
	}

	public String getMinLatitude() {
		return  SystemPropertyDef.getSystemPropertyValue("MinLatitude", null);
	}

	public void setMinLatitude(String minLatitude) {
		this.minLatitude = minLatitude;
	}

	public String getMaxLongitude() {
		return  SystemPropertyDef.getSystemPropertyValue("MaxLongitude", null);
	}

	public void setMaxLongitude(String maxLongitude) {
		this.maxLongitude = maxLongitude;
	}

	public String getMinLongitude() {
		return  SystemPropertyDef.getSystemPropertyValue("MinLongitude", null);
	}

	public void setMinLongitude(String minLongitude) {
		this.minLongitude = minLongitude;
	}

	public String getHidden() {
		Boolean hideDistrict = SystemPropertyDef.getSystemPropertyValueAsBoolean("hideDistrict", false);
		if (hideDistrict) {
			return "visibility: hidden";
		}		
		return "";
	}

	public String getDeqName() {
		return SystemPropertyDef.getSystemPropertyValue("deqName", null);
	}
	
	public void setDeqName(String deqName) {
		this.deqName = deqName;
	}
	
	public Integer getDefaultSearchLimit() {
		return getInfrastructureService().getDefaultSearchLimit();
	}
	
	public Integer getTotalEUTypeCount(){
		return  EmissionUnitTypeDef.getData().getItems().getCurrentItems().size();
		
	}
	
	public boolean isWOGCCAPIUrlPartAvailable() {

		String urlPart = SystemPropertyDef.getSystemPropertyValue("WOGCC_API_URL_PART", null);

		return (Utility.isNullOrEmpty(urlPart)) ? false : true;
	}

	
	public boolean isPlssAutoReplication(){
		return SystemPropertyDef.getSystemPropertyValueAsBoolean("APP_PLSS_AUTO_REPLICATION", true);
	}
	
	public String getFugComponentHOHeader(){
		String s = "Heavy Oil (\u226420\u00B0 API)";
		return s;
	}

	public String getFugComponentLOHeader(){
		String s = "Light Oil(>20\u00B0 API)";
		return s;
	}

	
	public final DefSelectItems getIndianReservations() {
		return IndianReservationDef.getData().getItems();
	}

	public boolean isDistrictVisible(){
		Boolean hideDistrict = SystemPropertyDef.getSystemPropertyValueAsBoolean("hideDistrict", false);
		return !hideDistrict;
	}

//	public Double getMapCenterLat(){
//		return SystemPropertyDef.getSystemPropertyValueAsDouble("APP_MAP_CENTER_LAT", null);
//	}
//	
//	public Double getMapCenterLong(){
//		return SystemPropertyDef.getSystemPropertyValueAsDouble("APP_MAP_CENTER_LONG", null);
//	}
//
//	
}
