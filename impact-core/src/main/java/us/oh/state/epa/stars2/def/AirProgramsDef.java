package us.oh.state.epa.stars2.def;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class AirProgramsDef {
    public static final String SIP = "sip";
    public static final String MACT = "mact";
    private static final String defName = "AirProgramsDef";

    // TODO associate this with table CM_AIR_PROGRAMS_DEF once it is defined.
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.addItem(SIP, "SIP");
            data.addItem(MACT, "Part 63 NESHAP");
            for(SelectItem si : InspectionClassDef.getData().getItems().getAllSearchItems()) {
                data.addItem(si.getLabel(), (String)si.getValue());
            }
//            data.addItem("TV", "Title V");
//            data.addItem("SM", "FEPTIO");
//            data.addItem("MEGA", "Mega TV");
//            data.addItem("NHPF", "Non-HPF");
            
            DefData inspDefData = InspectionClassDef.getData();
            DefSelectItems items = inspDefData.getItems();
            for (SelectItem it : items.getAllItems()) {
            	data.addItem(it.getValue().toString(), it.getLabel());
            }

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
