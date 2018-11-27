package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;

@SuppressWarnings("serial")
public class TestedEmissionsUnit extends CetaBaseDB implements Comparable {

	// NOTE THIS IS NOT PUT IN THE DATABASE

	private Integer testedEu; // correlation id
	private transient EmissionUnit eu;
	private transient String epaEmuId;
	private transient String opStatus;
	private transient Timestamp shutdownDate;
	private transient String description;
	private transient String sccs; // the SCC tested on this EU
	private transient String controlEquipment; // the control equipment
												// associated with the scc
	private transient String processDescription; // the process description
													// for sccs

	public TestedEmissionsUnit() {
		super();
	}

	public TestedEmissionsUnit(EmissionUnit eu) {
		super();
		this.eu = eu;
		populateFields();
	}

	public final int compareTo(Object b) {
		TestedEmissionsUnit rowB = (TestedEmissionsUnit) b;
		if (epaEmuId == null || rowB.epaEmuId == null)
			return 0;
		int i = epaEmuId.compareTo(rowB.epaEmuId);
		if (i != 0)
			return i;
		if (sccs == null || rowB.sccs == null)
			return 0;
		i = sccs.compareTo(rowB.sccs);
		return i;
	}

	private void populateFields() {
		if (eu != null) {
			epaEmuId = eu.getEpaEmuId();
			testedEu = eu.getCorrEpaEmuId();
			if (eu.getEuDesc() != null && eu.getEuDesc().trim().length() != 0) {
				description = eu.getEuDesc();
			} else {
				description = eu.getRegulatedUserDsc();
			}

			if (EuOperatingStatusDef.SD.equals(eu.getOperatingStatusCd())) {
				shutdownDate = eu.getEuShutdownDate();
			}
			String opString = "No Status";
			if (eu.getOperatingStatusCd() != null) {
				opString = EuOperatingStatusDef.getData().getItems()
						.getItemDesc(eu.getOperatingStatusCd());
			}
			opStatus = opString;
		}
	}

	/** Populate this instance from a database ResultSet. */
	public final void populate(java.sql.ResultSet rs) throws SQLException {

		try {
			testedEu = AbstractDAO.getInteger(rs, "tested_eu");
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	public Integer getTestedEu() {
		return testedEu;
	}

	public void setTestedEu(Integer testedEu) {
		this.testedEu = testedEu;
	}

	public EmissionUnit getEu() {
		return eu;
	}

	public void setEu(EmissionUnit eu) {
		this.eu = eu;
	}

	public String getDescription() {
		return description;
	}

	public String getEpaEmuId() {
		return epaEmuId;
	}

	public String getOpStatus() {
		return opStatus;
	}

	public String getSccs() {
		String rtn = "";
		if (sccs != null)
			rtn = sccs;
		return rtn;
	}

	public void setSccs(String cs) {
		sccs = cs;
	}

	public void setEpaEmuId(String epaEmuId) {
		this.epaEmuId = epaEmuId;
	}

	public Timestamp getShutdownDate() {
		return shutdownDate;
	}

	public void setShutdownDate(Timestamp shutdownDate) {
		this.shutdownDate = shutdownDate;
	}

	public String getControlEquipment() {
		String rtn = "";
		if (controlEquipment != null) {
			rtn = controlEquipment;
		}
		return rtn;
	}

	public void setControlEquipment(String controlEqt) {
		controlEquipment = controlEqt;
	}

	public String getProcessDescription() {
		String rtn = "";
		if (processDescription != null) {
			rtn = processDescription;
		}
		return rtn;
	}

	public void setProcessDescription(String processDesc) {
		processDescription = processDesc;
	}
}
