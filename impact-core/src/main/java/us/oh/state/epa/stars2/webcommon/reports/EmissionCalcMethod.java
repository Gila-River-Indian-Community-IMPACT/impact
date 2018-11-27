package us.oh.state.epa.stars2.webcommon.reports;

import java.io.Serializable;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class EmissionCalcMethod implements Comparable<EmissionCalcMethod>,
		Serializable {
	private String methodCd;
	private String methodDsc;
	private transient Logger logger;
	private boolean recommended;
	private String reason;
	private Integer priority;

	public EmissionCalcMethod() {
		logger = Logger.getLogger(this.getClass());
	}

	public String getMethodCd() {
		return methodCd;
	}

	public void setMethodCd(String methodCd) {
		this.methodCd = methodCd;
	}

	public String getMethodDsc() {
		return methodDsc;
	}

	public void setMethodDsc(String methodDsc) {
		this.methodDsc = methodDsc;
	}

	public boolean isRecommended() {
		return recommended;
	}

	public void setRecommended(boolean recommended) {
		this.recommended = recommended;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public final int compareTo(EmissionCalcMethod compObj) {
		if (null != priority) {
			if (null != compObj.priority) {
				return priority.compareTo(compObj.priority);
			}
			return -1; 
		}
		return -1;
	}

}
