package us.oh.state.epa.stars2.framework.util;

import java.text.DecimalFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.bo.BaseBO;
import us.oh.state.epa.stars2.database.dbObjects.facility.PlssConversion;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountyDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Transactional(rollbackFor=Exception.class)
@Service
public class LocationCalculator extends BaseBO {

	static public final String getPlssText(Address address) {
		return getPlssText(address.getSection(), address.getTownship(),
				address.getRange(), address.getQuarterQuarter(),
				address.getQuarter());
	}

	static public final String getPlssText(String section, String township,
			String range, String quarterQuarter, String quarter) {
		if (Utility.isNullOrEmpty(section) || Utility.isNullOrEmpty(township)
				|| Utility.isNullOrEmpty(range)) {
			return "";
		}

		String displayPlss = String.format("S%s-T%s-R%s", section, township,
				range);

		if (Utility.isNullOrEmpty(quarterQuarter)
				|| Utility.isNullOrEmpty(quarter)) {
			return displayPlss;
		}

		return String.format("Q%s%s-%s", quarterQuarter, quarter, displayPlss);
	}

	static public final String getGoogleMapsURL(Float latitude,
			Float longitude, String displayName) {
		return getGoogleMapsURL(latitude.toString(), longitude.toString(),
				displayName);
	}

	static public final String getGoogleMapsURL(String latitude,
			String longitude, String displayName) {
		String url = null;

		if (!Utility.isNullOrEmpty(latitude)
				&& !Utility.isNullOrEmpty(longitude)) {
			StringBuilder sb = new StringBuilder(
					"http://maps.google.com/maps?q=");

			sb.append(latitude);
			sb.append(",+" + longitude);

			if (!Utility.isNullOrEmpty(displayName)) {
				displayName = displayName.replace("(", "[").replace(")", "]");
				StringBuilder temp = new StringBuilder();
				for (int i = 0; i < displayName.length(); i++) {
					String c = displayName.substring(i, i + 1);
					// Can NOT use the following characters #=<>()&*
					// + can use, but that will not show in page.
					if (" 0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_/|\\[]{},.%$@?!\"':;^~"
							.contains(c)) {
						temp.append(c);
					}
				}
				sb.append("+(" + temp + ")");
			}

			sb.append("&iwloc=A&hl=en&t=p&z=8");
			url = sb.toString().replaceAll("\\s", "+");
		}

		return url;
	}

	public final PlssConversion calculatePlss(String latitude, String longitude) {
		PlssConversion plss = null;

		if (Utility.isNullOrEmpty(latitude) || Utility.isNullOrEmpty(longitude)) {
			return new PlssConversion();
		}

		try {
			plss = infrastructureDAO().getPlssByLatLong(latitude, longitude);

			if (plss == null) {
				plss = new PlssConversion();
			}
		} catch (DAOException e) {
			logger.error(e.getMessage(), e);
		}

		return plss;
	}

	public final PlssConversion calculateLatLong(String section,
			String township, String range) {
		PlssConversion plss = null;

		if (Utility.isNullOrEmpty(section) || Utility.isNullOrEmpty(township)
				|| Utility.isNullOrEmpty(range)) {
			return new PlssConversion();
		}

		try {
			plss = infrastructureDAO().getLatLongByPlss(section, township,
					range);
		} catch (DAOException e) {
			logger.error(e.getMessage(), e);
		}

		if (plss == null) {
			plss = new PlssConversion();
		}

		return plss;
	}

	public final CountyDef calculateCountyCd(String latitude, String longitude) {
		CountyDef county = null;

		try {
			county = infrastructureDAO()
					.getCountyByLatLong(latitude, longitude);
		} catch (DAOException e) {
			logger.error(e.getMessage(), e);
		}

		if (county == null) {
			county = new CountyDef();
		}

		return county;
	}

	public final String calculateDistance(Address address) {
		return calculateDistance(address.getLatitude(), address.getLongitude(),
				address.getSection(), address.getTownship(), address.getRange());
	}

	private final String calculateDistance(String latitude, String longitude,
			String section, String township, String range) {
		PlssConversion plss = null;

		if (Utility.isNullOrEmpty(latitude) || Utility.isNullOrEmpty(longitude)
				|| Utility.isNullOrEmpty(section)
				|| Utility.isNullOrEmpty(township)
				|| Utility.isNullOrEmpty(range)) {
			return "";
		}

		try {
			plss = infrastructureDAO().getLatLongByPlss(section, township,
					range);
		} catch (DAOException dex) {
			logger.error(dex.getMessage(), dex);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		if (plss == null) {
			plss = new PlssConversion();
		}

		String compareLat = plss.getLatitude();
		String compareLong = plss.getLongitude();

		if (Utility.isNullOrEmpty(compareLat)
				|| Utility.isNullOrEmpty(compareLong)) {
			return "";
		}

		try {
			double fromLat = Double.parseDouble(latitude);
			double fromLong = Double.parseDouble(longitude);
			double toLat = Double.parseDouble(compareLat);
			double toLong = Double.parseDouble(compareLong);
			double distance = getDistance(fromLat, fromLong, toLat, toLong);

			DecimalFormat dFormat = new DecimalFormat("0.##");

			if (distance >= 0.25)
				return dFormat.format(distance) + " Miles";
			else
				return dFormat.format((int) (distance * 5280)) + " Feet";
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return "";
		}
	}

	private final double getDistance(double fromLat, double fromLong,
			double toLat, double toLong) {
		double distance;
		try {
			distance = 69.171 * Math.sqrt(Math.pow(
					Math.cos(((toLat + fromLat) / 2) * 0.017453292)
							* (toLong - fromLong), 2)
					+ Math.pow(toLat - fromLat, 2));
			if (distance < 12500)
				return distance;
			else
				return -1;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return -1;
	}


	public String calculateIndianReservationCd(String latitude, String longitude){
		String indianReservationCd = new String();
		try {
			indianReservationCd = infrastructureDAO().getIndianReservationCdByLatLong(latitude, longitude);
		} catch (DAOException e) {
			logger.error(e.getMessage(), e);
		}

		return indianReservationCd;
	}

}
