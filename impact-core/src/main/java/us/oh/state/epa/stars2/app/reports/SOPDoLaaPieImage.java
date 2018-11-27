package us.oh.state.epa.stars2.app.reports;

import java.util.ArrayList;
import java.util.HashMap;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.database.dbObjects.report.PermitSOPData;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.Image;
import us.oh.state.epa.stars2.webcommon.output.ChartData;
import us.oh.state.epa.stars2.webcommon.output.PieChartOutputManager;

public class SOPDoLaaPieImage extends ReportImageBase implements Image {
    public SOPDoLaaPieImage() {
        super();
    }
    
    private void resetProfile() {
        setWidth(300);
        setHeight(300);
        image = null;
        areas = null;

    }
    
    protected String getMyManagedBean() {
        return "permitSOP";
    }

    /**
     * draw
     * 
     * @param width
     * @param height
     * @throws Exception
     */
    public final void draw(int width, int height) throws Exception {
        resetProfile();
        HashMap<String, SimpleIdDef> sdMap = new HashMap<String, SimpleIdDef>();

        PermitSOP permitSop = (PermitSOP) FacesUtil.getManagedBean(getMyManagedBean());

        PermitSOPData[] details = permitSop.getDetails();

        for (PermitSOPData sopData : details) {
            SimpleIdDef sd = sdMap.get(sopData.getDoLaaName());
            if (sd == null) {
                sd = new SimpleIdDef();
                sd.setId(1);
                sd.setDescription(sopData.getDoLaaName());
                sdMap.put(sopData.getDoLaaName(), sd);
            } else {
                sd.setId(sd.getId() + 1);
            }
        }

        ArrayList<Object> als = new ArrayList<Object>();
        ChartData cd;
        int color = 0;
        SimpleIdDef[] pts = sdMap.values().toArray(new SimpleIdDef[0]);
        for (SimpleIdDef p : pts) {
            cd = new ChartData();
            cd.setColor(colors[color]);
            cd.setLabel(p.getDescription());
            cd.setQparam("params=");
            cd.setValue(p.getId());
            als.add(cd);
            if (++color == colors.length) {
                color = 1;
            }
        }

        PieChartOutputManager pc = new PieChartOutputManager();
        pc.setHeight(height);
        pc.setWidth(width);
        pc.setTitle(getTitle());
        pc.setCreateLegend(false);
        pc.setClickURL(getClickURL());
        image = pc.process(als.toArray(new ChartData[0]), this, null);
    }

    /**
     * @return
     */
    public final String submitProfile() {
        return SUCCESS;
    }

}
