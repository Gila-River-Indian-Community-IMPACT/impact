package us.wy.state.deq.impact.def;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class IssuedMetricsBenchmarkDef extends SimpleDef{
	
	public static final String COMP = "COMP";
	public static final String MINOR_NSR = "OTH";
	public static final String PSD = "PSD";
	public static final String WAIVER = "WAIV";
	public static final String TV = "TV";
	
	// ****************** Variables *******************
		private static final String defName = "IssuedMetricsBenchmarkDef";
		private static final String benchmarkName = "IssuedMetricsBenchmarkDefBenchmark";
		private static final String tableName = "PT_ISSUED_METRICS_BENCHMARK_DEF";
		private static final String keyFieldName = "ISSUED_METRICS_BENCHMARK_CD";
		private static final String valueFieldName = "ISSUED_METRICS_BENCHMARK_DESC";
		private static final String daysFieldName = "ISSUED_METRICS_BENCHMARK_DAYS";
		private static final String deprecatedFieldName = "DEPRECATED";

		private BigDecimal benchmarkDays;

		// ****************** Public Static Methods *******************
		public static DefData getData() {
			ConfigManager cfgMgr = ConfigManagerFactory.configManager();
			DefData data = cfgMgr.getDef(defName);

			if (data == null) {
				data = new DefData();
	            data.loadFromDB("ReportsSQL.retrieveIssuedMetricsBenchmarkDefData", 
	            		IssuedMetricsBenchmarkDef.class);

				cfgMgr.addDef(defName, data);
			}

			return data;
		}

		public static DefData getBenchmarkData() {
			ConfigManager cfgMgr = ConfigManagerFactory.configManager();
			DefData benchmarkData = cfgMgr.getDef(benchmarkName);

			if (benchmarkData == null) {
				benchmarkData = new DefData();
				benchmarkData.loadFromDB(tableName, keyFieldName, daysFieldName,
						deprecatedFieldName);

				cfgMgr.addDef(benchmarkName, benchmarkData);
			}

			return benchmarkData;
		}

		public void populate(ResultSet rs) {
			super.populate(rs);

			try {
				setBenchmarkDays(rs.getBigDecimal(daysFieldName));
			} catch (SQLException sqle) {
				logger.error("Required field error: " + sqle.getMessage(), sqle);
			}
		}

		public BigDecimal getBenchmarkDays() {
			return benchmarkDays;
		}

		public void setBenchmarkDays(BigDecimal benchmarkDays) {
			this.benchmarkDays = benchmarkDays;
		}
}