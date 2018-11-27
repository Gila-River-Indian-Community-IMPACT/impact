package us.wy.state.deq.impact.app.emissionsReport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCDataImportPollutant;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCPollutant;
import us.oh.state.epa.stars2.def.EmissionCalcMethodDef;
import us.oh.state.epa.stars2.def.FireVariableNamesDef;
import us.oh.state.epa.stars2.def.MaterialDef;

public class EiDataImportRow implements java.io.Serializable {

	private static final long serialVersionUID = 7526014822425070664L;

	private ArrayList<String> validationMessages;
	private ArrayList<String> warningMessages;

	private String facilityId;
	private String euId;
	private String processId;

	private Integer maximumHours;
	private Integer maximumDays;
	private Integer maximumWeeks;
	private Double actualHours;

	private Integer winterPct;
	private Integer springPct;
	private Integer summerPct;
	private Integer fallPct;

	private String material;
	private String throughput;

	private List<FireVariable> fireVariableList;
	private List<String> fireVariablesAdded;
	private List<Pollutant> pollutantList;
	private String calculationMethod;

	public EiDataImportRow(String[] rowData, List<SCDataImportPollutant> dataImportPollutants,
			List<SCPollutant> capPollutants, double maxOperatingHoursInReportingPeriod, Map<String, Integer> enabledEmissionReportsForAllFacilities) {
		fireVariableList = new ArrayList<FireVariable>();
		fireVariablesAdded = new ArrayList<String>();
		pollutantList = new ArrayList<Pollutant>();
		validateAndPopulate(rowData, dataImportPollutants, capPollutants, maxOperatingHoursInReportingPeriod, enabledEmissionReportsForAllFacilities);
	}

	public EiDataImportRow(EiDataImportRow oldRow) {
		setFacilityId(oldRow.getFacilityId());
		setEuId(oldRow.getEuId());
		setProcessId(oldRow.getProcessId());
		setMaximumHours(oldRow.getMaximumHours());
		setMaximumDays(oldRow.getMaximumDays());
		setMaximumWeeks(oldRow.getMaximumWeeks());
		setActualHours(oldRow.getActualHours());
		setWinterPct(oldRow.getWinterPct());
		setSpringPct(oldRow.getSpringPct());
		setSummerPct(oldRow.getSummerPct());
		setFallPct(oldRow.getFallPct());
		setMaterial(oldRow.getMaterial());
		setThroughput(oldRow.getThroughput());
		setCalculationMethod(oldRow.getCalculationMethod());
		setFireVariableList(oldRow.getFireVariableList());
		setPollutantList(oldRow.getPollutantList());
	}

	private void validateAndPopulate(String[] rowData, List<SCDataImportPollutant> dataImportpollutants,
			List<SCPollutant> capPollutants, double maxOperatingHoursInReportingPeriod, Map<String, Integer> enabledEmissionReportsForAllFacilities) {
		validationMessages = new ArrayList<String>();
		warningMessages = new ArrayList<String>();

		validateFacilityId(rowData[0], enabledEmissionReportsForAllFacilities);
		validateEuId(rowData[1]);
		validateProcessId(rowData[2]);

		validateMaximumHours(rowData[3]);
		validateMaximumDays(rowData[4]);
		validateMaximumWeeks(rowData[5]);
		validateActualHours(rowData[6], maxOperatingHoursInReportingPeriod);

		validateQuartersPct(rowData[7], rowData[8], rowData[9], rowData[10]);

		validateMaterial(rowData[11]);
		validateThroughput(rowData[12]);

		validateFireVariable(rowData[13], rowData[14]);
		validateFireVariable(rowData[15], rowData[16]);
		validateFireVariable(rowData[17], rowData[18]);
		validateFireVariable(rowData[19], rowData[20]);
		validateFireVariable(rowData[21], rowData[22]);

		validateCalculationMethod(rowData[23]);

		int column = 24;
		boolean isContinue = true;
		cap: for (SCPollutant pollutant : capPollutants) {
			if (!isContinue)
				break cap;
			String pollutantCd = pollutant.getPollutantCd();
			String pollutantDesc = pollutant.getPollutantDsc();
			isContinue = validatePollutant(pollutantCd, pollutantDesc, true, rowData[column++], rowData[column++]);
		}
		hap: for (SCDataImportPollutant pollutant : dataImportpollutants) {
			if (!isContinue)
				break hap;
			String pollutantCd = pollutant.getPollutantCd();
			String pollutantDesc = pollutant.getPollutantDsc();
			isContinue = validatePollutant(pollutantCd, pollutantDesc, false, rowData[column++], rowData[column++]);
		}
	}

