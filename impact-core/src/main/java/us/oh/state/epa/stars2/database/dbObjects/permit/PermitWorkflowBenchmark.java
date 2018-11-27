package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.util.HashSet;
import java.util.Set;

import us.oh.state.epa.stars2.def.PermitWorkflowActivityBenchmarkDef;

public class PermitWorkflowBenchmark {

	private Set<String> applications = new HashSet<String>();
	private int applicationCount = 0;
	private PermitWorkflowActivityBenchmarkDef benchmarkDef;
	
	public PermitWorkflowBenchmark() {
		super();
	}
	public PermitWorkflowBenchmark(PermitWorkflowActivityBenchmarkDef benchmarkDef) {
		this();
		this.benchmarkDef = benchmarkDef;
	}
	public Set<String> getApplications() {
		return applications;
	}
	public int getApplicationCount() {
		return applicationCount;
	}
	public String getPredicate() {
		return benchmarkDef.getPredicateDef().getPredicateCd();
	}
	public int getValue() {
		return benchmarkDef.getDays();
	}
	public String getPredicateDescription() {
		return benchmarkDef.getPredicateDef().getPredicateDsc();
	}
	public int getDisplayOrder() {
		return benchmarkDef.getDisplayOrder();
	}
	public void incrementApplicationCount() {
		this.applicationCount  += 1;
	}
	@Override
	public String toString() {
		return "PermitWorkflowBenchmark [applications=" + applications
				+ ", applicationCount=" + applicationCount + ", benchmarkDef="
				+ benchmarkDef + "]";
	}

}
