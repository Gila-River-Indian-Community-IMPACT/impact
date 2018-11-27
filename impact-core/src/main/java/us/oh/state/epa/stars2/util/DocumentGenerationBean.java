package us.oh.state.epa.stars2.util;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.convert.ConverterException;

import com.aspose.words.ControlChar;

import us.oh.state.epa.common.util.Logger;
import us.oh.state.epa.stars2.bo.ApplicationService;
import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.DelegationRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.PBRNotification;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.RPERequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RPRRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplication;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionTotal;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsTable;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.genericIssuance.GenericIssuance;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountyDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.database.dbObjects.invoice.Invoice;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceDetail;
import us.oh.state.epa.stars2.database.dbObjects.permit.PTIOPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitCC;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEU;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitIssuance;
import us.oh.state.epa.stars2.database.dbObjects.permit.TVPermit;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.CerrClassDef;
import us.oh.state.epa.stars2.def.ContactTitle;
import us.oh.state.epa.stars2.def.ContactTypeDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.EmissionReportsDef;
import us.oh.state.epa.stars2.def.EmissionReportsRealDef;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.FacilityRoleDef;
import us.oh.state.epa.stars2.def.FacilityTypeDef;
import us.oh.state.epa.stars2.def.NAICSDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PBRTypeDef;
import us.oh.state.epa.stars2.def.PermitActionTypeDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PermitReceivedCommentsDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.PortableGroupTypes;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.ContactUtil;
import us.oh.state.epa.stars2.webcommon.converter.PhoneNumberConverter;
import us.oh.state.epa.stars2.webcommon.reports.NtvReport;
import us.oh.state.epa.stars2.webcommon.reports.ReportProfileBase;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.CompanyService;
import us.wy.state.deq.impact.database.dbObjects.company.Company;
import us.wy.state.deq.impact.def.NewspaperDef;


public class DocumentGenerationBean extends AppBase {

	private static final long serialVersionUID = -3288702610819438293L;

	private Map<String, String> properties;
    private Map<String, List<DocumentGenerationBean>> childCollections;
//    private Permit extPermit;
    PhoneNumberConverter phoneConvert = new PhoneNumberConverter();
    private EmissionsReportService emissionsReportService;
	//private FacilityService facilityService;
	private ApplicationService applicationService;
	//private CompanyService companyService;

	public ApplicationService getApplicationService() {
		return applicationService;
	}

	public void setApplicationService(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	private InfrastructureService infrastructureService = App.getApplicationContext().getBean(InfrastructureService.class);
	private FacilityService facilityService = App.getApplicationContext().getBean(FacilityService.class);
	private CompanyService companyService = App.getApplicationContext().getBean(CompanyService.class);

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}

	public void setEmissionsReportService(
			EmissionsReportService emissionsReportService) {
		this.emissionsReportService = emissionsReportService;
	}
	
	public CompanyService getCompanyService(){
		return companyService;
	}

	public void setCompanyService(CompanyService companyService){
		this.companyService =  companyService;
	}
	
    public DocumentGenerationBean()
    {
        properties = new HashMap<String, String>();
        childCollections =
            new HashMap<String, List<DocumentGenerationBean>>();

        String todaysDate = "";
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        Calendar cal = Calendar.getInstance();
        String yr = Integer.toString(cal.get(Calendar.YEAR));
        getProperties().put("currentYear", yr);
        //todaysDate = df.format(cal.getTime());
        todaysDate = DateFormat.getDateInstance(
                DateFormat.LONG).format(new Date());
        getProperties().put("todaysDate", todaysDate);
        
        

        getProperties().put("correspondenceDate", " ");
        getProperties().put("reportStartDate", " ");
        getProperties().put("reportEndDate", " ");
    }

    public Map<String, List<DocumentGenerationBean>> getChildCollections()
    {
        return childCollections;
    }

    public void setChildCollections(Map<String, List<DocumentGenerationBean>> childCollections)
    {
        this.childCollections = childCollections;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, String> properties)
    {
        this.properties = properties;
    }

    public void setCorrespondenceDate(String date) {

        String corrDate = " ";
        if (date != null && date.length() > 0) {
            corrDate = date;
        }
        getProperties().put("correspondenceDate", corrDate);

    }

