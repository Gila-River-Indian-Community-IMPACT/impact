package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.SiteVisitTypeDef;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;


@SuppressWarnings("serial")
public abstract class StackTestBase extends CetaBaseDB {
//	protected Integer id;
//    protected Integer fceId;
//    protected List<TestVisitDate> visitDates;  // witnessed dates if witnessed; otherwise test dates
//    protected List<Evaluator> witnesses;
//    protected String memo;
//
//    public StackTestBase() {
//        super();
//        visitDates = new ArrayList<TestVisitDate>();
//        witnesses = new ArrayList<Evaluator>();
//    }
//    
//    public StackTestBase(StackTestBase old) {
//        super();
//        id = old.id;
//        fceId = old.fceId;
//        //testDate = old.testDate;
//        witnesses = old.witnesses;
//        memo = old.memo;
//        visitDates = old.visitDates;
//    }
//
//    public String getMemo() {
//        return memo;
//    }
//
//    public String getShortMemo() {
//        String rtn = memo;
//        if(memo != null && memo.length()> 75) {
//            rtn = memo.substring(0, 75) + "...";
//        }
//        return rtn;
//    }
//
//    public void setMemo(String memo) {
//        this.memo = memo;
//    }
//
//	public Integer getId() {
//		return id;
//	}
//
//	public void setId(Integer id) {
//		this.id = id;
//	}
//
//    public Integer getFceId() {
//        return fceId;
//    }
//
//    public void setFceId(Integer fceId) {
//        this.fceId = fceId;
//    }
//
//    public List<Evaluator> getWitnesses() {
//        return witnesses;
//    }
//
//    public String getDatesStrings() {
//        return getDatesStrings(visitDates);
//    }
//    
//    
//    public void addEvaluator() {
//        if(witnesses == null) {
//            witnesses = new ArrayList<Evaluator>();
//        }
//        witnesses.add(new Evaluator());
//    }
//    
//    public void addTestDate() {
//        if(visitDates == null) {
//            visitDates = new ArrayList<TestVisitDate>();
//        }
//        visitDates.add(new TestVisitDate());
//    }
//
//    public boolean isWitnessesExist() {
//        return witnesses != null && witnesses.size() != 0;
//    }
//
//    public void setWitnessesExist(boolean witnessesExist) {
//        if(witnessesExist && (witnesses == null || witnesses.size() == 0)) {
//            witnesses = new ArrayList<Evaluator>();
//            Evaluator e = new Evaluator(InfrastructureDefs.getCurrentUserId());
//            witnesses.add(e);
//        }
//    }
//
//    public List<TestVisitDate> getVisitDates() {
//        return visitDates;
//    }
//
//    public void setVisitDates(List<TestVisitDate> visitDates) {
//        this.visitDates = visitDates;
//    }
//
//    public void setWitnesses(List<Evaluator> witnesses) {
//        this.witnesses = witnesses;
//    }
}
