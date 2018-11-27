package us.oh.state.epa.stars2.webcommon.documentgeneration.facility;

import java.rmi.RemoteException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipment;
import us.oh.state.epa.stars2.database.dbObjects.facility.EgressPoint;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityNode;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityOwner;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRole;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountyDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.def.CeOperatingStatusDef;
import us.oh.state.epa.stars2.def.CerrClassDef;
import us.oh.state.epa.stars2.def.ContEquipTypeDef;
import us.oh.state.epa.stars2.def.ContactTitle;
import us.oh.state.epa.stars2.def.EgOperatingStatusDef;
import us.oh.state.epa.stars2.def.EgrPointTypeDef;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.FacilityRoleDef;
import us.oh.state.epa.stars2.def.FacilityTypeDef;
import us.oh.state.epa.stars2.def.NAICSDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.PortableGroupTypes;
import us.oh.state.epa.stars2.util.DocumentGenerationException;
import us.oh.state.epa.stars2.webcommon.documentgeneration.DocumentBuilder;
import us.wy.state.deq.impact.database.dbObjects.company.Company;

public class FacilityInfoBuilder extends DocumentBuilder {

	private static final long serialVersionUID = -7450799791623239577L;
	
	private Integer fpId;

	public FacilityInfoBuilder() {
		super();
	}

