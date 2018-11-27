package us.oh.state.epa.stars2.database.dao.facility;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.facility.UndeliveredMail;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.DetailDataDAO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.AbstractDAO.ConnectionHandler;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.BaseDBObject;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceContinuousMonitorLineItem;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsRptInfo;
import us.oh.state.epa.stars2.database.dbObjects.facility.AppPermitSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipment;
import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipmentData;
import us.oh.state.epa.stars2.database.dbObjects.facility.DecaneProperties;
import us.oh.state.epa.stars2.database.dbObjects.facility.EgressPoint;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.EuEmission;
import us.oh.state.epa.stars2.database.dbObjects.facility.EventLog;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityCemComLimit;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityEmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityExport;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityHistList;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityIdRef;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityNode;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityNote;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityOwner;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityPurgeLog;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityPurgeSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRUM;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRelationship;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRequestList;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRole;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRoleActivity;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityVersion;
import us.oh.state.epa.stars2.database.dbObjects.facility.HydrocarbonAnalysisPollutant;
import us.oh.state.epa.stars2.database.dbObjects.facility.HydrocarbonAnalysisSampleDetail;
import us.oh.state.epa.stars2.database.dbObjects.facility.ModelingExtractResult;
import us.oh.state.epa.stars2.database.dbObjects.facility.MultiEstabFacilityList;
import us.oh.state.epa.stars2.database.dbObjects.facility.PollutantCompCode;
import us.oh.state.epa.stars2.database.dbObjects.facility.PollutantsControlled;
import us.oh.state.epa.stars2.database.dbObjects.facility.SubmissionLog;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitReplacement;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.database.dbObjects.facilityRequest.FacilityRequest;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ApiGroup;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.FacilityTypeDef;
import us.oh.state.epa.stars2.def.GeoPolygonDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.DataStoreConcurrencyException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.database.dbObjects.company.Company;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroup;
import us.wy.state.deq.impact.database.dbObjects.report.EiDataImportFacilityInformation;
import us.wy.state.deq.impact.database.dbObjects.tool.FixmeCompany;
import us.wy.state.deq.impact.def.CeDataDetailDef;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;

@Repository
public class FacilitySQLDAO extends AbstractDAO implements FacilityDAO {

	private Logger logger = Logger.getLogger(FacilitySQLDAO.class);
	
	@Resource DetailDataDAO readOnlyDetailDataDAO;
	
