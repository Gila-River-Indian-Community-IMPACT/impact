package us.oh.state.epa.stars2.webcommon.documentgeneration.inspection;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import us.oh.state.epa.stars2.app.ceta.FceDetail;
import us.oh.state.epa.stars2.database.dbObjects.ceta.AmbientConditions;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceAmbientMonitorLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceApplicationSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceAttachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceContinuousMonitorLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceEmissionsInventoryLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FcePermitCondition;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceStackTestSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SiteVisit;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.CetaStackTestMethodDef;
import us.oh.state.epa.stars2.def.ComplianceReportAcceptedDef;
import us.oh.state.epa.stars2.def.ComplianceReportTypeDef;
import us.oh.state.epa.stars2.def.ContentTypeDef;
import us.oh.state.epa.stars2.def.CorrespondenceCategoryDef;
import us.oh.state.epa.stars2.def.CorrespondenceDirectionDef;
import us.oh.state.epa.stars2.def.FceAttachmentTypeDef;
import us.oh.state.epa.stars2.def.MonitorStatusDef;
import us.oh.state.epa.stars2.def.MonitoringCollFreqCodeDef;
import us.oh.state.epa.stars2.def.PermitActionTypeDef;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.ReportComplianceStatusDef;
import us.oh.state.epa.stars2.def.ReportOfEmissionsStateDef;
import us.oh.state.epa.stars2.def.SiteVisitTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentGenerationException;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.ComplianceEvaluationDefs;
import us.oh.state.epa.stars2.webcommon.documentgeneration.DocumentBuilder;
import us.wy.state.deq.impact.def.OverallInspectionComplianceStatusDef;
import us.wy.state.deq.impact.def.PermitConditionStatusDef;
import us.wy.state.deq.impact.def.PermitLevelStatusDef;
import us.wy.state.deq.impact.def.SkyConditionsDef;
import us.wy.state.deq.impact.def.WindDirectionDef;

public class InspectionInfoBuilder extends DocumentBuilder {

	private static final long serialVersionUID = -8141483478129458369L;

	private Integer fceId;

	public Integer getFceId() {
		return fceId;
	}

	public void setFceId(Integer fceId) {
		this.fceId = fceId;
	}

	@Override
	public void loadData() throws DocumentGenerationException {

		if (null != this.fceId) {

			try {
				FullComplianceEval fce = retrieveCompleteFce(this.fceId);

				if (null != fce) {

					loadBasicInspectionData(fce);
					loadAmbientConditionsData(fce);
					loadApplicationsSnapshotData(fce);
					loadPermitsSnapshotData(fce);
					loadStackTestsSnapshotData(fce);
					loadComplianceReportsSnapshotData(fce);
					loadCorrespondenceSnapshotData(fce);
					loadEmissionsInventoriesSnapshotData(fce);
					loadContinuousMonitorsSnapshotData(fce);
					loadAmbientMonitorsSnapshotData(fce);
					loadSiteVisitsSnapshotData(fce);
					loadAttachmentsData(fce);
					loadPermitConditionsData(fce);
					loadHeadingData();
				}
			} catch (RemoteException re) {
				logger.error(
						"An error occured when getting the inspection data for " + this.fceId + ":" + re.getMessage(),
						re);
				throw new DocumentGenerationException(re.getMessage(), re);
			}
		}
	}

	@Override
	public <E> void setId(E id) {
		this.fceId = (Integer) id;
	}

	private void loadBasicInspectionData(final FullComplianceEval fce) throws RemoteException {

		if (null != fce) {

			this.tagValueMap.put("inspectionID", fce.getInspId());

			this.tagValueMap.put("inspectionOverallComplianceDesc",
					OverallInspectionComplianceStatusDef.getData().getItems().getItemDesc(fce.getComplianceStatusCd()));
			this.tagValueMap.put("inspectionOverallComplianceLongDesc",
					OverallInspectionComplianceStatusDef.getLongDscData().getItems().getItemDesc(fce.getComplianceStatusCd()));
			this.tagValueMap.put("inspectionPreviousDate",
					getFormattedDate(DEFAULT_DATE_TIME_FORMAT, fce.getLastInspDate()));
			this.tagValueMap.put("inspectionFacilityStaff", fce.getFacilityStaffPresent());
			this.tagValueMap.put("inspectionAQDInspector", retrieveUserName(fce.getEvaluator()));
			this.tagValueMap.put("inspectionConcerns", fce.getInspectionConcerns());
			this.tagValueMap.put("inspectionFileReview", fce.getFileReview());
			this.tagValueMap.put("inspectionRegDiscussion", fce.getRegulatoryDiscussion());
			this.tagValueMap.put("inspectionPhysical", fce.getPhysicalInspectionOfPlant());
			this.tagValueMap.put("inspectionAmbient", fce.getAmbientMonitoring());
			this.tagValueMap.put("inspectionOther", fce.getOtherInformation());
		}
	}

