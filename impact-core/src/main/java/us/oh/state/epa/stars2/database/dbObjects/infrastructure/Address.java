package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.PlssConversion;
import us.oh.state.epa.stars2.def.Country;
import us.oh.state.epa.stars2.def.State;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.util.LocationCalculator;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.wy.state.deq.impact.App;

/**
 * Address.
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version 1.0
 * @author Andrew Wilcox
 */
public class Address extends BaseDB {
	
	private static final long serialVersionUID = -136574425712813627L;
	//If there is any change of the following static field name, 
	//please make corresponding change in Company.basicValidations() & Contact.validate()
	private static final String stateField = "state";
	private static final String countyCdField = "countyCd";
	private static final String zipCodeField = "zipCode";
	private static final String latitudeField = "latitude";
	private static final String longitudeField = "longitude";
	private static final String sectionField = "section";
	private static final String townshipField = "township";
	private static final String rangeField = "range";
	private static final String beginDateField = "start_dt";
	private static final String districtField = "districtCd";
	private static final String latLongRange = "latLongRange";

	private Float latitudeMax;
	private Float latitudeMin;
	private Float longitudeMax;
	private Float longitudeMin;
	
	private static final int sectionMax = (int) 36;
	private static final int sectionMin = (int) 1;

	private final String outOfStateCountyCd = "24";
	private final String unknownDistrictCd = "D9";

	private Integer addressId;
	private String attentionName;
	private String addressLine1;
	private String addressLine2;
	private String cityName;
	private String state;
	private String zipCode5;
	private String zipCode4;
	private String zipCode;
	private String countyCd;
	private String countyName;
	private String districtCd = SystemPropertyDef.getSystemPropertyValue(districtField,null);

	private String countryCd;
	private Timestamp beginDate;
	private Timestamp endDate;
	private String latitude;
	private String longitude;
	private String latlong;
	private String quarterQuarter;
	private String quarter;
	private String section;
	private String township;
	private String range;
	private String distance;
	private String facilityName;

	private Boolean isMatch;
	
	private String indianReservationCd;
	
	public Address() {

		super();

		latitudeMax = SystemPropertyDef.getSystemPropertyValueAsFloat("MaxLatitude", null);
		latitudeMin = SystemPropertyDef.getSystemPropertyValueAsFloat("MinLatitude", null);
		longitudeMax = SystemPropertyDef.getSystemPropertyValueAsFloat("MaxLongitude", null);
		longitudeMin = SystemPropertyDef.getSystemPropertyValueAsFloat("MinLongitude", null);

		requiredFields();
	}

	public Address(Address old) {

		super(old);

		latitudeMax = SystemPropertyDef.getSystemPropertyValueAsFloat("MaxLatitude", null);
		latitudeMin = SystemPropertyDef.getSystemPropertyValueAsFloat("MinLatitude", null);
		longitudeMax = SystemPropertyDef.getSystemPropertyValueAsFloat("MaxLongitude", null);
		longitudeMin = SystemPropertyDef.getSystemPropertyValueAsFloat("MinLongitude", null);

		if (old != null) {
			setAddressId(old.getAddressId());
			setAttentionName(old.getAttentionName());
			setAddressLine1(old.getAddressLine1());
			setAddressLine2(old.getAddressLine2());
			setCityName(old.getCityName());
			setState(old.getState());
			setZipCode5(old.getZipCode5());
			setZipCode4(old.getZipCode4());
			setZipCode(old.getZipCode());
			setCountyCd(old.getCountyCd());
			setCountyName(old.getCountyName());
			setCountryCd(old.getCountryCd());
			setBeginDate(old.getBeginDate());
			setEndDate(old.getEndDate());
			setLatitude(old.getLatitude());
			setLongitude(old.getLongitude());
			setLatlong(old.getLatlong());
			setQuarterQuarter(old.getQuarterQuarter());
			setQuarter(old.getQuarter());
			setSection(old.getSection());
			setTownship(old.getTownship());
			setRange(old.getRange());
			setDistrictCd(old.getDistrictCd());
			setIndianReservationCd(old.getIndianReservationCd());
		}
		requiredFields();
	}

	public Address(us.oh.state.epa.portal.base.Address coreAddr) {

		super();

		latitudeMax = SystemPropertyDef.getSystemPropertyValueAsFloat("MaxLatitude", null);
		latitudeMin = SystemPropertyDef.getSystemPropertyValueAsFloat("MinLatitude", null);
		longitudeMax = SystemPropertyDef.getSystemPropertyValueAsFloat("MaxLongitude", null);
		longitudeMin = SystemPropertyDef.getSystemPropertyValueAsFloat("MinLongitude", null);

		if (coreAddr != null) {
			setAddressLine1(coreAddr.getAddressLine1());
			setAddressLine2(coreAddr.getAddressLine2());
			setCityName(coreAddr.getCity());
			if (coreAddr.getState() != null) {
				setState(coreAddr.getState());
			} else {
				setState(State.DEFAULT_STATE);
			}
			setZipCode(coreAddr.getZip());
			setCountyCd(coreAddr.getCounty());
			setCountryCd("US");
		}
		requiredFields();
	}

