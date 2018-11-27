package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;

public class PermitInfo implements java.io.Serializable {
    private Permit permit;
    private Facility currentFacility;

    private List<EmissionUnit> permittableEUs = new ArrayList<EmissionUnit>();

    public final Permit getPermit() {
        return permit;
    }

    public final void setPermit(Permit permit) {
        this.permit = permit;
    }

    public final Facility getCurrentFacility() {
        return currentFacility;
    }

    public final void setCurrentFacility(Facility currentFacility) {
        this.currentFacility = currentFacility;
    }

    public final List<EmissionUnit> getPermittableEUs() {
        return permittableEUs;
    }

    public final void setPermittableEUs(List<EmissionUnit> permittableEUs) {
        if (permittableEUs == null) {
            permittableEUs = new ArrayList<EmissionUnit>();
        }
        this.permittableEUs = permittableEUs;
    }
}