	private void loadAmbientConditionsData(final FullComplianceEval fce) {

		if (null != fce) {

			if (null != fce.getDayOneAmbientConditions()) {

				AmbientConditions dayOneAmbCond = fce.getDayOneAmbientConditions();

				this.tagValueMap.put("inspectionDateFirstDay", 
						getFormattedDate(DEFAULT_DATE_TIME_FORMAT, dayOneAmbCond.getInspectionDate()));
				this.tagValueMap.put("inspectionAmbientTempFirstDay", dayOneAmbCond.getAmbientTemperature());
				this.tagValueMap.put("inspectionAmbientWindDirectionFirstDay",
						WindDirectionDef.getData().getItems().getItemDesc(dayOneAmbCond.getWindDirectionCd()));
				this.tagValueMap.put("inspectionAmbientWindSpeedFirstDay", dayOneAmbCond.getWindSpeed());
				this.tagValueMap.put("inspectionAmbientSkyCondFirstDay",
						SkyConditionsDef.getData().getItems().getItemDesc(dayOneAmbCond.getSkyConditionCd()));
				this.tagValueMap.put("inspectionArrivalTimeFirstDay",
						getArrivalDepartureTimeValue(dayOneAmbCond.getArrivalTime()));
				this.tagValueMap.put("inspectionDepartureTimeFirstDay",
						getArrivalDepartureTimeValue(dayOneAmbCond.getDepartureTime()));

				this.tagValueMap.put("inspectionFFY", getFormattedDate("yyyy", dayOneAmbCond.getInspectionDate()));
			}

			if (null != fce.getDayTwoAmbientConditions()) {

				AmbientConditions dayTwoAmbCond = fce.getDayTwoAmbientConditions();

				this.tagValueMap.put("inspectionDateSecondDay", 
						getFormattedDate(DEFAULT_DATE_TIME_FORMAT, dayTwoAmbCond.getInspectionDate()));
				this.tagValueMap.put("inspectionAmbientTempSecondDay", dayTwoAmbCond.getAmbientTemperature());
				this.tagValueMap.put("inspectionAmbientWindDirectionSecondDay",
						WindDirectionDef.getData().getItems().getItemDesc(dayTwoAmbCond.getWindDirectionCd()));
				this.tagValueMap.put("inspectionAmbientWindSpeedSecondDay", dayTwoAmbCond.getWindSpeed());
				this.tagValueMap.put("inspectionAmbientSkyCondSecondDay",
						SkyConditionsDef.getData().getItems().getItemDesc(dayTwoAmbCond.getSkyConditionCd()));
				this.tagValueMap.put("inspectionArrivalTimeSecondDay",
						getArrivalDepartureTimeValue(dayTwoAmbCond.getArrivalTime()));
				this.tagValueMap.put("inspectionDepartureTimeSecondDay",
						getArrivalDepartureTimeValue(dayTwoAmbCond.getDepartureTime()));
			}
		}
	}

	private FullComplianceEval retrieveCompleteFce(final Integer fceId)
			throws RemoteException {

		FullComplianceEval fce = null;

		if (null != fceId) {

			fce = getFullComplianceEvalService().retrieveFceOnly(fceId);

			if (null != fce) {

				String facilityId = fce.getFacilityId();

				fce = getFullComplianceEvalService().retrieveFce(facilityId, fceId);
			}
		}

		return fce;
	}