	private boolean isZeroHoursAndThroughput() {
		if (this.throughput == null || this.actualHours == null){
			return false;
		}
		double throughpt = Double.parseDouble(this.throughput);
		return (throughpt == 0d && this.actualHours.equals(0d));
	}

	private void validateFacilityId(String facilityId, Map<String, Integer> enabledEmissionReportsForAllFacilities) {
		if (StringUtils.isEmpty(facilityId)) {
			validationMessages.add("Facility id is empty");
		} else {
			String facId = StringUtils.trim(facilityId);
			if (enabledEmissionReportsForAllFacilities.containsKey(facId)
					&& enabledEmissionReportsForAllFacilities.get(facId) > 0) {
				this.facilityId = StringUtils.trim(facilityId);
			} else {
				validationMessages.add("Facility " + facId + " does not have reporting enabled for selected year and content type");
			}
		}
	}

	private void validateEuId(String euId) {
		if (StringUtils.isEmpty(euId)) {
			validationMessages.add("EU id is empty");
		} else {
			this.euId = StringUtils.trim(euId);
		}
	}

	private void validateProcessId(String processId) {
		if (StringUtils.isEmpty(processId)) {
			validationMessages.add("Process id is empty");
		} else {
			this.processId = StringUtils.trim(processId);
		}
	}

	private void validateMaximumHours(String maximumHours) {
		if (StringUtils.isEmpty(maximumHours)) {
			validationMessages.add("Maximum Hours Per Day is empty");
		} else {
			try {
				int maximumHoursInt = Integer.parseInt(maximumHours);
				if (maximumHoursInt < 0 || maximumHoursInt > 24) {
					validationMessages.add(
							"Maximum Hours Per Day " + maximumHours + " is not in range. Expected range is 0 to 24");
				} else {
					this.maximumHours = maximumHoursInt;
				}
			} catch (NumberFormatException e) {
				validationMessages.add("Maximum Hours Per Day is not a valid value");
			}
		}
	}

	private void validateMaximumDays(String maximumDays) {
		if (StringUtils.isEmpty(maximumDays)) {
			validationMessages.add("Maximum Days Per Week is empty");
		} else {
			try {
				int maximumDaysInt = Integer.parseInt(maximumDays);
				if (maximumDaysInt < 0 || maximumDaysInt > 7) {
					validationMessages
							.add("Maximum Days Per Week " + maximumDays + " is not in range. Expected range is 0 to 7");
				} else {
					this.maximumDays = maximumDaysInt;
				}
			} catch (NumberFormatException e) {
				validationMessages.add("Maximum Days Per Week is not a valid value");
			}
		}
	}

	private void validateMaximumWeeks(String maximumWeeks) {
		if (StringUtils.isEmpty(maximumWeeks)) {
			validationMessages.add("Maximum Weeks Per Year is empty");
		} else {
			try {
				int maximumWeeksInt = Integer.parseInt(maximumWeeks);
				if (maximumWeeksInt < 0 || maximumWeeksInt > 52) {
					validationMessages.add(
							"Maximum Weeks Per Year " + maximumWeeks + " is not in range. Expected range is 0 to 52");
				} else {
					this.maximumWeeks = maximumWeeksInt;
				}
			} catch (NumberFormatException e) {
				validationMessages.add("Maximum Weeks Per Year is not a valid value");
			}
		}
	}

	private void validateActualHours(String actualHours, double maxOperatingHoursInReportingPeriod) {
		if (StringUtils.isEmpty(actualHours)) {
			validationMessages.add("Actual Hours is empty");
		} else {
			try {
				double actualHoursDbl = Double.parseDouble(actualHours);
				if (actualHoursDbl < 0 || actualHoursDbl > maxOperatingHoursInReportingPeriod) {
					validationMessages.add("Actual Hours " + actualHours + " is not in range. Expected range is 0 to "
							+ maxOperatingHoursInReportingPeriod);
				} else {
					this.actualHours = actualHoursDbl;
				}
			} catch (NumberFormatException e) {
				validationMessages.add("Actual Hours is not a valid value");
			}
		}
	}

