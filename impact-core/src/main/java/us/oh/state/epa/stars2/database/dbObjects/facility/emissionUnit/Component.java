package us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.wy.state.deq.impact.def.FugitiveComponentDef;

@SuppressWarnings("serial")
public class Component extends BaseDB {
	private Integer emuId;
	private String componentCd;
	private Integer gas = 0;
	private Integer heavyOil = 0;
	private Integer lightOil = 0;
	private Integer water = 0;
	private Integer MAX_VAL_INTEGER = new Integer(999999);
	
	public Component(Integer emuId, String componentCd){
		this.emuId = emuId;
		this.componentCd = componentCd;
	}
	
	public Component(){

	}

	@Override
	public void populate(ResultSet rs) throws SQLException{
		try {
			setEmuId(AbstractDAO.getInteger(rs, "emu_id"));
			setComponentCd(rs.getString("component_cd"));
			setGas(AbstractDAO.getInteger(rs, "gas_cnt"));
			setHeavyOil(AbstractDAO.getInteger(rs, "ho_cnt"));
			setLightOil(AbstractDAO.getInteger(rs, "lo_cnt"));
			setWater(AbstractDAO.getInteger(rs, "water_cnt"));
		} catch (SQLException sqle) {
			logger.warn(sqle.getMessage());
		}
	}
	
	public Integer getEmuId() {
		return emuId;
	}

	public void setEmuId(Integer emuId) {
		this.emuId = emuId;
	}

	public String getComponentCd() {
		return componentCd;
	}

	public void setComponentCd(String componentCd) {
		this.componentCd = componentCd;
	}

	public Integer getGas() {
		return gas;
	}

	public void setGas(Integer gas) {
		this.gas = gas;
	}

	public Integer getHeavyOil() {
		return heavyOil;
	}

	public void setHeavyOil(Integer heavyOil) {
		this.heavyOil = heavyOil;
	}

	public Integer getLightOil() {
		return lightOil;
	}

	public void setLightOil(Integer lightOil) {
		this.lightOil = lightOil;
	}

	public Integer getWater() {
		return water;
	}

	public void setWater(Integer water) {
		this.water = water;
	}

	public String getComponentName(){
		String ret = FugitiveComponentDef.getData().getItems().getItemDesc(componentCd);
		return ret;
	}

	//Override Equals method in Component Class
	@Override
	public int hashCode() {
		final int prime = 51;
		int result = super.hashCode();
		result = prime * result + ((emuId == null) ? 0 : emuId.hashCode());
		result = prime * result + ((componentCd == null) ? 0 : componentCd.hashCode());
		result = prime * result + ((gas == null) ? 0 : gas.hashCode());
		result = prime * result + ((heavyOil == null) ? 0 : heavyOil.hashCode());
		result = prime * result + ((lightOil == null) ? 0 : lightOil.hashCode());
		result = prime * result + ((water == null) ? 0 : water.hashCode());
		return result;
	}
	
	//The equals method also compare emuId
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Component other = (Component) obj;
		if (emuId == null){
			if (other.emuId != null){
				return false;
			}
		} else if (!emuId.equals(other.emuId)){
			return false;
		}	
		if (componentCd == null){
			if (other.componentCd != null){
				return false;
			}
		} else if (!componentCd.equals(other.componentCd)){
			return false;
		}		
		if (gas == null){
			if (other.gas != null){
				return false;
			}
		} else if (!gas.equals(other.gas)){
			return false;
		}	
		if (heavyOil == null){
			if (other.heavyOil != null){
				return false;
			}
		} else if (!heavyOil.equals(other.heavyOil)){
			return false;
		}			
		if (lightOil == null){
			if (other.lightOil != null){
				return false;
			}
		} else if (!lightOil.equals(other.lightOil)){
			return false;
		}				
		if (water == null){
			if (other.water != null){
				return false;
			}
		} else if (!water.equals(other.water)){
			return false;
		}						

		return true;
	}


	public ValidationMessage[] validate(){
		clearValidationMessages();
		String componentDesc = FugitiveComponentDef.getData().getItems().getItemDesc(componentCd);
		
		requiredField(gas, componentCd+"-gas", componentDesc + " - Gas");
		checkRangeValues(gas, new Integer(0), MAX_VAL_INTEGER, componentCd+"gas", componentDesc + " - Gas");
		
		requiredField(heavyOil, componentCd+"heavyOil", componentDesc + " - Heavy Oil");
		checkRangeValues(heavyOil, new Integer(0), MAX_VAL_INTEGER, componentCd+"heavyOil", componentDesc + " - Heavy Oil");
		
		requiredField(lightOil, componentCd+"lightOil", componentDesc + " - Light Oil");
		checkRangeValues(lightOil, new Integer(0), MAX_VAL_INTEGER, componentCd+"lightOil", componentDesc + " - Light Oil");
		
		requiredField(water, componentCd+"water", componentDesc + " - Water/Condensate");
		checkRangeValues(water, new Integer(0), MAX_VAL_INTEGER, componentCd+"water", componentDesc + " - Water/Condensate");
		return super.validate();
	}

}
	
	