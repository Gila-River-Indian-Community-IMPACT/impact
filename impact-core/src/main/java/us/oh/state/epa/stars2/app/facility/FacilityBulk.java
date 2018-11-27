package us.oh.state.epa.stars2.app.facility;

import javax.faces.event.ActionEvent;

import oracle.adf.view.faces.component.UIXTable;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRole;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;

public class FacilityBulk extends AppBase {
    private transient UIXTable table;
    private Contact contact;

    // for now
    private FacilityRole[] facilityRoles = new FacilityRole[13];

    private String[] roleDesc = { "CO EIS Reviewer", "CO Invoicer",
            "CO Revenue Adjuster", "DO/LAA Permit Writer",
            "DO/LAA FER/EIS Reviewer", "DO/LAA FER/EIS Approver",
            "CO Permit Approver", "DO/LAA Emissions Inventory Receiver",
            "CO Permit Issuer", "DO/LAA Reviewer/Approver",
            "CO Public Notice Generator", "DO/LAA Application Receiver",
            "CO Permit Reviewer" };

    public FacilityBulk() {
        super();
        // for now
        for (int i = 0; i < roleDesc.length; i++) {
            FacilityRole facilityRole = new FacilityRole();
            facilityRole.setFacilityRoleDsc(roleDesc[i]);
            facilityRoles[i] = facilityRole;
        }
    }

    public final UIXTable getTable() {
        return table;
    }

    public final void setTable(UIXTable table) {
        this.table = table;
    }

    public final void dialogDone() {
        return;
    }

    public final String startUpdateFacilityRoles() {
        return "dialog:bulkUpdateFacilityRoles";
    }

    public final FacilityRole[] getFacilityRoles() {
        return facilityRoles;
    }

    public final String applyUpdateFacilityRoles(ActionEvent actionEvent) {
        DisplayUtil.displayInfo("Facility roles updated successfully");
        FacesUtil.returnFromDialogAndRefresh();
        return SUCCESS;
    }

    public final void cancelUpdateFacilityRoles(ActionEvent actionEvent) {
        FacesUtil.returnFromDialogAndRefresh();
    }

    public final String startUpdatePrimaryContact() {
        contact = new Contact();
        return "dialog:bulkUpdateFacilityContact";
    }

    public final String startUpdateBillingContact() {
        contact = new Contact();
        return "dialog:bulkUpdateFacilityContact";
    }

    public final Contact getContact() {
        return contact;
    }

    public final void setContact(Contact contact) {
        this.contact = contact;
    }

    public final String applyUpdateFacilityContact(ActionEvent actionEvent) {
        DisplayUtil.displayInfo("Facility contact updated successfully");
        FacesUtil.returnFromDialogAndRefresh();
        return AppBase.SUCCESS;
    }

    public final void cancelUpdateFacilityContact(ActionEvent actionEvent) {
        FacesUtil.returnFromDialogAndRefresh();
    }
}
