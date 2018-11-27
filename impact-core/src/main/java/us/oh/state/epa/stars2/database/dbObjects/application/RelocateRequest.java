package us.oh.state.epa.stars2.database.dbObjects.application;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.RelocationDispositionDef;
import us.oh.state.epa.stars2.def.RelocationDispositionRPSDef;
import us.oh.state.epa.stars2.def.RelocationDispositionSPADef;
import us.oh.state.epa.stars2.def.RelocationJFODef;
import us.oh.state.epa.stars2.def.RelocationTypeDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

@SuppressWarnings("serial")
public class RelocateRequest extends Application {

    private Integer requestId;
    private boolean formComplete=false;
    private boolean facilityCompliant=false;
    private boolean sitePreApproved=false;
    private String jfoRecommendation="";
    private String requestDisposition="";
    private String futureAddress;           //up to 150 characters
    private String targetCountyCd;
    private String specialText;             //up to 2500 characters
    private List<RelocationAddtlAddr> additionalAddresses;
    private transient List<RelocationAddtlAddr> allAddresses;


	private Permit permit;
    private int userId;
    private boolean newRecord;
    private int requestLastModified;
    
    private Attachment[] attachments;
    
    public RelocateRequest() {
    	super();
    }

    public int getRequestLastModified() {
        return requestLastModified;
    }

    public void setRequestLastModified(int requestLastModified) {
        this.requestLastModified = requestLastModified;
    }

    public boolean isNewRecord() {
        return newRecord;
    }

    public void setNewRecord(boolean newRecord) {
        this.newRecord = newRecord;
    }


    public int getUserId() {
        return userId;
    }


    public void setUserId(int userId) {
        this.userId = userId;
    }


    public Integer getRequestId() {
        return requestId;
    }


    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public void setApplicationTypeCD(String requestType) {
        //print out the previous value of this
        super.setApplicationTypeCD(requestType);
        if (requestType.equals(RelocationTypeDef.RELOCATE_TO_PREAPPROVED_SITE)) {
            setTargetCountyCd(null);
            setSpecialText(null);
            setJfoRecommendation(null);
            setFutureAddress(null);
        } else if (requestType.equals(RelocationTypeDef.SITE_PRE_APPROVAL)) {
            setSitePreApproved(false);
        }
    }


    public boolean isFormComplete() {
        return formComplete;
    }


    public void setFormComplete(boolean formComplete) {
        this.formComplete = formComplete;
    }


    public boolean isFacilityCompliant() {
        return facilityCompliant;
    }


    public void setFacilityCompliant(boolean facilityCompliant) {
        this.facilityCompliant = facilityCompliant;
    }


    public boolean isSitePreApproved() {
        return sitePreApproved;
    }


    public void setSitePreApproved(boolean sitePreApproved) {
        this.sitePreApproved = sitePreApproved;
    }


    public String getJfoRecommendation() {
        return jfoRecommendation;
    }


    public void setJfoRecommendation(String jfoRecommendation) {
        this.jfoRecommendation = jfoRecommendation;
    }

    public String getFutureAddress() {
        return futureAddress;
    }


    public void setFutureAddress(String futureAddress) {
        this.futureAddress = futureAddress;
    }

    public String getSpecialText() {
        return specialText;
    }


    public void setSpecialText(String specialText) {
        this.specialText = specialText;
    }

