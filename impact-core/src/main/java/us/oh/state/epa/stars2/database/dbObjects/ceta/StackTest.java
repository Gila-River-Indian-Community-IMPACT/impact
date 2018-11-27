package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.permit.ComplianceStatusEvent;
import us.oh.state.epa.stars2.def.CetaStackTestMethodDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EmissionsTestStateDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

@SuppressWarnings("serial")
public class StackTest extends CetaBaseDB {
	protected Integer id;
	protected Integer fceId;
	protected List<TestVisitDate> visitDates; // witnessed dates if witnessed;
												// otherwise test dates
	protected List<Evaluator> witnesses;
	protected String memo;
	private Timestamp dateScheduled;
	private Integer fpId;
	private String company; // company conducting test
	private Timestamp dateReceived;
	private Integer reviewer;
	private Timestamp dateEvaluated; // review date
	protected Timestamp createdDt;
	protected Integer createdById;
	private String stackTestMethodCd;
	private transient List<TestedEmissionsUnit> testedEmissionsUnits;
	private boolean methodChangedToOther;
	private StackTestedPollutant[] testedPollutants;
	private String auditsCd; // audits passed; WHAT IS AUDITS SUBMITTED
	private String categoryCd; // e.g. initial, annual, etc
	private String testingMethodCd; // testing method - e.g. reference method,
									// portable analyzer
	private String emissionTestState;
	private String controlEquipUsed = "";
	private String monitoringEquip = "";
	// private Boolean conformance = false; // conformed to method PROBABLY
	// OBSOLETE
	private boolean forPti;
	private boolean forPtio;
	private boolean forPto;
	private boolean forTv;
	private boolean forNsps;
	private boolean forMact;
	private boolean forBif;
	private boolean forTiv;
	private boolean forFeptio;
	private boolean forOther;
	private boolean legacyFlag;
	private Timestamp reminderDate; // used when stack test is in Reminder state
	List<StAttachment> attachments;
	private String reviewerFullName;
	private String inspId;
	private String cmpId;
	private String companyName;
	private String stckId;
	

	private transient Timestamp firstVisitDate;
	private transient String facilityId;
	private transient String facilityNm;
    private String facilityTypeCd;
    private String permitClassCd;
	private transient Integer versionId;
	private transient String doLaaCd;
	private transient String countyCd;
	// allMethodPollutants is only the pollutant and whether selected--does not
	// include EU or SCC
	private transient StackTestedPollutant[] allMethodPollutants = new StackTestedPollutant[0];
	private transient boolean afsLocked = false;
	private transient String afsPartialLocked = null;
	private transient StringBuffer failedPollutants = new StringBuffer();
	private transient Facility associatedFacility;
	private transient String testedEus; // used only in search datagrids

	transient private String afsExportErrors = null; // not used here but is
														// used by
														// firstStackTestColumns.jsp
														// so needs to exist.

	private boolean testBeingSubmitted = false;
	private transient Timestamp submittedDate;
	private long submittedDateLong;

	private Note[] stackTestNotes;

	private String conformedToTestMethod;

	private TableSorter testedPollutantsWrapper;
	
	private boolean validated;
	
	private String facilityAfsNumber;
	
    private transient List<ComplianceStatusEvent> assocComplianceStatusEvents;
    
    private transient List<String> inspectionsReferencedIn;
	
	/*
	public StackTest(StackTest stackTest) {
		super();

		if (stackTest != null) {
			setId(stackTest.getId());
			setFceId(stackTest.getFceId());
			setVisitDates(stackTest.getVisitDates());
			setWitnesses(stackTest.getWitnesses());
			setMemo(stackTest.getMemo());
			setDateScheduled(stackTest.getDateScheduled());
			setFpId(stackTest.getFpId());
			setCompany(stackTest.getCompany());
			setDateReceived(stackTest.getDateReceived());
			setReviewer(stackTest.getReviewer());
			setDateEvaluated(stackTest.getDateEvaluated());
			setCreatedDt(stackTest.getCreatedDt());
			setCreatedById(stackTest.getCreatedById());
			setStackTestMethodCd(stackTest.getStackTestMethodCd());
			setTestedEmissionsUnits(stackTest.getTestedEmissionsUnits());
			this.methodChangedToOther = false;
			setTestedPollutants(stackTest.getTestedPollutants());
			setAuditsCd(stackTest.getAuditsCd());
			setCategoryCd(stackTest.getCategoryCd());
			setTestingMethodCd(stackTest.getTestingMethodCd());
			setEmissionTestState(stackTest.getEmissionTestState());
			setControlEquipUsed(stackTest.getControlEquipUsed());
			setMonitoringEquip(stackTest.getMonitoringEquip());
	--- Left off here		
			private boolean forPti;
			private boolean forPtio;
			private boolean forPto;
			private boolean forTv;
			private boolean forNsps;
			private boolean forMact;
			private boolean forBif;
			private boolean forTiv;
			private boolean forFeptio;
			private boolean forOther;
			private boolean legacyFlag;
			private Timestamp reminderDate; // used when stack test is in Reminder state
			List<StAttachment> attachments;
			private String reviewerFullName;
			private String inspId;
			private String cmpId;
			private String companyName;
			private String stckId;
			

			private transient Timestamp firstVisitDate;
			private transient String facilityId;
			private transient String facilityNm;
		    private String facilityTypeCd;
		    private String permitClassCd;
			private transient Integer versionId;
			private transient String doLaaCd;
			private transient String countyCd;
			// allMethodPollutants is only the pollutant and whether selected--does not
			// include EU or SCC
			private transient StackTestedPollutant[] allMethodPollutants = new StackTestedPollutant[0];
			private transient boolean afsLocked = false;
			private transient String afsPartialLocked = null;
			private transient StringBuffer failedPollutants = new StringBuffer();
			private transient Facility associatedFacility;
			private transient String testedEus; // used only in search datagrids

			transient private String afsExportErrors = null; // not used here but is
																// used by
																// firstStackTestColumns.jsp
																// so needs to exist.

			private boolean testBeingSubmitted = false;
			private transient Timestamp submittedDate;
			private long submittedDateLong;

			private Note[] stackTestNotes;

			private String conformedToTestMethod;

			private TableSorter testedPollutantsWrapper;
			
			setValidated(stackTest.getValidated());
			
		}
		
	}
	*/