	private void validateQuartersPct(String winterPct, String springPct, String summerPct, String fallPct) {
		boolean err = false;

		if (isValidSeasonalPercentage(winterPct, "Winter")) {
			this.winterPct = Integer.parseInt(winterPct);
		} else {
			err = true;
		}
		if (isValidSeasonalPercentage(springPct, "Spring")) {
			this.springPct = Integer.parseInt(springPct);
		} else {
			err = true;
		}
		if (isValidSeasonalPercentage(summerPct, "Summer")) {
			this.summerPct = Integer.parseInt(summerPct);
		} else {
			err = true;
		}
		if (isValidSeasonalPercentage(fallPct, "Fall")) {
			this.fallPct = Integer.parseInt(fallPct);
		} else {
			err = true;
		}

		if (!err && (this.winterPct + this.springPct + this.summerPct + this.fallPct) != 100) {
			validationMessages.add("Winter, Spring, Summer & Fall percent total must equal and not exceed 100%");
		}
	}

	private boolean isValidSeasonalPercentage(String seasonPct, String season) {
		boolean isValid = true;
		try {
			if (StringUtils.isNotBlank(seasonPct)) {
				int seasonPctInt = Integer.parseInt(seasonPct);
				if (seasonPctInt < 0) {
					isValid = false;
					validationMessages.add(season + " percentage is a negative value");
				}
			} else {
				isValid = false;
				validationMessages.add(season + " percentage is empty");
			}
		} catch (NumberFormatException e) {
			isValid = false;
			validationMessages.add(season + " percentage is not an integer");
		}

		return isValid;
	}

	private void validateMaterial(String material) {
		if (StringUtils.isEmpty(material)) {
			validationMessages.add("Material is empty");
		} else {
			material = StringUtils.trim(material);
			String materialCd = (String) MaterialDef.getMaterialCode(material);
			if (StringUtils.isBlank(materialCd)) {
				validationMessages.add("Material " + material + " not found in definition list or is Inactive");
			} else {
				this.material = materialCd;
			}

		}
	}

	private void validateThroughput(String throughput) {
		if (StringUtils.isEmpty(throughput)) {
			validationMessages.add("Throughput is empty");
		} else {
			try {
				if (Double.parseDouble(throughput) < 0) {
					validationMessages.add("Throughput is a negative value");
				} else {
					this.throughput = throughput;
				}
			} catch (NumberFormatException e) {
				validationMessages.add("Throughput is not a valid value");
			}
		}
	}

	private void validateFireVariable(String fireVariable, String fireVariableValue) {
		if (StringUtils.isNotBlank(fireVariable)) {
			fireVariable = StringUtils.trim(fireVariable);
			if (this.fireVariablesAdded.contains(fireVariable)) {
				validationMessages.add("Duplicate Fire Variable " + fireVariable + " found");
				return;
			}
			if (StringUtils.isNotBlank(fireVariableValue)) {
				try {
					double fireVal = Double.parseDouble(fireVariableValue);
					if (fireVal < 0d) {
						validationMessages.add("Fire variable value is negative for Fire variable: " + fireVariable);
					}
				} catch (NumberFormatException e) {
					validationMessages.add("Fire Variable " + fireVariable + " does not have a valid value");
				}
				String fireVariableNm = (String) FireVariableNamesDef.getFireVariableName(fireVariable);
				if (StringUtils.isBlank(fireVariableNm)) {
					validationMessages.add("Fire Variable " + fireVariable + " not found in definition list");
				} else {
					FireVariable newFireVariable = new FireVariable(fireVariable, fireVariableValue);
					this.fireVariableList.add(newFireVariable);
					this.fireVariablesAdded.add(fireVariable);
				}
			} else {
				validationMessages.add("Fire Variable " + fireVariable + " available, but value is empty");
			}
		} else if (StringUtils.isNotBlank(fireVariableValue)) {
			validationMessages.add("Fire Variable unavailable, but value " + fireVariableValue + " is available");
		}
	}

	private void validateCalculationMethod(String calculationMethod) {
		if (StringUtils.isEmpty(calculationMethod)) {
			validationMessages.add("Calculation Method is empty");
		} else {
			calculationMethod = StringUtils.trim(calculationMethod);
			if (calculationMethod.toLowerCase().startsWith("e")) {
				if (!StringUtils.equalsIgnoreCase(calculationMethod, "Emissions")) {
					warningMessages.add("Calculation method in file : " + calculationMethod
							+ " - Setting calculation method to : Emissions");
				}
				this.calculationMethod = EmissionCalcMethodDef.SCCEmissions;
			} else {
				if (!StringUtils.equalsIgnoreCase(calculationMethod, "AQD Generated")) {
					warningMessages.add("Calculation method in file : " + calculationMethod
							+ " - Setting calculation method to : AQD Generated");
				}
				this.calculationMethod = EmissionCalcMethodDef.AQDGenerated;
			}
		}
	}

