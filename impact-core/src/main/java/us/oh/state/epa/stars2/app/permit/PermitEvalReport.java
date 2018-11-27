package us.oh.state.epa.stars2.app.permit;

import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentGenerationUtil;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

/**
 * Builds Permit Evaluation Report (PER) forms by combining facility data,
 * emissions unit data, and PTIO data with an MS Word XML template.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1
 */
public class PermitEvalReport extends AppBase {
    private Collection<String> facilityIds;
    private String lastErrors;
	private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    public PermitEvalReport() {
        super();
    }
    
    /** Set the facility ids for which PERs will be generated. */
    public final void setFacilityIds(Collection<String> Ids) {
        facilityIds = Ids;
    }

    /**
     * Get the facility ids for which PERs will be (or have recently been)
     * generated.
     */
    public final Collection<String> getFacilityIds() {
        return facilityIds;
    }

    /**
     * Get the error messages resulting from the last call to
     * {@link #generatePERs()}. Each call to {@link #generatePERs()} begins by
     * resetting the error messages.
     */
    public final String getLastErrors() {
        return lastErrors;
    }

    /** Generate a PER for each facility id in the facilityIds collection. */
    public final String generatePERs() throws Exception {
        String ret = "Failure";

        if (facilityIds != null) {
            lastErrors = null;
            StringBuffer errors = new StringBuffer();

            try {
                // For each facility...
                Iterator<String> it = facilityIds.iterator();
                while (it.hasNext()) {

                    // ...fetch the basic facility data...
                    DocumentGenerationBean facilityBean = new DocumentGenerationBean();

                    String fid = it.next();
                    Facility facility = getFacilityService().retrieveFacility(fid);
                    if (facility == null) {
                        // ERROR!!!
                        errors.append("PER ERROR: Facility ID = " + fid
                                + ": No facility with this ID found.\n");
                        continue;
                    }
                    facilityBean.getProperties().put("facility_id", fid);

                    String fName = facility.getName();
                    if (fName == null || fName.length() < 1) {
                        // ERROR!!!
                        errors.append("PER ERROR: Facility ID = " + fid
                                + ": Facility is missing a facility name.\n");
                        fName = " ";
                    }
                    facilityBean.getProperties().put("facility_name", fName);

                    Address facilityAddr = facility.getPhyAddr();
                    String fAddr = " ";
                    if (facilityAddr == null) {
                        // ERROR!!!
                        errors
                                .append("PER ERROR: Facility ID = "
                                        + fid
                                        + ": Facility is missing a facility address.\n");
                    } else {
                        fAddr = facilityAddr.toString();
                    }
                    facilityBean.getProperties().put("facility_addr", fAddr);

                    Contact primaryContact = facility.getPrimaryContact();
                    StringBuffer contactName = new StringBuffer();
                    String cAddr = " ";
                    String cPhone = " ";
                    String cEmail = " ";
                    String cEmail2 = " ";
                    if (primaryContact == null) {
                        // ERROR!!!
                        errors.append("PER ERROR: Facility ID = " + fid
                                + ": Facility is missing a primary contact.\n");
                        contactName.append(" ");
                    } else {
                        String contactLName = primaryContact.getLastNm();
                        String contactFName = primaryContact.getFirstNm();
                        if (contactLName == null || contactLName.length() < 1
                                || contactFName == null
                                || contactFName.length() < 1) {
                            // ERROR!!!
                            errors
                                    .append("PER ERROR: Facility ID = "
                                            + fid
                                            + ": Facility is missing a primary contact first or last name.\n");
                        }
                        if (primaryContact.getTitleCd() != null
                                && primaryContact.getTitleCd().length() > 0) {
                            contactName.append(primaryContact.getTitleCd()
                                    + " ");
                        }
                        contactName.append(contactFName + " " + contactLName);
                        if (primaryContact.getSuffixCd() != null) {
                            contactName.append(", "
                                    + primaryContact.getSuffixCd());
                        }
                        cPhone = primaryContact.getPhoneNo();
                        if (cPhone == null || cPhone.length() < 1) {
                            cPhone = " ";
                        }
                        cEmail = primaryContact.getEmailAddressTxt();
                        if (cEmail == null || cEmail.length() < 1) {
                            cEmail = " ";
                        }
						cEmail2 = primaryContact.getEmailAddressTxt2();
						if (cEmail2 == null || cEmail2.length() < 1) {
							cEmail2 = " ";
						}

                        Address contactAddr = primaryContact.getAddress();
                        if (contactAddr == null) {
                            // ERROR!!!
                            errors
                                    .append("PER ERROR: Facility ID = "
                                            + fid
                                            + ": Facility is missing a primary contact address.\n");
                        } else {
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
                    EmissionUnit[] euArray = facility.getEmissionUnits()
                            .toArray(new EmissionUnit[0]);

                    // For each emission unit...
                    for (int i = 0; i < euArray.length; i++) {
                        // ...fetch the emission unit's data.
                        DocumentGenerationBean euBean = new DocumentGenerationBean();
                        euBean.getProperties().put("eu_id",
                                euArray[i].getEpaEmuId());
                        euBean.getProperties().put("eu_desc",
                                euArray[i].getEuDesc());
                        euBean.getProperties().put("eu_company_id",
                                euArray[i].getCompanyId());
                        eus.add(euBean);
                    }
                    facilityBean.getChildCollections().put("eus", eus);

                    // Locate the MS WordML XML template for the PER.
                    String templateFile = "c:\\Documents and Settings\\DAPC_User\\PER.xml";

                    // Stuff the facility and eu data into the template.
                    java.io.FileReader fi = new java.io.FileReader(templateFile);
                    StringBuffer template = new StringBuffer();
                    int len;
                    char[] buf = new char[4096];
                    while ((len = fi.read(buf)) > 0) {
                        template.append(buf, 0, len);
                    }
                    fi.close();

                    String outputFile = "c:\\Documents and Settings\\DAPC_User\\"
                            + fid + "-PER.xml";
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    new DocumentGenerationUtil().generate(template.toString(),
                            fos, facilityBean);
                    fos.close();
                }
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
        } else {
            lastErrors = "PER ERROR: No facilities requested.\n";
        }
        return ret;
    }
}
