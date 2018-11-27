package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.def.CeOperatingStatusDef;
import us.oh.state.epa.stars2.def.DesignCapacityDef;
import us.oh.state.epa.stars2.def.EgOperatingStatusDef;
import us.oh.state.epa.stars2.def.SccCodesDef;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.FacilityEmissionFlow;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

/**
 * @author Kbradley
 * 
 */
public class EmissionProcess extends FacilityNode implements
		Comparable<EmissionProcess> {
	
	private static final long serialVersionUID = -4800471799259125361L;

	private Integer emissionUnitId;
	private String emissionProcessNm;
	private String processId;
	private String processName;
	private SccCode sccCode;
	private String oldSccId;
	private List<EgressPoint> egressPoints = new ArrayList<EgressPoint>(0);
	private List<ControlEquipment> controlEquipments = new ArrayList<ControlEquipment>(
			0);
	private List<FacilityEmissionFlow> epEmissionFlows = new ArrayList<FacilityEmissionFlow>(
			0);
	private String wiseViewId;
	private String controlEquipment = "";
    private String sccDesc1;
    private String sccDesc2;
    private String sccDesc3;
    private String sccDesc4;


	public EmissionProcess() {
		super();
		sccCode = new SccCode();
	}

	/**
	 * @param EmissionProcess
	 * 
	 *            Determine if both processes have exactly the same control
	 *            equipment and release points in the same configuration. Will
	 *            be true if each references the same top level equipment
	 */
	public final boolean sameEquipment(EmissionProcess p) {
		if (controlEquipments.size() != p.controlEquipments.size()
				|| egressPoints.size() != p.egressPoints.size()
				|| !controlEquipments.containsAll(p.controlEquipments)
				|| !egressPoints.containsAll(p.egressPoints)) {
			return false;
		}

		return true;
	}

	public HashSet<String> getUniqueControlEquip() {
		HashSet<String> rtn = new HashSet<String>();
		if (getControlEquipments().size() == 0) {
			return rtn;
		}
		for (ControlEquipment ce : getControlEquipments()) {
			rtn.add(ce.getEquipmentTypeCd());
			getUniqueControlEquip(ce.getControlEquips(), rtn);
		}
		;
		return rtn;
	}

	private void getUniqueControlEquip(List<ControlEquipment> cel,
			HashSet<String> set) {
		if (cel == null || cel.size() == 0) {
			return;
		}
		for (ControlEquipment ce : cel) {
			set.add(ce.getEquipmentTypeCd());
			getUniqueControlEquip(ce.getControlEquips(), set);
		}
		;
	}

	public float percentCaptureEfficencyOverall() {
		float rtn = 0f;
		float lostFlow = 0f;
		float totFlow = 0f;
		if (this.epEmissionFlows != null) {
			for (FacilityEmissionFlow few : this.epEmissionFlows) {
				totFlow = totFlow + few.getFlowFactor();
				if (FacilityEmissionFlow.STACK_TYPE.equals(few.getType())) {
					lostFlow = lostFlow + few.getFlowFactor();
				} else {
					// locate the piece of control equipment
					ControlEquipment locatedCe = null;
					for (ControlEquipment ce : getControlEquipments()) {
						if (ce.getFpNodeId().equals(
								few.getRelationship().getToNodeId())) {
							locatedCe = ce;
							break;
						}
					}
					;
					if (locatedCe == null) {
						logger.error("Did not find control equipment with fpNodeId "
								+ few.getRelationship().getToNodeId()
								+ ", processId is "
								+ this.processId
								+ " and emissionUnitId is "
								+ this.emissionUnitId);
					} else {
						lostFlow = lostFlow + few.getFlowFactor()
								* (100f - locatedCe.captureEfficencyOverall())
								/ 100f;
					}
				}
			}
		}
		if (totFlow > 0) {
			rtn = (totFlow - lostFlow) * 100f / totFlow;
		}
		return rtn;
	}

	public List<FacilityEmissionFlow> getEpEmissionFlows(Facility f,
			String pollutantCd) {
		List<FacilityEmissionFlow> revisedFef = new ArrayList<FacilityEmissionFlow>(
				epEmissionFlows.size());
		for (FacilityEmissionFlow fef : epEmissionFlows) {
			if (FacilityEmissionFlow.CE_TYPE.equals(fef.getType())) {
				ControlEquipment ce = f.getControlEquipment(fef.getId());
				PollutantsControlled pc = ce.getPollCont(pollutantCd);
				if (pc != null && pc.isCaptureEffZero()) {
					FacilityEmissionFlow zeroFef = new FacilityEmissionFlow(fef);
					revisedFef.add(zeroFef);
				} else {
					FacilityEmissionFlow copyFef = new FacilityEmissionFlow(fef);
					copyFef.setPercent(fef.getPercent());
					revisedFef.add(copyFef);
				}
			} else {
				FacilityEmissionFlow copyFef = new FacilityEmissionFlow(fef);
				copyFef.setPercent(fef.getPercent());
				revisedFef.add(copyFef);
			}
		}
		return revisedFef;
	}

	public String getMouseOverTip() {
		return processName;
	}

	/**
	 * @return
	 */
	public final Integer getEmissionUnitId() {
		return emissionUnitId;
	}

	/**
	 * @param emissionUnitId
	 */
	public final void setEmissionUnitId(Integer emissionUnitId) {
		this.emissionUnitId = emissionUnitId;
	}

	/**
	 * @return
	 */
	public final String getEmissionProcessNm() {
		return emissionProcessNm;
	}

	public final ControlEquipment getLastControlEquip() {
		return controlEquipments.get(controlEquipments.size() - 1);
	}

	/**
	 * @param emissionProcessNm
	 */
	public final void setEmissionProcessNm(String emissionProcessNm) {
		this.emissionProcessNm = emissionProcessNm;
	}

	/**
	 * @return
	 */
	public final String getProcessId() {
		return processId;
	}

	/**
	 * @param ProcessId
	 */
	public final void setProcessId(String processId) {
		this.processId = processId;
	}

	public final String getProcessName() {
		return processName;
	}

	public final void setProcessName(String processName) {
		this.processName = processName;
	}

	public final SccCode getSccCode() {
		return sccCode;
	}

	public final String getSccId() {
		String rtn = null;
		if (sccCode != null) {
			rtn = sccCode.getSccId();
		}
		return rtn;
	}

	public final void setSccCode(SccCode sccCode) {
		this.sccCode = sccCode;
	}

	/**
	 * @return
	 */
	public final List<ControlEquipment> getControlEquipments() {
		return controlEquipments;
	}

	public final void setControlEquipments(
			List<ControlEquipment> ControlEquipments) {
		this.controlEquipments = ControlEquipments;
	}

	public final HashSet<ControlEquipment> getAllControlEquipments() {
		HashSet<ControlEquipment> rtn = new HashSet<ControlEquipment>();
		rtn.addAll(controlEquipments);
		for (ControlEquipment ce : controlEquipments) {
			ce.addAllControlEquipments(rtn);
		}
		return rtn;
	}

	/**
	 * @param controlEquipment
	 */
	public final void addControlEquipment(ControlEquipment controlEquipment) {
		if (controlEquipment != null) {
			this.controlEquipments.add(controlEquipment);
		}
	}

	/**
	 * @return
	 */
	public final List<EgressPoint> getEgressPoints() {
		return egressPoints;
	}

	public final void setEgressPoints(List<EgressPoint> egressPoints) {
		this.egressPoints = egressPoints;
	}

	public String getWiseViewId() {
		return wiseViewId;
	}

	public void setWiseViewId(String wiseViewId) {
		this.wiseViewId = wiseViewId;
	}

	/**
	 * @param egressPoint
	 */
	public final void addEgressPoint(EgressPoint egressPoint) {
		if (egressPoint != null) {
			this.egressPoints.add(egressPoint);
		}
	}

	public final ControlEquipment findControlEquipment(String contEquipId) {
		for (ControlEquipment tempCE : controlEquipments) {
			if (tempCE.getControlEquipmentId().equals(contEquipId)) {
				return tempCE;
			}
		}
		return null;
	}

	public final EgressPoint findEgressPoint(String releasePointId) {
		for (EgressPoint tempEGP : egressPoints) {
			if (tempEGP.getReleasePointId().equals(releasePointId)) {
				return tempEGP;
			}
		}
		return null;
	}

	public final void populate(ResultSet rs) {
		try {
			super.populate(rs);
			setProcessId(rs.getString("process_id"));
			setProcessName(rs.getString("process_name"));
			setEmissionUnitId(AbstractDAO.getInteger(rs, "emu_id"));
			setEmissionProcessNm(rs.getString("emission_process_dsc"));
			setFpNodeId(AbstractDAO.getInteger(rs, "emissionProcess_nodeId"));
			setLastModified(AbstractDAO.getInteger(rs, "emissionProcess_lm"));
			String tmpSccCode = rs.getString("scc_id");
			SccCode tmpScc = SccCodesDef.getSccCode(tmpSccCode);
			if (tmpScc == null) {
				sccCode.setSccId(tmpSccCode);
				sccCode.setEuCapacityTypeCd(DesignCapacityDef.NA);
			} else {
				sccCode.copySccCode(tmpScc);
			}

		} catch (SQLException sqle) {
			logger.error("Required field error");
		} finally {
			newObject = false;
		}
	}

	public final ValidationMessage[] validate() {
		clearValidationMessages();
		if (!sccCode.isValid()) {
			for (ValidationMessage temp : sccCode.validate()) {
				validationMessages.put(
						temp.getProperty(),
						new ValidationMessage(temp.getProperty(), temp
								.getMessage(), temp.getSeverity(),
								"emissionProcess:" + processId));
			}
		}

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public final ValidationMessage[] validate(String epaEmuId) {
		clearValidationMessages();
		if (!sccCode.validate(processId, epaEmuId)) {
			for (ValidationMessage temp : sccCode.validate()) {
				validationMessages.put(temp.getProperty(), temp);
			}
		}

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public final ValidationMessage[] validateAddContEquip(ControlEquipment toCE) {
		ArrayList<ValidationMessage> validMessages = new ArrayList<ValidationMessage>(
				0);

		if (findControlEquipment(toCE.getControlEquipmentId()) != null) {
			validMessages.add(new ValidationMessage("validateAddCE",
					"Cannot add Control Equipment: "
							+ toCE.getControlEquipmentId()
							+ " ; it is duplicate control equipment",
					ValidationMessage.Severity.ERROR, "emissionProcess:"
							+ processId));
		}

		return validMessages.toArray(new ValidationMessage[0]);
	}

	public final ValidationMessage[] validateAddEgressPoint(EgressPoint toEGP) {
		ArrayList<ValidationMessage> validMessages = new ArrayList<ValidationMessage>(
				0);

		if (findEgressPoint(toEGP.getReleasePointId()) != null) {
			validMessages.add(new ValidationMessage("validateAddEGP",
					"Cannot add release point: " + toEGP.getReleasePointId()
							+ " ; it is duplicate release point",
					ValidationMessage.Severity.ERROR, "emissionProcess:"
							+ processId));
		}

		return validMessages.toArray(new ValidationMessage[0]);
	}

	public EgressPoint getEgressPoint(EgressPoint egp) {
		EgressPoint ret = null;
		for (EgressPoint tempEGP : egressPoints) {
			if (tempEGP.getCorrelationId().equals(egp.getCorrelationId())) {
				ret = tempEGP;
				break;
			}
		}
		return ret;
	}

	public Set<EgressPoint> getAllEgressPoints() {
		HashSet<EgressPoint> set = new HashSet<EgressPoint>(getEgressPoints());
		for (ControlEquipment ce : getControlEquipments()) {
			ce.addEgressPoints(set);
		}
		return set;
	}

	public ControlEquipment getControlEquipment(ControlEquipment ce) {
		ControlEquipment ret = null;
		for (ControlEquipment tempCE : controlEquipments) {
			if (tempCE.getCorrelationId().equals(ce.getCorrelationId())) {
				ret = tempCE;
				break;
			}
		}
		return ret;
	}

	public String getOldSccId() {
		return oldSccId;
	}

	public void setOldSccId(String oldSccId) {
		this.oldSccId = oldSccId;
	}

	public List<FacilityEmissionFlow> getEpEmissionFlows() {
		return epEmissionFlows;
	}

	public void setEpEmissionFlows(List<FacilityEmissionFlow> epEmissionFlows) {
		this.epEmissionFlows = epEmissionFlows;
	}

	/**
	 * @param emissionFlow
	 */
	public final void addepEmissionFlow(FacilityEmissionFlow emissionFlow) {
		if (emissionFlow != null) {
			this.epEmissionFlows.add(emissionFlow);
		}
	}

	public final boolean isEmissionFlowsAccess() {
		boolean showTable = false;
		if (epEmissionFlows.size() > 1) {
			showTable = true;
		} else if (epEmissionFlows.size() == 1
				&& epEmissionFlows.get(0).getFlowFactor() <= 0) {
			showTable = true;
		}
		return showTable;
	}

	@Override
	public int compareTo(EmissionProcess o) {
		return this.processId.compareTo(o.getProcessId());
	}

	public void setControlEquipment() {
		controlEquipment = new String();
		if (getControlEquipments().size() == 0) {
			controlEquipment = "";
		} else {
			for (ControlEquipment ce : getControlEquipments()) {
				controlEquipment = controlEquipment
						+ ce.getControlEquipmentId() + ":" + ce.getDapcDesc()
						+ ";\n";
			}
		}
	}

	public String getControlEquipment() {
		this.setControlEquipment();
		return controlEquipment;
	}

	public void setControlEquipment(String controlEquipment) {
		this.controlEquipment = controlEquipment;
	}
	
	public boolean checkForMonitoredPollutant(String pollutantCd, Integer year) {
		boolean ret = false;
		checkCem: for (EgressPoint releasePoint : getEgressPoints()) {
			if(!releasePoint.getOperatingStatusCd().equalsIgnoreCase(EgOperatingStatusDef.OP)) {
				continue;
			} 
			for (EgressPointCem cem : releasePoint.getCems()) {
				if (cem.isPollutantMonitored(pollutantCd)) {
					ret = true;
					break checkCem;
				} else {
					continue;
				}
			}
		}
		
		if(ret) {
			return ret;
		} else {
			for(ControlEquipment ce : getControlEquipments()) {
				if(!ce.getOperatingStatusCd().equalsIgnoreCase(CeOperatingStatusDef.OP)) {
					continue;
				} else {
					if(ce.getContEquipInstallDate() != null) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(new Date(ce.getContEquipInstallDate().getTime()));
						int ceInstallYear = cal.get(Calendar.YEAR);
						if(ceInstallYear > year) {
							continue;
						}
					}
				}
				ret = ce.checkForMonitoredPollutant(pollutantCd, year);
				if(ret) {
					break;
				}
			}
			return ret;
		}
	}
	
	public final String getSccDesc1() {
		if (sccCode == null) {
			sccCode = new SccCode();
		}
		return sccCode.getSccLevel1Desc();
	}

	public final void setSccDesc1(String sccDesc1) {
		
		if (sccDesc1 == null || sccDesc1.length() == 0) {
			this.sccDesc1 = null;
			setSccDesc2(null);
			setSccDesc3(null);
			setSccDesc4(null);
		} else {
			this.sccDesc1 = sccDesc1;
		}

		if (sccCode == null) {
			sccCode = new SccCode();
		}
		sccCode.setSccIdL1Cd(sccDesc1);

	}

	public final String getSccDesc2() {
		if (sccCode == null) {
			sccCode = new SccCode();
		}
		return sccCode.getSccLevel2Desc();
	}

	public final void setSccDesc2(String sccDesc2) {

		if (sccDesc2 == null || sccDesc2.length() == 0) {
			this.sccDesc2 = null;
			setSccDesc3(null);
			setSccDesc4(null);
		} else {
			this.sccDesc2 = sccDesc2;
		}

		if (sccCode == null) {
			sccCode = new SccCode();
		}
		sccCode.setSccIdL2Cd(sccDesc2);

	}
	
	public final LinkedHashMap<String, String> getSccLevel2Codes() {
		
		if (sccCode == null || sccCode.getSccIdL1Cd() == null) {
			return null;
		}

		return ((InfrastructureDefs) FacesUtil.getManagedBean("infraDefs")).getSccLevelsCodes(
				2, sccCode.getSccLevel1Desc(), 
				null, null, null, this.getSccCode().getSccLevel1Desc());
		
	}

	public final String getSccDesc3() {
		if (sccCode == null) {
			sccCode = new SccCode();
		}
		return sccCode.getSccLevel3Desc();
	}

	public final void setSccDesc3(String sccDesc3) {

		if (sccDesc3 == null || sccDesc3.length() == 0) {
			this.sccDesc3 = null;
			setSccDesc4(null);
		} else {
			this.sccDesc3 = sccDesc3;
		}

		if (sccCode == null) {
			sccCode = new SccCode();
		}
		sccCode.setSccIdL3Cd(sccDesc3);

	}

	public final LinkedHashMap<String, String> getSccLevel3Codes() {

		if (sccCode == null || sccCode.getSccIdL2Cd() == null) {
			return null;
		}

		return ((InfrastructureDefs) FacesUtil.getManagedBean("infraDefs")).getSccLevelsCodes(
				3, sccCode.getSccLevel1Desc(), sccCode.getSccLevel2Desc(), 
				null, null, this.getSccCode().getSccLevel2Desc());
		
	}

	public final String getSccDesc4() {
		if (sccCode == null) {
			sccCode = new SccCode();
		}
		return sccCode.getSccLevel4Desc();
	}

	public final void setSccDesc4(String sccDesc4) {

		this.sccDesc4 = sccDesc4;
		if (sccCode != null) {
			sccCode.setSccIdL4Cd(sccDesc4);			
		}

	}

	public final LinkedHashMap<String, String> getSccLevel4Codes() {

		if (sccCode == null || sccCode.getSccIdL3Cd() == null) {
			return null;
		}

		return ((InfrastructureDefs) FacesUtil.getManagedBean("infraDefs")).getSccLevelsCodes(
				4, sccCode.getSccLevel1Desc(), sccCode.getSccLevel2Desc(), sccCode.getSccLevel3Desc(),
				null, this.getSccCode().getSccLevel3Desc());
		
	}

}
