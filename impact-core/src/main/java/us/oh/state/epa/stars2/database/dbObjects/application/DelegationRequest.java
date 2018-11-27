package us.oh.state.epa.stars2.database.dbObjects.application;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;


@SuppressWarnings("serial")
public class DelegationRequest extends Application  {
    private transient Timestamp effectiveDate;
    // declare dummy version of effectiveDate for XML serialization
    private long effectiveDateLong;
    private String origFirstName;
    private String origLastName;
    private String origTitle;
    private String origAddress1;
    private String origAddress2;
    private String origCity;
    private String origStateCd="OH";
    private String origZip;
    private String origPhoneNo;
    
    private String requestTypeCd="DLG";
    
    private boolean facCorpQualified=false;
    private boolean fac25MilOr250EmpQualified=false;
    
    /*
     * NOTE: To minimize db changes the 'fac_25mil_qualified' column
     * is being used to store the request disposition (varchar(1)).  The 
     * fac_250emp_qualified column is now used to store an 'either/or' value of 
     * whether the facility has 25Million $ in revenue or 250 employees.
     * 
     * Change approved by Erica Engel-Ishida July 2008.
     */
    private String requestDisposition=null;
    private boolean facSuccessorQualified=false;
    
    private String assigFirstName;
    private String assigLastName;
    private String assigTitle;
    private String assigAddress1;
    private String assigAddress2;
    private String assigCity;
    private String assigStateCd="OH";
    private String assigZip;
    
    private boolean newRecord;
    private int delegationLastModified;
    
    private Attachment[] attachments;
    
    public Attachment[] getAttachments() {
        return attachments;
    }

    public void setAttachments(Attachment[] attachments) {
        this.attachments = attachments;
    }

    public boolean isTitleV() {
        if (getFacility().getPermitClassCd().equals(PermitClassDef.TV)) {
            logger.debug("Is TV Facility");
            return true;
        }
        logger.debug("is NOT tv facility");
        return false;
    } 
    
    public final void populate(java.sql.ResultSet rs) {
        try {
            super.populate(rs);
            setOrigFirstName(rs.getString("pd_originator_first_name"));
            setOrigLastName(rs.getString("pd_originator_last_name"));
            setOrigTitle(rs.getString("pd_originator_title"));
            setOrigAddress1(rs.getString("pd_originator_address1"));
            setOrigAddress2(rs.getString("pd_originator_address2"));
            setOrigCity(rs.getString("pd_originator_city"));
            setOrigStateCd(rs.getString("pd_originator_state_cd"));
            setOrigZip(rs.getString("pd_originator_zip"));
            setOrigPhoneNo(rs.getString("pd_originator_phone_no"));
            
            setFacCorpQualified(AbstractDAO.translateIndicatorToBoolean(rs.getString("pd_corporate_qualified")));
            setFac25MilOr250EmpQualified(AbstractDAO.translateIndicatorToBoolean(rs.getString("pd_fac_250emp_qualified")));
            setRequestDispositionCd(rs.getString("pd_fac_25mil_qualified"));
            setFacSuccessorQualified(AbstractDAO.translateIndicatorToBoolean(rs.getString("pd_successor_qualified")));
            
            setRequestTypeCd(rs.getString("pd_request_type_cd"));
            
            setAssigFirstName(rs.getString("pd_assignee_first_name"));
            setAssigLastName(rs.getString("pd_assignee_last_name"));
            setAssigTitle(rs.getString("pd_assignee_title"));
            setAssigAddress1(rs.getString("pd_assignee_address1"));
            setAssigAddress2(rs.getString("pd_assignee_address2"));
            setAssigCity(rs.getString("pd_assignee_city"));
            setAssigStateCd(rs.getString("pd_assignee_state_cd"));
            setAssigZip(rs.getString("pd_assignee_zip"));
            try {
                setEffectiveDate(rs.getTimestamp("pd_effective_date"));
            } catch (Exception e) {
                
            }try {
                setDelegationLastModified(AbstractDAO.getInteger(rs, "pd_lm"));
            } catch (Exception ex) {
                logger.error("*** no PD_LM found**** ", ex);
            }
            
            FacilityService facilityBO = ServiceFactory.getInstance().getFacilityService();
            Facility facility = facilityBO.retrieveFacility(rs.getString("facility_id")); //this should get set by the parent method
           
            this.setFacility(facility);
           
            if (facility==null) {
                logger.error("Unable to retrieve facility object for Relocate Request");
                throw new SQLException("Failed to retrieve facility object for corresponding Relocate Request");
            }
            
            setDirty(false);
        } catch (SQLException sqle) {
            logger.error("Required field missing: "+sqle,sqle);
        } catch (ServiceFactoryException sfe) {
            logger.error("Unable to retrieve facility object",sfe);
        } catch (DAOException dao) {
            logger.error("Unable to retrieve facility object",dao);
        } catch (RemoteException re) {
            logger.error("Unable to retrieve facility object",re);
        } finally {
            newObject = false;
        }

    }

