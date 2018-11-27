package us.oh.state.epa.stars2.app.facility;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import oracle.adf.view.faces.component.UIXTable;
import oracle.adf.view.faces.context.AdfFacesContext;
import oracle.adf.view.faces.event.ReturnEvent;
import oracle.adf.view.faces.event.SelectionEvent;
import oracle.adf.view.faces.model.RowKeySet;
//import us.oh.state.epa.stars2.webcommon.facility.FacilityAddressUtil;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipment;
import us.oh.state.epa.stars2.database.dbObjects.facility.EgressPoint;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityNode;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRelationship;
import us.oh.state.epa.stars2.database.dbObjects.facility.PlssConversion;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountyDef;
import us.oh.state.epa.stars2.def.State;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.LocationCalculator;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.facility.FacilityProfileBase;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.CompanyService;

public class CloneFacility extends AppBase {

	private static final long serialVersionUID = -7977039522134746442L;

	private String facilityId;
    private Integer fpId;
    private String facilityName;
    private Integer ownerCompanyId;
    private Address phyAddr;
    private Facility facility;
    
    private final String outOfStateCountyCd = "24";
	private final String unknownDistrictCd = "D9";
    
   
	private String addressLine1;
	private String addressLine2;
	private String cityName;
	private String state;
	private String zipCode;
	private String countyCd;
	private String districtCd;
	private String countryCd;
	private Timestamp beginDate;
	private Timestamp endDate;
	private String latitude;
	private String longitude;
	private String latlong;
	private String quarterQuarter;
	private String quarter;
	private String section;
	private String township;
	private String range;
	
	// page table
	private TableSorter emusWrapper;
	private TableSorter cesWrapper;
	private TableSorter egpsWrapper;
	private boolean isIncludeChild;
	
	private List<ControlEquipment> controlEquips = new ArrayList<ControlEquipment>(
			0);
	private List<EgressPoint> egrPoints = new ArrayList<EgressPoint>(0);
	private List<EmissionUnit> emissionUnits = new ArrayList<EmissionUnit>(0);
	
	private boolean isEUsSelectedForCloning = false;
	
	private CompanyService companyService;
	
	private MyTasks myTasks;
	
	private FacilityProfileBase facilityProfile;
	
	private Facility targetFacility;
	
	public FacilityProfileBase getFacilityProfile() {
		return facilityProfile;
	}

	public void setFacilityProfile(FacilityProfileBase facilityProfile) {
		this.facilityProfile = facilityProfile;
	}

	public MyTasks getMyTasks() {
		return myTasks;
	}

	public void setMyTasks(MyTasks myTasks) {
		this.myTasks = myTasks;
	}

