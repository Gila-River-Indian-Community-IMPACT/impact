package us.oh.state.epa.stars2.app.facility;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import oracle.adf.view.faces.component.UIXTable;
import oracle.adf.view.faces.event.SelectionEvent;
import oracle.adf.view.faces.model.RowKeySet;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipment;
import us.oh.state.epa.stars2.database.dbObjects.facility.EgressPoint;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

public class TrEu extends AppBase {

	private static final long serialVersionUID = 4730275570196832520L;
	private String sourceFacilityId;
	private Integer sourceFpId;
	private Facility sourceFacility;
	private String targetFacilityId;
	private Integer targetFpId;
	private boolean changeStatus;
	private boolean isIncludeChild;
	private Timestamp euShutdownDate;

	// page table
	private TableSorter emusWrapper;
	private TableSorter cesWrapper;
	private TableSorter egpsWrapper;

	private List<ControlEquipment> controlEquips = new ArrayList<ControlEquipment>(
			0);
	private List<EgressPoint> egrPoints = new ArrayList<EgressPoint>(0);
	private List<EmissionUnit> emissionUnits = new ArrayList<EmissionUnit>(0);;

	private String popupRedirectOutcome;
	
	private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public TrEu() {
		this.resetTrEu();
	}

	public final void resetTrEu() {
		this.sourceFacilityId = "";
		this.targetFacilityId = "";
		this.changeStatus = false;
		this.euShutdownDate = null;
		this.isIncludeChild = true;
		sourceFacility = new Facility();
		resetWrapper();
	}

	private void resetWrapper() {
		if (emusWrapper != null) {
			if (emusWrapper.getTable() != null) {
				if (emusWrapper.getTable().getSelectionState() != null) {
					emusWrapper.getTable().getSelectionState().getKeySet().clear();
				}
			}
		}
		
		emusWrapper = new TableSorter();
		cesWrapper = new TableSorter();
		egpsWrapper = new TableSorter();
	}

	public final void loadEu(ValueChangeEvent valueChangeEvent) {

		setSourceFacilityId(valueChangeEvent.getNewValue().toString());

		resetWrapper();

		if (this.sourceFacilityId == null || this.sourceFacilityId.equals("")) {
			DisplayUtil.displayError("Source Facility ID is required.");
			return;
		}

		try {
			if (!Utility.isNullOrEmpty(sourceFacilityId)) {
				String format = "F%06d";

				int tempId;
				try {
					tempId = Integer.parseInt(sourceFacilityId);
					setSourceFacilityId(String.format(format, tempId));
				} catch (NumberFormatException nfe) {
				}
			}
			this.sourceFacility = getFacilityService().retrieveFacility(
					sourceFacilityId);

			if (this.sourceFacility == null) {
				DisplayUtil
						.displayError("Source Facility ID failed; Facility: ["
								+ sourceFacilityId + "] does not exist.");
				return;
			}

			this.sourceFpId = this.sourceFacility.getFpId();
			this.sourceFacility = getFacilityService().retrieveFacilityProfile(
					sourceFpId);

			if (this.sourceFacility.getEmissionUnits().isEmpty()) {
				DisplayUtil
						.displayError("Source Facility ID failed; Facility: ["
								+ sourceFacilityId + "] no Emission Units.");
				return;
			}

			setEpCeEpaEmuIds();
			this.emissionUnits = this.sourceFacility.getEmissionUnits();
			this.emusWrapper.setWrappedData(this.emissionUnits);
			this.controlEquips = this.sourceFacility.getControlEquips();
			this.egrPoints = this.sourceFacility.getEgressPointsList();

		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Load Facility failed");
			return;
		}

	}

	public final void submitTrEu(ActionEvent actionEvent) {
		if (this.isIncludeChild)
			selectEP();

		try {
			int currentUserId = InfrastructureDefs.getCurrentUserId();

			Facility targetFacility = getFacilityService().transferEmissionUnit(
					sourceFacility, targetFpId, changeStatus,
					this.euShutdownDate, currentUserId);

			FacilityProfile fp = (FacilityProfile) FacesUtil
					.getManagedBean("facilityProfile");
			fp.setFpId(targetFacility.getFpId());
			fp.submitProfile();

			this.popupRedirectOutcome = "facilityProfile";

			resetTrEu();
			DisplayUtil.displayInfo("Transfer equipment successfully.");

		} catch (DAOException e) {
			e.printStackTrace();
			DisplayUtil.displayError("Transfer equipment failed.");
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Transfer equipment failed.");
		}
		FacesUtil.returnFromDialogAndRefresh();

	}

	public void selectionEu(SelectionEvent action) {
		syncEmissionUnits();
		showCEEP();
	}

