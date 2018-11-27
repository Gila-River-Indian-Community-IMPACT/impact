package us.oh.state.epa.stars2.database.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceContinuousMonitorLineItem;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsRptInfo;
import us.oh.state.epa.stars2.database.dbObjects.facility.AppPermitSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipment;
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
import us.oh.state.epa.stars2.database.dbObjects.facilityRequest.FacilityRequest;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ApiGroup;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;
import us.oh.state.epa.stars2.def.GeoPolygonDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.database.dbObjects.report.EiDataImportFacilityInformation;
import us.wy.state.deq.impact.database.dbObjects.tool.FixmeCompany;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;

/**
 * @author Kbradley
 * 
 */
public interface FacilityDAO extends TransactableDAO {
	
	void updateFacilityDistrict(Integer fpId, Address address)	throws DAOException;
	
	GeoPolygonDef[] retrieveGeoPolygonDefs()	throws DAOException;
	
	ModelingExtractResult[] modelingExtract(Boolean searchTypePolygon, 
			Boolean searchTypeRadial, 
			Double latitudeDegrees, Double longitudeDegrees, 
			Integer distanceMeters, List<String> pollutants, 
			List<String> excludedFacilityTypes,
			Double latitudeSwDegrees, Double longitudeSwDegrees,
			Double latitudeSeDegrees, Double longitudeSeDegrees,
			Double latitudeNeDegrees, Double longitudeNeDegrees,
			Double latitudeNwDegrees, Double longitudeNwDegrees) throws DAOException;
	
	/**
	 * Creates the supplied facility. This method only creates the facility,
	 * none of the facilities contained objects are created. To create an entire
	 * Facility, including contained objects, the FacilityBO should be used.
	 * 
	 * This method should only be used by the migration routines, since Stars2
	 * should not be creating Facilities.
	 * 
	 * @param Facility
	 *            Facility to create
	 * @return Facility created facility.
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	Facility createFacility(Facility facility) throws DAOException;
	Facility createClonedFacility(Facility facility) throws DAOException;

	/**
	 * Removes the supplied facility.
	 * 
	 * @param fpId
	 *            Fp Id of facility to remove
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	void removeFacility(Integer fpId) throws DAOException;

	/**
	 * Modifies the supplied RUM. This method only modifies the.
	 * 
	 * @param RUMFacility
	 *            RUM Record to modify
	 * @return boolean True = success, false = failure.
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	boolean modifyRUM(FacilityRUM facilityRUM) throws DAOException;

	/**
	 * Modifies the supplied facility. This method only modifies the facility,
	 * none of the facilities contained objects are updated. To modify an entire
	 * Facility, including contained objects, the FacilityBO should be used.
	 * 
	 * @param Facility
	 *            Facility to modify
	 * @return boolean True = success, false = failure.
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	boolean modifyFacility(Facility facility) throws DAOException;

	/**
	 * Retrieves the Facility for the given FP id complete with all of its
	 * EmissionUnits.
	 * 
	 * @param Integer
	 *            FPId of the facility to be retrieved.
	 * @return Facility
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	Facility retrieveFacility(Integer fpId) throws DAOException;

	Facility retrieveCommonCompleteFacilityProfile(Facility facility)
			throws DAOException;

	/**
	 * Retrieves the real only tables associated with Facility from Gateway
	 * 
	 * @param Facility
	 *            Facility to be retrieved.
	 * @return Facility
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	Facility retrieveReadOnlyGateWayFacility(Facility facility)
			throws DAOException;

	/**
	 * Retrieves the Facility for the given facility id complete with all of its
	 * EmissionUnits.
	 * 
	 * @param String
	 *            Id of the facility to be retrieved.
	 * @return Facility
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	Facility retrieveFacility(String facilityId) throws DAOException;

	/**
	 * Retrives only the data in the fp_facility table.
	 * 
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 */
	Facility retrieveFacilityTable(String facilityId) throws DAOException;

	/**
	 * Retrieves the Facility for the given facility without any Emission Units
	 * 
	 * @param String
	 *            Facility ID of the facility to be retrieved.
	 * @param Integer
	 *            VesrsionId of the facility to be retrieved.
	 * @return Facility
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	Facility retrieveFacility(String facilityId, Integer versionId)
			throws DAOException;

	/**
	 * Retrieves the Facility Data for the given facility without any
	 * componenets
	 * 
	 * @param Integer
	 *            FP ID of the facility to be retrieved.
	 * @return Facility
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	Facility retrieveFacilityData(Integer fpId) throws DAOException;

	/**
	 * Retrieves the current Facility for the given facility core place ID
	 * without any components like EUs, etc.
	 * 
	 * @param Integer
	 *            corePlaceId of the facility to be retrieved.
	 * @return Facility
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	Facility retrieveFacilityByCorePlaceId(Integer corePlaceId)
			throws DAOException;

	/**
	 * @param attachment
	 * @return
	 * @throws DAOException
	 */
	Attachment createFacilityAttachment(Attachment attachment)
			throws DAOException;

	/**
	 * @param attachment
	 * @return
	 * @throws DAOException
	 */
	Attachment updateFacilityAttachment(Attachment attachment)
			throws DAOException;

	/**
	 * @param attachment
	 * @return
	 * @throws DAOException
	 */
	void removeFacilityAttachment(Attachment attachment) throws DAOException;

	/**
	 * @param fpId
	 * @throws DAOException
	 */
	void removeCountyNotifications(int fpId) throws DAOException;

	/**
	 * @param fpId
	 * @throws DAOException
	 */
	void removeFacilityNAICSs(int fpId) throws DAOException;

