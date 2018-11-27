package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.lang.math.NumberUtils;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.webcommon.facility.HCAnalysisSampleDetailRow;

public class HydrocarbonAnalysisSampleDetail extends BaseDB{
	
	private Integer fpId;
	// Variables for Sample Hydrocarbon Analysis (Table-2).
	private String sampleFacilityNameGas;
	private String sampleFacilityNameOil;
	private String sampleFacilityNameWater;

	private String sampleFacilityAPIGas;
	private String sampleFacilityAPIOil;
	private String sampleFacilityAPIWater;

	private String sampleFacilityProducingFieldGas;
	private String sampleFacilityProducingFieldOil;
	private String sampleFacilityProducingFieldWater;

	private String sampleFacilityProducingFormationGas;
	private String sampleFacilityProducingFormationOil;
	private String sampleFacilityProducingFormationWater;

	private Timestamp sampleDateGas;
	private Timestamp sampleDateOil;
	private Timestamp sampleDateWater;

	private String samplePointGas;
	private String samplePointOil;
	private String samplePointWater;

	private String analysisCompanyNameGas;
	private String analysisCompanyNameOil;
	private String analysisCompanyNameWater;

	private Timestamp analysisDateGas;
	private Timestamp analysisDateOil;
	private Timestamp analysisDateWater;

	private String samplePressureTxtGas;
	private String samplePressureTxtOil;
	private String samplePressureTxtWater;
	
	private String sampleTempTxtGas;
	private String sampleTempTxtOil;
	private String sampleTempTxtWater;
	
	private String sampleFlowRateTxtGas;
	private String sampleFlowRateTxtOil;
	private String sampleFlowRateTxtWater;
	
	private BigDecimal samplePressureGas;
	private BigDecimal samplePressureOil;
	private BigDecimal samplePressureWater;

	private BigDecimal sampleTempGas;
	private BigDecimal sampleTempOil;
	private BigDecimal sampleTempWater;

