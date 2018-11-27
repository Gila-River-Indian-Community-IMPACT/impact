package us.oh.state.epa.stars2.bo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipOutputStream;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.DocumentException;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.emissionsReport.UsEpaEisReport;
import us.oh.state.epa.stars2.bo.helpers.FacilityHelper;
import us.oh.state.epa.stars2.bo.helpers.InfrastructureHelper;
import us.oh.state.epa.stars2.database.dao.CorrespondenceDAO;
import us.oh.state.epa.stars2.database.dao.DocumentDAO;
import us.oh.state.epa.stars2.database.dao.EmissionsReportDAO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.InvoiceDAO;
import us.oh.state.epa.stars2.database.dao.ServiceCatalogDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestedPollutant;
import us.oh.state.epa.stars2.database.dbObjects.ceta.TestVisitDate;
import us.oh.state.epa.stars2.database.dbObjects.ceta.TestedEmissionsUnit;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.document.TmpDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.DefaultStackParms;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionTotal;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.Emissions;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsMaterialActionUnits;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEU;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportNote;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportPeriod;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsRptInfo;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsVariable;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FacilityAppInfo;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FacilityPermitInfo;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FacilityRptInfo;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FacilityYearPair;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FireRow;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.IntegerPair;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.MultiEstablishment;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.NTVContactType;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.PollutantPair;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.EuEmission;
import us.oh.state.epa.stars2.database.dbObjects.facility.EventLog;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityNote;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.database.dbObjects.invoice.Invoice;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceDetail;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCDataImportPollutant;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCNonChargePollutant;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCPollutant;
import us.oh.state.epa.stars2.database.dbObjects.util.DbInteger;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivityLight;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.CetaStackTestMethodDef;
import us.oh.state.epa.stars2.def.CetaStackTestTestingMethodDef;
import us.oh.state.epa.stars2.def.ContactTypeDef;
import us.oh.state.epa.stars2.def.ContentTypeDef;
import us.oh.state.epa.stars2.def.CorrespondenceDirectionDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EmissionCalcMethodDef;
import us.oh.state.epa.stars2.def.EmissionReportsDef;
import us.oh.state.epa.stars2.def.EmissionUnitReportingDef;
import us.oh.state.epa.stars2.def.EmissionsAttachmentTypeDef;
import us.oh.state.epa.stars2.def.EmissionsTestStateDef;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.EventLogTypeDef;
import us.oh.state.epa.stars2.def.InvoiceState;
import us.oh.state.epa.stars2.def.MaterialDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.RegulatoryRequirementTypeDef;
import us.oh.state.epa.stars2.def.ReportEisStatusDef;
import us.oh.state.epa.stars2.def.ReportOfEmissionsStateDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.def.ReportStatusDef;
import us.oh.state.epa.stars2.def.RevenueTypeDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.def.WorkflowProcessDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.ContactUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;
import us.oh.state.epa.stars2.webcommon.facility.FacilityValidation;
import us.oh.state.epa.stars2.webcommon.pdf.PdfGeneratorBase;
import us.oh.state.epa.stars2.webcommon.pdf.emissionsReport.EmissionsReportPdfGenerator;
import us.oh.state.epa.stars2.webcommon.pdf.emissionsReport.NtvEmissionsReportPdfGenerator;
import us.oh.state.epa.stars2.webcommon.reports.ComparePair;
import us.oh.state.epa.stars2.webcommon.reports.EmissionCalcMethod;
import us.oh.state.epa.stars2.webcommon.reports.EmissionRow;
import us.oh.state.epa.stars2.webcommon.reports.ExpressionEval;
import us.oh.state.epa.stars2.webcommon.reports.MissingFIREFactor;
import us.oh.state.epa.stars2.webcommon.reports.NtvReport;
import us.oh.state.epa.stars2.webcommon.reports.ReportProfileBase;
import us.oh.state.epa.stars2.webcommon.reports.ReportTemplates;
import us.oh.state.epa.stars2.workflow.engine.ProcessFlow;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.app.emissionsReport.EiDataImport;
import us.wy.state.deq.impact.app.emissionsReport.EiDataImportCriteria;
import us.wy.state.deq.impact.app.emissionsReport.EiDataImportPeriod;
import us.wy.state.deq.impact.app.emissionsReport.EiDataImportRow.FireVariable;
import us.wy.state.deq.impact.app.emissionsReport.EiDataImportRow.Pollutant;
import us.wy.state.deq.impact.bo.ContactService;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor;


/**
 * <p>
 * Title: EmissionsReportBO
 * </p>
 * 
 * <p>
 * Description: This is the Business Object for reporting.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * @author Dleinbaugh
 * @version 1.0
 */
@Transactional(rollbackFor=Exception.class)
@Service
public class EmissionsReportBO extends BaseBO implements EmissionsReportService {
	
	@Autowired
	InfrastructureHelper infraHelper;
	
	@Autowired ContactService contactService;
	@Autowired FacilityService facilityService;
	@Autowired InfrastructureService infraService;
	@Autowired ReadWorkFlowService wfService;
	@Autowired DocumentService docService;
	@Autowired StackTestService stackTestService;
	
    private static String unknownEU = "unknown-";
    static Logger logger = Logger.getLogger(EmissionsReportBO.class);

    private String dirName = null;  // Directory path to create.
    
    
    public ContactService getContactService() {
		return contactService;
	}

	public void setContactService(ContactService contactService) {
		this.contactService = contactService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public InfrastructureService getInfraService() {
		return infraService;
	}

	public void setInfraService(InfrastructureService infraService) {
		this.infraService = infraService;
	}

	public ReadWorkFlowService getWfService() {
		return wfService;
	}

	public void setWfService(ReadWorkFlowService wfService) {
		this.wfService = wfService;
	}

	public DocumentService getDocService() {
		return docService;
	}

	public void setDocService(DocumentService docService) {
		this.docService = docService;
	}

	public StackTestService getStackTestService() {
		return stackTestService;
	}

	public void setStackTestService(StackTestService stackTestService) {
		this.stackTestService = stackTestService;
	}

	private String getSchema(boolean staging) {
        String ret = null;

        if (isPortalApp()) {
            if (staging) {
                ret = getSchema(CommonConst.STAGING_SCHEMA);
            } else {
                ret = getSchema(CommonConst.READONLY_SCHEMA);
            }
        }

        return ret;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void createSubmitReport(String facilityId, int fpId,
            NtvReport report, Integer userId) throws DAOException {

        Facility facility = new Facility();
        facility.setFacilityId(facilityId);
        facility.setFpId(fpId);
        try {
            facilityService.retrieveFacilityAddresses(facility); // Note, not needed because county not used for NTV
            facility.setAllContacts(facilityService.retrieveFacilityContacts(facilityId)); // TODO  DENNIS -- do we care any more about contacts????
        } catch(RemoteException re) {
            logger.error("retrieveFacilityAddresses() failed for " + facilityId, re);
            throw new DAOException("retrieveFacilityAddresses() failed for " + facilityId, re);
        }
        if(report.getReport1() != null) {
            report.getReport1().setAutoGenerated(true);
        }
        if(report.getReport2() != null) {
            report.getReport2().setAutoGenerated(true);
        }
        Transaction trans = TransactionFactory.createTransaction();
        NtvReport createdRpt =  createEmissionsReport(facility, report, false, trans);
        // Because report is auto-generated, will will bypass validation
        // and always require DO/LAA review.
        ntvSubmit(createdRpt, facility, ReportReceivedStatusDef.SUBMITTED,
                0, null, null, userId,  false);

    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public NtvReport createEmissionsReport(Facility facility,
            NtvReport report) throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        return createEmissionsReport(facility, report, false, trans);
    }

//    private void prtDebug(NtvReport report, String tag) { // added by DENNIS
//        logger.error(tag + ": " + report.getPrimary().getEmissionTotalsTreeSet().size() + ":" + report.getPrimary().getEmissionTotals().size());
//    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public NtvReport createEmissionsReport(Facility facility,
            NtvReport report, boolean calledFromSubmit, Transaction trans) throws DAOException {
        dirName = null;
        NtvReport rpt = null;
        EmissionsReport rpt1 = null;
        EmissionsReport rpt2 = null;
        String county = null;
        if(facility.getPhyAddr() != null)  {
            county = facility.getPhyAddr().getCountyCd();
        }
        EmissionsReport oldRpt1 = null;
        EmissionsReport oldRpt2 = null;
        try {
            EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(trans);

            if(report.getReport1() != null) {
                if(report.getReport1().getAttachments().size() > 0){
                    oldRpt1 = report.getReport1();//to copy report attachments.
                }
                // Must add it in later.
                report.getReport1().setCompanionReport(null);
                createEmissionsReport(county, facility, report.getReport1(), false, trans);                
            }

            if(report.getReport2() != null) { 
                if(report.getReport2().getAttachments().size() > 0){
                    oldRpt2 = report.getReport2();//to copy report attachments.
                }
                // Must add it in later.
                report.getReport2().setCompanionReport(null);
                createEmissionsReport(county, facility, report.getReport2(), false, trans);                               
            }            

            if(report.getReport1() != null) {
                if(report.getReport2() != null) {
                    report.getReport1().setCompanionReport(report.getReport2().getEmissionsRptId());
                    report.getReport2().setCompanionReport(report.getReport1().getEmissionsRptId());
                    emissionsRptDAO.modifyEmissionsReport(report.getReport1(), null);
                    emissionsRptDAO.modifyEmissionsReport(report.getReport2(), null);
                }               

                rpt1 = retrieveEmissionsReport(report.getReport1().getEmissionsRptId(), true, trans);
            }
            if(report.getReport2() != null) {
                rpt2 = retrieveEmissionsReport(report.getReport2().getEmissionsRptId(), true, trans);             
            }          

            rpt = new NtvReport(rpt1, rpt2);

            if(!calledFromSubmit) {
                //create directory for report documents
                createReportDir(facility, rpt.getPrimary());
                if(oldRpt1 != null){                                
                    copyReportAttachments(oldRpt1, rpt1, false, trans);                 
                }
                if(oldRpt2 != null){                                
                    copyReportAttachments(oldRpt2, rpt2, false, trans);                 
                }
            } else {
                // already have files but need to copy db objects
                if(oldRpt1 != null){                                
                    copyReportAttachments(oldRpt1, rpt1, calledFromSubmit, trans);                 
                }
                if(oldRpt2 != null){                                
                    copyReportAttachments(oldRpt2, rpt2, calledFromSubmit, trans);                 
                }
            }

        } catch(DAOException e) {
            deleteReportDir();
            throw e;
        }
        return rpt;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public EmissionsReport createEmissionsReport(Facility facility,
            EmissionsReport newReport) throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        try {
            String county = facility.getPhyAddr().getCountyCd();
            createEmissionsReport(county, facility, newReport, false, trans);
            trans.complete();
        } catch (RemoteException de) {
            cancelTransaction(trans, de);  // does throw
        } finally {
            closeTransaction(trans);
        }
        return newReport;
    }

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void createEmissionsReport(String county, Facility facility, EmissionsReport newReport,
			boolean submitFromStaging, Transaction trans) throws DAOException {

		dirName = null;
		Integer reportId = null;

		try {

			InfrastructureDAO infraDAO = infrastructureDAO(getSchema(true));
			EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(true));
			emissionsRptDAO.setTransaction(trans);
			infraDAO.setTransaction(trans);

			if (!newReport.isFromCSV()) {
				newReport.setEisStatusCd(ReportEisStatusDef.NOT_FILED);
			}
			
			if (ReportReceivedStatusDef.SUBMITTED_CAUTION.equals(newReport.getRptReceivedStatusCd())
					|| ReportReceivedStatusDef.SUBMITTED.equals(newReport.getRptReceivedStatusCd())) {
				newReport.setEisStatusCd(ReportEisStatusDef.SUBMITTED);
			}

			// Create the report.
			if (newReport.getRptReceivedStatusCd() == null) {
				newReport.setRptReceivedStatusCd(ReportReceivedStatusDef.NOT_FILED);
			}

			if (!submitFromStaging && !newReport.isFromCSV()) {
				newReport.setFpId(facility.getFpId());
			} 

			EmissionsReport tempReport = emissionsRptDAO.createEmissionsReport(newReport);

			reportId = tempReport.getEmissionsRptId();
			newReport.setEmissionsRptId(reportId);

			List<EmissionsRptInfo> infos = getYearlyReportingInfo(facility.getFacilityId());
			for (EmissionsRptInfo info : infos) {

				if (info.getScEmissionsReportId() != null && info.getYear() != null
						&& info.getYear().equals(newReport.getReportYear()) && info.getContentTypeCd() != null
						&& (info.getContentTypeCd().equals(newReport.getCreateContentTypeCd())
								|| info.getContentTypeCd().equals(newReport.getContentTypeCd()))) {

					emissionsReportDAO(getSchema(isPortalApp())).associateSCEmissionsReports(newReport,
							info.getScEmissionsReportId());

				}
			}

			// Only create total lines if submit from staging.
			if (submitFromStaging || newReport.isFromCSV()) {
				// Create the totals lines.
				for (EmissionTotal e : newReport.getEmissionTotalsTreeSet()) {
					e.setEmissionsRptId(newReport.getEmissionsRptId());
					emissionsRptDAO.createEmissionTotal(e);
				}
			}

			for (EmissionsReportEU eu : newReport.getEus()) {

				eu.setEmissionsRptId(reportId);

				// Create the report EU...
				emissionsRptDAO.createReportEU(eu);

				for (EmissionsReportPeriod period : eu.getPeriods()) {

					period.setEmissionsRptId(reportId);
					period = emissionsRptDAO.createEmissionPeriod(period);
					emissionsRptDAO.addReportPeriod(period.getEmissionPeriodId(), eu.getCorrEpaEmuId(), null, reportId);

					for (Emissions emission : period.getEmissions().values()) {
						emission.setEmissionPeriodId(period.getEmissionPeriodId());
						emissionsRptDAO.createEmissions(emission);
					}

					for (EmissionsMaterialActionUnits mau : period.getMaus()) {

						if (!mau.isBelongs()) {
							continue;
						}

						mau.setEmissionPeriodId(period.getEmissionPeriodId());
						emissionsRptDAO.createMaterialActionUnits(mau);
					}

					for (EmissionsVariable v : period.getVars()) {
						v.setEmissionPeriodId(period.getEmissionPeriodId());
						emissionsRptDAO.createVariable(v);
					}
				}
			}

			// Deal with the report EU groups...
			for (EmissionsReportEUGroup group : newReport.getEuGroups()) {

				group = emissionsRptDAO.createReportEUGroup(group);
				int groupId = group.getReportEuGroupID();
				emissionsRptDAO.addEUGroupToReport(groupId, reportId);

				EmissionsReportPeriod period = emissionsRptDAO.createEmissionPeriod(group.getPeriod());
				emissionsRptDAO.addReportPeriod(period.getEmissionPeriodId(), null, groupId, reportId);

				group.setPeriod(period);

				for (Emissions emission : period.getEmissions().values()) {
					emission.setEmissionPeriodId(period.getEmissionPeriodId());
					emissionsRptDAO.createEmissions(emission);
				}

				for (EmissionsMaterialActionUnits mau : period.getMaus()) {
					mau.setEmissionPeriodId(period.getEmissionPeriodId());
					emissionsRptDAO.createMaterialActionUnits(mau);
				}

				for (EmissionsVariable v : period.getVars()) {
					v.setEmissionPeriodId(period.getEmissionPeriodId());
					emissionsRptDAO.createVariable(v);
				}

				for (Integer emuId : group.getEus()) {
					emissionsRptDAO.addReportEUGroup(groupId, emuId, reportId);
				}
			}
			
			for (EmissionsDocumentRef edr : newReport.getAttachments()) {
				edr.setEmissionsRptId(newReport.getEmissionsRptId());
				if (edr.getPublicDoc() != null) {
					edr.getPublicDoc().setEmissionsRptId(newReport.getEmissionsRptId());
				}
				if (edr.getTradeSecretDoc() != null) {
					edr.getTradeSecretDoc().setEmissionsRptId(newReport.getEmissionsRptId());
				}
			}

			// Copy attachments if TV/SMTV report.
			if (!submitFromStaging) {

				// Create directory for report documents.
				createReportDir(facility, newReport);
				copyReportAttachments(newReport, newReport, submitFromStaging, trans);

			} else {
				
				// Already have files but need to copy db objects.
				copyReportAttachments(newReport, newReport, submitFromStaging, trans);
			}

			// Re-retrieve report from DB to populate generated fields
			EmissionsReport genReport = emissionsRptDAO.retrieveEmissionsReport(newReport.getEmissionsRptId());
			newReport.setEmissionsInventoryId(genReport.getEmissionsInventoryId());

			// create directory for application documents
			// Why were we doing this twice?
			//if (!submitFromStaging) {
			//	createReportDir(facility, newReport);
			//}
			
			for (ReportTemplates tmpl : newReport.getServiceCatalogs()) {
				updateTotalEmissions(newReport, tmpl, true);
			}

		} catch (DAOException e) {
			deleteReportDir();
			throw e;
		} catch (ApplicationException ae) {
			deleteReportDir();
			throw new DAOException(ae.getMessage(), ae);
		}
	}

    void reportMau(String s, EmissionsMaterialActionUnits mau, Integer year, Integer pId, String scc, int size) {
        logger.warn(s + " " + mau.getEmissionPeriodId() + " "
                + mau.getMaterial() + " " + mau.getMeasure()
                + " " + mau.getAction() + " "
                + mau.getThroughput() + " "
                + year + " " + pId + " " + scc + " size " + size);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void modifyEmissionsReport(Facility facility, NtvReport rpt, int minValue,
            ReportTemplates scReports, ReportTemplates scReports2, boolean openedForEdit)
    throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        try {
            modifyEmissionsReport(facility, rpt, trans, minValue, scReports, scReports2, openedForEdit);
            // If we make it here, everything must have worked, commit it.
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de); // does throw
        }  catch (RuntimeException e) {
            logger.error("runtime exception for emissions inventory " + rpt.getPrimary().getEmissionsRptId(), e);
            if (trans != null) {
                trans.cancel();
            }
            throw new DAOException("modifyEmissionsReport failed");
        } finally {
            closeTransaction(trans);
        }
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public FireRow createFireRow(FireRow row) throws DAOException {
        FireRow ret = null;
        Transaction trans = TransactionFactory.createTransaction();
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(true));
        emissionsRptDAO.setTransaction(trans);

        try {
            ret = emissionsRptDAO.createFireRow(row);
            trans.complete();
        } catch (RemoteException de) {
            cancelTransaction(trans, de); // does throw
        } finally {
            closeTransaction(trans);
        }

        return ret;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public boolean modifyFireRow(FireRow row) throws DAOException {
        boolean ret = false;
        Transaction trans = TransactionFactory.createTransaction();
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(true));
        emissionsRptDAO.setTransaction(trans);

        try {
            ret = emissionsRptDAO.modifyFireRow(row);
            trans.complete();
        } catch (RemoteException de) {
            cancelTransaction(trans, de);  // does throw
        } finally {
            closeTransaction(trans);
        }

        return ret;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    private void modifyEmissionsReport(Facility facility, NtvReport rpt,
            Transaction trans, int minValue,
            ReportTemplates scReports, ReportTemplates scReports2, boolean openedForEdit)
    throws DAOException {
        if(rpt.getReport1() != null && rpt.getReportV1() == null) {
            // clear fields so they are not used
            rpt.getReport1().setFeeId(null);
            rpt.getReport1().setTotalEmissions(null);
            removePollutants(rpt.getReport1(), true, true);
            rpt.getReport1().setTotalReportedEmissions(null);
        }
        if(rpt.getReportV1() != null) {
            if(scReports != null) {   
                rpt.getReportV1().determineNtvTons(minValue, scReports);
            }
        }
        if(rpt.getReport1() != null) {
            removePollutants(rpt.getReport1());
            modifyEmissionsReport(facility, null, rpt.getReport1(), null, null, trans, openedForEdit);
        }

        if(rpt.getReport2() != null && rpt.getReportV2() == null) {
            // clear fields so they are not used
            rpt.getReport2().setFeeId(null);
            rpt.getReport2().setTotalEmissions(null);
            removePollutants(rpt.getReport2(), true, true);
            rpt.getReport2().setTotalReportedEmissions(null);
        }
        if(rpt.getReportV2() != null) {
            if(scReports2 != null) {
                rpt.getReportV2().determineNtvTons(minValue, scReports2);
            }
        }
        if(rpt.getReport2() != null) { // need to update values we set to null
            removePollutants(rpt.getReport2());
            modifyEmissionsReport(facility, null, rpt.getReport2(), null, null, trans, openedForEdit);
        }

        if(rpt.getPrimary().getRptApprovedStatusDate() != null) { // must be approved
            // Retrieve existing invoice.
            InvoiceDAO invoiceDAO = invoiceDAO(trans);
            Invoice oldInv = invoiceDAO.retrieveInvoiceByReportID(rpt.getPrimary().getEmissionsRptId());
            if(oldInv == null || InvoiceState.READY_TO_INVOICE.equals(oldInv.getInvoiceStateCd())) { // must not have been posted or cancelled
                // Build the invoice
                Invoice newInv = buildInvoice(facility, rpt);
                adjustInvoice(oldInv, newInv, trans);
            }
        }
    }

    private void adjustInvoice(Invoice oldInv, Invoice newInv, Transaction trans)
    throws DAOException {
        // modify the invoice
        // the only changes to the invoice may be original amount
        // and if TV/SMTV the invoice details.
        // unless we need to create it when it had not existed or
        // delete it because not needed.
        if(newInv != null && oldInv != null) {
            oldInv.setOrigAmount(newInv.getOrigAmount());
            oldInv.setInvoiceDetails(newInv.getInvoiceDetails());
            // update invoice
            try {
                infraService.modifyInvoice(oldInv, trans);
            } catch(RemoteException re) {
                logger.error("adjustInvoice failed for invoice " + oldInv.getInvoiceId(), re);
                throw new DAOException("adjustInvoice failed", re);
            }
        } else if(newInv != null && oldInv == null) {
            // create the invoice
            //InfrastructureHelper infraHelper = new InfrastructureHelper();
            newInv = infraHelper.createInvoice(newInv, trans); // create the invoice
        } else if(newInv == null && oldInv != null) {
            // delete the invoice
            InvoiceDAO invoiceDAO = invoiceDAO(trans);
            invoiceDAO.deleteInvoice(oldInv);
        } // else if both null, do nothing.
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void modifyEmissionsReportState(EmissionsReport report, String state)
    throws DAOException{
        Transaction trans = TransactionFactory.createTransaction();

        try{
            modifyEmissionsReportState(report, state, trans);
            trans.complete();

        } catch (DAOException de) {
            cancelTransaction(trans, de);  // does throw
        } catch (RuntimeException e) {
            logger.error("RunTimeException for emissions inventory " + report.getEmissionsRptId(), e);
            if (trans != null) {
                trans.cancel();
            }
            throw new DAOException("modifyEmissionsReportState failed for emissions inventory " + report.getEmissionsRptId());
        } finally {
            closeTransaction(trans);
        }    	
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void modifyEmissionsReportState(EmissionsReport report,
            String state, Transaction trans)
    throws DAOException {
        EmissionsReport rtn = null;        
        report.setRptReceivedStatusCd(state);
        
        if(report.getRptApprovedStatusDate() == null && !ReportReceivedStatusDef.isSubmittedCode(report.getRptReceivedStatusCd())) {
            // put date in once.
            Timestamp d = new Timestamp(new java.util.Date().getTime());
            report.setRptApprovedStatusDate(d);
        }
        try {
            EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(true));
            emissionsRptDAO.setTransaction(trans);
            emissionsRptDAO.modifyEmissionsReport(report, null);
            rtn = emissionsRptDAO.retrieveEmissionsReport(report.getEmissionsRptId());
            report.setLastModified(rtn.getLastModified());

        } catch (RuntimeException e) {
            logger.error("RunTimeException for emissions inventory " + report.getEmissionsRptId(), e);            
            throw new DAOException("modifyEmissionsReportState failed for emissions inventory " + report.getEmissionsRptId());
        } 
        return; 
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void modifyEmissionsReportState(NtvReport rpt,
            String state)
    throws DAOException {
        EmissionsReport rtn = null;
        Transaction trans = TransactionFactory.createTransaction();
        try {
            EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(true));
            emissionsRptDAO.setTransaction(trans);
            Timestamp d = new Timestamp(new java.util.Date().getTime());
            if(rpt.getReport1() != null) {
                rpt.getReport1().setRptReceivedStatusCd(state);
                if(rpt.getReport1().getRptApprovedStatusDate() == null && !rpt.getReport1().isSubmittedCode()) {
                    // put date in once.
                    rpt.getReport1().setRptApprovedStatusDate(d);
                }
                emissionsRptDAO.modifyEmissionsReport(rpt.getReport1(), null);
                rtn = emissionsRptDAO.retrieveEmissionsReport(rpt.getReport1().getEmissionsRptId());
                rpt.getReport1().setLastModified(rtn.getLastModified());
            }
            if(rpt.getReport2() != null) {
                rpt.getReport2().setRptReceivedStatusCd(state);
                if(rpt.getReport2().getRptApprovedStatusDate() == null && !rpt.getReport2().isSubmittedCode()) {
                    // put date in once.
                    rpt.getReport2().setRptApprovedStatusDate(d);
                }
                emissionsRptDAO.modifyEmissionsReport(rpt.getReport2(), null);
                rtn = emissionsRptDAO.retrieveEmissionsReport(rpt.getReport2().getEmissionsRptId());
                rpt.getReport2().setLastModified(rtn.getLastModified());
            }
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);  // does throw
        }  catch (RuntimeException e) {
            logger.error("RunTimeException for emissions inventory " + rpt.getPrimary().getEmissionsRptId(), e);
            if (trans != null) {
                trans.cancel();
            }
            throw new DAOException("modifyEmissionsReportState failed for emissions inventory " + rpt.getPrimary().getEmissionsRptId());
        } finally {
            closeTransaction(trans);
        }
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public boolean modifyEmissionsReport(Facility facility, ReportTemplates scReports, EmissionsReport report, ArrayList<EmissionsReportEU> reuClearList,
            EmissionsReportPeriod pClear, boolean openedForEdit)
    throws DAOException {
        // reuClear is the EU to remove the periods from
        Transaction trans = TransactionFactory.createTransaction();
        try {
            boolean b = modifyEmissionsReport(facility, scReports, report, reuClearList, pClear, trans, openedForEdit);
            if(!b){
            	return false;
            }
            // If we make it here, everything must have worked, commit it.
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);  // does throw
        }  catch (RuntimeException e) {
            logger.error("RunTimeException for emissions inventory " + report.getEmissionsRptId(), e);
            if (trans != null) {
                trans.cancel();
            }
            throw new DAOException("modifyEmissionsReport failed for emissions inventory " + report.getEmissionsRptId());
        } finally {
            closeTransaction(trans);
        }
        return true; 
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    private boolean modifyEmissionsReport(Facility facility, ReportTemplates scReports, EmissionsReport report, ArrayList<EmissionsReportEU> reuClearList,
            EmissionsReportPeriod pClear, Transaction trans, boolean openedForEdit)
    throws DAOException {
        Integer reportId = null;
        InfrastructureDAO infraDAO = infrastructureDAO(getSchema(true));
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(true));
        emissionsRptDAO.setTransaction(trans);
        infraDAO.setTransaction(trans);

        // changes to emissions inventory may make emissions inventory invalid.
        // Note: The user changes to submitted emissions inventory should not alter validated flag
        if ((report.getRptReceivedStatusDate() == null || openedForEdit) && report.isValidated()) {        	
        	report.setValidated(false);
        }
        
        // modify the report...
        //if(report.isRptEIS() && report.getEisStatusCd().equals(ReportEisStatusDef.NONE)) {
        //    report.setEisStatusCd(ReportEisStatusDef.NOT_FILED);
        //}else if(!report.isRptEIS()){
        //    report.setEisStatusCd(ReportEisStatusDef.NONE);
        //}
        boolean b = emissionsRptDAO.modifyEmissionsReport(report, null);
        if(!b){ 
        	return false;
        }
        reportId = report.getEmissionsRptId();

            // remove all period info for the reportEU we are to clear.
            if(reuClearList != null) {
                for(EmissionsReportEU reuClear : reuClearList) {
                    for(EmissionsReportPeriod p : reuClear.getPeriods()) {
                        Integer pId = p.getEmissionPeriodId();
                        emissionsRptDAO.removeEmissionMaterialActionUnits(pId);
                        emissionsRptDAO.removeEmissionsVariables(pId);
                        emissionsRptDAO.removeEmissionsByPeriodId(pId);
                        emissionsRptDAO.removeReportPeriod(pId);
                        emissionsRptDAO.removeEmissionPeriod(pId);
                    }
                    // mark the periods not in report
                    reuClear.clearPeriods();
                }
            }
            if(pClear != null) {
                Integer pId = pClear.getEmissionPeriodId();
                emissionsRptDAO.removeEmissionMaterialActionUnits(pId);
                emissionsRptDAO.removeEmissionsVariables(pId);
                emissionsRptDAO.removeEmissionsByPeriodId(pId);
                emissionsRptDAO.removeReportPeriod(pId);
                emissionsRptDAO.removeEmissionPeriod(pId);
                EmissionsReportEU e = report.findEU(pClear);
                if (null != e) { // if because of group, then e will be null
                    // otherwise remove from report EU
                    e.getPeriods().remove(pClear);
                }
            }
            // disassociate all existing periods for this report.
            emissionsRptDAO.removeReportPeriods(reportId);
            // remove the assoications for the existing groups and EUs,
            // we will add them back later.
            EmissionsReportEUGroup[] eugrpref = emissionsRptDAO.retrieveReportEUGroupRef(reportId);            
            emissionsRptDAO.removeReportEUGroupsFromReport(reportId);
            emissionsRptDAO.removeReportEUsFromGroups(reportId);

            for(int i=0;i<eugrpref.length;i++){
                emissionsRptDAO.removeReportEUGroups(eugrpref[i].getReportEuGroupID());
            }
            //delete report EUs
            emissionsRptDAO.deleteEUs(report);
            for (EmissionsReportEU eu : report.getEus()) {
                // set new report id to this eu.
                eu.setEmissionsRptId(reportId);
                // create the report EU...
                emissionsRptDAO.createReportEU(eu);

                for (EmissionsReportPeriod period : eu.getPeriods()) {
                    if (period.getEmissionPeriodId() != null
                            && period.getEmissionPeriodId().intValue() > 0) {
                        emissionsRptDAO.modifyEmissionPeriod(period);
                    } else {
                        // set new report id to this period.
                        period.setEmissionsRptId(reportId);

                        // create the period...
                        period = emissionsRptDAO.createEmissionPeriod(period);
                    }

                    // add the report to the EU and report...
                    emissionsRptDAO.addReportPeriod(period.getEmissionPeriodId(),
                            eu.getCorrEpaEmuId(), null, reportId);

                    // add all of the emissions for this period...
                    emissionsRptDAO.removeEmissionsByPeriodId(period.getEmissionPeriodId());
                    for (Emissions emission : period.getEmissions().values()) {
                        // set the new period id for this emission.
                        emission.setEmissionPeriodId(period
                                .getEmissionPeriodId());
                        emissionsRptDAO.createEmissions(emission);
                    }

                    // add all the materials for this period
                    emissionsRptDAO.removeEmissionMaterialActionUnits(
                            period.getEmissionPeriodId());
                    for (EmissionsMaterialActionUnits m : period.getMaus()) {
                        if (m.isBelongs()) {
                            // set the new period id for this emission.
                            m.setEmissionPeriodId(period.getEmissionPeriodId());
                            emissionsRptDAO.createMaterialActionUnits(m);
                        }
                    }

                    // add all the variables for this period
                    emissionsRptDAO.removeEmissionsVariables(
                            period.getEmissionPeriodId());
                    for (EmissionsVariable v : period.getVars()) {
                        if (v.isBelongs()) {
                            // set the new period id for this emission.
                            v.setEmissionPeriodId(period.getEmissionPeriodId());
                            emissionsRptDAO.createVariable(v);
                        }
                    }
                }
            }

            // deal with the report EU groups...
            for (EmissionsReportEUGroup group : report.getEuGroups()) {
                /*  if (group.getReportEuGroupID() != null) {
                    emissionsRptDAO.modifyReportEUGroup(group);
                } else {*/
                // create new EU group...
                group = emissionsRptDAO.createReportEUGroup(group);
                //}

                int groupId = group.getReportEuGroupID();

                // add EU group to report...
                emissionsRptDAO.addEUGroupToReport(groupId, reportId);

                EmissionsReportPeriod period = group.getPeriod();
                if (period.getEmissionPeriodId() != null) {
                    emissionsRptDAO.modifyEmissionPeriod(period);
                    emissionsRptDAO.removeEmissionsByPeriodId(period.getEmissionPeriodId());
                } else {
                    period = emissionsRptDAO.createEmissionPeriod(period);
                }
                emissionsRptDAO.addReportPeriod(period.getEmissionPeriodId(), null,
                        groupId, reportId);

                group.setPeriod(period);
//              add all of the emissions for this period...

                for (Emissions emission : period.getEmissions().values()) {
                    // set the new period id for this emission.
                    emission.setEmissionPeriodId(period
                            .getEmissionPeriodId());
                    emissionsRptDAO.createEmissions(emission);
                }

                // add all the materials for this period
                emissionsRptDAO.removeEmissionMaterialActionUnits(
                        period.getEmissionPeriodId());
                for (EmissionsMaterialActionUnits m : period.getMaus()) {
                    if (m.isBelongs()) {
                        // set the new period id for this emission.
                        m.setEmissionPeriodId(period.getEmissionPeriodId());
                        emissionsRptDAO.createMaterialActionUnits(m);
                    }
                }

//              add all the variables for this period
                emissionsRptDAO.removeEmissionsVariables(
                        period.getEmissionPeriodId());
                for (EmissionsVariable v : period.getVars()) {
                    if (v.isBelongs()) {
                        // set the new period id for this emission.
                        v.setEmissionPeriodId(period.getEmissionPeriodId());
                        emissionsRptDAO.createVariable(v);
                    }
                }

                // add EU's to Group...
                for (Integer emuId : group.getEus()) {
                    emissionsRptDAO.addReportEUGroup(groupId, emuId, reportId);
                }
            }
            // Modify the invoice for the TV/SMTV report
            if(report.getRptApprovedStatusDate() != null) { // must be approved
                // Retrieve existing invoice.
                InvoiceDAO invoiceDAO = invoiceDAO(trans);
                Invoice oldInv = invoiceDAO.retrieveInvoiceByReportID(report.getEmissionsRptId());
                if(oldInv == null || InvoiceState.READY_TO_INVOICE.equals(oldInv.getInvoiceStateCd())) { // must not have been posted or cancelled
                    // Build the invoice
                    ArrayList<EmissionRow> emissions;
                    try {
                        emissions = EmissionRow.getEmissions(report, false,
                                true, new Integer(1), scReports, logger, true);
                    } catch(ApplicationException ae) {
                        logger.error("getEmissions failed for emissions inventory " + report.getEmissionsRptId(), ae);
                        throw new DAOException("getEmissions failed for emissions inventory " + report.getEmissionsRptId(), ae);
                    }
                    Invoice newInv = buildInvoice(facility, report, emissions);
                    adjustInvoice(oldInv, newInv, trans);
                }
            }

        emissionsRptDAO.removeEmissionsByReportId(report.getEmissionsRptId());
        // Create the totals lines.
        for(EmissionTotal e : report.getEmissionTotalsTreeSet()) {
            e.setEmissionsRptId(report.getEmissionsRptId());
            emissionsRptDAO.createEmissionTotal(e);
        }

        emissionsRptDAO.removeReportTypes(reportId);

        //report.resetRptTypes();
        //for (String reportTypeCd : report.getReportTypes()) {
        //    emissionsRptDAO.addReportType(reportId, reportTypeCd);
        //}
        return true;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void modifyEmissionsPeriod(Facility facility, ReportTemplates scReports, EmissionsReport report, EmissionsReportPeriod period, boolean openedForEdit)
    throws DAOException {
        /*  DESIGN
         * If the Period is already in the database (its ID exists and is positive, then
         * just update the period rather than update the entire report.
         * In either case, re-read the entire report.  The rest of the report
         * should still be in the database buffers, so this should be a lot
         * more efficient than re-writing the entire report.
         */
        if(period.getEmissionPeriodId() == null
                || period.getEmissionPeriodId().intValue() <= 0) {
            // If period is not yet in the database, modify the entire report
            modifyEmissionsReport(facility, scReports, report, null, null, openedForEdit);
        } else {
            Transaction trans = TransactionFactory.createTransaction();
            try {
                EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(true));
                emissionsRptDAO.setTransaction(trans);

                emissionsRptDAO.modifyEmissionPeriod(period);

                // add all of the emissions for this period...
                emissionsRptDAO.removeEmissionsByPeriodId(period.getEmissionPeriodId());
                for (Emissions emission : period.getEmissions().values()) {
                    // set the new period id for this emission.
                    emission.setEmissionPeriodId(period
                            .getEmissionPeriodId());
                    emissionsRptDAO.createEmissions(emission);
                }

                // add all the materials for this period
                emissionsRptDAO.removeEmissionMaterialActionUnits(
                        period.getEmissionPeriodId());
                for (EmissionsMaterialActionUnits m : period.getMaus()) {
                    if (m.isBelongs()) {
                        // set the new period id for this emission.
                        m.setEmissionPeriodId(period.getEmissionPeriodId());
                        emissionsRptDAO.createMaterialActionUnits(m);
                    }
                }

                // add all the variables for this period
                emissionsRptDAO.removeEmissionsVariables(
                        period.getEmissionPeriodId());
                for (EmissionsVariable v : period.getVars()) {
                    if (v.isBelongs()) {
                        // set the new period id for this emission.
                        v.setEmissionPeriodId(period.getEmissionPeriodId());
                        emissionsRptDAO.createVariable(v);
                    }
                }

                // changes to emissions inventory may make emissions inventory invalid.
                // Note: The user changes to submitted emissions inventory should not alter validated flag
                if ((report.getRptReceivedStatusDate() == null || openedForEdit) && report.isValidated()) {        	
                	setValidatedFlag(report, false, trans);
                }
                                
                trans.complete();
            } catch (DAOException de) {
                cancelTransaction(trans, de);  // does throw
            } finally {
                closeTransaction(trans);
            }
        }
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void saveContact(EmissionsReport report,
            NTVContactType cType,
            boolean existing, Contact newContact) throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(true));
        emissionsRptDAO.setTransaction(trans);
        try {
            //InfrastructureHelper infraHelper = new InfrastructureHelper();
            if(newContact == null) {
                // remove existsing contact
                Contact deleteContact = null;
                switch (cType) {
                case oldOwner:
                    deleteContact = report.getPrevOwnerForwardingAddr();
                    report.setPrevOwnerForwardingAddr(null);
                    report.setPrevOwnerForwardingAddrInteger(null);
                    break;
                case newOwner:
                    deleteContact = report.getNewOwnerAddr();
                    report.setNewOwnerAddr(null);
                    report.setNewOwnerAddrInteger(null);
                    break;
                case billing:
                    deleteContact = report.getBillingAddr();
                    report.setBillingAddr(null);
                    report.setBillingAddrInteger(null);
                    break;
                case primary:
                    deleteContact = report.getPrimaryAddr();
                    report.setPrimaryAddr(null);
                    report.setPrimaryAddrInteger(null);
                default:
                }
                // delete the contact
                emissionsRptDAO.modifyEmissionsReport(report, null);
                InfrastructureDAO infraDAO = infrastructureDAO(getSchema(true));
                infraDAO.setTransaction(trans);
                infraDAO.deleteContact(deleteContact);
            } else {
                if (existing == false) {
                    newContact.getAddress().setAddressId(null);
                    newContact.setAddressId(null);
                    newContact.setContactId(null);
                    newContact.setContactTypes(new ArrayList<ContactType>());
                    newContact.getAddress().setCountryCd("US");
                    Contact contact;
                    contact = infraHelper.createContact(newContact, trans);
                    switch (cType) {
                    case oldOwner:
                        report.setPrevOwnerForwardingAddr(contact);
                        break;
                    case newOwner:
                        report.setNewOwnerAddr(contact);
                        break;
                    case billing:
                        report.setBillingAddr(contact);
                        break;
                    case primary:
                        report.setPrimaryAddr(contact);
                    default:
                        break;
                    }
                    emissionsRptDAO.modifyEmissionsReport(report, null);
                } else {
                    // edit
                    infraHelper.modifyContactData(newContact, trans);
                }
            }
            trans.complete();
        } catch (RemoteException re) {
            cancelTransaction(trans, re);  // does throw
        } finally {
            closeTransaction(trans);
        }
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void deleteEmissionsReport(NtvReport ntvReport)
    throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        deleteEmissionsReport(ntvReport, trans);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void deleteEmissionsReport(NtvReport ntvReport, Transaction trans)
    throws DAOException {
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(true));
        emissionsRptDAO.setTransaction(trans);
        EmissionsReport r1, r2;
        if(ntvReport.getReport1() != null && ntvReport.getReport2() != null) {
            // must remove companion references to avoid integrity constraint
            // violation.
            ntvReport.getReport1().setCompanionReport(null);
            ntvReport.getReport2().setCompanionReport(null);
            emissionsRptDAO.modifyEmissionsReport(ntvReport.getReport1(), null);
            emissionsRptDAO.modifyEmissionsReport(ntvReport.getReport2(), null);
            // Note that emissions do not need to be read since their IDs have not changed.
            r1 = emissionsRptDAO.retrieveEmissionsReport(ntvReport.getReport1().getEmissionsRptId());
            r2 = emissionsRptDAO.retrieveEmissionsReport(ntvReport.getReport2().getEmissionsRptId());
            // preserve the list of attachments
            r1.setAttachments(ntvReport.getPrimary().getAttachments());
        } else {
            r1 = ntvReport.getReport1();
            r2 = ntvReport.getReport2();
        }
        deleteEmissionsReport(r1, trans);
        deleteEmissionsReport(r2, trans);
        trans.complete();
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void deleteEmissionsReport(EmissionsReport report)
    throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        deleteEmissionsReport(report, trans);
        trans.complete();
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void deleteEmissionsReport(EmissionsReport report, Transaction trans)
    throws DAOException {
    	deleteEmissionsReport(report, trans, false);
    }

	@Override
	public void deleteEmissionsReport(EmissionsReport report, Transaction trans, boolean removeFiles)
			throws DAOException {
		if(report == null) return;
        InfrastructureDAO infraDAO = infrastructureDAO(getSchema(true));
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(true));
        emissionsRptDAO.setTransaction(trans);
        DocumentDAO documentDAO = documentDAO(getSchema(true));
        infraDAO.setTransaction(trans);

        // Mark all documents as temporary
        for(EmissionsDocumentRef eD : report.getAttachments()) {
            if(eD.getPublicDoc() != null) {
                eD.getPublicDoc().setTemporary(true);
                documentDAO.modifyDocument(eD.getPublicDoc());
            }
            if(eD.getTradeSecretDoc() != null) {
                eD.getTradeSecretDoc().setTemporary(true);
                documentDAO.modifyDocument(eD.getTradeSecretDoc());
            }
            
            if(isPortalApp()) {
            	if(removeFiles) {
                	documentDAO.removeDocument(eD.getPublicDoc());
                	if(eD.getTradeSecretDoc() != null) {
                		documentDAO.removeDocument(eD.getTradeSecretDoc());
                	}
                } else {
                	// cleanup document in staging
                	if (null != eD.getDocumentId()) {
                		documentDAO.removeDocumentReference(eD.getDocumentId());
                	}
                	if (null != eD.getTradeSecretDocId()) {
                		documentDAO.removeDocumentReference(eD.getTradeSecretDocId());
                	}
                }
            }
        }
        
        emissionsRptDAO.removeEmissionsDocuments(report.getEmissionsRptId());
        emissionsRptDAO.removeReportTypes(report.getEmissionsRptId());
        if(isInternalApp()) {
            // Delete all notes
            List<EmissionsReportNote> notes = report.getNotes();
            if (notes != null && notes.size() > 0) {
            	for (EmissionsReportNote note : notes) {
            		removeNote(note, trans);
            	}
            }
        }

            // Delete TV and SMTV parts
            emissionsRptDAO.removeReportPeriods(report.getEmissionsRptId());
            for(EmissionsReportEUGroup g : report.getEuGroups()) {
                if(null != g.getPeriod()) {
                    emissionsRptDAO.removeEmissionMaterialActionUnits(g.getPeriod().getEmissionPeriodId());
                    emissionsRptDAO.removeEmissionsVariables(g.getPeriod().getEmissionPeriodId());
                    emissionsRptDAO.removeEmissionsByPeriodId(g.getPeriod().getEmissionPeriodId());
                    //emissionsRptDAO.removeMaterialByPeriodId(g.getPeriod().getEmissionPeriodId());
                    emissionsRptDAO.removeEmissionPeriod(g.getPeriod().getEmissionPeriodId());
                }
            }
            emissionsRptDAO.removeReportEUsFromGroups(report.getEmissionsRptId());
            emissionsRptDAO.removeReportEUGroupsFromReport(report.getEmissionsRptId());
            for(EmissionsReportEU e : report.getEus()) {
                for(EmissionsReportPeriod p : e.getPeriods()) {
                    emissionsRptDAO.removeEmissionMaterialActionUnits(p.getEmissionPeriodId());
                    emissionsRptDAO.removeEmissionsVariables(p.getEmissionPeriodId());
                    emissionsRptDAO.removeEmissionsByPeriodId(p.getEmissionPeriodId());
                    emissionsRptDAO.removeReportPeriod(p.getEmissionPeriodId());
                    emissionsRptDAO.removeEmissionPeriod(p.getEmissionPeriodId());
                }
            }
            emissionsRptDAO.removeReportEUs(report.getEmissionsRptId());

        emissionsRptDAO.removeEmissionsByReportId(report.getEmissionsRptId());
        
        
       	emissionsRptDAO.disassociateSCEmissionsReports(report);

       	emissionsRptDAO.deleteEmissionsReport(report);
        infraDAO.deleteContact(report.getBillingAddr());
        infraDAO.deleteContact(report.getNewOwnerAddr());
        infraDAO.deleteContact(report.getPrevOwnerForwardingAddr());
        infraDAO.deleteContact(report.getPrimaryAddr());
        return;
	}
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void cancelEmissionsReport(Integer externalId)
    throws DAOException {
    	
        Transaction trans = TransactionFactory.createTransaction();
        boolean changeRptState = false;

        try {
            EmissionsReport report = retrieveEmissionsReportRow(externalId, false);    	

            if(report != null){
            	// Do not need invoicing yet
//                Invoice invoice = infraBO.retrieveInvoiceByReportID(report.getEmissionsRptId());
//
//                if(invoice != null && invoice.getInvoiceStateCd().equals(InvoiceState.READY_TO_INVOICE)){
//                    invoice.setInvoiceStateCd(InvoiceState.CANCELLED);
//                    infraBO.modifyInvoice(invoice, trans);
//                    changeRptState = true;
//
//                } else if(invoice == null){
//                    changeRptState = true;
//                }
                changeRptState = true;

                if(changeRptState){
                    String newState;
                    if(ReportReceivedStatusDef.isSubmittedCode(report.getRptReceivedStatusCd())) {
                        newState = ReportReceivedStatusDef.REPORT_NOT_NEEDED;
                    } else {
                        newState = ReportReceivedStatusDef.APPROVED_REPORT_NOT_NEEDED;
                    }
                    Timestamp d = null;
                    if(report.getRptApprovedStatusDate() == null) {
                        // put date in once.
                        d = new Timestamp(new java.util.Date().getTime());
                        report.setRptApprovedStatusDate(d);
                    }
                    if(report.getCompanionReport() != null){
                        EmissionsReport cReport = retrieveEmissionsReportRow(report.getCompanionReport(), false);
                        if(cReport != null){
                            cReport.setRptApprovedStatusDate(d);
                            modifyEmissionsReportState(cReport, newState, trans);        	        		
                        }        	        	
                    }

                    modifyEmissionsReportState(report, newState, trans);
                }
            }
            trans.complete();   	   

        } catch (RemoteException re) {
            logger.error("cancelEmissionsReport failed for " + externalId, re);
            cancelTransaction(trans, re);  // does throw
        } finally {
            closeTransaction(trans);
        }        
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public EmissionsReportNote retrieveReportNote(int noteId)
    throws DAOException {
        return emissionsReportDAO().retrieveReportNote(noteId);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Facility editFacilityProfile(EmissionsReport report,
            Facility oldFacility, Integer userId, boolean openedForEdit) throws DAOException {
        Facility newFacility = null;
        Transaction trans = TransactionFactory.createTransaction();

        // Create note text
        FacilityNote note = new FacilityNote();
        String txt = "For emissions inventory " + report.getEmissionsInventoryId();
        txt = txt + ".  Split from facility inventory Id " +
        oldFacility.getFpId() + ".";
        note.setNoteTxt(txt);
        Calendar cal = Calendar.getInstance();
        cal.set(report.getReportYear(), Calendar.DECEMBER, 31, 23, 59, 59);
        Timestamp revisedDate = new Timestamp(cal.getTimeInMillis());

        try {

            FacilityBO.CopyOnChangeMaps maps 
            = facilityService.splitFacilityProfile(oldFacility.getFpId(), revisedDate, note, userId, trans);
            report.setFpId(maps.fpId); // point to split facility
            newFacility = facilityService.retrieveFacilityProfile(maps.fpId, false, trans);
            // reAssociate(report, newFacility, oldFacility, true);
            // Rewrite the report.
            modifyEmissionsReport(newFacility, null, report, null, null, trans, openedForEdit);
            trans.complete();
        } catch (RemoteException re) {
            logger.error("editFacilityProfile failed for emissions inventory " + report.getEmissionsInventoryId(), re);
            cancelTransaction(trans, re);  // does throw
        } finally {
            closeTransaction(trans);
        }
        return newFacility;
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public void updateReportedEmissionsTotal(EmissionsReport report,
            Float reportedEmissions)  throws DAOException{
    	 Transaction trans = TransactionFactory.createTransaction();
    	 EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(true));
         emissionsRptDAO.setTransaction(trans);
         emissionsRptDAO.modifyEmissionsReport(report, null);
    }
   

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public void setFeeInfo(EmissionsReport report,
            ArrayList<EmissionRow> emissions,
            SCEmissionsReport sc)  throws DAOException{
// Determine total emissions (in TONS) for chargeable and reported emissions.
// In IMPACT, there is a minimum charge of $500 per year if a facility wants to remain part of the Title V
// program.  Even if there aren't any emissions or if the fee calculated based on emissions is less than $500,
// the facility will be charged $500.  Therefore, there is no reason in IMPACT to set the fee per ton to zero.
        //if(report.zeroEmissions()) { 
        //    report.setFeeId(99);  // Zero fee is feeId 99.  Not part of any fee structure.
        //    report.setFeeId2(99);
        //    report.setTotalEmissions(0f);
        //    report.setTotalReportedEmissions(0f);
        //} else {
            double total = 0f;
            double reportedTotal = 0f;
            double totalEmiss = 0f;
            ArrayList<EmissionRow> toBeRemoved = new ArrayList<EmissionRow>();
            
            SCNonChargePollutant nonCharPolllutants[] = sc
					.getNcPollutants();
            if (emissions != null) {
	            //System.out.println("Before emissions.size(): "+emissions.size());
	            for (int j = 0; j < nonCharPolllutants.length; j++) {
	            	for (EmissionRow er : emissions) {
						if (nonCharPolllutants[j].getPollutantCd()
								.equalsIgnoreCase(
										er.getPollutantCd())) {
							toBeRemoved.add(er);						
						}
	            	}
				}
            }
            
            //System.out.println("toBeRemoved.size(): "+toBeRemoved.size());
            for (EmissionRow to : toBeRemoved) {
            	if (emissions != null) {
            		emissions.remove(to);
            	}
            }
            
            //System.out.println("After emissions.size(): "+emissions.size());
        
            // Calculate total chargeable emissions
		if (emissions != null) {
			for (EmissionRow er : emissions) {
				if (er.isChargeable()
						&& (null != er.getStackEmissions() || null != er
								.getFugitiveEmissions())) {
					double s = 0d;
					double f = 0d;
					if (null != er.getStackEmissions()) {
						s = EmissionUnitReportingDef.convert(
								er.getEmissionsUnitNumerator(),
								er.getStackEmissionsV(),
								EmissionUnitReportingDef.TONS);
					}
					if (null != er.getFugitiveEmissions()) {
						f = EmissionUnitReportingDef.convert(
								er.getEmissionsUnitNumerator(),
								er.getFugitiveEmissionsV(),
								EmissionUnitReportingDef.TONS);
					}
					total += s + f;
				}

				// Calculate total reportable emissions (chargeable and
				// non-chargeable) for pollutants in the
				// Service Catalog
				// Note: there may be a better way to determine if emission row
				// is from the Service Catalog (TBD).
				if (er.isFromServiceCatalog()
						&& (null != er.getStackEmissions() || null != er
								.getFugitiveEmissions())) {
					double s = 0d;
					double f = 0d;
					if (null != er.getStackEmissions()) {
						s = EmissionUnitReportingDef.convert(
								er.getEmissionsUnitNumerator(),
								er.getStackEmissionsV(),
								EmissionUnitReportingDef.TONS);
					}
					if (null != er.getFugitiveEmissions()) {
						f = EmissionUnitReportingDef.convert(
								er.getEmissionsUnitNumerator(),
								er.getFugitiveEmissionsV(),
								EmissionUnitReportingDef.TONS);
					}
					reportedTotal += s + f;
				}
				
				
				if ((null != er.getStackEmissions() || null != er
								.getFugitiveEmissions())) {
					double s = 0d;
					double f = 0d;
					if (null != er.getStackEmissions()) {
						s = EmissionUnitReportingDef.convert(
								er.getEmissionsUnitNumerator(),
								er.getStackEmissionsV(),
								EmissionUnitReportingDef.TONS);
					}
					if (null != er.getFugitiveEmissions()) {
						f = EmissionUnitReportingDef.convert(
								er.getEmissionsUnitNumerator(),
								er.getFugitiveEmissionsV(),
								EmissionUnitReportingDef.TONS);
					}
					totalEmiss += s + f;
					//System.out.println("totalEmiss: "+totalEmiss);
				}
			}
		}
            report.setTotalEmissions((float)total);           // Chargeable
            report.setTotalReportedEmissions((float)totalEmiss);   // Reported - total 
           
            
            // Determine the fee to apply
            Fee[] f = sc.getFees();
            int index = -1;
            String ft = sc.getFeeType();            
            if(ft.equals("unit")) {
                index = 0;
            } /*else if(ft.equals("rnge")) {
                // The fees are in order
                for(int i = 0; i < f.length; i++) {
                    if(f[i].getLowRange() == null) {
                        logger.error("LowRange=null not expected for SCEmissionsReport Id "
                                + sc.getId()
                                + " while processing emissions inventory "
                                + report.getEmissionsRptId());
                        continue;
                    }
                    if(total < f[i].getLowRange()) break; // finished looking
                    if(total >= f[i].getLowRange()) {
                        if(f[i].getHighRange() == null ||
                                f[i].getHighRange() < 0) {
                            index = i;
                            break; // Found last range
                        }
                        if(total < f[i].getHighRange()) {
                            index = i;
                            break; // found it
                        }
                    }
                }
            }
            */
            if(-1 == index){
                throw new DAOException("No appropriate Fee of type \"" +
                        ft + "\" for SCEmissionsReport Id " + sc.getId() +
                        " with total emissions of " + total
                        + " while processing emissions inventory "
                        + report.getEmissionsRptId());
            }
            // Set two fees, one for each half of year.
            report.setFeeId(f[index].getFeeId());
            report.setFeeId2(f[index+1].getFeeId());
        //}
    }

    /* DESIGN--transitioning between states.
     *   If Submit Date set and Approve Date not set, then can be in any of
     *   the states:
     *      Submitted,
     *      Revision Requested
     *      Report Not Needed
     *   From any of these states can transition to the states:
     *      Revision Requested
     *      Report Not Needed
     *      Approve/Revision Requested
     *      DO/LAA Approval
     *  The two approval states set the Approve Date and may create an invoice.
     *  Note that there is no need to transition back to state Submitted
     *  because Submitted is not a final state while all the others can be.
     *  
     *  If Approve Date set, then can be in any of the states:
     *      Approve/Revision Requested
     *      DO/LAA Approval
     *      Report Not Needed
     *   From any of these states can transition to the states:
     *      Approve/Revision Requested
     *      DO/LAA Approval
     *      Report Not Needed
     *    None of these transitions result in any workflow or invoice changes.   
     *      
     * Note that you can be in the state Report Not Needed
     * when the Approve Date is or is not set--it all depends upoon
     * when the user determines the report is not needed.
     */

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void submitReportFromPortal(Facility facility, Task fromTask) 
    throws RemoteException {
        // For TV and SMTV reports
        EmissionsReport rpt = fromTask.getReport();
        
        Contact submitterContact = 
        		getContactService().retrieveContactByExternalUsername(
        				fromTask.getUserName());
        rpt.setSubmitterContact(submitterContact.getContactId());
        
        Integer userId = new Integer(CommonConst.GATEWAY_USER_ID);
        ReportTemplates rptT = null;
        ArrayList<EmissionRow> emissions = null;
        try {
            rptT = retrieveSCEmissionsReports(
                    rpt.getReportYear(), rpt.getContentTypeCd(), facility.getFacilityId());
            // count report as submitted since changes done.
            emissions = EmissionRow.getEmissions(rpt, false, true, 0, rptT, logger, false);
        } catch (ApplicationException e) {
            logger.error("Emissions Reporting data error for emissions inventory " + rpt.getEmissionsInventoryId(), e);
            throw new RemoteException("ApplicationException turned into RemoteException for reporting for emissions inventory " + rpt.getEmissionsInventoryId());
        }
//      logger.warn("submitReportFromPortal: " + facility.getFpId() + " " 
//      + fromTask.getFpId() + " "
//      + facility.getFacilityId());
        submitReport(fromTask.getReport(), facility,
                emissions, rptT.getSc(), userId, true, false);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void submitReportFromPortal(Task fromTask) 
    throws RemoteException {
        // For NTV reports
        if(fromTask == null) {
            throw new RemoteException("submitReportFromPortal:  fromTask is null");
        }
        if(fromTask.getNtvReport() == null) {
            throw new RemoteException("submitReportFromPortal:  fromTask.getNtvReport() is null");
        }
        NtvReport rpt = new NtvReport(fromTask.getNtvReport().getReport1(),
                fromTask.getNtvReport().getReport2());
        if(rpt.getPrimary() == null) {
            throw new RemoteException("submitReportFromPortal:  rpt.getPrimary() is null");
        }

        Facility facility = facilityService.retrieveFacility(rpt.getPrimary().getFpId());
        Integer userId = new Integer(CommonConst.GATEWAY_USER_ID);
        logger.warn("submitNtvReportFromPortal: " 
                + rpt.getPrimary().getFpId());
        ReportTemplates scReports = null;
        ReportTemplates scReports2 = null;
        if(rpt.getReport1() != null) {
            Integer yr = rpt.getReport1().getReportYear();
            String contentTypeCd = rpt.getReport1().getContentTypeCd();
            scReports = retrieveSCEmissionsReports(yr, contentTypeCd,
                    facility.getFacilityId());
        }
        if(rpt.getReport2() != null) {
            Integer yr = rpt.getReport2().getReportYear();
            String contentTypeCd = rpt.getReport2().getContentTypeCd();
            scReports2 = retrieveSCEmissionsReports(yr, contentTypeCd,
            		facility.getFacilityId());
        }
        Integer minValue = SystemPropertyDef.getSystemPropertyValueAsInteger("ER_NTV_FER_Must_Enumerate", null);
        rpt.determineReportable(facility); // needed to determine which reports to keep
        ntvSubmit(rpt, facility, ReportReceivedStatusDef.SUBMITTED, minValue, scReports, scReports2,
                userId, true);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public boolean ntvSubmit(NtvReport ntvReport, Facility facility, String state,
            int minValue, ReportTemplates scReports, ReportTemplates scReports2,
            Integer userId,  boolean sourceIsStaging) throws DAOException {
        /*  DESIGN
         * If this is a two year report, then change both to submitted and set
         * yearly reporting state to submitted.
         * If only one of the two years contains emissions information, then
         * discard the other report and submit just the one.
         * If neither year contains emissions information, then keep both,
         * submit both, but do not set hte yearly reporting state.
         * 
         * This will probably leave the reporting state in the yearly information
         * for those reports set to "reminder letter sent".  The system will
         * not automatically chagne the state back to "report required" because
         * that may not be is desired.  Changing that state will be left
         * as a manual operation by DAPC.
         */
        boolean logFlag = false; //true;
        boolean newlyCreatedWorkflow = true;  // did this method create a workflow?
        Transaction trans = TransactionFactory.createTransaction();
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(trans);
        try {
            // Determine if both years should be kept
            boolean del1 = false;
            boolean del2 = false;
            // If submitToYearly false, then keep both
            boolean submitToYearly = true;
            boolean keepBothAnyway = false;
            if(ntvReport.getReport1() != null && ntvReport.getReport2() != null) {
                // reportV1 & reportV2 set because of NtvReport.determineReportable()
                if(ntvReport.getReportV1() == null) del1 = true;
                if(ntvReport.getReportV2() == null) del2 = true;
                submitToYearly = !del1 || !del2; // Fees in at least one report
                keepBothAnyway = del1 && del2;  // keep both if no fees in either
                // want received date in both reports.
                ntvReport.getReport2().setReceiveDate(ntvReport.getReport1().getReceiveDate());
            }
            EmissionsReport r1, r2;
            if((del1 || del2) && submitToYearly) { // Also means there are two reports and reduce to one   
                // must remove companion references to avoid integrity constraint
                // violation.
                ntvReport.getReport1().setCompanionReport(null);
                ntvReport.getReport2().setCompanionReport(null);
                if(!sourceIsStaging) {
                    emissionsRptDAO.modifyEmissionsReport(ntvReport.getReport1(), null);
                    emissionsRptDAO.modifyEmissionsReport(ntvReport.getReport2(), null);
                    // Note that emissions do not need to be read since their IDs have not changed.
                    r1 = emissionsRptDAO.retrieveEmissionsReport(ntvReport.getReport1().getEmissionsRptId());
                    r2 = emissionsRptDAO.retrieveEmissionsReport(ntvReport.getReport2().getEmissionsRptId());
                } else {
                    r1 = ntvReport.getReport1();
                    r2 = ntvReport.getReport2();
                }
                if(del1) {
                    // If deleting primary report, move its attributes to report
                    // being kept.
                    r2.setAttachments(r1.getAttachments());
                    r1.setAttachments(null);
                    r2.setNotes(r1.getNotes());
                    r1.setNotes(null);
                    r2.setBillingAddr(r1.getBillingAddr());
                    r2.setBillingAddrInteger(r1.getBillingAddrInteger());
                    r1.setBillingAddr(null); // Needed to avoid deleting it with r1.
                    r2.setFacilityNm(r1.getFacilityNm());
                    r2.setNewOwner(r1.isNewOwner());
                    r2.setNewOwnerAddr(r1.getNewOwnerAddr());
                    r2.setNewOwnerAddrInteger(r1.getNewOwnerAddrInteger());
                    r1.setNewOwnerAddr(null);
                    r2.setPrevOwnerForwardingAddr(r1.getPrevOwnerForwardingAddr());
                    r2.setPrevOwnerForwardingAddrInteger(r1.getPrevOwnerForwardingAddrInteger());
                    r1.setPrevOwnerForwardingAddr(null);
                    r2.setPrimaryAddr(r1.getPrimaryAddr());
                    r2.setPrimaryAddrInteger(r1.getPrimaryAddrInteger());
                    r1.setPrimaryAddr(null);
                    r2.setProvideBothYears(r1.isProvideBothYears()); // this will be false
                    r2.setReportModified(r1.getReportModified());
                    r2.setReceiveDate(r1.getReceiveDate());
                    r2.setTransferDate(r1.getTransferDate());
                    r2.setShutdownDate(r2.getShutdownDate());
                    // Keep the first report Id since there is a file directory
                    //r2.setEmissionsRptId(r1.getEmissionsRptId());
                }
            } else {  // Only a single report or (not fees and keep both reports)
                r1 = ntvReport.getReport1();
                r2 = ntvReport.getReport2();
            }

            Timestamp d = new Timestamp(new java.util.Date().getTime());
            // Update report attributes and set state in facility inventory
            if(r1 != null && (!del1 || keepBothAnyway)) {
                r1.setRptReceivedStatusCd(state);
                r1.setRptReceivedStatusDate(d);
                if(r1.getReceiveDate() == null) {
                    r1.setReceiveDate(d);
                }
                removeNullPollutants(r1);
                setEmissionsRptInfo(emissionsRptDAO, r1.getReportYear(),
                        facility.getFacilityId(), state);
            }
            if(r2 != null && (!del2 || keepBothAnyway)) {
                r2.setRptReceivedStatusCd(state);
                r2.setRptReceivedStatusDate(d);
                if(r2.getReceiveDate() == null) {
                    r2.setReceiveDate(d);
                }
                removeNullPollutants(r2);
                setEmissionsRptInfo(emissionsRptDAO, r2.getReportYear(),
                        facility.getFacilityId(), state);
            }
            // Rewrite the report.
            NtvReport newRpt;
            Integer newRptId = null;
            if(del1 && !keepBothAnyway) {
                // Are two reports and the first one is to be deleted.
                // Use the first Id in the second report
                newRptId = r1.getEmissionsRptId();
            }

            if(sourceIsStaging) {
                if(del1 && !keepBothAnyway) {
                    // Are two reports and the first one is to be deleted.
                    // Use the first Id in the second report
                    r2.setEmissionsRptId(newRptId);
                }
                if(del1 && submitToYearly) r1 = null;
                if(del2 && submitToYearly) r2 = null;
                newRpt = new NtvReport(r1, r2);
                createEmissionsReport(facility, newRpt, true, trans);
            } else { // Is internal
                if(del1 && submitToYearly) {
                    Integer replaceId = r1.getEmissionsRptId();
                    deleteEmissionsReport(r1, trans);
                    // Remove types since associated with old rpt id
                    emissionsRptDAO.removeReportTypes(r2.getEmissionsRptId());
                    EmissionTotal[] ets = emissionsReportDAO().retrieveEmissionTotals(r2.getEmissionsRptId()); // retrieve detailed emissions
                    emissionsRptDAO.removeEmissionsByReportId(r2.getEmissionsRptId()); // remove emissions prior to rewriting them with other ID
                    emissionsRptDAO.modifyEmissionsReport(r2, replaceId);
                    for(EmissionTotal e : ets) { // rewrite the detailed emissions with new ID
                        e.setEmissionsRptId(replaceId);
                        emissionsRptDAO.createEmissionTotal(e);
                    }
                    // Read the types with the correct rpt id
                    //for (String reportTypeCd : ntvReport.getReport2().getReportTypes()) {
                    //    emissionsRptDAO.addReportType(r2.getEmissionsRptId(), reportTypeCd);
                    //}

                    r1 = null;
                } else if(del2 && submitToYearly) {
                    // order does not mater
                    emissionsRptDAO.modifyEmissionsReport(r1, null);
                    deleteEmissionsReport(r2, trans);
                    r2 = null;
                } else { // keeping both reports (if exist)
                    if(r1 != null) emissionsRptDAO.modifyEmissionsReport(r1, null);
                    if(r2 != null) emissionsRptDAO.modifyEmissionsReport(r2, null);
                }
                newRpt = new NtvReport(r1, r2);
            }
            boolean reviewRequired = false;
            if(r1 != null) { // If autoGenerated make sure review required and skip tests
                reviewRequired = r1.isAutoGenerated();
            }
            if(!reviewRequired && r2 != null) { // If autoGenerated make sure review required and skip tests
                reviewRequired = r2.isAutoGenerated();
            }
            // Is there a previous report that this modifies?
            if(newRpt.getPrimary().getReportModified() != null) {
                // Since this is a revised report, it must be reviewed
                reviewRequired = true;
            }
            
            if(ReportOfEmissionsStateDef.SUBMITTED_CAUTION.equals(state)) {
                reviewRequired = true;
            }

            boolean existingPurchaseOwner = false;
            boolean needNewOwnerInfo = false;
            boolean allowPrimaryAddress = false;
            Integer purchaseSold;
            if(!reviewRequired) {
                if(newRpt.getPrimary().getTransferDate() == null) {
                    // Have not saved the transfer date yet.
                    existingPurchaseOwner = true;
                    purchaseSold = null;
                } else { // report.getTransferDate() != null
                    if(newRpt.getPrimary().isNewOwner()) {
                        existingPurchaseOwner = true;
                        purchaseSold = NtvReport.NEW_OWNER;
                    } else {
                        // Since sold, this owner is not the curent owner
                        existingPurchaseOwner =  false;
                        purchaseSold = NtvReport.OLD_OWNER;
                    }
                }

                // Can Address Information be supplied
                // Any newer reports (staging or internal schema)?
                // or owners?
                boolean allowContactChange = onlyOlderReports(facility, newRpt);

                // After all known Transfer Dates?
                List<SelectItem>  pickListTransfers = facility.
                ownershipChange(ntvReport.getLeftPoint());
                Timestamp transfer = dropPartialDay(ntvReport.getPrimary().getTransferDate());
                if(allowContactChange && ntvReport.getPrimary().getTransferDate() != null) {
                    for(SelectItem si : pickListTransfers) {
                        if(transfer.before((Timestamp)si.getValue())) {
                            allowContactChange = false;
                            break;
                        }
                    }
                }
                boolean ownerChangeIndicator =  ntvReport.getPrimary().getTransferDate() != null;
                needNewOwnerInfo = ownerChangeIndicator && allowContactChange;
                allowPrimaryAddress = allowContactChange && (!ownerChangeIndicator || NtvReport.NEW_OWNER.equals(purchaseSold));
                if(needNewOwnerInfo) {
                    // If transfer date already known, then not new owner (one that can be updated)
                    for(SelectItem si : pickListTransfers) {
                        if(transfer.equals((Timestamp)si.getValue())) {
                            needNewOwnerInfo = false;
                            break;
                        }
                    }
                }
                // END Copied from ErNTVBase

                // Determine if reviews required.
                if(r1 == null || r2 == null) {
                    // If not a two year report, review required.
                    reviewRequired = true;
                    if(logFlag)logger.error("Emissions Inventory " + newRpt.getPrimary().getEmissionsInventoryId() + ": Review required because a one year report");
                }
                // Any addresses or shutdown date given
                if(newRpt.getPrimary().getBillingAddrInteger() != null ||
                        newRpt.getPrimary().getPrimaryAddrInteger() != null ||
                        newRpt.getPrimary().getPrevOwnerForwardingAddrInteger() != null ||
                        newRpt.getPrimary().getNewOwnerAddrInteger() != null ||
                        newRpt.getPrimary().getShutdownDate() != null ||
                        newRpt.getPrimary().getFacilityNm() != null) {
                    reviewRequired = true;
                    if(logFlag)logger.error("Emissions Inventory " + newRpt.getPrimary().getEmissionsInventoryId() + ": Review required because addresses or shutdown exist");
                }
            }
            // Any actual tons given
            if(!reviewRequired) {
                if(r1.getEmissionTotalsTreeSet() != null &&
                        r1.getEmissionTotalsTreeSet().size() > 0) {
                    reviewRequired = true;
                    if(logFlag)logger.error("Emissions Inventory " + newRpt.getPrimary().getEmissionsInventoryId() + ": Review required because actual Tons specified for first report");
                }
            }
            if(!reviewRequired) {
                if(r2.getEmissionTotalsTreeSet() != null &&
                        r2.getEmissionTotalsTreeSet().size() > 0) {
                    reviewRequired = true;
                    if(logFlag)logger.error("Emissions Inventory " + newRpt.getPrimary().getEmissionsInventoryId() + ": Review required because actual Tons specified for second report");
                }
            }

            //if(!reviewRequired) {
                // Are ES reports included?
            //    if(r1.isRptES() || r2.isRptES()) {
            //        reviewRequired = true;
            //        if(logFlag)logger.error("Emissions Inventory " + newRpt.getPrimary().getEmissionsRptId() + ": Review required because ES reports included");
            //    }
            //}

            // Ownership specified that not already known
            if(!reviewRequired && newRpt.getPrimary().getTransferDate() != null) {
                reviewRequired = true;  // temproraily set to required
                for(Contact c : facility.getAllContactsList()) {
                    outer:
                        for(ContactType t : c.getContactTypes()) {
                            /*if(!t.getContactTypeCd().equals(ContactTypeDef.OWNR)) {
                                continue;
                            }*/
                            Timestamp start = dropPartialDay(t.getStartDate());
                            Timestamp nTransfer = dropPartialDay(newRpt.getPrimary().getTransferDate());
                            if(nTransfer.equals(start)) {
                                reviewRequired = false;
                                break outer;
                            }
                        }
                }
                if(logFlag && reviewRequired)logger.error("Emissions Inventory " + newRpt.getPrimary().getEmissionsInventoryId() + ": Review required because Transfer date is not previously known");
            }

            EmissionsReport rPrevOdd = null;
            // Previous odd year has no report or not NTV
            if(!reviewRequired) {
                Integer prevRptOddYear = new Integer(ntvReport.getOddYear() - 2);
                rPrevOdd =
                    retrieveLatestEmissionReport(prevRptOddYear,facility.getFacilityId());
                if(rPrevOdd == null) {
                    // No previous report
                    reviewRequired = true;
                    if(logFlag)logger.error("Emissions Inventory " + newRpt.getPrimary().getEmissionsInventoryId() + ": Review required because no previous approved emissions inventory");
                } else {
                        // Previous report is not NTV
                        reviewRequired = true;
                        if(logFlag)logger.error("Emissions Inventory " + newRpt.getPrimary().getEmissionsInventoryId() + ": Review required because previous emissions inventory is not NTV");
                }
            }

            // Emissions range for either year different from the previous odd year
            if(!reviewRequired) {
                if(rPrevOdd.getFeeId()== null) {
                    reviewRequired = true;
                }
            }
            Fee f = null;
            if(!reviewRequired) {
                f = serviceCatalogDAO().retrieveFee(rPrevOdd.getFeeId());
                if(f == null) {
                    reviewRequired = true;
                    if(logFlag)logger.error("Emissions Inventory " + newRpt.getPrimary().getEmissionsInventoryId() + ": Review required because failed to find previous fee");
                }
            }
            if(!reviewRequired) {
                if(f.getLowRange() == null) {
                    reviewRequired = true;
                    logger.error("FeeId " + rPrevOdd.getFeeId() + " in emissions inventory " +
                            rPrevOdd.getEmissionsInventoryId() + " in Facility " +
                            facility.getFacilityId() + " has low range equal null.");
                }
            }
            Fee fR1 = null;
            Fee fR2 = null;
            if(!reviewRequired && r1 != null) {
                fR1 = serviceCatalogDAO().retrieveFee(r1.getFeeId());
                if(fR1 == null) {
                    reviewRequired = true;
                    if(logFlag)logger.error("Emissions Inventory " + newRpt.getPrimary().getEmissionsInventoryId() + ": Review required because failed to find fee for " +
                            r1.getReportYear());
                }
                if(!reviewRequired && fR1.getLowRange() == null) {
                    logger.error("FeeId " + r1.getFeeId() + " in emissions inventory " +
                            r1.getEmissionsInventoryId() + " in Facility " +
                            facility.getFacilityId() + " has low range equal null.");
                    reviewRequired = true;
                }
                if(!reviewRequired && !f.getLowRange().equals(fR1.getLowRange()) ||
                        !rangesEqual(f.getHighRange(), fR1.getHighRange())) {
                    reviewRequired = true;
                    if(logFlag)logger.error("Emissions Inventory " + newRpt.getPrimary().getEmissionsInventoryId() + ": Review required because fee different for " +
                            r1.getReportYear());
                }
            }
            if(!reviewRequired && r2 != null) {
                fR2 = serviceCatalogDAO().retrieveFee(r2.getFeeId());
                if(fR2 == null) {
                    reviewRequired = true;
                    if(logFlag)logger.error("Emissions Inventory " + newRpt.getPrimary().getEmissionsInventoryId() + ": Review required because failed to find fee for " +
                            r2.getReportYear());
                } 
                if(!reviewRequired && fR2.getLowRange() == null) {
                    reviewRequired = true;
                    logger.error("FeeId " + r2.getFeeId() + " in emissions inventory " +
                            r2.getEmissionsInventoryId() + " in Facility " +
                            facility.getFacilityId() + " has low range equal null.");
                }
                if(!reviewRequired && !f.getLowRange().equals(fR2.getLowRange()) ||
                        !rangesEqual(f.getHighRange(), fR2.getHighRange())) {
                    reviewRequired = true;
                    if(logFlag)logger.error("Emissions Inventory " + newRpt.getPrimary().getEmissionsInventoryId() + ": Review required because fee different for " +
                            r2.getReportYear());
                }
            }

            // If emissions high enough for ES then review 
            String mustEnumerateES = null;
            int mustEnumES = 0;
            boolean comply1 = true;
            boolean comply2 = true;
            if(!reviewRequired){
                String county = facility.getPhyAddr().getCountyCd();
                comply1 = retrieveCompliance(r1.getReportYear(), county);
                comply2 = retrieveCompliance(r2.getReportYear(), county);
                // Determine if ES may be required.
                mustEnumerateES = SystemPropertyDef.getSystemPropertyValue("ER_NTV_ES_Must_Enumerate", null);
                if(mustEnumerateES == null) {
                    reviewRequired = true;
                } else {
                    mustEnumES = Integer.parseInt(mustEnumerateES);
                }
            }
            if(!reviewRequired && !comply1){
                if(fR1 != null && (fR1.getHighRange() == null || fR1.getHighRange()>= mustEnumES)) {
                    reviewRequired = true;
                }
            }
            if(!reviewRequired && !comply2){
                if(fR2 != null && (fR2.getHighRange() == null || fR2.getHighRange()>= mustEnumES)) {
                    reviewRequired = true;
                }
            }

            //if(logFlag)throw new DAOException("Testing only, logFlag is set");
            if(!reviewRequired){
                // Any  submit validation failure-since AQDcan enter incorrect report.
                List<ValidationMessage> validationMessages;
                validationMessages = newRpt.submitVerify(facility,
                        isInternalApp(), needNewOwnerInfo, allowPrimaryAddress,
                        existingPurchaseOwner,
                        minValue, scReports, scReports2, r1, r2, false);
                if(validationMessages.size() > 0) {
                    reviewRequired = true;
                    if(logFlag)logger.debug("Emissions Inventory " + newRpt.getPrimary().getEmissionsInventoryId() + ": Review required because emissions inventory does not pass validation");
                }
            }

            boolean coReviewNeeded = COReviewNeeded(facility, ntvReport);
            if(!reviewRequired) {
                // If CO needs to review, then so does DO/LAA
                // Note that any shutdown will result in coReviewNeeded being set to
                // true because the shutdown not yet set in yearly category table, but
                // DO/LAA needs to review if there is shutdown anyway.
                reviewRequired = coReviewNeeded;
            }
            if(!reviewRequired) {
                // We need to approve the report, first get updated values (lastUpdated)
                EmissionsReport r1b = null;
                EmissionsReport r2b = null;
                if(newRpt.getReport1() != null) {
                    r1b = emissionsRptDAO.retrieveEmissionsReport(newRpt.getReport1().getEmissionsRptId());
                }
                if(newRpt.getReport2() != null) {
                    r2b = emissionsRptDAO.retrieveEmissionsReport(newRpt.getReport2().getEmissionsRptId());
                }
                NtvReport newRptb = new NtvReport(r1b, r2b);
                newRptb.determineReportable(facility); // what years are being reported.
                // Approve report.
                ntvDolaaApprove2(newRptb, facility, ReportReceivedStatusDef.DOLAA_APPROVED,
                        userId, existingPurchaseOwner, trans);
            }

            // Is there already a workflow for this report?
            WorkFlowProcess[] processes;
            Integer workflowId = null;

            try {

            	workflowId = wfService.retrieveWorkflowTempIdAndNm().
            			get(WorkFlowProcess.BLUE_CARD_REVIEW);
                final Integer ntvErptTemplateId = new Integer(workflowId);
                WorkFlowProcess wfProcess = new WorkFlowProcess();
                wfProcess.setProcessTemplateId(ntvErptTemplateId);
                wfProcess.setExternalId(newRpt.getPrimary().getEmissionsRptId());
                wfProcess.setCurrent(true);
                wfProcess.setUnlimitedResults(true);
                // Find the workflow process for this report
                processes = wfService.retrieveProcessList(wfProcess);

            } catch (RemoteException e) {
                logger.error("Caught RemoteException checking for exisiting workflow for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId(), e);
                throw new DAOException("Caught RemoteException checking for exisiting workflow for report " + ntvReport.getPrimary().getEmissionsInventoryId(), e);
            }
            if(processes.length > 0) {
                // already a workflow
                newlyCreatedWorkflow = false;
                logger.warn("Pre-existing Blue Card Review workflow for report "
                        + newRpt.getPrimary().getEmissionsInventoryId()
                        + ":  Since submit done second time, the workflow may be in the wrong state");
            } else {
                // Create work flow
                Timestamp dueDt = null;
                String rush = "N";

                WorkFlowManager wfm = new WorkFlowManager();
                WorkFlowResponse resp = wfm.submitProcess(workflowId,
                        newRpt.getPrimary().getEmissionsRptId(),
                        facility.getFpId(), userId, rush, 
                        d, dueDt, null);

                if (resp.hasError() || resp.hasFailed()) {
                    String[] errorMsgs = resp.getErrorMessages();
                    String[] recomMsgs = resp.getRecommendationMessages();
                    StringBuffer bs = new StringBuffer("Errors from WorkFlow Engine: ");
                    for(String msg : errorMsgs) {
                        bs.append(msg + " ");
                    }
                    if(errorMsgs.length > 0 && recomMsgs.length > 0) {
                        bs.append("; ");
                    }
                    for(String msg : errorMsgs) {
                        bs.append(msg + " ");
                    }
                    throw new DAOException(bs.toString());
                }
                boolean ok;
                ok = setWorkflowTask(newRpt.getPrimary().getEmissionsRptId(), reviewRequired, trans);
                if(!ok)  {
                    logger.error("During submit of emissions inventory "
                            + ntvReport.getPrimary().getEmissionsInventoryId()
                            + ", failed to set workflow step where reviewRequired=" + reviewRequired);
                }
            }
            trans.complete();
        } catch (DAOException de) {
            logger.error("Exception for report " + ntvReport.getPrimary().getEmissionsInventoryId(), de);
            cancelTransaction(trans, de);  // does throw of exception it got
        }catch (Exception e) {
            logger.error("Exception for report " +  ntvReport.getPrimary().getEmissionsInventoryId(), e);
            if (trans != null) {
                trans.cancel();
            }
            throw new DAOException("caught Exception", e);
        } finally {
            closeTransaction(trans);
        }
        return newlyCreatedWorkflow;
    }

    static boolean rangesEqual(Integer first, Integer second) {
        if(first == null && second == null) {
            return true;
        }
        if(first == null && second != null) {
            return false;
        }
        return first.equals(second);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    private void removePollutants(EmissionsReport rpt)
    		throws DAOException {
    	
        Integer minValue = SystemPropertyDef.getSystemPropertyValueAsInteger("ER_NTV_FER_Must_Enumerate", null);
        boolean removeFER = false;
        boolean removeES = false;
        if(rpt.getFeeId()!= null) {
            Fee f = null;
            // Determine pollultant Tons to remove
            f = serviceCatalogDAO().retrieveFee(rpt.getFeeId());
            if(f != null && f.getLowRange().compareTo(minValue) < 0) {
                removeFER = true;
            }
        }
        removePollutants(rpt, removeFER, removeES);
    }

    private void removePollutants(EmissionsReport rpt, boolean removeFER, boolean removeES) {
        //if(!rpt.isRptES()) removeES = true;
        Iterator<EmissionTotal> it = rpt.getEmissionTotalsTreeSet().iterator();
        while(it.hasNext()) {
            EmissionTotal e = it.next();
            if(e.isEsPollutant() && !e.isFerPollutant() && removeES) {
                it.remove();
            } else if(!e.isEsPollutant() && e.isFerPollutant() && removeFER) {
                it.remove();
            } else if(e.isEsPollutant() && e.isFerPollutant()
                    && removeFER && removeES) {
                it.remove();
            }
        }
    }

    private void removeNullPollutants(EmissionsReport rpt) {
        Iterator<EmissionTotal> it = rpt.getEmissionTotalsTreeSet().iterator();
        while(it.hasNext()) {
            EmissionTotal e = it.next();
            if(e.getTotalEmissions() == null) {
                it.remove();
            }
        }
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    private boolean setWorkflowTask(Integer ntvReportId, boolean reviewRequired, Transaction trans)
    throws DAOException {
    	
        ProcessFlow ntvProcessFlow = null;
        try {

            final Integer ntvErptTemplateId = new Integer(102);
            WorkFlowProcess wfProcess = new WorkFlowProcess();
            wfProcess.setProcessTemplateId(ntvErptTemplateId);
            wfProcess.setExternalId(ntvReportId);
            wfProcess.setCurrent(true);
            wfProcess.setUnlimitedResults(true);
            // Find the workflow process for this report
            WorkFlowProcess[] processes = wfService.retrieveProcessList(wfProcess);

            if (processes.length == 1) {
                ntvProcessFlow = wfService.retrieveActiveProcessFlow(processes[0].getProcessId());
            } else {
                logger.error("Unexpected result querying work flow processes for ntv reportId " + ntvReportId
                        + " " + processes.length + " work flow processes found.");
            }
        } catch (RemoteException e) {
            logger.error("Caught RemoteException in setWorkflowTask", e);
            throw new DAOException("Caught RemoteException in setWorkflowTask for ntv reportId " + ntvReportId, e);
        }

        Integer activityId = null;
        boolean ok = true;
        if(ntvProcessFlow == null) ok = false;
        if (ok) {
            if (reviewRequired) {
                activityId = new Integer(122);
            }else {
                activityId = new Integer(126);
            }
        }

        WorkFlowResponse response;
        if (ok) {
            WorkFlowManager workFlowManager = new WorkFlowManager();
            response = workFlowManager.checkInTo(ntvProcessFlow.getProcessId(),
                    activityId);
            if(response == null) ok = false;
        }
        return ok;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Integer backupWorkFlow(String facilityId, Integer rptId, boolean viewOnly)
    throws DAOException {
    	
        // Later this might change to unApproveReport, for now it just backs up workflow
        /* Report must be approved.  If there is an invoice
         * it must be in state ready to invoice.
         * 
         * Steps are to set report state back to submitted,
         * delete the Stars2 invoice and
         * back to workflow up to DO/LAA approve step.
         * 
         * FOR NOW IT JUST BACKS UP THE WORKFLOW.
         * 
         * Return null if retrieveProcessList() fails.
         * return 0 or small negative if number returned is not exactly 1.
         * return process Id if sucessful.
         * return minus process Id if redo() fails.
         */
        Integer pId = null;
        ProcessActivity pa = new ProcessActivity();
        ProcessActivityLight[] activities = new ProcessActivityLight[0];

        try {

            pa.setFacilityId(facilityId);
            pa.setActivityStatusCd(WorkFlowProcess.STATE_IN_PROCESS_CD);
            pa.setProcessCd(WorkflowProcessDef.EMISSION_REPORTING);
            pa.setPerformerTypeCd("M");
            pa.setExternalId(rptId);
            pa.setProcessTemplateNm("SMTV/TV Emission Review");

            activities = new ProcessActivityLight[0];
            activities = wfService.retrieveActivityListLight(pa);

        } catch (RemoteException re) {
            logger.error("Exception for report " + rptId, re);
            return -101; // indicate error
            // DisplayUtil.displayError("System error. Please contact system administrator");
            // TODO will need throw if used in product
        }
        if(activities.length != 1) {
            // failed to find workflow ID.
            pId = new Integer(-activities.length);   
        } else {
            pId = activities[0].getProcessId();
            Integer currActId = activities[0].getActivityTemplateId();
            // activityTemplateDefId 129 is for DO/LAA FER Approval
            if(currActId != 129 && !viewOnly) {
                WorkFlowManager wfm = new WorkFlowManager();
                WorkFlowResponse resp = wfm.redo(pId, 129);
                if (resp.hasError() || resp.hasFailed()) {
                    String[] errorMsgs = resp.getErrorMessages();
                    String[] recomMsgs = resp.getRecommendationMessages();
                    StringBuffer bs = new StringBuffer("Errors from WorkFlow Engine: ");
                    for(String msg : errorMsgs) {
                        bs.append(msg + " ");
                    }
                    if(errorMsgs.length > 0 && recomMsgs.length > 0) {
                        bs.append("; ");
                    }
                    for(String msg : errorMsgs) {
                        bs.append(msg + " ");
                    }
                    //DisplayUtil.displayError("System error. Please contact system administrator");
                    logger.error("Workflow redo() failed, currActId=" + currActId
                            + ", "+ bs.toString());
                    pId = new Integer(-pId.intValue());
                    //throw new DAOException(bs.toString());
                } else {
                    // succeeded
                }
            }
        }
        return pId;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void submitReport(EmissionsReport report,
            Facility facility, ArrayList<EmissionRow> emissions,
            SCEmissionsReport sc, Integer userId,
            boolean sourceIsStaging, boolean openedForEdit) throws DAOException {
    	
        Transaction trans = TransactionFactory.createTransaction();
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(trans);
        FacilityDAO facilityDAO = facilityDAO(trans);
        String oldState = report.getRptReceivedStatusCd();
        try {
            // Rewrite the report.
            if(report.getEisStatusCd() != null && 
                    report.getEisStatusCd().equals(ReportEisStatusDef.NOT_FILED)) {
                report.setEisStatusCd(ReportEisStatusDef.SUBMITTED);
            }
            report.setRptReceivedStatusCd(ReportReceivedStatusDef.SUBMITTED);
            Timestamp d = new Timestamp(new java.util.Date().getTime());
            report.setRptReceivedStatusDate(d);
            if(report.getReceiveDate() == null) {
                report.setReceiveDate(d);
            }


            // Create the emissions rows for searching
            createTotalsRows(report, emissions);
            
            if(sourceIsStaging) {
                String county = facility.getPhyAddr().getCountyCd();
                createEmissionsReport(county, facility, report, true, trans);
            } else {
            	report.setSubmitterUser(userId);
                modifyEmissionsReport(facility, null, report, null, null, trans, openedForEdit);
            }

            // Set the state in facility inventory
			updateEmissionsRptInfos(emissionsRptDAO, facility.getFacilityId(),
					report.getEmissionsRptId(),
					ReportReceivedStatusDef.SUBMITTED);
            
            WorkFlowProcess[] processes;
            ReadWorkFlowService wfBO;
            Integer workflowId = null;

            try {

                logger.debug("Retrieving workflow temp id and name...");
                workflowId = wfService.retrieveWorkflowTempIdAndNm().
                get(WorkFlowProcess.SMTV_TV_REVIEW);
                if(workflowId == null) {
                    String s = "Failed to find workflowId based upon SMTV_TV_REVIEW";
                    logger.error(s);
                    throw new DAOException(s);
                }
                final Integer tvErptTemplateId = workflowId;
                WorkFlowProcess wfProcess = new WorkFlowProcess();
                wfProcess.setProcessTemplateId(tvErptTemplateId);
                wfProcess.setExternalId(report.getEmissionsRptId());
                wfProcess.setCurrent(true);
                wfProcess.setUnlimitedResults(true);
                // Find the workflow process for this report
                logger.debug("Retrieving process list while submitting report...");
                processes = wfService.retrieveProcessList(wfProcess);

            } catch (RemoteException e) {
                logger.error("Caught RemoteException checking for exisiting workflow for emissions inventory " + report.getEmissionsInventoryId(), e);
                throw new DAOException("Caught RemoteException checking for exisiting workflow for emissions inventory " + report.getEmissionsInventoryId(), e);
            }
            if(processes.length > 0) {
                // already a workflow
                logger.debug("Pre-existing TV/SMTV Emission Report workflow for emissions inventory "
                        + report.getEmissionsInventoryId());
            } else {
                // Create work flow
                logger.debug("Create work flow for TV/SMTV emissions inventory "
                        + report.getEmissionsInventoryId());
                Timestamp dueDt = null;
                String rush = "N";
                WorkFlowManager wfm = new WorkFlowManager();
                WorkFlowResponse resp = wfm.submitProcess(workflowId,
                        report.getEmissionsRptId(),
                        report.getFpId(), userId, rush, 
                        d, dueDt, null);

                if (resp.hasError() || resp.hasFailed()) {
                    String[] errorMsgs = resp.getErrorMessages();
                    String[] recomMsgs = resp.getRecommendationMessages();
                    StringBuffer bs = new StringBuffer("Errors from WorkFlow Engine: ");
                    for(String msg : errorMsgs) {
                        bs.append(msg + " ");
                    }
                    if(errorMsgs.length > 0 && recomMsgs.length > 0) {
                        bs.append("; ");
                    }
                    for(String msg : errorMsgs) {
                        bs.append(msg + " ");
                    }
                    throw new DAOException(bs.toString());
                }
            }
            
            if(this.isInternalApp()) {
                // freeze the profile
                boolean rewriteProfile = false;
                if (facility.getVersionId().equals(-1)) {
                    facility.setCopyOnChange(true);
                    rewriteProfile = true;
                } else {
                    if (facility.isCopyOnChange()) {
                        facility.setCopyOnChange(false);
                        rewriteProfile = true;
                    }
                }
                if (rewriteProfile) {
                    // Only update profile if it needs to be frozen.
                    facilityDAO.modifyFacility(facility);
                }
            }
            
            trans.complete();
        } catch (DAOException de) {
            report.setRptReceivedStatusCd(oldState);
            report.setRptReceivedStatusDate(null);
            report.setReceiveDate(null);
            cancelTransaction(trans, de);  // does throw
        }catch (Exception e) {
            logger.error("Exception for report " + report.getEmissionsRptId(), e);
            report.setRptReceivedStatusCd(oldState);
            report.setRptReceivedStatusDate(null);
            report.setReceiveDate(null);
            if (trans != null) {
                trans.cancel();
            }
            throw new DAOException("caught Exception for emissions inventory " + report.getEmissionsInventoryId(), e);
        } finally {
            closeTransaction(trans);
        }
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public void createTotalsRows(EmissionsReport report, ArrayList<EmissionRow> emissions) {
        // Create the emissions rows for searching
        TreeSet<EmissionTotal> etSet = new TreeSet<EmissionTotal>();
        int tempOrder = 0;
        for(EmissionRow er : emissions) {
            if(null != er.getStackEmissions() || null != er.getFugitiveEmissions()){ 
                double s = 0d;
                double f = 0d;
                if(null != er.getStackEmissions()){
                    s = EmissionUnitReportingDef.convert(
                            er.getEmissionsUnitNumerator(), er.getStackEmissionsV(),
                            EmissionUnitReportingDef.TONS);
                }
                if(null != er.getFugitiveEmissions()){
                    f = EmissionUnitReportingDef.convert(
                            er.getEmissionsUnitNumerator(), er.getFugitiveEmissionsV(),
                            EmissionUnitReportingDef.TONS);
                }
                EmissionTotal et = new EmissionTotal();
                et.setPollutantCd(er.getPollutantCd());
                et.setTotalEmissions(EmissionsReport.numberToStringNoE(s + f));
                et.setOrder(tempOrder++);
                etSet.add(et);
            }
        }
        report.setEmissionTotalsTreeSet(etSet);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public EmissionsRptInfo getEmissionsRptInfo(Integer year, String facilityId)
    throws DAOException {
        EmissionsRptInfo e = emissionsReportDAO().retrieveEmissionsRptInfo(
                facilityId, year);
        if(e != null) {
            e = e.canReport();
        }
        return e;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public void setEmissionsRptInfo(EmissionsReportDAO emissionsRptDAO,
            Integer year, String facilityId, String state) throws DAOException {
        // Set the state in facility inventory
        EmissionsRptInfo info = emissionsRptDAO.retrieveEmissionsRptInfo(
                facilityId, year);
        if (null != info) {
            info.setState(state);
            emissionsRptDAO.modifyEmissionsRptInfo(facilityId, info);
        }
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void setEmissionsRptInfoReminder(Integer yearOne, Integer yearTwo,
            String fullAddr, String reportingTypeCd, String toState, String facilityId, Integer fpId,
            Correspondence correspondence, Integer userId)
    throws DAOException {
        EmissionsReportDAO emissionsRptDAO;
        CorrespondenceDAO correspondenceDAO;
        Transaction trans = TransactionFactory.createTransaction();
        try {
            emissionsRptDAO = emissionsReportDAO(getSchema(false));
            emissionsRptDAO.setTransaction(trans);
            correspondenceDAO = correspondenceDAO();
            correspondenceDAO.setTransaction(trans);
            // Set the state in facility inventory
            EmissionsRptInfo info;
            if(yearOne != null) {
                info = emissionsRptDAO.retrieveEmissionsRptInfo(facilityId, yearOne);
                if(info != null) {
                    info.setState(toState);
                    emissionsRptDAO.modifyEmissionsRptInfo(facilityId, info);
                } else {
                    logger.error("Should have found entry in fp_yearly_reporting_category for facility "
                            + facilityId + " and year " + yearOne);
                }
            }

            if(yearTwo!= null) {
                info = emissionsRptDAO.retrieveEmissionsRptInfo(facilityId, yearTwo);
                if(info != null) {
                    info.setState(toState);
                    emissionsRptDAO.modifyEmissionsRptInfo(facilityId, info);
                } else {
                    logger.error("Should have found entry in fp_yearly_reporting_category for facility "
                            + facilityId + " and year " + yearTwo);
                }
            }

            // Even if we did not locate existing records above to set the state,
            // we still would have generated the correspondence
            correspondence.setDirectionCd(CorrespondenceDirectionDef.OUTGOING);
            correspondenceDAO.createCorrespondence(correspondence);
            Document cDoc = correspondence.getDocument();
            if (cDoc != null && correspondence.getSavedDocReqd()) {
                documentDAO().unMarkTempDocument(cDoc.getDocumentID());
            }
        } catch (DAOException e) {
            logger.error("Exception for facility " + facilityId, e);
            throw e;
        }

        try {
            EventLog el = new EventLog();
            StringBuffer note = new StringBuffer();
            note.append(" ");
            if(yearOne != null) {
                note.append(yearOne.toString());
            }
            if(yearOne != null && yearTwo != null) {
                note.append(" and " + yearTwo.toString());
            } else if(yearTwo != null) {
                note.append(yearTwo.toString());
            }
            note.append(" ");
            note.append(EmissionReportsDef.getData().getItems().getItemDesc(reportingTypeCd));
            if(toState.equals(ReportReceivedStatusDef.SECOND_REMINDER_SENT)) {
                note.append(" Emissions Inventory Late/NOV letter sent to: ");
            } else {
                note.append(" Emissions Inventory Reminder letter sent to: ");
            }
            note.append(fullAddr);

            el.setFpId(fpId);
            el.setFacilityId(facilityId);
            el.setUserId(userId);
            el.setDate(new Timestamp(System.currentTimeMillis()));
            el.setEventTypeDefCd(EventLogTypeDef.EM_REPORT);
            el.setNote(note.toString());
            FacilityHelper facHelper = new FacilityHelper();
            facHelper.createEventLog(el);
        } catch (RemoteException re) {
            logger.error("Error generating EventLog for facility " + facilityId, re);
            throw new DAOException("Error generating EventLog for facility " + facilityId, re);
        }
    }
    //This would contain the reporting category, the report types and the due date

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public EmissionsReportNote[] retrieveReportNotes(Integer reportId)
    throws DAOException {
        checkNull(reportId);
        return emissionsReportDAO().retrieveReportNotes(reportId);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public EmissionTotal[] retrieveEmissionTotals(Integer reportId)
    throws DAOException {
        checkNull(reportId);
        return emissionsReportDAO().retrieveEmissionTotals(reportId);
    };

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Emissions[] retrieveEmissions(EmissionsReportPeriod period)
    throws DAOException {
        checkNull(period);
        return emissionsReportDAO().retrieveEmissions(period);
    };
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public EmissionsReport[] retrieveEmissionsReports(int fpId)
    throws DAOException {
        return emissionsReportDAO().retrieveEmissionsReports(fpId);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public NtvReport retrieveNtvEmissionsReport(Integer reportId, boolean staging)
    throws DAOException {
        checkNull(reportId);
        EmissionsReport reportA;
        reportA = retrieveEmissionsReport(reportId, staging);
        if(reportA == null) return null;
        EmissionsReport reportB = null;
        if(reportA.getCompanionReport() != null) {
            reportB = retrieveEmissionsReport(
                    reportA.getCompanionReport(), staging);
        }

        NtvReport ntvReport = new NtvReport(reportA, reportB);
        ntvReport.setProvidedRptId(reportId);
        ntvReport.setProvidedEmInventoryId(reportA.getEmissionsInventoryId());
        return  ntvReport;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public EmissionsReport retrieveLatestEmissionReport(Integer year, String facilityId)
    throws DAOException {
        // NOTE:  This returns the top level report object only
        EmissionsReport ret = null;
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(false));

        ret = emissionsRptDAO.retrieveLatestEmissionReport(year, facilityId);
        
        if (null != ret) {
        	ret.setServiceCatalogs(retrieveAssociatedSCEmissionsReports(ret));
        }
        
        return ret;
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public EmissionsReport retrieveLatestTvEmissionReport(Integer year, String facilityId)
    throws DAOException {
        // NOTE:  This returns the top level report object only
        EmissionsReport ret = null;
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(false));

        ret = emissionsRptDAO.retrieveLatestTvEmissionReport(year, facilityId);
        
        
        if (null != ret) {
        	ret.setServiceCatalogs(retrieveAssociatedSCEmissionsReports(ret));
        }
        
        return ret;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public EmissionsReport retrieveLatestSubmittedEmissionReport(Integer year, String facilityId)
    throws DAOException {
        // NOTE:  This returns the top level report object only
        EmissionsReport ret = null;
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(false));

        ret = emissionsRptDAO.retrieveLatestSubmittedEmissionReport(year, facilityId);
        
        
        if (null != ret) {
        	ret.setServiceCatalogs(retrieveAssociatedSCEmissionsReports(ret));
        }

        
        return ret;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    public EmissionsReport retrieveEmissionsReportRow(Integer reportId, boolean staging)
    throws DAOException {
        EmissionsReport ret;
        checkNull(reportId);
        Transaction trans = TransactionFactory.createTransaction();
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(staging));
        emissionsRptDAO.setTransaction(trans);
        ret = emissionsRptDAO.retrieveEmissionsReport(reportId);
        
        
        if (null != ret) {
        	ret.setServiceCatalogs(retrieveAssociatedSCEmissionsReports(ret,staging));
        	ret.setInspectionsReferencedIn(fullComplianceEvalDAO().retrieveInspectionIdsForEmissionRptId(reportId));
        }

        
        return ret;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    public EmissionsReport retrieveEmissionsReport(Integer reportId, boolean staging)
    throws DAOException {
        checkNull(reportId);
        return retrieveEmissionsReport(reportId, staging, null);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    private EmissionsReport retrieveEmissionsReport(Integer reportId, boolean staging,
            Transaction trans)
    throws DAOException {
    	
        boolean testTimings = false;
        checkNull(reportId);
        EmissionsReport ret = null;
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(staging));
        InfrastructureDAO infrastructureDAO = infrastructureDAO();
        InfrastructureDAO infrastructureDAOt = infrastructureDAO(getSchema(staging));
        if(null != trans) {
            emissionsRptDAO.setTransaction(trans);
            infrastructureDAOt.setTransaction(trans);
        }
        Timestamp s;
        if(testTimings) {
            s = new Timestamp(System.currentTimeMillis());
            logger.error("before retrieveEmissionsReport: " + s.toString());
        }
        ret = emissionsRptDAO.retrieveEmissionsReport(reportId);
        if(testTimings) {
            s = new Timestamp(System.currentTimeMillis());
            logger.error("after retrieveEmissionsReport: " + s.toString());
        }
        if (ret != null) {
            /*  Determine what to display about who submitted the Emissions Inventory
             * If not submitted, leave a blank submit date.
             *      Submitted:  No
             * If submitted before Stars2, just provide submit date.
             *      Submitted Date: 4/13/2009 
             * If displayed externally then either "Air User" or EnteredByAQDstring from parms.xml
             *  (Where "Air User" is the user name of all portal users)
             *      Entered by Ohio EPA: 4/13/2009
             *      Submitted by User, Air Services: 4/13/2009
             * if displayed internally then enter user name.
             *      Entered by Luksik, Linda: 4/13/2009 or
             *      Submitted by User, Air Services: 4/13/2009
             *      
             *  Determine what to display about who submitted the Application    
             * If not submitted
             *      Submitted:  No
             * If submitted before Stars2
             *      Submitted:  Yes
             * If displayed externally then either "Air User" or EnteredByAQDstring from parms.xml
             *  (Where "Air User" is the user name of all portal users)
             *      Entered by: Ohio EPA
             *      Submitted by: User, Air Services
             * if displayed internally then enter user name.
             *      Entered by: Luksik, Linda
             *      Submitted by: User, Air Services
             *      
             *   Determine what to display about who submitted the Compliance Report
             *  If displayed externally
             *       Entered by: Ohio EPA
             *       Submitted by:  User, Air Services
             *  If displayed internally
             *       Entered by: Luksik, Linda
             *       Submitted by: User, Air Services
             */

            GregorianCalendar defaultDeployDate = new GregorianCalendar(2008, 0, 1);
            Timestamp deployDate = new Timestamp(defaultDeployDate.getTimeInMillis());
            String deployDateStr = SystemPropertyDef.getSystemPropertyValue("STARS2_Deploy_Date", null);
            if (deployDateStr != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    java.util.Date date = dateFormat.parse(deployDateStr);
                    deployDate = new Timestamp(date.getTime());
                } catch (ParseException e) {
                    logger.error("Parameter 'STARS2_Deploy_Date' has an invalid format. " +
                            "Expected format is MM/dd/yyyy; parameter value is: '" +
                            deployDateStr + " for report " + reportId);
                }
            }

            String[] labelValue = new String[2];
            labelValue[0] = "Submitted: ";
            labelValue[1] = "No";
            UserDef userDef = null;
            if(ret.getRptReceivedStatusDate() != null) {
                labelValue[1] = "";
                if(!ret.getRptReceivedStatusDate().before(deployDate)) {
                    labelValue[0] = "Submitted Date: ";

                    try {
                        labelValue = infraService.retreiveSubmittingRptUser(ret.getEmissionsRptId());
                    	userDef = infraService.retrieveUserDef(ret.getSubmitterUser());
                    	} catch(RemoteException re) {
                        // do not generate an error.
                    }
                }
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(ret.getRptReceivedStatusDate().getTime());
                if (labelValue[1] == null || labelValue[1].length() == 0) {
                	if (userDef != null) {
                		labelValue[1] = userDef.getNameOnly();
                	}
                }
                labelValue[1] = labelValue[1] + (labelValue[1].length() > 0 ? "  " : "") + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.YEAR);
            }
            ret.setSubmitLabel(labelValue[0]);
            ret.setSubmitValue(labelValue[1]);

            if(isInternalApp()) {
            	ret.setNotes(Arrays.asList(emissionsRptDAO.retrieveReportNotes(reportId)));
            }

            DocumentDAO docDAO = documentDAO(getSchema(staging));
            for (EmissionsDocumentRef attachment : emissionsRptDAO
                    .retrieveEmissionsDocuments(reportId)) {
                if (attachment.getDocumentId() != null) {
                    Document d = docDAO.retrieveDocument(attachment.getDocumentId());
                    if(d == null) {
                        throw new DAOException("Did not locate public Document with id=" +
                                attachment.getDocumentId() + ", staging=" + staging);
                    }
                    EmissionsDocument doc = new EmissionsDocument(d);
                    doc.setEmissionsRptId(attachment.getEmissionsRptId());
                    attachment.setPublicDoc(doc);
                }
                if (attachment.getTradeSecretDocId() != null) {
                    Document d = docDAO.retrieveDocument(attachment.getTradeSecretDocId());
                    if(d == null) {
                        throw new DAOException("Did not locate trade secret Document with id=" +
                                attachment.getTradeSecretDocId() + ", staging=" + staging);
                    }
                    EmissionsDocument doc = new EmissionsDocument(d);
                    doc.setEmissionsRptId(attachment.getEmissionsRptId());
                    attachment.setTradeSecretDoc(doc);
                }
                ret.addAttachment(attachment);
            }

            // Retrieve total emissions
            EmissionTotal[]ee = emissionsRptDAO.retrieveEmissionTotals(reportId);
            ArrayList<EmissionTotal> list = new ArrayList<EmissionTotal>();
            // Establish an ordering since every row must have a distinct order value.
            int defaultOrder = 1000000000;
            for(EmissionTotal t : ee) {
                t.setOrder(defaultOrder++);
                list.add(t);
            }
            ret.setEmissionTotals(list);
            if(testTimings) {
                s = new Timestamp(System.currentTimeMillis());
                logger.error("after setEmissionTotals: " + s.toString());
            }
                EmissionsReportEU[] eus = emissionsRptDAO
                .retrieveReportEUs(reportId);
                for (EmissionsReportEU eu : eus) {
                    if(testTimings) {
                        s = new Timestamp(System.currentTimeMillis());
                        logger.error("before EmissionsReportEU eu: " + s.toString());
                    }
                    eu = emissionsRptDAO.retrieveReportEU(reportId, eu.getCorrEpaEmuId());
                    if(null == eu) {
                        eu = null;
                    }
                    ArrayList<EmissionsReportPeriod> periods = new ArrayList<EmissionsReportPeriod>();
                    EmissionsReportPeriod erpT;
                    eu.setNoProcesses(false); // we don't know what facility has
                    for (EmissionsReportPeriod erp : eu.getPeriods()) {
                        erpT = emissionsRptDAO.retrieveEmissionPeriod(erp
                                .getEmissionPeriodId());
                        if (erpT == null) {
                            logger.error("retrieveEmissionPeriod(" + erp
                                    .getEmissionPeriodId() + ") from EU with corrId " +
                                    eu.getCorrEpaEmuId() + " returned null for emissions inventory " + reportId);
                        } else {
                            erpT.setEmissionsRptId(reportId);
                            if(null != erpT.getSccId()) {
                                try {
                                    SccCode sccCode = null;
                                    sccCode = infrastructureDAO.retrieveSccCode(
                                            erpT.getSccId());
                                    if(sccCode == null) {
                                        throw new DAOException("Scc code " +
                                                erpT.getSccId() +
                                                " not found in database.  This is under Emission Unit "
                                                + eu.getEpaEmuId());
                                    }
                                    sccCode.determineDeprecated(ret.getReportYear());
                                    erpT.setSccCode(sccCode);
                                    if(sccCode.isDeprecated()) {
                                        erpT.setCaution(true);
                                    } else {
                                        Integer cnt = materialForScc(sccCode.getSccId(), ret.getReportYear());
                                        if(cnt != null && cnt.intValue() == 0) {
                                            //  should not be null, if it is ignore it.
                                            erpT.setCaution(true);
                                        }
                                    }
                                } catch(DAOException e) {
                                    throw new DAOException("Failure to read SCC code " +
                                            erpT.getSccId() +
                                            " for emissions inventory " + ret.getEmissionsRptId() +
                                            ", emissions unit " + eu.getEpaEmuId() +
                                            " and period " + erpT.getEmissionPeriodId() +
                                            ".  " + e.getMessage(), e);
                                }
                            }
                        }  
                        periods.add(erpT);
                        if(testTimings) {
                            s = new Timestamp(System.currentTimeMillis());
                            logger.error("before retrieveMaterialActionUnits: " + s.toString());
                        }
                        // Get materials
                        EmissionsMaterialActionUnits[] mau = emissionsRptDAO
                        .retrieveMaterialActionUnits(erp
                                .getEmissionPeriodId());
                        for (EmissionsMaterialActionUnits m : mau) {
                            m.setBelongs(true);
                            erpT.addMau(m);
                        }
                        if(testTimings) {
                            s = new Timestamp(System.currentTimeMillis());
                            logger.error("before retrieveEmissionsVariables: " + s.toString());
                        }
                        // Get variables
                        EmissionsVariable[] vars = emissionsRptDAO
                        .retrieveEmissionsVariables(erp
                                .getEmissionPeriodId());
                        for (EmissionsVariable v : vars) {
                            v.setBelongs(true);
                            erpT.addVar(v);
                        }
                    }
                    if(eu.isBelowRequirements() && !eu.isNoPeriods()) {
                        eu.setCaution(true);
                    }
                    eu.setPeriods(periods);
                    ret.addEu(eu);
                }
                if(testTimings) {
                    s = new Timestamp(System.currentTimeMillis());
                    logger.error("before EmissionsReportEU eu: " + s.toString());
                }
                EmissionsReportEUGroup[] grps = emissionsRptDAO
                .retrieveReportEUGroups(reportId);
                for (EmissionsReportEUGroup grp : grps) {
                    EmissionsReportEUGroup group = emissionsRptDAO
                    .retrieveReportEUGroup(grp.getReportEuGroupID());
                    if(group ==null) {
                    	//TODO Added log to find out reportId for null group which happens in invoice payment late letter.
                        logger.error("Report group is null, Report Id - " + reportId +
                                ", reportEuGroupId - " + grp.getReportEuGroupID() +
                                ", number of groups - " + grps.length);
                    }
                    checkNull(group);
                    checkNull(group.getPeriod());
                    if(null != group.getPeriod().getSccId()) {
                        try {
                            SccCode sccCode = null;
                            sccCode = infrastructureDAO.retrieveSccCode(
                                    group.getPeriod().getSccId());
                            if(sccCode == null) {
                                throw new DAOException("Scc code " +
                                        group.getPeriod().getSccId() +
                                        " not found in database.  This is in group "
                                        + group.getReportEuGroupName());
                            }
                            sccCode.determineDeprecated(ret.getReportYear());
                            group.getPeriod().setSccCode(sccCode);
                            if(sccCode.isDeprecated()) {
                                group.getPeriod().setCaution(true);                          
                            } else {
                                Integer cnt = materialForScc(sccCode.getSccId(), ret.getReportYear());
                                if(cnt != null && cnt.intValue() == 0) {
                                    //  should not be null, if it is ignore it.
                                    group.getPeriod().setCaution(true);
                                }
                            }
                        } catch(DAOException e) {
                            throw new DAOException("Failure to read SCC code " +
                                    group.getPeriod().getSccId() +
                                    " for emissions inventory " + ret.getEmissionsRptId() +
                                    " and group " + group.getReportEuGroupName() +
                                    ".  " + e.getMessage(), e);
                        }
                    }
                    Emissions[] emissions = emissionsRptDAO.
                    retrieveGroupEmissions(group.getPeriod().getEmissionPeriodId());
                    for(Emissions e : emissions) {
                        group.getPeriod().addEmission(e);
                    }

                    // Get materials
                    EmissionsMaterialActionUnits[] mau = emissionsRptDAO
                    .retrieveMaterialActionUnits(group.getPeriod()
                            .getEmissionPeriodId());
                    for (EmissionsMaterialActionUnits m : mau) {
                        m.setBelongs(true);
                        group.getPeriod().addMau(m);
                    }

                    // Get variables
                    EmissionsVariable[] vars = emissionsRptDAO
                    .retrieveEmissionsVariables(group.getPeriod()
                            .getEmissionPeriodId());
                    for (EmissionsVariable v : vars) {
                        v.setBelongs(true);
                        group.getPeriod().addVar(v);
                    }
                    ret.addEuGroup(group);
                }
                ret.setServiceCatalogs(retrieveAssociatedSCEmissionsReports(ret,staging));
                ret.setInspectionsReferencedIn(fullComplianceEvalDAO().retrieveInspectionIdsForEmissionRptId(reportId));
        }
        return ret;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public EmissionsReportSearch[] searchEmissionsReports(EmissionsReportSearch searchObj,
            boolean staging)
    throws DAOException {
           	
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(staging));
        return emissionsRptDAO.searchEmissionsReports(searchObj);
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public EmissionsReportSearch[] searchEmissionsReportForScore(EmissionsReportSearch searchObj)
    throws DAOException {

        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO();
        return emissionsRptDAO.searchEmissionsReportForScore(searchObj);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public FacilityYearPair[] retrieveStragglerNTV(int oddYear, int evenYear)
    throws DAOException {
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(false));
        return emissionsRptDAO.retrieveStragglerNTV(oddYear, evenYear);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Integer[] searchFacilities(List<String> counties, String doLaaCd, String facilityId, int firstYear, int lastYear,
            boolean staging)
    throws DAOException {

        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(staging));
        return emissionsRptDAO.searchFacilities(counties, doLaaCd, facilityId, firstYear, lastYear);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ArrayList<EmissionsRptInfo> getYearlyReportingInfo(String facilityId)
    throws DAOException {
        EmissionsRptInfo[] result = null;

        result = emissionsReportDAO().retrieveEmissionsRptInfos(facilityId);

        ArrayList<EmissionsRptInfo> res = new ArrayList<EmissionsRptInfo>(
                result.length);
        for (int i = 0; i < result.length; i++) {
            res.add(result[i]);
        }
        return res;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public EmissionsRptInfo getCurrentReportingInfo(Facility f) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        EmissionsRptInfo i = new EmissionsRptInfo();
        i.setYear(year);
        if(f != null) {
            i.setState(ReportReceivedStatusDef.REPORT_NOT_REQUESTED);
            if (!f.getOperatingStatusCd().equals(OperatingStatusDef.OP)) {
                i.setState(ReportReceivedStatusDef.NO_REPORT_NEEDED);
            }
        }
        return i;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void updateYearlyReportingInfo(String facilityId,
            ArrayList<EmissionsRptInfo> info) throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        try {
            updateYearlyReportingInfo(facilityId, info, trans);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);  //does throw
        } finally {
            closeTransaction(trans);
        }
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void updateYearlyReportingInfo(String facilityId,
            ArrayList<EmissionsRptInfo> info,
            Transaction trans) throws DAOException {
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(trans);
        for (EmissionsRptInfo i : info) {
            if (null != i.getIndatabase() && i.getIndatabase()) {
                emissionsRptDAO.modifyEmissionsRptInfo(facilityId, i);
            } else {
                emissionsRptDAO.createEmissionsRptInfo(facilityId, i);
            }
        }
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public boolean updateYearlyReportingInfo(String facilityId,
            SCEmissionsReport scEmissionsReport, Transaction trans,
            String  state, Boolean enableReporting, String comment) throws DAOException {
        /*
         * Set:
         * state from def class ReportStatusDef
         * comment is concatenated to any existing comment.
         * Create record if it does not exist.
         * 
         * Return of false means that no row was created.
         */
        EmissionsRptInfo info;
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(trans);
        EmissionsRptInfo[] infos = emissionsRptDAO.retrieveEmissionsRptInfos(facilityId);
        ArrayList<EmissionsRptInfo> list = new ArrayList<EmissionsRptInfo>();
        boolean updateExisting = false;
        int existingIndex = -1;
        if(infos.length> 0) {
            for(int i=0; i < infos.length; i++) {
            	if(infos[i].getScEmissionsReportId().equals(scEmissionsReport.getId())) {
                    updateExisting = true;
                    existingIndex = i;
                }
            }
        }
        if(updateExisting) {
            info = infos[existingIndex];
            info.setState(state);
        } else {
            info = new EmissionsRptInfo();
            info.setYear(new Integer(scEmissionsReport.getReportingYear()));
            info.setContentTypeCd(new String(scEmissionsReport.getContentTypeCd()));
            info.setRegulatoryRequirementCd(new String(scEmissionsReport.getRegulatoryRequirementCd()));
            info.setScEmissionsReportId(new Integer(scEmissionsReport.getId()));
            info.setState(state);
        }
        if(null != comment) {
            if(null != info.getComment()) {
                info.setComment(info.getComment() + "; " + comment);
            } else {
                info.setComment(comment);
            }
        }
        if(null != enableReporting && enableReporting) {
        	info.setReportingEnabled(true);
        }
        list.add(info);
        updateYearlyReportingInfo(facilityId, list, trans);
        return true;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void updateYearlyReportingToShutdown(String facilityId,
            int year, Transaction trans) throws DAOException {
        /*
         * Set:
         * state from def class ReportStatusDef
         * comment is concatenated to any existing comment.
         */
        EmissionsRptInfo info;
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(trans);
        EmissionsRptInfo[] infos = emissionsRptDAO.retrieveEmissionsRptInfos(facilityId);
        ArrayList<EmissionsRptInfo> list = new ArrayList<EmissionsRptInfo>();
        boolean updateExisting = false;
        for(EmissionsRptInfo eri : infos) {
            if(eri.getYear().intValue() < year) {
                continue;
            }
            updateExisting = true;
            eri.setIndatabase(new Boolean(true));
            String exComment = eri.getComment();
            if(exComment == null) {
                exComment = "";
            }
            String s = "Shutdown: state was: " +
            ReportReceivedStatusDef.getData().getItems().getItemDesc(
                    eri.getState()) + "; " + exComment;
            if(s.length() <= 4000) {
                eri.setComment(s);
            }
            list.add(eri);
        }

        updateYearlyReportingInfo(facilityId, list, trans);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public int turnOnReporting(SCEmissionsReport scEmissionsReport)
    throws DAOException {
        int cnt = 0;
        // enable reporting for all qualifying facilities for year.
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO();
        Transaction trans = TransactionFactory.createTransaction();
        emissionsRptDAO.setTransaction(trans);
        // turn on reporting for facilities that have entry in table
        EmissionsRptInfo[] eRptInfos = emissionsRptDAO.turnOnReporting(scEmissionsReport);
		if (eRptInfos != null) {
			for (int j = 0; j < eRptInfos.length; j++) {
				
				boolean setEnabled = true;
				if (setEnabled) {
					eRptInfos[j].setReportingEnabled(true);
					eRptInfos[j].setState(ReportReceivedStatusDef.REPORT_NOT_REQUESTED);
					emissionsRptDAO.modifyEmissionsRptInfo(
							eRptInfos[j].getFacilityId(), eRptInfos[j]);
					cnt++;
				}
			}
		}
        FacilityRptInfo[] infos = null;
        
        // get facilities that meet the search criteria that don't have entry for this year 
        infos = emissionsRptDAO.missingEmissionsRptInfo(scEmissionsReport);
        
		try {
			for (int i = 0; i < infos.length; i++) {
				String comment = null;
				String state = ReportReceivedStatusDef.REPORT_NOT_REQUESTED;
				Boolean enabled = new Boolean(true);
				if (infos[i].getOperatingStatusCd().equals("sd")) {
					comment = "Shutdown";
				}
				boolean createdRow = updateYearlyReportingInfo(
						infos[i].getFacilityId(), scEmissionsReport, trans,
						state, enabled, comment);
				if (createdRow) {
					cnt++;
				}
			}
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de); // does throw
		} finally {
			closeTransaction(trans);
		}
        return cnt;
    }
    
    @Override
    public List<MissingFIREFactor> retrieveMissingFactors(int year, String facilityClass) throws DAOException {
    	List<MissingFIREFactor> nonUniqueFactors = new ArrayList<MissingFIREFactor>();
    	
    	EmissionsReportDAO emissionsRptDAO = emissionsReportDAO();
    	nonUniqueFactors = emissionsRptDAO.retrieveMissingFactors(year, facilityClass);
    	
    	Set<MissingFIREFactor> uniqueFactors = new HashSet<MissingFIREFactor>(nonUniqueFactors);
    	
    	List<MissingFIREFactor> ret = new ArrayList<MissingFIREFactor>(uniqueFactors);
    	
    	return ret;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ArrayList<EmissionsMaterialActionUnits> compareMaterials(
            EmissionsReportPeriod period, Integer percentDiff, Integer origYear, Integer compYear) {
        ArrayList<EmissionsMaterialActionUnits> mList =
            new ArrayList<EmissionsMaterialActionUnits>();
        /*
         * ASSUME: single material per period.
         */
        EmissionsMaterialActionUnits mauOrig = new EmissionsMaterialActionUnits();
        if(null != period.getOrig() && period.getOrig().getCurrentMaus() != null) {
            mauOrig = new EmissionsMaterialActionUnits(period.getOrig().getCurrentMaus());
            mauOrig.populateFields(period.getOrig().getCurrentMaus());
            mauOrig.setTradeSecretM(period.getOrig().isTradeSecretS());
            mauOrig.setTradeSecretT(period.getOrig().isTradeSecretS());
        }
        mauOrig.setReportYear(origYear);
        mList.add(mauOrig);
        EmissionsMaterialActionUnits mauComp = new EmissionsMaterialActionUnits();
        if(null != period.getComp() && period.getComp().getCurrentMaus() != null) {
            mauComp = new EmissionsMaterialActionUnits(period.getComp().getCurrentMaus());
            mauComp.populateFields(period.getComp().getCurrentMaus());
            mauComp.setTradeSecretM(period.getComp().isTradeSecretS());
            mauComp.setTradeSecretT(period.getComp().isTradeSecretS());
        }
        mauComp.setReportYear(compYear);
        mList.add(mauComp);
        if(null != mauOrig.getMaterial() || null != mauComp.getMaterial()) {
            if(null == mauOrig.getMaterial() || null == mauComp.getMaterial()) {
                mauOrig.setMaterialDiff(true);
            } else if (!mauOrig.getMaterial().equals(mauComp.getMaterial())) {
                mauOrig.setMaterialDiff(true);
            }
        }
        // If measure different then indicate throughput different
        if(null != mauOrig.getMeasure() || null != mauComp.getMeasure()) {
            if(null == mauOrig.getMeasure() || null == mauComp.getMeasure()) {
                mauOrig.setThroughputDiff(true);
            } else if (!mauOrig.getMeasure().equals(mauComp.getMeasure())) {
                mauOrig.setThroughputDiff(true);
            }
        }

        double diffPercent = percentDiff.intValue()/100.0f;
        if(null != mauOrig.getThroughputV() || null != mauComp.getThroughputV()) {
            if(null != mauOrig.getThroughputV() && null != mauComp.getThroughputV()) {
                mauOrig.setThroughputDiff(EmissionRow.flagDifference(
                        mauComp.getThroughputV(), mauOrig.getThroughputV(), diffPercent));
            } else
                mauOrig.setThroughputDiff(true);
        }

        Double hoursPerYearOrig = null;
        if(period.getOrig() != null && period.getOrig().getHoursPerYear() != null) {
            hoursPerYearOrig = period.getOrig().getHoursPerYear();
        }
        Double hoursPerYearComp = null;
        if(period.getComp() != null && period.getComp().getHoursPerYear() != null) {
            hoursPerYearComp = period.getComp().getHoursPerYear();
        }
        if(hoursPerYearOrig != null || hoursPerYearComp != null) {
            if(hoursPerYearOrig != null && hoursPerYearComp != null) {
                period.setHpyDiff(EmissionRow.flagDifference(hoursPerYearComp.doubleValue(),
                        hoursPerYearOrig.doubleValue(), diffPercent));
            } else {
                period.setHpyDiff(true);
            }
        }
        return mList;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ArrayList<EmissionsMaterialActionUnits> processMaterialActions(
            int year,
            boolean submitted, EmissionsReportPeriod period, FireRow[] rows) {
        /*
         * Generate list of materials/actions/measures
         * Mark those that already belong to period.
         * 
         * If none belong, not submitted and only one material exists
         * add it to the period.
         */
        ArrayList<EmissionsMaterialActionUnits> mList =
            new ArrayList<EmissionsMaterialActionUnits>();

        EmissionsMaterialActionUnits currentMaus = period.getCurrentMaus();
        EmissionsMaterialActionUnits newMausRow = null;
        l1:
        for (FireRow row : rows) {
            boolean notActive = false;
            if(!row.isActive(year)) {
                notActive = true;
            }
            newMausRow = new EmissionsMaterialActionUnits(row.getMaterial(), row
                    .getAction(), row.getMeasure(), notActive);
            if(!row.isActive(year) && (currentMaus == null || !currentMaus.materialActionSame(newMausRow))) {
                // don't include non-active ones unless already in use.
                continue;
            }
            // Determine if already included
            for(EmissionsMaterialActionUnits emau : mList) {
                if(emau.materialActionSame(newMausRow)) continue l1;
            }
            mList.add(newMausRow);
        }

        // If not submitted, populate with material and corresponding
        // pollutants.
        if (!submitted && period.getMaus().isEmpty() && mList.size() == 1) {
            EmissionsMaterialActionUnits me = new EmissionsMaterialActionUnits(
                    mList.get(0));
            me.setBelongs(true);
            period.addMau(me);  // make sure cannot get more than one.
        }

        // Merge with materials actually included so can see all possible
        // materials
        // This is done regardless of whether already submitted to see materials
        // not used.
        for (EmissionsMaterialActionUnits e : mList) {
            EmissionsMaterialActionUnits pe = period.findRow(e);
            if (null != pe) {
                e.setBelongs(true);
                pe.setNotActive(e.isNotActive());
                e.populateFields(pe);
            }
        }
        return mList;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public boolean ntvDolaaApprove1(NtvReport ntvReport, Facility facility,
            int user) throws DAOException{
        Transaction trans = TransactionFactory.createTransaction();
        return ntvDolaaApprove1(ntvReport, facility, user, trans);
    }

    private boolean ntvDolaaApprove1(NtvReport ntvReport, Facility facility,
            int user, Transaction trans) throws DAOException{

        boolean succeeded = true;
        boolean createWF = COReviewNeeded(facility, ntvReport);
        //Create work flow
        if(createWF) {
            WorkFlowProcess[] processes;
            ReadWorkFlowService wfBO;
            Integer workflowId = null;
 
            try {

                workflowId = wfService.retrieveWorkflowTempIdAndNm().
                get(WorkFlowProcess.CO_REVIEW);
                if(workflowId == null) {
                    String s = "Failed to find workflowId based upon CO_REVIEW for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId();
                    logger.error(s);
                    throw new DAOException(s);
                }
                final Integer ntvErptTemplateId = workflowId;
                WorkFlowProcess wfProcess = new WorkFlowProcess();
                wfProcess.setProcessTemplateId(ntvErptTemplateId);
                wfProcess.setExternalId(ntvReport.getPrimary().getEmissionsRptId());
                wfProcess.setCurrent(true);
                wfProcess.setUnlimitedResults(true);
                // Find the workflow process for this report
                processes = wfService.retrieveProcessList(wfProcess);

            } catch (RemoteException e) {
                logger.error("Caught RemoteException checking for exisiting workflow for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId(), e);
                throw new DAOException("Caught RemoteException checking for exisiting workflow for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId() + e.getMessage(), e);
            }
            if(processes.length > 0) {
                // already a workflow
                logger.warn("Pre-existing CO Review workflow for report "
                        + ntvReport.getPrimary().getEmissionsRptId());
            } else {
                Timestamp dueDt = null;
                String rush = "N";
                try { 
                    Timestamp d = new Timestamp(new java.util.Date().getTime());
                    WorkFlowManager wfm = new WorkFlowManager();
                    WorkFlowResponse resp = wfm.submitProcess(workflowId,
                            ntvReport.getPrimary().getEmissionsRptId(),
                            facility.getFpId(), user, rush, 
                            d, dueDt, null);

                    if (resp.hasError() || resp.hasFailed()) {
                        String[] errorMsgs = resp.getErrorMessages();
                        String[] recomMsgs = resp.getRecommendationMessages();
                        StringBuffer bs = new StringBuffer("Errors from WorkFlow Engine: ");
                        for(String msg : errorMsgs) {
                            bs.append(msg + " ");
                        }
                        if(errorMsgs.length > 0 && recomMsgs.length > 0) {
                            bs.append("; ");
                        }
                        for(String msg : errorMsgs) {
                            bs.append(msg + " ");
                        }
                        throw new DAOException(bs.toString());
                    }
                } catch (DAOException de) {
                    succeeded = false;
                    logger.error("Exception for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId(), de);
                }catch (Exception e) {
                    succeeded = false;
                    logger.error("Exception for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId(), e);
                } finally {
                    if(!succeeded) {
                        if (trans != null) {
                            trans.cancel();
                        }
                        closeTransaction(trans);
                        String s = "Failed to create CO Review workflow for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId();
                        throw new DAOException(s, s);
                    }
                }
            }
        }
        trans.complete();
        closeTransaction(trans);
        return succeeded;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public NtvReport ntvDolaaApprove2(NtvReport ntvReport, Facility facility,
            String state, int user, boolean existingPurchaseOwner) throws DAOException{
        Transaction trans = TransactionFactory.createTransaction();
        return ntvDolaaApprove2(ntvReport, facility, state,
                user, existingPurchaseOwner, trans);
    }

    private NtvReport ntvDolaaApprove2(NtvReport ntvReport, Facility facility,
            String state, int user,
            boolean existingPurchaseOwner, Transaction trans) throws DAOException{
        // Note that the function returns the report just to return the
        // billingContactFailureMsg.
        String rtnErr = "";
        NtvReport ret = ntvReport;
        boolean failed = false;
        try {
            // Build the invoice
            Invoice newInv = buildInvoice(facility, ntvReport);
            if(newInv != null) {
                if(ntvReport.getPrimary().getBillingContactFailureMsg() != null) {
                    // need to fail, note, no database operations performed.
                    trans.cancel();
                    closeTransaction(trans);
                    return ntvReport; // report unchanged except for error message
                }
                // create the invoice;
                //InfrastructureHelper infraHelper = new InfrastructureHelper();

                newInv = infraHelper.createInvoice(newInv, trans); // create the invoice
            }
        } catch (DAOException de) {
            failed = true;
            if(!de.prettyMsgIsNull()) {
                rtnErr = de.getPrettyMsg();
            }
            logger.error("Exception for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId(), de);
        }catch (Exception e) {
            failed = true;
            logger.error("Exception for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId(), e);
        } finally {
            if(failed) {
                if (trans != null) {
                    trans.cancel();
                }
                closeTransaction(trans);
                String s = "Failed to create the IMPACT Invoice object for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId() + rtnErr;
                throw new DAOException(s, s);
            }
        }

        try {
            Timestamp d = new Timestamp(new java.util.Date().getTime());
            // Update report attributes and set state in facility inventory
            EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(trans);
            if(ntvReport.getReport1() != null) {
                ntvReport.getReport1().setRptReceivedStatusCd(state);
                ntvReport.getReport1().setRptApprovedStatusDate(d);
                emissionsRptDAO.modifyEmissionsReport(ntvReport.getReport1(), null);  // update report.
            }
            if(ntvReport.getReport2() != null) {
                ntvReport.getReport2().setRptReceivedStatusCd(state);
                ntvReport.getReport2().setRptApprovedStatusDate(d);
                emissionsRptDAO.modifyEmissionsReport(ntvReport.getReport2(), null);  // update report.
            }
        } catch (DAOException de) {
            failed = true;
            logger.error("Exception for report " + ntvReport.getPrimary().getEmissionsInventoryId(), de);
        }catch (Exception e) {
            failed = true;
            logger.error("Exception for report " + ntvReport.getPrimary().getEmissionsInventoryId(), e);
        } finally {
            if(failed) {
                if (trans != null) {
                    trans.cancel();
                }
                closeTransaction(trans);
                String s = "Failed to update the emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId();
                throw new DAOException(s, s);
            }
        }

        /* DESIGN
         * 
         * Contacts in the Facility to use:
         *
         * For Primary and Billing, use Dec 31, YYYY where YYYY is the lastest
         * year that this report is reporting on.
         * 
         * For Current Owners, use all owners that do not have an End Date.
         * 
         * Restrictions: ????
         *    Transfer Date must be within the span of the report and the following
         *    year.
         * 
         * Update Facility:
         * 
         * There are two types of Contact update.
         * A contact consists of name and information.
         * Each contact must have a unique name (whose components may include first,
         * middle and last name and company name).  If the name is the same but
         * the information has changed, then the existing contact is updated.  Since
         * the contact is stored once, any roles (past or present) using that contact
         * also have that updated information.
         * If the name and information is the same (except for capitalization) then
         * the contact is still updated.  If it is
         * a new contact name, then the contact is added to the facility (without any
         * roles--until the below rules are applied).
         * 
         *   New Owner Address (case 1: provided by previous owner)
         *      Use for New Owner and Updated Primary Contact.  Do not provide
         *      input for separate Updated Primary Contact.  Also do not provide
         *      for old owner forwarding address.  Start Date will be Transfer Date.
         *      
         *   New Owner Address (case 2: provided by new owner)
         *      Use for New Owner Contact only.  Separately the new owner can
         *      supply updated Primary Contact.
         *      
         *   New Owner Address (both cases)
         *      All previous owners are ended on the day prior to the Transfer Date.
         *      
         *   Old Owner Forwarding Address:  Only allowed if filled out by New
         *      Owner and the new owner does not report/pay for both years.
         *      This is determined by whether the Even Year is included.  If
         *      included (with FeeId), then no reason to contact previous owner.
         *      (We assume that any ownership change prior to this billing cycle
         *      has already been handled--because someone should have already paid
         *      for that previous cycle).
         *      The End Date will be Transfer Date -1.
         *      The Start Date will be 
         *      
         *      Update fails at approve time if the old owner Primary Contact
         *      started one day prior to transfer date--because then we cannot
         *      insert the changed Primary Contact.
         *      
         *      SEE COMMENTS BELOW IN updateContact2 AND UPDATE THIS.
         * 
         *   Updated Primary Contact provided (by new owner) and this report
         *      is current report:
         *      Locate current Primary Contact and it's Start Date--PC_StartDate
         *      Determine the earlier of December 31, FirstYear and Transfer Date
         *      Use the larger of that and PC_StartDate as the new End date for
         *      Primary Contact.  Add the Primary Contact role to this contact.
         *      However, do nothing if the updated Primary Contact is already the
         *      Primary Contact--user was just updating the contact information.
         *      
         *      If not current report or if filed by old owner, do not ask for
         *      updated Primary Contact.
         *      
         *   Updated Billing Contact provided by new owner and this report
         *      is current report: Handled similar to Primary Contact.
         *      
         *   Updated Billing Contact provided and report is not current:
         *      Do not update facility with this information.  Also, do not
         *      update an existing contact in Facility if this has the same
         *      contact name.  The only use made
         *      of this changed billing contact is the ContactId is placed into
         *      the Stars2 Invoice and will be used for mailing the bill.
         *      
         *   Updated Billing Contact provided by old owner and this report is
         *      current report:  Handle the same as non-current report because
         *      the billing contact in facility may remain the same for the
         *      new owner, but the old owner has a special place to send the
         *      last bill.
         *
         *   Note that these contacts kept with the NTV Report should remain
         *   unchanged.  The information in them is used to update Facility
         *   information, but the Contact object with the report is not put
         *   into the Facility.
         *   
         *   Updated Facility Name:
         *      Regardless of anything else, update the Facility with this name.
         *      
         *   Transfer Date specified, new owner filling out report, this
         *   is the current report and Transfer Date is newer than any existing
         *   owner start date.  New Owner Contact is required.
         *      Add the new owner using the Transfer Date as the start date.
         *      Any existing owners have their End Date set to the Transfer Date
         *      
         *   Shutdown specified:
         *      Must be within the reporting period or the following year.  The date
         *      is placed into the facility as the shutdown date.  The Shutdown
         *      Notification Date is set to the Receive Date of the NTV report.
         *      The same actions will be taken as when AQD directly sets the
         *      shutdown date in the Facility (if that would generate log
         *      entries, this will too).
         *      
         *      How dates are handled.  The Start and End dates are inclusive.
         *      In order to not overlap, the Start date for the next contact with
         *      the same role must be at least one day higher.  For example if one
         *      contact ends on Dec 31, 2006, then the next contact cannot start
         *      until Jan 1, 2007.  The Transfer Date is the first day for the new
         *      owner; therefore, the last day for the previous owner is one day
         *      less than that.
         */

        /* Handle updated Primary Contact
         *   At approval confirm that this report is still current.
         */

        // contactRefDate is last date owned by this owner
        Timestamp contactRefDate = ntvReport.getContactRefDate();
        Calendar cal = Calendar.getInstance();
        InfrastructureDAO infraDAO = null;
        FacilityDAO facilityDAO = null;
        // logger.debug("contactRefDate= " + contactRefDate);
        try {
            infraDAO = infrastructureDAO(getSchema(false));
            infraDAO.setTransaction(trans);
            facilityDAO = facilityDAO(getSchema(false));
            facilityDAO.setTransaction(trans);
        } catch (Exception e) {
            failed = true;
            logger.error("Exception", e);
        } finally {
            if(failed) {
                if (trans != null) {
                    trans.cancel();
                }
                closeTransaction(trans);
                String s = "Failed to update the emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId();
                throw new DAOException(s, s);
            }
        }

        cal.setTimeInMillis(ntvReport.getPrimary().getReceiveDate().getTime());
        Timestamp receiveTime = dropPartialDay(new Timestamp(cal.getTimeInMillis()));
        // logger.debug("ReceiveTime=" + receiveTime);

        Timestamp transfer = dropPartialDay(ntvReport.getPrimary().getTransferDate());
        /* Handle Old Owner Forwarding address (Primary)
         * Do it before new owner primiary handled.
         */
        try {
            rtnErr = updateConOldOwn(ntvReport.getPrimary().getPrevOwnerForwardingAddr(),
                    transfer,
                    ntvReport.getFirstNotPaid(), user, facility,
                    facilityService, facilityDAO, infraDAO, trans);
            if(rtnErr.length() > 0) {
                failed = true;
            }
        } catch (DAOException de) {
            failed = true;
            logger.error("Exception for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId(), de);
        }catch (Exception e) {
            failed = true;
            logger.error("Exception for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId(), e);
        } finally {
            if(failed) {
                if (trans != null) {
                    trans.cancel();
                }
                closeTransaction(trans);
                String s = "Failed to update old owner forwarding addresss." + rtnErr + " for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId();
                throw new DAOException(s, s);
            }
        }

        Contact newOwnerPrimary = null;
        Timestamp newOwnerPrimaryTimeStamp = null;
        try {
            // Handle New/Updated Owner field
            //InfrastructureHelper infraHelper = new InfrastructureHelper();
            if(ntvReport.getPrimary().getNewOwnerAddr() != null) {
                boolean knownOwner = transfer == null;
                if(!knownOwner) {
                    // Determine ownership change choices
                    List<SelectItem> pickListTransfers;
                    pickListTransfers = facility.ownershipChange(ntvReport.getLeftPoint());
                    // If transfer date already known, then not new owner (one that can be updated)
                    for(SelectItem si : pickListTransfers) {
                        if(transfer.equals((Timestamp)si.getValue())) {
                            knownOwner = true;
                            break;
                        }
                    }
                }
                if(knownOwner) {
                    // Same owner, but updated address.
                    // Cannot change owner name.
                    ArrayList<SelectItem> currentNewOwnerPickList = new ArrayList<SelectItem>();
                    //facility.currentContactsPickList(currentNewOwnerPickList, ContactTypeDef.OWNR);
                    Contact match = null;
                    for(SelectItem i : currentNewOwnerPickList) {
                        Contact c = (Contact)(i.getValue());
                        if(c.sameContact(ntvReport.getPrimary().getNewOwnerAddr())){
                            match = c;
                            break;
                        }
                    }
                    if(match == null) {
                        String s = "For facility "
                            + facility.getFacilityId()
                            + " and emissions inventory "
                            + ntvReport.getPrimary().getEmissionsInventoryId()
                            + " did not find current owner matching name "
                            + ntvReport.getPrimary().getNewOwnerAddr()
                            .getName();
                        logger.error(s);
                        throw new DAOException(s);
                    }
                    // Update owner address
                    ntvReport.getPrimary().getNewOwnerAddr().setContactId(
                            match.getContactId());
                    ntvReport.getPrimary().getNewOwnerAddr().setLastModified(
                            match.getLastModified());
                    ntvReport.getPrimary().getNewOwnerAddr().getAddress()
                    .setAddressId(match.getAddress().getAddressId());
                    ntvReport.getPrimary().getNewOwnerAddr().getAddress()
                    .setLastModified(
                            match.getAddress().getLastModified());
                    infraHelper.modifyContactData(ntvReport.getPrimary()
                            .getNewOwnerAddr(), trans);

                } else {
                    // Replace owners with new owner.
                    Contact[] pCArray = facilityService.retrieveDupFacilityContact(
                            ntvReport.getPrimary().getNewOwnerAddr(), trans);
                    if(pCArray != null && pCArray.length > 1) {
                        String s = "New Owner Contact " + pCArray[0].getName() + " is duplicated for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId();
                        throw new DAOException(s, s);
                    }
                    updateInsert(ntvReport.getPrimary().getNewOwnerAddr(),
                            pCArray, facility, facilityService, facilityDAO, trans);
                    // End old owners
                    cal = Calendar.getInstance();
                    cal.setTimeInMillis(transfer.getTime());
                    cal.add(Calendar.DAY_OF_YEAR, -1);
                    Timestamp endOwnership = dropPartialDay(new Timestamp(cal.getTimeInMillis()));
                    for(ContactUtil cu : facility.currentOwnersUtil()) {
                        Timestamp use = endOwnership;
                        if(cu.getContactType().getStartDate().equals(endOwnership)){
                            use = transfer;
                        }
                        modifyContactType(infraHelper, cu.getContactType(),
                                use, facility.getFpId(), user, trans);
                    } 
                    // Start new owner
                   /* infraDAO.addContactType(ntvReport.getPrimary().getNewOwnerAddr().getContactId(),
                            ContactTypeDef.OWNR, transfer);*/

                    // Also use same contact as Primary Contact if provided by previous owner
                    if(!ntvReport.getPrimary().isNewOwner()) {
                        newOwnerPrimary = ntvReport.getPrimary().getNewOwnerAddr();
                        newOwnerPrimaryTimeStamp = transfer;
                    }
                }

                // Refresh contact in memory with changes already made.
                facility.setAllContacts(
                        facilityDAO.retrieveFacilityContacts(
                                facility.getFacilityId()));
            }
        } catch (DAOException de) {
            failed = true;
            logger.error("Exception for report " + ntvReport.getPrimary().getEmissionsInventoryId(), de);
        }catch (Exception e) {
            failed = true;
            logger.error("Exception for report " + ntvReport.getPrimary().getEmissionsInventoryId(), e);
        } finally {
            if(failed) {
                if (trans != null) {
                    trans.cancel();
                }
                closeTransaction(trans);
                String s = "Failed to update New/Updated Owner for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId();
                throw new DAOException(s, s);
            }
        }

        try { 
            // Handle updated Billing Contact
            if(existingPurchaseOwner) {
                // Don't update if specified by old owner--only used for invoice
                rtnErr = updateContact(ntvReport.getPrimary().getBillingAddr(),
                        ContactTypeDef.BILL, contactRefDate,
                        receiveTime, null, user, facility, facilityService,
                        facilityDAO, infraDAO, trans);
                if(rtnErr.length() > 0) {
                    failed = true;
                }
            }
        } catch (DAOException de) {
            failed = true;
            logger.error("Exception for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId(), de);
        }catch (Exception e) {
            failed = true;
            logger.error("Exception for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId(), e);
        } finally {
            if(failed) {
                if (trans != null) {
                    trans.cancel();
                }
                closeTransaction(trans);
                String s = "Failed to update Billing Contact for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId() + rtnErr;
                throw new DAOException(s, s);
            }
        }

        try {
            // Handle updated Primary Contact
            Contact newPrimaryContact = ntvReport.getPrimary().getPrimaryAddr();
            Timestamp beginTime = receiveTime;
            if(newOwnerPrimary != null) {
                newPrimaryContact = newOwnerPrimary;
                beginTime = newOwnerPrimaryTimeStamp;
            }
            rtnErr = updateContact(newPrimaryContact,
                    null, contactRefDate,
                    beginTime, null, user, facility, facilityService,
                    facilityDAO, infraDAO, trans);
            if(rtnErr.length() > 0) {
                failed = true;
            }
        } catch (DAOException de) {
            failed = true;
            logger.error("Exception for emissions inventory " + ntvReport.getPrimary().getEmissionsRptId(), de);
        }catch (Exception e) {
            failed = true;
            logger.error("Exception for emissions inventory " + ntvReport.getPrimary().getEmissionsRptId(), e);
        } finally {
            if(failed) {
                if (trans != null) {
                    trans.cancel();
                }
                closeTransaction(trans);
                String s = "Failed to update Primary Contact for emissions inventory " + ntvReport.getPrimary().getEmissionsRptId()  + rtnErr;
                throw new DAOException(s, s);
            }
        }

        boolean shutdownChange = false;
        boolean nameChange = false;
        boolean alreadyDisplayed = false;
        try {
            // Update facility name and shutdown date
            if(ntvReport.getPrimary().getFacilityNm() != null &&
                    ntvReport.getPrimary().getFacilityNm().trim().length() > 0) {
                nameChange = true;
                facility.setName(ntvReport.getPrimary().getFacilityNm().trim());
            }
            if (ntvReport.getPrimary().getShutdownDate() != null) {
                if (facility.getOperatingStatusCd().equals(OperatingStatusDef.SD)
                        && !ntvReport.getPrimary().getShutdownDate()
                        .equals(facility.getShutdownDate())) {
                    alreadyDisplayed = true;
                    failed = true;
                    String s = "Facility already shutdown.  Shutdown date cannot be specified in emissions inventory.";
                    throw new DAOException(s, s);
                }
                if(!facility.getOperatingStatusCd().equals(OperatingStatusDef.SD)) {
                    shutdownChange = true;
                    facility.setOperatingStatusCd(OperatingStatusDef.SD);
                    facility.setShutdownNotifDate(ntvReport.getPrimary().getReceiveDate());
                    facility.setShutdownDate(ntvReport.getPrimary().getShutdownDate());
                }
            }
            if(!failed && (shutdownChange || nameChange)) {
                HashMap<String, String> shutdownToDoData = null;
                shutdownToDoData = facilityService.modifyFacilityRtnToDo(facility, user);
                // Note that if the ntv submit does an automatic approve, then
                // the facility was not shutdown and an empty HashMap is returned.
                ntvReport.setShutdownToDoData(shutdownToDoData);
            }

        } catch (DAOException de) {
            failed = true;
            logger.error("Exception for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId(), de);
        }catch (Exception e) {
            failed = true;
            logger.error("Exception for emissions inventory " + ntvReport.getPrimary().getEmissionsInventoryId(), e);
        } finally {
            if(failed) {
                if (trans != null) {
                    trans.cancel();
                }
                closeTransaction(trans);
                if(!alreadyDisplayed) {
                    String s = "Failed to update ";
                    if(nameChange) {
                        s = s + "Facility Name";
                    }
                    if(nameChange && shutdownChange) {
                        s = s + " or ";
                    }
                    if(shutdownChange) {
                        s = s + "shutdown information";
                    }
                    throw new DAOException(s, s);
                }
            }
        }
        trans.complete();
        closeTransaction(trans);
        return ret;
    }

    private static void modifyContactType(InfrastructureHelper infraHelper, ContactType ct, Timestamp ts,
            Integer fpId, int user, Transaction trans) throws DAOException {
        ContactType current = new ContactType(ct);
        ct.setEndDate(ts);
        infraHelper.modifyContactType(current, ct, fpId, user, trans);
    }

    boolean COReviewNeeded(Facility facility, NtvReport ntvReport)
    throws DAOException {
        /* Determine whether CO NTV Review Workflow should be created.
         * Consider the two years.
         *
         * If for one of these two years this report does not specify emissions 
         * (either because it is a one year report or because it does not specify 
         * emissions for one or more of the years it does cover) then look into the 
         * yearly reporting category table.
         *
         * If the yearly reporting category table indicates that a NTV report is 
         * owed for one of those missing years then create the CO review.

         * Note that we will not try to look for another report already submitted that 
         * might cover the missing years.
         * 
         * Also note that shutdown not yet refected in yearly category table.
         */
        boolean createWF = false;
        boolean rpt1Missing = false;
        boolean rpt2Missing = false;
        int shutdownYear = 0;
        Timestamp shutdown = ntvReport.getPrimary().getShutdownDate();
        if(shutdown != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(shutdown.getTime());
            shutdownYear = cal.get(Calendar.YEAR);
        }
        if(ntvReport.getReport1() == null) {
            rpt1Missing = true;
        } else {
            if(ntvReport.getReport1().getFeeId() == null) {
                rpt1Missing = true;
            }
        }
        if(rpt1Missing && shutdown != null &&
                shutdownYear <= ntvReport.getEvenYear()) {
            rpt1Missing = false;
        }
        if(ntvReport.getReport2() == null) {
            rpt2Missing = true;
        } else {
            if(ntvReport.getReport2().getFeeId() == null) {
                rpt2Missing = true;
            }
        }
        if(rpt2Missing && shutdown != null &&
                shutdownYear <= ntvReport.getOddYear()) {
            rpt2Missing = false;
        }

        EmissionsReportDAO  emissionsReportDao = null;
        EmissionsRptInfo info = null;
        emissionsReportDao = emissionsReportDAO(getSchema(false));
        info = null;
        if (rpt1Missing) {
            // It is OK if that year is covered by a different report category
            info = emissionsReportDao.retrieveEmissionsRptInfo(facility.getFacilityId(),
                    ntvReport.getEvenYear());
            if(info == null) {
                createWF = true;
            }
        }

        if (!createWF && rpt2Missing) {
            // It is OK if that year is covered by a different report category
            info = emissionsReportDao.retrieveEmissionsRptInfo(facility.getFacilityId(),
                    ntvReport.getOddYear());
            if(info == null) {
                createWF = true;
            }
        }
        return createWF;
    }

    /**
     * THIS CODE FINDS DUPLICATE CONTACTS, THIS SHOULD NOT HAPPEN:
     *  only duplicate found so far is for 0333010116 caused when software bugs.
       SELECT DISTINCT fcxr.facility_id, cc.last_nm
       FROM stars2.fp_facility_contact_xref fcxr, stars2.cm_contact cc
       WHERE fcxr.contact_id = cc.contact_id
        AND
       (SELECT COUNT(cc2.contact_id) FROM stars2.cm_contact cc2, stars2.fp_facility_contact_xref fcxr2
        WHERE fcxr.facility_id = fcxr2.facility_id
        AND fcxr2.contact_id = cc2.contact_id
        AND ((cc.last_nm is null AND cc2.last_nm is null)
           OR (cc.last_nm is not null AND cc2.last_nm is not null
            AND cc.last_nm = cc2.last_nm))
        AND ((cc.first_nm is null AND cc2.first_nm is null)
           OR (cc.first_nm is not null AND cc2.first_nm is not null
            AND cc.first_nm = cc2.first_nm))
        AND ((cc.middle_nm is null AND cc2.middle_nm is null)
           OR (cc.middle_nm is not null AND cc2.middle_nm is not null
            AND cc.middle_nm = cc2.middle_nm))
        AND
          (cc.company_id is null AND cc2.company_id is null
           OR cc.company_id is not null AND cc2.company_id is not null
            AND cc.company_id = cc2.company_id)
            ) > 1
        ORDER BY fcxr.facility_id
     *
     * returns a String of zero length if no error otherwise it returns the error to display.
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    private String updateContact(Contact updatedCon, String conType,
            Timestamp contactRefDate, Timestamp receiveD, Timestamp endDate,
            int user, Facility facility, FacilityService facilityBO,
            FacilityDAO facilityDAO, InfrastructureDAO infraDAO, Transaction trans) 
    throws RemoteException {
        boolean tracing = false;
        Timestamp receiveDate = dropPartialDay(receiveD);
        // Otherwise, if no updated contact--nothing to do
        if(updatedCon != null) {
            if(tracing) {
                logger.warn("updateContact name:" + updatedCon.getName() +
                        ", conType=" + conType + ", contactRefDate=" + contactRefDate +
                        ", receiveDate=" +
                        receiveDate + ", endDate=" + endDate);
            }

            /* updatedCon is the new Contact for role conType.
             * Need to see if it is in the
             * database (use retrieveDupFacilityContact().
             * Need to see what Contact is active on date
             * contactRefDate.
             * 
             * Can update Contact/ContactType info only if
             * the role is currently active or there is a
             * contact active for the role.
             */
            Calendar cal = Calendar.getInstance();
            // Create or update the contact.
            Contact[] pCArray = facilityBO.retrieveDupFacilityContact(updatedCon, trans);
            Contact pC = null;
            if(pCArray != null && pCArray.length > 0) {
                pC = pCArray[0];
            }
            if(pCArray != null && pCArray.length > 1) {
                // RETURN
                return "  Contact " + pC.getName() + " is duplicated.";
            }
            updateInsert(updatedCon, pCArray, facility,
                    facilityBO, facilityDAO, trans);

            ContactUtil activeConU =
                facility.getActiveContact(conType, contactRefDate);
            if(activeConU != null) {
                /* Contact with that role did exist.
                 * If different Contacts, then need to end previous role
                 * and create new role for new Contact.
                 */
                cal.setTimeInMillis(activeConU.getContactType().getStartDate().getTime());
                cal.add(Calendar.DAY_OF_YEAR, 2);
                Timestamp newStart = dropPartialDay(new Timestamp(cal.getTimeInMillis()));
                // Use larger of what calculated and submit date
                if(receiveDate.after(newStart)) {
                    newStart = receiveDate;
                }

                cal.setTimeInMillis(newStart.getTime());
                cal.add(Calendar.DAY_OF_YEAR, -1);
                Timestamp newEnd = dropPartialDay(new Timestamp(cal.getTimeInMillis()));

                if(activeConU.getContactType().getEndDate() == null) {
                    // Is current role
                    if(tracing) {
                        logger.warn("activeConU.endDate is null");
                    }
                    boolean updatedSameName = false;
                    if(pC != null) {
                        updatedSameName = pC.getContactId().equals(
                                activeConU.getContact().getContactId());
                    }
                    if(updatedSameName) {
                        if(tracing) {
                            logger.warn("just update address info");
                        }
                        // Contact is the same, leave role as is.
                    } else {
                        // Now different contact, end current role and
                        // start the new role.
                        if(tracing) {
                            logger.warn("endDate=" + newEnd + ", startDate= " + newStart);
                            logger.warn("Before terminate role, ContactId:" +
                                    activeConU.getContactType().getContactId() +
                                    ", sd:" + activeConU.getContactType().getStartDate() +
                                    ", ed:" + activeConU.getContactType().getEndDate());
                        }
                        //InfrastructureHelper infraHelper = new InfrastructureHelper();
                        modifyContactType(infraHelper, activeConU.getContactType(),
                                newEnd, facility.getFpId(), user, trans);
                        infraDAO.addContactType(updatedCon.getContactId(),
                                conType, newStart);
                        if(tracing) {
                            logger.warn("addContactType: ContactId" + updatedCon.getContactId() + ", name:" + updatedCon.getName() + ", conType: " +
                                    conType + ", newStart:" + newStart);
                        }
                    } 
                } else {
                    /*  Can add the role only if not active for a different contact
                     */
                    if(facility.getActiveContact(conType, null) != null) {
                        String s = "  Cannot update "
                            + ContactTypeDef.getData().getItems().getItem(
                                    conType) + " Contact because "
                                    + activeConU.getContact().getName()
                                    + " has been ended.";
                        // RETURN
                        return s;
                    }
                    // no contact with this role, add role
                    newStart = facility.latestEndDate(conType);
                    if (newStart == null) {
                        newStart = receiveDate;
                        cal.setTimeInMillis(newStart.getTime());
                        cal.add(Calendar.DAY_OF_YEAR, 1);
                        newStart = dropPartialDay(new Timestamp(cal.getTimeInMillis()));
                    }
                    infraDAO.addContactType(updatedCon.getContactId(), conType,
                            newStart);
                    if(tracing) {
                        logger.warn("No Contact, ContactId:" +
                                updatedCon.getContactId() + ", name:" +
                                updatedCon.getName() + ", conType: " + conType +
                                ", newStart:" + newStart);
                    }
                }
            } else {
                // Contact did not exist previously, just add the role.
                Timestamp newStart = facility.latestEndDate(conType);
                if(newStart == null) newStart = receiveDate;
                cal.setTimeInMillis(newStart.getTime());
                cal.add(Calendar.DAY_OF_YEAR, 1);
                newStart = dropPartialDay(new Timestamp(cal.getTimeInMillis()));
                if(tracing) {
                    logger.warn("new Roll, ContactId:" + updatedCon.getContactId() + ", name:" + updatedCon.getName() + ", conType: " + conType + ", newStart:" + newStart);
                }
                infraDAO.addContactType(updatedCon.getContactId(),
                        conType, newStart);
            }

            // Refresh contact in memory with changes already made.
            facility.setAllContacts(
                    facilityDAO.retrieveFacilityContacts(
                            facility.getFacilityId()));
        }
        return "";
    }

    Timestamp dropPartialDay(Timestamp ts) {
        if(ts == null) {
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

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    private String updateConOldOwn(Contact updatedCon, Timestamp transferDate,
            Timestamp firstNotPaid, int user, Facility facility, FacilityService facilityBO,
            FacilityDAO facilityDAO, InfrastructureDAO infraDAO, Transaction trans) 
    throws RemoteException {
        boolean tracing = false; //true;
        Calendar cal = Calendar.getInstance();
        // Otherwise, if no updated contact--nothing to do
        // If firstNotPaid is null, should not have forwarding address
        if(tracing) {
            logger.warn("Entered updateConOldOwn: " + updatedCon + ", " + firstNotPaid);
        }
        if(updatedCon != null && firstNotPaid != null) {
            cal.setTimeInMillis(firstNotPaid.getTime());
            cal.add(Calendar.DAY_OF_YEAR, -1);
            Timestamp firstNotPaidMinusOne = dropPartialDay(new Timestamp(cal.getTimeInMillis()));
            if(tracing) {
                logger.warn("updateConOldOwn name:" + updatedCon.getName() +
                        ", transferDate=" + transferDate +
                        ", firstNotPaid=" + firstNotPaid);
            }
            /* Note that forwarding info provided by new owner.
             * updatedCon is the new Contact for role conType
             * for the old owner.
             * MUST be current owner at time of this report.
             * WILL be the Current Primary Contact unless
             * there is no Current Primary Contact.
             * Need to see if it is in the
             * database (use retrieveDupFacilityContact().
             * 
             * If cannot perform this then provide an error and
             * fail the approval. 
             * Validation will have confirmed that that Transfer Date is set.
             * Also, previous owner forwarding was not allowed if new owner
             * paid for both years.
             * Update fails at approve time if the old owner Primary Contact
             * started one day prior to transfer date--because then we cannot
             * insert the changed Primary Contact.
             * Validation will not allow Forwarding address if not current
             * report.
             * 
             *   Current primary must have been active before end date.
             *      If person the same, then end it on end date.
             *      If person is different, then end the current on
             *      the later of:
             *          one day before December 31 of first year not
             *          paid by former owner.
             *        or
             *          the day the current contact started (will be a
             *          one day contact).
             *      Provide error message if this is not possible.
             *      
             *      If there is no current primary, then add it.
             */

            // Create or update the contact.
            Contact[] pCArray = facilityBO.retrieveDupFacilityContact(updatedCon, trans);
            Contact pC = null;
            if(pCArray != null && pCArray.length > 0) {
                pC = pCArray[0];
            }
            if(pCArray != null && pCArray.length > 1) {
                // RETURN
                return "  Contact " + pC.getName() + " is duplicated.";
            }
            updateInsert(updatedCon, pCArray, facility,
                    facilityBO, facilityDAO, trans);

            cal = Calendar.getInstance();
            cal.setTimeInMillis(transferDate.getTime());
            cal.add(Calendar.DAY_OF_YEAR, -1);
            Timestamp endOwn = dropPartialDay(new Timestamp(cal.getTimeInMillis()));
            if(tracing) {
                logger.warn("prev owner name:" + updatedCon.getName() +
                        ", eO:" + endOwn);
            }
            // Get Contact contact.
           /* ContactUtil activeConU = facility.getActiveContact(ContactTypeDef.PRIM, null);
            // Add role where there was none.
            // end date is endOwn
            if(activeConU == null) {
                Timestamp newStart = facility.latestEndDate(ContactTypeDef.PRIM);
                if(newStart == null) newStart = firstNotPaidMinusOne;
                else {
                    cal.setTimeInMillis(newStart.getTime());
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                    newStart = dropPartialDay(new Timestamp(cal.getTimeInMillis()));
                }

                if(tracing) {
                    logger.warn("new Roll, ContactId:" + updatedCon.getContactId() +
                            ", name:" + updatedCon.getName() +
                            ", newStart:" + newStart);
                }
                if(!endOwn.after(newStart)) {
                    // RETURN
                    return "  Cannot make use of Previous Owner Forwarding Info because the existing Primary Contact ends one day before the Transfer Date.  Change that end date to be earlier or later.";
                }
                /*ContactType ct = new ContactType(updatedCon.getContactId(),
                        ContactTypeDef.PRIM, newStart, endOwn);
                infraDAO.addContactType(updatedCon.getContactId(), ct);*/
            /*} else { // conact was acitive on last day of ownership
                Timestamp conStart = dropPartialDay(activeConU.getContactType().getStartDate());
                if(!conStart.before(endOwn)) {
                    // Was a contact change on or after last day of ownership,
                    // Cannot make use of forwarding address.
                    return "  Cannot make use of Previous Owner Forwarding Info because the existing Primary Contact did not start before the last day of ownership, The begin date for primary Contact must be earlier.";
                }
                boolean updatedSameName = false;
                if(pC != null) {
                    updatedSameName = pC.getContactId().equals(
                            activeConU.getContact().getContactId());
                }
                InfrastructureHelper infraHelper = new InfrastructureHelper();
                if(updatedSameName) {
                    if(tracing) {
                        logger.warn("name is the same");
                    }
                    // Contact is the same, end role.
                    modifyContactType(infraHelper, activeConU.getContactType(),
                            endOwn, facility.getFpId(), user, trans);
                } else {
                    // Now different contact, end current role and
                    // start the new role.
                    Timestamp end1 = conStart;
                    if(end1.before(firstNotPaidMinusOne)) {
                        end1 = firstNotPaidMinusOne;
                    }
                    if(tracing) {
                        logger.warn("end1=" + end1 + ", firstNotPaidMinusOne= " + firstNotPaidMinusOne);
                        logger.warn("Before terminate role, ContactId:" +
                                activeConU.getContactType().getContactId() +
                                ", sd:" + conStart +
                                ", ed:" + activeConU.getContactType().getEndDate());
                    }
                    modifyContactType(infraHelper, activeConU.getContactType(),
                            end1, facility.getFpId(), user, trans);

                    cal.setTimeInMillis(end1.getTime());
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                    Timestamp begin = dropPartialDay(new Timestamp(cal.getTimeInMillis()));
                    ContactType ct = new ContactType(updatedCon.getContactId(),
                            ContactTypeDef.PRIM, begin, endOwn);
                    infraDAO.addContactType(updatedCon.getContactId(), ct);
                    if(tracing) {
                        logger.warn("addContactType: ContactId" + updatedCon.getContactId() + ", name:" + updatedCon.getName() +
                                ", begin:" + begin + ", endOwn:" + endOwn);
                    }
                } 
            }*/
            // Refresh contact in memory with changes already made.
            facility.setAllContacts(
                    facilityDAO.retrieveFacilityContacts(
                            facility.getFacilityId()));
        }
        return "";
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    private void updateInsert(Contact updatedCon, Contact[] pCArray, Facility facility,
            FacilityService facilityBO, FacilityDAO facilityDAO,
            Transaction trans) 
    throws RemoteException {
        /*  Either update the existing Contact
         * or
         * Insert the new Contact.
         * pC is the existing Contact in database (or null if does not exist).
         */
        // logger.debug("name=\"" + updatedCon.getName() + ", updateExists=" + (pC != null));
        //InfrastructureHelper infraHelper = new InfrastructureHelper();
        Contact pC = null;
        if(pCArray != null && pCArray.length > 0) {
            pC = pCArray[0];
        }
        if(pC != null) {
            // Perform update to Contact
            updatedCon.setContactId(pC.getContactId());
            updatedCon.setLastModified(pC.getLastModified());
            updatedCon.setAddressId(pC.getAddressId());
            updatedCon.getAddress().setAddressId(pC.getAddress().getAddressId());
            updatedCon.getAddress().setLastModified(pC.getAddress().getLastModified());
            infraHelper.modifyContactData(updatedCon, trans);
            // logger.debug("updateInsert.modifyContactData:" + updatedCon.getContactId() + ":" + updatedCon.getName() );
        } else {
            // Insert new contact
            updatedCon.setContactId(null); // Need new Id
            updatedCon.getAddress().setAddressId(null);
            updatedCon.setAddressId(null);
            updatedCon = infraHelper.createContact(updatedCon, trans);
            //facilityDAO.addFacilityContact(facility.getFacilityId(),
            //        updatedCon.getContactId());
            // logger.debug("updateInsert.addFacilityContact:" + updatedCon.getContactId() + ":" + updatedCon.getName() );            
        }
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Integer conflictingReportForApproval(String facilityId, EmissionsReport report)
    throws DAOException{
        EmissionsReportSearch[] reports = null;
        try {
            EmissionsReportSearch searchObj = new EmissionsReportSearch();
            searchObj.setYear(report.getReportYear());
            searchObj.setContentTypeCd(report.getContentTypeCd());
            searchObj.setFacilityId(facilityId);
            searchObj.setUnlimitedResults(true);
            reports = searchEmissionsReports(searchObj, false);
        } catch(RemoteException e) {
            logger.error("Failed on conflictingReportForApproval() for emissionsRptId "
                    + report.getEmissionsRptId(), e);
            throw new DAOException("Failed on conflictingReportForApproval() for emissionsRptId "
                    + report.getEmissionsRptId(), e);
        }
        Integer rtn = null;
        for(EmissionsReportSearch ers : reports) {
            if(0 == ers.getEmissionsRptId().compareTo(report.getEmissionsRptId())) {
                continue; // skip this report and all newer reports (higher id values)
            }
            // Other reports which are not revisions must be in state
            // "emissions Prior/Invalid" or one of the approved states.
            if(ers.getReportModified() == null &&
                    !(ReportOfEmissionsStateDef.isNotNeededCode(ers.getReportingState())
                            || ReportOfEmissionsStateDef.isApprovedCode(ers.getReportingState()))) {
                rtn = ers.getEmissionsRptId();
                break;
            }
        }
        return rtn;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public EmissionsReport approveReport(Facility facility, EmissionsReport report,
            String state, ArrayList<EmissionRow> emissions)
    throws DAOException {

        EmissionsReport ret = report;
        Invoice newInv = null;
        Transaction trans = null;
        try {
        	// Currently do not need to build invoice for EI approval
            //newInv = buildInvoice(facility, report, emissions);
            if(report.getBillingContactFailureMsg() != null) {
                // need to fail, note, no database operations performed.
                return ret; // report unchanged except for error message
            }
            trans = TransactionFactory.createTransaction();

            if(newInv != null) {
                // create the invoice;
                //InfrastructureHelper infraHelper = new InfrastructureHelper();
            	
                //newInv = infraHelper.createInvoice(newInv, trans); // create the invoice
            }
            // approve the report
            report.setRptReceivedStatusCd(state);
            Timestamp dt = new Timestamp(new java.util.Date().getTime());
            report.setRptApprovedStatusDate(dt);
            EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(trans);
            emissionsRptDAO.modifyEmissionsReport(report, null);  // update report.
            trans.complete();
        } catch (RemoteException de) {
            cancelTransaction(trans, de);  // does throw
        } finally {
            closeTransaction(trans);
        }
        return ret;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    public Invoice buildInvoice(Facility facility,
            EmissionsReport report, ArrayList<EmissionRow> emissions) 
    throws DAOException {
    	
        Invoice newInv = new Invoice();
        newInv.setContact(null);
        newInv.setEmissionsRptId(report.getEmissionsRptId());
        newInv.setInvoiceStateCd(InvoiceState.READY_TO_INVOICE);

        String revTypeCd = null;
        DefSelectItems revenueTypeItems = RevenueTypeDef.getData().getItems();
        for (SelectItem i : revenueTypeItems.getCurrentItems()) {
            RevenueTypeDef rt = (RevenueTypeDef)revenueTypeItems.getItem(i.getValue().toString());
            if(rt.getYear().equals(report.getReportYear())) {
                revTypeCd = rt.getCode();
                break;
            }
        }
        
        if(null == revTypeCd) {
            String s = "Revenue Type code not found for category " +
             " and year " +
            report.getReportYear() +
            " while attempting to approve emissions inventory " +
            report.getEmissionsInventoryId() + " tied to facility inventory with fpId " +
            report.getFpId();
            logger.error(s);
            throw new DAOException(s, s);
        }
        
        newInv.setRevenueTypeCd(revTypeCd);
        newInv.setCreationDate(new Timestamp(System.currentTimeMillis()));
        newInv.setFacilityId(facility.getFacilityId());
        List<ValidationMessage> vMsgs = FacilityValidation.determineMissingBilling(facility, report.getReportYear());
        if(vMsgs.size() > 0) {
            // Did not find billing contact that should go with the report
            // Is there a current billing contact?
            if(facility.getBillingContact() == null) {
                report.setBillingContactFailureMsg("There is no billing contact for this emissions inventory");
            }
        }

        // Note that totalEmissions does not use the tonnage cap
        double chargable = 0;
        if(report.getTotalEmissions() != null) {
            chargable = report.getTotalEmissions();
        }
            // Only produce detail lines if TV.
            chargable = 0d;
            double pollCap = 0;
            try {
            	ReportTemplates serviceCatalog = retrieveSCEmissionsReports(report.getReportYear(), report.getContentTypeCd(), facility.getFacilityId());
            	SCEmissionsReport scEmissionsCatalog = serviceCatalog.getSc();
            	pollCap = scEmissionsCatalog.getPollutantCap();
			} catch (RemoteException e1) {
				// error
				logger.error("Could not retrieve service catalog: " + e1.getMessage());
			}
            for (EmissionRow e : emissions) {
                if (e.isChargeable() && (null != e.getStackEmissions()
                        || e.getFugitiveEmissions() != null)) {
                    InvoiceDetail d = new InvoiceDetail();
                    d.setDescription(e.getPollutantCd());
                    double s = 0d;
                    if(e.getStackEmissions() != null) {
                        s = EmissionsReport.convertStringToNum(e.getStackEmissions(), logger);
                    }
                    double f = 0d;
                    if(e.getFugitiveEmissions() != null) {
                        f = EmissionsReport.convertStringToNum(e.getFugitiveEmissions(), logger);
                    }
                    double v = EmissionUnitReportingDef.convert(
                            e.getEmissionsUnitNumerator(), s + f,
                            EmissionUnitReportingDef.TONS);
                    // Work with 6 significant digits
                    String detailTons = EmissionsReport.numberToString(v);
                    double dTons = EmissionsReport.convertStringToNum(detailTons, logger);
                    chargable = chargable + (dTons < pollCap?dTons:pollCap);
                    d.setAmount((float)dTons);
                    if(dTons > pollCap) {
                        d.setAmount((float)pollCap);
                    }
                    newInv.addInvoiceDetail(d);
                }
            }
        
        ServiceCatalogDAO serviceCatalogDAO = serviceCatalogDAO();
        Integer reportFeeId = report.getFeeId();

        Fee fee = serviceCatalogDAO.retrieveFee(reportFeeId);
        if(fee == null) {
            String s = "Failed to locate fee information for FeeId = "
                + report.getFeeId() + " in emissions inventory " + report.getEmissionsRptId();
            throw new DAOException(s, s);
        }
        double price;
        if(null == fee.getLowRange()) {
            price = chargable * fee.getAmount();
        } else {
            price = fee.getAmount();
        }
        if(price == 0d) {
            // Don't generate invoice if zero price and first report.
            if(report.getReportModified() == null) {
                newInv = null;
            }
            try {
                boolean hasPrev = infraService.previousInvoiceExists(report);
                if(!hasPrev) {
                    // no previous invoice, don't create one now either.
                    return null;
                }
            } catch(RemoteException re) {
                String s = "Exception calling  previousInvoiceExists()";
                logger.error(s);
                throw new DAOException(s, s);
            }
        }
        newInv.setOrigAmount(price);
        return newInv;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    public Invoice buildInvoice(Facility facility, NtvReport ntvReport) 
    throws DAOException {
    	
        double price = 0d;
        Fee fee;
        Integer feeId;
        
        if(ntvReport.getReportV1() != null) {
            feeId = ntvReport.getReportV1().getFeeId();
            if(feeId != null) {
                fee = serviceCatalogDAO().retrieveFee(feeId);
                if(fee == null) {
                    throw new DAOException("Failed to find fee Id " +
                            ntvReport.getReportV1().getFeeId() + " for emissions inventory" +
                            ntvReport.getReportV1().getEmissionsInventoryId() +
                            " for facility " + facility.getFacilityId());
                }
                if(null != fee.getLowRange()) {
                    price += fee.getAmount();
                }
            }
        }
        
        if(ntvReport.getReportV2() != null) {
            feeId = ntvReport.getReportV2().getFeeId();
            if(feeId != null) {
                fee = serviceCatalogDAO().retrieveFee(feeId);
                if(fee == null) {
                    String s = "Failed to find fee Id " +
                    ntvReport.getReportV2().getFeeId() + " for emissions inventory" +
                    ntvReport.getReportV2().getEmissionsInventoryId() +
                    " for facility " + facility.getFacilityId();
                    throw new DAOException(s, s);
                }
                if(null != fee.getLowRange()) {
                    price += fee.getAmount();
                }
            }
        }

        if(price < .001d) {
            if(ntvReport.getPrimary().getReportModified() == null) {
                // first report--do not create invoice
                return null;
            }
            try {
                boolean hasPrev = infraService.previousInvoiceExists(ntvReport.getPrimary());
                if(!hasPrev) {
                    // no previous invoice, don't create one now either.
                    return null;
                }
            } catch(RemoteException re) {
                String s = "Exception calling  previousInvoiceExists()";
                logger.error(s);
                throw new DAOException(s, s);
            }
        }

        Invoice newInv = new Invoice();
        newInv.setOrigAmount(price);
        newInv.setEmissionsRptId(ntvReport.getPrimary().getEmissionsRptId());
        newInv.setInvoiceStateCd(InvoiceState.READY_TO_INVOICE);

        String revTypeCd = null;
        DefSelectItems revenueTypeItems = RevenueTypeDef.getData().getItems();
        for (SelectItem i : revenueTypeItems.getCurrentItems()) {
            RevenueTypeDef rt = (RevenueTypeDef)revenueTypeItems.getItem(i.getValue().toString());
            if(rt.getYear().equals(ntvReport.getOddYear())) {
                revTypeCd = rt.getCode();
                break;
            }
        }
        if(null == revTypeCd) {
            String s = "Revenue Type code not found for category " +
            " and year " +
            ntvReport.getPrimary().getReportYear() +
            " while attempting to approve emissions inventory " +
            ntvReport.getPrimary().getEmissionsInventoryId() + " for facility " +
            facility.getFacilityId();
            logger.error(s);
            throw new DAOException(s, s);
        }
        newInv.setRevenueTypeCd(revTypeCd);
        newInv.setCreationDate(new Timestamp(System.currentTimeMillis()));
        newInv.setFacilityId(facility.getFacilityId());

        // Set billing contact
        newInv.setContact(null);
        if(ntvReport.getPrimary().getBillingAddr() != null) {
            newInv.setContact(ntvReport.getPrimary().getBillingAddr());
        } else {
            Timestamp anchor = ntvReport.getContactRefDate();
            Timestamp contactRefDate = facility.latestOwnerRefDate(anchor);
            ContactUtil activeBilling = facility.getActiveContact(ContactTypeDef.BILL, contactRefDate);
            if(activeBilling != null) {
                // Make copy so a new one created.
                newInv.setContact(new Contact(activeBilling.getContact()));
            }
            setBillingContactError(ntvReport.getPrimary(), newInv, contactRefDate);
        }
        return newInv;
    }

    private void setBillingContactError(EmissionsReport report, Invoice inv, Timestamp ref) {
        String err = null;
        if(inv.getContact() == null) {
            if(ref == null) {
                err = "There is no active Billing Contact";
            } else {
                err = "There is no active Billing Contact up through date "
                    + ref + ", when ownership changed.";
            }
        }
        report.setBillingContactFailureMsg(err);
    }

    /*
     * Retrieve the report definitions needed.
     * 
     * Expected scenario for creating report templates for the next reporting year
     * 
     * Goal:  Create FER for 2010 reporting year.
     * Late in 2010, prior to enabling reporting for 2010, FER report templates are
     * defined for TV, SMTV and NTV.
     * 
     * OBSOLETE -----
     *   The EffectiveDate date specified in the templates is a date in the future at
     *   or close to when reporting will be enabled for 2010.  This enables  revising
     *   this template rather than creating a modified copy of it up until facilities
     *   will be using it.  If changes are made subsequently, a modified copy of the
     *   report template would be created.  If you did not wish to keep the report
     *   template defined when reporting then a date for EffectiveDate farther in the
     *   future could be chosen.
     * OBSOLETE --------
     * 
     * To actually test the template then enable reporting for 2010 for some test facility.
     * Create test emissions inventories for this test facility to make use of these new
     * report templates.  These test reports must be 'submitted' and 'approved' to
     * test the use of the fee structure defined in the FER template.  Simply creating
     * a report will test the pollutant provisioning portion of the report
     * template-pollutant ordering and whether the pollutant is attached to fees.
     * 
     * What report template is used:
     * Rows marked with XXX are OBSOLETE for IMPACT.
     * XXX TV type A facilities use TV FER and TV ES report templates.
     * XXX TV type A facilities use their own EIS template.
     * XXX NTV facilities never use an EIS report template.
     * The specific template used for reporting year X is the one defined for year X
     * XXX if no report is defined for year X, the report defined for the latest year
     * XXX less than or X.  If more than one report is defined for that year, then the one
     * XXX with the latest EffectiveDate is used-even if the EffectiveDate is still in the future.
     * 
     * XXX ES and EIS reports are similar except since there is no fee structure,
     * XXX those reports need not be 'submitted'.
     */
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
@Deprecated
    public ReportTemplates retrieveSCEmissionsReports(
            Integer yr, String contentTypeCd, boolean isTvA)
    throws RemoteException  {
	
        isTvA = false;  // IGNORE TYPE A specification.
        boolean failed = false;
        ReportTemplates templates = new ReportTemplates();
        templates.setNull();

                SCEmissionsReport[] scRpts = infraService.retrieveSCEmissionsReports();
                SCEmissionsReport locatedSc =
                    locateActiveRpt(yr, contentTypeCd, scRpts);
                if(null == locatedSc) {
                    templates.addDisplayMsg("No" + 
                            " emissions inventory template found for year " + yr + 
                            " and content type " + contentTypeCd + ".");
                    failed = true;
                }
                templates.setSc(locatedSc);
        templates.setFailed(failed);
        return templates;
    }
    
	public ReportTemplates retrieveSCEmissionsReports(Integer yr,
			String contentTypeCd, String facilityId) throws RemoteException {

		boolean failed = false;
		ReportTemplates templates = new ReportTemplates();

		templates.setNull();
		try {
			SCEmissionsReport locatedSc = locateHighestPriorityRpt(yr,
					contentTypeCd, facilityId);
			if (null == locatedSc) {
				templates.addDisplayMsg("No"
						+ " emissions inventory template found for facility "
						+ facilityId + " and year " + yr
						+ " and content type " + contentTypeCd + ".");
				failed = true;
			}
			templates.setSc(locatedSc);
		} catch (DAOException e) {
			logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		}
		templates.setFailed(failed);
		return templates;
	}

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
     /**
     * Used to identify the active Service Catalog
     * @deprecated No longer used within IMPACT
	 * as it retrieves the highest priority SC for
     * for given year and content type forentire system.
     * IMPACT now determines which SC to use on a
     * facility basis, since each facility may be
     * configured differently...the highest priority
     * SC for one facility may be different than the
     * highest priority SC for another facility. The
     * new method locateHighestPriorityRpt(...) should
     * be used instead.
     * @return
     */
@Deprecated
    private SCEmissionsReport locateActiveRpt(Integer yr, String contentTypeCd,
            SCEmissionsReport[] scRpts) throws RemoteException {
	
        long latestDate = 0;
        boolean foundCorrectYear = false;
        Integer yearFound = 0;
        Integer scReportId = null;
        
        Integer priority = 0;
        Integer highestPriority = new Integer(
				Integer.MAX_VALUE);
        
        for (SCEmissionsReport s : scRpts) {
            if (null == s.getContentTypeCd() || !contentTypeCd.equals(s.getContentTypeCd())) continue;
            // Is the report useless
            if(null == s.getReportingYear()
            //        || null == s.getEffectiveDate()
                    ) {
                logger.error("Report template " + s.getId() +
                //" has ReportingYear or EffectiveDate null--template ignored");
                " has ReportingYear null--template ignored");
                continue;
            }
            //Is report definition too new?
            if (0 < s.getReportingYear().compareTo(yr)) continue;
            // Have we found a better year?
            if (!foundCorrectYear && 0 <= s.getReportingYear().compareTo(yearFound)){
                yearFound = s.getReportingYear();
                latestDate = 0;
                if (s.getReportingYear().equals(yr)) {
                    foundCorrectYear = true;
                }
            }
            // Can we now forget about other years?
            if (foundCorrectYear &&  !s.getReportingYear().equals(yr)) continue;
            // Find most recent one.
            
            if (foundCorrectYear && s.getContentTypeCd().equals(contentTypeCd)) {
	            //if (latestDate < s.getEffectiveDate().getTime()) {
	            //    latestDate = s.getEffectiveDate().getTime();
    			priority = RegulatoryRequirementTypeDef.getPriority(s.getRegulatoryRequirementCd());
    			if (priority == null || priority.intValue() == 0) continue; // skip this SC if associated reg req priority is null or zero
            	if (priority.intValue() <= highestPriority.intValue()) {
            		highestPriority = priority;
	                scReportId = s.getId();
            	}
	            //}
            }
        }

            if (null != scReportId) {
                return infraService.retrieveSCEmissionsReport(scReportId);
            }

            return null;
    }
    
    private SCEmissionsReport locateHighestPriorityRpt(Integer yr, String contentTypeCd,
            String facilityId) throws RemoteException {
       
        Integer scReportId = null;
        
         	scReportId = infraService.retrieveHighestPriorityRptReportId(yr,contentTypeCd,facilityId);
            if (null != scReportId) {
                return infraService.retrieveSCEmissionsReport(scReportId);
            }
 
            return null;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Boolean missingReport(String facilityId, int year, Integer rptId)
    throws DAOException {
        EmissionsReportSearch searchObj = new EmissionsReportSearch();
        Boolean rtn = new Boolean(true);
        // have these reports been submitted (at least in DAPC/readonly database)?
        searchObj = new EmissionsReportSearch();
        searchObj.setStagingDBQuery(false);
        searchObj.setFacilityId(facilityId);
        EmissionsReportSearch[] reports
        = searchEmissionsReports(searchObj, false);
        for(EmissionsReportSearch ers : reports) {
            if(!ers.getEmissionsRptId().equals(rptId) && year == (ers.getYear().intValue())) {
                rtn = null;
                break;
            }
        }
        return rtn;
    }


    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public boolean onlyOlderReports(Facility fac, NtvReport ntvReport)
    throws DAOException {
        boolean rtn = true;
        checkNull(fac);
        checkNull(ntvReport);
        // What is highest year in this report
        Integer maxYear = ntvReport.getPrimary().getReportYear();
        Integer rptId = ntvReport.getPrimary().getEmissionsRptId();
        if(ntvReport.getReport2() != null) {
            maxYear = ntvReport.getReport2().getReportYear();
            rptId = ntvReport.getReport2().getEmissionsRptId();
        }
        checkNull(rptId);
        checkNull(maxYear);
        String facilityId = fac.getFacilityId();
        EmissionsReportSearch searchObj = new EmissionsReportSearch();
        searchObj.setStagingDBQuery(true);
        searchObj.setFacilityId(facilityId);
        EmissionsReportSearch[] reports
        = searchEmissionsReports(searchObj, true);

//      Any newer reports (staging or internal schema)?
        for(EmissionsReportSearch ers : reports) {
            // Any other newer reports or reports for same year not completed
            if(ers.getYear().compareTo(maxYear) > 0) {
                rtn = false;
                break;
            }
            if(ers.getYear().compareTo(maxYear) == 0 &&
                    !ers.getEmissionsRptId().equals(rptId) &&
                    !ReportStatusDef.reportCompleted(ers.getReportingState())) {
                rtn = false;
                break;
            }
        }
        // Any newer reports (readonly schema)?
        if(rtn && isPortalApp()) {
            searchObj = new EmissionsReportSearch();
            searchObj.setStagingDBQuery(false);
            searchObj.setFacilityId(facilityId);
            reports = searchEmissionsReports(searchObj, false);
            for(EmissionsReportSearch ers : reports) {
                if(ers.getYear().compareTo(maxYear) > 0) {
                    rtn = false;
                    break;
                }
                if(ers.getYear().compareTo(maxYear) == 0 &&
                        !ers.getEmissionsRptId().equals(rptId) &&
                        !ReportStatusDef.reportCompleted(ers.getReportingState())) {
                    rtn = false;
                    break;
                } 
            }
        }
//      reportSearch.setSearchObj(saveRS);
        return rtn;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public boolean noLaterTransferDates(Facility fac, NtvReport ntvReport) {
        // After all known Transfer Dates?
        boolean rtn = true;
        Timestamp transfer = dropPartialDay(ntvReport.getPrimary().getTransferDate());
        List<SelectItem> pickListTransfers = fac.ownershipChange(ntvReport.getLeftPoint());
        if(transfer != null) {
            for(SelectItem si : pickListTransfers) {
                if(transfer.before((Timestamp)si.getValue())) {
                    rtn = false;
                    break;
                }
            }
        }
        return rtn;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public EmissionsReport generateComparisonReport(EmissionsReport report,
            EmissionsReport compareReport) {
        /*
         * This comparison report has report, group, EU and period objects
         * interconnected. However, they are all empty except for Ids and their
         * orig and comp references being set to the corresponding objects in
         * the real reports.
         */
        EmissionsReport cReport = new EmissionsReport();
        cReport.setEmissionsRptId(report.getEmissionsRptId());
        cReport.setEmissionsInventoryId(report.getEmissionsInventoryId());
        cReport.setOrig(report);
        cReport.setComp(compareReport);
        HashMap<String, Object> orig = new HashMap<String, Object>();
        for (EmissionsReportEUGroup g : report.getEuGroups()) {
            orig.put(g.getReportEuGroupName(), g);
        }
        HashMap<String, Object> comp = new HashMap<String, Object>();
        for (EmissionsReportEUGroup g : compareReport.getEuGroups()) {
            comp.put(g.getReportEuGroupName(), g);
        }
        TreeSet<ComparePair> ts = ComparePair.compareObjects(orig, comp);
        // process through all the group pairings
        for (ComparePair cp : ts) {
            EmissionsReportEUGroup gOrig = (EmissionsReportEUGroup) cp
            .getOrig();
            EmissionsReportEUGroup gComp = (EmissionsReportEUGroup) cp
            .getComp();
            EmissionsReportEUGroup g = new EmissionsReportEUGroup();

            if (null == gOrig || null == gComp) {
                g.setCaution(true);
                g.setOrig(gOrig);
                g.setComp(gComp);
                if(gOrig != null) {
                    g.setReportEuGroupName(gOrig.getReportEuGroupName());
                }
                if(gComp != null) {
                    g.setReportEuGroupName(gComp.getReportEuGroupName());
                }
                cReport.addEuGroup(g);
                EmissionsReportPeriod p = new EmissionsReportPeriod();

                if (null == gOrig) {
                    g.setReportEuGroupName(gComp.getReportEuGroupName());
                    p.setTreeLabel(gComp.getPeriod().getTreeLabel());
                    p.setEmissionPeriodId(gComp.getPeriod().getEmissionPeriodId());
                    p.setComp(gComp.getPeriod());
                    p.setSccCode(gComp.getPeriod().getSccCode());
                } else {
                    g.setReportEuGroupName(gOrig.getReportEuGroupName());
                    p.setTreeLabel(gOrig.getPeriod().getTreeLabel());
                    p.setEmissionPeriodId(gOrig.getPeriod().getEmissionPeriodId());
                    p.setOrig(gOrig.getPeriod());
                    p.setSccCode(gOrig.getPeriod().getSccCode());
                }
                g.setPeriod(p);
            } else { // Same group name
                if(!SccCode.comparIdValues(gOrig.getPeriod().getSccCode(),
                        gOrig.getPeriod().getSccCode())) g.setCaution(true);
                if(!gOrig.sameEus(gComp.getEus()))g.setCaution(true);
                g.setOrig(gOrig);
                g.setComp(gComp);
                g.setReportEuGroupName(gOrig.getReportEuGroupName());
                cReport.addEuGroup(g);
                g.setReportEuGroupName(gOrig.getReportEuGroupName());
                EmissionsReportPeriod p = new EmissionsReportPeriod();
                p.setTreeLabel(gOrig.getPeriod().getTreeLabel());
                p.setEmissionPeriodId(gOrig.getPeriod().getEmissionPeriodId());
                p.setOrig(gOrig.getPeriod());
                p.setComp(gComp.getPeriod());
                p.setSccCode(gOrig.getPeriod().getSccCode());
                g.setPeriod(p);
            }
        }

        // process through the EUs
        orig = new HashMap<String, Object>();
        for (EmissionsReportEU euR : report.getEus()) {
            orig.put(euR.getCorrEpaEmuId().toString(), euR);
        }
        comp = new HashMap<String, Object>();
        for (EmissionsReportEU euR : compareReport.getEus()) {
            comp.put(euR.getCorrEpaEmuId().toString(), euR);
        }
        ts = ComparePair.compareObjects(orig, comp);
        // process through all the eu pairings
        for (ComparePair cp : ts) {
            EmissionsReportEU euOrig = (EmissionsReportEU) cp.getOrig();
            EmissionsReportEU euComp = (EmissionsReportEU) cp.getComp();
            EmissionsReportEU euR = new EmissionsReportEU();
            if (null == euOrig) { // create eu and all periods for comp
                euR.setComp(euComp);
                euR.setCaution(true);
                euR.setEpaEmuId(euComp.getEpaEmuId());
                for (EmissionsReportPeriod oP : euComp.getPeriods()) {
                    EmissionsReportPeriod p = new EmissionsReportPeriod();
                    p.setCaution(true);
                    p.setComp(oP);
                    p.setSccCode(oP.getSccCode());
                    p.setTreeLabel(oP.getTreeLabel());
                    p.setEmissionPeriodId(oP.getEmissionPeriodId());
                    euR.addPeriod(p);
                }
            } else if (null == euComp) { 
                // create eu and all periods for orig
                euR.setOrig(euOrig);
                euR.setCaution(true);
                euR.setEpaEmuId(euOrig.getEpaEmuId());
                for (EmissionsReportPeriod cP : euOrig.getPeriods()) {
                    EmissionsReportPeriod p = new EmissionsReportPeriod();
                    p.setCaution(true);
                    p.setOrig(cP);
                    p.setSccCode(cP.getSccCode());
                    p.setTreeLabel(cP.getTreeLabel());
                    p.setEmissionPeriodId(cP.getEmissionPeriodId());
                    euR.addPeriod(p);
                }
            } else { // Both reports have the same EU
                euR.setOrig(euOrig);
                euR.setComp(euComp);
                if(!euOrig.getEpaEmuId().equals(euComp.getEpaEmuId())) {
                    euR.setEpaEmuId(euOrig.getEpaEmuId() + ":" + euComp.getEpaEmuId());
                } else euR.setEpaEmuId(euOrig.getEpaEmuId());
                // Compare the Periods -- based upon process name.
                HashMap<String, Object> pOrig = new HashMap<String, Object>();
                for (EmissionsReportPeriod p : euOrig.getPeriods()) {
                    pOrig.put(p.getTreeLabel(), p);
                }
                HashMap<String, Object> pComp = new HashMap<String, Object>();
                for (EmissionsReportPeriod p : euComp.getPeriods()) {
                    pComp.put(p.getTreeLabel(), p);
                }
                TreeSet<ComparePair> pTs = ComparePair.compareObjects(pOrig,
                        pComp);
                // process through all the period pairings
                for (ComparePair pCp : pTs) {
                    EmissionsReportPeriod oP = (EmissionsReportPeriod) pCp
                    .getOrig();
                    EmissionsReportPeriod cP = (EmissionsReportPeriod) pCp
                    .getComp();
                    EmissionsReportPeriod p = new EmissionsReportPeriod();
                    if (null == oP) {
                        p.setSccCode(cP.getSccCode());
                        p.setComp(cP);
                        p.setTreeLabel(cP.getTreeLabel());
                        p.setEmissionPeriodId(cP.getEmissionPeriodId());
                        p.setCaution(true);
                    } else if (null == cP) {
                        p.setSccCode(oP.getSccCode());
                        p.setOrig(oP);
                        p.setTreeLabel(oP.getTreeLabel());
                        p.setEmissionPeriodId(oP.getEmissionPeriodId());
                        p.setCaution(true);
                    } else { // both have same SCC code.
                        p.setSccCode(cP.getSccCode());
                        p.setComp(cP);
                        p.setTreeLabel(cP.getTreeLabel());
                        p.setEmissionPeriodId(cP.getEmissionPeriodId());
                        p.setOrig(oP);
                    }
                    euR.addPeriod(p);
                }
            }
            cReport.addEu(euR);
        }
        return cReport;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void performTask(Task newTask, Transaction trans) throws RemoteException {
        Facility facility = newTask.getFacility();
        EmissionsReport report = newTask.getReport();
        NtvReport ntvReport = newTask.getNtvReport();
        if(report != null) {
            String county = facility.getPhyAddr().getCountyCd();
            createEmissionsReport(county, facility, report, false, trans);
            newTask.setReport(report);
            newTask.setTaskDescription(
                    " Emissions Inventory for " + report.getReportYear() + " (" + report.getEmissionsInventoryId() + ")");
            newTask.setTaskInternalId(report.getEmissionsRptId());
        } else if(ntvReport != null) {
            // Note, the following create also retrieves the ntv report
            ntvReport = createEmissionsReport(facility, ntvReport, false, trans);
            newTask.setNtvReport(ntvReport);
            newTask.setTaskDescription(" Emissions Inventory for " + ntvReport.getYears() + " (" + ntvReport.getPrimary().getEmissionsInventoryId() + ")");
            newTask.setTaskInternalId(ntvReport.getPrimary().getEmissionsRptId());
        }
        newTask.setFacility(facility);
        newTask.setReferenceCount(0);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required" 
     */
    private void copyReportAttachments(EmissionsReport oldRpt,
            EmissionsReport newRpt, boolean staging, Transaction trans) throws DAOException {  
    	
        List<EmissionsDocumentRef> newAttachments = new ArrayList<EmissionsDocumentRef>();

        for (EmissionsDocumentRef doc : oldRpt.getAttachments()) {

        	EmissionsDocumentRef newDocRef = new EmissionsDocumentRef(doc);

        	if (doc.getPublicDoc() != null) {
  
        		EmissionsDocument publicDocCopy = new EmissionsDocument(doc.getPublicDoc());
                if (publicDocCopy.getFacilityID() == null) {
                	publicDocCopy.setFacilityID(newRpt.getFacilityId());
                }
                if (publicDocCopy.getEmissionRptId() == null) {
                	publicDocCopy.setEmissionsRptId(newRpt.getEmissionsRptId());
                	publicDocCopy.setBasePath(null);
                }
                if(!staging){
                     copyDocument(doc.getPublicDoc(), publicDocCopy, staging, trans);
                } else {
                    logger.debug(" Copying document with path = " + doc.getPublicDoc().getPath());
                    try {
                        copyDocument(doc.getPublicDoc(), publicDocCopy, staging, trans);
                    } catch (DAOException e) {
                         if (e.getMessage().contains("unique constraint")) {
                            logger.error("Ignoring unique constraint error while copying document: " + 
                                    doc.getPublicDoc().getPath());
                        } else {
                            throw e;
                        }
                    }
                }
                newDocRef.setDocumentId(publicDocCopy.getDocumentID());
            }           

            if (doc.getTradeSecretDoc() != null) {
                EmissionsDocument tsDocCopy = new EmissionsDocument(doc.getTradeSecretDoc());
                if (tsDocCopy.getFacilityID() == null) {
                	tsDocCopy.setFacilityID(newRpt.getFacilityId());
                }
                if (tsDocCopy.getEmissionRptId() == null) {
                	tsDocCopy.setEmissionsRptId(newRpt.getEmissionsRptId());
                	tsDocCopy.setBasePath(null);
                }
               if(!staging){
                    tsDocCopy.setEmissionsRptId(newRpt.getEmissionsRptId());
                    copyDocument(doc.getTradeSecretDoc(), tsDocCopy, staging, trans);
                } else {
                    copyDocument(doc.getTradeSecretDoc(), doc.getTradeSecretDoc(), staging, trans);
                }
                newDocRef.setTradeSecretDocId(tsDocCopy.getDocumentID());
            }
            newDocRef.setEmissionsDocId(null);
            newDocRef.setEmissionsRptId(newRpt.getEmissionsRptId());

            // create record in rp_emissions_document table
            EmissionsReportDAO emissionsReportDAO = emissionsReportDAO(trans);
            emissionsReportDAO.createEmissionsDocument(newDocRef);
            newAttachments.add(newDocRef);
        }
        newRpt.setAttachments(newAttachments);
    }

    private void copyDocument(Document fromDoc, Document toDoc,
            boolean staging, Transaction trans) throws DAOException {
        DocumentDAO docDAO = documentDAO(trans);
        // first copy the actual file for this document
        try {
            if(!staging) {
                toDoc.setDocumentID(null);
            }
            docDAO.createDocument(toDoc);

            if(!staging){
                DocumentUtil.copyDocument(fromDoc.getPath(), toDoc.getPath());
            }
        } catch (IOException e) {
            throw new DAOException("Exception copying document from "
                    + fromDoc.getPath() + " to " + toDoc.getPath(), e);
        }
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void generateNewReport(Task newTask, Task facTask, EmissionsReport oldRpt) throws RemoteException {

    	// Generate new report based upon current facility inventory.
            if (null == facTask) {
                // From AQD side
                Transaction trans = null;
                trans = TransactionFactory.createTransaction();
                performTask(newTask, trans);         
                trans.complete();
            } else {
                // From Portal side
                infraService.createTask(newTask, facTask);
            }
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void generateNewNtvReport(Task newTask) throws RemoteException {

    	// Generate new report based upon current facility inventory.
            if (isInternalApp()) {
                // From AQD side
                Transaction trans = null;
                trans = TransactionFactory.createTransaction();
                performTask(newTask, trans);
                trans.complete();
            } else if (isPortalApp()) {
                // From Portal side
                infraService.createTask(newTask);
            }
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     *
     * @return true if county is in compliance for year.
     */
    public boolean retrieveCompliance(Integer year, String countyCd)
    throws DAOException {
        return emissionsReportDAO().retrieveCompliance(year, countyCd);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     *
     * @return true if county is in compliance for year.
     */
    public void setEsEis(EmissionsReport report, Facility facility, Integer year) {
        //report.setRptEIS(false);
        //if(report.getEmissionsRptCd().equals(EmissionReportsDef.TV)) {
            //report.setRptEIS(true);
            //report.setEisStatusCd(ReportEisStatusDef.NOT_FILED);
        //} else {
        //    report.setEisStatusCd(ReportEisStatusDef.NONE);
        //}
        boolean attain = false;
        if(facility != null && facility.getPhyAddr() != null && 
                facility.getPhyAddr().getCountyCd() != null) {
            String countyCd = facility.getPhyAddr().getCountyCd();
            try {
                attain = emissionsReportDAO().retrieveCompliance(year, countyCd);
            }catch(RemoteException re) {
                logger.error("Failed to retrieveCompliance() for facility " +
                        facility.getFacilityId() + " and year " + year);
            }
        }
        //report.setRptES(!attain);
        return;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public boolean reportable(EmissionUnit e, int year, String contentTypeCd) {
        /*
         *  What routine does:
         *  If operating status not set -->include EU (validation will complain)
         *  If operating status set to not installed and CommenceOperation date set -->include EU (validation will complain)
         *  If operating status set to not installed or invalid -->exclude EU
         *  If shutdown with no shutdown date --> inlcude EU (validation will complain)
         *  If shutdown with shutdown date before reporting year -->excude EU
         *  
         *  From here on, consider it operating:
         *  No need to check CommenceOperation date because if that is too
         *  late then we will use initial installation complete anyway.
         *  Also, if initial installation date indicates to include the EU,
         *  then validation will ensure that CommenceOperation date is set.
         *  So, only check:
         *  If initial installation date is missing -->include EU
         *  If initial installation date is after the reporting year -->exclude EU
         *  otherwise if initial installation date is in or before reporting year -->include EU
         */
        // Determine if emission unit should be in emission report
        if(e.getOperatingStatusCd() == null) {
            return true;  // operating status must be set.
        }
        if(e.getOperatingStatusCd().equals(EuOperatingStatusDef.NI) &&
                null != e.getEuStartupDate()) {
            return true;
        }
        if(e.getOperatingStatusCd().equals(EuOperatingStatusDef.NI)
                || e.getOperatingStatusCd().equals(EuOperatingStatusDef.IV)) {
            return false;
        }
        // Is either operating or it is shutdown

        // first check shutdown & shutdown date
        if (e.getOperatingStatusCd().equals(EuOperatingStatusDef.SD)
                && null == e.getEuShutdownDate()) {
            return true;
        }
        Calendar c = null;
        c = Calendar.getInstance();
        if (e.getOperatingStatusCd().equals(EuOperatingStatusDef.SD)
                && null != e.getEuShutdownDate()) {
            // If shutdown with a date, then use that date to see if excluded.
            long euShutdownDate = e.getEuShutdownDate().getTime();
            long contentTypeStartDate = getContentTypeStartDateLong(year,contentTypeCd);
            if(euShutdownDate < contentTypeStartDate) {
                return false;
            }
        }
        // From here on consider it operating
        if(e.getEuInitInstallDate() == null) {
            return true;
        }
        
        long euInitInstallDate = e.getEuInitInstallDateLong();
        long contentTypeEndDate = getContentTypeEndDateLong(year,contentTypeCd);
        if(euInitInstallDate <= contentTypeEndDate) {
            return true;
        } else {
            return false;
        }
    }
    
    public long getContentTypeStartDateLong(int year, String contentTypeCd) {

		//long ret;

		ContentTypeDef c = ContentTypeDef.getContentTypeDef(contentTypeCd);

		int currentYear = year;

		// End Date for Content Type
		Calendar startDateCalendar = Calendar.getInstance();
		startDateCalendar.set(currentYear, c.getStartMonth() - 1, c.getStartDay());
		java.util.Date startDateDate = Utility.formatBeginOfDay(startDateCalendar
				.getTime());
		long startDateLong = startDateDate.getTime();

		return startDateLong;
	}
    
	public long getContentTypeEndDateLong(int year, String contentTypeCd) {

		//long ret;

		ContentTypeDef c = ContentTypeDef.getContentTypeDef(contentTypeCd);

		int currentYear = year;

		// End Date for Content Type
		Calendar endDateCalendar = Calendar.getInstance();
		endDateCalendar.set(currentYear, c.getEndMonth() - 1, c.getEndDay());
		java.util.Date endDateDate = Utility.formatEndOfDay(endDateCalendar
				.getTime());
		long endDateLong = endDateDate.getTime();

		return endDateLong;
	}

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public void locatePeriodNames(Facility facility, EmissionsReport report) {
        /*
         * If after reportFacilityMatch() is called, then call this routine.
         * 
         * This routine locates the Process names associated with each Process
         * to be displayed as the Period names--unless it is a group period. If
         * the Process does not have a name, then use its SCC code as the name.
         * 
         * Note that when an EU is removed from a group, it will again need the
         * Process name for its Period name.
         * 
         * Groups do not have a period name.
         */
        for (EmissionsReportEU rEu : report.getEus()) {
            EmissionUnit e = facility.getMatchingEmissionUnit(rEu.getCorrEpaEmuId());
            if(null != e) {
                rEu.setEpaEmuId(e.getEpaEmuId());
                rEu.setPhase1Boiler(facility.getFacilityId(), report.getReportYear());
                rEu.setDapcDescription(e.getEuDesc());
                rEu.setCompanyId(e.getCompanyId());
                rEu.setExemptStatusCd(e.getExemptStatusCd());
                rEu.setTvClassCd(e.getTvClassCd());
                rEu.setRegulatedUserDsc(e.getRegulatedUserDsc());
                rEu.setEmuId(e.getEmuId());  // needed for facility validation
                for (EmissionsReportPeriod p : rEu.getPeriods()) {
                    setPeriodLabel(e, p, report.getReportYear());
                }
            }else {  // not found in facility
                rEu.setEpaEmuId(unknownEU + rEu.getCorrEpaEmuId()); 
                if (null != rEu.getPeriods() && rEu.getPeriods().size() > 0) { // Individual EU has periods
                    rEu.setNotInFacility(true);
                }
                for (EmissionsReportPeriod p : rEu.getPeriods()) {
                    setPeriodLabel(e, p, report.getReportYear());
                }
            }
        }
        // Construct period names for group periods.
        for (EmissionsReportEUGroup g : report.getEuGroups()) {
            EmissionsReportPeriod p = g.getPeriod();
            p.setTreeLabel("SCC: " + EmissionsReportPeriod.convertSCC(p));
        }

        // put EUs in alphabetic order
        ArrayList<EmissionsReportEU> orderedList = 
            new ArrayList<EmissionsReportEU>(report.getEus().size());
        for(EmissionsReportEU eu : report.getEus()) {
            int i;
            boolean insertBefore = false;
            for(i=0; i<orderedList.size(); i++) {
                if(eu.isNotInFacility() || eu.getEpaEmuId().length() > 6) {
                    // if not in the profile, list first.
                    // length will not exceed 6 unless it is name "unknown xx"
                    insertBefore = true;
                    break;
                }
                if(orderedList.get(i).getEpaEmuId().length() > 6) {
                    // skip over the unknown ones.
                    continue;
                }
                if(eu.getEpaEmuId().compareToIgnoreCase(orderedList.get(i).getEpaEmuId()) < 0) {
                    // EU to add is less than one being compared to
                    insertBefore = true;
                    break;
                }
            }
            if(insertBefore) {
                orderedList.add(i, eu);
            } else {
                orderedList.add(eu);
            }
        }
        report.setEus(orderedList);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public void explodeGroupsLocatePeriodNames(Facility facility, EmissionsReport report) {
        for(EmissionsReportEUGroup eug : report.getEuGroups()) {
            EmissionsReportPeriod ep = eug.getPeriod();
            if(ep != null && eug.getEus().size() > 1) {
                int size = eug.getEus().size();
                // Reduce period values by 1/N
                if(ep.getCurrentMaus() != null &&
                        ep.getCurrentMaus().getThroughput() != null) {
                    double d = EmissionsReport.convertStringToNum(
                            ep.getCurrentMaus().getThroughput())/size;
                    ep.getCurrentMaus().setThroughput(BaseDB.numberToString(d));
                }
                if(ep.getEmissions() != null) {
                    for(Emissions e : ep.getEmissions().values()) {
                        if(e.getFugitiveEmissions() != null) {
                            double d = EmissionsReport.convertStringToNum(
                                    e.getFugitiveEmissions())/size;
                            e.setFugitiveEmissions(BaseDB.numberToString(d));
                        }
                        if(e.getStackEmissions() != null) {
                            double d = EmissionsReport.convertStringToNum(
                                    e.getStackEmissions())/size;
                            e.setStackEmissions(BaseDB.numberToString(d));
                        }
                    }
                }
            }
            for(Integer euCorr : eug.getEus()) {
                EmissionsReportEU eu = report.getEu(euCorr);
                if(eu == null) {
                    logger.error("Unable to find EmissionsReportEU with correlation id="
                            + euCorr + " from group=" + eug.getReportEuGroupName()
                            + " in emissions inventory " + report.getEmissionsRptId());
                } else {
                    // Add period to the EU
                    // shallow copy needed to have place for process name.
                    EmissionsReportPeriod newP = EmissionsReportPeriod.shallowCopy(ep);
                    eu.addPeriod(newP);
                }
            }
        }
        report.setEuGroups(null);
        locatePeriodNames(facility, report);
    }

    private void setPeriodLabel(EmissionUnit e, EmissionsReportPeriod p, int year) {
        String scc;
        if (null == p.getSccId()
                || p.getSccId().length() < 8) {
            scc = null;
            p.setCaution(true);
        } else {
            scc = p.getSccId();
            p.getSccCode().determineDeprecated(year);
            if(p.getSccCode().isDeprecated()) {
                p.setCaution(true);
            } else {
                Integer cnt = materialForScc(p.getSccId(), year);
                if(cnt != null && cnt.intValue() == 0) {
                    //  should not be null, if it is ignore it.
                    p.setCaution(true);
                }
            }
        }
        EmissionProcess eProcess = null;
        if(null != e) {
            eProcess = e.findProcess(scc);
        }
        if (null != eProcess) {
            p.setTreeLabel(eProcess.getProcessId());
            p.setTreeLabelDesc(eProcess.getEmissionProcessNm());
        } else if(scc == null){
            p.setTreeLabel("no SCC value");
        } else {
            p.setTreeLabel("SCC: " + scc.substring(0, 1) + "-"
                    + scc.substring(1, 3) + "-" + scc.substring(3, 6) + "-"
                    + scc.substring(6));
        }
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public EmissionsReport reportFacilityMatch(Facility facility,
            EmissionsReport report, ReportTemplates scReports) throws DAOException {
        int tempPId = -1;
        String sccVal;
        // Locate EUs in the report not represented in the facility inventory.
        // these will be flagged in the report tree or removed from report.
        Iterator<EmissionsReportEU> euIt = report.getEus().iterator();
        while (euIt.hasNext()) {
            EmissionsReportEU rEu = euIt.next();
            // is it in facility inventory
            EmissionUnit e = facility.getMatchingEmissionUnit(rEu.getCorrEpaEmuId());
            EmissionUnit eSave = e;
            if (null != e) { // should it also be in report
                // Get the name in case it needs to be displayed
                rEu.setEpaEmuId(e.getEpaEmuId());
                rEu.setExcludedDueToAttributes(scReports.getSc().isBelowRequirements(e));
                rEu.setPhase1Boiler(facility.getFacilityId(), report.getReportYear());
                if (!reportable(e, report.getReportYear().intValue(), scReports.getSc().getContentTypeCd())) {
                    e = null;
                } else {
                    if(e.getEmissionProcesses().size() > 0) {
                        rEu.setNoProcesses(false);
                    } else {
                        rEu.setNoProcesses(true);
                    }
                    // Regardless of when, mark exempt if De Minimis or permit exempt
                    if(scReports.getSc().isBelowRequirements(e)) {
                        if(!rEu.isZeroEmissions() && !rEu.isForceDetailedReporting()) {
                            rEu.setExemptEG71(true);
                        }
                        if(!rEu.isForceDetailedReporting()) {
                            // Delete periods if EU set to EG#71--if no user data.
                            if(!rEu.nonEmptyPeriods()) {
                                rEu.clearPeriods();
                            } 
                        }
                    }
                }
                rEu.setCaution(rEu.isShouldHaveProcesses() || 
                        rEu.isEg71WithEmissions() ||
                        rEu.isNotInFacility() ||
                        rEu.isNotOperWithEmissions());
            }
            if (null == e) { //rEu is not in profile
                rEu.setNotInFacility(true);
                boolean hasPeriods = true;
                // Should not be in report
                if (null == rEu.getPeriods() || rEu.getPeriods().size() == 0) { // Individual EU has periods
                    hasPeriods = false;
                }
                // Also check to see if the EU belongs to groups.
                boolean gFound = false;
                for (EmissionsReportEUGroup g : report.getEuGroups()) {
                    for (Integer euId : g.getEus()) {
                        if (rEu.getCorrEpaEmuId().equals(euId)) {
                            g.setNotInFacility(true);
                            if(eSave != null) {
                                g.addNotInFacilityList(eSave.getEpaEmuId());
                            } else {
                                g.addNotInFacilityList(unknownEU + rEu.getCorrEpaEmuId());
                            }
                            gFound = true;
                        }
                    }
                }
                if(!hasPeriods && !gFound) {
                    // Remove from the list of EUs in report
                    euIt.remove();
                    continue;
                }
            } else {
                // Locate periods in reportEu not represented as processes in
                // facility eu
                for (EmissionsReportPeriod p : rEu.getPeriods()) {
                    sccVal = p.getSccId();
                    if ((null == e.findProcess(sccVal))) {
                        p.setNotInFacility(true);
                    }
                }
            }
            // See if rEu is marked as exempt because of EG71 and has been
            // included in a group.
            if (rEu.isBelowRequirements() || rEu.isZeroEmissions()) {
                for (EmissionsReportEUGroup g : report.getEuGroups()) {
                    for (Integer euId : g.getEus()) {
                        if (rEu.getCorrEpaEmuId().equals(euId)) {
                            g.setNotInFacility(true);
                            g.addIsMarkedEG71List(rEu.getEpaEmuId());
                        }
                    }
                }
            }

            // Also, look for rEu in a group and see if that process is not in
            // facility
            for (EmissionsReportEUGroup g : report.getEuGroups()) {
                for (Integer rEuId : g.getEus()) {
                    if (e != null && rEuId.equals(e.getCorrEpaEmuId())) {
                        EmissionsReportPeriod p = g.getPeriod();
                        sccVal = p.getSccId();
                        if (null == e.findProcess(sccVal)) {
                            g.setNotInFacility(true);
                            g.addNotHaveProcessList(rEu.getEpaEmuId());
                        }
                    }
                }
            }
        }

        // See if all the EUs in a group have the same equipment
        // Only look at those with correct SCC code since other code
        // is already checking for that more important error
        for (EmissionsReportEUGroup g : report.getEuGroups()) {
            String sccId = g.getPeriod().getSccId();
            // Just look at those where the SCC id matches because the
            // mis-matched ones have already generated error messages.
            ArrayList<Integer> tmpG = new ArrayList<Integer>(g.getEus().size());
            for(Integer ci : g.getEus()) {
                EmissionUnit eu = facility.getMatchingEmissionUnit(ci);
                if(eu == null) {
                    continue;
                }
                EmissionProcess p = eu.findProcess(sccId);
                if(p == null) {
                    continue;
                }
                tmpG.add(ci);
            }

            if(tmpG.size() > 1) {
                // List of those that match no others
                ArrayList<String> singles = new ArrayList<String>();
                // Lists of those that match some others
                ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
                while (tmpG.size() > 0) {
                    // Get EU identifier
                    ArrayList<String> t = new ArrayList<String>();
                    Integer j = tmpG.remove(0);
                    EmissionUnit eUnit1 = facility.getMatchingEmissionUnit(j);
                    if(eUnit1 == null) {
                        singles.add(unknownEU + j);
                    } else {
                        t.add(eUnit1.getEpaEmuId());
                        EmissionProcess process1 = eUnit1.findProcess(sccId);
                        int ii = 0;
                        // look at remaining list
                        if(process1 != null) { // otherwise accounted for already
                            if(tmpG.size() > 0) {
                                while(ii < tmpG.size()) {
                                    Integer k = tmpG.get(ii);
                                    EmissionUnit eUnit2 = facility.getMatchingEmissionUnit(k);
                                    if(eUnit2 == null) {
                                        t.add(unknownEU + k);
                                        tmpG.remove(ii);
                                    } else {
                                        EmissionProcess process2 = eUnit2.findProcess(sccId);
                                        if(process2 != null) { // otherwise already accounted for
                                            if(process1.sameEquipment(process2)){
                                                t.add(eUnit2.getEpaEmuId());
                                                tmpG.remove(ii);
                                            }else {
                                                ii = ii + 1;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if(1 == t.size()) {
                            singles.add(eUnit1.getEpaEmuId());
                        } else {
                            lists.add(t);
                        }
                    }
                }
                StringBuffer b = new StringBuffer();
                if(lists.size() != 1 || singles.size() != 0) {
                    // we have mismatched equipment
                    g.setCaution(true);
                    g.setNotInFacility(true);
                    printList(singles, b,
                            "The processes with this SCC in the following Emissions Units each have a different control equipment and release point configuration: ",
                            ".  ", ", ", " and ") ;

                    if(lists.size() > 0) {
                        String sep1 = ", ";
                        String sep2 = " and ";
                        String sep = sep1;
                        b.append("The following subgroups of Emissions Units each have a different control equipment and release point configuration: ");
                        b.append("(");
                        printList(lists.get(0), b, "", "",
                                ", ", " & ") ;
                        b.append(")");
                        for(int i = 1; i < lists.size(); i++) {
                            if(i == lists.size() - 1) {
                                sep = sep2;
                            }
                            b.append(sep + "(");
                            printList(lists.get(i), b, "", "",
                                    ", ", " & ") ;
                            b.append(")");
                        }
                        b.append(".  "); 
                    }
                    b.append("  To be in this group, they must have the same configuration of the same control equipment and release points (with the same Company Ids).");
                }

                g.setNotSameCeEpList(b.toString());
            }
        }

        // Go thorugh groups and generate error messages from information
        // already collected.
        for (EmissionsReportEUGroup g : report.getEuGroups()) {
            g.notInFacilityMsgGen();
        }

        // Locate EUs and EU/periods that are in the facility but not in the
        // report and add them
        for (EmissionUnit e : facility.getEmissionUnits().toArray(
                new EmissionUnit[0])) {
            if (reportable(e, report.getReportYear().intValue(), scReports.getSc().getContentTypeCd())) {
                EmissionsReportEU rEu = report.getEu(e.getCorrEpaEmuId());
                if (null == rEu) {
                    // Add it to the report
                    tempPId = addReportEU(facility, report, e, false, tempPId--, scReports);
                } else { // EU is in the report, what about periods?
                    if((scReports.getSc().isBelowRequirements(e) && !rEu.isForceDetailedReporting()) || 
                            rEu.isBelowRequirements() || rEu.isZeroEmissions()) {
                    } else {  // not De minimis or zero emissions
                        // Locate processes in facility EU not represented as
                        // periods in reportEU--but only if not De minimis or zero emissions
                        boolean notFound = true;
                        for (EmissionProcess p : e.getEmissionProcesses().toArray(
                                new EmissionProcess[0])) {
                            rEu.setCaution(false);
                            sccVal = p.getSccId();
                            if (null == rEu.findPeriod(sccVal)) {
                                notFound = !properlyBelongsToGroup(e, sccVal, report);
                                if (notFound) {
                                    addReportPeriod(report, rEu, p.getSccCode(), false,
                                            tempPId--);
                                }
                            }
                        }
                    }
                }
            }
        }
        return report;
    }

    private static boolean properlyBelongsToGroup(EmissionUnit e, 
            String sccVal, EmissionsReport report) {
        // See if it belongs to a group
        boolean notFound = true;
        for (EmissionsReportEUGroup g : report
                .getEuGroups()) {
            String gSccVal;
            EmissionsReportPeriod per = g.getPeriod();
            gSccVal = per.getSccId();
            // Do the SCC codes match?
            if(gSccVal != null && sccVal != null &&
                    gSccVal.equals(sccVal)) {
                // they match, if the EU is here then
                // it is this process
                for (Integer rEuId : g.getEus()) {
                    if (rEuId.equals(e.getCorrEpaEmuId())) {
                        notFound = false;
                        break;
                    }
                }
            }
        }
        return !notFound;
    }

    static private void printList(ArrayList<String> list, StringBuffer b,
            String prefix, String postfix,
            String sepBetween, String sepLast) {

        String sep = sepBetween;
        if(0 != list.size()) {
            b.append(prefix);
            b.append(list.get(0));
            for(int i = 1; i < list.size(); i++) {
                if(i == list.size() - 1) {
                    sep = sepLast;
                }
                b.append(sep + list.get(i));
            }
            b.append(postfix); 
        }
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public DefaultStackParms retrieveDefaultStackParms(String sccId)
    throws DAOException {
        return emissionsReportDAO().retrieveDefaultStackParms(sccId);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public FireRow[] retrieveFireRows(Integer year, EmissionsReportPeriod p)
    throws DAOException {
        return emissionsReportDAO().retrieveFireRows(year, p);
    }

    private int addReportEU(Facility facility, EmissionsReport report, EmissionUnit e,
            boolean notInFacility, int tempPId, ReportTemplates scReports) throws DAOException {
        EmissionsReportEU rEu = new EmissionsReportEU();
        rEu.setCorrEpaEmuId(e.getCorrEpaEmuId());
        rEu.setEpaEmuId(e.getEpaEmuId());
        rEu.setPhase1Boiler(facility.getFacilityId(), report.getReportYear());
        rEu.setNotInFacility(notInFacility);
        report.addEu(rEu);
        rEu.setNoProcesses(true);
        if(scReports.getSc().isBelowRequirements(e)) {
            rEu.setExemptEG71(true);
            rEu.setCaution(false);
        } else {
            rEu.setCaution(true);
            for (EmissionProcess p : e.getEmissionProcesses().toArray(
                    new EmissionProcess[0])) {
                addReportPeriod(report, rEu, p.getSccCode(), notInFacility, tempPId--);
                rEu.setNoProcesses(false);
                if(rEu.isBelowRequirements() == false) {
                    rEu.setCaution(false);
                }
            }
        }
        return tempPId;
    }

    private void addReportPeriod(EmissionsReport report, EmissionsReportEU rEu, SccCode scc,
            boolean notInFacility, int tempPId) throws DAOException {
        InfrastructureDAO infrastructureDAO = infrastructureDAO();
        EmissionsReportPeriod erP = new EmissionsReportPeriod();
        erP.setSccCode(scc);
        erP.setNotInFacility(notInFacility);
        erP.setEmissionPeriodId(new Integer(tempPId));
        if(null != erP.getSccId())
        {
            try {
                SccCode sccCode = null;
                sccCode = infrastructureDAO.retrieveSccCode(
                        erP.getSccId());
                if(sccCode == null) {
                    throw new DAOException("Scc code " +
                            erP.getSccId() +
                            " not found in database.  This is under Emission Unit "
                            + rEu.getEpaEmuId());
                }
                sccCode.determineDeprecated(report.getReportYear());
                erP.setSccCode(sccCode);
            } catch(DAOException e) {
                logger.error(e.getMessage(), e);
                throw new DAOException("Failure to read SCC code " +
                        erP.getSccId() +
                        " for emissions inventory " + report.getEmissionsInventoryId() +
                        " and period " + erP.getEmissionPeriodId() +
                        ".  " + e.getMessage(), e);
            }
        }
        rEu.addPeriod(erP);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public PollutantPair[] retrievePollutantPartOf()
    throws DAOException {
        return emissionsReportDAO().retrievePartOf();

    }

	public PollutantPair retrievePollutantParent(String pollutantCode) throws DAOException {
		return emissionsReportDAO().retrievePollutantParent(pollutantCode);
	}

	// create directory for emission report documents
    private void createReportDir(Facility facility, EmissionsReport rpt)
    throws DAOException {
        dirName = "/Facilities/" + facility.getFacilityId()
        + "/EmissionsInventories/" + rpt.getEmissionsRptId();
        try {
            DocumentUtil.mkDir(dirName);
        } catch (IOException e) {
            throw new DAOException("Exception creating file store directory for emissions inventory " + rpt.getEmissionsInventoryId(), e);
        }
    }

    private void deleteReportDir() throws DAOException {
        if(dirName != null) {
            try {
                DocumentUtil.rmDir(dirName);
            } catch (IOException e) {
                dirName = null;
                throw new DAOException("Exception deleting file store directory " + dirName, e);
            }
        }
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void changeProfile(EmissionsReport report, 
            Facility facility, Task createRTask,
            Task oldFTask, Task replaceFTask, Task newFTask) 
    throws DAOException {
    	
        // fpId is id of profile to change to.
        // report contains fpId currently using.
        checkNull(report);
        checkNull(oldFTask);
        Transaction trans = null;
        try {
            trans = TransactionFactory.createTransaction();

            Integer replaceFpId;
            String replaceTaskId;
            if(replaceFTask != null) {
                // Already in Staging, increase its reference count
                infraService.incrementReferenceCount(replaceFTask.getTaskId(), trans);
                replaceFpId = replaceFTask.getFpId();
                replaceTaskId = replaceFTask.getTaskId();
            } else if(newFTask != null){
                // If not already in staging
                infraService.createFacilityTask(newFTask, trans);
                replaceFpId = newFTask.getFpId();
                replaceTaskId = newFTask.getTaskId();
            } else {
                logger.error("newFTask parameter is null for emissions inventory " + report.getEmissionsInventoryId());
                throw new DAOException("newFTask parameter is null " + report.getEmissionsInventoryId());
            }
            // Link report task to new task
            createRTask.setDependentTaskId(replaceTaskId);
            createRTask.setFpId(replaceFpId);
            infraService.modifyTask(createRTask, trans);
            
            // call decreaseReferenceCount() of old one if 
            // the reference count is higher than 1 or if old one is
            // a historical facility inventory or if it is
            // the current facility inventory but does not appear
            // changed.  Appears unchanged if lastSubmissionType &
            // and lastSubmissionVersion have same values as the
            // current in read-only.
            // Otherwise call decreaseReferenceCountOnly()
            if(oldFTask.getReferenceCount() > 1 || oldFTask.getTaskType() == Task.TaskType.FCH) {
                infraService.decrementReferenceCount(oldFTask.getTaskId(), trans);
                if(oldFTask.getReferenceCount() == 1) {
                    // delete the dataset.
                    infraService.deleteDataSet(oldFTask.getTaskId(), oldFTask.getUserName());
                }
            } else {
                // Compare against current facility inventory in read-only
                Facility roCurrentFacility = facilityService.retrieveFacility(facility.getFacilityId());
                checkNull(roCurrentFacility);
                if(facility.getLastSubmissionVersion().equals(roCurrentFacility.getLastSubmissionVersion())) {
                    // no modifications
                    infraService.decrementReferenceCount(oldFTask.getTaskId(), trans);
                    if(oldFTask.getReferenceCount() == 1) {
                        // delete the dataset.
                        infraService.deleteDataSet(oldFTask.getTaskId(), oldFTask.getUserName());
                    }
                } else {
                    // May have been modified
                    infraService.decrementReferenceCountOnly(oldFTask.getTaskId(), trans);
                }
            }
            
            // Associate with report
            report.setFpId(replaceFpId);
            EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(true));
            emissionsRptDAO.setTransaction(trans);
            emissionsRptDAO.modifyEmissionsReport(report, null);
            
        } catch (RemoteException e) {
            logger.error("Exception in re-associating profile for emissions inventory " + report.getEmissionsInventoryId(), e);
            throw new DAOException("Exception in re-associating profile for emissions inventory " + report.getEmissionsInventoryId(), e);
        } catch (ServiceFactoryException e) {
            logger.error(e.getMessage(), e);
            throw new DAOException("Exception in re-associating profile for emissions inventory " + report.getEmissionsRptId(), e);
        }
        
    }

    /**
     * Retrieve the emissions document identified by docId.
     * @param docId
     * @return
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    public EmissionsDocumentRef retrieveEmissionsDocument(Integer docId, boolean staging)
    throws DAOException {
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(staging));
        return emissionsRptDAO.retrieveEmissionsDocument(docId);
    }

    /**
     * Retrieve all emissions documents related to the report
     * identified by rptId.
     * @param rptId EmissionsReport identifier.
     * @return
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    public EmissionsDocumentRef[] retrieveEmissionsDocuments(Integer rptId, boolean staging)
    throws DAOException {
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(staging));
        return emissionsRptDAO.retrieveEmissionsDocuments(rptId);
    }

    /**
     * Create a zip file containing emissions inventory data and all its related
     * attachments and download its contents.
     * @param app
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Document generateTempAttachmentZipFile(String emRptId, List<EmissionsDocumentRef> attachmentList, boolean hideTradeSecret,
            String facilityId, Document facilityDoc, TmpDocument reportDoc, TmpDocument reportSummaryDoc, TmpDocument reportSubmittedDoc, boolean readOnly, Timestamp submittedDate, boolean includeAllAttachments) {    	

        TmpDocument erDoc = new TmpDocument();
        // Set the path elements of the temp doc.
        erDoc.setDescription("Emissions Inventory zip file");
        erDoc.setFacilityID(facilityId);
        erDoc.setTemporary(true);
        if(hideTradeSecret) {
        	erDoc.setTmpFileName(emRptId + ".zip");
        } else {
        	erDoc.setTmpFileName(emRptId + "_TS" + ".zip");
        }
        erDoc.setContentType(Document.CONTENT_TYPE_ZIP);

        // make sure temporary directory exists
        OutputStream os = null;
        try {
            DocumentUtil.mkDirs(erDoc.getDirName());
            os = DocumentUtil.createDocumentStream(erDoc.getPath());
        } catch(IOException e) {
            String s = "Failed to createDocumentStream, path=" + 
                    erDoc.getPath() + ", os is " + ((os != null)?"not null":"null") + " for emissions inventory " + emRptId;
            logger.error(s, e);
            erDoc = null;
        }
        ZipOutputStream zos = new ZipOutputStream(os);
        boolean ok = false;
        try {
            ok = zipAttachments(emRptId, attachmentList, !hideTradeSecret, zos, facilityDoc, reportDoc, reportSummaryDoc, reportSubmittedDoc, readOnly, submittedDate, includeAllAttachments);
        } catch(IOException e) {
            String s = "Failed to zip attachments, path=" + 
            erDoc.getPath() + ", os is " + ((os != null)?"not null":"null")  + " for emissions inventory " + emRptId;
            logger.error(s, e);
            erDoc = null;
        } finally {
            try {
                zos.close(); 
                os.close();
            } catch(FileNotFoundException e) {
                String s = "Failed to zip attachments, path=" + 
                erDoc.getPath() + ", os is " + ((os != null)?"not null":"null")  + " for emissions inventory " + emRptId;
                logger.error(s, e);
                erDoc = null;
            } catch(IOException e) {
                String s = "Failed to zip attachments, path=" + 
                erDoc.getPath() + ", os is " + ((os != null)?"not null":"null")  + " for emissions inventory " + emRptId;
                logger.error(s, e);
                erDoc = null;
            }
        }
        if(!ok) erDoc = null;
        return erDoc;
    }

    private boolean zipAttachments(String emRptId, List<EmissionsDocumentRef> attachmentList,
    		boolean includeTradeSecretFiles, ZipOutputStream zos, Document facilityDoc, 
    		TmpDocument reportDoc, TmpDocument reportSummaryDoc, TmpDocument reportSubmittedDoc, boolean readOnly, Timestamp submittedDate, boolean includeAllAttachments) throws DAOException {

		if (!isPublicApp()) {
			// add attachments to zip file
			HashSet<Integer> docIdSet = new HashSet<Integer>();
			for (EmissionsDocumentRef attachment : attachmentList) {
				// don't include documents added after emissions inventory was
				// submitted
				if ((DefData.isDapcAttachmentOnly(attachment.getEmissionsDocumentTypeCD()))
						|| (submittedDate != null && attachment.getLastModifiedTS() != null
								&& attachment.getLastModifiedTS().after(submittedDate))) {
					logger.debug("Excluding document " + attachment.getEmissionsDocId()
							+ " from emissions inventory zip file. Document last modified date ("
							+ attachment.getLastModifiedTS() + ") is after emissions inventory submission date ("
							+ submittedDate + ")");
					continue;
				}
				if (attachment.getDocumentId() != null && !docIdSet.contains(attachment.getDocumentId())) {
					docIdSet.add(attachment.getDocumentId());
					Document doc = null;
					try {
						doc = docService.getDocumentByID(attachment.getDocumentId(), readOnly);
					} catch (RemoteException re) {
						logger.error("docBO.getDocumentByID(" + attachment.getDocumentId()
								+ ") failed for emissions inventory " + emRptId, re);
						return false;
					}
					InputStream docIS = null;
					if (doc != null && !doc.isTemporary()) {
						try {
							docIS = DocumentUtil.getDocumentAsStream(doc.getPath());
						} catch (FileNotFoundException e) {
							docIS = null;
							logger.error("DocumentUtil.getDocumentAsStream(" + doc.getPath()
									+ ") failed for emissions inventory " + emRptId, e);
							return false;
						} catch (IOException e) {
							docIS = null;
							logger.error("DocumentUtil.getDocumentAsStream(" + doc.getPath()
									+ ") failed for emissions inventory " + emRptId, e);
							return false;
						}
						if (docIS != null) {
							try {
								addEntryToZip(
										getNameForDoc(attachment.getEmissionsDocumentTypeCD(),
												attachment.getEmissionsDocId().toString(), false, doc.getExtension()),
										docIS, zos);
							} catch (IOException ioe) {
								logger.error("addEntryToZip(" + doc.getDescription()
										+ ") failed for emissions inventory " + emRptId, ioe);
								return false;
							} finally {
								try {
									docIS.close();
								} catch (IOException e) {
									;
								}
							}
						}
					} else {
						String msg = "No document found with id " + attachment.getDocumentId();
						if (doc != null) {
							msg = "Document " + attachment.getDocumentId() + " is temporary";
						}
						logger.error(msg, new Exception());
						return false;
					}
				}
				if (includeTradeSecretFiles && attachment.getTradeSecretDocId() != null
						&& !docIdSet.contains(attachment.getTradeSecretDocId())
						&& (includeAllAttachments || submittedDate != null || attachment.isTradeSecretAllowed())) {
					docIdSet.add(attachment.getTradeSecretDocId());
					Document doc = null;
					try {
						doc = docService.getDocumentByID(attachment.getTradeSecretDocId(), readOnly);
					} catch (RemoteException re) {
						logger.error("docBO.getDocumentByID(" + attachment.getTradeSecretDocId()
								+ ") failed for emissions inventory " + emRptId, re);
						return false;
					}
					InputStream docIS = null;
					if (doc != null && !doc.isTemporary()) {
						try {
							docIS = DocumentUtil.getDocumentAsStream(doc.getPath());
						} catch (FileNotFoundException e) {
							docIS = null;
							logger.error("DocumentUtil.getDocumentAsStream(" + doc.getPath()
									+ ") failed for emissions inventory " + emRptId, e);
							return false;
						} catch (IOException e) {
							docIS = null;
							logger.error("DocumentUtil.getDocumentAsStream(" + doc.getPath() + ") failed for report "
									+ emRptId, e);
							return false;
						}
						if (docIS != null) {
							try {
								addEntryToZip(getNameForDoc(attachment.getEmissionsDocumentTypeCD(),
										attachment.getEmissionsDocId().toString(), includeTradeSecretFiles,
										doc.getExtension()), docIS, zos);
							} catch (IOException ioe) {
								logger.error("addEntryToZip(" + doc.getDescription()
										+ ") failed for emissions inventory " + emRptId, ioe);
								return false;
							} finally {
								try {
									docIS.close();
								} catch (IOException e) {
									;
								}
							}
						}
					} else {
						String msg = "No trade secret document found with id " + attachment.getTradeSecretDocId()
								+ " for emissions inventory " + emRptId;
						if (doc != null) {
							msg = "Trade secret Document " + attachment.getTradeSecretDocId()
									+ " for emissions inventory " + emRptId + " is temporary";
						}
						logger.error(msg, new Exception());
						return false;
					}
				}
			}
		}
        boolean ok = true;
        try {
        	String fileName;
        	InputStream docIS;
        	// generate file with facility data
        	if(facilityDoc != null) {
        		fileName = facilityDoc.getFacilityID() + ".pdf";
        		docIS = DocumentUtil.getDocumentAsStream(facilityDoc.getPath());
        		if (docIS != null) {
        			addEntryToZip(fileName, docIS, zos);
        			docIS.close(); 
        		} else {
        			logger.error("Error generating profile file for " + emRptId);
        			ok = false;
        		}
        	}
        	
        	// generate file with summary data
        	if(reportSummaryDoc != null) {
        		fileName = "EmissionsReportSummary_" + emRptId + (includeTradeSecretFiles?"_TS":"") + ".pdf";
        		docIS = DocumentUtil.getDocumentAsStream(reportSummaryDoc.getPath());
        		if (docIS != null) {
        			addEntryToZip(fileName, docIS, zos);
        			docIS.close(); 
        		} else {
        			logger.error("Error generating summary file for " + emRptId);
        			ok = false;
        		}
        	}
        	
        	if (reportDoc != null) {
        		// generate file with report data
        		fileName = "EmissionsReport_" + emRptId + (includeTradeSecretFiles?"_TS":"") + ".pdf";
        		docIS = DocumentUtil.getDocumentAsStream(reportDoc.getPath());
        		if (docIS != null) {
        			addEntryToZip(fileName, docIS, zos);
        			docIS.close(); 
        		} else {
        			logger.error("Error generating report file for " + emRptId);
        			ok = false;
        		}
        	}
        	
        	if (reportSubmittedDoc != null) {
        		// generate file with report submitted data
        		fileName = "EmissionsReport_Submitted_" + emRptId + (includeTradeSecretFiles?"_TS":"") + ".pdf";
        		docIS = DocumentUtil.getDocumentAsStream(reportSubmittedDoc.getPath());
        		if (docIS != null) {
        			addEntryToZip(fileName, docIS, zos);
        			docIS.close(); 
        		} else {
        			logger.error("Error generating report submitted file for " + emRptId);
        			ok = false;
        		}
        	}
        } catch(IOException ioe) {
        	logger.error("Failed adding temporary files", ioe);
        	ok = false;
        }
        return ok;
    }

    /**
     * Create a more descriptive name for attachments to be included in a
     * zip file.
     * @param docTypeCd document type code.
     * @param doc document record.
     * @return
     */
    private String getNameForDoc(String docTypeCd, String docId, boolean ts, String extension) {                    
        StringBuffer docName = new StringBuffer();
        if (docTypeCd != null) {
            docName.append(
                    EmissionsAttachmentTypeDef.getData().getItems().getItemDesc(docTypeCd) + "_");
        }
        docName.append(docId);
        if(ts) docName.append("_TS");
        if (extension != null && extension.length() > 0) {
            docName.append("." + extension);
        }
        return docName.toString();
    }

    /**
     * Load Document objects into all EmissionsDocumentRef objects associated
     * with report.
     * @param report
     * @param readOnly
     * @throws DAOException 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    public void loadAllDocuments(EmissionsReport report, boolean staging) throws DAOException {
        for (EmissionsDocumentRef docRef : report.getAttachments()) {
            docRef.setPublicDoc(null);
            loadDocuments(docRef, staging);
        }
    }

    /**
     * Load Document objects into docRef to avoid multiple queries for this information.
     * @param docRef
     * @param readOnly
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    public void loadDocuments(EmissionsDocumentRef docRef, boolean staging) throws DAOException {
        DocumentDAO docDAO = documentDAO(getSchema(staging));
        if (docRef != null) {
            if (docRef.getDocumentId() != null && docRef.getPublicDoc() == null) {
                Document d = docDAO.retrieveDocument(docRef.getDocumentId());
                if(d == null) {
                    throw new DAOException("Did not locate Document with id=" +
                            docRef.getDocumentId() + ", staging=" + staging);
                }
                EmissionsDocument doc = new EmissionsDocument(d);
                doc.setEmissionsRptId(docRef.getEmissionsRptId());
                String path = doc.getPath();
                logger.debug(" loading emissions inventory public attachment with path = " + path);
                docRef.setPublicDoc(doc);
            }
            if (docRef.getTradeSecretDocId() != null && docRef.getTradeSecretDoc() == null) {
                Document d = docDAO.retrieveDocument(docRef.getTradeSecretDocId());
                if(d == null) {
                    throw new DAOException("Did not locate trade secret Document with id=" +
                            docRef.getTradeSecretDocId() + ", staging=" + staging);
                }
                EmissionsDocument doc = new EmissionsDocument(d);
                doc.setEmissionsRptId(docRef.getEmissionsRptId());
                String path = doc.getPath();
                logger.debug(" loading emissions inventory trade secret attachment with path = " + path);
                docRef.setTradeSecretDoc(doc);
            }
        }
    }

    /**
     * @param rpt
     * @return
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public EmissionsDocumentRef uploadEmissionsDocument(Facility facility,
            EmissionsReport rpt,
            EmissionsDocumentRef doc, UploadedFileInfo publicFile, 
            UploadedFileInfo tsFile, Integer userId)
    throws DAOException {
        Transaction trans = null;
        EmissionsDocument publicDoc = null;
        EmissionsDocument tsDoc = null;
        try {
            trans = TransactionFactory.createTransaction();
            if (publicFile != null) {
                publicDoc = new EmissionsDocument();
                publicDoc.setFacilityID(facility.getFacilityId());
                publicDoc.setEmissionsRptId(rpt.getEmissionsRptId());
                publicDoc.setLastModifiedBy(userId);
                publicDoc.setDescription(doc.getDescription());
                try {
                    // upload public document and create record in dc_document
                    publicDoc = (EmissionsDocument)uploadDocument(publicDoc, 
                            publicFile.getFileName(), publicFile.getInputStream(), 
                            trans);
                    doc.setDocumentId(publicDoc.getDocumentID());
                } catch (IOException e) {
                    String s = "Exception while uploading public emissions inventory document for inventory " + rpt.getEmissionsRptId();
                    logger.error(s, e);
                    throw new DAOException(s, e);
                }
            }
            if (tsFile != null) {
                tsDoc = new EmissionsDocument();
                tsDoc.setFacilityID(facility.getFacilityId());
                tsDoc.setEmissionsRptId(rpt.getEmissionsRptId());
                tsDoc.setLastModifiedBy(userId);
                tsDoc.setDescription(doc.getDescription());
                try {
                    // upload trade secret document and create record in dc_document
                    tsDoc = (EmissionsDocument)uploadDocument(tsDoc, 
                            tsFile.getFileName(), tsFile.getInputStream(), trans);
                    doc.setTradeSecretDocId(tsDoc.getDocumentID());
                } catch (IOException e) {
                    String s = "Exception while uploading trade secret emissions inventory document for inventory " + rpt.getEmissionsRptId();
                    logger.error(s, e);
                    throw new DAOException(s, e);
                }
            }
            // create record in rp_emissions_document table
            EmissionsReportDAO emissionsReportDAO = emissionsReportDAO(trans);
            doc = emissionsReportDAO.createEmissionsDocument(doc);

            trans.complete();
        } catch (DAOException de) {
            if (publicDoc != null && publicDoc.getPath() != null) {
                try {
                    DocumentUtil.removeDocument(publicDoc.getPath());
                } catch (IOException e) {
                    logger.warn("Exception deleting document " + publicDoc.getPath());
                }
            }
            if (tsDoc != null && tsDoc.getPath() != null) {
                try {
                    DocumentUtil.removeDocument(tsDoc.getPath());
                } catch (IOException e) {
                    logger.warn("Exception deleting document " + tsDoc.getPath());
                }
            }
            cancelTransaction(trans, de);  // does throw
        } finally {
            closeTransaction(trans);
        }

        return doc;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public EmissionsDocumentRef createEmissionsDocument(EmissionsDocumentRef doc)
    throws DAOException {
        Transaction trans = null;
        EmissionsDocumentRef ret = null;

        try {
            trans = TransactionFactory.createTransaction();
            EmissionsReportDAO emissionsReportDAO = emissionsReportDAO(trans);
            ret = emissionsReportDAO.createEmissionsDocument(doc);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);  // does throw
        } finally {
            closeTransaction(trans);
        }

        return ret;
    }

    /**
     * 
     * @param doc
     * @param filename
     * @param is
     * @return
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Document uploadDocument(Document doc, String filename,
            InputStream is) throws DAOException {
        Transaction trans = null;
        Document result = null;

        try {
            trans = TransactionFactory.createTransaction();
            result = uploadDocument(doc, filename, is, trans);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);  // does throw
        } finally {
            closeTransaction(trans);
        }

        return result;
    }

    private Document uploadDocument(Document doc, String filename,
            InputStream is, Transaction trans) throws DAOException {

        DocumentDAO docDAO = documentDAO(trans);

        // Set the path elements of the temp doc.
        doc.setExtension(DocumentUtil.getFileExtension(filename));

        doc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
        if(doc.getUploadDate() == null) {
            doc.setUploadDate(doc.getLastModifiedTS());
        }

        // add document info to database
        // NOTE: This needs to be done before file is created in file store
        // since document id obtained from createDocument method is used as
        // the file name for the file store file
        docDAO.createDocument(doc);

        // copy document to file store
        try {
            DocumentUtil.createDocument(doc.getPath(), is);
        } catch (IOException e) {
            logger.error("Exception creating document in file store: " + doc.getPath(), e);
            throw new DAOException("Exception creating document in file store: " + doc.getPath(), e);
        }

        return doc;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    public MultiEstablishment[] facilitiesWithEisReports(int year)
    throws DAOException {
        Transaction trans = null;
        MultiEstablishment[] ret = null;

        try {
            trans = TransactionFactory.createTransaction();
            EmissionsReportDAO emissionsReportDAO = emissionsReportDAO(trans);
            ret = emissionsReportDAO.facilitiesWithEisReports(year);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);  // does throw
        } finally {
            closeTransaction(trans);
        }

        return ret;
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    public DbInteger[] locateUsingOldEuName(String facilityId, int fpId, String oldName) throws DAOException {
        Transaction trans = null;
        DbInteger[] ret = null;

        try {
            trans = TransactionFactory.createTransaction();
            EmissionsReportDAO emissionsReportDAO = emissionsReportDAO(trans);
            ret = emissionsReportDAO.locateUsingOldEuName(facilityId, fpId, oldName);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);  // does throw
        } finally {
            closeTransaction(trans);
        }

        return ret;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
     */
    public MultiEstablishment[] activeTvFacilities(int year)
    throws DAOException {
        Transaction trans = null;
        MultiEstablishment[] ret = null;

        try {
            trans = TransactionFactory.createTransaction();
            EmissionsReportDAO emissionsReportDAO = emissionsReportDAO(trans);
            ret = emissionsReportDAO.activeTvFacilities(year);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);  // does throw
        } finally {
            closeTransaction(trans);
        }

        return ret;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public boolean modifyEmissionsDocument(EmissionsDocumentRef doc)
    throws DAOException {
        Transaction trans = null;
        boolean ret = false;

        try {
            trans = TransactionFactory.createTransaction();
            EmissionsReportDAO emissionsReportDAO = emissionsReportDAO(trans);
            ret = emissionsReportDAO.modifyEmissionsDocument(doc);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);  // does throw
        } finally {
            closeTransaction(trans);
        }

        return ret;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void removeEmissionsDocument(EmissionsDocumentRef rptDoc) throws DAOException {
        Transaction trans = null;

        try {
            trans = TransactionFactory.createTransaction();
            removeEmissionsDocument(rptDoc, trans);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);  // does throw
        } finally {
            closeTransaction(trans);
        }
    }

    private void removeEmissionsDocument(EmissionsDocumentRef rptDoc, Transaction trans) throws DAOException {
    	
        EmissionsReportDAO emissionsReportDAO = emissionsReportDAO(trans);
        DocumentDAO docDAO = documentDAO(trans);

        // first, flag document as temporary so it will be cleaned up later
        Integer docId = rptDoc.getDocumentId();
        if (docId != null) {
            // mark document as temporary so it will be deleted later
            Document doc = docDAO.retrieveDocument(docId);
			if (doc != null) {
				if (isPortalApp()) {
					docDAO.removeDocument(doc);
				} else {
					doc.setTemporary(true);
					docDAO.modifyDocument(doc);
				}
			} else {
				logger.error("No document found in Emissions Inventory " + rptDoc.getEmissionsRptId()
						+ " for document: " + docId);
			}
        }

        // then delete trade secret attachment (if any)
        docId = rptDoc.getTradeSecretDocId();
        if (docId != null) {
            // mark document as temporary so it will be deleted later
            Document doc = docDAO.retrieveDocument(docId);
			if (doc != null) {
				if (isPortalApp()) {
					docDAO.removeDocument(doc);
				} else {
					doc.setTemporary(true);
					docDAO.modifyDocument(doc);
				}
			} else {
				logger.error("No document found in Emissions Inventory " + rptDoc.getEmissionsRptId()
						+ " for trade secret document: " + docId);
			}
        }

        emissionsReportDAO.removeEmissionsDocument(rptDoc.getEmissionsDocId());
        
    }

    /**
     * Get list of documents associated with the TV/SMTV emission report.
     * @param 
     * @return
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public List<Document> getPrintableDocumentList(Facility facility, Document facilityDoc, 
    		TmpDocument reportDoc, TmpDocument reportSummaryDoc, EmissionsReport report, 
    		ReportTemplates scReports, String user, boolean hideTradeSecret,
            boolean inStaging, boolean includeAllAttachments)throws DAOException {
        List<Document> docList = new ArrayList<Document>();
        boolean anyTradeSecret;
        
        // This may not be necessary, but be very sure that hideTradeSecret is true for Pubic Website.
        if (isPublicApp()) {
        	hideTradeSecret = true;
        }
        
        TmpDocument reportSubmittedDoc = null;
		if (!isPublicApp()) {
			if (report.getRptReceivedStatusDate() != null) {
				reportSubmittedDoc = new TmpDocument();
				String submittedDocDesc = "Printable View of What Will Be Submitted from Data Entered";
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
				submittedDocDesc = "Printable View of Emissions Inventory Data Submitted on "
						+ dateFormat.format(report.getRptReceivedStatusDate());
				anyTradeSecret = generateTempEmissionReport(facility, report, scReports, user,
						"EmissionsReport_Submitted_",
						(hideTradeSecret ? submittedDocDesc : submittedDocDesc + " with trade secret data"),
						hideTradeSecret, true, reportSubmittedDoc, true, includeAllAttachments);
				docList.add(reportSubmittedDoc);
			}
		}
        
        anyTradeSecret = generateTempEmissionReport(facility, report, scReports, user, 
                "EmissionsReportSummary_", (hideTradeSecret? "Emissions Inventory Summary" : "Emissions Inventory Summary (Trade Secret)"), hideTradeSecret, false, reportSummaryDoc, false, includeAllAttachments);
        docList.add(reportSummaryDoc);

        anyTradeSecret = generateTempEmissionReport(facility, report, scReports, user, 
                "EmissionsReport_" , (hideTradeSecret? "Emissions Inventory" : "Emissions Inventory (Trade Secret)"), hideTradeSecret, true, reportDoc, false, includeAllAttachments);
        if (!hideTradeSecret && anyTradeSecret) {
            try {
                PdfGeneratorBase.addTradeSecretWatermarkHorizontal(reportDoc.getPath());
            } catch (IOException ioe) {
                String s = "Got IOException for " + reportDoc.getPath() + " for emissions inventory " + report.getEmissionsRptId();
                logger.error(s, ioe);
                throw new DAOException(s, ioe);
            } catch (DocumentException de) {
                String s = "Got DocumentException for " + reportDoc.getPath() + " for emissions inventory " + report.getEmissionsRptId();
                logger.error(s, de);
                throw new DAOException(s, de);
            }
        }
        docList.add(reportDoc);
        report.setContainsTS(anyTradeSecret);
        
        if (facilityDoc != null && !isPublicApp()) {
            docList.add(facilityDoc);
        }

        boolean readOnly = CompMgr.getAppName().equals(CommonConst.INTERNAL_APP) ? false : !inStaging;
        
        if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
        	readOnly = false;
        }
        Document zipDoc = null;
        if (report.getRptReceivedStatusDate() != null && !isPublicApp()) {
        	zipDoc = generateTempAttachmentZipFile(report.getEmissionsInventoryId(), report.getAttachments(), hideTradeSecret, facility.getFacilityId(),
            		facilityDoc, null, null, reportSubmittedDoc, readOnly, report.getRptReceivedStatusDate(), includeAllAttachments);
        } else {
        	zipDoc = generateTempAttachmentZipFile(report.getEmissionsInventoryId(), report.getAttachments(), hideTradeSecret, facility.getFacilityId(),
            		facilityDoc, reportDoc, reportSummaryDoc, null, readOnly, report.getRptReceivedStatusDate(), includeAllAttachments);
        }
        if (zipDoc != null) {
        	docList.add(zipDoc);
        }

        return docList;
    }
    
    /**
     * Get list of documents associated with the TV/SMTV emission report.
     * @param 
     * @return
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public List<Document> getPrintableAttachmentList(Document facilityDoc, TmpDocument reportDoc, 
    		TmpDocument reportSummaryDoc, List<EmissionsDocumentRef> atts, boolean hideTradeSecret,
            boolean inStaging, Timestamp submittedDate, boolean includeAllAttachments)throws DAOException {
        List<Document> docList = new ArrayList<Document>();

        // add attachments        
        EmissionsDocumentRef attachments[] = atts.toArray(new EmissionsDocumentRef[0]);
        generateAttachments(docList, facilityDoc, reportDoc, 
        		reportSummaryDoc, attachments, inStaging, hideTradeSecret, submittedDate, includeAllAttachments);
        return docList;
    }

    private void generateAttachments(List<Document> docList, Document facilityDoc, TmpDocument reportDoc, 
    		TmpDocument reportSummaryDoc, EmissionsDocumentRef attachments[], 
            boolean inStaging, boolean hideTradeSecret, Timestamp submittedDate, boolean includeAllAttachments) throws DAOException {
        Document doc;
        DocumentDAO documentDAO = documentDAO(getSchema(
                !inStaging ? CommonConst.READONLY_SCHEMA : CommonConst.STAGING_SCHEMA));

        for (EmissionsDocumentRef attach : attachments) {
        	// logger.debug(" Emissions Inventory Attachment; description = " + attach.getDescription());
        	// don't include documents added after emissions inventory was submitted
			if (((CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP) || CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) && DefData.isDapcAttachmentOnly(attach.getEmissionsDocumentTypeCD())) || 
					(submittedDate != null
					&& attach.getLastModifiedTS() != null
					&& attach.getLastModifiedTS().after(
							submittedDate))) {
				logger.debug("Excluding document "
						+ attach.getEmissionsDocId()
						+ " from emissions inventory printable attachment list. Document last modified date ("
						+ attach.getLastModifiedTS()
						+ ") is after emissions inventory submission date ("
						+ submittedDate + ")");
				continue;
			}
        	
            if (attach.getDocumentId() != null) {
                doc = documentDAO.retrieveDocument(attach.getDocumentId());
                // document description may not be in synch with application document
                doc.setDescription(attach.getDescription());
                if (!doc.isTemporary()) {
                    docList.add(doc);
                }
            }
            if (!hideTradeSecret && attach.getTradeSecretDocId() != null && (includeAllAttachments || submittedDate != null || attach.isTradeSecretAllowed())) {
                doc = documentDAO
                .retrieveDocument(attach.getTradeSecretDocId());
                if (!doc.isTemporary()) {
                    // document description may not be in synch with application
                    // document
                    doc.setDescription(attach.getDescription()
                            + " (trade secret)");
                    docList.add(doc);
                }
            }

        }
    }

    private boolean generateTempEmissionReport(Facility facility, EmissionsReport report, 
            ReportTemplates scReports, String user, String subFileName, String desc, 
            boolean hideTradeSecret, boolean compleRpt, TmpDocument rptDoc, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws DAOException {
        boolean ret = false;

        try {
            // Set the path elements of the temp doc.
            rptDoc.setDescription(desc);
            rptDoc.setFacilityID(facility.getFacilityId());
            rptDoc.setTemporary(true);
            rptDoc.setTmpFileName(subFileName + report.getEmissionsRptId() + (hideTradeSecret ? "" : "_TS") +  ".pdf");
            // the items below are not needed since this document data is not
            // stored in the database
            // appDoc.setLastModifiedBy();
            // appDoc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
            // appDoc.setUploadDate(appDoc.getLastModifiedTS());
            DocumentUtil.mkDir(rptDoc.getDirName());
            OutputStream os = DocumentUtil.createDocumentStream(rptDoc.getPath());
            ret = writeEmissionReportToStream(facility, report, scReports, hideTradeSecret, compleRpt, os, isSubmittedPDFDoc, includeAllAttachments);
            os.close();

        } catch (Exception ex) {
            logger.error("Cannot generate emissions inventory: " + desc, ex);
            throw new DAOException("Cannot generate emissions inventory" + desc, ex);
        }

        return ret;
    }

    private boolean  writeEmissionReportToStream(Facility facility, EmissionsReport report, 
            ReportTemplates scReports, boolean hideTradeSecret, boolean completRpt, 
            OutputStream os, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws IOException {
        try {
            //make sure we have all Facility information
            EmissionsReportPdfGenerator generator = new EmissionsReportPdfGenerator();
            return generator.generatePdf(facility, report, scReports, hideTradeSecret, completRpt, os, isSubmittedPDFDoc, includeAllAttachments);
        } catch (DocumentException e) {
            logger.error("Exception writing emission report to stream for emissions inventory " + report.getEmissionsRptId(), e);
            throw new IOException("Exception writing emission report to stream for emissions inventory " + report.getEmissionsRptId());
        }
    }

    // NTV

    /**
     * Get list of documents associated with the NTV emission report.
     * @param 
     * @return
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public List<Document> getPrintableDocumentList(Facility facility, NtvReport ntvRpt, TmpDocument reportDoc,
            StringBuffer currentOwnersAddresses, StringBuffer prevOwnerAddress,
            StringBuffer newOwnerAddress,
            StringBuffer currentBillingAddress, StringBuffer newBillingAddress,
            StringBuffer currentPrimaryAddress, StringBuffer newPrimaryAddress,
            Integer prevRptEvenYear, Float prevRptEvenYearTons, String prevRptEvenYearString,
            Collection<SelectItem> fee1PickList, List<EmissionTotal> fer1, 
            List<EmissionTotal> es1, boolean nonAttain1, boolean incluldeES1,
            Integer prevRptOddYear, Float prevRptOddYearTons, String prevRptOddYearString,
            Collection<SelectItem> fee2PickList, List<EmissionTotal> fer2,
            List<EmissionTotal> es2, boolean nonAttain2, boolean incluldeES2,
            String user, boolean inStaging, boolean hideTradeSecret, boolean includeAllAttachments) throws RemoteException {
        List<Document> docList = new ArrayList<Document>();

        TmpDocument ntvRptDoc = new TmpDocument();

        boolean anyTradeSecret = generateTempEmissionReport(facility, ntvRpt, hideTradeSecret,
                currentOwnersAddresses, prevOwnerAddress, newOwnerAddress,
                currentBillingAddress, newBillingAddress,
                currentPrimaryAddress, newPrimaryAddress,
                prevRptEvenYear, prevRptEvenYearTons, prevRptEvenYearString,
                fee1PickList, fer1,
                es1, nonAttain1, incluldeES1,
                prevRptOddYear, prevRptOddYearTons, prevRptOddYearString,
                fee2PickList, fer2,
                es2, nonAttain2, incluldeES2, user, ntvRptDoc);
        
        if(hideTradeSecret) {  // Only provide public document if requested.
            docList.add(ntvRptDoc);
        }

        if(!hideTradeSecret && !anyTradeSecret) return null;  // returning null indicates wanted trade secret and none were.
        if (!hideTradeSecret && anyTradeSecret) {
            try {
                PdfGeneratorBase.addTradeSecretWatermarkHorizontal(ntvRptDoc.getPath());
            } catch (IOException ioe) {
                String s = "Got IOException for " + ntvRptDoc.getPath() + " for emissions inventory " + ntvRpt.getProvidedRptId();
                logger.error(s, ioe);
                throw new DAOException(s, ioe);
            } catch (DocumentException de) {
                String s = "Got DocumentException for " + ntvRptDoc.getPath() + " for emissions inventory " + ntvRpt.getProvidedRptId();
                logger.error(s, de);
                throw new DAOException(s, de);
            }
            docList.add(ntvRptDoc);
        }

        boolean readOnly = CompMgr.getAppName().equals(CommonConst.INTERNAL_APP) ? false : !inStaging;
        
        if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
        	readOnly = false;
        }
        Document zipDoc = generateTempAttachmentZipFile(ntvRpt.getProvidedEmInventoryId(), ntvRpt.getPrimary().getAttachments(), hideTradeSecret, facility.getFacilityId(),
            		null, ntvRptDoc, null, null, readOnly, ntvRpt.getPrimary().getRptReceivedStatusDate(), includeAllAttachments);
        if (zipDoc != null) {
        	docList.add(zipDoc);
        }

        return docList;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public FireRow retrieveFireRow(String fireId) throws DAOException {
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO();

        return emissionsRptDAO.retrieveFireRow(fireId);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public FireRow retrieveFireRow(String sccId, String materialCd, int year) throws DAOException {
    	
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO();
        return emissionsRptDAO.retrieveFireRow(sccId, materialCd, year);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public FireRow[] retrieveFireRow(String sccId, String materialCd,
            String pollutantCd, String factor, String formula, String measure,
            String action, int year) throws DAOException {
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO();

        return emissionsRptDAO.retrieveFireRow(sccId, materialCd,
                pollutantCd, factor, formula, measure, action, year);
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public FireRow[] retrieveFireRow(String sccId, String materialCd,
            String pollutantCd, String factor, String formula, String measure,
            String action, int year, boolean omitFactorAndFormula) throws DAOException {
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO();
        
        if (omitFactorAndFormula) {
        	return emissionsRptDAO.retrieveFireRow(sccId, materialCd,
                pollutantCd, measure, action, year);
        } else {
        	return emissionsRptDAO.retrieveFireRow(sccId, materialCd,
                pollutantCd, factor, formula, measure, action, year);
        }
    }

    /**
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void removeInvalidFireRows(int year) throws DAOException {
        Transaction trans = null;
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO();
        emissionsRptDAO.setTransaction(trans);

        try {
            trans = TransactionFactory.createTransaction();
            emissionsRptDAO.removeInvalidFireRows(year);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);  // does throw
        } finally {
            closeTransaction(trans);
        }        
    }

    /**
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void deprecateFire(int date) throws DAOException {
        Transaction trans = null;
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO();
        emissionsRptDAO.setTransaction(trans);

        try {
            trans = TransactionFactory.createTransaction();
            emissionsRptDAO.deprecateFire(date);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);  // does throw
        } finally {
            closeTransaction(trans);
        }                
    }
    
    /**
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void deprecateFire(int date, String sccId, List<String> materialCds, List<String> pollutantCds) throws DAOException {
        Transaction trans = null;
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO();
        emissionsRptDAO.setTransaction(trans);

        try {
            trans = TransactionFactory.createTransaction();
            emissionsRptDAO.deprecateFire(date, sccId, materialCds, pollutantCds);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);  // does throw
        } finally {
            closeTransaction(trans);
        }                
    }

    /**
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Integer activeFireRows() throws DAOException {
        Transaction trans = null;
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO();
        emissionsRptDAO.setTransaction(trans);
        Integer rtn = null;
        DbInteger dbI = null;
        try {
            trans = TransactionFactory.createTransaction();
            dbI = emissionsRptDAO.activeFireRows();
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);  // does throw
        } finally {
            closeTransaction(trans);
        }
        if(dbI != null) {
            rtn = dbI.getCnt();
        }
        return rtn;
    }

    /**
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public FireRow[] deprecatedFireRows(int year) throws DAOException {
        Transaction trans = null;
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO();
        emissionsRptDAO.setTransaction(trans);
        FireRow[] rtn = null;
        try {
            trans = TransactionFactory.createTransaction();
            rtn = emissionsRptDAO.deprecatedFireRows(year);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);  // does throw
        } finally {
            closeTransaction(trans);
        }
        return rtn;
    }

    public Integer materialForScc(String sccId, Integer year) {
        Transaction trans = null;
        Integer rtn = null;
        DbInteger dbI = null;
        try {
            EmissionsReportDAO emissionsRptDAO = emissionsReportDAO();
            emissionsRptDAO.setTransaction(trans);
            trans = TransactionFactory.createTransaction();
            dbI = emissionsRptDAO.materialForScc(sccId, year);
            trans.complete();
        } catch (DAOException de) {
            // ignore the exception
            logger.error("materialForScc() failed for " + sccId + " and " + year, de);
        }
        if(dbI != null) {
            rtn = dbI.getCnt();
        }
        return rtn;
    }

    private boolean generateTempEmissionReport(Facility facility, NtvReport ntvRpt, boolean hideTradeSecret,
            StringBuffer currentOwnersAddresses, StringBuffer prevOwnerAddress,
            StringBuffer newOwnerAddress,
            StringBuffer currentBillingAddress, StringBuffer newBillingAddress,
            StringBuffer currentPrimaryAddress, StringBuffer newPrimaryAddress,
            Integer prevRptEvenYear, Float prevRptEvenYearTons, String prevRptEvenYearString,
            Collection<SelectItem> fee1PickList, List<EmissionTotal> fer1, 
            List<EmissionTotal> es1, boolean nonAttain1, boolean incluldeES1,
            Integer prevRptOddYear, Float prevRptOddYearTons, String prevRptOddYearString,
            Collection<SelectItem> fee2PickList, List<EmissionTotal> fer2,
            List<EmissionTotal> es2, boolean nonAttain2, boolean incluldeES2,
            String user, TmpDocument rptDoc) throws RemoteException {
    	boolean containsTS = false;
        try {
            // Set the path elements of the temp doc.
            rptDoc.setDescription("Emissions Inventory" + (!hideTradeSecret?" (trade secret)": ""));
            rptDoc.setFacilityID(facility.getFacilityId());
            rptDoc.setTemporary(true);
            rptDoc.setTmpFileName("EmissionsReport_" + ntvRpt.getPrimary().getEmissionsRptId() + (!hideTradeSecret?"_TS": "") + ".pdf");
            // the items below are not needed since this document data is not
            // stored in the database
            // appDoc.setLastModifiedBy();
            // appDoc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
            // appDoc.setUploadDate(appDoc.getLastModifiedTS());
            DocumentUtil.mkDirs(rptDoc.getDirName());
            OutputStream os = DocumentUtil.createDocumentStream(rptDoc.getPath());
            containsTS = writeEmissionReportToStream(facility, ntvRpt,
                    currentOwnersAddresses, prevOwnerAddress, newOwnerAddress,
                    currentBillingAddress, newBillingAddress,
                    currentPrimaryAddress, newPrimaryAddress,
                    prevRptEvenYear, prevRptEvenYearTons, prevRptEvenYearString,
                    fee1PickList, fer1,
                    es1, nonAttain1, incluldeES1,
                    prevRptOddYear, prevRptOddYearTons, prevRptOddYearString,
                    fee2PickList, fer2,
                    es2, nonAttain2, incluldeES2, os);
            os.close();

        } catch (Exception ex) {
            logger.error("Cannot generate NTV emissions inventory for facility " + facility.getFacilityId(), ex);
            throw new DAOException("Cannot generate NTV emissions inventory for facility " + facility.getFacilityId(), ex);
        }

        return containsTS;
    }

    private boolean writeEmissionReportToStream(Facility facility, NtvReport ntvRpt,
            StringBuffer currentOwnersAddresses, StringBuffer prevOwnerAddress, StringBuffer newOwnerAddress,
            StringBuffer currentBillingAddress, StringBuffer newBillingAddress,
            StringBuffer currentPrimaryAddress, StringBuffer newPrimaryAddress,
            Integer prevRptEvenYear, Float prevRptEvenYearTons, String prevRptEvenYearString,
            Collection<SelectItem> fee1PickList, List<EmissionTotal> fer1, 
            List<EmissionTotal> es1, boolean nonAttain1, boolean incluldeES1,
            Integer prevRptOddYear, Float prevRptOddYearTons, String prevRptOddYearString,
            Collection<SelectItem> fee2PickList, List<EmissionTotal> fer2,
            List<EmissionTotal> es2, boolean nonAttain2, boolean incluldeES2, 
            OutputStream os) throws IOException {
        try {
            //make sure we have all Facility information
            NtvEmissionsReportPdfGenerator generator = new NtvEmissionsReportPdfGenerator();
            return generator.generatePdf(facility, ntvRpt,
                    currentOwnersAddresses, prevOwnerAddress, newOwnerAddress,
                    currentBillingAddress, newBillingAddress,
                    currentPrimaryAddress, newPrimaryAddress,
                    prevRptEvenYear, prevRptEvenYearTons, prevRptEvenYearString,
                    fee1PickList, fer1,
                    es1, nonAttain1, incluldeES1,
                    prevRptOddYear, prevRptOddYearTons, prevRptOddYearString,
                    fee2PickList, fer2,
                    es2, nonAttain2, incluldeES2, os);
        } catch (DocumentException e) {
            logger.error("Exception writing emission report to stream for report " + ntvRpt.getPrimary().getEmissionsRptId(), e);
            throw new IOException("Exception writing emission report to stream for report " + ntvRpt.getPrimary().getEmissionsRptId());
        }
    }
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public String getDAPCAttestationMessage(String permitClassCd, EmissionsReport report) {
        String message = "";
        if (permitClassCd != null) {
            if (PermitClassDef.TV.equals(permitClassCd)) {
                message = SystemPropertyDef.getSystemPropertyValue("TitleVFacilityERAttestationMessage", null);
            } else if (PermitClassDef.SMTV.equals(permitClassCd)){ 
                message = SystemPropertyDef.getSystemPropertyValue("SMTVERAttestationMessage", null);
            } else if (PermitClassDef.NTV.equals(permitClassCd)) {
                message = SystemPropertyDef.getSystemPropertyValue("NTVERAttestationMessage", null);
            }
        }
        if (message == null) {
            message = "";
        }
        
        // For IMPACT, intentionally removed ConfidentialAttestationMessage when submitting
        // Emissions Inventories.
        //String confidentialMsg = getParameter("ConfidentialAttestationMessage");
        String confidentialMsg = null;
    	if (confidentialMsg == null) {
    		confidentialMsg = "";
        }
    	message = confidentialMsg + message;
        return message;
    }

    /**
     * Return pdf file RO can sign as attestation document.
     * @param repor
     * @param facility
     * @return
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public EmissionsDocument createEmissionsAttestationDocument(EmissionsReport report, Facility facility) throws DAOException {
        EmissionsDocument erDoc = null;
        try {
            erDoc = getEmissionsAttestationDocument(report, facility.getFacilityId());
            OutputStream os = DocumentUtil.createDocumentStream(erDoc.getPath());
            try {

                EmissionsReportPdfGenerator generator = new EmissionsReportPdfGenerator();
                generator.setAttestationOnly(true);
                generator.generatePdf(facility, report, null, false, false, os, false, true);
            }  catch (DocumentException e) {
                logger.error(e.getMessage(), e);
                throw new IOException("Exception writing emissions inventory to stream for emissions inventory " + report.getEmissionsRptId());
            } 
        } catch (IOException e) {
            throw new DAOException("Exception getting emissions inventory as stream for emissions inventory " + report.getEmissionsRptId(), e);
        }
        return erDoc;
    }

    private EmissionsDocument getEmissionsAttestationDocument(EmissionsReport report, String facilityId) {
        EmissionsDocument erDoc = new EmissionsDocument();
        erDoc.setFacilityID(facilityId);
        erDoc.setEmissionsRptId(report.getEmissionsRptId());
        erDoc.setTemporary(true);
        erDoc.setOverloadFileName("AttestationForER" + report.getEmissionsRptId() + ".pdf");
        erDoc.setDescription("Attestation Document for Emissions Inventory " + report.getEmissionsRptId());
        return erDoc;
    }

    /**
     * Add attestation document to application 
     * @param app
     * @param attestationDoc
     * @param trans
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void addAttestationDocument(EmissionsReport report, EmissionsDocument attestationDoc, Transaction trans) throws DAOException {
        EmissionsReportDAO emissionsReportDAO = emissionsReportDAO(trans);
        EmissionsDocumentRef docRef = new EmissionsDocumentRef();
        docRef.setEmissionsRptId(report.getEmissionsRptId());
        docRef.setDocumentId(attestationDoc.getDocumentID());
        docRef.setEmissionsDocumentTypeCD(EmissionsAttachmentTypeDef.RO_SIGNATURE);
        emissionsReportDAO.createEmissionsDocument(docRef);
        // need to set publicDoc so document info is copied on internal server
        docRef.setPublicDoc(attestationDoc);
        report.addAttachment(docRef);
    }

    /**
     * 
     * @param report
     * @param trans
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void removeAttestationDocument(EmissionsReport report, Transaction trans) throws DAOException {
        for (EmissionsDocumentRef docRef : report.getAttachments()) {
            if (EmissionsAttachmentTypeDef.RO_SIGNATURE.equals(docRef.getEmissionsDocumentTypeCD())) {
                removeEmissionsDocument(docRef);
            }
        }
    }

    /**
     * 
     * @param task
     * @throws DAOException
     * @throws IOException
     * @throws FileNotFoundException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void addSubmissionAttachments(Task task) throws DAOException {
        EmissionsReport report = task.getReport();

        if (task.getNtvReport() != null) {
            report = task.getNtvReport().getPrimary();
        }

        // load document data into application
        // this needs to be done so Document data can be sent
        // to the internal application
        loadAllDocuments(report, true);

        // don't want duplicate attachments
        task.getAttachments().clear();

        // add zip file with application attachments to task for submission
        if (report.hasNonXAttachments()) {
            logger.debug("zipping emissions inventory attachments...");
            Document zipDoc = generateTempAttachmentZipFile(report.getEmissionsInventoryId(), report.getAttachments(), false, task.getFacilityId(), null, null, null, null, false, report.getRptReceivedStatusDate(), true);
            if(zipDoc != null) {
            	us.oh.state.epa.portal.datasubmit.Attachment submitAttachment = new us.oh.state.epa.portal.datasubmit.Attachment(report.getEmissionsRptId().toString(),
                        "text/zip", DocumentUtil.getFileStoreRootPath() + zipDoc.getPath(), null, null);
                submitAttachment.setSystemFilename(DocumentUtil.getFileStoreRootPath() + zipDoc.getPath());
                task.getAttachments().add(submitAttachment);
            }
        }
    }

    /**
     * @param facilityId
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public FacilityPermitInfo[] retrieveFacilityPermitInfo(String facilityId) throws DAOException {
        FacilityPermitInfo[] rtn = null;
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO();
        rtn =  emissionsRptDAO.retrieveFacilityPermitInfo(facilityId);
        IntegerPair[] pairs = emissionsRptDAO.retriveFacilityPermitAppInfo(facilityId);
        // add in the applications that go with the permit.
        for(FacilityPermitInfo fpi : rtn) {
            ArrayList<Integer>  ids = new ArrayList<Integer>();
            for(IntegerPair ip : pairs) {
                if(fpi.getPermitId().equals(ip.getA())) {
                    ids.add(ip.getB());
                }
            }
            fpi.setApplicationIds(ids);
        }
        return rtn;
    }
    
    /**
     * @param facilityId
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public FacilityAppInfo[] retrieveFacilityAppInfo(String facilityId) throws DAOException {
        FacilityAppInfo[] rtn = null;
        EmissionsReportDAO emissionsRptDAO = emissionsReportDAO();
        rtn =  emissionsRptDAO.retrieveFacilityAppInfo(facilityId);
        return rtn;
    }
    
    public String generateTempDocument(ReportProfileBase rp, EmissionsReport report, Facility facility, TemplateDocument templateDoc){  
    	String docURL=null;
    	try{
	    	//EmissionsReportDAO emissionsRptDAO = emissionsReportDAO();
			
	    	DocumentGenerationBean dgb = new DocumentGenerationBean();
			dgb.setEmissionsReport(report);
			dgb.setFacility(facility);
			dgb.setReportProfile(rp);
	        
	
			//DocumentUtil.generateDocument(templateDoc.getPath(), dgb,
				//	doc.getPath());
			String desFilePath=null;
			if(!templateDoc.getBasePath().endsWith(".docx")){
				desFilePath = templateDoc.getBasePath() +"Facilities\\"+ facility.getFacilityId() +"\\Correspondence\\" +templateDoc.getTemplateDocTypeDsc() +".docx";
			}//else{
				//desFilePath = doc.getPath().substring(0,doc.getPath().lastIndexOf("\\")+1) + templateDoc.getTemplateDocTypeDsc() +".docx";
			//}
			docURL = DocumentUtil.generateAsposeDocument(templateDoc.getTemplateDocPath(), dgb, desFilePath);	
			
			
    	}catch(Exception e){
    		logger.debug(e.getMessage());
    	}
		
    	 return docURL;
    	    	
    }
    
    public EmissionsReportNote createNote(EmissionsReportNote note)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		EmissionsReportNote ret = null;

		try {
			ret = createNote(note, trans);

			if (ret != null) {
				trans.complete();
			} else {
				trans.cancel();
				logger.error("Failed to insert emissions inventory note");
			}
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @param note
	 * @param trans
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	private EmissionsReportNote createNote(EmissionsReportNote note,
			Transaction trans) throws DAOException {
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		EmissionsReportDAO erDAO = emissionsReportDAO(trans);

		Note tempNote = infraDAO.createNote(note);
		if (tempNote != null) {
			note.setNoteId(tempNote.getNoteId());
			erDAO.addReportNote(note);
		} else {
			note = null;
		}
		return note;
	}

	/**
	 * 
	 * @return boolean <tt>true</tt> if a record was updated.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyNote(EmissionsReportNote note) throws DAOException {
		return infrastructureDAO().modifyNote(note);
	}    
	
	/**
	 * @param note
	 * @param trans
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	private boolean removeNote(EmissionsReportNote note, Transaction trans)
			throws DAOException {
		boolean ret = false;
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		EmissionsReportDAO erDAO = emissionsReportDAO(trans);

		ret = erDAO.removeNote(note.getEmissionsRptId(), note.getNoteId());
		if (ret) {
			ret = infraDAO.removeNote(note.getNoteId());
		}

		return ret;
	}
	
	/**
	 * @param note
	 * @param trans
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportPeriod[] retrieveEmissionPeriods(Integer reportId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException{
		return emissionsReportDAO().retrieveEmissionPeriods(reportId);
	}
	
	public us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee[] getFeeinfo(Integer feeId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException{
		return  emissionsReportDAO().retrieveFeeinfo(feeId);
	}
	
	public String[] getSuperCd(String pollutantCd)
			throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException{
		return  emissionsReportDAO().retrieveSuperCd(pollutantCd);
	}
	
	/**
	 * Set the "validated" flag for <code>report</code> to <code>validated</code>.
	 * This method exists to avoid a full update of all emissions report attributes
	 * when all that is needed is to set the validated flag.
	 * 
	 * @param report
	 *            the emissions report
	 * @param validated
	 *            <code>true</code> or <code>false</code> to indicate whether
	 *            the emissions report is validated.
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean setValidatedFlag(EmissionsReport report, boolean validated) throws DAOException {
		Transaction trans = null;
		boolean ret = false;

		try {
			trans = TransactionFactory.createTransaction();
			setValidatedFlag(report, validated, trans);
			trans.complete();
			ret = true;
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}
	
	private void setValidatedFlag(EmissionsReport report, boolean validated, Transaction trans) throws DAOException {
		EmissionsReportDAO reportDAO = emissionsReportDAO(trans);
		report.setValidated(validated);
		reportDAO.setEmissionsReportValidatedFlag(report.getEmissionsRptId(), validated);
	}

	@Override
	public List<EmissionCalcMethod> retrieveRecommendedMethods(
			EmissionRow line, EmissionsReport report, Facility facility, EmissionsReportPeriod period) {

		List<EmissionCalcMethod> ret = new ArrayList<EmissionCalcMethod>();
		String pollutantCd = line.getPollutantCd();
		String pollutantDsc = line.getPollutant();
		String sccId = line.getP().getSccId();
		Integer reportingYear = report.getReportYear();
		
		boolean hasCem = false;
		boolean hasStackTest = false;
		boolean isBilledByAllowable = false;
		boolean isAllowable = false;

		// Get service catalog and allowable pollutant
		try {
			ReportTemplates rt = retrieveSCEmissionsReports(report.getReportYear(),
					report.getContentTypeCd(),
					facility.getFacilityId());
			SCEmissionsReport catalog = rt.getSc();
			for(SCPollutant pollutant : catalog.getPollutants()) {
				if(pollutant.getPollutantCd().equalsIgnoreCase(pollutantCd)) {
					isBilledByAllowable = pollutant.isBilledBasedOnPermitted();
					break;
				}
			}
		} catch (RemoteException e) {
			logger.error("Could not retrieve catalog. Exception: "
					+ e.getMessage());
			return ret;
		}

		// Gather facility inventory objects
		EmissionsReportEU eiEU = null;
		for (EmissionsReportEU eu : report.getEus()) {
			for(EmissionsReportPeriod euPeriod : eu.getPeriods()) {
				if (euPeriod.getEmissionPeriodId().equals(
						period.getEmissionPeriodId())) {
					eiEU = eu;
					break;
				}
			}
		}

		EmissionUnit facEU = null;
		if (eiEU != null) {
			facEU = facility.getMatchingEmissionUnit(eiEU.getCorrEpaEmuId());
		}

		EmissionProcess facEP = null;
		for (EmissionProcess ep : facEU.getEmissionProcesses()) {
			if (ep.getSccId().equals(sccId)) {
				facEP = ep;
			}
		}

		// check for CEM
		//hasCem = facEP.checkForMonitoredPollutant(pollutantCd, reportingYear);
		//check facility._facilityContinuousMonitorList to whether there is monitor associated with that EU-->process
		checkMonitor:
		for (ContinuousMonitor monitor:facility.getContinuousMonitorList()){
			for (Integer euId: monitor.getAssociatedFpEuIds()){
				if (facEU.getEmuId().equals(euId)){
					hasCem = true;
					break checkMonitor;
				}
			}
		}
		
		
		// check for Stack Test
		try {

			StackTest[] stackTests = stackTestService.searchStackTests(facility
					.getFacilityId());
			if (stackTests != null && stackTests.length > 0) {
				stackTestSearch: for (StackTest stackTest : stackTests) {
					// Must be submitted
					if (stackTest.getEmissionTestState() == null
							|| !stackTest.getEmissionTestState()
									.equalsIgnoreCase(
											EmissionsTestStateDef.SUBMITTED)) {
						continue;
					}
					
					// Cannot be portable analyzer
					if (stackTest.getTestingMethodCd() == null
							|| stackTest
									.getTestingMethodCd()
									.equalsIgnoreCase(
											CetaStackTestTestingMethodDef.PORTABLE_ANALYZER)) {
						continue;
					}

					// Cannot be portable analyzer
					if (stackTest.getStackTestMethodCd() == null
							|| stackTest
									.getStackTestMethodCd()
									.equalsIgnoreCase(
											CetaStackTestMethodDef.PORTABLE_ANALYZER)) {
						continue;
					}
					
					stackTest = stackTestService
							.retrieveBasicStackTestWithPollutants(
									stackTest.getId(), facility);
					if (stackTest == null) {
						continue;
					}
					
					
					// check to see if stack test has test dates within reporting year
					boolean isInReportingYear = false;
					for(TestVisitDate tvd : stackTest.getVisitDates()) {
						if(tvd.getTestDate() == null) {
							continue;
						}
						Calendar cal = Calendar.getInstance();
						cal.setTime(new Date(tvd.getTestDate().getTime()));
						int testYear = cal.get(Calendar.YEAR);
						if(testYear == reportingYear) {
							isInReportingYear = true;
							break;
						}
					}
					
					if(!isInReportingYear) {
						continue;
					}
					
					// check that stack test contains process
					boolean testedEU = false;
					for (TestedEmissionsUnit tEU : stackTest
							.getTestedEmissionsUnits()) {
						if (tEU.getEu().getCorrEpaEmuId()
								.equals(facEU.getCorrEpaEmuId())
								&& tEU.getSccs().equalsIgnoreCase(sccId)) {
							testedEU = true;
						} 
					}
					
					if(!testedEU) {
						continue;
					}

					// check for the tested pollutant
					for (StackTestedPollutant testedPollutant : stackTest
							.getTestedPollutants()) {
						if (testedPollutant.getPollutantCd() != null && testedPollutant.getPollutantCd()
										.equalsIgnoreCase(pollutantCd)) {
							
							hasStackTest = true;
							break stackTestSearch;
						}
					}
				}
			}
 
		} catch(RemoteException re) {
            logger.error("retrieveRecommendedMethods() failed for " + facility.getFacilityId(), re);
            return ret;
        }
		
		// check for allowable rate
		if (isBilledByAllowable) {
			for (EuEmission permittedEmission : facEU.getEuEmissions()) {
				if (permittedEmission.getPollutantCd().equalsIgnoreCase(
						pollutantCd) && 
						(permittedEmission.getAllowableEmissionsLbsHour() != null ||
						permittedEmission.getAllowableEmissionsTonsYear() != null)) {
					isAllowable = true;
					break;
				}
			}
		}

		// setup recommended methods
		List<EmissionCalcMethodDef> methods = EmissionCalcMethodDef
				.retrieveMethods();
		for (EmissionCalcMethodDef method : methods) {
			EmissionCalcMethod recMethod = new EmissionCalcMethod();
			recMethod.setMethodCd(method.getCode());
			recMethod.setMethodDsc(method.getDescription());
			recMethod.setPriority(method.getAccuracy());

			if (method.getCode().equalsIgnoreCase(
					EmissionCalcMethodDef.SCCEmissionsTimeBasedFactorCEM)) {
				if (hasCem) {
					// recommend CEM
					recMethod.setRecommended(true);
					recMethod
							.setReason(method.getDescription()
									+ " is recommended because pollutant is being monitored by a CEM.");
				} else {
					recMethod.setRecommended(false);
					recMethod
							.setReason(method.getDescription()
									+ " is not recommended because pollutant is not being monitored by a CEM.");
				}
			} else if (method.getCode().equalsIgnoreCase(
					EmissionCalcMethodDef.SCCEmissionsTimeBasedFactorStackTest)) {
				if (hasCem) {
					// has stack test; however, also has CEM
					recMethod.setRecommended(false);
					recMethod
							.setReason(method.getDescription()
									+ " is not recommended because pollutant is being monitored by a CEM.");
				} else if (hasStackTest) {
					// recommend Stack Test
					recMethod.setRecommended(true);
					recMethod
							.setReason(method.getDescription()
									+ " is recommended because pollutant was tested by Stack Test.");
				} else {
					recMethod.setRecommended(false);
					recMethod
							.setReason(method.getDescription()
									+ " is not recommended because pollutant was not tested by Stack Test.");
				}
			} else if (method.getCode().equalsIgnoreCase(
					EmissionCalcMethodDef.AQDGenerated)) {
				recMethod.setRecommended(false);
				recMethod.setReason(method.getDescription() 
						+ " is available for IMPACT system generated use only.");
			} else {
				if (hasCem) {
					recMethod.setRecommended(false);
					recMethod
							.setReason(method.getDescription()
									+ " is not recommended because pollutant is being monitored by a CEM. Since the pollutant is being monitored by a CEM, it is recommended to use the CEM calculation method.");
				} else if (hasStackTest) {
					recMethod.setRecommended(false);
					recMethod
							.setReason(method.getDescription()
									+ " is not recommended because pollutant was tested by Stack Test. Since the pollutant was tested by a Stack Test, it is recommended to use the Stack Test calculation method.");
				} else {
					// recommend allowable, estimated, emissions,
					// throughput-based factor
					recMethod.setRecommended(true);

					if (method
							.getCode()
							.equalsIgnoreCase(
									EmissionCalcMethodDef.SCCEmissionsTimeBasedFactorAllowable)) {
						recMethod
								.setReason(method.getDescription()
										+ " is recommended if pollutant allowable rate is known and more accurate methods for calculation are unavailable.");
					} else if (method
							.getCode()
							.equalsIgnoreCase(
									EmissionCalcMethodDef.SCCEmissionsTimeBasedFactorEstimated)) {
						recMethod
								.setReason(method.getDescription()
										+ " is recommended if pollutant manufacturer data is known for estimation and more accurate methods for calculation are unavailable.");
					} else if (method.getCode().equalsIgnoreCase(
							EmissionCalcMethodDef.SCCEmissionsFactor)) {
						recMethod
								.setReason(method.getDescription()
										+ " is recommended if pollutant EPA WebFIRE factor is known and more accurate methods for calculation are unavailable.");
					} else if (method.getCode().equalsIgnoreCase(
							EmissionCalcMethodDef.SCCEmissions)) {
						recMethod
								.setReason(method.getDescription()
										+ " is recommended if pollutant emissions are known and more accurate methods for calculation are unavailable.");
					}
				}
			}

			ret.add(recMethod);
		}

		Collections.sort(ret, Collections.reverseOrder());
		
		return ret;
	}
	
	public boolean generateXML(UsEpaEisReport report) throws DAOException {
		EmissionsReportDAO emissionsReportDAO = emissionsReportDAO();
		
		return emissionsReportDAO.generateFacilityInventoryXML(report) &&
				emissionsReportDAO.generateEmissionsInventoryXML(report);
	}
	
	
	/**
	 * Compute total reported emissions and total chargeable emissions.
	 * 
	 * @param report
	 * @param scReports
	 * @param submitted
	 * 
	 * @return none
	 * 
	 * @throws DAOException
	 * @throws ApplicationException
	 */
	public void updateTotalEmissions(EmissionsReport report, ReportTemplates scReports, boolean submitted)
			throws DAOException, ApplicationException {

		EmissionsReportDAO emissionsReportDAO = emissionsReportDAO(getSchema(true));
		ArrayList<EmissionRow> emissions = new ArrayList<EmissionRow>();
		ArrayList<EmissionRow> scNonChargeableEmissions = new ArrayList<EmissionRow>();

		double totalEmissions = 0d;
		double totalReportedEmissions = 0d;

		// Roll-up emissions for each pollutant.
		emissions = EmissionRow.getEmissions(report, false, submitted, 1, scReports, logger, false);

		if (emissions != null && emissions.size() != 0) {

			// Get the list of non-chargeable pollutants from the service catalog.
			SCNonChargePollutant nonCharPollutants[] = scReports.getSc().getNcPollutants();

			for (int i = 0; i < nonCharPollutants.length; i++) {
				for (EmissionRow er : emissions) {
					if (nonCharPollutants[i].getPollutantCd().equalsIgnoreCase(er.getPollutantCd())) {
						scNonChargeableEmissions.add(er);
					}
				}
			}

			for (EmissionRow er : scNonChargeableEmissions) {
				emissions.remove(er);
			}

			// Compute total reported emissions and total chargeable emissions.
			for (EmissionRow er : emissions) {
				if (null != er.getStackEmissions() || null != er.getFugitiveEmissions()) {
					double s = 0f;
					double f = 0f;
					if (null != er.getStackEmissions()) {
						s = EmissionUnitReportingDef.convert(er.getEmissionsUnitNumerator(), er.getStackEmissionsV(),
								EmissionUnitReportingDef.TONS);
					}
					if (null != er.getFugitiveEmissions()) {
						f = EmissionUnitReportingDef.convert(er.getEmissionsUnitNumerator(), er.getFugitiveEmissionsV(),
								EmissionUnitReportingDef.TONS);
					}
					totalReportedEmissions += s + f;
					if (er.isChargeable()) {
						totalEmissions += s + f;
					}
				}
			}
		}

		report.setTotalEmissions((float) totalEmissions);
		report.setTotalReportedEmissions((float) totalReportedEmissions);

		emissionsReportDAO.updateTotalEmissions(report);
	}

	@Override
	public List<ReportTemplates> retrieveAssociatedSCEmissionsReports(
			EmissionsReport report, boolean staging) throws DAOException {
		List<ReportTemplates> templates = new ArrayList<ReportTemplates>();
		List<SCEmissionsReport> scEmissionsReports = 
				emissionsReportDAO(getSchema(staging)).retrieveAssociatedSCEmissionsReports(report);
		
		ServiceCatalogDAO serviceCatalogDao = serviceCatalogDAO(getSchema(staging));

		for (SCEmissionsReport sc : scEmissionsReports) {
            sc.setFees(serviceCatalogDAO().retrieveReportFees(sc.getId()));
            sc.setNcPollutants(serviceCatalogDAO().retrieveNonChargePollutants(sc.getId()));
            
            List<SCDataImportPollutant> scDataImportPollutantList = new ArrayList<SCDataImportPollutant>(Arrays.asList(serviceCatalogDAO().retrieveDataImportPollutants(sc.getId())));    
            sc.setDataImportPollutantList(scDataImportPollutantList);
            
			sc.setPermitClassCds(serviceCatalogDAO().retrievePermitClassCds(sc.getId()));
    		sc.setFacilityTypeCds(serviceCatalogDAO().retrieveFacilityTypeCds(sc.getId()));

    		ReportTemplates t = new ReportTemplates();
    		t.setSc(sc);
    		templates.add(t);
        }
		return templates;
	}

	@Override
	public List<ReportTemplates> retrieveAssociatedSCEmissionsReports(
			EmissionsReport report) throws DAOException {
		return retrieveAssociatedSCEmissionsReports(report,false);
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void deleteEmissionsRptInfo(String facilityId, Integer id)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(trans);

		emissionsRptDAO.deleteEmissionsRptInfo(facilityId, id);

	}

	public Integer emissionsInventoriesForFacilityAndServiceCatalog(
			String facilityId, Integer serviceCatalogId) {
		Transaction trans = null;
		Integer rtn = null;
		DbInteger dbI = null;
		try {
			EmissionsReportDAO emissionsRptDAO = emissionsReportDAO();
			emissionsRptDAO.setTransaction(trans);
			trans = TransactionFactory.createTransaction();
			dbI = emissionsRptDAO
					.emissionsInventoriesForFacilityAndServiceCatalog(
							facilityId, serviceCatalogId);
			trans.complete();
		} catch (DAOException de) {
			// ignore the exception
			logger.error(
					"emissionsInventoriesForFacilityAndServiceCatalog() failed for "
							+ facilityId + " and " + serviceCatalogId, de);
		}
		if (dbI != null) {
			rtn = dbI.getCnt();
		}
		return rtn;
	}
	
	private void updateEmissionsRptInfos(EmissionsReportDAO emissionsRptDAO,
			String facilityId, Integer emissionsReportId, String state)
			throws DAOException {
		EmissionsRptInfo[] infos = emissionsRptDAO.retrieveEmissionsRptInfos2(
				facilityId, emissionsReportId);
		for (EmissionsRptInfo info : infos) {
			if (null != info) {
				info.setState(state);
				emissionsRptDAO.modifyEmissionsRptInfo(facilityId, info);
			}
		}
	}
	
	/**
	 * Returns all of the materials currently defined in the FIRE Factor table for the
	 * SCC.
	 * 
	 * @return SimpleDef[] All materials associated with this SCC.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleDef[] retrieveSccMaterials(String sccId)
			throws DAOException {
		return emissionsReportDAO().retrieveSccMaterials(sccId);
	}
	
	/**
	 * Returns all of the pollutants currently defined in the FIRE Factor table for the
	 * SCC.
	 * 
	 * @return SimpleDef[] All pollutants associated with this SCC.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleDef[] retrieveSccPollutants(String sccId)
			throws DAOException {
		return emissionsReportDAO().retrieveSccPollutants(sccId);
	}
	
	/**
	 * Returns all of the pollutants currently defined in the FIRE Factor table for the
	 * SCC.
	 * 
	 * @return SimpleDef[] All pollutants associated with this SCC.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleDef[] retrieveSccPollutants(String sccId, List<String> materialCds)
			throws DAOException {
		return emissionsReportDAO().retrieveSccPollutants(sccId, materialCds);
	}

	@Override
	public EmissionsDocumentRef retrieveReportDocumentByTradeSecrectDocId(Integer tradeSecretDocId)
			throws DAOException {
		return emissionsReportDAO().retrieveReportDocumentByTradeSecrectDocId(tradeSecretDocId);
	}

	@Override
	public EmissionsReport retrieveEmissionsReportById(Integer reportId) throws DAOException {
		return emissionsReportDAO().retrieveEmissionsReport(reportId);
	}

	@Override
	public Attachment createEiDataImportAttachment(EiDataImportCriteria importCriteria, Attachment publicAttachment,
			InputStream publicInputStream, Attachment tsAttachment, InputStream tsInputStream) throws DAOException {
		
		try {
			Integer docId = infrastructureDAO().nextSequenceIdValue("S_Document_Id");
			publicAttachment.setDocumentID(docId);
			publicAttachment.setLastModified(1);

			DocumentUtil.createDocument(publicAttachment.getPath(), publicInputStream);
			
			if (tsAttachment != null) {
				createEiDataImportTradeSecretAttachment(publicAttachment, tsAttachment, tsInputStream);
			}
			importCriteria.getAttachmentDocs().add(publicAttachment);
			
		} catch (IOException ioe) {
			throw new DAOException(ioe.getMessage(), ioe);
		}
		
		return publicAttachment;
	}
	
	@Override
	public List<EmissionsReport> createEmissionsReportFromCSV(final EiDataImport eiDataImport)
			throws DAOException, ApplicationException {

		ArrayList<EmissionsReport> reportList = new ArrayList<EmissionsReport>();
		EiDataImportCriteria eiDataCriteria = eiDataImport.getImportCriteria();
		Integer reportYear = eiDataCriteria.getEiImportYear();
		HashMap<String, FireRow[]> fireMap = new HashMap<String, FireRow[]>();
		Map<Integer, Facility> facilityInfo = eiDataImport.getFacilityInfo();

		InfrastructureDAO infraDAO = infrastructureDAO(getSchema(true));
		FacilityDAO facilityDAO = facilityDAO(getSchema(false));
		EmissionsReportDAO emissionsRptDAO = emissionsReportDAO(getSchema(false));

		Map<Integer, ArrayList<EiDataImportPeriod>> dataImportByFacility = eiDataImport.getDataImportRowsByFacility();
		for (Integer facId : dataImportByFacility.keySet()) {

			Facility facility = facilityInfo.get(facId);
			int error = 1;
			
			HashMap<Integer, EmissionUnit> notReportableEus = new HashMap<Integer, EmissionUnit>();
			HashMap<Integer, EmissionUnit> reportableEus = new HashMap<Integer, EmissionUnit>();
			HashMap<Integer, EmissionUnit> reportedEus = new HashMap<Integer, EmissionUnit>();
			HashMap<String, EmissionProcess> processList = new HashMap<String, EmissionProcess>();
			for (EmissionUnit eu : facility.getEmissionUnits()) {
				if (!reportable(eu, reportYear, eiDataCriteria.getEiImportContentTypeCd())) {
					notReportableEus.put(eu.getCorrEpaEmuId(), eu);
				} else {
					reportableEus.put(eu.getCorrEpaEmuId(), eu);
					for (EmissionProcess eProc : eu.getEmissionProcesses()) {
						processList.put(eu.getEpaEmuId() + ":" + eProc.getProcessId(), eProc);
					}
				}
			}
			
			EmissionsReport report = new EmissionsReport();
			report.setFpId(facId);
			report.setFacilityId(facility.getFacilityId());
			report.setFacility(facility);
			report.setEisStatusCd(ReportEisStatusDef.SUBMITTED);
			report.setRptReceivedStatusCd(ReportReceivedStatusDef.DOLAA_APPROVED);
			Timestamp nowTs = new Timestamp(System.currentTimeMillis());
			report.setRptReceivedStatusDate(nowTs);
			report.setRptApprovedStatusDate(nowTs);
			report.setReceiveDate(null);
			report.setReportYear(reportYear);
			report.setSubmitterUser(eiDataImport.getCurrentUserId());
			report.setNewOwner(false);
			report.setLegacy(false);
			report.setLastModified(1);
			report.setAutoGenerated(true);
			report.setValidated(false);
			report.setFromCSV(true);

			HashMap<String, ValidationMessage> msgs = report.getValidationMessages();

			List<ReportTemplates> serviceCatalogs = new ArrayList<ReportTemplates>();
			ReportTemplates rptt = new ReportTemplates();
			rptt.setSc(eiDataImport.getReport());
			serviceCatalogs.add(rptt);
			report.setServiceCatalogs(serviceCatalogs);
			report.setCreateContentTypeCd(eiDataCriteria.getEiImportContentTypeCd());
			if (rptt.getSc() != null && rptt.getSc().getFeeFirstHalf() != null) {
				report.setFeeId(rptt.getSc().getFeeFirstHalf().getFeeId());
			}
			if (rptt.getSc() != null && rptt.getSc().getFeeSecondHalf() != null) {
				report.setFeeId2(rptt.getSc().getFeeSecondHalf().getFeeId());
			}

			ArrayList<EmissionsReportEU> euList = new ArrayList<EmissionsReportEU>();
			HashMap<Integer, ArrayList<EmissionsReportPeriod>> euMap = new HashMap<Integer, ArrayList<EmissionsReportPeriod>>();

			ArrayList<EiDataImportPeriod> periodList = dataImportByFacility.get(facId);
			HashMap<String, EmissionRow> emissionsTotals = new HashMap<String, EmissionRow>();

			for (EiDataImportPeriod period : periodList) {

				EmissionUnit eu = notReportableEus.get(period.getCorrEpaEmuId());
				if (eu != null) {
					// !reportable but CSV sent data anyway. Flag as error and skip.
					if (reportedEus.get(period.getCorrEpaEmuId()) != null) {
						// But we have already reported the error for this EU.
						continue;
					}
					// Otherwise flag as new error.
					ValidationMessage vMsg = new ValidationMessage("Facility Id " + report.getFacilityId(),
							"Non-reportable EU " + eu.getEpaEmuId() + " included in CSV file.", 
							ValidationMessage.Severity.ERROR);
					msgs.put("Facility Id " + report.getFacilityId() + ":" + error++, vMsg);
					reportedEus.put(period.getCorrEpaEmuId(), eu);
					continue;
				}

				eu = reportableEus.get(period.getCorrEpaEmuId());
				if (eu != null) {
					reportedEus.put(period.getCorrEpaEmuId(), eu);
				}
				
				ArrayList<EmissionsReportPeriod> erpList = euMap.get(period.getCorrEpaEmuId());
				if (erpList == null) {
					erpList = new ArrayList<EmissionsReportPeriod>();
					euMap.put(period.getCorrEpaEmuId(), erpList);
				}

				EmissionsReportPeriod erp = new EmissionsReportPeriod();
				erp.setTradeSecretS(false);
				erp.setWinterThroughputPct(period.getWinterPct());
				erp.setSpringThroughputPct(period.getSpringPct());
				erp.setSummerThroughputPct(period.getSummerPct());
				erp.setFallThroughputPct(period.getFallPct());
				erp.setDaysPerWeek(period.getMaximumDays());
				erp.setWeeksPerYear(period.getMaximumWeeks());
				erp.setHoursPerDay(period.getMaximumHours());
				erp.setHoursPerYear(period.getActualHours());
				erp.setFirstHalfHrsOfOperationPct(50);
				erp.setSecondHalfHrsOfOperationPct(50);
				erp.setTreeLabel(period.getProcessId());

				EmissionProcess ep = facilityDAO.retrieveEmissionProcess(facility.getFpId(), period.getProcessId());
				if (ep != null && processList.get(period.getEuId() + ":" + ep.getProcessId()) != null) {
					processList.remove(period.getEuId() + ":" + ep.getProcessId());
				}
				SccCode sccCode = infraDAO.retrieveSccCode(ep.getSccId());
				String materialCd = period.getMaterial();
				String matDesc = MaterialDef.getData().getItems().getDescFromAllItem(period.getMaterial());
				if (materialCd == null) {
					ValidationMessage vMsg = new ValidationMessage("Process Id " + period.getProcessId(),
							"Cannot obtain a material code for material " + period.getMaterial() + ".",
							ValidationMessage.Severity.ERROR);
					msgs.put("Facility Id " + report.getFacilityId() + ":" + error++, vMsg);
					continue;					
				}
				erp.setSccCode(sccCode);

				String actionCd = null;
				String factorId = null;
				String factor = null;
				String formula = null;
				FireRow[] fireRows = fireMap.get(sccCode.getSccId() + ":" + materialCd);
				if (fireRows == null) {
					fireRows = emissionsRptDAO.retrieveFireRows(sccCode.getSccId(), materialCd);
					if (fireRows != null && fireRows.length > 0 && fireRows[0] != null) {
						ArrayList<FireRow> al = new ArrayList<FireRow>();
						for (FireRow fr : fireRows) {
							if (fr.isActive(reportYear)) {
								al.add(fr);
							}
						}
						if (!al.isEmpty()) {
							fireRows = al.toArray(new FireRow[0]);
							fireMap.put(sccCode.getSccId() + ":" + materialCd, fireRows);
						} else {
							ValidationMessage vMsg = new ValidationMessage("Process Id " + period.getProcessId(),
									(matDesc !=null ? matDesc : period.getMaterial()) + " is not a valid material for"
											+ " SCC " + period.getSccId() 
											+ " in the " + reportYear	+ " reporting year.",
									ValidationMessage.Severity.ERROR);
							msgs.put("Facility Id " + report.getFacilityId() + ":" + error++, vMsg);
							continue;
						}
					} else {
						ValidationMessage vMsg = new ValidationMessage("Process Id " + period.getProcessId(),
								(matDesc !=null ? matDesc : period.getMaterial()) + " is not a valid material for"
										+ " SCC " + period.getSccId() 
										+ " in the " + reportYear	+ " reporting year.",
								ValidationMessage.Severity.ERROR);
						msgs.put("Facility Id " + report.getFacilityId() + ":" + error++, vMsg);
						continue;
					}
				}
				erp.setFireRows(fireRows);
				if (fireRows != null && fireRows[0] != null && fireRows[0].isActive(report.getReportYear())) {
					actionCd = fireRows[0].getAction();
					materialCd = fireRows[0].getMeasure();
					factorId = fireRows[0].getFactorId();
					factor = fireRows[0].getFactor();
					formula = fireRows[0].getFormula();
				}

				List<EmissionsMaterialActionUnits> maus = new ArrayList<EmissionsMaterialActionUnits>();

				EmissionsMaterialActionUnits emau = new EmissionsMaterialActionUnits();
				emau.setMaterial(period.getMaterial());
				emau.setAction(actionCd);
				emau.setMeasure(materialCd);
				emau.setThroughput(period.getThroughput());
				emau.setBelongs(true);
				emau.setTradeSecretM(false);
				emau.setTradeSecretT(false);
				emau.setLastModified(1);
				maus.add(emau);
				erp.setMaus(maus);

				List<FireVariable> fvList = period.getFireVariableList();
				HashMap<String, EmissionsVariable> usedVars = new HashMap<String, EmissionsVariable>();
				HashMap<String, EmissionsVariable> unusedVars = new HashMap<String, EmissionsVariable>();
				
				List<EmissionsVariable> vars = new ArrayList<EmissionsVariable>();
				for (FireVariable fv : fvList) {
					EmissionsVariable ev = new EmissionsVariable();
					ev.setVariable(fv.getVariableID());
					ev.setValue(fv.getValue());
					ev.setTradeSecret(false);
					ev.setLastModified(1);
					unusedVars.put(fv.getVariableID(), ev);
				}

				List<Pollutant> polList = period.getPollutantList();
				for (Pollutant poll : polList) {

					Emissions em = new Emissions();
					em.setPollutantCd(poll.getPollutantCd());

					FireRow activeRow = null;
					if (fireRows != null) {
						for (FireRow fRow : fireRows) {
							if (fRow != null && fRow.getPollutantCd() != null 
									&& fRow.getPollutantCd().equals(poll.getPollutantCd()) 
									&& fRow.isActive(report.getReportYear())) {
								activeRow = fRow;
								break;
							}
						}	
					}
					
					if (activeRow != null) {

						em.setFireRef(activeRow.getFactorId());

						if (activeRow.getFactor() != null) {
							em.setFactorNumericValue(activeRow.getFactor());

						} else if (activeRow.getFormula() != null) {
							
							boolean hasError = false;

							ExpressionEval eE = ExpressionEval.createFromFactory();
							eE.setExpression(activeRow.getFormula());
							String[] eVars = eE.getVariables();
							if (eE.getErr() != null) {
								ValidationMessage vMsg = new ValidationMessage("Process Id " + period.getProcessId(),
										"Could not obtain variables from formula (" + activeRow.getFormula() 
										+ ") for Process Id " + period.getProcessId() 
										+ " pollutant " + poll.getPollutantCd() + ": " + eE.getErr(),
										ValidationMessage.Severity.ERROR);
								msgs.put("Facility Id " + report.getFacilityId() + ":" + error++, vMsg);
								logger.error("Formula (" + activeRow.getFormula() + "): " + eE.getErr());
								hasError = true;
								// Try next pollutant.
								continue;
							}

							for (String eVar : eVars) {
								EmissionsVariable fvVar = unusedVars.get(ExpressionEval.upperToLower(eVar));
								if (fvVar == null) {
									fvVar = usedVars.get(ExpressionEval.upperToLower(eVar));
								}
								if (fvVar == null) {
									ValidationMessage vMsg = new ValidationMessage(
											"Process Id " + period.getProcessId(),
											"Formula (" + activeRow.getFormula() + ") for Process Id "
													+ period.getProcessId() + " pollutant " + poll.getPollutantCd()
													+ ". Check the row to be sure variable " + ExpressionEval.upperToLower(eVar)
													+ " is present.",
											ValidationMessage.Severity.ERROR);
									msgs.put("Facility Id " + report.getFacilityId() + ":" + error++, vMsg);
									hasError = true;
									// Try next variable.
									continue;
								}
								eE.setVariable(eVar, fvVar.getValueV());
								if (eE.getErr() != null) {
									ValidationMessage vMsg = new ValidationMessage(
											"Process Id " + period.getProcessId(),
											"Problem setting variable " + ExpressionEval.upperToLower(eVar) + " for formula ("
													+ activeRow.getFormula() + ") for Process Id "
													+ period.getProcessId() + " pollutant " + poll.getPollutantCd()
													+ ": " + eE.getErr(),
											ValidationMessage.Severity.ERROR);
									msgs.put("Facility Id " + report.getFacilityId() + ":" + error++, vMsg);
									logger.error("Formula (" + activeRow.getFormula() + "): " + eE.getErr());
									// Try next variable.
									hasError = true;
									continue;
								}
								unusedVars.remove(fvVar.getVariable());
								usedVars.put(fvVar.getVariable(), fvVar);
							}

							// If error, try next pollutant.
							if (hasError) {
								continue;
							}

							Double evalOut = eE.getValue();
							if (eE.getErr() != null) {
								ValidationMessage vMsg = new ValidationMessage("Process Id " + period.getProcessId(),
										"Unable to process formula (" + activeRow.getFormula() + ") for Process Id "
												+ period.getProcessId() + " pollutant " + poll.getPollutantCd() + ": "
												+ eE.getErr(), ValidationMessage.Severity.ERROR);
								msgs.put("Facility Id " + report.getFacilityId() + ":" + error++, vMsg);
								logger.error("Formula (" + activeRow.getFormula() + "): " + eE.getErr());
								// If error, try next pollutant.
								continue;

							} else {
								DecimalFormat df = new DecimalFormat("##,##0.0########");
								em.setFactorNumericValue(df.format(evalOut));
							}
						}

					} else {
						ValidationMessage vMsg = new ValidationMessage("Process Id " + period.getProcessId(),
								"No fire row found for pollutant " + poll.getPollutantCd() + ", ",
								ValidationMessage.Severity.WARNING);
						msgs.put("Facility Id " + report.getFacilityId() + ":" + error++, vMsg);
					}
			
					em.setTradeSecretE(false);
					em.setTradeSecretF(false);
					em.setFactorNumericValueOverride(false);
					em.setLastModified(1);
					// 201 == Emissions, 5 == AQD Generated
					em.setEmissionCalcMethodCd(period.getCalculationMethod());
					em.setStackEmissions(poll.getStackEmission());
					em.setFugitiveEmissions(poll.getFugitiveEmission());
					em.setEmissionsUnitNumerator(EmissionUnitReportingDef.TONS);

					erp.addEmission(em);

				}

				erp.setVars(new ArrayList<EmissionsVariable>(usedVars.values()));
				if (!unusedVars.isEmpty()) {
					String msg = "One or more unused fire variables were present for Process Id "
							+ period.getProcessId() + ": ";
					boolean isFirst = true;
					for (String var : unusedVars.keySet()) {
						if (!isFirst) {
							msg = msg.concat(", ");
						}
						msg = msg.concat(var);
						isFirst = false;
					}
					msg = msg.concat(".");
					ValidationMessage vMsg = new ValidationMessage("Process Id " + period.getProcessId(),
							msg, ValidationMessage.Severity.WARNING);
					msgs.put("Facility Id " + report.getFacilityId() + ":" + error++, vMsg);
				}					

				EmissionRow.addEmissions(erp, emissionsTotals, logger, reportYear);
				erpList.add(erp);

			}

			for (Integer corrEpaEmuId : euMap.keySet()) {
				EmissionsReportEU emreu = new EmissionsReportEU();
				emreu.setCorrelationId(corrEpaEmuId);
				emreu.setCorrEpaEmuId(corrEpaEmuId);
				emreu.setEmuId(facility.getMatchingEmissionUnit(corrEpaEmuId).getEmuId());
				emreu.setEpaEmuId(facility.getMatchingEmissionUnit(corrEpaEmuId).getEpaEmuId());
				emreu.setExemptEG71(false);
				emreu.setLastModified(1);
				emreu.setZeroEmissions(false);
				emreu.setForceDetailedReporting(false);
				ArrayList<EmissionsReportPeriod> erPeriodList = euMap.get(corrEpaEmuId);
				emreu.setPeriods(erPeriodList);
				EmissionUnit eu = reportableEus.get(corrEpaEmuId);
				if (eu != null) {
					report.addEu(emreu);
					// When done, any EUs left in the reportable bin were not included in the csv
					// and will be dealt with below.
					reportableEus.remove(corrEpaEmuId);
					for (EmissionsReportPeriod erp : erPeriodList) {
						// Any processes left in the process bin means that the csv file did not include 
						// one or more emission processes.
						processList.remove(eu.getEpaEmuId() + ":" + erp.getProcessId());
					}
				}				
			}

			// Any EUs left in the reportable bin were not included in the csv.
			for (EmissionUnit eu : reportableEus.values()) {
				EmissionsReportEU emreu = new EmissionsReportEU();
				emreu.setCorrelationId(eu.getCorrEpaEmuId());
				emreu.setCorrEpaEmuId(eu.getCorrEpaEmuId());
				emreu.setExemptEG71(false);
				emreu.setLastModified(1);
				emreu.setZeroEmissions(true);
				emreu.setForceDetailedReporting(false);
				report.addEu(emreu);
				for (EmissionProcess eProc : eu.getEmissionProcesses()) {
					// Any processes left in the process bin means that the csv file did not include 
					// one or more emission processes.
					processList.remove(eu.getEpaEmuId() + ":" + eProc.getProcessId());
				}
			}
			
			// Any processes left in the process bin means that the csv file did not include 
			// one or more emission processes associated with EUs where one or more processes 
			// were include (i.e. the EU data in the csv was incomplete).
			for (String pKey : processList.keySet()) {
				EmissionProcess ep = processList.get(pKey);
				ValidationMessage vMsg = new ValidationMessage("Facility Id " + report.getFacilityId(),
						"Incomplete EU. No row found for EU Id : Process Id " + pKey + ".",
						ValidationMessage.Severity.ERROR);
				msgs.put("Facility Id " + report.getFacilityId() + ":" + error++, vMsg);
			}

			List<EmissionsDocumentRef> attachments = new ArrayList<EmissionsDocumentRef>();
			List<Attachment> docList = eiDataCriteria.getAttachmentDocs();
			for (Attachment doc : docList) {
				
				if (doc.getBasePath() == null) {
					doc.setBasePath("\\tmp\\EiDataImport");
				}

				EmissionsDocumentRef emDocRef = new EmissionsDocumentRef();
				emDocRef.setEmissionsDocumentTypeCD(doc.getDocTypeCd());
				emDocRef.setDescription(doc.getDescription());
				emDocRef.setLastModified(1);

				EmissionsDocument emDoc = new EmissionsDocument(doc);
				emDocRef.setPublicDoc(emDoc);
				emDocRef.setDocumentId(doc.getDocumentID());

				EmissionsDocument tsDoc = new EmissionsDocument();
				if (doc.getTradeSecretDocId() != null) {
					tsDoc.setLastModifiedTS(doc.getLastModifiedTS());
					tsDoc.setUploadDate(doc.getLastModifiedTS());
					tsDoc.setBasePath("\\tmp\\EiDataImport\\" 
							+ DocumentUtil.getFileName(doc.getTradeSecretDocURL()));
					tsDoc.setExtension(DocumentUtil
							.getFileExtension(doc.getTradeSecretDocURL()));
					tsDoc.setLastModifiedBy(doc.getLastModifiedBy());
					tsDoc.setFacilityID(doc.getFacilityID());
					emDocRef.setTradeSecretDoc(tsDoc);
					emDocRef.setTradeSecretDocId(doc.getTradeSecretDocId());
					emDocRef.setTradeSecretReason(doc.getTradeSecretJustification());
				}

				attachments.add(emDocRef);
			}
			report.setAttachments(attachments);
			
			
			ArrayList<EmissionRow> erList = EmissionRow.getEmissions(report, false, true, 0, rptt, logger, false);
			setFeeInfo(report, erList, rptt.getSc());
			createTotalsRows(report, erList);

			reportList.add(report);

		}

		return reportList;
	}
	
	private Attachment createEiDataImportTradeSecretAttachment(
			Attachment publicAttachment, Attachment tsAttachment, InputStream tsInputStream) throws DAOException {
		
		try {
			Integer docId = infrastructureDAO().nextSequenceIdValue("S_Document_Id");
			tsAttachment.setDocumentID(docId);
			tsAttachment.setLastModified(1);
			DocumentUtil.createDocument(tsAttachment.getPath(), tsInputStream);
			
			publicAttachment.setTradeSecretDocId(docId);
			publicAttachment.setTradeSecretDocURL(tsAttachment.getDocURL());
						
		} catch (IOException ioe) {
			throw new DAOException(ioe.getMessage(), ioe);
		} 
		return publicAttachment;

	}

	@Override
	public void removeEiDataImportAttachment(EiDataImportCriteria importCriteria, Attachment doc)
			throws DAOException, RemoteException {
		return;

	}
	
	@Override
	public boolean isEmissionsReportBillable(List<ReportTemplates> scs) throws DAOException {

		double firstHalf = -1d;
		double secondHalf = -1d;

		for (ReportTemplates sc : scs) {
			Fee firstHalfFee = sc.getSc().getFeeFirstHalf();
			Fee secondHalfFee = sc.getSc().getFeeSecondHalf();
			firstHalf = null != firstHalfFee.getAmount() ? firstHalfFee
					.getAmount() : 0;
			secondHalf = null != secondHalfFee.getAmount() ? secondHalfFee
					.getAmount() : 0;
			if (firstHalf > 0 || secondHalf > 0) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public EmissionsReport createEmissionsReportFromCSV(Facility facility, EmissionsReport newReport,
			List<ReportTemplates> scs) throws DAOException {
		newReport = createEmissionsReport(facility, newReport);
		Integer newReportId = newReport.getEmissionsRptId();
		
		ArrayList<Integer> emissionsReportIds = emissionsReportDAO().retrieveValidEmissionsReportIds(
				facility.getFacilityId(), newReport.getReportYear(), newReport.getContentTypeCd());
		emissionsReportIds.removeAll(Collections.singleton(newReportId));

		if (!emissionsReportIds.isEmpty()) {
			emissionsReportDAO().updatePriorEIsToInvalidAfterCsvImport(facility.getFacilityId(), newReport.getReportYear(),
					newReport.getContentTypeCd(), newReportId);
		}
		
		// freeze the current version of the facility inventory
		facility.setCopyOnChange(true);
		getFacilityDAO().modifyFacility(facility);
		
		// Set the state in facility inventory
		updateEmissionsRptInfos(emissionsReportDAO(), facility.getFacilityId(), newReport.getEmissionsRptId(),
				ReportReceivedStatusDef.SUBMITTED);
		
		return newReport;
	}

	@Override
	public Map<String, Integer> retrieveEnabledEmissionRptsForYearAndContentType(Integer year, String contentType) throws DAOException {
		return emissionsReportDAO().retrieveEnabledEmissionRptsForYearAndContentType(year, contentType);
	}


	@Override
	public boolean deleteAssociatedInvoice(EmissionsReport emissionsReport)
			throws DAOException {
		boolean success = true;
		try {

			emissionsReportDAO().deleteEmissionsRptInvoiceNotes(emissionsReport);
			emissionsReportDAO().deleteEmissionsRptInvoiceDetails(emissionsReport);
			emissionsReportDAO().deleteAssociatedInvoice(emissionsReport);
		} catch (IOException ioe) {
			success = false;
			throw new DAOException(ioe.getMessage(), ioe);

		}
		return success;
	}

	@Override
	public boolean deleteReferences(String facilityId) throws DAOException {
	      return emissionsReportDAO().deleteReferences(facilityId);

	}

}