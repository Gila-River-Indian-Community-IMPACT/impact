package us.wy.state.deq.impact.database.dbObjects.projectTracking;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.bean.Stars2Object;

@SuppressWarnings("serial")
public class Budget extends BaseDB {
	public static final String PAGE_VIEW_ID = "budgetDetail:";
	
	private Integer projectId;
	private Integer budgetId;
	private Integer BFY;
	private String budgetFunctionCd;
	private BigDecimal amount;
	
	public Budget() {
		super();
	}
	
	public Budget(Budget budget) {
		super(budget);
		if(null != budget) {
			setProjectId(budget.getProjectId());
			setBudgetId(budget.getBudgetId());
			setBFY(budget.getBFY());
			setBudgetFunctionCd(budget.getBudgetFunctionCd());
			setAmount(budget.getAmount());
		}
	}
	
	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Integer getBudgetId() {
		return budgetId;
	}

	public void setBudgetId(Integer budgetId) {
		this.budgetId = budgetId;
	}

	public Integer getBFY() {
		return BFY;
	}

	public void setBFY(Integer BFY) {
		this.BFY = BFY;
	}

	public String getBudgetFunctionCd() {
		return budgetFunctionCd;
	}

	public void setBudgetFunctionCd(String budgetFunctionCd) {
		this.budgetFunctionCd = budgetFunctionCd;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		if(null != rs) {
			setProjectId(AbstractDAO.getInteger(rs, "project_id"));
			setBudgetId(AbstractDAO.getInteger(rs, "budget_id"));
			setBFY(AbstractDAO.getInteger(rs, "bfy"));
			setBudgetFunctionCd(rs.getString("budget_function_cd"));
			setAmount(rs.getBigDecimal("amount"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
			setNewObject(false);
		}
	}
	
	
	private void requiredFields() {
		requiredField(this.BFY, PAGE_VIEW_ID + "BFY", "BFY", "BFY");

		requiredField(this.budgetFunctionCd, PAGE_VIEW_ID + "budgetFunctionCd",
				"Function", "budgetFunctionCd");

		requiredField(this.amount, PAGE_VIEW_ID + "amount", "Amount", "amount");

	}

	@Override
	public final ValidationMessage[] validate() {
		validationMessages.clear();

		requiredFields();

		if (null != this.amount) {
			checkRangeValues(this.amount, new BigDecimal(0), new BigDecimal(
					100000000), PAGE_VIEW_ID + "amount", "AMOUNT", "amount");
		}

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
}
