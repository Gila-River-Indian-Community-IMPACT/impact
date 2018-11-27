package us.oh.state.epa.stars2.database.dbObjects.serviceCatalog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class SCEUCategory extends BaseDB {
	
	private static final long serialVersionUID = 4094289710413011391L;

	private Integer categoryId;
    private String categoryDsc;
    private boolean deprecated;
    private List<Fee> fees = new ArrayList<Fee>(0);

    public final Integer getCategoryId() {
        return categoryId;
    }

    public final void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public final String getCategoryDsc() {
        return categoryDsc;
    }

    public final void setCategoryDsc(String categoryDsc) {
        this.categoryDsc = categoryDsc;
    }

    public final List<Fee> getFees() {
        return fees;
    }

    public final void setFees(List<Fee> fees) {
        this.fees = fees;
    }

    public final boolean isDeprecated() {
        return deprecated;
    }

    public final void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public final void populate(ResultSet rs) throws SQLException {
        try {
            setCategoryId(AbstractDAO.getInteger(rs, "eu_category_id"));
            setCategoryDsc(rs.getString("eu_category_dsc"));
            setLastModified(AbstractDAO.getInteger(rs, "euCategory_lm"));
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }

        try {
            if (rs.getString("fee_nm") != null) {
                Fee fee;
                do {
                    fee = new Fee();
                    fee.populate(rs);

                    fees.add(fee);
                } while (rs.next());
            }
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }
}