	/**
	 * @param fpId
	 * @throws DAOException
	 */
	void removeFacilitySICs(int fpId) throws DAOException;

	/**
	 * @param fpId
	 * @throws DAOException
	 */
	void removeFacilityMACTSubparts(int fpId) throws DAOException;

	/**
	 * @param fpId
	 * @throws DAOException
	 */
	void removeFacilityNeshapsSubparts(int fpId) throws DAOException;

	/**
	 * @param fpId
	 * @throws DAOException
	 */
	void removeFacilityNSPSSubparts(int fpId) throws DAOException;

	/**
	 * @param fpId
	 * @throws DAOException
	 */
	ArrayList<Address> retrieveFacilityAddresses(int fpId) throws DAOException;

	/**
	 * @param fpId
	 * @param countyId
	 * @throws DAOException
	 */
	void addCountyNotification(int fpId, String countyId) throws DAOException;

	/**
	 * @param facilityId
	 * @param roleCd
	 * @param userId
	 * @throws DAOException
	 */
	void addFacilityRole(String facilityId, String roleCd, int userId)
			throws DAOException;

	/**
	 * @param fpId
	 * @param facilityId
	 * @param noteId
	 * @throws DAOException
	 */
	void addFacilityNote(int fpId, String facilityId, int noteId)
			throws DAOException;

	/**
	 * @param fpId
	 * @param facilityId
	 * @param noteId
	 * @throws DAOException
	 */
	FacilityRUM createFacilityRUM(FacilityRUM facilityRUM) throws DAOException;

	/**
	 * @param fpId
	 * @param naicsCd
	 * @throws DAOException
	 */
	void addFacilityNAICS(int fpId, String naicsCd) throws DAOException;

	void deleteFacilityNaics(Integer fpId, String naics) throws DAOException;

	/**
	 * @param fpId
	 * @param sicCd
	 * @throws DAOException
	 */
	void addFacilitySIC(int fpId, String sicCd) throws DAOException;

	/**
	 * @param fpId
	 * @param mactCd
	 * @throws DAOException
	 */
	void addFacilityMACTSubpart(int fpId, String mactCd) throws DAOException;

	/**
	 * @param subpart
	 * @throws DAOException
	 */
	void addFacilityNeshapsSubpart(PollutantCompCode subpart)
			throws DAOException;

	/**
	 * @param fpId
	 * @param nspsCd
	 * @throws DAOException
	 */
	void addFacilityNSPSSubpart(int fpId, String nspsCd) throws DAOException;

	/**
	 * @param pollutant
	 * @throws DAOException
	 */
	void addFacilityNSPSPollutant(PollutantCompCode pollutant)
			throws DAOException;

	/**
	 * @param pollutant
	 * @throws DAOException
	 */
	void addFacilityPSDPollutant(PollutantCompCode pollutant)
			throws DAOException;

	/**
	 * @param pollutant
	 * @throws DAOException
	 */
	void addFacilityNSRPollutant(PollutantCompCode pollutant)
			throws DAOException;

	/**
	 * @param emissionUnit
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	EmissionUnit createEmissionUnit(EmissionUnit emissionUnit)
			throws DAOException;

	/**
	 * @param emissionUnit
	 *            ID
	 * @return Emission Type
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	String retrieveEmissionUnitTypeCd(int emuId) throws DAOException;

	/**
	 * @param emissionUnit
	 *            ID
	 * @return EAP_EMU_ID
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	String retrieveEmissionUnitDisplayId(int emuId) throws DAOException;

	/**
	 * @param Facility
	 *            ID, Emission Type Cd
	 * @return The related Emission Unit IDs
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	ArrayList<Integer> retrieveRelatedEmuIds(int fpId, String emissionUnitTypeCd)
			throws DAOException;

	/**
	 * @param Control
	 *            Equipment fpNodeId, New Control Equipment ID
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	void updateControlEquipmentId(int fpNodeId, String newEquId,
			String description) throws DAOException;

	/**
	 * @param Control
	 *            Equipment fpNodeId
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	String retrieveControlEquipmentTypeCd(int fpNodeId) throws DAOException;

	/**
	 * retrieve the Control Equipment ID
	 * 
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	String retrieveControlEquipmentId(int fpNodeId) throws DAOException;

	/**
	 * @param Emission
	 *            Unit ID, New Emission Unit Display ID
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	void updateEmissionUnitId(int emuId, String newEuId, String description)
			throws DAOException;

	/**
	 * @param emissionUnit
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	boolean modifyEmissionUnit(EmissionUnit emissionUnit) throws DAOException;

	boolean modifyEmissionUnit(EmissionUnit emissionUnit, String schema) throws DAOException;

	/**
	 * Retrieves all EmissionUnits for the given facility id including its
	 * "relationships".
	 * 
	 * @param Integer
	 *            Id of the facility for which the requested EmissionUnits
	 *            belong.
	 * @return EmissionUnit[]
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	EmissionUnit[] retrieveFacilityEmissionUnits(Integer facilityId)
			throws DAOException;

	/**
	 * @param emissionProcess
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	EmissionProcess createEmissionProcess(EmissionProcess emissionProcess)
			throws DAOException;

	/**
	 * Remove emission Process
	 * 
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	void removeEmissionProcess(EmissionProcess emissionProcess)
			throws DAOException;

	/**
	 * Retrieves the Emission Unit for the given emission unit id without any EU
	 * emissions.
	 * 
	 * @param Integer
	 *            Epa Emission Id of the emission unit to be retrieved
	 * @return RmissionUnit
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	EmissionUnit retrieveEmissionUnit(Integer epmRmuId) throws DAOException;

	/**
	 * Retrieves the Emission Unit for the given facility id and EPAemission
	 * unit id without any EU emissions
	 * 
	 * @param Integer
	 *            Id of the facility to be retrieved.
	 * @param String
	 *            Epa Emission Id of the emission unit to be retrieved
	 * @return RmissionUnit
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	EmissionUnit retrieveEmissionUnit(Integer facilityId, String epaEmuId)
			throws DAOException;

	/**
	 * Delete the emission unit identified by emuId.
	 * 
	 * @param emuId
	 *            unique integer identifier for an emission unit.
	 * @throws DAOException
	 */
	void removeEmissionUnit(Integer emuId) throws DAOException;

