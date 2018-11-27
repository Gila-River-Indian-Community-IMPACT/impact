package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCNonChargePollutant;


public class EmissionsTable implements Comparable<EmissionsTable> {
	
    private Logger logger = Logger.getLogger(this.getClass());
	
	private String pollutantCd;
	private String pollutantDesc;
	private String parentDesc;

	private int firstHalfPercent;
	private int secondHalfPercent;

	private BigDecimal firstHalfFee = BigDecimal.ZERO;
	private BigDecimal secondHalfFee = BigDecimal.ZERO;
	private BigDecimal totalFeePerPollutant = BigDecimal.ZERO;

	private BigDecimal firstHalfActualTotalTons = BigDecimal.ZERO;
	private BigDecimal secondHalfActualTotalTons = BigDecimal.ZERO;
	private BigDecimal actualTotalTons = BigDecimal.ZERO;
	
	private BigDecimal firstHalfChargeableTotalTons = BigDecimal.ZERO;
	private BigDecimal nonChargeableFirstHalfFromParent = BigDecimal.ZERO;
	private BigDecimal secondHalfChargeableTotalTons = BigDecimal.ZERO;
	private BigDecimal nonChargeableSecondHalfFromParent = BigDecimal.ZERO;
	private BigDecimal chargeableTotalTons = BigDecimal.ZERO;
	private String chargeableTotalTonsStr;
	
	private BigDecimal allowableTotalTons = BigDecimal.ZERO;
	private BigDecimal firstHalfAllowableTotalTons = BigDecimal.ZERO;
	private BigDecimal secondHalfAllowableTotalTons = BigDecimal.ZERO;

	private BigDecimal firstHalfFugitiveEmissions = BigDecimal.ZERO;
	private BigDecimal secondHalfFugitiveEmissions = BigDecimal.ZERO;
	private BigDecimal fugitiveEmissions = BigDecimal.ZERO;
	private BigDecimal chargeableFugitiveEmissions = BigDecimal.ZERO;
	private BigDecimal nonChargeableFugitiveAmountFromParent = BigDecimal.ZERO;

	private BigDecimal firstHalfStackEmissions = BigDecimal.ZERO;
	private BigDecimal secondHalfStackEmissions = BigDecimal.ZERO;
	private BigDecimal stackEmissions = BigDecimal.ZERO;
	private BigDecimal chargeableStackEmissions = BigDecimal.ZERO;
	private BigDecimal nonChargeableStackAmountFromParent = BigDecimal.ZERO;;

	private boolean allowableIsUsedToCalculate;
	private boolean pollutantNonChargeableInSC;
	private boolean pollutantNonChargeableDueToSets;

    private boolean seen = false;
    private boolean reportable = false;

    private int numGens = 0;
    private HashMap<String, EmissionsTable> childEmissions = new HashMap<String, EmissionsTable>();

    private List<SCNonChargePollutant> mySCNonChargePollutantList;
    private List<SCNonChargePollutant> parentSCNonChargePollutantList;
    private List<Emissions> emissionsList = new ArrayList<Emissions>();

	public EmissionsTable() {
		super();
	}