	public StackTest() {
		super();
		emissionTestState = EmissionsTestStateDef.DRAFT;
		testedPollutants = new StackTestedPollutant[0];
		visitDates = new ArrayList<TestVisitDate>();
		witnesses = new ArrayList<Evaluator>();
		testedEmissionsUnits = new ArrayList<TestedEmissionsUnit>();
		testedPollutantsWrapper = new TableSorter();
		assocComplianceStatusEvents = new ArrayList<ComplianceStatusEvent>();
	}

	// public StackTestedPollutant getFirstPollutant() {
	// StackTestedPollutant stp = null;
	// if(testedPollutants != null && testedPollutants.length > 0) stp =
	// testedPollutants[0];
	// if(stp == null) stp = new StackTestedPollutant();
	// return stp;
	// }

	public boolean allSameSccs() {
		if (getTestedEmissionsUnits().size() == 0)
			return false;
		String sccs = getFirstSccs();
		if (sccs.length() == 0)
			return false;
		for (TestedEmissionsUnit teu : getTestedEmissionsUnits()) {
			if (!teu.getSccs().equals(sccs))
				return false;
		}
		return true;
	}

	public List<TestedEmissionsUnit> locateTested(EmissionUnit eu) {
		ArrayList<TestedEmissionsUnit> list = null;
		for (TestedEmissionsUnit teu : testedEmissionsUnits) {
			if (teu.getEpaEmuId().equals(eu.getEpaEmuId())) {
				if (list == null) {
					list = new ArrayList<TestedEmissionsUnit>();
				}
				list.add(teu);
			}
		}

		return list;
	}

	public String getFirstSccs() {
		String sccs = "";
		if (getTestedEmissionsUnits().size() > 0) {
			sccs = testedEmissionsUnits.get(0).getSccs();
		}
		return sccs;
	}

	public void clearForNewReminder() {
		dateScheduled = null;
		// company = null;
		dateReceived = null;
		// reviewer = null;
		dateEvaluated = null;
		// stackTestResultsCd = null;;
		// testAvgOperRate = null;
		// testRate = null;
		auditsCd = null;
		categoryCd = null;
		testingMethodCd = null;
		// controlEquipUsed = "";
		// monitoringEquip = "";
		// conformance = false;
		conformedToTestMethod = null;
		// afsSentDate = null;
		// afsId = null;
		visitDates = new ArrayList<TestVisitDate>();
		witnesses = new ArrayList<Evaluator>();
		for (StackTestedPollutant stp : testedPollutants) {
			stp.setStackTestResultsCd(null);
			stp.setAfsId(null);
			stp.setAfsSentDate(null);
		}
		testedPollutantsWrapper = new TableSorter();
		testedPollutantsWrapper.setWrappedData(testedPollutants);
		reCalTests();
	}

	public void clearFieldsAfterClone() {
		id = null;
		// this is to remove from object
		for (StackTestedPollutant stp : testedPollutants) {
			stp.setStackTestResultsCd(null);
			stp.setAfsId(null);
			stp.setAfsSentDate(null);
		}
		testedPollutantsWrapper = new TableSorter();
		testedPollutantsWrapper.setWrappedData(testedPollutants);
		// this is to remove from display
		for (StackTestedPollutant stp : allMethodPollutants) {
			stp.setStackTestResultsCd(null);
			stp.setAfsId(null);
			stp.setAfsSentDate(null);
		}
		reCalTests();
	}