	/**
	 * Retrieves the emission process for the given emission process fp node id
	 * 
	 * @param Integer
	 *            emission process Fp Node Id of the process to be retrieved.
	 * @return Emission Process
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	EmissionProcess retrieveEmissionProcess(Integer emissionProcessFpnodeId)
			throws DAOException;

	/**
	 * Retrieves the Emission Type definition list
	 * 
	 * @return Emission Type
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	ArrayList<EmissionUnitTypeDef> retrieveEmissionUnitTypeDefs()
			throws DAOException;

	/**
	 * Retrieves the emission process for the given fp id and process id
	 * 
	 * @param Integer
	 *            fp Id of the process to be retrieved.
	 * @parm String process id of the process.
	 * @return Emission Process
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	EmissionProcess retrieveEmissionProcess(Integer fpId, String processId)
			throws DAOException;

	/**
	 * Retrieves the emission process for the given fp id, emission unit id and
	 * scc id
	 * 
	 * @param Integer
	 *            fp Id of the process to be retrieved.
	 * @param Integer
	 *            emission unit Id of the process to be retrieved.
	 * @parm String scc id of the process.
	 * @return Emission Process
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	EmissionProcess retrieveEmissionProcess(Integer fpId, Integer emuId,
			String sccId) throws DAOException;

	/**
	 * Retrieves the Control Equipment for the given control equipment fp node
	 * id
	 * 
	 * @param Integer
	 *            control equipment Fp Node Id of the control equipment to be
	 *            retrieved.
	 * @return ControlEquipment
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	ControlEquipment retrieveControlEquipment(Integer controlEquipFpnodeId)
			throws DAOException;

	/**
	 * Retrieves the Control Equipment for the given fp id and control equipment
	 * id
	 * 
	 * @param Integer
	 *            fp Id of the control equipment to be retrieved.
	 * @parm String control equipment id
	 * @return ControlEquipment
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	ControlEquipment retrieveControlEquipment(Integer fpId,
			String controlEquipId) throws DAOException;

	/**
	 * Retrieves the Release Point for the given release fp node id
	 * 
	 * @param Integer
	 *            Egress Fp Node Id of the Egree Point to be retrieved.
	 * @return EgressPoint
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	EgressPoint retrieveEgressPoint(Integer egreeFpnodeId) throws DAOException;

	/**
	 * Retrieves the Release Point for the given release fp id and release point
	 * id
	 * 
	 * @param Integer
	 *            Fp Id of the Egree Point to be retrieved.
	 * @parm egressPointId egress point id
	 * @return EgressPoint
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	EgressPoint retrieveEgressPoint(Integer fpId, String egressPointId)
			throws DAOException;

	/**
	 * @param emissionProcess
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	boolean modifyEmissionProcess(EmissionProcess emissionProcess)
			throws DAOException;

	/**
	 * Retrieves all EmissionProcesses for the given facility id including their
	 * "relationships"..
	 * 
	 * @param Integer
	 *            Id of the facility for which the requested EmissionProcesses
	 *            belong.
	 * @return HashMap<Integer, EmissionProcess> Keyed by the fp_nodeId
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	List<FacilityNode> retrieveFacilityEmissionProcesses(Integer facilityId)
			throws DAOException;

	/**
	 * @param submissionLog
	 * @return
	 * @throws DAOException
	 */
	SubmissionLog createSubmissionLog(SubmissionLog submissionLog)
			throws DAOException;

