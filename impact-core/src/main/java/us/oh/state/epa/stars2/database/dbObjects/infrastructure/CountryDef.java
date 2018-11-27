package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class CountryDef extends BaseDB {
    private String countryCd;
    private String countryNm;

    public CountryDef() {
    }

    public CountryDef(CountryDef old) {
        super(old);

        if (old != null) {
            setCountryCd(old.getCountryCd());
            setCountryNm(old.getCountryNm());
        }
    }

    public final String getCountryCd() {
        return countryCd;
    }

    public final void setCountryCd(String countryCd) {
        this.countryCd = countryCd;
    }

    /**
     * CountryNm.
     * 
     * @return String
     */
    public final String getCountryNm() {
        return countryNm;
    }

    /**
     * CountryNm.
     * 
     * @param countryNm
     */
    public final void setCountryNm(String countryNm) {
        this.countryNm = countryNm;
    }

    public final void populate(ResultSet rs) {
        try {
            setCountryCd(rs.getString("country_cd"));
            setCountryNm(rs.getString("country_dsc"));

            setLastModified(AbstractDAO.getInteger(rs, "country_lm"));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }
}