	public String confirm() {

		boolean hasError = false;

		if (this.sourceFacilityId == null || this.sourceFacilityId.equals("")) {
			DisplayUtil.displayError("Source Facility ID is required.");
			hasError = true;
		}

		if (this.targetFacilityId == null || this.targetFacilityId.equals("")) {
			DisplayUtil.displayError("Destination Facility ID is required.");
			hasError = true;
		}

		if (this.changeStatus && this.euShutdownDate == null) {
			DisplayUtil.displayError("Shutdown Date is required.");
			hasError = true;
		}

		if (hasError)
			return null;

		if (this.sourceFacilityId.equals(this.targetFacilityId)) {
			DisplayUtil
					.displayError("Destination Facility ID can not be the same as Source Facility ID.");
			return null;
		}

		if (this.sourceFacility == null) {
			DisplayUtil.displayError("Source Facility ID failed; Facility: ["
					+ sourceFacilityId + "] does not exist.");
			return null;
		}

		if (this.emissionUnits.isEmpty()) {
			DisplayUtil.displayError("Source Facility ID failed; Facility: ["
					+ sourceFacilityId + "] no Emission Units.");
			return null;
		}

		try {
			if (!Utility.isNullOrEmpty(targetFacilityId)) {
				String format = "F%06d";

				int tempId;
				try {
					tempId = Integer.parseInt(targetFacilityId);
					setTargetFacilityId(String.format(format, tempId));
				} catch (NumberFormatException nfe) {
				}
			}
			if (!Utility.isNullOrEmpty(sourceFacilityId)) {
				String format = "F%06d";

				int tempId;
				try {
					tempId = Integer.parseInt(sourceFacilityId);
					setSourceFacilityId(String.format(format, tempId));
				} catch (NumberFormatException nfe) {
				}
			}
			Facility facility = getFacilityService().retrieveFacility(targetFacilityId);
			if (facility == null) {
				DisplayUtil
						.displayError("Destination Facility ID failed; Facility: ["
								+ targetFacilityId + "] does not exist.");
				return null;
			}
			this.targetFpId = facility.getFpId();
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Load Facility failed");
			return null;
		}

		if (!hasSelectedEU())
			return "dialog:msgTrEu";

		return "dialog:confirmTrEu";
	}

	public void noConfirm(ActionEvent actionEvent) {
		popupRedirectOutcome = null;
		FacesUtil.returnFromDialogAndRefresh();
	}

	public String getPopupRedirect() {
		if (this.popupRedirectOutcome != null) {
			FacesUtil.setOutcome(null, this.popupRedirectOutcome);
			this.popupRedirectOutcome = null;
		}
		return null;
	}

	public final TableSorter getEmusWrapper() {
		return this.emusWrapper;
	}

	public final TableSorter getCesWrapper() {
		return this.cesWrapper;
	}

	public final TableSorter getEgpsWrapper() {
		return this.egpsWrapper;
	}

	public String getSourceFacilityId() {
		return this.sourceFacilityId;
	}

	public void setSourceFacilityId(String sourceFacilityId) {
		this.sourceFacilityId = sourceFacilityId;
	}

	public String getTargetFacilityId() {
		return this.targetFacilityId;
	}

	public void setTargetFacilityId(String targetFacilityId) {
		this.targetFacilityId = targetFacilityId;
	}

	public boolean getChangeStatus() {
		return this.changeStatus;
	}

	public void setChangeStatus(boolean changeStatus) {
		this.changeStatus = changeStatus;
	}

	public boolean getIsIncludeChild() {
		return this.isIncludeChild;
	}

	public void setIsIncludeChild(boolean isIncludeChild) {
		this.isIncludeChild = isIncludeChild;
		syncEmissionUnits();
		showCEEP();
	}

	public final Timestamp getEuShutdownDate() {
		return euShutdownDate;
	}

	public final void setEuShutdownDate(Timestamp euShutdownDate) {
		// fieldChangeEventLog("feus", epaEmuId, this.euShutdownDate,
		// euShutdownDate);
		this.euShutdownDate = euShutdownDate;
	}

	private void showCEEP() {
		if (!this.isIncludeChild) {
			this.cesWrapper.clearWrappedData();
			this.egpsWrapper.clearWrappedData();
			return;
		} else {
			this.cesWrapper.setWrappedData(selectedCE());
			this.egpsWrapper.setWrappedData(selectedEP());
		}

	}

	private void syncEmissionUnits() {

		clearSelectedEU();
		clearSelectedCE();
		clearSelectedEP();

		try {
			UIXTable table = this.emusWrapper.getTable();
			@SuppressWarnings("unchecked")
			Iterator<RowKeySet> selection = table.getSelectionState()
					.getKeySet().iterator();
			while (selection.hasNext()) {
				table.setRowKey(selection.next());
				EmissionUnit row = (EmissionUnit) table.getRowData();
				row.setSelected(true);
				selectEU(row.getEpaEmuId());
				if (isIncludeChild) {
					selectCE(row.getEpaEmuId());
					selectEP(row.getEpaEmuId());
				}
			}
		} catch (Exception re) {
			DisplayUtil
					.displayError("Load Control Equipments and Release Points failed.");
			return;
		}

	}

