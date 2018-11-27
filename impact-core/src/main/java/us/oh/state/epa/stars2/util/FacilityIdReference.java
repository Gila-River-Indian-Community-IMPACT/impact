package us.oh.state.epa.stars2.util;

import java.io.Serializable;

/**
 * @author Kbradley
 * 
 */
public class FacilityIdReference implements Serializable {
    private String countyCd;
    private String dolaCd;
    private String cityCd;
    private String cityName;
    private String dolaId;

    public final String getCityCd() {
        return cityCd;
    }

    public final void setCityCd(String cityCd) {
        this.cityCd = cityCd;
    }

    public final String getCityName() {
        return cityName;
    }

    public final void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public final String getCountyCd() {
        return countyCd;
    }

    public final void setCountyCd(String countyCd) {
        this.countyCd = countyCd;
    }

    public final String getDolaCd() {
        return dolaCd;
    }

    public final void setDolaCd(String dolaCd) {
        this.dolaCd = dolaCd;
    }

    public final String getDolaId() {
        return dolaId;
    }

    public final void setDolaId(String dolaId) {
        this.dolaId = dolaId;
    }
}
