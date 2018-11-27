package us.wy.state.deq.impact.database.dbObjects.company;

import java.io.Serializable;
import java.util.List;

import us.oh.state.epa.stars2.def.OffsetTrackingNonAttainmentAreaDef;

public class AreaEmissionsOffset implements Serializable {
	
	private static final long serialVersionUID = -6782898867718612902L;

	private String nonAttainmentAreaCd;
	private String attainmentStandardCd;
	private List<CompanyEmissionsOffsetRow> emissionsOffsetRowList;
	
	public AreaEmissionsOffset() {
		super();
	}
	
	public AreaEmissionsOffset(AreaEmissionsOffset old) {
		super();
		if(null != old) {
			setNonAttainmentAreaCd(old.getNonAttainmentAreaCd());
			setAttainmentStandardCd(old.getAttainmentStandardCd());
			setEmissionsOffsetRowList(old.getEmissionsOffsetRowList());
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
	
	public List<CompanyEmissionsOffsetRow> getEmissionsOffsetRowList() {
		return emissionsOffsetRowList;
	}
	
	public void setEmissionsOffsetRowList(
			List<CompanyEmissionsOffsetRow> emissionsOffsetRowList) {
		this.emissionsOffsetRowList = emissionsOffsetRowList;
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