	// emissionUnits
	private boolean hasSelectedEU() {
		if (this.emissionUnits != null) {
			for (EmissionUnit eu : this.emissionUnits) {
				if (eu.isSelected())
					return true;
			}
		}
		return false;
	}

	private EmissionUnit[] selectedEU() {
		List<EmissionUnit> selectedEU = new ArrayList<EmissionUnit>(0);
		for (EmissionUnit temp : this.emissionUnits) {
			if (temp.isSelected())
				selectedEU.add(temp);
		}
		return selectedEU.toArray(new EmissionUnit[0]);
	}

	private void selectEU(String epaEmuId) {
		if (emissionUnits != null) {
			for (EmissionUnit eu : this.emissionUnits) {
				if (eu.getEpaEmuId().equals(epaEmuId))
					eu.setSelected(true);
			}
		}
	}

	private void clearSelectedEU() {
		if (emissionUnits != null) {
			for (EmissionUnit eu : this.emissionUnits) {
				eu.setSelected(false);
			}
		}
	}

	// EmissionProcess
	private void selectEP() {
		for (EmissionUnit eu : selectedEU()) {
			for (EmissionProcess ep : eu.getEmissionProcesses()) {
				ep.setSelected(true);
			}
		}
	}

	// ControlEquips
	private ControlEquipment[] selectedCE() {
		List<ControlEquipment> selectedCE = new ArrayList<ControlEquipment>(0);
		for (ControlEquipment temp : this.controlEquips) {
			if (temp.isSelected())
				selectedCE.add(temp);
		}
		return selectedCE.toArray(new ControlEquipment[0]);
	}

	private void selectCE(String epaEmuId) {
		for (ControlEquipment temp : this.controlEquips) {
			if (temp.getAssociatedEpaEuIds() != null
					&& temp.getAssociatedEpaEuIds().contains(epaEmuId))
				temp.setSelected(true);
		}
	}

	private void clearSelectedCE() {
		if (this.controlEquips != null) {
			for (ControlEquipment temp : this.controlEquips) {
				temp.setSelected(false);
			}
		}
	}

	// egrPoints
	private EgressPoint[] selectedEP() {
		List<EgressPoint> selectedEP = new ArrayList<EgressPoint>(0);
		for (EgressPoint temp : this.egrPoints) {
			if (temp.isSelected())
				selectedEP.add(temp);
		}
		return selectedEP.toArray(new EgressPoint[0]);
	}

	private void selectEP(String epaEmuId) {
		for (EgressPoint temp : this.egrPoints) {
			if (temp.getAssociatedEpaEuIds() != null
					&& temp.getAssociatedEpaEuIds().contains(epaEmuId))
				temp.setSelected(true);
		}
	}

	private void clearSelectedEP() {
		if (controlEquips != null) {
			for (EgressPoint temp : this.egrPoints) {
				temp.setSelected(false);
			}
		}
	}

	private void setEpCeEpaEmuIds() {
		EmissionUnit[] emissionUnits = this.sourceFacility.getEmissionUnits()
				.toArray(new EmissionUnit[0]);

		for (EmissionUnit tempEU : emissionUnits) {
			EmissionProcess[] emissionProcesses = tempEU.getEmissionProcesses()
					.toArray(new EmissionProcess[0]);
			for (EmissionProcess tempEP : emissionProcesses) {
				ControlEquipment[] controlEquips = tempEP
						.getControlEquipments()
						.toArray(new ControlEquipment[0]);
				EgressPoint[] egressPoints = tempEP.getEgressPoints().toArray(
						new EgressPoint[0]);

				for (ControlEquipment tempCE : controlEquips) {
					addEpaEmuIdToContEquip(tempCE, tempEU.getEpaEmuId());
				}

				for (EgressPoint tempEGP : egressPoints) {
					tempEGP.setAssociatedEpaEuIds(tempEU.getEpaEmuId());
				}
			}
		}
	}

	private void addEpaEmuIdToContEquip(ControlEquipment tempCE, String epaEuId) {
		tempCE.setAssociatedEpaEuIds(epaEuId);

		ControlEquipment[] ceControlEquips = tempCE.getControlEquips().toArray(
				new ControlEquipment[0]);
		EgressPoint[] ceEgressPoints = tempCE.getEgressPoints().toArray(
				new EgressPoint[0]);

		for (ControlEquipment tempCeCE : ceControlEquips) {
			addEpaEmuIdToContEquip(tempCeCE, epaEuId);
		}

		for (EgressPoint tempEGP : ceEgressPoints) {
			tempEGP.setAssociatedEpaEuIds(epaEuId);
		}
	}

	public String refreshTrEu() {
		this.resetTrEu();
		
		return "facilities.trEu";
	}
}
