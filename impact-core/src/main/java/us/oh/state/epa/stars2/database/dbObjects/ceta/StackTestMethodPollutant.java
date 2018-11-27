package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.PollutantDef;


@SuppressWarnings("serial")
public class StackTestMethodPollutant extends BaseDB {
    private String pollutantCd;
    private boolean deprecated;
    
    transient private String pollutantDsc;
   
    public StackTestMethodPollutant() {
        super();
    }
    
    public StackTestMethodPollutant(String pollutantCd) {
        super();
        this.pollutantCd = pollutantCd;
        pollutantDsc = PollutantDef.getData().getItems().getDescFromAllItem(pollutantCd);
    }
    
    /** Populate this instance from a database ResultSet. */
    public final void populate(java.sql.ResultSet rs)throws SQLException {

        try{
            setDeprecated(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("deprecated")));
            setPollutantCd(rs.getString("pollutant_cd"));
            setPollutantDsc(PollutantDef.getData().getItems().getDescFromAllItem(getPollutantCd()));
        } catch(SQLException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    public String getPollutantCd() {
        return pollutantCd;
    }

    public void setPollutantCd(String pollutantCd) {
        this.pollutantCd = pollutantCd;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getPollutantDsc() {
        return pollutantDsc;
    }

    public void setPollutantDsc(String pollutantDsc) {
        this.pollutantDsc = pollutantDsc;
    }

}