	private BigDecimal sampleFlowRateGas;
	private BigDecimal sampleFlowRateOil;
	private BigDecimal sampleFlowRateWater;
	
	
	
	
	public Integer getFpId() {
		return fpId;
	}
	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}
	
	public String getSampleFacilityNameGas() {
		return sampleFacilityNameGas;
	}
	public void setSampleFacilityNameGas(String sampleFacilityNameGas) {
		this.sampleFacilityNameGas = sampleFacilityNameGas;
	}
	public String getSampleFacilityNameOil() {
		return sampleFacilityNameOil;
	}
	public void setSampleFacilityNameOil(String sampleFacilityNameOil) {
		this.sampleFacilityNameOil = sampleFacilityNameOil;
	}
	public String getSampleFacilityNameWater() {
		return sampleFacilityNameWater;
	}
	public void setSampleFacilityNameWater(String sampleFacilityNameWater) {
		this.sampleFacilityNameWater = sampleFacilityNameWater;
	}
	public String getSampleFacilityAPIGas() {
		return sampleFacilityAPIGas;
	}
	public void setSampleFacilityAPIGas(String sampleFacilityAPIGas) {
		this.sampleFacilityAPIGas = sampleFacilityAPIGas;
	}
	public String getSampleFacilityAPIOil() {
		return sampleFacilityAPIOil;
	}
	public void setSampleFacilityAPIOil(String sampleFacilityAPIOil) {
		this.sampleFacilityAPIOil = sampleFacilityAPIOil;
	}
	public String getSampleFacilityAPIWater() {
		return sampleFacilityAPIWater;
	}
	public void setSampleFacilityAPIWater(String sampleFacilityAPIWater) {
		this.sampleFacilityAPIWater = sampleFacilityAPIWater;
	}
	public String getSampleFacilityProducingFieldGas() {
		return sampleFacilityProducingFieldGas;
	}
	public void setSampleFacilityProducingFieldGas(String sampleFacilityProducingFieldGas) {
		this.sampleFacilityProducingFieldGas = sampleFacilityProducingFieldGas;
	}
	public String getSampleFacilityProducingFieldOil() {
		return sampleFacilityProducingFieldOil;
	}
	public void setSampleFacilityProducingFieldOil(String sampleFacilityProducingFieldOil) {
		this.sampleFacilityProducingFieldOil = sampleFacilityProducingFieldOil;
	}
	public String getSampleFacilityProducingFieldWater() {
		return sampleFacilityProducingFieldWater;
	}
	public void setSampleFacilityProducingFieldWater(String sampleFacilityProducingFieldWater) {
		this.sampleFacilityProducingFieldWater = sampleFacilityProducingFieldWater;
	}
	public String getSampleFacilityProducingFormationGas() {
		return sampleFacilityProducingFormationGas;
	}
	public void setSampleFacilityProducingFormationGas(String sampleFacilityProducingFormationGas) {
		this.sampleFacilityProducingFormationGas = sampleFacilityProducingFormationGas;
	}
	public String getSampleFacilityProducingFormationOil() {
		return sampleFacilityProducingFormationOil;
	}
	public void setSampleFacilityProducingFormationOil(String sampleFacilityProducingFormationOil) {
		this.sampleFacilityProducingFormationOil = sampleFacilityProducingFormationOil;
	}
	public String getSampleFacilityProducingFormationWater() {
		return sampleFacilityProducingFormationWater;
	}
	public void setSampleFacilityProducingFormationWater(String sampleFacilityProducingFormationWater) {
		this.sampleFacilityProducingFormationWater = sampleFacilityProducingFormationWater;
	}
	public Timestamp getSampleDateGas() {
		return sampleDateGas;
	}
	public void setSampleDateGas(Timestamp sampleDateGas) {
		this.sampleDateGas = sampleDateGas;
	}
	public Timestamp getSampleDateOil() {
		return sampleDateOil;
	}
	public void setSampleDateOil(Timestamp sampleDateOil) {
		this.sampleDateOil = sampleDateOil;
	}
	public Timestamp getSampleDateWater() {
		return sampleDateWater;
	}
	public void setSampleDateWater(Timestamp sampleDateWater) {
		this.sampleDateWater = sampleDateWater;
	}
	public String getSamplePointGas() {
		return samplePointGas;
	}
	public void setSamplePointGas(String samplePointGas) {
		this.samplePointGas = samplePointGas;
	}
	public String getSamplePointOil() {
		return samplePointOil;
	}
	public void setSamplePointOil(String samplePointOil) {
		this.samplePointOil = samplePointOil;
	}
	public String getSamplePointWater() {
		return samplePointWater;
	}
	public void setSamplePointWater(String samplePointWater) {
		this.samplePointWater = samplePointWater;
	}
	public String getAnalysisCompanyNameGas() {
		return analysisCompanyNameGas;
	}
	public void setAnalysisCompanyNameGas(String analysisCompanyNameGas) {
		this.analysisCompanyNameGas = analysisCompanyNameGas;
	}
	public String getAnalysisCompanyNameOil() {
		return analysisCompanyNameOil;
	}
	public void setAnalysisCompanyNameOil(String analysisCompanyNameOil) {
		this.analysisCompanyNameOil = analysisCompanyNameOil;
	}
	public String getAnalysisCompanyNameWater() {
		return analysisCompanyNameWater;
	}
	public void setAnalysisCompanyNameWater(String analysisCompanyNameWater) {
		this.analysisCompanyNameWater = analysisCompanyNameWater;
	}
	public Timestamp getAnalysisDateGas() {
		return analysisDateGas;
	}
	public void setAnalysisDateGas(Timestamp analysisDateGas) {
		this.analysisDateGas = analysisDateGas;
	}
	public Timestamp getAnalysisDateOil() {
		return analysisDateOil;
	}
	public void setAnalysisDateOil(Timestamp analysisDateOil) {
		this.analysisDateOil = analysisDateOil;
	}
	public Timestamp getAnalysisDateWater() {
		return analysisDateWater;
	}
	public void setAnalysisDateWater(Timestamp analysisDateWater) {
		this.analysisDateWater = analysisDateWater;
	}

	public String getSamplePressureTxtGas() {
		return samplePressureTxtGas;
	}
	public void setSamplePressureTxtGas(String samplePressureTxtGas) {
		this.samplePressureTxtGas = samplePressureTxtGas;
	}
	public String getSamplePressureTxtOil() {
		return samplePressureTxtOil;
	}
	public void setSamplePressureTxtOil(String samplePressureTxtOil) {
		this.samplePressureTxtOil = samplePressureTxtOil;
	}
	public String getSamplePressureTxtWater() {
		return samplePressureTxtWater;
	}
	public void setSamplePressureTxtWater(String samplePressureTxtWater) {
		this.samplePressureTxtWater = samplePressureTxtWater;
	}
	public String getSampleTempTxtGas() {
		return sampleTempTxtGas;
	}
	public void setSampleTempTxtGas(String sampleTempTxtGas) {
		this.sampleTempTxtGas = sampleTempTxtGas;
	}
	public String getSampleTempTxtOil() {
		return sampleTempTxtOil;
	}
	public void setSampleTempTxtOil(String sampleTempTxtOil) {
		this.sampleTempTxtOil = sampleTempTxtOil;
	}
	public String getSampleTempTxtWater() {
		return sampleTempTxtWater;
	}
	public void setSampleTempTxtWater(String sampleTempTxtWater) {
		this.sampleTempTxtWater = sampleTempTxtWater;
	}
	public String getSampleFlowRateTxtGas() {
		return sampleFlowRateTxtGas;
	}
	public void setSampleFlowRateTxtGas(String sampleFlowRateTxtGas) {
		this.sampleFlowRateTxtGas = sampleFlowRateTxtGas;
	}
	public String getSampleFlowRateTxtOil() {
		return sampleFlowRateTxtOil;
	}
	public void setSampleFlowRateTxtOil(String sampleFlowRateTxtOil) {
		this.sampleFlowRateTxtOil = sampleFlowRateTxtOil;
	}
	public String getSampleFlowRateTxtWater() {
		return sampleFlowRateTxtWater;
	}
	public void setSampleFlowRateTxtWater(String sampleFlowRateTxtWater) {
		this.sampleFlowRateTxtWater = sampleFlowRateTxtWater;
	}
	public BigDecimal getSamplePressureGas() {
		return samplePressureGas;
	}
	public void setSamplePressureGas(BigDecimal samplePressureGas) {
		this.samplePressureGas = samplePressureGas;
	}
	public BigDecimal getSamplePressureOil() {
		return samplePressureOil;
	}
	public void setSamplePressureOil(BigDecimal samplePressureOil) {
		this.samplePressureOil = samplePressureOil;
	}
	public BigDecimal getSamplePressureWater() {
		return samplePressureWater;
	}
	public void setSamplePressureWater(BigDecimal samplePressureWater) {
		this.samplePressureWater = samplePressureWater;
	}
	public BigDecimal getSampleTempGas() {
		return sampleTempGas;
	}
	public void setSampleTempGas(BigDecimal sampleTempGas) {
		this.sampleTempGas = sampleTempGas;
	}
	public BigDecimal getSampleTempOil() {
		return sampleTempOil;
	}
	public void setSampleTempOil(BigDecimal sampleTempOil) {
		this.sampleTempOil = sampleTempOil;
	}
	public BigDecimal getSampleTempWater() {
		return sampleTempWater;
	}
	public void setSampleTempWater(BigDecimal sampleTempWater) {
		this.sampleTempWater = sampleTempWater;
	}
	public BigDecimal getSampleFlowRateGas() {
		return sampleFlowRateGas;
	}
	public void setSampleFlowRateGas(BigDecimal sampleFlowRateGas) {
		this.sampleFlowRateGas = sampleFlowRateGas;
	}
	public BigDecimal getSampleFlowRateOil() {
		return sampleFlowRateOil;
	}
	public void setSampleFlowRateOil(BigDecimal sampleFlowRateOil) {
		this.sampleFlowRateOil = sampleFlowRateOil;
	}
	public BigDecimal getSampleFlowRateWater() {
		return sampleFlowRateWater;
	}
	public void setSampleFlowRateWater(BigDecimal sampleFlowRateWater) {
		this.sampleFlowRateWater = sampleFlowRateWater;
	}
	@Override
	public void populate(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		try {
			setFpId(AbstractDAO.getInteger(rs, "fp_id"));
			setSampleFacilityNameGas(rs.getString("SAMPLE_FACILITY_NAME_GAS"));
			setSampleFacilityNameOil(rs.getString("SAMPLE_FACILITY_NAME_Oil"));
			setSampleFacilityNameWater(rs.getString("SAMPLE_FACILITY_NAME_Water"));

			setSampleFacilityAPIGas(rs.getString("SAMPLE_FACILITY_API_GAS"));
			setSampleFacilityAPIOil(rs.getString("SAMPLE_FACILITY_API_OIL"));
			setSampleFacilityAPIWater(rs.getString("SAMPLE_FACILITY_API_WATER"));

			setSampleFacilityProducingFieldGas(rs.getString("SAMPLE_FACILITY_PRODUCING_FIELD_GAS"));
			setSampleFacilityProducingFieldOil(rs.getString("SAMPLE_FACILITY_PRODUCING_FIELD_OIL"));
			setSampleFacilityProducingFieldWater(rs.getString("SAMPLE_FACILITY_PRODUCING_FIELD_WATER"));
		
			setSampleFacilityProducingFormationGas(rs.getString("SAMPLE_FACILITY_PRODUCING_FORMATION_GAS"));
			setSampleFacilityProducingFormationOil(rs.getString("SAMPLE_FACILITY_PRODUCING_FORMATION_OIL"));
			setSampleFacilityProducingFormationWater(rs.getString("SAMPLE_FACILITY_PRODUCING_FORMATION_WATER"));
			
			setSampleDateGas(rs.getTimestamp("SAMPLE_DATE_GAS"));
			setSampleDateOil(rs.getTimestamp("SAMPLE_DATE_OIL"));
			setSampleDateWater(rs.getTimestamp("SAMPLE_DATE_WATER"));

			setSamplePointGas(rs.getString("SAMPLE_POINT_GAS"));
			setSamplePointOil(rs.getString("SAMPLE_POINT_OIL"));
			setSamplePointWater(rs.getString("SAMPLE_POINT_WATER"));
			
			setAnalysisCompanyNameGas(rs.getString("ANALYSIS_COMPANY_NAME_GAS"));
			setAnalysisCompanyNameOil(rs.getString("ANALYSIS_COMPANY_NAME_OIL"));
			setAnalysisCompanyNameWater(rs.getString("ANALYSIS_COMPANY_NAME_WATER"));

			setAnalysisDateGas(rs.getTimestamp("ANALYSIS_DATE_GAS"));
			setAnalysisDateOil(rs.getTimestamp("ANALYSIS_DATE_OIL"));
			setAnalysisDateWater(rs.getTimestamp("ANALYSIS_DATE_WATER"));

			setSamplePressureTxtGas(rs.getString("SAMPLE_PRESSURE_TXT_GAS"));
			setSamplePressureTxtOil(rs.getString("SAMPLE_PRESSURE_TXT_OIL"));
			setSamplePressureTxtWater(rs.getString("SAMPLE_PRESSURE_TXT_WATER"));
			
			setSampleTempTxtGas(rs.getString("SAMPLE_TEMP_TXT_GAS"));
			setSampleTempTxtOil(rs.getString("SAMPLE_TEMP_TXT_OIL"));
			setSampleTempTxtWater(rs.getString("SAMPLE_TEMP_TXT_WATER"));
			
			setSampleFlowRateTxtGas(rs.getString("SAMPLE_FLOW_RATE_TXT_GAS"));
			setSampleFlowRateTxtOil(rs.getString("SAMPLE_FLOW_RATE_TXT_OIL"));
			setSampleFlowRateTxtWater(rs.getString("SAMPLE_FLOW_RATE_TXT_WATER"));
			
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
		} catch (SQLException sqle){
			logger.warn("----- Required field warning. -----");
		} finally {
			setNewObject(false);
		}
	}

	public static final BigDecimal SAMPLE_PRESSURE_MAX = new BigDecimal(10000);
	public static final BigDecimal SAMPLE_PRESSURE_MIN = new BigDecimal(-100);
	public static final BigDecimal SAMPLE_TEMP_MAX = new BigDecimal(250);
	public static final BigDecimal SAMPLE_TEMP_MIN = new BigDecimal(-100);	
	public static final BigDecimal SAMPLE_FLOW_RATE_MAX = new BigDecimal(1000000000);
	public static final BigDecimal SAMPLE_FLOW_RATE_MIN = new BigDecimal(0);	
	public static final Integer MAX_PRECISION = 2;
	
	private BigDecimal convertStringToBigDecimal(String txtValue, String fieldLabel, String fieldName){
		BigDecimal value = null;
		if (txtValue != null && txtValue.trim() != null && !txtValue.trim().isEmpty()){
			try {
				value = new BigDecimal(txtValue.trim());
			} catch (NumberFormatException nfe){
				validationMessages.put(fieldLabel, new ValidationMessage(fieldLabel,
					"The value of the attribute : " + fieldName + " is not a number", ValidationMessage.Severity.ERROR, null));
			}
		}
		return value;
	}
	
	private String rounding(BigDecimal value, String stringInput){
		String ret = stringInput;
		if (value!= null && value.scale() > MAX_PRECISION){
			ret = value.setScale(MAX_PRECISION, RoundingMode.HALF_UP).toString();
		}
		return ret;
	}
	
	
	public ValidationMessage[] validate(){
		this.clearValidationMessages();
		samplePressureGas = convertStringToBigDecimal(samplePressureTxtGas, "samplePressureTxtGas", HCAnalysisSampleDetailRow.SAMPLE_PRESSURE + " - Gas");
		samplePressureOil = convertStringToBigDecimal(samplePressureTxtOil, "samplePressureTxtOil", HCAnalysisSampleDetailRow.SAMPLE_PRESSURE + " - Oil");
		samplePressureWater = convertStringToBigDecimal(samplePressureTxtWater, "samplePressureTxtWater", HCAnalysisSampleDetailRow.SAMPLE_PRESSURE + " - Water");
		
		sampleTempGas = convertStringToBigDecimal(sampleTempTxtGas, "sampleTempTxtGas", HCAnalysisSampleDetailRow.SAMPLE_TEMP + " - Gas");
		sampleTempOil = convertStringToBigDecimal(sampleTempTxtOil, "sampleTempTxtOil", HCAnalysisSampleDetailRow.SAMPLE_TEMP + " - Oil");
		sampleTempWater = convertStringToBigDecimal(sampleTempTxtWater, "sampleTempTxtWater", HCAnalysisSampleDetailRow.SAMPLE_TEMP + " - Water");
	
		sampleFlowRateGas = convertStringToBigDecimal(sampleFlowRateTxtGas, "sampleFlowRateTxtGas", HCAnalysisSampleDetailRow.SAMPLE_FLOW_RATE + " - Gas");
		sampleFlowRateOil = convertStringToBigDecimal(sampleFlowRateTxtOil, "sampleFlowRateTxtOil", HCAnalysisSampleDetailRow.SAMPLE_FLOW_RATE + " - Oil");
		sampleFlowRateWater = convertStringToBigDecimal(sampleFlowRateTxtWater, "sampleFlowRateTxtWater", HCAnalysisSampleDetailRow.SAMPLE_FLOW_RATE + " - Water");		

		checkRangeValues(samplePressureGas, SAMPLE_PRESSURE_MIN, SAMPLE_PRESSURE_MAX, "samplePressureTxtGas", HCAnalysisSampleDetailRow.SAMPLE_PRESSURE + " - Gas");
		checkRangeValues(samplePressureOil, SAMPLE_PRESSURE_MIN, SAMPLE_PRESSURE_MAX, "samplePressureTxtOil", HCAnalysisSampleDetailRow.SAMPLE_PRESSURE + " - Oil");
		checkRangeValues(samplePressureWater, SAMPLE_PRESSURE_MIN, SAMPLE_PRESSURE_MAX, "samplePressureTxtWater", HCAnalysisSampleDetailRow.SAMPLE_PRESSURE + " - Water");
		
		
		checkRangeValues(sampleTempGas, SAMPLE_TEMP_MIN, SAMPLE_TEMP_MAX, "sampleTempTxtGas", HCAnalysisSampleDetailRow.SAMPLE_TEMP + " - Gas");
		checkRangeValues(sampleTempOil, SAMPLE_TEMP_MIN, SAMPLE_TEMP_MAX, "sampleTempTxtOil", HCAnalysisSampleDetailRow.SAMPLE_TEMP + " - Oil");
		checkRangeValues(sampleTempWater, SAMPLE_TEMP_MIN, SAMPLE_TEMP_MAX, "sampleTempTxtWater", HCAnalysisSampleDetailRow.SAMPLE_TEMP + " - Water");

		checkRangeValues(sampleFlowRateGas, SAMPLE_FLOW_RATE_MIN, SAMPLE_FLOW_RATE_MAX, "sampleFlowRateTxtGas", HCAnalysisSampleDetailRow.SAMPLE_FLOW_RATE + " - Gas");
		checkRangeValues(sampleFlowRateOil, SAMPLE_FLOW_RATE_MIN, SAMPLE_FLOW_RATE_MAX, "sampleFlowRateTxtOil", HCAnalysisSampleDetailRow.SAMPLE_FLOW_RATE + " - Oil");
		checkRangeValues(sampleFlowRateWater, SAMPLE_FLOW_RATE_MIN, SAMPLE_FLOW_RATE_MAX, "sampleFlowRateTxtWater", HCAnalysisSampleDetailRow.SAMPLE_FLOW_RATE + " - Water");

		if (samplePressureGas != null && samplePressureGas.scale() > MAX_PRECISION ){
			samplePressureGas = samplePressureGas.setScale(MAX_PRECISION, RoundingMode.HALF_UP);
			samplePressureTxtGas = samplePressureGas.toString();
		}
		
		if (samplePressureOil != null && samplePressureOil.scale() > MAX_PRECISION ){
			samplePressureOil = samplePressureOil.setScale(MAX_PRECISION, RoundingMode.HALF_UP);
			samplePressureTxtOil = samplePressureOil.toString();
		}
		
		if (samplePressureWater != null && samplePressureWater.scale() > MAX_PRECISION ){
			samplePressureWater = samplePressureWater.setScale(MAX_PRECISION, RoundingMode.HALF_UP);
			samplePressureTxtWater = samplePressureWater.toString();
		}
		
		if (sampleTempGas != null && sampleTempGas.scale() > MAX_PRECISION ){
			sampleTempGas = sampleTempGas.setScale(MAX_PRECISION, RoundingMode.HALF_UP);
			sampleTempTxtGas = sampleTempGas.toString();
		}
		
		if (sampleTempOil != null && sampleTempOil.scale() > MAX_PRECISION ){
			sampleTempOil = sampleTempOil.setScale(MAX_PRECISION, RoundingMode.HALF_UP);
			sampleTempTxtOil = sampleTempOil.toString();
		}
		
		if (sampleTempWater != null && sampleTempWater.scale() > MAX_PRECISION ){
			sampleTempWater = sampleTempWater.setScale(MAX_PRECISION, RoundingMode.HALF_UP);
			sampleTempTxtWater = sampleTempWater.toString();
		}
		
		if (sampleFlowRateGas!= null && sampleFlowRateGas.scale() > MAX_PRECISION ){
			sampleFlowRateGas = sampleFlowRateGas.setScale(MAX_PRECISION, RoundingMode.HALF_UP);
			sampleFlowRateTxtGas = sampleFlowRateGas.toString();
		}
		
		if (sampleFlowRateOil != null && sampleFlowRateOil.scale() > MAX_PRECISION ){
			sampleFlowRateOil = sampleFlowRateOil.setScale(MAX_PRECISION, RoundingMode.HALF_UP);
			sampleFlowRateTxtOil = sampleFlowRateOil.toString();
		}
		
		if (sampleFlowRateWater != null && sampleFlowRateWater.scale() > MAX_PRECISION ){
			sampleFlowRateWater = sampleFlowRateWater.setScale(MAX_PRECISION, RoundingMode.HALF_UP);
			sampleFlowRateTxtWater = sampleFlowRateWater.toString();
		}
		
		return validationMessages.values().toArray(new ValidationMessage[0]);
	}
}
