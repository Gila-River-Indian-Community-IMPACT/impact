package us.oh.state.epa.stars2.app.ceta;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import oracle.adf.view.faces.event.ReturnEvent;
import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.bo.StackTestService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.CetaBaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ceta.Evaluator;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SiteVisit;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SiteVisitNote;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SvAttachment;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.SiteVisitAttachmentTypeDef;
import us.oh.state.epa.stars2.util.TimestampUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.document.AttachmentEvent;
import us.oh.state.epa.stars2.webcommon.document.AttachmentException;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentListener;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;

/*
 * Note that if the object does not exist, delete will not fail.
 */

@SuppressWarnings("serial")
public class SiteVisitDetail extends ValidationBase implements IAttachmentListener {
    private Integer visitId;
    private String facilityId;
    private Facility facility;
    private FullComplianceEval fce;
    private SiteVisit siteVisit;
    private boolean editable;
    private boolean disableDelete;
    private String disableDeleteMsg;
    private String afsWarning;
    
    private boolean newVisitObject = false;
    private boolean editVisitObject = false;
    private boolean blankOutPage = false;
    
    private boolean newNote;
    private SiteVisitNote tempComment;
    private SiteVisitNote modifyComment;
    private boolean noteReadOnly;
    private static final String COMMENT_DIALOG_OUTCOME = "dialog:siteVisitNoteDetail";
    private StackTest[] stackTestList;
    private Timestamp visitDate;  

    private TableSorter allFCEs = new TableSorter();
    private String facilityIdForStackTestVisitType;
    
	private FacilityService facilityService;
	private FullComplianceEvalService fullComplianceEvalService;
	private StackTestService stackTestService;

	public StackTestService getStackTestService() {
		return stackTestService;
	}
	
	public void setStackTestService(
			StackTestService stackTestService) {
		this.stackTestService = stackTestService;
	}

	public FullComplianceEvalService getFullComplianceEvalService() {
		return fullComplianceEvalService;
	}

	public void setFullComplianceEvalService(
			FullComplianceEvalService fullComplianceEvalService) {
		this.fullComplianceEvalService = fullComplianceEvalService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}
	
    public SiteVisitDetail() {
        super();
    }
    
    public final void addVisitEvaluator() {
        siteVisit.addEvaluator();
    }

    public final void deleteEvaluators() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            deleteEvaluatorsInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private final void deleteEvaluatorsInternal() {
        if(siteVisit.getEvaluators() == null) return;
        Iterator<Evaluator> l = siteVisit.getEvaluators().iterator();
        while(l.hasNext()) {
            Evaluator e = l.next();
            if(e == null) continue;
            if(e.isSelected()) l.remove();
        }
    }
    
