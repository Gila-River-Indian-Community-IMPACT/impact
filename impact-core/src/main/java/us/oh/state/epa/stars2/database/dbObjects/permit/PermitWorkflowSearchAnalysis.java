package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import us.oh.state.epa.stars2.def.PermitWorkflowActivityBenchmarkDef;

public class PermitWorkflowSearchAnalysis {
	
	private String activityName;
	
	private Map<PermitWorkflowActivityBenchmarkDef,PermitWorkflowBenchmark> benchmarks = 
			new HashMap<PermitWorkflowActivityBenchmarkDef,PermitWorkflowBenchmark>();

	private Set<String> applications = new HashSet<String>();
	
	private Map<PermitWorkflowBenchmark,Map<String,Integer>> benchmarkDays = 
			new HashMap<PermitWorkflowBenchmark,Map<String,Integer>>();

	public Map<PermitWorkflowBenchmark,Map<String,Integer>> getBenchmarkDays() {
		return benchmarkDays;
	}
	public Set<String> getApplications() {
		return applications;
	}
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public int getApplicationCount() {
		return applications.size();
	}
	public Map<PermitWorkflowActivityBenchmarkDef,PermitWorkflowBenchmark> getBenchmarks() {
		return benchmarks;
	}
	public PermitWorkflowBenchmark[] getBenchmarksArray() {
		PermitWorkflowBenchmark[] benchmarksArray = 
				this.getBenchmarks().values().toArray(new PermitWorkflowBenchmark[0]);
		Arrays.sort(benchmarksArray, new Comparator<PermitWorkflowBenchmark>() {
			@Override
			public int compare(PermitWorkflowBenchmark o1, PermitWorkflowBenchmark o2) {
				if (o1.getDisplayOrder() == o2.getDisplayOrder()) {
					return 0;
				} else {
					return o1.getDisplayOrder() > o2.getDisplayOrder()? 1 : -1;
				}
			}
		});
		return benchmarksArray;
	}
	

}