    public Timestamp getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Timestamp effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public long getEffectiveDateLong() {
        return effectiveDateLong;
    }

    public void setEffectiveDateLong(long effectiveDateLong) {
        this.effectiveDateLong = effectiveDateLong;
    }

    public String getOrigFirstName() {
        return origFirstName;
    }

    public void setOrigFirstName(String origFirstName) {
        this.origFirstName = origFirstName;
    }

    public String getOrigLastName() {
        return origLastName;
    }

    public void setOrigLastName(String origLastName) {
        this.origLastName = origLastName;
    }

    public String getOrigTitle() {
        return origTitle;
    }

    public void setOrigTitle(String origTitle) {
        this.origTitle = origTitle;
    }

    public String getOrigAddress1() {
        return origAddress1;
    }

    public void setOrigAddress1(String origAddress1) {
        this.origAddress1 = origAddress1;
    }

    public String getOrigAddress2() {
        return origAddress2;
    }

    public void setOrigAddress2(String origAddress2) {
        this.origAddress2 = origAddress2;
    }

    public String getOrigCity() {
        return origCity;
    }

    public void setOrigCity(String origCity) {
        this.origCity = origCity;
    }

    public String getOrigStateCd() {
        return origStateCd;
    }

    public void setOrigStateCd(String origStateCd) {
        this.origStateCd = origStateCd;
    }

    public String getOrigZip() {
        return origZip;
    }

    public void setOrigZip(String origZip) {
        this.origZip = origZip;
    }

    public String getOrigPhoneNo() {
        return origPhoneNo;
    }

    public void setOrigPhoneNo(String origPhoneNo) {
        this.origPhoneNo = origPhoneNo;
    }

    public String getAssigFirstName() {
        return assigFirstName;
    }

    public void setAssigFirstName(String assigFirstName) {
        this.assigFirstName = assigFirstName;
    }

    public String getAssigLastName() {
        return assigLastName;
    }

    public void setAssigLastName(String assigLastName) {
        this.assigLastName = assigLastName;
    }

    public String getAssigTitle() {
        return assigTitle;
    }

    public void setAssigTitle(String assigTitle) {
        this.assigTitle = assigTitle;
    }

    public String getAssigAddress1() {
        return assigAddress1;
    }

    public void setAssigAddress1(String assigAddress1) {
        this.assigAddress1 = assigAddress1;
    }

    public String getAssigAddress2() {
        return assigAddress2;
    }

    public void setAssigAddress2(String assigAddress2) {
        this.assigAddress2 = assigAddress2;
    }

    public String getAssigCity() {
        return assigCity;
    }

    public void setAssigCity(String assigCity) {
        this.assigCity = assigCity;
    }

    public String getAssigStateCd() {
        return assigStateCd;
    }

    public void setAssigStateCd(String assigStateCd) {
        this.assigStateCd = assigStateCd;
    }

    public String getAssigZip() {
        return assigZip;
    }

    public void setAssigZip(String assigZip) {
        this.assigZip = assigZip;
    }

    public boolean isNewRecord() {
        return newRecord;
    }

    public void setNewRecord(boolean newRecord) {
        this.newRecord = newRecord;
    }

    public int getDelegationLastModified() {
        return delegationLastModified;
    }

    public void setDelegationLastModified(int delegationLastModified) {
        this.delegationLastModified = delegationLastModified;
    }

    public String getRequestTypeCd() {
        return requestTypeCd;
    }

    public void setRequestTypeCd(String requestTypeCd) {
        this.requestTypeCd = requestTypeCd;
    }

    public boolean isFacCorpQualified() {
        return facCorpQualified;
    }

    public void setFacCorpQualified(boolean facCorpQualified) {
        this.facCorpQualified = facCorpQualified;
    }

    public boolean isFac25MilOr250EmpQualified() {
        return fac25MilOr250EmpQualified;
    }

    public void setFac25MilOr250EmpQualified(boolean fac25MilOr250EmpQualified) {
        this.fac25MilOr250EmpQualified = fac25MilOr250EmpQualified;
    }

    public String getRequestDispositionCd() {
        return requestDisposition;
    }

    public void setRequestDispositionCd(String requestDisposition) {
        this.requestDisposition = requestDisposition;
    }

    public boolean isFacSuccessorQualified() {
        return facSuccessorQualified;
    }

    public void setFacSuccessorQualified(boolean facSuccessorQualified) {
        this.facSuccessorQualified = facSuccessorQualified;
    }

}
