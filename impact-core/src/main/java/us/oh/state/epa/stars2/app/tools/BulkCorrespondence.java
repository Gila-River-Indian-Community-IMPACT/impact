
package us.oh.state.epa.stars2.app.tools;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.event.ActionEvent;

import us.oh.state.epa.stars2.bo.ApplicationService;
import us.oh.state.epa.stars2.bo.ComplianceReportService;
import us.oh.state.epa.stars2.bo.CorrespondenceService;
import us.oh.state.epa.stars2.bo.DocumentService;
import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.document.CorrespondenceDocument;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.def.CorrespondenceAttachmentTypeDef;
import us.oh.state.epa.stars2.def.CorrespondenceDef;
import us.oh.state.epa.stars2.def.CorrespondenceDirectionDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.wy.state.deq.impact.App;

@SuppressWarnings("serial")
public abstract class BulkCorrespondence extends BulkOperation {

    private String _correspondenceTypeCode;
    private String _correspondenceDescription;
    protected String _correspondenceDate;
    protected boolean _correspondenceSent = false;

    private String _currentUser = InfrastructureDefs.getCurrentUserAttrs().getUserName();
    private Integer _currentUserId = InfrastructureDefs.getCurrentUserId();

    protected ArrayList<Correspondence> _correspondence = new ArrayList<Correspondence>();
    
    private CorrespondenceService correspondenceService = App.getApplicationContext()
			.getBean(CorrespondenceService.class);

    private DocumentService documentService = App.getApplicationContext()
			.getBean(DocumentService.class);
    
    private PermitService permitService = App.getApplicationContext()
			.getBean(PermitService.class);
    
    private FacilityService facilityService = App.getApplicationContext()
			.getBean(FacilityService.class);
    
    private ApplicationService applicationService = App.getApplicationContext()
			.getBean(ApplicationService.class);
    
    private ComplianceReportService complianceReportService = App.getApplicationContext()
			.getBean(ComplianceReportService.class);
    
    private EmissionsReportService emissionsReportService =  App.getApplicationContext()
			.getBean(EmissionsReportService.class);
    
    private FullComplianceEvalService fullComplianceEvalService = App.getApplicationContext()
    		.getBean(FullComplianceEvalService.class);
    
    public ComplianceReportService getComplianceReportService() {
		return complianceReportService;
	}

	public void setComplianceReportService(
			ComplianceReportService complianceReportService) {
		this.complianceReportService = complianceReportService;
	}

