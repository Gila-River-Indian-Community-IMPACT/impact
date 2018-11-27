package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ApiGroup;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEU;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEUGroup;
import us.oh.state.epa.stars2.def.AirProgramDef;
import us.oh.state.epa.stars2.def.ContactTypeDef;
import us.oh.state.epa.stars2.def.EmissionReportsDef;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.FacilityTypeDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.PermitStatusDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.util.LocationCalculator;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.TimestampUtil;
import us.oh.state.epa.stars2.util.TimestampUtilException;
import us.oh.state.epa.stars2.webcommon.ContactUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.facility.FacilityProfileBase;
import us.oh.state.epa.stars2.webcommon.facility.HCAnalysisSampleDetailRow;
import us.wy.state.deq.impact.database.dbObjects.company.Company;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroup;
import us.wy.state.deq.impact.def.PermittedEmissionsUnitDef;

/**
 * @author Kbradley Note: (TO DO LATER) we may derive this from FacilityList
 *         later
 */
public class Facility extends FacilityNode {
	
	private static final long serialVersionUID = 5750449036024605024L;

	private OperatingStatusDef operatingStatusDef;
	
	private PermitClassDef permitClassDef;
	
	private String name;
	private String desc;
	private String facilityTypeDsc;
	private Contact billingContact;
	private Contact complianceContact;
	private Contact emissionsContact;
	private Contact environmentalContact;
	private Contact monitoringContact;
	private Contact onSiteOperatorContact;
	private Contact permittingContact;
	private Contact respOfficialContact;
	private Contact primaryContact;
	private Address phyAddr;
	private String operatingStatusCd;
	private String reportingTypeCd;
	private String permitClassCd;
	private String transitStatusCd;
	private String doLaaCd;
	private Timestamp startDate;
	private Timestamp endDate;
	@SuppressWarnings("unused")
	private Long startDateLong;
	@SuppressWarnings("unused")
	private Long endDateLong;
	private Timestamp shutdownDate;
	@SuppressWarnings("unused")
	private Long shutdownDateLong;
	private Timestamp inactiveDate;
	@SuppressWarnings("unused")
	private Long inactiveDateLong;
	private Timestamp shutdownNotifDate;
	private boolean shutdownNotifEnabled;
	private String tvCertRepdueDate;
	private String portableGroupCd;
	private String portableGroupTypeCd;
	private boolean portable;
	private String api;
	private Integer maxVersion;
	private Integer corePlaceId;
	private boolean mact;
	private boolean neshaps;
	private boolean nsps;
	private boolean psd;
	private boolean nsrNonattainment;
	private boolean tivInd;
	private boolean ghgInd;
	private String permitStatusCd;
	private boolean sec112;
	private boolean intraStateVoucherFlag;
	private boolean copyOnChange;
	private boolean multiEstabFacility;
	private boolean validated;
	private boolean submitted;
	private boolean tvTypeA;
	private String federalSCSCId;
	private boolean maxVersionFlag = true;
	private String lastSubmissionType;
	private Integer lastSubmissionVersion;
	private FacilityOwner owner;
	private Integer ownerCompanyId;
	private String facilityTypeCd;
	private String companyName;
	private boolean hasApis;
	private String afs;

	private List<ApiGroup> apis = new ArrayList<ApiGroup>(0);

	private List<String> mactSubparts = new ArrayList<String>(0);
	private List<String> neshapsSubparts = new ArrayList<String>(0);
	private List<String> nspsSubparts = new ArrayList<String>(0);
	private List<String> sicCds = new ArrayList<String>(0);
	private List<String> naicsCds = new ArrayList<String>(0);
	private List<String> notificationCounties = new ArrayList<String>(0);
	private List<EmissionUnit> emissionUnits = new ArrayList<EmissionUnit>(0);
	private List<ControlEquipment> controlEquips = new ArrayList<ControlEquipment>(
			0);
	private List<EgressPoint> egrPoints = new ArrayList<EgressPoint>(0);
	private HashMap<Integer, ControlEquipment> unassignedCntEquips = new HashMap<Integer, ControlEquipment>(
			0);
	private HashMap<Integer, EgressPoint> unassignedEgrPoints = new HashMap<Integer, EgressPoint>(
			0);
	private List<Contact> allContacts = new ArrayList<Contact>(0);
	// TODO Clean up owner-contact
	// private HashMap<Integer, Contact> owners = new HashMap<Integer,
	// Contact>(0);
	private HashMap<String, Company> owners = new HashMap<String, Company>(0);
	private HashMap<Integer, Contact> operators = new HashMap<Integer, Contact>(
			0);
	private HashMap<Integer, Contact> responsibleOfficals = new HashMap<Integer, Contact>(
			0);
	private HashMap<String, FacilityRole> facilityRoles = new HashMap<String, FacilityRole>(
			0);
	private ArrayList<FacilityNote> notes = new ArrayList<FacilityNote>(0);
	private HashMap<String, FacilityEmission> facilityEmissions = new HashMap<String, FacilityEmission>(
			0);
	private List<Address> addresses = new ArrayList<Address>(0);
	private HashMap<Integer, Integer> copyOnChangeFpNodeIds;
	private HashMap<Integer, Integer> copyOnChangeEuIds;
	private boolean staging;
	private Attachment attestationDocument;

	// New fields for CETA
	private String govtFacilityTypeCd;
	private String airProgramCd; // for air program TV or SM/FESOP
	private String airProgramCompCd;
	private String sipCompCd;
	private String mactCompCd;
	private transient String oldMactCompCd;
	private transient String oldAirProgramCompCd;
	private transient String oldSipCompCd;
	private transient boolean oldMact;
	private transient boolean oldNeshaps;
	private transient boolean oldNsps;
	private transient boolean oldPsd;
	private transient boolean oldNsrNonattainment;
	private transient Integer oldCorePlaceId;
	private transient boolean tvInd;
	private transient boolean smInd;
	private List<PollutantCompCode> neshapsSubpartsCompCds = new ArrayList<PollutantCompCode>(
			0);
	private String cerrClassCd;
	private transient String origOperatingStatusCd;
	
	private List<Contact> billingContacts = new ArrayList<Contact>();
	private List<Contact> complianceContacts = new ArrayList<Contact>();
	private List<Contact> emissionsContacts = new ArrayList<Contact>();
	private List<Contact> environmentalContacts = new ArrayList<Contact>();
	private List<Contact> monitoringContacts = new ArrayList<Contact>();
	private List<Contact> onSiteOperatorContacts = new ArrayList<Contact>();
	private List<Contact> permittingContacts = new ArrayList<Contact>();
	private List<Contact> responsibleOfficialContacts = new ArrayList<Contact>();
	
	private Contact nsrBillingContact;
	private List<Contact> nsrBillingContacts = new ArrayList<Contact>();
	
	private MonitorGroup associatedMonitorGroup;
	
	private List<FacilityCemComLimit> _facilityCemComLimitList = new ArrayList<FacilityCemComLimit>(0);
	
	private List<ContinuousMonitor> _facilityContinuousMonitorList = new ArrayList<ContinuousMonitor>(0);

	private String aqdEmissionFactorGroupCd;
	//Extended Hydrocarbon Analysis
	
	private List<HydrocarbonAnalysisPollutant> hydrocarbonPollutantList = new ArrayList<HydrocarbonAnalysisPollutant>();
	private HydrocarbonAnalysisSampleDetail hydrocarbonAnalysisSampleDetail;	
	
	private DecaneProperties decaneProperties;
	
	//Administrative Hold 
	private boolean administrativeHold;


	public Facility() {
		super();
		this.setDoLaaCd(SystemPropertyDef.getSystemPropertyValue("districtCd",null));
		phyAddr = new Address();
		requiredFields();
	}

	// This constructor used only in GenerateBulkEmRptReminder.java
	public Facility(FacilityList fL) {
		super();
		this.setFacilityId(fL.getFacilityId());
		this.phyAddr = fL.getPhyAddr();
		this.doLaaCd = fL.getDoLaaCd();
		this.operatingStatusCd = fL.getOperatingStatusCd();
		this.reportingTypeCd = fL.getReportingTypeCd();
		this.permitClassCd = fL.getPermitClassCd();
		this.permitStatusCd = fL.getPermitStatusCd();
		/* this.portable = fL.isPortable(); */
		this.setVersionId(fL.getVersionId());
		this.name = fL.getName();
		this.facilityTypeCd = fL.getFacilityTypeCd();
		this.companyName = fL.getCompanyName();
	}
	
	public ValidationMessage[] requiredFields() {
		
		// Need to change null values for the following fields in order to pass
		// validation.
		if (permitClassCd == null) {
			permitClassCd = "ntv";
		}
		if (transitStatusCd == null) {
			transitStatusCd = "none";
		}
		if (permitStatusCd == null) {
			permitStatusCd = "N";
		}

		// requiredField(ownerCompanyId, "owner", "Owner", "facility");
		requiredField(operatingStatusCd, "facOperatingStatus",
				"Operating Status", "facility");
		requiredField(name, "name", "Facility Name", "facility");
		requiredField(facilityTypeCd, "facilityTypeCd", "Facility Type",
				"facility");
		requiredField(permitClassCd, "facPermitClassCd", "Facility class",
				"facility");
		requiredField(transitStatusCd, "facTransitStatusCd",
				"Transitional Status", "facility");
		requiredField(permitStatusCd, "facPermitStatusCd",
				"Title V Permit Status", "facility");
		
		return validationMessages.values().toArray(new ValidationMessage[0]);
	}

	public void incinerateEquipment(Facility newFacility,
			boolean includeProcesses) {
		setControlEquips(new ArrayList<ControlEquipment>(0));
		setUnassignedCntEquips(new HashMap<Integer, ControlEquipment>(0));
		setEgrPoints(new ArrayList<EgressPoint>(0));
		setUnassignedEgrPoints(new HashMap<Integer, EgressPoint>(0));
		for (EmissionUnit eu : emissionUnits) {
			if (includeProcesses) {
				eu.setEmissionProcesses(new ArrayList<EmissionProcess>(0));
			} else {
				for (EmissionProcess p : eu.getEmissionProcesses()) {
					p.setControlEquipments(new ArrayList<ControlEquipment>(0));
					p.setEgressPoints(new ArrayList<EgressPoint>(0));
				}
			}
		}
	}

	public OperatingStatusDef getOperatingStatusDef() {
		return operatingStatusDef;
	}