	private void requiredFields() {
		requiredField(state, stateField, "State");
		requiredField(countyCd, countyCdField, "County");
		requiredField(latitude, latitudeField, "Latitude");
		requiredField(longitude, longitudeField, "Longitude");
		requiredField(section, sectionField, "Section");
		requiredField(township, townshipField, "Township");
		requiredField(range, rangeField, "Range");
		requiredField(beginDate, beginDateField, "Effective Date");
//		requiredField(districtCd, distictField, "Distict");
	}

	public  void copyAddress(Address old) {
		if (old != null) {
			setAttentionName(old.getAttentionName());
			setAddressLine1(old.getAddressLine1());
			setAddressLine2(old.getAddressLine2());
			setCityName(old.getCityName());
			setState(old.getState());
			setZipCode5(old.getZipCode5());
			setZipCode4(old.getZipCode4());
			setZipCode(old.getZipCode());
			setCountyCd(old.getCountyCd());
			setCountyName(old.getCountyName());
			setCountryCd(old.getCountryCd());
			setLatitude(old.getLatitude());
			setLongitude(old.getLongitude());
			setLatlong(old.getLatlong());
			setQuarterQuarter(old.getQuarterQuarter());
			setQuarter(old.getQuarter());
			setSection(old.getSection());
			setTownship(old.getTownship());
			setRange(old.getRange());
			setDistrictCd(old.getDistrictCd());
			setIndianReservationCd(old.getIndianReservationCd());
		}
		requiredFields();
	}

	public  String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(addressLine1);
		sb.append("\n");

		if (addressLine2 != null) {
			sb.append(addressLine2);
			sb.append("\n");
		}
		sb.append(cityName);
		sb.append(", ");
		sb.append(state);
		sb.append(" ");
		sb.append(zipCode5);
		if (zipCode4 != null) {
			sb.append("-" + zipCode4);
		}
		sb.append("\n");