	/**
	 * @param controlEquipment
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	ControlEquipment createControlEquipment(ControlEquipment controlEquipment)
			throws DAOException;

	/**
	 * @param Emission
	 *            Unit
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	String retrieveNextEmissionUnitId(EmissionUnit emissionUnit)
			throws DAOException;
	
	/**
	 * @param Emission
	 *            Unit
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	String retrieveNextEmissionUnitIdBasedOnPreviousVersion(EmissionUnit emissionUnit, Integer oldFpId)
			throws DAOException;

	/**
	 * @param Emission
	 *            Process
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	String retrieveNextEmissonProcessId(EmissionProcess emissionProcess)
			throws DAOException;
	
	/**
	 * @param Emission
	 *            Process
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	String retrieveNextEmissonProcessIdBasedOnPreviousVersion(EmissionProcess emissionProcess, Integer oldFpId)
			throws DAOException;

	/**
	 * @param EgressPoint
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	String retrieveNextReleasePointId(EgressPoint releasePoint)
			throws DAOException;
	
	/**
	 * @param EgressPoint
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	String retrieveNextReleasePointIdBasedOnPreviousVersion(EgressPoint releasePoint, Integer oldfpId)
			throws DAOException;

	/**
	 * @param controlEquipment
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	String retrieveNextEquipmentId(ControlEquipment controlEquipment)
			throws DAOException;

	/**
	 * @param controlEquipment
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	String retrieveNextEquipmentIdBasedOnPreviousVersion(ControlEquipment controlEquipment, Integer oldFpId)
			throws DAOException;
	
	/**
	 * @param controlEquipment
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	boolean modifyControlEquipment(ControlEquipment controlEquipment)
			throws DAOException;

	/**
	 * Remove controlEquipment
	 * 
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	void removeControlEquipment(ControlEquipment controlEquipment)
			throws DAOException;

	/**
	 * retrieve the related release point fpnodeIDs
	 * 
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	ArrayList<Integer> retrieveRelatedReleasePointFpNodeIds(int fpId,
			String newRpTypeCd) throws DAOException;

	/**
	 * retrieve the release point ID
	 * 
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	String retrieveReleasePointId(int fpNodeId) throws DAOException;

	/**
	 * update the release point ID
	 * 
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	void updateReleasePointId(int fpNodeId, String newRpId, String description)
			throws DAOException;

	/**
	 * retrieve the release point type
	 * 
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	String retrieveReleasePointTypeCd(int fpNodeId) throws DAOException;

	/**
	 * @param egressPoint
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	EgressPoint createEgressPoint(EgressPoint egressPoint) throws DAOException;

	/**
	 * @param emissionUnit
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	boolean modifyEgressPoint(EgressPoint egressPoint) throws DAOException;

	/**
	 * Remove egressPoint
	 * 
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	void removeEgressPoint(EgressPoint egressPoint) throws DAOException;

	/**
	 * Retrieves all ControlEquipments for the given facility id including their
	 * "relationships".
	 * 
	 * @param Integer
	 *            Id of the facility for which the requested ControlEquipments
	 *            belong.
	 * @return List<ControlEquipment> Keyed by fp_nodeId
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	List<FacilityNode> retrieveFacilityControlEquipment(Integer facilityId)
			throws DAOException;

	/**
	 * Retrieves all ReleasePoints for the given facility id including their
	 * "relationships".
	 * 
	 * @param Integer
	 *            Id of the facility for which the requested EgressPoints
	 *            belong.
	 * @return List<FacilityNode> Keyed by fp_nodeId
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	List<FacilityNode> retrieveFacilityEgressPoints(Integer facilityId)
			throws DAOException;

	/**
	 * Retrieves emissions of the Emission Unit
	 * 
	 * @param EmissionUnit
	 *            EmissionUnit.
	 * @return None
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	void retrieveEuEmissions(EmissionUnit Emission) throws DAOException;

	/**
	 * @deprecated Replaced by {@link #createFacilityRoles(List<FacilityRole>)}
	 * @param newRoles
	 * @return
	 * @throws DAOException
	 */
	@Deprecated
	FacilityRole[] createFacilityRoles(FacilityRole[] newRoles)
			throws DAOException;

	/**
	 * @param fpId
	 * @throws DAOException
	 */
	void removeFacilityRoles(String facilityId) throws DAOException;

	/**
	 * @param fpId
	 * @return
	 * @throws DAOException
	 */
	FacilityRole[] retrieveFacilityRoles(String facilityId) throws DAOException;

	/**
	 * 
	 * @param userId
	 * @return
	 * @throws DAOException
	 */
	FacilityRole[] retrieveFacilityRolesByUserId(Integer userId)
			throws DAOException;

	/**
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 */
	FacilityNote[] retrieveFacilityNotes(String facilityId) throws DAOException;

	/**
	 * @param rumId
	 * @return
	 * @throws DAOException
	 */
	FacilityRUM retrieveFacilityRUM(int rumId) throws DAOException;

	/**
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 */
	FacilityRUM[] retrieveFacilityRUMs(String facilityId) throws DAOException;

	/**
	 * @param fpId
	 * @return
	 * @throws DAOException
	 */
	Attachment[] retrieveFacilityAttachments(String facilityId)
			throws DAOException;