	public void setOperatingStatusDef(OperatingStatusDef operatingStatusDef) {
		this.operatingStatusDef = operatingStatusDef;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public final boolean isMultiEstabFacility() {
		return multiEstabFacility;
	}

	public final void setMultiEstabFacility(boolean multiEstabFacility) {
		this.multiEstabFacility = multiEstabFacility;
	}

	public final HashMap<Integer, Integer> getCopyOnChangeFpNodeIds() {
		return copyOnChangeFpNodeIds;
	}

	public final void setCopyOnChangeFpNodeIds(
			HashMap<Integer, Integer> copyOnChangeFpNodeIds) {
		this.copyOnChangeFpNodeIds = copyOnChangeFpNodeIds;
	}

	public final HashMap<Integer, Integer> getCopyOnChangeEuIds() {
		return copyOnChangeEuIds;
	}

	public final void setCopyOnChangeEuIds(
			HashMap<Integer, Integer> copyOnChangeEuIds) {
		this.copyOnChangeEuIds = copyOnChangeEuIds;
	}

	public final boolean isCntEquipAssigned(Integer inFpNodeId) {
		if (unassignedCntEquips.containsKey(inFpNodeId)) {
			return false;
		}

		return true;
	}

	public final boolean isEgressPointAssigned(Integer inFpNodeId) {
		if (unassignedEgrPoints.containsKey(inFpNodeId)) {
			return false;
		}

		return true;
	}

	public final String getSecondNotifCountyCd() {
		if (notificationCounties.size() > 0) {
			return notificationCounties.get(0);
		}

		return null;
	}

	public final void setSecondNotifCountyCd(String secondNotifCountyCd) {
		notificationCounties.clear();
		notificationCounties.add(0, secondNotifCountyCd);
	}

	public final String getFacilityTypeCd() {
		return facilityTypeCd;
	}

	public final void setFacilityTypeCd(String facilityTypeCd) {
		this.facilityTypeCd = facilityTypeCd;
		this.portable = AbstractDAO.translateIndicatorToBoolean(FacilityTypeDef
				.isPortable(facilityTypeCd));
		this.hasApis = FacilityTypeDef.hasApis(facilityTypeCd);
	}

	public boolean isHasApis() {
		return hasApis;
	}

	public void setHasApis(boolean hasApis) {
		this.hasApis = hasApis;
	}

	public String getAfs() {
		return afs;
	}

	public void setAfs(String afs) {
		this.afs = afs;
	}

	public List<ApiGroup> getApis() {
		return apis;
	}

	public void setApis(List<ApiGroup> apis) {
		this.apis = apis;
		boolean hasApis = FacilityTypeDef.hasApis(facilityTypeCd);
		this.hasApis = hasApis;
	}

	public final Integer getCorePlaceId() {
		return corePlaceId;
	}

	public final void setCorePlaceId(Integer corePlaceId) {
		this.corePlaceId = corePlaceId;
	}

	public final String getTvCertRepdueDate() {
		return tvCertRepdueDate;
	}

	public final void setTvCertRepdueDate(String tvCertRepdueDate) {
		this.tvCertRepdueDate = tvCertRepdueDate;
	}

	public final String getDoLaaCd() {
		return doLaaCd;
	}

	public final void setDoLaaCd(String doLaaCd) {
		this.doLaaCd = doLaaCd;
	}

	public final String getPermitStatusCd() {
		return permitStatusCd;
	}

	public final void setPermitStatusCd(String permitStatusCd) {
		fieldChangeEventLog("fpts", "N/A", PermitStatusDef.getData().getItems()
				.getItemDesc(this.permitStatusCd), PermitStatusDef.getData()
				.getItems().getItemDesc(permitStatusCd));
		this.permitStatusCd = permitStatusCd;
	}

	public final List<String> getNotificationCounties() {
		return notificationCounties;
	}

	public final void setNotificationCounties(List<String> notificationCounties) {
		this.notificationCounties = notificationCounties;
	}

	public final List<String> getNaicsCds() {
		return naicsCds;
	}

	public final void setNaicsCds(List<String> naicsCds) {
		this.naicsCds = naicsCds;
	}

	public final void addNaicsCd(String naicsCd) {
		if (naicsCds == null) {
			naicsCds = new ArrayList<String>(0);
		}
		naicsCds.add(naicsCd);
	}

	public final List<String> getSicCds() {
		return sicCds;
	}

	public final void setSicCds(List<String> sicCds) {
		this.sicCds = sicCds;
	}

	public final void addSicCd(String newCd) {
		if (sicCds == null) {
			sicCds = new ArrayList<String>(0);
		}
		sicCds.add(newCd);
	}

	public final String getOperatingStatusCd() {
		return operatingStatusCd;
	}

	public final void setOperatingStatusCd(String operatingStatusCd) {
		if (requiredField(operatingStatusCd, "facOperatingStatus",
				"Operating Status", "facility")) {
			checkDirty(
					"opst",
					getFacilityId(),
					OperatingStatusDef.getData().getItems()
							.getItemDesc(this.operatingStatusCd),
					OperatingStatusDef.getData().getItems()
							.getItemDesc(operatingStatusCd));
			fieldChangeEventLog(
					"fops",
					"N/A",
					OperatingStatusDef.getData().getItems()
							.getItemDesc(this.operatingStatusCd),
					OperatingStatusDef.getData().getItems()
							.getItemDesc(operatingStatusCd));

			if ((this.operatingStatusCd != null)
					&& (!this.operatingStatusCd.equals(OperatingStatusDef.SD))
					&& (operatingStatusCd.equals(OperatingStatusDef.SD))
					&& (permitClassCd != null && permitClassCd.equals("tv"))) {
				shutdownNotifEnabled = true;
			} else {
				shutdownNotifEnabled = false;
			}

			if (operatingStatusCd != null) {
				if (!operatingStatusCd.equals(OperatingStatusDef.SD)) {
					shutdownNotifDate = null;
					setShutdownDate(null);
				}
			}
		}
		this.operatingStatusCd = operatingStatusCd;
	}

	public final String getReportingTypeCd() {
		return reportingTypeCd;
	}

	public final void setReportingTypeCd(String reportingTypeCd) {
		this.reportingTypeCd = reportingTypeCd;
	}

	public final String getPermitClassCd() {
		return permitClassCd;
	}

	public final void setPermitClassCd(String permitClassCd) {
		this.fieldChangeEventLog("fpcd", "N/A", PermitClassDef.getData()
				.getItems().getItemDesc(this.permitClassCd), PermitClassDef
				.getData().getItems().getItemDesc(permitClassCd));
		checkDirty("fpcd", getFacilityId(), PermitClassDef.getData()
				.getItems().getItemDesc(this.permitClassCd),  PermitClassDef
				.getData().getItems().getItemDesc(permitClassCd));
		this.permitClassCd = permitClassCd;
		if (permitClassCd == null || !permitClassCd.equals("tv")) {
			setTvTypeA(false);
		}
	}

	public final String getTransitStatusCd() {
		return transitStatusCd;
	}

	public final void setTransitStatusCd(String transitStatusCd) {
		this.transitStatusCd = transitStatusCd;
	}

	//public final Contact[] getOtherContacts() {
	//	ArrayList<Contact> ret = new ArrayList<Contact>(0);

	//	for (Contact tempContact : allContacts) {
	//		if (tempContact.isContactType(ContactTypeDef.OTHR)) {
	//			ret.add(tempContact);
	//		}
	//	}

	//	return ret.toArray(new Contact[0]);
	//}

	//public final void addOtherContact(Contact otherContact) {
	//	ContactType newType = new ContactType();

	//	newType.setContactId(otherContact.getContactId());
	//	newType.setContactTypeCd(ContactTypeDef.OTHR);
	//	otherContact.addContactType(newType);
	//	this.allContacts.add(otherContact);
	//}

	public final Contact[] getBillingContacts() {
		ArrayList<Contact> ret = new ArrayList<Contact>(0);

		for (Contact tempContact : allContacts) {
			if (tempContact.isContactType(ContactTypeDef.BILL)) {
				ret.add(tempContact);
			}
		}

		return ret.toArray(new Contact[0]);
	}

	public final void addBillingContact(Contact contact) {
		addContact(contact, ContactTypeDef.BILL);
	}

	// TODO Clean up owner-contact
	// public final void addOwnerContact(Contact contact) {
	// contact = addContact(contact, ContactTypeDef.OWNR);
	//
	// this.owners.put(contact.getContactId(), contact);
	// }

	public final void addOwnerCompany(Company company) {

		this.owners.put(company.getCmpId(), company);
	}

	public final void addOperatorContact(Contact contact) {
		contact = addContact(contact, ContactTypeDef.ONST);

		this.operators.put(contact.getContactId(), contact);
	}

	private Contact addContact(Contact contact, String typeCd) {
		ContactType newType = new ContactType();

		newType.setContactId(contact.getContactId());
		newType.setContactTypeCd(typeCd);
		contact.addContactType(newType);
		this.allContacts.add(contact);

		return contact;
	}

	public final List<EgressPoint> getEgrPoints() {
		return egrPoints;
	}

	public final void setEgrPoints(List<EgressPoint> egrPoints) {
		this.egrPoints = egrPoints;
	}

	/*
	 * Note: Facility Roles will not be in Staging Area; So The interface is OK
	 * for XML Encoder
	 */
	public final HashMap<String, FacilityRole> getFacilityRoles() {
		return facilityRoles;
	}

	public final void setFacilityRoles(
			HashMap<String, FacilityRole> facilityRoles) {
		this.facilityRoles = facilityRoles;
	}

	public final String getFederalSCSCId() {
		return federalSCSCId;
	}

	public final void setFederalSCSCId(String federalSCSCId) {
		this.federalSCSCId = federalSCSCId;
	}

	public final boolean isIntraStateVoucherFlag() {
		return intraStateVoucherFlag;
	}

	public final void setIntraStateVoucherFlag(boolean intraStateVoucherFlag) {
		this.intraStateVoucherFlag = intraStateVoucherFlag;
	}

	public final boolean isMact() {
		return mact;
	}

	public final void setMact(boolean mact) {
		this.mact = mact;
	}

	public final boolean isCopyOnChange() {
		return copyOnChange;
	}

	public final void setCopyOnChange(boolean copyOnChange) {
		this.copyOnChange = copyOnChange;
	}

	public final List<String> getMactSubparts() {
		return mactSubparts;
	}

	public final void setMactSubparts(List<String> mactSubparts) {
		if (mactSubparts == null) {
			mactSubparts = new ArrayList<String>(0);
		}
		this.mactSubparts = mactSubparts;
	}

	public final boolean isNeshaps() {
		return neshaps;
	}

	public final void setNeshaps(boolean neshaps) {
		this.neshaps = neshaps;
	}

	/*
	 * Note: Facility Roles will not be in Staging Area; So The interface is OK
	 * for XML Encoder
	 */
	public final ArrayList<FacilityNote> getNotes() {
		return notes;
	}

	public final void setNotes(ArrayList<FacilityNote> notes) {
		if (notes == null) {
			notes = new ArrayList<FacilityNote>(0);
		}
		this.notes = notes;
	}

	public final boolean isNsps() {
		return nsps;
	}

	public final void setNsps(boolean nsps) {
		this.nsps = nsps;
	}

	public final List<String> getNspsSubparts() {
		return nspsSubparts;
	}

	public final void setNspsSubparts(List<String> nspsSubparts) {
		if (nspsSubparts == null) {
			nspsSubparts = new ArrayList<String>(0);
		}
		this.nspsSubparts = nspsSubparts;
	}

	public final boolean isPsd() {
		return psd;
	}

	public final void setPsd(boolean psd) {
		this.psd = psd;
	}

	public final boolean isNsrNonattainment() {
		return nsrNonattainment;
	}

	public final void setNsrNonattainment(boolean nsrNonattainment) {
		this.nsrNonattainment = nsrNonattainment;
	}

	public final boolean isTivInd() {
		return tivInd;
	}

	public final void setTivInd(boolean tivInd) {
		this.tivInd = tivInd;
	}

	public final Contact[] getOnSiteContacts() {
		ArrayList<Contact> ret = new ArrayList<Contact>(0);

		for (Contact tempContact : allContacts) {
			if (tempContact.isContactType(ContactTypeDef.ONST)) {
				ret.add(tempContact);
			}
		}

		return ret.toArray(new Contact[0]);
	}

	public final void addRespOffContact(Contact respOffContact) {
		ContactType newType = new ContactType();

		newType.setContactId(respOffContact.getContactId());
		newType.setContactTypeCd(ContactTypeDef.RSOF);
		respOffContact.addContactType(newType);
		this.allContacts.add(respOffContact);
	}

	public final void addOnSiteContact(Contact onSiteContact) {
		ContactType newType = new ContactType();

		newType.setContactId(onSiteContact.getContactId());
		newType.setContactTypeCd(ContactTypeDef.ONST);
		onSiteContact.addContactType(newType);
		this.allContacts.add(onSiteContact);
	}

	/*
	 * Note: should return Contact[] so that XML Encoder does not include this
	 * for encoding
	 */
	public final Contact[] getOperators() {
		return operators.values().toArray(new Contact[0]);
	}

	public final void setOperators(HashMap<Integer, Contact> operators) {
		if (operators == null) {
			operators = new HashMap<Integer, Contact>(0);
		}
		this.operators = operators;
	}

	/*
	 * Note: should return Contact[] so that XML Encoder does not include this
	 * for encoding
	 */
	public final Contact[] getOwners() {
		return owners.values().toArray(new Contact[0]);
	}

	// public final void setOwners(HashMap<Integer, Contact> owners) {
	// if (owners == null) {
	// owners = new HashMap<Integer, Contact>(0);
	// }
	// this.owners = owners;
	// }

	public final void setOwners(HashMap<String, Company> owners) {
		if (owners == null) {
			owners = new HashMap<String, Company>(0);
		}
		this.owners = owners;
	}

	public FacilityOwner getOwner() {
		return owner;
	}

	public void setOwner(FacilityOwner owner) {
		this.owner = owner;
	}

	public Integer getOwnerCompanyId() {
		return ownerCompanyId;
	}

	public void setOwnerCompanyId(Integer ownerCompanyId) {
		this.ownerCompanyId = ownerCompanyId;
	}

	public final Contact getBillingContact() {
		return billingContact;
	}
	
	public final void setBillingContact(Contact billingContact) {
		this.billingContact = billingContact;
	}
	
	public final Contact getComplianceContact() {
		return complianceContact;
	}
	
	public final void setComplianceContact(Contact complianceContact) {
		this.complianceContact = complianceContact;
	}
	
	public final Contact getEmissionsContact() {
		return emissionsContact;
	}
	
	public final void setEmissionsContact(Contact emissionsContact) {
		this.emissionsContact = emissionsContact;
	}
	
	public final Contact getEnvironmentalContact() {
		return environmentalContact;
	}
	
	public final void setEnvironmentalContact(Contact environmentalContact) {
		this.environmentalContact = environmentalContact;
	}
	
	public final Contact getMonitoringContact() {
		return monitoringContact;
	}
	
	public final void setMonitoringContact(Contact monitoringContact) {
		this.monitoringContact = monitoringContact;
	}
	
	public final Contact getOnSiteOperatorContact() {
		return onSiteOperatorContact;
	}
	
	public final void setOnSiteOperatorContact(Contact onSiteOperatorContact) {
		this.onSiteOperatorContact = onSiteOperatorContact;
	}
	
	public final Contact getPermittingContact() {
		return permittingContact;
	}
	
	public final void setPermittingContact(Contact permittingContact) {
		this.permittingContact = permittingContact;
	}
	
	public final Contact getRespOfficialContact() {
		return respOfficialContact;
	}
	
	public final void setRespOfficialContact(Contact respOfficialContact) {
		this.respOfficialContact = respOfficialContact;
	}

	/*
	 * Find contact active at specified date, return a copy of it with Start and
	 * End dates. If specified date is null, then return the contact currently
	 * active.
	 */
	public final ContactUtil getActiveContact(String type,
			Timestamp effectiveDate) {
		ContactUtil rtn = null;
		// set hours/min/sec/milli all to zero for equivalent comparison.
		Timestamp ed = effectiveDate;
		if (effectiveDate != null) {
			ed = dropPartialDay(effectiveDate);
		}
		loop: for (Contact c : allContacts) {
			for (ContactType t : c.getContactTypes()) {
				if (!t.getContactTypeCd().equals(type))
					continue;
				if (effectiveDate == null) {
					if (t.getEndDate() == null) {
						Contact con = new Contact(c);
						ContactUtil conU = new ContactUtil(con, t);
						rtn = conU;
						break loop;
					}
				} else {
					if (t.getStartDate() != null) {
						Timestamp sd = dropPartialDay(t.getStartDate());
						if (sd.after(ed)) {
							continue;
						}
					}
					if (t.getEndDate() != null) {
						Timestamp endD = dropPartialDay(t.getEndDate());
						if (endD.before(ed)) {
							continue;
						}
					}
					Contact con = new Contact(c);
					ContactUtil conU = new ContactUtil(con, t);
					rtn = conU;
					break loop;
				}
			}

		}
		return rtn;
	}

	Timestamp dropPartialDay(Timestamp ts) {
		if (ts == null) {
			return ts;
		}
		Calendar cal = Calendar.getInstance();
		Date date = new Date(ts.getTime());
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Timestamp rtn = new Timestamp(cal.getTimeInMillis());
		rtn.setNanos(0);
		return rtn;
	}

	public Timestamp latestOwnerRefDate(Timestamp anchor) {
		/*
		 * DESIGN For the most recent year user is responsible for determine
		 * last date owned by that owner. The contact effective on that date is
		 * the one to use. The owner StartDate must not be after that Dec 31st
		 * and the EndDate must be on or after that Dec 31st.
		 */
		Timestamp a = dropPartialDay(anchor);
		Timestamp contactRefDate = a;
		boolean seenAnyOwner = false;
		out: if (anchor != null) {
			for (Contact c : getAllContactsList()) {
				for (ContactType t : c.getContactTypes()) {
					/*if (!t.getContactTypeCd().equals(ContactTypeDef.OWNR)) {
						continue;
					}*/
					Timestamp start = dropPartialDay(t.getStartDate());
					if (a.before(start))
						continue;
					Timestamp end = null;
					if (t.getEndDate() != null) {
						end = dropPartialDay(t.getEndDate());
						if (end.before(a)) {
							continue;
						}
					}
					seenAnyOwner = true;
					if (end == null) {
						// owner owned during anchor date
						contactRefDate = null; // First owner we have seen
						break out;
					}
					if (contactRefDate.before(end)) {
						// Of the owners that span the anchor point,
						// use the latest EndDate.
						seenAnyOwner = true;
						contactRefDate = end;
					}
				}
			}
		}
		if (!seenAnyOwner) {
			contactRefDate = null; // use current info if no owner.
		}
		// set to the end of the day.
		if (contactRefDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(contactRefDate.getTime());
			contactRefDate.setNanos(0);
		}
		return contactRefDate;
	}

	/*
	 * Find last active contact at or before specified date, return a copy of it
	 * with Start and End dates.
	 */
	public final ContactUtil getLastActiveContact(String type,
			Timestamp effectiveDate) {
		ContactUtil rtn = null;
		Timestamp last = null;
		Timestamp effDate = dropPartialDay(effectiveDate);
		Timestamp end = null;
		loop: for (Contact c : allContacts) {
			for (ContactType t : c.getContactTypes()) {
				if (!t.getContactTypeCd().equals(type))
					continue;
				if (t.getStartDate() != null) {
					Timestamp start = dropPartialDay(t.getStartDate());
					if (start.after(effDate)) {
						continue;
					}
				}
				end = null;
				if (t.getEndDate() != null) {
					end = dropPartialDay(t.getEndDate());
					if (end.before(effDate)) {
						continue;
					}
				}
				if (end == null) {
					Contact con = new Contact(c);
					rtn = new ContactUtil(con, t);
					break loop;
				}

				if (last == null) {
					last = end;
					Contact con = new Contact(c);
					rtn = new ContactUtil(con, t);
				} else {
					if (end.after(last)) {
						last = t.getEndDate();
						Contact con = new Contact(c);
						rtn = new ContactUtil(con, t);
					}
				}

			}

		}
		return rtn;
	}

	public final Contact getPrimaryContact() {
		return primaryContact;
	}

	public final void setPrimaryContact(Contact primaryContact) {
		this.primaryContact = primaryContact;
	}

	/*
	 * Note: should return Contact[] so that XML Encoder does not include this
	 * for encoding
	 */
	public final Contact[] getResponsibleOfficals() {
		return responsibleOfficals.values().toArray(new Contact[0]);
	}

	public final void setResponsibleOfficals(
			HashMap<Integer, Contact> responsibleOfficals) {
		if (responsibleOfficals == null) {
			responsibleOfficals = new HashMap<Integer, Contact>(0);
		}
		this.responsibleOfficals = responsibleOfficals;
	}

	public final boolean isSec112() {
		return sec112;
	}

	public final void setSec112(boolean sec112) {
		this.sec112 = sec112;
	}

	public final void setControlEquips(List<ControlEquipment> controlEquips) {
		if (controlEquips == null) {
			controlEquips = new ArrayList<ControlEquipment>(0);
		}
		this.controlEquips = controlEquips;
	}

	public final void setEmissionUnits(List<EmissionUnit> emissionUnits) {
		if (emissionUnits == null) {
			emissionUnits = new ArrayList<EmissionUnit>(0);
		}
		this.emissionUnits = emissionUnits;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		if (addresses == null) {
			addresses = new ArrayList<Address>(0);
		}
		this.addresses = addresses;

		// Assign the facility name to address
		for (Address tempAddr : addresses) {
			tempAddr.setFacilityName(this.getName());
		}

		for (Address tempAddr : addresses) {
			if (tempAddr.getEndDate() == null) {
				setPhyAddr(tempAddr);
				break;
			}
		}
	}

	// public final ArrayList<String> validateAddresses() {
	// ArrayList<String> errs = new ArrayList<String>();
	// int row = 1;
	// for (Address tempAddr : addresses) {
	// if(tempAddr.getBeginDate() == null) {
	// errs.add("Row " + row + " does not specify an address");
	// }
	// row++;
	// }
	// return errs;
	// }

	/**
	 * @return
	 */
	public final Integer getMaxVersion() {
		return maxVersion;
	}

	/**
	 * @param maxVersion
	 */
	public final void setMaxVersion(Integer maxVersion) {
		this.maxVersion = maxVersion;
	}

	/**
	 * @return
	 */
	public final String getFacilityTypeDsc() {
		return facilityTypeDsc;
	}

	/**
	 * @param facilityTypeDsc
	 */
	public final void setFacilityTypeDsc(String facilityTypeDsc) {
		this.facilityTypeDsc = facilityTypeDsc;
	}

	public final Contact[] getAllContacts() {
		return allContacts.toArray(new Contact[0]);
	}

	public final List<Contact> getAllContactsList() {
		return allContacts;
	}

	public final List<SelectItem> currentContactsPickList(
			List<SelectItem> list, String contType) {
		for (Contact contact : allContacts) {
			for (ContactType t : contact.getContactTypes()) {
				if (contType == null || t.getContactTypeCd().equals(contType)) {
					if (t.getEndDate() != null)
						continue;
					list.add(new SelectItem(contact, contact.getName()));
				}
			}
		}
		return list;
	}

	public final List<ContactUtil> currentOwnersUtil() {
		ArrayList<ContactUtil> ret = new ArrayList<ContactUtil>(0);
		for (Contact contact : allContacts) {
			for (ContactType t : contact.getContactTypes()) {
				//if (!t.getContactTypeCd().equals(ContactTypeDef.OWNR))
				//	continue;
				if (t.getEndDate() != null)
					continue;
				ret.add(new ContactUtil(contact, t));
			}
		}
		return ret;
	}

	/*
	 * Get latest End Date of role contType. Ignore role that has not ended.
	 */
	public final Timestamp latestEndDate(String contType) {
		/* For the conact type, return the latest end date */
		Timestamp latest = null;
		for (Contact contact : allContacts) {
			for (ContactType t : contact.getContactTypes()) {
				if (!t.getContactTypeCd().equals(contType))
					continue;
				if (t.getEndDate() != null) {
					Timestamp end = dropPartialDay(t.getEndDate());
					if (latest == null)
						latest = end;
					if (end.after(latest)) {
						latest = end;
					}
				}
			}
		}
		return latest;
	}

	public final void setAllContacts(List<Contact> allContacts) {
		this.allContacts = allContacts;
		for (Contact contact : allContacts) {
			if (contact.isCurrentContactType(ContactTypeDef.RSOF,
					getFacilityId())) {
				responsibleOfficals.put(contact.getContactId(), contact);
			}
			
			if (contact.isCurrentContactType(ContactTypeDef.BILL,
					getFacilityId())) {
				billingContacts.add(contact);
			}
			if (contact.isCurrentContactType(ContactTypeDef.COMP,
					getFacilityId())) {
				complianceContacts.add(contact);
			}
			if (contact.isCurrentContactType(ContactTypeDef.EMIS,
					getFacilityId())) {
				emissionsContacts.add(contact);
			}
			if (contact.isCurrentContactType(ContactTypeDef.ENVI,
					getFacilityId())) {
				environmentalContacts.add(contact);
			}
			if (contact.isCurrentContactType(ContactTypeDef.MONI,
					getFacilityId())) {
				monitoringContacts.add(contact);
			}
			if (contact.isCurrentContactType(ContactTypeDef.ONST,
					getFacilityId())) {
				onSiteOperatorContacts.add(contact);
			}
			if (contact.isCurrentContactType(ContactTypeDef.PERM,
					getFacilityId())) {
				permittingContacts.add(contact);
			}
			if (contact.isCurrentContactType(ContactTypeDef.RSOF,
					getFacilityId())) {
				responsibleOfficialContacts.add(contact);
			}
			if (contact.isCurrentContactType(ContactTypeDef.NSRB,
					getFacilityId())) {
				nsrBillingContacts.add(contact);
			}
		}
		
		setBillingContact(getOldestFacilityContact(billingContacts, ContactTypeDef.BILL, getFacilityId()));
		setComplianceContact(getOldestFacilityContact(complianceContacts, ContactTypeDef.COMP, getFacilityId()));
		setEmissionsContact(getOldestFacilityContact(emissionsContacts, ContactTypeDef.EMIS, getFacilityId()));
		setEnvironmentalContact(getOldestFacilityContact(environmentalContacts, ContactTypeDef.ENVI, getFacilityId()));
		setMonitoringContact(getOldestFacilityContact(monitoringContacts, ContactTypeDef.MONI, getFacilityId()));
		setOnSiteOperatorContact(getOldestFacilityContact(onSiteOperatorContacts, ContactTypeDef.ONST, getFacilityId()));
		setPermittingContact(getOldestFacilityContact(permittingContacts, ContactTypeDef.PERM, getFacilityId()));
		setRespOfficialContact(getOldestFacilityContact(responsibleOfficialContacts, ContactTypeDef.RSOF, getFacilityId()));
		setNSRBillingContact(getOldestFacilityContact(nsrBillingContacts, ContactTypeDef.NSRB, getFacilityId()));
	}

	/*
	 * Return true if facility sold during in the year Sold means there is an
	 * owner start date in the range.
	 */
	public final boolean ownershipChange(int year) {
		Calendar cal = Calendar.getInstance();
		for (Contact contact : allContacts) {
			for (ContactType type : contact.getContactTypes()) {
				//if (!type.getContactTypeCd().equals(ContactTypeDef.OWNR)) {
				//	continue;
				//}
				cal.setTimeInMillis(type.getStartDate().getTime());

				if (cal.get(Calendar.YEAR) == year) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Return pick list of times facility sold/bought on or after the specified
	 * date.
	 */
	public final List<SelectItem> ownershipChange(Timestamp left) {
		Timestamp leftTs = dropPartialDay(left);
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		TreeSet<Timestamp> tree = new TreeSet<Timestamp>();
		for (Contact contact : allContacts) {
			for (ContactType type : contact.getContactTypes()) {
				//if (!type.getContactTypeCd().equals(ContactTypeDef.OWNR)) {
				//	continue;
				//}
				Timestamp start = dropPartialDay(type.getStartDate());
				if (!start.before(leftTs)) {
					tree.add(start);
				}
			}
		}
		ArrayList<SelectItem> lst = new ArrayList<SelectItem>(tree.size());
		for (Timestamp ts : tree) {
			lst.add(new SelectItem(ts, df.format(ts)));
		}
		return lst;
	}

	/**
	 * @param phyAddr
	 */
	public final void setPhyAddr(Address phyAddr) {
		this.phyAddr = phyAddr;
	}

	/**
	 * @return
	 */
	public final Address getPhyAddr() {
		return phyAddr;
	}

	/**
	 * @return
	 */
	public final Timestamp getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 */
	public final void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return
	 */
	public final Timestamp getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 */
	public final void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return
	 */
	public final Timestamp getShutdownDate() {
		return shutdownDate;
	}

	/**
	 * @param endDate
	 */
	public final void setShutdownDate(Timestamp ShutdownDate) {
		checkDirty("sddt", getFacilityId(), this.shutdownDate, ShutdownDate);
		fieldChangeEventLog("fsdd", "N/A", this.shutdownDate, ShutdownDate);
		this.shutdownDate = FacesUtil.convertYear(ShutdownDate);
	}

	public final Timestamp getInactiveDate() {
		return inactiveDate;
	}

	public final void setInactiveDate(Timestamp inactiveDate) {
		this.inactiveDate = inactiveDate;
	}

	/**
	 * @param emissionUnitId
	 * @return
	 */
	public final EmissionUnit getEmissionUnit(Integer emissionUnitId) {
		EmissionUnit ret = null;
		for (EmissionUnit tempEU : emissionUnits) {
			if (tempEU.getEmuId().equals(emissionUnitId)) {
				ret = tempEU;
				break;
			}
		}
		if (ret == null) {
			logger.warn("emission unit with emu id : " + emissionUnitId
					+ " not found in " + getFacilityId() + ", fpId="
					+ getFpId());
		}
		return ret;
	}

	/**
	 * @param epaEmuId
	 * @return
	 */
	public final EmissionUnit getEmissionUnit(String epaEmuId) {
		for (EmissionUnit tempEU : emissionUnits) {
			if (tempEU.getEpaEmuId().compareTo(epaEmuId) == 0) {
				return tempEU;
			}
		}
		return null;
	}

	public final EmissionUnit getMatchingEmissionUnit(Integer corrEpaEmuId) {
		for (EmissionUnit tempEU : emissionUnits) {
			if (tempEU.getCorrEpaEmuId().equals(corrEpaEmuId)) {
				return tempEU;
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public final List<EmissionUnit> getEmissionUnits() {
		return emissionUnits;
	}

	/**
	 * Retrieve all emission units not marked invalid
	 * 
	 * @return
	 */
	public final List<EmissionUnit> getValidEmissionUnits() {
		List<EmissionUnit> validList = new ArrayList<EmissionUnit>();
		for (EmissionUnit eu : emissionUnits) {
			if (!EuOperatingStatusDef.IV.equals(eu.getOperatingStatusCd())) {
				validList.add(eu);
			}
		}
		return validList;
	}

	public void addFacilityCemComLimit(FacilityCemComLimit limit) {
		if (null != limit) {
			getFacilityCemComLimitList().add(limit);
		}
	}
	
	public void addContinuousMonitor(ContinuousMonitor monitor) {
		if (null != monitor) {
			getContinuousMonitorList().add(monitor);
		}
	}
	
	/**
	 * @param emissionUnit
	 */
	public final void addEmissionUnit(EmissionUnit emissionUnit) {
		if (emissionUnit != null) {
			this.emissionUnits.add(emissionUnit);
		}
	}

	/**
	 * @return
	 */
	public final List<ControlEquipment> getControlEquips() {
		return controlEquips;
	}

	/**
	 * @return
	 */
	public final void addControlEquip(ControlEquipment controlEquipment) {
		if (controlEquipment != null) {
			this.controlEquips.add(controlEquipment);
		}
	}

	/**
	 * @return
	 */
	public final EgressPoint[] getEgressPoints() {
		return egrPoints.toArray(new EgressPoint[0]);
	}

	/**
	 * @return
	 */
	public final List<EgressPoint> getEgressPointsList() {
		return egrPoints;
	}

	/**
	 * @return
	 */
	public final void addEgressPoint(EgressPoint egressPoint) {
		if (egressPoint != null) {
			this.egrPoints.add(egressPoint);
		}
	}

	/**
	 * @return
	 */
	public final ControlEquipment[] getUnassignedCntEquips() {
		return unassignedCntEquips.values().toArray(new ControlEquipment[0]);
	}

	/**
	 * @param emissionUnit
	 */
	public final void addUnassignedCntEquip(ControlEquipment controlEquipment) {
		if (controlEquipment != null) {
			this.unassignedCntEquips.put(controlEquipment.getFpNodeId(),
					controlEquipment);
		}
	}

	public final void removeFromUnassignCntEquips(ControlEquipment controlEquip) {
		unassignedCntEquips.remove(controlEquip.getFpNodeId());
	}

	public final void removeFromUnassignEgrPoints(EgressPoint egressPoint) {
		unassignedEgrPoints.remove(egressPoint.getFpNodeId());
	}

	public final void removeEmissionUnit(EmissionUnit eu) {
		emissionUnits.remove(eu);
	}

	/**
	 * @return
	 */
	public final EgressPoint[] getUnassignedEgrPoints() {
		return unassignedEgrPoints.values().toArray(new EgressPoint[0]);
	}

	/**
	 * @param emissionUnit
	 */
	public final void addUnassignedEgrPoint(EgressPoint egressPoint) {
		if (egressPoint != null) {
			this.unassignedEgrPoints
					.put(egressPoint.getFpNodeId(), egressPoint);
		}
	}

	/**
	 * @return
	 */
	public final String getDesc() {
		return desc;
	}

	/**
	 * @param desc
	 */
	public final void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public final void setName(String name) {
		requiredField(name.trim(), "name", "Facility Name", "facility");
		checkDirty("fnm", getFacilityId(), this.name, name.trim());
		fieldChangeEventLog("fnme", "N/A", this.name, name.trim(), true);

		this.name = name.trim();
	}

	// TODO Need to remove, Tim, 3/4/2013
	public final Float getLatitudeDeg() {
		return Float.parseFloat(this.phyAddr.getLatitude());
	}

	// TODO Need to remove, Tim, 3/4/2013
	public final Float getLongitude() {
		return Float.parseFloat(this.phyAddr.getLongitude());
	}

	/**
	 * @return
	 */
	public final String getPortableGroupCd() {
		return portableGroupCd;
	}

	/**
	 * @param portableGroupCd
	 */
	public final void setPortableGroupCd(String portableGroupCd) {
		this.portableGroupCd = portableGroupCd;
	}

	/**
	 * @return
	 */
	public final String getPortableGroupTypeCd() {
		return portableGroupTypeCd;
	}

	/**
	 * @param portableGroupTypeCd
	 */
	public final void setPortableGroupTypeCd(String portableGroupTypeCd) {
		this.portableGroupTypeCd = portableGroupTypeCd;
	}

	/**
	 * @return
	 */
	public final boolean getPortable() {
		return portable;
	}

	/**
	 * @param portable
	 */
	public final void setPortable(boolean portable) {
		fieldChangeEventLog("fpor", "N/A", this.portable, portable);
		this.portable = portable;
	}

	public final boolean isValidated() {
		return validated;
	}

	public final void setValidated(boolean validated) {
		this.validated = validated;
	}

	public final boolean isSubmitted() {
		return submitted;
	}

	public final void setSubmitted(boolean submitted) {
		this.submitted = submitted;
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
	 */
	public final void populate(ResultSet rs) {
		try {
			setFpId(AbstractDAO.getInteger(rs, "fp_id"));
			setFacilityId(rs.getString("facility_id"));
			setVersionId(AbstractDAO.getInteger(rs, "version_id"));
			setStartDate(rs.getTimestamp("start_dt"));
			setEndDate(rs.getTimestamp("end_dt"));
			setName(rs.getString("facility_nm"));
			setDesc(rs.getString("facility_desc"));
			setDoLaaCd(rs.getString("do_laa_cd"));
			setOperatingStatusCd(rs.getString("operating_status_cd"));
			setAfs(rs.getString("afs"));
			setPermitClassCd(rs.getString("permit_classification_cd"));
			setTransitStatusCd(rs.getString("transitional_status_cd"));
			setReportingTypeCd(EmissionReportsDef
					.reportingCategory(permitClassCd));
			if (maxVersionFlag) {
				setMaxVersion(AbstractDAO.getInteger(rs, "max_version"));
			}
			setCorePlaceId(AbstractDAO.getInteger(rs, "core_place_id"));
			oldCorePlaceId = corePlaceId;
			setShutdownDate(rs.getTimestamp("last_shutdown_date"));
			setInactiveDate(rs.getTimestamp("inactive_date"));
			/*
			 * setPortable(AbstractDAO.translateIndicatorToBoolean(rs
			 * .getString("portable")));
			 */
			setCopyOnChange(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("copy_on_change")));
			setPortableGroupCd(rs.getString("portable_group_cd"));
			setPortableGroupTypeCd(rs.getString("portable_group_type_cd"));
			setTvCertRepdueDate(rs.getString("tv_cert_report_due_date"));
			setMact(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("mact_ind")));
			setGhgInd(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("ghg_ind")));
			oldMact = mact;
			setNeshaps(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("neshaps_ind")));
			oldNeshaps = neshaps;
			setNsps(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("nsps_ind")));
			oldNsps = nsps;
			setPsd(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("psd_ind")));
			oldPsd = psd;
			setNsrNonattainment(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("nonattainment_nsr_ind")));
			oldNsrNonattainment = nsrNonattainment;
			setTivInd(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("tiv_ind")));
			setSec112(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("sec112_ind")));
			setPermitStatusCd(rs.getString("permit_status_cd"));
			setFederalSCSCId(rs.getString("federal_SCSC_ID"));
			setIntraStateVoucherFlag(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("intra_state_voucher_flag")));
			setValidated(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("valid")));
			setSubmitted(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("submitted")));
			setTvTypeA(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("type_a")));
			setLastSubmissionType(rs.getString("last_submission_type"));
			setLastSubmissionVersion(AbstractDAO.getInteger(rs,
					"last_submission_version"));
			setLastModified(AbstractDAO.getInteger(rs, "facility_lm"));
			setFacilityTypeCd(rs.getString("facility_type_cd"));
			setGovtFacilityTypeCd(rs.getString("govt_facility_type_cd"));
			setAirProgramCd(rs.getString("air_program_cd"));
			setAirProgramCompCd(rs.getString("air_program_comp_cd"));
			oldAirProgramCompCd = airProgramCompCd;
			setSipCompCd(rs.getString("sip_comp_cd"));
			oldSipCompCd = sipCompCd;
			setMactCompCd(rs.getString("mact_comp_cd"));
			oldMactCompCd = mactCompCd;
			setAqdEmissionFactorGroupCd(rs.getString("emission_factor_group_cd"));
			setAdministrativeHold(AbstractDAO.translateIndicatorToBoolean(rs.getString("administrative_hold")));  
		} catch (SQLException sqle) {
			logger.error("Invalid column in populate", sqle);
		} finally {
			newObject = false;
			setOrigOperatingStatusCd(operatingStatusCd);
		}

		try {
			setCerrClassCd(rs.getString("cerr_class_cd"));
			setGovtFacilityTypeCd(rs.getString("govt_facility_type_cd"));
		} catch (SQLException e) {
			logger.warn("Missing ceta columns", e);
		}
	}

	private void checkFacilityTable(boolean subject, String fieldName,
			String tableName, List<String> tableVals) {
		ValidationMessage validationMessage;

		if (subject) {
			if (tableVals.size() == 0) {
				validationMessage = new ValidationMessage(fieldName,
						"The facility is subject to : " + fieldName
								+ "; Table : " + tableName
								+ " must have at least one entry.",
						ValidationMessage.Severity.ERROR, "fedRules");
				validationMessages.put(tableName, validationMessage);
				return;
			}

			validationMessages.remove(tableName);
			checkFacilityTable(fieldName, tableName, tableVals);
		}

	}

	private void checkFacilityTable(String fieldName, String tableName,
			List<String> tableVals) {
		HashMap<String, Integer> values = new HashMap<String, Integer>(0);
		ValidationMessage validationMessage;

		for (String temp : tableVals) {
			if (temp == null || temp.equals("")) {
				validationMessage = new ValidationMessage(fieldName,
						"empty value in a row of the " + tableName + " table",
						ValidationMessage.Severity.ERROR, "facility");
				validationMessages.put(fieldName, validationMessage);
				return;
			} else if (values.containsKey(temp)) {
				validationMessage = new ValidationMessage(fieldName,
						"Duplicate value in " + tableName + " table",
						ValidationMessage.Severity.ERROR, "facility");
				validationMessages.put(fieldName, validationMessage);
				return;
			} else {
				values.put(temp, 1);
			}
		}
		validationMessages.remove(tableName);
	}

	public final ValidationMessage[] validateForCreateFacility() {
		this.clearValidationMessages();
		requiredField(companyName, "companyName", "Company Name", "facility");
		requiredFields();
		validationMessages.remove("facOperatingStatus");

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	private void afsValidation() {
		validationMessages.remove("afs");

		ValidationMessage validationMessage = checkAFS();

		if (validationMessage != null) {
			validationMessages.put("afs", validationMessage);
		}
	}

	public final ValidationMessage[] checkBasicFacility() {
		this.clearValidationMessages();
		ValidationMessage[] msgs = requiredFields();
		return msgs;
	}

	public final ValidationMessage[] validate(boolean internalApp) {
		return validate(internalApp, false);
	}

	public final ValidationMessage[] validate(boolean internalApp,
			boolean passRulesRegs) {
		
		checkBasicFacility();
		afsValidation();
		ArrayList<ValidationMessage> msgs = new ArrayList<ValidationMessage>();

		int cnt = 0;
		for (ValidationMessage vm : msgs) {
			String cntStr = String.valueOf(cnt++);
			validationMessages.put(cntStr, vm);
		}

		// validate address
		int addMsgCnt = 0;
		for (ValidationMessage msg : phyAddr.validateFacilityLocation()) {
			validationMessages.put("address:" + addMsgCnt, msg);
			addMsgCnt++;
		}

		checkOpStatus();

		checkFacilityTable("sicCd", "SIC", sicCds);
		checkFacilityTable("naicsCd", "NAICS", naicsCds);

		if (internalApp && !passRulesRegs) {
			validateFedRules();
		}

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	private List<String> convertPollutantCompCode(List<PollutantCompCode> cTab) {
		List<String> ret = new ArrayList<String>(0);
		if (cTab == null || cTab.size() == 0) {
			return ret;
		}

		for (PollutantCompCode c : cTab) {
			String p = c.getPollutantCd();
			ret.add(p);
		}
		return ret;
	}

	public final ValidationMessage[] validateFedRules() {
		List<String> neshapsSubparts = convertPollutantCompCode(neshapsSubpartsCompCds);
		checkFacilityTable(nsps, "Part 60 NSPSPART", "Part 60 NSPS Subparts",
				nspsSubparts);
		checkFacilityTable(neshaps, "Part 61 NESHAP",
				"Part 61 NESHAP Subparts", neshapsSubparts);
		checkFacilityTable(mact, "Part 63 NESHAP", "Part 63 NESHAP Subparts",
				mactSubparts);
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	private void validateContactAttributesRequired() {
		if (environmentalContact == null) {
			validationMessages.put("AttributeNotSet" + "Environmental Contact",
					new ValidationMessage("Environmental Contact",
							"Environmental Contact is not set.",
							ValidationMessage.Severity.ERROR, "contact"));
		}

		if (permitClassCd.equals("tv")) {
			if (responsibleOfficals.size() == 0) {
				validationMessages
						.put("AttributeNotSet" + "Responsible Official",
								new ValidationMessage(
										"Responsible Official",
										"Responsible Official is not set for a Title V facility.",
										ValidationMessage.Severity.ERROR,
										"contact"));
			}
		}
	}

	public final ValidationMessage[] validateFacility(boolean internalApp, Facility internalFacility) {

		ValidationMessage[] tempValidationMessages;
		List<ValidationMessage> validationMessagesList = new ArrayList<ValidationMessage>();
		int i = 0;

		// 2483
		if (internalFacility == null) {
			validationMessagesList.add(new ValidationMessage("FacilityProfile",
					"Facility Detail is not found in internal system by Facility ID.  Please contact AQD for this error.",
					ValidationMessage.Severity.ERROR, "FacilityProfile"));
		}

		validate(internalApp);

		// Check for API
		if (FacilityTypeDef.requiresApis(facilityTypeCd)) {
			logger.debug("Facility APIs = " + apis.size());
			if (apis.size() == 0) {
				validationMessages.put("apis",
						new ValidationMessage(FacilityProfileBase.FAC_NO_API,
								"Facility type requires APIs; At least one API must be provided",
								ValidationMessage.Severity.ERROR, "facility"));
			}
		}

		// TODO: Full facility validation

		if (getUnassignedCntEquips().length > 0) {
			validationMessages.put("UnassignedCEs", new ValidationMessage("UnassignedCEs",
					"Some control equipments are disassociated", ValidationMessage.Severity.INFO, "UnassignedCEs"));
		}

		if (getUnassignedEgrPoints().length > 0) {
			validationMessages.put("UnAssignEgrPnts", new ValidationMessage("UnassignedEgrPnts",
					"Some release points are disassociated", ValidationMessage.Severity.INFO, "UnassignedEgrPnts"));
		}

		EmissionUnit[] lEmissionUnits = getEmissionUnits().toArray(new EmissionUnit[0]);

		if (lEmissionUnits.length == 0) {
			validationMessages.put("NumEus", new ValidationMessage("NumEus",
					"The facility does not have any Emission Units", ValidationMessage.Severity.INFO, "facility"));
		}

		ArrayList<EgressPoint> EPs = new ArrayList<EgressPoint>(egrPoints);
		EPs.removeAll(unassignedEgrPoints.values());
		for (EgressPoint tempEGP : EPs) {
			if (!tempEGP.isActive()) {
				logger.debug("Skipping validation for non-active release point " + tempEGP.getReleasePointId());
				continue;
			}
			tempValidationMessages = tempEGP.validate(false);
			for (i = 0; i < tempValidationMessages.length; i++) {
				validationMessages.put("egrPoint:" + tempEGP.getFpNodeId() + tempValidationMessages[i].getProperty(),
						tempValidationMessages[i]);
			}
		}

		ArrayList<ControlEquipment> CEs = new ArrayList<ControlEquipment>(controlEquips);
		// Just validate control equipment being used.
		CEs.removeAll(unassignedCntEquips.values());
		for (ControlEquipment tempCE : CEs) {
			if (!tempCE.isActive()) {
				logger.debug("Skipping validation for non-active control equipment " + tempCE.getControlEquipmentId());
				continue;
			}
			// if (!tempCE.isValid()) {
			tempValidationMessages = tempCE.validate();
			for (i = 0; i < tempValidationMessages.length; i++) {
				validationMessages.put("cntEquip:" + tempCE.getFpNodeId() + tempValidationMessages[i].getProperty(),
						tempValidationMessages[i]);
			}
			// }
		}

		for (EmissionUnit tempEU : lEmissionUnits) {
			if (tempEU.getOperatingStatusCd().equals(EuOperatingStatusDef.SD)
					|| tempEU.getOperatingStatusCd().equals(EuOperatingStatusDef.IV)) {
				continue;
			}

			// if (!tempEU.isValid()) {
			tempValidationMessages = tempEU.validate(permitClassCd, internalApp);
			for (i = 0; i < tempValidationMessages.length; i++) {
				ValidationMessage newMsg = new ValidationMessage(tempValidationMessages[i].getProperty(),
						tempValidationMessages[i].getMessage(), tempValidationMessages[i].getSeverity(),
						tempValidationMessages[i].getReferenceID(), tempEU.getEpaEmuId());
				validationMessages.put("emUnit:" + tempEU.getEpaEmuId() + tempValidationMessages[i].getProperty(),
						newMsg);
			}
			// }

			EmissionProcess[] emissionProcesses = tempEU.getEmissionProcesses().toArray(new EmissionProcess[0]);

			for (EmissionProcess tempEP : emissionProcesses) {
				if (!tempEP.isValid()) {
					tempValidationMessages = tempEP.validate(tempEU.getEpaEmuId());
					for (i = 0; i < tempValidationMessages.length; i++) {
						validationMessages.put(
								"emProc:" + tempEP.getFpNodeId() + tempValidationMessages[i].getProperty(),
								tempValidationMessages[i]);
					}
				}
			}
		}

		validationMessagesList.addAll(validationMessages.values());
		return validationMessagesList.toArray(new ValidationMessage[0]);
	}

	public final FacilityEmission[] getFacilityEmissions() {
		return facilityEmissions.values().toArray(new FacilityEmission[0]);
	}

	public final void generateFacilityEmissions() {
		facilityEmissions = new HashMap<String, FacilityEmission>(0);

		for (EmissionUnit eu : getEmissionUnits()) {
			logger.debug("    ----> eu = " + eu.getEmuId());
			for (EuEmission euEmission : eu.getEuEmissions()) {
				logger.debug("    --------> euEmission = " + euEmission);
				if (facilityEmissions.containsKey(euEmission.getPollutantCd())) {
					FacilityEmission facilityEmission = facilityEmissions.remove(euEmission.getPollutantCd());
					logger.debug("    --------> facilityEmission = " + facilityEmission);
					
					if (euEmission.getAllowableEmissionsLbsHour() != null ||
							facilityEmission.getAllowableEmissionsLbsHour() != null) {
						BigDecimal euVal = euEmission.getAllowableEmissionsLbsHour();
						BigDecimal fVal = facilityEmission.getAllowableEmissionsLbsHour();
						if (null != fVal) {
							if (null != euVal) {
								facilityEmission.setAllowableEmissionsLbsHour(fVal.add(euVal));
							}
						} else {
							if (null != euVal) {
								facilityEmission.setAllowableEmissionsLbsHour(euVal);								
							}
						}
					}
					if (euEmission.getAllowableEmissionsTonsYear() != null ||
							facilityEmission.getAllowableEmissionsTonsYear() != null) {
						BigDecimal euVal = euEmission.getAllowableEmissionsTonsYear();
						BigDecimal fVal = facilityEmission.getAllowableEmissionsTonsYear();
						if (null != fVal) {
							if (null != euVal) {
								facilityEmission.setAllowableEmissionsTonsYear(fVal.add(euVal));
							}
						} else {
							if (null != euVal) {
								facilityEmission.setAllowableEmissionsTonsYear(euVal);								
							}
						}
					}
					if (euEmission.getPotentialEmissionsLbsHour() != null ||
							facilityEmission.getPotentialEmissionsLbsHour() != null) {
						BigDecimal euVal = euEmission.getPotentialEmissionsLbsHour();
						BigDecimal fVal = facilityEmission.getPotentialEmissionsLbsHour();
						if (null != fVal) {
							if (null != euVal) {
								facilityEmission.setPotentialEmissionsLbsHour(fVal.add(euVal));
							}
						} else {
							if (null != euVal) {
								facilityEmission.setPotentialEmissionsLbsHour(euVal);								
							}
						}
					}
					if (euEmission.getPotentialEmissionsTonsYear() != null ||
							facilityEmission.getPotentialEmissionsTonsYear() != null) {
						BigDecimal euVal = euEmission.getPotentialEmissionsTonsYear();
						BigDecimal fVal = facilityEmission.getPotentialEmissionsTonsYear();
						if (null != fVal) {
							if (null != euVal) {
								facilityEmission.setPotentialEmissionsTonsYear(fVal.add(euVal));
							}
						} else {
							if (null != euVal) {
								facilityEmission.setPotentialEmissionsTonsYear(euVal);								
							}
						}
					}
					logger.debug("    ------------> mapping facilityEmission: " + facilityEmission);
					facilityEmissions.put(facilityEmission.getPollutantCd(),facilityEmission);
				} else {
					euEmission = new EuEmission(euEmission);
					logger.debug("    ------------> mapping euEmission: " + euEmission);
					facilityEmissions.put(euEmission.getPollutantCd(),euEmission);
				}
			}
		}
	}

	public final Timestamp getShutdownNotifDate() {
		return shutdownNotifDate;
	}

	public final void setShutdownNotifDate(Timestamp shutdownNotifDate) {
		this.shutdownNotifDate = FacesUtil.convertYear(shutdownNotifDate);
	}

	public final boolean isShutdownNotifEnabled() {
		return shutdownNotifEnabled;
	}

	public final boolean isMaxVersionFlag() {
		return maxVersionFlag;
	}

	public final void setMaxVersionFlag(boolean maxVersionFlag) {
		this.maxVersionFlag = maxVersionFlag;
	}

	public final Long getEndDateLong() {
		if (endDate == null) {
			return null;
		}
		return endDate.getTime();
	}

	public final void setEndDateLong(Long endDateLong) {
		if (endDateLong != null) {
			endDate = new Timestamp(endDateLong);
		}
	}

	public final Long getStartDateLong() {
		if (startDate == null) {
			return null;
		}
		return startDate.getTime();
	}

	public final void setStartDateLong(Long startDateLong) {
		if (startDateLong != null) {
			startDate = new Timestamp(startDateLong);
		}
	}

	public boolean isTvTypeA() {
		return tvTypeA;
	}

	public void setTvTypeA(boolean tvTypeA) {
		this.tvTypeA = tvTypeA;
	}

	public List<Address> getAddressesList() {
		return addresses;
	}

	public void copyFacilityData(Facility dapcFac) {
		setPhyAddr(dapcFac.getPhyAddr());
		// setAddresses(dapcFac.getAddressesList());
		if (OperatingStatusDef.SD.equals(dapcFac.getOperatingStatusCd())) {
			setShutdownDate(dapcFac.getShutdownDate());
			setShutdownNotifDate(dapcFac.getShutdownNotifDate());
		}
		/* setPortable(dapcFac.getPortable()); */
		setPortableGroupCd(dapcFac.getPortableGroupCd());
		setPortableGroupTypeCd(dapcFac.getPortableGroupTypeCd());
		setPermitClassCd(dapcFac.getPermitClassCd());
		setReportingTypeCd(EmissionReportsDef
				.reportingCategory(getPermitClassCd()));
		setTvTypeA(dapcFac.isTvTypeA());
		setCorePlaceId(dapcFac.getCorePlaceId());
		setSecondNotifCountyCd(dapcFac.getSecondNotifCountyCd());
		setPermitStatusCd(dapcFac.getPermitStatusCd());
		setTransitStatusCd(dapcFac.getTransitStatusCd());
		setFederalSCSCId(dapcFac.getFederalSCSCId());
		setMact(dapcFac.isMact());
		setNeshaps(dapcFac.isNeshaps());
		setNsps(dapcFac.isNsps());
		setNsrNonattainment(dapcFac.isNsrNonattainment());
		setPsd(dapcFac.isPsd());
		setMactSubparts(dapcFac.getMactSubparts());
		setNeshapsSubpartsCompCds(dapcFac.getNeshapsSubpartsCompCds());
		setNspsSubparts(dapcFac.getNspsSubparts());
		setFacilityId(dapcFac.getFacilityId());
		setFacilityTypeCd(dapcFac.getFacilityTypeCd());
		setCerrClassCd(dapcFac.getCerrClassCd());
		setAfs(dapcFac.getAfs());
		setGovtFacilityTypeCd(dapcFac.getGovtFacilityTypeCd());
		setAirProgramCompCd(dapcFac.getAirProgramCompCd());
		setSipCompCd(dapcFac.getSipCompCd());
		setMactCompCd(dapcFac.getMactCompCd());
		setAssociatedMonitorGroup(dapcFac.getAssociatedMonitorGroup());
	}

	public EmissionUnit getGateWayEmissionUnit(EmissionUnit emissionUnit) {
		EmissionUnit ret = getEmissionUnit(emissionUnit);
		if (ret == null) {
			logger.debug(" emission unit with epa emu id : "
					+ emissionUnit.getEpaEmuId() + " not found");
		}
		return ret;
	}

	public EmissionUnit getEmissionUnit(EmissionUnit emissionUnit) {
		EmissionUnit ret = null;
		for (EmissionUnit tempEU : emissionUnits) {
			if (null != tempEU.getCorrEpaEmuId()
					&& tempEU.getCorrEpaEmuId().equals(
							emissionUnit.getCorrEpaEmuId())) {
				ret = tempEU;
				break;
			}
		}
		return ret;
	}

	public ControlEquipment getControlEquipment(ControlEquipment ce) {
		ControlEquipment ret = null;
		for (ControlEquipment tempCE : controlEquips) {
			if (tempCE.getCorrelationId().equals(ce.getCorrelationId())) {
				ret = tempCE;
				break;
			}
		}
		return ret;
	}

	public ControlEquipment getControlEquipment(String contEquipId) {
		ControlEquipment ret = null;
		for (ControlEquipment tempCE : controlEquips) {
			if (tempCE.getControlEquipmentId().equals(contEquipId)) {
				ret = tempCE;
				break;
			}
		}
		if (ret == null) {
			logger.debug("Control equipment not found for ID : " + contEquipId);
		}
		return ret;
	}

	public EgressPoint getEgressPoint(EgressPoint egp) {
		EgressPoint ret = null;
		for (EgressPoint tempEGP : egrPoints) {
			if (tempEGP.getCorrelationId().equals(egp.getCorrelationId())) {
				ret = tempEGP;
				break;
			}
		}
		return ret;
	}

	public EgressPoint getEgressPoint(String releasePointId) {
		EgressPoint ret = null;
		for (EgressPoint tempEGP : egrPoints) {
			if (tempEGP.getReleasePointId() != null
					&& tempEGP.getReleasePointId().equals(releasePointId)) {
				ret = tempEGP;
				break;
			}
		}
		if (ret == null) {
			logger.debug("Release Point not found for ID : " + releasePointId);
		}
		return ret;
	}

	public EgressPoint getEgressPointByCorr(Integer egressPointCorr) {
		EgressPoint ret = null;
		for (EgressPoint tempEGP : egrPoints) {
			if (tempEGP.getCorrelationId().equals(egressPointCorr)) {
				ret = tempEGP;
				break;
			}
		}
		return ret;
	}

	public String getLastSubmissionType() {
		return lastSubmissionType;
	}

	public void setLastSubmissionType(String lastSubmissionType) {
		this.lastSubmissionType = lastSubmissionType;
	}

	public Integer getLastSubmissionVersion() {
		return lastSubmissionVersion;
	}

	public void setLastSubmissionVersion(Integer lastSubmissionVersion) {
		this.lastSubmissionVersion = lastSubmissionVersion;
	}

	public String getOrigOperatingStatusCd() {
		return origOperatingStatusCd;
	}

	public void setOrigOperatingStatusCd(String origOperatingStatusCd) {
		this.origOperatingStatusCd = origOperatingStatusCd;
	}

	public Long getShutdownDateLong() {
		if (shutdownDate == null) {
			return null;
		}
		return shutdownDate.getTime();
	}

	public void setShutdownDateLong(Long shutdownDateLong) {
		if (shutdownDateLong != null) {
			shutdownDate = new Timestamp(shutdownDateLong);
		}
	}

	public Long getInactiveDateLong() {
		Long date = null;
		if (inactiveDate != null) {
			date = inactiveDate.getTime();
		}
		return date;
	}

	public void setInactiveDateLong(Long inactiveDateLong) {
		if (inactiveDateLong != null) {
			inactiveDate = new Timestamp(inactiveDateLong);
		}
	}

	public String getFormattedTvCertRepdueDate() {
		String ret = null;

		try {
			if (tvCertRepdueDate != null) {
				ret = TimestampUtil.convertToTimestamp(tvCertRepdueDate, null,
						"MMMM dd");
			}
		} catch (TimestampUtilException tue) {
			logger.debug("Cannot get formatted TV Certificate Report Due Date: "
					+ tue.getMessage());
			ret = null;
		}
		return ret;
	}

	// Determine the earliest initial install date of any EU regardless of
	// current state.
	// This is the best estimate of when the faciity started operating.
	Timestamp earliestEuCompletionOfInstallation() {
		Timestamp rtn = null;
		for (EmissionUnit eu : emissionUnits) {
			if (eu.getEuInitInstallDate() != null) {
				if (rtn == null) {
					rtn = eu.getEuInitInstallDate();
				} else {
					if (rtn.after(eu.getEuInitInstallDate())) {
						rtn = eu.getEuInitInstallDate();
					}
				}
			}
		}
		return rtn;
	}

	public final void setShutdownNotifEnabled(boolean shutdownNotifEnabled) {
		this.shutdownNotifEnabled = shutdownNotifEnabled;
	}

	public final void setUnassignedCntEquips(
			HashMap<Integer, ControlEquipment> unassignedCntEquips) {
		this.unassignedCntEquips = unassignedCntEquips;
	}

	public final void setUnassignedEgrPoints(
			HashMap<Integer, EgressPoint> unassignedEgrPoints) {
		this.unassignedEgrPoints = unassignedEgrPoints;
	}

	public final void setFacilityEmissions(
			HashMap<String, FacilityEmission> facilityEmissions) {
		this.facilityEmissions = facilityEmissions;
	}

	public final boolean isStaging() {
		return staging;
	}

	public final void setStaging(boolean staging) {
		this.staging = staging;
	}

	public final Attachment getAttestationDocument() {
		return attestationDocument;
	}

	public final void setAttestationDocument(Attachment attestationDocument) {
		this.attestationDocument = attestationDocument;
	}

	public final void updateEuAttributes(Permit permit) {
		List<PermitEUGroup> euGroups = permit.getEuGroups();
		for (PermitEUGroup peug : euGroups) {
			List<PermitEU> euList = peug.getPermitEUs();
			for (PermitEU peu : euList) {
				if (peu.getFpEU() != null) {
					EmissionUnit currEu = this.getMatchingEmissionUnit(peu
							.getFpEU().getCorrEpaEmuId());
					if (currEu == null) {
						// In case not found, leave values alone
						continue;
					}
					peu.getFpEU().setRegulatedUserDsc(
							currEu.getRegulatedUserDsc());

					peu.getFpEU().setCompanyId(currEu.getCompanyId());

					peu.getFpEU().setEuInstallDate(currEu.getEuInstallDate());

					peu.getFpEU().setEuInitInstallDate(
							currEu.getEuInitInstallDate());

					peu.getFpEU().setEuStartupDate(currEu.getEuStartupDate());

					peu.getFpEU().setOrisBoilerId(currEu.getOrisBoilerId());

					peu.setDapcDescription(currEu.getDapcDescription());

					peu.setCompanyId(currEu.getCompanyId());
				}
			}
		}
	}


	public void unionOperatingStatus(String opStat) {
		if (getOperatingStatusCd() == null) {
			setOperatingStatusCd(opStat);
			return;
		}
		if (OperatingStatusDef.OP.equals(opStat))
			setOperatingStatusCd(opStat);
	}

	public void unionNspsSubpart(String subpart) {
		if (!nspsSubparts.contains(subpart))
			nspsSubparts.add(subpart);
	}

	public void unionMactSubpart(String subpart) {
		if (!mactSubparts.contains(subpart))
			mactSubparts.add(subpart);
	}

	public void unionMactCompCd(String cCd) {
		if (getMactCompCd() == null) {
			setMactCompCd(cCd);
			return;
		}
		if (cCd != null && getMactCompCd().compareTo(cCd) > 0) {
			setMactCompCd(cCd);
		}
	}

	public void unionSipCompCd(String cCd) {
		if (getSipCompCd() == null) {
			setSipCompCd(cCd);
			return;
		}
		if (cCd != null && getSipCompCd().compareTo(cCd) > 0) {
			setSipCompCd(cCd);
		}
	}

	public void unionAirProg(String progCd, String progCompCd) {
		if (getAirProgramCd() == null) {
			setAirProgramCd(progCd);
			setAirProgramCompCd(progCompCd);
			return;
		}
		if (AirProgramDef.TITLE_V.equals(progCd)) {
			setAirProgramCd(progCd);
		}
		// Use the worst compliance regardless of what program under.
		if (getAirProgramCompCd() == null) {
			setAirProgramCompCd(progCompCd);
			return;
		}
		if (progCompCd != null
				&& getAirProgramCompCd().compareTo(progCompCd) > 0) {
			setAirProgramCompCd(progCompCd);
		}
	}

	public void unionNeshapsSubpartsCompCd(PollutantCompCode compCd) {
		boolean found = false;
		for (PollutantCompCode cCd : neshapsSubpartsCompCds) {
			if (cCd.getPollutantCd().equals(compCd.getPollutantCd())) {
				found = true;
				if (compCd.getPollutantCompCd() != null
						&& cCd.getPollutantCompCd().compareTo(
								compCd.getPollutantCompCd()) > 0) {
					cCd.setPollutantCompCd(compCd.getPollutantCompCd());
				}
			}
		}
		if (!found)
			neshapsSubpartsCompCds.add(compCd);
	}

	// CETA FIELDS

	public final String getGovtFacilityTypeCd() {
		return govtFacilityTypeCd;
	}

	public final void setGovtFacilityTypeCd(String govtFacilityTypeCd) {
		this.govtFacilityTypeCd = govtFacilityTypeCd;
	}


	public final String getCerrClassCd() {
		return this.cerrClassCd;
	}

	public final void setCerrClassCd(String cerrClassCd) {
		this.cerrClassCd = cerrClassCd;
	}

	public final String getAirProgramCompCd() {
		return airProgramCompCd;
	}

	public final void setAirProgramCompCd(String airProgramCompCd) {
		this.airProgramCompCd = airProgramCompCd;
	}

	public final String getSipCompCd() {
		return sipCompCd;
	}

	public final void setSipCompCd(String sipCompCd) {
		this.sipCompCd = sipCompCd;
	}

	public final String getMactCompCd() {
		return mactCompCd;
	}

	public final void setMactCompCd(String mactCompCd) {
		this.mactCompCd = mactCompCd;
	}

	public final List<PollutantCompCode> getNeshapsSubpartsCompCds() {
		return neshapsSubpartsCompCds;
	}

	public final void setNeshapsSubpartsCompCds(
			List<PollutantCompCode> neshapsSubpartsCompCds) {
		this.neshapsSubpartsCompCds = neshapsSubpartsCompCds;
	}

	public List<String> getNeshapsSubparts() {
		List<String> pollutants = convertPollutantCompCode(neshapsSubpartsCompCds);
		return pollutants;
	}

	public String getOldAirProgramCompCd() {
		return oldAirProgramCompCd;
	}

	
	public String getOldMactCompCd() {
		return oldMactCompCd;
	}

	public String getOldSipCompCd() {
		return oldSipCompCd;
	}

	public boolean isOldMact() {
		return oldMact;
	}

	public boolean isOldNeshaps() {
		return oldNeshaps;
	}

	public boolean isOldNsps() {
		return oldNsps;
	}

	public boolean isOldNsrNonattainment() {
		return oldNsrNonattainment;
	}

	public boolean isOldPsd() {
		return oldPsd;
	}

	public String getAirProgramCd() {
		return airProgramCd;
	}

	public void setAirProgramCd(String airProgramCd) {
		this.airProgramCd = airProgramCd;
		if (airProgramCd != null) {
			if (airProgramCd.equals(AirProgramDef.SM_FESOP)) {
				smInd = true;
				tvInd = false;
			} else {
				smInd = false;
				tvInd = true;
			}
		} else {
			smInd = false;
			tvInd = false;
			airProgramCompCd = null;
		}
	}

	public final boolean isTvInd() {
		return tvInd;
	}

	public final void setTvInd(boolean tvInd) {
		this.tvInd = tvInd;
	}

	public final boolean isSmInd() {
		return smInd;
	}

	public final void setSmInd(boolean smInd) {
		this.smInd = smInd;
	}

	public boolean isGhgInd() {
		return ghgInd;
	}

	public void setGhgInd(boolean ghgInd) {
		this.ghgInd = ghgInd;
	}

	public Integer getOldCorePlaceId() {
		return oldCorePlaceId;
	}

	// If have same EpaEmuId, rename old EU's EpaEmuId for insert this newEU.
	public void insertEmissionUnit(EmissionUnit newEU) {
		for (EmissionUnit tempEU : this.emissionUnits) {
			if (newEU.getEpaEmuId().equals(tempEU.getEpaEmuId())) {
				this.insertEpaEmuId(newEU.getEpaEmuId(),
						newEU.getEmissionUnitTypeCd());
			}
		}
		this.addEmissionUnit(newEU);
	}

	// rename other EpaEmuId
	// Only rename same type and the number equal or greater
	public void insertEpaEmuId(String epaEmuId, String emissionUnitTypeCd) {
		int insertNum = Integer.parseInt(epaEmuId.substring(3));
		for (EmissionUnit tempEU : this.emissionUnits) {
			if (emissionUnitTypeCd.equals(tempEU.getEmissionUnitTypeCd())) {
				int tempNum = Integer.parseInt(tempEU.getEpaEmuId()
						.substring(3));
				if (tempNum >= insertNum) {
					tempNum++;
					tempEU.setEpaEmuId(tempEU.getEmissionUnitTypeCd()
							+ String.format("%03d", tempNum));
				}
			}
		}
	}

	// insert EmissionProcesses(only rename other EP, need to add the EU)
	public void insertEmissionProcess(EmissionProcess newEP) {
		String prefixEP = "PRC";
		int insertNum = Integer.parseInt(newEP.getProcessId().substring(3));
		for (EmissionUnit tempEU : this.emissionUnits) {
			for (EmissionProcess tempEP : tempEU.getEmissionProcesses()) {
				int tempNum = Integer.parseInt(tempEP.getProcessId().substring(
						3));
				if (tempNum >= insertNum) {
					tempNum++;
					tempEP.setProcessId(prefixEP
							+ String.format("%03d", tempNum));
				}
			}
		}
	}

	public void insertControlEquip(ControlEquipment newCE) {
		for (ControlEquipment tempCE : this.controlEquips) {
			if (newCE.getControlEquipmentId().equals(
					tempCE.getControlEquipmentId())) {
				this.insertControlEquipmentId(newCE.getControlEquipmentId(),
						newCE.getEquipmentTypeCd());
			}
		}
		this.addControlEquip(newCE);
	}

	// rename other ControlEquipmentId
	private void insertControlEquipmentId(String controlEquipmentId,
			String equipmentTypeCd) {
		int insertNum = Integer.parseInt(controlEquipmentId.substring(3));
		for (ControlEquipment tempCE : this.controlEquips) {
			if (equipmentTypeCd.equals(tempCE.getEquipmentTypeCd())) {
				int tempNum = Integer.parseInt(tempCE.getControlEquipmentId()
						.substring(3));
				if (tempNum >= insertNum) {
					tempNum++;
					tempCE.setControlEquipmentId(tempCE.getEquipmentTypeCd()
							+ String.format("%03d", tempNum));
				}
			}
		}
	}

	public void insertEgressPoint(EgressPoint newRP) {
		for (EgressPoint tempRP : this.egrPoints) {
			if (newRP.getReleasePointId().equals(tempRP.getReleasePointId())) {
				this.insertReleasePointId(newRP.getReleasePointId(),
						newRP.getEgressPointTypeCd());
			}
		}
		this.addEgressPoint(newRP);
	}

	// rename other ReleasePointId
	private void insertReleasePointId(String releasePointId,
			String egressPointTypeCd) {
		int insertNum = Integer.parseInt(releasePointId.substring(3));
		for (EgressPoint tempRP : this.egrPoints) {
			if (egressPointTypeCd.equals(tempRP.getEgressPointTypeCd())) {
				int tempNum = Integer.parseInt(tempRP.getReleasePointId()
						.substring(3));
				if (tempNum >= insertNum) {
					tempNum++;
					tempRP.setReleasePointId(tempRP.getEgressPointTypeCd()
							+ String.format("%03d", tempNum));
				}
			}
		}

	}

	public final ValidationMessage[] checkFedRules() {
		this.clearValidationMessages();
		ValidationMessage[] valMsgs = validateFedRules();
		return valMsgs;
	}

	public ValidationMessage checkAFS() {
		
		ValidationMessage validationMessage = null;
		if (Utility.isNullOrEmpty(this.afs)) {
			return validationMessage;
		}

		String patternStr = "\\d{10}";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(this.afs);
		if (matcher.find()) {
			return validationMessage;
		}

		validationMessage = new ValidationMessage(
				"afs",
				"The format of AFS is incorrect. AFS is a 10 digit length number.",
				ValidationMessage.Severity.ERROR, "facility");
		
		return validationMessage;
	}

	public final ValidationMessage[] checkOpStatus() {
		ArrayList<ValidationMessage> msgs = new ArrayList<ValidationMessage>();
		ValidationMessage msg = null;

		if (OperatingStatusDef.IA.equals(operatingStatusCd)) {
			if (inactiveDate == null) {
				msg = new ValidationMessage(
						"inactiveDate",
						"Facility is in inactive state; Inactive date must be provided",
						ValidationMessage.Severity.ERROR, "facility");

				validationMessages.put("inactiveDate", msg);

				msgs.add(msg);
			}
		}

		if (OperatingStatusDef.SD.equals(operatingStatusCd)) {
			if (shutdownDate == null) {
				msg = new ValidationMessage(
						"shutdownDate",
						"Facility is in shutdown state; Shutdown date must be provided",
						ValidationMessage.Severity.ERROR, "facility");

				validationMessages.put("shutdownDate", msg);

				msgs.add(msg);
			} else {
				validationMessages.remove("shutdownDate");
				if (!shutdownDate.before(new Timestamp(System
						.currentTimeMillis()))) {
					msg = new ValidationMessage(
							"shutdownDate",
							"Facility is in shutdown state; Shutdown date must be in the past",
							ValidationMessage.Severity.ERROR, "facility");

					validationMessages.put("shutdownDate:1", msg);

					msgs.add(msg);
				} else {
					validationMessages.remove("shutdownDate:1");
				}
			}

			if (shutdownNotifEnabled) {
				if (shutdownNotifDate == null) {
					msg = new ValidationMessage(
							"shutdownNotifDate",
							"Title V Facility is in shutdown state; Shutdown Notification date must be provided",
							ValidationMessage.Severity.ERROR, "facility");

					validationMessages.put("shutdownNotifDate", msg);

					msgs.add(msg);
				} else {
					validationMessages.remove("shutdownNotifDate");
					if (!shutdownNotifDate.before(new Timestamp(System
							.currentTimeMillis()))) {
						msg = new ValidationMessage(
								"shutdownNotifDate",
								"Title V Facility is in shutdown state; Shutdown Notification date must be in the past",
								ValidationMessage.Severity.ERROR, "facility");

						validationMessages.put("shutdownNotifDate:1", msg);

						msgs.add(msg);
					} else {
						validationMessages.remove("shutdownNotifDate:1");
					}
				}
			}
		} else {
			if (shutdownDate != null) {
				msg = new ValidationMessage(
						"shutdownDate",
						"Facility is not in shutdown state; Shutdown date must not be provided",
						ValidationMessage.Severity.ERROR, "facility");

				validationMessages.put("shutdownDate", msg);

				msgs.add(msg);
			} else {
				validationMessages.remove("shutdownDate");
			}
		}

		return msgs.toArray(new ValidationMessage[0]);
	}
	
	public final String getGoogleMapsURL() {
		return LocationCalculator.getGoogleMapsURL(phyAddr.getLatitude(),
				phyAddr.getLongitude(), getName());
	}
	
	private final Contact getOldestFacilityContact(List<Contact> activeContacts, String contactTypeCd, String facilityId) {
		List<ContactType> activeContactTypes = new ArrayList<ContactType> ();
		List<Long> startDates = new ArrayList<Long>();
		Contact oldestContact = null;
		
		// get ordered contact types list
		for(Contact contact : activeContacts) {
			ContactType currentContactType = contact.getCurrentContactType(
					contactTypeCd, facilityId);
			if (currentContactType != null) {
				activeContactTypes.add(currentContactType);
				
				// need start dates for getting the oldest start date
				startDates.add(currentContactType.getStartDateLong());
			}
		}
		
		// get oldest start date
		Collections.sort(startDates);
		Long oldestStartDate = null;
		if(startDates.size() > 0) {
			oldestStartDate = startDates.get(0);
		}
		
		// search for contact with oldest start date (will be in alphabetical order)
		if(oldestStartDate != null) {
			ContactType oldestActiveContactType = null;
			for(ContactType cType : activeContactTypes) {
				if(cType.getStartDateLong().equals(oldestStartDate)) {
					oldestActiveContactType = cType;
					break;
				}
			}
			
			for (Contact contact : activeContacts) {
				if (contact.getContactId().equals(
						oldestActiveContactType.getContactId())) {
					oldestContact = contact;
				}
			}
		}
		
		return oldestContact;
	}
	
	public BigDecimal getPermittedEmissionsUnitFactor(String unitCd) {
		BigDecimal ret = null;

		ret = new BigDecimal(PermittedEmissionsUnitDef.getFactorData()
				.getItems().getItemDesc(unitCd));

		return ret;
	}
	
	public final Contact[] getNSRBillingContacts() {
		ArrayList<Contact> ret = new ArrayList<Contact>(0);

		for (Contact tempContact : allContacts) {
			if (tempContact.isContactType(ContactTypeDef.NSRB)) {
				ret.add(tempContact);
			}
		}

		return ret.toArray(new Contact[0]);
	}

	public final void addNSRBillingContact(Contact contact) {
		addContact(contact, ContactTypeDef.NSRB);
	}

	public Contact getNSRBillingContact() {
		return nsrBillingContact;
	}

	public void setNSRBillingContact(Contact nsrBillingContact) {
		this.nsrBillingContact = nsrBillingContact;
	}
	
	public MonitorGroup getAssociatedMonitorGroup() {
		return associatedMonitorGroup;
	}

	public void setAssociatedMonitorGroup(MonitorGroup associatedMonitorGroup) {
		this.associatedMonitorGroup = associatedMonitorGroup;
	}
	
	public final List<FacilityCemComLimit> getFacilityCemComLimitList() {

		if (_facilityCemComLimitList == null) {
			_facilityCemComLimitList = new ArrayList<FacilityCemComLimit>(0);
		}
		return _facilityCemComLimitList;
	}

	public final void setFacilityCemComLimitList(
			List<FacilityCemComLimit> fcclList) {
		_facilityCemComLimitList = fcclList;
		if (_facilityCemComLimitList == null) {
			_facilityCemComLimitList = new ArrayList<FacilityCemComLimit>(0);
		}
	}	
	
	public final List<ContinuousMonitor> getContinuousMonitorList() {

		if (_facilityContinuousMonitorList == null) {
			_facilityContinuousMonitorList = new ArrayList<ContinuousMonitor>(0);
		}
		return _facilityContinuousMonitorList;
	}

	public final void setContinuousMonitorList(
			List<ContinuousMonitor> cmlList) {
		_facilityContinuousMonitorList = cmlList;
		if (_facilityContinuousMonitorList == null) {
			_facilityContinuousMonitorList = new ArrayList<ContinuousMonitor>(0);
		}
	}

	public void insertContinuousMonitor(ContinuousMonitor monitor) {
		String prefix = "CM";
		int insertNum = Integer.parseInt(monitor.getMonId().substring(2));
		for (ContinuousMonitor mon : getContinuousMonitorList()) {
			int tempNum = Integer.parseInt(mon.getMonId().substring(2));
			if (tempNum >= insertNum) {
				insertNum = tempNum + 1;
				monitor.setMonId(prefix	+ String.format("%03d", insertNum));
			}
		}
	}

	public void insertFacilityCemComLimit(FacilityCemComLimit limit) {
		String prefix = "LIM";
		int insertNum = Integer.parseInt(limit.getLimId().substring(3));
		for (FacilityCemComLimit lim : getFacilityCemComLimitList()) {
			int existingNum = Integer.parseInt(lim.getLimId().substring(3));
			if (existingNum >= insertNum) {
				insertNum = existingNum + 1;
				limit.setLimId(prefix + String.format("%03d", insertNum));
			}
		}
	}

	public String getAqdEmissionFactorGroupCd() {
		return aqdEmissionFactorGroupCd;
	}

	public void setAqdEmissionFactorGroupCd(String aqdEmissionFactorGroupCd) {
		this.aqdEmissionFactorGroupCd = aqdEmissionFactorGroupCd;
	}

	
	
	public List<HydrocarbonAnalysisPollutant> getHydrocarbonPollutantList() {
		return hydrocarbonPollutantList;
	}

	public void setHydrocarbonPollutantList(List<HydrocarbonAnalysisPollutant> hydrocarbonPollutantList) {
		this.hydrocarbonPollutantList = hydrocarbonPollutantList;
	}

	public void initHydrocarbonPollutantList(){
		hydrocarbonPollutantList.clear();
		List<PollutantDef> hcPollutantDefs = PollutantDef.getHCAnalysisPollutantDefs();           
		for (PollutantDef p: hcPollutantDefs) {
			HydrocarbonAnalysisPollutant hcp = new HydrocarbonAnalysisPollutant();
			hcp.setFpId(this.getFpId());
			hcp.setPollutantCd(p.getCode());
       	 	hydrocarbonPollutantList.add(hcp);
		}
	}
	
	public void synchronizeHydrocarbonPollutantList(){
		List<PollutantDef> pdList = PollutantDef.getHCAnalysisPollutantDefs();
	    for (PollutantDef pd: pdList){
	    	boolean included = false;
	        for (HydrocarbonAnalysisPollutant hcp : hydrocarbonPollutantList){
	            if (pd.getCode().equals(hcp.getPollutantCd())){
	            	included = true;
	            	break;
	            }
	        }
	        if (!included){
	        	HydrocarbonAnalysisPollutant hcp = new HydrocarbonAnalysisPollutant();
				hcp.setFpId(this.getFpId());
				hcp.setPollutantCd(pd.getCode());
				hcp.setSortOrder(pd.getHCAnalysisSortOrder());
	       	 	hydrocarbonPollutantList.add(hcp);
	        }
	    }
		Collections.sort(hydrocarbonPollutantList, new Comparator<HydrocarbonAnalysisPollutant>(){
			@Override
			public int compare(HydrocarbonAnalysisPollutant hcp1, HydrocarbonAnalysisPollutant hcp2) {
				// TODO Auto-generated method stub
				if (hcp1.getSortOrder() != null && hcp2.getSortOrder() != null){
					return hcp1.getSortOrder().compareTo(hcp2.getSortOrder());
				} else if (hcp1.getSortOrder() == null && hcp1.getSortOrder() == null){
					return hcp1.getPollutantCd().compareTo(hcp2.getPollutantCd());
				} else {
					return hcp1.getSortOrder() == null ? 1 : -1;
				}
				
			}
		}
		);
	}

	public void calculateFacHCAnalysisTotal(){	
		for (Iterator<HydrocarbonAnalysisPollutant> it = hydrocarbonPollutantList.iterator(); it.hasNext();){
			if (PollutantDef.TOTAL.equals(it.next().getPollutantCd())){
				it.remove();
				break;
			}
		}
		for (HydrocarbonAnalysisPollutant hcp: hydrocarbonPollutantList){
			if (PollutantDef.TOTAL.equals(hcp.getPollutantCd())){
				hydrocarbonPollutantList.remove(hcp);
				continue;
			}	
		}		
		BigDecimal gasTotal = new BigDecimal(0);
		BigDecimal oilTotal = new BigDecimal(0);
		BigDecimal producedWaterTotal = new BigDecimal(0);
		for (HydrocarbonAnalysisPollutant hcp: hydrocarbonPollutantList){
			if (hcp.getGas() != null){
				gasTotal = gasTotal.add(hcp.getGas());
			}
			if (hcp.getOil() != null){
				oilTotal = oilTotal.add(hcp.getOil());
			}
			if (hcp.getProducedWater() != null){
				producedWaterTotal = producedWaterTotal.add(hcp.getProducedWater());
			}
		}
		HydrocarbonAnalysisPollutant total = new HydrocarbonAnalysisPollutant();
		total.setFpId(this.getFpId());//?????
		total.setPollutantCd(PollutantDef.TOTAL);
		total.setGas(gasTotal);
		total.setOil(oilTotal);
		total.setProducedWater(producedWaterTotal);
		total.setReadOnly(true);
		hydrocarbonPollutantList.add(total);
	}	
	
	public List<ValidationMessage> validateHydrocarbonAnalysis(){
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		boolean blankColumnGas = true;		
		boolean blankColumnOil = true;
		boolean blankColumnProducedWater = true;
		
		ValidationMessage columnTotalWarning = null; 
		HydrocarbonAnalysisPollutant hcpTotal = new HydrocarbonAnalysisPollutant();
		for (HydrocarbonAnalysisPollutant hcp: hydrocarbonPollutantList){
			if (PollutantDef.TOTAL.equals(hcp.getPollutantCd())){
				hcpTotal = hcp;
				continue;
			}
			if (hcp.getGas() != null){
				blankColumnGas = false;
			} 
			if (hcp.getOil() != null){
				blankColumnOil = false;
			} 
			if (hcp.getProducedWater() != null){
				blankColumnProducedWater = false;
			} 	
			for (ValidationMessage msg: hcp.validate()){
				messages.add(msg);
			}
		}
		//check if column totals are between 90 - 100 and create warning message.
		columnTotalWarning = validateHCColumnTotals(hcpTotal, blankColumnGas, blankColumnOil, blankColumnProducedWater);
		if  (columnTotalWarning != null){
			messages.add(columnTotalWarning);
		}
		return messages;
	}
	
	public ValidationMessage validateHCColumnTotals(HydrocarbonAnalysisPollutant hcpTotal, boolean blankColumnGas, 
			boolean blankColumnOil, boolean blankColumnProducedWater){
		BigDecimal gas = hcpTotal.getGas();
		BigDecimal oil = hcpTotal.getOil();
		BigDecimal producedWater = hcpTotal.getProducedWater();
		
		//if it is column total,  gas/oil/producedWater should never be null
		if (gas == null || oil == null || producedWater == null){
			ValidationMessage message = new ValidationMessage("columnTotal", 
					"Column Totals are not re-calculated", ValidationMessage.Severity.WARNING, "HC Analysis - Total");		
			return message;
		}
	
		if (!blankColumnGas){
			if (gas.compareTo(HydrocarbonAnalysisPollutant.HC_COLUMN_TOTAL_MIN) < 0 || gas.compareTo(HydrocarbonAnalysisPollutant.HC_COLUMN_TOTAL_MAX) > 0){
				ValidationMessage message = new ValidationMessage("gasTotal", 
						"Warning, the sum of the component percentages is less than 90% or greater than 110%", ValidationMessage.Severity.WARNING, "HC Analysis - Total");		
				if (this.getAqdEmissionFactorGroupCd() != null && gas.compareTo(new BigDecimal(0)) == 0){
					message = null;
				}
				if (message != null)
					return message;
			}
		}

		if (!blankColumnOil){
			if (oil.compareTo(HydrocarbonAnalysisPollutant.HC_COLUMN_TOTAL_MIN) < 0 || oil.compareTo(HydrocarbonAnalysisPollutant.HC_COLUMN_TOTAL_MAX) > 0){
				ValidationMessage message = new ValidationMessage("oilTotal", 
						"Warning, the sum of the component percentages is less than 90% or greater than 110%", ValidationMessage.Severity.WARNING, "HC Analysis - Total");		
				if (this.getAqdEmissionFactorGroupCd() != null && oil.compareTo(new BigDecimal(0)) == 0){
					message = null;
				}
				if (message != null)
					return message;
			}
		}		
		if (!blankColumnProducedWater){
			if (producedWater.compareTo(HydrocarbonAnalysisPollutant.HC_COLUMN_TOTAL_MIN) < 0 || producedWater.compareTo(HydrocarbonAnalysisPollutant.HC_COLUMN_TOTAL_MAX) > 0){
				ValidationMessage message = new ValidationMessage("producedWaterTotal", 
						"Warning, the sum of the component percentages is less than 90% or greater than 110%", ValidationMessage.Severity.WARNING, "HC Analysis - Total");		
				if (this.getAqdEmissionFactorGroupCd() != null && producedWater.compareTo(new BigDecimal(0)) == 0){
					message = null;
				}
				if (message != null)
					return message;
			}
		}		

		return null;
	}
	
	public HydrocarbonAnalysisSampleDetail getHydrocarbonAnalysisSampleDetail() {
		return hydrocarbonAnalysisSampleDetail;
	}

	public void setHydrocarbonAnalysisSampleDetail(HydrocarbonAnalysisSampleDetail hydrocarbonAnalysisSampleDetail) {
		this.hydrocarbonAnalysisSampleDetail = hydrocarbonAnalysisSampleDetail;
	}
	
	public List<HCAnalysisSampleDetailRow> convertHydrocarbonAnalysisSampleDetail(){
		List<HCAnalysisSampleDetailRow> hcAnalysisSampleDetailList = new ArrayList<HCAnalysisSampleDetailRow>();
		
		if (null == hydrocarbonAnalysisSampleDetail){
			hcAnalysisSampleDetailList.add(new HCAnalysisSampleDetailRow(
					HCAnalysisSampleDetailRow.SAMPLE_FACILITY_NAME, null, null, null));
			hcAnalysisSampleDetailList.add(new HCAnalysisSampleDetailRow(
					HCAnalysisSampleDetailRow.SAMPLE_FACILITY_API, null, null, null));
			hcAnalysisSampleDetailList.add(new HCAnalysisSampleDetailRow(
					HCAnalysisSampleDetailRow.SAMPLE_FACILITY_PRODUCING_FIELD, null, null, null));
			hcAnalysisSampleDetailList.add(new HCAnalysisSampleDetailRow(
					HCAnalysisSampleDetailRow.SAMPLE_FACILITY_PRODUCING_FORMATION, null, null, null));
			hcAnalysisSampleDetailList.add(new HCAnalysisSampleDetailRow(
					HCAnalysisSampleDetailRow.SAMPLE_DATE, null, null, null));
			hcAnalysisSampleDetailList.add(new HCAnalysisSampleDetailRow(
					HCAnalysisSampleDetailRow.SAMPLE_POINT, null, null, null));
			hcAnalysisSampleDetailList.add(new HCAnalysisSampleDetailRow(
					HCAnalysisSampleDetailRow.ANALYSIS_COMPANY_NAME, null, null, null));
			hcAnalysisSampleDetailList.add(new HCAnalysisSampleDetailRow(
					HCAnalysisSampleDetailRow.ANALYSIS_DATE, null, null, null));
			hcAnalysisSampleDetailList.add(new HCAnalysisSampleDetailRow(
					HCAnalysisSampleDetailRow.SAMPLE_PRESSURE, null, null, null));
			hcAnalysisSampleDetailList.add(new HCAnalysisSampleDetailRow(
					HCAnalysisSampleDetailRow.SAMPLE_TEMP, null, null, null));
			hcAnalysisSampleDetailList.add(new HCAnalysisSampleDetailRow(
					HCAnalysisSampleDetailRow.SAMPLE_FLOW_RATE, null, null, null));
			
			return hcAnalysisSampleDetailList;
		}
		
		HCAnalysisSampleDetailRow hcSampleDetailRow1 = new HCAnalysisSampleDetailRow(
				HCAnalysisSampleDetailRow.SAMPLE_FACILITY_NAME,
				hydrocarbonAnalysisSampleDetail.getSampleFacilityNameGas(), 
				hydrocarbonAnalysisSampleDetail.getSampleFacilityNameOil(), 
				hydrocarbonAnalysisSampleDetail.getSampleFacilityNameWater()
		);
		HCAnalysisSampleDetailRow hcSampleDetailRow2 = new HCAnalysisSampleDetailRow(
				HCAnalysisSampleDetailRow.SAMPLE_FACILITY_API,
				hydrocarbonAnalysisSampleDetail.getSampleFacilityAPIGas(), 
				hydrocarbonAnalysisSampleDetail.getSampleFacilityAPIOil(), 
				hydrocarbonAnalysisSampleDetail.getSampleFacilityAPIWater()
		);
		HCAnalysisSampleDetailRow hcSampleDetailRow3 = new HCAnalysisSampleDetailRow(
				HCAnalysisSampleDetailRow.SAMPLE_FACILITY_PRODUCING_FIELD,
				hydrocarbonAnalysisSampleDetail.getSampleFacilityProducingFieldGas(), 
				hydrocarbonAnalysisSampleDetail.getSampleFacilityProducingFieldOil(), 
				hydrocarbonAnalysisSampleDetail.getSampleFacilityProducingFieldWater()
		);
		HCAnalysisSampleDetailRow hcSampleDetailRow4 = new HCAnalysisSampleDetailRow(
				HCAnalysisSampleDetailRow.SAMPLE_FACILITY_PRODUCING_FORMATION,
				hydrocarbonAnalysisSampleDetail.getSampleFacilityProducingFormationGas(), 
				hydrocarbonAnalysisSampleDetail.getSampleFacilityProducingFormationOil(), 
				hydrocarbonAnalysisSampleDetail.getSampleFacilityProducingFormationWater()
		);
		HCAnalysisSampleDetailRow hcSampleDetailRow5 = new HCAnalysisSampleDetailRow(
				HCAnalysisSampleDetailRow.SAMPLE_DATE,
				hydrocarbonAnalysisSampleDetail.getSampleDateGas(), 
				hydrocarbonAnalysisSampleDetail.getSampleDateOil(), 
				hydrocarbonAnalysisSampleDetail.getSampleDateWater()
		);
		HCAnalysisSampleDetailRow hcSampleDetailRow6 = new HCAnalysisSampleDetailRow(
				HCAnalysisSampleDetailRow.SAMPLE_POINT,
				hydrocarbonAnalysisSampleDetail.getSamplePointGas(), 
				hydrocarbonAnalysisSampleDetail.getSamplePointOil(), 
				hydrocarbonAnalysisSampleDetail.getSamplePointWater()
		);
		HCAnalysisSampleDetailRow hcSampleDetailRow7 = new HCAnalysisSampleDetailRow(
				HCAnalysisSampleDetailRow.ANALYSIS_COMPANY_NAME,
				hydrocarbonAnalysisSampleDetail.getAnalysisCompanyNameGas(), 
				hydrocarbonAnalysisSampleDetail.getAnalysisCompanyNameOil(), 
				hydrocarbonAnalysisSampleDetail.getAnalysisCompanyNameWater()
		);
		
		HCAnalysisSampleDetailRow hcSampleDetailRow8 = new HCAnalysisSampleDetailRow(
				HCAnalysisSampleDetailRow.ANALYSIS_DATE,
				hydrocarbonAnalysisSampleDetail.getAnalysisDateGas(), 
				hydrocarbonAnalysisSampleDetail.getAnalysisDateOil(), 
				hydrocarbonAnalysisSampleDetail.getAnalysisDateWater()
		);
		
		HCAnalysisSampleDetailRow hcSampleDetailRow9 = new HCAnalysisSampleDetailRow(
				HCAnalysisSampleDetailRow.SAMPLE_PRESSURE,
				hydrocarbonAnalysisSampleDetail.getSamplePressureTxtGas(), 
				hydrocarbonAnalysisSampleDetail.getSamplePressureTxtOil(), 
				hydrocarbonAnalysisSampleDetail.getSamplePressureTxtWater()
		);
		
		HCAnalysisSampleDetailRow hcSampleDetailRow10 = new HCAnalysisSampleDetailRow(
				HCAnalysisSampleDetailRow.SAMPLE_TEMP,
				hydrocarbonAnalysisSampleDetail.getSampleTempTxtGas(), 
				hydrocarbonAnalysisSampleDetail.getSampleTempTxtOil(), 
				hydrocarbonAnalysisSampleDetail.getSampleTempTxtWater()
		);
		
		HCAnalysisSampleDetailRow hcSampleDetailRow11 = new HCAnalysisSampleDetailRow(
				HCAnalysisSampleDetailRow.SAMPLE_FLOW_RATE,
				hydrocarbonAnalysisSampleDetail.getSampleFlowRateTxtGas(), 
				hydrocarbonAnalysisSampleDetail.getSampleFlowRateTxtOil(), 
				hydrocarbonAnalysisSampleDetail.getSampleFlowRateTxtWater()
		);	
		hcAnalysisSampleDetailList.add(hcSampleDetailRow1);
		hcAnalysisSampleDetailList.add(hcSampleDetailRow2);
		hcAnalysisSampleDetailList.add(hcSampleDetailRow3);
		hcAnalysisSampleDetailList.add(hcSampleDetailRow4);
		hcAnalysisSampleDetailList.add(hcSampleDetailRow5);
		hcAnalysisSampleDetailList.add(hcSampleDetailRow6);
		hcAnalysisSampleDetailList.add(hcSampleDetailRow7);
		hcAnalysisSampleDetailList.add(hcSampleDetailRow8);
		hcAnalysisSampleDetailList.add(hcSampleDetailRow9);
		hcAnalysisSampleDetailList.add(hcSampleDetailRow10);
		hcAnalysisSampleDetailList.add(hcSampleDetailRow11);
		return hcAnalysisSampleDetailList;
	}
	
	public List<ValidationMessage> validateHydrocarbonAnalysisSampleDetail(){
		List<ValidationMessage> messages = Arrays.asList(this.hydrocarbonAnalysisSampleDetail.validate());
		return messages;
	}

	public DecaneProperties getDecaneProperties() {
		return decaneProperties;
	}

	public void setDecaneProperties(DecaneProperties decaneProperties) {
		this.decaneProperties = decaneProperties;
	}

	
	public boolean isAdministrativeHold() {
		return administrativeHold;
	}

	public void setAdministrativeHold(boolean administrativeHold) {
		System.out.print("set Administrative Hold - create field audit log, length = " + this.getFieldAuditLogs().length);
		checkDirty("fah", getFacilityId(), String.valueOf(this.administrativeHold), String.valueOf(administrativeHold));
		this.administrativeHold = administrativeHold;
	}
}
