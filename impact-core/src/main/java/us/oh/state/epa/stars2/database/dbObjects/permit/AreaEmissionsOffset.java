package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.util.List;

import us.oh.state.epa.stars2.def.OffsetTrackingNonAttainmentAreaDef;

public class AreaEmissionsOffset {
	
	private String nonAttainmentAreaCd;
	private String attainmentStandardCd;
	private List<EmissionsOffset> emissionsOffsets;
	
	public AreaEmissionsOffset() {
		super();
	}
	
	public AreaEmissionsOffset(AreaEmissionsOffset old) {
		super();
		if(null != old) {
			setNonAttainmentAreaCd(old.getNonAttainmentAreaCd());
			setAttainmentStandardCd(old.getAttainmentStandardCd());
			setEmissionsOffsets(emissionsOffsets);
		}
	}
	
	public String getNonAttainmentAreaCd() {
		return nonAttainmentAreaCd;
	}

	public void setNonAttainmentAreaCd(String nonAttainmentAreaCd) {
		this.nonAttainmentAreaCd = nonAttainmentAreaCd;
	}

	public String getAttainmentStandardCd() {
		return attainmentStandardCd;
	}

	public void setAttainmentStandardCd(String attainmentStandardCd) {
		this.attainmentStandardCd = attainmentStandardCd;
	}

	public List<EmissionsOffset> getEmissionsOffsets() {
		return emissionsOffsets;
	}

	public void setEmissionsOffsets(List<EmissionsOffset> emissionsOffsets) {
		this.emissionsOffsets = emissionsOffsets;
	}
	
	public String getSectionLabel() {
		String ret = null;
		if(null != nonAttainmentAreaCd) {
			ret = "Non-Attainment area: "
					+ OffsetTrackingNonAttainmentAreaDef.getData().
						getItems().getDescFromAllItem(nonAttainmentAreaCd);
		}
		
		return ret;
	}
	
}