	/**
	 * Retrieves all facilities matching the criteria parameters supplied.
	 * Suppling all "nulls" will return all facilities in the DB.
	 * 
	 * @param String
	 *            facilityName either the exact name of the facility, a
	 *            wildcarded string or null.
	 * @param String
	 *            facilityId either the exact ID, a wildcarded stringor null.
	 * @param Integer
	 *            counyId the exact county ID or null.
	 * @param String
	 *            operatingStatusCd either the exact operating status CD, or
	 *            null.
	 * @param String
	 *            doLaaCd either the exact DO-LAA CD, or null.
	 * @param String
	 *            naicsCd either the exact naicsCd, or null.
	 * @param String
	 *            permitClassCd either the exact permitClassCd, or null
	 * @param String
	 *            tvPermitStatusCd either the exact tvPermitStatusCd, or null
	 * 
	 * @return FacilityList[]
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	FacilityList[] searchFacilities(String facilityName, String facilityId,
			String companyName, Integer corePlaceId, String countyCd,
			String operatingStatusCd, String doLaaCd, String naicsCd,
			String permitClassCd, String tvPermitStatusCd, String address1,
			String city, String zip5, String portable, String portableGroupCd,
			boolean unlimitedResults, String facilityTypeCd)
			throws DAOException;

	FacilityEmissionUnit[] searchEuStatus(String facilityName,
			String facilityId, String doLaaCd,
			String facilityOperatingStatusCd, String euOperatingStatusCd,
			String permitClassCd, boolean unlimitedResults) throws DAOException;

	/**
	 * Retrieves all facilities matching the criteria parameters supplied.
	 * Suppling all "nulls" will return all facilities in the DB.
	 * 
	 * @param String
	 *            facilityName either the exact name of the facility, a
	 *            wildcarded string or null.
	 * @param String
	 *            facilityId either the exact ID, a wildcarded stringor null.
	 * @param Integer
	 *            counyId the exact county ID or null.
	 * @param String
	 *            doLaaCd either the exact DO-LAA CD, or null.
	 * 
	 * @return FacilityList[]
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	FacilityList[] searchTvPermitFacilities(String facilityName,
			String facilityId, String countyCd, String doLaaCd,
			Timestamp startDt, Timestamp endDt, boolean unlimitedResults)
			throws DAOException;

	/**
	 * Retrieves all facilities matching the criteria parameters supplied.
	 * Suppling all "nulls" will return all facilities in the DB.
	 * 
	 * @param String
	 *            facilityName either the exact name of the facility, a
	 *            wildcarded string or null.
	 * @param String
	 *            facilityId either the exact ID, a wildcarded stringor null.
	 * @param Integer
	 *            counyId the exact county ID or null.
	 * @param String
	 *            doLaaCd either the exact DO-LAA CD, or null.
	 * 
	 * @return FacilityList[]
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	FacilityList[] searchTvExpFacilities(String facilityName,
			String facilityId, String countyCd, String doLaaCd,
			boolean unlimitedResults) throws DAOException;

	/**
	 * @param searchFacility
	 * @return FacilityHistList[]
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	FacilityHistList[] searchFacilitiesHist(FacilityHistList searchFacility)
			throws DAOException;

	/**
	 * Retrieves all version of facility for a given facility ID
	 * 
	 * @param String
	 *            facilityId the exact ID.
	 * 
	 * @return FacilityVersion[]
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	FacilityVersion[] retrieveAllFacilityVersions(String facilityId)
			throws DAOException;

	/**
	 * Retrieves all version of facility for a given facility ID This method is
	 * intended to be used by migration.
	 * 
	 * @param String
	 *            facilityId the exact ID.
	 * 
	 * @return FacilityVersion[]
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	FacilityVersion[] retrieveAllMigFacilityVersions(String facilityId)
			throws DAOException;

	/**
	 * Create a facility node
	 * 
	 * @return FacilityNode
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	FacilityNode createFacilityNode(Integer fpId, Integer nodeId, Integer corrId)
			throws DAOException;

	/**
	 * Remove a facility node
	 * 
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	void removeFacilityNode(Integer fpNodeId) throws DAOException;

	/**
	 * Create a Relationship between two Facility Nodes
	 * 
	 * @return FacilityRelationship
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	FacilityRelationship createRelationShip(Integer fromFpNodeId,
			Integer toFpNodeId) throws DAOException;

	/**
	 * Create a Relationship between two Facility Nodes
	 * 
	 * @return FacilityRelationship
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	FacilityRelationship createRelationShip(Integer fromFpNodeId,
			Integer toFpNodeId, Float flowFactor) throws DAOException;

	/**
	 * Delete a Relationship between two Facility Nodes
	 * 
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	void removeRelationShip(Integer fromFpNodeId, Integer toFpNodeId)
			throws DAOException;

	/**
	 * Modify a Relationship between two Facility Nodes
	 * 
	 * @return
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	void modifyRelationShip(FacilityRelationship relationship)
			throws DAOException;

	/**
	 * Create a EuEmission
	 * 
	 * @return EuEmission
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	EuEmission createEuEmission(EuEmission euEmission) throws DAOException;

	/**
	 * Retrieves all contacts for the given facility
	 * 
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 */
	List<Contact> retrieveFacilityContacts(String facilityId)
			throws DAOException;

	/**
	 * Retrieves all contacts or only active contacts for the given facility
	 * 
	 * @param facilityId
	 * @param active
	 * @return
	 * @throws DAOException
	 */
	List<Contact> retrieveFacilityContacts(String facilityId, boolean active)
			throws DAOException;

	/**
	 * Retrieves all facilty contact based on first, middle, last name & company
	 * name
	 * 
	 * @param contact
	 * @return
	 * @throws DAOException
	 */

	List<Contact> retrieveStagingFacilityContacts(String facilityId)
			throws DAOException;

	/**
	 * Retrieves all facilty contact based on first, middle, last name & company
	 * name
	 * 
	 * @param contact
	 * @return
	 * @throws DAOException
	 */
	Contact[] retrieveDupFacilityContact(Contact contact) throws DAOException;

	/**
	 * Retrieves a facilty contact based on first, middle, last name & company
	 * name
	 * 
	 * @param contact
	 * @return
	 * @throws DAOException
	 */
	Contact retrieveFacilityContact(Contact contact) throws DAOException;

	/**
	 * @param el
	 * @return
	 * @throws DAOException
	 */
	EventLog createEventLog(EventLog el) throws DAOException;

	/**
	 * @param els
	 * @return
	 * @throws DAOException
	 */
	EventLog[] createEventLogs(EventLog[] els) throws DAOException;

	/**
	 * @param el
	 * @return
	 * @throws DAOException
	 */
	EventLog[] retrieveEventLogs(EventLog el) throws DAOException;

	/**
	 * Retrieves all data detail definitions for a control equipment type
	 * 
	 * param contEquipType
	 * 
	 * @return
	 * @throws DAOException
	 */
	DataDetail[] retrieveContEquipDataDetail(String contEquipType)
			throws DAOException;

	/**
	 * @param emuId
	 * @throws DAOException
	 */
	void removeEuEmissions(int emuId) throws DAOException;

	/**
	 * @param fpNodeId
	 * @throws DAOException
	 */
	void removePollutantsControlled(int fpNodeId) throws DAOException;

