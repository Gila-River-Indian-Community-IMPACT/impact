package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.def.CetaStackTestResultsDef;
import us.oh.state.epa.stars2.def.PollutantDef;

@SuppressWarnings("serial")
public class StackTestedPollutant extends BaseDB implements Comparable {
	transient private StackTest stackTest; // the stack test this oject belongs
											// to--needed when selection
											// changes.
	private Integer StackTestId;
	private Integer testedEu;
	transient private String epaEmuId;
	transient private EmissionUnit eu;
	private String sccId;
	private String pollutantCd;
	private String permitAllowRate;
	private String permitMaxRate;
	private String testAvgOperRate;
	private String testRate;
	private String stackTestResultsCd; // defined in CetaStackTestResultsDef
	private Timestamp afsSentDate;
	private String afsId;

	transient private String facilityId; // Only used by AFS export of stack
											// Tests
	private transient String scscId; // Only used by AFS export of stack Tests
	transient private String facilityNm; // Only used by AFS export of stack
											// Tests
	transient private Integer fpId; // Only used by AFS export of stack Tests
	transient private Integer versionId; // Only used by AFS export of stack
											// Tests
	transient private Integer reviewerId; // Only used by AFS export of stack
											// Tests
	transient private Integer fceId; // Only used by AFS export of stack Tests
	transient private Timestamp testDate; // Only used by AFS export of stack
											// Tests
	transient private String stackTestMethodCd; // Only used by AFS export of
												// stack Tests
	transient private Timestamp dateEvaluated; // Only used by AFS export of
												// stack Tests
	transient private String afsExportErrors; // Only used by AFS export of
												// stack Tests
	transient private Integer stackTestLastMod; // last modified from the stack
												// test

	transient private String pollutantDsc;
	transient private boolean deprecated;
	transient private boolean changedToFailed; // this is no longer used. It was
												// checked in the JSP to see
												// whether to provide warning
												// text.

	transient private boolean valueUpdated; // used to know if a stack test
											// should be upgraded from reminder
											// to draft.

	public StackTestedPollutant() {
		super();
	}

	public StackTestedPollutant(String pollutantCd, EmissionUnit eu,
			String sccId) {
		super();
		this.eu = eu;
		if (eu != null) {
			testedEu = eu.getCorrEpaEmuId();
			epaEmuId = eu.getEpaEmuId();
		}
		this.sccId = sccId;
		this.pollutantCd = pollutantCd;
		this.permitAllowRate = "";
		this.permitMaxRate = "";
		this.testAvgOperRate = "";
		this.testRate = "";
		this.stackTestResultsCd = null;
		pollutantDsc = PollutantDef.getData().getItems()
				.getDescFromAllItem(pollutantCd);
	}

	public StackTestedPollutant(StackTest st, String pollutantCd) {
		super();
		this.pollutantCd = pollutantCd;
		this.stackTest = st;
		pollutantDsc = PollutantDef.getData().getItems()
				.getDescFromAllItem(pollutantCd);
		this.permitAllowRate = "";
		this.permitMaxRate = "";
		this.testAvgOperRate = "";
		this.testRate = "";
		this.stackTestResultsCd = null;
	}

	public StackTestedPollutant(StackTest st, EmissionUnit eu,
			StackTestedPollutant stp) {
		super();
		this.stackTest = st;
		this.pollutantCd = stp.pollutantCd;
		this.pollutantDsc = PollutantDef.getData().getItems()
				.getDescFromAllItem(stp.pollutantCd);
		this.permitAllowRate = stp.permitAllowRate;
		this.permitMaxRate = stp.permitMaxRate;
		this.testAvgOperRate = stp.testAvgOperRate;
		this.testRate = stp.testRate;
		this.stackTestResultsCd = stp.stackTestResultsCd;
		this.setEu(eu);
		if (this.eu != null) {
			this.testedEu = this.eu.getCorrEpaEmuId();
			this.epaEmuId = this.eu.getEpaEmuId();
		}
		this.testedEu = stp.testedEu;
		this.setSccId(stp.sccId);
		this.afsId = stp.afsId;
		this.afsSentDate = stp.afsSentDate;
	}

