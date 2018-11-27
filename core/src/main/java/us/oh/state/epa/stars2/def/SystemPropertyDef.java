package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.util.Utility;

public class SystemPropertyDef extends SimpleDef {
	
	private static final long serialVersionUID = -8010171108895320839L;
	private static Logger logger = Logger.getLogger(SystemPropertyDef.class);

	private static final String defName = "SystemPropertyDef";

	private String systemPropExtDesc;
	
	private static HashMap<String, String> propsHash;
	
	private static final String getSystemPropertyValue(final String code) {

		if (propsHash == null) {
			propsHash = new HashMap<String, String>();
		}

		DefData sysProps = getData();
		if (sysProps.isRefresh()) {

			synchronized (SystemPropertyDef.class) {
				
				if (sysProps.isRefresh()) {

					sysProps.reload();

					propsHash = new HashMap<String, String>();
					sysProps = getData();
					DefSelectItems sysPropDef = sysProps.getItems();
					if (sysPropDef != null) {
						Iterator<SelectItem> itr = sysPropDef.getAllItems().iterator();
						while (itr.hasNext()) {
							SelectItem item = itr.next();
							if (item.getValue() != null && !item.isDisabled()) {
								propsHash.put(((String) item.getValue()).toUpperCase(), item.getLabel());
							}
						}
					}
					
					sysProps.setRefresh(false);
				}

			}

		}

		String sysPropsVal = propsHash.get(code.toUpperCase());

		if (Utility.isNullOrEmpty(sysPropsVal)) {
			String error = "System propery " + code + " not found in the system properties definition list.";
			logger.error(error);
		}

		return sysPropsVal;
	}

	public final String getSystemPropExtDesc() {
		return systemPropExtDesc;
	}

	public final void setSystemPropExtDesc(String systemPropVal) {
		this.systemPropExtDesc = systemPropVal;
	}

	public final static List<SelectItem> getAllData() {
		return getData().getItems().getAllItems();
	}

	public static DefData getData() {
		
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("InfrastructureSQL.retrieveSystemProperties", SystemPropertyDef.class);
			cfgMgr.addDef(defName, data);
		}
		
		return data;
	}	
   
	public final void populate(final ResultSet rs) {
		
		super.populate(rs);
		try {
			setSystemPropExtDesc(rs.getString("sys_prop_desc"));
			if (propsHash == null) {
				propsHash = new HashMap<String, String>();
			}
			propsHash.put(this.getCode().toUpperCase(), this.getDescription());
		} catch (SQLException sqle) {
			logger.warn("Unable to retrieve from DB: " + sqle.getMessage(), sqle);
		}

	}
		
	/**
	 * 
	 * @param code
	 * @param defaultVal
	 * @return Returns the value of the given system property if found, otherwise returns defaultVal 
	 */
	public static final String getSystemPropertyValue(final String code, final String defaultVal) {

		String ret = getSystemPropertyValue(code);

		if (ret == null) {
			logger.error("No system property found for code " + code + ". Using default value of " + defaultVal + ".");
			ret = defaultVal;
		}

		return ret;
	}
	
	public static final Boolean getSystemPropertyValueAsBoolean(final String code, final Boolean defaultVal) {
		
		Boolean ret = null;

		String val = getSystemPropertyValue(code);
		if (val == null) {
			logger.error("No system property found for code " + code + ". Using default value of " + defaultVal + ".");
			return defaultVal;
		}

		if (val.equalsIgnoreCase("Y") || val.equalsIgnoreCase("YES") 
				|| val.equalsIgnoreCase("T") || val.equalsIgnoreCase("TRUE")) {
			ret = new Boolean(true);
		} else if (val.equalsIgnoreCase("N") || val.equalsIgnoreCase("NO") 
				|| val.equalsIgnoreCase("F") || val.equalsIgnoreCase("FALSE")) {
			ret = new Boolean(false);
		} else {
			logger.error("System property " + code + "with value " + val + " cannot be interpreted as a Boolean value. Using default value of " + defaultVal + ".");			
			ret = defaultVal;
		}
		
		return ret;
	}

	public static final Date getSystemPropertyValueAsDate(final String code, final Date defaultVal) {
		
		Date ret = null;

		String val = getSystemPropertyValue(code);
		if (val == null) {
			logger.error("No system property found for code " + code + ". Using default value of " + defaultVal + ".");
			return defaultVal;
		}

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			ret = dateFormat.parse(val);
		} catch (Exception e) {
			logger.error("System property " + code + "with value " + val + " cannot be interpreted as a Date value. Using default value of " + defaultVal + ".");			
			ret = defaultVal;
		}
		
		return ret;
	}

	public static final Float getSystemPropertyValueAsFloat(final String code, final Float defaultVal) {

		Float ret = null;

		String val = getSystemPropertyValue(code);
		if (val == null) {
			logger.error("No system property found for code " + code + ". Using default value of " + defaultVal + ".");
			return defaultVal;
		}

		try {
			ret = Float.valueOf(val);
		} catch (Exception e) {
			logger.error("System property " + code + "with value " + val + " cannot be interpreted as a Float value. Using default value of " + defaultVal + ".");			
			ret = defaultVal;
		}
		
		return ret;
	}

	public static final Integer getSystemPropertyValueAsInteger(final String code, final Integer defaultVal) {
		
		Integer ret = null;

		String val = getSystemPropertyValue(code);
		if (val == null) {
			logger.error("No system property found for code " + code + ". Using default value of " + defaultVal + ".");
			return defaultVal;
		}

		try {
			ret = Integer.valueOf(val);
		} catch (Exception e) {
			logger.error("System property " + code + "with value " + val + " cannot be interpreted as an Integer value. Using default value of " + defaultVal + ".");			
			ret = defaultVal;
		}
		
		return ret;
	}
	
	public static final Double getSystemPropertyValueAsDouble(final String code, final Double defaultVal){

		Double ret = null;

		String val = getSystemPropertyValue(code);
		if (val == null) {
			logger.error("No system property found for code " + code + ". Using default value of " + defaultVal + ".");
			return defaultVal;
		}

		try {
			ret = Double.valueOf(val);
		} catch (Exception e) {
			logger.error("System property " + code + "with value " + val + " cannot be interpreted as a Float value. Using default value of " + defaultVal + ".");			
			ret = defaultVal;
		}
		
		return ret;
	}

}