	/**
	 * Create a PollutantsControlled
	 * 
	 * @return PollutantsControlled
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	PollutantsControlled createPollutantsControlled(
			PollutantsControlled pollutant) throws DAOException;

	/*
	 * Increase version number all versions of a facility where version number
	 * is greater than a given version.
	 */
	void increaseVersionIds(String facilityId, Integer versionId)
			throws DAOException;

	/*
	 * Retrieve Multi-Establishment facilities for a given facility.
	 * 
	 * @return MultiEstabFacilityList[] @throws
	 * us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	MultiEstabFacilityList[] retrieveMultiiEstabFacilities(String federalSCSCId)
			throws DAOException;

	/**
	 * Create a ControlEquipmentData
	 * 
	 * @return ControlEquipmentData
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	void addControlEquipmentData(Integer fpNodeId, DataDetail dataDetail)
			throws DAOException;

	/**
	 * @param fpNodeId
	 * @throws DAOException
	 */
	void removeControlEquipmentData(int fpNodeId) throws DAOException;

	/**
	 * @param fpId
	 * @param addressId
	 * @throws DAOException
	 */
	void addFacilityAddress(int fpId, int addressId) throws DAOException;

	/**
	 * @param companyName
	 * @throws DAOException
	 */
	int findCompanyId(String companyName) throws DAOException;

	/**
	 * @param facilityId
	 * @param startDate
	 * @param companyId
	 * @throws DAOException
	 */
	void addFacilityCompany(String facilityId, Timestamp startDate,
			int companyId, int lastModified) throws DAOException;

	/**
	 * Returns the next sequence Facility ID for a given county and city
	 * 
	 * @param facilityIdRef
	 * @return
	 * @throws DAOException
	 */
	Integer nextfacilityIdSeqNum() throws DAOException;
	
	/**
     * Returns the next sequence Facility ID for a given county and city
     * 
     * @param facilityIdRef
     * @return
     * @throws DAOException
     */
    Integer nextfacilityIdSeqNum(FacilityIdRef facilityIdRef)
            throws DAOException;

	/**
	 * @param currentFacilityId
	 * @param newFacilityId
	 * @throws DAOException
	 */
	void changeFacilityId(String tableName, String currentFacilityId,
			String newFacilityId) throws DAOException;

	/**
	 * @param year
	 * @param dueDateString1
	 * @param dueDateString2
	 * @param shutDownDateFrom
	 * @param shutDownDateTo
	 * @return
	 * @throws DAOException
	 */
	Facility[] retrieveTvCertOverdue(Integer year, String dueDateString1,
			String dueDateString2, Timestamp shutDownDateFrom,
			Timestamp shutDownDateTo) throws DAOException;

	/**
	 * @param year
	 * @param dueDateString
	 * @param shutDownDateFrom
	 * @param shutDownDateTo
	 * @return
	 * @throws DAOException
	 */
	FacilityList[] retrieveTvCertLate(Integer reportYear, String facilityId,
			String facilityName, String operatingStatusCd, String dueDate,
			Timestamp shutDownDateFrom, Timestamp shutDownDateTo)
			throws DAOException;

	/**
	 * @param year
	 * @param dueDateString
	 * @param effectiveDate
	 * @param expirationDate
	 * @return
	 * @throws DAOException
	 */
	FacilityList[] retrieveTvCertReminder(Integer reportYear,
			String facilityId, String facilityName, String dueDate,
			Timestamp effectiveDate, Timestamp expirationDate)
			throws DAOException;

	/**
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	EmissionsRptInfo[] retrieveFERNoticeViolation(Integer year)
			throws DAOException;

	/**
	 * @return
	 * @throws DAOException
	 */
	FacilityExport[] retrieveFacilityExports() throws DAOException;

	/**
	 * @param searchObj
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws DAOException
	 */
	SubmissionLog[] searchSubmissionLog(SubmissionLog searchObj,
			Timestamp beginDate, Timestamp endDate) throws DAOException;

	/**
	 * Change all version of facility to the dola cd.
	 * 
	 * @param facilityId
	 * @param dolaCd
	 * @throws DAOException
	 */
	void changeDolaCd(String facilityId, String dolaCd) throws DAOException;

	/**
	 * Retrieve past versions of an emission unit (i.e. emission unit data for
	 * historical facility inventories, but not the current profile).
	 * 
	 * @param corrEpaEmuId
	 *            the id to uniquely identify this EU
	 * @return
	 * @throws DAOException
	 */
	EmissionUnit[] retrieveEmissionUnitsFromPastProfiles(int corrEpaEmuId,
			String facilityId) throws DAOException;

	/**
	 * Retrieve the facility APIs
	 * 
	 * @param fpId
	 *            the facility fp_id
	 * @return
	 * @throws DAOException
	 */
	List<ApiGroup> retrieveFacilityApis(int fpId) throws DAOException;

	/**
	 * Create the facility API
	 * 
	 * @param api
	 *            the facility Api_Group object
	 * @return
	 * @throws DAOException
	 */
	void createFacilityApi(ApiGroup api) throws DAOException;

	/**
	 * Update the facility API
	 * 
	 * @param api
	 *            the facility Api_Group object
	 * @return
	 * @throws DAOException
	 */
	void updateFacilityApi(ApiGroup api) throws DAOException;

	/**
	 * Delete a facility API
	 * 
	 * @param apiCd
	 *            the facility Api_Group api_cd
	 * @return
	 * @throws DAOException
	 */
	void deleteFacilityApi(Integer apiCd) throws DAOException;

