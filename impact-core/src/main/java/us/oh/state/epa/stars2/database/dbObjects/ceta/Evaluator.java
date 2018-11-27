package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;

@SuppressWarnings("serial")
public class Evaluator extends CetaBaseDB {
    private Integer evaluator;

    
    private transient boolean reassign;   // not in database.
    
    public Evaluator() {
        super();
    }
    
    public Evaluator(Integer userId) {
        super();
        this.evaluator = userId;
    }
    
    /** Populate this instance from a database ResultSet. */
    public final void populate(java.sql.ResultSet rs)throws SQLException {

        try{
            setEvaluator(AbstractDAO.getInteger(rs, "user_id"));
        } catch(SQLException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    public Integer getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(Integer evaluator) {
        this.evaluator = evaluator;
    }

    public boolean isReassign() {
        return reassign;
    }

    public void setReassign(boolean reassign) {
        this.reassign = reassign;
    }
}
