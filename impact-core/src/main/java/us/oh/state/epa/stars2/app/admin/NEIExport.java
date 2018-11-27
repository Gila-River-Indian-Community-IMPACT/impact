package us.oh.state.epa.stars2.app.admin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;

import us.oh.state.epa.aport.admin.domain.User;
import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dao.DAOFactory;
import us.oh.state.epa.stars2.database.dao.DocumentDAO;
import us.oh.state.epa.stars2.database.dbObjects.document.NEIDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.Emissions;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsMaterialActionUnits;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEU;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportPeriod;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipment;
import us.oh.state.epa.stars2.database.dbObjects.facility.EgressPoint;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.PollutantsControlled;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountyDef;
import us.oh.state.epa.stars2.def.ActionsDef;
import us.oh.state.epa.stars2.def.CoordDataSources;
import us.oh.state.epa.stars2.def.DesignCapacityDef;
import us.oh.state.epa.stars2.def.EgrPointTypeDef;
import us.oh.state.epa.stars2.def.EmissionReportsDef;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.ReferencePoints;
import us.oh.state.epa.stars2.def.ReportEisStatusDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.StopWatch;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.util.XMLUtil;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.bean.NameValue;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class NEIExport extends AppBase {

	private static final long serialVersionUID = -3330798684100468344L;

	private static final String STATE_FIPS_CD = "39";
	private static final String TRIBAL_CODE = "000";
	private static final String SCHEMA_VERSION = "3.0";
	private Integer year;
	private boolean nif;
	private boolean xml;
	private boolean hasSearchResults;
	private String transCd;	
	private String transType;
	private HashMap<String, String> neiList;
	private StringBuffer data = new StringBuffer();
	private String transComment;
	private String tmpDirName;	
	private User user;	
	private ContentHandler handler;	
	private Map <String, List<String>> nodeCollections; 
	private List<String> nodeValues;	
	private String neiRootPath;
	
//	This map contains EU Id as the key and process Id(4 digits of eu id and 2 digits of sequence numbers) 
//	from EP file as value,
//  Since there is no process Id for processes which gels with US EPA validation, 
//	we need to generate a process Id for each processes.
//	process Id in EM file should match the process Id in EP file. 	
	private HashMap<String, String> processIds;
	private HashMap<String, String> releasePointIds;
	
	private Thread searchThread;
    private boolean browserCompleted;
    private FacilityProfile fProf;
    private int percentage;
    private String statusMsg = "";    
    private boolean searchStarted;    
    private boolean searchComplete;
    
    private DisplayUtil displayUtilBean;
	private InfrastructureService infrastructureService;
    
	private final static DateFormat dateFormat = new SimpleDateFormat(
				"yyyyMMdd");
    private EmissionsReportService emissionsReportService;
	private FacilityService facilityService;
	private DocumentDAO documentDAO; //TODO should call svc, not dao
	

	public DocumentDAO getDocumentDAO() {
		return documentDAO;
	}

	public void setDocumentDAO(DocumentDAO documentDAO) {
		this.documentDAO = documentDAO;
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
	
	public NEIExport() {
		super();
	}

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public boolean isNif() {
		return nif;
	}

	public void setNif(boolean nif) {
		this.nif = nif;
	}

	public boolean isXml() {
		return xml;
	}

	public void setXml(boolean xml) {
		this.xml = xml;
	}

	public String getTransCd() {
		return transCd;
	}

	public void setTransCd(String transCd) {
		this.transCd = transCd;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public boolean isHasSearchResults() {
		return hasSearchResults;
	}

	public void setHasSearchResults(boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
	}

	public String getTransComment() {
		return transComment;
	}

	public void setTransComment(String transComment) {
		this.transComment = transComment;
	}

	public HashMap<String, String> getProcessIds() {
		return processIds;
	}

	public void setProcessIds(HashMap<String, String> processIds) {
		this.processIds = processIds;
	}

	public HashMap<String, String> getReleasePointIds() {
		return releasePointIds;
	}

	public void setReleasePointIds(HashMap<String, String> releasePointIds) {
		this.releasePointIds = releasePointIds;
	}

	public final synchronized boolean isBrowserCompleted() {
		return browserCompleted;
	}

	public final synchronized void setBrowserCompleted(boolean completed) {
		browserCompleted = completed;
		
		 if (browserCompleted) {

	            if (getSearchThread() != null && getSearchThread().isAlive()) {
	                try {
	                    getSearchThread().interrupt();
	                    getSearchThread().join();
	                }
	                catch (Exception e) {
	                    // Ignore.
	                }
	                finally {
	                    setSearchThread(null);
	                }
	            }
	            else {
	                setSearchThread(null);
	            }

	            setSearchStarted(false);
	        }
	}

	public final synchronized int getPercentage() {
		return percentage;
	}

	public final synchronized void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public final synchronized Thread getSearchThread() {
		return searchThread;
	}

	public final synchronized void setSearchThread(Thread searchThread) {
		this.searchThread = searchThread;
	}

	public  final synchronized boolean isSearchCompleted() {
		return searchComplete;
	}

	public  final synchronized void setSearchCompleted(boolean searchComplete) {
		this.searchComplete = searchComplete;
	}

	public  final synchronized boolean isSearchStarted() {
		return searchStarted;
	}

	public  final synchronized void setSearchStarted(boolean searchStarted) {
		this.searchStarted = searchStarted;
	}

	public  final synchronized String getStatusMsg() {
		return statusMsg;
	}

	public  final synchronized void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}

	public final synchronized void start(Thread thread) {
        try {
            setSearchCompleted(false);
            thread.start();
            wait(2000);
        }
        catch (InterruptedException ie) {
            logger.error("NEI operation thread start was interrupted.");
        }
    }
	
	public final synchronized void setErrorMessage(String error) {
        displayUtilBean.addMessageToQueue(error, DisplayUtil.ERROR, null);
    }

    public final synchronized void setInfoMessage(String info) {
        displayUtilBean.addMessageToQueue(info, DisplayUtil.INFO, null);
    }
    
    public final boolean isShowProgressBar() {
        boolean ret = false;
        if (isSearchStarted()) {
            if (!isSearchCompleted()) {
                ret = true;
            }
            else if (isSearchCompleted() && !isBrowserCompleted()) {
                setBrowserCompleted(true);
                fProf.setRefreshStr(" ");
                ret = true;
            }
        }
logger.debug("Progress bar :" + ret);        
        return ret;
    }
    
	public NameValue[] getNeiList() {
		ArrayList<NameValue> ret = new ArrayList<NameValue>();

		for (String key : neiList.keySet()) {
			ret.add(new NameValue(key, neiList.get(key)));
		}

		return ret.toArray(new NameValue[0]);
	}

	public LinkedHashMap<String, String> getTransactionCds() {
		LinkedHashMap<String, String> codes = new LinkedHashMap<String, String>();
		codes.put("Add", "A");// Add
		codes.put("Delete", "D");// Delete
		codes.put("Revise/Delete", "RD");// Revise/Delete
		codes.put("Revise/Add", "RA");// Revise/Add

		return codes;
	}

	public LinkedHashMap<String, String> getTransactionTypes() {
		LinkedHashMap<String, String> types = new LinkedHashMap<String, String>();
		types.put("Original", "00");// Original
		types.put("Replacement", "05");// Replacement
		
		return types;
	}
	
	public final String generateNEIFiles() {
		boolean operationOK = true;
		String ret = null;
		neiList = null;

		try {
			if (year == null) {
				operationOK = false;
				DisplayUtil.displayError("Select Year");
			}
			if (!nif && !xml) {
				operationOK = false;
				DisplayUtil.displayError("Select Format");
			}
			if (transCd == null) {
				operationOK = false;
				DisplayUtil.displayError("Select Transaction Code");
			}
			if(transType == null){
				operationOK = false;
				DisplayUtil.displayError("Select Transaction Type");
			}			
			
			try{
				user = InfrastructureDefs.getPortalUser();
			}catch(Exception ex){
				operationOK = false;
				DisplayUtil.displayError(ex.getMessage());			
			}
			if(user == null){
				operationOK = false;
				DisplayUtil.displayError("User is null.");				
			}
			
			if(operationOK){
				neiList = new HashMap<String, String>();
				String yearPath = null;

				StopWatch exportTimer = new StopWatch();
				exportTimer.start();
				
				try {// create folder for selected year under NEI
					neiRootPath = File.separator + "NEI" + File.separator;
					yearPath = File.separator + "NEI" + File.separator
							+ year;
					tmpDirName = yearPath;
					DocumentUtil.mkDirs(yearPath);
				} catch (Exception e) {
					// Eat it, don't throw exception if Dir already exists.
				}	
			
				if (nif) {				
					ret = generate(year);
				
					if(ret.equals(FAIL)){
						operationOK = false;
					}
				}else if(xml){				
					retrieveNodesFromSchema();						
					createFile("XML");
				}
				
				setSearchCompleted(true);
				exportTimer.stop();
				logger.debug("Time elapsed for Export :" + exportTimer.toString());
			}
			
		} catch (RemoteException re) {
			operationOK = false;
			DisplayUtil.displayError(re.getMessage());
			handleException(re);
		} catch(Exception ex){
			operationOK = false;
			DisplayUtil.displayError(ex.getMessage());			
		}

		if (operationOK) {
			DisplayUtil.displayInfo("NEI Files generated successfully");
	//		ret = "dialog:viewNEIFiles";
		} else {
			DisplayUtil.displayError("NEI generation failed");
		}
		return ret;
	}

	public final String reset() {
		year = null;
		nif = false;
		xml = false;
		transCd = null;
		transComment = null;
		transType = null;
		hasSearchResults = false;

		return SUCCESS;
	}

	public final void closeDialog() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	private final synchronized String generate(Integer year) throws DAOException {

		try {
			EmissionsReportSearch searchObj = new EmissionsReportSearch();
			searchObj.setYear(year);
			searchObj.setReportingState(ReportReceivedStatusDef.DOLAA_APPROVED);
            searchObj.setUnlimitedResults(unlimitedResults());

			EmissionsReportSearch reports[] = getEmissionsReportService()
					.searchEmissionsReports(searchObj, false);

			EmissionsReport latestEmissionsRpt = null;
            logger.debug("No of Reports :" + reports.length);
            setMaximum(reports.length);
            setValue(0);

            final List<Integer> fpIDs = new ArrayList<Integer>();
            final List<Integer> erIDs = new ArrayList<Integer>();
            logger.debug("No of Reports :" + reports.length);
            int i = 0;
            for (EmissionsReportSearch rpt : reports) {
                latestEmissionsRpt = getEmissionsReportService()
						.retrieveLatestEmissionReport(year, rpt.getFacilityId());

				if (latestEmissionsRpt != null
						&& latestEmissionsRpt.getRptReceivedStatusCd().equals(
								ReportReceivedStatusDef.DOLAA_APPROVED) 
						&& latestEmissionsRpt.getEisStatusCd().equals(ReportEisStatusDef.EIS_APPROVED)) {
					erIDs.add(latestEmissionsRpt.getEmissionsRptId());
					fpIDs.add(latestEmissionsRpt.getFpId());
				}
				setValue(++i);
			}
			
			if(erIDs.isEmpty() || fpIDs.isEmpty()){
				DisplayUtil.displayWarning("No emissions inventory reports found for the year: " + year);				
				return FAIL;
			}
			
			hasSearchResults = true;			
			
			if (!searchStarted && getSearchThread() == null) {

	            FacesContext facesContext = FacesContext.getCurrentInstance();
                displayUtilBean = DisplayUtil.getSessionInstance(facesContext, true);
	           
	            setSearchStarted(true);
	            String refresh = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"15\" />"
	                + "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\" />"
	                + "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\" />"
	                + "<META HTTP-EQUIV=\"Cache-Control\" "
	                + "CONTENT=\"no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\" />";
	            fProf = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
	            fProf.setRefreshStr(refresh);
	            
	            //setFacilities(null);
	            //setSelectedFacilities(null);

	            final NEIExport nei = this;
	            
	            /*try{
	            	boc.setUser(InfrastructureDefs.getPortalUser());
	            }
	            catch(RemoteException re){
	            	logger.error(re.getMessage(), re);
	            }*/
	            
	            setSearchThread(new Thread("NEI Search Thread") {
	                   
	                    public void run() {
	                        try {
	                       //     setFacilities(bulkOperation.searchFacilities(boc));
	                        	setSearchStarted(true); 	

                            // *******************
                            buildTR(fpIDs);// Transmission data file
                            buildSI(fpIDs);// Site file
                            buildEU(fpIDs);// Emission unit
                            buildER(fpIDs);// Release point
                            buildEP(erIDs);// Emission Process
                            buildPE(erIDs);// Emission Report's Emission Period
                            buildEM(erIDs);// Emissions
                            buildCE(fpIDs);// Control Equipment
	                        }
	                        catch (Exception e) {
	                            String error = "Search failed: System Error. ";
	                            if (e.getMessage() != null && e.getMessage().length() > 0) {
	                                error += e.getMessage();
	                            }
	                            setErrorMessage(error + " Please contact the System Administrator.");
	                            setSearchStarted(false);
	                            setSearchCompleted(false);
	                        }
	                        
	                    }
	                });

	            getSearchThread().setDaemon(true);
	            setBrowserCompleted(false);
	            //bulkOperation.start(searchThread);
	            nei.start(searchThread);
	            
	            if (!isSearchCompleted()) {
	                DisplayUtil.displayInfo("Generating NEI Export. This may take several moments. "
	                                        + "You may cancel the operation by pressing the \"Reset\" button.");
	            }
	            else {
	                fProf.setRefreshStr(" ");
	                setBrowserCompleted(true);
	            }

	        }
	        else {
	            DisplayUtil.displayInfo("Still examining NEI Export. Please wait."
	                                    + "You may cancel the operation by pressing the \"Reset\" button.");
	        }
			
	            
	            
	/*		buildTR(fpIDs);// Transmission data file
			buildSI(fpIDs);// Site file
			buildEU(fpIDs);// Emission unit
			buildER(fpIDs);// Release point
			buildEP(erIDs);// Emission Process
			buildPE(erIDs);// Emission Report's Emission Period
			buildEM(erIDs);// Emissions
			buildCE(fpIDs);// Control Equipment
*/
			if(nif){
				try {
					Calendar cal = Calendar.getInstance();
					String date = Integer.toString(cal.get(Calendar.YEAR)) + "-"
							+ Integer.toString((cal.get(Calendar.MONTH) + 1)) + "-"
							+ Integer.toString(cal.get(Calendar.DAY_OF_MONTH))
							+ "-" + Integer.toString(cal.get(Calendar.HOUR_OF_DAY))
							+ "-" + Integer.toString(cal.get(Calendar.MINUTE))
							+ "-" + Integer.toString(cal.get(Calendar.SECOND));

					String zipFileName = "NeiFiles" + "-" + date + ".zip";

					DocumentUtil.createZipFile(tmpDirName, File.separator + "NEI"
							+ File.separator + year + File.separator + zipFileName,
							"NeiFiles");
					neiList.put("Zip File", DocumentUtil.getFileStoreBaseURL()
							+ "/NEI/" + year + "/" + zipFileName);
				} catch (Exception e) {
					String logStr = "Exception " + e.getClass().getName()
							+ ", Msg = " + e.getMessage();
					logger.error(logStr, e);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new DAOException("Unable to generate NEI files :"
					+ ex.getMessage(), ex);
		}
		return SUCCESS;
	}

	protected void buildSI(List<Integer> fpIDs) throws DAOException {

		String recordType = "SI";
		String countyFipsCd ="";
		String facilityId = "";
		String facilityRegistryId = "";
		String facilityCategoryCode = "";
		String orisFacilityCode = "";
		String sicCode = "";
		String naicsCode = "";
		String facilityName = "";
		String facilityDesc = "";
		String facilityAddress = "";
		String city = "";
		String state = "";
		String zipCd = "";
		String country = "";
		String facilityNTId = "";
		String orgDUNSNumber = "";
		String facilityTRId = "";
		
		try {
			Facility facility = null;
			CountyDef county = null;

			for (Integer fpID : fpIDs) {
				facility = getFacilityService().retrieveFacility(fpID);
				nodeValues = new ArrayList<String>();
				
				if (facility != null) {
					county = infrastructureService.retrieveCounty(
							facility.getPhyAddr().getCountyCd());
					
					if(county != null && county.getFipsCountyCd() != null){
						countyFipsCd = county.getFipsCountyCd();
					}
					if (facility.getFacilityId() != null) {
						facilityId = facility.getFacilityId();
					}
					if (facility.getSicCds() != null) {
						for (String sic : facility.getSicCds()) {// get the first sicCode
							sicCode = sic;
							break;
						}
					}
					if (facility.getNaicsCds() != null) {
						for (String naics : facility.getNaicsCds()) {// get the first naicsCode
							naicsCode = naics;
							break;
						}
					}
					if (facility.getName() != null) {
						facilityName = facility.getName();
					}
					if (facility.getDesc() != null) {
						facilityDesc = facility.getDesc();
					}
					if (facility.getPhyAddr().getAddressLine1() != null) {
						facilityAddress = facility.getPhyAddr()
								.getAddressLine1();
					}
					if (facility.getPhyAddr().getAddressLine2() != null) {
						facilityAddress += facility.getPhyAddr()
								.getAddressLine2();
					}
					if (facility.getPhyAddr().getCityName() != null) {
						city = facility.getPhyAddr().getCityName();
					}
					if (facility.getPhyAddr().getState() != null) {
						state = facility.getPhyAddr().getState();
					}
					if (facility.getPhyAddr().getZipCode() != null) {
						zipCd = facility.getPhyAddr().getZipCode();
					}
					if (facility.getPhyAddr().getCountryCd() != null) {
						country = facility.getPhyAddr().getCountryCd();
					}
				}
				
				
				formatData(recordType, 2, false);
				formatData(STATE_FIPS_CD + countyFipsCd, 5, false);
				formatData(facilityId, 15, false);
				formatData(facilityRegistryId, 12, false);// Facility Registry Identifier
				formatData(facilityCategoryCode, 2, false);// Facility Category code
				formatData(orisFacilityCode, 6, false);// ORIS Facility code
				formatData(sicCode, 4, false);// SICCode is a list of codes for a given fpId and nei needs only
											// one, Per Tom vellalis pick the first one.
				formatData(naicsCode, 6, false);// NAICSCode is a list of codes for a given fpId and nei
												// needs only one, Per Tom vellalis pick the first one.
				formatData(facilityName, 80, false);
				formatData(facilityDesc, 40, false);
				formatData(facilityAddress, 50, false);
				formatData(city, 60, false);
				formatData(state, 2, false);
				formatData(zipCd, 14, false);
				formatData(country, 40, false);
				formatData(facilityNTId, 20, false);// Facility NT Identifier
				formatData(orgDUNSNumber, 9, false);// Dun & Bradstreet number for the facility.
				formatData(facilityTRId, 20, false); // Toxic Release Inventory (TRI) ID for facility.
				formatData(transCd, 4, false); // Transaction Code
				formatData(TRIBAL_CODE, 3, false);
				
				
				if(nif){
					data.append(System.getProperty("line.separator"));
				}
				else{
					AttributesImpl attrs = new AttributesImpl();
					attrs.addAttribute("", "", "schemaVersion", "CDATA", SCHEMA_VERSION);					

					handler.startElement("", "", "SiteSubmissionGroup", attrs);
					attrs.clear();
					buildElements("SiteSubmissionGroupType", nodeValues);						
					handler.endElement("", "", "SiteSubmissionGroup");
				}
								
			}
			if(nif){
				createFile("SI");
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new DAOException("Unable to generate SI file :"
					+ ex.getMessage(), ex);
		}
	}

	protected void buildTR(List<Integer> fpIDs) throws DAOException {

		String recordType = "TR";
		String countyFipsCd = "";
		String organizationName = "Ohio EPA";
		String transactionType = "";
		String inventoryYear = "";
		String inventoryTypeCd = "";
		String transactionDate = "";
		String reliabilityInd = "100";
		String submissionNo = "1";
		String individualName = "";
		String phone = "6146442270";
		String phoneType = "Office";
		String emailAddress = "";
		String emailAddressType = "Email";
		String sourceType = "Point";
		String affiliationType = "Report Certifier";
		String versionNo = "";
	
		nodeValues = new ArrayList<String>();
		
		try {
			Facility facility = null;
			CountyDef county = null;
			inventoryTypeCd = SystemPropertyDef.getSystemPropertyValue("NEI_Inventory_Type_Code", null);
			versionNo = SystemPropertyDef.getSystemPropertyValue("NEI_Format_Version", null);
			
			for (Integer fpID : fpIDs) {
				facility = getFacilityService().retrieveFacility(fpID);

				if (facility != null) {
					county = infrastructureService.retrieveCounty(
							facility.getPhyAddr().getCountyCd());
					
					if(county != null && county.getFipsCountyCd() != null){
						countyFipsCd = county.getFipsCountyCd();						
					}
				}
				if (year != null) {
					inventoryYear = year.toString();					
				}
				if(transType != null){
					transactionType = transType;					
				}
				transactionDate = dateFormat.format(System.currentTimeMillis());
								
				if (user != null) {					
					if (user.getFullName() != null) {
						individualName = user.getFullName();						
					}		
									
					if (user.getEMailAddress() != null) {
						emailAddress = user.getEMailAddress();						
					}
				}				
				

				formatData(recordType, 2, false);
				formatData(STATE_FIPS_CD + countyFipsCd, 5, false);
				formatData(organizationName, 80, false);
				formatData(transactionType, 2, false); // Transaction Type code. 00 for original submission and 05 for replacement submission.
				formatData(inventoryYear, 4, false); 
				formatData(inventoryTypeCd, 10, false); // Inventory Type code "CRIT", "HAP" or "CRITHAP"
				formatData(transactionDate, 8, false);
				formatData(submissionNo, 4, false);// Submission number
				formatData(reliabilityInd, 5, false);// Reliability Indicator
				formatData(transComment, 80, false);
				formatData(individualName, 70, false); 
				formatData(phone, 15, false);
				formatData(phoneType, 10, false);// Telephone type name "Office", "Home"
				formatData(emailAddress, 100, false);
				formatData(emailAddressType, 10, false);// Electronic type name "Email"
				formatData(sourceType, 25, false); // Source type code
				formatData(affiliationType, 40, false); // Affiliation Type
				formatData(versionNo, 4, false);
				formatData(TRIBAL_CODE, 3, false);// Tribal code. Enter '000' if tribal code does not apply.
					
				
				if(nif){
					data.append(System.getProperty("line.separator"));
				}else{
					AttributesImpl attrs = new AttributesImpl();
					attrs.addAttribute("", "", "schemaVersion", "CDATA", SCHEMA_VERSION);					
					
					handler.startElement("", "", "TransmittalSubmissionGroup", attrs);
					attrs.clear();					
					buildElements("TransmittalSubmissionGroupType", nodeValues);					
					handler.endElement("", "", "TransmittalSubmissionGroup");
					break;//TODO check how many group elements for TR
				}			
			}
			
		if(nif){
			createFile("TR");
		}
			
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new DAOException("Unable to generate TR file :"
					+ ex.getMessage(), ex);
		}
	}

	protected void buildEU(List<Integer> fpIDs) throws DAOException {
		String recordType = "EU";	
		HashSet<String> nValues = new HashSet<String>();
		
		try {
			for (Integer fpID : fpIDs) {
				String facilityId = "";
				String epaEmuId = "";
				String sicCode = "";
				String naicsCode = "";
				
				Facility facility = null;
				CountyDef county = null;
				facility = getFacilityService().retrieveFacilityProfile(fpID);				
				
				if (facility != null) {
					String countyFipsCd = "";
					county = infrastructureService.retrieveCounty(
							facility.getPhyAddr().getCountyCd());
					
					if(county != null && county.getFipsCountyCd() != null){
						countyFipsCd = county.getFipsCountyCd();
					}
					
					for(EmissionUnit ee : facility.getEmissionUnits()){
						nValues.add(ee.getEpaEmuId());
					}
					
					for(String eEmuId : nValues.toArray(new String[0])){						
						EmissionUnit eu = facility.getEmissionUnit(eEmuId);
					
						if(eu != null){							
							if(eu.getOperatingStatusCd() != null && eu.getOperatingStatusCd().equals(EuOperatingStatusDef.OP)){						
								String boilerId = "";						
								String designCapacity = "";
								String unitNumerator = "";
								String unitDenominator = "";
								String designCapacityMaxValue = "0";
								String emUnitDesc =  "";
								String blank = " ";
								nodeValues = new ArrayList<String>();

								if (facility.getFacilityId() != null) {
									facilityId = facility.getFacilityId();
								}
								if (eu.getEpaEmuId() != null) {
									epaEmuId = eu.getEpaEmuId();
								}
								if (eu.getOrisBoilerId() != null) {
									boilerId = eu.getOrisBoilerId();
								}
								if (facility.getSicCds() != null) {
									for (String sic : facility.getSicCds()) {// get the first sicCode
										sicCode = sic;
										break;
									}
								}
								if (facility.getNaicsCds() != null) {
									for (String naics : facility.getNaicsCds()) {// get the first naicsCode
										naicsCode = naics;
										break;
									}
								}
								
								if(eu.getDesignCapacityUnitsVal() != null){
									designCapacity = eu.getDesignCapacityUnitsVal();
								}
								if (eu.getDesignCapacityCd() != null) {							
									if (eu.getDesignCapacityCd().equals(DesignCapacityDef.BO)) {
										unitNumerator = "BTU";
									} else {
										unitNumerator = "MW";// unitNumerator for Turbine	
									}
								}
								if (eu.getDesignCapacityCd() != null) {
									if (eu.getDesignCapacityCd().equals(DesignCapacityDef.BO)) {
										unitDenominator = "HR";
									} else {

									}

									if (unitNumerator.equals("HP")) {
										unitDenominator = "";// If numerator=HP then denominator does not apply.
									}
								}

								if(xml){
									// If EU desc is retrieved for nif format, for some unknown reasons there a validation error on BFCC validator.
									//so eu desc is retrieved for only xml format.
									if(eu.getEuDesc() != null){ 
										emUnitDesc = eu.getEuDesc(); 
									}
								}					

								formatData(recordType, 2, false);
								formatData(STATE_FIPS_CD + countyFipsCd, 5, false);
								formatData(facilityId, 15, false);
								formatData(epaEmuId, 6, false);
								formatData(boilerId, 5, false);
								formatData(sicCode, 4, false);
								formatData(naicsCode, 6, false);
								formatData(blank, 2, true);
								formatData(designCapacity, 10, false);
								formatData(unitNumerator, 10, false);
								formatData(unitDenominator, 10, false);
								formatData(designCapacityMaxValue, 10, false);// leave as blank per Tom.
								formatData(emUnitDesc, 80, false);
								formatData(transCd, 4, false);
								formatData(TRIBAL_CODE, 3, false);										

								if(nif){
									data.append(System.getProperty("line.separator"));	
								}
								else{								
									AttributesImpl attrs = new AttributesImpl();
									attrs.addAttribute("", "", "schemaVersion", "CDATA", SCHEMA_VERSION);					

									handler.startElement("", "", "EmissionUnitSubmissionGroup", attrs);
									attrs.clear();
									buildElements("EmissionUnitSubmissionGroupType", nodeValues);
									handler.endElement("", "", "EmissionUnitSubmissionGroup");
								}
							}//end operating status if
						}		
					}
				}
			}
		if(nif){
			createFile("EU");
		}
		
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new DAOException("Unable to generate EU file :"
					+ ex.getMessage(), ex);
		}
	}

	protected void buildEP(List<Integer> erIDs) throws DAOException {
		String recordType = "EP";
		String countyFipsCd = "";
		String processId = "";
		String releasePointId = "";
		String releaseEU = "";
		processIds = new HashMap<String, String>();
		
		try {
			for (Integer erID : erIDs) {
				Facility facility = null;
				CountyDef county = null;				
				EmissionsReport emissionReport = null;

				emissionReport = getEmissionsReportService().retrieveEmissionsReport(erID, false);
				if(emissionReport != null){
					facility = getFacilityService().retrieveFacilityProfile(emissionReport.getFpId());
					getEmissionsReportService().locatePeriodNames(facility, emissionReport);
				}
				if(facility != null){
					county = infrastructureService.retrieveCounty(facility.getPhyAddr().getCountyCd());
								
					if(county != null && county.getFipsCountyCd() != null){
						countyFipsCd = county.getFipsCountyCd();
					}
				
					int i = 0;
					if (emissionReport != null) {
						for (EmissionsReportEU erEU : emissionReport.getEus()) {
							String epaEmuId = "";
							String facilityId = "";
							EmissionUnit eu = null;					
							facilityId = facility.getFacilityId();
							eu = facility.getEmissionUnit(erEU);
					
							if (releaseEU == null) {
								if (eu != null) {
									releaseEU = eu.getEpaEmuId().substring(0, 4);
								}
							}

							for (EmissionsReportPeriod erEP : erEU.getPeriods()) {
								EmissionProcess facEP = null;
								String scCode = "";
								String mactCode = "";
								String processDesc = "";
								String winterThroughputPct = "";
								String springThroughputPct = "";
								String summerThroughputPct = "";
								String fallThroughputPct = "";
								String daysPerWeek = "";
								String weeksPerYear = "";
								String hoursPerDay = "";
								String hoursPerYear = "";
								String fuelHeatMeasure = "0.1";// no values exists in stars2
								String fuelSulfurMeasure = "0.1";//no values exists in stars2 
								String fuelAshMeasure = "0.1";//no values exists in stars2
								String mactComplianceCd = "";
								String processName = "";
								nodeValues = new ArrayList<String>();							
								
								if (erEP.getSccId() != null && eu != null) {
									facEP = eu.findProcess(erEP.getSccId());
								}
								if (eu != null) {
									if (eu.getEpaEmuId() != null) {// process Id - 4 characters of Emission
										// unit Id plus "00" sequence no.
										epaEmuId = eu.getEpaEmuId();
										processId = eu.getEpaEmuId().substring(0, 4);
										String c = Integer.toString(i);
										processName = erEP.getTreeLabel();

										if (c.length() == 1) {
											c = "0" + c;
										}
										processId += c;
										i++;
										processIds.put(processName, processId);// see declaration for more details.

										releasePointId = releasePointIds.get(epaEmuId);
									}
								}
								/*if (releaseEU != null) {// release point Id - 4 characters of Emission
													// unit Id plus "00" sequence no.
								String c = Integer.toString(j);
								if (c.length() == 1) {
									c = "0" + c;
								}
								releasePointId = releaseEU + c;
								j++;

								}*/
								if (erEP.getSccId() != null) {
									scCode = erEP.getSccCode().getSccId();
								}
								if (facEP != null
										&& facEP.getEmissionProcessNm() != null) {
									processDesc = facEP.getEmissionProcessNm();
								}
								if (erEP.getWinterThroughputPct() != null) {
									winterThroughputPct = erEP
									.getWinterThroughputPct().toString();
								}
								if (erEP.getSpringThroughputPct() != null) {
									springThroughputPct = erEP
									.getSpringThroughputPct().toString();
								}
								if (erEP.getSummerThroughputPct() != null) {
									summerThroughputPct = erEP
									.getSummerThroughputPct().toString();
								}
								if (erEP.getFallThroughputPct() != null) {
									fallThroughputPct = erEP.getFallThroughputPct()
									.toString();
								}
								if (erEP.getDaysPerWeek() != null) {
									daysPerWeek = erEP.getDaysPerWeek().toString();
								}
								if (erEP.getWeeksPerYear() != null) {
									weeksPerYear = erEP.getWeeksPerYear()
									.toString();
								}
								if (erEP.getHoursPerDay() != null) {
									hoursPerDay = erEP.getHoursPerDay().toString();
								}
								if (erEP.getHoursPerYear() != null) {
									hoursPerYear = erEP.getHoursPerYear()
									.toString();
								}

								formatData(recordType, 2, false);
								formatData(STATE_FIPS_CD + countyFipsCd, 5, false);
								formatData(facilityId, 15, false);
								formatData(epaEmuId, 6, false);
								formatData(releasePointId, 6, false);
								formatData(processId, 6, false);
								formatData(scCode, 10, false);
								formatData(mactCode, 6, false);
								formatData(processDesc, 78, false);
								formatData(winterThroughputPct, 3, false);
								formatData(springThroughputPct, 3, false);
								formatData(summerThroughputPct, 3, false);
								formatData(fallThroughputPct, 3, false);
								formatData(daysPerWeek, 1, false);
								formatData(weeksPerYear, 2, false);
								formatData(hoursPerDay, 2, false);
								formatData(hoursPerYear, 4, false);
								formatData(fuelHeatMeasure, 8, false);
								formatData(fuelSulfurMeasure, 5, false);
								formatData(fuelAshMeasure, 5, false);
								formatData(mactComplianceCd, 6, false);
								formatData(transCd, 4, false);
								formatData(TRIBAL_CODE, 3, false);


								if(nif){
									data.append(System.getProperty("line.separator"));
								}
								else{									
									AttributesImpl attrs = new AttributesImpl();
									attrs.addAttribute("", "", "schemaVersion", "CDATA", SCHEMA_VERSION);					

									handler.startElement("", "", "EmissionProcessSubmissionGroup", attrs);
									attrs.clear();								
									buildElements("EmissionProcessSubmissionGroupType", nodeValues);								
									handler.endElement("", "", "EmissionProcessSubmissionGroup");
								}

							}
						}
					}
				}//end facility not null
			}

			if(nif){
				createFile("EP");
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new DAOException("Unable to generate EP file :"
					+ ex.getMessage(), ex);
		}
	}

	protected void buildPE(List<Integer> erIDs) throws DAOException {// Emission Period
		try {
			String recordType = "PE";
			
			for (Integer erID : erIDs) {
				Facility facility = null;
				EmissionsReport emissionReport = null;
				CountyDef county = null;

				String facilityId = "";
				String emPeriodStartDate = "";
				String emPeriodEndDate = "";
				String countyFipsCd = "";

				emissionReport = getEmissionsReportService().retrieveEmissionsReport(erID, false);

				if (emissionReport != null) {
					facility = getFacilityService().retrieveFacilityProfile(emissionReport.getFpId());

					if (facility != null) {
						county = infrastructureService.retrieveCounty(facility.getPhyAddr().getCountyCd());
						getEmissionsReportService().locatePeriodNames(facility, emissionReport);					
					
						for (EmissionsReportEU erEU : emissionReport.getEus()) {
							String epaEmuId = "";
							String processId = "";
							EmissionUnit eu = null;							
							facilityId = facility.getFacilityId();
							eu = facility.getEmissionUnit(erEU);							

							for (EmissionsReportPeriod erEP : erEU.getPeriods()) {
								String throughputValue = "";
								String unitNumeratorMeasure = "";
								String materialCode = "";
								String materialIOCode = "";
								String daysPerWeek = "";
								String weeksPerPeriod = "";
								String hoursPerDay = "";
								String hoursPerPeriod = "";
								String blank = " ";
								nodeValues = new ArrayList<String>();

								EmissionsMaterialActionUnits mau = new EmissionsMaterialActionUnits();							

								for (EmissionsMaterialActionUnits maus : erEP
										.getMaus()) {
									if (maus.isBelongs()) {
										mau = maus;
									}
								}

								if (county != null
										&& county.getFipsCountyCd() != null) {
									countyFipsCd = county.getFipsCountyCd();
								}
								if (eu != null && eu.getEpaEmuId() != null) {
									epaEmuId = eu.getEpaEmuId();

									/*	processId = eu.getEpaEmuId().substring(0, 4);
								String c = Integer.toString(i);
								if (c.length() == 1) {
									c = "0" + c;
								}
								processId += c;
								i++;*/
									//	processId = processIds.get(erEP.getTreeLabel());// see processId declaration for more details.
								}									
								
								processId = processIds.get(erEP.getTreeLabel());// see processId declaration for more details.

								if (emissionReport.getReportYear() != null) {
									Calendar cal = Calendar.getInstance();
									cal.set(emissionReport.getReportYear(), Calendar.JANUARY, 1);
									emPeriodStartDate = dateFormat.format(cal.getTime());
								}
								if (emissionReport.getReportYear() != null) {
									Calendar cal = Calendar.getInstance();
									cal.set(emissionReport.getReportYear(), Calendar.DECEMBER, 31);
									emPeriodEndDate = dateFormat.format(cal.getTime());
								}
								if (mau.getThroughput() != null) {
									throughputValue = mau.getThroughput();
								}
								if (mau.getMeasure() != null) {
									unitNumeratorMeasure = mau.getMeasure();
								}
								if (mau.getMaterial() != null) {
									materialCode = mau.getMaterial();
								}
								if (mau.getAction() != null) {
									materialIOCode = ActionsDef.getData()
									.getItems().getItemDesc(mau.getAction());
								}
								if (erEP.getDaysPerWeek() != null) {
									daysPerWeek = erEP.getDaysPerWeek().toString();
								}
								if (erEP.getWeeksPerYear() != null) {
									weeksPerPeriod = erEP.getWeeksPerYear()
									.toString();
								}
								if (erEP.getHoursPerDay() != null) {
									hoursPerDay = erEP.getHoursPerDay().toString();
								}
								if (erEP.getHoursPerYear() != null) {
									hoursPerPeriod = erEP.getHoursPerYear()
									.toString();
								}

								formatData(recordType, 2, false);
								formatData(STATE_FIPS_CD + countyFipsCd, 5, false);
								formatData(facilityId, 15, false);
								formatData(epaEmuId, 6, false);
								formatData(processId, 6, false);
								formatData(emPeriodStartDate, 8, false);
								formatData(emPeriodEndDate, 8, false);
								formatData(blank, 2, true);// blank spaces
								formatData(blank, 4, false);// Emission Period start time
								formatData(blank, 4, false);// Emission Period end time
								formatData(blank, 10, true);// blank spaces
								formatData(throughputValue, 10, false);
								formatData(unitNumeratorMeasure, 10, false);
								formatData(materialCode, 4, false);
								formatData(materialIOCode, 10, false);
								formatData(daysPerWeek, 1, false);
								formatData(weeksPerPeriod, 2, false);
								formatData(hoursPerDay, 2, false);
								formatData(hoursPerPeriod, 4, false);
								formatData(transCd, 4, false);
								formatData(TRIBAL_CODE, 3, false);

								if(nif){
									data.append(System.getProperty("line.separator"));
								}
								else{
									AttributesImpl attrs = new AttributesImpl();
									attrs.addAttribute("", "", "schemaVersion", "CDATA", SCHEMA_VERSION);					

									handler.startElement("", "", "EmissionPeriodSubmissionGroup", attrs);
									attrs.clear();								
									buildElements("EmissionPeriodSubmissionGroupType", nodeValues);								
									handler.endElement("", "", "EmissionPeriodSubmissionGroup");
								}						
							}//end erPeriod
						}
					}
				}
			}

			if(nif){
				createFile("PE");
			}
			
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new DAOException("Unable to generate PE file :"
					+ ex.getMessage(), ex);
		}
	}

	protected void buildER(List<Integer> fpIDs) throws DAOException {// Release points
		String recordType = "ER";
		String releasePointId = "";
		String utmZoneCode = "";
		String xyCoordinate = "LATLON";
		String fugUnitOfMeasure = "";
		String referencePoints = ReferencePoints.R106;
		String coorDataSourceCd = "";
		String releaseEU = "";
		releasePointIds = new HashMap<String, String>();
		
		try {
			for (Integer fpID : fpIDs) {
				Facility facility = null;
				CountyDef county = null;
				facility = getFacilityService().retrieveFacilityProfile(fpID);

				String facilityId = "";
				String countyFipsCd = "";

				if (facility != null) {
					facilityId = facility.getFacilityId();
					county = infrastructureService.retrieveCounty(
							facility.getPhyAddr().getCountyCd());
					int j = 0;
					if (county != null && county.getFipsCountyCd() != null) {
						countyFipsCd = county.getFipsCountyCd();
					}

					for (EmissionUnit eu : facility.getEmissionUnits()) {
						if (releaseEU == null) {
							releaseEU = eu.getEpaEmuId().substring(0, 4);
						}
						for (EmissionProcess ep : eu.getEmissionProcesses()) {
														
								// Release points with Control Equipments
								for (ControlEquipment ce : ep.getControlEquipments()) {									
									for (EgressPoint egpC : ce.getEgressPoints()) {
										String releasePointTypeCd = "";
										String stackHeight = "0.01";
										String stackDiameter = "0.1";
										String stackFenceLineDst = "1000";
										String exitGasTempValue = "0.1";
										String exitGasStreamRate = "0.1";
										String exitGasFlowRate = "0.314";//pi r square/velocity = 3.14*.1*.1/.1
										String longitudeMeasure = "";
										String latitudeMeasure = "";
										int fugHorizontalValue = 0;
										int fugReleaseValue = 0;
										String releasePointDesc = "";
										String hortCollectionMethodCd = "";
										String hortAccuracyMeasure = "";
										String hortRefDatumCd = "";
										String sourceMapScale = "";
										String blank = " ";
										nodeValues = new ArrayList<String>();
										
										if (releaseEU != null) {
											String c = Integer.toString(j);
											if (c.length() == 1) {
												c = "0" + c;
											}
											releasePointId = releaseEU + c;
											j++;
											releasePointIds.put(eu.getEpaEmuId(), releasePointId);// see declaration for more details.
										}

										if (egpC.getEgressPointTypeCd() != null) {
											releasePointTypeCd = egpC.getEgressPointTypeCd();
										}
										if (egpC.getHeight() != null) {
											stackHeight = egpC.getHeight().toString();
										}
										
										if (egpC.getDiameter() != null) {
											stackDiameter = egpC.getDiameter().toString();
										}
										if (egpC.getExitGasTempMax() != null) {
											exitGasTempValue = egpC.getExitGasTempMax().toString();
										}
										if (egpC.getExitGasTempAvg() != null) {
											exitGasStreamRate = egpC.getExitGasTempAvg().toString();
										}
										if (egpC.getExitGasFlowMax() != null) {
											exitGasFlowRate = egpC.getExitGasFlowMax().toString();
										}										
										if (egpC.getStackFencelineDistance() != null) {
											stackFenceLineDst = egpC.getStackFencelineDistance().toString();
										}										
										if (egpC.getLongitudeDeg() != null) {
											longitudeMeasure = egpC.getLongitudeDeg().toString();
										}
										/*
										 * 	if(egpC.getLongitudeMin() != null){
										 * longitudeMeasure +=
										 * egpC.getLongitudeMin().toString(); }
										 * if(egpC.getLongitudeSec() != null){
										 * longitudeMeasure +=
										 * egpC.getLongitudeSec().toString(); }
										 */
										if (egpC.getLatitudeDeg() != null) {
											latitudeMeasure = egpC.getLatitudeDeg().toString();
										}
										/*
										 * if(egpC.getLatitudeMin() != null){
										 * latitudeMeasure +=
										 * egpC.getLatitudeMin().toString(); }
										 * if(egpC.getLatitudeSec() != null){
										 * latitudeMeasure +=
										 * egpC.getLatitudeSec().toString(); }
										 */
										if (egpC.getEgressPointTypeCd().equals(EgrPointTypeDef.FUGITIVE)) {
											if (egpC.getVolumeWidth() != null) {
												fugHorizontalValue = egpC.getVolumeWidth().intValue();
											}
											if (egpC.getReleaseHeight() != null) {
												fugReleaseValue = egpC.getReleaseHeight().intValue();
											}
										}
										if (egpC.getDapcDesc() != null) {
											releasePointDesc = egpC.getDapcDesc();
										}
										if (egpC.getHortCollectionMethodCd() != null) {
											hortCollectionMethodCd = egpC.getHortCollectionMethodCd();									
										}
										if (egpC.getHortAccurancyMeasure() != null) {
											hortAccuracyMeasure = egpC.getHortAccurancyMeasure();
										}
										if (egpC.getHortReferenceDatumCd() != null) {
											hortRefDatumCd = egpC.getHortReferenceDatumCd();
										}
										if (egpC.getSourceMapScaleNumber() != null) {
											sourceMapScale = egpC.getSourceMapScaleNumber().toString();
										}
										if (egpC.getCoordDataSourceCd() != null) {
											coorDataSourceCd = CoordDataSources.C080;
										}
									
										formatData(recordType, 2, false);
										formatData(STATE_FIPS_CD + countyFipsCd, 5, false);
										formatData(facilityId, 15, false);
										formatData(blank, 6, true);// blank spaces
										formatData(releasePointId, 6, false);
										formatData(releasePointTypeCd, 2, false);
										formatData(blank, 10, true);// blank spaces
										formatData(stackHeight, 10, false);
										formatData(stackDiameter, 10, false);
										formatData(stackFenceLineDst, 8, false);
										formatData(exitGasTempValue, 10, false);
										formatData(exitGasStreamRate, 10, false);
										formatData(exitGasFlowRate, 10, false);
										formatData(longitudeMeasure, 11, false);
										formatData(latitudeMeasure, 10, false); 
										formatData(utmZoneCode, 2, false); // UTM zone code. Stars2 maintains
																// only Lat & Long, UTM will be always blank.
										formatData(xyCoordinate, 8, false);
										formatData(fugHorizontalValue, 8, false);
										formatData(fugReleaseValue, 8, false);
										formatData(fugUnitOfMeasure, 10, false);
										formatData(releasePointDesc, 80, false);
										formatData(transCd, 4, false);
										formatData(hortCollectionMethodCd, 3, false);
										formatData(hortAccuracyMeasure, 6, false);
										formatData(hortRefDatumCd, 3, false);
										formatData(referencePoints, 3, false);
										formatData(sourceMapScale, 10, false); 
										formatData(coorDataSourceCd, 3, false);
										formatData(TRIBAL_CODE, 3, false);											
			
										
										if(nif){
											data.append(System.getProperty("line.separator"));
										}
										else{
											AttributesImpl attrs = new AttributesImpl();
											attrs.addAttribute("", "", "schemaVersion", "CDATA", SCHEMA_VERSION);					
											
											handler.startElement("", "", "EmissionReleasePointSubmissionGroup", attrs);
											attrs.clear();								
											buildElements("EmissionReleasePointSubmissionGroupType", nodeValues);											
											handler.endElement("", "", "EmissionReleasePointSubmissionGroup");
										}									
									}
								}

							// Release points without Control Equipments
							for (EgressPoint egp : ep.getEgressPoints()) {
								String releasePointTypeCd = "";
								String stackHeight = "0.01";
								String stackDiameter = "0.1";
								String stackFenceLineDst = "1000";
								String exitGasTempValue = "0.1";
								String exitGasStreamRate = "0.1";
								String exitGasFlowRate = "0.314";//pi r square/velocity = 3.14*.1*.1/.1
								String longitudeMeasure = "";
								String latitudeMeasure = "";
								String fugHorizontalValue = "0";
								String fugReleaseValue = "0";
								String releasePointDesc = "";
								String hortCollectionMethodCd = "";
								String hortAccuracyMeasure = "";
								String hortRefDatumCd = "";
								String sourceMapScale = "";
								String blank = " ";
								nodeValues = new ArrayList<String>();
								
								if (releaseEU != null) {
									String c = Integer.toString(j);
									if (c.length() == 1) {
										c = "0" + c;
									}
									releasePointId = releaseEU + c;
									j++;
									releasePointIds.put(eu.getEpaEmuId(), releasePointId);// see declaration for more details.
								}

								if (egp.getEgressPointTypeCd() != null) {
									releasePointTypeCd = egp.getEgressPointTypeCd();
								}
								if (egp.getHeight() != null) {
									stackHeight = egp.getHeight().toString();
								}
								if(egp.getEgressPointTypeCd() != null && !egp.getEgressPointTypeCd().equals(EgrPointTypeDef.FUGITIVE)
										&& !egp.getEgressPointTypeCd().equals(EgrPointTypeDef.FUGITIVE)){
								
									if (egp.getDiameter() != null) {
										stackDiameter = egp.getDiameter().toString();
									}
									if (egp.getExitGasTempMax() != null) {
										exitGasTempValue = egp.getExitGasTempMax().toString();
									}
									if (egp.getExitGasTempAvg() != null) {
										exitGasStreamRate = egp.getExitGasTempAvg().toString();
									}
									if (egp.getExitGasFlowMax() != null) {
										exitGasFlowRate = egp.getExitGasFlowMax().toString();
									}
								}
								
								if (egp.getStackFencelineDistance() != null) {
									stackFenceLineDst = egp.getStackFencelineDistance().toString();
								}
								
								if (egp.getLongitudeDeg() != null) {
									longitudeMeasure = egp.getLongitudeDeg().toString();
								}
								/*
								 * if(egp.getLongitudeMin() != null){
								 * longitudeMeasure +=
								 * egp.getLongitudeMin().toString(); }
								 * if(egp.getLongitudeSec() != null){
								 * longitudeMeasure +=
								 * egp.getLongitudeSec().toString(); }
								 */
								if (egp.getLatitudeDeg() != null) {
									latitudeMeasure = egp.getLatitudeDeg().toString();
								}
								/*
								 * if(egp.getLatitudeMin() != null){
								 * latitudeMeasure +=
								 * egp.getLatitudeMin().toString(); }
								 * if(egp.getLatitudeSec() != null){
								 * latitudeMeasure +=
								 * egp.getLatitudeSec().toString(); }
								 */
								if (egp.getEgressPointTypeCd().equals(
										EgrPointTypeDef.FUGITIVE)) {
									if (egp.getVolumeWidth() != null) {
										fugHorizontalValue = egp.getVolumeWidth().toString();
									}
									if (egp.getReleaseHeight() != null) {
										fugReleaseValue = egp.getReleaseHeight().toString();
									}
								}
								if (egp.getDapcDesc() != null) {
									releasePointDesc = egp.getDapcDesc();
								}
								if (egp.getHortCollectionMethodCd() != null) {
									hortCollectionMethodCd = egp.getHortCollectionMethodCd();
								}
								if (egp.getHortAccurancyMeasure() != null) {
									hortAccuracyMeasure = egp.getHortAccurancyMeasure();
								}
								if (egp.getHortReferenceDatumCd() != null) {
									hortRefDatumCd = egp.getHortReferenceDatumCd();
								}

								referencePoints = ReferencePoints.R106;

								if (egp.getSourceMapScaleNumber() != null) {
									sourceMapScale = egp.getSourceMapScaleNumber().toString();
								}
								if (egp.getCoordDataSourceCd() != null) {
									coorDataSourceCd = CoordDataSources.C080;
								}

								formatData(recordType, 2, false);
								formatData(STATE_FIPS_CD + countyFipsCd, 5, false);
								formatData(facilityId, 15, false);
								formatData(blank, 6, true);// blank spaces
								formatData(releasePointId, 6, false);
								formatData(releasePointTypeCd, 2, false);
								formatData(blank, 10, true);// blank spaces
								formatData(stackHeight, 10, false);
								formatData(stackDiameter, 10, false);
								formatData(stackFenceLineDst, 8, false);
								formatData(exitGasTempValue, 10, false);
								formatData(exitGasStreamRate, 10, false);
								formatData(exitGasFlowRate, 10, false);
								formatData(longitudeMeasure, 11, false);
								formatData(latitudeMeasure, 10, false);
								formatData(utmZoneCode, 2, false);
								formatData(xyCoordinate, 8, false);
								formatData(fugHorizontalValue, 8, false);
								formatData(fugReleaseValue, 8, false);
								formatData(fugUnitOfMeasure, 10, false);
								formatData(releasePointDesc, 80, false);
								formatData(transCd, 4, false);
								formatData(hortCollectionMethodCd, 3, false);
								/* ? */formatData(hortAccuracyMeasure, 6, false);
								formatData(hortRefDatumCd, 3, false);
								formatData(referencePoints, 3, false);
								formatData(sourceMapScale, 10, false);
								formatData(coorDataSourceCd, 3, false);
								formatData(TRIBAL_CODE, 3, false);								
													
								if(nif){
									data.append(System.getProperty("line.separator"));
								}
								else{
									AttributesImpl attrs = new AttributesImpl();
									attrs.addAttribute("", "", "schemaVersion", "CDATA", SCHEMA_VERSION);					
										
									handler.startElement("", "", "EmissionReleasePointSubmissionGroup", attrs);
									attrs.clear();										
									buildElements("EmissionReleasePointSubmissionGroupType", nodeValues);										
									handler.endElement("", "", "EmissionReleasePointSubmissionGroup");
								}							
							}//end for of Release points without Control Equipments
						}//end for of Eprocess
					}
				}
			}
			
			if(nif){
				createFile("ER");
			}
			
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new DAOException("Unable to generate ER file :"
					+ ex.getMessage(), ex);
		}
	}

	protected void buildEM(List<Integer> erIDs) throws DAOException {
		String recordType = "EM";
		String processId = "";
		String releasePointId = "";
		String emissionTypeCd = "30";// code for Entire period
		String emFactorRelIndicator = "A";
		String reliabilityInd = "100";
		String blank = " ";
		String emPeriodStartDt = "";
		String emPeriodEndDt = "";
//		Map<String, Map<String, String>> cePollutants = new HashMap<String, Map<String, String>>();
		
		try {
			Calendar cal = Calendar.getInstance();
			
			for (Integer erID : erIDs) {
				Facility facility = null;
				EmissionsReport emissionReport = null;
				CountyDef county = null;
				String facilityId = "";
				String countyFipsCd = "";				

				emissionReport = getEmissionsReportService().retrieveEmissionsReport(erID, false);				
				
				if (emissionReport != null) {
					facility = getFacilityService().retrieveFacilityProfile(
							emissionReport.getFpId());

					if (facility != null) {
						facilityId = facility.getFacilityId();
						county = infrastructureService.retrieveCounty(facility.getPhyAddr().getCountyCd());
						getEmissionsReportService().locatePeriodNames(facility, emissionReport);
					
/*						for(EmissionUnit eu : facility.getEmissionUnits()){
							for(EmissionProcess ep : eu.getEmissionProcesses()){
								for(ControlEquipment ce : ep.getControlEquipments()){
									for(PollutantsControlled cp : ce.getPollutantsControlled()){
										Map<String, String> d = new HashMap<String, String>();
										d.put(eu.getEpaEmuId(), ep.getProcessId());
logger.debug("pCd-" + cp.getPollutantCd()+", "+"Emu Id-" + eu.getEpaEmuId()+", "+"Process Id-"+ ep.getProcessId());		
logger.debug("ProcessName-"+ ep.getEmissionProcessNm());
										cePollutants.put(cp.getPollutantCd(), d);
									}
								}
							}
						}
*/						
						for (EmissionsReportEU erEU : emissionReport.getEus()) {
							String epaEmuId = "";
							String releaseEU = "";
							EmissionUnit eu = facility.getEmissionUnit(erEU);

							if (releaseEU == null) {
								releaseEU = eu.getEpaEmuId().substring(0, 4);
							}
							for (EmissionsReportPeriod erEP : erEU.getPeriods()) {
								String materialCd = "";
								String materialIOCd = "";

								EmissionsMaterialActionUnits mau = new EmissionsMaterialActionUnits();							
							
								for (EmissionsMaterialActionUnits maus : erEP.getMaus()) {
									if (maus.isBelongs()) {
										mau = maus;
									}
								}

								for (Emissions e : erEP.getEmissions().values()) {
									String pollutantCd = "";
									Double emissionV = 0.0d;
									String emissionValue = "";
									String emUnitNumerator = "";
									String emFactorValue = "";									
									String factorUnitNumerator = "";
									String factorUnitDenominator = "";
									String emCalcMethodCd = "";
									String ruleEffMeasure ="100";
									String ruleEffMethodCd = ""; 
									String HAPEmPerformanceCd = "";
									String controlStatus = "";
									String emDataLevelId = "";									
									nodeValues = new ArrayList<String>();

									if (eu != null) {
										if (eu.getEpaEmuId() != null) {
											epaEmuId = eu.getEpaEmuId();
											/*	processId = eu.getEpaEmuId().substring(0, 4);

										String c = Integer.toString(i);
										if (c.length() == 1) {
											c = "0" + c;
										}
										processId += c;
										i++;*/
											//	processId = processIds.get(erEP.getTreeLabel());// see processId declaration for more details.

											releasePointId = releasePointIds.get(eu.getEpaEmuId());
										}
									}
									logger.debug("Tree label in EM :" + erEP.getTreeLabel());								
									processId = processIds.get(erEP.getTreeLabel());// see processId declaration for more details.

									if (county != null && county.getFipsCountyCd() != null) {
										countyFipsCd = county.getFipsCountyCd();
									}
									/*if (releaseEU != null) {
									String c = Integer.toString(j);
									if (c.length() == 1) {
										c = "0" + c;
									}
									releasePointId = releaseEU + c;
									j++;
									releasePointId = releasePointIds.get(eu.getEpaEmuId());
								}*/
									if (e.getPollutantCd() != null) {
										pollutantCd = e.getPollutantCd();
			//							cePollutants.remove(e.getPollutantCd());
									}
									if(e.getPollutantCd() != null){//for control status
										//get Matching EU from facility inventory using correlation Id in EmissionReport EU
										EmissionUnit facEu = facility.getMatchingEmissionUnit(erEU.getCorrEpaEmuId());

										outer:
											for(EmissionProcess facEP : facEu.getEmissionProcesses()){
												for(ControlEquipment facCE : facEP.getControlEquipments()){
													for(PollutantsControlled pc : facCE.getPollutantsControlled()){
														if(pc.getPollutantCd().equals(e.getPollutantCd())){
															controlStatus = "CONTROLLED";
															break outer;
														}
														
														controlStatus = "UNCONTROLLED";
														break outer;
													}
												}
											}
									}
									if (emissionReport.getReportYear() != null) {										
										cal.set(emissionReport.getReportYear(), Calendar.JANUARY, 1);
										emPeriodStartDt = dateFormat.format(cal.getTime());
									}
									if (emissionReport.getReportYear() != null) {										
										cal.set(emissionReport.getReportYear(), Calendar.DECEMBER, 31);
										emPeriodEndDt = dateFormat.format(cal.getTime());
									}
									if (e.getFugitiveEmissions() != null) {
										emissionV = Double.parseDouble(e.getFugitiveEmissions().replace(",", ""));
									}
									if (e.getStackEmissions() != null) {
										emissionV += Double.parseDouble(e.getStackEmissions().replace(",",""));
									}
									if (emissionV != null) {
										emissionValue = emissionV.toString();
									}
									if (e.getEmissionsUnitNumerator() != null) {
										emUnitNumerator = e.getEmissionsUnitNumerator();
									}
									if (e.getFactorNumericValue() != null) {
										emFactorValue = e.getFactorNumericValue();
										factorUnitNumerator = e.getEmissionsUnitNumerator();
										factorUnitDenominator = e.getEmissionsUnitNumerator();
									}								
									if (mau.getMaterial() != null) {
										materialCd = mau.getMaterial();// reference into NEI material
										// table (rp_material_def)
									}
									if (mau.getAction() != null) {
										materialIOCd = ActionsDef.getData()
										.getItems().getItemDesc(mau.getAction());
									}
									if (e.getEmissionCalcMethodCd() != null) {
										emCalcMethodCd = e.getEmissionCalcMethodCd();
									}

									formatData(recordType, 2, false);
									formatData(STATE_FIPS_CD + countyFipsCd, 5, false);
									formatData(facilityId, 15, false);
									formatData(epaEmuId, 6, false);
									formatData(processId, 6, false);
									formatData(pollutantCd, 9, false);
									formatData(blank, 7, true);// blank spaces
									formatData(releasePointId, 6, false);
									formatData(emPeriodStartDt, 8, false);
									formatData(emPeriodEndDt, 8, false);
									formatData(blank, 4, false);// start time
									formatData(blank, 4, false);// end time
									formatData(blank, 10, true);// blank spaces
									formatData(emissionValue, 10, false);
									formatData(emUnitNumerator, 10, false);
									formatData(emissionTypeCd, 2, false);
									formatData(reliabilityInd, 5, false);
									formatData(emFactorValue, 10, false);
									formatData(factorUnitNumerator, 10, false);
									formatData(factorUnitDenominator, 10, false);
									formatData(materialCd, 4, false);
									formatData(materialIOCd, 10, false);
									formatData(blank, 5, true);// blank spaces
									formatData(emCalcMethodCd, 2, false);
									formatData(emFactorRelIndicator, 5, false);
									formatData(ruleEffMeasure, 5, false);// Rule effectiveness measure
									formatData(ruleEffMethodCd, 2, false);// Rule effectiveness method code
									formatData(blank, 3, true);// blank spaces
									formatData(HAPEmPerformanceCd, 2, false);// HAP Emission Performance level code
									formatData(controlStatus, 12, false);// Control status code
									formatData(emDataLevelId, 10, false);// Emission data level identifier
									formatData(transCd, 4, false);
									formatData(TRIBAL_CODE, 3, false);


									if(nif){
										data.append(System.getProperty("line.separator"));
									}
									else{
										AttributesImpl attrs = new AttributesImpl();
										attrs.addAttribute("", "", "schemaVersion", "CDATA", SCHEMA_VERSION);					

										handler.startElement("", "", "EmissionSubmissionGroup", attrs);
										attrs.clear();									
										buildElements("EmissionSubmissionGroupType", nodeValues);									
										handler.endElement("", "", "EmissionSubmissionGroup");
									}							
								}
							}//end erperiod
						}//end erEU
						
		/*				for (Iterator iter = cePollutants.entrySet().iterator(); iter.hasNext();)
						{ 
						    Map.Entry entry = (Map.Entry)iter.next();
						    String pollutantCd = (String)entry.getKey();
						  
						    Object obj = entry.getValue();
						    Map<String, String> byCode = cePollutants.get(pollutantCd);
						    String epaEmuId=""; 
						    String pId=""; 	
						    for (Iterator it = byCode.entrySet().iterator(); it.hasNext();)
						    { 
						        Map.Entry entrySet = (Map.Entry)it.next();
						        epaEmuId = (String)entrySet.getKey();
						        pId= (String)entrySet.getValue();
						    }
						    
						    formatData(recordType, 2, false);
							formatData(STATE_FIPS_CD + countyFipsCd, 5, false);
							formatData(facilityId, 15, false);
							formatData(epaEmuId, 6, false);
logger.debug("pId-" + pId);							
							formatData(processIds.get(pId), 6, false);
							formatData(pollutantCd, 9, false);
							formatData(blank, 7, true);
							formatData(releasePointId, 6, false);
							formatData(emPeriodStartDt, 8, false);
							formatData(emPeriodEndDt, 8, false);
							formatData(blank, 4, false);
							formatData(blank, 4, false);
							formatData(blank, 10, true);
							formatData("0", 10, false);
							formatData("TON", 10, false);
							formatData(emissionTypeCd, 2, false);
							formatData(reliabilityInd, 5, false);
							formatData("", 10, false);
							formatData("", 10, false);
							formatData("", 10, false);
							formatData("", 4, false);
							formatData("", 10, false);
							formatData(blank, 5, true);
							formatData("", 2, false);
							formatData(emFactorRelIndicator, 5, false);
							formatData("", 5, false);
							formatData("", 2, false);
							formatData(blank, 3, true);
							formatData("", 2, false);
							formatData("", 12, false);
							formatData("", 10, false);
							formatData(transCd, 4, false);
							formatData(TRIBAL_CODE, 3, false);
							if(nif){
								data.append(System.getProperty("line.separator"));
							}
						}	*/	   
												
					}
				}
				
				
			}
			
			if(nif){
				createFile("EM");
			}
			
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new DAOException("Unable to generate EM file :"
					+ ex.getMessage(), ex);
		}
	}

	protected void buildCE(List<Integer> fpIDs) throws DAOException {
		String recordType = "CE";
		String processId = "";
			
		try {
			for (Integer fpID : fpIDs) {
				Facility facility = null;
				CountyDef county = null;
				facility = getFacilityService().retrieveFacilityProfile(fpID);				

				if (facility != null) {
					String facilityId = "";
					String countyFipsCd = "";
					
					county = infrastructureService.retrieveCounty(
							facility.getPhyAddr().getCountyCd());
					
					for (EmissionUnit eu : facility.getEmissionUnits()) {
						for (EmissionProcess ep : eu.getEmissionProcesses()) {							
							for (ControlEquipment ce : ep.getControlEquipments()) {
							//	List<DataDetail> dataDetail = ce.getCeDataDetails();
								String euId = "";
								String pollutantCd = "";
								String primaryEff = "0";
								String captureEff = "0";
								String totalCaptureEff = "0";
								String primaryTypeCd = "";
								String secondaryTypeCd = "";
								String controlEquipDesc = "";
								String thirdTypeCd = "";
								String fourthTypeCd = "";
								String blank = " ";
								nodeValues = new ArrayList<String>();
								
								if (facility.getFacilityId() != null) {
									facilityId = facility.getFacilityId();
								}
								if (county != null
										&& county.getFipsCountyCd() != null) {
									countyFipsCd = county.getFipsCountyCd();
								}
								if (eu.getEpaEmuId() != null) {
									euId = eu.getEpaEmuId();
								}
								if (eu.getEpaEmuId() != null) {
									/*processId = eu.getEpaEmuId().substring(0, 4);

									String c = Integer.toString(i);
									if (c.length() == 1) {
										c = "0" + c;
									}
									processId += c;
									i++;*/
								//	processId = processIds.get(ep.getEmissionProcessNm());// see processId declaration for more details.
								}
								
								processId = processIds.get(ep.getProcessId());// see processId declaration for more details.
								
								if (ce.getPollutantsControlled() != null) {// get the first pollutant
									for (PollutantsControlled p : ce.getPollutantsControlled()) {
										pollutantCd = p.getPollutantCd();
										primaryEff = p.getOperContEff();
										captureEff = p.getCaptureEff();
										totalCaptureEff = p.getTotCaptureEff();
										break;
									}
								}
								if(ce.getEquipmentTypeCd() != null){
						/*		if(ce.getCeDataDetails() != null){
										DataDetail d = ce.getCeDataDetails().get(0);
									}
									primaryTypeCd = d.getDataDetailId().toString();*/
									primaryTypeCd = ce.getEquipmentTypeCd();
//TODO									
								}
								if (ce.getDapcDesc() != null) {
									controlEquipDesc = ce.getDapcDesc();
								}
								
								formatData(recordType, 2, false);
								formatData(STATE_FIPS_CD + countyFipsCd, 5, false);
								formatData(facilityId, 15, false);
								formatData(euId, 6, false);
								formatData(processId, 6, false);
								formatData(pollutantCd, 9, false);
								formatData(blank, 11, true);
								formatData(primaryEff, 5, false);
								formatData(captureEff, 5, false);
								formatData(totalCaptureEff, 5, false);
								formatData(primaryTypeCd, 4, false);
								formatData(secondaryTypeCd, 4, false);
								formatData(blank, 25, true);
								formatData(controlEquipDesc, 40, false);
								formatData(thirdTypeCd, 4, false);
								formatData(fourthTypeCd, 4, false);
								formatData(transCd, 4, false);
								formatData(TRIBAL_CODE, 3, false);
										
									
								if(nif){
									data.append(System.getProperty("line.separator"));
								}
								else{
									AttributesImpl attrs = new AttributesImpl();
									attrs.addAttribute("", "", "schemaVersion", "CDATA", SCHEMA_VERSION);					
									
									handler.startElement("", "", "ControlEquipmentSubmissionGroup", attrs);
									attrs.clear();									
									buildElements("ControlEquipmentSubmissionGroupType", nodeValues);								
									handler.endElement("", "", "ControlEquipmentSubmissionGroup");
								}
							}
						}
					}
				}
			}

			if(nif){
				createFile("CE");
			}
			
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new DAOException("Unable to generate CE file :"
					+ ex.getMessage(), ex);
		}
	}

	private void createFile(String fileType) throws DAOException {
		InputStream inStream;		
		
		try {
			String fileName = "ohpt" + fileType.toLowerCase()
					+ year.toString().substring(2);
			NEIDocument neiDoc = new NEIDocument();
//			DocumentDAO docDAO = (DocumentDAO) DAOFactory.getDAO("DocumentDAO");

			neiDoc.setYear(year);
			neiDoc.setTemporary(true);
			if(nif){
				neiDoc.setExtension("txt");
			}else{
				neiDoc.setExtension("xml");
			}
			neiDoc.setLastModifiedBy(1);
			neiDoc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
			neiDoc.setUploadDate(neiDoc.getLastModifiedTS());
			neiDoc.setOverLoadFileName(fileName);
			neiDoc.setDescription("NEI " + fileType + " file ");
			neiDoc = (NEIDocument) documentDAO.createDocument(neiDoc);

			if(nif){
				inStream = new ByteArrayInputStream(data.toString().getBytes("UTF-8"));
				DocumentUtil.createDocument(neiDoc.getPath(), inStream);
				
			}else{			
				String filePath = DocumentUtil.getFileStoreRootPath() + neiDoc.getPath();
				File newFile = new File(filePath);
				FileOutputStream fos = null;
				
				try{				
					AttributesImpl atts = new AttributesImpl();			
					fos = new FileOutputStream(newFile);
		     
					OutputFormat of = new OutputFormat();
					of.setIndent(1);
					of.setIndenting(true);
		        
					XMLSerializer serializer = new XMLSerializer(fos, of);		        
					
					handler = serializer.asContentHandler();		        
					handler.startDocument();
						atts.addAttribute("", "", "xmlns:hdr", "CDATA", "http://www.exchangenetwork.net/schema/v1.0/ExchangeNetworkDocument.xsd");
						atts.addAttribute("", "", "xmlns:xsi", "CDATA", "xsi:noNamespaceSchemaLocation=" + neiRootPath + "EN_NEI_Point_v3_0.xsd");
						atts.addAttribute("", "", "Id", "CDATA", "ID01");
					
					handler.startElement("","","hdr:Document",atts);		        
					getXMLHeader();			     
					
					atts.clear();
					
					String payloadTransType = "";
					if(transType.equals("00")){
						payloadTransType = "Original";							
					}else{
						payloadTransType = "Replacement";
					}
					atts.addAttribute("", "", "Operation", "CDATA", "Point|" + payloadTransType);					
					handler.startElement("", "", "hdr:Payload", atts);
			     
					atts.clear();
					atts.addAttribute("", "", "xmlns", "CDATA", "http://www.epa.gov/exchangenetwork");
					atts.addAttribute("", "", "schemaVersion", "CDATA", SCHEMA_VERSION);
			     	
					handler.startElement("", "", "PointSourceSubmissionGroup", atts);
					
					atts.clear();					
					atts.addAttribute("", "", "schemaVersion", "CDATA", SCHEMA_VERSION);
					
					handler.startElement("", "", "SystemRecordCountValues", atts);
					atts.clear();
					// retrieval of EU,EP,ER,PE,CE etc happens after header is generated, so used generic value.
						List<String> nodeValues = new ArrayList<String>();
						for(int i = 0;i< 8;i++){
							nodeValues.add("99999999999999999999");
						}			
						
						buildElements("SystemRecordCountValuesType", nodeValues);				
						
					handler.endElement("", "", "SystemRecordCountValues");
					
						generate(year);//get data
			     
			     	handler.endElement("", "", "PointSourceSubmissionGroup");			     	
			     	handler.endElement("", "", "hdr:Payload");			     
			     	handler.endElement("","","hdr:Document"); 
			     
			     	handler.endDocument ();
			     	fos.close();	     
		        
				}catch(Exception e){
					logger.debug("Exception XML Doc :" + e.getMessage(), e);
					throw new DAOException("Exception XML Doc :" + e.getMessage(), e);
				}
			}			

			logger.debug("File :" + neiDoc.getDescription() + " "
					+ neiDoc.getDocURL());
			neiList.put(neiDoc.getDescription() + " ", neiDoc.getDocURL());

			data = new StringBuffer();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new DAOException("Unable to generate NEI files :"
					+ ex.getMessage(), ex);
		}
	}

	/*
	 * If the value is null, output will be no of blank spaces, otherwise value
	 * will no of characters specified by length.
	 */
	private void formatData(Object value, int length, boolean blankSpace) {
		String ret;

		if (value == null) {
			value = "";
		}
		if (value.toString().length() > length) {
			value = value.toString().substring(0, length);
		}
		
		if(nif){
			ret = String.format("%-" + length + "s", value);
			data.append(ret);
		}else if(xml && !blankSpace){
			nodeValues.add(value.toString());
		}		
	}
		
	private void getXMLHeader() throws Exception {

		try {
			AttributesImpl attrs = new AttributesImpl();

			Calendar cal = Calendar.getInstance();
			String date = Integer.toString(cal.get(Calendar.YEAR)) + "-"
					+ Integer.toString((cal.get(Calendar.MONTH) + 1)) + "-"
					+ Integer.toString(cal.get(Calendar.DAY_OF_MONTH)) + "T"
					+ Integer.toString(cal.get(Calendar.HOUR_OF_DAY)) + ":"
					+ Integer.toString(cal.get(Calendar.MINUTE)) + ":"
					+ Integer.toString(cal.get(Calendar.SECOND));

			attrs.clear();
			
			handler.startElement("", "", "hdr:Header", attrs);

			handler.startElement("", "", "hdr:Author", attrs);			
			handler.characters(user.getFullName().toCharArray(), 0, user
					.getFullName().length());			
			handler.endElement("", "", "hdr:Author");

			handler.startElement("", "", "hdr:Organization", attrs);
			handler.characters("Ohio EPA".toCharArray(), 0, 8);
			handler.endElement("", "", "hdr:Organization");

			String s = "PointSource";
			handler.startElement("", "", "hdr:Title", attrs);
			handler.characters(s.toCharArray(), 0, 11);
			handler.endElement("", "", "hdr:Title");

			handler.startElement("", "", "hdr:CreationTime", attrs);
			handler.characters(date.toCharArray(), 0, date.length());
			handler.endElement("", "", "hdr:CreationTime");

			s = "Point source submission file";
			handler.startElement("", "", "hdr:Comment", attrs);
			handler.characters(s.toCharArray(), 0, 28);
			handler.endElement("", "", "hdr:Comment");

			s = "GetNEIPointDataByYear";
			handler.startElement("", "", "hdr:DataService", attrs);
			handler.characters(s.toCharArray(), 0, 21);
			handler.endElement("", "", "hdr:DataService");
			
			s = user.getLocation() + "," + user.getEMailAddress();			
			handler.startElement("", "", "hdr:ContactInfo", attrs);
			handler.characters(s.toCharArray(), 0, s.length());
			handler.endElement("", "", "hdr:ContactInfo");

			s = user.getEMailAddress();			
			handler.startElement("", "", "hdr:Notification", attrs);
			handler.characters(s.toCharArray(), 0, s.length());
			handler.endElement("", "", "hdr:Notification");

			handler.startElement("", "", "hdr:Sensitivity", attrs);
			// handler.characters("".toCharArray(), 0, 0);//TODO
			handler.endElement("", "", "hdr:Sensitivity");

			s = "GeographicCoverage";
			handler.startElement("", "", "hdr:Property", attrs);
			handler.startElement("", "", "hdr:name", attrs);
			handler.characters(s.toCharArray(), 0, 18);
			handler.endElement("", "", "hdr:name");
			handler.startElement("", "", "hdr:value", attrs);
			handler.characters(STATE_FIPS_CD.toCharArray(), 0, 2);
			handler.endElement("", "", "hdr:value");
			handler.endElement("", "", "hdr:Property");

			s = "InventoryYear";
			handler.startElement("", "", "hdr:Property", attrs);
			handler.startElement("", "", "hdr:name", attrs);
			handler.characters(s.toCharArray(), 0, 13);
			handler.endElement("", "", "hdr:name");
			handler.startElement("", "", "hdr:value", attrs);
			handler.characters(year.toString().toCharArray(), 0, 4);
			handler.endElement("", "", "hdr:value");
			handler.endElement("", "", "hdr:Property");

			handler.endElement("", "", "hdr:Header");

		} catch (Exception e) {
			logger.debug("Exception in generating XmlHeader :" + e.getMessage(),	e);
			throw new DAOException("Exception in XmlHeader :" + e.getMessage(), e);
		}

	}
	
	private void retrieveNodesFromSchema() throws DAOException {
		try {
			File file = new File(DocumentUtil.getFileStoreRootPath()
					+ neiRootPath + "EN_NEI_Point_v3_0.xsd");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			logger.debug("Root element "
					+ doc.getDocumentElement().getNodeName());

			nodeCollections = new HashMap<String, List<String>>();
			NodeList topNodeLst = doc.getElementsByTagName("xsd:complexType");

			for (int t = 0; t < topNodeLst.getLength(); t++) {
				Node nodeTop = topNodeLst.item(t);
				Element fstElmntop = (Element) nodeTop;

				String type = fstElmntop.getAttribute("name");
				List<String> values = new ArrayList<String>();

				NodeList nodeLst = fstElmntop
						.getElementsByTagName("xsd:sequence");

				for (int s = 0; s < nodeLst.getLength(); s++) {
					Node fstNode = nodeLst.item(s);
					if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
						Element fstElmnt = (Element) fstNode;
						NodeList fstNmElmntLst = fstElmnt
								.getElementsByTagName("xsd:element");

						// logger.debug("Element len - " +
						// fstNmElmntLst.getLength());

						for (int j = 0; j < fstNmElmntLst.getLength(); j++) {
							Element e = (Element) fstNmElmntLst.item(j);
							values.add(e.getAttribute("ref").substring(4));// +
																			// e.getAttribute("minOccurs")

							// logger.debug("attrib - " + e.getAttribute("ref"));
						}
					}
				}
				nodeCollections.put(type, values);

			}

		} catch (Exception e) {
			logger.error("Exception reading schema :" + e.getMessage(), e);
			throw new DAOException("Exception reading schema :"
					+ e.getMessage(), e);
		}
	}

	protected void buildElements(String type, List<String> values)
			throws DAOException {
		AttributesImpl attrs = new AttributesImpl();
		XMLUtil xmlUtil = XMLUtil.getInstance();

		try {
			List<String> nodes = nodeCollections.get(type);
			String nodeNames[] = nodes.toArray(new String[0]);
			String nodeValues[] = values.toArray(new String[0]);

			for (int i = 0; i < nodeNames.length; i++) {
				handler.startElement("", "", nodeNames[i], attrs);
				handler.characters(xmlUtil.utf2xml(nodeValues[i]).toCharArray(), 0,
						nodeValues[i].length());
				handler.endElement("", "", nodeNames[i]);
			}

		} catch (Exception e) {
			logger.debug("Building Elements failed :" + e.getMessage(), e);
			throw new DAOException("Building Elements failed :"
					+ e.getMessage(), e);
		}
	}
}
