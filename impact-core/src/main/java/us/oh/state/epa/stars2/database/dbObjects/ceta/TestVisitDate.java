package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;


@SuppressWarnings("serial")
public class TestVisitDate extends CetaBaseDB {
    private Timestamp testDate;
    private Integer StackTestId;
    
    public TestVisitDate() {
        super();
    }
    
    public TestVisitDate(Timestamp testDate) {
        super();
        this.testDate = testDate;
    }
    
    /** Populate this instance from a database ResultSet. */
    public final void populate(java.sql.ResultSet rs)throws SQLException {

        try{
            setStackTestId(AbstractDAO.getInteger(rs, "stack_test_id"));
            setTestDate(rs.getTimestamp("test_date"));
        } catch(SQLException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    public Integer getStackTestId() {
        return StackTestId;
    }

    public void setStackTestId(Integer stackTestId) {
        StackTestId = stackTestId;
    }

    public Timestamp getTestDate() {
        return testDate;
    }

    public void setTestDate(Timestamp testDate) {
        this.testDate = testDate;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((testDate == null) ? 0 : testDate.hashCode());
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
        final TestVisitDate other = (TestVisitDate) obj;
        if (testDate == null) {
            if (other.testDate != null)
                return false;
        } else if (!testDate.equals(other.testDate))
            return false;
        return true;
    }
}
