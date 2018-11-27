package us.wy.state.deq.impact.pub.filter;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import us.oh.state.epa.stars2.bo.ApplicationService;
import us.oh.state.epa.stars2.bo.ComplianceReportService;
import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.MonitoringService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.bo.StackTestService;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReport;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitDocument;
import us.oh.state.epa.stars2.def.ComplianceReportStatusDef;
import us.oh.state.epa.stars2.def.EmissionsTestStateDef;
import us.oh.state.epa.stars2.def.IssuanceStatusDef;
import us.oh.state.epa.stars2.def.PermitDocTypeDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorReport;

/**
 * Servlet Filter implementation class FilestoreFilter
 */
public class FilestoreFilter implements Filter {

	public final static String APPLICATION_URI_PART = "/Applications/";
	public final static String EMISSIONS_INVENTORIES_URI_PART = "/EmissionsInventories/";
	public final static String STACK_TEST_URI_PART = "/StackTest/";
	public final static String COMPLIANCE_REPRORTS_URI_PART = "/ComplianceReports/";
	public final static String PERMITS_URI_PART = "/Permits/";
	public final static String MONITOR_GROUP_URI_PART = "/MonitorGroup/";
	public final static String MONITOR_REPORT_URI_PART = "/MonitorReport/";
	public final static String TMP_URI_PART = "/tmp/Facilities/";
	public final static String FACILITY_SEARCH_URI = "/facilities/facilitySearch.jsf";
	
	public final static String FACILITY_PROFILE_PDF_REGEX = "FacProfileDoc_F[0-9]{6}\\.pdf";
	public final static String APPLICATION_DETAIL_PDF_REGEX = "Application_A[0-9]{7}\\.pdf";
	public final static String INCLUDED_EUS_ONLY_APPLICATION_ZIP_REGEX = "A[0-9]{7}_appEUsonly\\.zip";
	public final static String COMPLIANCE_REPORT_DETAIL_PDF_REGEX = "complianceReport_[0-9]+\\.pdf";
	public final static String COMPLIANCE_REPORT_ZIP_REGEX = "CRPT[0-9]{6}\\.zip";
	public final static String STACK_TEST_DETAIL_PDF_REGEX = "stackTest_[0-9]+\\.pdf";
	public final static String STACK_TEST_ZIP_REGEX = "STCK[0-9]{6}\\.zip";
	public final static String EMISSIONS_REPORT_SUMMARY_PDF_REGEX = "EmissionsReportSummary_[0-9]+\\.pdf";
	public final static String EMISSIONS_REPORT_DETAIL_PDF_REGEX = "EmissionsReport_[0-9]+\\.pdf";
	public final static String EMISSIONS_INVENTORY_ZIP_REGEX = "EI[0-9]{7}\\.zip";
	
	private static Logger logger = Logger.getLogger(FilestoreFilter.class);

	private FilterConfig filterConfig;

	private ApplicationService applicationService;
	private EmissionsReportService emissionsReportService;
	private StackTestService stackTestService;
	private ComplianceReportService complianceReportService;
	private PermitService permitService;
	private MonitoringService monitoringService;
	
