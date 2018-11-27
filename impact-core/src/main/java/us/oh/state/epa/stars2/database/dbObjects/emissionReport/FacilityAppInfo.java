package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class FacilityAppInfo extends BaseDB {
    private Integer applicationId;
    private String applicationNbr;
    private String applicationTypeCd;
    private Timestamp receivedDate;
    private String dispositionFlag; // If PBR: Accepted (treat like permit), nothing (treat like application) or denyed (ignore)
    private List<Integer> euCorrIds;

    public FacilityAppInfo() {
        super();
    }

    public final void populate(ResultSet rs) {
        try {
            setApplicationId(AbstractDAO.getInteger(rs, "application_id"));
            setApplicationNbr(rs.getString("application_nbr"));
            setApplicationTypeCd(rs.getString("application_type_cd"));
            setReceivedDate(rs.getTimestamp("received_date"));
            euCorrIds = new ArrayList<Integer>();
            euCorrIds.add(AbstractDAO.getInteger(rs, "corr_epa_emu_id"));
            setDispositionFlag(rs.getString("disposition_flag"));
            while(rs.next()) {
                if(applicationId.equals(AbstractDAO.getInteger(rs, "application_id"))) {
                    euCorrIds.add(AbstractDAO.getInteger(rs, "corr_epa_emu_id"));
                } else {
                    rs.previous();
                    break;
                }
            }
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationNbr() {
        return applicationNbr;
    }

    public void setApplicationNbr(String applicationNbr) {
        this.applicationNbr = applicationNbr;
    }

    public List<Integer> getEuCorrIds() {
        return euCorrIds;
    }

    public void setEuCorrIds(List<Integer> euCorrIds) {
        this.euCorrIds = euCorrIds;
    }

    public Timestamp getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Timestamp receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getApplicationTypeCd() {
        return applicationTypeCd;
    }

    public void setApplicationTypeCd(String applicationTypeCd) {
        this.applicationTypeCd = applicationTypeCd;
    }

    public String getDispositionFlag() {
        return dispositionFlag;
    }

    public void setDispositionFlag(String dispositionFlag) {
        this.dispositionFlag = dispositionFlag;
    }
}