	/**
	 * Delete All APIs in a facility
	 * 
	 * @param fpCd
	 *            the facility fp_Id
	 * @return
	 * @throws DAOException
	 */
	void deleteFacilityAllApis(Integer fpId) throws DAOException;

	/**
	 * remove All Emission Unit, Emission Process, Control Equipment, and
	 * Release Point in a facility
	 * 
	 * @param fpCd
	 *            the facility fp_Id
	 * @return
	 * @throws DAOException
	 */
	void removeFacilitySubObject(Integer fpId) throws DAOException;

	/**
	 * Removes the given owner as a facility's owner.
	 * 
	 * @param oldOwner
	 */
	void removeFacilityOwner(FacilityOwner oldCompany) throws DAOException;

	/**
	 * Adds the given owner as a facility's owner.
	 * 
	 * @param newOwner
	 */
	void addFacilityOwner(FacilityOwner newCompany) throws DAOException;

	/**
	 * Retrieves a list of owners for a given facility.
	 * 
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 */
	List<FacilityOwner> retrieveFacilityOwners(String facilityId)
			throws DAOException;

	/**
	 * Retrieves the current owner of a given facility.
	 * 
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 */
	FacilityOwner retrieveFacilityOwner(String facilityId) throws DAOException;

	List<String> retrieveFacilityNAICSCodes(Integer fpId) throws DAOException;

	Facility retrieveFacilityByFacilityId(String facilityId)
			throws DAOException;

	Integer retrieveEmissionUnitIdByWiseId(String wiseViewId)
			throws DAOException;

	Integer retrieveProcessIdByWiseId(String wiseViewId) throws DAOException;

	Integer retrieveFpIdByFacilityId(String facilityId) throws DAOException;

	List<Integer> retrieveControlEquipIdsByProcessWiseId(String epWiseViewId)
			throws DAOException;

	List<EmissionUnitReplacement> retrieveEmissionUnitReplacements(Integer emuId)
			throws DAOException;

	EmissionUnitReplacement createEmissionUnitReplacement(
			EmissionUnitReplacement emissionUnitReplacement)
			throws DAOException;

	void removeEmissionUnitReplacements(Integer EmuId) throws DAOException;

	List<FixmeCompany> retrieveFixmeCompanies() throws DAOException;

	boolean removeFixmeCompanies() throws DAOException;
	
	/**
     * Retrieves all facility ID reference Data for a given county.
     * 
     * @param countyCd
     * @return
     * @throws DAOException
     */
    FacilityIdRef[] retrieveFacilityIdRefs(String countyCd) throws DAOException;

	Map<String, ArrayList<DataDetail>> retrieveCeDataDetails()
			throws DAOException;
	
	EmissionUnit retrieveEmissionUnitByCorrEpaEmuId(Integer fpId, Integer corrEpaEmuId)
			throws DAOException;
	
	/**
	 * Creates the supplied facility request.
	 * 
	 * 
	 * @param FacilityRequest
	 *            FacilityRequest to create
	 * @return FacilityRequest created facility request.
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	FacilityRequest createFacilityRequest(FacilityRequest facilityRequest) throws DAOException;
	
	/**
	 * Retrieves all facility requests matching the criteria parameters supplied.
	 * Suppling all "nulls" will return all facility requests in the DB.
	 * 
	 * @param String
	 *            facilityName either the exact name of the facility, a
	 *            wildcarded string or null.
	 * @param String
	 *            requestId either the exact ID, a wildcarded string or null.
	 * @param Integer
	 *            counyId the exact county ID or null.
	 * @param String
	 *            operatingStatusCd either the exact operating status CD, or
	 *            null.
	 * @param String
	 *            doLaaCd either the exact DO-LAA CD, or null.
	 * @param String
	 *            naicsCd either the exact naicsCd, or null.
	 * @param String
	 *            permitClassCd either the exact permitClassCd, or null
	 * @param String
	 *            tvPermitStatusCd either the exact tvPermitStatusCd, or null
	 * 
	 * @return FacilityList[]
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	FacilityRequestList[] searchFacilityRequests(String facilityName, String requestId,
			String companyName, String countyCd,
			String operatingStatusCd, String doLaaCd, String firstName, String lastName, String externalUsername,
			String address1, String cntId, String phone, String email,
			String requestState, boolean unlimitedResults,
			String facilityTypeCd,
			Integer companyId)
			throws DAOException;
	
	FacilityRequest retrieveFacilityRequest(Integer requestId)
			throws DAOException;
	
	Integer retrieveFacilityRequestCount(Integer companyId)
			throws DAOException;
	
	/**
	 * Modifies the supplied facility request.
	 * 
	 * @param FacilityRequest
	 *            FacilityRequest to modify
	 * @return boolean True = success, false = failure.
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	boolean modifyFacilityRequest(FacilityRequest facilityRequest) throws DAOException;
	
	void removeFacilityRequest(int requestId) throws DAOException;

	void removeFacilityRoles(Set<String> facilityIds) throws DAOException;

	void createFacilityRoles(List<FacilityRole> facilityRoles) throws DAOException;

	FacilityRole[] retrieveFacilityRolesByFacilities(FacilityList[] facilities, Integer userId)
			throws DAOException;

	void removeFacilityRoles(List<FacilityRole> facilityRoles, String facilityRoleCd, int currentUserId)
		throws DAOException;

	FacilityRole[] retrieveFacilityRolesByFacilitiesByRole(
			FacilityList[] selectedFacilities, String facilityRole, int userId)
		throws DAOException;

	AppPermitSearchResult[] appPermitSearch(String searchCmpId,
			String searchFacilityId, Integer searchType, Integer appPermitType)
					throws DAOException;
	
	// Facility CEM/COM/CMS Limits
	
	/**
	 * @param fpId
	 * @throws DAOException
	 */
	List<FacilityCemComLimit> retrieveFacilityCemComLimitListByFpId(Integer fpId) throws DAOException;
	