	public void populate(ResultSet rs) throws SQLException {
		try {
			methodChangedToOther = false;
			// NOTE That the set operations have extra functionality not wanted
			// here.
			id = AbstractDAO.getInteger(rs, "stack_test_id");
			fceId = AbstractDAO.getInteger(rs, "fce_id");
			fpId = AbstractDAO.getInteger(rs, "fp_id");
			reviewer = AbstractDAO.getInteger(rs, "reviewer_id");
			dateReceived = rs.getTimestamp("date_received");
			dateEvaluated = rs.getTimestamp("date_evaluated");
			dateScheduled = rs.getTimestamp("date_scheduled");
			controlEquipUsed = rs.getString("control_equip_used");
			if (controlEquipUsed == null)
				controlEquipUsed = "";
			monitoringEquip = rs.getString("monitoring_equip");
			if (monitoringEquip == null)
				monitoringEquip = "";
			memo = rs.getString("memo");
			company = rs.getString("company");
			reminderDate = rs.getTimestamp("reminder_date");
			stackTestMethodCd = rs.getString("stack_test_method_cd");
			auditsCd = rs.getString("audit_result_cd");
			categoryCd = rs.getString("category_cd");
			testingMethodCd = rs.getString("testing_method_cd");
			emissionTestState = rs.getString("emission_test_state");
			// conformance = AbstractDAO.translateIndicatorToBoolean(rs
			// .getString("conformed"));
			conformedToTestMethod = rs.getString("conformed");
			legacyFlag = AbstractDAO.translateIndicatorToBoolean(rs
					.getString("legacy_flag"));
			forPti = AbstractDAO.translateIndicatorToBoolean(rs
					.getString("for_pti"));
			forPtio = AbstractDAO.translateIndicatorToBoolean(rs
					.getString("for_ptio"));
			forPto = AbstractDAO.translateIndicatorToBoolean(rs
					.getString("for_pto"));
			forTv = AbstractDAO.translateIndicatorToBoolean(rs
					.getString("for_tv"));
			forFeptio = AbstractDAO.translateIndicatorToBoolean(rs
					.getString("for_feptio"));
			forNsps = AbstractDAO.translateIndicatorToBoolean(rs
					.getString("for_nsps"));
			forMact = AbstractDAO.translateIndicatorToBoolean(rs
					.getString("for_mact"));
			forBif = AbstractDAO.translateIndicatorToBoolean(rs
					.getString("for_bif"));
			forTiv = AbstractDAO.translateIndicatorToBoolean(rs
					.getString("for_tiv"));
			forOther = AbstractDAO.translateIndicatorToBoolean(rs
					.getString("for_other"));
			setCreatedDt(rs.getTimestamp("created_dt"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
			facilityId = rs.getString("facility_id");
			facilityNm = rs.getString("facility_nm");
			setPermitClassCd(rs.getString("permit_classification_cd"));
            setFacilityTypeCd(rs.getString("facility_type_cd"));
			doLaaCd = rs.getString("do_laa_cd");
			versionId = AbstractDAO.getInteger(rs, "version_id");
			reviewerFullName = WordUtils.capitalize(rs
					.getString("reviewer_full_name"));
			inspId = rs.getString("insp_id");
			cmpId = rs.getString("cmp_id");
			companyName = rs.getString("company_nm");
			stckId = rs.getString("stck_id"); // e.g. STCK000001, STCK000002,
												// etc where last six digits =
												// value of id =
												// AbstractDAO.getInteger(rs,
												// "stack_test_id");
			String inactive = rs.getString("reviewer_full_name_inact");
			if (", ".equals(reviewerFullName))
				reviewerFullName = "";
			else {
				if ("N".equals(inactive)) {
					reviewerFullName = reviewerFullName
							+ DefSelectItems.INACTIVE;
				}
			}
			try {
				countyCd = rs.getString("county_cd");
			} catch (SQLException e) {
				;
			}

			setSubmittedDate(rs.getTimestamp("submitted_date"));
			setValidated(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("validated_flag")));
			createdById = AbstractDAO.getInteger(rs, "created_by_id");
			setFacilityAfsNumber(rs.getString("facility_afs_num"));
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			newObject = false;
		}
	}

	public void setAfsLockedTrue() {
		afsLocked = true;
	}

	public void setAfsLocked() {
		boolean unlocked = false;
		for (StackTestedPollutant stp : testedPollutants) {
			if (stp.getAfsId() != null) {
				afsLocked = true;
			} else
				unlocked = true;
		}
		afsPartialLocked = null;
		if (afsLocked && unlocked)
			afsPartialLocked = " partial";
	}

	public final ValidationMessage[] validateCorrectValues() {
		validationMessages.clear();
		int i = 0;
		for (TestVisitDate tvd : visitDates) {
			if (tvd.getTestDate() == null) {
				validationMessages.put("blankDate", new ValidationMessage(
						"stDateTable", "Some Test Dates are blank.",
						ValidationMessage.Severity.ERROR, null));
			} else {
				if (dateReceived != null) {
					if (dateReceived.after(tvd.getTestDate())) {
						validationMessages
								.put("receiveDate" + i++,
										new ValidationMessage(
												"stDateTable",
												"Test Date "
														+ getDateStr(tvd
																.getTestDate())
														+ " is after the Date Receive",
												ValidationMessage.Severity.ERROR,
												null));
					}
				}
			}
		}
		if (dateEvaluated != null && dateReceived != null) {
			if (dateEvaluated.before(dateReceived)) {
				validationMessages.put("reviewDate", new ValidationMessage(
						"stDateTable", "Received Date "
								+ getDateStr(dateReceived)
								+ " is after the Date Reviewed",
						ValidationMessage.Severity.ERROR, null));
			}
		}
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public final ValidationMessage[] validate(ValidationMessage vm) {
		validationMessages.clear();
		if (vm != null) {
			validationMessages.put("sccInacive", vm);
		}
		
		if (visitDates.size() == 0) {
			validationMessages.put("testDates", new ValidationMessage(
					"stDateTable", "There are no Test Dates.",
					ValidationMessage.Severity.ERROR, null));
		} else {
			for (TestVisitDate tvd : visitDates) {
				if (tvd.getTestDate() == null) {
					validationMessages.put("testDates", new ValidationMessage(
							"stDateTable", "Some Test Dates are blank.",
							ValidationMessage.Severity.ERROR, null));
				}
			}
		}
		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			requiredField(dateReceived, "dtRec", "Date Received");
		}
		
		// Do not require the remaining field values if this is a legacy stack test.
		if (!this.legacyFlag) {
			requiredField(company, "companyId", "Company Conducting Test");
			for (TestedEmissionsUnit teu : testedEmissionsUnits) {
				requiredField(teu.getSccs(), "teuSccs" + teu.getEpaEmuId(),
						"SCC IDs for EU " + teu.getEpaEmuId());
			}
			if (testedPollutants.length == 0) {
				validationMessages.put("pollutant", new ValidationMessage(
						"pollTab2", "No Pollutants were tested",
						ValidationMessage.Severity.ERROR, null));
			}

			requiredField(stackTestMethodCd, "stackTestMethodChoice",
					"Stack Test Method");
			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				requiredField(dateScheduled, "schedDate", "Scheduled Date");
				if (dateScheduled == null) {
					validationMessages
							.put("schedDate",
									new ValidationMessage(
											"schedDate",
											"Attribute "
													+ "Scheduled Date"
													+ " is not set but is optional.  Please provide if available.",
											ValidationMessage.Severity.INFO,
											null));
				}
			}
			requiredField(auditsCd, "stackTestAuditsChoice", "Audits");
			requiredField(categoryCd, "stackTestCategoryChoice", "Category");
			requiredField(testingMethodCd, "stackTestTestingMethodChoice",
					"Testing Method");
			// requiredField(controlEquipUsed, "noteTxt3",
			// "Control Equipment Used");
			// requiredField(monitoringEquip, "noteTxt2",
			// "Monitoring Equipment Used");
			// requiredField(memo, "noteTxt", "Memo");
			// requiredField(conformance, "conformanceTxt",
			// "Conformed to Test Method");
			requiredField(conformedToTestMethod, "conformanceTxt",
					"Conformed to Test Method");
			for (StackTestedPollutant stp : testedPollutants) {
				requiredField(
						stp.getPermitAllowRate(),
						"paeRate" + stp.getEpaEmuId() + stp.getPollutantCd(),
						"Permitted Allowable Emission Rate for "
								+ stp.getEpaEmuId() + " and pollutant "
								+ stp.getPollutantCd() + " ("
								+ stp.getPollutantDsc() + ")");
				// requiredField(stp.getPermitMaxRate(), "pmoRate" +
				// stp.getEpaEmuId() + stp.getPollutantCd(),
				// "Permitted Maximum Operating Rate for " + stp.getEpaEmuId() +
				// " and pollutant " + stp.getPollutantCd() +" (" +
				// stp.getPollutantDsc() + ")");
				// requiredField(stp.getTestAvgOperRate(), "atoRate" +
				// stp.getEpaEmuId() + stp.getPollutantCd(),
				// "Average Tested Operating Rate for " + stp.getEpaEmuId() +
				// " and pollutant " + stp.getPollutantCd() +" (" +
				// stp.getPollutantDsc() + ")");
				requiredField(stp.getTestRate(), "ateRate" + stp.getEpaEmuId()
						+ stp.getPollutantCd(),
						"Average Tested Emission Rate for " + stp.getEpaEmuId()
								+ " and pollutant " + stp.getPollutantCd()
								+ " (" + stp.getPollutantDsc() + ")");
				requiredField(
						stp.getStackTestResultsCd(),
						"stackTestResultsChoice" + stp.getEpaEmuId()
								+ stp.getPollutantCd(),
						"Test Results for " + stp.getEpaEmuId()
								+ " and pollutant " + stp.getPollutantCd()
								+ " (" + stp.getPollutantDsc() + ")");
			}

			StringBuffer vs = new StringBuffer("");
			if (memo == null || memo.length() == 0) {
				if (stackTestMethodCd != null
						&& stackTestMethodCd
								.equals(CetaStackTestMethodDef.METHODOTHER)) {
					vs.append("The Stack Test Method other is specified, please make a note in the Memo field.");
				}
				if (vs.length() > 0) {
					validationMessages.put("memo", new ValidationMessage(
							"noteTxt", vs.toString(),
							ValidationMessage.Severity.ERROR, null));
				}
			}
		}
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public void reCalTests() {
		// Go through testedEmissions Units and keep the tested pollutants we
		// already have if still applicable
		// and add additional rows as needed.
		// Sort when done.
		HashSet<String> polls = new HashSet<String>();
		ArrayList<StackTestedPollutant> newList = new ArrayList<StackTestedPollutant>();
		for (TestedEmissionsUnit teu : testedEmissionsUnits) {
			// Get a list of selected pollutants.
			polls = getMethPolls();
			// go through existing tests and keep those that still apply,
			// removing the pollutant code from polls as you go.
			int i = 0;
			
				for (StackTestedPollutant stp : testedPollutants) {
					if (stp.getEpaEmuId().equals(teu.getEpaEmuId())) {
						if (stp.getSccId() == null
								|| stp.getSccId() == ""
								|| (stp.getSccId() != null && stp.getSccId()
										.equals(teu.getSccs()))) {
	
							// If pollutant is checked in the Method Pollutant
							// Choices,
							// keep the existing record.
							if (polls.contains(stp.getPollutantCd())) {
								polls.remove(stp.getPollutantCd());
								// keep the test record and replace with current
								// SCCs in
								// case they have changed
	
								StackTestedPollutant newStp = new StackTestedPollutant(
										this, stp.getEu(), stp);
								newStp.setSccId(teu.getSccs());
								newList.add(newStp);
							}
	
						}
					}
				}
			
			// add additional test records for missing pollutants.
			for (String s : polls) {
				StackTestedPollutant newStp = new StackTestedPollutant(s,
						teu.getEu(), teu.getSccs());
				newStp.setStackTest(this);
				newList.add(newStp);
			}
		}
		// DO NOT SORT - unless we resolve the issue with test results grid.
		// Sorting either EU grid or test
		// results grid caused anolmalies with the test result data.
		// Collections.sort(newList);
		testedPollutants = newList.toArray(new StackTestedPollutant[0]);
		testedPollutantsWrapper = new TableSorter();
		testedPollutantsWrapper.setWrappedData(testedPollutants);

	}

	public String getHangingAfsIds() {
		StringBuffer sb = new StringBuffer();
		for (StackTestedPollutant stp : testedPollutants) {
			if (stp.getAfsId() != null && stp.getAfsSentDate() == null) {
				if (sb.length() > 0)
					sb.append(", ");
				sb.append(stp.getAfsId());
			}
		}
		return sb.toString();
	}

	private HashSet<String> getMethPolls() {
		HashSet<String> polls = new HashSet<String>();
		for (StackTestedPollutant pol : allMethodPollutants) {
			if (!pol.isSelected())
				continue;
			polls.add(pol.getPollutantCd());
		}
		return polls;
	}

	private void upgradeToDraft(Object o) {
		if (o != null
				&& EmissionsTestStateDef.REMINDER.equals(emissionTestState)) {
			emissionTestState = EmissionsTestStateDef.DRAFT;
		}
	}

	public void upgradeToDraftBasedOnPollutants() {
		for (StackTestedPollutant stp : testedPollutants) {
			if (stp.isValueUpdated()
					&& EmissionsTestStateDef.REMINDER.equals(emissionTestState)) {
				emissionTestState = EmissionsTestStateDef.DRAFT;
			}
		}
	}

	public String getListFailedPolls() {
		if (failedPollutants.length() == 0)
			return null;
		return failedPollutants.toString();
	}

	public final Integer getReviewer() {
		return reviewer;
	}

	public final void setReviewer(Integer reviewer) {
		this.reviewer = reviewer;
		upgradeToDraft(reviewer);
	}

	public final Timestamp getDateEvaluated() {
		return dateEvaluated;
	}

	public final void setDateEvaluated(Timestamp dateEvaluated) {
		checkDirty("drr", this.getStckId(), this.dateEvaluated, dateEvaluated);
		this.dateEvaluated = dateEvaluated;
		if(this.reviewer == null) {
			setReviewer(InfrastructureDefs.getCurrentUserId());
		}
		upgradeToDraft(dateEvaluated);
	}

	public final String getStackTestMethodCd() {
		return stackTestMethodCd;
	}

	public final void setStackTestMethodCd(String stackTestMethodCd) {
		this.stackTestMethodCd = stackTestMethodCd;
		if (CetaStackTestMethodDef.METHODOTHER.equals(stackTestMethodCd))
			methodChangedToOther = true;
		else
			methodChangedToOther = false;
		upgradeToDraft(stackTestMethodCd);
	}

	// public Boolean getConformance() {
	// return conformance;
	// }

	// public void setConformance(Boolean conformance) {
	// this.conformance = conformance;
	// upgradeToDraft(conformance);
	// }

	public String getAuditsCd() {
		return auditsCd;
	}

	public void setAuditsCd(String auditCd) {
		this.auditsCd = auditCd;
		upgradeToDraft(auditCd);
	}

	public String getCategoryCd() {
		return categoryCd;
	}

	public void setCategoryCd(String categoryCd) {
		this.categoryCd = categoryCd;
		upgradeToDraft(categoryCd);
	}

	public String getTestingMethodCd() {
		return testingMethodCd;
	}

	public void setTestingMethodCd(String testingMethodCd) {
		this.testingMethodCd = testingMethodCd;
		upgradeToDraft(testingMethodCd);
	}

	public Timestamp getDateReceived() {
		return dateReceived;
	}

	public void setDateReceived(Timestamp dateReceived) {
		this.dateReceived = dateReceived;
		upgradeToDraft(dateReceived);
	}

	public Timestamp getDateScheduled() {
		return dateScheduled;
	}

	public void setDateScheduled(Timestamp dateScheduled) {
		this.dateScheduled = dateScheduled;
	}

	public String getControlEquipUsed() {
		return controlEquipUsed;
	}

	public void setControlEquipUsed(String controlEquipUsed) {
		this.controlEquipUsed = trimString(controlEquipUsed);
		upgradeToDraft(this.controlEquipUsed);
	}

	public String getMonitoringEquip() {
		return monitoringEquip;
	}

	public void setMonitoringEquip(String monitoringEquip) {
		this.monitoringEquip = trimString(monitoringEquip);
		upgradeToDraft(this.monitoringEquip);
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = trimString(company);
		upgradeToDraft(this.company);
	}

	public boolean isForBif() {
		return forBif;
	}

	public void setForBif(boolean forBif) {
		this.forBif = forBif;
	}

	public boolean isForMact() {
		return forMact;
	}

	public void setForMact(boolean forMact) {
		this.forMact = forMact;
	}

	public boolean isForNsps() {
		return forNsps;
	}

	public void setForNsps(boolean forNsps) {
		this.forNsps = forNsps;
	}

	public boolean isForOther() {
		return forOther;
	}

	public void setForOther(boolean forOther) {
		this.forOther = forOther;
	}

	public boolean isForPti() {
		return forPti;
	}

	public void setForPti(boolean forPti) {
		this.forPti = forPti;
	}

	public boolean isForPto() {
		return forPto;
	}

	public void setForPto(boolean forPto) {
		this.forPto = forPto;
	}

	public boolean isForTiv() {
		return forTiv;
	}

	public void setForTiv(boolean forTiv) {
		this.forTiv = forTiv;
	}

	public boolean isForTv() {
		return forTv;
	}

	public void setForTv(boolean forTv) {
		this.forTv = forTv;
	}

	public boolean isForPtio() {
		return forPtio;
	}

	public void setForPtio(boolean forPtio) {
		this.forPtio = forPtio;
	}

	public Integer getFpId() {
		return fpId;
	}

	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}