    public final String submitVisit() {
        // (multiple) selected visits are reassigned to this Inspection.
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return submitVisitInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    public String from2ndLevelMenu() {
        return submitVisitInternal();
    }

    private final String submitVisitInternal() {
        blankOutPage = false;
        String ret = null;
        boolean ok = true;
        if (visitId != null) {
            try {
               siteVisit = getFullComplianceEvalService().retrieveSiteVisit(visitId);
               if(siteVisit == null) {
                   DisplayUtil.displayError("SiteVisit is not found with that number.");
                   return null;
               }
               setVisitTitle();
               facilityId = siteVisit.getFacilityId();
               facility = getFacilityService().retrieveFacility(facilityId);
               if(facility == null) {
                   DisplayUtil.displayError("(For Site Visit " + visitId + ") Facility " + facilityId + " not found.");
                   return null;
               }
               ok = retrieveFCE();
            } catch (RemoteException e) {
                blankOutPage = true;
                handleException(e);
                ok = false;
            }
            if(ok) {
            	((SimpleMenuItem)FacesUtil.getManagedBean("menuItem_siteVisitDetail")).setDisabled(false);
                initializeAttachmentBean();
                ret = "ceta.siteVisitDetail";
            }
        }
        return ret;
    }
    
    public final String submitStackTestVisitType() {
        String rtn = null;
        boolean ok = true;
        // This is to display a datagrid of stack tests associated with facilityId and visitDate.
        try {
            if(facility == null || 
                    !facility.getFacilityId().equals(facilityIdForStackTestVisitType)) {
            	facility = getFacilityService().retrieveFacility(facilityIdForStackTestVisitType);
            	if(facility == null) {
                    DisplayUtil.displayError("(For Site Visit " + visitId + ") Facility " + facilityId + " not found.");
                    return null;
                }
                
            }
            if (visitId != null) {
                   siteVisit = getFullComplianceEvalService().retrieveSiteVisit(visitId);
                   if(siteVisit == null) {
                       DisplayUtil.displayError("Site Visit " + visitId + " not found.");
                       return null;
                   }
                   setVisitTitle();
                   facilityId = siteVisit.getFacilityId();
                   ok = retrieveFCE();
                }
            stackTestList = getStackTestService().searchStackTests(facilityIdForStackTestVisitType, visitDate);
   
        } catch(RemoteException re) {
            DisplayUtil.displayError("Failed to read facility inventory for facility " + facilityIdForStackTestVisitType);
            handleException(re);
            blankOutPage = true;
            ok = false;
        }
        if(ok) {
        	((SimpleMenuItem)FacesUtil.getManagedBean("menuItem_siteVisitDetail")).setDisabled(false);
            initializeAttachmentBean();
            rtn = "ceta.siteVisitDetail";
        }
        return rtn;
    }
    
    // The Inspection is needed to populate the header field of the Site Visit
    private boolean retrieveFCE() {
        blankOutPage = false;
        Integer fceId = siteVisit.getFceId();
        fce = null;
        boolean ok = true;
        try {
            if(fceId != null) {
                fce = getFullComplianceEvalService().retrieveFce(facilityId, fceId);
                if(fce == null) { ok = false;
                    DisplayUtil.displayError("(For Site Visit " + visitId + ") Inspection " + fceId + " not found.");
                }
            }
        } catch(RemoteException e) {
            blankOutPage = true;
            handleException(e);
            ok = false;
        }
        return ok;
    }
    
    void setVisitTitle() {
        FceSiteVisits fceSV = (FceSiteVisits)FacesUtil.getManagedBean("fceSiteVisits");
        if(siteVisit.getFceId() != null) {
            fceSV.setTypeOfNewVisit("View/Edit Site Visit");  // Associated with Inspection " + siteVisit.getFceId());
        } else {
            fceSV.setTypeOfNewVisit("View/Edit Site Visit");
        }
    }
    
    public LinkedHashMap<String, Timestamp> getScheduleChoices() {
        LinkedHashMap<String, Timestamp> scheds = new LinkedHashMap<String, Timestamp>();
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH)/3 * 3;
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 1);
        Timestamp ref = new Timestamp(cal.getTimeInMillis());
        if(fce.getScheduledTimestamp() != null && fce.getScheduledTimestamp().before(ref)) ref = fce.getScheduledTimestamp();
        cal.setTime(new Date(ref.getTime()));
        for(int i=0; i<20; i++) {
            Timestamp use = ref;
            // make sure timestamp used will match something in picklist.
            if(CetaBaseDB.getScheduled(ref).equals(CetaBaseDB.getScheduled(fce.getScheduledTimestamp()))) {
                use = fce.getScheduledTimestamp();
            }
            scheds.put(CetaBaseDB.getScheduled(ref), use);
            cal.add(Calendar.MONTH, 3);
            ref = new Timestamp(cal.getTimeInMillis());
        }
        return scheds;
    }
    
    public String chgFceAssign() {
    	// Get all Inspections for facility
    	try {
    		FullComplianceEval[] all = getFullComplianceEvalService().retrieveFceBySearch(siteVisit.getFacilityId());
    		if(all.length == 0) {
    			DisplayUtil.displayInfo("Facility has no Inspections");
    			return null;
    		}
    		List <FullComplianceEval> result = new ArrayList<FullComplianceEval>();
    		if (getSiteVisit().getFceId() != null)
    		{
    			for (int i = 0; i < all.length; i++)
    			{
    				if (!getSiteVisit().getFceId().equals(all[i].getId()))
    				{
    					result.add(all[i]);
    				}
    			}
    			allFCEs.setWrappedData(result);
    		}
    		else{
    			allFCEs.setWrappedData(all);
    		}

    	} catch (RemoteException re) {
    		blankOutPage = true;
    		handleException(re);
    	}
    	return "dialog:fceReassign";
    }

    public final void saveChgFceAssign() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            saveChgFceAssignInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private void saveChgFceAssignInternal() {
        Iterator<?> it = allFCEs.getTable().getSelectionState().getKeySet().iterator();
        FullComplianceEval selectedRow = null;
        boolean ok = false;
        if (it.hasNext()) {
            Object obj = it.next();
            allFCEs.setRowKey(obj);
            selectedRow = (FullComplianceEval) allFCEs.getRowData();
            allFCEs.getTable().getSelectionState().clear();
            ok = true;
        }
        if(!ok) {
            DisplayUtil.displayError("Cancel or make an association selection");
            return;
        }
        siteVisit.setFceId(selectedRow.getId());
        editVisitObject = true;
        DisplayUtil.displayInfo("Site Visit associated with Inspection "
    		+ selectedRow.getInspId());
        saveInternal();
        ok = retrieveFCE();
        if(ok) {
            setVisitTitle();
            updateLists();
            FacesUtil.returnFromDialogAndRefresh();
        }
    }
    
    public final void clearFceAssign() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            clearFceAssignInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private void clearFceAssignInternal() {
        siteVisit.setFceId(null);
        editVisitObject = true;
        saveInternal();
        boolean ok = retrieveFCE();
        if(ok) {
            setVisitTitle();
            updateLists();
            DisplayUtil.displayInfo("Site visit not associated with any Inspection");
            FacesUtil.returnFromDialogAndRefresh();
        }
    }
    
    private void updateLists() {
        FceSiteVisits fceVisitsBean = (FceSiteVisits) FacesUtil.getManagedBean("fceSiteVisits");
        SiteVisitSearch visitSearchBean = (SiteVisitSearch) FacesUtil.getManagedBean("siteVisitSearch");
        FceDetail fceDetailBean = (FceDetail) FacesUtil.getManagedBean("fceDetail");
        fceVisitsBean.silentInitFCEs();
        visitSearchBean.silentSearch();
        fceDetailBean.submitSilently(facilityId);
    }
    
    public void cancelChgFceAssign() {
        FacesUtil.returnFromDialogAndRefresh();
    }

    public final void save() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            saveInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private final void saveInternal() {
        blankOutPage = false;
        if(newVisitObject) {
            // validate fields
            ValidationMessage[] validationMessages = siteVisit.validate();
            if (displayValidationMessages("siteVisitBody:",  validationMessages)) {
                return;
            }
            try {
                boolean dup = getFullComplianceEvalService().isVisitDup(siteVisit);
                if(dup) {
                    DisplayUtil.displayError("This Site Visit has the same visit date and visit type as another visit.");
                    return;
                }
                Calendar cal = Calendar.getInstance();
                Timestamp today = new Timestamp(cal.getTimeInMillis());
                siteVisit.setCreatedDt(today);
                siteVisit.setCreatedById(InfrastructureDefs.getCurrentUserId());
                siteVisit = getFullComplianceEvalService().createSiteVisit(siteVisit);
                setVisitTitle();
                siteVisit = getFullComplianceEvalService().retrieveSiteVisit(siteVisit.getId());
            } catch(RemoteException re) {
                blankOutPage = true;
                handleException(re);
                return;
            }
            setEditable(false);
            newVisitObject = false;
            updateLists();
            setVisitId(siteVisit.getId());
			((SimpleMenuItem) FacesUtil
					.getManagedBean("menuItem_siteVisitDetail"))
					.setDisabled(false);
            // FacesUtil.returnFromDialogAndRefresh();
            DisplayUtil.displayInfo("Site Visit created successfully.");
        } else if(editVisitObject) {
            // validate fields
            ValidationMessage[] validationMessages = siteVisit.validate();
            if (displayValidationMessages("siteVisitBody:",  validationMessages)) {
                return;
            }
            
			try {
				boolean dup;
				dup = getFullComplianceEvalService().isVisitDup(siteVisit);
				if (dup) {
					DisplayUtil
							.displayError("This Site Visit has the same visit date and visit type as another visit.");
					return;
				}
			} catch (RemoteException re) {
				blankOutPage = true;
				handleException(re);
				return;
			}

			try {
				getFullComplianceEvalService().modifySiteVisit(siteVisit);
				initializeAttachmentBean();
				setEditable(false);
				DisplayUtil.displayInfo("Site Visit updated successfully.");
			} catch (RemoteException re) {
				blankOutPage = true;
				handleException(re);
			} finally {
				try {
					siteVisit = getFullComplianceEvalService()
							.retrieveSiteVisit(siteVisit.getId());
					if(siteVisit != null) {
						blankOutPage = false;
					}
				} catch (RemoteException e) {
					setEditable(false);
					logger.error("Could not retrieve site visit : "
							+ siteVisit.getSiteId());
					DisplayUtil.displayError("Failed to update site visit "
							+ siteVisit.getSiteId()
							+ ". The site visit may no longer exist.");
					handleBadDetailAndRedirect();
				}
			}
            
			if(blankOutPage) {
				editVisitObject = false;
			}
            updateLists();
            // FacesUtil.returnFromDialogAndRefresh();
        } else {
            DisplayUtil.displayError("should not get here");
        }
    }

	public boolean isAdminEditOnly() {
        boolean rtn = false;
        if(siteVisit != null) {
            if(siteVisit.getAfsDate()!= null && isStars2Admin()) rtn = true;
        }
        return rtn;
    }
    
    public boolean isAllowEditOperations() {
        boolean rtn = false;
        if(siteVisit != null) {
            if(siteVisit.getAfsId() == null && isCetaUpdate() || isStars2Admin()) rtn = true;
            rtn = rtn && !isReadOnlyUser();
        }
        return rtn;
    }
    
    public final void close() {
        editVisitObject = false;
        newVisitObject = false;
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public final void editNewVisit() {
        setEditable(true);
        newVisitObject = true;
        editVisitObject = false;
    }
    
    public final void editVisit() {
        setEditable(true);
        editVisitObject = true;
        newVisitObject = false;
    }
    
    public final String cancelEdit() {
        setEditable(false);
        editVisitObject = false;
        if(newVisitObject) {
            newVisitObject = false;
            DisplayUtil.displayInfo("Operation cancelled, Site Visit not created.");
            FceSiteVisits fceSiteVisit = (FceSiteVisits) FacesUtil.getManagedBean("fceSiteVisits");
            return fceSiteVisit.initSiteVisitsForCancel();
        } else {
            DisplayUtil.displayInfo("Operation cancelled, Site Visit not updated.");
        }
        newVisitObject = false;
        return "ceta.siteVisitDetail";
    }
    
    public String pickReassign() {
        return "dialog:cetaReassociateFce";
    }
    
    public final void saveReassign() {
        updateLists();
        DisplayUtil.displayInfo("Reassociations completed sucessfully");
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public final void cancelReassign() {
        DisplayUtil.displayInfo("Reassociations cancelled");
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public final String deleteVisit() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return deleteVisitInternal();
        } finally {
            clearButtonClicked();
        }
    }

    private final String deleteVisitInternal() {
		// do confirmation popup.
		disableDelete = false;
		//try {
			/*Integer[] enfIds = getEnforcementService().getEnforcementWithDiscovery(AFSActionDiscoveryTypeDef.SITE_VISIT,
					siteVisit.getId());
			if (enfIds.length > 0) {
				StringBuffer sb = new StringBuffer();
				for (Integer i : enfIds) {
					sb.append(i.toString() + " ");
				}
				disableDeleteMsg = "This site visit was not deleted because it is specified as the Discovery reason in the following Enforcemnt(s):  "
						+ sb.toString();
				disableDelete = true;
			}*/
			afsWarning = null;
			if (siteVisit.getAfsId() != null) {
				afsWarning = "If you delete this Site Visit, you will lose the fact that it is exported to AFS.";
			}
			if (siteVisit.getInspectionsReferencedIn().size() > 0) {
				disableDelete = true;
				String inspectionMsg = "This site visit cannot be deleted because it is being referenced by the following Inspection report(s): "
						+ String.join(", ", siteVisit.getInspectionsReferencedIn());
				if (StringUtils.isBlank(disableDeleteMsg)) {
					disableDeleteMsg = inspectionMsg;
				} else {
					disableDeleteMsg = disableDeleteMsg + "\n\n" + inspectionMsg;
				}
			}
		/*} catch (RemoteException re) {
			blankOutPage = true;
			handleException(re);
		}*/
		return "dialog:confirmVisitDelete";
	}
    
    // Delete confirmed
    public final String deleteVisit2() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            deleteVisit2Internal();
        } finally {
            clearButtonClicked();
        }
        return null;
    }
    
	private void deleteVisit2Internal() {
		try {
			getFullComplianceEvalService().removeSiteVisit(siteVisit);
			// updateLists();
			DisplayUtil.displayInfo("Site Visit deleted successfully.");
			handleBadDetail();
		} catch (RemoteException re) {
			blankOutPage = true;
			handleException(re);
			DisplayUtil.displayError("Failed to delete Site Visit.");
			return;
		}
		// AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
		FacesUtil.returnFromDialogAndRefresh();
	}
    
   //  Return Listener
    public void deleteFinished(ReturnEvent actionEvent) {
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public void initializeAttachmentBean() {
        Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
        a.addAttachmentListener(this);
        a.setStaging(false);
        a.setNewPermitted(isCetaUpdate());
        a.setUpdatePermitted(isCetaUpdate());
        a.setFacilityId(siteVisit.getFacilityId());
        a.setSubPath("SiteVisit");
        if(siteVisit.getId() != null) a.setObjectId(Integer.toString(siteVisit.getId()));
        else a.setObjectId("");
        a.setSubtitle(null);
        a.setTradeSecretSupported(false);
        a.setHasDocType(true);
        a.setAttachmentTypesDef(SiteVisitAttachmentTypeDef.getData()
                .getItems());
        a.setAttachmentList(siteVisit.getAttachments().toArray(
                new Attachment[0]));
        a.cleanup();
    }

    public void cancelAttachment() {
        FacesUtil.returnFromDialogAndRefresh();
    }

    public AttachmentEvent createAttachment(Attachments attachment)
            throws AttachmentException {
        boolean ok = true;
        if (attachment.getDocument() == null) {
            // should never happen
            logger.error("Attempt to process null attachment");
            ok = false;
        } else {
        	if (attachment.getPublicAttachmentInfo() == null
                    && attachment.getFileToUpload() != null) {
                attachment.setPublicAttachmentInfo(new UploadedFileInfo(
                        attachment.getFileToUpload()));
            }
        	
        	// make sure document description and type are provided
            if (attachment.getDocument().getDescription() == null || attachment.getDocument().getDescription().trim().equals("")) {
                DisplayUtil
                        .displayError("Please specify the description for this attachment");
                ok = false;
            }
            if (attachment.getDocument().getDocTypeCd() == null || attachment.getDocument().getDocTypeCd().trim().equals("")) {
                DisplayUtil
                        .displayError("Please specify an attachment type for this attachment");
                ok = false;
            }
            
            // make sure document file is provided
            if (attachment.getPublicAttachmentInfo() == null
					&& attachment.getTempDoc().getDocumentID() == null) {
				DisplayUtil
						.displayError("Please specify the file to upload for this attachment");
				ok = false;
			}
        }
        if (ok) {
            try {
                try {
                    getFullComplianceEvalService().createSvAttachment(
                            siteVisit, attachment.getTempDoc(),
                            attachment.getPublicAttachmentInfo().getInputStream());
                } catch (IOException e) {
                    blankOutPage = true;
                    throw new RemoteException(e.getMessage(), e);
                }

                siteVisit.setAttachments(getFullComplianceEvalService().retrieveSvAttachments(siteVisit.getId()));
                Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
                a.setAttachmentList(siteVisit.getAttachments().toArray(new Attachment[0]));
                attachment.cleanup();
                DisplayUtil.displayInfo("The attachment has been added to the site visit.");
                FacesUtil.returnFromDialogAndRefresh();
            } catch (RemoteException e) {
                blankOutPage = true;
                setEditable(false);
                // turn it into an exception we can handle.
                handleException(e);
            }
        }
        return null;
    }

    public AttachmentEvent deleteAttachment(Attachments attachment) {
        boolean ok = true;
        try {
            Attachment doc = attachment.getTempDoc();
            getFullComplianceEvalService().removeSvAttachment(new SvAttachment(doc));
        } catch (RemoteException e) {
            blankOutPage = true;
            ok = false;
            handleException(e);
        }
        // reload attachments
        try {
            siteVisit.setAttachments(getFullComplianceEvalService().retrieveSvAttachments(siteVisit.getId()));
            Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
            a.setAttachmentList(siteVisit.getAttachments().toArray(new Attachment[0]));
            attachment.cleanup();
        } catch (RemoteException e) {
            blankOutPage = true;
            setEditable(false);
            ok = false;
            // turn it into an exception we can handle.
            handleException(e);
        }
        if(ok) {
            DisplayUtil.displayInfo("The attachment has been removed.");
            FacesUtil.returnFromDialogAndRefresh();
        }
        return null;
    }

    public AttachmentEvent updateAttachment(Attachments attachment) {
        Attachment doc = attachment.getTempDoc();
        boolean ok = true;
        
        // make sure document description is provided
        if (doc.getDescription() == null
        		|| doc.getDescription().trim().equals("")) {
        	DisplayUtil
        	.displayError("Please specify the description for this attachment");
        	ok = false;
        }
        
        if (ok) {
        	try {
        		getFullComplianceEvalService().modifySvAttachment(new SvAttachment(doc));
        	} catch (RemoteException e) {
        		blankOutPage = true;
        		ok = false;
        		handleException(e);
        	}
        	// reload attachments
        	try {
        		siteVisit.setAttachments(getFullComplianceEvalService().retrieveSvAttachments(siteVisit.getId()));
        		Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
        		a.setAttachmentList(siteVisit.getAttachments().toArray(new Attachment[0]));
        		attachment.cleanup();
        	} catch (RemoteException e) {
        		blankOutPage = true;
        		setEditable(false);
        		// turn it into an exception we can handle.
        		ok = false;
        		handleException(e);
        	}
        	if(ok) {
	        	DisplayUtil.displayInfo("The attachment information has been updated.");
        		FacesUtil.returnFromDialogAndRefresh();
        	}
        }
        
        return null;
    }
    
    
    public void submitProfileByFpId() {
        FacilityProfile fp = (FacilityProfile)FacesUtil.getManagedBean("facilityProfile");
        fp.submitProfileByFpId();
        StackTests stackTests = (StackTests)FacesUtil.getManagedBean("stackTests");
        stackTests.setPopupRedirectOutcome("facilityProfileFromVisit");
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public String submitProfileById() {
        FacilityProfile fp = (FacilityProfile)FacesUtil.getManagedBean("facilityProfile");
        StackTests stackTests = (StackTests)FacesUtil.getManagedBean("stackTests");
        stackTests.setPopupRedirectOutcome("facilityProfileFromVisit");
        return fp.submitProfileById();
     
    }
    
    public boolean isLegacy() {
        // Don't call new vists legacy.
        return siteVisit.getId() != null && TimestampUtil.isCetaLegacy(siteVisit.getCreatedDt());
    }

	public final String getFacilityId() {
		return facilityId;
	}

	public final String getFacilityName() {
		String facilityName = "";
		if (facility != null) {
			facilityName = facility.getName();
		}
		return facilityName;
	}

	public final boolean isEditable() {
		return editable;
	}

	public final void setEditable(boolean editable) {
		this.editable = editable;
        Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
        a.setNewPermitted(isCetaUpdate());
        a.setUpdatePermitted(isCetaUpdate());
        if(siteVisit != null && siteVisit.getId() != null) a.setObjectId(Integer.toString(siteVisit.getId()));
        else a.setObjectId("");
	}

	public final Facility getFacility() {
		return facility;
	}

	public final void setFacility(Facility facility) {
		this.facility = facility;
	}

    public FullComplianceEval getFce() {
        return fce;
    }

    public void setFce(FullComplianceEval fce) {
        this.fce = fce;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public Integer getVisitId() {
        return visitId;
    }

    public void setVisitId(Integer visitId) {
        this.visitId = visitId;
    }

    public SiteVisit getSiteVisit() {
        return siteVisit;
    }

    public void setSiteVisit(SiteVisit visit) {
        siteVisit = visit;
    }

    public TableSorter getAllFCEs() {
        return allFCEs;
    }
    
    public int getAllFCEsSize()
    {
    	return getAllFCEs().getRowCount();
    }

    public void setAllFCEs(TableSorter allFCEs) {
        this.allFCEs = allFCEs;
    }

    public boolean isDisableDelete() {
        return disableDelete;
    }

    public String getDisableDeleteMsg() {
        return disableDeleteMsg;
    }
    
    public final String goToSummaryPage() {
    	FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
    	fp.setFacilityId(facilityId);
    	fp.submitProfileById();
    	FceSiteVisits fceSiteVisit = (FceSiteVisits) FacesUtil.getManagedBean("fceSiteVisits");
    	return fceSiteVisit.initSiteVisits();
    }

    public String getAfsWarning() {
        return afsWarning;
    }

    public boolean isBlankOutPage() {
        return blankOutPage;
    }

    public void setBlankOutPage(boolean blankOutPage) {
        this.blankOutPage = blankOutPage;
    }
    

	private void loadNotes(int visitId) {
        try {
        	Note[] siteVisitNotes = getFullComplianceEvalService().retrieveSiteVisitNotes(visitId);
        	siteVisit.setSiteVisitNotes(siteVisitNotes);
        } catch (RemoteException ex) {
            DisplayUtil.displayError("cannot load Site Visit comments");
            handleException(ex);
            return;
        }
    }
	
	 public final String startAddComment() {
	        tempComment = new SiteVisitNote();
	        tempComment.setUserId(getCurrentUserID());
	        tempComment.setVisitId(siteVisit.getId());
	        tempComment.setDateEntered(new Timestamp(System.currentTimeMillis()));
	        tempComment.setNoteTypeCd(NoteType.DAPC);
	        newNote = true;
	        noteReadOnly = false;

	        return COMMENT_DIALOG_OUTCOME;
	    }
	
	 public final String startEditComment() {
	        newNote = false;
	        tempComment = new SiteVisitNote(modifyComment);
	        if (tempComment.getUserId().equals(getCurrentUserID()))
	            noteReadOnly = false;
	        else
	            noteReadOnly = true;

	        return COMMENT_DIALOG_OUTCOME;
	    }
	 
	 public final void saveComment(ActionEvent actionEvent) {
		 List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

		 // make sure note is provided
		 if (tempComment.getNoteTxt() == null || tempComment.getNoteTxt().trim().equals("")) {
			 validationMessages.add(new ValidationMessage("noteTxt", "Attribute " + "Note" + " is not set.",
					 ValidationMessage.Severity.ERROR, "noteTxt"));
		 }

		 if (validationMessages.size() > 0) {
			 displayValidationMessages("", validationMessages.toArray(new ValidationMessage[0]));
		 } else {
			 try {
				 if (newNote) {
					 getFullComplianceEvalService().createSiteVisitNote(tempComment);
				 } else {
					 getFullComplianceEvalService().modifySiteVisitNote(tempComment);
				 }
			 } catch (RemoteException e) {
				 DisplayUtil.displayError("cannot save comment");
				 handleException(e);
				 return;
			 }

			 tempComment = null;
			 reloadNotes();
			 FacesUtil.returnFromDialogAndRefresh();
		 }
	 }

	    public final void commentDialogDone(ReturnEvent returnEvent) {
	        tempComment = null;
	        reloadNotes();
	    }

	    public final SiteVisitNote getTempComment() {
	        return tempComment;
	    }

	    public final void setTempComment(SiteVisitNote tempComment) {
	        this.tempComment = tempComment;
	    }
	    
	    public Integer getCurrentUserID() {
	        return InfrastructureDefs.getCurrentUserId();
	    }
	    
	    public final boolean isNoteReadOnly() {
			if(isReadOnlyUser()){
				return true;
			}
			
	        return noteReadOnly;
	    }

	    public final void setNoteReadOnly(boolean noteReadOnly) {
	        this.noteReadOnly = noteReadOnly;
	    }
	    
	    public final String reloadNotes() {
	        loadNotes(siteVisit.getId());
	        return FacesUtil.getCurrentPage(); // stay on same page
	    }
	    
	    public final void closeDialog(ActionEvent actionEvent) {
	    	tempComment = null;
	    	noteReadOnly = false;
	        FacesUtil.returnFromDialogAndRefresh();
	    }
	    
	    public final SiteVisitNote getModifyComment() {
	        return modifyComment;
	    }

	    public final void setModifyComment(SiteVisitNote modifyComment) {
	        this.modifyComment = modifyComment;
	    }
	    
		public String getFacilityIdForStackTestVisitType() {
			return facilityIdForStackTestVisitType;
		}

		public void setFacilityIdForStackTestVisitType(
				String facilityIdForStackTestVisitType) {
			this.facilityIdForStackTestVisitType = facilityIdForStackTestVisitType;
		}
		
		public final StackTest[] getStackTestList() {
	    	return stackTestList;
	    }
		
		public void setVisitDate(Timestamp visitDate) {
	        this.visitDate = visitDate;
	    }
		
		public Timestamp getVisitDate() {
	        return visitDate;
	    }
		 public List<Evaluator> getWitnesses() {
		        if(stackTestList != null && stackTestList.length > 0) {
		            return stackTestList[0].getWitnesses();
		        }
		        ArrayList<Evaluator> eList = new ArrayList<Evaluator>();
		        return eList;
		    }
	
	private void handleBadDetail() {
		((SimpleMenuItem) FacesUtil
				.getManagedBean("menuItem_siteVisitDetail"))
				.setDisabled(true);
		StackTests sts = (StackTests) FacesUtil
				.getManagedBean("stackTests");
		SiteVisitSearch svSearch = (SiteVisitSearch) FacesUtil
				.getManagedBean("siteVisitSearch");
		svSearch.search();
		sts.setPopupRedirectOutcome("siteVisit.search");
		blankOutPage = true;
	}
		 
	private void handleBadDetailAndRedirect() {
		handleBadDetail();
		StackTests sts = (StackTests) FacesUtil
				.getManagedBean("stackTests");
		sts.getPopupRedirect();
	}
	
	public DefSelectItems getSvBasicUsersDef() {
		DefSelectItems allUsers = BasicUsersDef.getData().getItems();
		DefSelectItems unselectedUsers = new DefSelectItems();
		List<Integer> witnesses = new ArrayList<Integer>();

		// create a list of already selected witnesses
		for (Evaluator witness : siteVisit.getEvaluators()) {
			if (witness.getEvaluator() != null)
				witnesses.add(witness.getEvaluator());
		}

		// create a list of available witnesses i.e., allUsers minus already
		// selected witnesses
		for (SelectItem user : allUsers.getCurrentItems()) {
			if (!witnesses.contains((Integer) user.getValue()))
				unselectedUsers.add(user.getValue(), user.getLabel(), false);
		}
		return unselectedUsers;
	}
	
	//allow admin to delte orphaned site visits of type stack test
	public boolean isOrphanedSiteVisit() {
 		if(siteVisit.isStackTest()
 			&& isStars2Admin()
 			&& stackTestList != null && stackTestList.length == 0)
 					return true;
 		else
 			return false;
	}
}