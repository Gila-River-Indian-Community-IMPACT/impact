package us.oh.state.epa.stars2.app.workflow;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.webcommon.bean.AreaBean;
import us.oh.state.epa.stars2.webcommon.bean.ImageBase;
import us.oh.state.epa.stars2.webcommon.output.ChartData;
import us.oh.state.epa.stars2.webcommon.output.PieChartOutputManager;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

@SuppressWarnings("serial")
public class ProcessPieImage extends ImageBase {
    public static final String[] colors = { "33FFFF", "CCCCCC", "3300FF",
            "FF00CC", "3EFE2A", "990000", "660066", "999900", "006633" };
    private String assigned;
    private String subSystem;
    private String clickURL;
    private Integer[] groupIds;
    private String title;
    private ReadWorkFlowService workFlowService;


    public ReadWorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public void setWorkFlowService(ReadWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	public ProcessPieImage() {
        super();
    }
    
    public final String getClickURL() {
        return clickURL;
    }

    public final void setClickURL(String clickURL) {
        this.clickURL = clickURL;
    }

    public final String getAssigned() {
        return assigned;
    }

    public final void setAssigned(String assigned) {
        this.assigned = assigned;
    }

    public final Integer[] getGroupIds() {
        return groupIds;
    }

    public final void setGroupIds(Integer[] groupIds) {
        this.groupIds = groupIds;
    }

    public final String getSubSystem() {
        return subSystem;
    }

    public final void setSubSystem(String subSystem) {
        this.subSystem = subSystem;
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     */
    /*
     * private void resetProfile() { assigned = null; subSystem = null; image =
     * null; areas = null; }
     */
    /**
     * getImage
     * 
     * @return
     */
    public final BufferedImage getImage() {
        draw(getWidth(), getHeight());

        return image;
    }

    /**
     * getAreas
     * 
     * @return
     */
    public final AreaBean[] getAreas() {
        if (areas == null) {
            draw(getWidth(), getHeight());
        }
        return areas;
    }

    /**
     * draw
     * 
     * @param width
     * @param height
     * @throws Exception
     */
    public final void draw(int width, int height) {
        SimpleIdDef[] pts = null;

        try {
            pts = getWorkFlowService().retrieveProcessGroupByType();
        } catch (RemoteException re) {
            handleException(re);
        }
        
        ArrayList<ChartData> als = new ArrayList<ChartData>();
        ChartData cd;
        int color = 0;
        StringBuffer sb = new StringBuffer("by=type&section=");
        if (pts != null){
            for (SimpleIdDef p : pts) {
                cd = new ChartData();
                cd.setColor(colors[color]);
                StringTokenizer st = new StringTokenizer(p.getDescription(), ":");
                cd.setLabel(st.nextToken() + ":" + p.getId());
                cd.setQparam(sb + st.nextToken());
                cd.setValue(p.getId());
                als.add(cd);
                if (++color == colors.length) {
                    color = 1;
                }
            }
        }

        PieChartOutputManager pc = new PieChartOutputManager();
        pc.setHeight(height);
        pc.setWidth(width);
        pc.setTitle(title);
        pc.setClickURL(clickURL);
        try {
            image = pc.process(als.toArray(new ChartData[0]), this, null);
        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
        }
    }
    
}
