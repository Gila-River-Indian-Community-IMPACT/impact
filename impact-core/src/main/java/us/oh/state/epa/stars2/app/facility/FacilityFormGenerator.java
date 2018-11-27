package us.oh.state.epa.stars2.app.facility;

import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentGenerationUtil;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

/**
 * Builds a form by combining facility data, emissions unit data, and Permit
 * data with an MS Word XML template.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1
 */
public class FacilityFormGenerator extends AppBase {
    private String templateFileName;
    private String outputFileName;
    private Facility facility;
    private String lastErrors;

    public FacilityFormGenerator() {
        super();
    }
    
    /** Set the location of the form's template. */
    public final void setTemplateFileName(String template) {
        templateFileName = template;
    }

    /** Get the location of the form's template. */
    public final String getTemplateFileName() {
        return templateFileName;
    }

    /** Set the location of the completed form. */
    public final void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    /** Get the location of the completed form. */
    public final String getOutputFileName() {
        return outputFileName;
    }

    /** Set the facility for which a form will be generated. */
    public final void setFacility(Facility facility) {
        this.facility = facility;
    }

    /**
     * Get the facility for which a form will be (or has most recently been)
     * generated.
     */
    public final Facility getFacility() {
        return facility;
    }

    /**
     * Get the errors from the last call to
     * {@link #generateForm() generateForm()}.
     */
    public final String getLastErrors() {
        return lastErrors;
    }

    /** Generate a form for a facility given a template and an output file name. */
    public final String generateForm() throws Exception {
        String ret = null;

        if (facility != null) {
            StringBuffer errors = new StringBuffer();

            try {

                DocumentGenerationBean facilityBean = new DocumentGenerationBean();

                String fid = facility.getFacilityId();
                if (fid == null || fid.length() < 1) {
                    fid = " ";
                }
                facilityBean.getProperties().put("facility_id", fid);

                String fName = facility.getName();
                if (fName == null || fName.length() < 1) {
                    fName = " ";
                }
                facilityBean.getProperties().put("facility_name", fName);

                String fAddr = " ";
                Address facilityAddr = facility.getPhyAddr();
                if (facilityAddr != null) {
                    fAddr = facilityAddr.toString();
                }
                facilityBean.getProperties().put("facility_addr", fAddr);

                Contact primaryContact = facility.getPrimaryContact();
                StringBuffer contactName = new StringBuffer();
                String cAddr = " ";
                String cPhone = " ";
                String cEmail = " ";
                String cEmail2 = " ";
                if (primaryContact != null) {
                    if (primaryContact.getTitleCd() != null
                            && primaryContact.getTitleCd().length() > 0) {
                        contactName.append(primaryContact.getTitleCd() + " ");
                    }
                    if (primaryContact.getFirstNm() != null
                            && primaryContact.getFirstNm().length() > 0) {
                        contactName.append(primaryContact.getFirstNm() + " ");
                    }
                    if (primaryContact.getFirstNm() != null
                            && primaryContact.getFirstNm().length() > 0) {
                        contactName.append(primaryContact.getLastNm());
                    }
                    if (primaryContact.getSuffixCd() != null) {
                        contactName.append(", " + primaryContact.getSuffixCd());
                    }
                    if (primaryContact.getPhoneNo() != null
                            && primaryContact.getPhoneNo().length() > 0) {
                        cPhone = primaryContact.getPhoneNo();
                    }
                    if (primaryContact.getEmailAddressTxt() != null
                            && primaryContact.getEmailAddressTxt().length() > 0) {
                        cEmail = primaryContact.getEmailAddressTxt();
                    }
                    if (primaryContact.getEmailAddressTxt2() != null
                            && primaryContact.getEmailAddressTxt2().length() > 0) {
                        cEmail2 = primaryContact.getEmailAddressTxt2();
                    }
                    Address contactAddr = primaryContact.getAddress();
                    if (contactAddr != null) {
                        cAddr = contactAddr.toString();
                    }
                }
				facilityBean.getProperties().put("facilty_contact", contactName.toString());
				facilityBean.getProperties().put("facility_contact_addr", cAddr);
				facilityBean.getProperties().put("facility_contact_phone", cPhone);
				facilityBean.getProperties().put("facility_contact_email", cEmail);
				facilityBean.getProperties().put("facility_contact_email_secondary", cEmail2);

                // ...and then fetch the facility's emission units.
                ArrayList<DocumentGenerationBean> eus = new ArrayList<DocumentGenerationBean>();
                EmissionUnit[] euArray = facility.getEmissionUnits().toArray(
                        new EmissionUnit[0]);

                // For each emission unit...
                for (int i = 0; i < euArray.length; i++) {
                    // ...fetch the emission unit's data.
                    DocumentGenerationBean euBean = new DocumentGenerationBean();

                    String eu_id = " ";
                    if (euArray[i].getEpaEmuId() != null
                            && euArray[i].getEpaEmuId().length() > 0) {
                        eu_id = euArray[i].getEpaEmuId();
                    }
                    euBean.getProperties().put("eu_id", eu_id);

                    String eu_desc = " ";
                    if (euArray[i].getEuDesc() != null
                            && euArray[i].getEuDesc().length() > 0) {
                        eu_desc = euArray[i].getEuDesc();
                    }
                    euBean.getProperties().put("eu_desc", eu_desc);

                    String eu_company_id = " ";
                    if (euArray[i].getCompanyId() != null
                            && euArray[i].getCompanyId().length() > 0) {
                        eu_company_id = euArray[i].getCompanyId();
                    }
                    euBean.getProperties().put("eu_company_id", eu_company_id);
                    eus.add(euBean);
                }
                facilityBean.getChildCollections().put("eus", eus);

                // Stuff the facility and eu data into the template.
                java.io.FileReader fi = new java.io.FileReader(templateFileName);
                StringBuffer template = new StringBuffer();
                int len;
                char[] buf = new char[4096];
                while ((len = fi.read(buf)) > 0) {
                    template.append(buf, 0, len);
                }
                fi.close();

                FileOutputStream fos = new FileOutputStream(outputFileName);
                new DocumentGenerationUtil().generate(template.toString(), fos,
                        facilityBean);
                fos.close();
                ret = SUCCESS;
            } catch (RemoteException re) {
                logger.error(re.getMessage(), re);
                DisplayUtil.displayError("System error. Please contact system administrator");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                DisplayUtil.displayError("System error. Please contact system administrator");
            } finally {
                lastErrors = errors.toString();
            }
        }
        return ret;
    }
}
