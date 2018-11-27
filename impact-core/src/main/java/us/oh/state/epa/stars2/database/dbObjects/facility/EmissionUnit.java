package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.Emissions;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitReplacement;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.FieldAuditLog;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.EmissionUnitReportingDef;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.ExemptStatusDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PTIORegulatoryStatus;
import us.oh.state.epa.stars2.def.PTIRegulatoryStatus;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.TVClassification;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.reports.PollutantPartOf;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;

/**
 * @author Kbradley
 * 
 */
public class EmissionUnit extends FacilityNode implements
		Comparable<EmissionUnit> {
	
	private static final long serialVersionUID = -3836694355455780160L;

	public static final String EPAEMUMSGID = "epaemuId:1";
	private static String validEpaChar = "BDFGJKLNPRTWX"; // list of valid EPA
	// ID characters
	// private static Severity designCapErrorWarning =
	// ValidationMessage.Severity.WARNING;
	private Integer emuId;
	private String companyId; // Company ID
	private String epaEmuId; // AQD assigned ID like B001
	private Integer CorrEpaEmuId; // generated Correlated EPa Emu ID which never
									// changed
	private String euDesc; // this is the real AQD Description
	private String regulatedUserDsc; // Comapny Description
	private String orisBoilerId;
	private String designCapacityCd;
	private String designCapacityUnitsCd;
	private String designCapacityUnitsVal;
	private Timestamp euInitInstallDate;
	private Timestamp euInitStartupDate;
	private Timestamp euStartupDate;
	private Timestamp euShutdownNotificationDate;
	@SuppressWarnings("unused")
	private Long euShutdownNotificationDateLong;
	private Timestamp euShutdownDate;
	@SuppressWarnings("unused")
	private Long euShutdownDateLong;
	private Timestamp euInstallDate;
	@SuppressWarnings("unused")
	private Long euInitInstallDateLong;
	@SuppressWarnings("unused")
	private Long euStartupDateLong;
	@SuppressWarnings("unused")
	private Long euInstallDateLong;
	private String operatingStatusCd;
	private List<EmissionProcess> emissionProcesses = new ArrayList<EmissionProcess>(
			0);
	private List<EuEmission> euEmissions = new ArrayList<EuEmission>(0);
	private ArrayList<EmissionUnitTypeDef> emissionUnitTypes = new ArrayList<EmissionUnitTypeDef>(
			0);
	private ArrayList<Permit> activePermits = new ArrayList<Permit>(0);
	private String dapcDescription; // THIS ATTRIBUTE IS NEVER USED
	private String ptiStatusCd;
	private String ptioStatusCd;
	private String tvClassCd;
	private String exemptStatusCd;
	private String emissionUnitTypeCd;
	private String emissionUnitTypeName;
	private String wiseViewId;
	private String mouseOverTip;
	private EmissionUnitType emissionUnitType;

	private transient String origOperatingStatusCd;
	private transient String equipmentDescription;

	// flags used internally by the system to determine if certain fields are
	// required
	private boolean demEgPntModeling;
	private boolean psdOrScreenModeling;

	private List<EmissionUnitReplacement> emissionUnitReplacements = new ArrayList<EmissionUnitReplacement>(
			0);
	private List<EmissionUnitReplacement> currentEngineSerialNumbers = new ArrayList<EmissionUnitReplacement>(
			0);

	public EmissionUnit() {
		super();
		// default for Operating status
		operatingStatusCd = EuOperatingStatusDef.NI;
		origOperatingStatusCd = operatingStatusCd;
		ptiStatusCd = PTIRegulatoryStatus.NONE;
		ptioStatusCd = PTIORegulatoryStatus.NONE;
		tvClassCd = TVClassification.NOT_APPLICABLE;
		exemptStatusCd = ExemptStatusDef.NA;
		emissionUnitType = new EmissionUnitType();
		emissionUnitType.setComponents(EmissionUnitType.initializeComponentList(null));
		//emissionUnitTypes = retrieveEmissionUnitTypes();
		requiredField(emissionUnitTypeCd, "emissionUnitType",
				"Emissions Unit Type");
	}

	public EmissionUnit(EmissionUnit eu) {
		super(eu);

		if (eu != null) {
			newObject = false;

			this.setEmuId(eu.emuId);
			this.setDapcDescription(eu.dapcDescription);

			this.setCompanyId(eu.companyId);
			this.setEuDesc(eu.euDesc);
			this.euEmissions.addAll(eu.getEuEmissions());
			this.setEuInitInstallDate(eu.euInitInstallDate);
			this.setEuInitStartupDate(eu.euInitStartupDate);
			this.setEuInstallDate(eu.euInstallDate);
			this.setEuShutdownDate(eu.euShutdownDate);
			this.setEuShutdownNotificationDate(eu.euShutdownNotificationDate);
			this.setEuStartupDate(eu.euStartupDate);
			this.setExemptStatusCd(eu.exemptStatusCd);
			this.setOperatingStatusCd(eu.operatingStatusCd);
			this.setOrigOperatingStatusCd(eu.origOperatingStatusCd);
			this.setOrisBoilerId(eu.orisBoilerId);
			this.setPtioStatusCd(eu.ptioStatusCd);
			this.setPtiStatusCd(eu.ptiStatusCd);
			this.setRegulatedUserDsc(eu.regulatedUserDsc);
			this.setTvClassCd(eu.tvClassCd);
			this.setDesignCapacityCd(eu.designCapacityCd);
			this.setDesignCapacityUnitsCd(eu.designCapacityUnitsCd);
			this.setDesignCapacityUnitsVal(eu.designCapacityUnitsVal);
			this.setEmissionUnitTypeCd(eu.emissionUnitTypeCd);
			this.setWiseViewId(eu.wiseViewId);
			this.setEmissionUnitReplacements(eu.emissionUnitReplacements);
		}
	}

	private boolean requiredFields() {
		boolean b = !requiredField(emissionUnitTypeCd, "emissionUnitType",
				"Emissions Unit Type", "emissionUnit:" + epaEmuId);
		boolean b2 = !requiredField(operatingStatusCd, "euOperatingStatusCd",
				"Operating Status", "emissionUnit:" + epaEmuId);
		b = b || b2;
		b2 = !requiredField(ptioStatusCd, "euPtioStatusCd", "Permit Status",
				"emissionUnit:" + epaEmuId);
		b = b || b2;
		b2 = !requiredField(ptiStatusCd, "euPtiStatusCd", "PTI Status",
				"emissionUnit:" + epaEmuId);
		b = b || b2;
		b2 = !requiredField(tvClassCd, "euTvClassCd",
				"Title V EU Classification", "emissionUnit:" + epaEmuId);
		b = b || b2;
		b2 = !requiredField(exemptStatusCd, "euExemptStatusCd",
				"Exemption Status", "emissionUnit:" + epaEmuId);
		b = b || b2;
		return !b;
	}

	/**
	 * @return
	 */
	public final Integer getEmuId() {
		return emuId;
	}

	/**
	 * @param emuId
	 */
	public final void setEmuId(Integer emuId) {
		this.emuId = emuId;
	}

	/**
	 * @return
	 */
	public final String getCompanyId() {
		return companyId;
	}

	/**
	 * @param emissionUnitId
	 */
	public final void setCompanyId(String companyId) {
		checkDirty("euci", epaEmuId, this.companyId, companyId);
		this.companyId = companyId;
	}

	/**
	 * @return
	 */
	public final String getEpaEmuId() {
		return epaEmuId;
	}

	/**
	 * @param scc
	 */
	public final EmissionProcess findProcess(String scc) {
		EmissionProcess ret = null;
		boolean isNull = false;
		for (EmissionProcess p : this.getEmissionProcesses().toArray(
				new EmissionProcess[0])) {
			if (null == p.getSccId()) {
				isNull = true;
			} else {
				isNull = false;
			}
			if (isNull) {
				if (scc == null) {
					ret = p;
					break;
				}
			} else {
				if (scc != null && scc.equals(p.getSccId())) {
					ret = p;
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * @param companyCmpId
	 */
	public final void setEpaEmuId(String epaEmuId) {
		//
		// The first character must be an upper case alpha so go ahead and
		// convert
		// for them.
		//
		epaEmuId = Utility.isNullOrEmpty(epaEmuId) ? epaEmuId : epaEmuId.trim()
				.toUpperCase();
		checkDirty("euid", epaEmuId, this.epaEmuId, epaEmuId);
		fieldChangeEventLog("feui", epaEmuId, this.epaEmuId, epaEmuId);
		this.epaEmuId = epaEmuId;
	}

	/**
	 * @return
	 */
	public final String getEuDesc() {
		return euDesc;
	}

	public final String getAnyEuDesc() {
		if (euDesc != null && euDesc.trim().length() > 0)
			return euDesc;
		return regulatedUserDsc;
	}

	/**
	 * @param euDesc
	 */
	public final void setEuDesc(String euDesc) {
		this.euDesc = euDesc;
		mouseOverTip = "Emissions Unit";
		if (euDesc != null && !euDesc.equals("")) {
			if (euDesc.length() > 40) {
				mouseOverTip = euDesc.substring(0, 39) + "...";
			} else {
				mouseOverTip = euDesc;
			}
		}
	}

	/**
	 * @param companyCmpId
	 */
	public final void setRegulatedUserDsc(String regulatedUserDsc) {
		checkDirty("eucd", epaEmuId, this.regulatedUserDsc, regulatedUserDsc);
		this.regulatedUserDsc = regulatedUserDsc;
	}

	/**
	 * @return
	 */
	public final String getRegulatedUserDsc() {
		return regulatedUserDsc;
	}

	/**
	 * @param orisBoilerId
	 */
	public final void setOrisBoilerId(String orisBoilerId) {
		fieldChangeEventLog("feub", epaEmuId, this.orisBoilerId, orisBoilerId);
		this.orisBoilerId = orisBoilerId;
	}

	/**
	 * @return
	 */
	public final String getOrisBoilerId() {
		return orisBoilerId;
	}

	public final Timestamp getEuInitInstallDate() {
		return euInitInstallDate;
	}

	/**
	 * @param euInstallCompletionDate
	 */
	public final void setEuInitInstallDate(Timestamp euInitInstallDate) {
		this.euInitInstallDate = euInitInstallDate;
	}

	public final Timestamp getEuInitStartupDate() {
		return euInitStartupDate;
	}

	public final void setEuInitStartupDate(Timestamp euInitStartupDate) {
		this.euInitStartupDate = euInitStartupDate;
	}

	/**
	 * @return
	 */
	public final Timestamp getEuStartupDate() {
		return euStartupDate;
	}

	/**
	 * @param euStartupDate
	 */
	public final void setEuStartupDate(Timestamp euStartupDate) {
		this.euStartupDate = euStartupDate;
	}

	/**
	 * @return
	 */
	public final Timestamp getEuShutdownDate() {
		return euShutdownDate;
	}

	/**
	 * @param euShutdownDate
	 */
	public final void setEuShutdownDate(Timestamp euShutdownDate) {
		fieldChangeEventLog("feus", epaEmuId, this.euShutdownDate,
				euShutdownDate);
		this.euShutdownDate = euShutdownDate;
	}

	/**
	 * @return
	 */
	public final Timestamp getEuShutdownNotificationDate() {
		return euShutdownNotificationDate;
	}

	/**
	 * @return
	 */
	public final Timestamp getEuInstallDate() {
		return euInstallDate;
	}

	/**
	 * @param euInstallDate
	 */
	public final void setEuInstallDate(Timestamp euInstallDate) {
		this.euInstallDate = euInstallDate;
	}

	/**
	 * @param euShutdownNotificationDate
	 */
	public final void setEuShutdownNotificationDate(
			Timestamp euShutdownNotificationDate) {
		this.euShutdownNotificationDate = euShutdownNotificationDate;
	}

	public final String getDapcDescription() {
		// THIS ATTRIBUTE NEVER HAS A VALUE
		return dapcDescription;
	}

	public final void setDapcDescription(String dapcDescription) {
		// THIS ATTRIBUTE IS NEVER USED
		this.dapcDescription = dapcDescription;
	}

	/**
	 * @return
	 */
	public final String getOperatingStatusCd() {
		return operatingStatusCd;
	}

	/**
	 * @param operatingStatusCd
	 */
	public final void setOperatingStatusCd(String operatingStatusCd) {
		requiredField(operatingStatusCd, "euOperatingStatus",
				"Operating Status", "emissionUnit:" + epaEmuId);
		checkDirty("euos", epaEmuId, EuOperatingStatusDef.getData().getItems()
				.getItemDesc(this.operatingStatusCd), EuOperatingStatusDef
				.getData().getItems().getItemDesc(operatingStatusCd));
		fieldChangeEventLog(
				"feuo",
				epaEmuId,
				EuOperatingStatusDef.getData().getItems()
						.getItemDesc(this.operatingStatusCd),
				EuOperatingStatusDef.getData().getItems()
						.getItemDesc(operatingStatusCd));
		if (operatingStatusCd != null) {
			if (!operatingStatusCd.equals(OperatingStatusDef.SD)) {
				euShutdownNotificationDate = null;
				euShutdownDate = null;
			}
		}
		this.operatingStatusCd = operatingStatusCd;
	}

	/**
	 * @return
	 */
	public final String getPtiStatusCd() {
		return ptiStatusCd;
	}

	/**
	 * @param ptiStatusCd
	 */
	public final void setPtiStatusCd(String ptiStatusCd) {
		checkDirty("eupt", epaEmuId, PTIRegulatoryStatus.getData().getItems()
				.getItemDesc(this.ptiStatusCd), PTIRegulatoryStatus.getData()
				.getItems().getItemDesc(ptiStatusCd));
		this.ptiStatusCd = ptiStatusCd;
	}

	/**
	 * @return
	 */
	public final String getPtioStatusCd() {
		return ptioStatusCd;
	}

	/**
	 * @param ptioStatusCd
	 */
	public final void setPtioStatusCd(String ptioStatusCd) {
		checkDirty("eups", epaEmuId, PTIORegulatoryStatus.getData().getItems()
				.getItemDesc(this.ptioStatusCd), PTIORegulatoryStatus.getData()
				.getItems().getItemDesc(ptioStatusCd));
		fieldChangeEventLog(
				"feup",
				epaEmuId,
				PTIORegulatoryStatus.getData().getItems()
						.getItemDesc(this.ptioStatusCd),
				PTIORegulatoryStatus.getData().getItems()
						.getItemDesc(ptioStatusCd));
		this.ptioStatusCd = ptioStatusCd;
	}

	/**
	 * @return
	 */
	public final String getTvClassCd() {
		return tvClassCd;
	}

	/**
	 * @param operatingStatusCd
	 */
	public final void setTvClassCd(String tvClassCd) {
		checkDirty("eutc", epaEmuId, TVClassification.getData().getItems()
				.getItemDesc(this.tvClassCd), TVClassification.getData()
				.getItems().getItemDesc(tvClassCd));
		this.tvClassCd = tvClassCd;
	}

	public final String getEmissionUnitTypeCd() {
		return emissionUnitTypeCd;
	}

	public final void setEmissionUnitTypeCd(String emissionUnitTypeCd) {
		requiredField(emissionUnitTypeCd, "emissionUnitType",
				"Emissions Unit Type", "emissionUnit:" + epaEmuId);

		this.emissionUnitTypeCd = emissionUnitTypeCd;
		refreshEmissionUnitTypeName();
	}

	public final String getEmissionUnitTypeName() {
		return emissionUnitTypeName;
	}

	public final void setWiseViewId(String wiseViewId) {
		this.wiseViewId = wiseViewId;
	}

	public final String getWiseViewId() {
		return wiseViewId;
	}

	/**
	 * @return
	 */
	public final String getExemptStatusCd() {
		return exemptStatusCd;
	}

	/**
	 * @param operatingStatusCd
	 */
	public final void setExemptStatusCd(String exemptStatusCd) {
		checkDirty("eues", epaEmuId, ExemptStatusDef.getData().getItems()
				.getItemDesc(this.exemptStatusCd), ExemptStatusDef.getData()
				.getItems().getItemDesc(exemptStatusCd));
		fieldChangeEventLog("feue", epaEmuId, ExemptStatusDef.getData()
				.getItems().getItemDesc(this.exemptStatusCd), ExemptStatusDef
				.getData().getItems().getItemDesc(exemptStatusCd));
		this.exemptStatusCd = exemptStatusCd;
	}

	/**
	 * @return
	 */
	public final List<EmissionProcess> getEmissionProcesses() {
		return emissionProcesses;
	}

	public Set<EgressPoint> getAllEgressPoints() {
		HashSet<EgressPoint> set = new HashSet<EgressPoint>();
		for (EmissionProcess emp : emissionProcesses) {
			set.addAll(emp.getAllEgressPoints());
		}
		return set;
	}

	/**
	 * @param emissionProcess
	 */
	public final void addEmissionProcess(EmissionProcess emissionProcess) {
		if (emissionProcess != null) {
			this.emissionProcesses.add(emissionProcess);
		}
	}

	public final List<EuEmission> getEuEmissions() {
		return euEmissions;
	}

	public final void setEuEmissions(List<EuEmission> euEmissions) {
		this.euEmissions = euEmissions;
	}

	public final void addEuEmission(EuEmission emission) {
		if (emission != null) {
			euEmissions.add(emission);
		}
	}

	public final void removeEuEmission(EuEmission emission) {
		euEmissions.remove(emission);
	}

	public final Permit[] getActivePermits() {
		return activePermits.toArray(new Permit[0]);
	}

	public final void setActivePermits(ArrayList<Permit> permits) {
		if (permits != null) {
			for (Permit permit : permits) {
				if (permit.getPermitType().equals(PermitTypeDef.NSR)
						|| permit.getPermitType().equals(PermitTypeDef.TV_PTO)
						//|| permit.getPermitType().equals(PermitTypeDef.SPTO)
						) {
					activePermits.add(permit);
				}
			}
		}
	}

	public final String getDesignCapacityCd() {
		return designCapacityCd;
	}

	public final void setDesignCapacityCd(String designCapacityCd) {
		this.designCapacityCd = designCapacityCd;
	}

	public final String getDesignCapacityUnitsCd() {
		return designCapacityUnitsCd;
	}

	public final void setDesignCapacityUnitsCd(String designCapacityUnitsCd) {
		this.designCapacityUnitsCd = designCapacityUnitsCd;
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
	 */
	public void populate(ResultSet rs) {
		try {
			super.populate(rs);
			setEmuId(AbstractDAO.getInteger(rs, "emu_id"));
			setEpaEmuId(rs.getString("epa_emu_id"));
			setCorrEpaEmuId(AbstractDAO.getInteger(rs, "corr_epa_emu_id"));
			setCompanyId(rs.getString("emission_unit_id"));
			setEuDesc(rs.getString("eu_desc"));
			setLastModified(AbstractDAO.getInteger(rs, "emissionUnit_lm"));
			setRegulatedUserDsc(rs.getString("requlated_user_dsc"));
			setEuStartupDate(rs.getTimestamp("startup_dt"));
			setEuInstallDate(rs.getTimestamp("install_dt"));
			setEuInitInstallDate(rs.getTimestamp("initial_installation_dt"));
			setEuInitStartupDate(rs.getTimestamp("initial_startup_dt"));
			setEuShutdownNotificationDate(rs
					.getTimestamp("shutdown_notification_dt"));
			setEuShutdownDate(rs.getTimestamp("shutdown_dt"));
			setOperatingStatusCd(rs.getString("emUnit_operating_status_cd"));
			setPtioStatusCd(rs.getString("ptio_regulatory_status_cd"));
			setPtiStatusCd(rs.getString("pti_regulatory_status_cd"));
			setTvClassCd(rs.getString("tv_classification_cd"));
			setExemptStatusCd(rs.getString("exempt_status_cd"));
			setOrisBoilerId(rs.getString("oris_boiler_id"));
			setDesignCapacityCd(rs.getString("design_capacity_cd"));
			setDesignCapacityUnitsCd(rs.getString("design_capacity_units_cd"));
			setDesignCapacityUnitsVal(rs.getString("design_capacity_units_val"));
			setEmissionUnitTypeCd(rs.getString("eu_type_cd"));
			setWiseViewId(rs.getString("wiseview_id"));
		} catch (SQLException sqle) {
			logger.error("Required field error: " + sqle.getMessage(), sqle);
		} finally {
			newObject = false;
			setOrigOperatingStatusCd(operatingStatusCd);
		}
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result
				+ ((CorrEpaEmuId == null) ? 0 : CorrEpaEmuId.hashCode());
		result = PRIME * result
				+ ((companyId == null) ? 0 : companyId.hashCode());
		result = PRIME * result + ((emuId == null) ? 0 : emuId.hashCode());
		result = PRIME * result
				+ ((epaEmuId == null) ? 0 : epaEmuId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final EmissionUnit other = (EmissionUnit) obj;
		if (CorrEpaEmuId == null) {
			if (other.CorrEpaEmuId != null)
				return false;
		} else if (!CorrEpaEmuId.equals(other.CorrEpaEmuId))
			return false;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		if (emuId == null) {
			if (other.emuId != null)
				return false;
		} else if (!emuId.equals(other.emuId))
			return false;
		if (epaEmuId == null) {
			if (other.epaEmuId != null)
				return false;
		} else if (!epaEmuId.equals(other.epaEmuId))
			return false;
		return true;
	}

	public final void validateEuEmissionsTable() {
		HashMap<String, Integer> values = new HashMap<String, Integer>(0);
		ValidationMessage validationMessage;
		HashMap<String, Emissions> allowableEmissionsLbsHourMap = new HashMap<String, Emissions>();
		List<Emissions> allowableEmissionsLbsHourList = new ArrayList<Emissions>();
		HashMap<String, Emissions> potentialEmissionsLbsHourMap = new HashMap<String, Emissions>();
		List<Emissions> potentialEmissionsLbsHourList = new ArrayList<Emissions>();
		HashMap<String, Emissions> allowableEmissionsTonsYearMap = new HashMap<String, Emissions>();
		List<Emissions> allowableEmissionsTonsYearList = new ArrayList<Emissions>();
		HashMap<String, Emissions> potentialEmissionsTonsYearMap = new HashMap<String, Emissions>();
		List<Emissions> potentialEmissionsTonsYearList = new ArrayList<Emissions>();

		for (EuEmission tempEmi : euEmissions) {
			if(Utility.isNullOrEmpty(tempEmi.getPollutantCd())) {
				continue;
			}
			if (tempEmi.getAllowableEmissionsLbsHour() != null) {
				Emissions allowableEmissionsLbsHour = 
						convertToEmissionsFromAllowable(
								tempEmi,EmissionUnitReportingDef.POUNDS);
				allowableEmissionsLbsHourMap.put(tempEmi.getPollutantCd(),
						allowableEmissionsLbsHour);
				allowableEmissionsLbsHourList.add(allowableEmissionsLbsHour);
			}
			if (tempEmi.getAllowableEmissionsTonsYear() != null) {
				Emissions allowableEmissionsTonsYear = 
						convertToEmissionsFromAllowable(
								tempEmi,EmissionUnitReportingDef.TONS);
				allowableEmissionsTonsYearMap.put(tempEmi.getPollutantCd(),
						allowableEmissionsTonsYear);
				allowableEmissionsTonsYearList.add(allowableEmissionsTonsYear);
			}
			if (tempEmi.getPotentialEmissionsLbsHour() != null) {
				Emissions potentialEmissionsLbsHour = 
						convertToEmissionsFromPotential(
								tempEmi,EmissionUnitReportingDef.POUNDS);
				potentialEmissionsLbsHourMap.put(tempEmi.getPollutantCd(),
						potentialEmissionsLbsHour);
				potentialEmissionsLbsHourList.add(potentialEmissionsLbsHour);
			}
			if (tempEmi.getPotentialEmissionsTonsYear() != null) {
				Emissions potentialEmissionsTonsYear = 
						convertToEmissionsFromPotential(
								tempEmi,EmissionUnitReportingDef.TONS);
				potentialEmissionsTonsYearMap.put(tempEmi.getPollutantCd(),
						potentialEmissionsTonsYear);
				potentialEmissionsTonsYearList.add(potentialEmissionsTonsYear);
			}
		}

		for (EuEmission tempEmi : euEmissions) {
			String temp = tempEmi.getPollutantCd();
			if (temp == null || temp.equals("")) {
				validationMessage = new ValidationMessage(
						"pollutantCd",
						"No pollutant selected in the Potential Emissions table",
						ValidationMessage.Severity.ERROR, "emissionUnit:"
								+ epaEmuId, epaEmuId);
				validationMessages.put("pollutantCd", validationMessage);
			} else {
				validationMessages.remove("pollutantCd");
				if (values.containsKey(temp)) {
					validationMessage = new ValidationMessage(
							"pollutantCd",
							"Duplicate pollutant in the Potential Emissions table",
							ValidationMessage.Severity.ERROR, "emissionUnit:"
									+ epaEmuId, epaEmuId);
					validationMessages.put("pollutantCd" + temp,
							validationMessage);
				} else {
					validationMessages.remove("pollutantCd" + temp);
					values.put(temp, 1);
				}
			}

			boolean hasPotentialEmissionsLbsHour = null != tempEmi.getPotentialEmissionsLbsHour();
			boolean hasPotentialEmissionsTonsYear = null != tempEmi.getPotentialEmissionsTonsYear();
			boolean hasAllowableEmissionsLbsHour = null != tempEmi.getAllowableEmissionsLbsHour();
			boolean hasAllowableEmissionsTonsYear = null != tempEmi.getAllowableEmissionsTonsYear();
			boolean hasPotentialEmissions = hasPotentialEmissionsLbsHour || hasPotentialEmissionsTonsYear;
			boolean hasAllowableEmissions = hasAllowableEmissionsLbsHour || hasAllowableEmissionsTonsYear;

			
			if (!hasPotentialEmissions && !hasAllowableEmissions) {
				validationMessage = new ValidationMessage(
						"",
						"Potential Emissions Rates or Allowable Emissions Rates must be entered.",
						ValidationMessage.Severity.ERROR, "emissionUnit:"
								+ epaEmuId, epaEmuId);
				validationMessages.put("pollutantCd" + temp, validationMessage);
			}
			
		}
		
		// check for incorrect pollutant subsets
		PollutantPartOf partOf = new PollutantPartOf();
		for (Emissions emission : allowableEmissionsLbsHourList) {
			String msg = partOf.verifySubpartTotals(emission,
					allowableEmissionsLbsHourMap);
			if (msg != null) {
				String valMsg = "<" + PollutantDef.getData().getItems()
						.getItemDesc(emission.getPollutantCd()) + "> " + msg;
				validationMessage = new ValidationMessage(
						"allowableEmissionsUnit", valMsg,
						ValidationMessage.Severity.ERROR, "emissionUnit:"
								+ epaEmuId, epaEmuId);
				validationMessages.put(
						"pollutantCd" + emission.getPollutantCd(),
						validationMessage);
			}
		}
		
		partOf = new PollutantPartOf();
		for (Emissions emission : potentialEmissionsLbsHourList) {
			String msg = partOf.verifySubpartTotals(emission,
					potentialEmissionsLbsHourMap);
			if (msg != null) {
				String valMsg = "<" + PollutantDef.getData().getItems()
						.getItemDesc(emission.getPollutantCd()) + "> " + msg;
				validationMessage = new ValidationMessage(
						"potentialEmissionsUnit", valMsg,
						ValidationMessage.Severity.ERROR, "emissionUnit:"
								+ epaEmuId, epaEmuId);
				validationMessages.put(
						"pollutantCd" + emission.getPollutantCd(),
						validationMessage);
			}
		}		
		partOf = new PollutantPartOf();
		for (Emissions emission : allowableEmissionsTonsYearList) {
			String msg = partOf.verifySubpartTotals(emission,
					allowableEmissionsTonsYearMap);
			if (msg != null) {
				String valMsg = "<" + PollutantDef.getData().getItems()
						.getItemDesc(emission.getPollutantCd()) + "> " + msg;
				validationMessage = new ValidationMessage(
						"allowableEmissionsUnit", valMsg,
						ValidationMessage.Severity.ERROR, "emissionUnit:"
								+ epaEmuId, epaEmuId);
				validationMessages.put(
						"pollutantCd" + emission.getPollutantCd(),
						validationMessage);
			}
		}
		
		partOf = new PollutantPartOf();
		for (Emissions emission : potentialEmissionsTonsYearList) {
			String msg = partOf.verifySubpartTotals(emission,
					potentialEmissionsTonsYearMap);
			if (msg != null) {
				String valMsg = "<" + PollutantDef.getData().getItems()
						.getItemDesc(emission.getPollutantCd()) + "> " + msg;
				validationMessage = new ValidationMessage(
						"potentialEmissionsUnit", valMsg,
						ValidationMessage.Severity.ERROR, "emissionUnit:"
								+ epaEmuId, epaEmuId);
				validationMessages.put(
						"pollutantCd" + emission.getPollutantCd(),
						validationMessage);
			}
		}		
	}
	
	private Emissions convertToEmissionsFromAllowable(EuEmission euEmission, String unit) {
		Emissions emissions = new Emissions();
		emissions.setPollutantCd(euEmission.getPollutantCd());
		BigDecimal allowableValue = null;
		if (EmissionUnitReportingDef.POUNDS.equals(unit)) {
			allowableValue = euEmission.getAllowableEmissionsLbsHour()
					.multiply(euEmission.getUnitFactor("pph"));
		} else if (EmissionUnitReportingDef.TONS.equals(unit)) {
			allowableValue = euEmission.getAllowableEmissionsTonsYear();
		}
		String allowableStr = "0";
		if(allowableValue != null) {
			allowableStr = allowableValue.toPlainString();
		}
		emissions.setStackEmissions(allowableStr);
		emissions.setFugitiveEmissions("0");
		emissions.setEmissionsUnitNumerator(EmissionUnitReportingDef.TONS);
		return emissions;
	}

	private Emissions convertToEmissionsFromPotential(EuEmission euEmission, String unit) {
		Emissions emissions = new Emissions();
		emissions.setPollutantCd(euEmission.getPollutantCd());
		BigDecimal potentialValue = null;
		if (EmissionUnitReportingDef.POUNDS.equals(unit)) {
			potentialValue = euEmission.getPotentialEmissionsLbsHour()
					.multiply(euEmission.getUnitFactor("pph"));
		} else if (EmissionUnitReportingDef.TONS.equals(unit)) {
			potentialValue = euEmission.getPotentialEmissionsTonsYear();
		}
		String potentialStr = "0";
		if(potentialValue != null) {
			potentialStr = potentialValue.toPlainString();
		}
		emissions.setStackEmissions(potentialStr);
		emissions.setFugitiveEmissions("0");
		emissions.setEmissionsUnitNumerator(EmissionUnitReportingDef.TONS);
		return emissions;
	}
	
	private void validateEpEmuId() {
		if (!newObject) {
			boolean found = false;
			for (FieldAuditLog audLog : getFieldAuditLogs()) {
				if (audLog.getAttributeCd().equals("euid")) {
					found = true;
					break;
				}
			}
			if (!found) {
				return;
			}
		}

		ValidationMessage validationMessage = new ValidationMessage(
				"epaEmuId",
				"invalid EPA Emission Unit ID ["
						+ epaEmuId
						+ "]. The ID must consist of a character(BDFGJKLNPRTX) followed by three digits",
				ValidationMessage.Severity.ERROR, "emissionUnit:" + epaEmuId,
				epaEmuId);
		int validLen = 4;

		if (epaEmuId != null && epaEmuId.length() != validLen) {
			validationMessages.put(EPAEMUMSGID, validationMessage);
			return;
		}

		if ((epaEmuId != null) && (epaEmuId.length() > 0)) {
			char tempChar = epaEmuId.charAt(0);
			String validEpaChar1 = validEpaChar;
			if (operatingStatusCd.equals(EuOperatingStatusDef.SD)) {
				validEpaChar1 += "Z";
			}
			if (validEpaChar1.indexOf(tempChar) == -1) {
				validationMessages.put(EPAEMUMSGID, validationMessage);
				return;
			}
			String rest = epaEmuId.substring(1);
			try {
				Integer.valueOf(rest);
			} catch (NumberFormatException e) {
				validationMessages.put(EPAEMUMSGID, validationMessage);
				return;
			}
		}
		if (operatingStatusCd.equals(EuOperatingStatusDef.SD)
				&& epaEmuId.startsWith("Z")) {
			for (FieldAuditLog audLog : getFieldAuditLogs()) {
				if (audLog.getAttributeCd().equals("euid")) {
					validationMessages.put(EPAEMUMSGID, validationMessage);
					return;
				}
			}
		}
		validationMessages.remove(EPAEMUMSGID);
	}

	private void validateEmissionUnitReplacements() {
		if (this.isNonEngineReplacement()) {
			if (this.emissionUnitReplacements.isEmpty()) {
				validationMessages.put("emissionUnitReplacementTable",
						new ValidationMessage("emissionUnitReplacementTable",
								"You must have at least one entry in the serial number tracking table",
								ValidationMessage.Severity.ERROR,
								"emissionUnit:" + epaEmuId, epaEmuId));
			}

		}
	}
	
	private void validateEngineEmissionUnitSerialNumbers() {
		if (this.getEmissionUnitTypeCd().equals("ENG")) {
			if (this.emissionUnitReplacements.isEmpty()) {
				validationMessages.put("emissionUnitReplacementTable",
						new ValidationMessage("emissionUnitReplacementTable",
								"You must have at least one entry in the serial number tracking table",
								ValidationMessage.Severity.ERROR,
								"emissionUnit:" + epaEmuId, epaEmuId));
			} else {
				boolean hasCurrentEngine = false;
				for(EmissionUnitReplacement engineSerialNumber : this.emissionUnitReplacements) {
					if(engineSerialNumber.isSerialNumberCurrent()) {
						hasCurrentEngine = true;
						break;
					}
				}
				if(!hasCurrentEngine) {
					validationMessages.put("emissionUnitReplacementTable",
							new ValidationMessage("emissionUnitReplacementTable",
									"You must have a current engine",
									ValidationMessage.Severity.ERROR,
									"emissionUnit:" + epaEmuId, epaEmuId));
				}
			}

		}
	}

	public final ValidationMessage[] validate(String facPermitClassCd,
			boolean internal) {
		validationMessages.clear();
		boolean ok = requiredFields();

		requiredField(companyId, "companyId", "Company Equipment ID", "emissionUnit:"
				+ epaEmuId);
		requiredField(regulatedUserDsc, "regulatedUserDsc",
				"Company Equipment Description", "emissionUnit:" + epaEmuId);

		if (internal) {
			requiredField(euDesc, "euDesc", "AQD Description", "emissionUnit:"
					+ epaEmuId);
			if (ok && !operatingStatusCd.equals(EuOperatingStatusDef.SD)
					&& !operatingStatusCd.equals(EuOperatingStatusDef.IV)) {
				validateEpEmuId();
			}
		} else {
			Float result1 = new Float(0.0);
			try {
				if (designCapacityUnitsVal != null) {
					String format = "##,##0.##";

					DecimalFormat decFormat = new DecimalFormat(format);
					result1 = decFormat.parse(designCapacityUnitsVal)
							.floatValue();

					checkRangeValues(result1, new Float(0.1), null,
							"designCapacityUnitsVal", "Design Capacity");
				}
			} catch (ParseException e) {
				logger.error("Invalid Design Capacity: "
						+ designCapacityUnitsVal);
			} catch (IllegalArgumentException ie) {
				logger.error("Invalid Design Capacity: "
						+ designCapacityUnitsVal);
			}
		}

		if (ok) {
			// validate emission unit type data
			if (emissionUnitType != null) {
				emissionUnitType.setEmissionUnitTypeCd(emissionUnitTypeCd);
				ValidationMessage[] euTypeValMsgs = emissionUnitType.validate();

				for (ValidationMessage valMsg : euTypeValMsgs) {
					valMsg.setReferenceID("emissionUnit:" + epaEmuId);
					validationMessages.put(valMsg.getProperty() + ":1", valMsg);
				}
				validateEmissionUnitReplacements();
				validateEngineEmissionUnitSerialNumbers();

			}

			// validation EU's operating status
			checkOpStatus(facPermitClassCd);
		}

		validateEuEmissionsTable();

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public final ValidationMessage[] validateOnlyEuEmissionsTable() {
		validationMessages.clear();

		validateEuEmissionsTable();

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public final void setEmissionProcesses(
			List<EmissionProcess> emissionProcesses) {
		this.emissionProcesses = emissionProcesses;
	}

	public final Long getEuInitInstallDateLong() {
		if (euInitInstallDate == null) {
			return null;
		}
		return euInitInstallDate.getTime();
	}

	public final void setEuInitInstallDateLong(Long euInitInstallDateLong) {
		if (euInitInstallDateLong != null) {
			euInitInstallDate = new Timestamp(euInitInstallDateLong);
		}
	}

	public final Long getEuInitStartupDateLong() {
		if (euInitStartupDate == null) {
			return null;
		}
		return euInitStartupDate.getTime();
	}

	public final void setEuInitStartupDateLong(Long euInitStartupDateLong) {
		if (euInitStartupDateLong != null) {
			euInitStartupDate = new Timestamp(euInitStartupDateLong);
		}
	}

	public final Long getEuInstallDateLong() {
		if (euInstallDate == null) {
			return null;
		}
		return euInstallDate.getTime();
	}

	public final void setEuInstallDateLong(Long euInstallDateLong) {
		if (euInstallDateLong != null) {
			euInstallDate = new Timestamp(euInstallDateLong);
		}
	}

	public final Long getEuStartupDateLong() {
		if (euStartupDate == null) {
			return null;
		}
		return euStartupDate.getTime();
	}

	public final void setEuStartupDateLong(Long euStartupDateLong) {
		if (euStartupDateLong != null) {
			euStartupDate = new Timestamp(euStartupDateLong);
		}
	}

	// Copy the data that not show in the portal
	public void copyEmissionUnitData(EmissionUnit dapcEmissionUnit) {
		setEuDesc(dapcEmissionUnit.getEuDesc()); // AQD description
		setEuEmissions(dapcEmissionUnit.getEuEmissions());
		setPtiStatusCd(dapcEmissionUnit.getPtiStatusCd());
		setPtioStatusCd(dapcEmissionUnit.getPtioStatusCd());
		setEuEmissions(dapcEmissionUnit.getEuEmissions());
		if (EuOperatingStatusDef.SD.equals(dapcEmissionUnit
				.getOperatingStatusCd())) {
			setOperatingStatusCd(EuOperatingStatusDef.SD);
			setEuShutdownDate(dapcEmissionUnit.getEuShutdownDate());
			setEuShutdownNotificationDate(dapcEmissionUnit
					.getEuShutdownNotificationDate());
		}
	}

	public Integer getCorrEpaEmuId() {
		return CorrEpaEmuId;
	}

	public void setCorrEpaEmuId(Integer corrEpaEmuId) {
		CorrEpaEmuId = corrEpaEmuId;
	}

	// called from:
	// - internal eu clone
	public EmissionUnit cloneEmissionUnit(boolean internalApp) {
		EmissionUnit cloneEu = new EmissionUnit();

		newObject = false;
		cloneEu.setCompanyId(companyId);
		cloneEu.setRegulatedUserDsc(regulatedUserDsc);
		cloneEu.setOperatingStatusCd(operatingStatusCd);
		cloneEu.setOrigOperatingStatusCd(origOperatingStatusCd);
		cloneEu.euEmissions.addAll(euEmissions);
		cloneEu.setEmissionUnitType(emissionUnitType);
		cloneEu.setEmissionUnitTypeCd(emissionUnitTypeCd);
		cloneEu.setEuInitInstallDate(euInitInstallDate);
		cloneEu.setEuInitStartupDate(euInitStartupDate);
		cloneEu.setEuInstallDate(euInstallDate);
		cloneEu.setEuStartupDate(euStartupDate);
		cloneEu.setEmissionUnitReplacements(emissionUnitReplacements);
		if (internalApp) {
			cloneEu.setEuDesc(euDesc);
		}

		requiredField(emissionUnitTypeCd, "emissionUnitType",
				"Emissions Unit Type");

		return cloneEu;
	}

	public EmissionProcess getEmissionProcess(EmissionProcess ep) {
		EmissionProcess ret = null;
		for (EmissionProcess tempEP : emissionProcesses) {
			if (tempEP.getCorrelationId().equals(ep.getCorrelationId())) {
				ret = tempEP;
				break;
			}
		}
		return ret;
	}

	public EmissionProcess getEmissionProcess(String processID) {
		EmissionProcess ret = null;
		for (EmissionProcess tempEP : emissionProcesses) {
			if (tempEP.getProcessId().equals(processID)) {
				ret = tempEP;
				break;
			}
		}
		return ret;
	}

	public String getOrigOperatingStatusCd() {
		return origOperatingStatusCd;
	}

	public void setOrigOperatingStatusCd(String origOperatingStatusCd) {
		this.origOperatingStatusCd = origOperatingStatusCd;
	}

	public Long getEuShutdownDateLong() {
		if (euShutdownDate == null) {
			return null;
		}
		return euShutdownDate.getTime();
	}

	public void setEuShutdownDateLong(Long euShutdownDateLong) {
		if (euShutdownDateLong != null) {
			euShutdownDate = new Timestamp(euShutdownDateLong);
		}
	}

	public Long getEuShutdownNotificationDateLong() {
		if (euShutdownNotificationDate == null) {
			return null;
		}
		return euShutdownNotificationDate.getTime();
	}

	public void setEuShutdownNotificationDateLong(
			Long euShutdownNotificationDateLong) {
		if (euShutdownNotificationDateLong != null) {
			euShutdownNotificationDate = new Timestamp(
					euShutdownNotificationDateLong);
		}
	}

	public final String getEquipmentDescription() {
		return equipmentDescription;
	}

	public final void setEquipmentDescription(String equipmentDescription) {
		this.equipmentDescription = equipmentDescription;
	}

	public String getDesignCapacityUnitsVal() {
		return designCapacityUnitsVal;
	}

	public void setDesignCapacityUnitsVal(String designCapacityUnitsVal) {
		this.designCapacityUnitsVal = designCapacityUnitsVal;
	}

	public final String getFacPrintEuDesc() {
		if (euDesc != null && !euDesc.equals("")) {
			return ": " + euDesc;
		}

		return "";
	}

	public final String getMouseOverTip() {
		return mouseOverTip;
	}

	public final boolean isDemEgPntModeling() {
		return demEgPntModeling;
	}

	public final void setDemEgPntModeling(boolean demEgPntModeling) {
		this.demEgPntModeling = demEgPntModeling;
	}

	public final boolean isPsdOrScreenModeling() {
		return psdOrScreenModeling;
	}

	public final void setPsdOrScreenModeling(boolean psdOrScreenModeling) {
		this.psdOrScreenModeling = psdOrScreenModeling;
	}

	private ArrayList<EmissionUnitTypeDef> retrieveEmissionUnitTypes() {

		try {
//			FacilityDAO dao = (FacilityDAO) DAOFactory.getDAO("FacilityDAO"); //TODO dbobject calling dao
			FacilityDAO dao = (FacilityDAO) App.getApplicationContext().getBean("readOnlyFacilityDAO");

			
			return dao.retrieveEmissionUnitTypeDefs();
		} catch (DAOException e) {
			logger.error("EmissionUnit.retrieveEmissionUnitTypes Error", e);
		}

		return null;
	}

	private void refreshEmissionUnitTypeName() {
		if (Utility.isNullOrEmpty(this.emissionUnitTypeCd))
			return;
		
		String name =  EmissionUnitTypeDef.getData().getItems().getItemDesc(this.emissionUnitTypeCd);
		
		if(name != null) {
			this.emissionUnitTypeName = name;
			return;
		}
		
//		for (EmissionUnitTypeDef type : this.emissionUnitTypes) {
//			if (type.getEmissionUnitTypeCd().equals(this.emissionUnitTypeCd)) {
//				this.emissionUnitTypeName = type.getEmissionUnitTypeName();
//				return;
//			}
//		}
	}

	public EmissionUnitType getEmissionUnitType() {
		return emissionUnitType;
	}

	public void setEmissionUnitType(EmissionUnitType emissionUnitType) {
		if (emissionUnitType == null) {
			emissionUnitType = new EmissionUnitType();
		}

		this.emissionUnitType = emissionUnitType;
	}

	public void setEmissionUnitReplacements(
			List<EmissionUnitReplacement> emissionUnitReplacements) {
		this.emissionUnitReplacements = emissionUnitReplacements;
	}

	public List<EmissionUnitReplacement> getEmissionUnitReplacements() {
		return this.emissionUnitReplacements;
	}

	public final void addEmissionUnitReplacement(
			EmissionUnitReplacement emissionUnitReplacement) {
		if (emissionUnitReplacement != null) {
			emissionUnitReplacements.add(emissionUnitReplacement);
		}
	}

	public final void removeEmissionUnitReplacement(
			EmissionUnitReplacement emissionUnitReplacement) {
		emissionUnitReplacements.remove(emissionUnitReplacement);
	}

	@Override
	public int compareTo(EmissionUnit o) {
		return this.epaEmuId.compareTo(o.getEpaEmuId());
	}

	public boolean isReplacementType() {
		if (this.emissionUnitTypeCd.isEmpty())
			return false;

		return this.getEmissionUnitTypeCd().equals("ENG")
				|| this.getEmissionUnitTypeCd().equals("BOL")
				|| this.getEmissionUnitTypeCd().equals("CSH")
				|| this.getEmissionUnitTypeCd().equals("CKD");

	}
	
	public boolean isNonEngineReplacement() {
		if (this.emissionUnitTypeCd.isEmpty())
			return false;

		return this.getEmissionUnitTypeCd().equals("BOL")
				|| this.getEmissionUnitTypeCd().equals("CSH")
				|| this.getEmissionUnitTypeCd().equals("CKD");

	}
	
	public boolean isEngineReplacement() {
		if (this.emissionUnitTypeCd.isEmpty())
			return false;

		return this.getEmissionUnitTypeCd().equalsIgnoreCase("ENG");

	}

	public ValidationMessage[] checkOpStatus(String facPermitClassCd) {
		List<ValidationMessage> msgs = new ArrayList<ValidationMessage>();
		ValidationMessage msg = null;
		if (operatingStatusCd.equals(EuOperatingStatusDef.SD)) {
			if (euShutdownDate == null) {
				msg = new ValidationMessage(
						"euShutdownDate",
						"Emission Unit is shutdown; Shutdown date must be provided",
						ValidationMessage.Severity.ERROR, "emissionUnit:"
								+ epaEmuId, epaEmuId);
				validationMessages.put("euShutdownDate:1", msg);
				msgs.add(msg);
			} else {
				validationMessages.remove("euShutdownDate:1");
				if (!euShutdownDate.before(new Timestamp(System
						.currentTimeMillis()))) {
					msg = new ValidationMessage(
							"euShutdownDate",
							"Emission Unit is shutdown; Shutdown date must be in the past",
							ValidationMessage.Severity.ERROR, "emissionUnit:"
									+ epaEmuId, epaEmuId);
					validationMessages.put("euShutdownDate:2", msg);
					msgs.add(msg);
				} else if (euShutdownDate != null && euInitInstallDate != null
						&& euShutdownDate.before(euInitInstallDate)) {
					msg = new ValidationMessage(
							"euShutdownDate",
							"Emission Unit is shutdown; Shutdown date cannot be before \"Initial Construction Commencement Date\"",
							ValidationMessage.Severity.ERROR, "emissionUnit:"
									+ epaEmuId, epaEmuId);
					validationMessages.put("euShutdownDate:3", msg);
					msgs.add(msg);
				} else {
					validationMessages.remove("euShutdownDate:2");
				}
			}

			if (facPermitClassCd != null && facPermitClassCd.equals("tv")) {
				if (euShutdownNotificationDate == null) {
					msg = new ValidationMessage(
							"euShutdownNotificationDate",
							"Emission Unit is shutdown and facility is Title V; Shutdown Notification date must be provided",
							ValidationMessage.Severity.ERROR, "emissionUnit:"
									+ epaEmuId, epaEmuId);
					validationMessages.put("euShutdownNotificationDate:1", msg);
					msgs.add(msg);
				} else {
					validationMessages.remove("euShutdownNotificationDate:1");
					if (!euShutdownNotificationDate.before(new Timestamp(System
							.currentTimeMillis()))) {
						msg = new ValidationMessage(
								"euShutdownNotificationDate",
								"Emission Unit is shutdown and facility is Title V; Shutdown Notification date must be in the past",
								ValidationMessage.Severity.ERROR,
								"emissionUnit:" + epaEmuId, epaEmuId);
						validationMessages.put("euShutdownNotificationDate:2",
								msg);
						msgs.add(msg);
					} else {
						validationMessages
								.remove("euShutdownNotificationDate:2");
					}
				}
			} //else {  IMPACT should not do this validation, as it interferes with submitting EIs that
			    // became non-title V during the reporting year.
				//if (euShutdownNotificationDate != null) {
					//msg = new ValidationMessage(
					//		"euShutdownNotificationDate",
					//		"Emission Unit is shutdown and facility is not  Title V; Shutdown Notification date must not be provided",
					//		ValidationMessage.Severity.ERROR, "emissionUnit:"
					//				+ epaEmuId, epaEmuId);
					//validationMessages.put("euShutdownNotificationDate:3", msg);
					//msgs.add(msg);
				//} else {
				//	validationMessages.remove("euShutdownNotificationDate:3");
				//}
			//}
		} else {
			if (operatingStatusCd.equals(EuOperatingStatusDef.IV)) {
				// don't force changing EPA ID. It is invalid EU anyway.
				validationMessages.remove(EPAEMUMSGID);
				EmissionProcess[] euProcs = this.getEmissionProcesses()
						.toArray(new EmissionProcess[0]);
				if (euProcs != null && euProcs.length > 0) {
					msg = new ValidationMessage(
							"operatingStatusCd",
							"Emission Unit has processes; Cannot change state. Please delete processes",
							ValidationMessage.Severity.ERROR, "emissionUnit:"
									+ epaEmuId, epaEmuId);
					validationMessages.put("operatingStatusCd:1", msg);
					msgs.add(msg);
				} else {
					validationMessages.remove("operatingStatusCd:1");
				}
			} else if (operatingStatusCd.equals(EuOperatingStatusDef.NI)) {
				if (euStartupDate != null && !euStartupDate.equals("")) {
					msg = new ValidationMessage(
							"euStartupDate",
							"If EU operating status is \"Not yet installed\", you cannot enter a date for \"Most Recent Operation Commencement Date\"",
							ValidationMessage.Severity.ERROR, "emissionUnit:"
									+ epaEmuId, epaEmuId);
					validationMessages.put("euStartupDate", msg);
					msgs.add(msg);
				} else {
					validationMessages.remove("euStartupDate");
				}
			} else if (operatingStatusCd.equals(EuOperatingStatusDef.OP)) {
				if (euInitInstallDate == null && requiresEmissionsUnitDates()) {
					msg = new ValidationMessage(
							"euInitInstallDate",
							"Because the EU is operating, you must enter a date for \"Initial Construction Commencement Date\"",
							ValidationMessage.Severity.ERROR, "emissionUnit:"
									+ epaEmuId, epaEmuId);
					validationMessages.put("OPeuInitInstallDate", msg);
					msgs.add(msg);
				}
				if (euInitStartupDate == null && requiresEmissionsUnitDates()) {
					msg = new ValidationMessage(
							"euInitStartupDate",
							"Because the EU is operating, you must enter a date for \"Initial Operation Commencement Date\"",
							ValidationMessage.Severity.ERROR, "emissionUnit:"
									+ epaEmuId, epaEmuId);
					validationMessages.put("OPeuInitStartupDate", msg);
					msgs.add(msg);
				}
//				if (euInstallDate == null && requiresEmissionsUnitDates()) {
//					msg = new ValidationMessage(
//							"euInstallDate",
//							"Because the EU is operating, you must enter a date for \"Most Recent Construction/Modification Commencement Date\"",
//							ValidationMessage.Severity.ERROR, "emissionUnit:"
//									+ epaEmuId, epaEmuId);
//					validationMessages.put("OPeuInstallDate", msg);
//					msgs.add(msg);
//				}
//				if (euStartupDate == null && requiresEmissionsUnitDates()) {
//					msg = new ValidationMessage(
//							"euStartupDate",
//							"Because the EU is operating, you must enter a date for \"Most Recent Operation Commencement Date\"",
//							ValidationMessage.Severity.ERROR, "emissionUnit:"
//									+ epaEmuId, epaEmuId);
//					validationMessages.put("OPeuStartupDate", msg);
//					msgs.add(msg);
//				}
				if (euStartupDate != null && euInitInstallDate != null
						&& euStartupDate.before(euInitInstallDate)) {
					msg = new ValidationMessage(
							"euStartupDate",
							"The \"Most Recent Operation Commencement Date\" cannot be before \"Initial Construction Commencement Date\"",
							ValidationMessage.Severity.ERROR, "emissionUnit:"
									+ epaEmuId, epaEmuId);
					validationMessages.put("OPorder", msg);
					msgs.add(msg);
				}
				if (euInitStartupDate != null && euInitInstallDate != null
						&& euInitStartupDate.before(euInitInstallDate)) {
					msg = new ValidationMessage(
							"euInitStartupDate",
							"The \"Initial Operation Commencement Date\" cannot be before \"Initial Construction Commencement Date\"",
							ValidationMessage.Severity.ERROR, "emissionUnit:"
									+ epaEmuId, epaEmuId);
					validationMessages.put("OPorder_init_startup_dt", msg);
					msgs.add(msg);
				}
				if (euInstallDate != null && euInitInstallDate != null
						&& euInstallDate.before(euInitInstallDate)) {
					msg = new ValidationMessage(
							"euInstallDate",
							"The \"Most Recent Construction/Modification Commencement Date\" cannot be before \"Initial Construction Commencement Date\"",
							ValidationMessage.Severity.ERROR, "emissionUnit:"
									+ epaEmuId, epaEmuId);
					validationMessages.put("OPorder_install_dt", msg);
					msgs.add(msg);
				}
				if (euStartupDate != null && euInitStartupDate != null
						&& euStartupDate.before(euInitStartupDate)) {
					msg = new ValidationMessage(
							"euStartupDate",
							"The \"Most Recent Operation Commencement Date\" cannot be before \"Initial Operation Commencement Date\"",
							ValidationMessage.Severity.ERROR, "emissionUnit:"
									+ epaEmuId, epaEmuId);
					validationMessages.put("OPorder_startup_dt", msg);
					msgs.add(msg);
				}
				if (euStartupDate != null && euInstallDate != null
						&& euStartupDate.before(euInstallDate)) {
					msg = new ValidationMessage(
							"euStartupDate",
							"The \"Most Recent Operation Commencement Date\" cannot be before \"Most Recent Construction/Modification Commencement Date\"",
							ValidationMessage.Severity.ERROR, "emissionUnit:"
									+ epaEmuId, epaEmuId);
					validationMessages.put("OPorder_install_dt_startup_dt", msg);
					msgs.add(msg);
				}
			}

			if (euShutdownDate != null) {
				msg = new ValidationMessage(
						"euShutdownDate",
						"Emission Unit is not shutdown; Shutdown date must not be provided",
						ValidationMessage.Severity.ERROR, "emissionUnit:"
								+ epaEmuId, epaEmuId);
				validationMessages.put("euShutdownDate:2", msg);
				msgs.add(msg);
			} else {
				validationMessages.remove("euShutdownDate:2");
			}

			if (euShutdownNotificationDate != null) {
				msg = new ValidationMessage(
						"euShutdownNotificationDate",
						"Emission Unit is not shutdown; Shutdown Notification date must not be provided",
						ValidationMessage.Severity.ERROR, "emissionUnit:"
								+ epaEmuId, epaEmuId);
				validationMessages.put("euShutdownNotificationDate:2", msg);
				msgs.add(msg);
			} else {
				validationMessages.remove("euShutdownNotificationDate:2");
			}

		}

		return msgs.toArray(new ValidationMessage[0]);
	}
	
	public ValidationMessage[] checkEuEmissionsTable(){
		this.clearValidationMessages();
		validateEuEmissionsTable();
		return this.validate();
	}
	
	public ValidationMessage[] checkEmissionUnitReplacements() {
		this.clearValidationMessages();
		validateEmissionUnitReplacements();
		return this.validate();
	}
	
	public ValidationMessage[] checkEngineEmissionUnitSerialNumbers() {
		this.clearValidationMessages();
		validateEngineEmissionUnitSerialNumbers();
		
		
		return this.validate();
	}
	
	private boolean requiresEmissionsUnitDates() {
		boolean ret = true;
		String euType = getEmissionUnitTypeCd();
		if (!Utility.isNullOrEmpty(euType)) {
			if (euType.equalsIgnoreCase(EmissionUnitTypeDef.BVC)
					|| euType.equalsIgnoreCase(EmissionUnitTypeDef.LUD)
					|| euType.equalsIgnoreCase(EmissionUnitTypeDef.FUG)) {
				ret = false;
			}
		}
		
		return ret;
	}
	
	public boolean isEmissionsUnitDatesRequired() {
		boolean ret = false;

		if (operatingStatusCd.equals(EuOperatingStatusDef.OP)
				&& requiresEmissionsUnitDates()) {
			ret = true;
		}

		return ret;
	}

	public List<EmissionUnitReplacement> getCurrentEngineSerialNumbers() {
		currentEngineSerialNumbers = new ArrayList<EmissionUnitReplacement>();
		if (this.getEmissionUnitReplacements() != null
				&& !this.getEmissionUnitReplacements().isEmpty()) {
			for (EmissionUnitReplacement serialNumber : this
					.getEmissionUnitReplacements()) {
				if (serialNumber.isSerialNumberCurrent()) {
					this.addCurrentEngineSerialNumbers(serialNumber);
				}
			}
		}
		
		return currentEngineSerialNumbers;
	}

	public void setCurrentEngineSerialNumbers(
			List<EmissionUnitReplacement> currentEngineSerialNumbers) {
		this.currentEngineSerialNumbers = currentEngineSerialNumbers;
	}
	
	public final void addCurrentEngineSerialNumbers(
			EmissionUnitReplacement engineSerialNumber) {
		if (engineSerialNumber != null) {
			currentEngineSerialNumbers.add(engineSerialNumber);
		}
	}

	public final void removeCurrentEngineSerialNumbers(
			EmissionUnitReplacement engineSerialNumber) {
		currentEngineSerialNumbers.remove(engineSerialNumber);
	}
}
