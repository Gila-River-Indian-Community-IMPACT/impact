package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class StateDef extends BaseDB {
    private String stateCd;
    private String stateNm;
    private String countryCd;
    private String countryNm;

    public StateDef() {
    }

    public StateDef(StateDef old) {
        super(old);

        if (old != null) {
            setStateCd(old.getStateCd());
            setStateNm(old.getStateNm());
            setCountryCd(old.getCountryCd());
            setCountryNm(old.getCountryNm());
        }
    }

    public final String getStateCd() {
        return stateCd;
    }

    public final void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public final String getStateNm() {
        return stateNm;
    }

    public final void setStateNm(String stateNm) {
        this.stateNm = stateNm;
    }

    public final String getCountryCd() {
        return countryCd;
    }

    public final void setCountryCd(String countryCd) {
        this.countryCd = countryCd;
    }

    public final String getCountryNm() {
        return countryNm;
    }

    public final void setCountryNm(String countryNm) {
        this.countryNm = countryNm;
    }

    public final void populate(ResultSet rs) {
        try {
            setStateCd(rs.getString("state_cd"));
            setStateNm(rs.getString("state_nm"));
            setCountryCd(rs.getString("country_cd"));
            setCountryNm(rs.getString("country_dsc"));

            setLastModified(AbstractDAO.getInteger(rs, "state_lm"));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result
                + ((countryCd == null) ? 0 : countryCd.hashCode());
        result = PRIME * result
                + ((countryNm == null) ? 0 : countryNm.hashCode());
        result = PRIME * result + ((stateCd == null) ? 0 : stateCd.hashCode());
        result = PRIME * result + ((stateNm == null) ? 0 : stateNm.hashCode());
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
        final StateDef other = (StateDef) obj;
        if (countryCd == null) {
            if (other.countryCd != null)
                return false;
        } else if (!countryCd.equals(other.countryCd))
            return false;
        if (countryNm == null) {
            if (other.countryNm != null)
                return false;
        } else if (!countryNm.equals(other.countryNm))
            return false;
        if (stateCd == null) {
            if (other.stateCd != null)
                return false;
        } else if (!stateCd.equals(other.stateCd))
            return false;
        if (stateNm == null) {
            if (other.stateNm != null)
                return false;
        } else if (!stateNm.equals(other.stateNm))
            return false;
        return true;
    }
}
