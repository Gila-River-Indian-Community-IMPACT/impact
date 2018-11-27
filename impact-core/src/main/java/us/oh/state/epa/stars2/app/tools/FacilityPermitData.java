package us.oh.state.epa.stars2.app.tools;

import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;

public class FacilityPermitData extends FacilityList {

    private String permitNbr;
    private String perDueDateCd;
    private String perDueDateYear;
    private List<String> permitNumbers;
    private List<Permit> permits;
    private Facility facility;

    private boolean _init;

    public FacilityPermitData(FacilityList baseData) {

        if (baseData == null) {
            return;
        }

        setFpId(baseData.getFpId());
        setPermitClassCd(baseData.getPermitClassCd());
        setPermitStatusCd(baseData.getPermitStatusCd());
        setVersionId(baseData.getVersionId());
        setFacilityId(baseData.getFacilityId());
        setPhyAddr(baseData.getPhyAddr());
        setDoLaaCd(baseData.getDoLaaCd());
        setOperatingStatusCd(baseData.getOperatingStatusCd());
        setReportingTypeCd(baseData.getReportingTypeCd());
        setName(baseData.getName());

        if (permits == null) {
            permits = new ArrayList<Permit>();
        }
    }

    private void init() throws DAOException {

        if (_init) {
            return;
        }

        try {

            FacilityService facilityBO = ServiceFactory.getInstance().getFacilityService();
            PermitService permitBO = ServiceFactory.getInstance().getPermitService();

            if (facility == null) {
                facility = facilityBO.retrieveFacility(getFpId());
            }
            if (permits == null) {
                permits = new ArrayList<Permit>();
            }

            for (String permitNbr : permitNumbers) {
                Permit permit = permitBO.retrievePermit(permitNbr);
                permits.add(permit);
            }
        }
        catch (DAOException daoe) {
            logger.error("Unexpected DAOException: " + daoe.getMessage());
            throw daoe;
        }
        catch (Exception e) {
            String error = "Unexpected Exception: " + e.getMessage();
            logger.error(error, e);
            throw new DAOException(error, e);
        }
    }

    public final String getPermitNumber() {
        return permitNbr;
    }

    public final void setPermitNumber(String permitNbr) {
        this.permitNbr = permitNbr;
    }

    public final String getPERDueDateCd() {
        return perDueDateCd;
    }

    public final void setPERDueDateCd(String code) {
        perDueDateCd = code;
    }

    public final String getPERDueDateYear() {
        return perDueDateYear;
    }

    public final void setPERDueDateYear(String year) {
        perDueDateYear = year;
    }

    public final Permit[] getPermits() {
        return permits.toArray(new Permit[0]);
    }

    public final void addPermit(Permit permit) {
        if (permits == null) {
            permits = new ArrayList<Permit>();
        }
        if (permit != null) {
            permits.add(permit);
        }
    }

    public final void addPermitNumber(String permitNbr) {
        if (permitNumbers == null) {
            permitNumbers = new ArrayList<String>();
        }
        if (permitNbr != null && permitNbr.length() > 0) {
            permitNumbers.add(permitNbr);
        }
    }

    public final Contact getPrimaryContact() throws DAOException {
        init();
        return facility.getPrimaryContact();
    }

    public final Facility getFacility() throws DAOException {
        init();
        return facility;
    }
}