	public FilestoreFilter() {
		super();
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		this.filterConfig = null;
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		boolean redirect = true;

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String redirectURL = req.getContextPath() + FACILITY_SEARCH_URI;
		String uri = req.getRequestURI().substring(req.getRequestURI().indexOf("/filestore/"));
		
		if (uri.contains(TMP_URI_PART)) {
			
			redirect = !isThisTmpDocumentAccessibleByPublic(uri);
			
		} else {
			String[] uriParts = uri.split("/");
			Integer documentId = null;
			try {
				// last part of the uri has the document id
				documentId = Integer.parseInt(uriParts[uriParts.length - 1].split("\\.")[0]);
			
				if (uri.contains(APPLICATION_URI_PART)) {
					
					// e.g., /filestore/Facilities/F026888/Applications/3499/114443.txt
					Integer applicationId = Integer.parseInt(uriParts[5]);
					redirect = isTradeSecretApplicationDocument(documentId) || !isApplicationSubmitted(applicationId);
					
				} else if (uri.contains(EMISSIONS_INVENTORIES_URI_PART)) {
					
					// e.g., /filestore/Facilities/F000077/EmissionsInventories/476/114445.txt
					Integer reportId = Integer.parseInt(uriParts[5]);
					redirect = isTradeSecretEmissionsReportDocument(documentId) || !isEmissionsReportApproved(reportId);
					
				} else if (uri.contains(COMPLIANCE_REPRORTS_URI_PART)) {
					
					// e.g., /filestore/Facilities/F023345/ComplianceReports/15692/114447.txt
					Integer complianceReportId = Integer.parseInt(uriParts[5]);
					redirect = isTradeSecretComplianceReportDocument(documentId)
							|| !isComplianceReportReviewedBeforeToday(complianceReportId);
					
				} else if (uri.contains(STACK_TEST_URI_PART)) {
					
					// e.g., /filestore/Facilities/F000019/StackTest/16778/114449.txt
					Integer stackTestId = Integer.parseInt(uriParts[5]);
					redirect = isTradeSecretStackTestDocument(documentId) || !isStackTestReviewedBeforeToday(stackTestId);
					
				} else if (uri.contains(PERMITS_URI_PART)) {
					
					// e.g., /filestore/Facilities/F020004/Permits/815/143739.txt
					Integer permitId = Integer.parseInt(uriParts[5]);
					redirect = !isThisPermitDocumentAccessibleByPublic(permitId, documentId);
					
				} else if (uri.contains(MONITOR_GROUP_URI_PART)) {
					
					if (uri.contains(MONITOR_REPORT_URI_PART)) {
						
						// is a monitor report attachment
						// e.g., /filestore//MonitorGroup/60/MonitorReport/1381/167514.pdf
						Integer monitorReportId = Integer.parseInt(uriParts[6]);
						redirect = !isMonitorReportSubmitted(monitorReportId);
						
					} else {
						
						// is a monitor group attachment
						// e.g., /filestore//MonitorGroup/10/73354.pdf
						// it is a monitor group attachment. allow access
						redirect = false;
						
					}
				}
			} catch (NumberFormatException nfe) {
				// if you get here then the last part of the url does not
				// contain a valid numeric document id. e.g., ApplicationA0006044.pdf
				logger.error("Error converting string to integer. "
								+ nfe.getMessage());
			}
		}

		if (redirect) {
			logger.warn("Attempt to access an unauthorized document");
			res.sendRedirect(redirectURL);
		} else {
			chain.doFilter(request, response);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		WebApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(fConfig.getServletContext());

		this.filterConfig = fConfig;

		this.applicationService = ctx.getBean(ApplicationService.class);
		this.emissionsReportService = ctx.getBean(EmissionsReportService.class);
		this.complianceReportService = ctx.getBean(ComplianceReportService.class);
		this.stackTestService = ctx.getBean(StackTestService.class);
		this.permitService = ctx.getBean(PermitService.class);
		this.monitoringService = ctx.getBean(MonitoringService.class);

	}
	
	/**
	 * 
	 * @param documentId
	 * @return true if this application documentId represents a trade secret document, false otherwise
	 * @throws DAOException
	 */
	private boolean isTradeSecretApplicationDocument(Integer documentId) throws DAOException {
		return (null != this.applicationService.retrieveApplicationDocumentByTradeSecrectDocId(documentId))
				? true : false;
	}
	
	/**
	 * 
	 * @param documentId
	 * @return true if this emissions report documentId represents a trade secret document, false otherwise
	 * @throws DAOException
	 */
	private boolean isTradeSecretEmissionsReportDocument(Integer documentId) throws DAOException {
		return (null != this.emissionsReportService.retrieveReportDocumentByTradeSecrectDocId(documentId))
				? true : false;
	}
	
	/**
	 * 
	 * @param documentId
	 * @return  true if this compliance report documentId represents a trade secret document, false otherwise
	 * @throws DAOException
	 */

	private boolean isTradeSecretComplianceReportDocument(Integer documentId) throws DAOException {
		return (null != this.complianceReportService.retrieveCRTradeSecretAttachmentInfoById(documentId))
				? true : false;
	}

	/**
	 * 
	 * @param documentId
	 * @return  true if this stack test documentId represents a trade secret document, false otherwise
	 * @throws DAOException
	 */
	private boolean isTradeSecretStackTestDocument(Integer documentId) throws DAOException {
		return (null != this.stackTestService.retrieveStTradeSecretAttachmentInfoById(documentId))
				? true : false;
	}

	/**
	 * 
	 * @param applicationId
	 * @return true if this application has a not null submitted date, false otherwise
	 * @throws RemoteException
	 */
	private boolean isApplicationSubmitted(Integer applicationId) throws RemoteException {
		Application application = this.applicationService.retrieveBasicApplicationById(applicationId);
		return (null != application && null != application.getSubmittedDate()) ? true : false;
	}
	
	/**
	 * 
	 * @param applicationNbr
	 * @return true if this application has a not null submitted date, false otherwise
	 * @throws RemoteException
	 */
	private boolean isApplicationSubmitted(String applicationNbr) throws RemoteException {
		Application application = this.applicationService.retrieveBasicApplicationByNbr(applicationNbr);
		return (null != application && null != application.getSubmittedDate()) ? true : false;
	}

	/**
	 * @param permitId 
	 * @param documentId
	 * @return true if this permit document is allowed to be accessed on the public website, false otherwise
	 * @throws RemoteException
	 */
	private boolean isThisPermitDocumentAccessibleByPublic(Integer permitId, Integer documentId)
			throws RemoteException {
		PermitDocument document = null;
		boolean accessible = false;

		document = this.permitService.retrievePermitDocumentById(documentId);

		if (null != document) {
			String documentTypeCd = document.getPermitDocTypeCD();
			Permit permit = this.permitService.retrievePermitLight(permitId);
			if(null != permit) {
				boolean legacyPermit = permit.isLegacyPermit();
			
				if (documentTypeCd.equals(PermitDocTypeDef.NSR_PUBLIC_NOTICE_DOCUMENT)
					|| documentTypeCd.equals(PermitDocTypeDef.ANALYSIS_DOCUMENT)
					|| documentTypeCd.equals(PermitDocTypeDef.TV_PUBLIC_NOTICE_DOCUMENT)
					|| documentTypeCd.equals(PermitDocTypeDef.DRAFT_STATEMENT_BASIS)
					|| documentTypeCd.equals(PermitDocTypeDef.DRAFT_TV_PERMIT_DOCUMENT)) {
	
					// permit should be legacy or issued draft to access these document types
					String draftIssuanceStatusCd = permit.getDraftIssuanceStatusCd();
					if (legacyPermit || (null != draftIssuanceStatusCd
							&& draftIssuanceStatusCd.equals(IssuanceStatusDef.ISSUED))) {
						accessible = true;
					}
				} else if(documentTypeCd.equals(PermitDocTypeDef.TV_COMMENTS)
						|| documentTypeCd.equals(PermitDocTypeDef.TV_RESPONSE_TO_COMMENTS)
						|| documentTypeCd.equals(PermitDocTypeDef.PROPOSED_STATEMENT_BASIS)
						|| documentTypeCd.equals(PermitDocTypeDef.PROPOSED_TV_PERMIT_DOCUMENT)) {
					
					// permit should be legacy or issued proposed permit to access these document types
					String ppIssunaceStatusCd = permit.getPpIssuanceStatusCd();
					if (legacyPermit
							|| (null != ppIssunaceStatusCd && ppIssunaceStatusCd.equals(IssuanceStatusDef.ISSUED))) {
						accessible = true;
					}
				} else if(documentTypeCd.equals(PermitDocTypeDef.NSR_COMMENTS) 
						|| documentTypeCd.equals(PermitDocTypeDef.NSR_RESPONSE_TO_COMMENTS)
						|| documentTypeCd.equals(PermitDocTypeDef.NSR_FINAL_PERMIT_WAIVER_PACKAGE)
						|| documentTypeCd.equals(PermitDocTypeDef.FINAL_STATEMENT_BASIS)
						|| documentTypeCd.equals(PermitDocTypeDef.FINAL_TV_PERMIT_DOCUMENT)) {
					
					// permit should be legacy or issued final to access these document types
					String finalIssuanceStatusCd = permit.getFinalIssuanceStatusCd();
					if (legacyPermit || (null != finalIssuanceStatusCd
							&& finalIssuanceStatusCd.equals(IssuanceStatusDef.ISSUED))) {
						accessible = true;
					}
				}
			}
		}

		return accessible;
	}

	/**
	 * 
	 * @param reportId
	 * @return true if this emissions report is approved, false otherwise
	 * @throws DAOException
	 */
	private boolean isEmissionsReportApproved(Integer reportId) throws DAOException {
		EmissionsReport report = this.emissionsReportService.retrieveEmissionsReportById(reportId);
		return (null != report && report.getRptReceivedStatusCd().equals(ReportReceivedStatusDef.DOLAA_APPROVED)) 
				? true : false;
	}
	
	/**
	 * 
	 * @param eiId
	 * @return true if this emissions report is approved, false otherwise
	 * @throws RemoteException
	 */
	private boolean isEmissionsReportApproved(String reportId) throws RemoteException {
		boolean ret = false;

		EmissionsReportSearch searchObj = new EmissionsReportSearch();
		searchObj.setEmissionsInventoryId(reportId);

		EmissionsReportSearch[] searchedReports = this.emissionsReportService.searchEmissionsReports(searchObj, false);
		if (searchedReports.length > 0) {
			// there should be only one matching report
			EmissionsReportSearch report = searchedReports[0];
			if (null != report && report.getReportingState().equals(ReportReceivedStatusDef.DOLAA_APPROVED)) {
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * 
	 * @param complianceReportId
	 * @return true if this compliance report has been submitted and reviewed before today, false otherwise
	 * @throws RemoteException
	 */
	private boolean isComplianceReportReviewedBeforeToday(Integer complianceReportId) throws RemoteException {
		boolean ret = false;
		
		ComplianceReport complianceReport = this.complianceReportService
				.retrieveComplianceReportOnly(complianceReportId, true);

		if (null != complianceReport) {
			boolean submitted = null != complianceReport.getSubmittedDate()
					&& complianceReport.getReportStatus()
					.equals(ComplianceReportStatusDef.COMPLIANCE_STATUS_SUBMITTED);
			boolean reviewed = null != complianceReport.getDapcDateReviewed();
			
			if(submitted && reviewed) {
				LocalDate today = new LocalDate();
				LocalDate reviewedDate = new LocalDate(complianceReport.getDapcDateReviewed().getTime());
				
				boolean reviewedBeforeToday = reviewedDate.isBefore(today);
			
				if(reviewedBeforeToday) {
					ret = true;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @param crptId
	 * @return true if this compliance report has been submitted and reviewed before today, false otherwise
	 * @throws RemoteException
	 */
	private boolean isComplianceReportSubmittedBeforeToday(String crptId) throws RemoteException {
		boolean ret = false;

		ComplianceReportList[] complianceReports = this.complianceReportService.searchComplianceReports(crptId, null,
				null, null, null, null, null, null, null, null, null, null, null, true, true, null, null, null, null);
		if (complianceReports.length > 0) {
			// there should be only one matching report
			ComplianceReportList complianceReport = complianceReports[0]; 

			boolean submitted = null != complianceReport.getSubmittedDate()
					&& complianceReport.getReportStatus().equals(ComplianceReportStatusDef.COMPLIANCE_STATUS_SUBMITTED);
			boolean reviewed = null != complianceReport.getReviewDate();

			if (submitted && reviewed) {
				LocalDate today = new LocalDate();
				LocalDate reviewedDate = new LocalDate(complianceReport.getReviewDate().getTime());

				boolean reviewedBeforeToday = reviewedDate.isBefore(today);

				if (reviewedBeforeToday) {
					ret = true;
				}
			}
		}

		return ret;
	}

	/**
	 * 
	 * @param stackTestId
	 * @return true if this stack test has been submitted and reviewed before today, false otherwise
	 * @throws RemoteException
	 */
	private boolean isStackTestReviewedBeforeToday(Integer stackTestId) throws RemoteException {
		boolean ret = false;
		
		StackTest stackTest = this.stackTestService.retrieveStackTestRowOnly(stackTestId);
		
		if(null != stackTest) {
			boolean submitted = null != stackTest.getSubmittedDate()
						&&  stackTest.getEmissionTestState().equals(EmissionsTestStateDef.SUBMITTED);
			boolean reviewed = null != stackTest.getDateEvaluated(); 
			
			if(submitted && reviewed) {
				LocalDate today = new LocalDate();
				LocalDate reviewedDate = new LocalDate(stackTest.getDateEvaluated().getTime());
				
				boolean reviewedBeforeToday = reviewedDate.isBefore(today);
				
				if(reviewedBeforeToday) {
					ret = true;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @param stckId
	 * @return true if this stack test has been submitted and reviewed before today, false otherwise
	 * @throws RemoteException
	 */
	private boolean isStackTestReviewedBeforeToday(String stckId) throws RemoteException {
		boolean ret = false;
		
		StackTest[] stackTests = this.stackTestService.retrieveStackTestsBySearch(null, null, null, null, null,null,
				null, null, null, null, null, false, null, null, null, null, stckId, null);
		
		if(stackTests.length > 0) {
			StackTest stackTest = stackTests[0]; //there should be only one matching stack test
			boolean submitted = null != stackTest.getSubmittedDate()
						&&  stackTest.getEmissionTestState().equals(EmissionsTestStateDef.SUBMITTED);
			boolean reviewed = null != stackTest.getDateEvaluated(); 
			
			if(submitted && reviewed) {
				LocalDate today = new LocalDate();
				LocalDate reviewedDate = new LocalDate(stackTest.getDateEvaluated().getTime());
				
				boolean reviewedBeforeToday = reviewedDate.isBefore(today);
				
				if(reviewedBeforeToday) {
					ret = true;
				}
			}
		}
	 	
		return ret;
	}

	/**
	 * 
	 * @param monitorReportId
	 * @return true if this monitor report is submitted, false otherwise
	 * @throws DAOException
	 */
	private boolean isMonitorReportSubmitted(Integer monitorReportId) throws DAOException {
		boolean ret = false;

		MonitorReport report = this.monitoringService.retrieveMonitorReport(monitorReportId);
		if (null != report && null != report.getSubmittedDate()) {
			ret = true;
		}

		return ret;
	}
	
	/**
	 * 
	 * @param documentName
	 * @return true if this documentName is allowed to be accessed on the public website, false otherwise
	 * @throws RemoteException
	 */
	private boolean isThisTmpDocumentAccessibleByPublic(String uri) throws RemoteException {
		
		/*
		 * Documents generated upon clicking on the Download/Print button. Only the following generated
		 * documents should be accessible from the public website. These document are stored under the
		 * /filestore/tmp/Facilities path in the filestore.
		 * 
		 * 1. Facility profile pdf				e.g., /filestore/tmp/Facilities/F020004/FacProfileDoc_F020004.pdf
		 * 2. Application detail pdf			e.g., /filestore/tmp/Facilities/F000536/Application_A0006044.pdf
		 * 3. Application zip file				e.g., /filestore/tmp/Facilities/F000536/A0006044_appEUsonly.zip
		 * 4. Compliance report detail pdf		e.g., /filestore/tmp/Facilities/F000536/complianceReport_27074.pdf
		 * 5. Compliance report zip file		e.g., /filestore/tmp/Facilities/F000536/CRPT027074.zip
		 * 6. Stack test detail pdf				e.g., /filestore/tmp/Facilities/F000536/stackTest_29389.pdf
		 * 7. Stack test zip file				e.g., /filestore/tmp/Facilities/F000536/STCK029389.zip
		 * 8. Emissions inventory summary pdf	e.g., /filestore/tmp/Facilities/F000390/EmissionsReportSummary_466.pdf
		 * 9. Emissions inventory detail pdf	e.g., /filestore/tmp/Facilities/F000390/EmissionsReport_466.pdf
		 * 10. Emissions inventory zip file	e.g., /filestore/tmp/Facilities/F000390/EI0000466.zip
		 * 
		*/
		
		String[] uriParts = uri.split("/");
		String documentName = uriParts[uriParts.length - 1];
		
		if(Pattern.matches(FACILITY_PROFILE_PDF_REGEX, documentName)) {
			return true;
		}
		
		if(Pattern.matches(APPLICATION_DETAIL_PDF_REGEX, documentName)) {
			// Application_A0006044.pdf - application id begins at index 12
			int beginIndex = 12;
			int endIndex = documentName.indexOf('.');
			String applicationNbr = documentName.substring(beginIndex, endIndex);
			if(isApplicationSubmitted(applicationNbr)) {
				return true;
			}
		}
		
		if(Pattern.matches(INCLUDED_EUS_ONLY_APPLICATION_ZIP_REGEX, documentName)) {
			// A0006044_appEUsonly.zip  application number begins at index 0
			int beginIndex = 0;
			int endIndex = documentName.indexOf('_');
			String applicationNbr = documentName.substring(beginIndex, endIndex);
			if(isApplicationSubmitted(applicationNbr)) {
				return true;
			}
		}
		
		if(Pattern.matches(COMPLIANCE_REPORT_DETAIL_PDF_REGEX, documentName)) {
			// complianceReport_27074.pdf - compliance report id begins at index 17
			int beginIndex = 17;
			int endIndex = documentName.indexOf('.');
			Integer reportId = Integer.parseInt(documentName.substring(beginIndex, endIndex));
			if(isComplianceReportReviewedBeforeToday(reportId)) {
				return true;
			}
		}
		
		if(Pattern.matches(COMPLIANCE_REPORT_ZIP_REGEX, documentName)) {
			// CRPT027074.zip - compliance report id begins at index 0
			int beginIndex = 0;
			int endIndex = documentName.indexOf('.');
			String crptId = documentName.substring(beginIndex, endIndex);
			if(isComplianceReportSubmittedBeforeToday(crptId)) {
				return true;
			}
		}
		
		if(Pattern.matches(STACK_TEST_DETAIL_PDF_REGEX, documentName)) {
			// stackTest_29389.pdf - stack test id begins at index 10
			int beginIndex = 10;
			int endIndex = documentName.indexOf('.');
			Integer stackTestId = Integer.parseInt(documentName.substring(beginIndex, endIndex));
			if(isStackTestReviewedBeforeToday(stackTestId)) {
				return true;
			}
		}
		
		if(Pattern.matches(STACK_TEST_ZIP_REGEX, documentName)) {
			// STCK029389.zip - stack test id begins at index 0
			int beginIndex = 0;
			int endIndex = documentName.indexOf('.');
			String stckId = documentName.substring(beginIndex, endIndex);
			if(isStackTestReviewedBeforeToday(stckId)) {
				return true;
			}
		}
		
		if(Pattern.matches(EMISSIONS_REPORT_SUMMARY_PDF_REGEX, documentName)) {
			// EmissionsReportSummary_466.pdf - emissions inventory id begins at index 23
			int beginIndex = 23;
			int endIndex = documentName.indexOf('.');
			Integer emissionsReportId = Integer.parseInt(documentName.substring(beginIndex, endIndex));
			if(isEmissionsReportApproved(emissionsReportId)) {
				return true;
			}
		}
		
		if(Pattern.matches(EMISSIONS_REPORT_DETAIL_PDF_REGEX, documentName)) {
			// EmissionsReport_466.pdf -  - emissions inventory id begins at index 16
			int beginIndex = 16;
			int endIndex = documentName.indexOf('.');
			Integer emissionsReportId = Integer.parseInt(documentName.substring(beginIndex, endIndex));
			if(isEmissionsReportApproved(emissionsReportId)) {
				return true;
			}
		}
		
		
		if(Pattern.matches(EMISSIONS_INVENTORY_ZIP_REGEX, documentName)) {
			// EI0000466.zip - emissions inventory id begins at index 0
			int beginIndex = 0;
			int endIndex = documentName.indexOf('.');
			String eiId = documentName.substring(beginIndex, endIndex);
			if(isEmissionsReportApproved(eiId)) {
				return true;
			}
		}
				
		return false;
	}
}