		return sb.toString();
	}
	
	public  String getZipCode9() {
        String rtn;
        if (zipCode4 != null) {
            rtn = zipCode5 + zipCode4;
        } else {
            rtn = zipCode5 + "    ";
        }
        return rtn;
    }

	public  String getCountryCd() {
		return countryCd;
	}

	public  void setCountryCd(String countryCd) {
		this.countryCd = countryCd;
	}

	/**
	 * @return
	 */
	public  Integer getAddressId() {
		return addressId;
	}

	/**
	 * @param addressId
	 */
	public  void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	/**
	 * @return
	 */
	public  String getAttentionName() {
		return attentionName;
	}

	/**
	 * @param attentionName
	 */
	public  void setAttentionName(String attentionName) {
		this.attentionName = attentionName;
	}

	/**
	 * @return
	 */
	public  String getAddressLine1() {
		return addressLine1;
	}

	/**
	 * @param addressLine1
	 */
	public  void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	/**
	 * @return
	 */
	public  String getAddressLine2() {
		return addressLine2;
	}

	/**
	 * @param addressLine2
	 */
	public  void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	/**
	 * @param fullAddress
	 */
	public  String getFullAddress() {
		if (Utility.isNullOrEmpty(this.addressLine2)) {
			return addressLine1;
		}

		return addressLine1 + " " + addressLine2;
	}

	public  void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public  String getFacilityName() {
		return this.facilityName;
	}

	public  String getGoogleMapsURL() {
		return LocationCalculator.getGoogleMapsURL(this.latitude,
				this.longitude, this.facilityName);
	}

	/**
	 * @return
	 */
	public  String getCityName() {
		return cityName;
	}

	/**
	 * @param cityName
	 */
	public  void setCityName(String cityName) {
		this.cityName = cityName;
	}

	/**
	 * @return
	 */
	public  String getState() {
		return state;
	}

	/**
	 * @param state
	 */
	public  void setState(String state) {
		this.state = state;
	}

	/**
	 * @return
	 */
	public  String getZipCode5() {
		return zipCode5;
	}

	/**
	 * @param zipCode
	 */
	public  void setZipCode4(String zipCode4) {
		this.zipCode4 = zipCode4;
	}

	/**
	 * @return
	 */
	public  String getZipCode4() {
		return zipCode4;
	}

	/**
	 * @param zipCode
	 */
	public  void setZipCode5(String zipCode5) {
		this.zipCode5 = zipCode5;
	}

	/**
	 * @return
	 */
	public  String getCountyCd() {
		return countyCd;
	}

	/**
	 * @param countyId
	 */
	public  void setCountyCd(String countyCd) {
		this.countyCd = countyCd;
	}

	/**
	 * @return
	 */
	public  String getCountyName() {
		return countyName;
	}

	/**
	 * @param countyId
	 */
	public  void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public String getDistrictCd() {
		return districtCd;
	}

	public void setDistrictCd(String districtCd) {
		this.districtCd = districtCd;	
	}

	/**
	 * @return
	 */
	public  Timestamp getBeginDate() {
		return beginDate;
	}

	/**
	 * @param beginDate
	 */
	public  void setBeginDate(Timestamp beginDate) {
		this.beginDate = beginDate;
	}

	/**
	 * @return
	 */
	public  Timestamp getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 */
	public  void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return
	 */
	public  Boolean getIsMatch() {
		validateIsMatch();
		return this.isMatch;
	}

	/**
	 * @param isMatch
	 */
	public  void setIsMatch(Boolean isMatch) {
		this.isMatch = isMatch;
	}

	public  String getZipCode() {
		return zipCode;
	}

	public  void setZipCode(String zipCode) {
		// requiredField(zipCode, zipCodeField, "Zip Code");
		this.zipCode = zipCode;
	}

	public  String getLatitude() {
		return latitude;
	}

	public  void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public  String getLongitude() {
		return longitude;
	}

	public  void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public  String getLatlong() {
		return latlong;
	}

	public  void setLatlong(String latlong) {
		this.latlong = latlong;
	}

	public  String getQuarterQuarter() {
		return this.quarterQuarter;
	}

	public  void setQuarterQuarter(String quarterQuarter) {
		this.quarterQuarter = quarterQuarter;
	}

	public  String getQuarter() {
		return this.quarter;
	}

	public  void setQuarter(String quarter) {
		this.quarter = quarter;
	}

	public  String getSection() {
		return this.section;
	}

	public  void setSection(String section) {
		this.section = section;
	}

	public  String getTownship() {
		return this.township;
	}

	public  void setTownship(String township) {
		this.township = township;
	}

	private void validateTownship(String township) {
		if (township == null || township.equals(""))
			return;

		String matchesRegex = "[0-9][\\d]{0,1}[A-Z]";
		Boolean verify = true;
		if (Pattern.matches(matchesRegex, township)) {
			String splitRegex = "(?<=[0-9])(?=[A-Z])";
			String[] tempArray = township.split(splitRegex);
			int intTownship = Integer.parseInt(tempArray[0]);
			String alphaTownship = tempArray[1];
			if (intTownship < 1)
				verify = false;
			if (!(alphaTownship.equals("N") || alphaTownship.equals("S")))
				verify = false;
		} else {
			verify = false;
		}

		if (!verify) {
			validationMessages
					.put(townshipField,
							new ValidationMessage(
									townshipField,
									"Attribute Township must be of the format D followed by N or S, where D is a positive number. E.g., 45N",
									ValidationMessage.Severity.ERROR, null));
		}
	}

	public  String getRange() {
		return this.range;
	}

	public  void setRange(String range) {
		this.range = range;
	}

	private void validateRange(String range) {
		if (range == null || range.equals(""))
			return;
		String matchesRegex = "[0-9][\\d]{0,2}(\\.5)?[E|W]";
		Boolean verify = true;
		if (Pattern.matches(matchesRegex, range)) {
			String splitRegex = "(?<=[0-9])(?=[A-Z])";
			String[] tempArray = range.split(splitRegex);
			Float digitRange = Float.valueOf(tempArray[0]);
			if (digitRange < 1)
				verify = false;
		} else {
			verify = false;
		}

		if (!verify) {
			validationMessages
					.put(rangeField,
							new ValidationMessage(
									rangeField,
									"Attribute Range must be of the format D or D.5 followed by W or E (without whitespace), where D is a positive number. E.g., 6W or 6.5E",
									ValidationMessage.Severity.ERROR, null));
		}

	}

	public  String getDistance() {
		return this.distance;
	}

	public  void setDistance(String distance) {
		this.distance = distance;
	}

	public  String getPlss() {
		return LocationCalculator.getPlssText(this);
	}

	public void calculatePlss() {

		boolean hasError = false;

		requiredField(latitude, latitudeField, "Latitude");
		checkRangeValues(latitude, latitudeMin, latitudeMax, latitudeField, "Latitude");

		requiredField(longitude, longitudeField, "Longitude");
		checkRangeValues(longitude, longitudeMin, longitudeMax, longitudeField, "Longitude");
		
		if (showValidationMsg(latitudeField))
			hasError = true;

		if (showValidationMsg(longitudeField))
			hasError = true;

		if (hasError)
			return;

		calculateCounty();
		if (!validateLatLongInState()){
			showValidationMsg(latLongRange);
			return;
		}
		
		//TODO getting bean from spring context
		LocationCalculator calculator = App.getApplicationContext().getBean(LocationCalculator.class);
		PlssConversion plss = calculator.calculatePlss(this.latitude,
				this.longitude);
		PlssConversion centroidLatLong = calculator.calculateLatLong(plss.getSection(),plss.getTownship(),plss.getRange());
		quarter=calculateQuarter(new Double(centroidLatLong.getLatitude()),new Double(centroidLatLong.getLongitude()), new Double(this.latitude),new Double(this.longitude));
		if (plss != null && !Utility.isNullOrEmpty(plss.getRange())) {
			this.setRange(plss.getRange());
			this.setTownship(plss.getTownship());
			this.setSection(plss.getSection());
			this.setQuarter(quarter);
			
			calculateIndianReservationCd();
		}
	}

	private boolean showValidationMsg(String validatetionField) {
		List<ValidationMessage> tempMsgList = new ArrayList<ValidationMessage>();

		for (ValidationMessage temp : this.validate()) {
			if (temp.getProperty().equals(validatetionField)) {
				tempMsgList.add(temp);
			}
		}

		if (tempMsgList.size() > 0) {
			for (ValidationMessage temp : tempMsgList) {
				DisplayUtil.displayError("ERROR: " + temp.getMessage(),
						temp.getProperty());
			}
			return true; // has errors
		}
		return false; // no error
	}

	public  void calculateLatLong() {

		boolean hasError = false;

		requiredField(section, sectionField, "Section");
		checkRangeValues(section, sectionMin, sectionMax, sectionField, "Section");

		requiredField(township, townshipField, "Township");
		validateTownship(township);

		requiredField(range, rangeField, "Range");
		validateRange(range);
		
		if (showValidationMsg(sectionField))
			hasError = true;
		if (showValidationMsg(townshipField))
			hasError = true;
		if (showValidationMsg(rangeField))
			hasError = true;

		if (hasError)
			return;

		//TODO getting bean from spring context
		LocationCalculator calculator = App.getApplicationContext().getBean(LocationCalculator.class);
		String strPattern = "^0+";
		PlssConversion plss = calculator.calculateLatLong(this.section.replaceAll(strPattern, ""),
				this.township.replaceAll(strPattern,""), this.range.replaceAll(strPattern, ""));

		if (plss != null && !Utility.isNullOrEmpty(plss.getLatitude())) { //Why only latitude
			this.setLatitude(plss.getLatitude());
			this.setLongitude(plss.getLongitude());
			calculateCounty();
			calculateIndianReservationCd();
		}
	}


	public static String calculateQuarter(double centerLat, double centerLon, double lat, double lon) {
		double diffLat = lat - centerLat;
		double diffLon = lon - centerLon;
		if (diffLon > 0 && diffLat > 0) {
			return "NE";
		}
		if (diffLon < 0 && diffLat > 0) {
			return "NW";
		}
		if (diffLon < 0 && diffLat < 0) {
			return "SW";
		}
		if (diffLon > 0 && diffLat < 0) {
			return "SE";
		}
		return "";
	}

	
	private void calculateCounty() {
		//TODO getting bean from spring context
		LocationCalculator calculator = App.getApplicationContext().getBean(LocationCalculator.class);
		CountyDef county = calculator.calculateCountyCd(this.latitude, this.longitude);
		String countyCd = county.getCountyCd();
		String district = county.getDoLaaCd();

		if (Utility.isNullOrEmpty(countyCd)) {
			countyCd = this.outOfStateCountyCd;
		}

		if (Utility.isNullOrEmpty(district)) {
			district = this.unknownDistrictCd;
		}

		this.setCountyCd(countyCd);
		this.setDistrictCd(district);

		if (countyCd != this.outOfStateCountyCd) {
			this.setState(State.DEFAULT_STATE);
		} else {
			this.setState("");
		}
	}

	public int compareTo(Object obj) {
		if (!addressId.equals(((Address) obj).getAddressId())) {
			if ((attentionName != null)
					&& attentionName.compareTo(((Address) obj)
							.getAttentionName()) != 0) {
				return -1;
			}
			if ((addressLine1 != null)
					&& addressLine1
							.compareTo(((Address) obj).getAddressLine1()) != 0) {
				return -1;
			}
			if (addressLine2 != null) {
				if (((Address) obj).getAddressLine2() == null) {
					return -1;
				} else if (addressLine2.compareTo(((Address) obj)
						.getAddressLine2()) != 0) {
					return -1;
				}
			}
			if ((cityName != null)
					&& cityName.compareTo(((Address) obj).getCityName()) != 0) {
				return -1;
			}
			if ((state != null)
					&& state.compareTo(((Address) obj).getState()) != 0) {
				return -1;
			}
			if ((zipCode5 != null)
					&& zipCode5.compareTo(((Address) obj).getZipCode5()) != 0) {
				return -1;
			}
			if ((countyCd != null)
					&& countyCd.compareTo(((Address) obj).getCountyCd()) != 0) {
				return -1;
			}
		}

		return 0;
	}
	
	public boolean validZipCode() {
//		countryCd = State.getCountryCd(state);
		
		if (Country.US.equals(countryCd)) {
			return validUSZipCode(this.zipCode);
			
		} else if (Country.CA.equals(countryCd)) {
			return validCAZipCode(this.zipCode);
			
		} else {
			logger.error("Country code is not set for address");
		}
		
		return true;
	}
	
	private boolean validUSZipCode(String zipCode) {
		validationMessages.remove(zipCodeField);
		
		if (Utility.isNullOrEmpty(zipCode))
			return false;
		
		String pattern = "^([0-9]{5})|([0-9]{5}\\-?[0-9]{4})$";
		String errorMsg = "Invalid Zip Code: The zip code format must be either 'DDDDD' or 'DDDDD-DDDD' where D is a digit";

		//Zip Code is valid
		if (zipCode.matches(pattern))
			return true;
				
		validationMessages.put(
				zipCodeField, 
				new ValidationMessage(zipCodeField, errorMsg, ValidationMessage.Severity.ERROR, null));
		
		return false;
	}
	
	private boolean validCAZipCode(String zipCode) {
		validationMessages.remove(zipCodeField);
		
		if (Utility.isNullOrEmpty(zipCode))
			return false;
		
	      //Canadian Postal Code in the format of "M3A 1A5"
	      String pattern = "^[ABCEGHJ-NPRSTVXY]{1}[0-9]{1}[ABCEGHJ-NPRSTV-Z]{1}[ ]*[0-9]{1}[ABCEGHJ-NPRSTV-Z]{1}[0-9]{1}$";
	      String errorMsg = "Invalid Zip Code: Canadian zip codes must be of the form 'ADA DAD' where A is a valid zip code letter and D is a digit";
	      
	      //Zip Code is valid
	       if (zipCode.matches(pattern))
	    		return true;
	       
	   	validationMessages.put(
				zipCodeField, 
				new ValidationMessage(zipCodeField, errorMsg, ValidationMessage.Severity.ERROR, null));
	       
		return false;
	}

	private void setUSZipCode(String zipCode) {
		boolean isValid = validUSZipCode(zipCode); // blank zipCode --> not valid, but no validation message
		if (!isValid){
			zipCode5 = null;
			zipCode4 = null;
			return;
		}
		zipCode = zipCode.replaceAll("-", "");
		zipCode5 = zipCode.substring(0, 5);
		zipCode4 = null;
		
		if (zipCode.length() > 5) {
			zipCode4 = zipCode.substring(5);
			setZipCode(zipCode5 + "-" + zipCode4);
			
		} else {
			setZipCode(zipCode5);
			
		}
	}

	private void setCAZipCode(String zipCode) {
	      //Canadian Postal Code in the format of "M3A 1A5"
		zipCode = zipCode.toUpperCase();
		boolean isValid = validCAZipCode(zipCode); // blank zipCode --> not valid, but no validation message
	    
	    if (!isValid) {
//	    	validationMessages.put(
//	    	zipCodeField, 
//	    	new ValidationMessage(zipCodeField, errorMsg, ValidationMessage.Severity.ERROR, null));
	    	zipCode5 = null;
			zipCode4 = null;
	    	return;
	    }

		String tempZipCode = zipCode.replaceAll(" ", "");
		zipCode5 = tempZipCode.substring(0, 3);
		zipCode4 = tempZipCode.substring(3);
		setZipCode(zipCode5 + " " + zipCode4);
	}

	private void internalSetZipCode() {
//		countryCd = State.getCountryCd(state);
		if (Country.US.equals(countryCd)) {
			if (zipCode4 != null && !zipCode4.equals("")) {
				setZipCode(zipCode5 + "-" + zipCode4);
			} else {
				setZipCode(zipCode5);
			}
			setUSZipCode(zipCode);
		} else if (Country.CA.equals(countryCd)) {
			String zipCode = zipCode5 + " " + zipCode4;
			setCAZipCode(zipCode);
		} else {
			logger.error("Country code is not set for address");
		}
	}

	private String formatLatLong(String source) {
		String result = source;

		if (!Utility.isNullOrEmpty(source)) {
			float value = Float.valueOf(source.trim()).floatValue();
			result = String.format("%.5f", value);
		}

		return result;
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
	 */
	public  void populate(ResultSet rs) {
		try {
			setAddressId(AbstractDAO.getInteger(rs, "address_id"));
			setAddressLine1(rs.getString("address1"));
			setAddressLine2(rs.getString("address2"));
			setCityName(rs.getString("city"));
			setState(rs.getString("state_cd"));
			setZipCode5(rs.getString("zip5"));
			setZipCode4(rs.getString("zip4"));
			setCountryCd(rs.getString("country_cd"));
			setCountyCd(rs.getString("county_cd"));
			setBeginDate(rs.getTimestamp("start_dt"));
			setEndDate(rs.getTimestamp("end_dt"));
			setLastModified(AbstractDAO.getInteger(rs, "address_lm"));
			internalSetZipCode();
			setLatitude(formatLatLong(rs.getString("latitude")));
			setLongitude(formatLatLong(rs.getString("longitude")));
			setLatlong(rs.getString("latlong"));
			setQuarterQuarter(rs.getString("quarter_quarter"));
			setQuarter(rs.getString("quarter"));
			setSection(rs.getString("section"));
			setTownship(rs.getString("township"));
			setRange(rs.getString("range"));
			setDistrictCd(rs.getString("do_laa_cd"));
			setIndianReservationCd(rs.getString("indian_reservation_cd"));
		} catch (SQLException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e.getMessage());
			}
		}
		try {
			setCountyName(rs.getString("county_nm"));
		} catch (SQLException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e.getMessage());
			}
		}

	}

	@Override
	public int hashCode() {
		 int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result
				+ ((addressId == null) ? 0 : addressId.hashCode());
		result = PRIME * result
				+ ((addressLine1 == null) ? 0 : addressLine1.hashCode());
		result = PRIME * result
				+ ((addressLine2 == null) ? 0 : addressLine2.hashCode());
		result = PRIME * result
				+ ((attentionName == null) ? 0 : attentionName.hashCode());
		result = PRIME * result
				+ ((beginDate == null) ? 0 : beginDate.hashCode());
		result = PRIME * result
				+ ((cityName == null) ? 0 : cityName.hashCode());
		result = PRIME * result
				+ ((countryCd == null) ? 0 : countryCd.hashCode());
		result = PRIME * result
				+ ((countyCd == null) ? 0 : countyCd.hashCode());
		result = PRIME * result
				+ ((countyName == null) ? 0 : countyName.hashCode());
		result = PRIME * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = PRIME * result + ((state == null) ? 0 : state.hashCode());
		result = PRIME * result
				+ ((zipCode4 == null) ? 0 : zipCode4.hashCode());
		result = PRIME * result
				+ ((zipCode5 == null) ? 0 : zipCode5.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		if (addressId == ((Address) obj).addressId) {
			return true;
		}

		return true;
	}

	public boolean equalsIgnoreCase(Object obj) {
		if (obj == this) {
			return true;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Address other = (Address) obj;

		if (isEmpty(addressLine1)) {
			if (!isEmpty(other.addressLine1))
				return false;
		} else if (!addressLine1.equalsIgnoreCase(other.addressLine1))
			return false;
		if (isEmpty(addressLine2)) {
			if (!isEmpty(other.addressLine2))
				return false;
		} else if (!addressLine2.equalsIgnoreCase(other.addressLine2))
			return false;
		if (isEmpty(cityName)) {
			if (!isEmpty(other.cityName))
				return false;
		} else if (!cityName.equalsIgnoreCase(other.cityName))
			return false;
		if (isEmpty(state)) {
			if (!isEmpty(other.state))
				return false;
		} else if (!state.equalsIgnoreCase(other.state))
			return false;
		if (isEmpty(zipCode4)) {
			if (!isEmpty(other.zipCode4))
				return false;
		} else if (!zipCode4.equalsIgnoreCase(other.zipCode4))
			return false;
		if (isEmpty(zipCode5)) {
			if (!isEmpty(other.zipCode5)) {
				return false;
			}
		} else if (!zipCode5.equalsIgnoreCase(other.zipCode5)) {
			return false;
		}
		if (isEmpty(countyCd)) {
			if (!isEmpty(other.countyCd))
				return false;
		} else if (!countyCd.equalsIgnoreCase(other.countyCd))
			return false;
		if (isEmpty(countyName)) {
			if (!isEmpty(other.countyName))
				return false;
		} else if (!countyName.equalsIgnoreCase(other.countyName))
			return false;
		return true;
	}

	public boolean equalsNotIgnoreCase(Object obj) {
		if (obj == this) {
			return true;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Address other = (Address) obj;

		if (isEmpty(addressLine1)) {
			if (!isEmpty(other.addressLine1))
				return false;
		} else if (!addressLine1.equals(other.addressLine1))
			return false;
		if (isEmpty(addressLine2)) {
			if (!isEmpty(other.addressLine2))
				return false;
		} else if (!addressLine2.equals(other.addressLine2))
			return false;
		if (isEmpty(cityName)) {
			if (!isEmpty(other.cityName))
				return false;
		} else if (!cityName.equals(other.cityName))
			return false;
		if (isEmpty(state)) {
			if (!isEmpty(other.state))
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (isEmpty(zipCode4)) {
			if (!isEmpty(other.zipCode4))
				return false;
		} else if (!zipCode4.equals(other.zipCode4))
			return false;
		if (isEmpty(zipCode5)) {
			if (!isEmpty(other.zipCode5)) {
				return false;
			}
		} else if (!zipCode5.equals(other.zipCode5)) {
			return false;
		}
		if (isEmpty(countyCd)) {
			if (!isEmpty(other.countyCd))
				return false;
		} else if (!countyCd.equals(other.countyCd))
			return false;
		if (isEmpty(countyName)) {
			if (!isEmpty(other.countyName))
				return false;
		} else if (!countyName.equals(other.countyName))
			return false;
		return true;
	}

	public boolean equalsNotIgnoreCaseXcityIgnoreCase(Object obj) {
		if (obj == this) {
			return true;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Address other = (Address) obj;

		if (isEmpty(addressLine1)) {
			if (!isEmpty(other.addressLine1))
				return false;
		} else if (!addressLine1.equals(other.addressLine1))
			return false;
		if (isEmpty(addressLine2)) {
			if (!isEmpty(other.addressLine2))
				return false;
		} else if (!addressLine2.equals(other.addressLine2))
			return false;
		if (isEmpty(cityName)) {
			if (!isEmpty(other.cityName))
				return false;
		} else if (!cityName.equalsIgnoreCase(other.cityName))
			return false;
		if (isEmpty(state)) {
			if (!isEmpty(other.state))
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (isEmpty(zipCode4)) {
			if (!isEmpty(other.zipCode4))
				return false;
		} else if (!zipCode4.equals(other.zipCode4))
			return false;
		if (isEmpty(zipCode5)) {
			if (!isEmpty(other.zipCode5)) {
				return false;
			}
		} else if (!zipCode5.equals(other.zipCode5)) {
			return false;
		}
		if (isEmpty(countyCd)) {
			if (!isEmpty(other.countyCd))
				return false;
		} else if (!countyCd.equals(other.countyCd))
			return false;
		if (isEmpty(countyName)) {
			if (!isEmpty(other.countyName))
				return false;
		} else if (!countyName.equals(other.countyName))
			return false;
		return true;
	}

	private boolean isEmpty(String s) {
		if (s == null || s.length() == 0)
			return true;
		else
			return false;
	}

	/**
	 * Using in permitDetail.java
	 * 
	 * @return Add1, City, State, ZipCode5
	 */
	public  String toShortString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getAddressLine1());
		sb.append(", ");
		sb.append(getCityName());
		sb.append(", ");
		sb.append(getState());
		sb.append(", ");
		sb.append(getZipCode5());
		return sb.toString();

	}

	public void validateIsMatch() {

		this.setIsMatch(true);
		if (Utility.isNullOrEmpty(this.latitude)
				|| Utility.isNullOrEmpty(this.longitude)
				|| Utility.isNullOrEmpty(this.range)
				|| Utility.isNullOrEmpty(this.township)
				|| Utility.isNullOrEmpty(this.section)
				|| Utility.isNullOrEmpty(this.countyCd)
				|| Utility.isNullOrEmpty(this.districtCd)) {
			return;
		}

		//TODO band-aid ... need to seperate validation logic from db object
		LocationCalculator calculator = App.getApplicationContext().getBean(LocationCalculator.class);
		
		PlssConversion plss = calculator.calculatePlss(this.latitude,
				this.longitude);
		//TODO: Strip leading zeroes here
		String strPattern = "^0+";		
		if (plss != null && !Utility.isNullOrEmpty(plss.getRange())) {
			if (!this.getRange().replaceAll(strPattern, "").equals(plss.getRange()))
				this.setIsMatch(false);
			if (!this.getTownship().replaceAll(strPattern, "").equals(plss.getTownship()))
				this.setIsMatch(false);
			if (!this.getSection().replaceAll(strPattern, "").equals(plss.getSection()))
				this.setIsMatch(false);
		}

		if (!this.isMatch)
			return;

		CountyDef county = calculator.calculateCountyCd(this.latitude,
				this.longitude);

		if (Utility.isNullOrEmpty(county.getCountyCd()))
			county.setCountyCd(this.outOfStateCountyCd);

//		if (Utility.isNullOrEmpty(county.getDoLaaCd()))
//			county.setDoLaaCd(this.unknownDistrictCd);

		if (!this.getCountyCd().equals(county.getCountyCd()))
			this.setIsMatch(false);
//		if (!this.getDistrictCd().equals(county.getDoLaaCd()))
//			this.setIsMatch(false);
		if (!this.getState().equals(State.DEFAULT_STATE)
				&& !county.getCountyCd().equals(this.outOfStateCountyCd))
			this.setIsMatch(false);

	}

	public boolean validateAddress() { //Only validate Zip code
		//Based on the countryCd generated from state, check whether Zip code format is valid & setUp zip5/zip4 based on zipCode
		if (state != null) {
			countryCd = State.getCountryCd(state);
			if (countryCd != null) {
				if (countryCd.equals(Country.US)) {
					setUSZipCode(zipCode);
				} else if (countryCd.equals(Country.CA)) {
					setCAZipCode(zipCode);
				} else {
					logger.error("Country code is not valid for address");
				}
			} else {
				logger.error("Country code not found for State Code : " + state);
			}
		}

		return isValid();
	}

//	public ValidationMessage[] validateFacilityAddress() {
//		requiredFields();
//		return new ArrayList<ValidationMessage>(validationMessages.values())
//				.toArray(new ValidationMessage[0]);
//	}
	
	public ValidationMessage[] validateFacilityLocation() {
		this.clearValidationMessages();
		requiredField(latitude, latitudeField, "Latitude");
		requiredField(longitude, longitudeField, "Longitude");
		checkRangeValues(latitude, latitudeMin, latitudeMax, latitudeField,
				"Latitude");
		checkRangeValues(longitude, longitudeMin, longitudeMax, longitudeField,
				"Longitude");
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public  Address copy() {
		Address address = new Address();

		address.setAddressId(this.addressId);
		address.setAddressLine1(this.addressLine1);
		address.setAddressLine2(this.addressLine2);
		address.setAttentionName(this.attentionName);
		address.setBeginDate(this.beginDate);
		address.setEndDate(this.endDate);
		address.setAddressId(this.addressId);
		address.setState(this.state);
		address.setCityName(this.cityName);
		address.setZipCode(this.zipCode);
		address.setZipCode4(this.zipCode4);
		address.setZipCode5(this.zipCode5);
		address.setCountryCd(this.countryCd);
		address.setCountyCd(this.countyCd);
		address.setCountyName(this.countyName);
		address.setDistrictCd(this.districtCd);
		address.setLatitude(this.latitude);
		address.setLongitude(this.longitude);
		address.setQuarterQuarter(this.quarterQuarter);
		address.setQuarter(this.quarter);
		address.setSection(this.section);
		address.setTownship(this.township);
		address.setRange(this.range);
		address.setLastModified(this.getLastModified());
		address.setIndianReservationCd(this.getIndianReservationCd());
		return address;
	}
	
	/**
	 * This method checks the lat/long values and the PLSS values entered by the user to confirm
	 * that they are valid i.e., using the user entered lat/long values the system should
	 * be able to generate the same PLSS values that user has entered. If the generated PLSS
	 * values are different than the one entered by the user, then the given lat/long values
	 * are incorrect for PLSS values that the user has entered or vice-versa
	 *  
	 * @return true if lat/long and PLSS values are valid, otherwise false.
	 */
	public boolean validateLocationInfo() {
		
		boolean ok = true;
		
		if(getIsMatch()) {
			validationMessages.remove("location");
		} else {
			ok = false;
			validationMessages.put("location", new ValidationMessage("location",
					"There is a mismatch in the entered location data. "
						+  	"Please check that the Latitude/Longitude, Section/Township/Range, and County/District values are correct.",	
					ValidationMessage.Severity.ERROR, null));
		}			
		
		return ok;
	}
	
	public void validateFacilityAddress(){
		this.clearValidationMessages();
		
		requiredField(latitude, latitudeField, "Latitude");
		checkRangeValues(latitude, latitudeMin, latitudeMax, latitudeField, "Latitude");

		requiredField(longitude, longitudeField, "Longitude");
		checkRangeValues(longitude, longitudeMin, longitudeMax, longitudeField, "Longitude");

		requiredField(section, sectionField, "Section");
		checkRangeValues(section, sectionMin, sectionMax, sectionField, "Section");

		requiredField(township, townshipField, "Township");
		validateTownship(township);

		requiredField(range, rangeField, "Range");
		validateRange(range);
		
		requiredField(countyCd, countyCdField, "County");
		validateCounty(countyCd);
		requiredField(state, stateField, "State");
		validateState(state);
		requiredField(districtCd, districtField, "District");
		
		requiredField(beginDate, beginDateField, "Effective Date");
		
		if (!SystemPropertyDef.getSystemPropertyValueAsBoolean("APP_PLSS_AUTO_REPLICATION", true)){
			requiredField(addressLine1, "addressLine1", "Physical Address 1");
			requiredField(cityName, "cityName", "City");
			requiredField(zipCode, "zip", "Zip");
		} 
			
		validateAddress(); // This validates zip Format and parse value for zip5 & zip4
		validateLocationInfo(); // This checks location data mismatch 
	}
	
	public String getIndianReservationCd() {
		return indianReservationCd;
	}

	public void setIndianReservationCd(String indianReservationCd) {
		this.indianReservationCd = indianReservationCd;
	}
	
	private void calculateIndianReservationCd() {
		LocationCalculator calculator = App.getApplicationContext().getBean(LocationCalculator.class);
		String indianReservationCd = calculator.calculateIndianReservationCd(this.latitude,this.longitude);
		setIndianReservationCd(indianReservationCd);
	}


	private boolean validateLatLongInState(){
		if (this.outOfStateCountyCd.equals(this.countyCd)) {
			validationMessages.put(latLongRange, new ValidationMessage(latLongRange,
					"The given Latitude/Longitude is out of the state " + State.DEFAULT_STATE + ". Please provide Latitude/Longitude that is in the state.", ValidationMessage.Severity.ERROR, null));
			return false;
		} else {
			validationMessages.remove(latLongRange);
			return true;
		}
	}
	
	private void validateCounty(String countyCd){
		if (countyCd == null ||countyCd.equals("")){
			return;
		}
		if (countyCd.equals(outOfStateCountyCd)){
			validationMessages.put(countyCdField, new ValidationMessage(countyCdField,
							"Facility Address must be in one of the state county.", ValidationMessage.Severity.ERROR, null));
		}
	}
	
	private void validateState(String state){
		if (state == null || state.equals("")){
			return;
		}
		if (!state.equals(State.DEFAULT_STATE)){
			validationMessages.put(stateField, new ValidationMessage(stateField,
					"Facility Address must be in the State of " + State.DEFAULT_STATE + ".", ValidationMessage.Severity.ERROR, null));
		}
	}
}
