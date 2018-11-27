package us.oh.state.epa.stars2.webcommon.facility;

import java.math.BigDecimal;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dbObjects.facility.HydrocarbonAnalysisSampleDetail;

public class HCAnalysisSampleDetailRow {
	private String rowDesc;
	private String gasColumnValue;
	private String oilColumnValue;
	private String waterColumnValue;
	
	
	private Object gasValue;
	private Object oilValue;
	private Object waterValue;
	
	public static final String SAMPLE_FACILITY_NAME = "Sample Facility Name";
	public static final String SAMPLE_FACILITY_API = "Sample Facility API Number(s)";
	public static final String SAMPLE_FACILITY_PRODUCING_FIELD = "Sample Facility Producing Field";
	public static final String SAMPLE_FACILITY_PRODUCING_FORMATION = "Sample Facility Producing Formation";
	
	public static final String SAMPLE_DATE = "Sample Date";
	public static final String SAMPLE_POINT = "Sample Point (Separator, Sales Line, etc.)";
	public static final String ANALYSIS_COMPANY_NAME = "Analysis Company Name";
	public static final String ANALYSIS_DATE = "Analysis Date";
	
	public static final String SAMPLE_PRESSURE = "Sample Pressure (psig)";
	public static final String SAMPLE_TEMP = "Sample Temperature (°F)";
	public static final String SAMPLE_FLOW_RATE = "Sample Flow Rate (scf/hr)";

	
	public HCAnalysisSampleDetailRow(){
		
	}
	
//	HCAnalysisSampleDetailRow(String rowDesc, String gasColumnValue, String oilColumnValue, String waterColumnValue){
//		this.rowDesc = rowDesc;
//		this.gasColumnValue = gasColumnValue;
//		this.oilColumnValue = oilColumnValue;
//		this.waterColumnValue = waterColumnValue;
//	}
//	
//	HCAnalysisSampleDetailRow(String rowDesc, Timestamp gasColumnValue, Timestamp oilColumnValue, Timestamp waterColumnValue){
//		this.rowDesc = rowDesc;
//		if (gasColumnValue != null){
//			this.gasColumnValue = gasColumnValue.toString();
//		}
//		if (oilColumnValue != null){
//			this.oilColumnValue = oilColumnValue.toString();
//		}
//		if (waterColumnValue != null){
//			this.waterColumnValue = waterColumnValue.toString();
//		}
//	}
//	
//	HCAnalysisSampleDetailRow(String rowDesc, BigDecimal gasColumnValue, BigDecimal oilColumnValue, BigDecimal waterColumnValue){
//		this.rowDesc = rowDesc;
//		if (gasColumnValue != null){
//			this.gasColumnValue = gasColumnValue.toString();
//		}
//		if (oilColumnValue != null){
//			this.oilColumnValue = oilColumnValue.toString();
//		}
//		if (waterColumnValue != null){
//			this.waterColumnValue = waterColumnValue.toString();
//		}
//	}

	public HCAnalysisSampleDetailRow(String rowDesc, Object gasValue, Object oilValue, Object waterValue){
		this.rowDesc = rowDesc;
		if (gasValue != null){
			this.gasValue = gasValue;
		}
		if (oilValue != null){
			this.oilValue = oilValue;
		}
		if (waterValue != null){
			this.waterValue = waterValue;
		}
	}
	
	public String getRowDesc() {
		return rowDesc;
	}
	public void setRowDesc(String rowDesc) {
		this.rowDesc = rowDesc;
	}
//	
//	public String getGasColumnValue() {
//		return gasColumnValue;
//	}
//	public void setGasColumnValue(String gasColumnValue) {
//		this.gasColumnValue = gasColumnValue;
//	}
//	public String getOilColumnValue() {
//		return oilColumnValue;
//	}
//	public void setOilColumnValue(String oilColumnValue) {
//		this.oilColumnValue = oilColumnValue;
//	}
//	public String getWaterColumnValue() {
//		return waterColumnValue;
//	}
//	public void setWaterColumnValue(String waterColumnValue) {
//		this.waterColumnValue = waterColumnValue;
//	}
//
	public Object getGasValue() {
		return gasValue;
	}

	public void setGasValue(Object gasValue) {
		this.gasValue = gasValue;
	}

	public Object getOilValue() {
		return oilValue;
	}

	public void setOilValue(Object oilValue) {
		this.oilValue = oilValue;
	}

	public Object getWaterValue() {
		return waterValue;
	}

	public void setWaterValue(Object waterValue) {
		this.waterValue = waterValue;
	}



	public boolean isDateRow(){
		boolean ret = false;
		if (gasValue!=null && Timestamp.class.equals(gasValue.getClass())){
			ret = true;
		}
		if (SAMPLE_DATE.equals(this.rowDesc) || ANALYSIS_DATE.equals(this.rowDesc)){ //it is necessary since when gasValue is null, it does not has data type
			ret= true;
		}
		return ret;
	}
	public Integer getMaximumLength(){
		Integer max = 10;
		if (SAMPLE_FACILITY_NAME.equalsIgnoreCase(rowDesc)){
			max = 60; // according to fp_facility schema, max limit of facility name in DB is 60
		}
		if (SAMPLE_FACILITY_API.equals(rowDesc) || SAMPLE_FACILITY_PRODUCING_FIELD.equals(rowDesc)|| SAMPLE_FACILITY_PRODUCING_FORMATION.equals(rowDesc)){
			max = 3000;
		}
		if (SAMPLE_POINT.equals(rowDesc) || ANALYSIS_COMPANY_NAME.equals(rowDesc)){
			max = 1000;
		}
		return max;
	}

}