	public CompanyService getCompanyService() {
		return companyService;
	}

	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}
	
	public LinkedHashMap<String,String> getCompanyFacilities() {
		LinkedHashMap<String,String> companyFacilities = 
				new LinkedHashMap<String,String>();
		String loginName = getMyTasks().getLoginName();
		String cmpId = getMyTasks().getFacility().getOwner().getCompany().getCmpId();
		
		FacilityList[] facilities =
				getCompanyService().retrieveAuthorizedFacilities(cmpId, loginName);
        for (FacilityList f : facilities) {
        	companyFacilities.put(f.getFacilityId() + " - " + f.getName(), f.getFacilityId());
        }
		return companyFacilities;
	}
	
	public String selectAllEus() {
		if (facility.getEmissionUnits() != null) {
			for (EmissionUnit eu : facility.getEmissionUnits()) {
				eu.setSelected(true);
			}
		}
		return null;
	}

	public String selectNoneEus() {
		if (facility.getEmissionUnits() != null) {
			for (EmissionUnit eu : facility.getEmissionUnits()) {
				eu.setSelected(false);
			}
		}
		return null;
	}

	public void calculatePlss() { //this method is not being used 

		boolean hasError = false;

		if (showValidationMsg(latitude))
			hasError = true;

		if (showValidationMsg(longitude))
			hasError = true;

		if (hasError)
			return;

		//TODO getting bean from spring context
		LocationCalculator calculator = App.getApplicationContext().getBean(LocationCalculator.class);
		PlssConversion plss = calculator.calculatePlss(this.latitude,
				this.longitude);

		if (plss != null && !Utility.isNullOrEmpty(plss.getRange())) {
			this.setRange(plss.getRange());
			this.setTownship(plss.getTownship());
			this.setSection(plss.getSection());
			calculateCounty();
		}
	}
	public String refreshCloneFacility() {
		this.resetCloneFacility();
		
		return "facilities.cloneFacility";
	}
	private boolean showValidationMsg(String validatetionField) {
		List<ValidationMessage> tempMsgList = new ArrayList<ValidationMessage>();

		if (tempMsgList.size() > 0) {
			for (ValidationMessage temp : tempMsgList) {
				DisplayUtil.displayError("ERROR: " + temp.getMessage(),
						temp.getProperty());
			}
			return true; // has errors
		}
		return false; // no error
	}
	
	private void calculateCounty() { //this method is not being used 
		//TODO getting bean from spring context
		LocationCalculator calculator = App.getApplicationContext().getBean(LocationCalculator.class);
		CountyDef county = calculator.calculateCountyCd(this.latitude,
				this.longitude);
		String countyCd = county.getCountyCd();
		String district = county.getDoLaaCd();

		if (Utility.isNullOrEmpty(countyCd)) {
			countyCd = this.outOfStateCountyCd;
		}

		if (Utility.isNullOrEmpty(district)) {
			district = this.unknownDistrictCd;
		}

		this.setCountyCd(county.getCountyCd());
		this.setDistrictCd((county.getDoLaaCd()));

		if (countyCd != this.outOfStateCountyCd) {
			this.setState(State.DEFAULT_STATE);
		}
	}

	
	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCountyCd() {
		return countyCd;
	}

	public void setCountyCd(String countyCd) {
		this.countyCd = countyCd;
	}

	public String getDistrictCd() {
		return districtCd;
	}

	public void setDistrictCd(String districtCd) {
		this.districtCd = districtCd;
	}

	public String getCountryCd() {
		return countryCd;
	}

	public void setCountryCd(String countryCd) {
		this.countryCd = countryCd;
	}

	public Timestamp getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Timestamp beginDate) {
		this.beginDate = beginDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatlong() {
		return latlong;
	}

	public void setLatlong(String latlong) {
		this.latlong = latlong;
	}

	public String getQuarterQuarter() {
		return quarterQuarter;
	}

	public void setQuarterQuarter(String quarterQuarter) {
		this.quarterQuarter = quarterQuarter;
	}

	public String getQuarter() {
		return quarter;
	}

	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getTownship() {
		return township;
	}

	public void setTownship(String township) {
		this.township = township;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}
    

	private String popupRedirectOutcome;
    private FacilityService facilityService;

    public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public CloneFacility() {
        resetCloneFacility();
    }

	public Address getPhyAddr() {
		return phyAddr;
	}

	public void setPhyAddr(Address phyAddr) {
		this.phyAddr = phyAddr;
	}
		
    public final void  cancelResetCloneFacility() {
    	resetCloneFacility();
    	AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }
    
    public final String  resetCloneFacility() {
        facilityId = null;
        phyAddr = new Address();
        facilityName = null;
        ownerCompanyId=null;
        facility = new Facility();
        resetWrapper();
		clearSelectedEU();
		clearSelectedCE();
		clearSelectedEP();
		clearButtonClicked();
        this.isIncludeChild = true;
        phyAddr.setBeginDate(new Timestamp(System.currentTimeMillis()));
        return "facilities.cloneFacility";
    }
    
    public boolean getIsIncludeChild() {
		return this.isIncludeChild;
	}
    
    
    public void setIsIncludeChild(boolean isIncludeChild) {
		this.isIncludeChild = isIncludeChild;
		syncEmissionUnits();
		showCEEP();
	}
    
    private ValidationMessage[] validateCloneFacility() {
    	ValidationMessage[] ret = null;
    	List<ValidationMessage> valMsgs = new ArrayList<ValidationMessage>();

    	if (isInternalApp()) {
	    	// validate facility information
	    	if (Utility.isNullOrEmpty(facilityId)) {
	    		valMsgs.add(new ValidationMessage("facId", "Facility ID is required.",
	                    ValidationMessage.Severity.ERROR, null));
	    	}
	    	
	    	if (Utility.isNullOrEmpty(facilityName)) {
	    		valMsgs.add(new ValidationMessage("facName", "New facility Name is required.",
	                    ValidationMessage.Severity.ERROR, null));
	    	}  
	    	
	    	if (ownerCompanyId == null) {
	    		valMsgs.add(new ValidationMessage("companyName", "Company Name is required.",
	                    ValidationMessage.Severity.ERROR, null));
	    	}
	    	
	    	// validate location
//			phyAddr.validateAddress();
//	
//			phyAddr.getValidationMessages().remove("zipCode");
//			phyAddr.getValidationMessages().remove("cityName");
//			phyAddr.getValidationMessages().remove("addressLine1");
//			phyAddr.getValidationMessages().remove("districtCd");
//			
//			phyAddr.validZipCode();
//			phyAddr.validateLocationInfo();
	    	phyAddr.validateFacilityAddress();
			ValidationMessage[] addrVals = phyAddr.validate();
			if(addrVals != null && addrVals.length > 0) {
				valMsgs.addAll(new ArrayList<ValidationMessage>(Arrays.asList(addrVals))); 	
			}
    	}
    	
		ret = valMsgs.toArray(new ValidationMessage[0]);
		
    	return ret;
    }

   public final void submitCloneFacility(ActionEvent actionEvent) {
	   
		try {
			int currentUserId = InfrastructureDefs.getCurrentUserId();
			selectEP();

			phyAddr.setCountryCd("US");

			if (displayValidationMessages("cloneFacility",
					validateCloneFacility())) {
				return;
			}

			if (isInternalApp()) {
				Facility cloneFacility = getFacilityService().cloneFacility(facility,facilityName,
						ownerCompanyId, getPhyAddr(), currentUserId);

	        	// in case facilityProfile bean pointing at fpId with old facility ID or other facility or none
	        	facilityProfile.setFpId(cloneFacility.getFpId()); 
        	
	        	facilityProfile.submitProfile();
	        	popupRedirectOutcome = "facilityProfile";
	        	
	        	((us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem) FacesUtil.getManagedBean("menuItem_facProfile")).setDisabled(false);
			} else {
				getFacilityProfile().setStaging(true);
				cloneSelectedNodes();
			}
        	DisplayUtil.displayInfo("Facility cloned successfully.");
        	
        } catch (RemoteException re) {
        	handleException(re);
            DisplayUtil.displayError("Clone Facility failed");
			clearButtonClicked();
        }
   
		
        if(isEUsSelectedForCloning && isInternalApp())
        	FacesUtil.returnFromDialogAndRefresh();
        else
        	AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);

        clearButtonClicked();

    }
   
	private List<FacilityNode> cloneSelectedNodes() {
		List<FacilityNode> nodes = new ArrayList<FacilityNode>();
		List<EmissionUnit> eus = facility.getEmissionUnits();
		List<ControlEquipment> ces = facility.getControlEquips();
		List<EgressPoint> rps = facility.getEgrPoints();
		Map<Integer,Integer> srcToCloneNodeIdMap = new HashMap<Integer,Integer>();
		for (EmissionUnit eu : eus) {
			if (eu.isSelected()) {
				logger.debug(" ==> eu selected: " + eu.getEpaEmuId());
				
				getFacilityProfile().setEmissionUnit(eu);
				getFacilityProfile().setSelectedTreeNode(null);
				getFacilityProfile().cloneEmissionUnit(true);
				getFacilityProfile().saveEmissionUnit(false);
				
				
				List<EmissionProcess> eps = eu.getEmissionProcesses();
				for (EmissionProcess ep : eps) {
					if (ep.isSelected()) {
						logger.debug(" ====> ep selected: " + ep.getProcessId());
						logger.debug(" ======> ep fpNodeId: " + ep.getFpNodeId());

						Integer epFpNodeId = ep.getFpNodeId();
						getFacilityProfile().setSelectedTreeNode(null);
						getFacilityProfile().cloneEmissionProcess(ep);
						getFacilityProfile().saveEmissionProcess(false);
						FacilityRelationship[] frs = ep.getRelationships();
						ep = getFacilityProfile().getEmissionProcess();
						nodes.add(ep);
						srcToCloneNodeIdMap.put(epFpNodeId,ep.getFpNodeId());
						ep.setRelationships(frs);
						
					} else {
						logger.debug(" ====> ep not selected: " + ep.getProcessId());					
					}
				}
			} else {
				logger.debug(" ==> eu not selected: " + eu.getEpaEmuId());				
			}
		}
		for (ControlEquipment ce : ces) {
			if (ce.isSelected()) {
				logger.debug(" ======> ce selected: " + ce.getControlEquipmentId());
				
				Integer ceFpNodeId = ce.getFpNodeId();
				getFacilityProfile().setSelectedTreeNode(null);
				getFacilityProfile().setControlEquipment(ce);
				getFacilityProfile().cloneControlEquipment();
				getFacilityProfile().saveControlEquipment(false);
				nodes.add(getFacilityProfile().getControlEquipment());
				srcToCloneNodeIdMap.put(ceFpNodeId,getFacilityProfile().getControlEquipment().getFpNodeId());
				
				
				
			} else {
				logger.debug(" ======> ce not selected: " + ce.getControlEquipmentId());
			}
		}
		for (EgressPoint rp : rps) {
			if (rp.isSelected()) {
				logger.debug(" ======> rp selected: " + rp.getReleasePointId());										
				
				Integer rpFpNodeId = rp.getFpNodeId();
				getFacilityProfile().setSelectedTreeNode(null);
				getFacilityProfile().setEgressPoint(rp);
				getFacilityProfile().cloneEgressPoint();
				getFacilityProfile().saveEgressPoint(false);
				nodes.add(getFacilityProfile().getEgressPoint());
				srcToCloneNodeIdMap.put(rpFpNodeId,getFacilityProfile().getEgressPoint().getFpNodeId());
				
				
			} else {
				logger.debug(" ======> rp not selected: " + rp.getReleasePointId());										
			}
		}
		logger.debug("nodes.size() = " + nodes.size());
		
		for (FacilityNode node : nodes) {
			// TFS task: 5923
			// do not create relationship(s) if this is an egress point
			// as it causes primary key errors due to duplicates
			if(!(node instanceof EgressPoint)) {
				for (FacilityRelationship fr : node.getRelationships()) {
					try {
						getFacilityProfile().setSelectedTreeNode(null);
						getFacilityService().createRelationShip(
								srcToCloneNodeIdMap.get(fr.getFromNodeId()),
								srcToCloneNodeIdMap.get(fr.getToNodeId()), 
								fr.getFlowFactor(), 
								TransactionFactory.createTransaction());
					} catch (DAOException e) {
						handleException(e);
					}
				}
			}
		}
		return nodes;
	}
	
	public boolean isSubmitReady() {
		return hasSelectedEU();
	}

	public void returnFromDialog(ReturnEvent actionEvent) {
		resetCloneFacility();
		myTasks.setPageRedirect(myTasks.goFacilityDetailChange());
        FacesUtil.returnFromDialogAndRefresh();
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {	
		this.facilityId = facilityId;
	}
	
	public Integer getOwnerCompanyId() {
		return ownerCompanyId;
	}

	public void setOwnerCompanyId(Integer ownerCompanyId) {
		this.ownerCompanyId = ownerCompanyId;
	}
	
	public String confirm() {
		if (displayValidationMessages("cloneFacility:", validateCloneFacility())) {
			return null;
		}
    	
    	String format = "F%06d";

		int tempId;
		try {
			tempId = Integer.parseInt(facilityId);
			facilityId = String.format(format, tempId);
		} catch (NumberFormatException nfe) {
		}
		
    	try {
        	if (facility == null) {
        		DisplayUtil.displayError("Clone Facility failed; Facility: [" + facilityId + "] does not exist.");
        		return null;
        	}
        } catch (Exception re) {
            DisplayUtil.displayError("Clone Facility failed");
            return null;
        }
		
        return "dialog:cloneFacility";
    }
	
	public void noConfirm(ActionEvent actionEvent) {
		popupRedirectOutcome = null;
		clearButtonClicked();
		FacesUtil.returnFromDialogAndRefresh();
    }

	public String getPopupRedirect() {
		if (popupRedirectOutcome != null) {
            FacesUtil.setOutcome(null, popupRedirectOutcome);
            popupRedirectOutcome = null;
        }
        return null;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}
	
	public final TableSorter getEmusWrapper() {
		return this.emusWrapper;
	}

	public final TableSorter getCesWrapper() {
		return this.cesWrapper;
	}

	public final TableSorter getEgpsWrapper() {
		return this.egpsWrapper;
	}
	
	private void resetWrapper() {
		if (emusWrapper != null) {
			if (emusWrapper.getTable() != null) {
				if (emusWrapper.getTable().getSelectionState() != null) {
					emusWrapper.getTable().getSelectionState().getKeySet().clear();
				}
			}
		}
		
		emusWrapper = new TableSorter();
		cesWrapper = new TableSorter();
		egpsWrapper = new TableSorter();
	}
	 
	public final void loadEu(ValueChangeEvent valueChangeEvent) {

		setFacilityId(valueChangeEvent.getNewValue().toString());

		resetWrapper();

		if (this.facilityId == null || this.facilityId.equals("")) {
			DisplayUtil.displayError("Source Facility ID is required.");
			return;
		}

		try {
			if (!Utility.isNullOrEmpty(facilityId)) {
				String format = "F%06d";

				int tempId;
				try {
					tempId = Integer.parseInt(facilityId);
					setFacilityId(String.format(format, tempId));
				} catch (NumberFormatException nfe) {
				}
			}
			this.facility = getFacilityService().retrieveFacility(
					facilityId);

			if (this.facility == null) {
				DisplayUtil
						.displayError("Source Facility ID failed; Facility: ["
								+ facilityId + "] does not exist.");
				return;
			}

			this.fpId = this.facility.getFpId();
			this.facility = getFacilityService().retrieveFacilityProfile(
					fpId);

			if (this.facility.getEmissionUnits().isEmpty()) {
				DisplayUtil
						.displayWarning("Source facility "
								+ facilityId + " has no Emission Units.");
			}

			if (!isInternalApp()) {
				targetFacility = getFacilityService().retrieveFacilityProfile(
						getMyTasks().getFacility().getFpId());
			}
			
			setEpCeEpaEmuIds();
//			this.emissionUnits = filterEus(this.facility.getEmissionUnits(),targetFacility);
			this.emissionUnits = this.facility.getEmissionUnits();
			this.emusWrapper.setWrappedData(this.emissionUnits);
//			this.controlEquips = filterCes(this.facility.getControlEquips(),targetFacility);
//			this.egrPoints = filterEps(this.facility.getEgrPoints(),targetFacility);
			this.controlEquips = this.facility.getControlEquips();
			this.egrPoints = this.facility.getEgrPoints();

		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Load Facility failed");
			return;
		}

	}
	
	private List<EgressPoint> filterEps(List<EgressPoint> egressPoints, 
			Facility targetFacility) {
		List<EgressPoint> eps = egressPoints;
		if (!isInternalApp()) {
			eps = new ArrayList<EgressPoint>();
			for (EgressPoint ep : egressPoints) {
				boolean existsInTarget = false;
				boolean valid = false;
				for (EgressPoint targetEp : targetFacility.getEgressPoints()) {
					if (targetEp.getReleasePointId().equals(ep.getReleasePointId())) {
						existsInTarget = true;
						DisplayUtil.displayInfo(ep.getReleasePointId() + " cannot be cloned, because there is a release point with the same AQD ID in the current facility.");
					}
				}
				if (!existsInTarget) {
					eps.add(ep);
				}
			}
		}
		return eps;
	}
	
	private List<ControlEquipment> filterCes(List<ControlEquipment> controlEquipment, 
			Facility targetFacility) {
		List<ControlEquipment> ces = controlEquipment;
		if (!isInternalApp()) {
			ces = new ArrayList<ControlEquipment>();
			for (ControlEquipment ce : controlEquipment) {
				boolean existsInTarget = false;
				for (ControlEquipment targetCe : targetFacility.getControlEquips()) {
					if (targetCe.getControlEquipmentId().equals(ce.getControlEquipmentId())) {
						existsInTarget = true;
						DisplayUtil.displayInfo(ce.getControlEquipmentId() + " cannot be cloned, because there is a control device with the same AQD ID in the current facility.");
					}
				}
				if (!existsInTarget) {
					ces.add(ce);
				}
			}
		}
		return ces;
	}

	private List<EmissionUnit> filterEus(List<EmissionUnit> emissionUnits, 
			Facility targetFacility) throws DAOException, RemoteException {
		List<EmissionUnit> eus = emissionUnits;
		if (!isInternalApp()) {
			eus = new ArrayList<EmissionUnit>();
			for (EmissionUnit eu : emissionUnits) {
				boolean existsInTarget = false;
				for (EmissionUnit targetEu : targetFacility.getEmissionUnits()) {
					if (targetEu.getEpaEmuId().equals(eu.getEpaEmuId())) {
						existsInTarget = true;
						DisplayUtil.displayInfo(eu.getEpaEmuId() + " cannot be cloned, because there is an emission unit with the same AQD ID in the current facility.");
					}
				}
				if (!existsInTarget) {
						eus.add(eu);
				}
			}
		}
		return eus;
	}
	
	private void setEpCeEpaEmuIds() {
		EmissionUnit[] emissionUnits = this.facility.getEmissionUnits()
				.toArray(new EmissionUnit[0]);

		for (EmissionUnit tempEU : emissionUnits) {
			EmissionProcess[] emissionProcesses = tempEU.getEmissionProcesses()
					.toArray(new EmissionProcess[0]);
			for (EmissionProcess tempEP : emissionProcesses) {
				ControlEquipment[] controlEquips = tempEP
						.getControlEquipments()
						.toArray(new ControlEquipment[0]);
				EgressPoint[] egressPoints = tempEP.getEgressPoints().toArray(
						new EgressPoint[0]);

				for (ControlEquipment tempCE : controlEquips) {
					addEpaEmuIdToContEquip(tempCE, tempEU.getEpaEmuId());
				}

				for (EgressPoint tempEGP : egressPoints) {
					tempEGP.setAssociatedEpaEuIds(tempEU.getEpaEmuId());
				}
			}
		}
	}
	
	private void addEpaEmuIdToContEquip(ControlEquipment tempCE, String epaEuId) {
		tempCE.setAssociatedEpaEuIds(epaEuId);

		ControlEquipment[] ceControlEquips = tempCE.getControlEquips().toArray(
				new ControlEquipment[0]);
		EgressPoint[] ceEgressPoints = tempCE.getEgressPoints().toArray(
				new EgressPoint[0]);

		for (ControlEquipment tempCeCE : ceControlEquips) {
			addEpaEmuIdToContEquip(tempCeCE, epaEuId);
		}

		for (EgressPoint tempEGP : ceEgressPoints) {
			tempEGP.setAssociatedEpaEuIds(epaEuId);
		}
	}
	
	public void selectionEu(SelectionEvent action) {
		syncEmissionUnits();
		showCEEP();
	}
	
	private void showCEEP() {
		if (!this.isIncludeChild) {
			this.cesWrapper.clearWrappedData();
			this.egpsWrapper.clearWrappedData();
			return;
		} else {
			this.cesWrapper.setWrappedData(selectedCE());
			this.egpsWrapper.setWrappedData(selectedEP());
		}

	}

	private void syncEmissionUnits() {

		clearSelectedEU();
		clearSelectedCE();
		clearSelectedEP();

		try {
			UIXTable table = this.emusWrapper.getTable();
			@SuppressWarnings("unchecked")
			Iterator<RowKeySet> selection = table.getSelectionState()
					.getKeySet().iterator();
			while (selection.hasNext()) {
				table.setRowKey(selection.next());
				EmissionUnit row = (EmissionUnit) table.getRowData();
				row.setSelected(true);
				selectEU(row.getEpaEmuId());
				if (isIncludeChild) {
					selectCE(row.getEpaEmuId());
					selectEP(row.getEpaEmuId());
				}
			}
		} catch (Exception re) {
			DisplayUtil
					.displayError("Load Control Equipments and Release Points failed.");
			return;
		}

	}
	
	// emissionUnits
	private boolean hasSelectedEU() {
		if (this.emissionUnits != null) {
			for (EmissionUnit eu : this.emissionUnits) {
				if (eu.isSelected())
					return true;
			}
		}
		return false;
	}

	private EmissionUnit[] selectedEU() {
		List<EmissionUnit> selectedEU = new ArrayList<EmissionUnit>(0);
		for (EmissionUnit temp : this.emissionUnits) {
			if (temp.isSelected())
				selectedEU.add(temp);
		}
		return selectedEU.toArray(new EmissionUnit[0]);
	}

	private void selectEU(String epaEmuId) {
		if (emissionUnits != null) {
			for (EmissionUnit eu : this.emissionUnits) {
				if (eu.getEpaEmuId().equals(epaEmuId))
					eu.setSelected(true);
			}
		}
	}

	private void clearSelectedEU() {
		if (emissionUnits != null) {
			for (EmissionUnit eu : this.emissionUnits) {
				eu.setSelected(false);
			}
		}
	}

	// EmissionProcess
	private void selectEP() {
		for (EmissionUnit eu : selectedEU()) {
			for (EmissionProcess ep : eu.getEmissionProcesses()) {
				ep.setSelected(true);
			}
		}
	}

	// ControlEquips
	private ControlEquipment[] selectedCE() {
		List<ControlEquipment> selectedCE = new ArrayList<ControlEquipment>(0);
		for (ControlEquipment temp : this.controlEquips) {
			if (temp.isSelected())
				selectedCE.add(temp);
		}
		return selectedCE.toArray(new ControlEquipment[0]);
	}

	private void selectCE(String epaEmuId) {
		for (ControlEquipment temp : this.controlEquips) {
			if (temp.getAssociatedEpaEuIds() != null
					&& temp.getAssociatedEpaEuIds().contains(epaEmuId))
				temp.setSelected(true);
		}
	}

	private void clearSelectedCE() {
		if (this.controlEquips != null) {
			for (ControlEquipment temp : this.controlEquips) {
				temp.setSelected(false);
			}
		}
	}

	// egrPoints
	private EgressPoint[] selectedEP() {
		List<EgressPoint> selectedEP = new ArrayList<EgressPoint>(0);
		for (EgressPoint temp : this.egrPoints) {
			if (temp.isSelected())
				selectedEP.add(temp);
		}
		return selectedEP.toArray(new EgressPoint[0]);
	}

	private void selectEP(String epaEmuId) {
		for (EgressPoint temp : this.egrPoints) {
			if (temp.getAssociatedEpaEuIds() != null
					&& temp.getAssociatedEpaEuIds().contains(epaEmuId))
				temp.setSelected(true);
		}
	}

	private void clearSelectedEP() {
		if (controlEquips != null) {
			for (EgressPoint temp : this.egrPoints) {
				temp.setSelected(false);
			}
		}
	}

	public String checkEUsSelectedBeforeCloning() {
		
		 // Protect from multiple clicks.
		if (!firstButtonClick()) {
			return null;
		}
		
		isEUsSelectedForCloning = false;
		
		if(emissionUnits.size() != 0) { 
			for(EmissionUnit eu : emissionUnits) {
				if(eu.isSelected()) {
					isEUsSelectedForCloning = true;
					break;
				}
			}
		} else {
			// there are no emissions units in the source facility to select
			// so we don't want the warning pop-up to be displayed
			isEUsSelectedForCloning = true;
		}

        if (isEUsSelectedForCloning)
        	return "dialog:confirmCloneFacility";
        else
        	return "dialog:confirmCloneFacilityWithoutEUs";
    }
	
	public void returnFirstSelected(ReturnEvent actionEvent) {
		FacesUtil.returnFromDialogAndRefresh();
	}
	

}