	/**
	 * @param monitorId
	 * @throws DAOException
	 */
	List<FacilityCemComLimit> retrieveFacilityCemComLimitListByMonitorId(Integer monitorId) throws DAOException;
	
	
	/**
	 * @param monitorId
	 * @throws DAOException
	 */
	void removeFacilityCemComLimitList(int monitorId) throws DAOException;

	/**
	 * @param fccl
	 * @return
	 * @throws DAOException
	 */
	FacilityCemComLimit createFacilityCemComLimit(FacilityCemComLimit fccl)
			throws DAOException;

	/**
	 * @param fccl
	 * @throws DAOException
	 */
	void modifyFacilityCemComLimit(FacilityCemComLimit fccl) throws DAOException;

	/**
	 * @param fccl
	 * @throws DAOException
	 */
	void removeFacilityCemComLimit(FacilityCemComLimit fccl) throws DAOException;
	
	String retrieveNextLimId(Integer continuousMonitorId) throws DAOException;
	
	FacilityCemComLimit retrieveFacilityCemComLimitByMonitorIdAndCorrId(Integer monitorId, 
			Integer corrLimitId) throws DAOException;
	
	Integer retrieveCemsComplianceReportCountWithMonitor(Integer corrMonitorId)
			throws DAOException;

	Integer retrieveCemsComplianceReportCountWithLimit(Integer corrLimitId)
			throws DAOException;
	
	List<String> retrieveFacilitiesWithMatchingApiNumber(String apiNo,
			String facilityId) throws DAOException;
	
	List<Integer> retrieveAllControlEquipIdsByEmuId(Integer emuId)
			throws DAOException;
	
	List<PollutantsControlled> retrievePollutantsControlled(Integer ceFpNodeId)
			throws DAOException;
	
	Integer retrieveAddressIdForFacilityOnLastDOP(String facilityId, String lastDayOfPeriod)
			throws DAOException;

	EmissionUnit retrieveEmissionUnitFromCurrentProfile(int corrEpaEmuId, String facilityId) 
			throws DAOException;

	ArrayList<EiDataImportFacilityInformation> retrieveEiDataImportFacilityInformation()
			throws DAOException;

	List<FceContinuousMonitorLineItem> searchFacilityCemComLimitsByDate(String facId, Timestamp startDate,
			Timestamp endDate) throws DAOException;

	List<HydrocarbonAnalysisPollutant> retrieveFacExtendedHydrocarbonAnalysisPollutant(Integer fpId) throws DAOException;
	
	void addFacExtendedHCAnalysisPollutant(HydrocarbonAnalysisPollutant hcPollutant) throws DAOException;
	
	void deleteFacExtendedHCAnalysisPollutant(Integer fpId) throws DAOException;
	
	public HydrocarbonAnalysisSampleDetail retrieveFacHydrocarbonAnalysisSampleDetail(Integer fpId) throws DAOException;
	
	public void addFacHydrocarbonAnalysisSampleDetail(HydrocarbonAnalysisSampleDetail hcSampleDetail) throws DAOException;
	
	public void deleteFacHydrocarbonAnalysisSampleDetail(Integer fpId) throws DAOException;
	
	boolean modifyFacHydrocarbonAnalysisSampleDetail(HydrocarbonAnalysisSampleDetail hcSampleDetail) throws DAOException;
	
	DecaneProperties createDecaneProperties(DecaneProperties decaneProperties) throws DAOException;
	
	DecaneProperties retrieveDecaneProperties(Integer fpId) throws DAOException;
	
	boolean modifyDecaneProperties(DecaneProperties decaneProperty) throws DAOException;
	
	boolean deleteDecaneProperties(Integer fpId) throws DAOException;
	
	public List<FacilityRoleActivity> retrieveFacilityUserRoleActivities(String facilityRoleCd) throws DAOException;

	public List<FacilityPurgeSearchLineItem> retrieveFacilityListForPurging(Timestamp purdgeDate) throws DAOException;
	
	public List<FacilityPurgeLog> retrieveFacilityPurgeLogs() throws DAOException;
	
	public FacilityPurgeLog createFacilityPurgeLog(FacilityPurgeLog facilityPurgeLog) throws DAOException;

	void deleteFacilityCompanyRelationship(String facilityId) throws DAOException;

	void deleteFacilityContactRelationship(String facilityId) throws DAOException;

//	boolean deleteFacilitySubmissionLogs(String facilityId) throws DAOException;
	
	boolean deleteFacilityNotes(String facilityId) throws DAOException;

	boolean deleteSiteVisitsByFpid(Integer fpId) throws DAOException;

	boolean deleteSiteVisitsXrefByFpid(Integer fpId) throws DAOException;

	boolean deleteSiteVisitNoteXrefByFpId(Integer fpId) throws DAOException;

	boolean deleteSVAttachments(Integer fpId) throws DAOException;

	boolean deleteFacilitySubmissionLogs(String facilityId) throws DAOException;

	boolean deleteFacilityAttachments(String string) throws DAOException;

	boolean deleteCEEnforcementAttachments(String facilityId) throws DAOException;

	void deleteFacilityEventLogs(Integer fpId) throws DAOException;
	
	public void deleteFacilityRUM(String facilityId) throws DAOException;

}
