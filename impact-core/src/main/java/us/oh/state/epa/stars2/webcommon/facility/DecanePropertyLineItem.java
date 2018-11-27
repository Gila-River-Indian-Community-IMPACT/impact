package us.oh.state.epa.stars2.webcommon.facility;

import us.oh.state.epa.stars2.framework.util.Utility;

public class DecanePropertyLineItem {

	private String label;
	private String oilCondenstanteValue;
	private String producedWaterValue;

	public DecanePropertyLineItem() {
		super();
	}

	public DecanePropertyLineItem(String label, String oilCondenstanteValue, String producedWaterValue) {
		super();
		this.label = label;
		this.oilCondenstanteValue = oilCondenstanteValue;
		this.producedWaterValue = producedWaterValue;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getOilCondenstanteValue() {
		return oilCondenstanteValue;
	}

	public void setOilCondenstanteValue(String oilCondenstanteValue) {
		if (Utility.isNullOrEmpty(oilCondenstanteValue)) {
			this.oilCondenstanteValue = null;
		} else {
			this.oilCondenstanteValue = oilCondenstanteValue;
		}
	}

	public String getProducedWaterValue() {
		return producedWaterValue;
	}

	public void setProducedWaterValue(String producedWaterValue) {
		if(Utility.isNullOrEmpty(producedWaterValue)) {
			this.producedWaterValue = null;
		} else {
			this.producedWaterValue = producedWaterValue;
		}
	}
}
