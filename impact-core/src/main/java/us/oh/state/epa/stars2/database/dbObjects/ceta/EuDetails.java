package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipment;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;

//import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
//import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;

public class EuDetails {
	private boolean selected;
	private boolean alreadySelected;
	private String emuId;
	private EmissionUnit eu;
	private String euOpStatusCd;
	private Timestamp shutdownDate;
	private String euDesc;
	private String sccId;
	private String sccActiveInfo; // text describing why not active. Otherwise
									// null
	private EmissionProcess ep;
	private String processDesc;
	private String controlEquipment; // comma-separated list
	private String noProcess = "No Process";

	public EuDetails(String emuId, EmissionUnit eu, String euOpStatusCd,
			Timestamp shutdownDate, String euDesc, String sccId,
			EmissionProcess ep, String processDesc) {
		super();
		this.emuId = emuId;
		this.eu = eu;
		this.euOpStatusCd = euOpStatusCd;
		this.shutdownDate = shutdownDate;
		this.euDesc = euDesc;
		this.sccId = sccId;
		this.ep = ep;
		this.processDesc = processDesc;
		this.setControlEquipment();
	}

	public String getEmuId() {
		return emuId;
	}

	public String getEuDesc() {
		return euDesc;
	}

	public String getEuOpStatusCd() {
		return euOpStatusCd;
	}

	public String getProcessDesc() {
		return processDesc;
	}

	public String getSccId() {
		return sccId;
	}

	public EmissionProcess getEp() {
		return ep;
	}

	public EmissionUnit getEu() {
		return eu;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isAlreadySelected() {
		return alreadySelected;
	}

	public void setAlreadySelected(boolean alreadySelected) {
		this.alreadySelected = alreadySelected;
	}

	public String getSccActiveInfo() {
		return sccActiveInfo;
	}

	public void setSccActiveInfo(String sccActiveInfo) {
		this.sccActiveInfo = sccActiveInfo;
	}

	public Timestamp getShutdownDate() {
		return shutdownDate;
	}

	public void setShutdownDate(Timestamp shutdownDate) {
		this.shutdownDate = shutdownDate;
	}

	public String getControlEquipment() {
		return controlEquipment;
	}

	public void setControlEquipment(String controlEquipment) {
		this.controlEquipment = controlEquipment;
	}

	public void setControlEquipment() {
		if (ep != null) {
			controlEquipment = new String();
			for (ControlEquipment ce : ep.getAllControlEquipments()) {
				controlEquipment = controlEquipment
						+ ce.getControlEquipmentId() + ":" + ce.getDapcDesc()
						+ ";\n";
			}
		}
	}

	public String getNoProcess() {
		return noProcess;
	}

	public void setNoProcess(String noProcess) {
		this.noProcess = noProcess;
	}
}