	public boolean isLegacyFlag() {
		return legacyFlag;
	}

	public void setLegacyFlag(boolean legacyFlag) {
		this.legacyFlag = legacyFlag;
	}

	public String getEmissionTestState() {
		return emissionTestState;
	}

	public void setEmissionTestState(String emissionTestState) {
		this.emissionTestState = emissionTestState;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getFacilityNm() {
		return facilityNm;
	}

	public void setFacilityNm(String facilityNm) {
		this.facilityNm = facilityNm;
	}
	
	public String getDoLaaCd() {
		return doLaaCd;
	}

	public void setDoLaaCd(String doLaaCd) {
		this.doLaaCd = doLaaCd;
	}

	public String getCountyCd() {
		return countyCd;
	}

	public void setCountyCd(String countyCd) {
		this.countyCd = countyCd;
	}

	public Timestamp getReminderDate() {
		return reminderDate;
	}

	public void setReminderDate(Timestamp reminderDate) {
		this.reminderDate = reminderDate;
	}

	public String getMemo() {
		return memo;
	}

	public String getShortMemo() {
		String rtn = memo;
		if (memo != null && memo.length() > 75) {
			rtn = memo.substring(0, 75) + "...";
		}
		return rtn;
	}

	public void setMemo(String memo) {
		this.memo = trimString(memo);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getFceId() {
		return fceId;
	}

	public void setFceId(Integer fceId) {
		this.fceId = fceId;
	}

	public List<Evaluator> getWitnesses() {
		return witnesses;
	}

	public String getDatesStrings() {
		return getDatesStrings(visitDates);
	}

	public void addEvaluator() {
		if (witnesses == null) {
			witnesses = new ArrayList<Evaluator>();
		}
		witnesses.add(new Evaluator());
		upgradeToDraft("nonNull");
	}

	public void addTestDate() {
		if (visitDates == null) {
			visitDates = new ArrayList<TestVisitDate>();
		}
		visitDates.add(new TestVisitDate());
		upgradeToDraft("nonNull");
	}

	public boolean isWitnessesExist() {
		return witnesses != null && witnesses.size() != 0;
	}

	public void setWitnessesExist(boolean witnessesExist) {
		if (witnessesExist && (witnesses == null || witnesses.size() == 0)) {
			witnesses = new ArrayList<Evaluator>();
			Evaluator e = new Evaluator(InfrastructureDefs.getCurrentUserId());
			witnesses.add(e);
		}
	}

	public List<TestVisitDate> getVisitDates() {
		return visitDates;
	}

	public void setVisitDates(List<TestVisitDate> visitDates) {
		this.visitDates = visitDates;
	}

	public void setWitnesses(List<Evaluator> witnesses) {
		this.witnesses = witnesses;
	}

	public Timestamp getFirstVisitDate() {
		return firstVisitDate;
	}

	public void setFirstVisitDate(Timestamp firstVisitDate) {
		this.firstVisitDate = firstVisitDate;
	}

	public List<StAttachment> getAttachments() {
		if (attachments == null)
			return new ArrayList<StAttachment>();
		return attachments;
	}

	public boolean isHasAttachments() {
		return getAttachments().size() > 0;
	}

	public void setAttachments(List<StAttachment> attachments) {
		this.attachments = attachments;
	}

	public String getReviewerFullName() {
		return reviewerFullName;
	}

	public void setReviewerFullName(String reviewerFullName) {
		this.reviewerFullName = reviewerFullName;
	}

	public StackTestedPollutant[] getTestedPollutants() {
		return testedPollutants;
	}

	public void setTestedPollutants(StackTestedPollutant[] testedPollutants) {
		this.testedPollutants = testedPollutants;
	}

	public StackTestedPollutant[] getAllMethodPollutants() {
		return allMethodPollutants;
	}

	public void setAllMethodPollutants(
			StackTestedPollutant[] allMethodPollutants) {
		this.allMethodPollutants = allMethodPollutants;
	}

	public boolean isAfsLocked() {
		return afsLocked;
	}

	public String getAfsPartialLocked() {
		return afsPartialLocked;
	}

	public void setAfsPartialLocked(String afsPartialLocked) {
		this.afsPartialLocked = afsPartialLocked;
	}

	public StringBuffer getFailedPollutants() {
		return failedPollutants;
	}

	public void setFailedPollutants(StringBuffer failedPollutants) {
		this.failedPollutants = failedPollutants;
	}

	public Integer getVersionId() {
		return versionId;
	}

	public void setVersionId(Integer versionId) {
		this.versionId = versionId;
	}

	public Integer getCreatedById() {
		return createdById;
	}

	public void setCreatedById(Integer createdById) {
		this.createdById = createdById;
	}

	public Timestamp getCreatedDt() {
		return createdDt;
	}

	public void setCreatedDt(Timestamp createdDt) {
		this.createdDt = createdDt;
	}

	public boolean isMethodChangedToOther() {
		return methodChangedToOther;
	}

	public boolean isForFeptio() {
		return forFeptio;
	}

	public void setForFeptio(boolean forFeptio) {
		this.forFeptio = forFeptio;
	}

	public boolean isMoreThanOneEu() {
		if (testedEmissionsUnits != null && testedEmissionsUnits.size() > 1)
			return true;
		return false;
	}

	public boolean addTestedEmissionsUnit(TestedEmissionsUnit teu) {
		// return true if not alreay included
		if (testedEmissionsUnits == null) {
			testedEmissionsUnits = new ArrayList<TestedEmissionsUnit>();
		}
		boolean notAlreadyPresent = true;
		for (TestedEmissionsUnit u : testedEmissionsUnits) {
			if (u.getTestedEu().equals(teu.getTestedEu())
					&& u.getSccs().equals(teu.getSccs())) {
				notAlreadyPresent = false;
			}
		}
		if (notAlreadyPresent) {
			testedEmissionsUnits.add(teu);
		}
		// DO NOT SORT - unless we resolve the issue with test results grid.
		// Sorting either EU grid or test
		// results grid caused anolmalies with the test result data.
		// Collections.sort(testedEmissionsUnits);
		return notAlreadyPresent;
	}

	public List<TestedEmissionsUnit> getTestedEmissionsUnits() {
		if (testedEmissionsUnits == null)
			testedEmissionsUnits = new ArrayList<TestedEmissionsUnit>();
		return testedEmissionsUnits;
	}

	public void setTestedEmissionsUnits(
			List<TestedEmissionsUnit> testedEmissionsUnits) {
		this.testedEmissionsUnits = testedEmissionsUnits;
	}

	public Facility getAssociatedFacility() {
		return associatedFacility;
	}

	public void setAssociatedFacility(Facility associatedFacility) {
		this.associatedFacility = associatedFacility;
	}

	public String getTestedEus() {
		return testedEus;
	}

	public void setTestedEus(String testedEus) {
		this.testedEus = testedEus;
	}

	public String getAfsExportErrors() {
		return afsExportErrors;
	}

	public String getInspId() {
		return inspId;
	}

	public void setInspId(String inspId) {
		this.inspId = inspId;
	}

	public String getCmpId() {
		return cmpId;
	}

	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getStckId() {
		// NOTE: if this method definition is changed, please ensure that the new definition
		// does not break the setReportId method in the WorkflowSearch class
		String tempStckId = null;
		if (!Utility.isNullOrZero(id)) {
			String format = "STCK" + "%06d";
			try {
				tempStckId = String.format(format, id);
			} catch (NumberFormatException nfe) {
			}
		}
		return tempStckId;

	}

	public void setStckId(String stckId) {
		this.stckId = stckId;
	}

	public boolean isTestBeingSubmitted() {
		return testBeingSubmitted;
	}

	public void setTestBeingSubmitted(boolean testBeingSubmitted) {
		this.testBeingSubmitted = testBeingSubmitted;
	}

	public final Timestamp getSubmittedDate() {
		return submittedDate;
	}

	public final void setSubmittedDate(Timestamp submittedDate) {
		this.submittedDate = submittedDate;
		if (this.submittedDate != null) {
			this.setSubmittedDateLong(this.submittedDate.getTime());
		} else {
			this.setSubmittedDateLong(0);
		}
	}

	public long getSubmittedDateLong() {
		long ret = 0;
		if (submittedDate != null) {
			ret = submittedDate.getTime();
		}

		return ret;
	}

	public void setSubmittedDateLong(long submittedDateLong) {
		submittedDate = null;
		if (submittedDateLong > 0) {
			submittedDate = new Timestamp(submittedDateLong);
		}
	}

	public Note[] getStackTestNotes() {
		return stackTestNotes;
	}

	public void setStackTestNotes(Note[] stackTestNotes) {
		this.stackTestNotes = stackTestNotes;
		setDirty(true);
	}

	public String getConformedToTestMethod() {
		return conformedToTestMethod;
	}

	public void setConformedToTestMethod(String conformedToTestMethod) {
		this.conformedToTestMethod = conformedToTestMethod;
		upgradeToDraft(conformedToTestMethod);
	}

	public final TableSorter getTestedPollutantsWrapper() {
		return testedPollutantsWrapper;
	}

	public final void setTestedPollutantsWrapper(
			TableSorter testedPollutantsWrapper) {
		this.testedPollutantsWrapper = testedPollutantsWrapper;
	}

	public String getFacilityTypeCd() {
		return facilityTypeCd;
	}

	public void setFacilityTypeCd(String facilityTypeCd) {
		this.facilityTypeCd = facilityTypeCd;
	}

	public String getPermitClassCd() {
		return permitClassCd;
	}

	public void setPermitClassCd(String permitClassCd) {
		this.permitClassCd = permitClassCd;
	}
	
	public final boolean getValidated() {
		return validated;
	}

	public final void setValidated(boolean validated) {
		this.validated = validated;
	}
	
	public String getFacilityAfsNumber() {
		return facilityAfsNumber;
	}

	public void setFacilityAfsNumber(String facilityAfsNumber) {
		this.facilityAfsNumber = facilityAfsNumber;
	}

	public List<ComplianceStatusEvent> getAssocComplianceStatusEvents() {
		if(assocComplianceStatusEvents == null) return new ArrayList<ComplianceStatusEvent>();
		return assocComplianceStatusEvents;
	}

	public void setAssocComplianceStatusEvents(List<ComplianceStatusEvent> assocComplianceStatusEvents) {
		this.assocComplianceStatusEvents = assocComplianceStatusEvents;
	}

	public List<String> getInspectionsReferencedIn() {
		if(inspectionsReferencedIn == null) return new ArrayList<String>();
		return inspectionsReferencedIn;
	}

	public void setInspectionsReferencedIn(List<String> inspectionsReferencedIn) {
		this.inspectionsReferencedIn = inspectionsReferencedIn;
	}

}
