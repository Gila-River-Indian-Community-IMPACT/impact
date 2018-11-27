package us.oh.state.epa.stars2.webcommon;

import java.util.List;

import us.oh.state.epa.stars2.bo.FacilityBO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRelationship;

@SuppressWarnings("serial")
public class FacilityEmissionFlow implements java.io.Serializable {
	public static String CE_TYPE = "Control";
	public static String STACK_TYPE = "Stack";
	private FacilityRelationship relationship;
	private String type;
	private String percent;
    private Float percentValue;
	private String id;
	
	public FacilityEmissionFlow() {
		
	}
	
	public FacilityEmissionFlow(String type, String id, FacilityRelationship rel) {
		relationship = rel;
		this.type = type;
		this.id = id;
	}
    
    public FacilityEmissionFlow(FacilityEmissionFlow fef) {
        relationship = fef.relationship;
        this.type = fef.type;
        this.id = fef.id;
        this.percent = "0";
        this.percentValue = 0f;
    }
    
    // Return false if all percents are now zero.
    // aAdjust percents so they add to 100%
    // Note that String percent will not be used after calling this function.
    public static boolean normalize(List<FacilityEmissionFlow> fefs) {
        if(fefs.size() == 0) return false;
        float sum = 0f;
        for(FacilityEmissionFlow fef : fefs) {
            if(fef.getPercentValue() != null) {
                sum = sum + fef.getPercentValue();
            }
        }
        if(sum == 0f) return true;
        for(FacilityEmissionFlow fef : fefs) {
            fef.percentValue = fef.percentValue/sum*100f;
            fef.percent = "notUsed";
        }
        return false;
    }
	
	/* type : FacilityEmissionFlow.CE_TYPe or FacilityEmissionFlow.STACK_TYPE */
    public final static FacilityEmissionFlow getEmissionFlow(
    		List<FacilityEmissionFlow> emissionFlows, String type, String id) {
    	FacilityEmissionFlow ret = null;
    	if (type == null || id == null) {
    		return ret;
    	}
    	for (FacilityEmissionFlow emFlow: emissionFlows) {
    		if (emFlow.getType().equals(type) && emFlow.getId() != null && emFlow.getId().equals(id)) {
    			ret = emFlow;
    		}
    	}
    	return ret;
    }
	
	public String getPercent() {
		return percent;
	}
    
	public void setPercent(String percent) {
		this.percent = percent;
        if(percent != null && percent.length() > 0) {
            percentValue = Float.parseFloat(percent);
        } else {
            percentValue = 0f;
            relationship.setFlowFactor(FacilityBO.MISSING_FLOW_PERCENT);
        }
	}
    
    public void setPercents(String percent, float percentVal) {
        this.percent = percent;
        this.percentValue = percentVal;
    }
    
	public FacilityRelationship getRelationship() {
		return relationship;
	}
	public void setRelationship(FacilityRelationship relationship) {
		this.relationship = relationship;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Float getFlowFactor() {
		return relationship.getFlowFactor();
	}
	
	public void setFlowFactor(Float flowFact) {
		relationship.setFlowFactor(flowFact);
	}

	public String getFlowFactorStr() {
		return BaseDB.numberToString(new Double(getFlowFactor()));
	}

    public Float getPercentValue() {
        return percentValue;
    }
}