	public Integer getFpId() {
		return fpId;
	}

	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}

	@Override
	public <E> void setId(final E id) {
		this.fpId = (Integer) id;
	}

	@Override
	public void loadData() throws DocumentGenerationException {

		if (null != this.fpId) {

			try {

				Facility facility = getFacilityService().retrieveFacility(this.fpId);

				if (null != facility) {

					loadBasicFacilityData(facility);
					loadOwnershipData(facility);
					loadPhysicalAddressData(facility);
					loadFacilityRolesData(facility);
					loadNAICSData(facility);
					loadBillingContactData(facility);
					loadComplianceContactData(facility);
					loadEmissionsContactData(facility);
					loadEnvironmentalContactData(facility);
					loadMonitoringContactData(facility);
					loadOnsiteOperatorContactData(facility);
					loadPermittingContactData(facility);
					loadResponsibleOfficialContactData(facility);
					loadFacilityDistrictData(facility);
					loadMiscData(facility);
					loadTvCertRepDueDateData(facility);
					loadEmissionsUnitsData(facility);
					loadControlEquipmentData(facility.getFpId());
					loadReleasePointsData(facility.getFpId());
					loadHeadingData();
				}
			} catch (RemoteException re) {
				logger.error("An error occured when getting the facility data for facility with fpId " + this.fpId + ":"
						+ re.getMessage(), re);
				throw new DocumentGenerationException(re.getMessage(), re);
			}
		}
	}

	private void loadBasicFacilityData(final Facility facility) {

		if (null != facility) {
			
			this.tagValueMap.put("facilityID", facility.getFacilityId());
			this.tagValueMap.put("facilityName", facility.getName());
			this.tagValueMap.put("facilityDescription", facility.getDesc());
			this.tagValueMap.put("facilityOperatingStatus",
					OperatingStatusDef.getDemData().getItems().getItemDesc(facility.getOperatingStatusCd()));
			this.tagValueMap.put("facilityClass",
					PermitClassDef.getData().getItems().getItemDesc(facility.getPermitClassCd()));
			this.tagValueMap.put("facilityType",
					FacilityTypeDef.getTextData().getItems().getItemDesc(facility.getFacilityTypeCd()));
			this.tagValueMap.put("facilityAfs", facility.getAfs());
			this.tagValueMap.put("facilityCerrClass",
					CerrClassDef.getData().getItems().getItemDesc(facility.getCerrClassCd()));
			this.tagValueMap.put("facilityInspectionClass", null);
			this.tagValueMap.put("portableType",
					PortableGroupTypes.getData().getItems().getItemDesc(facility.getPortableGroupTypeCd()));
			this.tagValueMap.put("perStart", "");
			this.tagValueMap.put("perEnd", "");
		}
	}

	private void loadPhysicalAddressData(final Facility facility) throws RemoteException {

		if (null != facility && null != facility.getPhyAddr()) {

			Address physicalAddress = facility.getPhyAddr();

			this.tagValueMap.put("locationEffectiveDate", physicalAddress.getBeginDate());
			this.tagValueMap.put("facilityAddrLine1", physicalAddress.getAddressLine1());
			this.tagValueMap.put("facilityAddrLine2", physicalAddress.getAddressLine2());
			this.tagValueMap.put("facilityLat", physicalAddress.getLatitude());
			this.tagValueMap.put("facilityLong", physicalAddress.getLongitude());
			this.tagValueMap.put("facilityPlss", physicalAddress.getPlss());
			this.tagValueMap.put("quarterQuarterVal", physicalAddress.getQuarterQuarter());
			this.tagValueMap.put("quarterVal", physicalAddress.getQuarter());
			this.tagValueMap.put("sectionVal", physicalAddress.getSection());
			this.tagValueMap.put("townShipVal", physicalAddress.getTownship());
			this.tagValueMap.put("rangeVal", physicalAddress.getRange());
			this.tagValueMap.put("facilityCity", physicalAddress.getCityName());
			this.tagValueMap.put("facilityState", physicalAddress.getState());

			if (null != physicalAddress.getCountyCd()) {
				CountyDef cd = getInfrastructureService().retrieveCounty(physicalAddress.getCountyCd());
				if (null != cd) {
					this.tagValueMap.put("facilityCounty", cd.getCountyNm());
					this.tagValueMap.put("countySeat", cd.getCountySeat());
				}
			}

			String facilityZip = physicalAddress.getZipCode5();
			if (null != facilityZip && null != physicalAddress.getZipCode4()) {
				facilityZip += "-" + physicalAddress.getZipCode4();
			}
			this.tagValueMap.put("facilityZip", facilityZip);
		}
	}

	private void loadBillingContactData(final Facility facility) {

		if (null != facility && null != facility.getBillingContact()) {

			Contact billingContact = facility.getBillingContact();

			StringBuffer billingName = new StringBuffer();

			String title = ContactTitle.getData().getItems().getItemDesc(billingContact.getTitleCd());
			if (null != title) {
				billingName.append(title + " ");
			}

			String fName = billingContact.getFirstNm();
			if (null != fName) {
				billingName.append(fName + " ");
			}

			String lName = billingContact.getLastNm();
			if (null != lName) {
				billingName.append(lName);
			}

			String suffixCd = billingContact.getSuffixCd();
			if (null != suffixCd) {
				billingName.append(" " + suffixCd);
			}

			this.tagValueMap.put("billingName", billingName.toString());
			this.tagValueMap.put("billingCoName", billingContact.getCompanyName());

			if (null != billingContact.getAddress()) {

				Address billContAddress = billingContact.getAddress();

				this.tagValueMap.put("billingAddrLine1", billContAddress.getAddressLine1());
				this.tagValueMap.put("billingAddrLine2", billContAddress.getAddressLine2());
				this.tagValueMap.put("billingCity", billContAddress.getCityName());
				this.tagValueMap.put("billingState", billContAddress.getState());

				String zip = billContAddress.getZipCode5();
				if (null != zip && null != billContAddress.getZipCode4()) {
					zip += "-" + billContAddress.getZipCode4();
				}
				this.tagValueMap.put("billingZip", zip);
			}

			this.tagValueMap.put("billingPhone", getFormattedPhoneNbr(billingContact.getPhoneNo()));
			this.tagValueMap.put("billingEmail", billingContact.getEmailAddressTxt());
			this.tagValueMap.put("billingEmailSecondary", billingContact.getEmailAddressTxt2());
		}
	}

	private void loadComplianceContactData(final Facility facility) {

		if (null != facility && null != facility.getComplianceContact()) {

			Contact complianceContact = facility.getComplianceContact();

			StringBuffer complianceName = new StringBuffer();

			String title = ContactTitle.getData().getItems().getItemDesc(complianceContact.getTitleCd());
			this.tagValueMap.put("complianceContactTitle", title);
			if (null != title) {
				complianceName.append(title + " ");
			}

			String fName = complianceContact.getFirstNm();
			this.tagValueMap.put("complianceContactFName", fName);
			if (null != fName) {
				complianceName.append(fName + " ");
			}

			String lName = complianceContact.getLastNm();
			this.tagValueMap.put("complianceContactLName", lName);
			if (null != lName) {
				complianceName.append(lName);
			}

			String suffixCd = complianceContact.getSuffixCd();
			if (null != suffixCd) {
				complianceName.append(" " + suffixCd);
			}
			this.tagValueMap.put("complianceName", complianceName.toString());

			this.tagValueMap.put("complianceCoName", complianceContact.getCompanyName());
			this.tagValueMap.put("complianceContactJobTitle", complianceContact.getCompanyTitle());
			this.tagValueMap.put("compliancePhone", getFormattedPhoneNbr(complianceContact.getPhoneNo()));
			this.tagValueMap.put("complianceEmail", complianceContact.getEmailAddressTxt());
			this.tagValueMap.put("complianceEmailSecondary", complianceContact.getEmailAddressTxt2());

			Address complContAddress = complianceContact.getAddress();
			if (null != complContAddress) {

				this.tagValueMap.put("complianceAddrLine1", complContAddress.getAddressLine1());
				this.tagValueMap.put("complianceAddrLine2", complContAddress.getAddressLine2());
				this.tagValueMap.put("complianceCity", complContAddress.getCityName());
				this.tagValueMap.put("complianceState", complContAddress.getState());

				String zip = complContAddress.getZipCode5();
				if (null != zip && null != complContAddress.getZipCode4()) {
					zip += "-" + complContAddress.getZipCode4();
				}
				this.tagValueMap.put("complianceZip", zip);
			}
		}
	}

	private void loadEmissionsContactData(final Facility facility) {

		if (null != facility && null != facility.getEmissionsContact()) {

			Contact emissionsContact = facility.getEmissionsContact();

			StringBuffer emissionsName = new StringBuffer();

			String title = ContactTitle.getData().getItems().getItemDesc(emissionsContact.getTitleCd());
			if (null != title) {
				emissionsName.append(title + " ");
			}

			String fName = emissionsContact.getFirstNm();
			if (null != fName) {
				emissionsName.append(fName + " ");
			}

			String lName = emissionsContact.getLastNm();
			if (null != lName) {
				emissionsName.append(lName);
			}

			String suffixCd = emissionsContact.getSuffixCd();
			if (null != suffixCd) {
				emissionsName.append(" " + suffixCd);
			}
			this.tagValueMap.put("emissionsName", emissionsName.toString());

			this.tagValueMap.put("emissionsCoName", emissionsContact.getCompanyName());
			this.tagValueMap.put("emissionsPhone", getFormattedPhoneNbr(emissionsContact.getPhoneNo()));
			this.tagValueMap.put("emissionsEmail", emissionsContact.getEmailAddressTxt());
			this.tagValueMap.put("emissionsEmailSecondary", emissionsContact.getEmailAddressTxt2());

			Address emisContAddress = emissionsContact.getAddress();
			if (null != emisContAddress) {

				this.tagValueMap.put("emissionsAddrLine1", emisContAddress.getAddressLine1());
				this.tagValueMap.put("emissionsAddrLine2", emisContAddress.getAddressLine2());
				this.tagValueMap.put("emissionsCity", emisContAddress.getCityName());
				this.tagValueMap.put("emissionsState", emisContAddress.getState());

				String zip = emisContAddress.getZipCode5();
				if (null != zip && null != emisContAddress.getZipCode4()) {
					zip += "-" + emisContAddress.getZipCode4();
				}
				this.tagValueMap.put("emissionsZip", zip);
			}
		}
	}

	private void loadEnvironmentalContactData(final Facility facility) {

		if (null != facility && null != facility.getEnvironmentalContact()) {

			Contact envContact = facility.getEnvironmentalContact();

			StringBuffer envName = new StringBuffer();

			String title = ContactTitle.getData().getItems().getItemDesc(envContact.getTitleCd());
			this.tagValueMap.put("environmentalTitle", title);
			if (null != title) {
				envName.append(title + " ");
			}

			String fName = envContact.getFirstNm();
			if (null != fName) {
				envName.append(fName + " ");
			}

			String lName = envContact.getLastNm();
			if (null != lName) {
				envName.append(lName);
			}

			String suffixCd = envContact.getSuffixCd();
			if (null != suffixCd) {
				envName.append(" " + suffixCd);
			}
			this.tagValueMap.put("environmentalName", envName.toString());

			this.tagValueMap.put("environmentalCoName", envContact.getCompanyName());
			this.tagValueMap.put("environmentalJobTitle", envContact.getCompanyTitle());
			this.tagValueMap.put("environmentalPhone", getFormattedPhoneNbr(envContact.getPhoneNo()));
			this.tagValueMap.put("environmentalEmail", envContact.getEmailAddressTxt());
			this.tagValueMap.put("environmentalEmailSecondary", envContact.getEmailAddressTxt2());

			Address envContAddress = envContact.getAddress();
			if (null != envContAddress) {

				this.tagValueMap.put("environmentalAddrLine1", envContAddress.getAddressLine1());
				this.tagValueMap.put("environmentalAddrLine2", envContAddress.getAddressLine2());
				this.tagValueMap.put("environmentalCity", envContAddress.getCityName());
				this.tagValueMap.put("environmentalState", envContAddress.getState());

				String zip = envContAddress.getZipCode5();
				if (null != zip && null != envContAddress.getZipCode4()) {
					zip += "-" + envContAddress.getZipCode4();
				}
				this.tagValueMap.put("environmentalZip", zip);
			}
		}
	}

	private void loadMonitoringContactData(final Facility facility) {

		if (null != facility && null != facility.getMonitoringContact()) {

			Contact monitoringContact = facility.getMonitoringContact();

			StringBuffer monitoringName = new StringBuffer();

			String title = ContactTitle.getData().getItems().getItemDesc(monitoringContact.getTitleCd());
			if (null != title) {
				monitoringName.append(title + " ");
			}

			String fName = monitoringContact.getFirstNm();
			if (null != fName) {
				monitoringName.append(fName + " ");
			}

			String lName = monitoringContact.getLastNm();
			if (null != lName) {
				monitoringName.append(lName);
			}

			String suffixCd = monitoringContact.getSuffixCd();
			if (null != suffixCd) {
				monitoringName.append(" " + suffixCd);
			}
			this.tagValueMap.put("monitoringName", monitoringName.toString());

			this.tagValueMap.put("monitoringCoName", monitoringContact.getCompanyName());
			this.tagValueMap.put("monitoringPhone", getFormattedPhoneNbr(monitoringContact.getPhoneNo()));
			this.tagValueMap.put("monitoringEmail", monitoringContact.getEmailAddressTxt());
			this.tagValueMap.put("monitoringEmailSecondary", monitoringContact.getEmailAddressTxt2());

			Address monitoringContAddress = monitoringContact.getAddress();
			if (null != monitoringContAddress) {

				this.tagValueMap.put("monitoringAddrLine1", monitoringContAddress.getAddressLine1());
				this.tagValueMap.put("monitoringAddrLine2", monitoringContAddress.getAddressLine2());
				this.tagValueMap.put("monitoringCity", monitoringContAddress.getCityName());
				this.tagValueMap.put("monitoringState", monitoringContAddress.getState());

				String zip = monitoringContAddress.getZipCode5();
				if (null != zip && null != monitoringContAddress.getZipCode4()) {
					zip += "-" + monitoringContAddress.getZipCode4();
				}
				this.tagValueMap.put("monitoringZip", zip);
			}
		}
	}

	private void loadOnsiteOperatorContactData(final Facility facility) {

		if (null != facility && null != facility.getOnSiteOperatorContact()) {

			Contact onsiteOpContact = facility.getOnSiteOperatorContact();

			StringBuffer onsiteOpName = new StringBuffer();

			String title = ContactTitle.getData().getItems().getItemDesc(onsiteOpContact.getTitleCd());
			if (null != title) {
				onsiteOpName.append(title + " ");
			}

			String fName = onsiteOpContact.getFirstNm();
			if (null != fName) {
				onsiteOpName.append(fName + " ");
			}

			String lName = onsiteOpContact.getLastNm();
			if (null != lName) {
				onsiteOpName.append(lName);
			}

			String suffixCd = onsiteOpContact.getSuffixCd();
			if (null != suffixCd) {
				onsiteOpName.append(" " + suffixCd);
			}
			this.tagValueMap.put("onSiteOperatorName", onsiteOpName.toString());

			this.tagValueMap.put("onSiteOperatorCoName", onsiteOpContact.getCompanyName());
			this.tagValueMap.put("onSiteOperatorPhone", getFormattedPhoneNbr(onsiteOpContact.getPhoneNo()));
			this.tagValueMap.put("onSiteOperatorEmail", onsiteOpContact.getEmailAddressTxt());
			this.tagValueMap.put("onSiteOperatorEmailSecondary", onsiteOpContact.getEmailAddressTxt2());

			Address onsiteOpContAddress = onsiteOpContact.getAddress();
			if (null != onsiteOpContAddress) {

				this.tagValueMap.put("onSiteOperatorAddrLine1", onsiteOpContAddress.getAddressLine1());
				this.tagValueMap.put("onSiteOperatorAddrLine2", onsiteOpContAddress.getAddressLine2());
				this.tagValueMap.put("onSiteOperatorCity", onsiteOpContAddress.getCityName());
				this.tagValueMap.put("onSiteOperatorState", onsiteOpContAddress.getState());

				String zip = onsiteOpContAddress.getZipCode5();
				if (null != zip && null != onsiteOpContAddress.getZipCode4()) {
					zip += "-" + onsiteOpContAddress.getZipCode4();
				}
				this.tagValueMap.put("onSiteOperatorZip", zip);
			}
		}

	}

	private void loadPermittingContactData(final Facility facility) {

		if (null != facility && null != facility.getPermittingContact()) {

			Contact permittingContact = facility.getPermittingContact();

			StringBuffer permittingName = new StringBuffer();

			String title = ContactTitle.getData().getItems().getItemDesc(permittingContact.getTitleCd());
			if (null != title) {
				permittingName.append(title + " ");
			}

			String fName = permittingContact.getFirstNm();
			if (null != fName) {
				permittingName.append(fName + " ");
			}

			String lName = permittingContact.getLastNm();
			if (null != lName) {
				permittingName.append(lName);
			}

			String suffixCd = permittingContact.getSuffixCd();
			if (null != suffixCd) {
				permittingName.append(" " + suffixCd);
			}
			this.tagValueMap.put("permittingName", permittingName.toString());

			this.tagValueMap.put("permittingCoName", permittingContact.getCompanyName());
			this.tagValueMap.put("permittingPhone", getFormattedPhoneNbr(permittingContact.getPhoneNo()));
			this.tagValueMap.put("permittingEmail", permittingContact.getEmailAddressTxt());
			this.tagValueMap.put("permittingEmailSecondary", permittingContact.getEmailAddressTxt2());

			Address permittingContAddress = permittingContact.getAddress();
			if (null != permittingContAddress) {

				this.tagValueMap.put("permittingAddrLine1", permittingContAddress.getAddressLine1());
				this.tagValueMap.put("permittingAddrLine2", permittingContAddress.getAddressLine2());
				this.tagValueMap.put("permittingCity", permittingContAddress.getCityName());
				this.tagValueMap.put("permittingState", permittingContAddress.getState());

				String zip = permittingContAddress.getZipCode5();
				if (null != zip && null != permittingContAddress.getZipCode4()) {
					zip += "-" + permittingContAddress.getZipCode4();
				}
				this.tagValueMap.put("permittingZip", zip);
			}
		}
	}

	private void loadResponsibleOfficialContactData(final Facility facility) {

		if (null != facility && null != facility.getRespOfficialContact()) {

			Contact respOfficialContact = facility.getRespOfficialContact();

			StringBuffer respOfficialName = new StringBuffer();

			String title = ContactTitle.getData().getItems().getItemDesc(respOfficialContact.getTitleCd());
			if (null != title) {
				respOfficialName.append(title + " ");
			}

			String fName = respOfficialContact.getFirstNm();
			if (null != fName) {
				respOfficialName.append(fName + " ");
			}

			String lName = respOfficialContact.getLastNm();
			if (null != lName) {
				respOfficialName.append(lName);
			}

			String suffixCd = respOfficialContact.getSuffixCd();
			if (null != suffixCd) {
				respOfficialName.append(" " + suffixCd);
			}
			this.tagValueMap.put("respOfficialName", respOfficialName.toString());

			this.tagValueMap.put("respOfficialCoName", respOfficialContact.getCompanyName());
			this.tagValueMap.put("respOfficialPhone", getFormattedPhoneNbr(respOfficialContact.getPhoneNo()));
			this.tagValueMap.put("respOfficialEmail", respOfficialContact.getEmailAddressTxt());
			this.tagValueMap.put("respOfficialEmailSecondary", respOfficialContact.getEmailAddressTxt2());

			Address respOfficialContAddress = respOfficialContact.getAddress();
			if (null != respOfficialContAddress) {

				this.tagValueMap.put("respOfficialAddrLine1", respOfficialContAddress.getAddressLine1());
				this.tagValueMap.put("respOfficialAddrLine2", respOfficialContAddress.getAddressLine2());
				this.tagValueMap.put("respOfficialCity", respOfficialContAddress.getCityName());
				this.tagValueMap.put("respOfficialState", respOfficialContAddress.getState());

				String zip = respOfficialContAddress.getZipCode5();
				if (null != zip && null != respOfficialContAddress.getZipCode4()) {
					zip += "-" + respOfficialContAddress.getZipCode4();
				}
				this.tagValueMap.put("respOfficialZip", zip);
			}
		}
	}

	private void loadFacilityDistrictData(final Facility facility) {

		if (null != facility) {

			DoLaaDef doLaaDef = (DoLaaDef) DoLaaDef.getData().getItem(facility.getDoLaaCd());

			if (null != doLaaDef) {

				this.tagValueMap.put("districtCd", doLaaDef.getDoLaaShortDsc());
				this.tagValueMap.put("district", doLaaDef.getDescription());
				this.tagValueMap.put("districtAddrLine1", doLaaDef.getAddressLine1());
				this.tagValueMap.put("districtAddrLine2", doLaaDef.getAddressLine2());
				this.tagValueMap.put("districtAddrLine3", doLaaDef.getAddressLine3());
				this.tagValueMap.put("districtPhone", getFormattedPhoneNbr(doLaaDef.getPhoneNumber()));
			}
		}
	}

	private void loadMiscData(final Facility facility) throws RemoteException {

		if (null != facility && null != facility.getPhyAddr()) {

			if (null != facility.getPhyAddr().getCountyCd()) {

				CountyDef cd = getInfrastructureService().retrieveCounty(facility.getPhyAddr().getCountyCd());

				if (cd != null) {

					this.tagValueMap.put("county", cd.getCountyNm());
					this.tagValueMap.put("ptiPtioDraftCC", cd.getPtioCCList());
					this.tagValueMap.put("titleVDraftCC", cd.getPtoCCList());
					this.tagValueMap.put("doLaaCC", cd.getDoLaaCCList());
				}
			}
		}

	}

	private String getFacilityRoleUser(final Facility facility, final String facilityRoleCd)
			throws RemoteException {

		String user = null;

		if (null != facility && null != facilityRoleCd && null != facility.getFacilityRoles()) {

			FacilityRole role = facility.getFacilityRoles().get(facilityRoleCd);

			if (null != role) {

				Integer userId = role.getUserId();
				
				if (null != userId) {
					
					user = retrieveUserName(userId);
				}
			}
		}

		return user;
	}

	private void loadFacilityRolesData(final Facility facility) throws RemoteException {

		this.tagValueMap.put("tvRoReviewEngineer",
				getFacilityRoleUser(facility, FacilityRoleDef.TV_RO_REVIEW_ENGINEER));
		this.tagValueMap.put("nsrPermitEngineer",
				getFacilityRoleUser(facility, FacilityRoleDef.NSR_PERMIT_ENGINEER));
		this.tagValueMap.put("tvPermitEngineer",
				getFacilityRoleUser(facility, FacilityRoleDef.TV_PERMIT_ENGINEER));
		this.tagValueMap.put("complianceReportReviewer",
				getFacilityRoleUser(facility, FacilityRoleDef.COMPLIANCE_REPORT_REVIEWER));
		this.tagValueMap.put("aqdAdministrator",
				getFacilityRoleUser(facility, FacilityRoleDef.AQD_ADMINISTRATOR));
		this.tagValueMap.put("nsrPublicationCoordinator",
				getFacilityRoleUser(facility, FacilityRoleDef.NSR_PUBLICATION_COORDINATOR));
		this.tagValueMap.put("emissionsInventoryReviewer",
				getFacilityRoleUser(facility, FacilityRoleDef.EMISSIONS_INVENTORY_REVIEWER));
		this.tagValueMap.put("undeliveredMailAdmin",
				getFacilityRoleUser(facility, FacilityRoleDef.UNDELIVERED_MAIL_ADMIN));
		this.tagValueMap.put("nsrAdminAssistant",
				getFacilityRoleUser(facility, FacilityRoleDef.NSR_ADMIN_ASSISTANT));
		this.tagValueMap.put("tvPublicationCoordinator",
				getFacilityRoleUser(facility, FacilityRoleDef.TV_PUBLICATION_COORDINATOR));
		this.tagValueMap.put("facilityProfileAdmin",
				getFacilityRoleUser(facility, FacilityRoleDef.FACILITY_PROFILE_ADMIN));
		this.tagValueMap.put("emissionsInventoryInvoicer",
				getFacilityRoleUser(facility, FacilityRoleDef.EMISSIONS_INVENTORY_INVOICER));
		this.tagValueMap.put("nsrPublicNoticeReviewer",
				getFacilityRoleUser(facility, FacilityRoleDef.NSR_PUBLIC_NOTICE_REVIEWER));
		this.tagValueMap.put("tvAdminAssistant",
				getFacilityRoleUser(facility, FacilityRoleDef.TV_ADMIN_ASSISTANT));
		this.tagValueMap.put("nsrPermitSupervisor",
				getFacilityRoleUser(facility, FacilityRoleDef.NSR_PERMIT_SUPERVISOR));
		this.tagValueMap.put("nsrPermitPeerReviewEngineer",
				getFacilityRoleUser(facility, FacilityRoleDef.NSR_PERMIT_PEER_REVIEW_ENGINEER));
		this.tagValueMap.put("wdeqDirector", getFacilityRoleUser(facility, FacilityRoleDef.WDEQ_DIRECTOR));
		this.tagValueMap.put("districtUndeliveredMailReviewer",
				getFacilityRoleUser(facility, FacilityRoleDef.DISTRICT_ENGINEER_UNDELIVERED_MAIL_REVIEWER));
		this.tagValueMap.put("tvPublicNoticeReviewer",
				getFacilityRoleUser(facility, FacilityRoleDef.TV_PUBLIC_NOTICE_REVIEWER));
		this.tagValueMap.put("enforcementActionReviewerCheyenne",
				getFacilityRoleUser(facility, FacilityRoleDef.ENFORCEMENT_REVIEWER_CHEYENNE));
		this.tagValueMap.put("enforcementActionReviewerDistrict",
				getFacilityRoleUser(facility, FacilityRoleDef.ENFORCEMENT_REVIEWER_DISTRICT));
		this.tagValueMap.put("tvPermitSupervisor",
				getFacilityRoleUser(facility, FacilityRoleDef.TV_PERMIT_SUPERVISOR));
		this.tagValueMap.put("tvPermitPeerReviewEngineer",
				getFacilityRoleUser(facility, FacilityRoleDef.TV_PERMIT_PEER_REVIEW_ENGINEER));

	}

	private void loadNAICSData(final Facility facility) {

		if (null != facility && null != facility.getNaicsCds()) {

			String naics = "";

			for (String code : facility.getNaicsCds()) {
				String desc = NAICSDef.getData().getItems().getItemDesc(code);
				if (null != desc && desc.length() > 0) {

					if (naics.length() == 0) {
						naics = new String(desc);
					} else {
						naics = naics + ", " + desc;
					}
				}

				this.tagValueMap.put("naics", naics);
			}
		}
	}

	private void loadTvCertRepDueDateData(final Facility facility) {

		if (null != facility && facility.getTvCertRepdueDate() != null) {

			if (facility.getTvCertRepdueDate().matches("\\d\\d\\d\\d")) {
				Integer month = Integer.valueOf(facility.getTvCertRepdueDate().substring(0, 2));
				String day = facility.getTvCertRepdueDate().substring(2);

				String tvccDueDate = new DateFormatSymbols().getMonths()[month - 1] + " " + day;
				this.tagValueMap.put("tvccDueDate", tvccDueDate);
			}
		}
	}

	private void loadOwnershipData(final Facility facility) {

		if (null != facility && null != facility.getOwner()) {

			FacilityOwner owner = facility.getOwner();

			if (null != owner.getCompany()) {

				Company company = owner.getCompany();

				this.tagValueMap.put("facilityCompanyName", company.getName());
				this.tagValueMap.put("facilityCompanyID", company.getCmpId());

				if (null != company.getAddress()) {

					Address address = company.getAddress();

					this.tagValueMap.put("facilityCompanyAddress1", address.getAddressLine1());
					this.tagValueMap.put("facilityCompanyAddress2", address.getAddressLine2());
					this.tagValueMap.put("facilityCompanyCity", address.getCityName());
					this.tagValueMap.put("facilityCompanyZip", address.getZipCode());
					this.tagValueMap.put("facilityCompanyState", address.getState());
				}
			}
		}
	}
	
	// emissions units
	private void loadEmissionsUnitsData(final Facility facility) {
		
		if (null != facility) {
			
			List<EmissionUnit> eus = facility.getEmissionUnits();

			if (null != eus) {
				
				Collections.sort(eus, new Comparator<EmissionUnit>() {
					@Override
					public int compare(EmissionUnit eu1, EmissionUnit eu2) {
						return eu1.getEpaEmuId().compareTo(eu2.getEpaEmuId());
					}
				});
				
				List<List<?>> table = new ArrayList<List<?>>();
				
				List<String> headerRow = new ArrayList<String>(Arrays.asList(
						"AQD ID",
						"AQD Description",
						"Company Equipment ID",
						"Company Equipment Description",
						"Emissions Unit Type",
						"Operating Status",
						"Completion of Initial Installation Date"
					)
				);

				table.add(headerRow);

				for (EmissionUnit eu : eus) {

					if (eu.getOperatingStatusCd().equals(EuOperatingStatusDef.IV)){
						continue;
					}
					
					List<Object> row = new ArrayList<Object>();

					row.add(eu.getEpaEmuId());

					row.add(eu.getEuDesc());

					row.add(eu.getCompanyId());

					row.add(eu.getRegulatedUserDsc());

					row.add(eu.getEmissionUnitTypeName());

					row.add(EuOperatingStatusDef.getData().getItems().getItemDesc(eu.getOperatingStatusCd()));

					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, eu.getEuInitInstallDate()));

					table.add(row);
				}

				this.tagValueMap.put("facilityEmissionUnitTable", table);
			}
			
		}
	}
	
	
	// control equipment units
	private void loadControlEquipmentData(final Integer fpId) throws RemoteException {

		if (null != fpId) {

			List<FacilityNode> ceList = getFacilityService().retrieveFacilityControlEquipment(fpId);

			if (null != ceList) {

				// sort the list according to control equipment id
				Collections.sort(ceList, new Comparator<FacilityNode>() {
					@Override
					public int compare(FacilityNode ce1, FacilityNode ce2) {
						if (ce1 instanceof ControlEquipment && ce2 instanceof ControlEquipment) {
							return ((ControlEquipment) ce1).getControlEquipmentId()
									.compareTo(((ControlEquipment) ce2).getControlEquipmentId());
						} else {
							return ce1.getFpNodeId().compareTo(ce2.getFpNodeId());
						}
					}
				});

				List<List<?>> table = new ArrayList<List<?>>();

				List<String> headerRow = new ArrayList<String>(Arrays.asList(
						"AQD ID",
						"AQD Description",
						"Company Equipment ID",
						"Company Equipment Description",
						"Control Equipment Type",
						"Operating Status",
						"Initial Installation Date"
					)
				);

				table.add(headerRow);

				for (FacilityNode node : ceList) {
					
					if (node instanceof ControlEquipment) {
						
						ControlEquipment ce = (ControlEquipment) node;

						List<Object> row = new ArrayList<Object>();
	
						row.add(ce.getControlEquipmentId());
	
						row.add(ce.getDapcDesc());
	
						row.add(ce.getCompanyId());
	
						row.add(ce.getRegUserDesc());
	
						row.add(ContEquipTypeDef.getData().getItems().getItemDesc(ce.getEquipmentTypeCd()));
	
						row.add(CeOperatingStatusDef.getData().getItems().getItemDesc(ce.getOperatingStatusCd()));
	
						row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, ce.getContEquipInstallDate()));
	
						table.add(row);
					}
				}

				this.tagValueMap.put("inspectionControlEquipmentList", table);
			}

		}
	}
	
	
	// release points
	private void loadReleasePointsData(final Integer fpId) throws RemoteException {

		if (null != fpId) {

			List<FacilityNode> egressPointList = getFacilityService().retrieveFacilityEgressPoints(fpId);

			if (null != egressPointList) {

				// sort the list according to egress point id
				Collections.sort(egressPointList, new Comparator<FacilityNode>() {
					@Override
					public int compare(FacilityNode egp1, FacilityNode egp2) {
						if (egp1 instanceof EgressPoint && egp2 instanceof EgressPoint) {
							return ((EgressPoint) egp1).getReleasePointId()
									.compareTo(((EgressPoint) egp2).getReleasePointId());
						} else {
							return egp1.getFpNodeId().compareTo(egp2.getFpNodeId());
						}
					}
				});

				List<List<?>> table = new ArrayList<List<?>>();

				List<String> headerRow = new ArrayList<String>(Arrays.asList(
						"AQD ID",
						"AQD Description",
						"Company Release Point ID",
						"Company Release Point Description",
						"Release Point Type",
						"Operating Status"
					)
				);

				table.add(headerRow);

				for (FacilityNode node : egressPointList) {
					
					if (node instanceof EgressPoint) {
						
						EgressPoint egp = (EgressPoint) node;
						
						List<Object> row = new ArrayList<Object>();
						
						row.add(egp.getReleasePointId());
						
						row.add(egp.getDapcDesc());
						
						row.add(egp.getEgressPointId());
						
						row.add(egp.getRegulatedUserDsc());
						
						row.add(EgrPointTypeDef.getData().getItems().getItemDesc(egp.getEgressPointTypeCd()));
						
						row.add(EgOperatingStatusDef.getData().getItems().getItemDesc(egp.getOperatingStatusCd()));
						
						table.add(row);
					}
				}

				this.tagValueMap.put("inspectionReleasePointList", table);
			}

		}
	}
	
	private void loadHeadingData() {
		this.tagValueMap.put("facilityEmissionUnitTableHeading", "EMISSION UNITS:");
		this.tagValueMap.put("inspectionControlEquipmentListHeading", "CONTROL EQUIPMENT:");
		this.tagValueMap.put("inspectionReleasePointListHeading", "RELEASE POINTS:");
	}
}