	public EmissionsTable(String pollutantCd, String pollutantDesc, int firstHalfPercent, int secondHalfPercent,
			BigDecimal firstHalfFee, BigDecimal secondHalfFee, BigDecimal totalFeePerPollutant,
			BigDecimal firstHalfActualTotalTons, BigDecimal secondHalfActualTotalTons, BigDecimal actualTotalTons,
			BigDecimal firstHalfChargeableTotalTons, BigDecimal secondHalfChargeableTotalTons,
			BigDecimal chargeableTotalTons, BigDecimal allowableTotalTons, BigDecimal firstHalfAllowableTotalTons,
			BigDecimal secondHalfAllowableTotalTons, BigDecimal fugitiveEmissions, BigDecimal stackEmissions,
			BigDecimal chargeableFugitiveEmissions, BigDecimal chargeableStackEmissions,
			boolean allowableIsUsedToCalculate, boolean pollutantNonChargeableInSC,
			boolean pollutantNonChargeableDueToSets, boolean reportable, int numGens,
			HashMap<String, EmissionsTable> childEmissions, List<SCNonChargePollutant> mySCNonChargePollutant) {
		
		super();
		this.pollutantCd = pollutantCd;
		this.pollutantDesc = pollutantDesc;
		this.firstHalfPercent = firstHalfPercent;
		this.secondHalfPercent = secondHalfPercent;
		this.firstHalfFee = firstHalfFee;
		this.secondHalfFee = secondHalfFee;
		this.totalFeePerPollutant = totalFeePerPollutant;
		this.firstHalfActualTotalTons = firstHalfActualTotalTons;
		this.secondHalfActualTotalTons = secondHalfActualTotalTons;
		this.actualTotalTons = actualTotalTons;
		this.firstHalfChargeableTotalTons = firstHalfChargeableTotalTons;
		this.secondHalfChargeableTotalTons = secondHalfChargeableTotalTons;
		this.chargeableTotalTons = chargeableTotalTons;
		this.allowableTotalTons = allowableTotalTons;
		this.firstHalfAllowableTotalTons = firstHalfAllowableTotalTons;
		this.secondHalfAllowableTotalTons = secondHalfAllowableTotalTons;
		this.fugitiveEmissions = fugitiveEmissions;
		this.stackEmissions = stackEmissions;
		this.chargeableFugitiveEmissions = chargeableFugitiveEmissions;
		this.chargeableStackEmissions = chargeableStackEmissions;
		this.allowableIsUsedToCalculate = allowableIsUsedToCalculate;
		this.pollutantNonChargeableInSC = pollutantNonChargeableInSC;
		this.pollutantNonChargeableDueToSets = pollutantNonChargeableDueToSets;
		this.reportable = reportable;
		this.numGens = numGens;
		this.childEmissions = childEmissions;
		this.mySCNonChargePollutantList = mySCNonChargePollutant;
	}

	public int compareTo(EmissionsTable et) {
		return this.pollutantDesc.compareToIgnoreCase(et.pollutantDesc);
	}

	public final String getPollutantCd() {
		return pollutantCd;
	}

