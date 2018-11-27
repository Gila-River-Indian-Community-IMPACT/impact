package us.oh.state.epa.stars2.app.emissionsReport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.facility.EgressPoint;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.def.EgOperatingStatusDef;
import us.oh.state.epa.stars2.def.EgrPointShapeDef;
import us.oh.state.epa.stars2.def.EgrPointTypeDef;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

public class FacilityStackInfo {

	private static Logger logger = Logger.getLogger(FacilityStackInfo.class);
	// private static int numFacilities = 0;
	private static FacilityService fBO;
	protected static FacilityList[] facilities;
	static Comparator<Object> compare = null;
	static FileWriter logFileTall;
	static FileWriter logFileElecGen;
	static FileWriter logFileAllElecGen;

	private static OperatingStatusDef operatingStatusDef; //TODO assess the static members of this class
	
	FacilityStackInfo() {

	}

	public static String performOperation() {
		compare = new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				EpInfo epI1 = (EpInfo) o1;
				EpInfo epI2 = (EpInfo) o2;
				if (epI1.ep.getReleasePointId() != null) {
					int r1 = epI1.ep.getReleasePointId().compareTo(
							epI2.ep.getReleasePointId());
					if (r1 != 0)
						return r1;
				}
				int r2 = epI1.eu.getEpaEmuId().compareTo(epI2.eu.getEpaEmuId());
				if (r2 != 0)
					return r2;
				return epI1.sccId.compareTo(epI2.sccId);
			}
		};

		try {
			fBO = ServiceFactory.getInstance().getFacilityService();
		} catch (ServiceFactoryException sfe) {
			logger.error("Failed on accessing the BOs", sfe);
			DisplayUtil
					.displayError("A system error has occurred. Please contact System Administrator.");
			return "";
		}

		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		Node root = cfgMgr.getNode("app.fileStore.rootPath");
		String filePath = root.getAsString("value");
		String logFileNameTall = filePath + File.separatorChar + "EIS"
				+ File.separator + "tallStacks.csv";
		String logFileNameElecGen = filePath + File.separatorChar + "EIS"
				+ File.separator + "elecGen.csv";
		String logFileNameAllElecGen = filePath + File.separatorChar + "EIS"
				+ File.separator + "allElecGen.csv";
		try {
			logFileTall = new FileWriter(logFileNameTall);
			logFileElecGen = new FileWriter(logFileNameElecGen);
			logFileAllElecGen = new FileWriter(logFileNameAllElecGen);
			logFileTall
					.write("FacilityId, FacilityName, FacOpStatus, EU, SccId, EpId, Type, DapcDescption, CompanyDescription, EpOpStatus, BaseElev, FenceDist, ReleaseHeight, BuildLength, BuildWidth, BuildHeight, Lat(dec), Long(dec), Shape, Diameter, CrossArea, Maxtemp, AvgTemp, MaxFlow, AvgFlow"
							+ "\n");
			logFileElecGen
					.write("FacilityId, FacilityName, FacOpStatus, EU, SccId, EpId, Type, DapcDescption, CompanyDescription, EpOpStatus, BaseElev, FenceDist, ReleaseHeight, BuildLength, BuildWidth, BuildHeight, Lat(dec), Long(dec), Shape, Diameter, CrossArea, Maxtemp, AvgTemp, MaxFlow, AvgFlow"
							+ "\n");
			logFileAllElecGen
					.write("FacilityId, FacilityName, FacOpStatus, EU, SccId, EpId, Type, DapcDescption, CompanyDescription, EpOpStatus, BaseElev, FenceDist, ReleaseHeight, BuildLength, BuildWidth, BuildHeight, Lat(dec), Long(dec), Shape, Diameter, CrossArea, Maxtemp, AvgTemp, MaxFlow, AvgFlow"
							+ "\n");
			logFileTall.flush();
			logFileElecGen.flush();
			logFileAllElecGen.flush();
		} catch (IOException ioe) {
			logger.error("Failed to create files", ioe);
			return "";
		}

		facilities = getFacilities();
		if (facilities == null) {
			return "";
		}
		logger.error("Info:  number of facilities read is " + facilities.length);
		for (FacilityList fl : facilities) {
			if (OperatingStatusDef.SD.equals(fl.getOperatingStatusCd()))
				continue;
			Facility facility = null;
			try {
				facility = fBO.retrieveFacilityProfile(fl.getFpId(), false);
			} catch (RemoteException re) {
				logger.error(
						"Failed on retrieveFacilityProfile(): fpId "
								+ fl.getFpId(), re);
			}
			if (facility == null) {
				logger.error("Failed to read facility with fpId "
						+ fl.getFpId());

				continue;
			}
			// numFacilities++;
			processFacility(facility);
		}
		try {
			logFileTall.write("End of tall stack records" + "\n");
			logFileTall.flush();
			logFileElecGen.write("End of electric generation records" + "\n");
			logFileElecGen.flush();
			logFileAllElecGen.write("End of all electric generation records"
					+ "\n");
			logFileAllElecGen.flush();
			logFileTall.close();
			logFileElecGen.close();
			logFileAllElecGen.close();
		} catch (IOException ioe) {
			logger.error("Error writing last record");
		}
		return "";
	}

	static void processFacility(Facility facility) {
		ArrayList<EpInfo> tallStacks = new ArrayList<EpInfo>();
		ArrayList<EpInfo> elecGen = new ArrayList<EpInfo>();
		for (EmissionUnit eu : facility.getEmissionUnits()) {
			if (!EuOperatingStatusDef.OP.equals(eu.getOperatingStatusCd()))
				continue;
			for (EmissionProcess ep : eu.getEmissionProcesses()) {
				SccCode sccCode = ep.getSccCode();
				if (sccCode == null)
					continue;
				String sccId = sccCode.getSccId();
				if (sccId == null || sccId.length() < 8)
					continue;
				// if(sccId.startsWith("1010") || sccId.startsWith("2010")) {
				// EgressPoint fakeEP = new EgressPoint();
				// fakeEP.setEgressPointShapeCd(null);
				// fakeEP.setOperatingStatusCd(null);
				// ArrayList<EpInfo> allElecGen = new ArrayList<EpInfo>();
				// allElecGen.add(new EpInfo(eu, sccId, fakeEP));
				// writeRows(facility, allElecGen, logFileAllElecGen);
				// }

				// The following to provide place for a breakpoint
				// if(facility.getFacilityId().equals("0744000150")) {
				// facility.setFacilityId("0744000150");
				// }
				for (EgressPoint egp : ep.getAllEgressPoints()) {
					if (!EgrPointTypeDef.VERTICAL.equals(egp
							.getEgressPointTypeCd())
							&& !EgrPointTypeDef.VERTICAL.equals(egp
									.getEgressPointTypeCd()))
						continue;
					if (egp.getReleaseHeight() != null
							&& egp.getReleaseHeight() > 400f) {
						tallStacks.add(new EpInfo(eu, sccId, egp));
					}
					if (sccId.startsWith("1010") || sccId.startsWith("2010")) {
						elecGen.add(new EpInfo(eu, sccId, egp));
					}
				}
			}
		}
		// sort and write out
		writeRows(facility, tallStacks, logFileTall);
		writeRows(facility, elecGen, logFileElecGen);
	}

	static void writeRows(Facility facility, List<EpInfo> l, FileWriter f) {
		if (l.size() > 0) {
			Collections.sort(l, compare);
			try {
				for (EpInfo ei : l) {
					String fName = facility.getName();
					String fStat = operatingStatusDef.getData().getItems()
							.getItemDesc(facility.getOperatingStatusCd());

					String type = EgrPointTypeDef.getData().getItems()
							.getItemDesc(ei.ep.getEgressPointTypeCd());

					String EpOpStatus = EgOperatingStatusDef.getData()
							.getItems()
							.getItemDesc(ei.ep.getOperatingStatusCd());

					String baseEl = null;
					if (ei.ep.getBaseElevation() != null) {
						baseEl = measurementConvertToHundredths(ei.ep
								.getBaseElevation());
					}
					String fDist = null;
					if (ei.ep.getStackFencelineDistance() != null) {
						fDist = measurementConvertToHundredths(ei.ep
								.getStackFencelineDistance());
					}
					String relHei = null;
					if (ei.ep.getReleaseHeight() != null) {
						relHei = measurementConvertToHundredths(ei.ep
								.getReleaseHeight());
					}
					String buildL = null;
					if (ei.ep.getBuildingLength() != null) {
						buildL = measurementConvertToHundredths(ei.ep
								.getBuildingLength());
					}
					String buildW = null;
					if (ei.ep.getBuildingWidth() != null) {
						buildW = measurementConvertToHundredths(ei.ep
								.getBuildingWidth());
					}
					String buildH = null;
					if (ei.ep.getBuildingHeight() != null) {
						buildH = measurementConvertToHundredths(ei.ep
								.getBuildingHeight());
					}

					String shape = EgrPointShapeDef.getData().getItems()
							.getItemDesc(ei.ep.getEgressPointShapeCd());

					String maxT = null;
					if (ei.ep.getExitGasTempMax() != null) {
						maxT = measurementConvertToHundredths(ei.ep
								.getExitGasTempMax());
					}
					String avgT = null;
					if (ei.ep.getExitGasTempAvg() != null) {
						avgT = measurementConvertToHundredths(ei.ep
								.getExitGasTempAvg());
					}

					String maxF = null;
					if (ei.ep.getExitGasFlowMax() != null) {
						maxF = measurementConvertToHundredths(ei.ep
								.getExitGasFlowMax());
					}
					String avgF = null;
					if (ei.ep.getExitGasFlowAvg() != null) {
						avgF = measurementConvertToHundredths(ei.ep
								.getExitGasFlowAvg());
					}
					String diameter = null;
					if (ei.ep.getDiameter() != null) {
						diameter = measurementConvertToHundredths(ei.ep
								.getDiameter());
					}

					f.write(facility.getFacilityId() + ",\"" + noNull(fName)
							+ "\"," + noNull(fStat) + "," + ei.eu.getEpaEmuId()
							+ "," + ei.sccId + ",\""
							+ noNull(ei.ep.getEgressPointId()) + "\","
							+ noNull(type) + ",\""
							+ noNull(ei.ep.getDapcDesc()) + "\",\""
							+ noNull(ei.ep.getRegulatedUserDsc()) + "\","
							+ noNull(EpOpStatus) + "," + noNull(baseEl) + ","
							+ noNull(fDist) + "," + noNull(relHei) + ","
							+ noNull(buildL) + "," + noNull(buildW) + ","
							+ noNull(buildH) + ",\""
							+ noNull(ei.ep.getLatitude()) + "\",\""
							+ noNull(ei.ep.getLongitude()) + "\","
							+ noNull(shape) + "," + noNull(diameter) + ","
							+ noNull(ei.ep.getCrossSectArea()) + ","
							+ noNull(maxT) + "," + noNull(avgT) + ","
							+ noNull(maxF) + "," + noNull(avgF) + "\n");
					f.flush();
				}
			} catch (IOException ioe) {
				logger.error("failed to write row", ioe);
			}
		}
	}

	static String noNull(String s) {
		if (s == null)
			return "";
		if (s.indexOf('"') >= 0) {
			s = s.replace('"', '_');
			logger.error("Found embedded double quote");
		}
		return s.trim();
	}

	static String measurementConvertToHundredths(float meas) {
		String format = "###########0.00";
		DecimalFormat decFormat = new DecimalFormat(format);
		return decFormat.format(meas);
	}
	
	static String measurementConvertToHundredths(BigDecimal meas) {
		String format = "###########0.00";
		DecimalFormat decFormat = new DecimalFormat(format);
		return decFormat.format(meas); // NEED TO TEST
	}

	protected static FacilityList[] getFacilities() {
		FacilityList[] fs = null;
		try {
			fs = fBO.searchFacilities(null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, true, null);
		} catch (RemoteException re) {
			logger.error("Failed to read facilitites", re);
		}
		return fs;
	}
}

class EpInfo {
	EmissionUnit eu;
	String sccId;
	EgressPoint ep;

	EpInfo(EmissionUnit eu, String sccId, EgressPoint ep) {
		this.eu = eu;
		this.sccId = sccId;
		this.ep = ep;
	}
}