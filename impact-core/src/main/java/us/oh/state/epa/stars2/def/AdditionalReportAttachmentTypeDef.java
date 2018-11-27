package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class AdditionalReportAttachmentTypeDef {
    private static final String defName = "AdditionalReportAttachmentTypeDef";
//  public static final String RO_SIGNATURE = "10";
    public static final String QUARTERLY_DATA_SUMMARY_REPORT= "01";
    public static final String QUARTERLY_AQS_DATA_FILE = "02";
    public static final String QUARTERLY_AQS_PA_FILE = "03";
    public static final String QUARTERLY_AQS_DATA_TRANSMITTAL_LATTER = "04";
    public static final String PERFORMANCE_AUDIT_SUMMARY = "05";
    public static final String ANNUAL_REPORT = "06";
    public static final String ANNUAL_AQS_DATA_FILE = "07";
    public static final String ANNUAL_NETWORK_DEMONSTRATION = "08";
    public static final String EXCEPTIONAL_EVENT_DEMONSTRATION = "09";
    public static final String MET_SPREADSHEET = "10";
    public static final String OTHER = "11";
    public static final String REQUEST_TO_CHANGE_MONITORING_NETWORK = "12";
    public static final String DATA_SUMMARY_REPORT = "13";
    public static final String RAW_DATA_FILE = "14";
    public static final String PRECISION_AND_ACCURACY = "15";
    

  public static DefData getData() {
      ConfigManager cfgMgr = ConfigManagerFactory.configManager();
      DefData data = cfgMgr.getDef(defName);

      if (data == null) {
          data = new DefData();
          data.loadFromDB("MO_ADDL_RPT_ATTACHMNT_TYPE_DEF", "MO_ADDL_RPT_ATTACHMNT_TYPE_CD", "MO_ADDL_RPT_ATTACHMNT_TYPE_DSC", "deprecated");

          cfgMgr.addDef(defName, data);
      }
      return data;
  }
}