	/** Populate this instance from a database ResultSet. */
	public final void populate(java.sql.ResultSet rs) throws SQLException {
		boolean haveRequired = false;
		try {
			setStackTestId(AbstractDAO.getInteger(rs, "stack_test_id"));
			setAfsSentDate(rs.getTimestamp("afs_date"));
			setAfsId(rs.getString("afs_id"));
			setPollutantCd(rs.getString("pollutant_cd"));
			setStackTestResultsCd(rs.getString("stack_test_result_cd"));
			permitAllowRate = rs.getString("permitted_allow_emission_rate");
			permitMaxRate = rs.getString("permitted_max_operating_rate");
			testRate = rs.getString("tested_emission_rate");
			testAvgOperRate = rs.getString("tested_operating_rate");
			testedEu = AbstractDAO.getInteger(rs, "tested_eu");
			sccId = rs.getString("scc_id");
			changedToFailed = false;
			haveRequired = true;
			setFacilityId(rs.getString("facility_id"));
			setScscId(rs.getString("scsc_id"));
			setFpId(AbstractDAO.getInteger(rs, "fp_id"));
			setVersionId(AbstractDAO.getInteger(rs, "version_id"));
			setFacilityNm(rs.getString("facility_nm"));
			setReviewerId(AbstractDAO.getInteger(rs, "reviewer_id"));
			setFceId(AbstractDAO.getInteger(rs, "fce_id"));
			setStackTestMethodCd(rs.getString("stack_test_method_cd"));
			setDateEvaluated(rs.getTimestamp("date_evaluated"));
			setEpaEmuId(rs.getString("epa_emu_id"));
			setTestDate(rs.getTimestamp("test_date"));
			setStackTestLastMod(AbstractDAO.getInteger(rs, "last_modified"));
		} catch (SQLException e) {
			if (!haveRequired) {
				logger.error(e.getMessage(), e);
				throw e;
			}
		}
	}

	public final int compareTo(Object b) {
		StackTestedPollutant rowB = (StackTestedPollutant) b;
		if (epaEmuId == null || rowB.epaEmuId == null)
			return 0;
		int i = epaEmuId.compareTo(rowB.epaEmuId);
		if (i != 0)
			return i;
		if (sccId == null || rowB.sccId == null)
			return 0;
		i = sccId.compareTo(rowB.sccId);
		if (i != 0)
			return i;
		if (pollutantDsc == null || rowB.pollutantDsc == null)
			return 0;
		i = pollutantDsc.compareTo(rowB.pollutantDsc);
		return i;
	}

	public void setSuperSelected(boolean selected) {
		super.setSelected(selected);
	}

	public void setSelected(boolean selected) {
		super.setSelected(selected);
		stackTest.reCalTests();
	}

	public Timestamp getAfsSentDate() {
		return afsSentDate;
	}

	public void setAfsSentDate(Timestamp afsSentDate) {
		this.afsSentDate = afsSentDate;
	}

	public String getPollutantCd() {
		return pollutantCd;
	}