	/**
	 * @param pollutantCd
	 * @param pollutantDesc
	 * @param isCapPollutant
	 * @param stackEmission
	 * @param fugitiveEmission
	 * @return boolean returned is used to determine if parsing should continue.
	 */
	private boolean validatePollutant(String pollutantCd, String pollutantDesc, boolean isCapPollutant,
			String stackEmission, String fugitiveEmission) {
		boolean noErrors = true;

		if (isCapPollutant) {
			if (isZeroHoursAndThroughput() && !StringUtils.isBlank(stackEmission)) {
				// CAP Pollutant has to be null if Actual hours and Throughput are zero
				noErrors = false;
				validationMessages.add("Actual Hours and Throughput are zero, but pollutants have values");
				return false;
			} else if (StringUtils.isBlank(stackEmission) && !isZeroHoursAndThroughput()) {
				// CAP Pollutant emission values cannot be empty
				noErrors = false;
				validationMessages.add("Stack Emission value is blank for CAP Pollutant: " + pollutantDesc);
			}

			if (isZeroHoursAndThroughput() && !StringUtils.isBlank(fugitiveEmission)) {
				noErrors = false;
				validationMessages.add("Actual Hours and Throughput are zero, but pollutants have values");
				return false;
			} else if (StringUtils.isBlank(fugitiveEmission) && !isZeroHoursAndThroughput()) {
				noErrors = false;
				validationMessages.add("Fugitive Emission value is blank for CAP Pollutant: " + pollutantDesc);
			}
		} else {
			if (StringUtils.isBlank(stackEmission) && StringUtils.isBlank(fugitiveEmission)) {
				warningMessages.add("Stack and Fugitive Emission values are blank for Pollutant: " + pollutantDesc
						+ " - Skipping HAP pollutant");
				return true;
			} else if (isZeroHoursAndThroughput()) {
				noErrors = false;
				validationMessages.add("Actual Hours and Throughput are zero, but pollutants have values");
				return false;
			}
		}

		if (noErrors && !isZeroHoursAndThroughput()) {
			try {
				double stackEmissionD = Double.parseDouble(stackEmission);
				if (stackEmissionD < 0d) {
					validationMessages.add("Stack Emission is a negative value for Pollutant: " + pollutantDesc);
					noErrors = false;
				}
			} catch (NumberFormatException e) {
				validationMessages.add("Stack Emission value is invalid for Pollutant: " + pollutantDesc);
			}
			try {
				double fugitiveEmissionD = Double.parseDouble(fugitiveEmission);
				if (fugitiveEmissionD < 0d) {
					validationMessages.add("Fugitive Emission is a negative value for Pollutant: " + pollutantDesc);
					noErrors = false;
				}
			} catch (NumberFormatException e) {
				validationMessages.add("Fugitive Emission value is invalid for Pollutant: " + pollutantDesc);
			}
		}

		if (noErrors && !isZeroHoursAndThroughput()) {
			Pollutant newPollutant = new Pollutant(pollutantCd, stackEmission, fugitiveEmission);
			this.pollutantList.add(newPollutant);
		}
		return true;
	}

	/*******************************
	 * Setters and getters
	 *******************************/

	public ArrayList<String> getValidationMessages() {
		return validationMessages;
	}