    public final void populate(java.sql.ResultSet rs) {
        try {
            super.populate(rs);
            
            try {
                setRequestLastModified(AbstractDAO.getInteger(rs, "pr_lm"));
            } catch (Exception e) {
                logger.error("Unable to retrieve request LM",e);
            }
            
            try {
                setLastModified(AbstractDAO.getInteger(rs, "pa_lm"));
            } catch (Exception e) {
                logger.error("Unable to retrieve request App LM",e);
            }
            
            try {
                setRequestId(AbstractDAO.getInteger(rs, "pr_request_id"));
            } catch (Exception e) {
                logger.error("Unable to retrieve Request ID",e);
            }
            
            if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
            	FacilityService facilityBO = ServiceFactory.getInstance().getFacilityService();
            	Facility facility = facilityBO.retrieveFacility(rs.getString("facility_id")); //this should get set by the parent method
           
            	this.setFacility(facility);
           
            	if (facility==null) {
            		logger.error("Unable to retrieve facility object for Relocate Request");
            		throw new SQLException("Failed to retrieve facility object for corresponding Relocate Request");
            	}
            }
            
            setFormComplete(AbstractDAO.translateIndicatorToBoolean(rs.getString("pr_form_complete")));
            setFacilityCompliant(AbstractDAO.translateIndicatorToBoolean(rs.getString("pr_facility_compliant")));
            setSitePreApproved(AbstractDAO.translateIndicatorToBoolean(rs.getString("pr_site_approved")));
            setJfoRecommendation(rs.getString("pr_jfo_recommendation_cd"));
            if (getJfoRecommendation()==null) {
                setJfoRecommendation("");
            }
            setRequestDisposition(rs.getString("pr_request_disposition_cd"));
            setFutureAddress(rs.getString("pr_future_address"));
            setTargetCountyCd(rs.getString("pr_target_county_cd"));
            setSpecialText(rs.getString("pr_special_text"));
            setUserId(AbstractDAO.getInteger(rs, "pr_user_id"));
            try {
                setReceivedDate(rs.getTimestamp("pr_date_received"));
            } catch (Exception e) {
                //ignore this - the PA_ALL_APPLICATIONS view doesn't need this field
                logger.debug("Optional field date_received not found",e);
            }
            //setApplicationID(AbstractDAO.getInteger(rs,"application_id"));    this should get set by the super method
            setDirty(false);
        } catch (SQLException sqle) {
            logger.error("Required field missing: "+sqle,sqle);
        } catch (ServiceFactoryException sfe) {
            logger.error("Unable to retrieve facility object",sfe);
        } catch (DAOException dao) {
            logger.error("Unable to retrieve facility object",dao);
        } catch (RemoteException re) {
            logger.error("Unable to retrieve facility object",re);
        } catch (Exception e) {
            logger.error("Some other exception found trying to populate",e);
        } finally {
            newObject = false;
        }

    }


    public String getRequestDisplayId() {
        String mask = "0000000";
        java.text.DecimalFormat df = new java.text.DecimalFormat(mask);
        return "REL"+df.format(getRequestId());
    }

    public String getTargetCountyCd() {
        return targetCountyCd;
    }


    public void setTargetCountyCd(String targetCountyCd) {
        this.targetCountyCd = targetCountyCd;
    }


    public String getRequestDisposition() {
        return requestDisposition;
    }


    public void setRequestDisposition(String requestDisposition) {
        this.requestDisposition = requestDisposition;
    }

    public Permit getPermit() {
        return permit;
    }


    public void setPermit(Permit permit) {
        this.permit = permit;
    }
    public DefSelectItems getJFODef() {
        return RelocationJFODef.getData().getItems();
     }
     
     public DefSelectItems getDispositionDef() {
         DefData dd = new DefData();
         try {
             if (this.getApplicationTypeCD().equals(RelocationTypeDef.RELOCATE_TO_PREAPPROVED_SITE)) {
                 dd = RelocationDispositionRPSDef.getData();
             } else if (this.getApplicationTypeCD().equals(RelocationTypeDef.SITE_PRE_APPROVAL)) {
                 if (getJfoRecommendation() != null && getJfoRecommendation().equals(RelocationJFODef.APPROVE)) {
                     dd = RelocationDispositionSPADef.getApprovedData();
                 } else if (getJfoRecommendation() != null && getJfoRecommendation().equals(RelocationJFODef.APPROVE_WITH_CONDITIONS)) {
                     dd = RelocationDispositionSPADef.getApprovedCondData();
                 } else if (getJfoRecommendation() != null && getJfoRecommendation().equals(RelocationJFODef.DENY)) {
                     dd = RelocationDispositionSPADef.getDeniedData();
                 }
             } else if (this.getApplicationTypeCD().equals(RelocationTypeDef.INTENT_TO_RELOCATE)) {
                 logger.debug("setting up disposition def for ITR reloc type");
                 if (getJfoRecommendation() != null && getJfoRecommendation().equals(RelocationJFODef.APPROVE)) {
                     dd = RelocationDispositionDef.getApprovedData();
                 } else if (getJfoRecommendation() != null && getJfoRecommendation().equals(RelocationJFODef.APPROVE_WITH_CONDITIONS)) {
                     dd = RelocationDispositionDef.getApprovedCondData();
                 } else if (getJfoRecommendation() != null && getJfoRecommendation().equals(RelocationJFODef.DENY)) {
                     dd = RelocationDispositionDef.getDeniedData();
                 }
             } 
         } catch (Exception e) {
            logger.error("Error trying to create def pick lists\n"+e);
         }
         return dd.getItems();
      }
     
     public ValidationMessage[] validate() {
         return null;
     }


    public String getPermitIdStr() {
        String ret = "";
        if (getPermitId()>0 ) {
            ret = Integer.toString(getPermitId());
        }

        return ret;
    }
    
    public boolean isIncludeIssuance() {
        // only show Issuance panel if issuance is ITR or SPA && its submitted
        // and it has a JFOReco
        if (getApplicationTypeCD().equals(RelocationTypeDef.INTENT_TO_RELOCATE) 
        		|| getApplicationTypeCD().equals(RelocationTypeDef.SITE_PRE_APPROVAL)) {
            if (getSubmittedDate() !=null && getJfoRecommendation()!=null) {
                return true;
            }
        }
        return false;
    }

    public Attachment[] getAttachments() {
        return attachments;
    }

    public void setAttachments(Attachment[] attachments) {
    	this.attachments = attachments;
    }

    public final RelocationAddtlAddr[] getAllAddresses() {
    	allAddresses = new ArrayList<RelocationAddtlAddr>();
    	if (futureAddress != null) {
    		RelocationAddtlAddr mainAddress = new RelocationAddtlAddr();
    		mainAddress.setRequestId(requestId);
    		mainAddress.setFutureAddress(futureAddress);
    		mainAddress.setTargetCountyCd(targetCountyCd);
    		allAddresses.add(mainAddress);
    	}
    	for (RelocationAddtlAddr addr : getAdditionalAddresses()) {
    		allAddresses.add(addr);
    	}

    	return allAddresses.toArray(new RelocationAddtlAddr[0]);
    }

    public final void setAllAddresses(RelocationAddtlAddr[] allAddresses) {
    	logger.debug(" setting allAddresses");
    	additionalAddresses = null;
    	if (allAddresses != null && allAddresses.length > 0) {
    		logger.debug(" main future address = " + allAddresses[0].getFutureAddress());
    		futureAddress = allAddresses[0].getFutureAddress();
    		targetCountyCd = allAddresses[0].getTargetCountyCd();
    		if (allAddresses.length > 1) {
    			if (additionalAddresses == null) {
    				additionalAddresses = new ArrayList<RelocationAddtlAddr>();
    			}
    			for (int i = 1; i<allAddresses.length; i++) {
    				logger.debug(" additional future address = " + allAddresses[i].getFutureAddress());
    				additionalAddresses.add(allAddresses[i]);
    			}
    		}
    	} else {
    		logger.debug(" Skipped reading allAddresses");
    	}
    }
    
    public final RelocationAddtlAddr[] getAdditionalAddresses() {
    	if (additionalAddresses == null) {
    		additionalAddresses = new ArrayList<RelocationAddtlAddr>();
    	}
		return additionalAddresses.toArray(new RelocationAddtlAddr[0]);
	}
    
    public final void addAdditionalAddress(RelocationAddtlAddr address) {
    	if (additionalAddresses == null) {
    		additionalAddresses = new ArrayList<RelocationAddtlAddr>();
    	}
    	additionalAddresses.add(address);
    }

	public final void setAdditionalAddresses(RelocationAddtlAddr[] additionalAddresses) {
		this.additionalAddresses = new ArrayList<RelocationAddtlAddr>();
		if (additionalAddresses != null) {
			for (RelocationAddtlAddr addr : additionalAddresses) {
				this.additionalAddresses.add(addr);
			}
		}
	}
}