	public final void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}

	public final String getPollutantDesc() {
		return pollutantDesc;
	}

	public final void setPollutantDesc(String pollutantDesc) {
		this.pollutantDesc = pollutantDesc;
	}

	public final String getParentDesc() {
		return parentDesc;
	}

	public final void setParentDesc(String parentDesc) {
		this.parentDesc = parentDesc;
	}
	
	public final String getFullPollutantDesc() {
		String ret = pollutantDesc;
		if (parentDesc != null && parentDesc.length() > 0) {
			ret = pollutantDesc + " (See: " + parentDesc + ")";
		}
		return ret; 
	}

	public final BigDecimal getFirstHalfFee() {
		if (firstHalfFee == null) {
			firstHalfFee = new BigDecimal(0);
		}
		return firstHalfFee;
	}

	public final void setFirstHalfFee(BigDecimal firstHalfFee) {
		this.firstHalfFee = firstHalfFee;
	}

	public final BigDecimal getSecondHalfFee() {
		if (secondHalfFee == null) {
			secondHalfFee = new BigDecimal(0);
		}
		return secondHalfFee;
	}

	public final void setSecondHalfFee(BigDecimal secondHalfFee) {
		this.secondHalfFee = secondHalfFee;
	}

	public BigDecimal getTotalFeePerPollutant() {
		return totalFeePerPollutant;
	}

	public void setTotalFeePerPollutant(BigDecimal totalFeePerPollutant) {
		this.totalFeePerPollutant = totalFeePerPollutant;
	}
	
	public String getTotalFeePerPollutantFormatted() {
		return NumberFormat.getCurrencyInstance().format(totalFeePerPollutant.setScale(2, RoundingMode.HALF_UP));
	}
	
	public void setTotalFeePerPollutantFormatted(String fee) {
		// Ignored.
	}

	public boolean isAllowableIsUsedToCalculate() {
		return allowableIsUsedToCalculate;
	}

	public void setAllowableIsUsedToCalculate(boolean allowableIsUsedToCalculate) {
		this.allowableIsUsedToCalculate = allowableIsUsedToCalculate;
	}
	
	public boolean isPollutantNonChargeableInSC() {
		return pollutantNonChargeableInSC;
	}

	public void setPollutantNonChargeableInSC(boolean pollutantNonChargeableInSC) {
		this.pollutantNonChargeableInSC = pollutantNonChargeableInSC;
	}
	
	public boolean isPollutantNonChargeableDueToSets() {
		return pollutantNonChargeableDueToSets;
	}

	public void setPollutantNonChargeableDueToSets(boolean pollutantNonChargeableDueToSets) {
		this.pollutantNonChargeableDueToSets = pollutantNonChargeableDueToSets;
	}
	
	
	public double parseDoubleSafely(String str) {
		double result = 0;
		try {
			result = Double.parseDouble(str.replaceAll(",", ""));
		} catch (NullPointerException npe) {
			logger.debug("parseDoubleSafely NullPointerException ", npe);
		} catch (NumberFormatException nfe) {
			logger.debug("parseDoubleSafely NumberFormatException ", nfe);
		}
		return result;
	}

	public final boolean isSeen() {
		return seen;
	}

	public final void setSeen(boolean seen) {
		this.seen = seen;
	}

	public final int getNumGens() {
		return numGens;
	}

	public final void setNumGens(int numGens) {
		this.numGens = numGens;
	}

	public final boolean isReportable() {
		return reportable;
	}

	public final void setReportable(boolean reportable) {
		this.reportable = reportable;
	}

	public final HashMap<String, EmissionsTable> getChildEmissions() {
		return childEmissions;
	}

	public final void setChildEmissions(HashMap<String, EmissionsTable> childEmissions) {
		this.childEmissions = childEmissions;
	}
	
	public final EmissionsTable getChildEmissionTable(String pollutantCd) {
		return childEmissions.get(pollutantCd);
	}
	
	public final void addChildEmissionsTable(EmissionsTable eTable) {
		if (eTable != null) {
			childEmissions.put(eTable.getPollutantCd(), eTable);
		}
	}

    public final List<SCNonChargePollutant> getMySCNonChargePollutantList() {
		return mySCNonChargePollutantList;
	}

	public final void setMySCNonChargePollutantList(List<SCNonChargePollutant> mySCNonChargePollutantList) {
		if (mySCNonChargePollutantList == null) {
			this.mySCNonChargePollutantList = new ArrayList<SCNonChargePollutant>();
		} else {
			this.mySCNonChargePollutantList = mySCNonChargePollutantList;			
		}
	}
	
	public final void addMySCNonChargePollutantList(List<SCNonChargePollutant> mySCNonChargePollutantList) {

		if (this.mySCNonChargePollutantList == null) {
			this.mySCNonChargePollutantList = new ArrayList<SCNonChargePollutant>();
		}
		this.mySCNonChargePollutantList.addAll(mySCNonChargePollutantList);

	}

    public final List<SCNonChargePollutant> getParentSCNonChargePollutantList() {
		return parentSCNonChargePollutantList;
	}

	public final void setParentSCNonChargePollutantList(List<SCNonChargePollutant> parentSCNonChargePollutantList) {
		if (parentSCNonChargePollutantList == null) {
			this.parentSCNonChargePollutantList = new ArrayList<SCNonChargePollutant>();
		} else {
			this.parentSCNonChargePollutantList = parentSCNonChargePollutantList;			
		}
	}
	
	public final void addParentSCNonChargePollutantList(List<SCNonChargePollutant> parentSCNonChargePollutantList) {

		if (this.parentSCNonChargePollutantList == null) {
			this.parentSCNonChargePollutantList = new ArrayList<SCNonChargePollutant>();
		}
		this.parentSCNonChargePollutantList.addAll(parentSCNonChargePollutantList);

	}

	public int getFirstHalfPercent() {
		return firstHalfPercent;
	}

	public void setFirstHalfPercent(int firstHalfPercent) {
		this.firstHalfPercent = firstHalfPercent;
	}

	public int getSecondHalfPercent() {
		return secondHalfPercent;
	}

	public void setSecondHalfPercent(int secondHalfPercent) {
		this.secondHalfPercent = secondHalfPercent;
	}

	public final BigDecimal getFirstHalfActualTotalTons() {
		return firstHalfActualTotalTons;
	}

	public final void setFirstHalfActualTotalTons(BigDecimal firstHalfActualTotalTons) {
		this.firstHalfActualTotalTons = firstHalfActualTotalTons;
	}

	public final String getFirstHalfActualTotalTonsStr() {
		return firstHalfActualTotalTons.toPlainString();
	}

	public final void setFirstHalfActualTotalTonsStr(String firstHalfActualTotalTons) {
		// Do nothing. All calcs done using BigDecimal type.
	}

	public final BigDecimal getSecondHalfActualTotalTons() {
		return secondHalfActualTotalTons;
	}

	public final void setSecondHalfActualTotalTons(BigDecimal secondHalfActualTotalTons) {
		this.secondHalfActualTotalTons = secondHalfActualTotalTons;
	}

	public final String getSecondHalfActualTotalTonsStr() {
		return secondHalfActualTotalTons.toPlainString();
	}

	public final void setSecondHalfActualTotalTonsStr(String secondHalfActualTotalTons) {
		// Do nothing. All calcs done using BigDecimal type.
	}

	public final BigDecimal getActualTotalTons() {
		return actualTotalTons;
	}

	public final void setActualTotalTons(BigDecimal actualTotalTons) {
		this.actualTotalTons = actualTotalTons;
	}

	public final String getActualTotalTonsStr() {
		return actualTotalTons.toPlainString();
	}

	public final void setActualTotalTonsStr(String actualTotalTons) {
		// Do nothing. All calcs done using BigDecimal type.
	}

	public final BigDecimal getFirstHalfChargeableTotalTons() {
		return firstHalfChargeableTotalTons;
	}

	public final void setFirstHalfChargeableTotalTons(BigDecimal firstHalfChargeableTotalTons) {
		this.firstHalfChargeableTotalTons = firstHalfChargeableTotalTons;
	}

	public final BigDecimal getNonChargeableFirstHalfFromParent() {
		return nonChargeableFirstHalfFromParent;
	}

	public final void setNonChargeableFirstHalfFromParent(BigDecimal nonChargeableFirstHalfFromParent) {
		this.nonChargeableFirstHalfFromParent = nonChargeableFirstHalfFromParent;
	}

	public final BigDecimal getSecondHalfChargeableTotalTons() {
		return secondHalfChargeableTotalTons;
	}

	public final void setSecondHalfChargeableTotalTons(BigDecimal secondHalfChargeableTotalTons) {
		this.secondHalfChargeableTotalTons = secondHalfChargeableTotalTons;
	}

	public final BigDecimal getNonChargeableSecondHalfFromParent() {
		return nonChargeableSecondHalfFromParent;
	}

	public final void setNonChargeableSecondHalfFromParent(BigDecimal nonChargeableSecondHalfFromParent) {
		this.nonChargeableSecondHalfFromParent = nonChargeableSecondHalfFromParent;
	}

	public final BigDecimal getChargeableTotalTons() {
		return chargeableTotalTons;
	}

	public final void setChargeableTotalTons(BigDecimal chargeableTotalTons) {
		this.chargeableTotalTons = chargeableTotalTons;
	}

	public final String getChargeableTotalTonsStr() {
		return chargeableTotalTonsStr;
	}

	public final void setChargeableTotalTonsStr(String chargeableTotalTons) {
		this.chargeableTotalTonsStr = chargeableTotalTons;
	}

	public final BigDecimal getAllowableTotalTons() {
		return allowableTotalTons;
	}

	public final void setAllowableTotalTons(BigDecimal allowableTotalTons) {
		this.allowableTotalTons = allowableTotalTons;
	}

	public final BigDecimal getFirstHalfAllowableTotalTons() {
		return firstHalfAllowableTotalTons;
	}

	public final void setFirstHalfAllowableTotalTons(BigDecimal firstHalfAllowableTotalTons) {
		this.firstHalfAllowableTotalTons = firstHalfAllowableTotalTons;
	}

	public final BigDecimal getSecondHalfAllowableTotalTons() {
		return secondHalfAllowableTotalTons;
	}

	public final void setSecondHalfAllowableTotalTons(BigDecimal secondHalfAllowableTotalTons) {
		this.secondHalfAllowableTotalTons = secondHalfAllowableTotalTons;
	}

	public final BigDecimal getFirstHalfFugitiveEmissions() {
		return firstHalfFugitiveEmissions;
	}

	public final void setFirstHalfFugitiveEmissions(BigDecimal firstHalfFugitiveEmissions) {
		this.firstHalfFugitiveEmissions = firstHalfFugitiveEmissions;
	}

	public final BigDecimal getSecondHalfFugitiveEmissions() {
		return secondHalfFugitiveEmissions;
	}

	public final void setSecondHalfFugitiveEmissions(BigDecimal secondHalfFugitiveEmissions) {
		this.secondHalfFugitiveEmissions = secondHalfFugitiveEmissions;
	}

	public final BigDecimal getFugitiveEmissions() {
		return fugitiveEmissions;
	}

	public final void setFugitiveEmissions(BigDecimal fugitiveEmissions) {
		this.fugitiveEmissions = fugitiveEmissions;
	}

	public final BigDecimal getFirstHalfStackEmissions() {
		return firstHalfStackEmissions;
	}

	public final void setFirstHalfStackEmissions(BigDecimal firstHalfStackEmissions) {
		this.firstHalfStackEmissions = firstHalfStackEmissions;
	}

	public final BigDecimal getSecondHalfStackEmissions() {
		return secondHalfStackEmissions;
	}

	public final void setSecondHalfStackEmissions(BigDecimal secondHalfStackEmissions) {
		this.secondHalfStackEmissions = secondHalfStackEmissions;
	}

	public final BigDecimal getStackEmissions() {
		return stackEmissions;
	}

	public final void setStackEmissions(BigDecimal stackEmissions) {
		this.stackEmissions = stackEmissions;
	}

	public final BigDecimal getChargeableFugitiveEmissions() {
		return chargeableFugitiveEmissions;
	}

	public final void setChargeableFugitiveEmissions(BigDecimal chargeableFugitiveEmissions) {
		this.chargeableFugitiveEmissions = chargeableFugitiveEmissions;
	}

	public final BigDecimal getNonChargeableFugitiveAmountFromParent() {
		return nonChargeableFugitiveAmountFromParent;
	}

	public final void setNonChargeableFugitiveAmountFromParent(BigDecimal nonChargeableFugitiveAmountFromParent) {
		this.nonChargeableFugitiveAmountFromParent = nonChargeableFugitiveAmountFromParent;
	}

	public final BigDecimal getChargeableStackEmissions() {
		return chargeableStackEmissions;
	}

	public final void setChargeableStackEmissions(BigDecimal chargeableStackEmissions) {
		this.chargeableStackEmissions = chargeableStackEmissions;
	}
	
	public final BigDecimal getNonChargeableStackAmountFromParent() {
		return nonChargeableStackAmountFromParent;
	}

	public final void setNonChargeableStackAmountFromParent(BigDecimal nonChargeableStackAmountFromParent) {
		this.nonChargeableStackAmountFromParent = nonChargeableStackAmountFromParent;
	}

	public final void addEmission(Emissions emission) {
		emissionsList.add(emission);
	}
	
	public final List<Emissions> getEmissions() {
		return emissionsList;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actualTotalTons == null) ? 0 : actualTotalTons.hashCode());
		result = prime * result + (allowableIsUsedToCalculate ? 1231 : 1237);
		result = prime * result + ((allowableTotalTons == null) ? 0 : allowableTotalTons.hashCode());
		result = prime * result + ((chargeableFugitiveEmissions == null) ? 0 : chargeableFugitiveEmissions.hashCode());
		result = prime * result + ((chargeableStackEmissions == null) ? 0 : chargeableStackEmissions.hashCode());
		result = prime * result + ((chargeableTotalTons == null) ? 0 : chargeableTotalTons.hashCode());
		result = prime * result + ((childEmissions == null) ? 0 : childEmissions.hashCode());
		result = prime * result + ((firstHalfActualTotalTons == null) ? 0 : firstHalfActualTotalTons.hashCode());
		result = prime * result + ((firstHalfAllowableTotalTons == null) ? 0 : firstHalfAllowableTotalTons.hashCode());
		result = prime * result
				+ ((firstHalfChargeableTotalTons == null) ? 0 : firstHalfChargeableTotalTons.hashCode());
		result = prime * result + ((firstHalfFee == null) ? 0 : firstHalfFee.hashCode());
		result = prime * result + firstHalfPercent;
		result = prime * result + ((fugitiveEmissions == null) ? 0 : fugitiveEmissions.hashCode());
		result = prime * result + numGens;
		result = prime * result + ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
		result = prime * result + ((pollutantDesc == null) ? 0 : pollutantDesc.hashCode());
		result = prime * result + (pollutantNonChargeableDueToSets ? 1231 : 1237);
		result = prime * result + (pollutantNonChargeableInSC ? 1231 : 1237);
		result = prime * result + (reportable ? 1231 : 1237);
		result = prime * result + ((mySCNonChargePollutantList == null) ? 0 : mySCNonChargePollutantList.hashCode());
		result = prime * result + ((secondHalfActualTotalTons == null) ? 0 : secondHalfActualTotalTons.hashCode());
		result = prime * result
				+ ((secondHalfAllowableTotalTons == null) ? 0 : secondHalfAllowableTotalTons.hashCode());
		result = prime * result
				+ ((secondHalfChargeableTotalTons == null) ? 0 : secondHalfChargeableTotalTons.hashCode());
		result = prime * result + ((secondHalfFee == null) ? 0 : secondHalfFee.hashCode());
		result = prime * result + secondHalfPercent;
		result = prime * result + ((stackEmissions == null) ? 0 : stackEmissions.hashCode());
		result = prime * result + ((totalFeePerPollutant == null) ? 0 : totalFeePerPollutant.hashCode());
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
		EmissionsTable other = (EmissionsTable) obj;
		if (actualTotalTons == null) {
			if (other.actualTotalTons != null)
				return false;
		} else if (!actualTotalTons.equals(other.actualTotalTons))
			return false;
		if (allowableIsUsedToCalculate != other.allowableIsUsedToCalculate)
			return false;
		if (allowableTotalTons == null) {
			if (other.allowableTotalTons != null)
				return false;
		} else if (!allowableTotalTons.equals(other.allowableTotalTons))
			return false;
		if (chargeableFugitiveEmissions == null) {
			if (other.chargeableFugitiveEmissions != null)
				return false;
		} else if (!chargeableFugitiveEmissions.equals(other.chargeableFugitiveEmissions))
			return false;
		if (chargeableStackEmissions == null) {
			if (other.chargeableStackEmissions != null)
				return false;
		} else if (!chargeableStackEmissions.equals(other.chargeableStackEmissions))
			return false;
		if (chargeableTotalTons == null) {
			if (other.chargeableTotalTons != null)
				return false;
		} else if (!chargeableTotalTons.equals(other.chargeableTotalTons))
			return false;
		if (childEmissions == null) {
			if (other.childEmissions != null)
				return false;
		} else if (!childEmissions.equals(other.childEmissions))
			return false;
		if (firstHalfActualTotalTons == null) {
			if (other.firstHalfActualTotalTons != null)
				return false;
		} else if (!firstHalfActualTotalTons.equals(other.firstHalfActualTotalTons))
			return false;
		if (firstHalfAllowableTotalTons == null) {
			if (other.firstHalfAllowableTotalTons != null)
				return false;
		} else if (!firstHalfAllowableTotalTons.equals(other.firstHalfAllowableTotalTons))
			return false;
		if (firstHalfChargeableTotalTons == null) {
			if (other.firstHalfChargeableTotalTons != null)
				return false;
		} else if (!firstHalfChargeableTotalTons.equals(other.firstHalfChargeableTotalTons))
			return false;
		if (firstHalfFee == null) {
			if (other.firstHalfFee != null)
				return false;
		} else if (!firstHalfFee.equals(other.firstHalfFee))
			return false;
		if (firstHalfPercent != other.firstHalfPercent)
			return false;
		if (fugitiveEmissions == null) {
			if (other.fugitiveEmissions != null)
				return false;
		} else if (!fugitiveEmissions.equals(other.fugitiveEmissions))
			return false;
		if (numGens != other.numGens)
			return false;
		if (pollutantCd == null) {
			if (other.pollutantCd != null)
				return false;
		} else if (!pollutantCd.equals(other.pollutantCd))
			return false;
		if (pollutantDesc == null) {
			if (other.pollutantDesc != null)
				return false;
		} else if (!pollutantDesc.equals(other.pollutantDesc))
			return false;
		if (pollutantNonChargeableDueToSets != other.pollutantNonChargeableDueToSets)
			return false;
		if (pollutantNonChargeableInSC != other.pollutantNonChargeableInSC)
			return false;
		if (reportable != other.reportable)
			return false;
		if (mySCNonChargePollutantList == null) {
			if (other.mySCNonChargePollutantList != null)
				return false;
		} else if (!mySCNonChargePollutantList.equals(other.mySCNonChargePollutantList))
			return false;
		if (secondHalfActualTotalTons == null) {
			if (other.secondHalfActualTotalTons != null)
				return false;
		} else if (!secondHalfActualTotalTons.equals(other.secondHalfActualTotalTons))
			return false;
		if (secondHalfAllowableTotalTons == null) {
			if (other.secondHalfAllowableTotalTons != null)
				return false;
		} else if (!secondHalfAllowableTotalTons.equals(other.secondHalfAllowableTotalTons))
			return false;
		if (secondHalfChargeableTotalTons == null) {
			if (other.secondHalfChargeableTotalTons != null)
				return false;
		} else if (!secondHalfChargeableTotalTons.equals(other.secondHalfChargeableTotalTons))
			return false;
		if (secondHalfFee == null) {
			if (other.secondHalfFee != null)
				return false;
		} else if (!secondHalfFee.equals(other.secondHalfFee))
			return false;
		if (secondHalfPercent != other.secondHalfPercent)
			return false;
		if (stackEmissions == null) {
			if (other.stackEmissions != null)
				return false;
		} else if (!stackEmissions.equals(other.stackEmissions))
			return false;
		if (totalFeePerPollutant == null) {
			if (other.totalFeePerPollutant != null)
				return false;
		} else if (!totalFeePerPollutant.equals(other.totalFeePerPollutant))
			return false;
		return true;
	}


}
