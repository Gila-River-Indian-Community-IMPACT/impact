package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;


@SuppressWarnings("serial")
public class TIVApplication extends Application {
    private String reasonCd;
    private String appPurposeCd;
    
    public TIVApplication() {
        super();
        setApplicationTypeCD(ApplicationTypeDef.TITLE_IV_APPLICATION);
    }
    
    public TIVApplication(TIVApplication app) {
        super();
        setApplicationTypeCD(ApplicationTypeDef.TITLE_IV_APPLICATION);
        if (app != null) {
            setReasonCd(app.getReasonCd());
            setAppPurposeCd(app.getAppPurposeCd());
        }
    }
    
    @Override
    public void populate(ResultSet rs) {
        try {
            super.populate(rs);
            setReasonCd(rs.getString("tiv_reason_cd"));
            setAppPurposeCd(rs.getString("tiv_app_purpose_cd"));
        } catch (SQLException e) {
            logger.warn(e.getMessage(), e);
        }
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((appPurposeCd == null) ? 0 : appPurposeCd.hashCode());
        result = prime * result
                + ((reasonCd == null) ? 0 : reasonCd.hashCode());
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
        final TIVApplication other = (TIVApplication) obj;
        if (appPurposeCd == null) {
            if (other.appPurposeCd != null)
                return false;
        } else if (!appPurposeCd.equals(other.appPurposeCd))
            return false;
        if (reasonCd == null) {
            if (other.reasonCd != null)
                return false;
        } else if (!reasonCd.equals(other.reasonCd))
            return false;
        return true;
    }
    public final String getReasonCd() {
        return reasonCd;
    }
    public final void setReasonCd(String reasonCd) {
        this.reasonCd = reasonCd;
    }
    public final String getAppPurposeCd() {
        return appPurposeCd;
    }
    public final void setAppPurposeCd(String appPurposeCd) {
        this.appPurposeCd = appPurposeCd;
    }

    @Override
    public String getApplicationPurposeDesc() {
        StringBuffer sb = new StringBuffer();
        if (reasonCd != null) {
            sb.append(PermitReasonsDef.getData().getItems().getItemDesc(reasonCd));
        }
        return sb.toString();
    }
}
