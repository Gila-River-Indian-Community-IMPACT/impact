package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.PollutantDef;

@SuppressWarnings("serial")
public class ApplicationEUMaterialUsed extends BaseDB {
    private int applicationEuId;
    private String materialUsedCd;
    private String materialAmount;
    private String unitCd;
    public ApplicationEUMaterialUsed() {
        super();
        setMaterialAmount("0");
    }

    public ApplicationEUMaterialUsed(Integer applicationEUID, String materialUsedCd) {
        this.applicationEuId = applicationEUID;
        this.materialUsedCd = materialUsedCd;
        setMaterialAmount("0");
    
    }
    
    public ApplicationEUMaterialUsed(ApplicationEUMaterialUsed old) {
        super(old);
        setApplicationEuId(old.getApplicationEuId());
        setMaterialUsedCd(old.getMaterialUsedCd());
        setMaterialAmount(old.getMaterialAmount());
        setUnitCd(old.getUnitCd());
    }

    public void populate(ResultSet rs) throws SQLException {
        setApplicationEuId(AbstractDAO.getInteger(rs, "application_eu_id"));
        setMaterialUsedCd(rs.getString("material_used_cd"));
        setMaterialAmount(rs.getString("material_amount"));
        setUnitCd(rs.getString("unit_cd"));
        setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
    }

    public final int getApplicationEuId() {
        return applicationEuId;
    }

    public final void setApplicationEuId(int applicationEUID) {
        this.applicationEuId = applicationEUID;
    }
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME
                * result
                + ((materialUsedCd == null) ? 0 : materialUsedCd
                        .hashCode());
        result = PRIME
                * result
                + ((materialAmount == null) ? 0 : materialAmount
                        .hashCode());
        result = PRIME
                * result
                + ((unitCd == null) ? 0 : unitCd
                        .hashCode());
        result = PRIME * result + applicationEuId;
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
    	final ApplicationEUMaterialUsed other = (ApplicationEUMaterialUsed) obj;
    	if (materialUsedCd == null) {
    		if (other.materialUsedCd != null)
    			return false;
    	} else if (!materialUsedCd.equals(other.materialUsedCd))
    		return false;
    	if (materialAmount == null) {
    		if (other.materialAmount != null)
    			return false;
    	} else if (!materialAmount.equals(other.materialAmount))
    		return false;
    	if (applicationEuId != other.applicationEuId)
    		return false;
    	if (unitCd == null) {
    		if (other.unitCd != null)
    			return false;
    	} else if (!unitCd.equals(other.unitCd))
    		return false;
    	return true;
    }

    @Override
    public final ValidationMessage[] validate() {
        clearValidationMessages();
        requiredField(applicationEuId, "applicationEuId", "Application EU ID");
        requiredField(materialUsedCd, "materialUsedCd", 
                "Material Used");
        requiredField(materialAmount, "materialAmount", 
                "Material Amount");
        requiredField(unitCd, "unitCd", 
                "Units");
        return super.validate();
    }

  
    public boolean isValueSpecified() {
        return (materialAmount != null && !materialAmount.equals("0"));
    }

	public String getUnitCd() {
		return unitCd;
	}

	public void setUnitCd(String unitCd) {
		this.unitCd = unitCd;
	}

	public String getMaterialUsedCd() {
		return materialUsedCd;
	}

	public void setMaterialUsedCd(String materialUsedCd) {
		this.materialUsedCd = materialUsedCd;
	}

	public String getMaterialAmount() {
		return materialAmount;
	}

	public void setMaterialAmount(String materialAmount) {
		this.materialAmount = materialAmount;
	}
}