	/**
	 * @see FacilityDAO#createFacilityAttachment(FacilityAttachment attachment)
	 */
	public final Attachment createFacilityAttachment(Attachment attachment)
			throws DAOException {
		Attachment ret = attachment;

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.createFacilityAttachment", false);

		connHandler.setInteger(1, attachment.getDocumentID());
		connHandler.setString(2, attachment.getDocTypeCd());
		connHandler.update();

		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see FacilityDAO#updateFacilityAttachment(FacilityAttachment attachment)
	 */
	public final Attachment updateFacilityAttachment(Attachment attachment)
			throws DAOException {
		Attachment ret = attachment;

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.updateFacilityAttachment", false);

		connHandler.setInteger(1, attachment.getDocumentID());
		connHandler.setString(2, attachment.getDocTypeCd());
		connHandler.setInteger(3, attachment.getRefLastModified() + 1);
		connHandler.setInteger(4, attachment.getDocumentID());
		connHandler.setInteger(5, attachment.getRefLastModified());
		connHandler.update();
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see FacilityDAO#removeFacilityAttachment(FacilityAttachment attachment)
	 */
	public final void removeFacilityAttachment(Attachment attachment)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeFacilityAttachment", false);

		connHandler.setInteger(1, attachment.getDocumentID());
		connHandler.remove();

		removeRows("dc_document", "document_id", attachment.getDocumentID());
		try {
			DocumentUtil.removeDocument(attachment.getPath());
		} catch (IOException ioe) {
			throw new DAOException("Could not delete FACILITY ATTACHMENT "
					+ attachment.getPath(), ioe);
		}
	}

	/**
	 * @see FacilityDAO#createFacility(Facility facility)
	 */
	public final Facility createFacility(Facility facility) throws DAOException {
		Facility ret = facility;

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.createFacility", false);

		Integer fpId = nextSequenceVal("S_Fp_Id", facility.getFpId());

		int i = 1;
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isTivInd()));
		connHandler.setInteger(i++, fpId);
		connHandler.setString(i++, facility.getPermitStatusCd());
		connHandler.setString(i++, facility.getTransitStatusCd());
		connHandler.setString(i++, facility.getPermitClassCd());
		connHandler.setString(i++, facility.getPortableGroupTypeCd());
		connHandler.setString(i++, facility.getOperatingStatusCd());
		connHandler.setString(i++, facility.getDoLaaCd());
		connHandler.setString(i++, facility.getFacilityId());
		connHandler.setInteger(i++, facility.getVersionId());
		connHandler.setString(i++, facility.getName());
		connHandler.setString(i++, facility.getDesc());
		connHandler.setTimestamp(i++, facility.getStartDate());
		connHandler.setTimestamp(i++, facility.getEndDate());
		connHandler
				.setString(i++, AbstractDAO
						.translateBooleanToIndicator(facility.getPortable()));
		connHandler.setString(i++, facility.getPortableGroupCd());
		connHandler.setTimestamp(i++, facility.getShutdownDate());
		connHandler.setTimestamp(i++, facility.getInactiveDate());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isMact()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isNeshaps()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isNsps()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isPsd()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(facility.isNsrNonattainment()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isGhgInd()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isSec112()));
		connHandler.setString(i++, facility.getFederalSCSCId());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility
						.isIntraStateVoucherFlag()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(facility.isCopyOnChange()));
		connHandler.setInteger(i++, facility.getCorePlaceId());
		connHandler.setString(i++, facility.getTvCertRepdueDate());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isTvTypeA()));
		connHandler.setString(i++, facility.getLastSubmissionType());
		connHandler.setInteger(i++, facility.getLastSubmissionVersion());
		connHandler.setString(i++, facility.getGovtFacilityTypeCd());
		connHandler.setString(i++, facility.getFacilityTypeCd());
		connHandler.setString(i++, facility.getCerrClassCd());
		connHandler.setString(i++, facility.getAfs());
		connHandler.setString(i++, facility.getAirProgramCd());
		connHandler.setString(i++, facility.getAqdEmissionFactorGroupCd());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(facility.isAdministrativeHold()));
		// connHandler.setString(i++, facility.getAirProgramCompCd());
		// connHandler.setString(i++, facility.getSipCompCd());
		// connHandler.setString(i++, facility.getMactCompCd());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setFpId(fpId);
		ret.setLastModified(1);

		return ret;
	}
	
	/**
	 * @see FacilityDAO#createFacility(Facility facility)
	 */
	public final Facility createClonedFacility(Facility facility) throws DAOException {
		Facility ret = facility;

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.createFacility", false);

		Integer fpId = nextSequenceVal("S_Fp_Id", facility.getFpId());

		int i = 1;
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(null));
		connHandler.setInteger(i++, fpId);
		connHandler.setString(i++, facility.getPermitStatusCd());
		connHandler.setString(i++, facility.getTransitStatusCd());
		connHandler.setString(i++, facility.getPermitClassCd());
		connHandler.setString(i++, facility.getPortableGroupTypeCd());
		connHandler.setString(i++, facility.getOperatingStatusCd());
		connHandler.setString(i++, facility.getDoLaaCd());
		connHandler.setString(i++, facility.getFacilityId());
		connHandler.setInteger(i++, facility.getVersionId());
		connHandler.setString(i++, facility.getName());
		connHandler.setString(i++, facility.getDesc());
		connHandler.setTimestamp(i++, facility.getStartDate());
		connHandler.setTimestamp(i++, facility.getEndDate());
		connHandler
				.setString(i++, AbstractDAO
						.translateBooleanToIndicator(facility.getPortable()));
		connHandler.setString(i++, facility.getPortableGroupCd());
		connHandler.setTimestamp(i++, facility.getShutdownDate());
		connHandler.setTimestamp(i++, facility.getInactiveDate());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(null));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(null));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(null));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(null));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(null));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isGhgInd()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(null));
		connHandler.setString(i++, facility.getFederalSCSCId());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility
						.isIntraStateVoucherFlag()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(facility.isCopyOnChange()));
		connHandler.setInteger(i++, facility.getCorePlaceId());
		connHandler.setString(i++, facility.getTvCertRepdueDate());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isTvTypeA()));
		connHandler.setString(i++, facility.getLastSubmissionType());
		connHandler.setInteger(i++, facility.getLastSubmissionVersion());
		connHandler.setString(i++, facility.getGovtFacilityTypeCd());
		connHandler.setString(i++, facility.getFacilityTypeCd());
		connHandler.setString(i++, facility.getCerrClassCd());
		connHandler.setString(i++, facility.getAfs());
		connHandler.setString(i++, facility.getAirProgramCd());
		connHandler.setString(i++, facility.getAqdEmissionFactorGroupCd());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(facility.isAdministrativeHold()));
		// connHandler.setString(i++, facility.getAirProgramCompCd());
		// connHandler.setString(i++, facility.getSipCompCd());
		// connHandler.setString(i++, facility.getMactCompCd());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setFpId(fpId);
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see FacilityDAO#removeFacility(Integer fpId)
	 */
	public final void removeFacility(Integer fpId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeFacility", false);

		connHandler.setInteger(1, fpId);

		connHandler.remove();

	}

	/**
	 * @see FacilityDAO#createSubmissionLog(SubmissionLog submissionLog)
	 */
	public final SubmissionLog createSubmissionLog(SubmissionLog submissionLog)
			throws DAOException {
		SubmissionLog ret = submissionLog;
		ConnectionHandler connHandler = new ConnectionHandler(false);

		connHandler.setSQLString("FacilitySQL.createSubmissionLog");

		int i = 1;
		connHandler.setString(i++, submissionLog.getFacilityId());
		connHandler.setString(i++, submissionLog.getSubmissionType());
		connHandler.setTimestamp(i++, submissionLog.getSubmissionDt());
		connHandler.setString(i++, submissionLog.getGatewayUserName());
		connHandler.setString(i++, submissionLog.getGatewaySubmissionId());
		connHandler
				.setString(i++, AbstractDAO
						.translateBooleanToIndicator(submissionLog
								.isNonROSubmission()));
		connHandler.setInteger(i++, submissionLog.getDocumentId());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see FacilityDAO#createControlEquipment(ControlEquipment
	 *      controlEquipment)
	 */
	public final ControlEquipment createStagingControlEquipment(
			ControlEquipment controlEquipment) throws DAOException {
		ControlEquipment ret = controlEquipment;
		ConnectionHandler connHandler = new ConnectionHandler(false);

		Integer cdId = nextSequenceVal("S_CE_Id");

		connHandler = new ConnectionHandler(false);
		connHandler.setSQLString("FacilitySQL.createControlEquipment");
		connHandler.setInteger(1, controlEquipment.getFpNodeId());
		connHandler.setString(2, controlEquipment.getControlEquipmentId());
		connHandler.setString(3, controlEquipment.getDapcDesc());
		connHandler.setString(4, controlEquipment.getManufacturer());
		connHandler.setString(5, controlEquipment.getModel());
		connHandler.setTimestamp(6, controlEquipment.getContEquipInstallDate());
		connHandler.setString(7, controlEquipment.getEquipmentTypeCd());
		connHandler.setString(8, controlEquipment.getRegUserDesc());
		connHandler.setString(9, controlEquipment.getOperatingStatusCd());
		connHandler.setString(10, controlEquipment.getCompanyId());
		connHandler.setInteger(11, cdId);

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	public final String retrieveNextEmissionUnitId(EmissionUnit emissionUnit)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(false);
		String typeCd = emissionUnit.getEmissionUnitTypeCd();

		connHandler.setSQLString("FacilitySQL.retrieveNextEmissionUnitId");
		connHandler.setString(1, typeCd);
		connHandler.setInteger(2, emissionUnit.getFpId());

		Integer nextId = (Integer) connHandler
				.retrieveJavaObject(Integer.class);
		String ret = typeCd + String.format("%03d", nextId);

		return ret;
	}
	
	public final String retrieveNextEmissionUnitIdBasedOnPreviousVersion(EmissionUnit emissionUnit, Integer fpId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(false);
		String typeCd = emissionUnit.getEmissionUnitTypeCd();

		connHandler.setSQLString("FacilitySQL.retrieveNextEmissionUnitId");
		connHandler.setString(1, typeCd);
		connHandler.setInteger(2, fpId);

		Integer nextId = (Integer) connHandler
				.retrieveJavaObject(Integer.class);
		String ret = typeCd + String.format("%03d", nextId);

		return ret;
	}


	public final String retrieveNextReleasePointId(EgressPoint releasePoint)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(false);
		String typeCd = releasePoint.getEgressPointTypeCd();

		connHandler.setSQLString("FacilitySQL.retrieveNextReleasePointId");
		connHandler.setString(1, typeCd);
		connHandler.setInteger(2, releasePoint.getFpId());

		Integer nextId = (Integer) connHandler
				.retrieveJavaObject(Integer.class);
		String ret = typeCd + String.format("%03d", nextId);

		return ret;
	}
	
	public final String retrieveNextReleasePointIdBasedOnPreviousVersion(EgressPoint releasePoint, Integer oldFpId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(false);
		String typeCd = releasePoint.getEgressPointTypeCd();

		connHandler.setSQLString("FacilitySQL.retrieveNextReleasePointId");
		connHandler.setString(1, typeCd);
		connHandler.setInteger(2, oldFpId);

		Integer nextId = (Integer) connHandler
				.retrieveJavaObject(Integer.class);
		String ret = typeCd + String.format("%03d", nextId);

		return ret;
	}

	public final String retrieveNextEquipmentId(
			ControlEquipment controlEquipment) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(false);
		String typeCd = controlEquipment.getEquipmentTypeCd();

		connHandler.setSQLString("FacilitySQL.retrieveNextEquipmentId");
		connHandler.setString(1, typeCd);
		connHandler.setInteger(2, controlEquipment.getFpId());

		Integer nextId = (Integer) connHandler
				.retrieveJavaObject(Integer.class);
		String ret = typeCd + String.format("%03d", nextId);

		return ret;
	}
	
	public final String retrieveNextEquipmentIdBasedOnPreviousVersion(
			ControlEquipment controlEquipment, Integer oldFpId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(false);
		String typeCd = controlEquipment.getEquipmentTypeCd();

		connHandler.setSQLString("FacilitySQL.retrieveNextEquipmentId");
		connHandler.setString(1, typeCd);
		connHandler.setInteger(2, oldFpId);

		Integer nextId = (Integer) connHandler
				.retrieveJavaObject(Integer.class);
		String ret = typeCd + String.format("%03d", nextId);

		return ret;
	}

	public final String retrieveNextEmissonProcessId(
			EmissionProcess emissionProcess) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(false);

		connHandler.setSQLString("FacilitySQL.retrieveNextEmissonProcessId");
		connHandler.setInteger(1, emissionProcess.getFpId());
		String prefix = "PRC";
		Integer nextId = (Integer) connHandler
				.retrieveJavaObject(Integer.class);
		String ret = prefix + String.format("%03d", nextId);

		return ret;
	}
	
	public final String retrieveNextEmissonProcessIdBasedOnPreviousVersion(
			EmissionProcess emissionProcess, Integer oldFpId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(false);

		connHandler.setSQLString("FacilitySQL.retrieveNextEmissonProcessId");
		connHandler.setInteger(1, oldFpId);
		String prefix = "PRC";
		Integer nextId = (Integer) connHandler
				.retrieveJavaObject(Integer.class);
		String ret = prefix + String.format("%03d", nextId);

		return ret;
	}


	/**
	 * @see FacilityDAO#createControlEquipment(ControlEquipment
	 *      controlEquipment)
	 */
	public final ControlEquipment createControlEquipment(
			ControlEquipment controlEquipment) throws DAOException {
		ControlEquipment ret = controlEquipment;
		ConnectionHandler connHandler = new ConnectionHandler(false);

		int ceId = controlEquipment.getCeId();
		boolean isInnerOldCE = ceId > 0; // Copy internal CE to protal CE

		if (!isInnerOldCE) {
			ceId = nextSequenceVal("S_CE_Id");
		}

		connHandler = new ConnectionHandler(false);
		connHandler.setSQLString("FacilitySQL.createControlEquipment");

		connHandler.setInteger(1, controlEquipment.getFpNodeId());
		connHandler.setString(2, controlEquipment.getControlEquipmentId());
		connHandler.setString(3, controlEquipment.getDapcDesc());
		connHandler.setString(4, controlEquipment.getManufacturer());
		connHandler.setString(5, controlEquipment.getModel());
		connHandler.setTimestamp(6, controlEquipment.getContEquipInstallDate());
		connHandler.setString(7, controlEquipment.getEquipmentTypeCd());
		connHandler.setString(8, controlEquipment.getRegUserDesc());
		connHandler.setString(9, controlEquipment.getOperatingStatusCd());
		connHandler.setString(10, controlEquipment.getCompanyId());
		connHandler.setInteger(11, ceId);
		connHandler.setString(12, controlEquipment.getWiseViewId());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see FacilityDAO#createEgressPoint(EgressPoint egressPoint)
	 */
	public final EgressPoint createEgressPoint(EgressPoint egressPoint)
			throws DAOException {
		EgressPoint ret = egressPoint;

		ConnectionHandler connHandler = new ConnectionHandler(false);

		connHandler.setSQLString("FacilitySQL.createEgressPoint");

		int index = 1;
		connHandler.setInteger(index++, egressPoint.getFpNodeId());
		connHandler.setString(index++, egressPoint.getEgressPointId());
		connHandler.setString(index++, egressPoint.getEgressPointTypeCd());
		connHandler.setString(index++, egressPoint.getEgressPointShapeCd());
		connHandler.setString(index++, egressPoint.getOperatingStatusCd());

		connHandler.setString(index++, egressPoint.getEgressPointNm());
		connHandler.setString(index++, egressPoint.getDapcDesc());
		connHandler.setString(index++, egressPoint.getFugitiveDimensionsUnit());
		connHandler.setString(index++,
				egressPoint.getEmissionReleasePointType());
		connHandler.setString(index++, egressPoint.getHortCollectionMethodCd());
		connHandler.setString(index++, egressPoint.getHortReferenceDatumCd());
		connHandler.setString(index++, egressPoint.getReferencePointCd());
		connHandler.setString(index++, egressPoint.getRegulatedUserDsc());
		connHandler.setString(index++, egressPoint.getCrossSectArea());
		connHandler.setFloat(index++, egressPoint.getHeight());
		connHandler.setBigDecimal(index++, egressPoint.getDiameter());
		connHandler.setFloat(index++, egressPoint.getExitGasTempMax());
		connHandler.setBigDecimal(index++, egressPoint.getExitGasTempAvg());
		connHandler.setFloat(index++, egressPoint.getExitGasFlowMax());
		connHandler.setBigDecimal(index++, egressPoint.getExitGasFlowAvg());
		connHandler.setFloat(index++, egressPoint.getBaseElevation());
		connHandler.setFloat(index++, egressPoint.getReleaseHeight());
		connHandler.setFloat(index++, egressPoint.getPlumeTemp());
		connHandler.setFloat(index++, egressPoint.getAreaOfEmissions());
		connHandler.setString(index++, egressPoint.getHortAccurancyMeasure());
		connHandler.setFloat(index++, egressPoint.getStackFencelineDistance());
		connHandler.setFloat(index++, egressPoint.getSourceMapScaleNumber());
		connHandler.setString(index++, egressPoint.getCoordDataSourceCd());
		connHandler.setFloat(index++, egressPoint.getBuildingLength());
		connHandler.setFloat(index++, egressPoint.getBuildingWidth());
		connHandler.setFloat(index++, egressPoint.getBuildingHeight());
		connHandler.setFloat(index++, egressPoint.getVolumeLength());
		connHandler.setFloat(index++, egressPoint.getVolumeWidth());
		connHandler.setFloat(index++, egressPoint.getVolumeHeight());
		connHandler.setString(index++, egressPoint.getAqdWiseEgressPointId());
		connHandler.setBigDecimal(index++, egressPoint.getExitGasVelocity());
		connHandler.setString(index++, egressPoint.getReleasePointId());
		connHandler.setString(index++, egressPoint.getLatitudeDeg());
		connHandler.setString(index++, egressPoint.getLongitudeDeg());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see FacilityDAO#modifyEgressPoint(EgressPoint egressPoint)
	 */
	public final boolean modifyEgressPoint(EgressPoint egressPoint)
			throws DAOException {
		boolean ret = false;

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.modifyEgressPoint", false);

		int index = 1;
		connHandler.setString(index++, egressPoint.getCoordDataSourceCd());
		connHandler.setString(index++, egressPoint.getEgressPointId());
		connHandler.setString(index++, egressPoint.getEgressPointTypeCd());
		connHandler.setString(index++, egressPoint.getEgressPointShapeCd());
		connHandler.setString(index++, egressPoint.getOperatingStatusCd());

		connHandler.setString(index++, egressPoint.getEgressPointNm());
		connHandler.setString(index++, egressPoint.getDapcDesc());
		connHandler.setString(index++, egressPoint.getFugitiveDimensionsUnit());
		connHandler.setString(index++,
				egressPoint.getEmissionReleasePointType());
		connHandler.setString(index++, egressPoint.getHortCollectionMethodCd());
		connHandler.setString(index++, egressPoint.getHortReferenceDatumCd());
		connHandler.setString(index++, egressPoint.getReferencePointCd());
		connHandler.setString(index++, egressPoint.getRegulatedUserDsc());
		connHandler.setString(index++, egressPoint.getCrossSectArea());
		connHandler.setFloat(index++, egressPoint.getHeight());
		connHandler.setBigDecimal(index++, egressPoint.getDiameter());
		connHandler.setFloat(index++, egressPoint.getExitGasTempMax());
		connHandler.setBigDecimal(index++, egressPoint.getExitGasTempAvg());
		connHandler.setFloat(index++, egressPoint.getExitGasFlowMax());
		connHandler.setBigDecimal(index++, egressPoint.getExitGasFlowAvg());
		connHandler.setFloat(index++, egressPoint.getBaseElevation());
		connHandler.setFloat(index++, egressPoint.getReleaseHeight());
		connHandler.setFloat(index++, egressPoint.getPlumeTemp());
		connHandler.setFloat(index++, egressPoint.getAreaOfEmissions());
		connHandler.setString(index++, egressPoint.getHortAccurancyMeasure());
		connHandler.setFloat(index++, egressPoint.getStackFencelineDistance());
		connHandler.setFloat(index++, egressPoint.getSourceMapScaleNumber());
		connHandler.setInteger(index++, egressPoint.getLastModified() + 1);
		connHandler.setFloat(index++, egressPoint.getBuildingLength());
		connHandler.setFloat(index++, egressPoint.getBuildingWidth());
		connHandler.setFloat(index++, egressPoint.getBuildingHeight());
		connHandler.setFloat(index++, egressPoint.getVolumeLength());
		connHandler.setFloat(index++, egressPoint.getVolumeWidth());
		connHandler.setFloat(index++, egressPoint.getVolumeHeight());
		connHandler.setString(index++, egressPoint.getAqdWiseEgressPointId());
		connHandler.setBigDecimal(index++, egressPoint.getExitGasVelocity());
		connHandler.setString(index++, egressPoint.getReleasePointId());
		connHandler.setString(index++, egressPoint.getLatitudeDeg());
		connHandler.setString(index++, egressPoint.getLongitudeDeg());

		// WHERE ...
		connHandler.setInteger(index++, egressPoint.getFpNodeId());
		// AND ...
		connHandler.setInteger(index++, egressPoint.getLastModified());

		connHandler.update();

		return ret;
	}

	/**
	 * @see FacilityDAO#createEmissionProcess(EmissionProcess emissionProcess)
	 */
	public final EmissionProcess createEmissionProcess(
			EmissionProcess emissionProcess) throws DAOException {

		EmissionProcess ret = emissionProcess;

		ConnectionHandler connHandler = new ConnectionHandler(false);

		connHandler.setSQLString("FacilitySQL.createEmissionsProcess");

		connHandler.setInteger(1, emissionProcess.getFpNodeId());
		connHandler.setInteger(2, emissionProcess.getEmissionUnitId());
		connHandler.setString(3, emissionProcess.getProcessId());
		connHandler.setString(4, emissionProcess.getEmissionProcessNm());
		connHandler.setString(5, emissionProcess.getSccId());
		connHandler.setString(6, emissionProcess.getProcessName());
		connHandler.setString(7, emissionProcess.getWiseViewId());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see FacilityDAO#removeEmissionProcess(EmissionProcess emissionProcess)
	 */
	public final void removeEmissionProcess(EmissionProcess emissionProcess)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeEmissionsProcess", false);

		connHandler.setInteger(1, emissionProcess.getFpNodeId());

		connHandler.remove();
	}

	/**
	 * @see FacilityDAO#removeControlEquipmet(ControlEquipment controlEquipment)
	 */
	public final void removeControlEquipment(ControlEquipment controlEquipment)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeControlEquipment", false);

		connHandler.setInteger(1, controlEquipment.getFpNodeId());

		connHandler.remove();
	}

	/**
	 * @see FacilityDAO#removeEgressPoint(EgressPoint egressPoint)
	 */
	public final void removeEgressPoint(EgressPoint egressPoint)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeEgressPoint", false);

		connHandler.setInteger(1, egressPoint.getFpNodeId());

		connHandler.remove();
	}

	/**
	 * @see FacilityDAO#createEmissionUnit(EmissionUnit emissionUnit)
	 */
	public final EmissionUnit createEmissionUnit(EmissionUnit emissionUnit)
			throws DAOException {
		EmissionUnit ret = emissionUnit;

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.createEmissionsUnit", false);

		Integer id = nextSequenceVal("S_Emu_Id", emissionUnit.getEmuId());
		if (emissionUnit.getEpaEmuId() == null
				|| emissionUnit.getCorrEpaEmuId() == null) {
			Integer epaId = nextSequenceVal("S_Epa_Emu_Id");
			if (emissionUnit.getEpaEmuId() == null) {
				emissionUnit.setEpaEmuId("");
				emissionUnit.setCorrEpaEmuId(epaId);
			}

			if (emissionUnit.getCorrEpaEmuId() == null) {
				emissionUnit.setCorrEpaEmuId(epaId);
			}
		}

		connHandler.setInteger(1, id);
		connHandler.setInteger(2, emissionUnit.getFpId());
		connHandler.setString(3, emissionUnit.getEuDesc());
		connHandler.setString(4, emissionUnit.getOperatingStatusCd());
		connHandler.setString(5, emissionUnit.getEpaEmuId());
		connHandler.setString(6, emissionUnit.getRegulatedUserDsc());
		connHandler.setTimestamp(7, emissionUnit.getEuStartupDate());
		connHandler.setTimestamp(8, emissionUnit.getEuInstallDate());
		connHandler.setString(9, emissionUnit.getCompanyId());
		connHandler.setString(10, emissionUnit.getPtioStatusCd());
		connHandler.setString(11, emissionUnit.getPtiStatusCd());
		connHandler.setString(12, emissionUnit.getTvClassCd());
		connHandler.setString(13, emissionUnit.getExemptStatusCd());
		connHandler.setTimestamp(14, emissionUnit.getEuInitInstallDate());
		connHandler.setTimestamp(15,
				emissionUnit.getEuShutdownNotificationDate());
		connHandler.setString(16, emissionUnit.getOrisBoilerId());
		connHandler.setTimestamp(17, emissionUnit.getEuShutdownDate());
		connHandler.setString(18, emissionUnit.getDesignCapacityCd());
		connHandler.setString(19, emissionUnit.getDesignCapacityUnitsCd());
		connHandler.setInteger(20, emissionUnit.getCorrEpaEmuId());
		connHandler.setString(21, emissionUnit.getDesignCapacityUnitsVal());
		connHandler.setString(22, emissionUnit.getEmissionUnitTypeCd());
		connHandler.setString(23, emissionUnit.getWiseViewId());
		connHandler.setTimestamp(24, emissionUnit.getEuInitStartupDate());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setEmuId(id);
		ret.setLastModified(1);

		return ret;

	}

	public final String retrieveEmissionUnitTypeCd(int emuId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveEmissionUnitTypeCd", false);

		connHandler.setInteger(1, emuId);
		String emissionUnitType = connHandler.retrieveJavaObject(String.class)
				.toString();

		return emissionUnitType;
	}

	public final String retrieveEmissionUnitDisplayId(int emuId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveEmissionUnitDisplayId", false);

		connHandler.setInteger(1, emuId);
		String displayId = connHandler.retrieveJavaObject(String.class)
				.toString();

		return displayId;
	}

	public final String retrieveControlEquipmentTypeCd(int fpNodeId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveControlEquipmentTypeCd", false);

		connHandler.setInteger(1, fpNodeId);
		String equTypeCd = connHandler.retrieveJavaObject(String.class)
				.toString();

		return equTypeCd;
	}

	public final String retrieveControlEquipmentId(int fpNodeId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveControlEquipmentId", false);

		connHandler.setInteger(1, fpNodeId);
		String rpId = connHandler.retrieveJavaObject(String.class).toString();

		return rpId;
	}

	public final ArrayList<Integer> retrieveRelatedEmuIds(int fpId,
			String emissionUnitTypeCd) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveRelatedEmuIds", false);

		connHandler.setInteger(1, fpId);
		connHandler.setString(2, emissionUnitTypeCd);

		ArrayList<Integer> emuIds = connHandler
				.retrieveJavaObjectArray(Integer.class);

		return emuIds;
	}

	public void updateEmissionUnitId(int emuId, String newEuId,
			String description) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.updateEmissionUnitId", false);

		connHandler.setString(1, newEuId);
		connHandler.setString(2, description);
		connHandler.setInteger(3, emuId);

		connHandler.update();
	}

	public final ArrayList<Integer> retrieveRelatedReleasePointFpNodeIds(
			int fpId, String newRpTypeCd) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveRelatedReleasePointFpNodeIds", false);

		connHandler.setString(1, newRpTypeCd);
		connHandler.setInteger(2, fpId);

		ArrayList<Integer> fpNodeIds = connHandler
				.retrieveJavaObjectArray(Integer.class);

		return fpNodeIds;
	}

	public final String retrieveReleasePointId(int fpNodeId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveReleasePointId", false);

		connHandler.setInteger(1, fpNodeId);
		String rpId = connHandler.retrieveJavaObject(String.class).toString();

		return rpId;
	}

	public void updateReleasePointId(int fpNodeId, String newRpId,
			String description) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.updateReleasePointId", false);

		connHandler.setString(1, newRpId);
		connHandler.setString(2, description);
		connHandler.setInteger(3, fpNodeId);

		connHandler.update();
	}

	public final String retrieveReleasePointTypeCd(int fpNodeId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveReleasePointTypeCd", false);

		connHandler.setInteger(1, fpNodeId);
		String rpTypeCd = connHandler.retrieveJavaObject(String.class)
				.toString();

		return rpTypeCd;
	}

	/**
	 * @see FacilityDAO#modifyControlEquipment(ControlEquipment
	 *      controlEquipment)
	 */
	public final boolean modifyControlEquipment(
			ControlEquipment controlEquipment) throws DAOException {
		boolean ret = false;
		ConnectionHandler connHandler;

		/*
		 * String ceuId = controlEquipment.getControlEquipmentId(); if
		 * (Utility.isNullOrEmpty(ceuId)) { connHandler = new
		 * ConnectionHandler(false);
		 * connHandler.setSQLString("FacilitySQL.getControlEquipNewID");
		 * connHandler.setInteger(1, controlEquipment.getFpNodeId()); }
		 */

		connHandler = new ConnectionHandler(
				"FacilitySQL.modifyControlEquipment", false);

		connHandler.setString(1, controlEquipment.getControlEquipmentId());
		connHandler.setInteger(2, controlEquipment.getLastModified() + 1);
		connHandler.setString(3, controlEquipment.getDapcDesc());
		connHandler.setString(4, controlEquipment.getManufacturer());
		connHandler.setString(5, controlEquipment.getModel());
		connHandler.setTimestamp(6, controlEquipment.getContEquipInstallDate());
		connHandler.setString(7, controlEquipment.getEquipmentTypeCd());
		connHandler.setString(8, controlEquipment.getRegUserDesc());
		connHandler.setString(9, controlEquipment.getOperatingStatusCd());
		connHandler.setString(10, controlEquipment.getCompanyId());
		connHandler.setString(11, controlEquipment.getCeId());
		connHandler.setString(12, controlEquipment.getWiseViewId());
		connHandler.setInteger(13, controlEquipment.getFpNodeId());
		connHandler.setInteger(14, controlEquipment.getLastModified());

		connHandler.update();

		// If we get here the UPDATE must have succeeded, so set the important
		// data and return the object.
		ret = true;

		return ret;
	}

	public void updateControlEquipmentId(int fpNodeId, String newEquId,
			String description) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.updateControlEquipmentId", false);

		connHandler.setString(1, newEquId);
		connHandler.setString(2, description);
		connHandler.setInteger(3, fpNodeId);

		connHandler.update();
	}

	/**
	 * @see FacilityDAO#modifyEmissionProcess(EmissionProcess emissionProcess)
	 */
	public final boolean modifyEmissionProcess(EmissionProcess emissionProcess)
			throws DAOException {
		boolean ret = false;

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.modifyEmissionsProcess", false);

		connHandler.setString(1, emissionProcess.getEmissionProcessNm());
		connHandler.setInteger(2, emissionProcess.getLastModified() + 1);
		connHandler.setString(3, emissionProcess.getProcessId());
		connHandler.setString(4, emissionProcess.getSccId());
		connHandler.setString(5, emissionProcess.getProcessName());
		connHandler.setInteger(6, emissionProcess.getFpNodeId());
		connHandler.setInteger(7, emissionProcess.getLastModified());

		connHandler.update();

		// If we get here the UPDATE must have succeeded, so set the important
		// data and return the object.
		ret = true;

		return ret;
	}

	public final boolean modifyEmissionUnit(EmissionUnit emissionUnit)
			throws DAOException {
		return modifyEmissionUnit(emissionUnit,null);
	}
	
	/**
	 * @see FacilityDAO#modifyEmissionUnit(EmissionUnit emissionUnit)
	 */
	public final boolean modifyEmissionUnit(EmissionUnit emissionUnit, String schema)
			throws DAOException {
		boolean ret = false;

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.modifyEmissionsUnit", false);

		connHandler.setString(1, emissionUnit.getEuDesc());
		connHandler.setInteger(2, emissionUnit.getLastModified() + 1);
		connHandler.setString(3, emissionUnit.getOperatingStatusCd());
		connHandler.setString(4, emissionUnit.getEpaEmuId());
		connHandler.setString(5, emissionUnit.getRegulatedUserDsc());
		connHandler.setTimestamp(6, emissionUnit.getEuInstallDate());
		connHandler.setTimestamp(7, emissionUnit.getEuStartupDate());
		connHandler.setTimestamp(8, emissionUnit.getEuShutdownDate());
		connHandler.setString(9, emissionUnit.getCompanyId());
		connHandler.setString(10, emissionUnit.getPtioStatusCd());
		connHandler.setString(11, emissionUnit.getPtiStatusCd());
		connHandler.setString(12, emissionUnit.getTvClassCd());
		connHandler.setString(13, emissionUnit.getExemptStatusCd());
		connHandler.setTimestamp(14, emissionUnit.getEuInitInstallDate());
		connHandler.setTimestamp(15,
				emissionUnit.getEuShutdownNotificationDate());
		connHandler.setString(16, emissionUnit.getOrisBoilerId());
		connHandler.setString(17, emissionUnit.getDesignCapacityCd());
		connHandler.setString(18, emissionUnit.getDesignCapacityUnitsCd());
		connHandler.setString(19, emissionUnit.getDesignCapacityUnitsVal());
		connHandler.setString(20, emissionUnit.getEmissionUnitTypeCd());
		connHandler.setString(21, emissionUnit.getWiseViewId());
		connHandler.setTimestamp(22, emissionUnit.getEuInitStartupDate());
		connHandler.setInteger(23, emissionUnit.getEmuId());
		connHandler.setInteger(24, emissionUnit.getLastModified());
		connHandler.update();

		// If we get here the UPDATE must have succeeded, so set the important
		// data and return the object.
		ret = true;

		return ret;
	}

	/**
	 * @see FacilityDAO#modifyRUM(UndeliveredMail undeliveredMail)
	 */
	public final boolean modifyRUM(FacilityRUM facilityRUM) throws DAOException {
		boolean ret = false;

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.modifyRUM", false);

		connHandler.setString(1, facilityRUM.getReasonCd());
		connHandler.setString(2, facilityRUM.getUndeliverableAddress());
		connHandler.setTimestamp(3, facilityRUM.getOriginalMailDt());
		connHandler.setTimestamp(4, facilityRUM.getCreatedDt());
		connHandler.setString(5, facilityRUM.getCategoryCd());
		connHandler.setString(6, facilityRUM.getDisposition());
		connHandler.setString(7, facilityRUM.getDapcNote());
		connHandler.setInteger(8, facilityRUM.getLastModified() + 1);
		connHandler.setInteger(9, facilityRUM.getRumId());
		connHandler.setInteger(10, facilityRUM.getLastModified());
		connHandler.update();

		// If we get here the UPDATE must have succeeded, so set the important
		// data and return the object.
		ret = true;
		return ret;
	}

	/**
	 * @see FacilityDAO#modifyFacility(Facility facility)
	 */
	public final boolean modifyFacility(Facility facility) throws DAOException {
		boolean ret = false;

		ConnectionHandler connHandler;
		// boolean coreUpd = false;

		// if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
		// if ((facility.getOldCorePlaceId() != null && !facility
		// .getOldCorePlaceId().equals(facility.getCorePlaceId()))
		// || (facility.getCorePlaceId() != null && !facility
		// .getCorePlaceId().equals(
		// facility.getOldCorePlaceId()))) {
		// coreUpd = true;
		// }
		// }
		//
		// if (coreUpd) {
		// connHandler = new ConnectionHandler("FacilitySQL.modifyFacility",
		// false);
		// } else {
		// connHandler = new ConnectionHandler(
		// "FacilitySQL.modifyFacilityExt", false);
		// }

		connHandler = new ConnectionHandler("FacilitySQL.modifyFacility", false);

		int i = 1;
		connHandler.setString(i++, facility.getDesc());
		connHandler.setString(i++, facility.getName());
		connHandler.setString(i++, facility.getOperatingStatusCd());
		connHandler
				.setString(i++, AbstractDAO
						.translateBooleanToIndicator(facility.getPortable()));
		connHandler.setString(i++, facility.getPortableGroupCd());
		connHandler.setTimestamp(i++, facility.getShutdownDate());
		connHandler.setTimestamp(i++, facility.getInactiveDate());
		connHandler.setInteger(i++, facility.getLastModified() + 1);
		connHandler.setString(i++, facility.getPortableGroupTypeCd());
		connHandler.setString(i++, facility.getPermitClassCd());
		connHandler.setString(i++, facility.getTransitStatusCd());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isTivInd()));
		connHandler.setTimestamp(i++, facility.getStartDate());
		connHandler.setTimestamp(i++, facility.getEndDate());
		connHandler.setInteger(i++, facility.getVersionId());
		connHandler.setString(i++, facility.getPermitStatusCd());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(facility.isCopyOnChange()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isMact()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isNeshaps()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isNsps()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isPsd()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(facility.isNsrNonattainment()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isGhgInd()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isSec112()));
		connHandler.setString(i++, facility.getFederalSCSCId());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility
						.isIntraStateVoucherFlag()));
		// if (coreUpd) {
		connHandler.setInteger(i++, facility.getCorePlaceId());
		// }
		connHandler.setString(i++, facility.getTvCertRepdueDate());
		connHandler
				.setString(i++, AbstractDAO
						.translateBooleanToIndicator(facility.isValidated()));
		connHandler
				.setString(i++, AbstractDAO
						.translateBooleanToIndicator(facility.isSubmitted()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(facility.isTvTypeA()));
		connHandler.setString(i++, facility.getLastSubmissionType());
		connHandler.setString(i++, facility.getDoLaaCd());
		connHandler.setInteger(i++, facility.getLastSubmissionVersion());
		connHandler.setString(i++, facility.getCerrClassCd());
		connHandler.setString(i++, facility.getGovtFacilityTypeCd());
		connHandler.setString(i++, facility.getFacilityTypeCd());
		connHandler.setString(i++, facility.getAfs());
		connHandler.setString(i++, facility.getAirProgramCd());
		connHandler.setString(i++, facility.getSipCompCd());
		connHandler.setString(i++, facility.getMactCompCd());
		connHandler.setString(i++, facility.getAqdEmissionFactorGroupCd());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(facility.isAdministrativeHold()));		
		connHandler.setInteger(i++, facility.getFpId());
		connHandler.setInteger(i++, facility.getLastModified());

		connHandler.update();

		String facilityTypeCd = facility.getFacilityTypeCd();

		// Delete all apis
		List<ApiGroup> apis = facility.getApis();
		if (apis != null && apis.size() > 0
				&& !FacilityTypeDef.hasApis(facilityTypeCd)) {
			connHandler = new ConnectionHandler(
					"FacilitySQL.deleteFacilityAllApis", false);
			connHandler.setInteger(1, facility.getFpId());
			connHandler.update();
		}

		ret = true;
		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveFacilityControlEquipment(Integer facilityId)
	 */
	public final List<FacilityNode> retrieveFacilityControlEquipment(
			Integer fpId) throws DAOException {
		logger.debug("Retrieving facility control equipment...");
		List<FacilityNode> ret;
		ret = retrieveFacilityNode(fpId, -1,
				"FacilitySQL.retrieveControlEquips", ControlEquipment.class);

		DataDetail[] dataTypes;
		ControlEquipmentData[] ceDataValues;
		DataDetail data;

		// Get Pollutant controlled and Data Types
		for (ControlEquipment tempCE : ret.toArray(new ControlEquipment[0])) {
			setPollutantsControlled(tempCE);
			if (tempCE.getEquipmentTypeCd() != null) {
//				FacilityDAO readOnlyFacilityDAO = (FacilityDAO) DAOFactory
//						.getDAO("FacilityDAO", "ReadOnly");
				//TODO read-only facility dao
				FacilityDAO readOnlyFacilityDAO = 
						(FacilityDAO)App.getApplicationContext().getBean("readOnlyFacilityDAO");
				//TODO check to see if the facility dao methods called below
				//     need to use the read only dao instead of 'this'
				dataTypes = CeDataDetailDef.getDataDetails(tempCE
								.getEquipmentTypeCd());
//				dataTypes = readOnlyFacilityDAO.retrieveContEquipDataDetail(tempCE
//								.getEquipmentTypeCd());
				for (DataDetail tempData : dataTypes) {
					tempCE.addCeTypeData(tempData.getDataDetailId(), tempData);
				}
				ceDataValues = retrieveContEquipDataDetailValues(tempCE
						.getFpNodeId());
				for (ControlEquipmentData tempCeData : ceDataValues) {
					data = tempCE.getCeTypeData(tempCeData.getDataDetailId());
					if (data == null) {
						String s = "For fpId "
								+ fpId
								+ ", failed on getCeTypeData() for equipment type "
								+ tempCE.getEquipmentTypeCd()
								+ " and argument "
								+ tempCeData.getDataDetailId();
						logger.error(s);
						throw new DAOException(s);
					}
					data.setDataDetailVal(tempCeData.getDataValue());
				}
			}
		}

		logger.debug("Done retrieving facility control equipment...");
		return ret;
	}

	public final List<ApiGroup> retrieveFacilityApis(int fpId)
			throws DAOException {
		logger.trace("DLTRACE --> retrieveFacilityApis");

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilityApis", false);

		connHandler.setInteger(1, fpId);

		List<ApiGroup> apis = connHandler.retrieveArray(ApiGroup.class);

		return apis;
	}

	public final void createFacilityApi(ApiGroup api) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.createFacilityApi", false);

		int index = 1;
		connHandler.setInteger(index++, api.getFpId());
		connHandler.setString(index++, api.getApiNo());

		connHandler.update();
	}

	public final void updateFacilityApi(ApiGroup api) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.updateFacilityApi", false);

		int index = 1;
		connHandler.setInteger(index++, api.getFpId());
		connHandler.setString(index++, api.getApiNo());
		connHandler.setInteger(index++, api.getApiCd());

		connHandler.update();
	}

	public final void deleteFacilityApi(Integer apiCd) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.deleteFacilityApi", false);

		connHandler.setInteger(1, apiCd);

		connHandler.update();
	}

	public final void deleteFacilityNaics(Integer fpId, String naics)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.deleteFacilityNAICS", false);

		connHandler.setInteger(1, fpId);
		connHandler.setString(2, naics);

		connHandler.update();
	}

	public final void deleteFacilityAllApis(Integer fpId) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.deleteFacilityAllApis", false);

		connHandler.setInteger(1, fpId);

		connHandler.remove();
	}

	private ControlEquipmentData[] retrieveContEquipDataDetailValues(
			Integer fpNodeId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveControlEquipmentData", true);

		connHandler.setInteger(1, fpNodeId);

		ArrayList<ControlEquipmentData> ret = connHandler
				.retrieveArray(ControlEquipmentData.class);

		return ret.toArray(new ControlEquipmentData[0]);
	}

	/**
	 * @see FacilityDAO#retrieveAllFacilityEgressPoints(Integer facilityId)
	 */
	public final List<FacilityNode> retrieveFacilityEgressPoints(
			Integer facilityId) throws DAOException {
		logger.debug("Retrieving facility release points...");
		List<FacilityNode> ret;
		ret = retrieveFacilityNode(facilityId, -1,
				"FacilitySQL.retrieveEgressPoints", EgressPoint.class);
		// Get CEMs
//		for (EgressPoint tempEgr : ret.toArray(new EgressPoint[0])) {
//			setEgressPointCems(tempEgr);
//		}

		logger.debug("Done retrieving facility release points...");
		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveFacilityEmissionProcessesByEmissionUnit(Integer
	 *      facilityId, Integer emissionUnitId)
	 */
	public final List<FacilityNode> retrieveFacilityEmissionProcesses(
			Integer fpId) throws DAOException {
		return retrieveFacilityNode(fpId, -1,
				"FacilitySQL.retrieveEmissionProcesses", EmissionProcess.class);
	}

	/**
	 * @see FacilityDAO#retrieveFacilityEmissionUnits(Integer facilityId)
	 */
	public final EmissionUnit[] retrieveFacilityEmissionUnits(Integer fpId)
			throws DAOException {
		checkNull(fpId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveEmissionUnits", true);

		connHandler.setInteger(1, fpId);

		ArrayList<EmissionUnit> ret = connHandler
				.retrieveArray(EmissionUnit.class);

		return ret.toArray(new EmissionUnit[0]);
	}

	/**
	 * @see FacilityDAO#retrieveReadOnlyGateWayFacility(Facility facility)
	 */
	public Facility retrieveReadOnlyGateWayFacility(Facility facility)
			throws DAOException {
		Facility ret = facility;

		//ret.setAssociatedMonitorGroup(retrieveFacilityMonitorGroup(ret.getFacilityId()));
		
		MonitorGroup mg = new MonitorGroup(retrieveFacilityMonitorGroup(ret.getFacilityId()));
		ret.setAssociatedMonitorGroup(mg);
		ret.setMactSubparts(retrieveFacilityMACTCodes(ret.getFpId()));
		ret.setNspsSubparts(retrieveFacilityNSPSCodes(ret.getFpId()));
		ret.setNeshapsSubpartsCompCds(retrieveFacilityNESHAPSCodes(ret
				.getFpId()));

		ret.setOwner(retrieveFacilityOwner(ret.getFacilityId()));

		// Get EU Emissions
		for (EmissionUnit tempEU : ret.getEmissionUnits().toArray(
				new EmissionUnit[0])) {
			retrieveEuEmissions(tempEU);
		}

		return ret;
	}
	
	public ModelingExtractResult[] modelingExtract(Boolean searchTypePolygon, 
			Boolean searchTypeRadial, 
			Double latitudeDegrees, Double longitudeDegrees, 
			Integer distanceMeters, List<String> pollutants, 
			List<String> excludedFacilityTypes,
			Double latitudeSwDegrees, Double longitudeSwDegrees,
			Double latitudeSeDegrees, Double longitudeSeDegrees,
			Double latitudeNeDegrees, Double longitudeNeDegrees,
			Double latitudeNwDegrees, Double longitudeNwDegrees
			) throws DAOException {
		ArrayList<ModelingExtractResult> ret = 
				new ArrayList<ModelingExtractResult>();
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = getReadOnlyConnection();

			String sqlStr = getSqlStr(searchTypePolygon, searchTypeRadial);
			sqlStr += addPollutantParams(pollutants);
			sqlStr += addExcludedFacilityTypeParams(excludedFacilityTypes);
			sqlStr += " ORDER BY RP_DISTANCE asc, F_DISTANCE asc, EPA_EMU_ID asc ";
			logger.debug(sqlStr);
			stmt = conn.prepareStatement(sqlStr);
			
			if (searchTypePolygon && !searchTypeRadial) {
				stmt.setDouble(1, latitudeSwDegrees);
				stmt.setDouble(2, longitudeSwDegrees);
				stmt.setDouble(3, latitudeSeDegrees);
				stmt.setDouble(4, longitudeSeDegrees);
				stmt.setDouble(5, latitudeNeDegrees);
				stmt.setDouble(6, longitudeNeDegrees);
				stmt.setDouble(7, latitudeNwDegrees);
				stmt.setDouble(8, longitudeNwDegrees);
			}
			else
			if (!searchTypePolygon && searchTypeRadial) {
				stmt.setDouble(1, latitudeDegrees);
				stmt.setDouble(2, longitudeDegrees);
				stmt.setInt(3, distanceMeters);
			}
			else
			{
				throw new DAOException("Unable to determine the search type (radial or polygon)");
			}
			
			boolean isResultSetNext = stmt.execute();

			while (!isResultSetNext) {
				if (stmt.getUpdateCount() == 0) {
					break;
				}
				isResultSetNext = stmt.getMoreResults();
			}
			
			ResultSet rs = stmt.getResultSet();
			
			while (null != rs && rs.next()) {
				ModelingExtractResult result = new ModelingExtractResult();
				result.populate(rs);
				ret.add(result);
			}
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(stmt);
			handleClosing(conn);
		}
		return ret.toArray(new ModelingExtractResult[0]);		
	}

	private String addPollutantParams(List<String> pollutants) {
		String params =	"";
		if (null != pollutants && !pollutants.isEmpty()) {
			params =	" and fpeue.POLLUTANT_CD IN ( ";
			for (int i = 0; i < pollutants.size(); i++) {
				params += "'" + SQLizeString(pollutants.get(i)) + "'";
				if (pollutants.size() != (i + 1)) {
					params += ",";
				}
			}
			params += " ) ";
		}
		return params;
	}
	
	private String addExcludedFacilityTypeParams(List<String> facilityTypes) {
		String params =	"";
		if (null != facilityTypes && !facilityTypes.isEmpty()) {
			params = " and fpft.FACILITY_TYPE_CD NOT IN ( ";
			for (int i = 0; i < facilityTypes.size(); i++) {
				params += "'" + SQLizeString(facilityTypes.get(i)) + "'";
				if (facilityTypes.size() != (i + 1)) {
					params += ",";
				}
			}
			params += " ) ";
		}
		return params;
	}
	
	private String getSqlStr(Boolean searchTypePolygon, Boolean searchTypeRadial)
			throws DAOException {
		String sqlStr = null;
		
		if (searchTypePolygon && !searchTypeRadial) {
			sqlStr = "FacilitySQL.extractModelingDataFromGeoPolygon";
		}
		else
		if (!searchTypePolygon && searchTypeRadial) {
			sqlStr = "FacilitySQL.extractModelingDataFromGeoRadial";
		}
		else
		{
			throw new DAOException("Unable to determine the search type (radial or polygon)");
		}
		return loadSQL(sqlStr);
	}

	public Facility retrieveFacility(Integer fpId) throws DAOException {
		logger.debug("Retrieving facility...");
		Facility ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		if (fpId != null) {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(loadSQL("FacilitySQL.retrieveFacility"));

				pStmt.setInt(1, fpId);

				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret = new Facility();
					ret.setMaxVersionFlag(true);
					ret.populate(rs);

					List<EmissionUnit> inValidList = new ArrayList<EmissionUnit>();
					do {
						Integer emissionUnitId = AbstractDAO.getInteger(rs,
								"emu_id");

						if (emissionUnitId != null) {
							EmissionUnit tempEU = new EmissionUnit();

							tempEU.populate(rs);

							// 0073
							if (EuOperatingStatusDef.IV.equals(tempEU
									.getOperatingStatusCd())
									|| EuOperatingStatusDef.SD.equals(tempEU
											.getOperatingStatusCd())) {
								inValidList.add(tempEU);
							} else {
								ret.addEmissionUnit(tempEU);
							}
							
							EmissionUnitType euType = new EmissionUnitType();
							euType.populate(rs);
							euType.setEmissionUnitTypeCd(tempEU.getEmissionUnitTypeCd());
							tempEU.setEmissionUnitType(euType);
						}
					} while (rs.next());

					// 0073
					for (EmissionUnit ieu : inValidList) {
						ret.addEmissionUnit(ieu);
					}

					if (getConfigManager().getNode("app")
							.getAsString("application-code")
							.equals(CommonConst.EXTERNAL_APP)) {
						retrieveGateWayCompleteFacilityProfile(ret);
					} else if (getConfigManager().getNode("app")
							.getAsString("application-code")
							.equals(CommonConst.PUBLIC_APP)) {
						retrieveGateWayCompleteFacilityProfile(ret);
					} else {
						retrieveCompleteFacilityProfile(ret);
					}
				}
				rs.close();
			} catch (Exception e) {
				handleException(e, conn);
			} finally {
				closeStatement(pStmt);
				handleClosing(conn);
			}
		}

		logger.debug("Done retrieving facility...");
		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveFacility(String facilityId)
	 */
	public final Facility retrieveFacility(String facilityId)
			throws DAOException {
		Facility ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		int attempts = 0;
		boolean keepTrying = true;
		checkNull(facilityId);

		do {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(loadSQL("FacilitySQL.retrieveFacilityByFacId"));

				pStmt.setString(1, facilityId);
				pStmt.setInt(2, -1);

				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret = new Facility();
					ret.setMaxVersionFlag(false);
					ret.populate(rs);

					List<EmissionUnit> inValidList = new ArrayList<EmissionUnit>();
					do {
						Integer emissionUnitId = AbstractDAO.getInteger(rs,
								"emu_id");

						if (emissionUnitId != null) {
							EmissionUnit tempEU = new EmissionUnit();

							tempEU.populate(rs);

							// 0073
							if (EuOperatingStatusDef.IV.equals(tempEU
									.getOperatingStatusCd())
									|| EuOperatingStatusDef.SD.equals(tempEU
											.getOperatingStatusCd())) {
								inValidList.add(tempEU);
							} else {
								ret.addEmissionUnit(tempEU);
							}
						}
					} while (rs.next());

					// 0073
					for (EmissionUnit ieu : inValidList) {
						ret.addEmissionUnit(ieu);
					}

					if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
						retrieveGateWayCompleteFacilityProfile(ret);
					} else if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
						retrieveGateWayCompleteFacilityProfile(ret);
					} else {
						retrieveCompleteFacilityProfile(ret);
					}
				}
				rs.close();
				keepTrying = false;
			} catch (SQLException e) {
				// if we get a "fetch out of sequence" exception keep trying,
				// this may go away after a few tries
				if (e.getMessage() != null
						&& e.getMessage().contains(
								"ORA-01002: fetch out of sequence")) {
					if (++attempts > 10) {
						keepTrying = false;
					} else {
						logger.error("Got fetch out-of-sequence: re-trying query "
								+ attempts);
					}
				} else {
					handleException(e, conn);
					keepTrying = false;
				}
			} catch (Exception e) {
				handleException(e, conn);
				keepTrying = false;
			} finally {
				closeStatement(pStmt);
				handleClosing(conn);
			}
		} while (keepTrying);

		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveFacility(String facilityId)
	 */
	public final List<String> retrieveFacilitiesWithActivePERDueDate(
			String doLaaCd, String countyCd) throws DAOException {
		List<String> result = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pStmt = null;
		StringBuffer s1 = new StringBuffer(
				loadSQL("FacilitySQL.retrieveFacilitiesWithActivePERDueDate"));
		setDefaultSearchLimit(-1);
		if (countyCd != null && countyCd.length() > 0) {
			s1.append(" ");
			s1.append(loadSQL("FacilitySQL.retrieveFacilitiesWithActivePERDueDateCounty"));
		}
		if (doLaaCd != null && doLaaCd.length() > 0) {
			if (countyCd == null || countyCd.length() == 0) {
				s1.append(" WHERE ff.do_laa_cd = ?");
			} else {
				s1.append(" AND ff.do_laa_cd = ?");
			}
		}
		s1.append(" ORDER BY ff.facility_id");
		try {
			conn = getReadOnlyConnection();
			pStmt = conn.prepareStatement(s1.toString());
			int cnt = 1;
			if (countyCd != null && countyCd.length() > 0)
				pStmt.setString(cnt++, countyCd);
			if (doLaaCd != null && doLaaCd.length() > 0)
				pStmt.setString(cnt++, doLaaCd);
			ResultSet rs = pStmt.executeQuery();

			while (rs.next()) {
				String facId = rs.getString("facility_id");
				result.add(facId);
			}
			rs.close();
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}
		return result;
	}

	public final Facility retrieveFacilityTable(String facilityId)
			throws DAOException {
		Facility ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		int attempts = 0;
		boolean keepTrying = true;
		checkNull(facilityId);

		do {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(loadSQL("FacilitySQL.retrieveFacilityTableByFacId"));

				pStmt.setString(1, facilityId);
				pStmt.setInt(2, -1);

				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret = new Facility();
					ret.setMaxVersionFlag(false);
					ret.populate(rs);
				}
				rs.close();
				keepTrying = false;
			} catch (SQLException e) {
				// if we get a "fetch out of sequence" exception keep trying,
				// this may go away after a few tries
				if (e.getMessage() != null
						&& e.getMessage().contains(
								"ORA-01002: fetch out of sequence")) {
					if (++attempts > 10) {
						keepTrying = false;
					} else {
						logger.error("Got fetch out-of-sequence: re-trying query "
								+ attempts);
					}
				} else {
					handleException(e, conn);
					keepTrying = false;
				}
			} catch (Exception e) {
				handleException(e, conn);
				keepTrying = false;
			} finally {
				closeStatement(pStmt);
				handleClosing(conn);
			}
		} while (keepTrying);

		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveFacility(String facilityId, Integer versionId)
	 */
	public final Facility retrieveFacility(String facilityId, Integer versionId)
			throws DAOException {
		Facility ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveFacilityData"));

			pStmt.setString(1, facilityId);
			pStmt.setInt(2, versionId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new Facility();
				ret.setMaxVersionFlag(false);
				ret.populate(rs);

				if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
					retrieveGateWayCompleteFacilityProfile(ret);
				} else if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
					retrieveGateWayCompleteFacilityProfile(ret);
				} else {
					retrieveCompleteFacilityProfile(ret);
				}
			}
			rs.close();
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveFacilityData(Integer fpId)
	 */
	public final Facility retrieveFacilityData(Integer fpId)
			throws DAOException {
		Facility ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveOnlyFacilityData"));

			pStmt.setInt(1, fpId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new Facility();
				ret.setMaxVersionFlag(false);
				ret.populate(rs);
			}
			rs.close();
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveFacilityByCorePlaceId(Integer corePlaceId)
	 */
	public final Facility retrieveFacilityByCorePlaceId(Integer corePlaceId)
			throws DAOException {
		Facility ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		checkNull(corePlaceId);

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveFacDataByCoreId"));

			pStmt.setInt(1, corePlaceId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new Facility();
				ret.setMaxVersionFlag(false);
				ret.populate(rs);

				if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
					retrieveGateWayCompleteFacilityProfile(ret);
				} else if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
					retrieveGateWayCompleteFacilityProfile(ret);
				} else {
					retrieveCompleteFacilityProfile(ret);
				}
			}
			rs.close();
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	public final Facility retrieveFacilityByFacilityId(String facilityId)
			throws DAOException {
		logger.trace("DLTRACE --> retrieveFacilityByFacilityId");
		Facility ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		checkNull(facilityId);

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveFacDataByFacId"));

			pStmt.setString(1, facilityId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new Facility();
				ret.setMaxVersionFlag(false);
				ret.populate(rs);

				if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
					retrieveGateWayCompleteFacilityProfile(ret);
				} else if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
					retrieveGateWayCompleteFacilityProfile(ret);
				} else {
					retrieveCompleteFacilityProfile(ret);
				}
			}
			rs.close();
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveEmissionUnitPollutants(EmissionUnit
	 *      emissionUnit)
	 */

	public final void retrieveEuEmissions(EmissionUnit emissionUnit)
			throws DAOException {
		logger.trace("DLTRACE --> retrieveEuEmissions");
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveEuEmissions", true);

		connHandler.setInteger(1, emissionUnit.getEmuId());
		ArrayList<EuEmission> ret = connHandler.retrieveArray(EuEmission.class);

		EuEmission[] emissions = ret.toArray(new EuEmission[0]);
		for (int i = 0; i < emissions.length; i++) {
			emissionUnit.addEuEmission(emissions[i]);
		}
	}

	/**
	 * @see FacilityDAO#retrieveEmissionUnit(Integer fpId, String epaEmuId)
	 */

	public final EmissionUnit retrieveEmissionUnit(Integer fpId, String epaEmuId)
			throws DAOException {
		EmissionUnit ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveEmissionUnitByEpa"));

			pStmt.setInt(1, fpId);
			pStmt.setString(2, epaEmuId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new EmissionUnit();

				ret.populate(rs);
			}
			rs.close();

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveEmissionUnit(Integer fpId, Integer
	 *      emissionUnitId)
	 */
	public final EmissionUnit retrieveEmissionUnit(Integer emuId)
			throws DAOException {
		EmissionUnit ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveEmissionUnit"));

			pStmt.setInt(1, emuId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new EmissionUnit();

				ret.populate(rs);
			}
			rs.close();

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveEmissionProcess(Integer emissionProcessFpnodeId)
	 */

	public final EmissionProcess retrieveEmissionProcess(
			Integer emissionProcessFpnodeId) throws DAOException {
		EmissionProcess ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveEmissionProcess"));

			pStmt.setInt(1, emissionProcessFpnodeId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new EmissionProcess();

				ret.populate(rs);
			}
			rs.close();

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveEmissionProcess(Integer fpId, String processId)
	 */

	public final EmissionProcess retrieveEmissionProcess(Integer fpId,
			String processId) throws DAOException {
		EmissionProcess ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveEmissionProcessByProcId"));

			pStmt.setInt(1, fpId);
			pStmt.setString(2, processId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new EmissionProcess();

				ret.populate(rs);
			}
			rs.close();

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveEmissionProcess(Integer fpId, Integer emuId,
	 *      String sccId)
	 */

	public final EmissionProcess retrieveEmissionProcess(Integer fpId,
			Integer emuId, String sccId) throws DAOException {
		EmissionProcess ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveEmissionProcessBySccId"));

			pStmt.setInt(1, fpId);
			pStmt.setInt(2, emuId);
			pStmt.setString(3, sccId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new EmissionProcess();

				ret.populate(rs);
			}
			rs.close();

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	public final ArrayList<EmissionUnitTypeDef> retrieveEmissionUnitTypeDefs()
			throws DAOException {
		ArrayList<EmissionUnitTypeDef> ret = null;
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveEmissionUnitTypeDefs", false);

		try {
			ret = connHandler.retrieveArray(EmissionUnitTypeDef.class);
		} catch (DAOException ex) {
			throw ex;
		}

		return ret;
	}

	private void setPollutantsControlled(ControlEquipment controlEquip)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveCePollsControlled", true);

		connHandler.setInteger(1, controlEquip.getFpNodeId());
		ArrayList<PollutantsControlled> ret = connHandler
				.retrieveArray(PollutantsControlled.class);

		PollutantsControlled[] pollutants = ret
				.toArray(new PollutantsControlled[0]);
		for (int i = 0; i < pollutants.length; i++) {
			controlEquip.addPollutantsControlled(pollutants[i]);
		}
	}

	/**
	 * @see FacilityDAO#retrieveControlEquipment(Integer controlEquipFpnodeId)
	 */
	public final ControlEquipment retrieveControlEquipment(
			Integer controlEquipFpnodeId) throws DAOException {
		ControlEquipment ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveControlEquipment"));

			pStmt.setInt(1, controlEquipFpnodeId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new ControlEquipment();

				ret.populate(rs);
			}
			rs.close();

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveControlEquipment(Integer fpId, String
	 *      controlEquipId)
	 */
	public final ControlEquipment retrieveControlEquipment(Integer fpId,
			String ControlEquipId) throws DAOException {
		ControlEquipment ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveControlEquipmentById"));

			pStmt.setInt(1, fpId);
			pStmt.setString(2, ControlEquipId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new ControlEquipment();

				ret.populate(rs);
			}
			rs.close();

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveEgressPoint(Integer egressFpnodeId)
	 */

	public final EgressPoint retrieveEgressPoint(Integer egressFpnodeId)
			throws DAOException {
		EgressPoint ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveEgressPoint"));

			pStmt.setInt(1, egressFpnodeId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new EgressPoint();

				ret.populate(rs);
			}
			rs.close();

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveEgressPoint(Integer fpId, String egressPointId)
	 */

	public final EgressPoint retrieveEgressPoint(Integer fpId,
			String egressPointId) throws DAOException {
		EgressPoint ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveEgressPointById"));

			pStmt.setInt(1, fpId);
			pStmt.setString(2, egressPointId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new EgressPoint();

				ret.populate(rs);
			}
			rs.close();

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	@Override
	public final FacilityList[] searchFacilities(String facilityName,
			String facilityId, String companyName, Integer corePlaceId,
			String countyCd, String operatingStatusCd, String doLaaCd,
			String naicsCd, String permitClassCd, String tvPermitStatusCd,
			String address1, String city, String zip5, String portable,
			String portableGroupCd, boolean unlimitedResults,
			String facilityTypeCd) throws DAOException {

		if (!Utility.isNullOrEmpty(facilityId)) {
			String format = "F%06d";
			facilityId = facilityId.trim();
			int tempId;
			try {
				tempId = Integer.parseInt(facilityId);
				facilityId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}

		String statementSQL;

		if (unlimitedResults) {
			setDefaultSearchLimit(-1);
		}

		if (naicsCd == null) {
			statementSQL = loadSQL("FacilitySQL.findFacilities");
		} else {
			statementSQL = loadSQL("FacilitySQL.findFacilitiesNaics");
		}

		StringBuffer whereClause = new StringBuffer("");

		if (facilityName != null && facilityName.trim().length() > 0) {
			whereClause.append(" AND LOWER(facility_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(facilityName.trim().replace("*",
					"%")));
			whereClause.append("')");
		}
		if (facilityId != null && facilityId.trim().length() > 0) {
			whereClause.append(" AND LOWER(ff.facility_id) LIKE ");
			whereClause.append("LOWER('");
			whereClause
					.append(SQLizeString(facilityId.trim().replace("*", "%")));
			whereClause.append("')");
		}
		if (companyName != null) {
			whereClause.append(" AND ccm.cmp_id = '");
			whereClause.append(companyName);
			whereClause.append("'");
		}
		if (corePlaceId != null) {
			whereClause.append(" AND ff.core_place_id = " + corePlaceId);
		}
		if (operatingStatusCd != null) {
			whereClause.append(" AND ff.operating_status_cd = '");
			whereClause.append(operatingStatusCd);
			whereClause.append("'");
		}
		if (countyCd != null) {
			whereClause.append(" AND ca.county_cd = '");
			whereClause.append(countyCd);
			whereClause.append("'");
		}
		if (doLaaCd != null) {
			whereClause.append(" AND ca.do_laa_cd = '");
			whereClause.append(doLaaCd);
			whereClause.append("'");
		}
		if (naicsCd != null) {
			whereClause.append(" AND fnxref.naics_cd = '");
			whereClause.append(naicsCd);
			whereClause.append("'");
		}
		if (permitClassCd != null) {
			whereClause.append(" AND permit_classification_cd = '");
			whereClause.append(permitClassCd);
			whereClause.append("'");
		}
		if (tvPermitStatusCd != null) {
			whereClause.append(" AND permit_status_cd = '");
			whereClause.append(tvPermitStatusCd);
			whereClause.append("'");
		}
		if (address1 != null && address1.trim().length() > 0) {
			whereClause.append(" AND LOWER(ca.address1) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(address1.trim().replace("*", "%")));
			whereClause.append("')");
		}
		if (city != null) {
			whereClause.append(" AND ca.city = '");
			whereClause.append(city);
			whereClause.append("'");
		}
		if (zip5 != null) {
			whereClause.append(" AND ca.zip5 = '");
			whereClause.append(zip5);
			whereClause.append("'");
		}
		if (portable != null) {
			whereClause.append(" AND ff.portable = '");
			whereClause.append(portable);
			whereClause.append("'");
		}
		if (portableGroupCd != null) {
			whereClause.append(" AND ff.portable_group_cd = '");
			whereClause.append(portableGroupCd);
			whereClause.append("'");
		}
		if (facilityTypeCd != null) {
			whereClause.append(" AND ff.facility_type_cd = '");
			whereClause.append(facilityTypeCd);
			whereClause.append("'");
		}

		StringBuffer sortBy = new StringBuffer(" ORDER BY ff.facility_id");

		statementSQL += whereClause.toString() + " " + sortBy.toString();

		ConnectionHandler connHandler = new ConnectionHandler(true);
		
		connHandler.setSQLStringRaw(statementSQL);

		connHandler.setInteger(1, -1);

		ArrayList<FacilityList> ret = connHandler.retrieveArray(
				FacilityList.class, defaultSearchLimit);

		return ret.toArray(new FacilityList[0]);
	}

	public final FacilityEmissionUnit[] searchEuStatus(String facilityName,
			String facilityId, String facilityOperatingStatusCd,
			String euOperatingStatusCd, String doLaaCd, String permitClassCd,
			boolean unlimitedResults) throws DAOException {

		String statementSQL;

		if (unlimitedResults) {
			setDefaultSearchLimit(-1);
		}

		statementSQL = loadSQL("FacilitySQL.findFacilityEmissionUnits");

		StringBuffer whereClause = new StringBuffer("");

		if (facilityName != null && facilityName.trim().length() > 0) {
			whereClause.append(" AND LOWER(facility_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(facilityName.trim().replace("*",
					"%")));
			whereClause.append("')");
		}
		if (facilityId != null && facilityId.trim().length() > 0) {
			whereClause.append(" AND LOWER(ff.facility_id) LIKE ");
			whereClause.append("LOWER('");
			whereClause
					.append(SQLizeString(facilityId.trim().replace("*", "%")));
			whereClause.append("')");
		}

		if (facilityOperatingStatusCd != null) {
			whereClause.append(" AND ff.operating_status_cd = '");
			whereClause.append(facilityOperatingStatusCd);
			whereClause.append("'");
		}

		if (euOperatingStatusCd != null) {
			whereClause.append(" AND feu.operating_status_cd = '");
			whereClause.append(euOperatingStatusCd);
			whereClause.append("'");
		}

		if (doLaaCd != null) {
			whereClause.append(" AND ff.do_laa_cd = '");
			whereClause.append(doLaaCd);
			whereClause.append("'");
		}

		if (permitClassCd != null) {
			whereClause.append(" AND permit_classification_cd = '");
			whereClause.append(permitClassCd);
			whereClause.append("'");
		}

		StringBuffer sortBy = new StringBuffer(
				" ORDER BY ff.facility_id, epa_emu_id");

		statementSQL += whereClause.toString() + " " + sortBy.toString();

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(statementSQL);

		connHandler.setInteger(1, -1);

		ArrayList<FacilityEmissionUnit> ret = connHandler.retrieveArray(
				FacilityEmissionUnit.class, defaultSearchLimit);

		return ret.toArray(new FacilityEmissionUnit[0]);
	}

	public final FacilityList[] searchTvPermitFacilities(String facilityName,
			String facilityId, String countyCd, String doLaaCd,
			Timestamp startDt, Timestamp endDt, boolean unlimitedResults)
			throws DAOException {

		if (unlimitedResults) {
			setDefaultSearchLimit(-1);
		}

		String statementSQL = loadSQL("FacilitySQL.findTvPermitNoticeFacilities");

		StringBuffer whereClause = new StringBuffer("");

		if (facilityName != null && facilityName.trim().length() > 0) {
			whereClause.append(" AND LOWER(facility_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(facilityName.replace("*", "%")));
			whereClause.append("')");
		}
		if (facilityId != null && facilityId.trim().length() > 0) {
			whereClause.append(" AND LOWER(ff.facility_id) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(facilityId.replace("*", "%")));
			whereClause.append("')");
		}

		if (countyCd != null) {
			whereClause.append(" AND ca.county_cd = '");
			whereClause.append(countyCd);
			whereClause.append("'");
		}
		if (doLaaCd != null) {
			whereClause.append(" AND fpdo.do_laa_cd = '");
			whereClause.append(doLaaCd);
			whereClause.append("'");
		}

		StringBuffer sortBy = new StringBuffer(" ORDER BY ff.facility_id");

		statementSQL += whereClause.toString() + " " + sortBy.toString();

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(statementSQL);

		connHandler.setInteger(1, -1);
		connHandler.setTimestamp(2, startDt);
		connHandler.setTimestamp(3, endDt);

		ArrayList<FacilityList> ret = connHandler.retrieveArray(
				FacilityList.class, defaultSearchLimit);

		return ret.toArray(new FacilityList[0]);
	}

	public final FacilityList[] searchTvExpFacilities(String facilityName,
			String facilityId, String countyCd, String doLaaCd,
			boolean unlimitedResults) throws DAOException {

		if (unlimitedResults) {
			setDefaultSearchLimit(-1);
		}

		String statementSQL = loadSQL("FacilitySQL.findTvExpFacilities");

		StringBuffer whereClause = new StringBuffer("");

		if (facilityName != null && facilityName.trim().length() > 0) {
			whereClause.append(" AND LOWER(facility_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(facilityName.replace("*", "%")));
			whereClause.append("')");
		}
		if (facilityId != null && facilityId.trim().length() > 0) {
			whereClause.append(" AND LOWER(ff.facility_id) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(facilityId.replace("*", "%")));
			whereClause.append("')");
		}

		if (countyCd != null) {
			whereClause.append(" AND ca.county_cd = '");
			whereClause.append(countyCd);
			whereClause.append("'");
		}
		if (doLaaCd != null) {
			whereClause.append(" AND fpdo.do_laa_cd = '");
			whereClause.append(doLaaCd);
			whereClause.append("'");
		}

		StringBuffer sortBy = new StringBuffer(" ORDER BY ff.facility_id");

		statementSQL += whereClause.toString() + " " + sortBy.toString();

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(statementSQL);

		connHandler.setInteger(1, -1);

		ArrayList<FacilityList> ret = connHandler.retrieveArray(
				FacilityList.class, defaultSearchLimit);

		return ret.toArray(new FacilityList[0]);
	}

	public final FacilityHistList[] searchFacilitiesHist(
			FacilityHistList searchFacility) throws DAOException {

		checkNull(searchFacility);

		if (searchFacility.isUnlimitedResults()) {
			setDefaultSearchLimit(-1);
		}

		String statementSQL = loadSQL("FacilitySQL.findFacilitiesHist");

		StringBuffer whereClause = new StringBuffer("");

		if (searchFacility.getFacilityId() != null
				&& searchFacility.getFacilityId().trim().length() > 0) {
			whereClause.append(" AND LOWER(ff.facility_id) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(searchFacility.getFacilityId()
					.trim().replace("*", "%")));
			whereClause.append("')");
		}
		if (searchFacility.getName() != null
				&& searchFacility.getName().trim().length() > 0) {
			whereClause.append(" AND LOWER(ff.facility_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(searchFacility.getName().trim()
					.replace("*", "%")));
			whereClause.append("')");
		}
		if (searchFacility.getDesc() != null
				&& searchFacility.getDesc().trim().length() > 0) {
			whereClause.append(" AND LOWER(ff.facility_desc) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(searchFacility.getDesc().trim()
					.replace("*", "%")));
			whereClause.append("')");
		}

		if (searchFacility.getAddressLine1() != null
				&& searchFacility.getAddressLine1().trim().length() > 0) {
			whereClause.append(" AND LOWER(ca.address1) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(searchFacility.getAddressLine1()
					.trim().replace("*", "%")));
			whereClause.append("')");
		}

		StringBuffer sortBy = new StringBuffer(
				"order by ff.facility_id, ff.start_dt DESC");

		statementSQL += whereClause.toString() + " " + sortBy.toString();

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(statementSQL);

		ArrayList<FacilityHistList> ret = connHandler.retrieveArray(
				FacilityHistList.class, defaultSearchLimit);

		return ret.toArray(new FacilityHistList[0]);
	}

	public final FacilityVersion[] retrieveAllFacilityVersions(String facilityId)
			throws DAOException {
		String statementSQL = loadSQL("FacilitySQL.retrieveAllFacilityVersions");

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(statementSQL);

		connHandler.setString(1, facilityId);

		ArrayList<FacilityVersion> ret = connHandler
				.retrieveArray(FacilityVersion.class);
		FacilityVersion currFac = null;

		ArrayList<FacilityVersion> facVers = new ArrayList<FacilityVersion>();

		for (BaseDBObject temp : ret) {
			boolean found = false;
			for (FacilityVersion tempFac : facVers) {
				if (tempFac.getFpId()
						.equals(((FacilityVersion) temp).getFpId())) {
					found = true;
					break;
				}
			}
			if (!found) {
				if (((FacilityVersion) temp).getEndDate() == null) {
					currFac = (FacilityVersion) temp;
				} else {
					facVers.add((FacilityVersion) temp);
				}
			}
		}
		facVers.add(0, currFac);

		return facVers.toArray(new FacilityVersion[0]);
	}

	public final FacilityVersion[] retrieveAllMigFacilityVersions(
			String facilityId) throws DAOException {
		String statementSQL = loadSQL("FacilitySQL.retrieveAllMigFacilityVersions");

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(statementSQL);

		connHandler.setString(1, facilityId);

		ArrayList<FacilityVersion> ret = connHandler
				.retrieveArray(FacilityVersion.class);

		return ret.toArray(new FacilityVersion[0]);
	}

	/**
	 * @see FacilityDAO#retrieveFacilityContacts(String facilityId)
	 */
	public final List<Contact> retrieveFacilityContacts(String facilityId)
			throws DAOException {
		logger.trace("DLTRACE --> retrieveFacilityContacts");
		List<Contact> ret = new ArrayList<Contact>();
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveFacilityContacts"));

			pStmt.setString(1, facilityId);

			ResultSet rs = pStmt.executeQuery();

			while (rs.next()) {
				Integer contactId = AbstractDAO.getInteger(rs, "contact_id");
				Contact tempContact = null;

				for (Contact tempC : ret) {
					if (tempC.getContactId().equals(contactId)) {
						tempContact = tempC;
						break;
					}
				}
				if (tempContact == null) {
					tempContact = new Contact();

					tempContact.populate(rs);
					tempContact.setFacilityId(facilityId);
					ret.add(tempContact);
				}

				if (rs.getString("contact_type_cd") != null) {
					ContactType tempType = new ContactType();

					tempType.populate(rs);

					tempContact.addContactType(tempType);
				}
			}
			rs.close();

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveFacilityContacts(String facilityId)
	 */
	public final List<Contact> retrieveStagingFacilityContacts(String facilityId)
			throws DAOException {
		List<Contact> ret = new ArrayList<Contact>();
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveStagingFacilityContacts"));

			pStmt.setString(1, facilityId);

			ResultSet rs = pStmt.executeQuery();

			while (rs.next()) {
				Integer contactId = AbstractDAO.getInteger(rs, "contact_id");
				Contact tempContact = null;

				for (Contact tempC : ret) {
					if (tempC.getContactId().equals(contactId)) {
						tempContact = tempC;
						break;
					}
				}
				if (tempContact == null) {
					tempContact = new Contact();

					tempContact.populate(rs);
					tempContact.setFacilityId(facilityId);
					ret.add(tempContact);
				}

				if (rs.getString("contact_type_cd") != null) {
					ContactType tempType = new ContactType();

					tempType.populate(rs);

					tempContact.addContactType(tempType);
				}
			}
			rs.close();

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	public final List<Contact> retrieveFacilityContacts(String facilityId,
			boolean active) throws DAOException {
		List<Contact> ret = new ArrayList<Contact>();
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			if (active) {
				pStmt = conn
						.prepareStatement(loadSQL("FacilitySQL.retrieveActiveFacilityContacts"));
			} else {
				pStmt = conn
						.prepareStatement(loadSQL("FacilitySQL.retrieveFacilityContacts"));
			}

			pStmt.setString(1, facilityId);

			ResultSet rs = pStmt.executeQuery();

			while (rs.next()) {
				Integer contactId = AbstractDAO.getInteger(rs, "contact_id");
				Contact tempContact = null;

				for (Contact tempC : ret) {
					if (tempC.getContactId().equals(contactId)) {
						tempContact = tempC;
						break;
					}
				}
				if (tempContact == null) {
					tempContact = new Contact();

					tempContact.populate(rs);
					tempContact.setFacilityId(facilityId);
					ret.add(tempContact);
				}

				if (rs.getString("contact_type_cd") != null) {
					ContactType tempType = new ContactType();

					tempType.populate(rs);

					tempContact.addContactType(tempType);
				}
			}
			rs.close();

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveFacilityContact(Contact contact)
	 */
	public final Contact[] retrieveDupFacilityContact(Contact contact)
			throws DAOException {
		String statementSQL = createSqlRetrieveFacilityContact(contact);
		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(statementSQL);
		connHandler.setString(1, contact.getFacilityId());
		ArrayList<Contact> ret = connHandler.retrieveArray(Contact.class);
		logger.debug("DEBUG]" + ret.size() + " duplicate contacts found");
		return ret.toArray(new Contact[0]);
	}

	/**
	 * @see FacilityDAO#retrieveFacilityContact(Contact contact)
	 */
	public final Contact retrieveFacilityContact(Contact contact)
			throws DAOException {
		Contact ret = null;

		String statementSQL = createSqlRetrieveFacilityContact(contact);

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(statementSQL);

		connHandler.setString(1, contact.getFacilityId());

		ret = (Contact) connHandler.retrieve(Contact.class);

		return ret;
	}

	private final String createSqlRetrieveFacilityContact(Contact contact) {
		String statementSQL = loadSQL("FacilitySQL.retrieveFacilityContact");

		StringBuffer whereClause = new StringBuffer("");

		if (contact.getFirstNm() != null && contact.getFirstNm().length() > 0) {
			whereClause.append(" AND LOWER(cc.first_nm) = ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(contact.getFirstNm()));
			whereClause.append("')");
		} else {
			whereClause.append(" AND cc.first_nm is null");
		}

		if (contact.getLastNm() != null && contact.getLastNm().length() > 0) {
			whereClause.append(" AND LOWER(cc.last_nm) = ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(contact.getLastNm()));
			whereClause.append("')");
		} else {
			whereClause.append(" AND cc.last_nm is null");
		}

		if (contact.getMiddleNm() != null && contact.getMiddleNm().length() > 0) {
			whereClause.append(" AND LOWER(cc.middle_nm) = ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(contact.getMiddleNm()));
			whereClause.append("')");
		} else {
			whereClause.append(" AND cc.middle_nm is null");
		}

		if (contact.getCompanyId() != null) {
			whereClause.append(" AND cc.company_id = ");
			whereClause.append(contact.getCompanyId());
			whereClause.append(")");
		} else {
			whereClause.append(" AND cc.company_id is null");
		}

		statementSQL += whereClause.toString();
		return statementSQL;
	}

	/**
	 * @see FacilityDAO#retrieveFacilitySICCodes(int fpId)
	 */
	private List<String> retrieveFacilitySICCodes(int fpId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilitySICCodes", true);

		connHandler.setInteger(1, fpId);

		return connHandler.retrieveJavaObjectArray(String.class);
	}

	public List<String> retrieveFacilityNAICSCodes(Integer fpId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilityNAICSCodes", true);

		connHandler.setInteger(1, fpId);

		return connHandler.retrieveJavaObjectArray(String.class);
	}

	/**
	 * @see FacilityDAO#retrieveFacilityNESHAPSCodes(int fpId)
	 */
	private List<PollutantCompCode> retrieveFacilityNESHAPSCodes(int fpId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilityNESHAPSCodes", true);
		connHandler.setInteger(1, fpId);
		ArrayList<PollutantCompCode> ret = connHandler
				.retrieveArray(PollutantCompCode.class);

		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveFacilityNSPSCodes(int fpId)
	 */
	private List<String> retrieveFacilityNSPSCodes(int fpId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilityNSPSCodes", true);

		connHandler.setInteger(1, fpId);

		return connHandler.retrieveJavaObjectArray(String.class);
	}

	/**
	 * @see FacilityDAO#retrieveFacilityMACTCodes(Integer fpId)
	 */
	private List<String> retrieveFacilityMACTCodes(int fpId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilityMACTCodes", true);

		connHandler.setInteger(1, fpId);

		return connHandler.retrieveJavaObjectArray(String.class);
	}

	/**
	 * @see FacilityDAO#retrieveFacilityNotificationCounties(Integer fpId)
	 */
	private List<String> retrieveFacilityNotificationCounties(int fpId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilityNotificationCounties", true);

		connHandler.setInteger(1, fpId);

		return connHandler.retrieveJavaObjectArray(String.class);
	}

	/**
	 * @see FacilityDAO#retrieveFacilityNotes(String facilityId)
	 */
	public final FacilityNote[] retrieveFacilityNotes(String facilityId)
			throws DAOException {
		logger.trace("DLTRACE --> retrieveFacilityNotes");
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilityNotes", true);

		connHandler.setString(1, facilityId);

		ArrayList<FacilityNote> ret = connHandler
				.retrieveArray(FacilityNote.class);

		return ret.toArray(new FacilityNote[0]);
	}

	/**
	 * @see FacilityDAO#retrieveFacilityRUMs(String facilityId)
	 */
	public final FacilityRUM[] retrieveFacilityRUMs(String facilityId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilityRUMs", true);

		connHandler.setString(1, facilityId);

		ArrayList<FacilityRUM> ret = connHandler
				.retrieveArray(FacilityRUM.class);

		return ret.toArray(new FacilityRUM[0]);
	}

	/**
	 * @see FacilityDAO#retrieveFacilityRUM(int rumId)
	 */
	public final FacilityRUM retrieveFacilityRUM(int rumId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilityRUM", true);
		connHandler.setInteger(1, rumId);
		return (FacilityRUM) connHandler.retrieve(FacilityRUM.class);
	}

	/**
	 * @see FacilityDAO#retrieveFacilityAttachments(String facilityId)
	 */
	public final Attachment[] retrieveFacilityAttachments(String facilityId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilityAttachments", true);

		connHandler.setString(1, facilityId);

		ArrayList<Attachment> ret = connHandler.retrieveArray(Attachment.class);

		return ret.toArray(new Attachment[0]);
	}

	public final FacilityRole[] createFacilityRoles(FacilityRole newRoles[])
			throws DAOException {
		createFacilityRoles(Arrays.asList(newRoles));
		return newRoles;
	}

	public final void removeFacilityRoles(String facilityId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeFacilityRoles", false);

		connHandler.setString(1, facilityId);

		connHandler.remove();

		return;
	}

	public final FacilityRole[] retrieveFacilityRoles(String facilityId)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilityRoles", true);

		connHandler.setString(1, facilityId);

		ArrayList<FacilityRole> ret = connHandler
				.retrieveArray(FacilityRole.class);

		FacilityRole[] facRoles = ret.toArray(new FacilityRole[0]);

		for (int i = 0; i < facRoles.length; i++) {
			facRoles[i].setFacilityId(facilityId);
		}

		return facRoles;
	}

	public final FacilityRole[] retrieveFacilityRolesByUserId(Integer userId)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilityRolesByUserId", true);

		connHandler.setInteger(1, userId);

		ArrayList<FacilityRole> ret = connHandler
				.retrieveArray(FacilityRole.class);

		FacilityRole[] facRoles = ret.toArray(new FacilityRole[0]);

		return facRoles;
	}

	private List<FacilityNode> retrieveFacilityNode(Integer fpId,
			Integer versionId, String sqlString,
			Class<? extends BaseDB> classType) throws DAOException {
		logger.debug("Retrieving facility emission processes...");
		List<FacilityNode> ret = new ArrayList<FacilityNode>();
		List<FacilityNode> facNodeList = new ArrayList<FacilityNode>();
		List<FacilityRelationship> frList = new ArrayList<FacilityRelationship>();
		Connection conn = null;
		PreparedStatement pStmt = null;
		int attempts = 0;
		boolean keepTrying = true;

		logger.debug("fpId = " + fpId);

		do {
			try {
				conn = getConnection();
				pStmt = conn.prepareStatement(loadSQL(sqlString));
				pStmt.setInt(1, fpId);
				ResultSet rs = pStmt.executeQuery();

				logger.debug("Fetch Size = " + rs.getFetchSize());

				/* Extract the ControlEquipment and the relationships */
				int rowCount = 0;
				while (rs.next()) {
					if (++rowCount % 10 == 0) {
						logger.debug("rows retrieved = " + rowCount);
					}
					FacilityNode temp = (FacilityNode) classType.newInstance();
					temp.populate(rs);
					facNodeList.add(temp);

					FacilityRelationship tempFR = new FacilityRelationship();
					tempFR.populate(rs);
					frList.add(tempFR);
				}
				rs.close();

				for (FacilityNode temp : facNodeList) {
					if (FacilityNode.findFpNode(ret, temp.getFpNodeId()) == null) {
						ret.add(temp);
					}
				}

				/* Add the relationships to the ControlEquipment */
				for (FacilityRelationship tempFR : frList) {
					if (classType.equals(EgressPoint.class)) {
						if (tempFR.getToNodeId() != null) {
							(FacilityNode.findFpNode(ret, tempFR.getToNodeId()))
									.addRelationship(tempFR);
						}
					} else {
						if (tempFR.getFromNodeId() != null) {
							(FacilityNode.findFpNode(ret,
									tempFR.getFromNodeId()))
									.addRelationship(tempFR);
						}
					}
				}
				keepTrying = false;
			} catch (SQLException e) {
				// if we get a "fetch out of sequence" exception keep trying,
				// this may go away after a few tries
				if (e.getMessage() != null
						&& e.getMessage().contains(
								"ORA-01002: fetch out of sequence")) {
					if (++attempts > 10) {
						keepTrying = false;
					} else {
						logger.error("Got fetch out-of-sequence: re-trying query "
								+ attempts);
					}
				} else {
					handleException(e, conn);
					keepTrying = false;
				}
			} catch (Exception e) {
				handleException(e, conn);
				keepTrying = false;
			} finally {
				closeStatement(pStmt);
				handleClosing(conn);
			}
		} while (keepTrying);

		logger.debug("Done retrieving facility emission processes...");
		return ret;
	}

	public final FacilityNode createFacilityNode(Integer fpId, Integer nodeId,
			Integer corrId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.createFPNode", false);
		Integer correlationId = corrId;
		Integer id = nextSequenceVal("S_FPNode_Id", nodeId);
		if (corrId == null) {
			correlationId = id;
		}
		connHandler.setInteger(1, id);
		connHandler.setInteger(2, fpId);
		connHandler.setInteger(3, correlationId);

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		FacilityNode ret = new FacilityNode();

		ret.setFpNodeId(id);
		ret.setCorrelationId(correlationId);
		ret.setLastModified(1);

		return ret;
	}

	public final void removeFacilityNode(Integer fpNodeId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeFPNode", false);

		connHandler.setInteger(1, fpNodeId);

		connHandler.remove();
	}

	public final FacilityRelationship createRelationShip(Integer fromFpNodeId,
			Integer toFpNodeId) throws DAOException {
		// logger.error("At createRelationShip " + fromFpNodeId + ":" +
		// toFpNodeId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.createFPRelationShip", false);

		connHandler.setInteger(1, fromFpNodeId);
		connHandler.setInteger(2, toFpNodeId);

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		FacilityRelationship ret = new FacilityRelationship();
		ret.setFromNodeId(fromFpNodeId);
		ret.setToNodeId(toFpNodeId);
		ret.setLastModified(1);

		return ret;
	}

	public final FacilityRelationship createRelationShip(Integer fromFpNodeId,
			Integer toFpNodeId, Float flowFactor) throws DAOException {
		// logger.error("At createRelationShip " + fromFpNodeId + ":" +
		// toFpNodeId + "--" + flowFactor);
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.createFPRelationShipWithFlowFactor", false);

		connHandler.setInteger(1, fromFpNodeId);
		connHandler.setInteger(2, toFpNodeId);
		connHandler.setFloat(3, flowFactor);

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		FacilityRelationship ret = new FacilityRelationship();
		ret.setFromNodeId(fromFpNodeId);
		ret.setToNodeId(toFpNodeId);
		ret.setFlowFactor(flowFactor);
		ret.setLastModified(1);

		return ret;
	}

	public final void removeRelationShip(Integer fromFpNodeId,
			Integer toFpNodeId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeFPRelationShip", false);

		connHandler.setInteger(1, fromFpNodeId);
		connHandler.setInteger(2, toFpNodeId);

		connHandler.remove();

		return;
	}

	public final void modifyRelationShip(FacilityRelationship relationship)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.modifyFPRelationShip", false);

		connHandler.setInteger(1, relationship.getFromNodeId());
		connHandler.setInteger(2, relationship.getToNodeId());
		connHandler.setFloat(3, relationship.getFlowFactor());
		connHandler.setInteger(4, relationship.getFromNodeId());
		connHandler.setInteger(5, relationship.getToNodeId());

		connHandler.update();
	}

	public final EuEmission createEuEmission(EuEmission euEmission)
			throws DAOException {
		EuEmission ret = euEmission;

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.createEuEmission", false);

		int i=1;
		connHandler.setString(i++, euEmission.getPollutantCd());
		connHandler.setInteger(i++, euEmission.getEmuId());
		connHandler.setString(i++, euEmission.getComment());
		connHandler.setBigDecimal(i++, euEmission.getPotentialEmissionsVal());
		connHandler.setBigDecimal(i++, euEmission.getAllowableEmissionsVal());
		connHandler.setString(i++, euEmission.getPotentialEmissionsUnit());
		connHandler.setString(i++, euEmission.getAllowableEmissionsUnit());
		connHandler.setBigDecimal(i++, euEmission.getAllowableEmissionsLbsHour());
		connHandler.setBigDecimal(i++, euEmission.getAllowableEmissionsTonsYear());
		connHandler.setBigDecimal(i++, euEmission.getPotentialEmissionsLbsHour());
		connHandler.setBigDecimal(i++, euEmission.getPotentialEmissionsTonsYear());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see FacilityDAO#addCountyNotification(int fpId, String countyId)
	 */
	public final void addCountyNotification(int fpId, String countyId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.addCountyNotification", false);

		connHandler.setInteger(1, fpId);
		connHandler.setString(2, countyId);

		connHandler.update();

		return;
	}

	/**
	 * @see FacilityDAO#addFacilityRole(int fpId, String roleCd, int userId)
	 */
	public final void addFacilityRole(String facilityId, String roleCd,
			int userId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.addFacilityRole", false);

		connHandler.setString(1, facilityId);
		connHandler.setString(2, roleCd);
		connHandler.setInteger(3, userId);

		connHandler.update();

		return;
	}

	/**
	 * @see FacilityDAO#addFacilityNote(int fpId, String facilityId, int noteId)
	 */
	public final void addFacilityNote(int fpId, String facilityId, int noteId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.addFacilityNote", false);

		connHandler.setInteger(1, fpId);
		connHandler.setInteger(2, noteId);
		connHandler.setString(3, facilityId);

		connHandler.update();

		return;
	}

	/**
	 * @see FacilityDAO#addFacilityNAICS(int fpId, String naicsCd)
	 */
	public final void addFacilityNAICS(int fpId, String naicsCd)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.addFacilityNAICS", false);

		connHandler.setInteger(1, fpId);
		connHandler.setString(2, naicsCd);

		connHandler.update();

		return;
	}

	/**
	 * @see FacilityDAO#addFacilitySIC(int fpId, String sicCd)
	 */
	public final void addFacilitySIC(int fpId, String sicCd)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.addFacilitySIC", false);

		connHandler.setInteger(1, fpId);
		connHandler.setString(2, sicCd);

		connHandler.update();

		return;
	}

	/**
	 * @see FacilityDAO#addFacilityMACTSubpart(int fpId, String mactCd)
	 */
	public final void addFacilityMACTSubpart(int fpId, String mactCd)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.addFacilityMACTSubpart", false);

		connHandler.setInteger(1, fpId);
		connHandler.setString(2, mactCd);

		connHandler.update();

		return;
	}

	/**
	 * @see FacilityDAO#addFacilityNeshapsSubpart(PollutantCompCode)
	 */
	public final void addFacilityNeshapsSubpart(PollutantCompCode subpart)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.addFacilityNeshapsSubpart", false);

		connHandler.setInteger(1, subpart.getFpId());
		connHandler.setString(2, subpart.getPollutantCd());
		connHandler.setString(3, subpart.getPollutantCompCd());

		connHandler.update();

		return;
	}

	/**
	 * @see FacilityDAO#addFacilityNSPSSubpart(int fpId, String nspsCd)
	 */
	public final void addFacilityNSPSSubpart(int fpId, String nspsCd)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.addFacilityNSPSSubpart", false);

		connHandler.setInteger(1, fpId);
		connHandler.setString(2, nspsCd);

		connHandler.update();

		return;
	}

	/**
	 * @see FacilityDAO#addFacilityNSPSPollutant(PollutantCompCode pollutant)
	 */
	public final void addFacilityNSPSPollutant(PollutantCompCode pollutant)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.addFacilityNSPSPollutant", false);

		connHandler.setInteger(1, pollutant.getFpId());
		connHandler.setString(2, pollutant.getPollutantCd());
		connHandler.setString(3, pollutant.getPollutantCompCd());

		connHandler.update();

		return;
	}

	/**
	 * @see FacilityDAO#addFacilityPSDPollutant(PollutantCompCode pollutant)
	 */
	public final void addFacilityPSDPollutant(PollutantCompCode pollutant)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.addFacilityPSDPollutant", false);

		connHandler.setInteger(1, pollutant.getFpId());
		connHandler.setString(2, pollutant.getPollutantCd());
		connHandler.setString(3, pollutant.getPollutantCompCd());

		connHandler.update();

		return;
	}

	/**
	 * @see FacilityDAO#addFacilityNSRPollutant(PollutantCompCode pollutant)
	 */
	public final void addFacilityNSRPollutant(PollutantCompCode pollutant)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.addFacilityNSRPollutant", false);

		connHandler.setInteger(1, pollutant.getFpId());
		connHandler.setString(2, pollutant.getPollutantCd());
		connHandler.setString(3, pollutant.getPollutantCompCd());

		connHandler.update();

		return;
	}

	/**
	 * @see FacilityDAO#removeCountyNotifications(int fpId)
	 */
	public final void removeCountyNotifications(int fpId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeCountyNotifications", false);

		connHandler.setInteger(1, fpId);

		connHandler.remove();

		return;
	}

	/**
	 * @see FacilityDAO#removeFacilityNAICSs(int fpId)
	 */
	public final void removeFacilityNAICSs(int fpId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeFacilityNAICSs", false);

		connHandler.setInteger(1, fpId);

		connHandler.remove();

		return;
	}

	/**
	 * @see FacilityDAO#removeFacilitySICs(int fpId)
	 */
	public final void removeFacilitySICs(int fpId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeFacilitySICs", false);

		connHandler.setInteger(1, fpId);

		connHandler.remove();

		return;
	}

	/**
	 * @see FacilityDAO#removeFacilityMACTSubparts(int fpId)
	 */
	public final void removeFacilityMACTSubparts(int fpId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeFacilityMACTSubparts", false);

		connHandler.setInteger(1, fpId);

		connHandler.remove();

		return;
	}

	/**
	 * @see FacilityDAO#removeFacilityNeshapsSubparts(int fpId)
	 */
	public final void removeFacilityNeshapsSubparts(int fpId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeFacilityNeshapsSubparts", false);

		connHandler.setInteger(1, fpId);

		connHandler.remove();

		return;
	}

	/**
	 * @see FacilityDAO#removeFacilityNSPSSubparts(int fpId)
	 */
	public final void removeFacilityNSPSSubparts(int fpId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeFacilityNSPSSubparts", false);

		connHandler.setInteger(1, fpId);

		connHandler.remove();

		return;
	}

	public Facility retrieveCommonCompleteFacilityProfile(Facility facility)
			throws DAOException {
		logger.trace("DLTRACE --> retrieveCommonCompleteFacilityProfile");
		Facility ret = facility;

		ret.setSicCds(retrieveFacilitySICCodes(ret.getFpId()));
		ret.setNaicsCds(retrieveFacilityNAICSCodes(ret.getFpId()));
		//retrieve Hydrocarbon Analysis Section Data
		List<HydrocarbonAnalysisPollutant> hcAnalysisPollutantList = retrieveFacExtendedHydrocarbonAnalysisPollutant(ret.getFpId());
		ret.setHydrocarbonPollutantList(hcAnalysisPollutantList);
		ret.setDecaneProperties(retrieveDecaneProperties(ret.getFpId()));
		
		if (FacilityTypeDef.hasHCAnalysis(ret.getFacilityTypeCd())){
			if (!ret.isCopyOnChange()){//not preserved
				ret.synchronizeHydrocarbonPollutantList();
			} 
			//if preserved, do not synchronize
			ret.calculateFacHCAnalysisTotal();//calculate Total and insert the total row
		}

		ret.setHydrocarbonAnalysisSampleDetail(retrieveFacHydrocarbonAnalysisSampleDetail(ret.getFpId()));

		// for creating facility in Gateway
		ret.setAddresses(retrieveFacilityAddresses(ret.getFpId()));
		ret.setApis(retrieveFacilityApis(ret.getFpId()));
		ret.setAllContacts(retrieveFacilityContacts(ret.getFacilityId()));

		return ret;
	}

	private Facility retrieveGateWayCompleteFacilityProfile(Facility facility)
			throws DAOException {
		Facility ret = retrieveCommonCompleteFacilityProfile(facility);
//		FacilityDAO readOnlyFacilityDAO = (FacilityDAO) DAOFactory.getDAO(
//				"FacilityDAO", "ReadOnly");
		//TODO read-only facility dao
		FacilityDAO readOnlyFacilityDAO = 
				(FacilityDAO)App.getApplicationContext().getBean("readOnlyFacilityDAO");
		return readOnlyFacilityDAO.retrieveReadOnlyGateWayFacility(ret);
//		return retrieveReadOnlyGateWayFacility(ret);
	}

	private Facility retrieveCompleteFacilityProfile(Facility facility)
			throws DAOException {
		logger.trace("DLTRACE --> retrieveCompleteFacilityProfile");
		Facility ret = retrieveCommonCompleteFacilityProfile(facility);

		MonitorGroup mg = new MonitorGroup(retrieveFacilityMonitorGroup(ret.getFacilityId()));
		ret.setAssociatedMonitorGroup(mg);
		ret.setMactSubparts(retrieveFacilityMACTCodes(ret.getFpId()));
		ret.setNspsSubparts(retrieveFacilityNSPSCodes(ret.getFpId()));
		ret.setNeshapsSubpartsCompCds(retrieveFacilityNESHAPSCodes(ret
				.getFpId()));

		// Get EU Emissions
		for (EmissionUnit tempEU : ret.getEmissionUnits().toArray(
				new EmissionUnit[0])) {
			retrieveEuEmissions(tempEU);
		}

		ret.setNotificationCounties(retrieveFacilityNotificationCounties(ret
				.getFpId()));

		FacilityNote[] notes = retrieveFacilityNotes(ret.getFacilityId());
		ArrayList<FacilityNote> hashNotes = new ArrayList<FacilityNote>();

		for (FacilityNote note : notes) {
			hashNotes.add(note);
		}

		ret.setNotes(hashNotes);

		FacilityRole[] tempRoles = retrieveFacilityRoles(ret.getFacilityId());
		HashMap<String, FacilityRole> roles = new HashMap<String, FacilityRole>();

		for (FacilityRole tempRole : tempRoles) {
			roles.put(tempRole.getFacilityRoleCd(), tempRole);
		}

		ret.setFacilityRoles(roles);

		// APIs
		ret.setApis(retrieveFacilityApis(ret.getFpId()));

		ret.setOwner(retrieveFacilityOwner(ret.getFacilityId()));
		
		// Continuous Monitors
		ret.setFacilityCemComLimitList(retrieveFacilityCemComLimitListByFpId(ret.getFpId()));

		ret.setContinuousMonitorList(retrieveFacilityContinuousMonitorList(ret.getFpId()));

		return ret;
	}

	public final ArrayList<Address> retrieveFacilityAddresses(int fpId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilityAddresses", true);

		connHandler.setInteger(1, fpId);

		ArrayList<Address> ret = connHandler.retrieveArray(Address.class);

		return ret;
	}

	public final EventLog createEventLog(EventLog el) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.createEventLog", false);
		el.setEventLogId(nextSequenceVal("S_Event_Log_Id"));

		connHandler.setInteger(1, el.getEventLogId());
		connHandler.setString(2, el.getEventTypeDefCd());
		connHandler.setInteger(3, el.getFpId());
		connHandler.setString(4, el.getFacilityId());
		connHandler.setTimestamp(5, el.getDate());
		connHandler.setInteger(6, el.getUserId());
		connHandler.setString(7, el.getNote());

		connHandler.update();

		el.setLastModified(1);

		return el;
	}

	public final EventLog[] createEventLogs(EventLog[] els) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.createEventLog", false);
		EventLog[] ret = els;
		try {
			for (EventLog el : els) {
				el.setEventLogId(nextSequenceVal("S_Event_Log_Id"));

				connHandler.setInteger(1, el.getEventLogId());
				connHandler.setString(2, el.getEventTypeDefCd());
				connHandler.setInteger(3, el.getFpId());
				connHandler.setString(4, el.getFacilityId());
				connHandler.setTimestamp(5, el.getDate());
				connHandler.setInteger(6, el.getUserId());
				connHandler.setString(7, el.getNote());

				connHandler.updateNoClose();

				el.setLastModified(1);
			}
		} finally {
			connHandler.close();
		}

		return ret;
	}

	public final EventLog[] retrieveEventLogs(EventLog el) throws DAOException {
		StringBuffer query = new StringBuffer(
				loadSQL("FacilitySQL.retrieveEventLogs"));
				
		String facilityId = el.getFacilityId();
		if (!Utility.isNullOrEmpty(el.getFacilityId())) {
			String format = "F%06d";

			int tempId;
			try {
				tempId = Integer.parseInt(facilityId);
				facilityId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}

		if (el.getFpId() != null) {
			query.append(" AND f.FP_ID = ");
			query.append(el.getFpId());
		}
		
		if (facilityId != null && facilityId.trim().length() > 0) {
			query.append(" AND LOWER(f.FACILITY_ID) LIKE ");
			query.append("LOWER('");
			query.append(SQLizeString(facilityId.trim().replace("*", "%")));
			query.append("')");
		}

		if (el.getFacilityName() != null
				&& el.getFacilityName().trim().length() > 0) {
			query.append(" AND LOWER(f.FACILITY_NM) LIKE ");
			query.append("LOWER('");
			query.append(SQLizeString(el.getFacilityName().trim()
					.replace("*", "%")));
			query.append("')");
		}
		
		if (el.getUserId() != null) {
			query.append(" AND el.USER_ID = ");
			query.append(el.getUserId());
		}
		
		if (!Utility.isNullOrEmpty(el.getNote())) {
			query.append(" AND LOWER(el.EVENT_NOTE) LIKE LOWER('");
			query.append(SQLizeString(el.getNote().trim().replace("*", "%")));
			query.append("')");
		}
		if (el.getEventTypeDefCd() != null) {
			query.append(" AND el.EVENT_TYPE_CD = '");
			query.append(SQLizeString(el.getEventTypeDefCd()));
			query.append("'");
		}
		boolean setTime = false;
		boolean setTimeTwo = false;
		if (el.getDate() != null || el.getDateTo() != null) {
			query.append(" AND ");
			query.append("(");
			if (el.getDate() != null) {
				setTime = true;
				query.append(" el.EVENT_DATE >= ?");
				if (el.getDateTo() != null) {
					query.append(" AND ");
				}
			}

			if (el.getDateTo() != null) {
				setTimeTwo = true;
				query.append(" el.EVENT_DATE <= ?");
			}
			query.append(" ) ");
		}

		StringBuffer sortBy = new StringBuffer(" ORDER BY el.EVENT_DATE DESC");
		query.append(sortBy.toString());

		ConnectionHandler connHandler = new ConnectionHandler(true);
		logger.debug("sql = " + query.toString());
		connHandler.setSQLStringRaw(query.toString());

		int index = 1;
		if (setTime) {
			connHandler.setTimestamp(index, formatBeginOfDay(el.getDate()));
			index++;
		}
		if (setTimeTwo) {
			connHandler.setTimestamp(index, formatEndOfDay(el.getDateTo()));
			index++;
		}

		ArrayList<EventLog> ret = connHandler.retrieveArray(EventLog.class);
		return ret.toArray(new EventLog[0]);
	}

	@Override
	public final Map<String, ArrayList<DataDetail>> retrieveCeDataDetails()
			throws DAOException {
		Map<String, ArrayList<DataDetail>> dataDetails = new HashMap<String, ArrayList<DataDetail>>();
		DataDetail dataDetail = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveCEDataDetails"));

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				do {
					Integer dataDetailId = AbstractDAO.getInteger(rs,
							"data_detail_id");
					String ceType = rs.getString("equipment_type_cd");
					
					if (dataDetailId != null) {
						try {
							dataDetail = readOnlyDetailDataDAO
									.retrieveDataDetail(dataDetailId);

							ArrayList<DataDetail> ceDDs = dataDetails
									.get(ceType);
							if (ceDDs == null) {
								ceDDs = new ArrayList<DataDetail>();
								ceDDs.add(dataDetail);
								dataDetails.put(ceType, ceDDs);
							} else {
								ceDDs.add(dataDetail);
								dataDetails.put(ceType, ceDDs);
							}
						} catch (DAOException de) {
							handleException(de, conn);
						}
					}
				} while (rs.next());
			}
			rs.close();
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return dataDetails;
	}
	
	public final DataDetail[] retrieveContEquipDataDetail(String contEquipType)
			throws DAOException {
		ArrayList<DataDetail> dataDetails = new ArrayList<DataDetail>();
		DataDetail dataDetail = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		if (contEquipType != null) {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(loadSQL("FacilitySQL.retrieveContEquipDataDetail"));

				pStmt.setString(1, contEquipType);

				ResultSet rs = pStmt.executeQuery();
//				DetailDataDAO detailDataDAO = (DetailDataDAO) DAOFactory
//						.getDAO("DetailDataDAO", "ReadOnly");
				//TODO read-only detailDataDAO
				if (rs.next()) {
					do {
						Integer dataDetailId = AbstractDAO.getInteger(rs,
								"data_detail_id");

						if (dataDetailId != null) {
							try {
								dataDetail = readOnlyDetailDataDAO
										.retrieveDataDetail(dataDetailId);
								dataDetails.add(dataDetail);
							} catch (DAOException de) {
								handleException(de, conn);
							}
						}
					} while (rs.next());
				}
				rs.close();
			} catch (Exception e) {
				handleException(e, conn);
			} finally {
				closeStatement(pStmt);
				handleClosing(conn);
			}
		}

		return dataDetails.toArray(new DataDetail[0]);
	}

	public final void addControlEquipmentData(Integer fpNodeId,
			DataDetail dataDetail) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.addControlEquipmentData", false);

		connHandler.setInteger(1, fpNodeId);
		connHandler.setInteger(2, dataDetail.getDataDetailId());
		if (dataDetail.getDataDetailVal() == null) {
			dataDetail.setDataDetailVal(" ");
		}
		connHandler.setString(3, dataDetail.getDataDetailVal());

		connHandler.update();
	}

	public final void removeControlEquipmentData(int fpNodeId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeControlEquipmentData", false);

		connHandler.setInteger(1, fpNodeId);

		connHandler.remove();

		return;
	}

	public final void removeEuEmissions(int emuId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeEuEmissions", false);

		connHandler.setInteger(1, emuId);

		connHandler.remove();

		return;
	}

	public final PollutantsControlled createPollutantsControlled(
			PollutantsControlled pollutant) throws DAOException {
		PollutantsControlled ret = pollutant;

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.createCePollsControlled", false);

		connHandler.setString(1, pollutant.getPollutantCd());
		connHandler.setInteger(2, pollutant.getFpNodeId());
		connHandler.setString(3, pollutant.getDesignContEff());
		connHandler.setString(4, pollutant.getOperContEff());
		connHandler.setString(5, pollutant.getCaptureEff());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	public final void removePollutantsControlled(int fpNodeId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeCePollsControlled", false);

		connHandler.setInteger(1, fpNodeId);

		connHandler.remove();

		return;
	}

	/*
	 * Increase version number all versions of a facility where version number
	 * is greater than a given version.
	 */
	public final void increaseVersionIds(String facilityId, Integer versionId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.increaseVersionIds", false);
		connHandler.setString(1, facilityId);
		connHandler.setInteger(2, versionId);

		connHandler.update();

		return;

	}

	public final MultiEstabFacilityList[] retrieveMultiiEstabFacilities(
			String federalSCSCId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveMutliEstabFacilities", true);

		connHandler.setInteger(1, -1);
		connHandler.setString(2, federalSCSCId);

		ArrayList<MultiEstabFacilityList> ret = connHandler
				.retrieveArray(MultiEstabFacilityList.class);

		return ret.toArray(new MultiEstabFacilityList[0]);
	}

	/**
	 * @see FacilityDAO#addFacilityAddress(int fpId, int addressId)
	 */
	public final void addFacilityAddress(int fpId, int addressId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.addFacilityAddress", false);

		connHandler.setInteger(1, fpId);
		connHandler.setInteger(2, addressId);

		connHandler.update();

		return;
	}

	public int findCompanyId(String companyName) throws DAOException {

		int company_id = retrieve(companyName);

		return company_id;
	}

	public void addFacilityCompany(String facilityId, Timestamp startDate,
			int companyId, int lastmodified) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.addFacilityToCompany", false);
		connHandler.setString(1, facilityId);
		connHandler.setTimestamp(2, startDate);
		connHandler.setInteger(3, companyId);
		connHandler.setInteger(4, lastmodified);
		connHandler.update();

		return;
	}

	/**
	 * @see FacilityDAO#nextfacilityIdSeqNum()
	 */
	public final Integer nextfacilityIdSeqNum() throws DAOException {

		String sequence = "S_FACID";
		Integer sequenceNum = nextSequenceVal(sequence);

		return sequenceNum;
	}

    /**
     * @see FacilityDAO#nextfacilityIdSeqNum(FacilityIdRef facilityIdRef)
     */
    public final Integer nextfacilityIdSeqNum(FacilityIdRef facilityIdRef)
            throws DAOException {

        String sequence = "S_FACID_" + facilityIdRef.getDolaId()
                + facilityIdRef.getCountyCd() + facilityIdRef.getCityCd();
        Integer sequenceNum = nextSequenceVal(sequence);

        return sequenceNum;
    }
    
	/**
	 * @see InfrastructureDAO#createNote(Note newNote)
	 */
	public final FacilityRUM createFacilityRUM(FacilityRUM newRum)
			throws DAOException {
		checkNull(newRum);

		FacilityRUM ret = newRum;

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.createRUM", false);

		Integer id = nextSequenceVal("S_Rum_Id");

		connHandler.setInteger(1, id);
		connHandler.setString(2, newRum.getReasonCd());
		connHandler.setString(3, newRum.getUndeliverableAddress());
		connHandler.setInteger(4, newRum.getUserId());
		connHandler.setString(5, newRum.getFacilityId());
		connHandler.setTimestamp(6, newRum.getOriginalMailDt());
		connHandler.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
		connHandler.setString(8, newRum.getCategoryCd());
		connHandler.setString(9, newRum.getDisposition());
		connHandler.setString(10, newRum.getDapcNote());
		connHandler.setInteger(11, 1);
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setRumId(id);
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see InfrastructureDAO#changeFacilityFacilityId(String currentFacilityId,
	 *      String newFacilityId)
	 */
	public void changeFacilityId(String tableName, String currentFacilityId,
			String newFacilityId) throws DAOException {
		checkNull(tableName);
		checkNull(currentFacilityId);
		checkNull(newFacilityId);

		ConnectionHandler connHandler = new ConnectionHandler(false);
		connHandler
				.setSQLStringRaw("UPDATE "
						+ addSchemaToTable(tableName)
						+ " SET facility_id = ?, last_modified = last_modified + 1 WHERE facility_id = ?");

		connHandler.setString(1, newFacilityId);
		connHandler.setString(2, currentFacilityId);

		connHandler.updateNoCheck();
	}

	public void changeDolaCd(String facilityId, String dolaCd)
			throws DAOException {
		checkNull(facilityId);
		checkNull(dolaCd);

		ConnectionHandler connHandler = new ConnectionHandler(false);
		connHandler.setSQLStringRaw("UPDATE " + addSchemaToTable("FP_FACILITY")
				+ " SET DO_LAA_CD = ?, last_modified = last_modified + 1"
				+ " WHERE DO_LAA_CD != ? AND facility_id = ? ");

		connHandler.setString(1, dolaCd);
		connHandler.setString(2, dolaCd);
		connHandler.setString(3, facilityId);

		connHandler.updateNoCheck();
	}

	/**
	 * yehp
	 * 
	 * @param dueDateString1
	 * @param dueDateString2
	 * @param shutDownDateFrom
	 * @param shutDownDateTo
	 * 
	 * @see FacilityDAO#retrieveTvCertOverdue(Integer year, String
	 *      dueDateString1, String dueDateString2, Timestamp shutDownDateFrom,
	 *      Timestamp shutDownDateTo)
	 */
	public final Facility[] retrieveTvCertOverdue(Integer year,
			String dueDateString1, String dueDateString2,
			Timestamp shutDownDateFrom, Timestamp shutDownDateTo)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.tvCertOverdue", true);

		connHandler.setTimestamp(1, shutDownDateTo); // PERMIT EFFECTIVE DATE <=
		connHandler.setTimestamp(2, shutDownDateTo); // PERMIT EXPIRATION DATE
														// >=
		connHandler.setTimestamp(3, shutDownDateTo); // RENEWAL
														// LAST_SUBMITTED_DATE
														// <=
		connHandler.setTimestamp(4, shutDownDateTo); // RENEWAL PERMIT
														// EXPIRATION DATE >=
		connHandler.setInteger(5, year); // TVCC_REPORTING_YEAR
		connHandler.setTimestamp(6, shutDownDateFrom); // LAST_SHUTDOWN_DATE >=
		connHandler.setTimestamp(7, shutDownDateTo); // LAST_SHUTDOWN_DATE <=
		connHandler.setString(8, dueDateString1); // TV_CERT_REPORT_DUE_DATE
		connHandler.setString(9, dueDateString2); // or TV_CERT_REPORT_DUE_DATE

		return connHandler.retrieveArray(Facility.class).toArray(
				new Facility[0]);
	}

	/**
	 * @param year
	 * @param dueDateString
	 * @param shutDownDateFrom
	 * @param shutDownDateTo
	 * @return
	 * @throws DAOException
	 * @see FacilityDAO#retrieveTvCertOverdue(Integer year, String
	 *      dueDateString, Timestamp shutDownDateFrom, Timestamp shutDownDateTo)
	 */
	public final FacilityList[] retrieveTvCertLate(Integer reportYear,
			String facilityId, String facilityName, String operatingStatusCd,
			String dueDate, Timestamp shutDownDateFrom, Timestamp shutDownDateTo)
			throws DAOException {

		String statementSQL = loadSQL("FacilitySQL.tvCertLate");
		StringBuffer whereClause = new StringBuffer();

		if (facilityName != null && facilityName.trim().length() > 0) {
			whereClause.append(" AND LOWER(facility_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(facilityName.replace("*", "%")));
			whereClause.append("')");
		}
		if (facilityId != null && facilityId.trim().length() > 0) {
			whereClause.append(" AND LOWER(ff.facility_id) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(facilityId.replace("*", "%")));
			whereClause.append("')");
		}
		if (operatingStatusCd != null) {
			whereClause.append(" AND ff.operating_status_cd = '");
			whereClause.append(operatingStatusCd);
			whereClause.append("'");
		}

		statementSQL += whereClause.toString();

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(statementSQL);

		connHandler.setTimestamp(1, shutDownDateTo); // PERMIT EFFECTIVE DATE <=
		connHandler.setTimestamp(2, shutDownDateTo); // PERMIT EXPIRATION DATE
														// >=
		connHandler.setTimestamp(3, shutDownDateTo); // RENEWAL
														// LAST_SUBMITTED_DATE
														// <=
		connHandler.setTimestamp(4, shutDownDateTo); // RENEWAL PERMIT
														// EXPIRATION DATE >=
		connHandler.setInteger(5, reportYear); // TVCC_REPORTING_YEAR
		connHandler.setTimestamp(6, shutDownDateFrom); // LAST_SHUTDOWN_DATE >=
		connHandler.setTimestamp(7, shutDownDateTo); // LAST_SHUTDOWN_DATE <=
		connHandler.setString(8, dueDate); // TV_CERT_REPORT_DUE_DATE

		return connHandler.retrieveArray(FacilityList.class).toArray(
				new FacilityList[0]);
	}

	/**
	 * @param year
	 * @param dueDateString
	 * @param shutDownDateFrom
	 * @param shutDownDateTo
	 * @return
	 * @throws DAOException
	 * @see FacilityDAO#retrieveTvCertReminder(Integer year, String
	 *      dueDateString, Timestamp effectiveDate, Timestamp expirationDate)
	 */
	public final FacilityList[] retrieveTvCertReminder(Integer reportYear,
			String facilityId, String facilityName, String dueDate,
			Timestamp effectiveDate, Timestamp expirationDate)
			throws DAOException {
		String statementSQL = loadSQL("FacilitySQL.tvCertReminder");
		StringBuffer whereClause = new StringBuffer();

		if (facilityName != null && facilityName.trim().length() > 0) {
			whereClause.append(" AND LOWER(facility_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(facilityName.replace("*", "%")));
			whereClause.append("')");
		}
		if (facilityId != null && facilityId.trim().length() > 0) {
			whereClause.append(" AND LOWER(ff.facility_id) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(facilityId.replace("*", "%")));
			whereClause.append("')");
		}

		statementSQL += whereClause.toString();

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(statementSQL);

		connHandler.setTimestamp(1, effectiveDate); // LAST_SHUTDOWN_DATE >=

		return connHandler.retrieveArray(FacilityList.class).toArray(
				new FacilityList[0]);
	}

	/**
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	public EmissionsRptInfo[] retrieveFERNoticeViolation(Integer year)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFERNoticeViolation", true);

		connHandler.setInteger(1, year);

		ArrayList<EmissionsRptInfo> ret = connHandler
				.retrieveArray(EmissionsRptInfo.class);

		return ret.toArray(new EmissionsRptInfo[0]);
	}

	public FacilityExport[] retrieveFacilityExports() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.facilityExport", true);

		ArrayList<FacilityExport> ret = connHandler
				.retrieveArray(FacilityExport.class);

		return ret.toArray(new FacilityExport[0]);
	}

	/**
	 * @see InfrastructureDAO#searchSubmissionLog(SubmissionLog)
	 */
	public final SubmissionLog[] searchSubmissionLog(SubmissionLog searchObj,
			Timestamp beginDate, Timestamp endDate) throws DAOException {
		StringBuffer statementSQL = new StringBuffer(
				loadSQL("FacilitySQL.retrieveSubmissionLogs"));

		String facilityId = searchObj.getFacilityId();
		if (!Utility.isNullOrEmpty(facilityId)) {
			String format = "F%06d";

			int tempId;
			try {
				tempId = Integer.parseInt(facilityId);
				facilityId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}

		if (searchObj.getFacilityId() != null
				&& searchObj.getFacilityId().trim().length() > 0) {
			statementSQL.append(" AND LOWER(fsub.facility_id) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(facilityId.replace("*", "%")));
			statementSQL.append("')");
		}
		if (searchObj.getGatewayUserName() != null
				&& searchObj.getGatewayUserName().trim().length() > 0) {
			statementSQL.append(" AND LOWER(fsub.gateway_user_nm) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(searchObj.getGatewayUserName()
					.replace("*", "%")));
			statementSQL.append("')");
		}
		if (searchObj.getGatewaySubmissionId() != null
				&& searchObj.getGatewaySubmissionId().trim().length() > 0) {
			statementSQL.append(" AND LOWER(fsub.gateway_submission_id) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(searchObj.getGatewaySubmissionId()
					.replace("*", "%")));
			statementSQL.append("')");
		}
		if (searchObj.getSubmissionType() != null
				&& searchObj.getSubmissionType().trim().length() > 0) {
			statementSQL.append(" AND LOWER(fsub.submission_type) = ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(searchObj.getSubmissionType()));
			statementSQL.append("')");
		}
		if (beginDate != null) {
			statementSQL.append(" AND fsub.submission_dt >= ?");
		}
		if (endDate != null) {
			statementSQL.append(" AND fsub.submission_dt <= ?");
		}

		statementSQL.append(" ORDER BY fsub.submission_dt DESC");

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(statementSQL.toString());

		int i = 1;

		if (beginDate != null) {
			connHandler.setTimestamp(i++, formatBeginOfDay(beginDate));
		}

		if (endDate != null) {
			connHandler.setTimestamp(i++, formatEndOfDay(endDate));
		}

		ArrayList<SubmissionLog> ret = connHandler
				.retrieveArray(SubmissionLog.class);

		return ret.toArray(new SubmissionLog[0]);
	}

	public void removeEmissionUnit(Integer emuId) throws DAOException {
		checkNull(emuId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeEmissionUnit", false);

		connHandler.setInteger(1, emuId);

		connHandler.remove();
	}

	public EmissionUnit[] retrieveEmissionUnitsFromPastProfiles(
			int corrEpaEmuId, String facilityId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveEmissionUnitsFromPastProfiles", true);

		connHandler.setInteger(1, corrEpaEmuId);
		connHandler.setString(2, facilityId);

		ArrayList<EmissionUnit> ret = connHandler
				.retrieveArray(EmissionUnit.class);

		return ret.toArray(new EmissionUnit[0]);
	}

	@Override
	public EmissionUnit retrieveEmissionUnitFromCurrentProfile(int corrEpaEmuId, String facilityId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.retrieveEmissionUnitFromCurrentProfile",
				true);

		connHandler.setInteger(1, corrEpaEmuId);
		connHandler.setString(2, facilityId);

		EmissionUnit ret = (EmissionUnit) connHandler.retrieve(EmissionUnit.class);

		return ret;
	}

	// remove All Emission Unit, Emission Process, Control Equipment, and
	// Release Point in a facility
	public void removeFacilitySubObject(Integer fpId) throws DAOException {
		checkNull(fpId);
		ConnectionHandler connHandlerTEST = new ConnectionHandler(
				"FacilitySQL.removeFacilitySubObject", false);
		connHandlerTEST.setString(1, fpId);
		connHandlerTEST.remove();

	}

	@Override
	public void removeFacilityOwner(FacilityOwner owner) throws DAOException {
		checkNull(owner);
		ConnectionHandler connHandler = null;

		if (owner.getEndDate() != null) {
			connHandler = new ConnectionHandler(
					"FacilitySQL.removeFacilityOwner", false);
		} else {
			connHandler = new ConnectionHandler(
					"FacilitySQL.removeCurrentFacilityOwner", false);
		}

		connHandler.setInteger(1, owner.getCompany().getCompanyId());
		connHandler.setString(2, owner.getFacilityId());
		connHandler.setTimestamp(3, owner.getStartDate());

		if (owner.getEndDate() != null) {
			connHandler.setTimestamp(4, owner.getEndDate());
		}

		connHandler.remove();

	}

	@Override
	public void addFacilityOwner(FacilityOwner owner) throws DAOException {
		checkNull(owner);

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.addFacilityOwner", false);
		int lastModified = 1;
		connHandler.setInteger(1, owner.getCompany().getCompanyId());
		connHandler.setString(2, owner.getFacilityId());
		connHandler.setTimestamp(3, owner.getStartDate());
		connHandler.setTimestamp(4, owner.getEndDate());
		connHandler.setInteger(5, lastModified);
		connHandler.update();

		return;
	}

	@Override
	public final List<FacilityOwner> retrieveFacilityOwners(String facilityId)
			throws DAOException {
		List<FacilityOwner> ret = new ArrayList<FacilityOwner>();
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveFacilityOwners"));

			pStmt.setString(1, facilityId);

			ResultSet rs = pStmt.executeQuery();

			while (rs.next()) {
				Company tempCompany = new Company();
				tempCompany.populate(rs);

				FacilityOwner owner = new FacilityOwner();
				owner.populate(rs);
				owner.setCompany(tempCompany);

				ret.add(owner);
			}
			rs.close();

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	@Override
	public final FacilityOwner retrieveFacilityOwner(String facilityId)
			throws DAOException {
		logger.trace("DLTRACE --> retrieveFacilityOwner");

		FacilityOwner ret = new FacilityOwner();
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveFacilityOwner"));

			pStmt.setString(1, facilityId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				Company tempCompany = new Company();
				tempCompany.populate(rs);

				FacilityOwner owner = new FacilityOwner();
				owner.populate(rs);
				owner.setCompany(tempCompany);

				ret = owner;
			}
			rs.close();

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	@Override
	public Integer retrieveEmissionUnitIdByWiseId(String wiseViewId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveReleatedEmissionUnitByWiseId", false);

		connHandler.setString(1, wiseViewId);

		return (Integer) connHandler.retrieveJavaObject(Integer.class);
	}

	@Override
	public final Integer retrieveProcessIdByWiseId(String wiseViewId)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveProcessIdByWiseId", false);

		connHandler.setString(1, wiseViewId);

		return (Integer) connHandler.retrieveJavaObject(Integer.class);
	}

	@Override
	public final List<Integer> retrieveControlEquipIdsByProcessWiseId(
			String epWiseViewId) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveControlEquipIdsByProcessWiseId", false);

		connHandler.setString(1, epWiseViewId);

		return connHandler.retrieveJavaObjectArray(Integer.class);
	}

	@Override
	public final Integer retrieveFpIdByFacilityId(String facilityId)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFpIdByFacilityId", false);

		connHandler.setString(1, facilityId);

		return (Integer) connHandler.retrieveJavaObject(Integer.class);
	}

	@Override
	public List<EmissionUnitReplacement> retrieveEmissionUnitReplacements(
			Integer emuId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveEmissionUnitReplacement", false);

		connHandler.setInteger(1, emuId);

		List<EmissionUnitReplacement> emissionUnitReplacements = connHandler
				.retrieveArray(EmissionUnitReplacement.class);

		return emissionUnitReplacements;
	}

	@Override
	public EmissionUnitReplacement createEmissionUnitReplacement(
			EmissionUnitReplacement emissionUnitReplacement)
			throws DAOException {
		EmissionUnitReplacement ret = emissionUnitReplacement;

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.createEmissionUnitReplacement", false);

		Integer replacementId = nextSequenceVal("S_Replacement_Id");

		int index = 1;
		connHandler.setInteger(index++, replacementId);
		connHandler.setInteger(index++, emissionUnitReplacement.getEmuId());
		connHandler.setString(index++,
				emissionUnitReplacement.getManufacturerName());
		connHandler.setString(index++,
				emissionUnitReplacement.getSerialNumber());
		connHandler.setString(index++,
				emissionUnitReplacement.getSerialNumberEffectiveDate());
		connHandler
				.setTimestamp(index++, emissionUnitReplacement.getInstallDate());
		connHandler.setTimestamp(index++,
				emissionUnitReplacement.getOperationStartupDate());
		connHandler.setTimestamp(index++, emissionUnitReplacement.getOrderDate());
		connHandler.setTimestamp(index++, emissionUnitReplacement.getManufactureDate());
		connHandler.setTimestamp(index++, emissionUnitReplacement.getShutdownDate());
		connHandler
				.setTimestamp(index++, emissionUnitReplacement.getRemovalDate());
		
		connHandler.update();

		ret.setReplacementId(replacementId);
		ret.setLastModified(1);

		return ret;
	}

	@Override
	public void removeEmissionUnitReplacements(Integer EmuId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeEmissionUnitReplacements", false);

		connHandler.setInteger(1, EmuId);

		connHandler.remove();
	}

	@Override
	public List<FixmeCompany> retrieveFixmeCompanies() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFixmeCompanies", false);
		List<FixmeCompany> fixmeCompanies = connHandler
				.retrieveArray(FixmeCompany.class);
		return fixmeCompanies;
	}

	@Override
	public boolean removeFixmeCompanies() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeFixmeCompanies", false);
		return connHandler.updateNoCheck();
	}
	
	 /**
     * @see FacilityDAO#retrieveFacilityIdRefs(String countyCd)
     */
    public final FacilityIdRef[] retrieveFacilityIdRefs(String countyCd)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "FacilitySQL.retrieveFacilityIdRefs", true);

        connHandler.setString(1, countyCd);

        ArrayList<FacilityIdRef> ret = connHandler
                .retrieveArray(FacilityIdRef.class);

        return ret.toArray(new FacilityIdRef[0]);
    }
    
	public final EmissionUnit retrieveEmissionUnitByCorrEpaEmuId(Integer fpId, Integer corrEpaEmuId)
			throws DAOException {
		EmissionUnit ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveEmissionUnitByCorrEpaEmuId"));

			pStmt.setInt(1, fpId);
			pStmt.setInt(2, corrEpaEmuId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new EmissionUnit();

				ret.populate(rs);
			}
			rs.close();

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	@Override
	public GeoPolygonDef[] retrieveGeoPolygonDefs()
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveGeoPolygonDefs", true);

		ArrayList<GeoPolygonDef> ret = 
				connHandler.retrieveArray(GeoPolygonDef.class);

		return ret.toArray(new GeoPolygonDef[0]);
	}
	
	public FacilityRequest createFacilityRequest(FacilityRequest newFacilityRequest)
            throws DAOException {
        checkNull(newFacilityRequest);
        FacilityRequest ret = newFacilityRequest;
        ConnectionHandler connHandler = new ConnectionHandler(
                "FacilitySQL.createFacilityRequest", false);
        Integer id = nextSequenceVal("S_FACILITY_REQUEST",newFacilityRequest.getRequestId());
        logger.debug("id = " + id);
        int i=1;
        connHandler.setInteger(i++, id);
        connHandler.setInteger(i++, newFacilityRequest.getCurrentUserId());
        connHandler.setInteger(i++, newFacilityRequest.getCompanyId());
        connHandler.setString(i++, newFacilityRequest.getName());
        connHandler.setString(i++, newFacilityRequest.getDesc());
        connHandler.setString(i++, newFacilityRequest.getFacilityTypeCd());
        connHandler.setString(i++, newFacilityRequest.getOperatingStatusCd());
        connHandler.setString(i++, newFacilityRequest.getMemo());
        connHandler.setTimestamp(i++, newFacilityRequest.getSubmitDate());
        connHandler.setInteger(i++, newFacilityRequest.getPhyAddr().getAddressId());
        
        connHandler.update();

        // If we get here the INSERT must have succeeded, so set the important
        // data and return the object.
        ret.setRequestId(id);
        ret.setLastModified(1);
        ret.setReqId(formatId("FCR", id.toString()));
        return ret;
    }
	
	@Override
	public final FacilityRequestList[] searchFacilityRequests(String facilityName,
			String requestId, String companyName,
			String countyCd, String operatingStatusCd, String doLaaCd,
			String firstName, String lastName, String externalUsername,
			String address1, String cntId, String phone, String email,
			String requestStateCd, boolean unlimitedResults,
			String facilityTypeCd,
			Integer companyId) throws DAOException {

		String statementSQL;

		if (unlimitedResults) {
			setDefaultSearchLimit(-1);
		}
		
		if (!Utility.isNullOrEmpty(requestId)) {
			String format = "FCR%06d";
			int tempId;
			try {
				tempId = Integer.parseInt(requestId.trim());
				requestId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}

		statementSQL = loadSQL("FacilitySQL.findFacilityRequests");

		StringBuffer whereClause = new StringBuffer("");

		if (facilityName != null && facilityName.trim().length() > 0) {
			whereClause.append(" AND LOWER(ff.facility_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(facilityName.trim().replace("*",
					"%")));
			whereClause.append("')");
		}
		if (requestId != null && requestId.trim().length() > 0) {
			whereClause.append(" AND LOWER(ff.req_id) LIKE ");
			whereClause.append("LOWER('");
			whereClause
					.append(SQLizeString(requestId.trim().replace("*", "%")));
			whereClause.append("')");
		}

		
		if (companyName != null) {
			whereClause.append(" AND ccm.cmp_id = '");
			whereClause.append(companyName);
			whereClause.append("'");
		}
		if (operatingStatusCd != null) {
			whereClause.append(" AND ff.operating_status_cd = '");
			whereClause.append(operatingStatusCd);
			whereClause.append("'");
		}
		
		if (countyCd != null) {
			whereClause.append(" AND ca.county_cd = '");
			whereClause.append(countyCd);
			whereClause.append("'");
		}
		if (doLaaCd != null) {
			whereClause.append(" AND ca.do_laa_cd = '");
			whereClause.append(doLaaCd);
			whereClause.append("'");
		}
		
		if (firstName != null
				&& firstName.trim().length() > 0) {
			whereClause.append(" AND LOWER(cc.first_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(firstName.trim()
					.replace("*", "%")));
			whereClause.append("')");
		}
		if (lastName != null
				&& lastName.trim().length() > 0) {
			whereClause.append(" AND LOWER(cc.last_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(lastName.trim()
					.replace("*", "%")));
			whereClause.append("')");
		}
		
		if (externalUsername != null
				&& externalUsername.trim().length() > 0) {
			whereClause.append(" AND LOWER(cc.envite_username) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(externalUsername.trim()
					.replace("*", "%")));
			whereClause.append("')");
		}

		if (address1 != null && address1.trim().length() > 0) {
			whereClause.append(" AND LOWER(ca.address1) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(address1.trim().replace("*", "%")));
			whereClause.append("')");
		}
		
		if (cntId != null && cntId.trim().length() > 0) {
			whereClause.append(" AND LOWER(ff.cnt_id) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(cntId.trim().replace("*", "%")));
			whereClause.append("')");
		}
		
		if (phone != null && phone.trim().length() > 0) {
			StringBuffer phoneNum = new StringBuffer(12);
			for (int i = 0; i < phone.length(); i++) {
				String c = phone.substring(i, i + 1);
				if ("%*0123456789".contains(c)) {
					phoneNum.append(c);
				}
			}
			if (phoneNum.length() > 0) {
				whereClause.append(" AND cc.phone_no like '");
				whereClause.append(SQLizeString(phoneNum.toString().replace(
						"*", "%")));
				whereClause.append("'");
			}
		}
		
		if (email != null
				&& email.trim().length() > 0) {
			whereClause.append(" AND LOWER(cc.email_address_txt) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(email.trim()
					.replace("*", "%")));
			whereClause.append("')");
		}
		
		if (requestStateCd != null) {
			whereClause.append(" AND ff.request_status_cd = '");
			whereClause.append(requestStateCd);
			whereClause.append("'");
		}
		
		if (facilityTypeCd != null) {
			whereClause.append(" AND ff.facility_type_cd = '");
			whereClause.append(facilityTypeCd);
			whereClause.append("'");
		}
		
		if (companyId != null) {
			whereClause.append(" AND ccm.company_id = ");
			whereClause.append(companyId);
		}


		StringBuffer sortBy = new StringBuffer(" ORDER BY ff.submit_dt DESC");

		statementSQL += whereClause.toString() + " " + sortBy.toString();

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(statementSQL);
		
		// logger.debug("statementSQL = " + statementSQL);

		ArrayList<FacilityRequestList> ret = connHandler.retrieveArray(
				FacilityRequestList.class, defaultSearchLimit);

		return ret.toArray(new FacilityRequestList[0]);
	}
	
	public final FacilityRequest retrieveFacilityRequest(Integer requestId)
			throws DAOException {
		FacilityRequest ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		if (requestId != null) {
		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("FacilitySQL.retrieveFacilityRequest"));

			pStmt.setInt(1, requestId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new FacilityRequest();
				
				ret.populate(rs);
			}
			rs.close();
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}
		}

		return ret;
	}
	
	/**
	 * @see FacilityDAO#modifyFacility(Facility facility)
	 */
	public final boolean modifyFacilityRequest(FacilityRequest facilityRequest) throws DAOException {
		boolean ret = false;

		ConnectionHandler connHandler;

		connHandler = new ConnectionHandler("FacilitySQL.modifyFacilityRequest", false);

		int i = 1;
		connHandler.setString(i++, facilityRequest.getName());
		connHandler.setString(i++, facilityRequest.getDesc());
		connHandler.setString(i++, facilityRequest.getOperatingStatusCd());
		connHandler.setString(i++, facilityRequest.getFacilityTypeCd());
		connHandler.setString(i++, facilityRequest.getMemo());
		connHandler.setTimestamp(i++, facilityRequest.getSubmitDate());
		connHandler.setString(i++, facilityRequest.getRequestStatusCd());
		connHandler.setInteger(i++, facilityRequest.getLastModified() + 1);
		
		connHandler.setInteger(i++, facilityRequest.getRequestId());
		connHandler.setInteger(i++, facilityRequest.getLastModified());
	
		connHandler.update();

		ret = true;
		return ret;
	}

	public void removeFacilityRequest(int requestId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeFacilityRequest", false);
		connHandler.setInteger(1, requestId);
		connHandler.remove();
	}
	
	public final Integer retrieveFacilityRequestCount(Integer companyId)
			throws DAOException {
		Integer ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		if (companyId != null) {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(loadSQL("FacilitySQL.retrieveFacilityRequestCount"));

				pStmt.setInt(1, companyId);

				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret = new Integer(AbstractDAO.getInteger(rs, "request_cnt"));
				}
				rs.close();
			} catch (Exception e) {
				handleException(e, conn);
			} finally {
				closeStatement(pStmt);
				handleClosing(conn);
			}
		}

		return ret;
	}
	
	public final FacilityRole[] retrieveFacilityRolesByFacilities(FacilityList[] facilities, Integer userId)
			throws DAOException {

		int remainder = facilities.length;
		int startIndex = 0;
		while (remainder > 0) {
			
			int batchSize = (remainder < 2048 ? remainder : 2048); //calculateBatchSize(remainder);
			remainder = remainder - batchSize;
			
			ConnectionHandler connHandler0 = new ConnectionHandler(
					"FacilitySQL.insertFacilityIdsByUser", true);
			
			int endIndex = startIndex + batchSize;
			for (int i = startIndex; i < endIndex; i++) {
				connHandler0.setString(1, facilities[i].getFacilityId());
				connHandler0.setString(2, userId);
				connHandler0.addBatch();
				startIndex++;
			}
			connHandler0.executeBatchUpdate();
			logger.debug("batchSize: " + batchSize + "; total: " + facilities.length + "; processed: " + startIndex + "; remainder: " + remainder);
		}
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilityRolesByFacilities", true);
		connHandler.setInteger(1,userId);
		ArrayList<FacilityRole> ret = connHandler.retrieveArray(FacilityRole.class);

		FacilityRole[] facRoles = ret.toArray(new FacilityRole[0]);
		
		ConnectionHandler connHandler2 = new ConnectionHandler(
				"FacilitySQL.deleteFacilityIdsByUser", true);
		connHandler2.setInteger(1,userId);
		connHandler2.remove();
				
		return facRoles;
	}

	@Override
	public void removeFacilityRoles(Set<String> facilityIds) throws DAOException {
		Iterator<String> facilityIdsIter = facilityIds.iterator();
		int remainder = facilityIds.size();
		int startIndex = 0;
		while (remainder > 0) {
			int batchSize = calculateBatchSize(remainder);
			remainder = remainder - batchSize;
			
			ConnectionHandler connHandler = new ConnectionHandler(
					"FacilitySQL.removeFacilityRoles", false);
			
			int endIndex = startIndex + batchSize;
			for (int i = startIndex; i < endIndex; i++) {
				connHandler.setString(1, facilityIdsIter.next());
				connHandler.addBatch();
				startIndex++;
			}
			connHandler.executeBatchRemove();
			logger.debug("batchSize: " + batchSize + "; total: " + facilityIds.size() + "; processed: " + startIndex + "; remainder: " + remainder);
		}
	}

	@Override
	public void createFacilityRoles(List<FacilityRole> facilityRoles) throws DAOException {
		int remainder = facilityRoles.size();
		int startIndex = 0;
		while (remainder > 0) {

			int batchSize = calculateBatchSize(remainder);
			remainder = remainder - batchSize;

			ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.createFacilityRole", false);

			int endIndex = startIndex + batchSize;
			for (int i = startIndex; i < endIndex; i++) {
				FacilityRole role = facilityRoles.get(i);
				if (role.getUserId() == null) {
					continue;
				}
				connHandler.setString(1, role.getFacilityId());
				connHandler.setString(2, role.getFacilityRoleCd());
				connHandler.setInteger(3, role.getUserId());

				role.setLastModified(1);
				connHandler.addBatch();
				startIndex++;
			}
			connHandler.executeBatchUpdate();
			logger.debug("batchSize: " + batchSize + "; total: " + facilityRoles.size() + "; processed: " + startIndex
					+ "; remainder: " + remainder);
		}
	}

	@Override
	public void removeFacilityRoles(List<FacilityRole> facilityRoles, String facilityRoleCd, int currentUserId)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.removeFacilityRolesByFacilityByRole", false);

		connHandler.setInteger(1, currentUserId);
		connHandler.setString(2, facilityRoleCd);
		connHandler.remove();

		ConnectionHandler connHandler2 = new ConnectionHandler("FacilitySQL.deleteFacilityIdsByUser", true);
		connHandler2.setInteger(1, currentUserId);
		connHandler2.remove();

	}

	@Override
	public FacilityRole[] retrieveFacilityRolesByFacilitiesByRole(FacilityList[] facilities, String facilityRole,
			int userId) throws DAOException {

		int remainder = facilities.length;
		int startIndex = 0;
		while (remainder > 0) {

			int batchSize = calculateBatchSize(remainder);
			remainder = remainder - batchSize;

			ConnectionHandler connHandler0 = new ConnectionHandler("FacilitySQL.insertFacilityIdsByUser", true);

			int endIndex = startIndex + batchSize;
			for (int i = startIndex; i < endIndex; i++) {
				connHandler0.setString(1, facilities[i].getFacilityId());
				connHandler0.setString(2, userId);
				connHandler0.addBatch();
				startIndex++;
			}
			connHandler0.executeBatchUpdate();
			logger.debug("batchSize: " + batchSize + "; total: " + facilities.length + "; processed: " + startIndex
					+ "; remainder: " + remainder);
		}

		ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.retrieveFacilityRolesByFacilitiesByRole",
				true);
		connHandler.setInteger(1, userId);
		connHandler.setString(2, facilityRole);
		ArrayList<FacilityRole> ret = connHandler.retrieveArray(FacilityRole.class);

		FacilityRole[] facRoles = ret.toArray(new FacilityRole[0]);

		return facRoles;
	}

	@Override
	public void updateFacilityDistrict(Integer fpId, Address address)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.updateFacilityDistrict", false);

		connHandler.setString(1, address.getDistrictCd());
		connHandler.setInteger(2, fpId);
		connHandler.setInteger(3, address.getAddressId());
		connHandler.setInteger(4, fpId);

		try {
			connHandler.update();	
		} catch (DataStoreConcurrencyException e) {
			// ignore.  this means the district was not updated because the
			// address was not the oldest for the facility.
		}
	}
	
	public MonitorGroup retrieveFacilityMonitorGroup(String facilityId)
			throws DAOException {

		StringBuffer statementSQL = new StringBuffer(
				loadSQL("MonitoringSQL.searchMonitorGroups"));

		if (!Utility.isNullOrEmpty(facilityId)) {
			String format = "F%06d";

			int tempId;
			try {
				tempId = Integer.parseInt(facilityId);
				facilityId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}

		if (!Utility.isNullOrEmpty(facilityId)) {
			statementSQL.append(" AND LOWER(f.facility_id) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(facilityId.replace("*", "%")));
			statementSQL.append("')");
		}

		statementSQL.append(" ORDER BY mgrp.group_id");

		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(statementSQL.toString());

		ArrayList<MonitorGroup> ret = connHandler
				.retrieveArray(MonitorGroup.class);

		MonitorGroup[] mg;
		if (ret != null && ret.size() > 0) {
			mg = ret.toArray(new MonitorGroup[0]);
			return mg[0];
		} else {
			return new MonitorGroup();
		}
	}

	@Override
	public AppPermitSearchResult[] appPermitSearch(String searchCmpId,
			String searchFacilityId, Integer searchType, Integer appPermitType) throws DAOException {
		
		ArrayList<AppPermitSearchResult> ret = new ArrayList<AppPermitSearchResult>();
		ArrayList<AppPermitSearchResult> permitResults = new ArrayList<AppPermitSearchResult>();
		ArrayList<AppPermitSearchResult> appResults = new ArrayList<AppPermitSearchResult>();
		
		if (AppPermitSearchResult.SEARCH_TYPE_PERMITS == searchType ||
				AppPermitSearchResult.SEARCH_TYPE_BOTH == searchType) {

			String permitSearchSql = null;
			if (AppPermitSearchResult.APP_PERMIT_TYPE_NSR == appPermitType) {
				permitSearchSql = "FacilitySQL.nsrPermitSearch";
			} else if (AppPermitSearchResult.APP_PERMIT_TYPE_TV == appPermitType) {
				permitSearchSql = "FacilitySQL.tvPermitSearch";
			} else {
				throw new DAOException("appPermitType not supported: " + appPermitType);
			}
			StringBuffer permitStatementSQL = 
					new StringBuffer(loadSQL(permitSearchSql));
	
			if (!Utility.isNullOrEmpty(searchCmpId)) {
				permitStatementSQL.append(" AND lower(c.cmp_id) = ");
				permitStatementSQL.append(" lower('" + searchCmpId + "')");
			}
	
			if (!Utility.isNullOrEmpty(searchFacilityId)) {
				permitStatementSQL.append(" AND lower(f.facility_id) = ");
				permitStatementSQL.append(" lower('" + searchFacilityId + "')");
			}
	
			if (AppPermitSearchResult.APP_PERMIT_TYPE_NSR == appPermitType) {
				StringBuffer permitStatementSQL2 = new StringBuffer(
						loadSQL("FacilitySQL.nsrPermitSearch2"));
				permitStatementSQL.append(permitStatementSQL2);
			}
			
			permitStatementSQL.append(" ORDER BY p.permit_id desc");
			
	
			ConnectionHandler permitConnHandler = new ConnectionHandler(true);
			permitConnHandler.setSQLStringRaw(permitStatementSQL.toString());
	
			permitResults = permitConnHandler.retrieveArray(AppPermitSearchResult.class);
			ret.addAll(permitResults);
		}
		
		
		if (AppPermitSearchResult.SEARCH_TYPE_APPLICATIONS == searchType ||
				AppPermitSearchResult.SEARCH_TYPE_BOTH == searchType) {

			String appSearchSql = null;
			if (AppPermitSearchResult.APP_PERMIT_TYPE_NSR == appPermitType) {
				appSearchSql = "FacilitySQL.nsrAppSearch";
			} else if (AppPermitSearchResult.APP_PERMIT_TYPE_TV == appPermitType) {
				appSearchSql = "FacilitySQL.tvAppSearch";
			} else {
				throw new DAOException("appPermitType not supported: " + appPermitType);
			}
			StringBuffer appStatementSQL = new StringBuffer(
					loadSQL(appSearchSql));
	
			if (!Utility.isNullOrEmpty(searchCmpId)) {
				appStatementSQL.append(" AND lower(c.cmp_id) = ");
				appStatementSQL.append(" lower('" + searchCmpId + "')");
			}
	
			if (!Utility.isNullOrEmpty(searchFacilityId)) {
				appStatementSQL.append(" AND lower(f.facility_id) = ");
				appStatementSQL.append(" lower('" + searchFacilityId + "')");
			}
	
			if (AppPermitSearchResult.APP_PERMIT_TYPE_NSR == appPermitType) {
				StringBuffer appStatementSQL2 = new StringBuffer(
						loadSQL("FacilitySQL.nsrAppSearch2"));
				appStatementSQL.append(appStatementSQL2);
			}
			
			appStatementSQL.append(" ORDER BY pa.application_id desc");
			
	
			ConnectionHandler appConnHandler = new ConnectionHandler(true);
			appConnHandler.setSQLStringRaw(appStatementSQL.toString());
	
			appResults = appConnHandler.retrieveArray(AppPermitSearchResult.class);
			ret.addAll(appResults);
		}

		// combine permits and apps
		return ret.toArray(new AppPermitSearchResult[0]);
	}
	
	// Facility CEM/COM/CMS Limits
	
	public final List<FacilityCemComLimit> retrieveFacilityCemComLimitListByFpId(
			Integer fpId) throws DAOException {

		checkNull(fpId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilityCemComLimitListByFpId", true);

		connHandler.setInteger(1, fpId);

		ArrayList<FacilityCemComLimit> ret = new ArrayList<FacilityCemComLimit>();
		ArrayList<FacilityCemComLimit> base = connHandler
				.retrieveArray(FacilityCemComLimit.class);
		for (BaseDBObject bd : base) {
			ret.add((FacilityCemComLimit) bd);
		}

		return ret;
	}
	
	public final List<FacilityCemComLimit> retrieveFacilityCemComLimitListByMonitorId(
			Integer monitorId) throws DAOException {

		checkNull(monitorId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilityCemComLimitListByMonitorId", true);

		connHandler.setInteger(1, monitorId);

		ArrayList<FacilityCemComLimit> ret = new ArrayList<FacilityCemComLimit>();
		ArrayList<FacilityCemComLimit> base = connHandler
				.retrieveArray(FacilityCemComLimit.class);
		for (BaseDBObject bd : base) {
			ret.add((FacilityCemComLimit) bd);
		}

		return ret;
	}

	public final void removeFacilityCemComLimitList(int monitorId)
			throws DAOException {

		checkNull(monitorId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeFacilityCemComLimitList", false);

		connHandler.setInteger(1, monitorId);
		connHandler.remove();
	}

	public final FacilityCemComLimit createFacilityCemComLimit(
			FacilityCemComLimit fccl) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.createFacilityCemComLimit", false);

		checkNull(fccl);
		int i = 1;
		
		int id = nextSequenceVal("S_Facility_Cem_Com_Limit_Id", fccl.getLimitId());
		
		if(null == fccl.getCorrLimitId()) {
			fccl.setCorrLimitId(nextSequenceVal("S_Cem_Com_Limit_Corr_Lim_Id"));
		}
		
		if(Utility.isNullOrEmpty(fccl.getLimId())) {
			fccl.setLimId(retrieveNextLimId(fccl.getMonitorId()));
		}
		
		connHandler.setInteger(i++, id);
		connHandler.setInteger(i++, fccl.getMonitorId());
		connHandler.setString(i++, fccl.getLimitDesc());
		connHandler.setString(i++, fccl.getLimitSource());
		connHandler.setTimestamp(i++, fccl.getStartDate());
		connHandler.setTimestamp(i++, fccl.getEndDate());
		connHandler.setString(i++, fccl.getAddlInfo());
		connHandler.setInteger(i++, fccl.getAddedBy());
		connHandler.setString(i++, fccl.getLimId());
		connHandler.setInteger(i++, fccl.getCorrLimitId());

		connHandler.update();

		fccl.setLimitId(id);
		fccl.setLastModified(1);

		return fccl;
	}

	public final void modifyFacilityCemComLimit(FacilityCemComLimit fccl)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.modifyFacilityCemComLimit", false);

		checkNull(fccl);
		int i = 1;
		connHandler.setInteger(i++, fccl.getLimitId());
		connHandler.setInteger(i++, fccl.getMonitorId());
		connHandler.setString(i++, fccl.getLimitDesc());
		connHandler.setString(i++, fccl.getLimitSource());
		connHandler.setTimestamp(i++, fccl.getStartDate());
		connHandler.setTimestamp(i++, fccl.getEndDate());
		connHandler.setString(i++, fccl.getAddlInfo());
		connHandler.setInteger(i++, fccl.getAddedBy());
		connHandler.setString(i++, fccl.getLimId());
		connHandler.setInteger(i++, fccl.getCorrLimitId());

		connHandler.setInteger(i++, fccl.getLastModified() + 1);

		connHandler.setInteger(i++, fccl.getLimitId());
		connHandler.setInteger(i++, fccl.getLastModified());

		connHandler.update();
	}

	public final void removeFacilityCemComLimit(FacilityCemComLimit fccl)
			throws DAOException {

		checkNull(fccl);

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.removeFacilityCemComLimit", false);

		int i = 1;
		connHandler.setInteger(i++, fccl.getLimitId());

		connHandler.remove();
	}
	
	public final List<ContinuousMonitor> retrieveFacilityContinuousMonitorList(
			Integer fpId) throws DAOException {

		checkNull(fpId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ContinuousMonitorSQL.retrieveContinuousMonitorByFpId", true);

		connHandler.setInteger(1, fpId);

		ArrayList<ContinuousMonitor> ret = new ArrayList<ContinuousMonitor>();
		ArrayList<ContinuousMonitor> base = connHandler
				.retrieveArray(ContinuousMonitor.class);
		for (BaseDBObject bd : base) {
			ret.add((ContinuousMonitor) bd);
		}

		return ret;
	}
	
	@Override
	public String retrieveNextLimId(Integer continuousMonitorId) throws DAOException {
		String ret = null;
		
		checkNull(continuousMonitorId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveNextLimId", true);
		
		connHandler.setInteger(1, continuousMonitorId);
		
		Integer limId = (Integer)connHandler.retrieveJavaObject(Integer.class);
		
		if(null != limId) {
			ret = "LIM" + String.format("%03d", limId);
		}
		
		return ret;
		
	}
	
	
	@Override
	public FacilityCemComLimit retrieveFacilityCemComLimitByMonitorIdAndCorrId(Integer monitorId, 
			Integer corrLimitId) throws DAOException {
		checkNull(monitorId);
		checkNull(corrLimitId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilityCemComLimitByMonitorIdAndCorrId", true);
		
		connHandler.setInteger(1, monitorId);
		connHandler.setInteger(2, corrLimitId);
		
		return (FacilityCemComLimit)connHandler.retrieve(FacilityCemComLimit.class);
		
	}
	
	@Override
	public Integer retrieveCemsComplianceReportCountWithMonitor(
			Integer corrMonitorId) throws DAOException {
		return retrieveCemsComplianceReportCountWithMonitor(corrMonitorId,
				new StringBuffer());
	}

	public final Integer retrieveCemsComplianceReportCountWithMonitor(
			Integer corrMonitorId, StringBuffer whereClause)
			throws DAOException {
		Integer ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			String sqlStatement = loadSQL("FacilitySQL.retrieveCemsComplianceReportCountWithMonitor");

			if (null != corrMonitorId) {
				whereClause
						.append(" AND cm.CORR_MONITOR_ID = " + corrMonitorId);
			}

			if (whereClause.length() > 0) {
				sqlStatement += whereClause;
			}

			conn = getReadOnlyConnection();
			pStmt = conn.prepareStatement(sqlStatement);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new Integer(AbstractDAO.getInteger(rs, "report_cnt"));
			}
			rs.close();
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	@Override
	public Integer retrieveCemsComplianceReportCountWithLimit(
			Integer corrLimitId) throws DAOException {
		return retrieveCemsComplianceReportCountWithLimit(corrLimitId,
				new StringBuffer());
	}

	public final Integer retrieveCemsComplianceReportCountWithLimit(
			Integer corrLimitId, StringBuffer whereClause) throws DAOException {
		Integer ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			String sqlStatement = loadSQL("FacilitySQL.retrieveCemsComplianceReportCountWithLimit");

			if (null != corrLimitId) {
				whereClause.append(" AND lim.CORR_LIMIT_ID = " + corrLimitId);
			}

			if (whereClause.length() > 0) {
				sqlStatement += whereClause;
			}

			conn = getReadOnlyConnection();
			pStmt = conn.prepareStatement(sqlStatement);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new Integer(AbstractDAO.getInteger(rs, "report_cnt"));
			}
			rs.close();
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}
	
	@Override
	public List<String> retrieveFacilitiesWithMatchingApiNumber(String apiNo,
			String facilityId) throws DAOException {
		
		checkNull(apiNo);
		checkNull(facilityId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacilitiesWithMatchingApiNumber", true);
		
		int i = 1;
		connHandler.setString(i++, apiNo);
		connHandler.setString(i++, facilityId);
		
		return connHandler.retrieveJavaObjectArray(String.class);
	}
	
	@Override
	public final List<Integer> retrieveAllControlEquipIdsByEmuId(
			Integer emuId) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveAllControlEquipIdsByEmuId", false);

		connHandler.setInteger(1, emuId);

		return connHandler.retrieveJavaObjectArray(Integer.class);
	}
	
	public List<PollutantsControlled> retrievePollutantsControlled(Integer ceFpNodeId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveCePollsControlled", true);

		connHandler.setInteger(1, ceFpNodeId);
		ArrayList<PollutantsControlled> ret = connHandler
				.retrieveArray(PollutantsControlled.class);
		
		return ret;

	}
	public final Integer retrieveAddressIdForFacilityOnLastDOP(String facilityId, String lastDayOfPeriod)
			throws DAOException {
		Integer ret = null;

		ConnectionHandler connHandler = new ConnectionHandler(false);

		connHandler.setSQLString("FacilitySQL.retrieveAddressIdForFacilityOnLastDOP");
		int i = 1;
		connHandler.setString(i++, facilityId);
		connHandler.setString(i++, lastDayOfPeriod);
		connHandler.setString(i++, lastDayOfPeriod);
		connHandler.setString(i++, lastDayOfPeriod);

		ret = (Integer) connHandler.retrieveJavaObject(Integer.class);

		return ret;
	}
	
	@Override
	public ArrayList<EiDataImportFacilityInformation> retrieveEiDataImportFacilityInformation() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(false);
		
		connHandler.setSQLString("FacilitySQL.retrieveEiDataImportFacilityInformation");
		ArrayList<EiDataImportFacilityInformation> rs = connHandler.retrieveArray(EiDataImportFacilityInformation.class);
		
		return rs;
	}
	
	@Override
	public List<FceContinuousMonitorLineItem> searchFacilityCemComLimitsByDate(String facId, Timestamp startDate,
			Timestamp endDate) throws DAOException {

		checkNull(facId);
		checkNull(startDate);
		
		ArrayList<FceContinuousMonitorLineItem> ret = new ArrayList<FceContinuousMonitorLineItem>();
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL = null;
		
        statementSQL = new StringBuffer(loadSQL("FacilitySQL.searchFacilityCemComLimitsByDate"));
        StringBuffer sortBy = new StringBuffer(" ORDER BY fcm.mon_id, fccl.lim_id");
        statementSQL.append(sortBy.toString());
        
        connHandler.setSQLStringRaw(statementSQL.toString());
		
		int i = 1;
		connHandler.setString(i++, facId);
		connHandler.setTimestamp(i++, endDate);
		connHandler.setTimestamp(i++, startDate);
		connHandler.setTimestamp(i++, endDate);
		connHandler.setTimestamp(i++, startDate);
		
		ret = connHandler.retrieveArray(FceContinuousMonitorLineItem.class);

		return ret;
	}
	
	@Override
	public List<HydrocarbonAnalysisPollutant> retrieveFacExtendedHydrocarbonAnalysisPollutant(Integer fpId) throws DAOException{
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacExtendedHydrocarbonAnalysisPollutant", true);

		connHandler.setInteger(1, fpId);
		ArrayList<HydrocarbonAnalysisPollutant> ret = connHandler.retrieveArray(HydrocarbonAnalysisPollutant.class);
		return ret;
	}
	
	
	public List<HydrocarbonAnalysisPollutant> retrieveFacHCPollutantFromPollutantDef(Integer fpId) throws DAOException{
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacHCPollutantFromPollutantDef", true);

		connHandler.setInteger(1, fpId);
		ArrayList<HydrocarbonAnalysisPollutant> ret = connHandler.retrieveArray(HydrocarbonAnalysisPollutant.class);
		return ret;
		
	}
	
	@Override
	public void addFacExtendedHCAnalysisPollutant(HydrocarbonAnalysisPollutant hcPollutant) throws DAOException{
//		if (PollutantDef.TOTAL.equals(hcPollutant.getPollutantCd())){
//			return;
//		}

		checkNull(hcPollutant.getFpId());
		checkNull(hcPollutant.getPollutantCd());
//		checkNull(hcPollutant.getGas());
//		checkNull(hcPollutant.getOil());
//		checkNull(hcPollutant.getProducedWater());
		
		ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.addFacExtendedHCAnalysisPollutant", false);
		int i = 1;
		connHandler.setInteger(i++, hcPollutant.getFpId());
		connHandler.setString(i++, hcPollutant.getPollutantCd());
		connHandler.setBigDecimal(i++, hcPollutant.getGas());
		connHandler.setBigDecimal(i++, hcPollutant.getOil());
		connHandler.setBigDecimal(i++, hcPollutant.getProducedWater());
		connHandler.update();
	}
	
	@Override
	public void deleteFacExtendedHCAnalysisPollutant(Integer fpId) throws DAOException{
	    checkNull(fpId);
	    ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.deleteFacExtendedHCAnalysisPollutant", false);
		int i = 1;
		connHandler.setInteger(i++, fpId);
		connHandler.remove();
	}

	@Override
	public HydrocarbonAnalysisSampleDetail retrieveFacHydrocarbonAnalysisSampleDetail(Integer fpId) throws DAOException{
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.retrieveFacHydrocarbonAnalysisSampleDetail", true);

		connHandler.setInteger(1, fpId);
		HydrocarbonAnalysisSampleDetail ret = (HydrocarbonAnalysisSampleDetail)connHandler.retrieve(HydrocarbonAnalysisSampleDetail.class);
		return ret;
	}
	
	@Override
	public void addFacHydrocarbonAnalysisSampleDetail(HydrocarbonAnalysisSampleDetail hcSampleDetail) throws DAOException{
		checkNull(hcSampleDetail.getFpId());
		
		ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.addFacHydrocarbonAnalysisSampleDetail", false);
		int i = 1;
		connHandler.setInteger(i++, hcSampleDetail.getFpId());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityNameGas());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityNameOil());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityNameWater());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityAPIGas());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityAPIOil());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityAPIWater());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityProducingFieldGas());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityProducingFieldOil());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityProducingFieldWater());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityProducingFormationGas());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityProducingFormationOil());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityProducingFormationWater());
		connHandler.setTimestamp(i++, hcSampleDetail.getSampleDateGas());
		connHandler.setTimestamp(i++, hcSampleDetail.getSampleDateOil());
		connHandler.setTimestamp(i++, hcSampleDetail.getSampleDateWater());
		connHandler.setString(i++, hcSampleDetail.getSamplePointGas());
		connHandler.setString(i++, hcSampleDetail.getSamplePointOil());
		connHandler.setString(i++, hcSampleDetail.getSamplePointWater());
		connHandler.setString(i++, hcSampleDetail.getAnalysisCompanyNameGas());
		connHandler.setString(i++, hcSampleDetail.getAnalysisCompanyNameOil());
		connHandler.setString(i++, hcSampleDetail.getAnalysisCompanyNameWater());
		connHandler.setTimestamp(i++, hcSampleDetail.getAnalysisDateGas());
		connHandler.setTimestamp(i++, hcSampleDetail.getAnalysisDateOil());
		connHandler.setTimestamp(i++, hcSampleDetail.getAnalysisDateWater());
		
		connHandler.setString(i++, hcSampleDetail.getSamplePressureTxtGas());
		connHandler.setString(i++, hcSampleDetail.getSamplePressureTxtOil());
		connHandler.setString(i++, hcSampleDetail.getSamplePressureTxtWater());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSamplePressureGas());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSamplePressureOil());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSamplePressureWater());
		
		connHandler.setString(i++, hcSampleDetail.getSampleTempTxtGas());
		connHandler.setString(i++, hcSampleDetail.getSampleTempTxtOil());
		connHandler.setString(i++, hcSampleDetail.getSampleTempTxtWater());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSampleTempGas());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSampleTempOil());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSampleTempWater());
		
		connHandler.setString(i++, hcSampleDetail.getSampleFlowRateTxtGas());
		connHandler.setString(i++, hcSampleDetail.getSampleFlowRateTxtOil());
		connHandler.setString(i++, hcSampleDetail.getSampleFlowRateTxtWater());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSampleFlowRateGas());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSampleFlowRateOil());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSampleFlowRateWater());
		
		connHandler.update();
	}
	
	@Override
	public void deleteFacHydrocarbonAnalysisSampleDetail(Integer fpId) throws DAOException{
	    checkNull(fpId);
	    ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.deleteFacHydrocarbonAnalysisSampleDetail", false);
		int i = 1;
		connHandler.setInteger(i++, fpId);
		connHandler.remove();
	}

	
	@Override
	public boolean modifyFacHydrocarbonAnalysisSampleDetail(HydrocarbonAnalysisSampleDetail hcSampleDetail) throws DAOException {
		
		checkNull(hcSampleDetail);
		checkNull(hcSampleDetail.getFpId());

		ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.modifyFacHydrocarbonAnalysisSampleDetail", false);

		int i = 1;

		connHandler.setInteger(i++, hcSampleDetail.getFpId());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityNameGas());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityNameOil());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityNameWater());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityAPIGas());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityAPIOil());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityAPIWater());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityProducingFieldGas());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityProducingFieldOil());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityProducingFieldWater());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityProducingFormationGas());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityProducingFormationOil());
		connHandler.setString(i++, hcSampleDetail.getSampleFacilityProducingFormationWater());
		connHandler.setTimestamp(i++, hcSampleDetail.getSampleDateGas());
		connHandler.setTimestamp(i++, hcSampleDetail.getSampleDateOil());
		connHandler.setTimestamp(i++, hcSampleDetail.getSampleDateWater());
		connHandler.setString(i++, hcSampleDetail.getSamplePointGas());
		connHandler.setString(i++, hcSampleDetail.getSamplePointOil());
		connHandler.setString(i++, hcSampleDetail.getSamplePointWater());
		connHandler.setString(i++, hcSampleDetail.getAnalysisCompanyNameGas());
		connHandler.setString(i++, hcSampleDetail.getAnalysisCompanyNameOil());
		connHandler.setString(i++, hcSampleDetail.getAnalysisCompanyNameWater());
		connHandler.setTimestamp(i++, hcSampleDetail.getAnalysisDateGas());
		connHandler.setTimestamp(i++, hcSampleDetail.getAnalysisDateOil());
		connHandler.setTimestamp(i++, hcSampleDetail.getAnalysisDateWater());
		
		connHandler.setString(i++, hcSampleDetail.getSamplePressureTxtGas());
		connHandler.setString(i++, hcSampleDetail.getSamplePressureTxtOil());
		connHandler.setString(i++, hcSampleDetail.getSamplePressureTxtWater());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSamplePressureGas());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSamplePressureOil());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSamplePressureWater());
		
		connHandler.setString(i++, hcSampleDetail.getSampleTempTxtGas());
		connHandler.setString(i++, hcSampleDetail.getSampleTempTxtOil());
		connHandler.setString(i++, hcSampleDetail.getSampleTempTxtWater());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSampleTempGas());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSampleTempOil());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSampleTempWater());
		
		connHandler.setString(i++, hcSampleDetail.getSampleFlowRateTxtGas());
		connHandler.setString(i++, hcSampleDetail.getSampleFlowRateTxtOil());
		connHandler.setString(i++, hcSampleDetail.getSampleFlowRateTxtWater());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSampleFlowRateGas());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSampleFlowRateOil());
		connHandler.setBigDecimal(i++, hcSampleDetail.getSampleFlowRateWater());
		
		connHandler.setInteger(i++, hcSampleDetail.getLastModified() + 1);

		// where clause
		connHandler.setInteger(i++, hcSampleDetail.getFpId());
		connHandler.setInteger(i++, hcSampleDetail.getLastModified());

		return connHandler.update();
	}
	
	/**
	 * @see FacilityDAO#createDecaneProperties(DecaneProperties decaneProperty)
	 */
	@Override
	public DecaneProperties createDecaneProperties(DecaneProperties decaneProperties) throws DAOException {
		checkNull(decaneProperties);
		checkNull(decaneProperties.getFpId());

		DecaneProperties ret = decaneProperties;

		ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.createDecaneProperties", false);

		int i = 1;

		connHandler.setInteger(i++, decaneProperties.getFpId());
		connHandler.setString(i++, decaneProperties.getAvgMolecularWtOfOil());
		connHandler.setString(i++, decaneProperties.getAvgMolecularWtOfProducedWater());
		connHandler.setString(i++, decaneProperties.getSpecificGravityOfOil());
		connHandler.setString(i++, decaneProperties.getSpecificGravityOfProducedWater());

		connHandler.update();

		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see FacilityDAO#retrieveDecaneProperties(Integer fpId)
	 */
	@Override
	public DecaneProperties retrieveDecaneProperties(Integer fpId) throws DAOException {
		
		checkNull(fpId);

		ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.retrieveDecaneProperties", false);

		connHandler.setInteger(1, fpId);

		return (DecaneProperties) connHandler.retrieve(DecaneProperties.class);
	}

	/**
	 * @see FacilityDAO#modifyDecaneProperties(DecaneProperties decaneProperties)
	 */
	@Override
	public boolean modifyDecaneProperties(DecaneProperties decaneProperties) throws DAOException {
		
		checkNull(decaneProperties);
		checkNull(decaneProperties.getFpId());

		ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.modifyDecaneProperties", false);

		int i = 1;

		connHandler.setInteger(i++, decaneProperties.getFpId());
		connHandler.setString(i++, decaneProperties.getAvgMolecularWtOfOil());
		connHandler.setString(i++, decaneProperties.getAvgMolecularWtOfProducedWater());
		connHandler.setString(i++, decaneProperties.getSpecificGravityOfOil());
		connHandler.setString(i++, decaneProperties.getSpecificGravityOfProducedWater());
		connHandler.setInteger(i++, decaneProperties.getLastModified() + 1);

		// where clause
		connHandler.setInteger(i++, decaneProperties.getFpId());
		connHandler.setInteger(i++, decaneProperties.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see FacilityDAO#deleteDecaneProperty(Integer fpId)
	 */
	@Override
	public boolean deleteDecaneProperties(Integer fpId) throws DAOException {
		
		checkNull(fpId);

		ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.deleteDecaneProperties", false);

		connHandler.setInteger(1, fpId);

		return connHandler.remove();
	}
	
	public List<FacilityRoleActivity> retrieveFacilityUserRoleActivities(String facilityRoleCd) throws DAOException {
		checkNull(facilityRoleCd);

		ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.retrieveFacilityUserRoleActivities", true);
		connHandler.setString(1, facilityRoleCd);

		return (List<FacilityRoleActivity>) connHandler.retrieveArray(FacilityRoleActivity.class);
	}

	@Override
	public List<FacilityPurgeSearchLineItem> retrieveFacilityListForPurging(Timestamp purdgeDate) throws DAOException {
		checkNull(purdgeDate);

		ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.retrieveFacilityListForPurging", true);

		connHandler.setString(1, OperatingStatusDef.SD);
		connHandler.setTimestamp(2, purdgeDate);
		ArrayList<FacilityPurgeSearchLineItem> ret = connHandler.retrieveArray(FacilityPurgeSearchLineItem.class);
		return ret;
	}

	
	@Override
	public List<FacilityPurgeLog> retrieveFacilityPurgeLogs() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.retrieveFacilityPurgeLogs", true);

		ArrayList<FacilityPurgeLog> ret = connHandler.retrieveArray(FacilityPurgeLog.class);
		return ret;
	}
	
	@Override
	public FacilityPurgeLog createFacilityPurgeLog(FacilityPurgeLog facilityPurgeLog) throws DAOException {
		
		checkNull(facilityPurgeLog);
		checkNull(facilityPurgeLog.getFacilityId());
		checkNull(facilityPurgeLog.getFacilityName());
		checkNull(facilityPurgeLog.getCompanyId());
		checkNull(facilityPurgeLog.getCompanyName());
		checkNull(facilityPurgeLog.getShutdownDate());
		checkNull(facilityPurgeLog.getPurgedDate());
		checkNull(facilityPurgeLog.getUserId());
		
		ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.createFacilityPurgeLog", false);
		facilityPurgeLog.setPurgeLogId(nextSequenceVal("S_Facility_Purge_Log_Id"));

		int i = 1;
		connHandler.setInteger(i++, facilityPurgeLog.getPurgeLogId());
		connHandler.setString(i++, facilityPurgeLog.getFacilityId());
		connHandler.setString(i++, facilityPurgeLog.getFacilityName());
		connHandler.setString(i++, facilityPurgeLog.getCompanyId());
		connHandler.setString(i++, facilityPurgeLog.getCompanyName());
		connHandler.setTimestamp(i++, facilityPurgeLog.getShutdownDate());
		connHandler.setTimestamp(i++, facilityPurgeLog.getPurgedDate());
		connHandler.setInteger(i++, facilityPurgeLog.getUserId());
		connHandler.update();

		facilityPurgeLog.setLastModified(1);

		return facilityPurgeLog;
	}

	@Override
	public boolean  deleteFacilitySubmissionLogs(String facilityId) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.deleteSubmissionLogs", false);

		connHandler.setString(1, facilityId);

		return connHandler.remove();
	}

	@Override
	public boolean deleteFacilityNotes(String facilityId) throws DAOException {
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.deleteFacilityNotes", false);
		connHandler.setString(1, facilityId);
		return connHandler.remove();
	}

	@Override
	public boolean  deleteSiteVisitsByFpid(Integer fpId) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.deleteSiteVisitsByFpid", false);

		connHandler.setInteger(1, fpId);

		return connHandler.remove();
	}

	@Override
	public boolean deleteSiteVisitsXrefByFpid(Integer fpId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.deleteSiteVisitsXrefByFpid", false);

		connHandler.setInteger(1, fpId);

		return connHandler.remove();

	}

	@Override
	public boolean deleteSiteVisitNoteXrefByFpId(Integer fpId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.deleteSiteVisitsNotesXrefByFpid", false);

		connHandler.setInteger(1, fpId);

		return connHandler.remove();

	}

	@Override
	public boolean deleteSVAttachments(Integer fpId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.deleteSVAttachments", false);
		connHandler.setInteger(1, fpId);
		return connHandler.remove();

		
	}

	@Override
	public boolean deleteFacilityAttachments(String facilityId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.deleteFacilityAttachments", false);
		connHandler.setString(1, facilityId);
		return connHandler.remove();

	}

	@Override
	public boolean deleteCEEnforcementAttachments(String facilityId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.deleteCEEnforcementAttachments", false);
		connHandler.setString(1, facilityId);
		return connHandler.remove();

		
	}

	@Override
	public  void deleteFacilityEventLogs(Integer fpId) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.deleteEventLogs", false);

		connHandler.setInteger(1, fpId);

		connHandler.remove();
	}

	
	@Override
	public void deleteFacilityCompanyRelationship(String facilityId) throws DAOException {
		checkNull(facilityId);
		ConnectionHandler connHandler = null;
		connHandler = new ConnectionHandler(
					"FacilitySQL.deleteFacilityCompanyRelationship", false);

		connHandler.setString(1, facilityId);
		connHandler.remove();
		
	}

	@Override
	public final void deleteFacilityContactRelationship(String facilityId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"FacilitySQL.deleteFacilityContactRelationship", false);

		logger.debug("Removing contacts from facility: " + facilityId);

		connHandler.setString(1, facilityId);
		connHandler.remove();
	}
	
	@Override
	public void deleteFacilityRUM(String facilityId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler("FacilitySQL.deleteFacilityRUM", false);
		connHandler.setString(1, facilityId);
		connHandler.remove();

	}

}