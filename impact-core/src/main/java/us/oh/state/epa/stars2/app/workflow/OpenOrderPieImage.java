package us.oh.state.epa.stars2.app.workflow;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.bean.AreaBean;
import us.oh.state.epa.stars2.webcommon.bean.ImageBase;
import us.oh.state.epa.stars2.webcommon.output.ChartData;
import us.oh.state.epa.stars2.webcommon.output.PieChartOutputManager;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

@SuppressWarnings("serial")
public class OpenOrderPieImage extends ImageBase {
    private String assigned;
    private String subSystem;
    private String clickURL;
    private Integer[] groupIds;
    private String title;

    private ReadWorkFlowService readWorkFlowService;

    public ReadWorkFlowService getReadWorkFlowService() {
		return readWorkFlowService;
	}

	public void setReadWorkFlowService(ReadWorkFlowService readWorkFlowService) {
		this.readWorkFlowService = readWorkFlowService;
	}

	public OpenOrderPieImage() {
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
     * getImage
     * 
     * @return
     */
    public final BufferedImage getImage() {
        try {
            draw(getWidth(), getHeight());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

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
        WorkFlowProcess[] processes = null;

        WorkFlowProcess wp = new WorkFlowProcess();
        wp.setState(WorkFlowProcess.STATE_IN_PROCESS_CD);
        wp.setCurrent(true);
        wp.setUnlimitedResults(unlimitedResults());
        try {
            processes = getReadWorkFlowService().retrieveProcessList(wp);
            DisplayUtil.displayHitLimit(processes.length);
        } catch (RemoteException re) {
            handleException(re);
        }

        int ok = 0;
        int jeop = 0;
        int past = 0;
        String okcd = WorkFlowProcess.STATUS_OK_CD;
        String jpcd = WorkFlowProcess.STATUS_JEOPARDY_CD;
        String lacd = WorkFlowProcess.STATUS_LATE_CD;

        for (WorkFlowProcess p : processes) {
            if (p.getStatus().equalsIgnoreCase(lacd)) {
                past++;
            } else if (p.getStatus().equalsIgnoreCase(jpcd)) {
                jeop++;
            } else {
                ok++;
            }
        }

        ArrayList<ChartData> als = new ArrayList<ChartData>();
        ChartData cd = new ChartData();
        cd.setColor(WorkFlowProcess.OK_COLOR_STRING);
        cd.setLabel(WorkFlowProcess.STATUS_OK_DESC);
        StringBuffer sb = new StringBuffer("by=status&section=");
        cd.setQparam(sb + okcd);
        cd.setValue(ok);
        als.add(cd);

        cd = new ChartData();
        cd.setColor(WorkFlowProcess.STATUS_JEOP_COLOR_STRING);
        cd.setLabel(WorkFlowProcess.STATUS_JEOPARDY_DESC);
        cd.setQparam(sb + jpcd);
        cd.setValue(jeop);
        als.add(cd);

        cd = new ChartData();
        cd.setColor(WorkFlowProcess.STATUS_LATE_COLOR_STRING);
        cd.setLabel(WorkFlowProcess.STATUS_LATE_DESC);
        cd.setQparam(sb + lacd);
        cd.setValue(past);
        als.add(cd);

        PieChartOutputManager pc = new PieChartOutputManager();
        pc.setHeight(height);
        pc.setWidth(width);
        pc.setTitle(title);
        pc.setClickURL(clickURL);
        
        try {
            image = pc.process(als.toArray(new ChartData[0]), this, null);
        } catch (IOException ioe) {
            logger.error("IOException", ioe);
        }
    }
    
}