	public void setValidationMessages(ArrayList<String> validationMessages) {
		this.validationMessages = validationMessages;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getEuId() {
		return euId;
	}

	public void setEuId(String euId) {
		this.euId = euId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Integer getMaximumHours() {
		return maximumHours;
	}

	public void setMaximumHours(Integer maximumHours) {
		this.maximumHours = maximumHours;
	}

	public Integer getMaximumDays() {
		return maximumDays;
	}

	public void setMaximumDays(Integer maximumDays) {
		this.maximumDays = maximumDays;
	}

	public Integer getMaximumWeeks() {
		return maximumWeeks;
	}

	public void setMaximumWeeks(Integer maximumWeeks) {
		this.maximumWeeks = maximumWeeks;
	}

	public Double getActualHours() {
		return actualHours;
	}

	public void setActualHours(Double actualHours) {
		this.actualHours = actualHours;
	}

	public Integer getWinterPct() {
		return winterPct;
	}

	public void setWinterPct(Integer winterPct) {
		this.winterPct = winterPct;
	}

	public Integer getSpringPct() {
		return springPct;
	}

	public void setSpringPct(Integer springPct) {
		this.springPct = springPct;
	}

	public Integer getSummerPct() {
		return summerPct;
	}

	public void setSummerPct(Integer summerPct) {
		this.summerPct = summerPct;
	}

	public Integer getFallPct() {
		return fallPct;
	}

	public void setFallPct(Integer fallPct) {
		this.fallPct = fallPct;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getThroughput() {
		return throughput;
	}

	public void setThroughput(String throughput) {
		this.throughput = throughput;
	}

	public String getCalculationMethod() {
		return calculationMethod;
	}

	public void setCalculationMethod(String calculationMethod) {
		this.calculationMethod = calculationMethod;
	}

	public List<FireVariable> getFireVariableList() {
		return fireVariableList;
	}

	public void setFireVariableList(List<FireVariable> fireVariableList) {
		this.fireVariableList = fireVariableList;
	}

	public List<Pollutant> getPollutantList() {
		return pollutantList;
	}

	public List<String> getFireVariablesAdded() {
		return fireVariablesAdded;
	}

	public void setFireVariablesAdded(List<String> fireVariablesAdded) {
		this.fireVariablesAdded = fireVariablesAdded;
	}

	public void setPollutantList(List<Pollutant> pollutantList) {
		this.pollutantList = pollutantList;
	}

	public ArrayList<String> getWarningMessages() {
		return warningMessages;
	}

	public void setWarningMessages(ArrayList<String> warningMessages) {
		this.warningMessages = warningMessages;
	}

	public class FireVariable implements Serializable {

		private static final long serialVersionUID = 1L;

		private String variableID;
		private String value;

		public FireVariable(String variableID, String value) {
			super();
			this.variableID = variableID;
			this.value = value;
		}

		public String getVariableID() {
			return variableID;
		}

		public void setVariableID(String variableID) {
			this.variableID = variableID;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			result = prime * result + ((variableID == null) ? 0 : variableID.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FireVariable other = (FireVariable) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			if (variableID == null) {
				if (other.variableID != null)
					return false;
			} else if (!variableID.equals(other.variableID))
				return false;
			return true;
		}

		private EiDataImportRow getOuterType() {
			return EiDataImportRow.this;
		}

		@Override
		public String toString() {
			return "FireVariable [variableID=" + variableID + ", value=" + value + "]";
		}

	}

	public class Pollutant implements Serializable {

		private static final long serialVersionUID = 1590213581942409099L;

		private String pollutantCd;
		private String stackEmission;
		private String fugitiveEmission;

		public Pollutant(String pollutantCd, String stackEmission, String fugitiveEmission) {
			super();
			this.pollutantCd = pollutantCd;
			this.stackEmission = stackEmission;
			this.fugitiveEmission = fugitiveEmission;
		}

		public String getPollutantCd() {
			return pollutantCd;
		}

		public void setPollutantCd(String pollutantCd) {
			this.pollutantCd = pollutantCd;
		}

		public String getStackEmission() {
			return stackEmission;
		}

		public void setStackEmission(String stackEmission) {
			this.stackEmission = stackEmission;
		}

		public String getFugitiveEmission() {
			return fugitiveEmission;
		}

		public void setFugitiveEmission(String fugitiveEmission) {
			this.fugitiveEmission = fugitiveEmission;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
			result = prime * result + ((stackEmission == null) ? 0 : stackEmission.hashCode());
			result = prime * result + ((fugitiveEmission == null) ? 0 : fugitiveEmission.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pollutant other = (Pollutant) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (pollutantCd == null) {
				if (other.pollutantCd != null)
					return false;
			} else if (!pollutantCd.equals(other.pollutantCd))
				return false;
			if (stackEmission == null) {
				if (other.stackEmission != null)
					return false;
			} else if (!stackEmission.equals(other.stackEmission))
				return false;
			if (fugitiveEmission == null) {
				if (other.fugitiveEmission != null)
					return false;
			} else if (!fugitiveEmission.equals(other.fugitiveEmission))
				return false;
			return true;
		}

		private EiDataImportRow getOuterType() {
			return EiDataImportRow.this;
		}

		@Override
		public String toString() {
			return "Pollutant [pollutantCd=" + pollutantCd + ", stackEmission=" + stackEmission + ", fugitiveEmission="
					+ fugitiveEmission + "]";
		}

	}
}