	public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}

	public void setEmissionsReportService(
			EmissionsReportService emissionsReportService) {
		this.emissionsReportService = emissionsReportService;
	}

	public PermitService getPermitService() {
		return permitService;
	}

	public void setPermitService(PermitService permitService) {
		this.permitService = permitService;
	}

	public ApplicationService getApplicationService() {
		return applicationService;
	}

	public void setApplicationService(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}


    public CorrespondenceService getCorrespondenceService() {
		return correspondenceService;
	}

	public void setCorrespondenceService(CorrespondenceService correspondenceService) {
		this.correspondenceService = correspondenceService;
	}

	public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}
	
	public FullComplianceEvalService getFullComplianceEvalService() {
		return fullComplianceEvalService;
	}

	public void setFullComplianceEvalService(FullComplianceEvalService fullComplianceEvalService) {
		this.fullComplianceEvalService = fullComplianceEvalService;
	}

	public void correspondence(ActionEvent actionEvent) throws RemoteException {

        CorrespondenceService corrBO = null;
        DocumentService docBO;

        try {
            corrBO = getCorrespondenceService();
            docBO = getDocumentService();
        }
        catch (Exception e) {
            logger.error("Exception caught while fetching services, msg = "
                         + e.getMessage(), e);
            return;
        }

        if (_correspondenceDate == null) {
            DisplayUtil.displayError("Missing Correspondence Date");
            return;
        }

        StringTokenizer st = new StringTokenizer(_correspondenceDate, "/");
        if (st.countTokens() != 3) {
            DisplayUtil.displayError("Correspondence Date should be in the form MM/DD/YYYY");
            return;
        }
        try {
            int month = Integer.parseInt(st.nextToken()) - 1;
            int day = Integer.parseInt(st.nextToken());
            int year = Integer.parseInt(st.nextToken());
            
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            
            for (Correspondence correspondence : _correspondence) {
                Timestamp ts = new Timestamp(cal.getTimeInMillis());
                correspondence.setDateGenerated(ts);
                correspondence.setDirectionCd(CorrespondenceDirectionDef.OUTGOING);
                Document cDoc = correspondence.getDocument(); 
                corrBO.createCorrespondence(correspondence);
                
                if (cDoc != null && correspondence.getSavedDocReqd()) {
                    cDoc.setTemporary(false);
                    docBO.updateDocument(cDoc);
                    
                    Attachment att = new Attachment();
                    att.setDocumentID(cDoc.getDocumentID());
                    att.setDocTypeCd(cDoc.getDocTypeCd());
                    corrBO.createCorrespondenceAttachment(correspondence.getCorrespondenceID(), att);
                }
                
            }
        }
        catch (Exception e) {
            String logStr = "Problem while storing correspondence. " + "Exception type = "
                + e.getClass().getName() + ", Msg = "
                + e.getMessage();
            logger.error(logStr, e);
            DisplayUtil.displayError(logStr);
        }
        _correspondenceSent = true;
        return;

    }

    public final void addCorrespondence(String facilityId, String externalNumber, String pathToFile)
    	throws IOException {

        InputStream is = null;
    	try {

            CorrespondenceDocument cDoc = new CorrespondenceDocument();
            cDoc.setFacilityID(facilityId);
            cDoc.setLastModifiedBy(_currentUserId);
            cDoc.setTemporary(true);
            cDoc.setDescription(_correspondenceDescription);
            cDoc.setDocTypeCd(CorrespondenceAttachmentTypeDef.CA);

            Correspondence correspondence = new Correspondence();
            correspondence.setCorrespondenceTypeCode(_correspondenceTypeCode);

            if (correspondence.getSavedDocReqd()) {
                if (pathToFile != null) {
                    int extDot = pathToFile.lastIndexOf(".");
                    if (extDot > 0) {
                        cDoc.setExtension(pathToFile.substring(extDot + 1));
                    }
                    is = DocumentUtil.getDocumentAsStream(pathToFile);
                    cDoc = (CorrespondenceDocument) getDocumentService().uploadTempDocument(cDoc, null, is);
                }
            
                correspondence.setDocument(cDoc);
            }

            correspondence.setFacilityID(facilityId);
            if (externalNumber != null) {
                correspondence.setAdditionalInfo(externalNumber);
            }
            _correspondence.add(correspondence);

        }
        catch (Exception e) {
            logger.error("Exception caught while trying to addCorrespondence: " + e.getMessage(), e);
        } finally {
        	if(null != is)
        		is.close();
        }
    }

    public final void addCorrespondence(Correspondence correspondence) {
        if (correspondence != null) {
            _correspondence.add(correspondence);
        }            
    }

    public final String getCorrespondenceTypeCode() {
        return _correspondenceTypeCode;
    }

    public final void setCorrespondenceTypeCode(String correspondenceTypeCode) {

        _correspondenceTypeCode = correspondenceTypeCode;
        
        DefData corrDefData = CorrespondenceDef.getDescriptionData();
        _correspondenceDescription = corrDefData.getItems().getItemDesc(_correspondenceTypeCode);

    }

    public final boolean isCorrespondenceSent() {
        return _correspondenceSent;
    }

    public final void setCorrespondenceSent(boolean sent) {
        _correspondenceSent = sent;
    }

    public final String getCorrespondenceDate() {
        return _correspondenceDate;
    }

    public final void setCorrespondenceDate(String correspondenceDate) {
        _correspondenceDate = correspondenceDate;
    }

    public final String getCorrespondenceDescription() {
        return _correspondenceDescription;
    }

    public final void setCorrespondenceDescription(String correspondenceDescription) {
        _correspondenceDescription = correspondenceDescription;
    }

    public final String getCurrentUser() {
        return _currentUser;
    }

    public final Integer getCurrentUserId() {
        return _currentUserId;
    }

    public final List<Correspondence> getCorrespondence() {
        return _correspondence;
    }

}
