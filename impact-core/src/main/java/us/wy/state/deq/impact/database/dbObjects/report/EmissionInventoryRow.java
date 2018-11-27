package us.wy.state.deq.impact.database.dbObjects.report;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class EmissionInventoryRow extends BaseDB {
	
	private static final long serialVersionUID = 1L;

	// Selected from a drop-down list containing year values from 2017 
	// through the current calendar year (inclusive).
	private int inventoryYear;
	
	// Selected from a drop-down list containing values for the content type 
	// as defined in IMPACT’s definition list.
	private String contentType; 
	
	// Selected from a drop-down list containing values for the regulatory 
	// requirement as defined in IMPACT’s definition list. IMPACT shall show 
	// only those rows in the definition list with “Allow file import” set to “Yes."
	private String regulatoryRequirement;
	
	// A file selector field indicating data file to import.
	private String eiFileName;

	// Datagrid of attachment files. The user may specify zero, one, or multiple 
	// files to attach to each generated Emissions Inventory record. IMPACT shall 
	// allow the user to specify the attachment type and description for each file.
	private List<String> attachmentFiles;
	
	private String facilityID;
	private String euID;
	private String processID;
	private Integer maxHours;
	private Integer maxDays;
	private Integer maxWeeks;
	private Integer actualHours;
	private Integer winterPct;
	private Integer springPct;
	private Integer summerPct;
	private Integer fallPct;
	private String material;
	private Integer throughput;
	// If the string begins with “E” (regardless of case), then use “Emissions” 
	// as the calculation method, else use “AQD Generated”.
	private String calculationMethod;
	private List<FireVariable> fireVariableList;
	
	private Integer pmPrimaryStackEmissions;
	private Integer pmPrimaryFugitiveEmissions;
	private Integer pm10PrimaryStackEmissions;
	private Integer pm10PrimaryFugitiveEmissions;
	private Integer pm25PrimaryStackEmissions;
	private Integer pm25PrimaryFugitiveEmissions;

	
	public EmissionInventoryRow() {
		super();
	}

	public EmissionInventoryRow(int inventoryYear, String contentType, String regulatoryRequirement, String eiFileName,
			List<String> attachmentFiles, String facilityID, String euID, String processID, Integer maxHours,
			Integer maxDays, Integer maxWeeks, Integer actualHours, Integer winterPct, Integer springPct,
			Integer summerPct, Integer fallPct, String material, Integer throughput, String calculationMethod,
			List<FireVariable> fireVariableList, Integer pmPrimaryStackEmissions, Integer pmPrimaryFugitiveEmissions,
			Integer pm10PrimaryStackEmissions, Integer pm10PrimaryFugitiveEmissions, Integer pm25PrimaryStackEmissions,
			Integer pm25PrimaryFugitiveEmissions) {
		
		super();

		this.inventoryYear = inventoryYear;
		this.contentType = contentType;
		this.regulatoryRequirement = regulatoryRequirement;
		this.eiFileName = eiFileName;
		this.attachmentFiles = attachmentFiles;
		this.facilityID = facilityID;
		this.euID = euID;
		this.processID = processID;
		this.maxHours = maxHours;
		this.maxDays = maxDays;
		this.maxWeeks = maxWeeks;
		this.actualHours = actualHours;
		this.winterPct = winterPct;
		this.springPct = springPct;
		this.summerPct = summerPct;
		this.fallPct = fallPct;
		this.material = material;
		this.throughput = throughput;
		this.calculationMethod = calculationMethod;
		this.fireVariableList = fireVariableList;
		this.pmPrimaryStackEmissions = pmPrimaryStackEmissions;
		this.pmPrimaryFugitiveEmissions = pmPrimaryFugitiveEmissions;
		this.pm10PrimaryStackEmissions = pm10PrimaryStackEmissions;
		this.pm10PrimaryFugitiveEmissions = pm10PrimaryFugitiveEmissions;
		this.pm25PrimaryStackEmissions = pm25PrimaryStackEmissions;
		this.pm25PrimaryFugitiveEmissions = pm25PrimaryFugitiveEmissions;
	}

	public int getInventoryYear() {
		return inventoryYear;
	}

	public void setInventoryYear(int inventoryYear) {
		this.inventoryYear = inventoryYear;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getRegulatoryRequirement() {
		return regulatoryRequirement;
	}

	public void setRegulatoryRequirement(String regulatoryRequirement) {
		this.regulatoryRequirement = regulatoryRequirement;
	}

	public String getEiFileName() {
		return eiFileName;
	}

	public void setEiFileName(String eiFileName) {
		this.eiFileName = eiFileName;
	}

	public List<String> getAttachmentFiles() {
		return attachmentFiles;
	}

	public void setAttachmentFiles(List<String> attachmentFiles) {
		this.attachmentFiles = attachmentFiles;
	}

	public String getFacilityID() {
		return facilityID;
	}

	public void setFacilityID(String facilityID) {
		this.facilityID = facilityID;
	}

	public String getEuID() {
		return euID;
	}

	public void setEuID(String euID) {
		this.euID = euID;
	}

	public String getProcessID() {
		return processID;
	}

	public void setProcessID(String processID) {
		this.processID = processID;
	}

	public Integer getMaxHours() {
		return maxHours;
	}

	public void setMaxHours(Integer maxHours) {
		this.maxHours = maxHours;
	}

	public Integer getMaxDays() {
		return maxDays;
	}

	public void setMaxDays(Integer maxDays) {
		this.maxDays = maxDays;
	}

	public Integer getMaxWeeks() {
		return maxWeeks;
	}

	public void setMaxWeeks(Integer maxWeeks) {
		this.maxWeeks = maxWeeks;
	}

	public Integer getActualHours() {
		return actualHours;
	}

	public void setActualHours(Integer actualHours) {
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

	public Integer getThroughput() {
		return throughput;
	}

	public void setThroughput(Integer throughput) {
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

	public Integer getPmPrimaryStackEmissions() {
		return pmPrimaryStackEmissions;
	}

	public void setPmPrimaryStackEmissions(Integer pmPrimaryStackEmissions) {
		this.pmPrimaryStackEmissions = pmPrimaryStackEmissions;
	}

	public Integer getPmPrimaryFugitiveEmissions() {
		return pmPrimaryFugitiveEmissions;
	}

	public void setPmPrimaryFugitiveEmissions(Integer pmPrimaryFugitiveEmissions) {
		this.pmPrimaryFugitiveEmissions = pmPrimaryFugitiveEmissions;
	}

	public Integer getPm10PrimaryStackEmissions() {
		return pm10PrimaryStackEmissions;
	}

	public void setPm10PrimaryStackEmissions(Integer pm10PrimaryStackEmissions) {
		this.pm10PrimaryStackEmissions = pm10PrimaryStackEmissions;
	}

	public Integer getPm10PrimaryFugitiveEmissions() {
		return pm10PrimaryFugitiveEmissions;
	}

	public void setPm10PrimaryFugitiveEmissions(Integer pm10PrimaryFugitiveEmissions) {
		this.pm10PrimaryFugitiveEmissions = pm10PrimaryFugitiveEmissions;
	}

	public Integer getPm25PrimaryStackEmissions() {
		return pm25PrimaryStackEmissions;
	}

	public void setPm25PrimaryStackEmissions(Integer pm25PrimaryStackEmissions) {
		this.pm25PrimaryStackEmissions = pm25PrimaryStackEmissions;
	}

	public Integer getPm25PrimaryFugitiveEmissions() {
		return pm25PrimaryFugitiveEmissions;
	}

	public void setPm25PrimaryFugitiveEmissions(Integer pm25PrimaryFugitiveEmissions) {
		this.pm25PrimaryFugitiveEmissions = pm25PrimaryFugitiveEmissions;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((actualHours == null) ? 0 : actualHours.hashCode());
		result = prime * result + ((attachmentFiles == null) ? 0 : attachmentFiles.hashCode());
		result = prime * result + ((calculationMethod == null) ? 0 : calculationMethod.hashCode());
		result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
		result = prime * result + ((eiFileName == null) ? 0 : eiFileName.hashCode());
		result = prime * result + ((euID == null) ? 0 : euID.hashCode());
		result = prime * result + ((facilityID == null) ? 0 : facilityID.hashCode());
		result = prime * result + ((fallPct == null) ? 0 : fallPct.hashCode());
		result = prime * result + ((fireVariableList == null) ? 0 : fireVariableList.hashCode());
		result = prime * result + inventoryYear;
		result = prime * result + ((material == null) ? 0 : material.hashCode());
		result = prime * result + ((maxDays == null) ? 0 : maxDays.hashCode());
		result = prime * result + ((maxHours == null) ? 0 : maxHours.hashCode());
		result = prime * result + ((maxWeeks == null) ? 0 : maxWeeks.hashCode());
		result = prime * result
				+ ((pm10PrimaryFugitiveEmissions == null) ? 0 : pm10PrimaryFugitiveEmissions.hashCode());
		result = prime * result + ((pm10PrimaryStackEmissions == null) ? 0 : pm10PrimaryStackEmissions.hashCode());
		result = prime * result
				+ ((pm25PrimaryFugitiveEmissions == null) ? 0 : pm25PrimaryFugitiveEmissions.hashCode());
		result = prime * result + ((pm25PrimaryStackEmissions == null) ? 0 : pm25PrimaryStackEmissions.hashCode());
		result = prime * result + ((pmPrimaryFugitiveEmissions == null) ? 0 : pmPrimaryFugitiveEmissions.hashCode());
		result = prime * result + ((pmPrimaryStackEmissions == null) ? 0 : pmPrimaryStackEmissions.hashCode());
		result = prime * result + ((processID == null) ? 0 : processID.hashCode());
		result = prime * result + ((regulatoryRequirement == null) ? 0 : regulatoryRequirement.hashCode());
		result = prime * result + ((springPct == null) ? 0 : springPct.hashCode());
		result = prime * result + ((summerPct == null) ? 0 : summerPct.hashCode());
		result = prime * result + ((throughput == null) ? 0 : throughput.hashCode());
		result = prime * result + ((winterPct == null) ? 0 : winterPct.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmissionInventoryRow other = (EmissionInventoryRow) obj;
		if (actualHours == null) {
			if (other.actualHours != null)
				return false;
		} else if (!actualHours.equals(other.actualHours))
			return false;
		if (attachmentFiles == null) {
			if (other.attachmentFiles != null)
				return false;
		} else if (!attachmentFiles.equals(other.attachmentFiles))
			return false;
		if (calculationMethod == null) {
			if (other.calculationMethod != null)
				return false;
		} else if (!calculationMethod.equals(other.calculationMethod))
			return false;
		if (contentType == null) {
			if (other.contentType != null)
				return false;
		} else if (!contentType.equals(other.contentType))
			return false;
		if (eiFileName == null) {
			if (other.eiFileName != null)
				return false;
		} else if (!eiFileName.equals(other.eiFileName))
			return false;
		if (euID == null) {
			if (other.euID != null)
				return false;
		} else if (!euID.equals(other.euID))
			return false;
		if (facilityID == null) {
			if (other.facilityID != null)
				return false;
		} else if (!facilityID.equals(other.facilityID))
			return false;
		if (fallPct == null) {
			if (other.fallPct != null)
				return false;
		} else if (!fallPct.equals(other.fallPct))
			return false;
		if (fireVariableList == null) {
			if (other.fireVariableList != null)
				return false;
		} else if (!fireVariableList.equals(other.fireVariableList))
			return false;
		if (inventoryYear != other.inventoryYear)
			return false;
		if (material == null) {
			if (other.material != null)
				return false;
		} else if (!material.equals(other.material))
			return false;
		if (maxDays == null) {
			if (other.maxDays != null)
				return false;
		} else if (!maxDays.equals(other.maxDays))
			return false;
		if (maxHours == null) {
			if (other.maxHours != null)
				return false;
		} else if (!maxHours.equals(other.maxHours))
			return false;
		if (maxWeeks == null) {
			if (other.maxWeeks != null)
				return false;
		} else if (!maxWeeks.equals(other.maxWeeks))
			return false;
		if (pm10PrimaryFugitiveEmissions == null) {
			if (other.pm10PrimaryFugitiveEmissions != null)
				return false;
		} else if (!pm10PrimaryFugitiveEmissions.equals(other.pm10PrimaryFugitiveEmissions))
			return false;
		if (pm10PrimaryStackEmissions == null) {
			if (other.pm10PrimaryStackEmissions != null)
				return false;
		} else if (!pm10PrimaryStackEmissions.equals(other.pm10PrimaryStackEmissions))
			return false;
		if (pm25PrimaryFugitiveEmissions == null) {
			if (other.pm25PrimaryFugitiveEmissions != null)
				return false;
		} else if (!pm25PrimaryFugitiveEmissions.equals(other.pm25PrimaryFugitiveEmissions))
			return false;
		if (pm25PrimaryStackEmissions == null) {
			if (other.pm25PrimaryStackEmissions != null)
				return false;
		} else if (!pm25PrimaryStackEmissions.equals(other.pm25PrimaryStackEmissions))
			return false;
		if (pmPrimaryFugitiveEmissions == null) {
			if (other.pmPrimaryFugitiveEmissions != null)
				return false;
		} else if (!pmPrimaryFugitiveEmissions.equals(other.pmPrimaryFugitiveEmissions))
			return false;
		if (pmPrimaryStackEmissions == null) {
			if (other.pmPrimaryStackEmissions != null)
				return false;
		} else if (!pmPrimaryStackEmissions.equals(other.pmPrimaryStackEmissions))
			return false;
		if (processID == null) {
			if (other.processID != null)
				return false;
		} else if (!processID.equals(other.processID))
			return false;
		if (regulatoryRequirement == null) {
			if (other.regulatoryRequirement != null)
				return false;
		} else if (!regulatoryRequirement.equals(other.regulatoryRequirement))
			return false;
		if (springPct == null) {
			if (other.springPct != null)
				return false;
		} else if (!springPct.equals(other.springPct))
			return false;
		if (summerPct == null) {
			if (other.summerPct != null)
				return false;
		} else if (!summerPct.equals(other.summerPct))
			return false;
		if (throughput == null) {
			if (other.throughput != null)
				return false;
		} else if (!throughput.equals(other.throughput))
			return false;
		if (winterPct == null) {
			if (other.winterPct != null)
				return false;
		} else if (!winterPct.equals(other.winterPct))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EmissionInventory [inventoryYear=" + inventoryYear + ", contentType=" + contentType
				+ ", regulatoryRequirement=" + regulatoryRequirement + ", eiFileName=" + eiFileName
				+ ", attachmentFiles=" + attachmentFiles + ", facilityID=" + facilityID + ", euID=" + euID
				+ ", processID=" + processID + ", maxHours=" + maxHours + ", maxDays=" + maxDays + ", maxWeeks="
				+ maxWeeks + ", actualHours=" + actualHours + ", winterPct=" + winterPct + ", springPct=" + springPct
				+ ", summerPct=" + summerPct + ", fallPct=" + fallPct + ", material=" + material + ", throughput="
				+ throughput + ", calculationMethod=" + calculationMethod + ", fireVariableList=" + fireVariableList
				+ ", pmPrimaryStackEmissions=" + pmPrimaryStackEmissions + ", pmPrimaryFugitiveEmissions="
				+ pmPrimaryFugitiveEmissions + ", pm10PrimaryStackEmissions=" + pm10PrimaryStackEmissions
				+ ", pm10PrimaryFugitiveEmissions=" + pm10PrimaryFugitiveEmissions + ", pm25PrimaryStackEmissions="
				+ pm25PrimaryStackEmissions + ", pm25PrimaryFugitiveEmissions=" + pm25PrimaryFugitiveEmissions + "]";
	}

	class FireVariable implements Serializable {
		
		private static final long serialVersionUID = 1L;

		private String variableID;
		private Integer value;
		
		public FireVariable(String variableID, Integer value) {
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
		
		public Integer getValue() {
			return value;
		}
		
		public void setValue(Integer value) {
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

		private EmissionInventoryRow getOuterType() {
			return EmissionInventoryRow.this;
		}

		@Override
		public String toString() {
			return "FireVariable [variableID=" + variableID + ", value=" + value + "]";
		}		

	}

}