	private String getArrivalDepartureTimeValue(final String arrivalDepartureTimeCd) {

		String arrivalDepartureTimeValue = null;

		if (null != arrivalDepartureTimeCd) {

			LinkedHashMap<String, String> map = FceDetail.getArrivalDepartureTimeMap();

			if (null != map) {

				Iterator<String> iter = map.keySet().iterator();

				while (iter.hasNext()) {

					String key = (String) iter.next();
					String value = (String) map.get(key);

					if (arrivalDepartureTimeCd.equalsIgnoreCase(value)) {

						arrivalDepartureTimeValue = key;
						break;
					}
				}
			}
		}

		return arrivalDepartureTimeValue;
	}
	
	// permit application snapshot from the pre-inspection page
	private void loadApplicationsSnapshotData(final FullComplianceEval fce) throws  DAOException {
		
		if (null != fce) {

			List<FceApplicationSearchLineItem> preservedApplicationList = getFullComplianceEvalService()
					.retrieveFceApplicationsPreserved(fce);

			if (null != preservedApplicationList) {

				List<List<?>> table = new ArrayList<List<?>>();

				List<String> headerRow = new ArrayList<String>(Arrays.asList(
						"Request Number",
						"Purpose of Application",
						"Request Type",
						"Received Date",
						"Submitted?",
						"Previous Application Number",
						"Permit Number",
						"Permit Final Issuance Date"));

				table.add(headerRow);

				for (FceApplicationSearchLineItem application : preservedApplicationList) {

					List<Object> row = new ArrayList<Object>();

					row.add(application.getApplicationNumber());

					row.add(application.getApplicationDesc());

					row.add(ApplicationTypeDef.getData().getItems().getItemDesc(application.getApplicationTypeCd()));

					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, application.getReceivedDate()));

					String submitted = (null != application.getSubmittedDate()) ? "Yes" : "No";
					row.add(submitted);

					String previousApplicationNumber = application.getPreviousApplicationNumber();
					if (Utility.isNullOrEmpty(previousApplicationNumber)) {
						row.add("N/A");
					} else {
						row.add(previousApplicationNumber);
					}

					row.add(application.getPermitNumber());

					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, application.getFinalIssuanceDate()));

