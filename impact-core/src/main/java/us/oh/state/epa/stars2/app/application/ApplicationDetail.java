package us.oh.state.epa.stars2.app.application;

import java.rmi.RemoteException;

import javax.faces.event.ActionEvent;

import us.oh.state.epa.stars2.database.dbObjects.application.PBRNotification;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.RPCRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplication;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.application.ApplicationDetailCommon;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

public class ApplicationDetail extends ApplicationDetailCommon {

	private static final long serialVersionUID = 3976250683160792764L;

	@Override
	public final boolean isOkToUpdateFacilityProfile() {
		// facility inventory may be updated for non-submitted PTIO or TV apps
		return (!readOnly
				&& application != null
				&& (application instanceof PTIOApplication
						|| application instanceof TVApplication
						|| application instanceof RPCRequest 
						|| application instanceof PBRNotification)
				&& application.getSubmittedDate() == null);
	}

	@Override
	public boolean isOkToSyncEUsWithProfile() {
		return (isInternalApp() && application != null
				&& application instanceof PBRNotification && application
					.getSubmittedDate() != null);
	}

	public final void updateFacilityProfile(ActionEvent actionEvent) {
		if (application != null) {
			String infoMsg = "The application is now associated with "
					+ "the current version of the Facility Inventory.";
			try {
				reloadApplicationWithAllEUs();
				if (!getApplicationService().synchAppWithCurrentFacilityProfile(
						application)) {
					infoMsg = "The application was already associated with "
							+ "the current version of the Facility Inventory. No change was made.";
				}
				reloadApplicationSummary();
				FacesUtil.returnFromDialogAndRefresh();
				DisplayUtil.displayInfo(infoMsg);
			} catch (RemoteException re) {
				handleException(re);
				reloadApplicationSummary();
			}
		}
	}
	public boolean isNSRAdmin() {
		return InfrastructureDefs.getCurrentUserAttrs().isNSRAdminUser();
	}
	
	public boolean isStars2Admin() {
		return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin();
	}	
}
