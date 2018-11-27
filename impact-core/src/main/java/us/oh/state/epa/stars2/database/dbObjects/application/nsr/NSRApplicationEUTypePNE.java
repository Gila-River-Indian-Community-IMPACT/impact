package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

@SuppressWarnings("serial")
public class NSRApplicationEUTypePNE extends NSRApplicationEUType {

	/******** Variables **********/
	private static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypePNE:";
	private static Short MIN_NEW_OR_MODIFIED_EQP_CNT_VAL = (short) 1;
	private static Short MAX_NEW_OR_MODIFIED_EQP_CNT_VAL = (short) 999;

	private Integer applicationEUId;
	private String motiveForceCd;
	private BigDecimal voc;
	private BigDecimal hap;
	private Short newOrModifiedEqpCnt;

	/******** Properties **********/
	public Integer getApplicationEUId() {
		return applicationEUId;
	}

	public void setApplicationEUId(Integer applicationEUId) {
		this.applicationEUId = applicationEUId;
	}

	public String getMotiveForceCd() {
		return motiveForceCd;
	}

	public void setMotiveForceCd(String motiveForceCd) {
		this.motiveForceCd = motiveForceCd;
	}

	public BigDecimal getVoc() {
		return voc;
	}

	public void setVoc(BigDecimal voc) {
		this.voc = voc;
		requiredFieldVoc();
	}

	public BigDecimal getHap() {
		return hap;
	}

	public void setHap(BigDecimal hap) {
		this.hap = hap;
		requiredFieldHap();
	}

	/******** Constructors **********/
	public NSRApplicationEUTypePNE() {
		super();
	}

	public NSRApplicationEUTypePNE(NSRApplicationEUTypePNE old) {
		super(old);
		if (old != null) {
			setMotiveForceCd(old.getMotiveForceCd());
			setVoc(old.getVoc());
			setHap(old.getHap());
			setNewOrModifiedEqpCnt(old.getNewOrModifiedEqpCnt());
		}
	}

	/******** Implement Abstract Class - NSRApplicationEUType **********/
	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);

		setApplicationEUId(AbstractDAO.getInteger(rs, "application_eu_id"));
		setMotiveForceCd(rs.getString("motive_force_cd"));
		setVoc(rs.getBigDecimal("voc"));
		setHap(rs.getBigDecimal("hap"));
		setNewOrModifiedEqpCnt(AbstractDAO.getShort(rs, "new_or_modified_eqp_cnt"));
	}

	@Override
	public final ValidationMessage[] validate() {
		this.clearValidationMessages();
		requiredFields();
		validateRanges();

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	/******** Private Methods **********/
	private void requiredFields() {
		requiredFieldMotiveForce();
		requiredFieldVoc();
		requiredFieldHap();
		requiredFieldNewOrModifiedEqpCnt();
	}

	private void validateRanges() {
		checkRangeValues(this.voc, new BigDecimal(0), new BigDecimal(100),
				PAGE_VIEW_ID_PREFIX + "voc", "VOC Content (%)");

		checkRangeValues(this.hap, new BigDecimal(0), new BigDecimal(100),
				PAGE_VIEW_ID_PREFIX + "hap", "HAP Content (%)");
		
		checkRangeValues(this.newOrModifiedEqpCnt, MIN_NEW_OR_MODIFIED_EQP_CNT_VAL, MAX_NEW_OR_MODIFIED_EQP_CNT_VAL,
				PAGE_VIEW_ID_PREFIX + "newOrModifiedEqpCnt", "Count of New or Modified Equipment");
	}

	private void requiredFieldMotiveForce() {
		requiredField(this.motiveForceCd,
				PAGE_VIEW_ID_PREFIX + "motiveForceCd", "Motive Force",
				"motiveForceCd");
	}

	private void requiredFieldVoc() {
		requiredField(this.voc, PAGE_VIEW_ID_PREFIX + "voc", "VOC Content (%)",
				"voc");
	}

	private void requiredFieldHap() {
		requiredField(this.hap, PAGE_VIEW_ID_PREFIX + "hap", "HAP Content (%)",
				"hap");
	}

	public Short getNewOrModifiedEqpCnt() {
		return newOrModifiedEqpCnt;
	}

	public void setNewOrModifiedEqpCnt(Short newOrModifiedEqpCnt) {
		this.newOrModifiedEqpCnt = newOrModifiedEqpCnt;
		requiredFieldNewOrModifiedEqpCnt();
	}
	
	private void requiredFieldNewOrModifiedEqpCnt() {
		requiredField(this.newOrModifiedEqpCnt,
				PAGE_VIEW_ID_PREFIX + "newOrModifiedEqpCnt", "Count of New or Modified Equipment",
				"newOrModifiedEqpCnt");
	}
	
}