					table.add(row);
				}

				this.tagValueMap.put("inspectionPermitApplicationList", table);
			}
		}
	}
	
	// permit snapshot from the pre-inspection page
	private void loadPermitsSnapshotData(final FullComplianceEval fce) throws DAOException {

		if (null != fce) {
			
			List<Permit> preservedPermitList = getFullComplianceEvalService().retrieveFcePermitsPreserved(fce);

			if (null != preservedPermitList) {

				List<List<?>> table = new ArrayList<List<?>>();

				List<String> headerRow = new ArrayList<String>(Arrays.asList(
						"Permit Number",
						"Legacy Permit No.",
						"Type",
						"Permit Status",
						"Action",
						"Publication Stage",
						"Reason(s)",
						"Expiration Date",
						"Final Issuance Date",
						"Permit Basis Date",
						"Rescission Date",
						"Description"));

				table.add(headerRow);

				for (Permit permit : preservedPermitList) {

					List<Object> row = new ArrayList<Object>();

					row.add(permit.getPermitNumber());

					row.add(permit.getLegacyPermitNumber());

					row.add(permit.getPermitTypeDsc());

					row.add(PermitLevelStatusDef.getData().getItems().getItemDesc(permit.getPermitLevelStatusCd()));

					row.add(PermitActionTypeDef.getData().getItems().getItemDesc(permit.getActionType()));

					row.add(PermitGlobalStatusDef.getData().getItems().getItemDesc(permit.getPermitGlobalStatusCD()));

					row.add(permit.getPermitReasons());

					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, permit.getExpirationDate()));

					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, permit.getFinalIssueDate()));

					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, permit.getPermitBasisDt()));

					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, permit.getRecissionDate()));

					row.add(truncate(permit.getDescription()));

					table.add(row);
				}

				this.tagValueMap.put("inspectionPermitList", table);
			}
		}
	}
	
	// stack test snapshot from the pre-inspection page
	private void loadStackTestsSnapshotData(final FullComplianceEval fce) throws DAOException {

		if (null != fce) {
			
			List<FceStackTestSearchLineItem> preservedStackTestList = getFullComplianceEvalService()
					.retrieveFceStackTestsPreserved(fce);
			
			if (null != preservedStackTestList) {
				
				List<List<?>> table = new ArrayList<List<?>>();
				
				List<String> headerRow = new ArrayList<String>(Arrays.asList(
						"Stack Test ID",
						"Pollutants Tested",
						"EUs",
						"Stack Test Method",
						"Failed Pollutants",
						"Test Date(s)",
						"Date Received",
						"Date Reviewed",
						"Conformed to Test Method"
					)
				);
				
				table.add(headerRow);
	
				for (FceStackTestSearchLineItem stackTest : preservedStackTestList) {
	
					List<Object> row = new ArrayList<Object>();
	
					row.add(stackTest.getStckId());
	
					row.add(stackTest.getPollutantsTested());
	
					row.add(stackTest.getEus());
	
					row.add(CetaStackTestMethodDef.getData().getItems().getItemDesc(stackTest.getStackTestMethodCd()));
	
					row.add(stackTest.getPollutantsFailed());
	
					row.add(stackTest.getTestDatesString());
	
					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, stackTest.getDateReceived()));
	
					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, stackTest.getDateReviewed()));
	
					String conformedToTestMethod = stackTest.getConformedToTestMethod();
					if (!Utility.isNullOrEmpty(conformedToTestMethod)) {
	
						if ("Y".equalsIgnoreCase(conformedToTestMethod)) {
							conformedToTestMethod = "Yes";
						} else if ("N".equalsIgnoreCase(conformedToTestMethod)) {
							conformedToTestMethod = "No";
						}
					}
					row.add(conformedToTestMethod);
					
	
					table.add(row);
				}
				
				this.tagValueMap.put("inspectionStackTestList", table);
			}
		}
	}
	
	// compliance reports snapshot from the pre-inspection page
	private void loadComplianceReportsSnapshotData(final FullComplianceEval fce) throws DAOException {

		if (null != fce) {
		
			List<ComplianceReportList> preservedComplianceReportList = getFullComplianceEvalService()
					.retrieveFceComplianceReportsPreserved(fce);
	
			if (null != preservedComplianceReportList) {
	
				List<List<?>> table = new ArrayList<List<?>>();
	
				List<String> headerRow = new ArrayList<String>(Arrays.asList(
						"Report ID",
						"Report Type",
						"Category",
						"Description",
						"Received Date",
						"Compliance Status",
						"Accepted",
						"Comments"
					)
				);
	
				table.add(headerRow);
	
				for (ComplianceReportList complianceReport : preservedComplianceReportList) {
	
					List<Object> row = new ArrayList<Object>();
	
					row.add(complianceReport.getReportCRPTId());
	
					row.add(ComplianceReportTypeDef.getData().getItems().getItemDesc(complianceReport.getReportType()));
	
					row.add(complianceReport.getOtherTypeDsc());
	
					row.add(truncate(complianceReport.getDescription()));
	
					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, complianceReport.getReceivedDate()));
					
					row.add(ReportComplianceStatusDef.getData().getItems().getItemDesc(complianceReport.getComplianceStatusCd()));
	
					row.add(ComplianceReportAcceptedDef.getData().getItems().getItemDesc(complianceReport.getAcceptable()));
	
					row.add(truncate(complianceReport.getDapcReviewComments()));
	
					table.add(row);
				}
	
				this.tagValueMap.put("inspectionCompRptList", table);
			}
		}
	}
		
	// correspondence snapshot from the pre-inspection page
	private void loadCorrespondenceSnapshotData(final FullComplianceEval fce) throws DAOException {
		
		if (null != fce) {

			List<Correspondence> preservedCorrespondenceList = getFullComplianceEvalService()
					.retrieveFceCorrespondencesPreserved(fce);
	
			if (null != preservedCorrespondenceList) {
	
				List<List<?>> table = new ArrayList<List<?>>();
	
				List<String> headerRow = new ArrayList<String>(Arrays.asList(
						"Correspondence ID",
						"Correspondence Direction",
						"Correspondence Type",
						"Correspondence Category",
						"Subject",
						"Date Generated",
						"Receipt Date",
						"Additional Info",
						"Attachment Count"
					)
				);
	
				table.add(headerRow);
	
				for (Correspondence correspondence : preservedCorrespondenceList) {
	
					List<Object> row = new ArrayList<Object>();
	
					row.add(correspondence.getCorId());
	
					row.add(CorrespondenceDirectionDef.getData().getItems().getItemDesc(correspondence.getDirectionCd()));
	
					row.add(correspondence.getCorrespondenceTypeDescription());
	
					row.add(CorrespondenceCategoryDef.getData().getItems()
							.getItemDesc(correspondence.getCorrespondenceCategoryCd()));
	
					row.add(correspondence.getRegarding());
	
					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, correspondence.getDateGenerated()));
	
					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, correspondence.getReceiptDate()));
	
					row.add(correspondence.getAdditionalInfo());
	
					row.add(correspondence.getAttachmentCount());
	
					table.add(row);
				}
	
				this.tagValueMap.put("inspectionCorrespondenceList", table);
			}
		}
	}
		
	// emissions inventory snapshot from the pre-inspection page
	private void loadEmissionsInventoriesSnapshotData(final FullComplianceEval fce) throws DAOException {
		
		if (null != fce) {

			List<FceEmissionsInventoryLineItem> preservedEIList = getFullComplianceEvalService()
					.retrieveFceEmissionsInventoriesPreserved(fce);
	
			if (null != preservedEIList) {
	
				List<List<?>> table = new ArrayList<List<?>>();
	
				List<String> headerRow = new ArrayList<String>(Arrays.asList(
						"Inventory ID",
						"Previous Inventory",
						"Facility History ID",
						"Year",
						"Content Type",
						"Regulatory Requirements",
						"Reporting State",
						"Received Date",
						"PM Primary",
						"PM10 Primary",
						"PM2.5 Primary",
						"CO",
						"NOx",
						"SO2",
						"VOC"
					)
				);
				
				table.add(headerRow);
	
				for (FceEmissionsInventoryLineItem emissionsInventory : preservedEIList) {
	
					List<Object> row = new ArrayList<Object>();
	
					row.add(emissionsInventory.getEmissionsInventoryId());
	
					row.add(emissionsInventory.getEmissionsInventoryModifiedId());
	
					row.add(emissionsInventory.getFpId());
	
					row.add(emissionsInventory.getReportYear());
	
					row.add(ContentTypeDef.getData().getItems().getItemDesc(emissionsInventory.getContentTypeCd()));
	
					row.add(emissionsInventory.getRegulatoryRequirementCdsString());
	
					row.add(ReportOfEmissionsStateDef.getData().getItems()
							.getItemDesc(emissionsInventory.getReportingStateCd()));
	
					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, emissionsInventory.getReceivedDate()));
	
					row.add(emissionsInventory.getEmissionsPM());
	
					row.add(emissionsInventory.getEmissionsPM10());
	
					row.add(emissionsInventory.getEmissionsPM25());
	
					row.add(emissionsInventory.getEmissionsCO());
	
					row.add(emissionsInventory.getEmissionsNOx());
	
					row.add(emissionsInventory.getEmissionsSO2());
	
					row.add(emissionsInventory.getEmissionsVOC());
	
					table.add(row);
				}
	
				this.tagValueMap.put("inspectionEIList", table);
			}
		}
	}
	
	// cem/com/cms snapshot from the pre-inspection page
	private void loadContinuousMonitorsSnapshotData(final FullComplianceEval fce) throws DAOException {

		if (null != fce) {
		
			List<FceContinuousMonitorLineItem> preservedMonitorsList = getFullComplianceEvalService()
					.retrieveFceCemComLimitsPreserved(fce);
	
			if (null != preservedMonitorsList) {
	
				List<List<?>> table = new ArrayList<List<?>>();
	
				List<String> headerRow = new ArrayList<String>(Arrays.asList(
						"Limit ID",
						"Monitor ID",
						"Monitor Description",
						"Monitor Install Date",
						"Date of Last Audit",
						"Limit Description",
						"Source of Limit",
						"Start Monitoring Limit",
						"End Monitoring Limit",
						"Additional Information"
					)
				);
				
				table.add(headerRow);
	
				for (FceContinuousMonitorLineItem monitor : preservedMonitorsList) {
	
					List<Object> row = new ArrayList<Object>();
	
					row.add(monitor.getLimId());
	
					row.add(monitor.getMonId());
	
					row.add(monitor.getMonitorDesc());
	
					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, monitor.getMonitorInstallDate()));
					
					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, monitor.getLastAuditDate()));
	
					row.add(monitor.getLimitDesc());
	
					row.add(monitor.getLimitSource());
	
					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, monitor.getLimitStartDate()));
					
					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, monitor.getLimitEndDate()));
	
					row.add(monitor.getAddlInfo());
	
					table.add(row);
				}
	
				this.tagValueMap.put("inspectionCEMCOMCMSList", table);
			}
		}
	}
	
	// ambient monitor snapshot from the pre-inspection page
	private void loadAmbientMonitorsSnapshotData(final FullComplianceEval fce) throws DAOException {

		if (null != fce) {
		
			List<FceAmbientMonitorLineItem> preservedMonitorsList = getFullComplianceEvalService()
					.retrieveFceAmbientMonitorsPreserved(fce);
	
			if (null != preservedMonitorsList) {
	
				List<List<?>> table = new ArrayList<List<?>>();
				
				List<String> headerRow = new ArrayList<String>(Arrays.asList(
						"Site ID",
						"Site Name",
						"AQS Site ID",
						"Site Status",
						"Start Date",
						"End Date",
						"Lat/Long",
						"Monitor ID",
						"Name",
						"Parameter",
						"Monitor Status",
						"Collection Frequency Code"
					)
				);
	
				table.add(headerRow);
	
				for (FceAmbientMonitorLineItem monitor : preservedMonitorsList) {
	
					List<Object> row = new ArrayList<Object>();
	
					row.add(monitor.getMstId());
	
					row.add(monitor.getSiteName());
	
					row.add(monitor.getAqsSiteId());
	
					row.add(MonitorStatusDef.getData().getItems().getItemDesc(monitor.getSiteStatus()));
	
					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, monitor.getStartDate()));
	
					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, monitor.getEndDate()));
	
					row.add(monitor.getLatLon());
	
					row.add(monitor.getMntrId());
	
					row.add(monitor.getName());
	
					row.add(monitor.getParameterDesc());
	
					row.add(MonitorStatusDef.getData().getItems().getItemDesc(monitor.getStatus()));
	
					row.add(MonitoringCollFreqCodeDef.getData().getItems().getItemDesc(monitor.getFrequencyCode()));
	
					table.add(row);
				}
	
				this.tagValueMap.put("inspectionAmbientMonitorList", table);
			}
		}
	}
	
	// site visit snapshot from the pre-inspection page
	private void loadSiteVisitsSnapshotData(final FullComplianceEval fce) throws DAOException {
		
		if (null != fce) {

			List<SiteVisit> preservedSiteVisitList = getFullComplianceEvalService()
					.retrieveFceSiteVisitsPreserved(fce);
	
			if (null != preservedSiteVisitList) {
	
				List<List<?>> table = new ArrayList<List<?>>();
				
				List<String> headerRow = new ArrayList<String>(Arrays.asList(
						"Site Visit ID",
						"Visit Date",
						"Visit Type",
						"Compliance Issue",
						"Memo"
					)
				);
	
				table.add(headerRow);
	
				for (SiteVisit siteVisit : preservedSiteVisitList) {
	
					List<Object> row = new ArrayList<Object>();
	
					row.add(siteVisit.getSiteId());
	
					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, siteVisit.getVisitDate()));
	
					row.add(SiteVisitTypeDef.getData().getItems().getItemDesc(siteVisit.getVisitType()));
	
					String complianceIssued = siteVisit.getComplianceIssued();
					if (!Utility.isNullOrEmpty(complianceIssued)) {
	
						if ("Y".equalsIgnoreCase(complianceIssued)) {
							complianceIssued = "Yes";
						} else if ("N".equalsIgnoreCase(complianceIssued)) {
							complianceIssued = "No";
						}
					}
					row.add(complianceIssued);
	
					row.add(truncate(siteVisit.getMemo()));
	
					table.add(row);
				}
	
				this.tagValueMap.put("inspectionSiteVisitList", table);
			}
		}
	}

	// attachment list
	private void loadAttachmentsData(final FullComplianceEval fce) throws RemoteException {
		
		if (null != fce) {
		
			List<FceAttachment> attachmentList = fce.getAttachments();
	
			if (null != attachmentList) {
	
				List<List<?>> table = new ArrayList<List<?>>();
				
				List<String> headerRow = new ArrayList<String>(Arrays.asList(
						"Attachment ID",
						"Attachment Type",
						"Description",
						"Uploaded By",
						"Uploaded Date"
					)
				);
	
				table.add(headerRow);
	
				for (FceAttachment attachment : attachmentList) {
	
					List<Object> row = new ArrayList<Object>();
	
					row.add(attachment.getDocumentID());
					
					row.add(FceAttachmentTypeDef.getData().getItems().getItemDesc(attachment.getDocTypeCd()));
					
					row.add(attachment.getDescription());
					
					row.add(retrieveUserName(attachment.getLastModifiedBy()));
	
					row.add(getFormattedDate(DEFAULT_DATE_TIME_FORMAT, attachment.getUploadDate()));
	
					table.add(row);
				}
	
				this.tagValueMap.put("inspectionAttachments", table);
			}
		}
	}
	
	// permit condition list
	private void loadPermitConditionsData(final FullComplianceEval fce) throws RemoteException {
		
		if (null != fce && null != fce.getFcePermitConditions()) {
		
			List<FcePermitCondition> permitConditionList = fce.getFcePermitConditions();
	
			if (null != permitConditionList) {
	
				List<List<?>> table = new ArrayList<List<?>>();
				
				List<String> headerRow = new ArrayList<String>(Arrays.asList(
						"Permit Condition ID",
						"Associated Permit ID",
						"Permit Type",
						"Permit Condition Number",
						"Condition Text",
						"Associated EUs",
						"Permit Condition Status",
						"Compliance Status",
						"Comments"
					)
				);
	
				table.add(headerRow);
	
				for (FcePermitCondition permitCondition : permitConditionList) {
	
					List<Object> row = new ArrayList<Object>();
	
					row.add(permitCondition.getPcondId());
					
					row.add(permitCondition.getPermitNbr());
					
					row.add(PermitTypeDef.getData().getItems().getItemDesc(permitCondition.getPermitTypeCd()));
					
					row.add(permitCondition.getPermitConditionNumber());
					
					row.add(truncate(permitCondition.getConditionTextPlain()));
					
					row.add(permitCondition.getAssociatedEUsValue());
					
					row.add(PermitConditionStatusDef.getData().getItems().getItemDesc(permitCondition.getPermitConditionStatusCd()));
					
					ComplianceEvaluationDefs compEvalDefs = (ComplianceEvaluationDefs) FacesUtil.getManagedBean("compEvalDefs");
					row.add(compEvalDefs.getPermitConditionComplianceStatusDef().getItemDesc(permitCondition.getComplianceStatusCd()));
					
					row.add(permitCondition.getComments());
					
					table.add(row);
				}
	
				this.tagValueMap.put("inspectionPermitConditions", table);
			}
		}
	}
	
	private void loadHeadingData() {
		this.tagValueMap.put("inspectionConcernsHeading", "INSPECTION CONCERNS:");
		this.tagValueMap.put("inspectionFileReviewHeading", "FILE REVIEW/RECORDS REQUEST:");
		this.tagValueMap.put("inspectionRegDiscussionHeading", "REGULATORY DISCUSSION:");
		this.tagValueMap.put("inspectionPhysicalHeading", "PHYSICAL INSPECTION OF PLANT:");
		this.tagValueMap.put("inspectionAmbientHeading", "AMBIENT MONITORING:");
		this.tagValueMap.put("inspectionOtherHeading", "OTHER INFORMATION:");
		this.tagValueMap.put("inspectionPermitApplicationListHeading", "APPLICATION LIST:");
		this.tagValueMap.put("inspectionPermitListHeading", "PERMIT LIST:");
		this.tagValueMap.put("inspectionStackTestListHeading", "STACK TEST LIST:");
		this.tagValueMap.put("inspectionCompRptListHeading", "COMPLIANCE REPORT LIST:");
		this.tagValueMap.put("inspectionEIListHeading", "EMISSIONS INVENTORY LIST:");
		this.tagValueMap.put("inspectionCEMCOMCMSListHeading", "CEM/COMS/CMS LIST:");
		this.tagValueMap.put("inspectionAmbientMonitorListHeading", "AMBIENT MONITORS LIST:");
		this.tagValueMap.put("inspectionSiteVisitListHeading", "SITE VISITS LIST:");
		this.tagValueMap.put("inspectionPermitConditionsHeading", "PERMIT CONDITION AND STATUS:");
		this.tagValueMap.put("inspectionAttachmentsHeading", "ATTACHMENTS:");
	}
}
