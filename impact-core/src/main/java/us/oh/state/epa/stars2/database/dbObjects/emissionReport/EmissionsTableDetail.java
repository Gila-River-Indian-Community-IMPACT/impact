package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.math.BigDecimal;

public class EmissionsTableDetail implements Comparable<EmissionsTableDetail> {
	
	private String pollutantCd;
	private String pollutantDesc;

	private Integer emuID;
	private String epaEmuID;

	private String processID;
	private Double fugitiveEmissions;
	private Double stackEmissions;
	private String firstHalfActualTotalTons;
	private String secondHalfActualTotalTons;
	private String actualTotalTons;
	private String firstHalfAllowableTotalTons;
	private String secondHalfAllowableTotalTons;
	private String allowableTotalTons;
	private String firstHalfFee;
	private String secondHalfFee;
	private String totalFeePerPollutant;

	public EmissionsTableDetail() {
		super();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((actualTotalTons == null) ? 0 : actualTotalTons.hashCode());
		result = prime
				* result
				+ ((allowableTotalTons == null) ? 0 : allowableTotalTons
						.hashCode());
		result = prime * result + ((emuID == null) ? 0 : emuID.hashCode());
		result = prime * result
				+ ((epaEmuID == null) ? 0 : epaEmuID.hashCode());
		result = prime
				* result
				+ ((firstHalfActualTotalTons == null) ? 0
						: firstHalfActualTotalTons.hashCode());
		result = prime
				* result
				+ ((firstHalfAllowableTotalTons == null) ? 0
						: firstHalfAllowableTotalTons.hashCode());
		result = prime * result
				+ ((firstHalfFee == null) ? 0 : firstHalfFee.hashCode());
		result = prime
				* result
				+ ((fugitiveEmissions == null) ? 0 : fugitiveEmissions
						.hashCode());
		result = prime * result
				+ ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
		result = prime * result
				+ ((pollutantDesc == null) ? 0 : pollutantDesc.hashCode());
		result = prime * result
				+ ((processID == null) ? 0 : processID.hashCode());
		result = prime
				* result
				+ ((secondHalfActualTotalTons == null) ? 0
						: secondHalfActualTotalTons.hashCode());
		result = prime
				* result
				+ ((secondHalfAllowableTotalTons == null) ? 0
						: secondHalfAllowableTotalTons.hashCode());
		result = prime * result
				+ ((secondHalfFee == null) ? 0 : secondHalfFee.hashCode());
		result = prime * result
				+ ((stackEmissions == null) ? 0 : stackEmissions.hashCode());
		result = prime
				* result
				+ ((totalFeePerPollutant == null) ? 0 : totalFeePerPollutant
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof EmissionsTableDetail)) {
			return false;
		}
		EmissionsTableDetail other = (EmissionsTableDetail) obj;
		if (actualTotalTons == null) {
			if (other.actualTotalTons != null) {
				return false;
			}
		} else if (!actualTotalTons.equals(other.actualTotalTons)) {
			return false;
		}
		if (allowableTotalTons == null) {
			if (other.allowableTotalTons != null) {
				return false;
			}
		} else if (!allowableTotalTons.equals(other.allowableTotalTons)) {
			return false;
		}
		if (emuID == null) {
			if (other.emuID != null) {
				return false;
			}
		} else if (!emuID.equals(other.emuID)) {
			return false;
		}
		if (epaEmuID == null) {
			if (other.epaEmuID != null) {
				return false;
			}
		} else if (!epaEmuID.equals(other.epaEmuID)) {
			return false;
		}
		if (firstHalfActualTotalTons == null) {
			if (other.firstHalfActualTotalTons != null) {
				return false;
			}
		} else if (!firstHalfActualTotalTons
				.equals(other.firstHalfActualTotalTons)) {
			return false;
		}
		if (firstHalfAllowableTotalTons == null) {
			if (other.firstHalfAllowableTotalTons != null) {
				return false;
			}
		} else if (!firstHalfAllowableTotalTons
				.equals(other.firstHalfAllowableTotalTons)) {
			return false;
		}
		if (firstHalfFee == null) {
			if (other.firstHalfFee != null) {
				return false;
			}
		} else if (!firstHalfFee.equals(other.firstHalfFee)) {
			return false;
		}
		if (fugitiveEmissions == null) {
			if (other.fugitiveEmissions != null) {
				return false;
			}
		} else if (!fugitiveEmissions.equals(other.fugitiveEmissions)) {
			return false;
		}
		if (pollutantCd == null) {
			if (other.pollutantCd != null) {
				return false;
			}
		} else if (!pollutantCd.equals(other.pollutantCd)) {
			return false;
		}
		if (pollutantDesc == null) {
			if (other.pollutantDesc != null) {
				return false;
			}
		} else if (!pollutantDesc.equals(other.pollutantDesc)) {
			return false;
		}
		if (processID == null) {
			if (other.processID != null) {
				return false;
			}
		} else if (!processID.equals(other.processID)) {
			return false;
		}
		if (secondHalfActualTotalTons == null) {
			if (other.secondHalfActualTotalTons != null) {
				return false;
			}
		} else if (!secondHalfActualTotalTons
				.equals(other.secondHalfActualTotalTons)) {
			return false;
		}
		if (secondHalfAllowableTotalTons == null) {
			if (other.secondHalfAllowableTotalTons != null) {
				return false;
			}
		} else if (!secondHalfAllowableTotalTons
				.equals(other.secondHalfAllowableTotalTons)) {
			return false;
		}
		if (secondHalfFee == null) {
			if (other.secondHalfFee != null) {
				return false;
			}
		} else if (!secondHalfFee.equals(other.secondHalfFee)) {
			return false;
		}
		if (stackEmissions == null) {
			if (other.stackEmissions != null) {
				return false;
			}
		} else if (!stackEmissions.equals(other.stackEmissions)) {
			return false;
		}
		if (totalFeePerPollutant == null) {
			if (other.totalFeePerPollutant != null) {
				return false;
			}
		} else if (!totalFeePerPollutant.equals(other.totalFeePerPollutant)) {
			return false;
		}
		return true;
	}

	public String getPollutantCd() {
		return pollutantCd;
	}

	public void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}

	public String getPollutantDesc() {
		return pollutantDesc;
	}

	public void setPollutantDesc(String pollutantDesc) {
		this.pollutantDesc = pollutantDesc;
	}

	public Integer getEmuID() {
		return emuID;
	}

	public void setEmuID(Integer emuID) {
		this.emuID = emuID;
	}

	public String getEpaEmuID() {
		return epaEmuID;
	}

	public void setEpaEmuID(String epaEmuID) {
		this.epaEmuID = epaEmuID;
	}

	public String getProcessID() {
		return processID;
	}

	public void setProcessID(String processID) {
		this.processID = processID;
	}

	public Double getFugitiveEmissions() {
		return fugitiveEmissions;
	}

	public void setFugitiveEmissions(Double fugitiveEmissions) {
		this.fugitiveEmissions = fugitiveEmissions;
	}

	public Double getStackEmissions() {
		return stackEmissions;
	}

	public void setStackEmissions(Double stackEmissions) {
		this.stackEmissions = stackEmissions;
	}

	public String getFirstHalfActualTotalTons() {
		return firstHalfActualTotalTons;
	}

	public void setFirstHalfActualTotalTons(String firstHalfActualTotalTons) {
		this.firstHalfActualTotalTons = firstHalfActualTotalTons;
	}

	public String getSecondHalfActualTotalTons() {
		return secondHalfActualTotalTons;
	}

	public void setSecondHalfActualTotalTons(String secondHalfActualTotalTons) {
		this.secondHalfActualTotalTons = secondHalfActualTotalTons;
	}

	public String getActualTotalTons() {
		return actualTotalTons;
	}

	public void setActualTotalTons(String actualTotalTons) {
		this.actualTotalTons = actualTotalTons;
	}

	public String getFirstHalfAllowableTotalTons() {
		return firstHalfAllowableTotalTons;
	}

	public void setFirstHalfAllowableTotalTons(
			String firstHalfAllowableTotalTons) {
		this.firstHalfAllowableTotalTons = firstHalfAllowableTotalTons;
	}

	public String getSecondHalfAllowableTotalTons() {
		return secondHalfAllowableTotalTons;
	}

	public void setSecondHalfAllowableTotalTons(
			String secondHalfAllowableTotalTons) {
		this.secondHalfAllowableTotalTons = secondHalfAllowableTotalTons;
	}

	public String getAllowableTotalTons() {
		return allowableTotalTons;
	}

	public void setAllowableTotalTons(String allowableTotalTons) {
		this.allowableTotalTons = allowableTotalTons;
	}

	public String getFirstHalfFee() {
		return firstHalfFee;
	}

	public void setFirstHalfFee(String firstHalfFee) {
		this.firstHalfFee = firstHalfFee;
	}

	public String getSecondHalfFee() {
		return secondHalfFee;
	}

	public void setSecondHalfFee(String secondHalfFee) {
		this.secondHalfFee = secondHalfFee;
	}

	public String getTotalFeePerPollutant() {
		return totalFeePerPollutant;
	}

	public void setTotalFeePerPollutant(String totalFeePerPollutant) {
		this.totalFeePerPollutant = totalFeePerPollutant;
	}

	public int compareTo(EmissionsTableDetail etd) {
		return this.pollutantDesc.compareToIgnoreCase(etd.pollutantDesc);
	}
	
	public BigDecimal getFirstHalfActualTotalTonsNumeric() {
		BigDecimal bd = null;
		if (null != this.firstHalfActualTotalTons) {
			String str = this.firstHalfActualTotalTons.replace(",", "");
			bd = new BigDecimal(str);
		}
		return bd;
	}

	public BigDecimal getSecondHalfActualTotalTonsNumeric() {
		BigDecimal bd = null;
		if (null != this.secondHalfActualTotalTons) {
			String str = this.secondHalfActualTotalTons.replace(",", "");
			bd = new BigDecimal(str);
		}
		return bd;
	}

	public BigDecimal getActualTotalTonsNumeric() {
		BigDecimal bd = null;
		if (null != this.actualTotalTons) {
			String str = this.actualTotalTons.replace(",", "");
			bd = new BigDecimal(str);
		}
		return bd;
	}

	public BigDecimal getAllowableTotalTonssNumeric() {
		BigDecimal bd = null;
		if (null != this.allowableTotalTons) {
			String str = this.allowableTotalTons.replace(",", "");
			bd = new BigDecimal(str);
		}
		return bd;
	}

}
