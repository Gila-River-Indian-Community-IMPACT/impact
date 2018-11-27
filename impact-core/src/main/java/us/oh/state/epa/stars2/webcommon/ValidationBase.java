package us.oh.state.epa.stars2.webcommon;

import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;

import oracle.adf.view.faces.component.UIXShowDetail;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.webcommon.application.ApplicationDetailCommon;
import us.oh.state.epa.stars2.webcommon.ceta.StackTestDetailCommon;
import us.oh.state.epa.stars2.webcommon.compliance.ComplianceReportsCommon;
import us.oh.state.epa.stars2.webcommon.facility.FacilityProfileBase;

public class ValidationBase extends AppBase {
	
	private static final long serialVersionUID = -8960639644850368204L;

	public static final String FACILITY_TAG = "facility";
    public static final String REPORT_TAG = "report";
    public static final String APPLICATION_TAG = "application";
    public static final String PERMIT_TAG = "permit";
    public static final String COMPLIANCE_TAG = "compliance";
    public static final String STACK_TEST_TAG = "stacktest";
    public static final String ENFORCEMENT_TAG = "enforcement";
    public static final String FCE_TAG = "fce";
    public static final String CONTINUOUS_MONITOR_TAG = "continuousMonitor";
    private String  tag;
    protected String newValidationDlgReference;

    /**
     * This method retrieves newValidationDlgReference from
     * ValidationDlgReference in requestMap and uses it to select the correct
     * tree node.
     * 
     * The way to find correct tree node has to be implemented in each bean.
     * 
     * @return the referenceID in ValidationMessage
     */
    public String validationDlgAction() {
        newValidationDlgReference = (String) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap().remove(
                        "ValidationDlgReference");
        String validationDlgShowDetailClientID = (String) FacesContext
                .getCurrentInstance().getExternalContext().getRequestMap()
                .remove("ValidationDlgShowDetailClientID");
        
        if(null != tag && newValidationDlgReference != null && newValidationDlgReference.length() != 0) {
            StringTokenizer st = new StringTokenizer(newValidationDlgReference, ":");
            String subSystem = st.nextToken();
            
            if(!subSystem.equals(tag)) {
                if (subSystem.equals(FACILITY_TAG)) {
                    FacilityProfileBase fp = (FacilityProfileBase) FacesUtil
                    .getManagedBean("facilityProfile");
                    fp.setValidationDlgReference(newValidationDlgReference);
                    return fp.validationDlgAction();
                } else if (subSystem.equals(REPORT_TAG)) {
                        ValidationBase rd = (ValidationBase) FacesUtil
                        .getManagedBean("reportDetail");
                        rd.setValidationDlgReference(newValidationDlgReference);
                        return rd.validationDlgAction();
                } else if (subSystem.equals(APPLICATION_TAG)) {
                    ApplicationDetailCommon ad = (ApplicationDetailCommon) FacesUtil
                    .getManagedBean("applicationDetail");
                    ad.setValidationDlgReference(newValidationDlgReference);
                    return ad.validationDlgAction();
                } else if (subSystem.equals(COMPLIANCE_TAG)) {
                	ComplianceReportsCommon rd = (ComplianceReportsCommon) FacesUtil
                    .getManagedBean("complianceReport");
                	rd.setValidationDlgReference(newValidationDlgReference);
                    return rd.validationDlgAction();
                } else if (subSystem.equals(ENFORCEMENT_TAG)) {
                    ValidationBase rd = (ValidationBase) FacesUtil
                    .getManagedBean("enforcementActionDetail");
                    rd.setValidationDlgReference(newValidationDlgReference);
                    return rd.validationDlgAction();
                } else if (subSystem.equals(FCE_TAG)) {
                    ValidationBase rd = (ValidationBase) FacesUtil
                    .getManagedBean("fceDetail");
                    rd.setValidationDlgReference(newValidationDlgReference);
                    return rd.validationDlgAction();
                } else if (subSystem.equals(PERMIT_TAG)) {
                    ValidationBase rd = (ValidationBase) FacesUtil
                    .getManagedBean("permitDetail");
                    rd.setValidationDlgReference(newValidationDlgReference);
                    return rd.validationDlgAction();
                } else if (subSystem.equals(STACK_TEST_TAG)) {
                    StackTestDetailCommon sd = (StackTestDetailCommon) FacesUtil
                    .getManagedBean("stackTestDetail");
                    sd.setValidationDlgReference(newValidationDlgReference);
                    return sd.validationDlgAction();
                } else if (subSystem.equals(CONTINUOUS_MONITOR_TAG)) {
                    ValidationBase rd = (ValidationBase) FacesUtil
                    .getManagedBean("continuousMonitorDetail");
                    rd.setValidationDlgReference(newValidationDlgReference);
                    return rd.validationDlgAction();
                }
            }
        }
        
        if (validationDlgShowDetailClientID != null
                && validationDlgShowDetailClientID.length() > 0) {
            
            UIXShowDetail showDetail = (UIXShowDetail) FacesContext
                    .getCurrentInstance().getViewRoot().findComponent(
                            validationDlgShowDetailClientID);
            if (showDetail == null) {
                DisplayUtil
                        .displayError("cannot find show dialog selected in popup");
            } else {
                showDetail.setDisclosed(true);
            }
        }

        if(null == tag) {
            return newValidationDlgReference;
        }
        
        return null;
    }

    /**
     * This method need to implement in children. This method find out the
     * current tree path. Later it will save into ValidationDlgReference for
     * validationDlgAction to find out which tree node to use.
     * 
     * This return string is used to compare with the referenceID in 
     * ValidationMessage.
     * 
     * @return the current page outcome
     */
    public String getValidationDlgReference() {
        return FacesUtil.getCurrentPage();
    }

    /**
     * Store new value in request scope, it will be processed by
     * validationDlgAction()
     */
    @SuppressWarnings("unchecked")
    public void setValidationDlgReference(String validationDlgReference) {
        FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
                .put("ValidationDlgReference", validationDlgReference);
    }

    public String getValidationDlgShowDetailClientID() {
        return null;
    }

    /**
     * Store new value in request scope, it will be processed by
     * validationDlgAction()
     */
    @SuppressWarnings("unchecked")
    public void setValidationDlgShowDetailClientID(
            String validationDlgShowDetailClientID) {
        FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
                .put("ValidationDlgShowDetailClientID",
                        validationDlgShowDetailClientID);
    }
    
    public static void removeDuplicates(List<ValidationMessage> msgs) {
        // remove duplicates and maintain order.  Remove first instance
        int row = 0;
        ListIterator<ValidationMessage> it = msgs.listIterator(row);
        while(it.hasNext()) {
            row++;
            ValidationMessage vm = it.next();
            // see if it has a duplicate
            if(it.hasNext()) {
                while(it.hasNext()) {
                    ValidationMessage vm2 = it.next();
                    if(vm.getEuId() != null && vm2.getEuId() == null ||
                            vm2.getEuId() != null && vm.getEuId() == null) continue;   
                    if((vm.getEuId() == null || vm.getEuId() != null && vm.getEuId().equals(vm2.getEuId())) &&
                            vm.getMessage() != null && vm.getMessage().equals(vm2.getMessage())) {
                        it.remove();
                        row--;
                        it = msgs.listIterator(row);
                        break;
                    }
                }
                it = msgs.listIterator(row);
            }
        }
    }

    public boolean getEditMode() {
        return false;
    }

    public void setEditMode(boolean edit) {
    }

    public String getIdMap() {
        AppValidationMsg.buildMap();
        return "ok";
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