    public void setFacility(Facility facility) 
        throws DocumentGenerationException {

        String facilityID = "";
        String facilityName = "";
        String facilityDescription = "";
        String facilityOperatingStatus = "";
        String facilityClass = "";
        String facilityType = "";
        String facilityAfs = "";
        String facilityCerrClass = "";
        String facilityInspectionClass = "";
        
        
        String facilityAddrLine1 = "";
        String facilityAddrLine2 = "";
        //String facilityAddrLine3 = " ";
        String facilityCity = "";
        String facilityState = "";
        String facilityZip = "";
        String facilityLat = "";
        String facilityLong = "";
        String facilityPlss = "";
        String locationEffectiveDate = "";
        String facilityCounty ="";

        //String contactName = " ";
        //String contactCoName = " ";
        //String contactAddrLine1 = " ";
        //String contactAddrLine2 = "";
        //String contactAddrLine3 = " ";
        //String contactZip5 = " ";
        //String contactPhone = " ";
        //String contactEmail = " ";

        String billingName = "";
        String billingCoName = "";
        String billingAddrLine1 = "";
        String billingAddrLine2 = "";
        String billingCity = "";
        String billingState = "";
        String billingZip = "";
        String billingPhone = "";
        String billingEmail = "";
        
        String complianceName = "";
        String complianceCoName = "";
        String complianceContactTitle= "";
        String complianceContactTitleDesc="";
        String complianceContactJobTitle = "";
        String complianceContactLName="";
        String complianceContactFName="";
        String complianceAddrLine1 = "";
        String complianceAddrLine2 = "";
        String complianceCity = "";
        String complianceState = "";
        String complianceZip = "";
        String compliancePhone = "";
        String complianceEmail = "";
        
        String emissionsName = "";
        String emissionsCoName = "";
        String emissionsAddrLine1 = "";
        String emissionsAddrLine2 = "";
        String emissionsCity = "";
        String emissionsState = "";
        String emissionsZip = "";
        String emissionsPhone = "";
        String emissionsEmail = "";
        
        String environmentalName = "";
        String environmentalCoName = "";
        String environmentalJobTitle = "";
        String environmentalTitle = "";
        String environmentalAddrLine1 = "";
        String environmentalAddrLine2 = "";
        String environmentalCity = "";
        String environmentalState = "";
        String environmentalZip = "";
        String environmentalPhone = "";
        String environmentalEmail = "";
        
        String monitoringName = "";
        String monitoringCoName = "";
        String monitoringAddrLine1 = "";
        String monitoringAddrLine2 = "";
        String monitoringCity = "";
        String monitoringState = "";
        String monitoringZip = "";
        String monitoringPhone = "";
        String monitoringEmail = "";
        
        String onSiteOperatorName = "";
        String onSiteOperatorCoName = "";
        String onSiteOperatorAddrLine1 = "";
        String onSiteOperatorAddrLine2 = "";
        String onSiteOperatorCity = "";
        String onSiteOperatorState = "";
        String onSiteOperatorZip = "";
        String onSiteOperatorPhone = "";
        String onSiteOperatorEmail = "";
        
        String permittingName = "";
        String permittingCoName = "";
        String permittingAddrLine1 = "";
        String permittingAddrLine2 = "";
        String permittingCity = "";
        String permittingState = "";
        String permittingZip = "";
        String permittingPhone = "";
        String permittingEmail = "";
        
        String respOfficialName = "";
        String respOfficialCoName = "";
        String respOfficialAddrLine1 = "";
        String respOfficialAddrLine2 = "";
        String respOfficialCity = "";
        String respOfficialState = "";
        String respOfficialZip = "";
        String respOfficialPhone = "";
        String respOfficialEmail = "";
        
        //String ownerName = " ";
        //String ownerCoName = " ";
        //String ownerAddrLine1 = " ";
        //String ownerAddrLine2 = "";
        //String ownerAddrLine3 = " ";
        //String ownerZip5 = " ";
        //String ownerPhone = " ";
        //String ownerEmail = " ";

        String countyCd = null;
        String county = "";
        String districtCd = "";  // what is returned here is actually the short description.
        String district = "";
        String districtAddrLine1 = "";
        String districtAddrLine2 = "";
        String districtAddrLine3 = "";
        String districtPhone = "";
        
        
        String ptiPtioDraftCC = "";
        String titleVDraftCC = "";
        String doLaaCC = "";
        
        
        String tvRoReviewEngineer = "";
        String nsrPermitEngineer = "";
        String tvPermitEngineer = "";
        String complianceReportReviewer = "";
        String aqdAdministrator = "";
        String nsrPublicationCoordinator = "";
        String emissionsInventoryReviewer = "";
        String undeliveredMailAdmin = "";
        String nsrAdminAssistant = "";
        String tvPublicationCoordinator = "";
        String facilityProfileAdmin = "";
        String emissionsInventoryInvoicer = "";
        String nsrPublicNoticeReviewer = "";
        String tvAdminAssistant = "";
        String nsrPermitSupervisor = "";
        String nsrPermitPeerReviewEngineer = "";
        String wdeqDirector = "";
        String districtUndeliveredMailReviewer = "";
        String tvPublicNoticeReviewer = "";
        String enforcementActionReviewerCheyenne = "";
        String enforcementActionReviewerDistrict = "";
        String tvPermitSupervisor = "";
        String tvPermitPeerReviewEngineer = "";


        String naics = "";
//        String perDueDate = " ";
        String tvccDueDate = "";
        String portableType = "";
        
        String facilityCompanyName ="";
        String facilityCompanyID ="";
        String facilityCompanyAddress1 ="";
        String facilityCompanyAddress2 ="";
        String facilityCompanyCity ="";
        String facilityCompanyState="";
        String facilityCompanyZip ="";
        
        
        String quarterQuarterVal="";
        String quarterVal="";
        String sectionVal="";
        String townShipVal="";
        String rangeVal="";
        String countySeat="";
        

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

        try {

            if (facility != null) {
            	
            	if (facility.getFacilityId() != null
                    && facility.getFacilityId().length() > 0) {
                    facilityID = facility.getFacilityId();
                }
            	
            	
                if(facility.getOwner() !=null){
                	facilityCompanyName=facility.getOwner().getCompany().getName();  
                	facilityCompanyID = facility.getOwner().getCompany().getCmpId();
                	Logger.fine("facility.getOwner().getCompany().getCompanyId(): "+facility.getOwner().getCompany().getCompanyId());              	
                	Logger.fine("facility.getOwner().getCompany().getAddress().getAddressId(): "+facility.getOwner().getCompany().getAddress().getAddressId());  
                	Logger.fine("facility.getOwner().getCompany().getAddress().getAddressLine1():"+facility.getOwner().getCompany().getAddress().getAddressLine1());
                	Company comp =null;
                	if(companyService!=null)
                	comp = companyService.retrieveCompanyProfile(facility.getOwner().getCompany().getCompanyId());
                	if(comp!=null){
	                    facilityCompanyAddress1 = comp.getAddress().getAddressLine1();	                  
	                    facilityCompanyAddress2 = comp.getAddress().getAddressLine2();
	                    facilityCompanyCity = comp.getAddress().getCityName();
	                    facilityCompanyState = comp.getAddress().getState();
	                    facilityCompanyZip = comp.getAddress().getZipCode();  
                	}
                }

                if (facility.getName() != null
                    && facility.getName().length() > 0) {
                    facilityName = facility.getName();
                }
                
                if (facility.getDesc() != null
                    && facility.getDesc().length() > 0) {
                    facilityDescription = facility.getDesc();
                }
                
                
				if (facility.getOperatingStatusCd() != null
						&& facility.getOperatingStatusCd().length() > 0) {
					facilityOperatingStatus = OperatingStatusDef.getDemData()
							.getItems()
							.getItemDesc(facility.getOperatingStatusCd());
				}
				
				if (facility.getPermitClassCd() != null
						&& facility.getPermitClassCd().length() > 0) {
					facilityClass = PermitClassDef.getData().getItems()
							.getItemDesc(facility.getPermitClassCd());
				}

				if (facility.getFacilityTypeCd() != null
						&& facility.getFacilityTypeCd().length() > 0) {
					facilityType = FacilityTypeDef.getTextData().getItems()
							.getItemDesc(facility.getFacilityTypeCd());
				}
				
				if (facility.getAfs() != null
						&& facility.getAfs().length() > 0) {
					facilityAfs = facility.getAfs();
				}
				
				if (facility.getCerrClassCd() != null
						&& facility.getCerrClassCd().length() > 0) {
					facilityCerrClass = CerrClassDef.getData().getItems()
							.getItemDesc(facility.getCerrClassCd());
				}
				
                Address facilityAddr = facility.getPhyAddr();
                if (facilityAddr != null) {
                    if (facilityAddr.getAddressLine1() != null
                        && facilityAddr.getAddressLine1().length() > 0) {
                        facilityAddrLine1 = facilityAddr.getAddressLine1();                    
                    }

                    if (facilityAddr.getAddressLine2() != null
                        && facilityAddr.getAddressLine2().length() > 0) {
                        facilityAddrLine2 = facilityAddr.getAddressLine2();                        
                    }
                    
                    if(facilityAddr.getLatitude() != null){
                    	facilityLat = facility.getPhyAddr().getLatitude();
                    }
                    
                    if(facilityAddr.getLongitude() != null){
                    	facilityLong = facility.getPhyAddr().getLongitude();
                    }
                    
                    if(facility.getPhyAddr().getPlss() != null){
                    	facilityPlss = facility.getPhyAddr().getPlss();
                    }
                    
                    if(facility.getPhyAddr().getCountyCd() != null){
                    	countyCd = facility.getPhyAddr().getCountyCd();
                    	CountyDef cd = getInfrastructureService().retrieveCounty(countyCd);
                        if (cd != null) {
                            if (cd.getCountyNm() != null
                                && cd.getCountyNm().length() > 0) {
                                facilityCounty = cd.getCountyNm();                                
                            }
                        }
                    }
                    
                    if(facility.getPhyAddr().getBeginDate() != null){
                    	locationEffectiveDate = df.format(facility.getPhyAddr().getBeginDate());
                    }
                    
                    if (facilityAddr.getCityName() != null
                        && facilityAddr.getCityName().length() > 0) {
                        facilityCity = facilityAddr.getCityName();
                    }
                    if (facilityAddr.getState() != null
                        && facilityAddr.getState().length() > 0) {
                        facilityState = facilityAddr.getState();
                    }
                    if (facilityAddr.getZipCode5() != null
                        && facilityAddr.getZipCode5().length() > 0) {
                        facilityZip = facilityAddr.getZipCode5();
                    }
                    if (facilityAddr.getZipCode4() != null
                        && facilityAddr.getZipCode4().length() > 0) {
                        facilityZip = facilityZip + "-"
                            + facilityAddr.getZipCode4();
                    }
                    
                    if (facilityAddr.getCountyCd() != null
                        && facilityAddr.getCountyCd().length() > 0) {
                        countyCd = facilityAddr.getCountyCd();                        
                    }
                    
                    if (facilityAddr.getQuarterQuarter() != null
                            && facilityAddr.getQuarterQuarter().length() > 0) {
                            quarterQuarterVal = facilityAddr.getQuarterQuarter();
                    }
                    
                    if (facilityAddr.getQuarter() != null
                            && facilityAddr.getQuarter().length() > 0) {
                            quarterVal = facilityAddr.getQuarter();
                    }
                    if (facilityAddr.getSection() != null
                            && facilityAddr.getSection().length() > 0) {
                            sectionVal = facilityAddr.getSection();
                    }
                    if (facilityAddr.getTownship() != null
                            && facilityAddr.getTownship().length() > 0) {
                            townShipVal = facilityAddr.getTownship();
                    }
                    if (facilityAddr.getRange() != null
                            && facilityAddr.getRange().length() > 0) {
                            rangeVal = facilityAddr.getRange();
                    }
                    
                }
/*
                Contact primaryContact = facility.getPrimaryContact();
              
                
                if (primaryContact != null) {
                    Address contactAddr = primaryContact.getAddress();
                    if (contactAddr != null) {
                        if (contactAddr.getAddressLine1() != null
                            && contactAddr.getAddressLine1().length() > 0) {
                            contactAddrLine1 = contactAddr.getAddressLine1();
                        }
                        if (contactAddr.getAddressLine2() != null
                            && contactAddr.getAddressLine2().length() > 0) {
                            contactAddrLine2 = contactAddr.getAddressLine2();
                        }
                        if (contactAddr.getCityName() != null
                            && contactAddr.getCityName().length() > 0) {
                            contactAddrLine3 = contactAddr.getCityName();
                        }
                        if (contactAddr.getState() != null
                            && contactAddr.getState().length() > 0) {
                            contactAddrLine3 = contactAddrLine3 + ", "
                                + contactAddr.getState();
                        }
                        if (contactAddr.getZipCode5() != null
                            && contactAddr.getZipCode5().length() > 0) {
                            contactAddrLine3 = contactAddrLine3 + " "
                                + contactAddr.getZipCode5();
                            contactZip5 = contactAddr.getZipCode5();
                        }
                        if (contactAddr.getZipCode4() != null
                            && contactAddr.getZipCode4().length() > 0) {
                            contactAddrLine3 = contactAddrLine3 + "-"
                                + contactAddr.getZipCode4();
                        }

                        StringBuffer cName = new StringBuffer();
                        if (primaryContact.getTitleCd() != null
                            && primaryContact.getTitleCd().length() > 0) {
                            DefData ddata = ContactTitle.getData();
                            String titleDsc = ddata.getItems().getItemDesc(primaryContact.getTitleCd());
                            cName.append(titleDsc
                                         + " ");
                        }
                        if (primaryContact.getFirstNm() != null
                            && primaryContact.getFirstNm().length() > 0) {
                            cName.append(primaryContact.getFirstNm() + " ");
                        }
                        if (primaryContact.getFirstNm() != null
                            && primaryContact.getFirstNm().length() > 0) {
                            cName.append(primaryContact.getLastNm());
                        }
                        if (primaryContact.getSuffixCd() != null) {
                            cName.append(", " + primaryContact.getSuffixCd());
                        }
                        if (cName.length() > 0) {
                            contactName = cName.toString();
                        }
                        if(primaryContact.getCompanyName() != null
                                && primaryContact.getCompanyName().trim().length() > 0) {   
                            contactCoName = primaryContact.getCompanyName();
                        }
                        if (primaryContact.getPhoneNo() != null
                            && primaryContact.getPhoneNo().length() > 0) {
                            contactPhone = phoneConvert.tryFormatPhoneNumber(primaryContact.getPhoneNo());
                        }
                        if (primaryContact.getEmailAddressTxt() != null
                            && primaryContact.getEmailAddressTxt().length() > 0) {
                            contactEmail = primaryContact.getEmailAddressTxt();
                        }
                    }
                }
*/
                Contact billingContact = facility.getBillingContact();
                if(billingContact != null) {
                    Address billingAddr = billingContact.getAddress();
                    if (billingAddr != null) {
                        if (billingAddr.getAddressLine1() != null
                                && billingAddr.getAddressLine1().length() > 0) {
                            billingAddrLine1 = billingAddr.getAddressLine1();
                        }
                        if (billingAddr.getAddressLine2() != null
                                && billingAddr.getAddressLine2().length() > 0) {
                            billingAddrLine2 = billingAddr.getAddressLine2();
                        }
                        if (billingAddr.getCityName() != null
                                && billingAddr.getCityName().length() > 0) {
                        	billingCity = billingAddr.getCityName();
                        }
                        if (billingAddr.getState() != null
                                && billingAddr.getState().length() > 0) {
                            billingState = billingAddr.getState();
                        }
                        
                        if (billingAddr.getZipCode5() != null
                                && billingAddr.getZipCode5().length() > 0) {
                            billingZip = billingAddr.getZipCode5();
                        }
                        if (billingAddr.getZipCode4() != null
                                && billingAddr.getZipCode4().length() > 0) {
                        	billingZip = billingZip + "-"
                            + billingAddr.getZipCode4();
                        }

                        StringBuffer bName = new StringBuffer();
                        if (billingContact.getTitleCd() != null
                                && billingContact.getTitleCd().length() > 0) {
                            DefData ddata = ContactTitle.getData();
                            String titleDsc = ddata.getItems().getItemDesc(billingContact.getTitleCd());
                            bName.append(titleDsc + " ");
                        }
                        if (billingContact.getFirstNm() != null
                                && billingContact.getFirstNm().length() > 0) {
                            bName.append(billingContact.getFirstNm() + " ");
                        }
                        if (billingContact.getFirstNm() != null
                                && billingContact.getFirstNm().length() > 0) {
                            bName.append(billingContact.getLastNm());
                        }
                        if (billingContact.getSuffixCd() != null) {
                            bName.append(", " + billingContact.getSuffixCd());
                        }
                        if (bName.length() > 0) {
                            billingName = bName.toString();
                        }
                        if(billingContact.getCompanyName() != null
                                && billingContact.getCompanyName().trim().length() > 0) {   
                            billingCoName = billingContact.getCompanyName();
                        }
                        if (billingContact.getPhoneNo() != null
                                && billingContact.getPhoneNo().length() > 0) {
                            billingPhone = phoneConvert.tryFormatPhoneNumber(billingContact.getPhoneNo());
                        }
                        if (billingContact.getEmailAddressTxt() != null
                                && billingContact.getEmailAddressTxt().length() > 0) {
                            billingEmail = billingContact.getEmailAddressTxt();
                        }
						if (billingContact.getEmailAddressTxt2() != null
								&& billingContact.getEmailAddressTxt2().length() > 0) {
							billingEmail = billingContact.getEmailAddressTxt2();
						}
                    }
                }
                
                Contact complianceContact = facility.getComplianceContact();
                if(complianceContact != null) {
                    Address complianceAddr = complianceContact.getAddress();
                    if (complianceAddr != null) {
                        if (complianceAddr.getAddressLine1() != null
                                && complianceAddr.getAddressLine1().length() > 0) {
                            complianceAddrLine1 = complianceAddr.getAddressLine1();
                        }
                        if (complianceAddr.getAddressLine2() != null
                                && complianceAddr.getAddressLine2().length() > 0) {
                            complianceAddrLine2 = complianceAddr.getAddressLine2();
                        }
                        if (complianceAddr.getCityName() != null
                                && complianceAddr.getCityName().length() > 0) {
                            complianceCity = complianceAddr.getCityName();
                        }
                        if (complianceAddr.getState() != null
                                && complianceAddr.getState().length() > 0) {
                            complianceState = complianceAddr.getState();
                        }
                        if (complianceAddr.getZipCode5() != null
                                && complianceAddr.getZipCode5().length() > 0) {
                            complianceZip = complianceAddr.getZipCode5();
                        }
                        if (complianceAddr.getZipCode4() != null
                                && complianceAddr.getZipCode4().length() > 0) {
                        	complianceZip = complianceZip + "-"
                            + complianceAddr.getZipCode4();
                        }

                        StringBuffer bName = new StringBuffer();
                        if (complianceContact.getTitleCd() != null
                                && complianceContact.getTitleCd().length() > 0) {
                            DefData ddata = ContactTitle.getData();
                            complianceContactTitle = complianceContact.getTitleCd();
                            complianceContactTitleDesc = ddata.getItems().getItemDesc(complianceContactTitle);
                            bName.append(complianceContactTitleDesc + " ");

                        }
                        if (complianceContact.getFirstNm() != null
                                && complianceContact.getFirstNm().length() > 0) {
                            bName.append(complianceContact.getFirstNm() + " ");
                            complianceContactFName =  complianceContact.getFirstNm();
                        }
                        if (complianceContact.getFirstNm() != null
                                && complianceContact.getFirstNm().length() > 0) {
                            bName.append(complianceContact.getLastNm());
                            complianceContactLName =  complianceContact.getLastNm();
                        }
                        if (complianceContact.getSuffixCd() != null) {
                            bName.append(", " + complianceContact.getSuffixCd());
                        }
                        if (bName.length() > 0) {
                            complianceName = bName.toString();
                        }

    		            if(complianceContact.getCompanyTitle()!=null){
    			    		            	complianceContactJobTitle = complianceContact.getCompanyTitle();
    		            }
                        if(complianceContact.getCompanyName() != null
                                && complianceContact.getCompanyName().trim().length() > 0) {   
                            complianceCoName = complianceContact.getCompanyName();
                        }
                        if (complianceContact.getPhoneNo() != null
                                && complianceContact.getPhoneNo().length() > 0) {
                            compliancePhone = phoneConvert.tryFormatPhoneNumber(complianceContact.getPhoneNo());
                        }
                        if (complianceContact.getEmailAddressTxt() != null
                                && complianceContact.getEmailAddressTxt().length() > 0) {
                            complianceEmail = complianceContact.getEmailAddressTxt();
                        }
						if (complianceContact.getEmailAddressTxt2() != null
								&& complianceContact.getEmailAddressTxt2().length() > 0) {
							complianceEmail = complianceContact.getEmailAddressTxt2();
						}
                    }
                }
                
                Contact emissionsContact = facility.getEmissionsContact();
                if(emissionsContact != null) {
                    Address emissionsAddr = emissionsContact.getAddress();
                    if (emissionsAddr != null) {
                        if (emissionsAddr.getAddressLine1() != null
                                && emissionsAddr.getAddressLine1().length() > 0) {
                            emissionsAddrLine1 = emissionsAddr.getAddressLine1();
                        }
                        if (emissionsAddr.getAddressLine2() != null
                                && emissionsAddr.getAddressLine2().length() > 0) {
                            emissionsAddrLine2 = emissionsAddr.getAddressLine2();
                        }
                        if (emissionsAddr.getCityName() != null
                                && emissionsAddr.getCityName().length() > 0) {
                            emissionsCity = emissionsAddr.getCityName();
                        }
                        if (emissionsAddr.getState() != null
                                && emissionsAddr.getState().length() > 0) {
                            emissionsState = emissionsAddr.getState();
                        }
                        if (emissionsAddr.getZipCode5() != null
                                && emissionsAddr.getZipCode5().length() > 0) {
                            emissionsZip = emissionsAddr.getZipCode5();
                        }
                        if (emissionsAddr.getZipCode4() != null
                                && emissionsAddr.getZipCode4().length() > 0) {
                            emissionsZip = emissionsZip + "-"
                            + emissionsAddr.getZipCode4();
                        }

                        StringBuffer bName = new StringBuffer();
                        if (emissionsContact.getTitleCd() != null
                                && emissionsContact.getTitleCd().length() > 0) {
                            DefData ddata = ContactTitle.getData();
                            String titleDsc = ddata.getItems().getItemDesc(emissionsContact.getTitleCd());
                            bName.append(titleDsc + " ");

                        }
                        if (emissionsContact.getFirstNm() != null
                                && emissionsContact.getFirstNm().length() > 0) {
                            bName.append(emissionsContact.getFirstNm() + " ");
                        }
                        if (emissionsContact.getFirstNm() != null
                                && emissionsContact.getFirstNm().length() > 0) {
                            bName.append(emissionsContact.getLastNm());
                        }
                        if (emissionsContact.getSuffixCd() != null) {
                            bName.append(", " + emissionsContact.getSuffixCd());
                        }
                       
                        if (bName.length() > 0) {
                            emissionsName = bName.toString();
                        }
                        if(emissionsContact.getCompanyName() != null
                                && emissionsContact.getCompanyName().trim().length() > 0) {   
                            emissionsCoName = emissionsContact.getCompanyName();
                        }
                        if (emissionsContact.getPhoneNo() != null
                                && emissionsContact.getPhoneNo().length() > 0) {
                            emissionsPhone = phoneConvert.tryFormatPhoneNumber(emissionsContact.getPhoneNo());
                        }
                        if (emissionsContact.getEmailAddressTxt() != null
                                && emissionsContact.getEmailAddressTxt().length() > 0) {
                            emissionsEmail = emissionsContact.getEmailAddressTxt();
                        }
						if (emissionsContact.getEmailAddressTxt2() != null
								&& emissionsContact.getEmailAddressTxt2().length() > 0) {
							emissionsEmail = emissionsContact.getEmailAddressTxt2();
						}
                    }
                }
                
                Contact environmentalContact = facility.getEnvironmentalContact();
                if(environmentalContact != null) {
                    Address environmentalAddr = environmentalContact.getAddress();
                    if (environmentalAddr != null) {
                        if (environmentalAddr.getAddressLine1() != null
                                && environmentalAddr.getAddressLine1().length() > 0) {
                            environmentalAddrLine1 = environmentalAddr.getAddressLine1();
                        }
                        if (environmentalAddr.getAddressLine2() != null
                                && environmentalAddr.getAddressLine2().length() > 0) {
                            environmentalAddrLine2 = environmentalAddr.getAddressLine2();
                        }
                        if (environmentalAddr.getCityName() != null
                                && environmentalAddr.getCityName().length() > 0) {
                            environmentalCity = environmentalAddr.getCityName();
                        }
                        if (environmentalAddr.getState() != null
                                && environmentalAddr.getState().length() > 0) {
                        	environmentalState = environmentalAddr.getState();
                        }
                        if (environmentalAddr.getZipCode5() != null
                                && environmentalAddr.getZipCode5().length() > 0) {
                            environmentalZip = environmentalAddr.getZipCode5();
                        }
                        if (environmentalAddr.getZipCode4() != null
                                && environmentalAddr.getZipCode4().length() > 0) {
                        	environmentalZip = environmentalZip + "-"
                            + environmentalAddr.getZipCode4();
                        }
                        
                        if(environmentalContact.getCompanyTitle()!=null) {
                        	environmentalJobTitle = environmentalContact.getCompanyTitle();
                        }

                        StringBuffer bName = new StringBuffer();
                        if (environmentalContact.getTitleCd() != null
                                && environmentalContact.getTitleCd().length() > 0) {
                            DefData ddata = ContactTitle.getData();
                            String titleDsc = ddata.getItems().getItemDesc(environmentalContact.getTitleCd());
                            bName.append(titleDsc + " ");
                            environmentalTitle = titleDsc;

                        }
                        if (environmentalContact.getFirstNm() != null
                                && environmentalContact.getFirstNm().length() > 0) {
                            bName.append(environmentalContact.getFirstNm() + " ");
                        }
                        if (environmentalContact.getFirstNm() != null
                                && environmentalContact.getFirstNm().length() > 0) {
                            bName.append(environmentalContact.getLastNm());
                        }
                        if (environmentalContact.getSuffixCd() != null) {
                            bName.append(", " + environmentalContact.getSuffixCd());
                        }
                        if (bName.length() > 0) {
                            environmentalName = bName.toString();
                        }
                        if(environmentalContact.getCompanyName() != null
                                && environmentalContact.getCompanyName().trim().length() > 0) {   
                            environmentalCoName = environmentalContact.getCompanyName();
                        }
                        if (environmentalContact.getPhoneNo() != null
                                && environmentalContact.getPhoneNo().length() > 0) {
                            environmentalPhone = phoneConvert.tryFormatPhoneNumber(environmentalContact.getPhoneNo());
                        }
                        if (environmentalContact.getEmailAddressTxt() != null
                                && environmentalContact.getEmailAddressTxt().length() > 0) {
                            environmentalEmail = environmentalContact.getEmailAddressTxt();
                        }
						if (environmentalContact.getEmailAddressTxt2() != null
								&& environmentalContact.getEmailAddressTxt2().length() > 0) {
							environmentalEmail = environmentalContact.getEmailAddressTxt2();
						}
                    }
                }
                
                Contact monitoringContact = facility.getMonitoringContact();
                if(monitoringContact != null) {
                    Address monitoringAddr = monitoringContact.getAddress();
                    if (monitoringAddr != null) {
                        if (monitoringAddr.getAddressLine1() != null
                                && monitoringAddr.getAddressLine1().length() > 0) {
                            monitoringAddrLine1 = monitoringAddr.getAddressLine1();
                        }
                        if (monitoringAddr.getAddressLine2() != null
                                && monitoringAddr.getAddressLine2().length() > 0) {
                            monitoringAddrLine2 = monitoringAddr.getAddressLine2();
                        }
                        if (monitoringAddr.getCityName() != null
                                && monitoringAddr.getCityName().length() > 0) {
                            monitoringCity = monitoringAddr.getCityName();
                        }
                        if (monitoringAddr.getState() != null
                                && monitoringAddr.getState().length() > 0) {
                        	monitoringState = monitoringAddr.getState();
                        }
                        if (monitoringAddr.getZipCode5() != null
                                && monitoringAddr.getZipCode5().length() > 0) {
                        	monitoringZip = monitoringAddr.getZipCode5();
                        }
                        if (monitoringAddr.getZipCode4() != null
                                && monitoringAddr.getZipCode4().length() > 0) {
                        	monitoringZip = monitoringZip + "-"
                            + monitoringAddr.getZipCode4();
                        }

                        StringBuffer bName = new StringBuffer();
                        if (monitoringContact.getTitleCd() != null
                                && monitoringContact.getTitleCd().length() > 0) {
                            DefData ddata = ContactTitle.getData();
                            String titleDsc = ddata.getItems().getItemDesc(monitoringContact.getTitleCd());
                            bName.append(titleDsc + " ");

                        }
                        if (monitoringContact.getFirstNm() != null
                                && monitoringContact.getFirstNm().length() > 0) {
                            bName.append(monitoringContact.getFirstNm() + " ");
                        }
                        if (monitoringContact.getFirstNm() != null
                                && monitoringContact.getFirstNm().length() > 0) {
                            bName.append(monitoringContact.getLastNm());
                        }
                        if (monitoringContact.getSuffixCd() != null) {
                            bName.append(", " + monitoringContact.getSuffixCd());
                        }
                        if (bName.length() > 0) {
                            monitoringName = bName.toString();
                        }
                        if(monitoringContact.getCompanyName() != null
                                && monitoringContact.getCompanyName().trim().length() > 0) {   
                            monitoringCoName = monitoringContact.getCompanyName();
                        }
                        if (monitoringContact.getPhoneNo() != null
                                && monitoringContact.getPhoneNo().length() > 0) {
                            monitoringPhone = phoneConvert.tryFormatPhoneNumber(monitoringContact.getPhoneNo());
                        }
                        if (monitoringContact.getEmailAddressTxt() != null
                                && monitoringContact.getEmailAddressTxt().length() > 0) {
                            monitoringEmail = monitoringContact.getEmailAddressTxt();
                        }
						if (monitoringContact.getEmailAddressTxt2() != null
								&& monitoringContact.getEmailAddressTxt2().length() > 0) {
							monitoringEmail = monitoringContact.getEmailAddressTxt2();
						}
                    }
                }
                
                Contact onSiteOperatorContact = facility.getOnSiteOperatorContact();
                if(onSiteOperatorContact != null) {
                    Address onSiteOperatorAddr = onSiteOperatorContact.getAddress();
                    if (onSiteOperatorAddr != null) {
                        if (onSiteOperatorAddr.getAddressLine1() != null
                                && onSiteOperatorAddr.getAddressLine1().length() > 0) {
                            onSiteOperatorAddrLine1 = onSiteOperatorAddr.getAddressLine1();
                        }
                        if (onSiteOperatorAddr.getAddressLine2() != null
                                && onSiteOperatorAddr.getAddressLine2().length() > 0) {
                            onSiteOperatorAddrLine2 = onSiteOperatorAddr.getAddressLine2();
                        }
                        if (onSiteOperatorAddr.getCityName() != null
                                && onSiteOperatorAddr.getCityName().length() > 0) {
                            onSiteOperatorCity = onSiteOperatorAddr.getCityName();
                        }
                        if (onSiteOperatorAddr.getState() != null
                                && onSiteOperatorAddr.getState().length() > 0) {
                        	onSiteOperatorState = onSiteOperatorAddr.getState();
                        }
                        if (onSiteOperatorAddr.getZipCode5() != null
                                && onSiteOperatorAddr.getZipCode5().length() > 0) {
                        	onSiteOperatorZip = onSiteOperatorAddr.getZipCode5();
                        }
                        if (onSiteOperatorAddr.getZipCode4() != null
                                && onSiteOperatorAddr.getZipCode4().length() > 0) {
                        	onSiteOperatorZip = onSiteOperatorZip + "-"
                            + onSiteOperatorAddr.getZipCode4();
                        }

                        StringBuffer bName = new StringBuffer();
                        if (onSiteOperatorContact.getTitleCd() != null
                                && onSiteOperatorContact.getTitleCd().length() > 0) {
                            DefData ddata = ContactTitle.getData();
                            String titleDsc = ddata.getItems().getItemDesc(onSiteOperatorContact.getTitleCd());
                            bName.append(titleDsc + " ");

                        }
                        if (onSiteOperatorContact.getFirstNm() != null
                                && onSiteOperatorContact.getFirstNm().length() > 0) {
                            bName.append(onSiteOperatorContact.getFirstNm() + " ");
                        }
                        if (onSiteOperatorContact.getFirstNm() != null
                                && onSiteOperatorContact.getFirstNm().length() > 0) {
                            bName.append(onSiteOperatorContact.getLastNm());
                        }
                        if (onSiteOperatorContact.getSuffixCd() != null) {
                            bName.append(", " + onSiteOperatorContact.getSuffixCd());
                        }
                        if (bName.length() > 0) {
                            onSiteOperatorName = bName.toString();
                        }
                        if(onSiteOperatorContact.getCompanyName() != null
                                && onSiteOperatorContact.getCompanyName().trim().length() > 0) {   
                            onSiteOperatorCoName = onSiteOperatorContact.getCompanyName();
                        }
                        if (onSiteOperatorContact.getPhoneNo() != null
                                && onSiteOperatorContact.getPhoneNo().length() > 0) {
                            onSiteOperatorPhone = phoneConvert.tryFormatPhoneNumber(onSiteOperatorContact.getPhoneNo());
                        }
                        if (onSiteOperatorContact.getEmailAddressTxt() != null
                                && onSiteOperatorContact.getEmailAddressTxt().length() > 0) {
                            onSiteOperatorEmail = onSiteOperatorContact.getEmailAddressTxt();
                        }
						if (onSiteOperatorContact.getEmailAddressTxt2() != null
								&& onSiteOperatorContact.getEmailAddressTxt2().length() > 0) {
							onSiteOperatorEmail = onSiteOperatorContact.getEmailAddressTxt2();
						}
                    }
                }
                
                Contact permittingContact = facility.getPermittingContact();
                if(permittingContact != null) {
                    Address permittingAddr = permittingContact.getAddress();
                    if (permittingAddr != null) {
                        if (permittingAddr.getAddressLine1() != null
                                && permittingAddr.getAddressLine1().length() > 0) {
                            permittingAddrLine1 = permittingAddr.getAddressLine1();
                        }
                        if (permittingAddr.getAddressLine2() != null
                                && permittingAddr.getAddressLine2().length() > 0) {
                            permittingAddrLine2 = permittingAddr.getAddressLine2();
                        }
                        if (permittingAddr.getCityName() != null
                                && permittingAddr.getCityName().length() > 0) {
                            permittingCity = permittingAddr.getCityName();
                        }
                        if (permittingAddr.getState() != null
                                && permittingAddr.getState().length() > 0) {
                        	permittingState = permittingAddr.getState();
                        }
                        if (permittingAddr.getZipCode5() != null
                                && permittingAddr.getZipCode5().length() > 0) {
                            permittingZip = permittingAddr.getZipCode5();
                        }
                        if (permittingAddr.getZipCode4() != null
                                && permittingAddr.getZipCode4().length() > 0) {
                        	permittingZip = permittingZip + "-"
                            + permittingAddr.getZipCode4();
                        }

                        StringBuffer bName = new StringBuffer();
                        if (permittingContact.getTitleCd() != null
                                && permittingContact.getTitleCd().length() > 0) {
                            DefData ddata = ContactTitle.getData();
                            String titleDsc = ddata.getItems().getItemDesc(permittingContact.getTitleCd());
                            bName.append(titleDsc + " ");

                        }
                        if (permittingContact.getFirstNm() != null
                                && permittingContact.getFirstNm().length() > 0) {
                            bName.append(permittingContact.getFirstNm() + " ");
                        }
                        if (permittingContact.getFirstNm() != null
                                && permittingContact.getFirstNm().length() > 0) {
                            bName.append(permittingContact.getLastNm());
                        }
                        if (permittingContact.getSuffixCd() != null) {
                            bName.append(", " + permittingContact.getSuffixCd());
                        }
                        if (bName.length() > 0) {
                            permittingName = bName.toString();
                        }
                        if(permittingContact.getCompanyName() != null
                                && permittingContact.getCompanyName().trim().length() > 0) {   
                            permittingCoName = permittingContact.getCompanyName();
                        }
                        if (permittingContact.getPhoneNo() != null
                                && permittingContact.getPhoneNo().length() > 0) {
                            permittingPhone = phoneConvert.tryFormatPhoneNumber(permittingContact.getPhoneNo());
                        }
                        if (permittingContact.getEmailAddressTxt() != null
                                && permittingContact.getEmailAddressTxt().length() > 0) {
                            permittingEmail = permittingContact.getEmailAddressTxt();
                        }
						if (permittingContact.getEmailAddressTxt2() != null
								&& permittingContact.getEmailAddressTxt2().length() > 0) {
							permittingEmail = permittingContact.getEmailAddressTxt2();
						}
                    }
                }
                
                Contact respOfficialContact = facility.getRespOfficialContact();
                if(respOfficialContact != null) {
                    Address respOfficialAddr = respOfficialContact.getAddress();
                    if (respOfficialAddr != null) {
                        if (respOfficialAddr.getAddressLine1() != null
                                && respOfficialAddr.getAddressLine1().length() > 0) {
                            respOfficialAddrLine1 = respOfficialAddr.getAddressLine1();
                        }
                        if (respOfficialAddr.getAddressLine2() != null
                                && respOfficialAddr.getAddressLine2().length() > 0) {
                            respOfficialAddrLine2 = respOfficialAddr.getAddressLine2();
                        }
                        if (respOfficialAddr.getCityName() != null
                                && respOfficialAddr.getCityName().length() > 0) {
                            respOfficialCity = respOfficialAddr.getCityName();
                        }
                        if (respOfficialAddr.getState() != null
                                && respOfficialAddr.getState().length() > 0) {
                        	respOfficialState = respOfficialAddr.getState();
                        }
                        if (respOfficialAddr.getZipCode5() != null
                                && respOfficialAddr.getZipCode5().length() > 0) {
                        	respOfficialZip = respOfficialAddr.getZipCode5();
                        }
                        if (respOfficialAddr.getZipCode4() != null
                                && respOfficialAddr.getZipCode4().length() > 0) {
                        	respOfficialZip = respOfficialZip + "-"
                            + respOfficialAddr.getZipCode4();
                        }

                        StringBuffer bName = new StringBuffer();
                        if (respOfficialContact.getTitleCd() != null
                                && respOfficialContact.getTitleCd().length() > 0) {
                            DefData ddata = ContactTitle.getData();
                            String titleDsc = ddata.getItems().getItemDesc(respOfficialContact.getTitleCd());
                            bName.append(titleDsc + " ");

                        }
                        if (respOfficialContact.getFirstNm() != null
                                && respOfficialContact.getFirstNm().length() > 0) {
                            bName.append(respOfficialContact.getFirstNm() + " ");
                        }
                        if (respOfficialContact.getFirstNm() != null
                                && respOfficialContact.getFirstNm().length() > 0) {
                            bName.append(respOfficialContact.getLastNm());
                        }
                        if (respOfficialContact.getSuffixCd() != null) {
                            bName.append(", " + respOfficialContact.getSuffixCd());
                        }
                        if (bName.length() > 0) {
                            respOfficialName = bName.toString();
                        }
                        if(respOfficialContact.getCompanyName() != null
                                && respOfficialContact.getCompanyName().trim().length() > 0) {   
                            respOfficialCoName = respOfficialContact.getCompanyName();
                        }
                        if (respOfficialContact.getPhoneNo() != null
                                && respOfficialContact.getPhoneNo().length() > 0) {
                            respOfficialPhone = phoneConvert.tryFormatPhoneNumber(respOfficialContact.getPhoneNo());
                        }
                        if (respOfficialContact.getEmailAddressTxt() != null
                                && respOfficialContact.getEmailAddressTxt().length() > 0) {
                            respOfficialEmail = respOfficialContact.getEmailAddressTxt();
                        }
						if (respOfficialContact.getEmailAddressTxt2() != null
								&& respOfficialContact.getEmailAddressTxt2().length() > 0) {
							respOfficialEmail = respOfficialContact.getEmailAddressTxt2();
						}
                    }
                }
/*
                Contact[] ownerContacts = facility.getOwners();
              if (ownerContacts != null && ownerContacts.length > 0) {
                  Contact ownerContact = ownerContacts[0];
                  Address ownerAddr = ownerContact.getAddress();
                  if (ownerAddr != null) {
                      if (ownerAddr.getAddressLine1() != null
                          && ownerAddr.getAddressLine1().length() > 0) {
                          ownerAddrLine1 = ownerAddr.getAddressLine1();
                      }
                      if (ownerAddr.getAddressLine2() != null
                          && ownerAddr.getAddressLine2().length() > 0) {
                          ownerAddrLine2 = ownerAddr.getAddressLine2();
                      }
                      if (ownerAddr.getCityName() != null
                          && ownerAddr.getCityName().length() > 0) {
                          ownerAddrLine3 = ownerAddr.getCityName();
                      }
                      if (ownerAddr.getState() != null
                          && ownerAddr.getState().length() > 0) {
                          ownerAddrLine3 = ownerAddrLine3 + ", "
                              + ownerAddr.getState();
                      }
                      if (ownerAddr.getZipCode5() != null
                          && ownerAddr.getZipCode5().length() > 0) {
                          ownerAddrLine3 = ownerAddrLine3 + " "
                              + ownerAddr.getZipCode5();
                          ownerZip5 = ownerAddr.getZipCode5();
                      }
                      if (ownerAddr.getZipCode4() != null
                          && ownerAddr.getZipCode4().length() > 0) {
                          ownerAddrLine3 = ownerAddrLine3 + "-"
                              + ownerAddr.getZipCode4();
                      }
                      StringBuffer bName = new StringBuffer();
                      if (ownerContact.getTitleCd() != null
                          && ownerContact.getTitleCd().length() > 0) {
                          DefData ddata = ContactTitle.getData();
                          String titleDsc = ddata.getItems().getItemDesc(ownerContact.getTitleCd());
                          bName.append(titleDsc + " ");                
                      }
                      if (ownerContact.getFirstNm() != null
                          && ownerContact.getFirstNm().length() > 0) {
                          bName.append(ownerContact.getFirstNm() + " ");
                      }
                      if (ownerContact.getLastNm() != null
                          && ownerContact.getLastNm().length() > 0) {
                          bName.append(ownerContact.getLastNm());
                      }
                      if (ownerContact.getSuffixCd() != null) {
                          bName.append(", " + ownerContact.getSuffixCd());
                      }
                      if (bName.length() > 0) {
                          ownerName = bName.toString();
                      }
                      if(ownerContact.getCompanyName() != null
                              && ownerContact.getCompanyName().trim().length() > 0) {   
                          ownerCoName = ownerContact.getCompanyName();
                      }
                      if (ownerContact.getPhoneNo() != null
                          && ownerContact.getPhoneNo().length() > 0) {
                          ownerPhone = phoneConvert.tryFormatPhoneNumber(ownerContact.getPhoneNo());
                      }
                      if (ownerContact.getEmailAddressTxt() != null
                          && ownerContact.getEmailAddressTxt().length() > 0) {
                          ownerEmail = ownerContact.getEmailAddressTxt();
                      }
                  }
              }
*/
                if (facility.getDoLaaCd() != null 
                    && facility.getDoLaaCd().length() > 0) {

                	districtCd = facility.getDoLaaCd();  // here it is the actual code.
                    DefData doLaaData = DoLaaDef.getData();
                    if (doLaaData != null) {
                        DoLaaDef doLaaDef 
                            = (DoLaaDef) doLaaData.getItem(districtCd);
                        district = doLaaDef.getDescription();
                        String doLaaAddr = doLaaDef.getAddressLine1();
                        if (doLaaAddr != null && doLaaAddr.length() > 0) {
                        	districtAddrLine1 = doLaaAddr;
                        }
                        doLaaAddr = doLaaDef.getAddressLine2();
                        if (doLaaAddr != null && doLaaAddr.length() > 0) {
                        	districtAddrLine2 = doLaaAddr;
                        }
                        doLaaAddr = doLaaDef.getAddressLine3();
                        if (doLaaAddr != null && doLaaAddr.length() > 0) {
                        	districtAddrLine3 = doLaaAddr;
                        }
                        doLaaAddr = doLaaDef.getPhoneNumber();
                        if (doLaaAddr != null && doLaaAddr.length() > 0) {
                        	districtPhone = phoneConvert.tryFormatPhoneNumber(doLaaAddr);
                        }
                        doLaaAddr = doLaaDef.getDoLaaShortDsc();
                        if (doLaaAddr != null && doLaaAddr.length() > 0) {
                        	districtCd = doLaaAddr;  // hesre it is the short description
                        }
                        
                    }
                }

                if (facility.getFacilityRoles() != null
                    && facility.getFacilityRoles().get(FacilityRoleDef.TV_RO_REVIEW_ENGINEER) != null
                    && facility.getFacilityRoles().get(FacilityRoleDef.TV_RO_REVIEW_ENGINEER).getUserId() != null) {
                    UserDef pWriter 
                        = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.TV_RO_REVIEW_ENGINEER ).getUserId());
                    if (pWriter != null) {
                        String pWriterFName = pWriter.getUserFirstNm();
                        if (pWriterFName != null && pWriterFName.length() > 0) {
                        	tvRoReviewEngineer = pWriterFName + " ";
                        }
                        String pWriterLName = pWriter.getUserLastNm();
                        if (pWriterLName != null && pWriterLName.length() > 0) {
                        	tvRoReviewEngineer = tvRoReviewEngineer + pWriterLName;
                        }
                    }
                }
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.NSR_PERMIT_ENGINEER) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.NSR_PERMIT_ENGINEER).getUserId() != null) {

                        UserDef pWriter 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.NSR_PERMIT_ENGINEER).getUserId());
                        if (pWriter != null) {
                            String pWriterFName = pWriter.getUserFirstNm();
                            if (pWriterFName != null && pWriterFName.length() > 0) {
                            	nsrPermitEngineer = pWriterFName + " ";
                            }
                            String pWriterLName = pWriter.getUserLastNm();
                            if (pWriterLName != null && pWriterLName.length() > 0) {
                            	nsrPermitEngineer = nsrPermitEngineer + pWriterLName;
                            }
                        }
                    }

                if (facility.getFacilityRoles() != null
                    && facility.getFacilityRoles().get(FacilityRoleDef.TV_PERMIT_ENGINEER) != null
                    && facility.getFacilityRoles().get(FacilityRoleDef.TV_PERMIT_ENGINEER).getUserId() != null) {

                    UserDef erReviewer 
                        = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.TV_PERMIT_ENGINEER).getUserId());
                    if (erReviewer != null) {
                        String reviewerFName = erReviewer.getUserFirstNm();
                        if (reviewerFName != null && reviewerFName.length() > 0) {
                        	tvPermitEngineer = reviewerFName + " ";
                        }
                        String reviewerLName = erReviewer.getUserLastNm();
                        if (reviewerLName != null && reviewerLName.length() > 0) {
                        	tvPermitEngineer = tvPermitEngineer + reviewerLName;
                        }
                    }
                }
 
                if (facility.getFacilityRoles() != null
                    && facility.getFacilityRoles().get(FacilityRoleDef.COMPLIANCE_REPORT_REVIEWER) != null
                    && facility.getFacilityRoles().get(FacilityRoleDef.COMPLIANCE_REPORT_REVIEWER).getUserId() != null) {

                    UserDef reviewer 
                        = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.COMPLIANCE_REPORT_REVIEWER).getUserId());
                    if (reviewer != null) {
                        String reviewerFName = reviewer.getUserFirstNm();
                        if (reviewerFName != null && reviewerFName.length() > 0) {
                        	complianceReportReviewer = reviewerFName + " ";
                        }
                        String reviewerLName = reviewer.getUserLastNm();
                        if (reviewerLName != null && reviewerLName.length() > 0) {
                        	complianceReportReviewer = complianceReportReviewer + reviewerLName;
                        }
                    }
                }
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.AQD_ADMINISTRATOR) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.AQD_ADMINISTRATOR).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.AQD_ADMINISTRATOR).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	aqdAdministrator = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	aqdAdministrator = aqdAdministrator + reviewerLName;
                            }
                        }
                    }
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.NSR_PUBLICATION_COORDINATOR) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.NSR_PUBLICATION_COORDINATOR).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.NSR_PUBLICATION_COORDINATOR).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	nsrPublicationCoordinator = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	nsrPublicationCoordinator = nsrPublicationCoordinator + reviewerLName;
                            }
                        }
                    }
                
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.EMISSIONS_INVENTORY_REVIEWER) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.EMISSIONS_INVENTORY_REVIEWER).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.EMISSIONS_INVENTORY_REVIEWER).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	emissionsInventoryReviewer = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	emissionsInventoryReviewer = emissionsInventoryReviewer + reviewerLName;
                            }
                        }
                    }
                
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.UNDELIVERED_MAIL_ADMIN) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.UNDELIVERED_MAIL_ADMIN).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.UNDELIVERED_MAIL_ADMIN).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	undeliveredMailAdmin = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	undeliveredMailAdmin = undeliveredMailAdmin + reviewerLName;
                            }
                        }
                    }
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.NSR_ADMIN_ASSISTANT) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.NSR_ADMIN_ASSISTANT).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.NSR_ADMIN_ASSISTANT).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	nsrAdminAssistant = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	nsrAdminAssistant = nsrAdminAssistant + reviewerLName;
                            }
                        }
                    }
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.TV_PUBLICATION_COORDINATOR) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.TV_PUBLICATION_COORDINATOR).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.TV_PUBLICATION_COORDINATOR).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	tvPublicationCoordinator = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	tvPublicationCoordinator = tvPublicationCoordinator + reviewerLName;
                            }
                        }
                    }
                
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.FACILITY_PROFILE_ADMIN) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.FACILITY_PROFILE_ADMIN).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.FACILITY_PROFILE_ADMIN).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	facilityProfileAdmin = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	facilityProfileAdmin = facilityProfileAdmin + reviewerLName;
                            }
                        }
                    }
                
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.EMISSIONS_INVENTORY_INVOICER) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.EMISSIONS_INVENTORY_INVOICER).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.EMISSIONS_INVENTORY_INVOICER).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	emissionsInventoryInvoicer = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	emissionsInventoryInvoicer = emissionsInventoryInvoicer + reviewerLName;
                            }
                        }
                    }
                
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.NSR_PUBLIC_NOTICE_REVIEWER) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.NSR_PUBLIC_NOTICE_REVIEWER).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.NSR_PUBLIC_NOTICE_REVIEWER).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	nsrPublicNoticeReviewer = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	nsrPublicNoticeReviewer = nsrPublicNoticeReviewer + reviewerLName;
                            }
                        }
                    }
                
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.TV_ADMIN_ASSISTANT) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.TV_ADMIN_ASSISTANT).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.TV_ADMIN_ASSISTANT).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	tvAdminAssistant = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	tvAdminAssistant = tvAdminAssistant + reviewerLName;
                            }
                        }
                    }
                
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.NSR_PERMIT_SUPERVISOR) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.NSR_PERMIT_SUPERVISOR).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.NSR_PERMIT_SUPERVISOR).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	nsrPermitSupervisor = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	nsrPermitSupervisor = nsrPermitSupervisor + reviewerLName;
                            }
                        }
                    }

                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.NSR_PERMIT_PEER_REVIEW_ENGINEER) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.NSR_PERMIT_PEER_REVIEW_ENGINEER).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.NSR_PERMIT_PEER_REVIEW_ENGINEER).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	nsrPermitPeerReviewEngineer = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	nsrPermitPeerReviewEngineer = nsrPermitPeerReviewEngineer + reviewerLName;
                            }
                        }
                    }
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.WDEQ_DIRECTOR) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.WDEQ_DIRECTOR).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.WDEQ_DIRECTOR).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	wdeqDirector = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	wdeqDirector = wdeqDirector + reviewerLName;
                            }
                        }
                    }
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.DISTRICT_ENGINEER_UNDELIVERED_MAIL_REVIEWER) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.DISTRICT_ENGINEER_UNDELIVERED_MAIL_REVIEWER).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.DISTRICT_ENGINEER_UNDELIVERED_MAIL_REVIEWER).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	districtUndeliveredMailReviewer = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	districtUndeliveredMailReviewer = districtUndeliveredMailReviewer + reviewerLName;
                            }
                        }
                    }
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.TV_PUBLIC_NOTICE_REVIEWER) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.TV_PUBLIC_NOTICE_REVIEWER).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.TV_PUBLIC_NOTICE_REVIEWER).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	tvPublicNoticeReviewer = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	tvPublicNoticeReviewer = tvPublicNoticeReviewer + reviewerLName;
                            }
                        }
                    }
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.ENFORCEMENT_REVIEWER_CHEYENNE) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.ENFORCEMENT_REVIEWER_CHEYENNE).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.ENFORCEMENT_REVIEWER_CHEYENNE).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	enforcementActionReviewerCheyenne = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	enforcementActionReviewerCheyenne = enforcementActionReviewerCheyenne + reviewerLName;
                            }
                        }
                    }
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.ENFORCEMENT_REVIEWER_DISTRICT) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.ENFORCEMENT_REVIEWER_DISTRICT).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.ENFORCEMENT_REVIEWER_DISTRICT).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	enforcementActionReviewerDistrict = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	enforcementActionReviewerDistrict = enforcementActionReviewerDistrict + reviewerLName;
                            }
                        }
                    }
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.TV_PERMIT_SUPERVISOR) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.TV_PERMIT_SUPERVISOR).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.TV_PERMIT_SUPERVISOR).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	tvPermitSupervisor = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	tvPermitSupervisor = tvPermitSupervisor + reviewerLName;
                            }
                        }
                    }
                
                if (facility.getFacilityRoles() != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.TV_PERMIT_PEER_REVIEW_ENGINEER) != null
                        && facility.getFacilityRoles().get(FacilityRoleDef.TV_PERMIT_PEER_REVIEW_ENGINEER).getUserId() != null) {

                        UserDef reviewer 
                            = getInfrastructureService().retrieveUserDef(facility.getFacilityRoles().get(FacilityRoleDef.TV_PERMIT_PEER_REVIEW_ENGINEER).getUserId());
                        if (reviewer != null) {
                            String reviewerFName = reviewer.getUserFirstNm();
                            if (reviewerFName != null && reviewerFName.length() > 0) {
                            	tvPermitPeerReviewEngineer = reviewerFName + " ";
                            }
                            String reviewerLName = reviewer.getUserLastNm();
                            if (reviewerLName != null && reviewerLName.length() > 0) {
                            	tvPermitPeerReviewEngineer = tvPermitPeerReviewEngineer + reviewerLName;
                            }
                        }
                    }
                

                if (facility.getNaicsCds() != null) {
                    DefData naicsDefs = NAICSDef.getData();
                    for (String code : facility.getNaicsCds()) {
                        String desc = naicsDefs.getItems().getItemDesc(code);
                        if (desc != null && desc.length() > 0) {
                            if (naics.length() == 0) {
                                naics = new String(desc);
                            } 
                            else {
                                naics = naics + ", " + desc;
                            }
                        }
                    }
                }

                if (facility.getPortableGroupTypeCd() != null) {
                    DefData ptgDefs = PortableGroupTypes.getData();
                    String ptgDesc = ptgDefs.getItems().getItemDesc(facility.getPortableGroupTypeCd());
                    if (ptgDesc != null && ptgDesc.length() > 0) {
                        portableType = ptgDesc;
                    }
                }
                
                
                
                          

                ArrayList<DocumentGenerationBean> fpeus = new ArrayList<DocumentGenerationBean>();

                for (EmissionUnit feu : facility.getEmissionUnits()) {

                    DocumentGenerationBean euBean = new DocumentGenerationBean();

                    // ...fetch the emission unit's data.
                    String fpEUID = "";
                    String fpEUType = "";
                    String fpEUAQDDesc = "";
                    String fpEUWiseViewId = "";
                    String fpEUCompanyDesc = "";
                    String fpEUCompanyID = "";
                    String fpEUStatus = "";
                    String fpEUBeginInstall = "";
                    String fpEUBeginOper = "";
                    String fpEURecentInstall = "";
                    String fpEURecentOper = "";
                    String fpEUShutdownDate = "";
                    String fpEUShutdownNotificationDate = "";
                    
                    

                    if (feu.getEpaEmuId() != null && feu.getEpaEmuId().length() > 0) {
                        fpEUID = feu.getEpaEmuId();
                    }
                    if (feu.getEmissionUnitTypeName() != null && feu.getEmissionUnitTypeName().length() > 0) {
                    	fpEUType = feu.getEmissionUnitTypeName();
                    }
                    if (feu.getEuDesc() != null && feu.getEuDesc().length() > 0) {
                    	fpEUAQDDesc = feu.getEuDesc();
                    }
                    if (feu.getWiseViewId() != null && feu.getWiseViewId().length() > 0) {
                    	fpEUWiseViewId = feu.getWiseViewId();
                    }
                    if (feu.getRegulatedUserDsc() != null && feu.getRegulatedUserDsc().length() > 0) {
                        fpEUCompanyDesc = feu.getRegulatedUserDsc();
                    }
                    if (feu.getCompanyId() != null && feu.getCompanyId().length() > 0) {
                        fpEUCompanyID = feu.getCompanyId();
                    }
                    if (feu.getOperatingStatusCd() != null) {
                    	fpEUStatus = EuOperatingStatusDef.getData().getItems().getItemDesc(feu.getOperatingStatusCd());
                    }
                    if (feu.getEuInitInstallDate() != null) {
                        fpEUBeginInstall = df.format(feu.getEuInitInstallDate());
                    }
                    if (feu.getEuInitStartupDate() != null) {
                    	fpEUBeginOper = df.format(feu.getEuInitStartupDate());
                    }
                    if (feu.getEuInstallDate() != null) {
                    	fpEURecentInstall = df.format(feu.getEuInstallDate());
                    }
                    if (feu.getEuStartupDate() != null) {
                    	fpEURecentOper = df.format(feu.getEuStartupDate());
                    }
                    if (feu.getEuShutdownDate() != null) {
                    	fpEUShutdownDate = df.format(feu.getEuShutdownDate());
                    }
                    if (feu.getEuShutdownNotificationDate() != null) {
                    	fpEUShutdownNotificationDate = df.format(feu.getEuShutdownNotificationDate());
                    }

                    euBean.getProperties().put("fpEUID", fpEUID);
                    euBean.getProperties().put("fpEUType", fpEUType);
                    euBean.getProperties().put("fpEUAQDDesc", fpEUAQDDesc);
                    euBean.getProperties().put("fpEUWiseViewId", fpEUWiseViewId);
                    euBean.getProperties().put("fpEUCompanyDesc", fpEUCompanyDesc);
                    euBean.getProperties().put("fpEUCompanyID", fpEUCompanyID);
                    euBean.getProperties().put("fpEUStatus", fpEUStatus);
                    euBean.getProperties().put("fpEUBeginInstall", fpEUBeginInstall);
                    euBean.getProperties().put("fpEUBeginOper", fpEUBeginOper);
                    euBean.getProperties().put("fpEURecentInstall", fpEURecentInstall);
                    euBean.getProperties().put("fpEURecentOper", fpEURecentOper);
                    euBean.getProperties().put("fpEUShutdownDate", fpEUShutdownDate);
                    euBean.getProperties().put("fpEUShutdownNotificationDate", fpEUShutdownNotificationDate);

                    fpeus.add(euBean);
                }

                getChildCollections().put("fpEUs", fpeus);
                
            }
            else {
                throw new DocumentGenerationException("Facility parameter is null. ");
            }

            getProperties().put("facilityID", facilityID);
            getProperties().put("facilityName", facilityName);
            getProperties().put("facilityDescription", facilityDescription);
            getProperties().put("facilityOperatingStatus", facilityOperatingStatus);
            getProperties().put("facilityClass", facilityClass);
            getProperties().put("facilityType", facilityType);
            getProperties().put("facilityAfs", facilityAfs);
            getProperties().put("facilityCerrClass", facilityCerrClass);
            getProperties().put("facilityInspectionClass", facilityInspectionClass);            
            getProperties().put("facilityAddrLine1", facilityAddrLine1);
            getProperties().put("facilityAddrLine2", facilityAddrLine2);
            //getProperties().put("facilityAddrLine3", facilityAddrLine3);
            getProperties().put("facilityCity", facilityCity);
            getProperties().put("facilityState", facilityState);
            getProperties().put("facilityZip", facilityZip);
            
            //getProperties().put("contactName", contactName.toString());
            //getProperties().put("contactCoName", contactCoName);
            //getProperties().put("contactAddrLine1", contactAddrLine1);
            //getProperties().put("contactAddrLine2", contactAddrLine2);
            //getProperties().put("contactAddrLine3", contactAddrLine3);
            //getProperties().put("contactPhone", contactPhone);
            //getProperties().put("contactEmail", contactEmail);
            //getProperties().put("contactZip5", contactZip5);

            getProperties().put("billingName", billingName.toString());
            getProperties().put("billingCoName", billingCoName);
            getProperties().put("billingAddrLine1", billingAddrLine1);
            getProperties().put("billingAddrLine2", billingAddrLine2);
            getProperties().put("billingCity", billingCity);
            getProperties().put("billingState", billingState);
            getProperties().put("billingZip", billingZip);
            getProperties().put("billingPhone", billingPhone);
            getProperties().put("billingEmail", billingEmail);
            
            getProperties().put("complianceName", complianceName.toString());
            getProperties().put("complianceCoName", complianceCoName);
            getProperties().put("complianceContactTitle", complianceContactTitleDesc);
            getProperties().put("complianceContactFName", complianceContactFName);
            getProperties().put("complianceContactLName", complianceContactLName);
            getProperties().put("complianceContactJobTitle", complianceContactJobTitle);
            getProperties().put("complianceAddrLine1", complianceAddrLine1);
            getProperties().put("complianceAddrLine2", complianceAddrLine2);
            getProperties().put("complianceCity", complianceCity);
            getProperties().put("complianceState", complianceState);
            getProperties().put("complianceZip", complianceZip);
            getProperties().put("compliancePhone", compliancePhone);
            getProperties().put("complianceEmail", complianceEmail);
            
          
            getProperties().put("emissionsName", emissionsName);
            getProperties().put("emissionsCoName", emissionsCoName);
            getProperties().put("emissionsAddrLine1", emissionsAddrLine1);
            getProperties().put("emissionsAddrLine2", emissionsAddrLine2);
            getProperties().put("emissionsCity", emissionsCity);
            getProperties().put("emissionsState", emissionsState);
            getProperties().put("emissionsZip", emissionsZip);
            getProperties().put("emissionsPhone", emissionsPhone);
            getProperties().put("emissionsEmail", emissionsEmail);
            
            getProperties().put("environmentalName", environmentalName.toString());
            getProperties().put("environmentalCoName", environmentalCoName);
            getProperties().put("environmentalTitle", environmentalTitle);
            getProperties().put("environmentalJobTitle", environmentalJobTitle);
            getProperties().put("environmentalAddrLine1", environmentalAddrLine1);
            getProperties().put("environmentalAddrLine2", environmentalAddrLine2);
            getProperties().put("environmentalCity", environmentalCity);
            getProperties().put("environmentalState", environmentalState);
            getProperties().put("environmentalZip", environmentalZip);
            getProperties().put("environmentalPhone", environmentalPhone);
            getProperties().put("environmentalEmail", environmentalEmail);
            
            getProperties().put("monitoringName", monitoringName.toString());
            getProperties().put("monitoringCoName", monitoringCoName);
            getProperties().put("monitoringAddrLine1", monitoringAddrLine1);
            getProperties().put("monitoringAddrLine2", monitoringAddrLine2);
            getProperties().put("monitoringCity", monitoringCity);
            getProperties().put("monitoringState", monitoringState);
            getProperties().put("monitoringZip", monitoringZip);
            getProperties().put("monitoringPhone", monitoringPhone);
            getProperties().put("monitoringEmail", monitoringEmail);
            
            getProperties().put("onSiteOperatorName", onSiteOperatorName.toString());
            getProperties().put("onSiteOperatorCoName", onSiteOperatorCoName);
            getProperties().put("onSiteOperatorAddrLine1", onSiteOperatorAddrLine1);
            getProperties().put("onSiteOperatorAddrLine2", onSiteOperatorAddrLine2);
            getProperties().put("onSiteOperatorCity", onSiteOperatorCity);
            getProperties().put("onSiteOperatorState", onSiteOperatorState);
            getProperties().put("onSiteOperatorZip", onSiteOperatorZip);
            getProperties().put("onSiteOperatorPhone", onSiteOperatorPhone);
            getProperties().put("onSiteOperatorEmail", onSiteOperatorEmail);
            
            getProperties().put("permittingName", permittingName.toString());
            getProperties().put("permittingCoName", permittingCoName);
            getProperties().put("permittingAddrLine1", permittingAddrLine1);
            getProperties().put("permittingAddrLine2", permittingAddrLine2);
            getProperties().put("permittingCity", permittingCity);
            getProperties().put("permittingState", permittingState);
            getProperties().put("permittingZip", permittingZip);
            getProperties().put("permittingPhone", permittingPhone);
            getProperties().put("permittingEmail", permittingEmail);
            
            getProperties().put("respOfficialName", respOfficialName.toString());
            getProperties().put("respOfficialCoName", respOfficialCoName);
            getProperties().put("respOfficialAddrLine1", respOfficialAddrLine1);
            getProperties().put("respOfficialAddrLine2", respOfficialAddrLine2);
            getProperties().put("respOfficialCity", respOfficialCity);
            getProperties().put("respOfficialState", respOfficialState);
            getProperties().put("respOfficialZip", respOfficialZip);
            getProperties().put("respOfficialPhone", respOfficialPhone);
            getProperties().put("respOfficialEmail", respOfficialEmail);
            
            //getProperties().put("ownerName", ownerName.toString());
            //getProperties().put("ownerCoName", ownerCoName);
            //getProperties().put("ownerAddrLine1", ownerAddrLine1);
            //getProperties().put("ownerAddrLine2", ownerAddrLine2);
            //getProperties().put("ownerAddrLine3", ownerAddrLine3);
            //getProperties().put("ownerPhone", ownerPhone);
            //getProperties().put("ownerEmail", ownerEmail);
            //getProperties().put("ownerZip5", ownerZip5);
           

            if (countyCd != null) {
                CountyDef cd = getInfrastructureService().retrieveCounty(countyCd);
                if (cd != null) {
                    if (cd.getCountyNm() != null
                        && cd.getCountyNm().length() > 0) {
                        county = cd.getCountyNm();
                        countySeat = cd.getCountySeat();
                    }
                    if (cd.getPtioCCList() != null
                        && cd.getPtioCCList().length() > 0) {
                        ptiPtioDraftCC = cd.getPtioCCList();
                    }
                    if (cd.getDoLaaCCList() != null
                        && cd.getDoLaaCCList().length() > 0) {
                        doLaaCC = cd.getDoLaaCCList();
                    }
                    if (cd.getPtoCCList() != null
                        && cd.getPtoCCList().length() > 0) {
                        titleVDraftCC = cd.getPtoCCList();
                    }
                }
            }
            getProperties().put("county", county);
            getProperties().put("countyCd", countyCd);

            getProperties().put("districtCd", districtCd);
            getProperties().put("district", district);
            getProperties().put("districtAddrLine1", districtAddrLine1);
            getProperties().put("districtAddrLine2", districtAddrLine2);
            getProperties().put("districtAddrLine3", districtAddrLine3);
            getProperties().put("districtPhone", districtPhone);
            
            getProperties().put("ptiPtioDraftCC", ptiPtioDraftCC);
            getProperties().put("titleVDraftCC", titleVDraftCC);
            getProperties().put("doLaaCC", doLaaCC);
            
            
            getProperties().put("tvRoReviewEngineer", tvRoReviewEngineer);
            getProperties().put("nsrPermitEngineer", nsrPermitEngineer);
            getProperties().put("tvPermitEngineer", tvPermitEngineer);
            getProperties().put("complianceReportReviewer", complianceReportReviewer);
            getProperties().put("aqdAdministrator", aqdAdministrator);
            getProperties().put("nsrPublicationCoordinator", nsrPublicationCoordinator);
            getProperties().put("emissionsInventoryReviewer", emissionsInventoryReviewer);
            getProperties().put("undeliveredMailAdmin", undeliveredMailAdmin);
            getProperties().put("nsrAdminAssistant", nsrAdminAssistant);
            getProperties().put("tvPublicationCoordinator", tvPublicationCoordinator);
            getProperties().put("facilityProfileAdmin", facilityProfileAdmin);
            getProperties().put("emissionsInventoryInvoicer", emissionsInventoryInvoicer);
            getProperties().put("nsrPublicNoticeReviewer", nsrPublicNoticeReviewer);
            getProperties().put("tvAdminAssistant", tvAdminAssistant);
            getProperties().put("nsrPermitSupervisor", nsrPermitSupervisor);
            getProperties().put("nsrPermitPeerReviewEngineer", nsrPermitPeerReviewEngineer);
            getProperties().put("wdeqDirector", wdeqDirector);
            getProperties().put("districtUndeliveredMailReviewer", districtUndeliveredMailReviewer);
            getProperties().put("tvPublicNoticeReviewer", tvPublicNoticeReviewer);
            getProperties().put("enforcementActionReviewerCheyenne", enforcementActionReviewerCheyenne);
            getProperties().put("enforcementActionReviewerDistrict", enforcementActionReviewerDistrict);
            getProperties().put("tvPermitSupervisor", tvPermitSupervisor);
            getProperties().put("tvPermitPeerReviewEngineer", tvPermitPeerReviewEngineer);

            getProperties().put("naics", naics);
            getProperties().put("portableType", portableType);


            //getProperties().put("perDueDate", "");
            getProperties().put("perStart", "");
            getProperties().put("perEnd", "");

            if (facility.getTvCertRepdueDate() != null) {
                if (facility.getTvCertRepdueDate().matches("\\d\\d\\d\\d")) {
                    String month = facility.getTvCertRepdueDate().substring(0, 2);
                    String day = facility.getTvCertRepdueDate().substring(2);
                    month = monthNameForMonthNumber(Integer.valueOf(month).intValue());
                    tvccDueDate = month + " " + day;
                } else {
                    logger.error("Illegal value in tv_cert_report_due_date field (" +
                            facility.getTvCertRepdueDate() + " for facility: " + 
                            facility.getFacilityId());
                }
            }
            getProperties().put("tvccDueDate", tvccDueDate);
            
            getProperties().put("facilityCompanyName", facilityCompanyName);
            getProperties().put("facilityCompanyID", facilityCompanyID);
            getProperties().put("facilityCompanyAddress1", facilityCompanyAddress1);
            getProperties().put("facilityCompanyAddress2", facilityCompanyAddress2);
            getProperties().put("facilityCompanyCity", facilityCompanyCity);
            getProperties().put("facilityCompanyZip", facilityCompanyZip);
            getProperties().put("facilityCompanyState", facilityCompanyState);              
            
            getProperties().put("quarterQuarterVal", quarterQuarterVal);
            getProperties().put("quarterVal", quarterVal);
            getProperties().put("sectionVal", sectionVal);
            getProperties().put("townShipVal", townShipVal);
            getProperties().put("rangeVal", rangeVal);
            getProperties().put("countySeat", countySeat);
            
            getProperties().put("facilityLat", facilityLat);
            getProperties().put("facilityLong", facilityLong);
            getProperties().put("facilityPlss", facilityPlss);
            getProperties().put("facilityCounty", facilityCounty);
            getProperties().put("locationEffectiveDate", locationEffectiveDate);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DocumentGenerationException("setFacility(Facility) failed: " 
                                                  + e.getMessage(), e);
        }

    }
    
    String monthNameForMonthNumber(int monthNumber) {
        String month = "";
        switch (monthNumber) {
        case 1:
            month = "January";
            break;
        case 2:
            month = "February";
            break;
        case 3:
            month = "March";
            break;
        case 4:
            month = "April";
            break;
        case 5:
            month = "May";
            break;
        case 6:
            month = "June";
            break;
        case 7:
            month = "July";
            break;
        case 8:
            month = "August";
            break;
        case 9:
            month = "September";
            break;
        case 10:
            month = "October";
            break;
        case 11:
            month = "November";
            break;
        case 12:
            month = "December";
            break;
        }
        return month;
    }

    public void setPermit(Permit permit) throws DocumentGenerationException {

        String permitNumber = "";
        String permitType = "";
        String permitDescription = "";
        String permitReasons = "";
        String ppPublishDate = "";
        String finalIssueDate = "";
        String permitEffectiveDate = "";
        String permitExpirationDate = "";
        String tvAppDueDate = "";
        String permitInitialFee = "";
        String permitFinalFee = "";
        String permitTotalFee = "";
        String permitCCNames = "";
        String permitActionType = "";
        String permitDemonstrationRequired = "";
        String permitModelingRequired = "";
        String subjectToPsd = "";
        String subjectToNANSR = "";
        String noMajorGHG = "";
        String syntheticMinor = "";
        String cems = "";
        String mact = "";
        String nsps = "";
        String majorGHG = "";
        String neshaps = "";
        String netting = "";
        String majorNonAttainment = "";
        String modelingSubmitted = "";
        String receivedComments = "";
        String permitReceiptLetter = "";
        String checkFacilityInventoryPTETable = "";
        String publicNoticeNeeded = "";
        String hearingRequested = "";
        
        String part61NESHAP = "";
        String part63NESHAP = "";
        String applicationNumbers = "";
        int appNo;
        
        String noticePublishDate = "";
        String hearingNoticePublishDate = "";
        String hearingDate = "";
        
        String commentPeriodEndDate = "";
        String newspaperName = "";

        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        

        try {

            if (permit != null) {
//            	extPermit = permit;
                if (permit.getPermitNumber() != null
                    && permit.getPermitNumber().length() > 0) {
                    permitNumber = permit.getPermitNumber();
                }

                if (permit.getPermitType() != null 
                    && permit.getPermitType().length() > 0) {
                    String permitTypeDsc 
                        = PermitTypeDef.getLongData().getItems().getItemDesc(permit.getPermitType());
                    if (permitTypeDsc != null && permitTypeDsc.length() > 0) {
                        permitType = permitTypeDsc;
                    }
                }

                List<String> reasonDescriptions 
                    = PermitReasonsDef.getReasonDescriptions(permit.getPermitReasonCDs(),
                                                             permit.getPermitType());
                StringBuffer sb = new StringBuffer();
                for (String reason : reasonDescriptions) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(reason);
                }
                if (sb.length() > 0) {
                    permitReasons = sb.toString();
                }

                if (permit.getDescription() != null 
                    && permit.getDescription().length() > 0) {
                    permitDescription = permit.getDescription();
               //} else if(permit instanceof RPEPermit){
               // 	permitDescription = "Time extension to begin installation.";
                }

                if (permit instanceof TVPermit) {
                    TVPermit tvPermit = (TVPermit) permit;
                    if (tvPermit.getPpIssueDate() != null) {
                        cal = Calendar.getInstance();
                        cal.setTimeInMillis(tvPermit.getPpIssueDate().getTime());
                        ppPublishDate 
                            = Integer.toString(cal.get(Calendar.MONTH) + 1)
                            + "/"
                            + Integer.toString(cal.get(Calendar.DAY_OF_MONTH))
                            + "/" + Integer.toString(cal.get(Calendar.YEAR));
                    }
                    //if (tvPermit.getPppIssueDate() != null) {
                    //    cal = Calendar.getInstance();
                    //    cal.setTimeInMillis(tvPermit.getPppIssueDate().getTime());
                    //    pppIssueDate 
                    //        = Integer.toString(cal.get(Calendar.MONTH) + 1)
                    //        + "/"
                    //        + Integer.toString(cal.get(Calendar.DAY_OF_MONTH))
                    //        + "/" + Integer.toString(cal.get(Calendar.YEAR));
                    //}
                }
                if (permit.getFinalIssueDate() != null) {
                    cal = Calendar.getInstance();
                    cal.setTimeInMillis(permit.getFinalIssueDate().getTime());
                    finalIssueDate 
                        = Integer.toString(cal.get(Calendar.MONTH) + 1)
                        + "/"
                        + Integer.toString(cal.get(Calendar.DAY_OF_MONTH))
                        + "/" + Integer.toString(cal.get(Calendar.YEAR));
                }
                if (permit.getEffectiveDate() != null) {
                    cal = Calendar.getInstance();
                    cal.setTimeInMillis(permit.getEffectiveDate().getTime());
                    permitEffectiveDate 
                        = Integer.toString(cal.get(Calendar.MONTH) + 1)
                        + "/"
                        + Integer.toString(cal.get(Calendar.DAY_OF_MONTH))
                        + "/" + Integer.toString(cal.get(Calendar.YEAR));
                }
                if (permit.getExpirationDate() != null) {
                    cal = Calendar.getInstance();
                    cal.setTimeInMillis(permit.getExpirationDate().getTime());
                    permitExpirationDate 
                        = Integer.toString(cal.get(Calendar.MONTH) + 1)
                        + "/"
                        + Integer.toString(cal.get(Calendar.DAY_OF_MONTH))
                        + "/" + Integer.toString(cal.get(Calendar.YEAR));
                    cal.setTimeInMillis(permit.getExpirationDate().getTime()
                                        - (180L * 86400L * 1000L));
                    tvAppDueDate = Integer.toString(cal.get(Calendar.MONTH) + 1)
                        + "/"
                        + Integer.toString(cal.get(Calendar.DAY_OF_MONTH))
                        + "/" + Integer.toString(cal.get(Calendar.YEAR));
                }

                if (permit.getApplicationNumbers() != null
                    && permit.getApplicationNumbers().length() > 0) {
                    applicationNumbers = permit.getApplicationNumbers();                  
                }
                //List<Application> app = permit.getApplications();
                //for (int i = 0; i < app.size(); i++)
               // {
                 //   Application a = app.get(i);
                  // System.out.println("a.getContact().getFirstNm(): "+a.getContact().getFirstNm());
               // }


                if (permit instanceof PTIOPermit) {
                    if (((PTIOPermit) permit).getInitialInvoice() != null) {
                        NumberFormat nf = NumberFormat.getInstance(Locale.US);
                        nf.setMaximumFractionDigits(2);
                        nf.setMinimumFractionDigits(2);
                        permitInitialFee = "$" + nf.format(((PTIOPermit) permit).getInitialInvoice());
                    }
                    if (((PTIOPermit) permit).getFinalInvoice() != null) {
                        NumberFormat nf = NumberFormat.getInstance(Locale.US);
                        nf.setMaximumFractionDigits(2);
                        nf.setMinimumFractionDigits(2);
                        permitFinalFee = "$" + nf.format(((PTIOPermit) permit).getFinalInvoice());
                    }
                    if (((PTIOPermit) permit).getTotalAmount() != null) {
                        NumberFormat nf = NumberFormat.getInstance(Locale.US);
                        nf.setMaximumFractionDigits(2);
                        nf.setMinimumFractionDigits(2);
                        permitTotalFee = "$" + nf.format(((PTIOPermit) permit).getTotalAmount());
                    }
                }
                
                if (permit instanceof PTIOPermit) {
					if (((PTIOPermit) permit).getPermitActionType() != null) {
						permitActionType = PermitActionTypeDef
								.getData()
								.getItems()
								.getItemDesc(
										((PTIOPermit) permit)
												.getPermitActionType());
					}
					

					
                }
                
                
                if (permit instanceof PTIOPermit) {
					String otherTypeOfDemonstrationReqFlag = ((PTIOPermit) permit)
							.getOtherTypeOfDemonstrationReq();
					if (otherTypeOfDemonstrationReqFlag != null
							&& otherTypeOfDemonstrationReqFlag
									.equalsIgnoreCase("Y")) {
						permitDemonstrationRequired = "Yes";
					} else {
						permitDemonstrationRequired = "No";
					}
					
					String modelingRequiredFlag = ((PTIOPermit) permit).getModelingRequired();
					if (modelingRequiredFlag != null
							&& modelingRequiredFlag
									.equalsIgnoreCase("Y")) {
						permitModelingRequired = "Yes";
					} else {
						permitModelingRequired = "No";
					}
					
					String subjectPSDFlag = ((PTIOPermit) permit).getSubjectToPSD();
					if (subjectPSDFlag != null
							&& subjectPSDFlag
									.equalsIgnoreCase("Y")) {
						subjectToPsd = "Yes";
					} else {
						subjectToPsd = "No";
					}
					
					String subjectNSRFlag = ((PTIOPermit) permit).getSubjectToNANSR();
					if (subjectNSRFlag != null
							&& subjectNSRFlag
									.equalsIgnoreCase("Y")) {
						subjectToNANSR = "Yes";
					} else {
						subjectToNANSR = "No";
					}
                }
                
				if (permit.getReceivedComments() != null) {
					receivedComments = PermitReceivedCommentsDef.getData()
							.getItems()
							.getItemDesc(permit.getReceivedComments());
				}
				
				if (permit.getPublicNoticeNewspaperCd() != null
						&& permit.getPublicNoticeNewspaperCd().length() > 0) {
					newspaperName = NewspaperDef.getData().getItems()
							.getItemDesc(permit.getPublicNoticeNewspaperCd());
				}
                
                if (permit instanceof PTIOPermit) {
                    if (((PTIOPermit) permit).isReceiptLetterSent()) {
                    	permitReceiptLetter = "Yes";
                    }
                    else {
                    	permitReceiptLetter = "No";
                    }
                    if (((PTIOPermit) permit).isUpdateFacilityDtlPTETableComments()) {
                    	checkFacilityInventoryPTETable = "Yes";
                    }
                    else {
                    	checkFacilityInventoryPTETable = "No";
                    }
                    if (((PTIOPermit) permit).isIssueDraft()) {
                    	publicNoticeNeeded = "Yes";
                    }
                    else {
                    	publicNoticeNeeded = "No";
                    }
                    if (((PTIOPermit) permit).isDapcHearingReqd()) {
                    	hearingRequested = "Yes";
                    }
                    else {
                    	hearingRequested = "No";
                    }
                    if (((PTIOPermit) permit).isAvoidMajorGHGSM()) {
                        noMajorGHG = "Yes";
                    }
                    else {
                    	noMajorGHG = "No";
                    }
                    if (((PTIOPermit) permit).isSmtv()) {
                        syntheticMinor = "Yes";
                    }
                    else {
                        syntheticMinor = "No";
                    }
                    if (((PTIOPermit) permit).isCem()) {
                        cems = "Yes";
                    }
                    else {
                        cems = "No";
                    }
                    if (((PTIOPermit) permit).isNetting()) {
                        netting = "Yes";
                    }
                    else {
                        netting = "No";
                    }
                    if (((PTIOPermit) permit).isModelingSubmitted()) {
                        modelingSubmitted = "Yes";
                    }
                    else {
                        modelingSubmitted = "No";
                    }
                    if (((PTIOPermit) permit).isMajorNonAttainment()) {
                        majorNonAttainment = "Yes";
                    }
                    else {
                        majorNonAttainment = "No";
                    }
                    if (((PTIOPermit) permit).isPart61NESHAP()) {
                    	part61NESHAP = "Yes";
                    }
                    else {
                    	part61NESHAP = "No";
                    }
                    if (((PTIOPermit) permit).isPart63NESHAP()) {
                    	part63NESHAP = "Yes";
                    }
                    else {
                    	part63NESHAP = "No";
                    }
                }
                else {
                	permitReceiptLetter = "No";
                	checkFacilityInventoryPTETable = "No";
                	publicNoticeNeeded = "No";
                	hearingRequested = "No";
                    noMajorGHG = "No";
                    syntheticMinor = "No";
                    cems = "No";
                    netting = "No";
                    modelingSubmitted = "No";
                    part61NESHAP = "No";
                    part63NESHAP = "No";
                }

                if (permit.isMact()) {
                    mact = "Yes";
                }
                else {
                    mact = "No";
                }
                if (permit.isNsps()) {
                    nsps = "Yes";
                }
                else {
                    nsps = "No";
                }
                if (permit.isMajorGHG()) {
                	majorGHG = "Yes";
                } else {
                	majorGHG = "No";
                }
                if (permit.isNeshaps()) {
                    neshaps = "Yes";
                }
                else {
                    neshaps = "No";
                }

                ArrayList<DocumentGenerationBean> pccBeans = new ArrayList<DocumentGenerationBean>();

                List<PermitCC> permitCCs = permit.getPermitCCList();
                boolean isFirst = true;
                for (PermitCC pcc : permitCCs) {

                    DocumentGenerationBean pccBean = new DocumentGenerationBean();
                    String ccName = "";
                    String ccAddress1 = "";
                    String ccAddress2 = "";
                    String ccCity = "";
                    String ccState = "";
                    String ccZipCode = "";
                    String ccZipCode5 = "";
                    if (pcc.getName() != null) {
                        ccName = pcc.getName();
                        if (isFirst) {
                            permitCCNames = ccName;
                            isFirst = false;
                        }
                        else {
                            permitCCNames = permitCCNames + ", " + ccName;
                        }
                    }
                    if (pcc.getAddress1() != null) {
                        ccAddress1 = pcc.getAddress1();
                    }
                    if (pcc.getAddress2() != null) {
                        ccAddress2 = pcc.getAddress2();
                    }
                    if (pcc.getCity() != null) {
                        ccCity = pcc.getCity();
                    }
                    if (pcc.getState() != null) {
                        ccState = pcc.getState();
                    }
                    if (pcc.getZipCode() != null) {
                        ccZipCode = pcc.getZipCode();
                    }
                    if (ccZipCode.length() >= 5) {
                        ccZipCode5 = ccZipCode.substring(0, 5);
                    }
                    String ccAddress3 = ccCity + ", " + ccState + " " + ccZipCode;
                    pccBean.getProperties().put("ccName", ccName);
                    pccBean.getProperties().put("ccAddress1", ccAddress1);
                    pccBean.getProperties().put("ccAddress2", ccAddress2);
                    pccBean.getProperties().put("ccCity", ccCity);
                    pccBean.getProperties().put("ccState", ccState);
                    pccBean.getProperties().put("ccZipCode", ccZipCode);
                    pccBean.getProperties().put("ccZipCode5", ccZipCode5);
                    pccBean.getProperties().put("ccAddress3", ccAddress3);
                    pccBeans.add(pccBean);
                }
                getChildCollections().put("permitCCList", pccBeans);
                getProperties().put("permitCCNames", permitCCNames);

                // Fetch the permits's emission units.
                ArrayList<DocumentGenerationBean> euGroupBeans = new ArrayList<DocumentGenerationBean>();
                    
                // For each euGroup...
                List<PermitEUGroup> euGroups = permit.getEuGroups();
                for (PermitEUGroup peug : euGroups) {

                    DocumentGenerationBean euGroupBean = new DocumentGenerationBean();
                    ArrayList<DocumentGenerationBean> eus = new ArrayList<DocumentGenerationBean>();

                    List<PermitEU> euList = peug.getPermitEUs();
                    for (PermitEU peu : euList) {

                        DocumentGenerationBean euBean = new DocumentGenerationBean();

                        // ...fetch the permit emission unit's data.
                        String euID = "";
                        String aqdDescription = "";
                        String companyID = "";
                        String supersededPermitNbr = "";
                        String euPermitNumber = "";
                        String euPermitEffectiveDate = "";

                        // ...fetch the facility emission unit's data.
                        String fpEUCompanyDesc = "";
                        String fpEUCompanyID = "";
                        String fpEUBeginInstall = "";
                        String fpEUCompleteInstall = "";
                        String fpEUCommenceOper = "";

                        if (peu.getFpEU() != null) {
                            euID = peu.getFpEU().getEpaEmuId();

                            if (peu.getFpEU().getRegulatedUserDsc() != null 
                                && peu.getFpEU().getRegulatedUserDsc().length() > 0) {
                                fpEUCompanyDesc = peu.getFpEU().getRegulatedUserDsc();
                            }
                            if (peu.getFpEU().getCompanyId() != null
                                && peu.getFpEU().getCompanyId().length() > 0) {
                                fpEUCompanyID = peu.getFpEU().getCompanyId();
                            }
                            if (peu.getFpEU().getEuInstallDate() != null) {
                                fpEUBeginInstall = df.format(peu.getFpEU().getEuInstallDate());
                            }
                            if (peu.getFpEU().getEuInitInstallDate() != null) {
                                fpEUCompleteInstall = df.format(peu.getFpEU().getEuInitInstallDate());
                            }
                            if (peu.getFpEU().getEuStartupDate() != null) {
                                fpEUCommenceOper = df.format(peu.getFpEU().getEuStartupDate());
                            }
                        }
                        if (peu.getDapcDescription() != null 
                            && peu.getDapcDescription().length() > 0) {
                            aqdDescription = peu.getDapcDescription();
                        }
                        if (peu.getCompanyId() != null
                            && peu.getCompanyId().length() > 0) {
                            companyID = peu.getCompanyId();
                        }
                        if (peu.getSupersededPermitNbr() != null 
                            && peu.getSupersededPermitNbr().length() > 0) {
                            supersededPermitNbr = peu.getSupersededPermitNbr();
                        }
                        if (permitNumber != null && permitNumber.length() > 1) {
                            euPermitNumber = permitNumber;
                        }
                        
                        if (peu.getPermitNumber() != null) {
                            euPermitNumber = peu.getPermitNumber();
                        }

                        if (permitEffectiveDate != null && permitEffectiveDate.length() > 1) {
                            euPermitEffectiveDate = permitEffectiveDate;
                        }
                        else if (peu.getEffectiveDate() != null) {
                            cal = Calendar.getInstance();
                            cal.setTimeInMillis(peu.getEffectiveDate().getTime());
                            euPermitEffectiveDate 
                                = Integer.toString(cal.get(Calendar.MONTH) + 1)
                                + "/"
                                + Integer.toString(cal.get(Calendar.DAY_OF_MONTH))
                                + "/" + Integer.toString(cal.get(Calendar.YEAR));
                        }

                        euBean.getProperties().put("euID", euID);
                        euBean.getProperties().put("aqdDescription", aqdDescription);
                        euBean.getProperties().put("companyID", companyID);
                        euBean.getProperties().put("supersededPermitNbr", supersededPermitNbr);
                        euBean.getProperties().put("euPermitNumber", euPermitNumber);
                        euBean.getProperties().put("euPermitEffectiveDate", euPermitEffectiveDate);
                        euBean.getProperties().put("permitExpirationDate", permitExpirationDate);
                        euBean.getProperties().put("fpEUCompanyDesc", fpEUCompanyDesc);
                        euBean.getProperties().put("fpEUCompanyID", fpEUCompanyID);
                        euBean.getProperties().put("fpEUCompleteInstall", fpEUCompleteInstall);
                        euBean.getProperties().put("fpEUBeginInstall", fpEUBeginInstall);
                        euBean.getProperties().put("fpEUCommenceOper", fpEUCommenceOper);

                        eus.add(euBean);
                    }
                    if (peug.isIndividualEUGroup()) {
                        getChildCollections().put("eus", eus);
                    }
                    else {
                        euGroupBean.getChildCollections().put("eus", eus);
                        String euGroupName = " ";
                        if (peug.getName() != null && peug.getName().length() > 0) {
                            euGroupName = peug.getName();
                        }
                        euGroupBean.getProperties().put("euGroupName", euGroupName);
                        euGroupBeans.add(euGroupBean);
                    }
                }
                getChildCollections().put("euGroups", euGroupBeans);

                PermitIssuance di = permit.getDraftIssuance();
                if (di != null){
                    if (di.getPublicNoticePublishDate() != null) {
                        noticePublishDate = df.format(di.getPublicNoticePublishDate());
                    }
                    if (di.getHearingNoticePublishDate() != null) {
                        hearingNoticePublishDate = df.format(di.getHearingNoticePublishDate());
                    }
                    if (di.getHearingDate() != null) {
                        hearingDate = df.format(di.getHearingDate());
                    }
                    if (di.getPublicCommentEndDate() != null) {
                        commentPeriodEndDate = df.format(di.getPublicCommentEndDate());
                    }
                }
            }
            else {
                throw new DocumentGenerationException("Permit parameter is null. ");
            }

            getProperties().put("permitNumber", permitNumber);
            getProperties().put("permitType", permitType);
            getProperties().put("permitDescription", permitDescription);
            getProperties().put("permitReasons", permitReasons);
            getProperties().put("noticePublishDate", noticePublishDate);
            getProperties().put("ppPublishDate", ppPublishDate);
            getProperties().put("finalIssueDate", finalIssueDate);
            getProperties().put("permitEffectiveDate", permitEffectiveDate);
            getProperties().put("permitExpirationDate", permitExpirationDate);
            getProperties().put("tvAppDueDate", tvAppDueDate);
            getProperties().put("permitInitialFee", permitInitialFee);
            getProperties().put("permitFinalFee",permitFinalFee);
            getProperties().put("permitTotalFee", permitTotalFee);
            getProperties().put("applicationNumbers", applicationNumbers);
            getProperties().put("permitActionType", permitActionType);
            
            getProperties().put("receivedComments", receivedComments);
            getProperties().put("newspaperName", newspaperName);
            
            getProperties().put("permitDemonstrationRequired", permitDemonstrationRequired);
            getProperties().put("permitModelingRequired", permitModelingRequired);
            getProperties().put("subjectToPsd", subjectToPsd);
            getProperties().put("subjectToNANSR", subjectToNANSR);

            getProperties().put("permitReceiptLetter", permitReceiptLetter);
            getProperties().put("checkFacilityInventoryPTETable", checkFacilityInventoryPTETable);
            getProperties().put("publicNoticeNeeded", publicNoticeNeeded);
            getProperties().put("hearingRequested", hearingRequested);
            getProperties().put("part61NESHAP", part61NESHAP);
            getProperties().put("part63NESHAP", part63NESHAP);
            getProperties().put("noMajorGHG", noMajorGHG);
            getProperties().put("syntheticMinor", syntheticMinor);
            getProperties().put("cems", cems);
            getProperties().put("netting", netting);
            getProperties().put("majorNonAttainment", majorNonAttainment);
            
            getProperties().put("modelingSubmitted", modelingSubmitted);
            getProperties().put("mact", mact);
            getProperties().put("nsps", nsps);
            getProperties().put("majorGHG", majorGHG);
            getProperties().put("neshaps", neshaps);

            getProperties().put("noticePublishDate", noticePublishDate);
            getProperties().put("hearingNoticePublishDate", hearingNoticePublishDate);
            getProperties().put("hearingDate", hearingDate);
            
            getProperties().put("commentPeriodEndDate", commentPeriodEndDate);

        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DocumentGenerationException("setPermit(Permit) failed: " 
                    + e.getMessage(), e);
        }
    }

    public void setInvoice(Facility facility, Invoice invoice) throws DocumentGenerationException{

        String billingContact = "";
        String billingAddrLine1 = "";
        String billingAddrLine2 = "";
        String billingAddrLine3 = "";
        String invoiceDate = "";
        String revenueId = "";
        String revenueType = "";		
        String totalTons ="";
        String balanceDue = "";
        String amountDue  = "";

        if(invoice != null) {
            // put placeholder contact in invoice until the real
            // contact is determined.  That way, if this routine throws
            // an exception, the only time the contact is null
            // is when it really is.
            invoice.setContact(new Contact());
            if(invoice.getPermitId() != null) { // Get permitting billing address (current)
                invoice.setContact(null); // Get contact to be used with permit (current billing)   
                ContactUtil activeConU =
                    facility.getActiveContact(ContactTypeDef.BILL, null);
                if(activeConU != null) {
                    invoice.setContact(activeConU.getContact());
                }
            }

            EmissionsReport report = null;
            if (invoice.getEmissionsRptId() != null) { // Get reporting billing address
                try {
                    report = getEmissionsReportService().retrieveEmissionsReport(invoice.getEmissionsRptId(), false);
                } catch (Exception ex) {
                    String s = "Failed to read emissions inventory." + invoice.getEmissionsRptId();
                    logger.error(s, ex);
                    throw new DocumentGenerationException(s + ". " + ex.getClass() + ", " + ex.getMessage());
                }

                EmissionsReport tvSmtvRpt = null;
                NtvReport ntvReport = null;
                tvSmtvRpt = report;

                //  existingPurchaseOwner flag indicates whether the
                // billing contact in the report is just for this
                // invoice.
                boolean existingPurchaseOwner;
                existingPurchaseOwner = true;
                // now, no other problems can occur so set contact to null--to see if it gets set
                invoice.setContact(null);
                if(!existingPurchaseOwner) {
                    invoice.setContact(report.getBillingAddr());
                } 
                if(invoice.getContact() == null) { // get correct contact from facility
                    ContactUtil activeConU = null;
                    try {
                        activeConU = locateInvoiceContact(facility, invoice, tvSmtvRpt, ntvReport);
                    } catch(RemoteException re) {
                        String s = "Failed will looking for contact in facility  for report " + invoice.getEmissionsRptId();
                        logger.error(s, re);
                        throw new DocumentGenerationException(s + ", " + re.getMessage());
                    }
                    if(activeConU != null) {
                        invoice.setContact(activeConU.getContact());
                    } else {
                        // No contact, fail the operation
                        invoice.setContact(null);
                        throw new DocumentGenerationException("No billing contact for invoice");
                    }
                }
            }

            try{				
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                NumberFormat nf = NumberFormat.getInstance(Locale.US);
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);

                if (invoice.getContact().getFirstNm() != null
                        && invoice.getContact().getFirstNm().length() > 0) {
                    billingContact = invoice.getContact().getFirstNm();
                }
                if (invoice.getContact().getMiddleNm() != null
                        && invoice.getContact().getMiddleNm().length() > 0) {
                    billingContact = billingContact + " "
                    + invoice.getContact().getMiddleNm();
                }
                if (invoice.getContact().getLastNm() != null
                        && invoice.getContact().getLastNm().length() > 0) {
                    billingContact = billingContact + " "
                    + invoice.getContact().getLastNm();
                }
                if (invoice.getContact().getAddress().getAddressLine1() != null
                        && invoice.getContact().getAddress().getAddressLine1().length() > 0) {
                    billingAddrLine1 = invoice.getContact().getAddress().getAddressLine1();
                }
                if (invoice.getContact().getAddress().getAddressLine2() != null
                        && invoice.getContact().getAddress().getAddressLine2().length() > 0) {
                    billingAddrLine2 = invoice.getContact().getAddress().getAddressLine2();
                }
                if (invoice.getContact().getAddress().getCityName() != null
                        && invoice.getContact().getAddress().getCityName().length() > 0) {
                    billingAddrLine3 = invoice.getContact().getAddress().getCityName();
                }
                if (invoice.getContact().getAddress().getState() != null
                        && invoice.getContact().getAddress().getState().length() > 0) {
                    billingAddrLine3 = billingAddrLine3 + ", "
                    + invoice.getContact().getAddress().getState();
                }
                if (invoice.getContact().getAddress().getZipCode5() != null
                        && invoice.getContact().getAddress().getZipCode5().length() > 0) {
                    billingAddrLine3 = billingAddrLine3 + " "
                    + invoice.getContact().getAddress().getZipCode5();
                }
                if (invoice.getContact().getAddress().getZipCode4() != null
                        && invoice.getContact().getAddress().getZipCode4().length() > 0) {
                    billingAddrLine3 = billingAddrLine3 + "-"
                    + invoice.getContact().getAddress().getZipCode4();
                }
                if(invoice.getPermitId() != null){ 
                    //Revenue Effective date = invoice creation date = Permit Issuance date 
                    invoiceDate = df.format(invoice.getCreationDate());
                }
                if(invoice.getEmissionsRptId() != null){
                    //Due Date field in db is only a placeholder for adjustment date on which an adjusted invoice is posted and 
                    //this date will be used as Invoice date for printed invoice. This applies only to adjusted invoice.
                    if(invoice.getDueDate() != null){        		
                        invoiceDate = df.format(invoice.getDueDate());
                    }
                    else if(invoice.getRevenue() != null && invoice.getRevenue().getEffectiveDate() != null){
                        invoiceDate = df.format(invoice.getRevenue().getEffectiveDate().getTimeInMillis());
                    }    
                }
                if (invoice.getRevenueId() != null) {
                    revenueId = invoice.getRevenueId().toString();
                }
                if (invoice.getRevenueTypeCd() != null
                        && invoice.getRevenueTypeCd().length() > 0) {
                    revenueType = invoice.getRevenueTypeCd();
                }           
                if(invoice.getRevenue() != null && invoice.getRevenue().getBalanceDue() != null){
                    balanceDue = invoice.getRevenue().getBalanceDue().toString();
                }else if(invoice.getPermitId() != null){
                    //see infrastructureBO.preparePostToRevenue for more details.
                    balanceDue = "$" + nf.format(invoice.getOrigAmount());//String.valueOf(Math.round(
                }
                if(invoice.getOrigAmount() != null){
                    amountDue = "$" + nf.format(invoice.getOrigAmount());//String.valueOf(Math.round
                }

                ArrayList<DocumentGenerationBean> invDetail = new ArrayList<DocumentGenerationBean>();	

                Float total = 0.0f;
                for(InvoiceDetail d : invoice.getInvoiceDetails()){
                    DocumentGenerationBean dtl = new DocumentGenerationBean();

                    String description = " ";
                    String tons = " ";					

                    description = d.getPrintableDesc();                   
                    //tons = nf.format(d.getAmount());
                    tons = d.getAmount().toString();
                    total += d.getAmount();

                    dtl.getProperties().put("pollutant", description);
                    dtl.getProperties().put("tons", tons);
                    invDetail.add(dtl);
                }
                //totalTons = nf.format(total);
                totalTons = total.toString();

                getChildCollections().put("emissions", invDetail);
            }
            catch(Exception e){
                logger.error(e.getMessage(), e);
                throw new DocumentGenerationException("setInvoice(Invoice) failed: "
                        + e.getMessage(), e);
            }
        }  else{
            throw new DocumentGenerationException("Invoice parameter is null");
        }

        getProperties().put("invBillingName", billingContact);
        getProperties().put("invBillingAddrLine1", billingAddrLine1);
        getProperties().put("invBillingAddrLine2", billingAddrLine2);
        getProperties().put("invBillingAddrLine3", billingAddrLine3);
        getProperties().put("invoiceDate", invoiceDate);
        getProperties().put("revenueID", revenueId);
        getProperties().put("balanceDue", balanceDue);
        getProperties().put("amountDue", amountDue);
        getProperties().put("revenueType", revenueType);
        getProperties().put("totalTons", totalTons);	
    }

    protected ContactUtil locateInvoiceContact(Facility facility, Invoice invoice, EmissionsReport tvSmtvRpt, NtvReport ntvReport)
    throws DAOException {

        ContactUtil activeConU = null;
//        Facility facility = new Facility();  TODO Not needed because facility passed.
//        facility.setFacilityId(invoice.getFacilityId());
//        // need facility shutdown date
//        try {
//            facility = getFacilityService().retrieveFacilityTable(facility.getFacilityId());
//            // need facility contacts
//            facility.setAllContacts(
//                    getFacilityService().retrieveFacilityContacts(
//                            facility.getFacilityId()));
//        } catch(RemoteException re) {
//            throw new DAOException("operation failed", re);
//        }
        Calendar cal = Calendar.getInstance();
        Timestamp anchor = null;
        if(ntvReport != null) {
            ntvReport.determineReportable(facility);
            anchor = ntvReport.getAnchor();
            if(ntvReport.getPrimary().getTransferDate() != null) {
                if(ntvReport.getPrimary().isNewOwner()) {  // is new owner
                    anchor = ntvReport.getPrimary().getTransferDate();
                } else {
                    // need one day earlier
                    Calendar calA = Calendar.getInstance();
                    Date date = new Date(ntvReport.getPrimary().getTransferDate().getTime());
                    calA.setTime(date);
                    calA.add(Calendar.HOUR_OF_DAY, -24);
                    calA.set(Calendar.HOUR_OF_DAY, 23);
                    calA.set(Calendar.MINUTE, 59);
                    calA.set(Calendar.SECOND, 59);
                    calA.set(Calendar.MILLISECOND, 0);
                    anchor = new Timestamp(calA.getTimeInMillis());
                }
            }
        } else if(tvSmtvRpt != null) {
            cal.set(tvSmtvRpt.getReportYear(), Calendar.DECEMBER, 31, 23, 59, 59);
            cal.set(Calendar.MILLISECOND, 999);
            anchor = new Timestamp(cal.getTimeInMillis());
            anchor.setNanos(0);
        } else {
            // this is a permit
            invoice.setContact(null);
            String errMsg = null;
            activeConU =
                facility.getActiveContact(ContactTypeDef.BILL, null);
            if(activeConU != null) {
                invoice.setContact(activeConU.getContact());
            } else {
                invoice.setAllowOperations(false);
                errMsg = "No current billing contact.";
                if(invoice.getPrevInvoiceFailureMsg() != null) {
                    invoice.setPrevInvoiceFailureMsg(invoice.getPrevInvoiceFailureMsg() +
                            "<br><br>" + errMsg);
                } else {
                    invoice.setPrevInvoiceFailureMsg(errMsg);
                }
            } 
            return activeConU;
        }
        // continue with invoice for emission reporting
        invoice.setContact(null);
        String errMsg = null;
        // Is there an owner at the end of the reporting period?
        /*ContactUtil ownerAt = facility.getActiveContact(ContactTypeDef.OWNR, anchor);
        if(ownerAt != null) {
            Timestamp contactRefDate = facility.latestOwnerRefDate(anchor);
            activeConU =
                facility.getActiveContact(ContactTypeDef.BILL, contactRefDate);
            if(activeConU != null) {
                invoice.setContact(activeConU.getContact());
            } else {
                invoice.setAllowOperations(false);
                if(contactRefDate != null) {
                    errMsg = "No billing contact active on date " +
                    ContactUtil.datePrtFormat(contactRefDate)
                    + " (which is the last date this owner owned the facility).";
                } else {
                    errMsg = "No current billing contact.";
                }
            } 
        } else {*/
            // There is no owner
            invoice.setAllowOperations(false);
            errMsg = "There is no owner on date " + ContactUtil.datePrtFormat(anchor)
                + ".";
       // }
        if(errMsg != null) {
            if(invoice.getPrevInvoiceFailureMsg() != null) {
                invoice.setPrevInvoiceFailureMsg(invoice.getPrevInvoiceFailureMsg() +
                        "<br><br>" + errMsg);
            } else {
                invoice.setPrevInvoiceFailureMsg(errMsg);
            }
        }
        return activeConU;
    }
    
    public void setApplication(Application application) throws DocumentGenerationException {

        if (application == null) {
            throw new DocumentGenerationException("Application parameter is missing.");
        }

        String appNumber = "";
        String appReceivedDate = "";
        String appType = "";
        String appDescription = "";

        // PBR specific fields.
        String pbrType = "";

        // RPR specific fields.
        String revokeEntirePermit = "";
        String basisForPermitRevoke = "";

        // RPE specific fields.
        String terminationDate = "";
        
        // ITR specific fields.
        String itrFutureAddress="";
        String itrTargetCounty="";
        String itrSpecialText="";
        
        String itrNewspaperName="";
        String itrNewspaperContact="";
        String itrNewspaperFax="";
        String itrNewspaperPhone="";
        String itrNewspaperEmail="";
        
        //DOR specific fields
        String dorFromFirstName="";
        String dorFromLastName="";
        String dorFromTitle="";
        String dorFromAddress1="";
        String dorFromAddress2="";
        String dorFromCity="";
        String dorFromState="";
        String dorFromZip="";
        
        String dorToFirstName="";
        String dorToLastName="";
        String dorToTitle="";
        String dorToAddress1="";
        String dorToAddress2="";
        String dorToCity="";
        String dorToState="";
        String dorToZip="";
        
        // Fields for Title V App Technical Incomplete 2nd Warning (not logged)
        String responsibleOfficialName = "";
        String respOffAddrLine1 = "";
        String respOffAddrLine2 = "";
        String respOffAddrLine3 = "";
        
        String AQDEUID = "";
        String AQDEUDescription = "";
        String CriteriaPollutant="";
        String CriteriaPTE="";
        
        String contactTitle="";
        String contactFName="";
        String contactLName="";
        String contactJobTitle="";
        String contactAdd1="";
        String contactCity="";
        String contactState="";
        String contactZip="";
        String contactCounty="";
        String contactPhone = "";
        String contactEmail = "";
        String contactEmail2 = "";
        String contactCompany = "";
        String reasonCode;
        String contactTitleDesc="";
        

        try {
            appNumber = application.getApplicationNumber();
            
            
            /*List<ApplicationEU>  appEuList= application.getEus();
		    Iterator<ApplicationEU> appEuGen=  appEuList.iterator();
		    //Set setFpEu=null;
		    while (appEuGen.hasNext()) {
		    				  
		    	ApplicationEU appEU = appEuGen.next();
			    System.out.println("Facility EU Document Properties "+appEU);
				//setFpEu = dBean.getProperties().keySet();
				//Iterator iterFpEu = setFpEu.iterator();
		    }*/
            logger.debug("application.getApplicationTypeCD(): "+application.getApplicationTypeCD());
            if(application instanceof PTIOApplication){
            	
            	if(application.getContact()!=null){
            	
		            contactTitle = application.getContact().getTitleCd();
		            
		            if(contactTitle!=null)
		            contactTitleDesc=ContactTitle.getData().getItems().getItemDesc(contactTitle);
		            
		            if(application.getContact().getFirstNm()!=null){    
		            	contactFName = application.getContact().getFirstNm();
		            }
		            if(application.getContact().getLastNm()!=null){
		            	contactLName = application.getContact().getLastNm();
		            }
		            if(application.getContact().getCompanyTitle()!=null){
		            	contactJobTitle = application.getContact().getCompanyTitle();
		            }
		            if(application.getContact().getAddress().getAddressLine1()!=null){
		            	contactAdd1 = application.getContact().getAddress().getAddressLine1();
		            }
		            if(application.getContact().getAddress().getCityName()!=null)
		            {
		            	contactCity = application.getContact().getAddress().getCityName();
		            }
		            if(application.getContact().getAddress().getState()!=null){
		            	contactState = application.getContact().getAddress().getState();
		            }
		            if(application.getContact().getAddress().getZipCode()!=null){
		            	contactZip = application.getContact().getAddress().getZipCode();
		            }
		            if(application.getContact().getAddress().getCountyCd()!=null){
		            	contactCounty = application.getContact().getAddress().getCountyCd();
		            }
		            if(application.getContact().getPhoneNo()!=null){
		            	contactPhone = application.getContact().getPhoneNo();
		            }
					if (application.getContact().getEmailAddressTxt() != null) {
						contactEmail = application.getContact().getEmailAddressTxt();
					}
					if (application.getContact().getEmailAddressTxt2() != null) {
						contactEmail2 = application.getContact().getEmailAddressTxt2();
					}
		            if(application.getContact().getCompanyName()!=null){
		            	contactCompany = application.getContact().getCompanyName();
		            }
            	}
            }    
		    
            
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            logger.debug("About to get received date");
            Timestamp receivedDate = application.getReceivedDate();
            if (receivedDate == null) {
                receivedDate = new Timestamp(System.currentTimeMillis());
            }
            appReceivedDate = df.format(receivedDate);
            logger.debug("Got received date");
            appType 
                = ApplicationTypeDef.getData().getItems().getItemDesc(application.getApplicationTypeCD());
            appDescription = application.getApplicationDesc();
            
            if (application instanceof PBRNotification) {
                pbrType 
                    = PBRTypeDef.getData().getItems().getItemDesc(((PBRNotification) application).getPbrTypeCd());
            }
            else if (application instanceof RPRRequest) {
                if (((RPRRequest) application).isRevokeEntirePermit()) {
                    revokeEntirePermit = "Yes";
                }
                else {
                    revokeEntirePermit = "No";
                }
                basisForPermitRevoke = ((RPRRequest) application).getBasisForRevocation();
            }
            else if (application instanceof RPERequest) {
                terminationDate = df.format(((RPERequest) application).getTerminationDate());
            } else if (application instanceof RelocateRequest) {
                itrFutureAddress = ((RelocateRequest)application).getFutureAddress();
                itrSpecialText = ((RelocateRequest)application).getSpecialText();
                String itrTargetCountyCd = ((RelocateRequest)application).getTargetCountyCd();
                CountyDef cd = getInfrastructureService().retrieveCounty(itrTargetCountyCd);
                if (cd != null) {
                    if (cd.getCountyNm() != null
                        && cd.getCountyNm().length() > 0) {
                        itrTargetCounty = cd.getCountyNm();
                    }
                }
                
//                NewspaperDef newspaper = getInfrastructureService().retrieveNewspaper(itrTargetCountyCd);
//                if (newspaper != null) {
//                    if (newspaper.getNewspaperName() != null
//                        && newspaper.getNewspaperName().length() > 0) {
//                        itrNewspaperName = newspaper.getNewspaperName();
//                    }
//                    if (newspaper.getContactPerson() != null
//                        && newspaper.getContactPerson().length() > 0) {
//                        itrNewspaperContact = newspaper.getContactPerson();
//                    }
//                    if (newspaper.getFaxNumber() != null
//                        && newspaper.getFaxNumber().length() > 0) {
//                        itrNewspaperFax = newspaper.getFaxNumber();
//                    }
//                    if (newspaper.getPhoneNumber() != null
//                        && newspaper.getPhoneNumber().length() > 0) {
//                        itrNewspaperPhone = phoneConvert.tryFormatPhoneNumber(newspaper.getPhoneNumber());
//                    }
//                    if (newspaper.getEmailAddress() != null
//                        && newspaper.getEmailAddress().length() > 0) {
//                        itrNewspaperEmail = newspaper.getEmailAddress();
//                    }
//                }
            } else if (application instanceof DelegationRequest) {
                logger.debug("Assigning NVP vals");
                dorFromFirstName = ((DelegationRequest)application).getOrigFirstName();
                dorFromLastName = ((DelegationRequest)application).getOrigLastName();
                dorFromTitle =  ((DelegationRequest)application).getOrigTitle();
                dorFromAddress1 = ((DelegationRequest)application).getOrigAddress1();
                dorFromAddress2 = ((DelegationRequest)application).getOrigAddress2();
                dorFromCity = ((DelegationRequest)application).getOrigCity();
                dorFromState = ((DelegationRequest)application).getOrigStateCd();
                dorFromZip = ((DelegationRequest)application).getOrigZip();
                
                dorToFirstName = ((DelegationRequest)application).getAssigFirstName();
                dorToLastName = ((DelegationRequest)application).getAssigLastName();
                dorToTitle =  ((DelegationRequest)application).getAssigTitle();
                dorToAddress1 = ((DelegationRequest)application).getAssigAddress1();
                dorToAddress2 = ((DelegationRequest)application).getAssigAddress2();
                dorToCity = ((DelegationRequest)application).getAssigCity();
                dorToState = ((DelegationRequest)application).getAssigStateCd();
                dorToZip = ((DelegationRequest)application).getAssigZip();
            } else if (application instanceof TVApplication) {
            	Facility facility = application.getFacility();
            	if (facility != null) {
            		Contact respOfficial = null;
            		List<Contact> contacts = getFacilityService().retrieveFacilityContacts(facility.getFacilityId(), false);
            		for (Contact contact : contacts) {
            			if (contact.getEndDate() == null) {
            				for (ContactType contactType : contact.getContactTypes()) {
            					if (contactType.getEndDate() == null && 
            							"rpof".equals(contactType.getContactTypeCd())) {
            						respOfficial = contact;
            						break;
            					}
            				}
            			}
            		}
            		if (respOfficial != null) {

            	        /* 
            	         * 
            	         * 	responsibleOfficialName = First Name+[space]+Last Name
            				respOffAddrLine1 = Address Line 1
            				respOffAddrLine2 = Address Line 2
            				respOffAddrLine2 = City+[,]+[space]+State+[space]+[space]+Zip
            			*/
            			if (respOfficial != null) {
	            			if (respOfficial.getFirstNm() != null &&
	            					respOfficial.getLastNm() != null) {
	            				responsibleOfficialName = respOfficial.getFirstNm() + " " +
	            					respOfficial.getLastNm();
	            			}
	            			if (respOfficial.getAddress() != null) {
	            				if (respOfficial.getAddress().getAddressLine1() != null) {
	            					respOffAddrLine1 = respOfficial.getAddress().getAddressLine1();
	            				}
	            				if (respOfficial.getAddress().getAddressLine2() != null) {
	            					respOffAddrLine2 = respOfficial.getAddress().getAddressLine2();
	            				}
	            				if (respOfficial.getAddress().getCityName() != null) {
	            					respOffAddrLine3 = respOfficial.getAddress().getCityName() + ", ";
	            				}
	            				if (respOfficial.getAddress().getState() != null) {
	            					respOffAddrLine3 += respOfficial.getAddress().getState() + " ";
	            				}
	            				if (respOfficial.getAddress().getZipCode() != null) {
	            					respOffAddrLine3 += respOfficial.getAddress().getZipCode();
	            				}
	            			}
            			}
            		}
            	}
            }
            
            
            
            getProperties().put("appNumber", appNumber);
            getProperties().put("appReceivedDate", appReceivedDate);
            getProperties().put("appType", appType);
            getProperties().put("appDescription", appDescription);
            
            getProperties().put("pbrType", pbrType);
            
            getProperties().put("itrFutureAddress", itrFutureAddress);
            getProperties().put("itrTargetCounty", itrTargetCounty);
            getProperties().put("itrNewspaperName", itrNewspaperName);
            getProperties().put("itrNewspaperContact", itrNewspaperContact);
            getProperties().put("itrNewspaperFax", itrNewspaperFax);
            getProperties().put("itrNewspaperPhone", itrNewspaperPhone);
            getProperties().put("itrNewspaperEmail", itrNewspaperEmail);
            getProperties().put("itrSpecialText", itrSpecialText);
            
            getProperties().put("dorRequesterName",dorFromFirstName + " " + dorFromLastName);
            getProperties().put("dorRequesterAddrLine1",dorFromAddress1);
            getProperties().put("dorRequesterAddrLine2",dorFromAddress2);
            getProperties().put("dorRequesterAddrLine3",dorFromCity + ", " + dorFromState + " " + dorFromZip);
            getProperties().put("dorRequesterZip5", dorFromZip);
            getProperties().put("dorRequesterTitle",dorFromTitle);
            
            getProperties().put("dorDelegateeName",dorToFirstName + " " + dorToLastName);
            getProperties().put("dorDelegateeAddrLine1",dorToAddress1);
            getProperties().put("dorDelegateeAddrLine2",dorToAddress2);
            getProperties().put("dorDelegateeAddrLine3",dorToCity + ", " + dorToState + " " + dorToZip);
            getProperties().put("dorDelegateeZip5", dorToZip);
            getProperties().put("dorDelegateeTitle", dorToTitle);

            
            getProperties().put("revokeEntirePermit", revokeEntirePermit);
            getProperties().put("basisForPermitRevoke", basisForPermitRevoke);

            getProperties().put("terminationDate", terminationDate);

            getProperties().put("responsibleOfficialName", responsibleOfficialName);
            getProperties().put("respOffAddrLine1", respOffAddrLine1);
            getProperties().put("respOffAddrLine2", respOffAddrLine2);
            getProperties().put("respOffAddrLine3", respOffAddrLine3);
            
            getProperties().put("contactTitle", contactTitleDesc);
            getProperties().put("contactFName", contactFName);
            getProperties().put("contactLName", contactLName);
            Logger.fine("contactJobTitle: "+contactJobTitle.length());
            getProperties().put("contactJobTitle", contactJobTitle);
            getProperties().put("contactAdd1", contactAdd1);
            getProperties().put("contactCity", contactCity);
            getProperties().put("contactState", contactState);
            getProperties().put("contactZip", contactZip);
            getProperties().put("contactCounty", contactCounty);
            getProperties().put("contactCompany", contactCompany);
            getProperties().put("contactPhone", contactPhone);
            getProperties().put("contactEmail", contactEmail);
            getProperties().put("contactEmailSecondary", contactEmail2);
          
            
            ArrayList<DocumentGenerationBean> eus = new ArrayList<DocumentGenerationBean>();
            
            List<ApplicationEU> euList = application.getIncludedEus();
            for (ApplicationEU aeu : euList) {
                
                DocumentGenerationBean euBean = new DocumentGenerationBean();

                // ...fetch the facility emission unit's data.
                String euID = "";
                String fpEUCompanyDesc = "";
                String fpEUCompanyID = "";
                String fpEUBeginInstall = "";
                String fpEUCompleteInstall = "";
                String fpEUCommenceOper = "";
                String basisForEURevoke = "";
                
                if (aeu.getEuText() != null) {
                    basisForEURevoke = aeu.getEuText();
                }

                EmissionUnit fpeu = aeu.getFpEU();
                if (fpeu != null) {
                    euID = fpeu.getEpaEmuId();
                    
                    if (fpeu.getRegulatedUserDsc() != null 
                        && fpeu.getRegulatedUserDsc().length() > 0) {
                        fpEUCompanyDesc = fpeu.getRegulatedUserDsc();
                    }
                    if (fpeu.getCompanyId() != null
                        && fpeu.getCompanyId().length() > 0) {
                        fpEUCompanyID = fpeu.getCompanyId();
                    }
                    if (fpeu.getEuInstallDate() != null) {
                        fpEUBeginInstall = df.format(fpeu.getEuInstallDate());
                    }
                    if (fpeu.getEuInitInstallDate() != null) {
                        fpEUCompleteInstall = df.format(fpeu.getEuInitInstallDate());
                    }
                    if (fpeu.getEuStartupDate() != null) {
                        fpEUCommenceOper = df.format(fpeu.getEuStartupDate());
                    }
                }
                
                euBean.getProperties().put("euID", euID);
                euBean.getProperties().put("fpEUCompanyDesc", fpEUCompanyDesc);
                euBean.getProperties().put("fpEUCompanyID", fpEUCompanyID);
                euBean.getProperties().put("fpEUCompleteInstall", fpEUCompleteInstall);
                euBean.getProperties().put("fpEUBeginInstall", fpEUBeginInstall);
                euBean.getProperties().put("fpEUCommenceOper", fpEUCommenceOper);
                euBean.getProperties().put("basisForEURevoke", basisForEURevoke);
                
                eus.add(euBean);
            }
            getChildCollections().put("appIncludedEUs", eus);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DocumentGenerationException("setApplication(Application) failed: "
                                                  + e.getMessage(), e);
        }
    }
    
    public void setGenericIssuance(GenericIssuance issuance)
        throws DocumentGenerationException {

        if (issuance == null) {
            throw new DocumentGenerationException("GenericIssuance parameter is missing.");
        }

        String genericIssuanceDate = " ";
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        if (issuance.getIssuanceDate() != null) {
            genericIssuanceDate = df.format(issuance.getIssuanceDate());
        }

        getProperties().put("genericIssuanceDate", genericIssuanceDate);
    }

    public void setEmissionsReport(EmissionsReport emissionsReport)
        throws DocumentGenerationException {

        String eiNumber = "";
        String eiReceivedDate = "";
        String eiType = "";
        String eiCategory = "";
        String reportingYear = "";
        String pricePerTon = "";
        String emissionRange = "";
        
        try {
            if (emissionsReport != null) {

                if (emissionsReport.getEmissionsRptId() != null) {
                    eiNumber = emissionsReport.getEmissionsInventoryId();
                }

                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                if (emissionsReport.getReceiveDate() != null) {
                    eiReceivedDate = df.format(emissionsReport.getReceiveDate());
                }

                eiType = "Emissions Inventory";


                if(emissionsReport.getReportYear() != null){
                    reportingYear = emissionsReport.getReportYear().toString();
                }

                NumberFormat nf = NumberFormat.getInstance(Locale.US);
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
                
                Fee fee = null;
                if(emissionsReport.getFeeId() != null){
                	fee = getInfrastructureService().retrieveFee(emissionsReport.getFeeId());
                   
                	if(fee != null && fee.getAmount() != null){
                		pricePerTon = nf.format(fee.getAmount());
                	}
                }
                
                ArrayList<DocumentGenerationBean> ntvEmissionDtl = new ArrayList<DocumentGenerationBean>();

                getChildCollections().put("ntvEmissions", ntvEmissionDtl);             
                
            } else {
                throw new DocumentGenerationException("EmissionsReport parameter is null.");
            }

            getProperties().put("eiNumber", eiNumber);
            getProperties().put("eiReceivedDate", eiReceivedDate);
            getProperties().put("eiType", eiType);
            getProperties().put("eiCategory", eiCategory);
            getProperties().put("reportingYear", reportingYear);
            getProperties().put("pricePerTon", pricePerTon);
            getProperties().put("emissionRange", emissionRange);                    
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DocumentGenerationException("setEmissionsReport(EmissionsReport) failed: "
                                                  + e.getMessage(), e);
        }
        
    }
    
    public void setInspection(FullComplianceEval inspection) throws DocumentGenerationException {
    	if (inspection == null) {
            throw new DocumentGenerationException("Inspection parameter is missing.");
        }

    	String inspectorName= "";
    	String assignedStaff= "";
    	String scheduledTimestamp= "";
    	String dateEvaluated="";
    	String createdDt="";
    	String createdById="";
   	    String inspId="";
    	String dateSentToEPA="";
    	String inspectionType="";
    	

    	

    	try {
    		if(inspection.getEvaluator() != null) {
    			UserDef reviewer 
    			= getInfrastructureService().retrieveUserDef(inspection.getEvaluator());
    			if (reviewer != null) {
                    String inspectorFirstName = reviewer.getUserFirstNm();
                    if (inspectorFirstName != null && inspectorFirstName.length() > 0) {
                    	inspectorName = inspectorFirstName + " ";
                    }
                    String inspectorLastName = reviewer.getUserLastNm();
                    if (inspectorLastName != null && inspectorLastName.length() > 0) {
                    	inspectorName = inspectorName + inspectorLastName;
                    }
                }
    		}
    		if(inspection.getAssignedStaff() != null) {
    			UserDef assigned 
    			= getInfrastructureService().retrieveUserDef(inspection.getAssignedStaff());
    			if (assigned != null) {
                    String assignedStaffFirstName = assigned.getUserFirstNm();
                    if (assignedStaffFirstName != null && assignedStaffFirstName.length() > 0) {
                    	assignedStaff = assignedStaffFirstName + " ";
                    }
                    String assignedStaffLastName = assigned.getUserLastNm();
                    if (assignedStaffLastName != null && assignedStaffLastName.length() > 0) {
                    	assignedStaff = assignedStaff + assignedStaffLastName;
                    }
                }
    		}
    		
    		if(inspection.getCreatedById() != null) {
    			UserDef createdBy 
    			= getInfrastructureService().retrieveUserDef(inspection.getCreatedById());
    			if (createdBy != null) {
                    String createdByFirstName = createdBy.getUserFirstNm();
                    if (createdByFirstName != null && createdByFirstName.length() > 0) {
                    	createdById = createdByFirstName + " ";
                    }
                    String createdByLastName = createdBy.getUserLastNm();
                    if (createdByLastName != null && createdByLastName.length() > 0) {
                    	createdById = createdById + createdByLastName;
                    }
                }
    		}
    		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    		Timestamp scheduledDate = inspection.getScheduledTimestamp();
    		if (scheduledDate == null)
    			scheduledDate = new Timestamp(System.currentTimeMillis());
    		scheduledTimestamp = df.format(scheduledDate);
    		Timestamp evaluatedDate = inspection.getDateEvaluated();
    		if (evaluatedDate == null)
    			evaluatedDate = new Timestamp(System.currentTimeMillis());
    		dateEvaluated = df.format(evaluatedDate);
    		Timestamp dateCreated = inspection.getCreatedDt();
    		if (dateCreated == null)
    			dateCreated = new Timestamp(System.currentTimeMillis());
    		createdDt = df.format(dateCreated);
    		Timestamp epaSentDate = inspection.getDateSentToEPA();
    		if (epaSentDate == null)
    			epaSentDate = new Timestamp(System.currentTimeMillis());
    		dateSentToEPA = df.format(epaSentDate);
    		inspId = inspection.getInspId();
    		
    		
    		getProperties().put("inspectorName", inspectorName);
    		getProperties().put("assignedStaff", assignedStaff);
    		getProperties().put("createdById", createdById);
    		getProperties().put("scheduledTimestamp", scheduledTimestamp);
    		getProperties().put("dateEvaluated", dateEvaluated);
    		getProperties().put("createdDt", createdDt);
    		getProperties().put("inspId", inspId);
    		getProperties().put("dateSentToEPA", dateSentToEPA);
    		

    	}
    	catch (Exception e) {
    		logger.error(e.getMessage(), e);
    		throw new DocumentGenerationException("setApplication(Application) failed: "
    				+ e.getMessage(), e);
    	}
    }
    
    public void setReportProfile(ReportProfileBase rp) throws DocumentGenerationException {
    	
    	Double totalDue = 0d;
    	
    	ArrayList<DocumentGenerationBean> poll = new ArrayList<DocumentGenerationBean>();
        
        List<EmissionsTable> etList = rp.getEmissionsInvoice().getEmissTable();
        for (EmissionsTable et : etList) {
            
            DocumentGenerationBean pollBean = new DocumentGenerationBean();
            
            // ...fetch the Pollutant emissions data.
            String pollutantCd = "";
            String pollutantDesc = "";
            Double fugitiveEmissions = 0d;
            Double stackEmissions = 0d;
            String totalPerPollutant = "";
            String firstHalfTotal = "";
            String secondHalfTotal = "";
            BigDecimal firstHalfFee = BigDecimal.ZERO;
            BigDecimal secondHalfFee = BigDecimal.ZERO;
            String totalFeeForPollutant = "";
            
            pollutantCd = et.getPollutantCd();
            pollutantDesc =  et.getPollutantDesc();
            fugitiveEmissions =  et.getFugitiveEmissions().doubleValue();
            stackEmissions = et.getStackEmissions().doubleValue();
            totalPerPollutant = et.getActualTotalTons().toPlainString();
            firstHalfTotal = et.getFirstHalfActualTotalTons().toPlainString();
            secondHalfTotal =  et.getSecondHalfActualTotalTons().toPlainString();
            firstHalfFee = et.getFirstHalfFee();
            secondHalfFee =  et.getSecondHalfFee();
            totalFeeForPollutant =  et.getTotalFeePerPollutant().toPlainString();
            
            pollBean.getProperties().put("pollutantCd", pollutantCd);
            pollBean.getProperties().put("pollutantDesc", pollutantDesc);
            if(fugitiveEmissions!=null)
            pollBean.getProperties().put("fugitiveEmissions", fugitiveEmissions.toString());
            if(stackEmissions!=null)
            pollBean.getProperties().put("stackEmissions", stackEmissions.toString());
            if(totalPerPollutant !=null)
            pollBean.getProperties().put("totalPerPollutant", totalPerPollutant.toString());
            if(firstHalfTotal !=null)
            pollBean.getProperties().put("firstHalfTotal", firstHalfTotal.toString());
            if(secondHalfTotal!=null)
            pollBean.getProperties().put("secondHalfTotal", secondHalfTotal.toString());
            if(firstHalfFee!=null)
            pollBean.getProperties().put("firstHalfFee", firstHalfFee.toString());
            if(secondHalfFee!=null)
            pollBean.getProperties().put("secondHalfFee", secondHalfFee.toString());
            if(totalFeeForPollutant!=null)
            pollBean.getProperties().put("totalFeeForPollutant", totalFeeForPollutant.toString());
            
            // include pollutant in the invoice document only if the total fee for the polluant is non-zero
            if(!totalFeeForPollutant.equalsIgnoreCase("$0.00"))
            	poll.add(pollBean);
        }
        getChildCollections().put("poll", poll);
        getProperties().put("totalFormatedFee", rp.getEmissionsInvoice().getTotalFormattedFee().toString());
    }
    
    /**
     * Document generation bean for NSR Invoice
     * @param permit
     * @param invoiceType
     * 
     * @return none
     */
    public void setNSRInvoice(Permit permit, String invoiceType) {
    	
    	Locale locale = new Locale("en", "US");
    	NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
    	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    	
    	String billingDate = "";
    	String appNumbers = "";
    	String permitNumber = "";
    	String payKey = "";
    	String vendorNumber = "";
    	
    	billingDate = sdf.format(System.currentTimeMillis());
    	appNumbers = permit.getApplicationNumbers();
    	permitNumber = permit.getPermitNumber();
    	
    	payKey = ((PTIOPermit)permit).getCompanyPayKey();
    	if(Utility.isNullOrEmpty(payKey)) {
    		payKey = "N/A";
    	}
    	
    	Long vendorNum = ((PTIOPermit)permit).getCompanyVendorNumber(); 
    	if(null != vendorNum) {
    		vendorNumber = String.format("%010d", vendorNum);
    	} else {
    		vendorNumber = "N/A";	
    	}
    	
    	// heading
    	getProperties().put("invoiceType", invoiceType);
    	getProperties().put("billingDate",billingDate);
    	getProperties().put("appNumbers", appNumbers);
    	getProperties().put("permitNumber", permitNumber);
    	getProperties().put("payKey",payKey);
    	getProperties().put("vendorNumber", vendorNumber);
    	
    }
    
    public void setContact(Contact contact) {
    	
    	PhoneNumberConverter phoneNumberConverter = new PhoneNumberConverter();
    	
    	String contactId = "";
    	String titleCd = "";
    	String firstNm = "";
    	String middleNm = "";
    	String lastNm = "";
    	String suffixCd = "";
    	String phoneNo = "";
    	String phoneExtensionVal = "";
    	String secondaryPhoneNo = "";
    	String secondaryExtensionVal = "";
    	String mobilePhoneNo = "";
    	String faxNo = "";
    	String pagerNo = "";
    	String pagerPinNo = "";
    	String emailAddressTxt = "";
    	String emailAddressTxtSecondary = "";
    	String emailPagerAddress = "";
    	String attentionName = "";
    	String addressLine1 = "";
    	String addressLine2 = "";
    	String addressLine = "";
    	String cityName = "";
    	String state = "";
    	String zipCode5 = "";
    	String zipCode4 = "";
    	String zipCode = "";
    	String countryCd = "";
    	
    	if(null != contact) {
	    	if(null != contact.getContactId()) {
	    		contactId = contact.getContactId().toString();
	    	}
	    	
	    	titleCd = contact.getTitleCd();
	    	
	    	firstNm = contact.getFirstNm();
	    	lastNm = null != contact.getLastNm() ? contact.getLastNm() : "";
	    	// if middle name and suffix are empty, then default to blank
	    	middleNm = Utility.isNullOrEmpty(contact.getMiddleNm()) ? " " : contact.getMiddleNm();
	    	suffixCd = Utility.isNullOrEmpty(contact.getSuffixCd()) ? " " : contact.getSuffixCd(); 
	    	
	    	try{
	    		if(null != contact.getPhoneNo()) {
	    			phoneNo = phoneNumberConverter.formatPhoneNumber(contact.getPhoneNo());
	    		}	
	    		if(null != contact.getSecondaryPhoneNo()) {
	    			secondaryPhoneNo = phoneNumberConverter.formatPhoneNumber(contact.getSecondaryPhoneNo());
	    		}	
	    		if(null != contact.getMobilePhoneNo()) {
	    			mobilePhoneNo = phoneNumberConverter.formatPhoneNumber(contact.getMobilePhoneNo());
	    		}
	    		if(null != contact.getFaxNo()) {
	    			faxNo = phoneNumberConverter.formatPhoneNumber(contact.getFaxNo());
	    		}	
	    		if(null != contact.getPagerNo()) {
	    			pagerNo = phoneNumberConverter.formatPhoneNumber(contact.getPagerNo());
	    		}
	    	}catch(ConverterException ce) {
	    		logger.error(ce.getMessage(), ce);
	    	}
	    	
	    	phoneExtensionVal = contact.getPhoneExtensionVal();
	    	secondaryExtensionVal = contact.getSecondaryExtensionVal();
	    	pagerPinNo = contact.getPagerPinNo();
	    	emailAddressTxt = contact.getEmailAddressTxt();
	    	emailAddressTxtSecondary = contact.getEmailAddressTxt2();
	    	emailPagerAddress = contact.getEmailPagerAddress();
	    	
	    	if(null != contact.getAddress()) {
	    		attentionName = contact.getAddress().getAttentionName();
	    		addressLine1 = contact.getAddress().getAddressLine1();
	    		addressLine2 = contact.getAddress().getAddressLine2();
	    		if(!Utility.isNullOrEmpty(addressLine2)) {
	    			addressLine = addressLine1 + ControlChar.LINE_BREAK + addressLine2;
	    		} else {
	    			addressLine = addressLine1;
	    		}
	    			
	    		cityName = contact.getAddress().getCityName();
	    		state = contact.getAddress().getState();
	    		zipCode5 = contact.getAddress().getZipCode5();
	    		zipCode4 = contact.getAddress().getZipCode4();
	    		zipCode = contact.getAddress().getZipCode();
	    		countryCd = contact.getAddress().getCountyCd();
	    	}
    	}
    	
    	getProperties().put("contactId", contactId);
    	getProperties().put("titleCd", titleCd);
    	getProperties().put("firstNm", firstNm);
    	getProperties().put("middleNm", middleNm);
    	getProperties().put("lastNm", lastNm);
    	getProperties().put("suffixCd", suffixCd);
    	getProperties().put("phoneNo", phoneNo);
    	getProperties().put("phoneExtensionVal", phoneExtensionVal);
    	getProperties().put("secondaryPhoneNo", secondaryPhoneNo);
    	getProperties().put("secondaryExtensionVal", secondaryExtensionVal);
    	getProperties().put("mobilePhoneNo", mobilePhoneNo);
    	getProperties().put("faxNo", faxNo);
    	getProperties().put("pagerNo", pagerNo);
    	getProperties().put("pagerPinNo", pagerPinNo);
    	getProperties().put("emailAddressTxt", emailAddressTxt);
    	getProperties().put("emailAddressTxtSecondary", emailAddressTxtSecondary);
    	getProperties().put("emailPagerAddress", emailPagerAddress);
    	getProperties().put("attentionName", attentionName);
    	getProperties().put("addressLine1", addressLine1);
    	getProperties().put("addressLine2", addressLine2);
   		getProperties().put("addressLine", addressLine);
    	getProperties().put("cityName", cityName);
    	getProperties().put("state", state);
    	getProperties().put("zipCode5", zipCode5);
    	getProperties().put("zipCode4", zipCode4);
    	getProperties().put("zipCode", zipCode);
    	getProperties().put("countryCd", countryCd);

    }
    
    public void setTimesheetInfo(UserDef userDef, String reportEndingDate) {
    	String positionNumber = userDef.getPositionNumberString();
    	if(null == positionNumber) {
    		positionNumber = " ";
    	}
    	getProperties().put("positionNumber", positionNumber);
    	getProperties().put("employeeName", BasicUsersDef.getUserNm(userDef.getUserId()));
    	getProperties().put("reportEndingDate", reportEndingDate);
    }

}