	public void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}

	public Integer getStackTestId() {
		return StackTestId;
	}

	public Integer getId() { // used by some of the jsp pages
								// (firstStackTestColumns)
		return StackTestId;
	}

	public void setStackTestId(Integer stackTestId) {
		StackTestId = stackTestId;
	}

	public String getStackTestResultsCd() {
		return stackTestResultsCd;
	}

	public void setStackTestResultsCd(String stackTestResultsCd) {
		this.stackTestResultsCd = stackTestResultsCd;
		if (stackTestResultsCd != null) {
			valueUpdated = true;
			if (CetaStackTestResultsDef.FAIL.equals(stackTestResultsCd))
				changedToFailed = true;
			else
				changedToFailed = false;
		} else {
			valueUpdated = false;
			changedToFailed = false;
		}
	}

	public String getPollutantDsc() {
		return pollutantDsc;
	}

	public void setPollutantDsc(String pollutantDsc) {
		this.pollutantDsc = pollutantDsc;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	public boolean isValueUpdated() {
		return valueUpdated;
	}

	public String getAfsId() {
		return afsId;
	}

	public void setAfsId(String afsId) {
		this.afsId = afsId;
	}

	public boolean isChangedToFailed() {
		return changedToFailed;
	}

	public void setChangedToFailed(boolean changedToFailed) {
		this.changedToFailed = changedToFailed;
	}

	public String getPermitAllowRate() {
		return permitAllowRate;
	}

	public void setPermitAllowRate(String permitAllowRate) {
		this.permitAllowRate = permitAllowRate;
	}

	public String getPermitMaxRate() {
		return permitMaxRate;
	}

	public void setPermitMaxRate(String permitMaxRate) {
		this.permitMaxRate = permitMaxRate;
	}

	public String getTestAvgOperRate() {
		return testAvgOperRate;
	}

	public void setTestAvgOperRate(String testAvgOperRate) {
		this.testAvgOperRate = testAvgOperRate;
		if (testAvgOperRate != null)
			valueUpdated = true;
		else
			valueUpdated = false;
	}

	public String getTestRate() {
		return testRate;
	}

	public void setTestRate(String testRate) {
		this.testRate = testRate;
		if (testRate != null)
			valueUpdated = true;
		else
			valueUpdated = false;
	}

	public String getSccId() {
		return sccId;
	}

	public void setSccId(String sccId) {
		this.sccId = sccId;
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
		if (eu != null) {
			this.testedEu = eu.getCorrEpaEmuId();
			this.epaEmuId = eu.getEpaEmuId();
		}
	}

	public void setStackTest(StackTest stackTest) {
		this.stackTest = stackTest;
	}

	public String getEpaEmuId() {
		return epaEmuId;
	}

	public void setEpaEmuId(String epaEmuId) {
		this.epaEmuId = epaEmuId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public Integer getFpId() {
		return fpId;
	}

	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}

	public String getFacilityNm() {
		return facilityNm;
	}

	public void setFacilityNm(String facilityNm) {
		this.facilityNm = facilityNm;
	}

	public Integer getReviewerId() {
		return reviewerId;
	}

	public void setReviewerId(Integer reviewerId) {
		this.reviewerId = reviewerId;
	}

	public Integer getFceId() {
		return fceId;
	}

	public void setFceId(Integer fceId) {
		this.fceId = fceId;
	}

	public Integer getVersionId() {
		return versionId;
	}

	public void setVersionId(Integer versionId) {
		this.versionId = versionId;
	}

	public String getStackTestMethodCd() {
		return stackTestMethodCd;
	}

	public void setStackTestMethodCd(String stackTestMethodCd) {
		this.stackTestMethodCd = stackTestMethodCd;
	}

	public Timestamp getDateEvaluated() {
		return dateEvaluated;
	}

	public void setDateEvaluated(Timestamp dateEvaluated) {
		this.dateEvaluated = dateEvaluated;
	}

	public String getAfsExportErrors() {
		return afsExportErrors;
	}

	public void setAfsExportErrors(String afsExportErrors) {
		this.afsExportErrors = afsExportErrors;
	}

	public Timestamp getTestDate() {
		return testDate;
	}

	public void setTestDate(Timestamp testDate) {
		this.testDate = testDate;
	}

	public Integer getStackTestLastMod() {
		return stackTestLastMod;
	}

	public void setStackTestLastMod(Integer stackTestLastMod) {
		this.stackTestLastMod = stackTestLastMod;
	}

	public String getScscId() {
		return scscId;
	}

	public void setScscId(String scscId) {
		this.scscId = scscId;
	}
}
